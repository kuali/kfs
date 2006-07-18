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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.user.Options;
import org.kuali.core.dao.OptionsDao;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.event.SufficientFundsCheckingPreparationEvent;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.chart.service.ObjectLevelService;
import org.kuali.module.financial.document.YearEndDocument;
import org.kuali.module.financial.rules.TransferOfFundsDocumentRuleConstants;
import org.kuali.module.financial.rules.TransactionalDocumentRuleBaseConstants.APPLICATION_PARAMETER;
import org.kuali.module.financial.rules.TransactionalDocumentRuleBaseConstants.APPLICATION_PARAMETER_SECURITY_GROUP;
import org.kuali.module.gl.bo.SufficientFundBalances;
import org.kuali.module.gl.dao.SufficientFundBalancesDao;
import org.kuali.module.gl.dao.SufficientFundsDao;
import org.kuali.module.gl.service.SufficientFundsService;
import org.kuali.module.gl.service.SufficientFundsServiceConstants;
import org.kuali.module.gl.util.SufficientFundsItemHelper.SufficientFundsItem;

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
    private OptionsDao optionsDao;

    /**
     * Default constructor
     */
    public SufficientFundsServiceImpl() {
        super();
    }

    /**
     * 
     * @see org.kuali.module.gl.service.SufficientFundsService#getSufficientFundsObjectCode(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public String getSufficientFundsObjectCode(String chartOfAccountsCode, String financialObjectCode, String accountSufficientFundsCode, String financialObjectLevelCode) {
        // fp_sasfc: operation get_sf_object_cd. 1-1...23
        String sfObjectCode = null;
        if (StringUtils.equals(Constants.SF_TYPE_OBJECT, accountSufficientFundsCode)) {
            sfObjectCode = financialObjectCode;
        }
        else if (StringUtils.equals(Constants.SF_TYPE_LEVEL, accountSufficientFundsCode)) {
            sfObjectCode = financialObjectLevelCode;
        }
        else if (StringUtils.equals(Constants.SF_TYPE_CONSOLIDATION, accountSufficientFundsCode)) {
            ObjLevel objectLevel = objectLevelService.getByPrimaryId(chartOfAccountsCode, financialObjectLevelCode);
            if (objectLevel == null) {
                throw new IllegalArgumentException("Invalid (null) objLevel for chartOfAccountsCode=" + chartOfAccountsCode + ", financialObjectLevelCode=" + financialObjectLevelCode);
            }
            sfObjectCode = objectLevel.getFinancialConsolidationObjectCode();
        }
        else {
            sfObjectCode = Constants.NOT_AVAILABLE_STRING;
        }
        return sfObjectCode;
    }

    /**
     * @see org.kuali.module.gl.service.SufficientFundsService#checkSufficientFunds(org.kuali.core.document.TransactionalDocument)
     */
    public boolean checkSufficientFunds(TransactionalDocument transactionalDocument) {

        // if global budget checking is not true then return
        if (!StringUtils.equals(Constants.BUDGET_CHECKING_OPTIONS_CD_ACTIVE, optionsDao.getByPrimaryId(transactionalDocument.getPostingYear()).getBudgetCheckingOptionsCode())) {
            LOG.debug( "global budget checking is not true" );
            return true;
        }
        // check for reversing entries generated by an error correction.
        if (StringUtils.isNotBlank(transactionalDocument.getDocumentHeader().getFinancialDocumentInErrorNumber())) {
            LOG.debug( "reversing entries generated by an error correction exist" );
            return true;
        }

        SufficientFundsCheckingPreparationEvent event = new SufficientFundsCheckingPreparationEvent(transactionalDocument);
        boolean isSufficientFunds = true;
        kualiRuleService.applyRules(event);
        // call check sf array
        for (Iterator i = event.getSufficientFundsItemHelper().iterator(); i.hasNext() && isSufficientFunds;) {
            SufficientFundsItem item = (SufficientFundsItem) i.next();
            if (item.getAmount().isPositive()) {
                isSufficientFunds &= checkSufficientFunds(item.getPropertyNames(), item.getFiscalYear(), item.getChartOfAccountsCode(), item.getAccountNumber(), item.getSufficientFundsObjectCode(), item.getAmount(), transactionalDocument.getClass());
            }
        }

        return isSufficientFunds;
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

        return calculatePLEBuckets(propertyNames, isYearEndDocument, amount, sufficientFundsObjectCode, sfBalances);
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
    private boolean calculatePLEBuckets(List propertyNames, boolean isYearEndDocument, KualiDecimal lineAmount, String sufficientFundsObjectCode, SufficientFundBalances sufficientFundBalances) {
        Integer universityFiscalYear = sufficientFundBalances.getUniversityFiscalYear();
        String chartOfAccountsCode = sufficientFundBalances.getChartOfAccountsCode();
        String accountNumber = sufficientFundBalances.getAccountNumber();
        String accountSufficientFundsCode = sufficientFundBalances.getAccountSufficientFundsCode();
        KualiDecimal pendingActual = null;
        KualiDecimal pendingEncumb = null;
        KualiDecimal pendingBudget = null;
        KualiDecimal pfyrBudget = null;
        KualiDecimal pfyrEncum = null;

        // retrieve system options
        Options options = optionsDao.getByPrimaryId(universityFiscalYear);
        // cash level checking
        if (StringUtils.equals(Constants.SF_TYPE_CASH_AT_ACCOUNT, accountSufficientFundsCode)) {
            // fp_sasfc:48-2...79-2
            if (!options.isFinancialBeginBalanceLoadInd()) {
                pfyrBudget = sufficientFundsDao.calculateM113PfyrBudget(universityFiscalYear, chartOfAccountsCode, accountNumber);
                pfyrEncum = sufficientFundsDao.calculateM113PfyrEncum(universityFiscalYear, chartOfAccountsCode, accountNumber);
            }
            pendingActual = sufficientFundsDao.calculateM113PendActual(options.isFinancialBeginBalanceLoadInd(), universityFiscalYear, chartOfAccountsCode, accountNumber, getSpecialFinancialObjectCodes(), getFinancialObjectCodeForCashInBank());
        }
        // non cash
        else {
            // fp_sasfc:99-1...167-1
            pendingActual = sufficientFundsDao.calculatePendActual(isYearEndDocument, options.getActualFinancialBalanceTypeCd(), universityFiscalYear, chartOfAccountsCode, accountNumber, accountSufficientFundsCode, getExpenditureCodes());
            pendingBudget = sufficientFundsDao.calculatePendBudget(isYearEndDocument, options.getBudgetCheckingBalanceTypeCd(), universityFiscalYear, chartOfAccountsCode, accountNumber, accountSufficientFundsCode, getExpenditureCodes());
            pendingEncumb = sufficientFundsDao.calculatePendEncum(isYearEndDocument, options.getExtrnlEncumFinBalanceTypCd(), options.getIntrnlEncumFinBalanceTypCd(), options.getPreencumbranceFinBalTypeCd(), universityFiscalYear, chartOfAccountsCode, accountNumber, accountSufficientFundsCode, getExpenditureCodes());
        }
        //
        return hasSufficientFunds(propertyNames, options.isFinancialBeginBalanceLoadInd(), lineAmount, accountSufficientFundsCode, pfyrBudget, pfyrEncum, pendingActual, pendingBudget, pendingEncumb, sufficientFundBalances, sufficientFundsObjectCode);
    }

    /**
     * fp_sasfc:operation chk_suff_funds. 168-1...183 preforms the actual check to see if there is sufficient funds in an account
     * 
     * @param propertyNames
     * @param financialBeginBalanceLoadInd
     * @param lineAmount
     * @param sufficientFundsCode
     * @param pfyrBudget
     * @param pfyrEncum
     * @param pendingActual
     * @param pendingBudget
     * @param pendingEncumb
     * @param sufficientFundBalances
     * @param sufficientFundsObjectCode
     * @return true if sufficient funds where found
     */
    private boolean hasSufficientFunds(List propertyNames, boolean financialBeginBalanceLoadInd, KualiDecimal lineAmount, String sufficientFundsCode, KualiDecimal pfyrBudget, KualiDecimal pfyrEncum, KualiDecimal pendingActual, KualiDecimal pendingBudget, KualiDecimal pendingEncumb, SufficientFundBalances sufficientFundBalances, String sufficientFundsObjectCode) {
        KualiDecimal available = new KualiDecimal(0);


        if (StringUtils.equals(Constants.SF_TYPE_CASH_AT_ACCOUNT, sufficientFundsCode)) {
            if (financialBeginBalanceLoadInd) {
                // fp_sasfc: 172-2
                available = sufficientFundBalances.getCurrentBudgetBalanceAmount().add(pendingActual).subtract(sufficientFundBalances.getAccountEncumbranceAmount());
            }
            else {
                // fp_sasfc:170-2
                available = sufficientFundBalances.getCurrentBudgetBalanceAmount().add(pfyrBudget).add(pendingActual).subtract(sufficientFundBalances.getAccountEncumbranceAmount().add(pfyrEncum));
            }
        }
        else {
            // non cash
            // fp_sasfc:175-1
            available = sufficientFundBalances.getCurrentBudgetBalanceAmount().add(pendingBudget).subtract(sufficientFundBalances.getAccountActualExpenditureAmt().add(pendingActual).add(sufficientFundBalances.getAccountEncumbranceAmount()).add(pendingEncumb));
        }
        boolean isSufficient = available.isGreaterThan(lineAmount);
        if (!isSufficient) {
            // fp_sasfc:operation check_sf_array. 10-4
            // create error message parameter list
            String[] errorParameters = { sufficientFundBalances.getAccountNumber(), sufficientFundsObjectCode, sufficientFundBalances.getAccountSufficientFundsCode() };
            for (Iterator i = propertyNames.iterator(); i.hasNext();) {
                GlobalVariables.getErrorMap().putError((String) i.next(), KeyConstants.SufficientFunds.ERROR_INSUFFICIENT_FUNDS, errorParameters);
            }
        }
        return isSufficient;
    }

    /**
     * checks to see if a document is a <code>YearEndDocument</code>
     * 
     * @param documentClass
     * @return true if the class implements <code>YearEndDocument</code>
     */
    final boolean isYearEndDocument(Class documentClass) {
        return YearEndDocument.class.isAssignableFrom(documentClass);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.SufficientFundsService#getExpenditureCodes()
     */
    public List getExpenditureCodes() {
        final List list = new ArrayList();
        list.addAll(Arrays.asList(kualiConfigurationService.getApplicationParameterValues(APPLICATION_PARAMETER_SECURITY_GROUP.KUALI_TRANSACTION_PROCESSING_GLOBAL_RULES_SECURITY_GROUPING, APPLICATION_PARAMETER.EXPENSE_OBJECT_TYPE_CODES)));
        list.add(kualiConfigurationService.getApplicationParameterValue(TransferOfFundsDocumentRuleConstants.KUALI_TRANSACTION_PROCESSING_TRANSFER_OF_FUNDS_SECURITY_GROUPING, TransferOfFundsDocumentRuleConstants.TRANSFER_OF_FUNDS_EXPENSE_OBJECT_TYPE_CODE));
        return list;
    }

    /**
     * 
     * @see org.kuali.module.gl.service.SufficientFundsService#getSpecialFinancialObjectCodes()
     */
    public List getSpecialFinancialObjectCodes() {
        final List list = new ArrayList();
        list.addAll(Arrays.asList(kualiConfigurationService.getApplicationParameterValues(KUALI_TRANSACTION_PROCESSING_SUFFICIENT_FUNDS_SECURITY_GROUPING, SUFFICIENT_FUNDS_OBJECT_CODE_SPECIALS)));

        return list;
    }

    /**
     * 
     * @see org.kuali.module.gl.service.SufficientFundsService#getFinancialObjectCodeForCashInBank()
     */
    public String getFinancialObjectCodeForCashInBank() {
        String value = kualiConfigurationService.getApplicationParameterValue(KUALI_TRANSACTION_PROCESSING_SUFFICIENT_FUNDS_SECURITY_GROUPING, SUFFICIENT_FUNDS_OBJECT_CODE_CASH_IN_BANK);
        return value;
    }


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
    /**
     * Sets the accountService attribute value.
     * 
     * @param accountService The accountService to set.
     */
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Sets the objectLevelService attribute value.
     * 
     * @param objectLevelService The objectLevelService to set.
     */
    public void setObjectLevelService(ObjectLevelService objectLevelService) {
        this.objectLevelService = objectLevelService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Sets the sufficientFundsDao attribute value.
     * 
     * @param sufficientFundsDao The sufficientFundsDao to set.
     */
    public void setSufficientFundsDao(SufficientFundsDao sufficientFundsDao) {
        this.sufficientFundsDao = sufficientFundsDao;
    }

    /**
     * Sets the kualiRuleService attribute value.
     * 
     * @param kualiRuleService The kualiRuleService to set.
     */
    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    public void setOptionsDao(OptionsDao optionsDao) {
        this.optionsDao = optionsDao;
    }

    public void setSufficientFundBalancesDao(SufficientFundBalancesDao sufficientFundBalanceDao) {
        this.sufficientFundBalanceDao = sufficientFundBalanceDao;
    }
}