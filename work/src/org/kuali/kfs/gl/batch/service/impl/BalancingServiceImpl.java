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

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.PosterBalancingStep;
import org.kuali.kfs.gl.batch.service.BalancingService;
import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.gl.businessobject.AccountBalanceHistory;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.BalanceHistory;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.businessobject.EncumbranceHistory;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.EntryHistory;
import org.kuali.kfs.gl.businessobject.LedgerBalanceHistory;
import org.kuali.kfs.gl.businessobject.OriginEntry;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.dataaccess.AccountBalanceDao;
import org.kuali.kfs.gl.dataaccess.BalancingDao;
import org.kuali.kfs.gl.dataaccess.EncumbranceDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BalancingService of GL balancing
 */
@Transactional
public class BalancingServiceImpl extends BalancingServiceBaseImpl<EntryHistory, BalanceHistory> implements BalancingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalancingServiceImpl.class);
    
    protected BalancingDao balancingDao;
    protected AccountBalanceDao accountBalanceDao;
    protected EncumbranceDao encumbranceDao;
    
    protected File posterInputFile = null;
    protected File posterErrorOutputFile = null;
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getPosterInputFile()
     */
    public File getPosterInputFile() {
        // avoid running scanning logic on file system
        if (posterInputFile != null) {
            return posterInputFile;
        }
        
        FilenameFilter filenameFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.startsWith(GeneralLedgerConstants.BatchFileSystem.POSTER_INPUT_FILE) &&
                        name.endsWith(GeneralLedgerConstants.BatchFileSystem.EXTENSION));
            }
        };
        
        posterInputFile = this.getNewestDataFile(filenameFilter);
        
        return posterInputFile;
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getPosterErrorOutputFile()
     */
    public File getPosterErrorOutputFile() {
        // avoid running scanning logic on file system
        if (posterErrorOutputFile != null) {
            return posterErrorOutputFile;
        }
        
        FilenameFilter filenameFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.startsWith(GeneralLedgerConstants.BatchFileSystem.POSTER_ERROR_OUTPUT_FILE) &&
                        name.endsWith(GeneralLedgerConstants.BatchFileSystem.EXTENSION));
            }
        };
        
        posterErrorOutputFile = this.getNewestDataFile(filenameFilter);
        
        return posterErrorOutputFile;
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getPastFiscalYearsToConsider()
     */
    public int getPastFiscalYearsToConsider() {
        return Integer.parseInt(parameterService.getParameterValue(PosterBalancingStep.class, GeneralLedgerConstants.Balancing.NUMBER_OF_PAST_FISCAL_YEARS_TO_INCLUDE));
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getComparisonFailuresToPrintPerReport()
     */
    public int getComparisonFailuresToPrintPerReport() {
        return Integer.parseInt(parameterService.getParameterValue(PosterBalancingStep.class, GeneralLedgerConstants.Balancing.NUMBER_OF_COMPARISON_FAILURES_TO_PRINT_PER_REPORT));
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getShortTableLabel(java.lang.String)
     */
    public String getShortTableLabel(String businessObjectName) {
        Map<String, String> names = new HashMap<String, String>();
        names.put((Entry.class).getSimpleName(), kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_ENTRY_LABEL));
        names.put((EntryHistory.class).getSimpleName(), kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_ENTRY_LABEL));
        names.put((Balance.class).getSimpleName(), kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_BALANCE_LABEL));
        names.put((BalanceHistory.class).getSimpleName(), kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_BALANCE_LABEL));
        names.put((AccountBalance.class).getSimpleName(), kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_ACCOUNT_BALANCE_LABEL));
        names.put((AccountBalanceHistory.class).getSimpleName(), kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_ACCOUNT_BALANCE_LABEL));
        names.put((Encumbrance.class).getSimpleName(), kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_ENCUMBRANCE_LABEL));
        names.put((EncumbranceHistory.class).getSimpleName(), kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_ENCUMBRANCE_LABEL));
        
        return names.get(businessObjectName) == null ? kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_UNKNOWN_LABEL) : names.get(businessObjectName);
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getOriginEntry(java.lang.String, int)
     */
    public OriginEntry getOriginEntry(String inputLine, int lineNumber) {
        // We need a OriginEntryFull because that's what updateBalanceHistory is looking for
        OriginEntryFull originEntry = new OriginEntryFull();
        originEntry.setFromTextFileForBatch(inputLine, lineNumber);
        
        return originEntry;
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#updateEntryHistory(org.kuali.kfs.gl.businessobject.OriginEntry)
     * @see org.kuali.kfs.gl.batch.service.impl.PostEntry#post(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    public void updateEntryHistory(OriginEntry originEntry) {
        // TODO Retrieve and update 1 by 1? Is a HashMap or cache better so that storing only occurs once at the end?
        EntryHistory entryHistory = new EntryHistory(originEntry);

        EntryHistory retrievedEntryHistory = (EntryHistory) businessObjectService.retrieve(entryHistory);
        if(ObjectUtils.isNotNull(retrievedEntryHistory)) {
            entryHistory = retrievedEntryHistory;
        }
        
        entryHistory.addAmount(originEntry.getTransactionLedgerEntryAmount());
        
        businessObjectService.save(entryHistory);
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#updateBalanceHistory(org.kuali.kfs.gl.businessobject.OriginEntry)
     * @see org.kuali.kfs.gl.batch.service.impl.PostBalance#post(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    public void updateBalanceHistory(OriginEntry originEntry) {
        // TODO Retrieve and update 1 by 1? Is a HashMap or cache better so that storing only occurs once at the end?
        OriginEntryFull originEntryFull = (OriginEntryFull) originEntry;
        BalanceHistory balanceHistory = new BalanceHistory(originEntryFull);
        
        BalanceHistory retrievedBalanceHistory = (BalanceHistory) businessObjectService.retrieve(balanceHistory);
        if(ObjectUtils.isNotNull(retrievedBalanceHistory)) {
            balanceHistory = retrievedBalanceHistory;
        }
        
        KualiDecimal amount = originEntryFull.getTransactionLedgerEntryAmount();

        // Make sure the amount update properly recognized debit / credit logic. This is modeled after PostBalance#post
        originEntryFull.refreshReferenceObject(KFSPropertyConstants.BALANCE_TYPE);
        originEntryFull.refreshReferenceObject(KFSPropertyConstants.OBJECT_TYPE);
        if (originEntryFull.getBalanceType().isFinancialOffsetGenerationIndicator()) {
            if (!originEntryFull.getTransactionDebitCreditCode().equals(originEntryFull.getObjectType().getFinObjectTypeDebitcreditCd())) {
                amount = amount.negated();
            }
        }

        balanceHistory.addAmount(originEntryFull.getUniversityFiscalPeriodCode(), amount);
        
        businessObjectService.save(balanceHistory);
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getBalance(org.kuali.kfs.gl.businessobject.LedgerBalanceHistory)
     */
    public Balance getBalance(LedgerBalanceHistory ledgerBalanceHistory) {
        Balance balance = new Balance((BalanceHistory) ledgerBalanceHistory);
        return (Balance) businessObjectService.retrieve(balance);
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#clearPosterFileCache()
     */
    public void clearPosterFileCache() {
        this.posterInputFile = null;
        this.posterErrorOutputFile = null;
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.impl.BalancingServiceBaseImpl#customPopulateHistoryTables(java.lang.Integer)
     */
    @Override
    public void customPopulateHistoryTables(Integer fiscalYear) {
        balancingDao.populateAccountBalancesHistory(fiscalYear);
        balancingDao.populateEncumbranceHistory(fiscalYear);
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.impl.BalancingServiceBaseImpl#doesCustomHistoryExist(java.lang.Integer)
     */
    @Override
    protected boolean doesCustomHistoryExist(Integer fiscalYear) {
        return (this.getHistoryCount(fiscalYear, AccountBalanceHistory.class) > 0 &&
                this.getHistoryCount(fiscalYear, EncumbranceHistory.class) > 0);
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.impl.BalancingServiceBaseImpl#deleteCustomHistory(java.lang.Integer)
     */
    @Override
    protected void deleteCustomHistory(Integer fiscalYear) {
        deleteHistory(fiscalYear, AccountBalanceHistory.class);
        deleteHistory(fiscalYear, EncumbranceHistory.class);
        
        reportWriterService.printf(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_OBSOLETE_FISCAL_YEAR_DATA_DELETED), (AccountBalanceHistory.class).getSimpleName(), (EncumbranceHistory.class).getSimpleName(), fiscalYear);
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.impl.BalancingServiceBaseImpl#updateCustomHistory(org.kuali.kfs.gl.businessobject.OriginEntry)
     */
    @Override
    protected void updateCustomHistory(OriginEntry originEntry) {
        this.updateAccountBalanceHistory(originEntry);
        this.updateEncumbranceHistory(originEntry);
    }
    
    
    /**
     * Update the account balance history table
     * @param originEntry representing the update details
     * @see org.kuali.kfs.gl.batch.service.impl.PostAccountBalance#post(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    protected void updateAccountBalanceHistory(OriginEntry originEntry) {
        OriginEntryFull originEntryFull = (OriginEntryFull) originEntry;
        
        // As taken from PostAccountBalance#post: only post transactions where: balance type code is AC or CB or where object type isn't FB and
        // balance type code is EX, IE, PE and CE
        originEntryFull.refreshReferenceObject(KFSPropertyConstants.OPTION);
        if ((originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getActualFinancialBalanceTypeCd()) || originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getBudgetCheckingBalanceTypeCd())) || (originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getExtrnlEncumFinBalanceTypCd()) || originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getIntrnlEncumFinBalanceTypCd()) || originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getPreencumbranceFinBalTypeCd()) || originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getCostShareEncumbranceBalanceTypeCd())) && (!originEntryFull.getFinancialObjectTypeCode().equals(originEntryFull.getOption().getFinObjectTypeFundBalanceCd()))) {
            // TODO Retrieve and update 1 by 1? Is a HashMap or cache better so that storing only occurs once at the end?
            AccountBalanceHistory accountBalanceHistory = new AccountBalanceHistory(originEntry);
    
            AccountBalanceHistory retrievedAccountBalanceHistory = (AccountBalanceHistory) businessObjectService.retrieve(accountBalanceHistory);
            if(ObjectUtils.isNotNull(retrievedAccountBalanceHistory)) {
                accountBalanceHistory = retrievedAccountBalanceHistory;
            }
            
            // Following is a copy of PostAccountBalance.updateAccountBalanceReturn since the blancing process is to do this independently
            if (accountBalanceHistory.addAmount(originEntryFull)) {
                businessObjectService.save(accountBalanceHistory);
            }
        }
    }
    
    /**
     * Update the encumbrance history table
     * @param originEntry representing the update details
     * @see org.kuali.kfs.gl.batch.service.impl.PostEncumbrance#post(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    protected void updateEncumbranceHistory(OriginEntry originEntry) {
        OriginEntryFull originEntryFull = (OriginEntryFull) originEntry;
        
        // PostEncumbrance.verifyTransaction is not run because entries that fail that verification will be in the error poster file which entries
        // are already ignored before being passed to this method.
        
        // As taken from PostEncumbrance#post: If the encumbrance update code is space or N, or the object type code is FB we don't need to post an encumbrance
        originEntryFull.refreshReferenceObject(KFSPropertyConstants.OPTION);
        if ((StringUtils.isBlank(originEntryFull.getTransactionEncumbranceUpdateCode())) || " ".equals(originEntryFull.getTransactionEncumbranceUpdateCode()) || KFSConstants.ENCUMB_UPDT_NO_ENCUMBRANCE_CD.equals(originEntryFull.getTransactionEncumbranceUpdateCode()) || originEntryFull.getOption().getFinObjectTypeFundBalanceCd().equals(originEntryFull.getFinancialObjectTypeCode())) {
            return;
        }
        
        EncumbranceHistory encumbranceHistory = new EncumbranceHistory(originEntryFull);
        if (KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(originEntryFull.getTransactionEncumbranceUpdateCode())) {
            encumbranceHistory.setDocumentNumber(originEntryFull.getReferenceFinancialDocumentNumber());
            encumbranceHistory.setOriginCode(originEntryFull.getReferenceFinancialSystemOriginationCode());
            encumbranceHistory.setDocumentTypeCode(originEntryFull.getReferenceFinancialDocumentTypeCode());
        }
        // TODO Retrieve and update 1 by 1? Is a HashMap or cache better so that storing only occurs once at the end?
        EncumbranceHistory retrievedEncumbranceHistory = (EncumbranceHistory) businessObjectService.retrieve(encumbranceHistory);
        
        if(ObjectUtils.isNotNull(retrievedEncumbranceHistory)) {
            encumbranceHistory = retrievedEncumbranceHistory;
        }
        
        // Following is a copy & paste of PostEncumbrance.updateEncumbrance since the balancing process is to do this independently
        encumbranceHistory.addAmount(originEntryFull);
        
        businessObjectService.save(encumbranceHistory);
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.impl.BalancingServiceBaseImpl#customCompareHistory()
     */
    @Override
    protected Map<String, Integer> customCompareHistory() {
        Integer countAccountBalanceComparisionFailure = this.accountBalanceCompareHistory();
        Integer countEncumbranceComparisionFailure = this.encumbranceCompareHistory();
        
        // Using LinkedHashMap because we want it ordered
        Map<String, Integer> countMap = new LinkedHashMap<String, Integer>();
        countMap.put((AccountBalanceHistory.class).getSimpleName(), countAccountBalanceComparisionFailure);
        countMap.put((EncumbranceHistory.class).getSimpleName(), countEncumbranceComparisionFailure);
        
        return countMap;
    }
    
    /**
     * Does comparision, error printing and returns failure count for account balances
     * @return failure count
     */
    protected Integer accountBalanceCompareHistory() {
        Integer countComparisionFailures = 0;
        
        // TODO findAll might not be a good idea performance wise. Do some kind of LIMIT stepping?
        // Finding all history lines as starting point for comparison
        for (Iterator<AccountBalanceHistory> iterator = businessObjectService.findAll(AccountBalanceHistory.class).iterator(); iterator.hasNext();) {
            AccountBalanceHistory accountBalanceHistory = iterator.next();
            
            AccountBalance accountBalance = (AccountBalance) businessObjectService.retrieve(new AccountBalance(accountBalanceHistory));
            
            if (!accountBalanceHistory.compareAmounts(accountBalance)) {
                // Compare failed, properly log it if we havn't written more then TOTAL_COMPARISION_FAILURES_TO_PRINT yet
                countComparisionFailures++;
                if (countComparisionFailures <= this.getComparisonFailuresToPrintPerReport()) {
                    reportWriterService.writeError(accountBalanceHistory, new Message(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_RECORD_FAILED_BALANCING), Message.TYPE_WARNING, accountBalanceHistory.getClass().getSimpleName()));
                }
            }
        }
        
        if (countComparisionFailures != 0) {
            reportWriterService.printf(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_FAILURE_COUNT), (AccountBalanceHistory.class).getSimpleName(), countComparisionFailures, this.getComparisonFailuresToPrintPerReport());
        }
        
        return countComparisionFailures;
    }
    
    /**
     * Does comparision, error printing and returns failure count for encumbrances
     * @return failure count
     */
    protected Integer encumbranceCompareHistory() {
        Integer countComparisionFailures = 0;
        
        // TODO findAll might not be a good idea performance wise. Do some kind of LIMIT stepping?
        // Finding all history lines as starting point for comparison
        for (Iterator<EncumbranceHistory> iterator = businessObjectService.findAll(EncumbranceHistory.class).iterator(); iterator.hasNext();) {
            EncumbranceHistory encumbranceHistory = iterator.next();
            
            Encumbrance encumbrance = (Encumbrance) businessObjectService.retrieve(new Encumbrance(encumbranceHistory));
            
            if (!encumbranceHistory.compareAmounts(encumbrance)) {
                // Compare failed, properly log it if we havn't written more then TOTAL_COMPARISION_FAILURES_TO_PRINT yet
                countComparisionFailures++;
                if (countComparisionFailures <= this.getComparisonFailuresToPrintPerReport()) {
                    reportWriterService.writeError(encumbranceHistory, new Message(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_RECORD_FAILED_BALANCING), Message.TYPE_WARNING, encumbranceHistory.getClass().getSimpleName()));
                }
            }
        }
        
        if (countComparisionFailures != 0) {
            reportWriterService.printf(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_FAILURE_COUNT), (EncumbranceHistory.class).getSimpleName(), countComparisionFailures, this.getComparisonFailuresToPrintPerReport());
        }
        
        return countComparisionFailures;
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.impl.BalancingServiceBaseImpl#customPrintRowCountHistory()
     */
    @Override
    protected void customPrintRowCountHistory(Integer fiscalYear){
        // Note that fiscal year is passed as null for the History tables because for those we shouldn't have data prior to the fiscal year anyway (and
        // if we do it's a bug that should be discovered)
        reportWriterService.writeStatistic(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_ACCOUNT_BALANCE_ROW_COUNT_HISTORY), this.getShortTableLabel((AccountBalanceHistory.class).getSimpleName()), "(" + (AccountBalanceHistory.class).getSimpleName() + ")", this.getHistoryCount(null, AccountBalanceHistory.class));
        reportWriterService.writeStatistic(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_ACCOUNT_BALANCE_ROW_COUNT_PRODUCTION), this.getShortTableLabel((AccountBalance.class).getSimpleName()), accountBalanceDao.findCountGreaterOrEqualThan(fiscalYear));
        reportWriterService.writeStatistic(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_ENCUMBRANCE_ROW_COUNT_HISTORY), this.getShortTableLabel((EncumbranceHistory.class).getSimpleName()), "(" + (EncumbranceHistory.class).getSimpleName() + ")", this.getHistoryCount(null, EncumbranceHistory.class));
        reportWriterService.writeStatistic(kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_ENCUMBRANCE_ROW_COUNT_PRODUCTION), this.getShortTableLabel((Encumbrance.class).getSimpleName()), encumbranceDao.findCountGreaterOrEqualThan(fiscalYear));
    }
    
    /**
     * Sets the BalancingDao
     * 
     * @param balancingDao The BalancingDao to set.
     */
    public void setBalancingDao(BalancingDao balancingDao) {
        this.balancingDao = balancingDao;
    }
    
    /**
     * Sets the AccountBalanceDao
     * 
     * @param accountBalanceDao The AccountBalanceDao to set.
     */
    public void setAccountBalanceDao(AccountBalanceDao accountBalanceDao) {
        this.accountBalanceDao = accountBalanceDao;
    }
    
    /**
     * Sets the EncumbranceDao
     * 
     * @param encumbranceDao The EncumbranceDao to set.
     */
    public void setEncumbranceDao(EncumbranceDao encumbranceDao) {
        this.encumbranceDao = encumbranceDao;
    }
}
