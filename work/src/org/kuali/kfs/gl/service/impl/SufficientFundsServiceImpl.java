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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.user.Options;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.event.SufficientFundsCheckingPreparationEvent;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.chart.service.ObjectLevelService;
import org.kuali.module.financial.document.YearEndDistributionOfIncomeAndExpenseDocument;
import org.kuali.module.financial.document.YearEndGeneralErrorCorrectionDocument;
import org.kuali.module.financial.document.YearEndTransferOfFundsDocument;
import org.kuali.module.gl.bo.SufficientFundBalances;
import org.kuali.module.gl.dao.ojb.SufficientFundsDaoOjb;
import org.kuali.module.gl.service.SufficientFundsService;
import org.kuali.module.gl.util.SufficientFundsItemHelper.SufficientFundsItem;

public class SufficientFundsServiceImpl implements SufficientFundsService {
    private AccountService accountService;
    private BusinessObjectService businessObjectService;
    private ObjectLevelService objectLevelService;
    private KualiConfigurationService kualiConfigurationService;
    private PersistenceService persistenceService;
    private KualiRuleService kualiRuleService;
    private DataDictionaryService dataDictionaryService;
    private SufficientFundsDaoOjb sufficientFundsDaoOjb;

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
    public String getSufficientFundsObjectCode(String chartOfAccountsCode, String financialObjectCode,
            String accountSufficientFundsCode, String financialObjectLevelCode) {
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
                throw new IllegalArgumentException("Invalid (null) objLevel for chartOfAccountsCode=" + chartOfAccountsCode
                        + ", financialObjectLevelCode=" + financialObjectLevelCode);
            }
            sfObjectCode = objectLevel.getFinancialConsolidationObjectCode();
        }
        else {
            sfObjectCode = Constants.NOT_AVAILABLE_STRING;
        }
        return sfObjectCode;
    }

    /**
     * @see org.kuali.core.service.SufficientFundsService#checkSufficientFunds(org.kuali.core.document.TransactionalDocument)
     */
    public boolean checkSufficientFunds(TransactionalDocument transactionalDocument) {

        // if global budget checking is not true then return
        if (!StringUtils.equals(Constants.BUDGET_CHECKING_OPTIONS_CD_ACTIVE,
                retrieveOptions(transactionalDocument.getPostingYear()).getBudgetCheckingOptionsCode())) {
            return true;
        }
        // MSA skipped check for reversing entries generated by an error correction. look where this value is coming from

        SufficientFundsCheckingPreparationEvent event = new SufficientFundsCheckingPreparationEvent(transactionalDocument);
        boolean isSufficientFunds = true;
        kualiRuleService.applyRules(event);
        // call check sf array
        for (Iterator i = event.getSufficientFundsItemHelper().iterator(); i.hasNext() && isSufficientFunds;) {
            SufficientFundsItem item = (SufficientFundsItem) i.next();
            if (item.getAmount().isPositive()) {
                isSufficientFunds &= checkSufficientFunds(item.getPropertyName(), item.getFiscalYear(), item
                        .getChartOfAccountsCode(), item.getAccountNumber(), item.getSufficientFundsObjectCode(), item.getAmount(),
                        transactionalDocument.getClass());
            }
        }

        return isSufficientFunds;
    }

    /**
     * fp_sasfc:operation chk_suff_funds
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param sufficientFundsObjectCode
     * @param amount
     * @param documentClass
     * @return
     */
    private boolean checkSufficientFunds(String propertyName, Integer universityFiscalYear, String chartOfAccountsCode,
            String accountNumber, String sufficientFundsObjectCode, KualiDecimal amount, Class documentClass) {

        if (universityFiscalYear == null) {
            throw new IllegalArgumentException("Invalid (null) universityFiscalYear");
        }
        Integer originalUniversityFiscalYear = universityFiscalYear;

        Account account = accountService.getByPrimaryId(chartOfAccountsCode, accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Invalid (null) account for: chartOfAccountsCode=" + chartOfAccountsCode
                    + ";accountNumber=" + accountNumber);
        }
        boolean isYearEndDocument = isYearEndDocument(documentClass);
        // universityFiscalYear is universityFiscalYear-1 if year end document & chash
        // level checking
        if (isYearEndDocument && StringUtils.equals(Constants.SF_TYPE_CASH_AT_ACCOUNT, account.getAccountSufficientFundsCode())) {
            universityFiscalYear = new Integer(originalUniversityFiscalYear.intValue() - 1);
        }

        // exit sufficient funds checking if not enabled for an account
        if (StringUtils.equals(Constants.SF_TYPE_NO_CHECKING, account.getAccountSufficientFundsCode())
                || !account.isPendingAcctSufficientFundsIndicator()) {
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

        sfBalances = (SufficientFundBalances) businessObjectService.findByPrimaryKey(SufficientFundBalances.class,
                persistenceService.getPrimaryKeyFieldValues(sfBalances));
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

        return calculatePLEBuckets(propertyName, isYearEndDocument, amount, sufficientFundsObjectCode, sfBalances);
    }

    /**
     * fp_sasfc:38 calculates pending ledger entry buckets
     * 
     * @param universityFiscalYear
     * @param lineAmount
     * @param sufficientFundsObjectCode
     * @param sufficientFundBalances
     * @return
     */
    private boolean calculatePLEBuckets(String propertyName, boolean isYearEndDocument, KualiDecimal lineAmount,
            String sufficientFundsObjectCode, SufficientFundBalances sufficientFundBalances) {
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
        Options options = retrieveOptions(universityFiscalYear);
        // cash level checking
        if (StringUtils.equals(Constants.SF_TYPE_CASH_AT_ACCOUNT, accountSufficientFundsCode)) {
            // fp_sasfc:48-2...79-2
            if (!options.isFinancialBeginBalanceLoadInd()) {
                pfyrBudget = sufficientFundsDaoOjb
                        .calculateM113PfyrBudget(universityFiscalYear, chartOfAccountsCode, accountNumber);
                pfyrEncum = sufficientFundsDaoOjb.calculateM113PfyrEncum(universityFiscalYear, chartOfAccountsCode, accountNumber);
            }
            pendingActual = sufficientFundsDaoOjb.calculateM113PendActual(options.isFinancialBeginBalanceLoadInd(),
                    universityFiscalYear, chartOfAccountsCode, accountNumber, getSpecialFinancialObjectCodes(),
                    getFinancialObjectCodeForCashInBank());
        }
        // non cash
        else {
            // fp_sasfc:99-1...167-1
            pendingActual = sufficientFundsDaoOjb.calculatePendActual(isYearEndDocument, options.getActualFinancialBalanceTypeCd(),
                    universityFiscalYear, chartOfAccountsCode, accountNumber, accountSufficientFundsCode, getExpenditureCodes());
            pendingBudget = sufficientFundsDaoOjb.calculatePendBudget(isYearEndDocument, options.getBudgetCheckingBalanceTypeCd(),
                    universityFiscalYear, chartOfAccountsCode, accountNumber, accountSufficientFundsCode, getExpenditureCodes());
            pendingEncumb = sufficientFundsDaoOjb.calculatePendEncum(isYearEndDocument, options.getExtrnlEncumFinBalanceTypCd(),
                    options.getIntrnlEncumFinBalanceTypCd(), options.getPreencumbranceFinBalTypeCd(), universityFiscalYear,
                    chartOfAccountsCode, accountNumber, accountSufficientFundsCode, getExpenditureCodes());
        }
        //
        return hasSufficientFunds(propertyName, options.isFinancialBeginBalanceLoadInd(), lineAmount, accountSufficientFundsCode,
                pfyrBudget, pfyrEncum, pendingActual, pendingBudget, pendingEncumb, sufficientFundBalances,
                sufficientFundsObjectCode);
    }

    /**
     * fp_sasfc:operation chk_suff_funds. 168-1...183 preforms the actual check to see if there is sufficient funds in an account
     * 
     * @param lineAmount
     * @param sufficientFundsCode
     * @param financialBeginBalanceLoadInd
     * @param pendingActual
     * @param pendingBudget
     * @param pendingEncumb
     * @param sufficientFundBalances
     * @return
     */
    private boolean hasSufficientFunds(String propertyName, boolean financialBeginBalanceLoadInd, KualiDecimal lineAmount,
            String sufficientFundsCode, KualiDecimal pfyrBudget, KualiDecimal pfyrEncum, KualiDecimal pendingActual,
            KualiDecimal pendingBudget, KualiDecimal pendingEncumb, SufficientFundBalances sufficientFundBalances,
            String sufficientFundsObjectCode) {
        KualiDecimal available = new KualiDecimal(0);


        if (StringUtils.equals(Constants.SF_TYPE_CASH_AT_ACCOUNT, sufficientFundsCode)) {
            if (financialBeginBalanceLoadInd) {
                // fp_sasfc: 172-2
                available = sufficientFundBalances.getCurrentBudgetBalanceAmount().add(pendingActual).subtract(
                        sufficientFundBalances.getAccountEncumbranceAmount());
            }
            else {
                // fp_sasfc:170-2
                available = sufficientFundBalances.getCurrentBudgetBalanceAmount().add(pfyrBudget).add(pendingActual).subtract(
                        sufficientFundBalances.getAccountEncumbranceAmount().add(pfyrEncum));
            }
        }
        else {
            // non cash
            // fp_sasfc:175-1
            available = sufficientFundBalances.getCurrentBudgetBalanceAmount().add(pendingBudget).subtract(
                    sufficientFundBalances.getAccountActualExpenditureAmt().add(pendingActual).add(
                            sufficientFundBalances.getAccountEncumbranceAmount()).add(pendingEncumb));
        }
        boolean isSufficient = available.isGreaterThan(lineAmount);
        if (!isSufficient) {
            // fp_sasfc:operation check_sf_array. 10-4
            // create error message parameter list
            String[] errorParameters = { sufficientFundBalances.getAccountNumber(), sufficientFundsObjectCode,
                    sufficientFundBalances.getAccountSufficientFundsCode() };
            GlobalVariables.getErrorMap().put(propertyName, KeyConstants.SufficientFunds.ERROR_INSUFFICIENT_FUNDS, errorParameters);
        }
        return isSufficient;
    }

    /**
     * retrieves the fs_options_t for a given universityFiscalYear
     * 
     * @param universityFiscalYear identifies which financial system options to retrieve
     * @return
     */
    private Options retrieveOptions(Integer universityFiscalYear) {
        Options options = new Options();
        options.setUniversityFiscalYear(universityFiscalYear);
        Map map = persistenceService.getPrimaryKeyFieldValues(options);
        options = (Options) businessObjectService.findByPrimaryKey(Options.class, map);
        if (options == null) {
            throw new IllegalArgumentException("Invalid (null) Options for universityFiscalYear=" + universityFiscalYear);
        }
        return options;
    }


    private boolean isYearEndDocument(Class documentClass) {
        String documentTypeName = dataDictionaryService.getDocumentTypeNameByClass(documentClass);
        String documentTypeCode = dataDictionaryService.getDocumentTypeCodeByTypeName(documentTypeName);
        // msa apc ?
        return StringUtils.defaultString(documentTypeCode).startsWith("YE");
    }

    // msa apc
    private List getExpenditureCodes() {
        // msa apc
        final List list = new ArrayList();
        list.add("EX");
        list.add("ES");
        list.add("EE");
        list.add("TE");
        return list;
    }

    // msa change this method name once it is clear what these actually do
    private List getSpecialFinancialObjectCodes() {
        // msa apc passed in from service
        final List list = new ArrayList();
        list.add("9040");
        list.add("9041");
        list.add("9050");

        return list;
    }

    private String getFinancialObjectCodeForCashInBank() {
        // msa apc passed in from service
        return "8000";
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
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
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
     * Sets the persistenceService attribute value.
     * 
     * @param persistenceService The persistenceService to set.
     */
    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    /**
     * Sets the sufficientFundsDaoOjb attribute value.
     * 
     * @param sufficientFundsDaoOjb The sufficientFundsDaoOjb to set.
     */
    public void setSufficientFundsDaoOjb(SufficientFundsDaoOjb sufficientFundsDaoOjb) {
        this.sufficientFundsDaoOjb = sufficientFundsDaoOjb;
    }

    /**
     * Sets the kualiRuleService attribute value.
     * 
     * @param kualiRuleService The kualiRuleService to set.
     */
    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * 
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }


}