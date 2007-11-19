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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.IteratorUtils;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.GlSummary;
import org.kuali.module.gl.dao.BalanceDao;
import org.kuali.module.gl.service.BalanceService;
import org.kuali.module.gl.util.OJBUtility;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is the OJB implementation of the Balance Service
 */
@Transactional
public class BalanceServiceImpl implements BalanceService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceServiceImpl.class);

    protected BalanceDao balanceDao;
    protected OptionsService optionsService;

    // must have no asset, liability or fund balance balances other than object code 9899

    String[] assetLiabilityFundBalanceObjectTypeCodes = null;
    String[] encumbranceBaseBudgetBalanceTypeCodes = null;
    String[] actualBalanceCodes = null;
    String[] incomeObjectTypeCodes = null;
    String[] expenseObjectTypeCodes = null;

    /**
     * Turns an array of Strings into a List of Strings
     * 
     * @param s an array of Strings
     * @return an implementation of Collection (a List) of Strings
     */
    private Collection wrap(String[] s) {
        return Arrays.asList(s);
    }

    /**
     * @param universityFiscalYear the fiscal year to find balances for
     * @param balanceTypeCodes the balance types to summarize
     * @return a list of summarized GL balances
     * @see org.kuali.module.gl.service.BalanceService#getGlSummary(int, java.util.List)
     */
    public List getGlSummary(int universityFiscalYear, List<String> balanceTypeCodes) {
        LOG.debug("getGlSummary() started");

        List sum = new ArrayList();

        Iterator i = balanceDao.getGlSummary(universityFiscalYear, balanceTypeCodes);
        while (i.hasNext()) {
            Object[] data = (Object[]) i.next();
            sum.add(new GlSummary(data));
        }
        return sum;
    }

    /**
     * Defers to the DAO to find all balances in the fiscal year.
     * 
     * @param fiscalYear the fiscal year to find balances for
     * @return an Iterator full of balances from the given fiscal year
     * @see org.kuali.module.gl.service.BalanceService#findBalancesForFiscalYear(java.lang.Integer)
     */
    public Iterator<Balance> findBalancesForFiscalYear(Integer fiscalYear) {
        

        return (Iterator<Balance>) balanceDao.findBalancesForFiscalYear(fiscalYear);
    }

    /**
     * Checks the given account to see if there are any non zero asset fund liability fund balances for them
     * 
     * @param account an account to find balances for
     * @return true if there are non zero asset liability fund balances, false if otherwise
     * @see org.kuali.module.gl.service.BalanceService#hasAssetLiabilityFundBalanceBalances(org.kuali.module.chart.bo.Account)
     */
    public boolean hasAssetLiabilityFundBalanceBalances(Account account) {
        
        /*
         * Here is an excerpt from the original Oracle trigger: SELECT fin_object_cd FROM gl_balance_t WHERE
         * univ_fiscal_yr = p_univ_fiscal_yr AND fin_coa_cd = p_fin_coa_cd AND account_nbr = p_account_nbr AND fin_object_cd != '9899'
         * AND fin_obj_typ_cd IN ('AS', 'LI', 'FB') AND fin_balance_typ_cd = 'AC' GROUP BY fin_object_cd HAVING
         * ABS(SUM(fin_beg_bal_ln_amt + acln_annl_bal_amt)) > 0); added absolute value function to sum--prevents the case of 2 entries
         * (1 pos and 1 neg) from canceling each other out and allowing the acct to be closed when it shouldn't be.
         */

        Integer fiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        ArrayList fundBalanceObjectCodes = new ArrayList();
        fundBalanceObjectCodes.add(null == account.getChartOfAccounts() ? null : account.getChartOfAccounts().getFundBalanceObjectCode());
        Iterator balances = balanceDao.findBalances(account, fiscalYear, null, fundBalanceObjectCodes, wrap(getAssetLiabilityFundBalanceBalanceTypeCodes()), wrap(getActualBalanceCodes()));

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
                runningTotal = new KualiDecimal(0);
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
     * Given an iterator of balances, this returns the sum of each balance's beginning balance line amount + annual account linge balance amount
     * 
     * @param balances an Iterator of balances to sum
     * @return the sum of all of those balances
     */
    private KualiDecimal sumBalances(Iterator balances) {
        KualiDecimal runningTotal = new KualiDecimal(0);

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
         * fin_coa_cd = p_fin_coa_cd AND account_nbr = p_account_nbr AND (fin_object_cd = '9899' OR fin_obj_typ_cd IN ('CH', 'IC', 'IN',
         * 'TI')) AND fin_balance_typ_cd = 'AC';
         * 
         * @return
         */

        Integer fiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();

        ArrayList fundBalanceObjectCodes = new ArrayList();
        fundBalanceObjectCodes.add(account.getChartOfAccounts().getFundBalanceObjectCode());
        Iterator balances = balanceDao.findBalances(account, fiscalYear, fundBalanceObjectCodes, null, wrap(getIncomeObjectTypeCodes()), wrap(getActualBalanceCodes()));

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
        
        Integer fiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        Iterator balances = balanceDao.findBalances(account, fiscalYear, null, null, wrap(getExpenseObjectTypeCodes()), wrap(getActualBalanceCodes()));

        return sumBalances(balances);

    }

    /**
     * Checks to see if the total income balances for the given account equal the total expense balances for the given account
     * 
     * @param an account to find balances for
     * @return true if income balances equal expense balances, false otherwise
     * @see org.kuali.module.gl.service.BalanceService#fundBalanceWillNetToZero(org.kuali.module.chart.bo.Account)
     */
    public boolean fundBalanceWillNetToZero(Account account) {
        KualiDecimal income = incomeBalances(account);
        KualiDecimal expense = expenseBalances(account);

        return income.equals(expense);
    }

    /**
     * Finds all of the encumbrance balances for the given account, and figures out if those encumbrances will have a net impact on the budget
     *
     * @param account an account to find balances for
     * @return true if summed encumbrances for the account are not zero (meaning encumbrances will have a net impact on the budget), false if otherwise
     * @see org.kuali.module.gl.service.BalanceService#hasEncumbrancesOrBaseBudgets(org.kuali.module.chart.bo.Account)
     */
    public boolean hasEncumbrancesOrBaseBudgets(Account account) {
        
        /*
         * check for Encumbrances and base budgets Here is an excerpt from the original Oracle Trigger: SELECT SUM(fin_beg_bal_ln_amt +
         * acln_annl_bal_amt) INTO v_y FROM gl_balance_t WHERE univ_fiscal_yr = p_univ_fiscal_yr AND fin_coa_cd = p_fin_coa_cd AND
         * account_nbr = p_account_nbr AND fin_balance_typ_cd IN ('EX', 'IE', 'PE', 'BB'); v_rowcnt := SQL%ROWCOUNT;
         */

        Integer fiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        Iterator balances = balanceDao.findBalances(account, fiscalYear, null, null, null, wrap(getEncumbranceBaseBudgetBalanceTypeCodes()));

        return sumBalances(balances).isNonZero();
    }

    /**
     * Returns whether or not the beginning budget is loaded for the given account.  Of course, it doesn't
     * really check the account...just the options for the current year to see if all the beginning balances
     * have been loaded
     * 
     * @param an account to check whether the beginning balance is loaded for
     * @return true if the beginning balance is loaded, false otherwise
     * @see org.kuali.module.gl.service.BalanceService#beginningBalanceLoaded(org.kuali.module.chart.bo.Account)
     */
    public boolean beginningBalanceLoaded(Account account) {
        return optionsService.getCurrentYearOptions().isFinancialBeginBalanceLoadInd();
    }

    /**
     * Determines if the account has asset/liability balances associated with it that will have a net impact
     * 
     * @param account an account to check balances for
     * @return true if the account has an asset liability balance, false otherwise
     * @see org.kuali.module.gl.service.BalanceService#hasAssetLiabilityOrFundBalance(org.kuali.module.chart.bo.Account)
     */
    public boolean hasAssetLiabilityOrFundBalance(Account account) {
        return hasAssetLiabilityFundBalanceBalances(account) || !fundBalanceWillNetToZero(account) || hasEncumbrancesOrBaseBudgets(account);
    }

    /**
     * Saves the balance in a no-nonsense, straight away, three piece suit sort of way
     * 
     * @param b the balance to save
     * @see org.kuali.module.gl.service.BalanceService#save(org.kuali.module.gl.bo.Balance)
     */
    public void save(Balance b) {
        balanceDao.save(b);
    }

    public void setBalanceDao(BalanceDao balanceDao) {
        this.balanceDao = balanceDao;
    }

    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    /**
     * This method finds the summary records of balance entries according to input fields an values, using the DAO
     * 
     * @param fieldValues the input fields an values
     * @param isConsolidated consolidation option is applied or not
     * @return the summary records of balance entries
     * @see org.kuali.module.gl.service.BalanceService#findCashBalance(java.util.Map, boolean)
     */
    public Iterator findCashBalance(Map fieldValues, boolean isConsolidated) {
        LOG.debug("findCashBalance() started");

        return balanceDao.findCashBalance(fieldValues, isConsolidated);
    }

    /**
     * This method gets the size of cash balance entries according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the count of cash balance entries
     * @see org.kuali.module.gl.service.BalanceService#getCashBalanceRecordCount(java.util.Map, boolean)
     */
    public Integer getCashBalanceRecordCount(Map fieldValues, boolean isConsolidated) {
        LOG.debug("getCashBalanceRecordCount() started");

        Integer recordCount = new Integer(0);
        if (!isConsolidated) {
            recordCount = balanceDao.getDetailedCashBalanceRecordCount(fieldValues);
        }
        else {
            Iterator recordCountIterator = balanceDao.getConsolidatedCashBalanceRecordCount(fieldValues);
            // TODO: WL: why build a list and waste time/memory when we can just iterate through the iterator and do a count?
            List recordCountList = IteratorUtils.toList(recordCountIterator);
            recordCount = recordCountList.size();
        }
        return recordCount;
    }

    /**
     * This method gets the size of balance entries according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the size of balance entries
     * @see org.kuali.module.gl.service.BalanceService#findBalance(java.util.Map, boolean)
     */
    public Iterator findBalance(Map fieldValues, boolean isConsolidated) {
        LOG.debug("findBalance() started");
        return balanceDao.findBalance(fieldValues, isConsolidated);
    }

    /**
     * This method finds the summary records of balance entries according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the summary records of balance entries
     * @see org.kuali.module.gl.service.BalanceService#getBalanceRecordCount(java.util.Map, boolean)
     */
    public Integer getBalanceRecordCount(Map fieldValues, boolean isConsolidated) {
        LOG.debug("getBalanceRecordCount() started");

        Integer recordCount = null;
        if (!isConsolidated) {
            recordCount = OJBUtility.getResultSizeFromMap(fieldValues, new Balance()).intValue();
        }
        else {
            Iterator recordCountIterator = balanceDao.getConsolidatedBalanceRecordCount(fieldValues);
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
    private void loadConstantsFromOptions() {
        LOG.debug("loadConstantsFromOptions() started");
        Options options = optionsService.getCurrentYearOptions();
        // String[] actualBalanceCodes = new String[] { "AC" };
        actualBalanceCodes = new String[] { options.getActualFinancialBalanceTypeCd() }; // AC
        // String[] incomeObjectTypeCodes = new String[] { "CH", "IC", "IN", "TI" };
        incomeObjectTypeCodes = new String[] { options.getFinObjTypeIncomeNotCashCd(), // IC
                options.getFinObjectTypeIncomecashCode(), // IN
                options.getFinObjTypeCshNotIncomeCd(), // CH
                options.getFinancialObjectTypeTransferIncomeCd() // TI
        };
        // String[] expenseObjectTypeCodes = new String[] { "EE", "ES", "EX", "TE" };
        expenseObjectTypeCodes = new String[] { options.getFinObjTypeExpendNotExpCode(), // EE?
                options.getFinObjTypeExpenditureexpCd(), // ES
                options.getFinObjTypeExpNotExpendCode(), // EX?
                options.getFinancialObjectTypeTransferExpenseCd() // TE
        };
        // String[] assetLiabilityFundBalanceBalanceTypeCodes = new String[] { "AS", "LI", "FB" };
        assetLiabilityFundBalanceObjectTypeCodes = new String[] { options.getFinancialObjectTypeAssetsCd(), // AS
                options.getFinObjectTypeLiabilitiesCode(), // LI
                options.getFinObjectTypeFundBalanceCd() // FB
        };
        // String[] encumbranceBaseBudgetBalanceTypeCodes = new String[] { "EX", "IE", "PE", "BB" };
        encumbranceBaseBudgetBalanceTypeCodes = new String[] { options.getExtrnlEncumFinBalanceTypCd(), // EX
                options.getIntrnlEncumFinBalanceTypCd(), // IE
                options.getPreencumbranceFinBalTypeCd(), // PE
                options.getBaseBudgetFinancialBalanceTypeCd() // BB
        };
    }

    /**
     * Use the options table to get a list of all the balance type codes associated with actual balances 
     *
     * @return an array of balance type codes for actual balances
     */
    private String[] getActualBalanceCodes() {
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
    private String[] getIncomeObjectTypeCodes() {
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
    private String[] getExpenseObjectTypeCodes() {
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
    private String[] getAssetLiabilityFundBalanceBalanceTypeCodes() {
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
    private String[] getEncumbranceBaseBudgetBalanceTypeCodes() {
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
     * @see org.kuali.module.gl.service.BalanceService#countBalancesForFiscalYear(java.lang.Integer)
     */
    public int countBalancesForFiscalYear(Integer year) {
        return balanceDao.countBalancesForFiscalYear(year);
    }

    /**
     * This method returns all of the balances specifically for the nominal activity closing job 
     * @param year year to find balances for
     * @return an Iterator of nominal activity balances
     * @see org.kuali.module.gl.service.BalanceService#findNominalActivityBalancesForFiscalYear(java.lang.Integer)
     */
    public Iterator<Balance> findNominalActivityBalancesForFiscalYear(Integer year) {
        return balanceDao.findNominalActivityBalancesForFiscalYear(year);
    }

    /**
     * Returns all the balances to be forwarded for the "cumulative" rule
     * @param year the fiscal year to find balances for
     * @return an Iterator of balances to process for the cumulative/active balance forward process
     * @see org.kuali.module.gl.service.BalanceService#findCumulativeBalancesToForwardForFiscalYear(java.lang.Integer)
     */
    public Iterator<Balance> findCumulativeBalancesToForwardForFiscalYear(Integer year) {
        return balanceDao.findCumulativeBalancesToForwardForFiscalYear(year);
    }

    /**
     * Returns all the balances specifically to be processed by the balance forwards job for the "general" rule
     * @param year the fiscal year to find balances for
     * @return an Iterator of balances to process for the general balance forward process
     * @see org.kuali.module.gl.service.BalanceService#findGeneralBalancesToForwardForFiscalYear(java.lang.Integer)
     */
    public Iterator<Balance> findGeneralBalancesToForwardForFiscalYear(Integer year) {
        return balanceDao.findGeneralBalancesToForwardForFiscalYear(year);
    }

    /**
     * Returns all of the balances to be forwarded for the organization reversion process
     * @param year the year of balances to find
     * @param endOfYear whether the organization reversion process is running end of year (before the fiscal year change over) or beginning of year (after the fiscal year change over)
     * @return an iterator of balances to put through the strenuous organization reversion process
     * @see org.kuali.module.gl.service.BalanceService#findOrganizationReversionBalancesForFiscalYear(java.lang.Integer, boolean)
     */
    public Iterator<Balance> findOrganizationReversionBalancesForFiscalYear(Integer year, boolean endOfYear) {
        return balanceDao.findOrganizationReversionBalancesForFiscalYear(year, endOfYear);
    }

}
