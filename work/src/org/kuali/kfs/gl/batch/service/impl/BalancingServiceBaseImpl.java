/*
 * Copyright 2007-2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
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
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.batch.dataaccess.LedgerEntryBalanceCachingDao;
import org.kuali.kfs.gl.batch.service.BalancingService;
import org.kuali.kfs.gl.batch.service.PosterService;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.gl.dataaccess.LedgerBalanceBalancingDao;
import org.kuali.kfs.gl.dataaccess.LedgerBalanceHistoryBalancingDao;
import org.kuali.kfs.gl.dataaccess.LedgerBalancingDao;
import org.kuali.kfs.gl.dataaccess.LedgerEntryBalancingDao;
import org.kuali.kfs.gl.dataaccess.LedgerEntryHistoryBalancingDao;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base service implementation for BalancingService. Useful for generic implementation of common code between la`bor and GL
 * balancing.
 */
@Transactional
public abstract class BalancingServiceBaseImpl<T extends Entry, S extends Balance> implements BalancingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalancingServiceBaseImpl.class);

    // Used to enable us to do generic general ledger or labor balancing
    protected Class<T> entryHistoryPersistentClass;
    protected Class<S> balanceHistoryPersistentClass;

    protected ParameterService parameterService;
    protected PersistenceStructureService persistenceStructureService;
    protected ConfigurationService kualiConfigurationService;
    protected BusinessObjectService businessObjectService;
    protected DateTimeService dateTimeService;
    protected UniversityDateService universityDateService;
    protected LedgerBalancingDao ledgerBalancingDao;
    protected LedgerEntryBalancingDao ledgerEntryBalancingDao;
    protected LedgerEntryBalanceCachingDao ledgerEntryBalanceCachingDao;
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
    @Override
    public boolean runBalancing() {
        // Prepare date constants used throughout the process
        Integer currentUniversityFiscalYear = universityDateService.getCurrentFiscalYear();
        int startUniversityFiscalYear = currentUniversityFiscalYear - this.getPastFiscalYearsToConsider();

        LOG.debug("Checking files required for balancing process are present.");
        if (!this.isFilesReady()) {
            reportWriterService.writeFormattedMessageLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.ERROR_BATCH_BALANCING_FILES));

            return false;
        }

        LOG.debug("Checking data required for balancing process is present.");
        boolean historyTablesPopulated = false;
        // Following does not check for custom data (AccountBalance & Encumbrance) present. Should be OK since it can't exist
        // without entry and balance data.
        if (this.getHistoryCount(null, entryHistoryPersistentClass) == 0 || this.getHistoryCount(null, balanceHistoryPersistentClass) == 0) {
            reportWriterService.writeFormattedMessageLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_DATA_INSERT), entryHistoryPersistentClass.getSimpleName(), balanceHistoryPersistentClass.getSimpleName());
            reportWriterService.writeNewLines(1);

            ledgerBalancingDao.populateLedgerEntryHistory(startUniversityFiscalYear);
            ledgerBalancingDao.populateLedgerBalanceHistory(startUniversityFiscalYear);
            this.customPopulateHistoryTables(startUniversityFiscalYear);

            historyTablesPopulated = true;
        }

        LOG.debug("Checking if obsolete historic data present. Deleting if yes.");
        // This only happens on the first accounting cycle after universityDateService.getFirstDateOfFiscalYear(currentYear) but
        // since we are not
        // guaranteed a batch cycle on each day there isn't a generic way of only running this once during the year.
        boolean obsoleteUniversityFiscalYearDeleted = false;
        int obsoleteUniversityFiscalYear = startUniversityFiscalYear - 1;
        if (this.getHistoryCount(obsoleteUniversityFiscalYear, entryHistoryPersistentClass) != 0 || this.getHistoryCount(obsoleteUniversityFiscalYear, balanceHistoryPersistentClass) != 0 || this.doesCustomHistoryExist(obsoleteUniversityFiscalYear)) {
            reportWriterService.writeFormattedMessageLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_OBSOLETE_FISCAL_YEAR_DATA_DELETED), entryHistoryPersistentClass.getSimpleName(), balanceHistoryPersistentClass.getSimpleName(), obsoleteUniversityFiscalYear);
            reportWriterService.writeNewLines(1);
            this.deleteHistory(obsoleteUniversityFiscalYear, entryHistoryPersistentClass);
            this.deleteHistory(obsoleteUniversityFiscalYear, balanceHistoryPersistentClass);
            this.deleteCustomHistory(obsoleteUniversityFiscalYear);
            obsoleteUniversityFiscalYearDeleted = true;
        }

        // We only do update step if history has not been populated. If it has we can't run the update cycle because they were
        // already picked up
        int updateRecordsIgnored = 0;
        if (!historyTablesPopulated) {
            LOG.debug("Getting postable records and save them to history tables.");
            updateRecordsIgnored = this.updateHistoriesHelper(PosterService.MODE_ENTRIES, startUniversityFiscalYear, this.getPosterInputFile(), this.getPosterErrorOutputFile());
            updateRecordsIgnored += this.updateHistoriesHelper(PosterService.MODE_REVERSAL, startUniversityFiscalYear, this.getReversalInputFile(), this.getReversalErrorOutputFile());
            updateRecordsIgnored += this.updateHistoriesHelper(PosterService.MODE_ICR, startUniversityFiscalYear, this.getICRInputFile(), this.getICRErrorOutputFile());
        }

        LOG.debug("Comparing entry history table with the PRD counterpart.");
        int countEntryComparisionFailure = this.compareEntryHistory();
        if (countEntryComparisionFailure != 0) {
            reportWriterService.writeNewLines(1);
            reportWriterService.writeFormattedMessageLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_FAILURE_COUNT), entryHistoryPersistentClass.getSimpleName(), countEntryComparisionFailure, this.getComparisonFailuresToPrintPerReport());
        }

        LOG.debug("Comparing balance history table with the PRD counterpart.");
        int countBalanceComparisionFailure = this.compareBalanceHistory();
        if (countBalanceComparisionFailure != 0) {
            reportWriterService.writeNewLines(1);
            reportWriterService.writeFormattedMessageLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_FAILURE_COUNT), balanceHistoryPersistentClass.getSimpleName(), countBalanceComparisionFailure, this.getComparisonFailuresToPrintPerReport());
        }

        LOG.debug("Comparing custom, if any, history table with the PRD counterpart.");
        Map<String, Integer> countCustomComparisionFailures = this.customCompareHistory();

        if (!historyTablesPopulated) {
            reportWriterService.writeNewLines(1);
            reportWriterService.writeFormattedMessageLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_FILE_LISTING), this.getFilenames());
        }

        LOG.debug("Writing statistics section");
        reportWriterService.writeStatisticLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_FISCAL_YEARS_INCLUDED), ledgerBalanceHistoryBalancingDao.findDistinctFiscalYears());
        reportWriterService.writeStatisticLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_HISTORY_TABLES_INITIALIZED), historyTablesPopulated ? "Yes" : "No");
        reportWriterService.writeStatisticLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_OBSOLETE_DELETED), obsoleteUniversityFiscalYearDeleted ? "Yes (" + obsoleteUniversityFiscalYear + ")" : "No");
        reportWriterService.writeStatisticLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_UPDATED_SKIPPED), updateRecordsIgnored);
        reportWriterService.writeStatisticLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_COMPARISION_FAILURE), this.getShortTableLabel(entryHistoryPersistentClass.getSimpleName()), "(" + entryHistoryPersistentClass.getSimpleName() + ")", countEntryComparisionFailure);
        reportWriterService.writeStatisticLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_COMPARISION_FAILURE), this.getShortTableLabel(balanceHistoryPersistentClass.getSimpleName()), "(" + balanceHistoryPersistentClass.getSimpleName() + ")", countBalanceComparisionFailure);
        reportWriterService.writeStatisticLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_ENTRY_SUM_ROW_COUNT_HISTORY), this.getShortTableLabel(entryHistoryPersistentClass.getSimpleName()), "(" + entryHistoryPersistentClass.getSimpleName() + ")", ledgerEntryHistoryBalancingDao.findSumRowCountGreaterOrEqualThan(startUniversityFiscalYear));
        reportWriterService.writeStatisticLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_ENTRY_ROW_COUNT_PRODUCTION), this.getShortTableLabel((Entry.class).getSimpleName()), ledgerEntryBalancingDao.findCountGreaterOrEqualThan(startUniversityFiscalYear));
        reportWriterService.writeStatisticLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_BALANCE_ROW_COUNT_HISTORY), this.getShortTableLabel(balanceHistoryPersistentClass.getSimpleName()), "(" + balanceHistoryPersistentClass.getSimpleName() + ")", this.getHistoryCount(null, balanceHistoryPersistentClass));
        reportWriterService.writeStatisticLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_BALANCE_ROW_COUNT_PRODUCTION), this.getShortTableLabel((Balance.class).getSimpleName()), ledgerBalanceBalancingDao.findCountGreaterOrEqualThan(startUniversityFiscalYear));

        if (ObjectUtils.isNotNull(countCustomComparisionFailures)) {
            for (Iterator<String> names = countCustomComparisionFailures.keySet().iterator(); names.hasNext();) {
                String name = names.next();
                int count = countCustomComparisionFailures.get(name);

                reportWriterService.writeStatisticLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_COMPARISION_FAILURE), this.getShortTableLabel(name), "(" + name + ")", count);
            }
        }
        this.customPrintRowCountHistory(startUniversityFiscalYear);

        return true;
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
     *
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
     *
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
     *
     * @param startUniversityFiscalYear fiscal year for which to accept the earlier parsed lines from the input file
     * @return indicated whether records where ignored due to being older then startUniversityFiscalYear
     */
    protected int updateHistoriesHelper(Integer postMode, Integer startUniversityFiscalYear, File inputFile, File errorFile) {
        int ignoredRecordsFound = 0;
        int lineNumber = 0;

        if (inputFile == null || errorFile == null) {
            return 0;
        }
        try {
            FileReader posterInputFileReader = new FileReader(inputFile);
            BufferedReader posterInputBufferedReader = new BufferedReader(posterInputFileReader);
            FileReader posterErrorFileReader = new FileReader(errorFile);
            BufferedReader posterErrorBufferedReader = new BufferedReader(posterErrorFileReader);

            // Reading input and error lines in tandem. Eliminating input lines if they were a line in error.
            String currentInputLine = posterInputBufferedReader.readLine();
            String currentErrorLine = posterErrorBufferedReader.readLine();

            while (currentInputLine != null) {
                lineNumber++;

                if (!StringUtils.isEmpty(currentInputLine) && !StringUtils.isBlank(currentInputLine.trim())) {

                    if (currentInputLine.equals(currentErrorLine)) {
                        // Skip it, it's in error. Increment to next error line
                        currentErrorLine = posterErrorBufferedReader.readLine();
                    }
                    else {
                        // Line is good, parse it via delegation
                        OriginEntryInformation originEntry = this.getOriginEntry(currentInputLine, lineNumber);

                        if (originEntry.getUniversityFiscalYear() >= startUniversityFiscalYear) {
                            // Line is in acceptable FY range, update history tables
                            this.updateEntryHistory(postMode, originEntry);
                            this.updateBalanceHistory(postMode, originEntry);
                            this.updateCustomHistory(postMode, originEntry);
                        }
                        else {
                            // Outside of trackable FY range. Log as being a failed line
                            ignoredRecordsFound++;
                            reportWriterService.writeError(originEntry, new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_RECORD_BEFORE_FISCAL_YEAR), Message.TYPE_WARNING, startUniversityFiscalYear));
                        }
                    }
                }

                currentInputLine = posterInputBufferedReader.readLine();
            }

            posterInputFileReader.close();
            posterInputBufferedReader.close();
            posterErrorFileReader.close();
            posterErrorBufferedReader.close();
        }
        catch (Exception e) {
            LOG.fatal(String.format(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.ERROR_BATCH_BALANCING_UNKNOWN_FAILURE), e.getMessage(), lineNumber), e);
            reportWriterService.writeFormattedMessageLine(String.format(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.ERROR_BATCH_BALANCING_UNKNOWN_FAILURE), e.getMessage(), lineNumber));
            throw new RuntimeException(String.format(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.ERROR_BATCH_BALANCING_UNKNOWN_FAILURE), e.getMessage(), lineNumber), e);
        }

        return ignoredRecordsFound;
    }

    abstract protected Integer compareBalanceHistory();

    abstract protected Integer compareEntryHistory();

    /**
     * 
     * @return
     */
    protected int getFiscalYear(){
        return universityDateService.getCurrentFiscalYear()-getPastFiscalYearsToConsider();
    }
    
    
    /**
     * Possible override if sub class has additional history tables. Populates custom history tables.
     *
     * @param fiscalYear fiscal year populate should start from
     */
    public void customPopulateHistoryTables(Integer fiscalYear) {
        return;
    }

    /**
     * Possible override if sub class has additional history tables. This returns true if value populated in any such tables.
     *
     * @param fiscalYear given fiscal year
     * @return if data exists for any such table for given year
     */
    protected boolean doesCustomHistoryExist(Integer fiscalYear) {
        return false;
    }

    /**
     * Possible override if sub class has additional history tables. Deletes data in history table. Also should print message to
     * that affect to be consistent with rest of functionality.
     *
     * @param fiscalYear given fiscal year
     */
    protected void deleteCustomHistory(Integer fiscalYear) {
        return;
    }

    /**
     * Possible override if sub class has additional history tables. Updates history data for custom table(s).
     *
     * @param originEntry representing the update details
     */
    protected void updateCustomHistory(Integer postMode, OriginEntryInformation originEntry) {
        return;
    }

    /**
     * Possible override if sub class has additional history tables.
     *
     * @return compare failures. As a HashMap of key: businessObjectName, value: count
     */
    protected Map<String, Integer> customCompareHistory() {
        return null;
    }

    /**
     * Possible override if sub class has additional history tables. Prints the row count history for STATISTICS section.
     *
     * @param fiscalYear starting from which fiscal year the comparision should take place
     */
    protected void customPrintRowCountHistory(Integer fiscalYear) {
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
     * Sets the ConfigurationService
     *
     * @param kualiConfigurationService The ConfigurationService to set.
     */
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
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

    /**
     * Sets the ledgerEntryBalanceCachingDao attribute value.
     *
     * @param ledgerEntryBalanceCachingDao The ledgerEntryBalanceCachingDao to set.
     */
    public void setLedgerEntryBalanceCachingDao(LedgerEntryBalanceCachingDao ledgerEntryBalanceCachingDao) {
        this.ledgerEntryBalanceCachingDao = ledgerEntryBalanceCachingDao;
    }

    /**
     * Sets the persistenceStructureService.
     *
     * @param persistenceStructureService
     */
    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }
}
