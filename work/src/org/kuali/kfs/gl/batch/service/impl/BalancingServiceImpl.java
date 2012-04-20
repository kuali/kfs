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

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
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
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.gl.dataaccess.AccountBalanceDao;
import org.kuali.kfs.gl.dataaccess.BalancingDao;
import org.kuali.kfs.gl.dataaccess.EncumbranceDao;
import org.kuali.kfs.sys.FileUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;
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

    protected File reversalInputFile = null;
    protected File reversalErrorOutputFile = null;

    protected File icrInputFile = null;
    protected File icrErrorOutputFile = null;


    @Override
    public boolean runBalancing() {
        // clear out the file cache, otherwise, it won't update the history tables with the latest poster files
        // therefore, it will use the files that were first used when the balancing job was run when the JVM started, and that'll
        // cause out of balance errors
        clearPosterFileCache();
        return super.runBalancing();
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getPosterInputFile()
     */
    @Override
    public File getPosterInputFile() {
        // avoid running scanning logic on file system
        if (posterInputFile != null) {
            return posterInputFile;
        }
        return posterInputFile = getFile(
                GeneralLedgerConstants.BatchFileSystem.POSTER_INPUT_FILE,
                GeneralLedgerConstants.BatchFileSystem.EXTENSION);
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getPosterErrorOutputFile()
     */
    @Override
    public File getPosterErrorOutputFile() {
        // avoid running scanning logic on file system
        if (posterErrorOutputFile != null) {
            return posterErrorOutputFile;
        }
        return posterErrorOutputFile = getFile(
                GeneralLedgerConstants.BatchFileSystem.POSTER_ERROR_OUTPUT_FILE,
                GeneralLedgerConstants.BatchFileSystem.EXTENSION);
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getReversalInputFile()
     */
    @Override
    public File getReversalInputFile(){
        if (reversalInputFile != null) {
            return reversalInputFile;
        }
        return reversalInputFile = getFile(
                GeneralLedgerConstants.BatchFileSystem.REVERSAL_POSTER_VALID_OUTPUT_FILE,
                GeneralLedgerConstants.BatchFileSystem.EXTENSION);
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getReversalErrorOutputFile()
     */
    @Override
    public File getReversalErrorOutputFile(){
        if (reversalErrorOutputFile != null) {
            return reversalErrorOutputFile;
        }
        return reversalErrorOutputFile = getFile(
                GeneralLedgerConstants.BatchFileSystem.REVERSAL_POSTER_ERROR_OUTPUT_FILE,
                GeneralLedgerConstants.BatchFileSystem.EXTENSION);
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getICRInputFile()
     */
    @Override
    public File getICRInputFile(){
        if (icrInputFile != null) {
            return icrInputFile;
        }
        return icrInputFile = getFile(
                GeneralLedgerConstants.BatchFileSystem.ICR_POSTER_INPUT_FILE,
                GeneralLedgerConstants.BatchFileSystem.EXTENSION);
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getICRErrorOutputFile()
     */
    @Override
    public File getICRErrorOutputFile(){
        if (icrErrorOutputFile != null) {
            return icrErrorOutputFile;
        }
        return icrErrorOutputFile = getFile(
                GeneralLedgerConstants.BatchFileSystem.ICR_POSTER_ERROR_OUTPUT_FILE,
                GeneralLedgerConstants.BatchFileSystem.EXTENSION);
    }

    public File getFile(final String fileName, final String fileExtension){
        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.startsWith(fileName) && name.endsWith(fileExtension));
            }
        };
        return FileUtil.getNewestFile(new File(batchFileDirectoryName), filenameFilter);
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getPastFiscalYearsToConsider()
     */
    @Override
    public int getPastFiscalYearsToConsider() {
        return Integer.parseInt(parameterService.getParameterValueAsString(PosterBalancingStep.class, GeneralLedgerConstants.Balancing.NUMBER_OF_PAST_FISCAL_YEARS_TO_INCLUDE));
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getComparisonFailuresToPrintPerReport()
     */
    @Override
    public int getComparisonFailuresToPrintPerReport() {
        return Integer.parseInt(parameterService.getParameterValueAsString(PosterBalancingStep.class, GeneralLedgerConstants.Balancing.NUMBER_OF_COMPARISON_FAILURES_TO_PRINT_PER_REPORT));
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getShortTableLabel(java.lang.String)
     */
    @Override
    public String getShortTableLabel(String businessObjectName) {
        Map<String, String> names = new HashMap<String, String>();
        names.put((Entry.class).getSimpleName(), kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_ENTRY_LABEL));
        names.put((EntryHistory.class).getSimpleName(), kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_ENTRY_LABEL));
        names.put((Balance.class).getSimpleName(), kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_BALANCE_LABEL));
        names.put((BalanceHistory.class).getSimpleName(), kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_BALANCE_LABEL));
        names.put((AccountBalance.class).getSimpleName(), kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_ACCOUNT_BALANCE_LABEL));
        names.put((AccountBalanceHistory.class).getSimpleName(), kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_ACCOUNT_BALANCE_LABEL));
        names.put((Encumbrance.class).getSimpleName(), kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_ENCUMBRANCE_LABEL));
        names.put((EncumbranceHistory.class).getSimpleName(), kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_ENCUMBRANCE_LABEL));

        return names.get(businessObjectName) == null ? kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_UNKNOWN_LABEL) : names.get(businessObjectName);
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getOriginEntry(java.lang.String, int)
     */
    @Override
    public OriginEntryInformation getOriginEntry(String inputLine, int lineNumber) {
        // We need a OriginEntryFull because that's what updateBalanceHistory is looking for
        OriginEntryFull originEntry = new OriginEntryFull();
        originEntry.setFromTextFileForBatch(inputLine, lineNumber);

        return originEntry;
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#updateEntryHistory(org.kuali.kfs.gl.businessobject.OriginEntryInformation)
     * @see org.kuali.kfs.gl.batch.service.impl.PostEntry#post(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    @Override
    public void updateEntryHistory(Integer postMode, OriginEntryInformation originEntry) {
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
     * @see org.kuali.kfs.gl.batch.service.BalancingService#updateBalanceHistory(org.kuali.kfs.gl.businessobject.OriginEntryInformation)
     * @see org.kuali.kfs.gl.batch.service.impl.PostBalance#post(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    @Override
    public void updateBalanceHistory(Integer postMode, OriginEntryInformation originEntry) {
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
    @Override
    public Balance getBalance(LedgerBalanceHistory ledgerBalanceHistory) {
        Balance balance = new Balance((BalanceHistory) ledgerBalanceHistory);
        return (Balance) businessObjectService.retrieve(balance);
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#clearPosterFileCache()
     */
    @Override
    public void clearPosterFileCache() {
        this.posterInputFile = null;
        this.posterErrorOutputFile = null;
        this.reversalInputFile = null;
        this.reversalErrorOutputFile = null;
        this.icrInputFile = null;
        this.icrErrorOutputFile = null;
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

        reportWriterService.writeFormattedMessageLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_OBSOLETE_FISCAL_YEAR_DATA_DELETED), (AccountBalanceHistory.class).getSimpleName(), (EncumbranceHistory.class).getSimpleName(), fiscalYear);
        reportWriterService.writeNewLines(1);
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.impl.BalancingServiceBaseImpl#updateCustomHistory(org.kuali.kfs.gl.businessobject.OriginEntryInformation)
     */
    @Override
    protected void updateCustomHistory(Integer postMode, OriginEntryInformation originEntry) {
        this.updateAccountBalanceHistory(originEntry);
        this.updateEncumbranceHistory(originEntry);
    }


    /**
     * Update the account balance history table
     * @param originEntry representing the update details
     * @see org.kuali.kfs.gl.batch.service.impl.PostAccountBalance#post(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    protected void updateAccountBalanceHistory(OriginEntryInformation originEntry) {
        OriginEntryFull originEntryFull = (OriginEntryFull) originEntry;

        // As taken from PostAccountBalance#post: only post transactions where: balance type code is AC or CB or where object type
        // isn't FB and
        // balance type code is EX, IE, PE and CE
        originEntryFull.refreshReferenceObject(KFSPropertyConstants.OPTION);
        if ((originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getActualFinancialBalanceTypeCd()) || originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getBudgetCheckingBalanceTypeCd())) || (originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getExtrnlEncumFinBalanceTypCd()) || originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getIntrnlEncumFinBalanceTypCd()) || originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getPreencumbranceFinBalTypeCd()) || originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getCostShareEncumbranceBalanceTypeCd())) && (!originEntryFull.getFinancialObjectTypeCode().equals(originEntryFull.getOption().getFinObjectTypeFundBalanceCd()))) {
            // TODO Retrieve and update 1 by 1? Is a HashMap or cache better so that storing only occurs once at the end?
            AccountBalanceHistory accountBalanceHistory = new AccountBalanceHistory(originEntry);

            AccountBalanceHistory retrievedAccountBalanceHistory = (AccountBalanceHistory) businessObjectService.retrieve(accountBalanceHistory);
            if(ObjectUtils.isNotNull(retrievedAccountBalanceHistory)) {
                accountBalanceHistory = retrievedAccountBalanceHistory;
            }

            // Following is a copy of PostAccountBalance.updateAccountBalanceReturn since the blancing process is to do this
            // independently
            if (accountBalanceHistory.addAmount(originEntryFull)) {
                businessObjectService.save(accountBalanceHistory);
            }
        }
    }

    /**
     *
     * @see org.kuali.kfs.gl.batch.service.BalancingService#clearBalanceHistory()
     */

    @Override
    public void clearHistories() {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        businessObjectService.deleteMatching(EntryHistory.class, fieldValues);
        businessObjectService.deleteMatching(BalanceHistory.class, fieldValues);
        businessObjectService.deleteMatching(EncumbranceHistory.class, fieldValues);
        businessObjectService.deleteMatching(AccountBalanceHistory.class, fieldValues);

        reportWriterService.writeFormattedMessageLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_HISTORY_PURGED));
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getFilenames()
     */
    @Override
    public String getFilenames() {
        return (this.posterInputFile == null ? null : this.posterInputFile.getName()) + "\n"
          + (this.posterErrorOutputFile == null ? null : this.posterErrorOutputFile.getName()) + "\n"
          + (this.reversalInputFile == null ? null : this.reversalInputFile.getName()) + "\n"
          + (this.reversalErrorOutputFile == null ? null : this.reversalErrorOutputFile.getName()) + "\n"
          + (this.icrInputFile == null ? null : this.icrInputFile.getName()) + "\n"
          + (this.icrErrorOutputFile == null ? null : this.icrErrorOutputFile.getName());
    }

    /**
     * Compares entries in the Balance and BalanceHistory tables to ensure the amounts match.
     * @return count is compare failures
     */
    @Override
    protected Integer compareBalanceHistory() {
        Integer countComparisionFailures = 0;


        String balanceTable = persistenceStructureService.getTableName(Balance.class);
        String historyTable = persistenceStructureService.getTableName(balanceHistoryPersistentClass);

        List<Balance> data = ledgerEntryBalanceCachingDao.compareBalanceHistory(balanceTable, historyTable, getFiscalYear());

        if (!data.isEmpty()) {
            for (Iterator<Balance> itr = data.iterator(); itr.hasNext();) {
                BalanceHistory balance = createBalanceFromMap((Map<String, Object>)itr.next());
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
    @Override
    protected Integer compareEntryHistory() {
        Integer countComparisionFailures = 0;

        String entryTable = persistenceStructureService.getTableName(Entry.class);
        String historyTable = persistenceStructureService.getTableName(entryHistoryPersistentClass);

        List<Entry> data = ledgerEntryBalanceCachingDao.compareEntryHistory(entryTable, historyTable, getFiscalYear());

        if (!data.isEmpty()) {
            for (Iterator<Entry> itr = data.iterator(); itr.hasNext();) {
                EntryHistory entry = createEntryHistoryFromMap((Map<String, Object>)itr.next());
                countComparisionFailures++;
                if (countComparisionFailures <= this.getComparisonFailuresToPrintPerReport()) {
                    reportWriterService.writeError(entry, new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_RECORD_FAILED_BALANCING), Message.TYPE_WARNING, entry.getClass().getSimpleName()));
                }

            }
        }

        return countComparisionFailures;
    }

    /**
     * Update the encumbrance history table
     * @param originEntry representing the update details
     * @see org.kuali.kfs.gl.batch.service.impl.PostEncumbrance#post(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    protected void updateEncumbranceHistory(OriginEntryInformation originEntry) {
        OriginEntryFull originEntryFull = (OriginEntryFull) originEntry;

        // PostEncumbrance.verifyTransaction is not run because entries that fail that verification will be in the error poster file
        // which entries
        // are already ignored before being passed to this method.

        // As taken from PostEncumbrance#post: If the encumbrance update code is space or N, or the object type code is FB we don't
        // need to post an encumbrance
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

        String accountBalanceTable = persistenceStructureService.getTableName(AccountBalance.class);
        String historyTable = persistenceStructureService.getTableName(AccountBalanceHistory.class);


        List<AccountBalance> data = ledgerEntryBalanceCachingDao.accountBalanceCompareHistory(accountBalanceTable, historyTable, getFiscalYear());

        if (!data.isEmpty()) {
            for (Iterator<AccountBalance> itr = data.iterator(); itr.hasNext();) {
                AccountBalanceHistory accountBalanceHistory = createAccountBalanceHistoryFromMap((Map<String, Object>)itr.next());
                countComparisionFailures++;
                if (countComparisionFailures <= this.getComparisonFailuresToPrintPerReport()) {
                    reportWriterService.writeError(accountBalanceHistory, new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_RECORD_FAILED_BALANCING), Message.TYPE_WARNING, accountBalanceHistory.getClass().getSimpleName()));
                }
            }

        }
        else {
            reportWriterService.writeNewLines(1);
            reportWriterService.writeFormattedMessageLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_FAILURE_COUNT), (AccountBalanceHistory.class).getSimpleName(), countComparisionFailures, this.getComparisonFailuresToPrintPerReport());
        }
        return countComparisionFailures;
    }

    /**
     * Does comparision, error printing and returns failure count for encumbrances
     * @return failure count
     */
    protected Integer encumbranceCompareHistory() {
        Integer countComparisionFailures = 0;

        String encumbranceTable = persistenceStructureService.getTableName(Encumbrance.class);
        String historyTable = persistenceStructureService.getTableName(EncumbranceHistory.class);

        List<Encumbrance> data = ledgerEntryBalanceCachingDao.encumbranceCompareHistory(encumbranceTable, historyTable, getFiscalYear());

        if (!data.isEmpty()) {
            for (Iterator<Encumbrance> itr = data.iterator(); itr.hasNext();) {
                EncumbranceHistory encumbranceHistory = createEncumbranceHistoryFromMap((Map<String, Object>)itr.next());
                countComparisionFailures++;
                if (countComparisionFailures <= this.getComparisonFailuresToPrintPerReport()) {
                    reportWriterService.writeError(encumbranceHistory, new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_RECORD_FAILED_BALANCING), Message.TYPE_WARNING, encumbranceHistory.getClass().getSimpleName()));
                }
            }

        }
        else {
            reportWriterService.writeNewLines(1);
            reportWriterService.writeFormattedMessageLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_FAILURE_COUNT), (EncumbranceHistory.class).getSimpleName(), countComparisionFailures, this.getComparisonFailuresToPrintPerReport());
        }


        countComparisionFailures = data.size();

        return countComparisionFailures;
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.impl.BalancingServiceBaseImpl#customPrintRowCountHistory()
     */
    @Override
    protected void customPrintRowCountHistory(Integer fiscalYear) {
        // Note that fiscal year is passed as null for the History tables because for those we shouldn't have data prior to the
        // fiscal year anyway (and
        // if we do it's a bug that should be discovered)
        reportWriterService.writeStatisticLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_ACCOUNT_BALANCE_ROW_COUNT_HISTORY), this.getShortTableLabel((AccountBalanceHistory.class).getSimpleName()), "(" + (AccountBalanceHistory.class).getSimpleName() + ")", this.getHistoryCount(null, AccountBalanceHistory.class));
        reportWriterService.writeStatisticLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_ACCOUNT_BALANCE_ROW_COUNT_PRODUCTION), this.getShortTableLabel((AccountBalance.class).getSimpleName()), accountBalanceDao.findCountGreaterOrEqualThan(fiscalYear));
        reportWriterService.writeStatisticLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_ENCUMBRANCE_ROW_COUNT_HISTORY), this.getShortTableLabel((EncumbranceHistory.class).getSimpleName()), "(" + (EncumbranceHistory.class).getSimpleName() + ")", this.getHistoryCount(null, EncumbranceHistory.class));
        reportWriterService.writeStatisticLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_ENCUMBRANCE_ROW_COUNT_PRODUCTION), this.getShortTableLabel((Encumbrance.class).getSimpleName()), encumbranceDao.findCountGreaterOrEqualThan(fiscalYear));
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

    protected BalanceHistory createBalanceFromMap(Map<String, Object> map) {
        BalanceHistory balance = new BalanceHistory();
        balance.setUniversityFiscalYear(((BigDecimal)(map.get(GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR))).intValue());
        balance.setChartOfAccountsCode((String)map.get(GeneralLedgerConstants.ColumnNames.CHART_OF_ACCOUNTS_CODE));
        balance.setAccountNumber((String)map.get(GeneralLedgerConstants.ColumnNames.ACCOUNT_NUMBER));
        balance.setSubAccountNumber((String)map.get(GeneralLedgerConstants.ColumnNames.SUB_ACCOUNT_NUMBER));
        balance.setObjectCode((String)map.get(GeneralLedgerConstants.ColumnNames.OBJECT_CODE));
        balance.setSubObjectCode((String)map.get(GeneralLedgerConstants.ColumnNames.SUB_OBJECT_CODE));
        balance.setBalanceTypeCode((String)map.get(GeneralLedgerConstants.ColumnNames.BALANCE_TYPE_CODE));
        balance.setObjectTypeCode((String)map.get(GeneralLedgerConstants.ColumnNames.OBJECT_TYPE_CODE));

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

    protected EntryHistory createEntryHistoryFromMap(Map<String, Object> map) {
        EntryHistory entry = new EntryHistory();
        entry.setUniversityFiscalYear(((BigDecimal)(map.get(GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR))).intValue());
        entry.setChartOfAccountsCode((String)map.get(GeneralLedgerConstants.ColumnNames.CHART_OF_ACCOUNTS_CODE));
        entry.setFinancialObjectCode((String)map.get(GeneralLedgerConstants.ColumnNames.OBJECT_CODE));
        entry.setFinancialBalanceTypeCode((String)map.get(GeneralLedgerConstants.ColumnNames.BALANCE_TYPE_CODE));
        entry.setUniversityFiscalPeriodCode((String)map.get(GeneralLedgerConstants.ColumnNames.FISCAL_PERIOD_CODE));
       // entry.setFinancialObjectTypeCode((String)map.get(GeneralLedgerConstants.ColumnNames.OBJECT_TYPE_CODE));
        entry.setTransactionDebitCreditCode((String)map.get(GeneralLedgerConstants.ColumnNames.TRANSACTION_DEBIT_CREDIT_CD));
        entry.setTransactionLedgerEntryAmount(convertBigDecimalToKualiDecimal((BigDecimal)map.get(GeneralLedgerConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT)));

        return entry;

    }

    protected AccountBalanceHistory createAccountBalanceHistoryFromMap(Map<String, Object> map) {
        // UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, CURR_BDLN_BAL_AMT,
        // ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT
        AccountBalanceHistory accountBalanceHistory = new AccountBalanceHistory();
        accountBalanceHistory.setUniversityFiscalYear(((BigDecimal)(map.get(GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR))).intValue());
        accountBalanceHistory.setChartOfAccountsCode((String)map.get(GeneralLedgerConstants.ColumnNames.CHART_OF_ACCOUNTS_CODE));
        accountBalanceHistory.setAccountNumber((String)map.get(GeneralLedgerConstants.ColumnNames.ACCOUNT_NUMBER));
        accountBalanceHistory.setSubAccountNumber((String)map.get(GeneralLedgerConstants.ColumnNames.SUB_ACCOUNT_NUMBER));
        accountBalanceHistory.setObjectCode((String)map.get(GeneralLedgerConstants.ColumnNames.OBJECT_CODE));
        accountBalanceHistory.setSubObjectCode((String)map.get(GeneralLedgerConstants.ColumnNames.SUB_OBJECT_CODE));
        accountBalanceHistory.setCurrentBudgetLineBalanceAmount(convertBigDecimalToKualiDecimal((BigDecimal)map.get(GeneralLedgerConstants.ColumnNames.CURRENT_BUDGET_LINE_BALANCE_AMOUNT)));
        accountBalanceHistory.setAccountLineActualsBalanceAmount(convertBigDecimalToKualiDecimal((BigDecimal)map.get(GeneralLedgerConstants.ColumnNames.ACCOUNT_LINE_ACTUALS_BALANCE_AMOUNT)));
        accountBalanceHistory.setAccountLineEncumbranceBalanceAmount(convertBigDecimalToKualiDecimal((BigDecimal)map.get(GeneralLedgerConstants.ColumnNames.ACCOUNT_LINE_ENCUMBRANCE_BALANCE_AMOUNT)));


        return accountBalanceHistory;
    }

    protected EncumbranceHistory createEncumbranceHistoryFromMap(Map<String, Object> map) {
        EncumbranceHistory encumbranceHistory = new EncumbranceHistory();
        encumbranceHistory.setUniversityFiscalYear(((BigDecimal)(map.get(GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR))).intValue());
        encumbranceHistory.setChartOfAccountsCode((String)map.get(GeneralLedgerConstants.ColumnNames.CHART_OF_ACCOUNTS_CODE));
        encumbranceHistory.setAccountNumber((String)map.get(GeneralLedgerConstants.ColumnNames.ACCOUNT_NUMBER));
        encumbranceHistory.setSubAccountNumber((String)map.get(GeneralLedgerConstants.ColumnNames.SUB_ACCOUNT_NUMBER));
        encumbranceHistory.setObjectCode((String)map.get(GeneralLedgerConstants.ColumnNames.OBJECT_CODE));
        encumbranceHistory.setSubObjectCode((String)map.get(GeneralLedgerConstants.ColumnNames.SUB_OBJECT_CODE));
        encumbranceHistory.setBalanceTypeCode((String)map.get(GeneralLedgerConstants.ColumnNames.BALANCE_TYPE_CODE));
        encumbranceHistory.setDocumentTypeCode((String)map.get(GeneralLedgerConstants.ColumnNames.FINANCIAL_DOCUMENT_TYPE_CODE));
        encumbranceHistory.setOriginCode((String)map.get(GeneralLedgerConstants.ColumnNames.ORIGINATION_CODE));
        encumbranceHistory.setDocumentNumber((String)map.get(GeneralLedgerConstants.ColumnNames.DOCUMENT_NUMBER));
        encumbranceHistory.setAccountLineEncumbranceAmount(convertBigDecimalToKualiDecimal((BigDecimal)map.get(GeneralLedgerConstants.ColumnNames.ACCOUNT_LINE_ENCUMBRANCE_AMOUNT)));
        encumbranceHistory.setAccountLineEncumbranceClosedAmount(convertBigDecimalToKualiDecimal((BigDecimal)map.get(GeneralLedgerConstants.ColumnNames.ACCOUNT_LINE_ENCUMBRANCE_CLOSED_AMOUNT)));


        return encumbranceHistory;
    }

    protected KualiDecimal convertBigDecimalToKualiDecimal(BigDecimal biggy) {
        if (ObjectUtils.isNull(biggy))
            return new KualiDecimal(0);
        else
            return new KualiDecimal(biggy);

    }

}
