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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.IteratorUtils;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.TransactionalServiceUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.bo.AccountBalance;
import org.kuali.module.gl.dao.AccountBalanceDao;
import org.kuali.module.gl.service.AccountBalanceService;
import org.kuali.module.gl.util.OJBUtility;
import org.kuali.module.gl.web.Constant;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AccountBalanceServiceImpl implements AccountBalanceService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountBalanceServiceImpl.class);

    AccountBalanceDao accountBalanceDao;
    KualiConfigurationService kualiConfigurationService;

    /**
     * @see org.kuali.module.gl.service.AccountBalanceService#findConsolidatedAvailableAccountBalance(java.util.Map)
     */
    public Iterator findConsolidatedAvailableAccountBalance(Map fieldValues) {
        LOG.debug("findConsolidatedAvailableAccountBalance() started");

        return TransactionalServiceUtils.copyToExternallyUsuableIterator(accountBalanceDao.findConsolidatedAvailableAccountBalance(fieldValues));
    }

    /**
     * @see org.kuali.module.gl.service.AccountBalanceService#findAvailableAccountBalance(java.util.Map)
     */
    public Iterator findAvailableAccountBalance(Map fieldValues) {
        LOG.debug("findAvailableAccountBalance() started");

        return TransactionalServiceUtils.copyToExternallyUsuableIterator(accountBalanceDao.findAvailableAccountBalance(fieldValues));
    }

    /**
     * @see org.kuali.module.gl.service.AccountBalanceService#findAccountBalanceByConsolidation(java.util.Map, boolean, boolean)
     */
    public List findAccountBalanceByConsolidation(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber, boolean isCostShareExcluded, boolean isConsolidated, int pendingEntryCode) {
        LOG.debug("findAccountBalanceByConsolidation() started");

        String[] incomeObjectTypes = kualiConfigurationService.getApplicationParameterValues(GLConstants.GL_ACCOUNT_BALANCE_SERVICE_GROUP, GLConstants.GlAccountBalanceGroupParameters.INCOME_OBJECT_TYPE_CODES);
        String[] incomeTransferObjectTypes = kualiConfigurationService.getApplicationParameterValues(GLConstants.GL_ACCOUNT_BALANCE_SERVICE_GROUP, GLConstants.GlAccountBalanceGroupParameters.INCOME_TRANSFER_OBJECT_TYPE_CODES);
        String[] expenseObjectTypes = kualiConfigurationService.getApplicationParameterValues(GLConstants.GL_ACCOUNT_BALANCE_SERVICE_GROUP, GLConstants.GlAccountBalanceGroupParameters.EXPENSE_OBJECT_TYPE_CODES);
        String[] expenseTransferObjectTypes = kualiConfigurationService.getApplicationParameterValues(GLConstants.GL_ACCOUNT_BALANCE_SERVICE_GROUP, GLConstants.GlAccountBalanceGroupParameters.EXPENSE_TRANSFER_OBJECT_TYPE_CODES);

        // Consolidate all object types into one array (yes I could have used lists, but it was just as many lines of code than
        // this)
        String[] allObjectTypes = new String[incomeObjectTypes.length + incomeTransferObjectTypes.length + expenseObjectTypes.length + expenseTransferObjectTypes.length];
        int count = 0;
        for (int i = 0; i < incomeObjectTypes.length; i++) {
            allObjectTypes[count++] = incomeObjectTypes[i];
        }
        for (int i = 0; i < incomeTransferObjectTypes.length; i++) {
            allObjectTypes[count++] = incomeTransferObjectTypes[i];
        }
        for (int i = 0; i < expenseObjectTypes.length; i++) {
            allObjectTypes[count++] = expenseObjectTypes[i];
        }
        for (int i = 0; i < expenseTransferObjectTypes.length; i++) {
            allObjectTypes[count++] = expenseTransferObjectTypes[i];
        }

        // Put the total lines at the beginning of the list
        List results = new ArrayList();
        AccountBalance income = new AccountBalance(kualiConfigurationService.getPropertyString(KFSKeyConstants.AccountBalanceService.INCOME));
        AccountBalance incomeTransfers = new AccountBalance(kualiConfigurationService.getPropertyString(KFSKeyConstants.AccountBalanceService.INCOME_FROM_TRANSFERS));
        AccountBalance incomeTotal = new AccountBalance(kualiConfigurationService.getPropertyString(KFSKeyConstants.AccountBalanceService.INCOME_TOTAL));
        AccountBalance expense = new AccountBalance(kualiConfigurationService.getPropertyString(KFSKeyConstants.AccountBalanceService.EXPENSE));
        AccountBalance expenseTransfers = new AccountBalance(kualiConfigurationService.getPropertyString(KFSKeyConstants.AccountBalanceService.EXPENSE_FROM_TRANSFERS));
        AccountBalance expenseTotal = new AccountBalance(kualiConfigurationService.getPropertyString(KFSKeyConstants.AccountBalanceService.EXPENSE_TOTAL));
        AccountBalance total = new AccountBalance(kualiConfigurationService.getPropertyString(KFSKeyConstants.AccountBalanceService.TOTAL));

        results.add(income);
        results.add(incomeTransfers);
        results.add(incomeTotal);
        results.add(expense);
        results.add(expenseTransfers);
        results.add(expenseTotal);
        results.add(total);

        // If you want a sub account, you can't do consolidated
        if ((subAccountNumber != null) && (subAccountNumber.length() > 0)) {
            subAccountNumber = subAccountNumber.toUpperCase();
            isConsolidated = false;
        }

        // Get the data
        List balances = accountBalanceDao.findAccountBalanceByConsolidationByObjectTypes(allObjectTypes, universityFiscalYear, chartOfAccountsCode, accountNumber, isCostShareExcluded, isConsolidated, pendingEntryCode);

        // Convert it to Account Balances
        for (Iterator iter = balances.iterator(); iter.hasNext();) {
            Map bal = (Map) iter.next();
            AccountBalance bbc = new AccountBalance(AccountBalance.TYPE_CONSOLIDATION, bal, universityFiscalYear, chartOfAccountsCode, accountNumber);

            if ((subAccountNumber != null) && (subAccountNumber.length() > 0)) {
                if (bbc.getSubAccountNumber().equals(subAccountNumber)) {
                    addBalanceToTotals(bbc, incomeTotal, expenseTotal);
                    results.add(bbc);
                }
            }
            else {
                addBalanceToTotals(bbc, incomeTotal, expenseTotal);
                results.add(bbc);
            }
        }

        // Calculate totals

        // Get balances for these parameters, then based on the object type code, put balances into the correct summary line
        List data = accountBalanceDao.findAccountBalanceByConsolidationByObjectTypes(incomeObjectTypes, universityFiscalYear, chartOfAccountsCode, accountNumber, isCostShareExcluded, isConsolidated, pendingEntryCode);
        for (Iterator iter = data.iterator(); iter.hasNext();) {
            Map bal = (Map) iter.next();
            AccountBalance bbc = new AccountBalance(AccountBalance.TYPE_CONSOLIDATION, bal, universityFiscalYear, chartOfAccountsCode, accountNumber);
            if ((subAccountNumber != null) && (subAccountNumber.length() > 0)) {
                if (bbc.getSubAccountNumber().equals(subAccountNumber)) {
                    income.add(bbc);
                }
            }
            else {
                income.add(bbc);
            }
        }

        data = accountBalanceDao.findAccountBalanceByConsolidationByObjectTypes(incomeTransferObjectTypes, universityFiscalYear, chartOfAccountsCode, accountNumber, isCostShareExcluded, isConsolidated, pendingEntryCode);
        for (Iterator iter = data.iterator(); iter.hasNext();) {
            Map bal = (Map) iter.next();
            AccountBalance bbc = new AccountBalance(AccountBalance.TYPE_CONSOLIDATION, bal, universityFiscalYear, chartOfAccountsCode, accountNumber);
            if ((subAccountNumber != null) && (subAccountNumber.length() > 0)) {
                if (bbc.getSubAccountNumber().equals(subAccountNumber)) {
                    incomeTransfers.add(bbc);
                }
            }
            else {
                incomeTransfers.add(bbc);
            }
        }

        data = accountBalanceDao.findAccountBalanceByConsolidationByObjectTypes(expenseObjectTypes, universityFiscalYear, chartOfAccountsCode, accountNumber, isCostShareExcluded, isConsolidated, pendingEntryCode);
        for (Iterator iter = data.iterator(); iter.hasNext();) {
            Map bal = (Map) iter.next();
            AccountBalance bbc = new AccountBalance(AccountBalance.TYPE_CONSOLIDATION, bal, universityFiscalYear, chartOfAccountsCode, accountNumber);
            if ((subAccountNumber != null) && (subAccountNumber.length() > 0)) {
                if (bbc.getSubAccountNumber().equals(subAccountNumber)) {
                    expense.add(bbc);
                }
            }
            else {
                expense.add(bbc);
            }
        }

        data = accountBalanceDao.findAccountBalanceByConsolidationByObjectTypes(expenseTransferObjectTypes, universityFiscalYear, chartOfAccountsCode, accountNumber, isCostShareExcluded, isConsolidated, pendingEntryCode);
        for (Iterator iter = data.iterator(); iter.hasNext();) {
            Map bal = (Map) iter.next();
            AccountBalance bbc = new AccountBalance(AccountBalance.TYPE_CONSOLIDATION, bal, universityFiscalYear, chartOfAccountsCode, accountNumber);
            if ((subAccountNumber != null) && (subAccountNumber.length() > 0)) {
                if (bbc.getSubAccountNumber().equals(subAccountNumber)) {
                    expenseTransfers.add(bbc);
                }
            }
            else {
                expenseTransfers.add(bbc);
            }
        }

        // Add up variances
        income.getDummyBusinessObject().setGenericAmount(income.getAccountLineActualsBalanceAmount().add(income.getAccountLineEncumbranceBalanceAmount()).subtract(income.getCurrentBudgetLineBalanceAmount()));
        incomeTransfers.getDummyBusinessObject().setGenericAmount(incomeTransfers.getAccountLineActualsBalanceAmount().add(incomeTransfers.getAccountLineEncumbranceBalanceAmount()).subtract(incomeTransfers.getCurrentBudgetLineBalanceAmount()));
        incomeTotal.getDummyBusinessObject().setGenericAmount(income.getDummyBusinessObject().getGenericAmount().add(incomeTransfers.getDummyBusinessObject().getGenericAmount()));

        expense.getDummyBusinessObject().setGenericAmount(expense.getCurrentBudgetLineBalanceAmount().subtract(expense.getAccountLineActualsBalanceAmount()).subtract(expense.getAccountLineEncumbranceBalanceAmount()));
        expenseTransfers.getDummyBusinessObject().setGenericAmount(expenseTransfers.getCurrentBudgetLineBalanceAmount().subtract(expenseTransfers.getAccountLineActualsBalanceAmount()).subtract(expenseTransfers.getAccountLineEncumbranceBalanceAmount()));
        expenseTotal.getDummyBusinessObject().setGenericAmount(expense.getDummyBusinessObject().getGenericAmount().add(expenseTransfers.getDummyBusinessObject().getGenericAmount()));

        total.getDummyBusinessObject().setGenericAmount(incomeTotal.getDummyBusinessObject().getGenericAmount().add(expenseTotal.getDummyBusinessObject().getGenericAmount()));

        return results;
    }

    /**
     * @param accountBalance
     * @param income
     * @param grossExpense
     * @param expin
     * @param total
     */
    private void addBalanceToTotals(AccountBalance accountBalance, AccountBalance incomeTotal, AccountBalance expenseTotal) {
        String reportingSortCode = accountBalance.getFinancialObject().getFinancialObjectType().getFinancialReportingSortCode();

        if (reportingSortCode.startsWith(Constant.START_CHAR_OF_REPORTING_SORT_CODE_B)) {
            expenseTotal.add(accountBalance);
        }
        else {
            incomeTotal.add(accountBalance);
        }
    }

    /**
     * @see org.kuali.module.gl.service.AccountBalanceService#findAccountBalanceByLevel(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, boolean, boolean, int)
     */
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
            bal.put(GLConstants.ColumnNames.CONSOLIDATION_OBJECT_CODE, financialConsolidationObjectCode);
            AccountBalance bbc = new AccountBalance(AccountBalance.TYPE_LEVEL, bal, universityFiscalYear, chartOfAccountsCode, accountNumber);
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
     * @see org.kuali.module.gl.service.AccountBalanceService#findAccountBalanceByObject(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, boolean, int)
     */
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
            bal.put(GLConstants.ColumnNames.OBJECT_LEVEL_CODE, financialObjectLevelCode);
            AccountBalance bbc = new AccountBalance(AccountBalance.TYPE_OBJECT, bal, universityFiscalYear, chartOfAccountsCode, accountNumber);
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

    /**
     * @see org.kuali.module.gl.service.AccountBalanceService#getAvailableAccountBalanceCount(java.util.Map, boolean)
     */
    public Integer getAvailableAccountBalanceCount(Map fieldValues, boolean isConsolidated) {
        Integer recordCount = null;
        if (!isConsolidated) {
            recordCount = OJBUtility.getResultSizeFromMap(fieldValues, new AccountBalance()).intValue();
        }
        else {
            Iterator recordCountIterator = accountBalanceDao.findConsolidatedAvailableAccountBalance(fieldValues);
            // TODO: WL: why build a list and waste time/memory when we can just iterate through the iterator and do a count?
            List recordCountList = IteratorUtils.toList(recordCountIterator);
            recordCount = recordCountList.size();
        }
        return recordCount;
    }

    public void setKualiConfigurationService(KualiConfigurationService kcs) {
        kualiConfigurationService = kcs;
    }

    public void setAccountBalanceDao(AccountBalanceDao accountBalanceDao) {
        this.accountBalanceDao = accountBalanceDao;
    }
}