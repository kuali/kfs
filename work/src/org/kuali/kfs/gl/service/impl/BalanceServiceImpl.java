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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.IteratorUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.OrganizationReversion;
import org.kuali.kfs.coa.service.BalanceTypeService;
import org.kuali.kfs.coa.service.ObjectTypeService;
import org.kuali.kfs.coa.service.SubFundGroupService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.gl.batch.BalanceForwardStep;
import org.kuali.kfs.gl.batch.service.FilteringBalanceIterator;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.GlSummary;
import org.kuali.kfs.gl.dataaccess.BalanceDao;
import org.kuali.kfs.gl.service.BalanceService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is the OJB implementation of the Balance Service
 */
@Transactional
public class BalanceServiceImpl implements BalanceService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceServiceImpl.class);

    protected static final String PARAMETER_PREFIX = "SELECTION_";

    protected BalanceDao balanceDao;
    protected OptionsService optionsService;
    protected ObjectTypeService objectTypeService;
    protected SubFundGroupService subFundGroupService;
    protected ParameterService parameterService;
    protected BalanceTypeService balanceTypService;
    // must have no asset, liability or fund balance balances other than object code 9899

    Collection<String> assetLiabilityFundBalanceObjectTypeCodes = null;
    Collection<String> encumbranceBaseBudgetBalanceTypeCodes = null;
    Collection<String> actualBalanceCodes = null;
    Collection<String> incomeObjectTypeCodes = null;
    Collection<String> expenseObjectTypeCodes = null;

    /**
     * @param universityFiscalYear the fiscal year to find balances for
     * @param balanceTypeCodes the balance types to summarize
     * @return a list of summarized GL balances
     * @see org.kuali.kfs.gl.service.BalanceService#getGlSummary(int, java.util.List)
     */
    public List<GlSummary> getGlSummary(int universityFiscalYear, List<String> balanceTypeCodes) {
        LOG.debug("getGlSummary() started");

        List<GlSummary> sum = new ArrayList<GlSummary>();

        Iterator<Object[]> i = balanceDao.getGlSummary(universityFiscalYear, balanceTypeCodes);
        while (i.hasNext()) {
            Object[] data = i.next();
            sum.add(new GlSummary(data));
        }
        return sum;
    }

    /**
     * Defers to the DAO to find all balances in the fiscal year.
     * 
     * @param fiscalYear the fiscal year to find balances for
     * @return an Iterator full of balances from the given fiscal year
     * @see org.kuali.kfs.gl.service.BalanceService#findBalancesForFiscalYear(java.lang.Integer)
     */
    public Iterator<Balance> findBalancesForFiscalYear(Integer fiscalYear) {
        return (Iterator<Balance>) balanceDao.findBalancesForFiscalYear(fiscalYear);
    }

    /**
     * Checks the given account to see if there are any non zero asset fund liability fund balances for them
     * 
     * @param account an account to find balances for
     * @return true if there are non zero asset liability fund balances, false if otherwise
     * @see org.kuali.kfs.gl.service.BalanceService#hasAssetLiabilityFundBalanceBalances(org.kuali.kfs.coa.businessobject.Account)
     */
    public boolean hasAssetLiabilityFundBalanceBalances(Account account) {

        /*
         * Here is an excerpt from the original Oracle trigger: SELECT fin_object_cd FROM gl_balance_t WHERE univ_fiscal_yr =
         * p_univ_fiscal_yr AND fin_coa_cd = p_fin_coa_cd AND account_nbr = p_account_nbr AND fin_object_cd != '9899' AND
         * fin_obj_typ_cd IN ('AS', 'LI', 'FB') AND fin_balance_typ_cd = 'AC' GROUP BY fin_object_cd HAVING
         * ABS(SUM(fin_beg_bal_ln_amt + acln_annl_bal_amt)) > 0); added absolute value function to sum--prevents the case of 2
         * entries (1 pos and 1 neg) from canceling each other out and allowing the acct to be closed when it shouldn't be.
         */

        // FIXME! - date service should be injected
        Integer fiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        ArrayList fundBalanceObjectCodes = new ArrayList();
        fundBalanceObjectCodes.add(null == account.getChartOfAccounts() ? null : account.getChartOfAccounts().getFundBalanceObjectCode());
        Iterator balances = balanceDao.findBalances(account, fiscalYear, null, fundBalanceObjectCodes, getAssetLiabilityFundBalanceBalanceTypeCodes(), getActualBalanceCodes());

        KualiDecimal begin;
        KualiDecimal annual;

        // TODO KULCOA-335 - is absolute value necessary to prevent obscure sets of values
        // from masking accounts that should remain open?

        Map groups = new HashMap();

        while (balances.hasNext()) {
            Balance balance = (Balance) balances.next();
            begin = balance.getBeginningBalanceLineAmount();
            annual = balance.getAccountLineAnnualBalanceAmount();

            String objectCode = balance.getObjectCode();

            KualiDecimal runningTotal = (KualiDecimal) groups.get(objectCode);

            if (runningTotal == null) {
                runningTotal = KualiDecimal.ZERO;
            }

            runningTotal = runningTotal.add(begin);
            runningTotal = runningTotal.add(annual);

            groups.put(objectCode, runningTotal);


        }

        boolean success = false;

        Iterator iter = groups.keySet().iterator();
        while (iter.hasNext()) {
            success |= ((KualiDecimal) groups.get(iter.next())).isNonZero();
        }

        return success;

    }

    /**
     * Given an iterator of balances, this returns the sum of each balance's beginning balance line amount + annual account linge
     * balance amount
     * 
     * @param balances an Iterator of balances to sum
     * @return the sum of all of those balances
     */
    protected KualiDecimal sumBalances(Iterator balances) {
        KualiDecimal runningTotal = KualiDecimal.ZERO;

        KualiDecimal begin;
        KualiDecimal annual;

        while (balances.hasNext()) {
            Balance balance = (Balance) balances.next();
            begin = balance.getBeginningBalanceLineAmount();
            annual = balance.getAccountLineAnnualBalanceAmount();

            runningTotal = runningTotal.add(begin);
            runningTotal = runningTotal.add(annual);
        }

        return runningTotal;

    }

    /**
     * Returns the sum of balances considered as income for the given account
     * 
     * @param account the account to find income balances for
     * @return the sum of income balances
     */
    protected KualiDecimal incomeBalances(Account account) {

        /*
         * SELECT SUM(fin_beg_bal_ln_amt + acln_annl_bal_amt) INTO v_y FROM gl_balance_t WHERE univ_fiscal_yr = p_univ_fiscal_yr AND
         * fin_coa_cd = p_fin_coa_cd AND account_nbr = p_account_nbr AND (fin_object_cd = '9899' OR fin_obj_typ_cd IN ('CH', 'IC',
         * 'IN', 'TI')) AND fin_balance_typ_cd = 'AC';
         * @return
         */

        // FIXME! - date service should be injected
        Integer fiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();

        ArrayList fundBalanceObjectCodes = new ArrayList();
        fundBalanceObjectCodes.add(account.getChartOfAccounts().getFundBalanceObjectCode());
        Iterator balances = balanceDao.findBalances(account, fiscalYear, fundBalanceObjectCodes, null, getIncomeObjectTypeCodes(), getActualBalanceCodes());

        return sumBalances(balances);

    }

    /**
     * Sums all the balances associated with a given account that would be considered "expense" balances
     * 
     * @param account an account to find expense balances for
     * @return the sum of those balances
     */
    protected KualiDecimal expenseBalances(Account account) {
        /*
         * Here is an excerpt from the original Oracle Trigger: SELECT SUM(fin_beg_bal_ln_amt || acln_annl_bal_amt) INTO v_x FROM
         * gl_balance_t WHERE univ_fiscal_yr = p_univ_fiscal_yr AND fin_coa_cd = p_fin_coa_cd AND account_nbr = p_account_nbr AND
         * fin_obj_typ_cd IN ('EE', 'ES', 'EX', 'TE') AND fin_balance_typ_cd = 'AC'; This method...
         */

        // FIXME! - date service should be injected
        Integer fiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        Iterator balances = balanceDao.findBalances(account, fiscalYear, null, null, getExpenseObjectTypeCodes(), getActualBalanceCodes());

        return sumBalances(balances);

    }

    /**
     * Checks to see if the total income balances for the given account equal the total expense balances for the given account
     * 
     * @param an account to find balances for
     * @return true if income balances equal expense balances, false otherwise
     * @see org.kuali.kfs.gl.service.BalanceService#fundBalanceWillNetToZero(org.kuali.kfs.coa.businessobject.Account)
     */
    public boolean fundBalanceWillNetToZero(Account account) {
        KualiDecimal income = incomeBalances(account);
        KualiDecimal expense = expenseBalances(account);

        return income.equals(expense);
    }

    /**
     * Finds all of the encumbrance balances for the given account, and figures out if those encumbrances will have a net impact on
     * the budget
     * 
     * @param account an account to find balances for
     * @return true if summed encumbrances for the account are not zero (meaning encumbrances will have a net impact on the budget),
     *         false if otherwise
     * @see org.kuali.kfs.gl.service.BalanceService#hasEncumbrancesOrBaseBudgets(org.kuali.kfs.coa.businessobject.Account)
     */
    public boolean hasEncumbrancesOrBaseBudgets(Account account) {

        /*
         * check for Encumbrances and base budgets Here is an excerpt from the original Oracle Trigger: SELECT
         * SUM(fin_beg_bal_ln_amt + acln_annl_bal_amt) INTO v_y FROM gl_balance_t WHERE univ_fiscal_yr = p_univ_fiscal_yr AND
         * fin_coa_cd = p_fin_coa_cd AND account_nbr = p_account_nbr AND fin_balance_typ_cd IN ('EX', 'IE', 'PE', 'BB'); v_rowcnt :=
         * SQL%ROWCOUNT;
         */

        // FIXME! - date service should be injected
        Integer fiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        Iterator balances = balanceDao.findBalances(account, fiscalYear, null, null, null, getEncumbranceBaseBudgetBalanceTypeCodes());

        return sumBalances(balances).isNonZero();
    }

    /**
     * Returns whether or not the beginning budget is loaded for the given account. Of course, it doesn't really check the
     * account...just the options for the current year to see if all the beginning balances have been loaded
     * 
     * @param an account to check whether the beginning balance is loaded for
     * @return true if the beginning balance is loaded, false otherwise
     * @see org.kuali.kfs.gl.service.BalanceService#beginningBalanceLoaded(org.kuali.kfs.coa.businessobject.Account)
     */
    public boolean beginningBalanceLoaded(Account account) {
        return optionsService.getCurrentYearOptions().isFinancialBeginBalanceLoadInd();
    }

    /**
     * Determines if the account has asset/liability balances associated with it that will have a net impact
     * 
     * @param account an account to check balances for
     * @return true if the account has an asset liability balance, false otherwise
     * @see org.kuali.kfs.gl.service.BalanceService#hasAssetLiabilityOrFundBalance(org.kuali.kfs.coa.businessobject.Account)
     */
    public boolean hasAssetLiabilityOrFundBalance(Account account) {
        return hasAssetLiabilityFundBalanceBalances(account) || !fundBalanceWillNetToZero(account) || hasEncumbrancesOrBaseBudgets(account);
    }

    public void setBalanceDao(BalanceDao balanceDao) {
        this.balanceDao = balanceDao;
    }

    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    /**
     * This method finds the summary records of balance entries according to input fields an values, using the DAO. The results will
     * be limited to the system lookup results limit.
     * 
     * @param fieldValues the input fields an values
     * @param isConsolidated consolidation option is applied or not
     * @return the summary records of balance entries
     * @see org.kuali.kfs.gl.service.BalanceService#lookupCashBalance(java.util.Map, boolean)
     */
    public Iterator lookupCashBalance(Map fieldValues, boolean isConsolidated) {
        LOG.debug("findCashBalance() started");

        return balanceDao.lookupCashBalance(fieldValues, isConsolidated, getEncumbranceBalanceTypes(fieldValues));
    }

    /**
     * This method gets the size of cash balance entries according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the count of cash balance entries
     * @see org.kuali.kfs.gl.service.BalanceService#getCashBalanceRecordCount(java.util.Map, boolean)
     */
    public Integer getCashBalanceRecordCount(Map fieldValues, boolean isConsolidated) {
        LOG.debug("getCashBalanceRecordCount() started");

        Integer recordCount = new Integer(0);
        if (!isConsolidated) {
            recordCount = balanceDao.getDetailedCashBalanceRecordCount(fieldValues, getEncumbranceBalanceTypes(fieldValues));
        }
        else {
            recordCount = balanceDao.getConsolidatedCashBalanceRecordCount(fieldValues, getEncumbranceBalanceTypes(fieldValues));
        }
        return recordCount;
    }

    /**
     * This method gets the size of balance entries according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the size of balance entries
     * @see org.kuali.kfs.gl.service.BalanceService#findBalance(java.util.Map, boolean)
     */
    public Iterator findBalance(Map fieldValues, boolean isConsolidated) {
        LOG.debug("findBalance() started");
        return balanceDao.findBalance(fieldValues, isConsolidated, getEncumbranceBalanceTypes(fieldValues));
    }

    /**
     * This method finds the summary records of balance entries according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the summary records of balance entries
     * @see org.kuali.kfs.gl.service.BalanceService#getBalanceRecordCount(java.util.Map, boolean)
     */
    public Integer getBalanceRecordCount(Map fieldValues, boolean isConsolidated) {
        LOG.debug("getBalanceRecordCount() started");

        Integer recordCount = null;
        if (!isConsolidated) {
            recordCount = OJBUtility.getResultSizeFromMap(fieldValues, new Balance()).intValue();
        }
        else {
            Iterator recordCountIterator = balanceDao.getConsolidatedBalanceRecordCount(fieldValues, getEncumbranceBalanceTypes(fieldValues));
            // TODO: WL: why build a list and waste time/memory when we can just iterate through the iterator and do a count?
            List recordCountList = IteratorUtils.toList(recordCountIterator);
            recordCount = recordCountList.size();
        }
        return recordCount;
    }

    /**
     * Purge the balance table by year/chart
     * 
     * @param chart the chart of balances to purge
     * @param year the year of balances to purge
     */
    public void purgeYearByChart(String chart, int year) {
        LOG.debug("purgeYearByChart() started");

        balanceDao.purgeYearByChart(chart, year);
    }

    /**
     * Private method to load the values from the system options service and store them locally for later use.
     */
    protected void loadConstantsFromOptions() {
        LOG.debug("loadConstantsFromOptions() started");
        SystemOptions options = optionsService.getCurrentYearOptions();
        // String[] actualBalanceCodes = new String[] { "AC" };
        actualBalanceCodes = Arrays.asList( options.getActualFinancialBalanceTypeCd() ); // AC
        // String[] incomeObjectTypeCodes = new String[] { "CH", "IC", "IN", "TI" };
        incomeObjectTypeCodes = Arrays.asList( options.getFinObjTypeIncomeNotCashCd(), // IC
                options.getFinObjectTypeIncomecashCode(), // IN
                options.getFinObjTypeCshNotIncomeCd(), // CH
                options.getFinancialObjectTypeTransferIncomeCd() // TI
        );
        // String[] expenseObjectTypeCodes = new String[] { "EE", "ES", "EX", "TE" };
        expenseObjectTypeCodes = Arrays.asList( options.getFinObjTypeExpendNotExpCode(), // EE?
                options.getFinObjTypeExpenditureexpCd(), // ES
                options.getFinObjTypeExpNotExpendCode(), // EX?
                options.getFinancialObjectTypeTransferExpenseCd() // TE
        );
        // String[] assetLiabilityFundBalanceBalanceTypeCodes = new String[] { "AS", "LI", "FB" };
        assetLiabilityFundBalanceObjectTypeCodes = Arrays.asList( options.getFinancialObjectTypeAssetsCd(), // AS
                options.getFinObjectTypeLiabilitiesCode(), // LI
                options.getFinObjectTypeFundBalanceCd() // FB
        );
        // String[] encumbranceBaseBudgetBalanceTypeCodes = new String[] { "EX", "IE", "PE", "BB" };
        encumbranceBaseBudgetBalanceTypeCodes = Arrays.asList( options.getExtrnlEncumFinBalanceTypCd(), // EX
                options.getIntrnlEncumFinBalanceTypCd(), // IE
                options.getPreencumbranceFinBalTypeCd(), // PE
                options.getBaseBudgetFinancialBalanceTypeCd() // BB
        );
    }

    /**
     * Use the options table to get a list of all the balance type codes associated with actual balances
     * 
     * @return an array of balance type codes for actual balances
     */
    protected Collection<String> getActualBalanceCodes() {
        if (actualBalanceCodes == null) {
            loadConstantsFromOptions();
        }
        return actualBalanceCodes;
    }

    /**
     * Uses the options table to find all the balance type codes associated with income
     * 
     * @return an array of income balance type codes
     */
    protected Collection<String> getIncomeObjectTypeCodes() {
        if (incomeObjectTypeCodes == null) {
            loadConstantsFromOptions();
        }
        return incomeObjectTypeCodes;
    }

    /**
     * Uses the options table to find all the balance type codes associated with expenses
     * 
     * @return an array of expense option type codes
     */
    protected Collection<String> getExpenseObjectTypeCodes() {
        if (expenseObjectTypeCodes == null) {
            loadConstantsFromOptions();
        }
        return expenseObjectTypeCodes;
    }

    /**
     * Uses the options table to find all the balance type codes associated with asset/liability
     * 
     * @return an array of asset/liability balance type codes
     */
    protected Collection<String> getAssetLiabilityFundBalanceBalanceTypeCodes() {
        if (assetLiabilityFundBalanceObjectTypeCodes == null) {
            loadConstantsFromOptions();
        }
        return assetLiabilityFundBalanceObjectTypeCodes;
    }

    /**
     * Uses the options table to create a list of all the balance type codes associated with encumbrances
     * 
     * @return an array of encumbrance balance type codes
     */
    protected Collection<String> getEncumbranceBaseBudgetBalanceTypeCodes() {
        if (encumbranceBaseBudgetBalanceTypeCodes == null) {
            loadConstantsFromOptions();
        }
        return encumbranceBaseBudgetBalanceTypeCodes;
    }

    /**
     * Uses the DAO to count the number of balances associated with the given fiscal year
     * 
     * @param fiscal year a fiscal year to count balances for
     * @return an integer with the number of balances
     * @see org.kuali.kfs.gl.service.BalanceService#countBalancesForFiscalYear(java.lang.Integer)
     */
    public int countBalancesForFiscalYear(Integer year) {
        return balanceDao.countBalancesForFiscalYear(year);
    }

    /**
     * This method returns all of the balances specifically for the nominal activity closing job
     * 
     * @param year year to find balances for
     * @return an Iterator of nominal activity balances
     * @see org.kuali.kfs.gl.service.BalanceService#findNominalActivityBalancesForFiscalYear(java.lang.Integer)
     */
    public Iterator<Balance> findNominalActivityBalancesForFiscalYear(Integer year) {
        // generate List of nominal activity object type codes
        List<String> nominalActivityObjectTypeCodes = objectTypeService.getNominalActivityClosingAllowedObjectTypes(year);
        SystemOptions currentYearOptions = optionsService.getCurrentYearOptions();
        return balanceDao.findNominalActivityBalancesForFiscalYear(year, nominalActivityObjectTypeCodes, currentYearOptions);
    }

    /**
     * Returns all the balances to be forwarded for the "cumulative" rule
     * 
     * @param year the fiscal year to find balances for
     * @return an Iterator of balances to process for the cumulative/active balance forward process
     * @see org.kuali.kfs.gl.service.BalanceService#findCumulativeBalancesToForwardForFiscalYear(java.lang.Integer)
     */
    public Iterator<Balance> findCumulativeBalancesToForwardForFiscalYear(Integer year) {
        List<String> cumulativeForwardBalanceObjectTypes = objectTypeService.getCumulativeForwardBalanceObjectTypes(year);
        Collection<String> contractsAndGrantsDenotingValues = subFundGroupService.getContractsAndGrantsDenotingValues();
        Collection<String> subFundGroupsForCumulativeBalanceForwardingArray = parameterService.getParameterValuesAsString(BalanceForwardStep.class, GeneralLedgerConstants.BalanceForwardRule.SUB_FUND_GROUPS_FOR_INCEPTION_TO_DATE_REPORTING);
        Collection<String> cumulativeBalanceForwardBalanceTypesArray = parameterService.getParameterValuesAsString(BalanceForwardStep.class, GeneralLedgerConstants.BalanceForwardRule.BALANCE_TYPES_TO_ROLL_FORWARD_FOR_INCOME_EXPENSE);
        boolean fundGroupDenotesCGInd = parameterService.getParameterValueAsBoolean(Account.class, KFSConstants.ChartApcParms.ACCOUNT_FUND_GROUP_DENOTES_CG);
        Iterator<Balance> balances = balanceDao.findCumulativeBalancesToForwardForFiscalYear(year, cumulativeForwardBalanceObjectTypes, contractsAndGrantsDenotingValues, subFundGroupsForCumulativeBalanceForwardingArray, cumulativeBalanceForwardBalanceTypesArray, fundGroupDenotesCGInd);

        FilteringBalanceIterator filteredBalances = SpringContext.getBean(FilteringBalanceIterator.class, "glBalanceAnnualAndCGTotalNotZeroIterator");
        filteredBalances.setBalancesSource(balances);

        return filteredBalances;
    }

    /**
     * Returns all the balances specifically to be processed by the balance forwards job for the "general" rule
     * 
     * @param year the fiscal year to find balances for
     * @return an Iterator of balances to process for the general balance forward process
     * @see org.kuali.kfs.gl.service.BalanceService#findGeneralBalancesToForwardForFiscalYear(java.lang.Integer)
     */
    public Iterator<Balance> findGeneralBalancesToForwardForFiscalYear(Integer year) {
        List<String> generalForwardBalanceObjectTypes = objectTypeService.getGeneralForwardBalanceObjectTypes(year);
        Collection<String> generalBalanceForwardBalanceTypesArray = parameterService.getParameterValuesAsString(BalanceForwardStep.class, GeneralLedgerConstants.BalanceForwardRule.BALANCE_TYPES_TO_ROLL_FORWARD_FOR_BALANCE_SHEET);
        Iterator<Balance> balances = balanceDao.findGeneralBalancesToForwardForFiscalYear(year, generalForwardBalanceObjectTypes, generalBalanceForwardBalanceTypesArray);

        Map<String, FilteringBalanceIterator> balanceIterators = SpringContext.getBeansOfType(FilteringBalanceIterator.class);
        FilteringBalanceIterator filteredBalances = balanceIterators.get("glBalanceTotalNotZeroIterator");
        filteredBalances.setBalancesSource(balances);

        return filteredBalances;
    }

    /**
     * Returns all of the balances to be forwarded for the organization reversion process
     * 
     * @param year the year of balances to find
     * @param endOfYear whether the organization reversion process is running end of year (before the fiscal year change over) or
     *        beginning of year (after the fiscal year change over)
     * @return an iterator of balances to put through the strenuous organization reversion process
     * @see org.kuali.kfs.gl.service.BalanceService#findOrganizationReversionBalancesForFiscalYear(java.lang.Integer, boolean)
     */
    public Iterator<Balance> findOrganizationReversionBalancesForFiscalYear(Integer year, boolean endOfYear) {
        SystemOptions options = SpringContext.getBean(OptionsService.class).getOptions(year);
        List<ParameterEvaluator> parameterEvaluators = new ArrayList<ParameterEvaluator>();

        int i = 1;
        boolean moreParams = true;
        while (moreParams) {
            if (parameterService.parameterExists(OrganizationReversion.class, PARAMETER_PREFIX + i)) {
                ParameterEvaluator parameterEvaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(OrganizationReversion.class, PARAMETER_PREFIX + i);
                parameterEvaluators.add(parameterEvaluator);
            }
            else {
                moreParams = false;
            }
            i++;
        }
        return balanceDao.findOrganizationReversionBalancesForFiscalYear(year, endOfYear, options, parameterEvaluators);
    }

    /**
     * Gets the encumbrance balance types.
     * 
     * @param fieldValues
     * @return a list with the encumbrance balance types
     */
    protected List<String> getEncumbranceBalanceTypes(Map fieldValues) {

        List<String> encumbranceBalanceTypes = null;
        if (fieldValues.containsKey(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR)) {
            // the year should be part of the results for both the cash balance and regular balance lookupables
            String universityFiscalYearStr = (String) fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
            Integer universityFiscalYear = new Integer(universityFiscalYearStr);
            encumbranceBalanceTypes = balanceTypService.getEncumbranceBalanceTypes(universityFiscalYear);
        }
        return encumbranceBalanceTypes;
    }

    /**
     * Sets the objectTypeService.
     * 
     * @param objectTypeService
     */
    public void setObjectTypeService(ObjectTypeService objectTypeService) {
        this.objectTypeService = objectTypeService;
    }

    /**
     * Sets the subFundGroupService.
     * 
     * @param subFundGroupService
     */
    public void setSubFundGroupService(SubFundGroupService subFundGroupService) {
        this.subFundGroupService = subFundGroupService;
    }

    /**
     * Sets the parameterService.
     * 
     * @param parameterService
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the balanceTypService.
     * 
     * @param balanceTypService
     */
    public void setBalanceTypService(BalanceTypeService balanceTypService) {
        this.balanceTypService = balanceTypService;
    }

}
