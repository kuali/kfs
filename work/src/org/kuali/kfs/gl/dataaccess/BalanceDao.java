/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.gl.dataaccess;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;

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
    public Iterator<Object[]> getGlSummary(int universityFiscalYear, Collection<String> balanceTypeCodes);

    /**
     * Given a transaction, finds the balance record it would affect
     *
     * @param t a transaction
     * @return the balance record it would affect
     */
    public Balance getBalanceByTransaction(Transaction t);

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
     * This method finds the cash balance entries according to input fields and values. The results will be limited to the system
     * lookup results limit.
     *
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @param encumbranceBalanceTypes a list of encumbrance Balance Types
     * @return the records of cash balance entries
     */
    public Iterator<Balance> lookupCashBalance(Map fieldValues, boolean isConsolidated, Collection<String> encumbranceBalanceTypes);

    /**
     * This method gets the size collection of cash balance entries or entry groups according to input fields and values
     *
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @param encumbranceBalanceTypes a list of encumbrance balance types
     * @return the size collection of cash balance entry groups
     */
    public Integer getDetailedCashBalanceRecordCount(Map fieldValues, Collection<String> encumbranceBalanceTypes);

    /**
     * This method gets the size collection of cash balance entry groups according to input fields and values if the entries are
     * required to be consolidated
     *
     * @param fieldValues the input fields and values
     * @param encumbranceBalanceTypes a list of encumbrance balance types
     * @return the size collection of cash balance entry groups
     */
    public int getConsolidatedCashBalanceRecordCount(Map fieldValues, Collection<String> encumbranceBalanceTypes);

    /**
     * This method finds the records of balance entries according to input fields and values
     *
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @param encumbranceBalanceTypes a list of encumbrance balance types
     * @return the records of balance entries
     */
    public Iterator findBalance(Map fieldValues, boolean isConsolidated, Collection<String> encumbranceBalanceTypes);

    /**
     * This method gets the size collection of balance entry groups according to input fields and values if the entries are required
     * to be consolidated
     *
     * @param fieldValues the input fields and values
     * @param encumbranceBalanceTypes a list of encumbrance balance types
     * @return the size collection of balance entry groups
     */
    public Iterator getConsolidatedBalanceRecordCount(Map fieldValues, Collection<String> encumbranceBalanceTypes);

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
     * @param nominalActivityObjectTypeCodes a List of nominal activity object type codes
     * @param currentYearOptions current year options
     * @return an Iterator of nominal activity balances
     */
    public Iterator<Balance> findNominalActivityBalancesForFiscalYear(Integer year, Collection<String> nominalActivityObjectTypeCodes, SystemOptions currentYearOptions);

    /**
     * Returns the balances specifically to be forwarded to the next fiscal year, based on the "general" rule
     *
     * @param year the fiscal year to find balances for
     * @param generalForwardBalanceObjectTypes a List of general Forward Balance Object Types
     * @param generalBalanceForwardBalanceTypesArray an array of general Balance Forward Balance Types
     * @return an Iterator full of Balances
     */
    public Iterator<Balance> findGeneralBalancesToForwardForFiscalYear(Integer year, Collection<String> generalForwardBalanceObjectTypes, Collection<String> generalBalanceForwardBalanceTypesArray);

    /**
     * Returns the C&G balances specifically to be forwarded to the next fiscal year, based on the "cumulative" rule
     *
     * @param year the fiscal year to find balances for
     * @param cumulativeForwardBalanceObjectTypes a List of cumulative Forward Balance Object Types
     * @param contractsAndGrantsDenotingValues a List of contracts And Grants Denoting Values
     * @param subFundGroupsForCumulativeBalanceForwardingArray an array of sub Fund Groups For Cumulative Balance Forwarding
     * @param cumulativeBalanceForwardBalanceTypesArray an array of cumulative Balance Forward Balance Types
     * @return and Iterator chuck full of Balances
     */
    public Iterator<Balance> findCumulativeBalancesToForwardForFiscalYear(Integer year, Collection<String> cumulativeForwardBalanceObjectTypes, Collection<String> contractsAndGrantsDenotingValues, Collection<String> subFundGroupsForCumulativeBalanceForwardingArray, Collection<String> cumulativeBalanceForwardBalanceTypesArray, boolean fundGroupDenotesCGInd);

    /**
     * Returns the balances that would specifically be picked up by the Organization Reversion year end process
     *
     * @param year the year to find balances for
     * @param endOfYear
     * @param options
     * @param parameterEvaluators a list of parameter evaluators
     * @return an iterator of the balances to process
     */
    public Iterator<Balance> findOrganizationReversionBalancesForFiscalYear(Integer year, boolean endOfYear, SystemOptions options, List<ParameterEvaluator> parameterEvaluators);
}
