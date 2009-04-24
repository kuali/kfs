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
import java.io.FilenameFilter;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.batch.service.BalancingService;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.LedgerBalanceHistory;
import org.kuali.kfs.gl.businessobject.LedgerEntryHistory;
import org.kuali.kfs.gl.businessobject.OriginEntry;
import org.kuali.kfs.gl.dataaccess.LedgerBalanceBalancingDao;
import org.kuali.kfs.gl.dataaccess.LedgerBalanceHistoryBalancingDao;
import org.kuali.kfs.gl.dataaccess.LedgerBalancingDao;
import org.kuali.kfs.gl.dataaccess.LedgerEntryBalancingDao;
import org.kuali.kfs.gl.dataaccess.LedgerEntryHistoryBalancingDao;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base service implementation for BalancingService. Useful for generic implementation of common code between labor and GL balancing.
 */
@Transactional
public abstract class BalancingServiceBaseImpl<T extends Entry, S extends Balance> implements BalancingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalancingServiceBaseImpl.class);

    // Used to enable us to do generic general ledger or labor balancing
    protected Class<T> entryHistoryPersistentClass;
    protected Class<S> balanceHistoryPersistentClass;
    
    protected ParameterService parameterService;
    protected KualiConfigurationService kualiConfigurationService;
    protected BusinessObjectService businessObjectService;
    protected OptionsService optionsService;
    protected DateTimeService dateTimeService;
    protected UniversityDateService universityDateService;
    protected LedgerBalancingDao ledgerBalancingDao;
    protected LedgerEntryBalancingDao ledgerEntryBalancingDao;
    protected LedgerBalanceBalancingDao ledgerBalanceBalancingDao;
    protected LedgerBalanceHistoryBalancingDao ledgerBalanceHistoryBalancingDao;
    protected LedgerEntryHistoryBalancingDao ledgerEntryHistoryBalancingDao;
    protected ReportWriterService reportWriterService;
    protected String batchFileDirectoryName;
    
    /**
     * Constructs a BalancingServiceBaseImpl.java. The generics are expected to be of type Balance and Entry respectively.
     */
    public BalancingServiceBaseImpl() {
        super();
        this.entryHistoryPersistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.balanceHistoryPersistentClass = (Class<S>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#runBalancing()
     */
    public boolean runBalancing() {
        // Prepare date constants used throughout the process
        Integer currentUniversityFiscalYear = optionsService.getCurrentYearOptions().getUniversityFiscalYear();
        int startUniversityFiscalYear = currentUniversityFiscalYear - this.getPastFiscalYearsToConsider();

        LOG.debug("Checking files required for balancing process are present.");
        if(!this.isFilesReady()) {
            reportWriterService.writeFormattedMessage(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.ERROR_BATCH_BALANCING_FILES));
            
            return false;
        }
        
        LOG.debug("Checking data required for balancing process is present.");
        boolean historyTablesPopulated = false;
        // Following does not check for custom data (AccountBalance & Encumbrance) present. Should be OK since it can't exist without entry and balance data.
        if (this.getHistoryCount(null, entryHistoryPersistentClass) == 0 || this.getHistoryCount(null, balanceHistoryPersistentClass) == 0) {
            reportWriterService.writeFormattedMessage(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_DATA_INSERT), entryHistoryPersistentClass.getSimpleName(), balanceHistoryPersistentClass.getSimpleName());
            
            ledgerBalancingDao.populateLedgerEntryHistory(startUniversityFiscalYear);
            ledgerBalancingDao.populateLedgerBalanceHistory(startUniversityFiscalYear);
            this.customPopulateHistoryTables(startUniversityFiscalYear);
            
            historyTablesPopulated = true;
        }
        
        LOG.debug("Checking if obsolete historic data present. Deleting if yes.");
        // This only happens on the first accounting cycle after universityDateService.getFirstDateOfFiscalYear(currentYear) but since we are not
        // guaranteed a batch cycle on each day there isn't a generic way of only running this once during the year.
        boolean obsoleteUniversityFiscalYearDeleted = false;
        int obsoleteUniversityFiscalYear = startUniversityFiscalYear - 1;
        if(this.getHistoryCount(obsoleteUniversityFiscalYear, entryHistoryPersistentClass) != 0 || this.getHistoryCount(obsoleteUniversityFiscalYear, balanceHistoryPersistentClass) != 0 || this.doesCustomHistoryExist(obsoleteUniversityFiscalYear)) {
            reportWriterService.writeFormattedMessage(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_OBSOLETE_FISCAL_YEAR_DATA_DELETED), entryHistoryPersistentClass.getSimpleName(), balanceHistoryPersistentClass.getSimpleName(), obsoleteUniversityFiscalYear);
            this.deleteHistory(obsoleteUniversityFiscalYear, entryHistoryPersistentClass);
            this.deleteHistory(obsoleteUniversityFiscalYear, balanceHistoryPersistentClass);
            this.deleteCustomHistory(obsoleteUniversityFiscalYear);
            obsoleteUniversityFiscalYearDeleted = true;
        }
        
        // We only do update step if history has not been populated. If it has we can't run the update cycle because they were already picked up
        int updateRecordsIgnored = 0;
        if (!historyTablesPopulated) {
            LOG.debug("Getting postable records and save them to history tables.");
            updateRecordsIgnored = this.updateHistoriesHelper(startUniversityFiscalYear);            
        }

        LOG.debug("Comparing entry history table with the PRD counterpart.");
        int countEntryComparisionFailure = this.compareEntryHistory();
        if (countEntryComparisionFailure != 0) {
            reportWriterService.writeFormattedMessage(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_FAILURE_COUNT), entryHistoryPersistentClass.getSimpleName(), countEntryComparisionFailure, this.getComparisonFailuresToPrintPerReport());
        }
        
        LOG.debug("Comparing balance history table with the PRD counterpart.");
        int countBalanceComparisionFailure = this.compareBalanceHistory();
        if (countBalanceComparisionFailure != 0) {
            reportWriterService.writeFormattedMessage(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_FAILURE_COUNT), balanceHistoryPersistentClass.getSimpleName(), countBalanceComparisionFailure, this.getComparisonFailuresToPrintPerReport());
        }
        
        LOG.debug("Comparing custom, if any, history table with the PRD counterpart.");
        Map<String, Integer> countCustomComparisionFailures = this.customCompareHistory();
        
        LOG.debug("Writing statistics section");
        reportWriterService.writeStatistic(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_FISCAL_YEARS_INCLUDED), ledgerBalanceHistoryBalancingDao.findDistinctFiscalYears());
        reportWriterService.writeStatistic(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_HISTORY_TABLES_INITIALIZED), historyTablesPopulated ? "Yes" : "No");
        reportWriterService.writeStatistic(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_OBSOLETE_DELETED), obsoleteUniversityFiscalYearDeleted ? "Yes (" + obsoleteUniversityFiscalYear + ")": "No");
        reportWriterService.writeStatistic(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_UPDATED_SKIPPED), updateRecordsIgnored);
        reportWriterService.writeStatistic(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_COMPARISION_FAILURE), this.getShortTableLabel(entryHistoryPersistentClass.getSimpleName()), "(" + entryHistoryPersistentClass.getSimpleName() + ")", countEntryComparisionFailure);
        reportWriterService.writeStatistic(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_COMPARISION_FAILURE), this.getShortTableLabel(balanceHistoryPersistentClass.getSimpleName()), "(" + balanceHistoryPersistentClass.getSimpleName() + ")", countBalanceComparisionFailure);
        reportWriterService.writeStatistic(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_ENTRY_SUM_ROW_COUNT_HISTORY), this.getShortTableLabel(entryHistoryPersistentClass.getSimpleName()), "(" + entryHistoryPersistentClass.getSimpleName() + ")", ledgerEntryHistoryBalancingDao.findSumRowCountGreaterOrEqualThan(startUniversityFiscalYear));
        reportWriterService.writeStatistic(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_ENTRY_ROW_COUNT_PRODUCTION), this.getShortTableLabel((Entry.class).getSimpleName()), ledgerEntryBalancingDao.findCountGreaterOrEqualThan(startUniversityFiscalYear));
        reportWriterService.writeStatistic(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_BALANCE_ROW_COUNT_HISTORY), this.getShortTableLabel(balanceHistoryPersistentClass.getSimpleName()), "(" + balanceHistoryPersistentClass.getSimpleName() + ")", this.getHistoryCount(null, balanceHistoryPersistentClass));
        reportWriterService.writeStatistic(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_BALANCE_ROW_COUNT_PRODUCTION), this.getShortTableLabel((Balance.class).getSimpleName()), ledgerBalanceBalancingDao.findCountGreaterOrEqualThan(startUniversityFiscalYear));
        
        if (ObjectUtils.isNotNull(countCustomComparisionFailures)) {
            for (Iterator<String> names = countCustomComparisionFailures.keySet().iterator(); names.hasNext();) {
                String name = names.next();
                int count = countCustomComparisionFailures.get(name);
                
                reportWriterService.writeStatistic(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_COMPARISION_FAILURE), this.getShortTableLabel(name), "(" + name + ")", count);
            }
        }
        this.customPrintRowCountHistory(startUniversityFiscalYear);
        
        return true;
    }
    
    /**
     * In batchFileDirectoryName looks for a pattern matching filenameFilter and returns the filename with the highest lastModified()
     * @param filenameFilter to filter filenames in batchFileDirectoryName for
     * @return File with highest lastModified()
     */
    protected File getNewestDataFile(FilenameFilter filenameFilter) {
        File newestFile = null;
        
        File directory = new File(batchFileDirectoryName);
        File[] directoryListing = directory.listFiles(filenameFilter);
        if (directoryListing == null || directoryListing.length == 0) {
            return null;
        } else {
            for (int i = 0; i < directoryListing.length; i++) {
                File file = directoryListing[i];
                if (newestFile == null) {
                    newestFile = file;
                } else {
                    if (newestFile.lastModified() < file.lastModified()){
                        newestFile = file;                        
                    }
                }
            }
        }
        
        return newestFile;
    }
    
    /**
     * @return if the files required for processing of this job are present and readable.
     */
    protected boolean isFilesReady() {
        File inputFile = this.getPosterInputFile();
        File errorFile = this.getPosterErrorOutputFile();
        
        return inputFile != null && errorFile != null && inputFile.exists() && errorFile.exists() && inputFile.canRead() && errorFile.canRead();
    }
    
    /**
     * Deletes data for the given fiscal year of entries from persistentClass.
     * @param universityFiscalYear the given university fiscal year
     * @param persistentClass table for which to delete the history
     */
    protected void deleteHistory(Integer universityFiscalYear, Class<? extends PersistableBusinessObjectBase> persistentClass) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        
        businessObjectService.deleteMatching(persistentClass, fieldValues);
    }
    
    /**
     * Gets count for given fiscal year of entries from persistentClass.
     * @param fiscalYear parameter may be null which will get count for all years
     * @param persistentClass table for which to get the count
     * @return count
     */
    protected int getHistoryCount(Integer fiscalYear, Class<? extends PersistableBusinessObjectBase> persistentClass) {
        Map<String, String> keyMap = new HashMap<String, String>();
        
        if (fiscalYear != null) {
            keyMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear.toString());
        }
        
        return businessObjectService.countMatching(persistentClass, keyMap);
    }
    
    /**
     * This is a helper method that wraps parsing poster entries for updateEntryHistory and updateBalanceHistory.
     * @param startUniversityFiscalYear fiscal year for which to accept the earlier parsed lines from the input file
     * @return indicated whether records where ignored due to being older then startUniversityFiscalYear
     */
    protected int updateHistoriesHelper(Integer startUniversityFiscalYear) {
        int ignoredRecordsFound = 0;
        int lineNumber = 0;

        try {
            FileReader posterInputFileReader = new FileReader(this.getPosterInputFile());
            BufferedReader posterInputBufferedReader = new BufferedReader(posterInputFileReader);
            FileReader posterErrorFileReader = new FileReader(this.getPosterErrorOutputFile());
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
                            this.updateCustomHistory(originEntry);
                        } else {
                            // Outside of trackable FY range. Log as being a failed line
                            ignoredRecordsFound++;
                            reportWriterService.writeError(originEntry, new Message(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_RECORD_BEFORE_FISCAL_YEAR), Message.TYPE_WARNING, startUniversityFiscalYear));
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
            LOG.fatal(String.format(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.ERROR_BATCH_BALANCING_UNKNOWN_FAILURE), e.getMessage(), lineNumber));
            reportWriterService.writeFormattedMessage(String.format(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.ERROR_BATCH_BALANCING_UNKNOWN_FAILURE), e.getMessage(), lineNumber));
            throw new RuntimeException(String.format(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.ERROR_BATCH_BALANCING_UNKNOWN_FAILURE), e.getMessage(), lineNumber));
        }
        
        return ignoredRecordsFound;
    }
    
    /**
     * Compares entries in the Balance and BalanceHistory tables to ensure the amounts match.
     * @return count is compare failures
     */
    protected Integer compareBalanceHistory() {
        Integer countComparisionFailures = 0;
        
        // TODO findAll might not be a good idea performance wise. Do some kind of LIMIT stepping?
        // Finding all history lines as starting point for comparision
        for (Iterator<LedgerBalanceHistory> iterator = businessObjectService.findAll(balanceHistoryPersistentClass).iterator(); iterator.hasNext();) {
            LedgerBalanceHistory ledgerBalanceHistory = iterator.next();
            Balance balance = this.getBalance(ledgerBalanceHistory);
            
            if (ObjectUtils.isNull(balance) || !ledgerBalanceHistory.compareAmounts(balance)) {
                // Compare failed, properly log it if we havn't written more then TOTAL_COMPARISION_FAILURES_TO_PRINT yet
                countComparisionFailures++;
                if (countComparisionFailures <= this.getComparisonFailuresToPrintPerReport()) {
                    reportWriterService.writeError(ledgerBalanceHistory, new Message(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_RECORD_FAILED_BALANCING), Message.TYPE_WARNING, ledgerBalanceHistory.getClass().getSimpleName()));
                }
            }
        }
        
        return countComparisionFailures;
    }
    
    /**
     * Compares entries in the Entry and EntryHistory tables to ensure the amounts match.
     * @return count is compare failures
     */
    protected Integer compareEntryHistory() {
        Integer countComparisionFailures = 0;
        
        // TODO findAll might not be a good idea performance wise. Do some kind of LIMIT stepping?
        // Finding all history lines as starting point for comparision
        for (Iterator<LedgerEntryHistory> iterator = businessObjectService.findAll(entryHistoryPersistentClass).iterator(); iterator.hasNext();) {
            LedgerEntryHistory ledgerEntryHistory = iterator.next();
            
            Object[] objects = ledgerEntryBalancingDao.findEntryByGroup(ledgerEntryHistory.getUniversityFiscalYear(), ledgerEntryHistory.getChartOfAccountsCode(), ledgerEntryHistory.getFinancialObjectCode(), ledgerEntryHistory.getFinancialBalanceTypeCode(), ledgerEntryHistory.getUniversityFiscalPeriodCode(), ledgerEntryHistory.getTransactionDebitCreditCode());
            
            if (ObjectUtils.isNull(objects) ||
                    !(((Integer) objects[0]).intValue() == ledgerEntryHistory.getRowCount()
                            && ((KualiDecimal) objects[1]).equals(ledgerEntryHistory.getTransactionLedgerEntryAmount()))) {
                // Compare failed, properly log it if we havn't written more then TOTAL_COMPARISION_FAILURES_TO_PRINT yet
                countComparisionFailures++;
                if (countComparisionFailures <= this.getComparisonFailuresToPrintPerReport()) {
                    reportWriterService.writeError(ledgerEntryHistory, new Message(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_RECORD_FAILED_BALANCING), Message.TYPE_WARNING, ledgerEntryHistory.getClass().getSimpleName()));
                }
            }
        }
        
        return countComparisionFailures;
    }
    
    /**
     * Possible override if sub class has additional history tables. Populates custom history tables.
     * @param fiscalYear fiscal year populate should start from
     */
    public void customPopulateHistoryTables(Integer fiscalYear) {
        return;
    }
    
    /**
     * Possible override if sub class has additional history tables. This returns true if value populated in any such tables.
     * @param fiscalYear given fiscal year
     * @return if data exists for any such table for given year
     */
    protected boolean doesCustomHistoryExist(Integer fiscalYear) {
        return false;
    }
    
    /**
     * Possible override if sub class has additional history tables. Deletes data in history table. Also
     * should print message to that affect to be consistent with rest of functionality.
     * @param fiscalYear given fiscal year
     */
    protected void deleteCustomHistory(Integer fiscalYear) {
        return;
    }
    
    /**
     * Possible override if sub class has additional history tables. Updates history data for custom table(s).
     * @param originEntry representing the update details
     */
    protected void updateCustomHistory(OriginEntry originEntry) {
        return;
    }
    
    /**
     * Possible override if sub class has additional history tables.
     * @return compare failures. As a HashMap of key: businessObjectName, value: count
     */
    protected Map<String, Integer> customCompareHistory() {
        return null;
    }
    
    /**
     * Possible override if sub class has additional history tables. Prints the row count history for STATISTICS section.
     * @param fiscalYear starting from which fiscal year the comparision should take place
     */
    protected void customPrintRowCountHistory(Integer fiscalYear){
        return;
    }
    
    /**
     * Sets the ParameterService
     * 
     * @param parameterService The ParameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
    /**
     * Sets the KualiConfigurationService
     * 
     * @param kualiConfigurationService The KualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
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
     * Sets the LedgerBalancingDao
     * 
     * @param ledgerBalancingDao The LedgerBalancingDao to set.
     */
    public void setLedgerBalancingDao(LedgerBalancingDao ledgerBalancingDao) {
        this.ledgerBalancingDao = ledgerBalancingDao;
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
     * Sets the ledgerBalanceHistoryBalancingDao
     * 
     * @param ledgerBalanceHistoryBalancingDao The LedgerBalanceHistoryBalancingDao to set.
     */
    public void setLedgerBalanceHistoryBalancingDao(LedgerBalanceHistoryBalancingDao ledgerBalanceHistoryBalancingDao) {
        this.ledgerBalanceHistoryBalancingDao = ledgerBalanceHistoryBalancingDao;
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
     * Sets the reportWriterService
     * 
     * @param reportWriterService The reportWriterService to set.
     */
    public void setReportWriterService(ReportWriterService reportWriterService) {
        this.reportWriterService = reportWriterService;
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
