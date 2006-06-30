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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.module.gl.bo.AccountBalance;
import org.kuali.module.gl.dao.AccountBalanceDao;
import org.kuali.module.gl.service.AccountBalanceService;
import org.kuali.module.gl.web.Constant;

/**
 * This class...
 * 
 * @author Bin Gao from Michigan State University
 */
public class AccountBalanceServiceImpl implements AccountBalanceService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountBalanceServiceImpl.class);

    AccountBalanceDao accountBalanceDao;

    /**
     * Sets the accountBalanceDao attribute value.
     * 
     * @param accountBalanceDao The accountBalanceDao to set.
     */
    public void setAccountBalanceDao(AccountBalanceDao accountBalanceDao) {
        this.accountBalanceDao = accountBalanceDao;
    }

    /**
     * @see org.kuali.module.gl.service.AccountBalanceService#findConsolidatedAvailableAccountBalance(java.util.Map)
     */
    public Iterator findConsolidatedAvailableAccountBalance(Map fieldValues) {
        LOG.debug("findConsolidatedAvailableAccountBalance() started");

        return accountBalanceDao.findConsolidatedAvailableAccountBalance(fieldValues);
    }
    
    /**
     * @see org.kuali.module.gl.service.AccountBalanceService#findAvailableAccountBalance(java.util.Map)
     */
    public Iterator findAvailableAccountBalance(Map fieldValues) {
        LOG.debug("findAvailableAccountBalance() started");

        return accountBalanceDao.findAvailableAccountBalance(fieldValues);
    }

    /**
     * @see org.kuali.module.gl.service.AccountBalanceService#findAccountBalanceByConsolidation(java.util.Map, boolean, boolean)
     */
    public List findAccountBalanceByConsolidation(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber, boolean isCostShareExcluded, boolean isConsolidated, int pendingEntryCode) {
        LOG.debug("findAccountBalanceByConsolidation() started");

        // Put the 4 total lines at the beginning of the list
        List results = new ArrayList();
        AccountBalance inc = new AccountBalance(Constant.TOTAL_ACCOUNT_BALANCE_INCOME);
        AccountBalance exp = new AccountBalance(Constant.TOTAL_ACCOUNT_BALANCE_EXPENSE_GROSS);
        AccountBalance expin = new AccountBalance(Constant.TOTAL_ACCOUNT_BALANCE_EXPENSE_IN);
        AccountBalance total = new AccountBalance(Constant.TOTAL_ACCOUNT_BALANCE_AVAILABLE);
        results.add(inc);
        results.add(exp);
        results.add(expin);
        results.add(total);

        // If you want a sub account, you can't do consolidated
        if ((subAccountNumber != null) && (subAccountNumber.length() > 0)) {
            subAccountNumber = subAccountNumber.toUpperCase();
            isConsolidated = false;
        }

        // Get the data
        List balances = accountBalanceDao.findAccountBalanceByConsolidation(universityFiscalYear, chartOfAccountsCode, accountNumber, isCostShareExcluded, isConsolidated, pendingEntryCode);

        // Convert it to Account Balances
        for (Iterator iter = balances.iterator(); iter.hasNext();) {
            Map bal = (Map) iter.next();
            AccountBalance bbc = new AccountBalance("Consolidation", bal, universityFiscalYear, chartOfAccountsCode, accountNumber);
            if ((subAccountNumber != null) && (subAccountNumber.length() > 0)) {
                if (bbc.getSubAccountNumber().equals(subAccountNumber)) {
                    addBalanceToTotals(bbc, inc, exp, expin, total);
                    results.add(bbc);
                }
            }
            else {
                addBalanceToTotals(bbc, inc, exp, expin, total);
                results.add(bbc);
            }
        }

        // Calculate the transfer in total
        inc.getDummyBusinessObject().setGenericAmount(inc.getAccountLineActualsBalanceAmount().subtract(inc.getCurrentBudgetLineBalanceAmount()));
        exp.getDummyBusinessObject().setGenericAmount(exp.getCurrentBudgetLineBalanceAmount().subtract(exp.getAccountLineActualsBalanceAmount()).subtract(exp.getAccountLineEncumbranceBalanceAmount()));
        expin.setCurrentBudgetLineBalanceAmount(exp.getCurrentBudgetLineBalanceAmount());
        expin.setAccountLineActualsBalanceAmount(exp.getAccountLineActualsBalanceAmount());
        expin.setAccountLineEncumbranceBalanceAmount(exp.getAccountLineEncumbranceBalanceAmount());
        expin.getDummyBusinessObject().setGenericAmount(exp.getDummyBusinessObject().getGenericAmount());
        total.getDummyBusinessObject().setGenericAmount(inc.getDummyBusinessObject().getGenericAmount().add(exp.getDummyBusinessObject().getGenericAmount()));
        return results;
    }

    private void addBalanceToTotals(AccountBalance add, AccountBalance inc, AccountBalance exp, AccountBalance expin, AccountBalance total) {
        String reportingSortCode = add.getFinancialObject().getFinancialObjectType().getFinancialReportingSortCode();

        if (reportingSortCode.startsWith(Constant.START_CHAR_OF_REPORTING_SORT_CODE_B)) {
            exp.add(add);
        }
        else {
            inc.add(add);
        }
    }

    public List findAccountBalanceByLevel(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber, String financialConsolidationObjectCode, boolean isCostShareExcluded, boolean isConsolidated, int pendingEntryCode) {
        LOG.debug("findAccountBalanceByLevel() started");

        List results = new ArrayList();

        // If you want a sub account, you can't do consolidated
        if ((subAccountNumber != null) && (subAccountNumber.length() > 0)) {
            subAccountNumber = subAccountNumber.toUpperCase();
            isConsolidated = false;
        }

        // Get the data
        List balances = accountBalanceDao.findAccountBalanceByLevel(universityFiscalYear, chartOfAccountsCode, accountNumber, financialConsolidationObjectCode, isCostShareExcluded, isConsolidated, pendingEntryCode);

        // Convert it to Account Balances
        for (Iterator iter = balances.iterator(); iter.hasNext();) {
            Map bal = (Map) iter.next();
            bal.put("FIN_CONS_OBJ_CD", financialConsolidationObjectCode);
            AccountBalance bbc = new AccountBalance("Level", bal, universityFiscalYear, chartOfAccountsCode, accountNumber);
            if ((subAccountNumber != null) && (subAccountNumber.length() > 0)) {
                if (bbc.getSubAccountNumber().equals(subAccountNumber)) {
                    results.add(bbc);
                }
            }
            else {
                results.add(bbc);
            }
        }

        return results;
    }

    public List findAccountBalanceByObject(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber, String financialObjectLevelCode, String financialReportingSortCode, boolean isCostShareExcluded, boolean isConsolidated, int pendingEntryCode) {
        LOG.debug("findAccountBalanceByObject() started");

        List results = new ArrayList();

        // If you want a sub account, you can't do consolidated
        if ((subAccountNumber != null) && (subAccountNumber.length() > 0)) {
            subAccountNumber = subAccountNumber.toUpperCase();
            isConsolidated = false;
        }

        // Get the data
        List balances = accountBalanceDao.findAccountBalanceByObject(universityFiscalYear, chartOfAccountsCode, accountNumber, financialObjectLevelCode, financialReportingSortCode, isCostShareExcluded, isConsolidated, pendingEntryCode);

        // Convert it to Account Balances
        for (Iterator iter = balances.iterator(); iter.hasNext();) {
            Map bal = (Map) iter.next();
            bal.put("FIN_OBJ_LVL_CD", financialObjectLevelCode);
            AccountBalance bbc = new AccountBalance("Object", bal, universityFiscalYear, chartOfAccountsCode, accountNumber);
            if ((subAccountNumber != null) && (subAccountNumber.length() > 0)) {
                if (bbc.getSubAccountNumber().equals(subAccountNumber)) {
                    results.add(bbc);
                }
            }
            else {
                results.add(bbc);
            }
        }

        return results;
    }

    /**
     * @see org.kuali.module.gl.service.AccountBalanceService#save(org.kuali.module.gl.bo.AccountBalance)
     */
    public void save(AccountBalance ab) {
        accountBalanceDao.save(ab);
    }

    /**
     * Purge an entire fiscal year for a single chart.
     * 
     * @param chartOfAccountscode
     * @param year
     */
    public void purgeYearByChart(String chartOfAccountsCode, int year) {
        LOG.debug("purgeYearByChart() started");

        accountBalanceDao.purgeYearByChart(chartOfAccountsCode, year);
    }
}