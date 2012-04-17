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
package org.kuali.kfs.module.ld.batch.service.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.BalancingService;
import org.kuali.kfs.gl.batch.service.PosterService;
import org.kuali.kfs.gl.batch.service.impl.BalancingServiceBaseImpl;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.LedgerBalanceHistory;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.batch.LaborBalancingStep;
import org.kuali.kfs.module.ld.businessobject.LaborBalanceHistory;
import org.kuali.kfs.module.ld.businessobject.LaborEntryHistory;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.businessobject.LedgerEntry;
import org.kuali.kfs.sys.FileUtil;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BalancingService for Labor balancing
 */
@Transactional
public class LaborBalancingServiceImpl extends BalancingServiceBaseImpl<LaborEntryHistory, LaborBalanceHistory> implements BalancingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborBalancingServiceImpl.class);
    
    protected File laborPosterInputFile = null;
    protected File laborPosterErrorOutputFile = null;
    
    @Override
    public boolean runBalancing() {
        // clear out the file cache, otherwise, it won't update the history tables with the latest poster files
        // therefore, it will use the files that were first used when the balancing job was run when the JVM started, and that'll cause out of balance errors
        clearPosterFileCache();
        return super.runBalancing();
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getPosterInputFile()
     */
    public File getPosterInputFile() {
        // avoid running scanning logic on file system
        if (laborPosterInputFile != null) {
            return laborPosterInputFile;
        }
        
        FilenameFilter filenameFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.startsWith(LaborConstants.BatchFileSystem.POSTER_INPUT_FILE) &&
                        name.endsWith(GeneralLedgerConstants.BatchFileSystem.EXTENSION));
            }
        };
        
        laborPosterInputFile = FileUtil.getNewestFile(new File(batchFileDirectoryName), filenameFilter);
        
        return laborPosterInputFile;
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getPosterErrorOutputFile()
     */
    public File getPosterErrorOutputFile() {
        // avoid running scanning logic on file system
        if (laborPosterErrorOutputFile != null) {
            return laborPosterErrorOutputFile;
        }
        
        FilenameFilter filenameFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.startsWith(LaborConstants.BatchFileSystem.POSTER_ERROR_OUTPUT_FILE) &&
                        name.endsWith(GeneralLedgerConstants.BatchFileSystem.EXTENSION));
            }
        };
        
        laborPosterErrorOutputFile = FileUtil.getNewestFile(new File(batchFileDirectoryName), filenameFilter);
        
        return laborPosterErrorOutputFile;
    }
    
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getPastFiscalYearsToConsider()
     */
    public int getPastFiscalYearsToConsider() {
        return Integer.parseInt(parameterService.getParameterValueAsString(LaborBalancingStep.class, LaborConstants.Balancing.NUMBER_OF_PAST_FISCAL_YEARS_TO_INCLUDE));
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getComparisonFailuresToPrintPerReport()
     */
    public int getComparisonFailuresToPrintPerReport() {
        return Integer.parseInt(parameterService.getParameterValueAsString(LaborBalancingStep.class, LaborConstants.Balancing.NUMBER_OF_COMPARISON_FAILURES_TO_PRINT_PER_REPORT));
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getShortTableLabel(java.lang.String)
     */
    public String getShortTableLabel(String businessObjectName) {
        Map<String, String> names = new HashMap<String, String>();
        names.put((Entry.class).getSimpleName(), kualiConfigurationService.getPropertyValueAsString(LaborKeyConstants.Balancing.REPORT_ENTRY_LABEL));
        names.put((LaborEntryHistory.class).getSimpleName(), kualiConfigurationService.getPropertyValueAsString(LaborKeyConstants.Balancing.REPORT_ENTRY_LABEL));
        names.put((Balance.class).getSimpleName(), kualiConfigurationService.getPropertyValueAsString(LaborKeyConstants.Balancing.REPORT_BALANCE_LABEL));
        names.put((LaborBalanceHistory.class).getSimpleName(), kualiConfigurationService.getPropertyValueAsString(LaborKeyConstants.Balancing.REPORT_BALANCE_LABEL));

        return names.get(businessObjectName) == null ? kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_UNKNOWN_LABEL) : names.get(businessObjectName);
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getOriginEntry(java.lang.String, int)
     */
    public OriginEntryInformation getOriginEntry(String inputLine, int lineNumber) {
        LaborOriginEntry originEntry = new LaborOriginEntry();
        originEntry.setFromTextFileForBatch(inputLine, lineNumber);
        
        return originEntry;
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.impl.BalancingServiceBaseImpl#updateHistoriesHelper(java.lang.Integer, java.lang.Integer, java.io.File, java.io.File)
     */
    protected int updateHistoriesHelper(Integer postMode, Integer startUniversityFiscalYear, File inputFile, File errorFile) {
        if(postMode == PosterService.MODE_ENTRIES){
            return super.updateHistoriesHelper(postMode, startUniversityFiscalYear, inputFile, errorFile);
        }
        return 0;
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#updateEntryHistory(org.kuali.kfs.gl.businessobject.OriginEntryInformation)
     * @see org.kuali.kfs.module.ld.batch.service.impl.LaborPosterServiceImpl#postAsLedgerEntry(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    public void updateEntryHistory(Integer postMode, OriginEntryInformation originEntry) {
        if(postMode == PosterService.MODE_ENTRIES){
            // TODO Retrieve and update 1 by 1? Is a HashMap or cache better so that storing only occurs once at the end?
            LaborOriginEntry laborOriginEntry = (LaborOriginEntry) originEntry;
            LaborEntryHistory ledgerEntryHistory = new LaborEntryHistory(laborOriginEntry);
    
            LaborEntryHistory retrievedLedgerEntryHistory = (LaborEntryHistory) businessObjectService.retrieve(ledgerEntryHistory);
            if(ObjectUtils.isNotNull(retrievedLedgerEntryHistory)) {
                ledgerEntryHistory = retrievedLedgerEntryHistory;
            }
            
            ledgerEntryHistory.addAmount(laborOriginEntry.getTransactionLedgerEntryAmount());
            
            businessObjectService.save(ledgerEntryHistory);
        }
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#updateBalanceHistory(org.kuali.kfs.gl.businessobject.OriginEntryInformation)
     * @see org.kuali.kfs.module.ld.batch.service.impl.LaborPosterServiceImpl#updateLedgerBalance(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    public void updateBalanceHistory(Integer postMode, OriginEntryInformation originEntry) {
        if(postMode == PosterService.MODE_ENTRIES){
            // TODO Retrieve and update 1 by 1? Is a HashMap or cache better so that storing only occurs once at the end?
            LaborOriginEntry laborOriginEntry = (LaborOriginEntry) originEntry;
            LaborBalanceHistory ledgerBalanceHistory = new LaborBalanceHistory(laborOriginEntry);
            
            LaborBalanceHistory retrievedLedgerBalanceHistory = (LaborBalanceHistory) businessObjectService.retrieve(ledgerBalanceHistory);
            if(ObjectUtils.isNotNull(retrievedLedgerBalanceHistory)) {
                ledgerBalanceHistory = retrievedLedgerBalanceHistory;
            }

            // Make sure the amount update properly recognized debit / credit logic. This is modeled after
            // LaborPosterServiceImpl#updateLedgerBalance
            KualiDecimal amount = laborOriginEntry.getTransactionLedgerEntryAmount();
            laborOriginEntry.refreshReferenceObject(KFSPropertyConstants.BALANCE_TYPE);
            laborOriginEntry.refreshReferenceObject(KFSPropertyConstants.OBJECT_TYPE);
            if (laborOriginEntry.getBalanceType().isFinancialOffsetGenerationIndicator()) {
                if (!laborOriginEntry.getTransactionDebitCreditCode().equals(laborOriginEntry.getObjectType().getFinObjectTypeDebitcreditCd())) {
                    amount = amount.negated();
                }
            }

            ledgerBalanceHistory.addAmount(laborOriginEntry.getUniversityFiscalPeriodCode(), amount);
            
            businessObjectService.save(ledgerBalanceHistory);
        }
    }
    
    /**
     * Compares entries in the Balance and BalanceHistory tables to ensure the amounts match.
     * @return count is compare failures
     */
    protected Integer compareBalanceHistory() {
        Integer countComparisionFailures = 0;
        
        String balanceTable = persistenceStructureService.getTableName(LedgerBalance.class);
        String historyTable = persistenceStructureService.getTableName(balanceHistoryPersistentClass);
        
        List<LedgerBalance> data = ledgerEntryBalanceCachingDao.compareBalanceHistory(balanceTable, historyTable, getFiscalYear());
        
        if (!data.isEmpty()) {
            for (Iterator<LedgerBalance> itr = data.iterator(); itr.hasNext();) {
                LaborBalanceHistory balance = createBalanceFromMap((Map<String, Object>)itr.next());
                countComparisionFailures++;
                if (countComparisionFailures <= this.getComparisonFailuresToPrintPerReport()) {
                    reportWriterService.writeError(balance, new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_RECORD_FAILED_BALANCING), Message.TYPE_WARNING, balance.getClass().getSimpleName()));
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
        
        String entryTable = persistenceStructureService.getTableName(LedgerEntry.class);
        String historyTable = persistenceStructureService.getTableName(entryHistoryPersistentClass);
        
        List<LedgerEntry> data = ledgerEntryBalanceCachingDao.compareEntryHistory(entryTable, historyTable, getFiscalYear());
        
        if (!data.isEmpty()) {
            for (Iterator<LedgerEntry> itr = data.iterator(); itr.hasNext();) {
                LaborEntryHistory entry = createEntryHistoryFromMap((Map<String, Object>)itr.next());
                countComparisionFailures++;
                if (countComparisionFailures <= this.getComparisonFailuresToPrintPerReport()) {
                    reportWriterService.writeError(entry, new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_RECORD_FAILED_BALANCING), Message.TYPE_WARNING, entry.getClass().getSimpleName()));
                }
                
            }
        }

        return countComparisionFailures;
    }
    
    /**
     * 
     * @see org.kuali.kfs.gl.batch.service.BalancingService#clearBalanceHistory()
     */
   
    public void clearHistories() {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        businessObjectService.deleteMatching(LaborEntryHistory.class, fieldValues);
        businessObjectService.deleteMatching(LaborBalanceHistory.class, fieldValues);
        
        reportWriterService.writeFormattedMessageLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_HISTORY_PURGED));
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getFilenames()
     */
    public String getFilenames() {
        return (this.laborPosterInputFile == null ? null : this.laborPosterInputFile.getName()) + "\n"
          + (this.laborPosterErrorOutputFile == null ? null : this.laborPosterErrorOutputFile.getName());
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getBalance(org.kuali.kfs.gl.businessobject.LedgerBalanceHistory)
     */
    public Balance getBalance(LedgerBalanceHistory ledgerBalanceHistory) {
        LedgerBalance ledgerBalance = new LedgerBalance((LaborBalanceHistory) ledgerBalanceHistory);
        return (LedgerBalance) businessObjectService.retrieve(ledgerBalance);
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#clearPosterFileCache()
     */
    public void clearPosterFileCache() {
        this.laborPosterInputFile = null;
        this.laborPosterErrorOutputFile = null;
    }
    
    protected LaborBalanceHistory createBalanceFromMap(Map<String, Object> map) {
        LaborBalanceHistory balance = new LaborBalanceHistory();
        balance.setUniversityFiscalYear(((BigDecimal)(map.get(GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR))).intValue());
        balance.setChartOfAccountsCode((String)map.get(GeneralLedgerConstants.ColumnNames.CHART_OF_ACCOUNTS_CODE));
        balance.setAccountNumber((String)map.get(GeneralLedgerConstants.ColumnNames.ACCOUNT_NUMBER));
        balance.setSubAccountNumber((String)map.get(GeneralLedgerConstants.ColumnNames.SUB_ACCOUNT_NUMBER));
        balance.setObjectCode((String)map.get(GeneralLedgerConstants.ColumnNames.OBJECT_CODE));
        balance.setSubObjectCode((String)map.get(GeneralLedgerConstants.ColumnNames.SUB_OBJECT_CODE));
        balance.setBalanceTypeCode((String)map.get(GeneralLedgerConstants.ColumnNames.BALANCE_TYPE_CODE));
        balance.setObjectTypeCode((String)map.get(GeneralLedgerConstants.ColumnNames.OBJECT_TYPE_CODE));
        balance.setEmplid((String)map.get(LaborConstants.ColumnNames.EMPLOYEE_IDENTIFIER));
        balance.setPositionNumber((String)map.get(LaborConstants.ColumnNames.POSITION_NUMBER));
        
        balance.setAccountLineAnnualBalanceAmount(convertBigDecimalToKualiDecimal((BigDecimal)map.get(GeneralLedgerConstants.ColumnNames.ACCOUNTING_LINE_ACTUALS_BALANCE_AMOUNT)));
        balance.setContractsGrantsBeginningBalanceAmount(convertBigDecimalToKualiDecimal((BigDecimal)map.get(GeneralLedgerConstants.ColumnNames.CONTRACT_AND_GRANTS_BEGINNING_BALANCE)));
        balance.setBeginningBalanceLineAmount(convertBigDecimalToKualiDecimal((BigDecimal)map.get(GeneralLedgerConstants.ColumnNames.BEGINNING_BALANCE)));
        balance.setMonth1Amount(convertBigDecimalToKualiDecimal((BigDecimal)map.get(GeneralLedgerConstants.ColumnNames.MONTH_1_ACCT_AMT)));
        balance.setMonth2Amount(convertBigDecimalToKualiDecimal((BigDecimal)map.get(GeneralLedgerConstants.ColumnNames.MONTH_2_ACCT_AMT)));
        balance.setMonth3Amount(convertBigDecimalToKualiDecimal((BigDecimal)map.get(GeneralLedgerConstants.ColumnNames.MONTH_3_ACCT_AMT)));
        balance.setMonth4Amount(convertBigDecimalToKualiDecimal((BigDecimal)map.get(GeneralLedgerConstants.ColumnNames.MONTH_4_ACCT_AMT)));
        balance.setMonth5Amount(convertBigDecimalToKualiDecimal((BigDecimal)map.get(GeneralLedgerConstants.ColumnNames.MONTH_5_ACCT_AMT)));
        balance.setMonth6Amount(convertBigDecimalToKualiDecimal((BigDecimal)map.get(GeneralLedgerConstants.ColumnNames.MONTH_6_ACCT_AMT)));
        balance.setMonth7Amount(convertBigDecimalToKualiDecimal((BigDecimal)map.get(GeneralLedgerConstants.ColumnNames.MONTH_7_ACCT_AMT)));
        balance.setMonth8Amount(convertBigDecimalToKualiDecimal((BigDecimal)map.get(GeneralLedgerConstants.ColumnNames.MONTH_8_ACCT_AMT)));
        balance.setMonth9Amount(convertBigDecimalToKualiDecimal((BigDecimal)map.get(GeneralLedgerConstants.ColumnNames.MONTH_9_ACCT_AMT)));
        balance.setMonth10Amount(convertBigDecimalToKualiDecimal((BigDecimal)map.get(GeneralLedgerConstants.ColumnNames.MONTH_10_ACCT_AMT)));
        balance.setMonth11Amount(convertBigDecimalToKualiDecimal((BigDecimal)map.get(GeneralLedgerConstants.ColumnNames.MONTH_11_ACCT_AMT)));
        balance.setMonth12Amount(convertBigDecimalToKualiDecimal((BigDecimal)map.get(GeneralLedgerConstants.ColumnNames.MONTH_12_ACCT_AMT)));
        balance.setMonth13Amount(convertBigDecimalToKualiDecimal((BigDecimal)map.get(GeneralLedgerConstants.ColumnNames.MONTH_13_ACCT_AMT)));
        
        return balance;
        
    }
    
    protected LaborEntryHistory createEntryHistoryFromMap(Map<String, Object> map) {
        LaborEntryHistory entry = new LaborEntryHistory();
        entry.setUniversityFiscalYear(((BigDecimal)(map.get(GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR))).intValue());
        entry.setChartOfAccountsCode((String)map.get(GeneralLedgerConstants.ColumnNames.CHART_OF_ACCOUNTS_CODE));
        entry.setFinancialObjectCode((String)map.get(GeneralLedgerConstants.ColumnNames.OBJECT_CODE));
        entry.setFinancialBalanceTypeCode((String)map.get(GeneralLedgerConstants.ColumnNames.BALANCE_TYPE_CODE));
        entry.setUniversityFiscalPeriodCode((String)map.get(GeneralLedgerConstants.ColumnNames.FISCAL_PERIOD_CODE));
        entry.setTransactionDebitCreditCode((String)map.get(GeneralLedgerConstants.ColumnNames.TRANSACTION_DEBIT_CREDIT_CD));
        entry.setTransactionLedgerEntryAmount(convertBigDecimalToKualiDecimal((BigDecimal)map.get(GeneralLedgerConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT)));
        
        return entry;
        
    }
    
    protected KualiDecimal convertBigDecimalToKualiDecimal(BigDecimal biggy) {
        if (ObjectUtils.isNull(biggy))
            return new KualiDecimal(0);   
        else 
            return new KualiDecimal(biggy);
    
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getReversalInputFile()
     */
    public File getReversalInputFile(){
        return null;
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getReversalErrorOutputFile()
     */
    public File getReversalErrorOutputFile(){
        return null;
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getICRInputFile()
     */
    public File getICRInputFile(){
        return null;
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getICRErrorOutputFile()
     */
    public File getICRErrorOutputFile(){
        return null;
    }

}
