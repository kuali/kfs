/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.GlSummary;
import org.kuali.module.gl.dao.BalanceDao;
import org.kuali.module.gl.service.BalanceService;

/**
 * 
 * This class is the OJB implementation of the Balance Service
 * 
 * @author Randall P. Embry (rpembry@indiana.edu)
 */
public class BalanceServiceImpl implements BalanceService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceServiceImpl.class);

    protected BalanceDao balanceDao;
    protected DateTimeService dateTimeService;

    // must have no asset, liability or fund balance balances other than object code 9899

    String[] fundBalanceObjectCodes = new String[] { "9899" }; // TODO Bill suggested
    // adding this to CHART

    // TODO extract these from APC
    String[] assetLiabilityFundBalanceBalanceTypeCodes = new String[] { "AS", "LI", "FB" };
    String[] encumbranceBaseBudgetBalanceTypeCodes = new String[] { "EX", "IE", "PE", "BB" };
    String[] actualBalanceCodes = new String[] { "AC" };
    String[] incomeObjectTypeCodes = new String[] { "CH", "IC", "IN", "TI" };
    String[] expenseObjectTypeCodes = new String[] { "EE", "ES", "EX", "TE" };

    private Collection wrap(String[] s) {
        return Arrays.asList(s);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.BalanceService#getGlSummary(int, java.util.List)
     */
    public List getGlSummary(int universityFiscalYear,List<String> balanceTypeCodes) {
        LOG.debug("getGlSummary() started");

        List sum = new ArrayList();

        Iterator i = balanceDao.getGlSummary(universityFiscalYear, balanceTypeCodes);
        while ( i.hasNext() ) {
            Object[] data = (Object[])i.next();
            sum.add(new GlSummary(data));
        }
        return sum;
    }

    /**
     * 
     * This method...
     * 
     * 
     * Here is an excerpt from the original Oracle trigger: SELECT fin_object_cd FROM gl_balance_t WHERE univ_fiscal_yr =
     * p_univ_fiscal_yr AND fin_coa_cd = p_fin_coa_cd AND account_nbr = p_account_nbr AND fin_object_cd != '9899' AND fin_obj_typ_cd
     * IN ('AS', 'LI', 'FB') AND fin_balance_typ_cd = 'AC' GROUP BY fin_object_cd HAVING ABS(SUM(fin_beg_bal_ln_amt +
     * acln_annl_bal_amt)) > 0);
     * 
     * 
     * 
     * added absolute value function to sum--prevents the case of 2 entries (1 pos and 1 neg) from canceling each other out and
     * allowing the acct to be closed when it shouldn't be.
     * 
     * 
     * 
     * @param account
     * @return
     */

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.service.BalanceService#findBalancesForFiscalYear(java.lang.Integer)
     */
    public Iterator<Balance> findBalancesForFiscalYear(Integer fiscalYear) {

        return (Iterator<Balance>) balanceDao.findBalancesForFiscalYear(fiscalYear);
    }

    /**
     * @see org.kuali.module.gl.service.BalanceService#hasAssetLiabilityFundBalanceBalances(org.kuali.module.chart.bo.Account)
     */
    public boolean hasAssetLiabilityFundBalanceBalances(Account account) {

        Integer fiscalYear = dateTimeService.getCurrentFiscalYear();
        Iterator balances = balanceDao.findBalances(account, fiscalYear, null, wrap(fundBalanceObjectCodes), wrap(assetLiabilityFundBalanceBalanceTypeCodes), wrap(actualBalanceCodes));

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
     * 
     * SELECT SUM(fin_beg_bal_ln_amt + acln_annl_bal_amt) INTO v_y FROM gl_balance_t WHERE univ_fiscal_yr = p_univ_fiscal_yr AND
     * fin_coa_cd = p_fin_coa_cd AND account_nbr = p_account_nbr AND (fin_object_cd = '9899' OR fin_obj_typ_cd IN ('CH', 'IC', 'IN',
     * 'TI')) AND fin_balance_typ_cd = 'AC';
     * 
     * @return
     */

    /**
     * 
     * 
     */
    protected KualiDecimal incomeBalances(Account account) {

        Integer fiscalYear = dateTimeService.getCurrentFiscalYear();
        Iterator balances = balanceDao.findBalances(account, fiscalYear, wrap(fundBalanceObjectCodes), null, wrap(incomeObjectTypeCodes), wrap(actualBalanceCodes));

        return sumBalances(balances);

    }


    /**
     * Here is an excerpt from the original Oracle Trigger: SELECT SUM(fin_beg_bal_ln_amt || acln_annl_bal_amt) INTO v_x FROM
     * gl_balance_t WHERE univ_fiscal_yr = p_univ_fiscal_yr AND fin_coa_cd = p_fin_coa_cd AND account_nbr = p_account_nbr AND
     * fin_obj_typ_cd IN ('EE', 'ES', 'EX', 'TE') AND fin_balance_typ_cd = 'AC';
     * 
     * This method...
     * 
     * @param account
     * @return
     */

    protected KualiDecimal expenseBalances(Account account) {

        Integer fiscalYear = dateTimeService.getCurrentFiscalYear();
        Iterator balances = balanceDao.findBalances(account, fiscalYear, null, null, wrap(expenseObjectTypeCodes), wrap(actualBalanceCodes));

        return sumBalances(balances);

    }


    /**
     * @see org.kuali.module.gl.service.BalanceService#fundBalanceWillNetToZero(org.kuali.module.chart.bo.Account)
     */
    public boolean fundBalanceWillNetToZero(Account account) {
        KualiDecimal income = incomeBalances(account);
        KualiDecimal expense = expenseBalances(account);

        return income.equals(expense);
    }

    /*
     * check for Encumbrances and base budgets
     * 
     * Here is an excerpt from the original Oracle Trigger: SELECT SUM(fin_beg_bal_ln_amt + acln_annl_bal_amt) INTO v_y FROM
     * gl_balance_t WHERE univ_fiscal_yr = p_univ_fiscal_yr AND fin_coa_cd = p_fin_coa_cd AND account_nbr = p_account_nbr AND
     * fin_balance_typ_cd IN ('EX', 'IE', 'PE', 'BB'); v_rowcnt := SQL%ROWCOUNT;
     * 
     * 
     */

    /**
     * @see org.kuali.module.gl.service.BalanceService#hasEncumbrancesOrBaseBudgets(org.kuali.module.chart.bo.Account)
     */
    public boolean hasEncumbrancesOrBaseBudgets(Account account) {

        Integer fiscalYear = dateTimeService.getCurrentFiscalYear();
        Iterator balances = balanceDao.findBalances(account, fiscalYear, null, null, null, wrap(encumbranceBaseBudgetBalanceTypeCodes));

        return sumBalances(balances).isNonZero();

    }

    /**
     * @see org.kuali.module.gl.service.BalanceService#beginningBalanceLoaded(org.kuali.module.chart.bo.Account)
     */
    public boolean beginningBalanceLoaded(Account account) {
        return true; // TODO: KULCOA-748 retrieve this from SystemOptions per Bill Overman
    }

    /**
     * @see org.kuali.module.gl.service.BalanceService#hasAssetLiabilityOrFundBalance(org.kuali.module.chart.bo.Account)
     */
    public boolean hasAssetLiabilityOrFundBalance(Account account) {
        return hasAssetLiabilityFundBalanceBalances(account) || !fundBalanceWillNetToZero(account) || hasEncumbrancesOrBaseBudgets(account);
    }

    /**
     * @see org.kuali.module.gl.service.BalanceService#save(org.kuali.module.gl.bo.Balance)
     */
    public void save(Balance b) {
        balanceDao.save(b);
    }


    public void setBalanceDao(BalanceDao balanceDao) {
        this.balanceDao = balanceDao;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * @see org.kuali.module.gl.service.BalanceService#findCashBalance(java.util.Map, boolean)
     */
    public Iterator findCashBalance(Map fieldValues, boolean isConsolidated) {
        return balanceDao.findCashBalance(fieldValues, isConsolidated);
    }

    /**
     * @see org.kuali.module.gl.service.BalanceService#findBalance(java.util.Map, boolean)
     */
    public Iterator findBalance(Map fieldValues, boolean isConsolidated) {
        return balanceDao.findBalance(fieldValues, isConsolidated);
    }

    /**
     * Purge the sufficient funds balance table by year/chart
     * 
     * @param chart
     * @param year
     */
    public void purgeYearByChart(String chart, int year) {
        LOG.debug("purgeYearByChart() started");

        balanceDao.purgeYearByChart(chart, year);
    }
}
