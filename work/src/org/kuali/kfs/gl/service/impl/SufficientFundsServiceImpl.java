/*
 * Copyright (c) 2004, 2005 The National Association of College and University
 * Business Officers, Cornell University, Trustees of Indiana University,
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the
 * University of Arizona, and the r*smart group. Licensed under the Educational
 * Community License Version 1.0 (the "License"); By obtaining, using and/or
 * copying this Original Work, you agree that you have read, understand, and
 * will comply with the terms and conditions of the Educational Community
 * License. You may obtain a copy of the License at:
 * http://kualiproject.org/license.html THE SOFTWARE IS PROVIDED "AS IS",
 * WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.kuali.module.gl.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.user.Options;
import org.kuali.core.dao.OptionsDao;
import org.kuali.core.document.FinancialDocument;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.event.SufficientFundsCheckingPreparationEvent;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.service.OptionsService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.chart.service.ObjectLevelService;
import org.kuali.module.financial.document.YearEndDocument;
import org.kuali.module.financial.rules.TransferOfFundsDocumentRuleConstants;
import org.kuali.module.financial.rules.TransactionalDocumentRuleBaseConstants.APPLICATION_PARAMETER;
import org.kuali.module.financial.rules.TransactionalDocumentRuleBaseConstants.APPLICATION_PARAMETER_SECURITY_GROUP;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.module.gl.bo.SufficientFundBalances;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.SufficientFundBalancesDao;
import org.kuali.module.gl.dao.SufficientFundsDao;
import org.kuali.module.gl.service.SufficientFundsService;
import org.kuali.module.gl.service.SufficientFundsServiceConstants;
import org.kuali.module.gl.util.SufficientFundsItem;


/**
 * Sufficient Funds implementation
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class SufficientFundsServiceImpl implements SufficientFundsService, SufficientFundsServiceConstants {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundsServiceImpl.class);

    private AccountService accountService;
    private ObjectLevelService objectLevelService;
    private KualiConfigurationService kualiConfigurationService;
    private KualiRuleService kualiRuleService;
    private SufficientFundsDao sufficientFundsDao;
    private SufficientFundBalancesDao sufficientFundBalanceDao;
    private OptionsService optionsService;

    /**
     * Default constructor
     */
    public SufficientFundsServiceImpl() {
        super();
    }

    /**
     * 
     * @see org.kuali.module.gl.service.SufficientFundsService#getSufficientFundsObjectCode(org.kuali.module.chart.bo.ObjectCode, java.lang.String)
     */
    public String getSufficientFundsObjectCode(ObjectCode financialObject, String accountSufficientFundsCode) {
        LOG.debug("getSufficientFundsObjectCode() started");

        financialObject.refreshNonUpdateableReferences();

        if ( Constants.SF_TYPE_NO_CHECKING.equals(accountSufficientFundsCode) ) {
            return Constants.NOT_AVAILABLE_STRING;
        } else if ( Constants.SF_TYPE_ACCOUNT.equals(accountSufficientFundsCode) ) {
            return "    ";
        } else if ( Constants.SF_TYPE_CASH_AT_ACCOUNT.equals(accountSufficientFundsCode) ) {
            return "    ";
        } else if ( Constants.SF_TYPE_OBJECT.equals(accountSufficientFundsCode) ) {
            return financialObject.getFinancialObjectCode();            
        } else if ( Constants.SF_TYPE_LEVEL.equals(accountSufficientFundsCode) ) {
            return financialObject.getFinancialObjectLevelCode();
        } else if ( Constants.SF_TYPE_CONSOLIDATION.equals(accountSufficientFundsCode) ) {
            return financialObject.getFinancialObjectLevel().getFinancialConsolidationObjectCode();
        } else {
            throw new IllegalArgumentException("Invalid Sufficient Funds Code: " + accountSufficientFundsCode);
        }
    }

    /**
     * 
     * @see org.kuali.module.gl.service.SufficientFundsService#checkSufficientFunds(org.kuali.core.document.FinancialDocument)
     */
    public List<SufficientFundsItem> checkSufficientFunds(FinancialDocument document) {
        LOG.debug("checkSufficientFunds() started");

        // check for reversing entries generated by an error correction.
        if (StringUtils.isNotBlank(document.getDocumentHeader().getFinancialDocumentInErrorNumber())) {
            LOG.debug("checkSufficientFunds() Reversing documents are not checked for sufficient funds");
            return new ArrayList<SufficientFundsItem>();
        }

        List<GeneralLedgerPendingEntry> entries = document.getGeneralLedgerPendingEntries();
        for (GeneralLedgerPendingEntry e : entries) {
            e.refreshNonUpdateableReferences();
        }

        return checkSufficientFunds((List<? extends Transaction>)entries);
    }

    /**
     * checks to see if a document is a <code>YearEndDocument</code>
     * 
     * @param documentClass
     * @return true if the class implements <code>YearEndDocument</code>
     */
    private boolean isYearEndDocument(Class documentClass) {
        return YearEndDocument.class.isAssignableFrom(documentClass);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.SufficientFundsService#checkSufficientFunds(java.util.List)
     */
    public List<SufficientFundsItem> checkSufficientFunds(List<? extends Transaction> transactions) {
        LOG.debug("checkSufficientFunds() started");

        List<SufficientFundsItem> summaryItems = summarizeTransactions(transactions);
        for (Iterator iter = summaryItems.iterator(); iter.hasNext();) {
            SufficientFundsItem item = (SufficientFundsItem) iter.next();
            LOG.error("checkSufficientFunds() " + item.toString());
        }

        for (Iterator iter = summaryItems.iterator(); iter.hasNext();) {
            SufficientFundsItem item = (SufficientFundsItem) iter.next();
            if ( hasSufficientFundsOnItem(item) ) {
                iter.remove();
            }
        }

        return summaryItems;
    }

    private List<SufficientFundsItem> summarizeTransactions(List<? extends Transaction> transactions) {
        Map<String,SufficientFundsItem> items = new HashMap<String,SufficientFundsItem>();

        Options currentYear = optionsService.getCurrentYearOptions();

        for (Iterator iter = transactions.iterator(); iter.hasNext();) {
            Transaction tran = (Transaction) iter.next();

            Options year = tran.getOption();
            if ( year == null ) {
                year = currentYear;
            }
            if ( ObjectUtils.isNull(tran.getAccount()) ) {
                throw new IllegalArgumentException("Invalid account: " + tran.getChartOfAccountsCode() + "-" + tran.getAccountNumber());
            }
            SufficientFundsItem sfi = new SufficientFundsItem(year,tran,getSufficientFundsObjectCode(tran.getFinancialObject(), tran.getAccount().getAccountSufficientFundsCode()));

            if ( items.containsKey(sfi.getKey()) ) {
                SufficientFundsItem item = (SufficientFundsItem)items.get(sfi.getKey());
                item.add(tran);
            } else {
                items.put(sfi.getKey(), sfi);
            }
        }

        List<SufficientFundsItem> sfiList = new ArrayList<SufficientFundsItem>(items.values());
        return sfiList;
    }

    private boolean hasSufficientFundsOnItem(SufficientFundsItem item) {

        if (!StringUtils.equals(Constants.BUDGET_CHECKING_OPTIONS_CD_ACTIVE, item.getYear().getBudgetCheckingOptionsCode())) {
            LOG.debug("hasSufficientFundsOnItem() No sufficient funds checking");
            return true;
        }

        if ( ! item.getAccount().isPendingAcctSufficientFundsIndicator() ) {
            LOG.debug("hasSufficientFundsOnItem() No checking on eDocs for account " + item.getAccount().getChartOfAccountsCode() + "-" + item.getAccount().getAccountNumber() );
            return true;
        }

        // exit sufficient funds checking if not enabled for an account
        if ( Constants.SF_TYPE_NO_CHECKING.equals(item.getAccountSufficientFundsCode()) ) {
            LOG.debug("hasSufficientFundsOnItem() sufficient funds not enabled for account " + item.getAccount().getChartOfAccountsCode() + "-" + item.getAccount().getAccountNumber() );
            return true;
        }

        if ( Constants.SF_TYPE_CASH_AT_ACCOUNT.equals(item.getAccount().getAccountSufficientFundsCode())  && !item.getFinancialObject().getChartOfAccounts().getFinancialCashObjectCode().equals(item.getFinancialObject().getFinancialObjectCode())) {
            LOG.debug("hasSufficientFundsOnItem() SF checking is cash and transaction is not cash");
            return true;
        } else if (!Constants.SF_TYPE_CASH_AT_ACCOUNT.equals(item.getAccount().getAccountSufficientFundsCode()) 
                &&  kualiConfigurationService.getApplicationParameterRule(Constants.ParameterGroups.SYSTEM, Constants.SystemGroupParameterNames.SUFFICIENT_FINDS_EXPENSE_OBJECT_TYPES).failsRule(item.getFinancialObjectType().getCode())) {
            LOG.debug("hasSufficientFundsOnItem() SF checking is budget and transaction is not expense");
            return true;
        }

        SufficientFundBalances sfBalance = sufficientFundBalanceDao.getByPrimaryId(item.getYear().getUniversityFiscalYear(), item.getAccount().getChartOfAccountsCode(), item.getAccount().getAccountNumber(), item.getSufficientFundsObjectCode());

        if ( sfBalance == null ) {
            LOG.debug("hasSufficientFundsOnItem() No balance record, no sufficient funds");
            return false;
        }

        // TODO Handle year bb not loaded
        // TODO Handle pending

        KualiDecimal available = KualiDecimal.ZERO;
        if ( Constants.SF_TYPE_CASH_AT_ACCOUNT.equals(item.getAccount().getAccountSufficientFundsCode())) {
            // Cash checking
            available = sfBalance.getCurrentBudgetBalanceAmount().subtract(sfBalance.getAccountActualExpenditureAmt());
        } else {
            // Budget checking
            available = sfBalance.getCurrentBudgetBalanceAmount().subtract(sfBalance.getAccountActualExpenditureAmt()).subtract(sfBalance.getAccountEncumbranceAmount());
        }

        if ( item.getAmount().compareTo(available) > 0 ) {
            LOG.debug("hasSufficientFundsOnItem() no sufficient funds");
            return false;
        }

        LOG.debug("hasSufficientFundsOnItem() has sufficient funds");
        return true;
    }

    /**
     * fp_sasfc:operation chk_suff_funds
     * 
     * @param propertyNames
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param sufficientFundsObjectCode
     * @param amount
     * @param documentClass
     * @return true is sufficientFunds were found
     */
    private boolean checkSufficientFunds(List propertyNames, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String sufficientFundsObjectCode, KualiDecimal amount, Class documentClass) {

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
        if (isYearEndDocument && StringUtils.equals(Constants.SF_TYPE_CASH_AT_ACCOUNT, account.getAccountSufficientFundsCode())) {
            universityFiscalYear = new Integer(originalUniversityFiscalYear.intValue() - 1);
        }

        // exit sufficient funds checking if not enabled for an account
        if (StringUtils.equals(Constants.SF_TYPE_NO_CHECKING, account.getAccountSufficientFundsCode()) || !account.isPendingAcctSufficientFundsIndicator()) {
            LOG.debug( "sufficient funds not enabled for account " + account.getAccountNumber() );
            return true;
        }

        // fp_sasfc:19
        // retrieve gl sufficient fund balances for account
        SufficientFundBalances sfBalances = new SufficientFundBalances();
        sfBalances.setUniversityFiscalYear(universityFiscalYear);
        sfBalances.setChartOfAccountsCode(chartOfAccountsCode);
        sfBalances.setAccountNumber(accountNumber);
        if (!StringUtils.equals(Constants.NOT_AVAILABLE_STRING, sufficientFundsObjectCode)) {
            sfBalances.setFinancialObjectCode(sufficientFundsObjectCode);
        }
        else if (StringUtils.equals(Constants.SF_TYPE_ACCOUNT, account.getAccountSufficientFundsCode())) {
            // dont set anything for account level checking
        }
        else {
            sfBalances.setAccountSufficientFundsCode(Constants.SF_TYPE_CASH_AT_ACCOUNT);
        }

        sfBalances = sufficientFundBalanceDao.getByPrimaryId(sfBalances.getUniversityFiscalYear(), sfBalances.getChartOfAccountsCode(), sfBalances.getAccountNumber(), sfBalances.getFinancialObjectCode());
        // fp_sasfc:32-1
        if (sfBalances == null) {
            sfBalances = new SufficientFundBalances();
            sfBalances.setCurrentBudgetBalanceAmount(new KualiDecimal(0));
            sfBalances.setAccountActualExpenditureAmt(new KualiDecimal(0));
            sfBalances.setAccountEncumbranceAmount(new KualiDecimal(0));
            sfBalances.setChartOfAccountsCode(chartOfAccountsCode);
            sfBalances.setAccountNumber(accountNumber);
            sfBalances.setAccountSufficientFundsCode(account.getAccountSufficientFundsCode());

        }
        // fp_sasfc;37
        // restore universityFiscalYear

        sfBalances.setUniversityFiscalYear(originalUniversityFiscalYear);

//        return calculatePLEBuckets(propertyNames, isYearEndDocument, amount, sufficientFundsObjectCode, sfBalances);
        return false;
    }

    /**
     * fp_sasfc:38 calculates pending ledger entry buckets
     * 
     * @param propertyNames
     * @param isYearEndDocument
     * @param lineAmount
     * @param sufficientFundsObjectCode
     * @param sufficientFundBalances
     * @return true if sufficent funds were found
     */
//    private boolean calculatePLEBuckets(List propertyNames, boolean isYearEndDocument, KualiDecimal lineAmount, String sufficientFundsObjectCode, SufficientFundBalances sufficientFundBalances) {
//        Integer universityFiscalYear = sufficientFundBalances.getUniversityFiscalYear();
//        String chartOfAccountsCode = sufficientFundBalances.getChartOfAccountsCode();
//        String accountNumber = sufficientFundBalances.getAccountNumber();
//        String accountSufficientFundsCode = sufficientFundBalances.getAccountSufficientFundsCode();
//        KualiDecimal pendingActual = null;
//        KualiDecimal pendingEncumb = null;
//        KualiDecimal pendingBudget = null;
//        KualiDecimal pfyrBudget = null;
//        KualiDecimal pfyrEncum = null;
//
//        // retrieve system options
//        Options options = optionsService.getOptions(universityFiscalYear);
//        // cash level checking
//        if (StringUtils.equals(Constants.SF_TYPE_CASH_AT_ACCOUNT, accountSufficientFundsCode)) {
//            // fp_sasfc:48-2...79-2
//            if (!options.isFinancialBeginBalanceLoadInd()) {
//                pfyrBudget = sufficientFundsDao.calculateM113PfyrBudget(universityFiscalYear, chartOfAccountsCode, accountNumber);
//                pfyrEncum = sufficientFundsDao.calculateM113PfyrEncum(universityFiscalYear, chartOfAccountsCode, accountNumber);
//            }
//            pendingActual = sufficientFundsDao.calculateM113PendActual(options.isFinancialBeginBalanceLoadInd(), universityFiscalYear, chartOfAccountsCode, accountNumber, getSpecialFinancialObjectCodes(), getFinancialObjectCodeForCashInBank());
//        }
//        // non cash
//        else {
//            // fp_sasfc:99-1...167-1
//            pendingActual = sufficientFundsDao.calculatePendActual(isYearEndDocument, options.getActualFinancialBalanceTypeCd(), universityFiscalYear, chartOfAccountsCode, accountNumber, accountSufficientFundsCode, getExpenditureCodes());
//            pendingBudget = sufficientFundsDao.calculatePendBudget(isYearEndDocument, options.getBudgetCheckingBalanceTypeCd(), universityFiscalYear, chartOfAccountsCode, accountNumber, accountSufficientFundsCode, getExpenditureCodes());
//            pendingEncumb = sufficientFundsDao.calculatePendEncum(isYearEndDocument, options.getExtrnlEncumFinBalanceTypCd(), options.getIntrnlEncumFinBalanceTypCd(), options.getPreencumbranceFinBalTypeCd(), universityFiscalYear, chartOfAccountsCode, accountNumber, accountSufficientFundsCode, getExpenditureCodes());
//        }
//        //
//        return hasSufficientFunds(propertyNames, options.isFinancialBeginBalanceLoadInd(), lineAmount, accountSufficientFundsCode, pfyrBudget, pfyrEncum, pendingActual, pendingBudget, pendingEncumb, sufficientFundBalances, sufficientFundsObjectCode);
//    }


    //                 GlobalVariables.getErrorMap().putError((String) i.next(), KeyConstants.SufficientFunds.ERROR_INSUFFICIENT_FUNDS, errorParameters);

    /**
     * Purge the sufficient funds balance table by year/chart
     * 
     * @param chart
     * @param year
     */
    public void purgeYearByChart(String chart, int year) {
        LOG.debug("setAccountService() started");
        sufficientFundsDao.purgeYearByChart(chart, year);
    }

    // spring injected services
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setObjectLevelService(ObjectLevelService objectLevelService) {
        this.objectLevelService = objectLevelService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setSufficientFundsDao(SufficientFundsDao sufficientFundsDao) {
        this.sufficientFundsDao = sufficientFundsDao;
    }

    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    public void setOptionsService(OptionsService os) {
        this.optionsService = os;
    }

    public void setSufficientFundBalancesDao(SufficientFundBalancesDao sufficientFundBalanceDao) {
        this.sufficientFundBalanceDao = sufficientFundBalanceDao;
    }
}