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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.IteratorUtils;
import org.kuali.kfs.coa.service.ObjectTypeService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.gl.dataaccess.AccountBalanceDao;
import org.kuali.kfs.gl.service.AccountBalanceService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The basic implementation of the AccountBalanceService interface
 */
@Transactional
public class AccountBalanceServiceImpl implements AccountBalanceService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountBalanceServiceImpl.class);

    protected AccountBalanceDao accountBalanceDao;
    protected ConfigurationService kualiConfigurationService;
    protected UniversityDateService universityDateService;
    protected OptionsService optionsService;

    /**
     * Defers to the DAO to find the consolidated account balances, based on the keys given in the Map parameter
     * 
     * @param fieldValues the input fields and values
     * @return the summary records of account balance entries
     * @see org.kuali.kfs.gl.service.AccountBalanceService#findConsolidatedAvailableAccountBalance(java.util.Map)
     */
    public Iterator findConsolidatedAvailableAccountBalance(Map fieldValues) {
        LOG.debug("findConsolidatedAvailableAccountBalance() started");

        return accountBalanceDao.findConsolidatedAvailableAccountBalance(fieldValues);
    }

    /**
     * Given the map of parameters, constructs a query to find all qualifying account balance records
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated determine whether the search results are consolidated
     * @return a collection of account balance entries
     * @see org.kuali.kfs.gl.service.AccountBalanceService#findAvailableAccountBalance(java.util.Map)
     */
    public Iterator findAvailableAccountBalance(Map fieldValues) {
        LOG.debug("findAvailableAccountBalance() started");

        return accountBalanceDao.findAvailableAccountBalance(fieldValues);
    }

    /**
     * This finds account balances grouped by consolidation
     * 
     * @param universityFiscalYear the fiscal year account to find account balances for
     * @param chartOfAccountsCode the chart of accounts code to find account balances for
     * @param accountNumber the account number to find account balances for
     * @param subAccountNumber the sub account number to find account balances for
     * @param isCostShareExcluded should account balances found have cost share information excluded?
     * @param isConsolidated should account balances found be consolidated?
     * @param pendingEntryCode should pending entries be included in the query?
     * @return a List of qualifying account balance records
     * @see org.kuali.kfs.gl.service.AccountBalanceService#findAccountBalanceByConsolidation(java.util.Map, boolean, boolean)
     */
    public List findAccountBalanceByConsolidation(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber, boolean isCostShareExcluded, boolean isConsolidated, int pendingEntryCode) {
        LOG.debug("findAccountBalanceByConsolidation() started");

        ObjectTypeService objectTypeService = (ObjectTypeService) SpringContext.getBean(ObjectTypeService.class);

        String[] incomeObjectTypes = objectTypeService.getBasicIncomeObjectTypes(universityFiscalYear).toArray(new String[0]);
        String[] incomeTransferObjectTypes = { objectTypeService.getIncomeTransferObjectType(universityFiscalYear) };
        String[] expenseObjectTypes = objectTypeService.getBasicExpenseObjectTypes(universityFiscalYear).toArray(new String[0]);
        String[] expenseTransferObjectTypes = { objectTypeService.getExpenseTransferObjectType(universityFiscalYear) };

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
        AccountBalance income = new AccountBalance(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.INCOME));
        AccountBalance incomeTransfers = new AccountBalance(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.INCOME_FROM_TRANSFERS));
        AccountBalance incomeTotal = new AccountBalance(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.INCOME_TOTAL));
        AccountBalance expense = new AccountBalance(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.EXPENSE));
        AccountBalance expenseTransfers = new AccountBalance(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.EXPENSE_FROM_TRANSFERS));
        AccountBalance expenseTotal = new AccountBalance(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.EXPENSE_TOTAL));
        AccountBalance total = new AccountBalance(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.TOTAL));

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

        SystemOptions options = optionsService.getOptions(universityFiscalYear);
        UniversityDate today = universityDateService.getCurrentUniversityDate();
        // Get the data
        List balances = accountBalanceDao.findAccountBalanceByConsolidationByObjectTypes(allObjectTypes, universityFiscalYear, chartOfAccountsCode, accountNumber, isCostShareExcluded, isConsolidated, pendingEntryCode, options, today);

        // Convert it to Account Balances
        for (Iterator iter = balances.iterator(); iter.hasNext();) {
            Map bal = (Map) iter.next();
            AccountBalance bbc = new AccountBalance(AccountBalance.TYPE_CONSOLIDATION, bal, universityFiscalYear, chartOfAccountsCode, accountNumber);
            // Add variances to the AccountBalance and add the account balance (which will be on the detail line) to the results table (KFSCNTRB-1734)
            bbc.getDummyBusinessObject().setGenericAmount(bbc.getVariance());
            if ((subAccountNumber != null) && (subAccountNumber.length() > 0)) {
                if (bbc.getSubAccountNumber().equals(subAccountNumber)) {
                    results.add(bbc);
                }
            }
            else {
                results.add(bbc);
            }
        }

        // Calculate totals

        // Get balances for these parameters, then based on the object type code, put balances into the correct summary line
        List data = accountBalanceDao.findAccountBalanceByConsolidationByObjectTypes(incomeObjectTypes, universityFiscalYear, chartOfAccountsCode, accountNumber, isCostShareExcluded, isConsolidated, pendingEntryCode, options, today);
        for (Iterator iter = data.iterator(); iter.hasNext();) {
            Map bal = (Map) iter.next();
            AccountBalance bbc = new AccountBalance(AccountBalance.TYPE_CONSOLIDATION, bal, universityFiscalYear, chartOfAccountsCode, accountNumber);
            if ((subAccountNumber != null) && (subAccountNumber.length() > 0)) {
                if (bbc.getSubAccountNumber().equals(subAccountNumber)) {
                    income.add(bbc);
                    incomeTotal.add(bbc);
                }
            }
            else {
                String transferExpenseCode = bbc.getFinancialObject().getFinancialObjectLevel().getFinancialConsolidationObject().getFinConsolidationObjectCode();
                if (transferExpenseCode.equals(GeneralLedgerConstants.INCOME_OR_EXPENSE_TRANSFER_CONSOLIDATION_CODE)) {
                    incomeTransfers.add(bbc);
                    incomeTotal.add(bbc);
                }
                else {
                    income.add(bbc);
                    incomeTotal.add(bbc);
                }
            }
        }

        data = accountBalanceDao.findAccountBalanceByConsolidationByObjectTypes(incomeTransferObjectTypes, universityFiscalYear, chartOfAccountsCode, accountNumber, isCostShareExcluded, isConsolidated, pendingEntryCode, options, today);
        for (Iterator iter = data.iterator(); iter.hasNext();) {
            Map bal = (Map) iter.next();
            AccountBalance bbc = new AccountBalance(AccountBalance.TYPE_CONSOLIDATION, bal, universityFiscalYear, chartOfAccountsCode, accountNumber);
            if ((subAccountNumber != null) && (subAccountNumber.length() > 0)) {
                if (bbc.getSubAccountNumber().equals(subAccountNumber)) {
                    incomeTransfers.add(bbc);
                    incomeTotal.add(bbc);
                }
            }
            else {
                incomeTransfers.add(bbc);
                incomeTotal.add(bbc);
            }
        }

        data = accountBalanceDao.findAccountBalanceByConsolidationByObjectTypes(expenseObjectTypes, universityFiscalYear, chartOfAccountsCode, accountNumber, isCostShareExcluded, isConsolidated, pendingEntryCode, options, today);
        for (Iterator iter = data.iterator(); iter.hasNext();) {
            Map bal = (Map) iter.next();
            AccountBalance bbc = new AccountBalance(AccountBalance.TYPE_CONSOLIDATION, bal, universityFiscalYear, chartOfAccountsCode, accountNumber);
            if ((subAccountNumber != null) && (subAccountNumber.length() > 0)) {
                if (bbc.getSubAccountNumber().equals(subAccountNumber)) {
                    expense.add(bbc);
                    expenseTotal.add(bbc);
                }
            }
            else {
                String transferExpenseCode = bbc.getFinancialObject().getFinancialObjectLevel().getFinancialConsolidationObject().getFinConsolidationObjectCode();
                if (transferExpenseCode.equals(GeneralLedgerConstants.INCOME_OR_EXPENSE_TRANSFER_CONSOLIDATION_CODE)) {
                    expenseTransfers.add(bbc);
                    expenseTotal.add(bbc);
                }
                else {
                    expense.add(bbc);
                    expenseTotal.add(bbc);
                }
            }
        }

        data = accountBalanceDao.findAccountBalanceByConsolidationByObjectTypes(expenseTransferObjectTypes, universityFiscalYear, chartOfAccountsCode, accountNumber, isCostShareExcluded, isConsolidated, pendingEntryCode, options, today);
        for (Iterator iter = data.iterator(); iter.hasNext();) {
            Map bal = (Map) iter.next();
            AccountBalance bbc = new AccountBalance(AccountBalance.TYPE_CONSOLIDATION, bal, universityFiscalYear, chartOfAccountsCode, accountNumber);
            if ((subAccountNumber != null) && (subAccountNumber.length() > 0)) {
                if (bbc.getSubAccountNumber().equals(subAccountNumber)) {
                    expenseTransfers.add(bbc);
                    expenseTotal.add(bbc);
                }
            }
            else {
                expenseTransfers.add(bbc);
                expenseTotal.add(bbc);
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
     * Finds account balances grouped by object level
     * 
     * @param universityFiscalYear the fiscal year account to find account balances for
     * @param chartOfAccountsCode the chart of accounts code to find account balances for
     * @param accountNumber the account number to find account balances for
     * @param subAccountNumber the sub account number to find account balances for
     * @param financialConsolidationCode the consolidation code to find account balances for
     * @param isCostShareExcluded should account balances found have cost share information excluded?
     * @param isConsolidated should account balances found be consolidated?
     * @param pendingEntryCode should pending entries be included in the query?
     * @return a List of qualifying account balance records
     * @see org.kuali.kfs.gl.service.AccountBalanceService#findAccountBalanceByLevel(java.lang.Integer, java.lang.String,
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
        UniversityDate today = universityDateService.getCurrentUniversityDate();
        SystemOptions options = optionsService.getOptions(universityFiscalYear);
        List balances = accountBalanceDao.findAccountBalanceByLevel(universityFiscalYear, chartOfAccountsCode, accountNumber, financialConsolidationObjectCode, isCostShareExcluded, isConsolidated, pendingEntryCode, today, options);

        // Convert it to Account Balances
        for (Iterator iter = balances.iterator(); iter.hasNext();) {
            Map bal = (Map) iter.next();
            bal.put(GeneralLedgerConstants.ColumnNames.CONSOLIDATION_OBJECT_CODE, financialConsolidationObjectCode);
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
     * Finds account balances that match the qualifying parameters, grouped by object code
     * 
     * @param universityFiscalYear the fiscal year account to find account balances for
     * @param chartOfAccountsCode the chart of accounts code to find account balances for
     * @param accountNumber the account number to find account balances for
     * @param subAccountNumber the sub account number to find account balances for
     * @param financialObjectLevelCode the financial object level code to find account balances for
     * @param financialReportingSortCode the reporting sort code to sort account balances by
     * @param isCostShareExcluded should account balances found have cost share information excluded?
     * @param isConsolidated should account balances found be consolidated?
     * @param pendingEntryCode should pending entries be included in the query?
     * @return a List of qualifying account balance records
     * @see org.kuali.kfs.gl.service.AccountBalanceService#findAccountBalanceByObject(java.lang.Integer, java.lang.String,
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
        UniversityDate today = universityDateService.getCurrentUniversityDate();
        SystemOptions options = optionsService.getOptions(universityFiscalYear);
        List balances = accountBalanceDao.findAccountBalanceByObject(universityFiscalYear, chartOfAccountsCode, accountNumber, financialObjectLevelCode, financialReportingSortCode, isCostShareExcluded, isConsolidated, pendingEntryCode, today, options);

        // Convert it to Account Balances
        for (Iterator iter = balances.iterator(); iter.hasNext();) {
            Map bal = (Map) iter.next();
            bal.put(GeneralLedgerConstants.ColumnNames.OBJECT_LEVEL_CODE, financialObjectLevelCode);
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
     * Defers to the DAO to save the account balance.
     * 
     * @param ab account balance record to save
     * @see org.kuali.kfs.gl.service.AccountBalanceService#save(org.kuali.kfs.gl.businessobject.AccountBalance)
     */
    public void save(AccountBalance ab) {
        SpringContext.getBean(BusinessObjectService.class).save(ab);
    }

    /**
     * Purge an entire fiscal year for a single chart.
     * 
     * @param chartOfAccountsCode the chart of accounts of account balances to purge
     * @param year the fiscal year of account balances to purge
     */
    public void purgeYearByChart(String chartOfAccountsCode, int year) {
        LOG.debug("purgeYearByChart() started");

        accountBalanceDao.purgeYearByChart(chartOfAccountsCode, year);
    }

    /**
     * This method gets the number of the available account balances according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated determine whether the search results are consolidated
     * @return the number of the available account balances
     * @see org.kuali.kfs.gl.service.AccountBalanceService#getAvailableAccountBalanceCount(java.util.Map, boolean)
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

    /**
     * @param kcs
     */
    public void setConfigurationService(ConfigurationService kcs) {
        kualiConfigurationService = kcs;
    }

    /**
     * @param accountBalanceDao
     */
    public void setAccountBalanceDao(AccountBalanceDao accountBalanceDao) {
        this.accountBalanceDao = accountBalanceDao;
    }

    /**
     * @param universityDateService
     */
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    /**
     * Sets the optionsService.
     * 
     * @param optionsService
     */
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }
}
