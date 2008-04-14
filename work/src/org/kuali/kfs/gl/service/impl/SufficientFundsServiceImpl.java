/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.GeneralLedgerPostingDocument;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.chart.service.ObjectLevelService;
import org.kuali.module.chart.service.ObjectTypeService;
import org.kuali.module.financial.document.YearEndDocument;
import org.kuali.module.gl.bo.SufficientFundBalances;
import org.kuali.module.gl.bo.SufficientFundRebuild;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.SufficientFundBalancesDao;
import org.kuali.module.gl.dao.SufficientFundsDao;
import org.kuali.module.gl.service.SufficientFundRebuildService;
import org.kuali.module.gl.service.SufficientFundsService;
import org.kuali.module.gl.service.SufficientFundsServiceConstants;
import org.kuali.module.gl.util.SufficientFundsItem;
import org.springframework.transaction.annotation.Transactional;

/**
 * The base implementation of SufficientFundsService
 */
@Transactional
public class SufficientFundsServiceImpl implements SufficientFundsService, SufficientFundsServiceConstants {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundsServiceImpl.class);

    private AccountService accountService;
    private ObjectLevelService objectLevelService;
    private KualiConfigurationService kualiConfigurationService;
    private SufficientFundsDao sufficientFundsDao;
    private SufficientFundBalancesDao sufficientFundBalancesDao;
    private OptionsService optionsService;
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private SufficientFundRebuildService sufficientFundRebuildService;

    /**
     * Default constructor
     */
    public SufficientFundsServiceImpl() {
        super();
    }

    /**
     * This operation derives the acct_sf_finobj_cd which is used to populate the General Ledger Pending entry table, so that later
     * we can do Suff Fund checking against that entry
     * 
     * @param financialObject the object code being checked against
     * @param accountSufficientFundsCode the kind of sufficient funds checking turned on in this system
     * @return the object code that should be used for the sufficient funds inquiry, or a blank String
     * @see org.kuali.module.gl.service.SufficientFundsService#getSufficientFundsObjectCode(org.kuali.module.chart.bo.ObjectCode,
     *      java.lang.String)
     */
    public String getSufficientFundsObjectCode(ObjectCode financialObject, String accountSufficientFundsCode) {
        LOG.debug("getSufficientFundsObjectCode() started");

        financialObject.refreshNonUpdateableReferences();

        if (KFSConstants.SF_TYPE_NO_CHECKING.equals(accountSufficientFundsCode)) {
            return KFSConstants.NOT_AVAILABLE_STRING;
        }
        else if (KFSConstants.SF_TYPE_ACCOUNT.equals(accountSufficientFundsCode)) {
            return "    ";
        }
        else if (KFSConstants.SF_TYPE_CASH_AT_ACCOUNT.equals(accountSufficientFundsCode)) {
            return "    ";
        }
        else if (KFSConstants.SF_TYPE_OBJECT.equals(accountSufficientFundsCode)) {
            return financialObject.getFinancialObjectCode();
        }
        else if (KFSConstants.SF_TYPE_LEVEL.equals(accountSufficientFundsCode)) {
            return financialObject.getFinancialObjectLevelCode();
        }
        else if (KFSConstants.SF_TYPE_CONSOLIDATION.equals(accountSufficientFundsCode)) {
            return financialObject.getFinancialObjectLevel().getFinancialConsolidationObjectCode();
        }
        else {
            throw new IllegalArgumentException("Invalid Sufficient Funds Code: " + accountSufficientFundsCode);
        }
    }

    /**
     * Checks for sufficient funds on a single document
     * 
     * @param document document to check
     * @return Empty List if has sufficient funds for all accounts, List of SufficientFundsItem if not
     * @see org.kuali.module.gl.service.SufficientFundsService#checkSufficientFunds(org.kuali.core.document.FinancialDocument)
     */
    public List<SufficientFundsItem> checkSufficientFunds(GeneralLedgerPostingDocument document) {
        LOG.debug("checkSufficientFunds() started");

        return checkSufficientFunds((List<? extends Transaction>) document.getPendingLedgerEntriesForSufficientFundsChecking());
    }

    /**
     * checks to see if a document is a <code>YearEndDocument</code>
     * 
     * @param documentClass the class of a Document to check
     * @return true if the class implements <code>YearEndDocument</code>
     */
    private boolean isYearEndDocument(Class documentClass) {
        return YearEndDocument.class.isAssignableFrom(documentClass);
    }

    /**
     * Checks for sufficient funds on a list of transactions
     * 
     * @param transactions list of transactions
     * @return Empty List if has sufficient funds for all accounts, List of SufficientFundsItem if not
     * @see org.kuali.module.gl.service.SufficientFundsService#checkSufficientFunds(java.util.List)
     */
    public List<SufficientFundsItem> checkSufficientFunds(List<? extends Transaction> transactions) {
        LOG.debug("checkSufficientFunds() started");

        for (Transaction e : transactions) {
            e.refreshNonUpdateableReferences();
        }

        List<SufficientFundsItem> summaryItems = summarizeTransactions(transactions);
        for (Iterator iter = summaryItems.iterator(); iter.hasNext();) {
            SufficientFundsItem item = (SufficientFundsItem) iter.next();
            LOG.error("checkSufficientFunds() " + item.toString());
            if (hasSufficientFundsOnItem(item)) {
                iter.remove();
            }
        }

        return summaryItems;
    }

    /**
     * For each transaction, fetches the appropriate sufficient funds item to check against
     * 
     * @param transactions a list of Transactions
     * @return a List of corresponding SufficientFundsItem
     */
    private List<SufficientFundsItem> summarizeTransactions(List<? extends Transaction> transactions) {
        Map<String, SufficientFundsItem> items = new HashMap<String, SufficientFundsItem>();

        Options currentYear = optionsService.getCurrentYearOptions();

        for (Iterator iter = transactions.iterator(); iter.hasNext();) {
            Transaction tran = (Transaction) iter.next();

            Options year = tran.getOption();
            if (year == null) {
                year = currentYear;
            }
            if (ObjectUtils.isNull(tran.getAccount())) {
                throw new IllegalArgumentException("Invalid account: " + tran.getChartOfAccountsCode() + "-" + tran.getAccountNumber());
            }
            SufficientFundsItem sfi = new SufficientFundsItem(year, tran, getSufficientFundsObjectCode(tran.getFinancialObject(), tran.getAccount().getAccountSufficientFundsCode()));
            sfi.setDocumentTypeCode(tran.getFinancialDocumentTypeCode());

            if (items.containsKey(sfi.getKey())) {
                SufficientFundsItem item = (SufficientFundsItem) items.get(sfi.getKey());
                item.add(tran);
            }
            else {
                items.put(sfi.getKey(), sfi);
            }
        }

        return new ArrayList<SufficientFundsItem>(items.values());
    }

    /**
     * Given a sufficient funds item record, determines if there are sufficient funds available for the transaction
     * 
     * @param item the item to check
     * @return true if there are sufficient funds available, false otherwise
     */
    private boolean hasSufficientFundsOnItem(SufficientFundsItem item) {

        if (item.getAmount().equals(KualiDecimal.ZERO)) {
            LOG.debug("hasSufficientFundsOnItem() Transactions with zero amounts shold pass");
            return true;
        }

        if (!item.getYear().isBudgetCheckingOptionsCode()) {
            LOG.debug("hasSufficientFundsOnItem() No sufficient funds checking");
            return true;
        }

        if (!item.getAccount().isPendingAcctSufficientFundsIndicator()) {
            LOG.debug("hasSufficientFundsOnItem() No checking on eDocs for account " + item.getAccount().getChartOfAccountsCode() + "-" + item.getAccount().getAccountNumber());
            return true;
        }

        // exit sufficient funds checking if not enabled for an account
        if (KFSConstants.SF_TYPE_NO_CHECKING.equals(item.getAccountSufficientFundsCode())) {
            LOG.debug("hasSufficientFundsOnItem() sufficient funds not enabled for account " + item.getAccount().getChartOfAccountsCode() + "-" + item.getAccount().getAccountNumber());
            return true;
        }

        ObjectTypeService objectTypeService = (ObjectTypeService) SpringContext.getBean(ObjectTypeService.class);
        List<String> expenseObjectTypes = objectTypeService.getCurrentYearExpenseObjectTypes();

        if (KFSConstants.SF_TYPE_CASH_AT_ACCOUNT.equals(item.getAccount().getAccountSufficientFundsCode()) && !item.getFinancialObject().getChartOfAccounts().getFinancialCashObjectCode().equals(item.getFinancialObject().getFinancialObjectCode())) {
            LOG.debug("hasSufficientFundsOnItem() SF checking is cash and transaction is not cash");
            return true;
        }

        else if (!KFSConstants.SF_TYPE_CASH_AT_ACCOUNT.equals(item.getAccount().getAccountSufficientFundsCode()) && !expenseObjectTypes.contains(item.getFinancialObjectType().getCode())) {
            LOG.debug("hasSufficientFundsOnItem() SF checking is budget and transaction is not expense");
            return true;
        }

        SufficientFundBalances sfBalance = sufficientFundBalancesDao.getByPrimaryId(item.getYear().getUniversityFiscalYear(), item.getAccount().getChartOfAccountsCode(), item.getAccount().getAccountNumber(), item.getSufficientFundsObjectCode());

        if (sfBalance == null) {
            SufficientFundRebuild sufficientFundRebuild = sufficientFundRebuildService.getByAccount(item.getAccount().getChartOfAccountsCode(), item.getAccount().getAccountNumber());
            if (sufficientFundRebuild != null) {
                LOG.debug("hasSufficientFundsOnItem() No balance record and waiting on rebuild, no sufficient funds");
                return false;
            }
            else {
                sfBalance = new SufficientFundBalances();
                sfBalance.setAccountActualExpenditureAmt(KualiDecimal.ZERO);
                sfBalance.setAccountEncumbranceAmount(KualiDecimal.ZERO);
                sfBalance.setCurrentBudgetBalanceAmount(KualiDecimal.ZERO);
            }
        }

        KualiDecimal balanceAmount = item.getAmount();
        if (KFSConstants.SF_TYPE_CASH_AT_ACCOUNT.equals(item.getAccount().getAccountSufficientFundsCode()) || item.getYear().getBudgetCheckingBalanceTypeCd().equals(item.getBalanceTyp().getCode())) {
            // We need to change the sign on the amount because the amount in the item is an increase in cash. We only care
            // about decreases in cash.

            // Also, negating if this is a balance type code of budget checking and the transaction is a budget transaction.

            balanceAmount = balanceAmount.negated();
        }

        if (balanceAmount.isNegative()) {
            LOG.debug("hasSufficientFundsOnItem() balanceAmount is negative, allow transaction to proceed");
            return true;
        }

        PendingAmounts priorYearPending = new PendingAmounts();
        if ((KFSConstants.SF_TYPE_CASH_AT_ACCOUNT.equals(item.getAccount().getAccountSufficientFundsCode())) && (!item.getYear().isFinancialBeginBalanceLoadInd())) {
            priorYearPending = getPendingPriorYearBalanceAmount(item);
        }

        PendingAmounts pending = getPendingBalanceAmount(item);

        KualiDecimal availableBalance = null;
        if (KFSConstants.SF_TYPE_CASH_AT_ACCOUNT.equals(item.getAccount().getAccountSufficientFundsCode())) {
            if (!item.getYear().isFinancialBeginBalanceLoadInd()) {
                availableBalance = sfBalance.getCurrentBudgetBalanceAmount().add(priorYearPending.budget).add(pending.actual).subtract(sfBalance.getAccountEncumbranceAmount()).subtract(priorYearPending.encumbrance);
            }
            else {
                availableBalance = sfBalance.getCurrentBudgetBalanceAmount().add(pending.actual).subtract(sfBalance.getAccountEncumbranceAmount());
            }
        }
        else {
            availableBalance = sfBalance.getCurrentBudgetBalanceAmount().add(pending.budget).subtract(sfBalance.getAccountActualExpenditureAmt()).subtract(pending.actual).subtract(sfBalance.getAccountEncumbranceAmount()).subtract(pending.encumbrance);
        }

        LOG.debug("hasSufficientFundsOnItem() balanceAmount: " + balanceAmount + " availableBalance: " + availableBalance);
        if (balanceAmount.compareTo(availableBalance) > 0) {
            LOG.debug("hasSufficientFundsOnItem() no sufficient funds");
            return false;
        }

        LOG.debug("hasSufficientFundsOnItem() has sufficient funds");
        return true;
    }

    /**
     * An inner class to hold summary totals of pending ledger entry amounts
     */
    private class PendingAmounts {
        public KualiDecimal budget;
        public KualiDecimal actual;
        public KualiDecimal encumbrance;

        /**
         * Constructs a SufficientFundsServiceImpl.PendingAmounts instance
         */
        public PendingAmounts() {
            budget = KualiDecimal.ZERO;
            actual = KualiDecimal.ZERO;
            encumbrance = KualiDecimal.ZERO;
        }

    }

    /**
     * Given a sufficient funds item to check, gets the prior year sufficient funds balance to check against
     * 
     * @param item the sufficient funds item to check against
     * @return a PendingAmounts record with the pending budget and encumbrance
     */
    private PendingAmounts getPendingPriorYearBalanceAmount(SufficientFundsItem item) {
        LOG.debug("getPendingBalanceAmount() started");

        PendingAmounts amounts = new PendingAmounts();

        // This only gets called for sufficient funds type of Cash at Account (H). The object code in the table for this type is
        // always
        // 4 spaces.
        SufficientFundBalances bal = sufficientFundBalancesDao.getByPrimaryId(Integer.valueOf(item.getYear().getUniversityFiscalYear().intValue() - 1), item.getAccount().getChartOfAccountsCode(), item.getAccount().getAccountNumber(), "    ");

        if (bal != null) {
            amounts.budget = bal.getCurrentBudgetBalanceAmount();
            amounts.encumbrance = bal.getAccountEncumbranceAmount();
        }

        LOG.debug("getPendingPriorYearBalanceAmount() budget      " + amounts.budget);
        LOG.debug("getPendingPriorYearBalanceAmount() encumbrance " + amounts.encumbrance);
        return amounts;
    }

    /**
     * Totals the amounts of actual, encumbrance, and budget amounts from related pending entries
     * 
     * @param item a sufficient funds item to find pending amounts for
     * @return the totals encapsulated in a PendingAmounts object
     */
    private PendingAmounts getPendingBalanceAmount(SufficientFundsItem item) {
        LOG.debug("getPendingBalanceAmount() started");

        Integer fiscalYear = item.getYear().getUniversityFiscalYear();
        String chart = item.getAccount().getChartOfAccountsCode();
        String account = item.getAccount().getAccountNumber();
        String sfCode = item.getAccount().getAccountSufficientFundsCode();

        PendingAmounts amounts = new PendingAmounts();

        if (KFSConstants.SF_TYPE_CASH_AT_ACCOUNT.equals(sfCode)) {
            // Cash checking
            List years = new ArrayList();
            years.add(item.getYear().getUniversityFiscalYear());

            // If the beginning balance isn't loaded, we need to include cash from
            // the previous fiscal year
            if (!item.getYear().isFinancialBeginBalanceLoadInd()) {
                years.add(item.getYear().getUniversityFiscalYear() - 1);
            }

            // Calculate the pending actual amount
            // Get Cash (debit amount - credit amount)
            amounts.actual = generalLedgerPendingEntryService.getCashSummary(years, chart, account, true);
            amounts.actual = amounts.actual.subtract(generalLedgerPendingEntryService.getCashSummary(years, chart, account, false));

            // Get Payables (credit amount - debit amount)
            amounts.actual = amounts.actual.add(generalLedgerPendingEntryService.getActualSummary(years, chart, account, false));
            amounts.actual = amounts.actual.subtract(generalLedgerPendingEntryService.getActualSummary(years, chart, account, true));
        }
        else {
            // Non-Cash checking

            // Get expenditure (debit - credit)
            amounts.actual = generalLedgerPendingEntryService.getExpenseSummary(fiscalYear, chart, account, item.getSufficientFundsObjectCode(), true, item.getDocumentTypeCode().startsWith("YE"));
            amounts.actual = amounts.actual.subtract(generalLedgerPendingEntryService.getExpenseSummary(fiscalYear, chart, account, item.getSufficientFundsObjectCode(), false, item.getDocumentTypeCode().startsWith("YE")));

            // Get budget
            amounts.budget = generalLedgerPendingEntryService.getBudgetSummary(fiscalYear, chart, account, item.getSufficientFundsObjectCode(), item.getDocumentTypeCode().startsWith("YE"));

            // Get encumbrance (debit - credit)
            amounts.encumbrance = generalLedgerPendingEntryService.getEncumbranceSummary(fiscalYear, chart, account, item.getSufficientFundsObjectCode(), true, item.getDocumentTypeCode().startsWith("YE"));
            amounts.encumbrance = amounts.encumbrance.subtract(generalLedgerPendingEntryService.getEncumbranceSummary(fiscalYear, chart, account, item.getSufficientFundsObjectCode(), false, item.getDocumentTypeCode().startsWith("YE")));
        }

        LOG.debug("getPendingBalanceAmount() actual      " + amounts.actual);
        LOG.debug("getPendingBalanceAmount() budget      " + amounts.budget);
        LOG.debug("getPendingBalanceAmount() encumbrance " + amounts.encumbrance);
        return amounts;
    }

    /**
     * Determines the current sufficient funds
     * 
     * @param propertyNames not referenced in the method
     * @param universityFiscalYear the university fiscal year to check
     * @param chartOfAccountsCode the chart to check
     * @param accountNumber the account to check
     * @param sufficientFundsObjectCode the object code to check
     * @param amount the amount to check if sufficient funds exist for
     * @param documentClass the class of the document doing the check
     * @return true is sufficientFunds were found, false otherwise
     */
    private boolean checkSufficientFunds(List propertyNames, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String sufficientFundsObjectCode, KualiDecimal amount, Class documentClass) {
        // fp_sasfc:operation chk_suff_funds
        // I'm not certain this method is actually used currently
        if (universityFiscalYear == null) {
            throw new IllegalArgumentException("Invalid (null) universityFiscalYear");
        }
        Integer originalUniversityFiscalYear = universityFiscalYear;

        Account account = accountService.getByPrimaryId(chartOfAccountsCode, accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Invalid (null) account for: chartOfAccountsCode=" + chartOfAccountsCode + ";accountNumber=" + accountNumber);
        }
        boolean isYearEndDocument = isYearEndDocument(documentClass);
        // universityFiscalYear is universityFiscalYear-1 if year end document & chash
        // level checking
        if (isYearEndDocument && StringUtils.equals(KFSConstants.SF_TYPE_CASH_AT_ACCOUNT, account.getAccountSufficientFundsCode())) {
            universityFiscalYear = new Integer(originalUniversityFiscalYear.intValue() - 1);
        }

        // exit sufficient funds checking if not enabled for an account
        if (StringUtils.equals(KFSConstants.SF_TYPE_NO_CHECKING, account.getAccountSufficientFundsCode()) || !account.isPendingAcctSufficientFundsIndicator()) {
            LOG.debug("sufficient funds not enabled for account " + account.getAccountNumber());
            return true;
        }

        // fp_sasfc:19
        // retrieve gl sufficient fund balances for account
        SufficientFundBalances sfBalances = new SufficientFundBalances();
        sfBalances.setUniversityFiscalYear(universityFiscalYear);
        sfBalances.setChartOfAccountsCode(chartOfAccountsCode);
        sfBalances.setAccountNumber(accountNumber);
        if (!StringUtils.equals(KFSConstants.NOT_AVAILABLE_STRING, sufficientFundsObjectCode)) {
            sfBalances.setFinancialObjectCode(sufficientFundsObjectCode);
        }
        else if (StringUtils.equals(KFSConstants.SF_TYPE_ACCOUNT, account.getAccountSufficientFundsCode())) {
            // dont set anything for account level checking
        }
        else {
            sfBalances.setAccountSufficientFundsCode(KFSConstants.SF_TYPE_CASH_AT_ACCOUNT);
        }

        sfBalances = sufficientFundBalancesDao.getByPrimaryId(sfBalances.getUniversityFiscalYear(), sfBalances.getChartOfAccountsCode(), sfBalances.getAccountNumber(), sfBalances.getFinancialObjectCode());
        // fp_sasfc:32-1
        if (sfBalances == null) {
            sfBalances = new SufficientFundBalances();
            sfBalances.setCurrentBudgetBalanceAmount(KualiDecimal.ZERO);
            sfBalances.setAccountActualExpenditureAmt(KualiDecimal.ZERO);
            sfBalances.setAccountEncumbranceAmount(KualiDecimal.ZERO);
            sfBalances.setChartOfAccountsCode(chartOfAccountsCode);
            sfBalances.setAccountNumber(accountNumber);
            sfBalances.setAccountSufficientFundsCode(account.getAccountSufficientFundsCode());

        }
        // fp_sasfc;37
        // restore universityFiscalYear

        sfBalances.setUniversityFiscalYear(originalUniversityFiscalYear);

        // return calculatePLEBuckets(propertyNames, isYearEndDocument, amount, sufficientFundsObjectCode, sfBalances);
        return false;
    }

    /**
     * Purge the sufficient funds balance table by year/chart
     * 
     * @param chart the chart of sufficient fund balances to purge
     * @param year the fiscal year of sufficient fund balances to purge
     */
    public void purgeYearByChart(String chart, int year) {
        LOG.debug("setAccountService() started");
        sufficientFundsDao.purgeYearByChart(chart, year);
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setObjectLevelService(ObjectLevelService objectLevelService) {
        this.objectLevelService = objectLevelService;
    }

    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    public void setSufficientFundBalancesDao(SufficientFundBalancesDao sufficientFundBalancesDao) {
        this.sufficientFundBalancesDao = sufficientFundBalancesDao;
    }

    public void setSufficientFundsDao(SufficientFundsDao sufficientFundsDao) {
        this.sufficientFundsDao = sufficientFundsDao;
    }

    public void setSufficientFundRebuildService(SufficientFundRebuildService sufficientFundRebuildService) {
        this.sufficientFundRebuildService = sufficientFundRebuildService;
    }
}