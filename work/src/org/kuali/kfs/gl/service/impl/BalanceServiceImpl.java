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
import org.kuali.core.util.TransactionalServiceUtils;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.service.OptionsService;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
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

    private Collection wrap(String[] s) {
        return Arrays.asList(s);
    }

    /**
     * 
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

        return (Iterator<Balance>) TransactionalServiceUtils.copyToExternallyUsuableIterator(balanceDao.findBalancesForFiscalYear(fiscalYear));
    }

    /**
     * @see org.kuali.module.gl.service.BalanceService#hasAssetLiabilityFundBalanceBalances(org.kuali.module.chart.bo.Account)
     */
    public boolean hasAssetLiabilityFundBalanceBalances(Account account) {

        Integer fiscalYear = SpringServiceLocator.getUniversityDateService().getCurrentFiscalYear();
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

        Integer fiscalYear = SpringServiceLocator.getUniversityDateService().getCurrentFiscalYear();

        ArrayList fundBalanceObjectCodes = new ArrayList();
        fundBalanceObjectCodes.add(account.getChartOfAccounts().getFundBalanceObjectCode());
        Iterator balances = balanceDao.findBalances(account, fiscalYear, fundBalanceObjectCodes, null, wrap(getIncomeObjectTypeCodes()), wrap(getActualBalanceCodes()));

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

        Integer fiscalYear = SpringServiceLocator.getUniversityDateService().getCurrentFiscalYear();
        Iterator balances = balanceDao.findBalances(account, fiscalYear, null, null, wrap(getExpenseObjectTypeCodes()), wrap(getActualBalanceCodes()));

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

        Integer fiscalYear = SpringServiceLocator.getUniversityDateService().getCurrentFiscalYear();
        Iterator balances = balanceDao.findBalances(account, fiscalYear, null, null, null, wrap(getEncumbranceBaseBudgetBalanceTypeCodes()));

        return sumBalances(balances).isNonZero();
    }

    /**
     * @see org.kuali.module.gl.service.BalanceService#beginningBalanceLoaded(org.kuali.module.chart.bo.Account)
     */
    public boolean beginningBalanceLoaded(Account account) {
        return optionsService.getCurrentYearOptions().isFinancialBeginBalanceLoadInd();
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

    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    /**
     * @see org.kuali.module.gl.service.BalanceService#findCashBalance(java.util.Map, boolean)
     */
    public Iterator findCashBalance(Map fieldValues, boolean isConsolidated) {
        LOG.debug("findCashBalance() started");
        
        return TransactionalServiceUtils.copyToExternallyUsuableIterator(balanceDao.findCashBalance(fieldValues, isConsolidated));
    }

    /**
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
            List recordCountList = IteratorUtils.toList(recordCountIterator);
            recordCount = recordCountList.size();
        }
        return recordCount;
    }

    /**
     * @see org.kuali.module.gl.service.BalanceService#findBalance(java.util.Map, boolean)
     */
    public Iterator findBalance(Map fieldValues, boolean isConsolidated) {
        LOG.debug("findBalance() started");
        return TransactionalServiceUtils.copyToExternallyUsuableIterator(balanceDao.findBalance(fieldValues, isConsolidated));
    }

    /**
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
            List recordCountList = IteratorUtils.toList(recordCountIterator);
            recordCount = recordCountList.size();
        }
        return recordCount;
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

    /**
     * Private method to load the values from the system options service and store them locally for later use.
     * 
     * 
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
                options.getFinancialObjectTypeTransferIncomeCode() // TI
        };
        // String[] expenseObjectTypeCodes = new String[] { "EE", "ES", "EX", "TE" };
        expenseObjectTypeCodes = new String[] { options.getFinObjTypeExpendNotExpCode(), // EE?
                options.getFinObjTypeExpenditureexpCd(), // ES
                options.getFinObjTypeExpNotExpendCode(), // EX?
                options.getFinancialObjectTypeTransferExpenseCode() // TE
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
                options.getBaseBudgetFinancialBalanceTypeCode() // BB
        };
    }

    private String[] getActualBalanceCodes() {
        if (actualBalanceCodes == null) {
            loadConstantsFromOptions();
        }
        return actualBalanceCodes;
    }

    private String[] getIncomeObjectTypeCodes() {
        if (incomeObjectTypeCodes == null) {
            loadConstantsFromOptions();
        }
        return incomeObjectTypeCodes;
    }

    private String[] getExpenseObjectTypeCodes() {
        if (expenseObjectTypeCodes == null) {
            loadConstantsFromOptions();
        }
        return expenseObjectTypeCodes;
    }

    private String[] getAssetLiabilityFundBalanceBalanceTypeCodes() {
        if (assetLiabilityFundBalanceObjectTypeCodes == null) {
            loadConstantsFromOptions();
        }
        return assetLiabilityFundBalanceObjectTypeCodes;
    }

    private String[] getEncumbranceBaseBudgetBalanceTypeCodes() {
        if (encumbranceBaseBudgetBalanceTypeCodes == null) {
            loadConstantsFromOptions();
        }
        return encumbranceBaseBudgetBalanceTypeCodes;
    }
}
