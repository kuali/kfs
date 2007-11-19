/*
 * Copyright 2005-2007 The Kuali Foundation.
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
 * The DAO interface that declares methods needed to query the database about balances
 */
public interface BalanceDao {

    /**
     * Get the GL Summary data
     * 
     * @param universityFiscalYear the fiscal year of balances to search for
     * @param balanceTypeCodes a list of balance type codes of balances to search for
     * @return iterator of reported on java.lang.Object arrays with the report data
     */
    public Iterator getGlSummary(int universityFiscalYear, List<String> balanceTypeCodes);

    /**
     * Given a transaction, finds the balance record it would affect
     * 
     * @param t a transaction
     * @return the balance record it would affect
     */
    public Balance getBalanceByTransaction(Transaction t);

    /**
     * Given the primary keys of a balance, finds the balance in the database. Although not all of the
     * primary keys are sent into this method...
     * 
     * Programmers are seriously advised not to use this method; the default implementation does not work
     * 
     * @param universityFiscalYear the university fiscal year of the balance to find
     * @param chartOfAccountsCode the chart of accounts code of the balance to find
     * @param accountNumber the account number of the balance to find
     * @return the balance that is specified by those...er...partially defined primary keys.
     */
    public Balance getBalanceByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber);

    /**
     * Based on specific query types, return an Iterator of balance records
     * 
     * @param account the account of balances to find
     * @param fiscalYear the fiscal year of balances to find
     * @param includedObjectCodes a Collection of object codes found balances should have one of
     * @param excludedObjectCodes a Collection of object codes found balances should not have one of
     * @param objectTypeCodes a Collection of object type codes found balances should have one of
     * @param balanceTypeCodes a Collection of balance type codes found balances should have one of
     * @return an Iterator of Balances
     */
    public Iterator findBalances(Account account, Integer fiscalYear, Collection includedObjectCodes, Collection excludedObjectCodes, Collection objectTypeCodes, Collection balanceTypeCodes);

    /**
     * Saves a balance to the database
     * 
     * @param b a balance to save
     */
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
     * @param universityFiscalYear the unversity fiscal year of balances to return
     * @param chartOfAccountsCode the chart of accounts code of balances to return
     * @param accountNumber the account number of balances to return
     * @param sfCode Sufficient Funds Code (used to determine sorting)
     * @return balance entries matching above
     */
    public Iterator<Balance> findAccountBalances(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String sfCode);

    /**
     * Returns the balance entries for the given year, chart, and account.
     * 
     * @param universityFiscalYear the fiscal year of balances to return
     * @param chartOfAccountsCode the chart of accounts code of balances to return
     * @param accountNumber the account number of balances to return
     * @return balance entries matching above sorted by object code
     */
    public Iterator<Balance> findAccountBalances(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber);

    /**
     * Returns the CB (current budget) record for the given year, chart, account, and object code if one is found.
     * 
     * @param universityFiscalYear the fiscal year of the CB balance to return
     * @param chartOfAccountsCode the chart of the accounts code of the CB balanes to return
     * @param accountNumber the account number of the CB balance to return
     * @param objectCode the object code of the CB balance to return
     * @return the CB Balance record
     */
    public Balance getCurrentBudgetForObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String objectCode);

    /**
     * Purge the sufficient funds balance table by year/chart
     * 
     * @param chart the chart of balances to purge
     * @param year the university fiscal year of balances to purge
     */
    public void purgeYearByChart(String chart, int year);

    /**
     * Returns all of the balances of a given fiscal year
     * 
     * @param year the university fiscal year of balances to return
     * @return an iterator over all balances for a given fiscal year
     */
    public Iterator<Balance> findBalancesForFiscalYear(Integer year);

    /**
     * This method returns the total count of balances for a fiscal year
     * 
     * @param year fiscal year to check
     * @return the count of balances
     */
    public int countBalancesForFiscalYear(Integer year);

    /**
     * This method returns all of the balances specifically for the nominal activity closing job
     * 
     * @param year year to find balances for
     * @return an Iterator of nominal activity balances
     */
    public Iterator<Balance> findNominalActivityBalancesForFiscalYear(Integer year);

    /**
     * Returns the balances specifically to be forwarded to the next fiscal year, based on the "general" rule
     * 
     * @param year the fiscal year to find balances for
     * @return an Iterator full of Balances
     */
    public Iterator<Balance> findGeneralBalancesToForwardForFiscalYear(Integer year);

    /**
     * Returns the C&G balances specifically to be forwarded to the next fiscal year, based on the "cumulative" rule
     * 
     * @param year the fiscal year to find balances for
     * @return and Iterator chuck full of Balances
     */
    public Iterator<Balance> findCumulativeBalancesToForwardForFiscalYear(Integer year);

    /**
     * Returns the balances that would specifically be picked up by the Organization Reversion year end process
     * 
     * @param year the year to find balances for
     * @return an iterator of the balances to process
     */
    public Iterator<Balance> findOrganizationReversionBalancesForFiscalYear(Integer year, boolean endOfYear);
}