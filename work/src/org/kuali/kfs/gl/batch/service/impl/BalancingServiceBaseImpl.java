/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.gl.batch.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.TextReportHelper;
import org.kuali.kfs.gl.batch.service.BalancingService;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.LedgerBalanceHistory;
import org.kuali.kfs.gl.businessobject.LedgerEntryHistory;
import org.kuali.kfs.gl.businessobject.OriginEntry;
import org.kuali.kfs.gl.dataaccess.LedgerBalanceBalancingDao;
import org.kuali.kfs.gl.dataaccess.LedgerEntryBalancingDao;
import org.kuali.kfs.gl.dataaccess.LedgerEntryHistoryBalancingDao;
import org.kuali.kfs.gl.service.impl.ReportServiceImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base service implementation for BalancingService. Useful for generic implementation of common code between labor and GL balancing.
 */
@Transactional
public abstract class BalancingServiceBaseImpl<T, S> implements BalancingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalancingServiceBaseImpl.class);

    private static final int TOTAL_COMPARISION_FAILURES_TO_PRINT = 10;
    
    // Used to enable us to do generic general ledger or labor balancing
    private Class<T> balanceHistoryPersistentClass;
    private Class<S> entryHistoryPersistentClass;
    
    protected BusinessObjectService businessObjectService;
    private OptionsService optionsService;
    private DateTimeService dateTimeService;
    private UniversityDateService universityDateService;
    private LedgerEntryBalancingDao ledgerEntryBalancingDao;
    private LedgerBalanceBalancingDao ledgerBalanceBalancingDao;
    private LedgerEntryHistoryBalancingDao ledgerEntryHistoryBalancingDao;
    // TODO Shouldn't we do SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.STAGING_DIRECTORY_KEY); instead of injecting constants via spring?
    private String batchFileDirectoryName;
    
    /**
     * Constructs a BalancingServiceBaseImpl.java. The generics are expected to be of type Balance and Entry respectively.
     */
    public BalancingServiceBaseImpl() {
        super();
        this.balanceHistoryPersistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.entryHistoryPersistentClass = (Class<S>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        
        try {
            if(!(balanceHistoryPersistentClass.newInstance() instanceof Balance)) {
                throw new IllegalArgumentException("T needs to be of type Balance.");
            }
            
            if(!(entryHistoryPersistentClass.newInstance() instanceof Entry)) {
                throw new IllegalArgumentException("S needs to be of type Entry.");
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException("T needs to be of type Balance. S needs to be of type Entry.", e);
        }
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#runBalancing()
     */
    public boolean runBalancing() {
        LOG.debug("Preparing parameters for report.");
        // Prepare the report
        String reportsDirectory = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.REPORTS_DIRECTORY_KEY);
        // TODO should ReportServiceImpl.DATE_FORMAT_STRING be in KFSConstants?
        // TODO no constant for .TXT?
        String reportFilename = reportsDirectory + File.separator + this.getReportFilename() + new SimpleDateFormat(ReportServiceImpl.DATE_FORMAT_STRING).format(dateTimeService.getCurrentDate()) + ".txt";
        PrintStream REPORT_ps;
        try {
            REPORT_ps = new PrintStream(reportFilename);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        // We need one textReportHelper here so that we can print appropriate headers. See other methods for the ones that take care of writing error lines.
        TextReportHelper textReportHelper = new TextReportHelper(this.getReportTitle(), REPORT_ps, dateTimeService);
        
        // Prepare date constants used throughout the process
        Integer currentUniversityFiscalYear = optionsService.getCurrentYearOptions().getUniversityFiscalYear();
        // TODO could make "- 1" some kind of System Parameter to make this configurable
        int startUniversityFiscalYear = currentUniversityFiscalYear - 1;

        LOG.debug("Checking data required for balancing process is present.");
        if (this.getBalanceHistoryCount(null) == 0 || this.getEntryHistoryCount(null) == 0) {
            // Write title manually since there won't be any error records
            textReportHelper.writeTitle();
            
            REPORT_ps.println(balanceHistoryPersistentClass.getSimpleName() + " or " + entryHistoryPersistentClass.getSimpleName() + " empty. Use something like the following to populate your local tables. Note obj_id needs to be properly generated (for oracle use sys_guid()). Also your table / field names may be different:\n");
            
            REPORT_ps.println(getTableCreationInstructions(startUniversityFiscalYear));
            
            return true;
        }
        
        LOG.debug("Checking is obsolete historic data present. Deleting if yes.");
        // This only happens on the first accounting cycle after universityDateService.getFirstDateOfFiscalYear(currentYear) but since we are not
        // guaranteed a batch cycle on each day there isn't a generic way of only running this once during the year.
        boolean obsoleteUniversityFiscalYearDeleted = false;
        int obsoleteUniversityFiscalYear = startUniversityFiscalYear - 1;
        if(this.getBalanceHistoryCount(obsoleteUniversityFiscalYear) != 0 || this.getEntryHistoryCount(obsoleteUniversityFiscalYear) != 0) {
            REPORT_ps.println(balanceHistoryPersistentClass.getSimpleName() + " or " + entryHistoryPersistentClass.getSimpleName() + " for universityFiscalYear=" + obsoleteUniversityFiscalYear + " found. That is out of range and will be deleted.\n");
            this.deleteBalanceHistory(obsoleteUniversityFiscalYear);
            this.deleteEntryHistory(obsoleteUniversityFiscalYear);
            obsoleteUniversityFiscalYearDeleted = true;
        }
        
        LOG.debug("Getting postable records and save them to history tables.");
        boolean updateRecordsIgnored = this.updateHistoriesHelper(startUniversityFiscalYear, REPORT_ps, textReportHelper);

        LOG.debug("Comparing balance and entry history tables with the PRD counterparts.");
        int countBalanceComparisionFailures = this.compareBalanceHistory(REPORT_ps, textReportHelper);
        int countEntryComparisionFailures = this.compareEntryHistory(REPORT_ps, textReportHelper);
        
        if (countBalanceComparisionFailures != 0 | countEntryComparisionFailures != 0) {
            REPORT_ps.println("\nTotal failure count for " + balanceHistoryPersistentClass.getSimpleName() + " is " + countBalanceComparisionFailures + " and " + entryHistoryPersistentClass.getSimpleName() + " is " + countEntryComparisionFailures + ". Only up to " + TOTAL_COMPARISION_FAILURES_TO_PRINT + " failures for each table printed.\n");
        }
        
        LOG.debug("Doing rowCount compare between balance and entry history tables with the PRD counterparts.");
        boolean rowCountBalanceCompare = this.countBalanceCompare(startUniversityFiscalYear, REPORT_ps);
        boolean rowCountEntryCompare = this.countEntryCompare(startUniversityFiscalYear, REPORT_ps);
        
        LOG.debug("Writing statistics section");
        // We call writeTitle here because no title may have been written if there were no errors. It will check for us that the title isn't written twice
        textReportHelper.writeTitle();
        textReportHelper.writeStatisticsHeader();
        REPORT_ps.printf("                             OBSOLETE HISTORY DELETED                         %9b\n", obsoleteUniversityFiscalYearDeleted ? obsoleteUniversityFiscalYear : false);
        REPORT_ps.printf("                             UPDATES SKIPPED DUE TO OUT OF RANGE FISCAL YEAR  %9b\n", updateRecordsIgnored);
        REPORT_ps.printf("                             BALANCE COMPARISION FAILURE COUNT                %,9d\n", countBalanceComparisionFailures);
        REPORT_ps.printf("                             ENTRY COMPARISION FAILURE COUNT                  %,9d\n", countEntryComparisionFailures);
        REPORT_ps.printf("                             BALANCE ROW COUNT COMPARISION SUCCESS            %9b\n", rowCountBalanceCompare);
        REPORT_ps.printf("                             ENTRY ROW COUNT COMPARISION SUCCESS              %9b\n", rowCountEntryCompare);
        
        REPORT_ps.close();
        
        return true;
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#deleteBalanceHistory(java.lang.Integer)
     */
    public void deleteBalanceHistory(Integer universityFiscalYear) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        
        businessObjectService.deleteMatching(balanceHistoryPersistentClass, fieldValues);
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#deleteEntryHistory(java.lang.Integer)
     */
    public void deleteEntryHistory(Integer universityFiscalYear) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        
        businessObjectService.deleteMatching(entryHistoryPersistentClass, fieldValues);
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getBalanceHistoryCount(java.lang.Integer)
     */
    public int getBalanceHistoryCount(Integer fiscalYear) {
        Map<String, String> keyMap = new HashMap<String, String>();
        
        if (fiscalYear != null) {
            keyMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear.toString());
        }
        
        return businessObjectService.countMatching(balanceHistoryPersistentClass, keyMap);
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getEntryHistoryCount(java.lang.Integer)
     */
    public int getEntryHistoryCount(Integer fiscalYear) {
        Map<String, String> keyMap = new HashMap<String, String>();
        
        if (fiscalYear != null) {
            keyMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear.toString());
        }
        
        return businessObjectService.countMatching(entryHistoryPersistentClass, keyMap);
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#updateHistoriesHelper(java.lang.Integer, java.io.PrintStream, org.kuali.kfs.gl.TextReportHelper)
     */
    public boolean updateHistoriesHelper(Integer startUniversityFiscalYear, PrintStream REPORT_ps, TextReportHelper textReportHelper) {
        boolean ignoredRecordFound = false;
        int lineNumber = 0;

        try {
            FileReader posterInputFileReader = new FileReader(batchFileDirectoryName + File.separator + this.getPosterInputFilename());
            BufferedReader posterInputBufferedReader = new BufferedReader(posterInputFileReader);
            FileReader posterErrorFileReader = new FileReader(batchFileDirectoryName + File.separator + this.getPosterErrorOutputFilename());
            BufferedReader posterErrorBufferedReader = new BufferedReader(posterErrorFileReader);
            
            // Reading input and error lines in tandem. Eliminating input lines if they were a line in error.
            String currentInputLine = posterInputBufferedReader.readLine();
            String currentErrorLine = posterErrorBufferedReader.readLine();

            while (currentInputLine != null) {
                lineNumber++;
                
                if (!StringUtils.isEmpty(currentInputLine) && !StringUtils.isBlank(currentInputLine.trim())) {
                    
                    if(currentInputLine.equals(currentErrorLine)) {
                        // Skip it, it's in error. Increment to next error line
                        currentErrorLine = posterErrorBufferedReader.readLine();
                    } else {
                        // Line is good, parse it via delegation
                        OriginEntry originEntry = this.getOriginEntry(currentInputLine, lineNumber);
                        
                        if (originEntry.getUniversityFiscalYear() >= startUniversityFiscalYear) {
                            // Line is in acceptable FY range, update history tables
                            this.updateEntryHistory(originEntry);
                            this.updateBalanceHistory(originEntry);
                        } else {
                            // Outside of trackable FY range. Log as being a failed line
                            if (!ignoredRecordFound) {
                                textReportHelper.writeErrorHeader(originEntry);
                                ignoredRecordFound = true;
                            }
                            textReportHelper.writeErrors(originEntry, new Message("Record before universityFiscalYear=" + startUniversityFiscalYear, Message.TYPE_WARNING));
                        }
                    }
                }
                
                currentInputLine = posterInputBufferedReader.readLine();
            }

            posterInputFileReader.close();
            posterInputBufferedReader.close();
            posterErrorFileReader.close();
            posterErrorBufferedReader.close();
        } catch (Exception e) {
            LOG.fatal("Process stopped due to " + e.getMessage() + " on line number : " + lineNumber);
            REPORT_ps.println("Process stopped due to: " + e.getMessage() + " on line number : " + lineNumber);
            throw new RuntimeException("Unable to execute: " + e.getMessage() + " on line number: " + lineNumber, e);
        }
        
        return ignoredRecordFound;
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#compareBalanceHistory(java.io.PrintStream, org.kuali.kfs.gl.TextReportHelper)
     */
    public int compareBalanceHistory(PrintStream REPORT_ps, TextReportHelper textReportHelper) {
        int countComparisionFailures = 0;
        
        // TODO findAll might not be a good idea performance wise. Do some kind of LIMIT stepping?
        // Finding all history lines as starting point for comparision
        for (Iterator<LedgerBalanceHistory> iterator = businessObjectService.findAll(balanceHistoryPersistentClass).iterator(); iterator.hasNext();) {
            LedgerBalanceHistory ledgerBalanceHistory = iterator.next();
            Balance balance = this.getBalance(ledgerBalanceHistory);
            
            if (!ledgerBalanceHistory.compareAmounts(balance)) {
                // Compare failed, properly log it if we havn't written more then TOTAL_COMPARISION_FAILURES_TO_PRINT yet
                if (countComparisionFailures == 0) {
                    textReportHelper.writeErrorHeader(ledgerBalanceHistory);
                }
                countComparisionFailures++;
                if (countComparisionFailures <= TOTAL_COMPARISION_FAILURES_TO_PRINT) {
                    textReportHelper.writeErrors(ledgerBalanceHistory, new Message("Failed " + ledgerBalanceHistory.getClass().getSimpleName() + " balancing", Message.TYPE_WARNING));
                }
            }
        }
        
        return countComparisionFailures;
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#compareEntryHistory(java.io.PrintStream, org.kuali.kfs.gl.TextReportHelper)
     */
    public int compareEntryHistory(PrintStream REPORT_ps, TextReportHelper textReportHelper) {
        int countComparisionFailures = 0;
        
        // TODO findAll might not be a good idea performance wise. Do some kind of LIMIT stepping?
        // Finding all history lines as starting point for comparision
        for (Iterator<LedgerEntryHistory> iterator = businessObjectService.findAll(entryHistoryPersistentClass).iterator(); iterator.hasNext();) {
            LedgerEntryHistory ledgerEntryHistory = iterator.next();
            
            Object[] objects = ledgerEntryBalancingDao.findEntryByGroup(ledgerEntryHistory.getUniversityFiscalYear(), ledgerEntryHistory.getChartOfAccountsCode(), ledgerEntryHistory.getFinancialObjectCode(), ledgerEntryHistory.getFinancialBalanceTypeCode(), ledgerEntryHistory.getUniversityFiscalPeriodCode(), ledgerEntryHistory.getTransactionDebitCreditCode());
            
            if (ObjectUtils.isNull(objects) ||
                    !(((Long) objects[0]).intValue() == ledgerEntryHistory.getRowCount()
                            && ((KualiDecimal) objects[1]).equals(ledgerEntryHistory.getTransactionLedgerEntryAmount()))) {
                // Compare failed, properly log it if we havn't written more then TOTAL_COMPARISION_FAILURES_TO_PRINT yet
                if (countComparisionFailures == 0) {
                    textReportHelper.writeErrorHeader(ledgerEntryHistory);
                }
                countComparisionFailures++;
                if (countComparisionFailures <= TOTAL_COMPARISION_FAILURES_TO_PRINT) {
                    textReportHelper.writeErrors(ledgerEntryHistory, new Message("Failed " + ledgerEntryHistory.getClass().getSimpleName() + " balancing", Message.TYPE_WARNING));
                }
            }
        }
        
        return countComparisionFailures;
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#countBalanceCompare(java.lang.Integer, java.io.PrintStream)
     */
    public boolean countBalanceCompare(Integer startUniversityFiscalYear, PrintStream REPORT_ps) {
        boolean successCountCompare = true;

        // Following we compare a "great or equal than fiscal year" count of PRD tables to our full count of the history tables. They
        // should match since deleteEntryHistory takes care of deleting old records. Furthermore the balance tables are a 1:1 mapping,
        // so simple counts should compare.
        int balanceBalancingCount = ledgerBalanceBalancingDao.findCountGreaterOrEqualThan(startUniversityFiscalYear);
        int balanceHistoryBalancingCount = this.getBalanceHistoryCount(null);
        
        if (balanceBalancingCount != balanceHistoryBalancingCount) {
            REPORT_ps.println(balanceHistoryPersistentClass.getSimpleName() + " has count of " + balanceHistoryBalancingCount + " which does not match production table count of " + balanceBalancingCount + ". countBalanceCompare failed.\n");
            successCountCompare = false;
        }
        
        return successCountCompare;
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#countEntryCompare(java.lang.Integer, java.io.PrintStream)
     */
    public boolean countEntryCompare(Integer startUniversityFiscalYear, PrintStream REPORT_ps) {
        boolean successCountCompare = true;

        // Note that unlike in countBalanceCompare we can't to a simple count compare because the balance history table aggregates.
        // findSumRowCountGreaterOrEqualThan is doing a sum(row_count)
        int entryBalancingCount = ledgerEntryBalancingDao.findCountGreaterOrEqualThan(startUniversityFiscalYear);
        int entryHistoryBalancingSumRowCount = ledgerEntryHistoryBalancingDao.findSumRowCountGreaterOrEqualThan(startUniversityFiscalYear);
        
        if (entryBalancingCount != entryHistoryBalancingSumRowCount) {
            REPORT_ps.println(entryHistoryPersistentClass.getSimpleName() + " has count of " + entryHistoryBalancingSumRowCount + " which does not match production table count of " + entryBalancingCount + ". countEntryCompare failed.\n");
            successCountCompare = false;
        }
        
        return successCountCompare;
    }
    
    /**
     * Sets the OptionsService
     * 
     * @param optionsService The OptionsService to set.
     */
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }
    
    /**
     * Sets the DateTimeService
     * 
     * @param dateTimeService The DateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the UniversityDateService
     * 
     * @param universityDateService The UniversityDateService to set.
     */
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }
    
    /**
     * Sets the BusinessObjectService
     * 
     * @param businessObjectService The BusinessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the LedgerEntryBalancingDao
     * 
     * @param ledgerEntryBalancingDao The LedgerEntryBalancingDao to set.
     */
    public void setLedgerEntryBalancingDao(LedgerEntryBalancingDao ledgerEntryBalancingDao) {
        this.ledgerEntryBalancingDao = ledgerEntryBalancingDao;
    }

    /**
     * Sets the LedgerBalanceBalancingDao
     * 
     * @param ledgerBalanceBalancingDao The LedgerBalanceBalancingDao to set.
     */
    public void setLedgerBalanceBalancingDao(LedgerBalanceBalancingDao ledgerBalanceBalancingDao) {
        this.ledgerBalanceBalancingDao = ledgerBalanceBalancingDao;
    }

    /**
     * Sets the LedgerEntryHistoryBalancingDao
     * 
     * @param ledgerEntryHistoryBalancingDao The LedgerEntryHistoryBalancingDao to set.
     */
    public void setLedgerEntryHistoryBalancingDao(LedgerEntryHistoryBalancingDao ledgerEntryHistoryBalancingDao) {
        this.ledgerEntryHistoryBalancingDao = ledgerEntryHistoryBalancingDao;
    }

    /**
     * Sets the batchFileDirectoryName
     * 
     * @param batchFileDirectoryName The batchFileDirectoryName to set.
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }
}
