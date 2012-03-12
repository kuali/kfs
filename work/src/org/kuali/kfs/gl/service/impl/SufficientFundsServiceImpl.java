/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.ObjectLevelService;
import org.kuali.kfs.coa.service.ObjectTypeService;
import org.kuali.kfs.fp.document.YearEndDocument;
import org.kuali.kfs.gl.batch.dataaccess.SufficientFundsDao;
import org.kuali.kfs.gl.businessobject.SufficientFundBalances;
import org.kuali.kfs.gl.businessobject.SufficientFundRebuild;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.dataaccess.SufficientFundBalancesDao;
import org.kuali.kfs.gl.service.SufficientFundsService;
import org.kuali.kfs.gl.service.SufficientFundsServiceConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SufficientFundsItem;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.GeneralLedgerPostingDocument;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * The base implementation of SufficientFundsService
 */
@Transactional
public class SufficientFundsServiceImpl implements SufficientFundsService, SufficientFundsServiceConstants {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundsServiceImpl.class);

    private AccountService accountService;
    private ObjectLevelService objectLevelService;
    private ConfigurationService kualiConfigurationService;
    private SufficientFundsDao sufficientFundsDao;
    private SufficientFundBalancesDao sufficientFundBalancesDao;
    private OptionsService optionsService;
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private BusinessObjectService businessObjectService;

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
     * @see org.kuali.kfs.gl.service.SufficientFundsService#getSufficientFundsObjectCode(org.kuali.kfs.coa.businessobject.ObjectCode,
     *      java.lang.String)
     */
    public String getSufficientFundsObjectCode(ObjectCode financialObject, String accountSufficientFundsCode) {

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
            financialObject.refreshReferenceObject("financialObjectLevel");
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
     * @see org.kuali.kfs.gl.service.SufficientFundsService#checkSufficientFunds(org.kuali.rice.krad.document.FinancialDocument)
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
    @SuppressWarnings("unchecked")
    protected boolean isYearEndDocument(Class documentClass) {
        return YearEndDocument.class.isAssignableFrom(documentClass);
    }

    /**
     * Checks for sufficient funds on a list of transactions
     * 
     * @param transactions list of transactions
     * @return Empty List if has sufficient funds for all accounts, List of SufficientFundsItem if not
     * @see org.kuali.kfs.gl.service.SufficientFundsService#checkSufficientFunds(java.util.List)
     */
    @SuppressWarnings("unchecked")
    public List<SufficientFundsItem> checkSufficientFunds(List<? extends Transaction> transactions) {
        LOG.debug("checkSufficientFunds() started");

        for (Transaction e : transactions) {
            e.refreshNonUpdateableReferences();
        }

        List<SufficientFundsItem> summaryItems = summarizeTransactions(transactions);
        for (Iterator iter = summaryItems.iterator(); iter.hasNext();) {
            SufficientFundsItem item = (SufficientFundsItem) iter.next();
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("checkSufficientFunds() " + item.toString());
            }
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
    @SuppressWarnings("unchecked")
    protected List<SufficientFundsItem> summarizeTransactions(List<? extends Transaction> transactions) {
        Map<String, SufficientFundsItem> items = new HashMap<String, SufficientFundsItem>();

        SystemOptions currentYear = optionsService.getCurrentYearOptions();

        // loop over the given transactions, grouping into SufficientFundsItem objects
        // which are keyed by the appropriate chart/account/SF type, and derived object value
        // see getSufficientFundsObjectCode() for the "object" used for grouping
        for (Iterator iter = transactions.iterator(); iter.hasNext();) {
            Transaction tran = (Transaction) iter.next();

            SystemOptions year = tran.getOption();
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
    protected boolean hasSufficientFundsOnItem(SufficientFundsItem item) {

        if (item.getAmount().equals(KualiDecimal.ZERO)) {
            LOG.debug("hasSufficientFundsOnItem() Transactions with zero amounts shold pass");
            return true;
        }

        if (!item.getYear().isBudgetCheckingOptionsCode()) {
            LOG.debug("hasSufficientFundsOnItem() No sufficient funds checking");
            return true;
        }

        if (!item.getAccount().isPendingAcctSufficientFundsIndicator()) {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("hasSufficientFundsOnItem() No checking on eDocs for account " + item.getAccount().getChartOfAccountsCode() + "-" + item.getAccount().getAccountNumber());
            }
            return true;
        }

        // exit sufficient funds checking if not enabled for an account
        if (KFSConstants.SF_TYPE_NO_CHECKING.equals(item.getAccountSufficientFundsCode())) {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("hasSufficientFundsOnItem() sufficient funds not enabled for account " + item.getAccount().getChartOfAccountsCode() + "-" + item.getAccount().getAccountNumber());
            }
            return true;
        }

        ObjectTypeService objectTypeService = (ObjectTypeService) SpringContext.getBean(ObjectTypeService.class);
        List<String> expenseObjectTypes = objectTypeService.getCurrentYearExpenseObjectTypes();

        if (KFSConstants.SF_TYPE_CASH_AT_ACCOUNT.equals(item.getAccount().getAccountSufficientFundsCode()) 
                && !item.getFinancialObject().getChartOfAccounts().getFinancialCashObjectCode().equals(item.getFinancialObject().getFinancialObjectCode())) {
            LOG.debug("hasSufficientFundsOnItem() SF checking is cash and transaction is not cash");
            return true;
        }

        else if (!KFSConstants.SF_TYPE_CASH_AT_ACCOUNT.equals(item.getAccount().getAccountSufficientFundsCode()) 
                && !expenseObjectTypes.contains(item.getFinancialObjectType().getCode())) {
            LOG.debug("hasSufficientFundsOnItem() SF checking is budget and transaction is not expense");
            return true;
        }

        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, item.getYear().getUniversityFiscalYear());
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, item.getAccount().getChartOfAccountsCode());
        keys.put(KFSPropertyConstants.ACCOUNT_NUMBER, item.getAccount().getAccountNumber());
        keys.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, item.getSufficientFundsObjectCode());
        SufficientFundBalances sfBalance = (SufficientFundBalances)businessObjectService.findByPrimaryKey(SufficientFundBalances.class, keys);

        if (sfBalance == null) {
            Map criteria = new HashMap();
            criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, item.getAccount().getChartOfAccountsCode());
            criteria.put(KFSPropertyConstants.ACCOUNT_NUMBER_FINANCIAL_OBJECT_CODE, item.getAccount().getAccountNumber());
            
            Collection sufficientFundRebuilds = businessObjectService.findMatching(SufficientFundRebuild.class, criteria);
            if (sufficientFundRebuilds != null && sufficientFundRebuilds.size() > 0) {
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
        if (KFSConstants.SF_TYPE_CASH_AT_ACCOUNT.equals(item.getAccount().getAccountSufficientFundsCode()) 
                || item.getYear().getBudgetCheckingBalanceTypeCd().equals(item.getBalanceTyp().getCode())) {
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
        // if we're checking the CASH_AT_ACCOUNT type, then we need to consider the prior year pending transactions
        // if the balance forwards have not been run
        if ((KFSConstants.SF_TYPE_CASH_AT_ACCOUNT.equals(item.getAccount().getAccountSufficientFundsCode())) 
                && (!item.getYear().isFinancialBeginBalanceLoadInd())) {
            priorYearPending = getPriorYearSufficientFundsBalanceAmount(item);
        }

        PendingAmounts pending = getPendingBalanceAmount(item);

        KualiDecimal availableBalance = null;
        if (KFSConstants.SF_TYPE_CASH_AT_ACCOUNT.equals(item.getAccount().getAccountSufficientFundsCode())) {
            // if the beginning balances have not loaded for the transaction FY, pull the remaining balance from last year
            if (!item.getYear().isFinancialBeginBalanceLoadInd()) {
                availableBalance = sfBalance.getCurrentBudgetBalanceAmount()
                        .add(priorYearPending.budget) // add the remaining budget from last year (assumed to carry to this year's)
                        .add(pending.actual) // any pending expenses (remember sense is negated)
                        .subtract(sfBalance.getAccountEncumbranceAmount()) // subtract the encumbrances (not reflected in cash yet)
                        .subtract(priorYearPending.encumbrance);
            } else { // balance forwards have been run, don't need to consider prior year remaining budget 
                availableBalance = sfBalance.getCurrentBudgetBalanceAmount()
                        .add(pending.actual)
                        .subtract(sfBalance.getAccountEncumbranceAmount());
            }
        }
        else {
            availableBalance = sfBalance.getCurrentBudgetBalanceAmount() // current budget balance
                    .add(pending.budget) // pending budget entries
                    .subtract(sfBalance.getAccountActualExpenditureAmt()) // minus all current and pending actuals and encumbrances
                    .subtract(pending.actual)
                    .subtract(sfBalance.getAccountEncumbranceAmount())
                    .subtract(pending.encumbrance);
        }

        if ( LOG.isDebugEnabled() ) {
            LOG.debug("hasSufficientFundsOnItem() balanceAmount: " + balanceAmount + " availableBalance: " + availableBalance);
        }
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
    protected class PendingAmounts {
        public KualiDecimal budget = KualiDecimal.ZERO;
        public KualiDecimal actual = KualiDecimal.ZERO;
        public KualiDecimal encumbrance = KualiDecimal.ZERO;
    }

    /**
     * Given a sufficient funds item to check, gets the prior year sufficient funds balance to check against
     * 
     * @param item the sufficient funds item to check against
     * @return a PendingAmounts record with the pending budget and encumbrance
     */
    protected PendingAmounts getPriorYearSufficientFundsBalanceAmount(SufficientFundsItem item) {
        PendingAmounts amounts = new PendingAmounts();

        // This only gets called for sufficient funds type of Cash at Account (H). The object code in the table for this type is
        // always
        // 4 spaces.
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, Integer.valueOf(item.getYear().getUniversityFiscalYear().intValue() - 1));
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, item.getAccount().getChartOfAccountsCode());
        keys.put(KFSPropertyConstants.ACCOUNT_NUMBER, item.getAccount().getAccountNumber());
        keys.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, "    ");
        SufficientFundBalances bal = (SufficientFundBalances)businessObjectService.findByPrimaryKey(SufficientFundBalances.class, keys);

        if (bal != null) {
            amounts.budget = bal.getCurrentBudgetBalanceAmount();
            amounts.encumbrance = bal.getAccountEncumbranceAmount();
        }

        if ( LOG.isDebugEnabled() ) {
            LOG.debug("getPriorYearSufficientFundsBalanceAmount() budget      " + amounts.budget);
            LOG.debug("getPriorYearSufficientFundsBalanceAmount() encumbrance " + amounts.encumbrance);
        }
        return amounts;
    }

    /**
     * Totals the amounts of actual, encumbrance, and budget amounts from related pending entries
     * 
     * @param item a sufficient funds item to find pending amounts for
     * @return the totals encapsulated in a PendingAmounts object
     */
    @SuppressWarnings("unchecked")
    protected PendingAmounts getPendingBalanceAmount(SufficientFundsItem item) {
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
            amounts.actual = amounts.actual.add(generalLedgerPendingEntryService.getActualSummary(years, chart, account, true));
            amounts.actual = amounts.actual.subtract(generalLedgerPendingEntryService.getActualSummary(years, chart, account, false));
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

        if ( LOG.isDebugEnabled() ) {
            LOG.debug("getPendingBalanceAmount() actual      " + amounts.actual);
            LOG.debug("getPendingBalanceAmount() budget      " + amounts.budget);
            LOG.debug("getPendingBalanceAmount() encumbrance " + amounts.encumbrance);
        }
        return amounts;
    }

    /**
     * Purge the sufficient funds balance table by year/chart
     * 
     * @param chart the chart of sufficient fund balances to purge
     * @param year the fiscal year of sufficient fund balances to purge
     */
    public void purgeYearByChart(String chart, int year) {
        sufficientFundsDao.purgeYearByChart(chart, year);
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
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
    
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
