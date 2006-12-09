/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.module.gl.dao;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.module.chart.bo.Account;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.Transaction;

/**
 * 
 * 
 */
public interface BalanceDao {

    /**
     * Get the GL Summary data
     * 
     * @param universityFiscalYear
     * @param balanceTypeCodes
     * @return iterator to Object[]
     */
    public Iterator getGlSummary(int universityFiscalYear, List<String> balanceTypeCodes);

    public Balance getBalanceByTransaction(Transaction t);

    public Balance getBalanceByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber);

    public Iterator findBalances(Account account, Integer fiscalYear, Collection includedObjectCodes, Collection excludedObjectCodes, Collection objectTypeCodes, Collection balanceTypeCodes);

    public void save(Balance b);

    /**
     * This method finds the cash balance entries according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the records of cash balance entries
     */
    public Iterator<Balance> findCashBalance(Map fieldValues, boolean isConsolidated);

    /**
     * This method gets the size collection of cash balance entries or entry groups according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the size collection of cash balance entry groups
     */
    public Integer getDetailedCashBalanceRecordCount(Map fieldValues);

    /**
     * This method gets the size collection of cash balance entry groups according to input fields and values if the entries are
     * required to be consolidated
     * 
     * @param fieldValues the input fields and values
     * @return the size collection of cash balance entry groups
     */
    public Iterator getConsolidatedCashBalanceRecordCount(Map fieldValues);

    /**
     * This method finds the records of balance entries according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the records of balance entries
     */
    public Iterator findBalance(Map fieldValues, boolean isConsolidated);

    /**
     * This method gets the size collection of balance entry groups according to input fields and values if the entries are required
     * to be consolidated
     * 
     * @param fieldValues the input fields and values
     * @return the size collection of balance entry groups
     */
    public Iterator getConsolidatedBalanceRecordCount(Map fieldValues);

    /**
     * Returns the balance entries for the given year, chart, and account.
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param sfCode Sufficient Funds Code (used to determine sorting)
     * @return balance entries matching above
     */
    public Iterator<Balance> findAccountBalances(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String sfCode);

    /**
     * Returns the balance entries for the given year, chart, and account.
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @return balance entries matching above sorted by object code
     */
    public Iterator<Balance> findAccountBalances(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber);

    /**
     * Returns the CB (current budget) record for the given year, chart, account, and object code if one is found.
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param objectCode
     * @return the CB Balance record
     */
    public Balance getCurrentBudgetForObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String objectCode);

    /**
     * Purge the sufficient funds balance table by year/chart
     * 
     * @param chart
     * @param year
     */
    public void purgeYearByChart(String chart, int year);

    /**
     * @param year
     * @return an iterator over all balances for a given fiscal year
     */
    public Iterator<Balance> findBalancesForFiscalYear(Integer year);
}