/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.service;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.ld.businessobject.EmployeeFunding;
import org.kuali.kfs.module.ld.businessobject.LaborBalanceSummary;
import org.kuali.kfs.module.ld.businessobject.LaborTransaction;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.businessobject.LedgerBalanceForYearEndBalanceForward;

public interface LaborLedgerBalanceService {

    /**
     * @param fiscalYear the given fiscal year
     * @return an Iterator over all balances for a given year
     */
    public Iterator<LedgerBalance> findBalancesForFiscalYear(Integer fiscalYear);

    /**
     * @param fiscalYear the given fiscal year
     * @param fieldValues the input fields and values
     * @param encumbranceBalanceTypes a list of encumbrance types
     * @return an Iterator over all balances for a given year and search criteria
     */
    public Iterator<LedgerBalance> findBalancesForFiscalYear(Integer fiscalYear, Map<String, String> fieldValues, List<String> encumbranceBalanceTypes);

    /**
     * @param fiscalYear the given fiscal year
     * @param fieldValues the input fields and values
     * @param subFundGroupCodes the given list of qualified sub fund group codes
     * @param fundGroupCodes the given list of qualified group codes
     * @return an Iterator over all balances for a given year and search criteria that include the accounts of balances must belong
     *         to the given sub fund group or fund group
     */
    public Iterator<LedgerBalanceForYearEndBalanceForward> findBalancesForFiscalYear(Integer fiscalYear, Map<String, String> fieldValues, Collection<String> subFundGroupCodes, Collection<String> fundGroupCodes);

    /**
     * This method gets the size of balance entries according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @param encumbranceBalanceTypes a list of encumbrance balance types
     * @param noZeroAmounts makes sure at least one of the 13 monthly buckets has an amount not equals to zero
     * @return the size of balance entries
     */
    public Iterator findBalance(Map fieldValues, boolean isConsolidated, List<String> encumbranceBalanceTypes, boolean noZeroAmounts);

    /**
     * This method finds the summary records of balance entries according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @param encumbranceBalanceTypes a list of encumbranceBalanceTypes
     * @param noZeroAmounts makes sure at least one of the 13 monthly buckets has an amount not equals to zero
     * @return the summary records of balance entries
     */
    public Integer getBalanceRecordCount(Map fieldValues, boolean isConsolidated, List<String> encumbranceBalanceTypes, boolean noZeroAmounts);

    /**
     * find a ledger balance from the given ledger balance collection with the given transaction information
     * 
     * @param ledgerBalanceCollection the given ledger balance collection
     * @param transaction the given transaction information
     * @param keyList the given list of keys that need to be compared
     * @return a matching ledger balance from the given ledger balance
     */
    public <T extends LedgerBalance> T findLedgerBalance(Collection<T> ledgerBalanceCollection, LaborTransaction transaction, List<String> keyList);

    /**
     * find a ledger balance from the given ledger balance collection with the given transaction information
     * 
     * @param ledgerBalanceCollection the given ledger balance collection
     * @param transaction the given transaction information
     * @return a matching ledger balance from the given ledger balance
     */
    public <T extends LedgerBalance> T findLedgerBalance(Collection<T> ledgerBalanceCollection, LaborTransaction transaction);

    /**
     * convert the given transaction information into a ledger balance and add it into the given ledger balance collection with
     * 
     * @param ledgerBalanceCollection the given ledger balance collection
     * @param transaction the given transaction information
     * @return the ledger balance that has been added; otherwise, null;
     */
    public LedgerBalance addLedgerBalance(Collection<LedgerBalance> ledgerBalanceCollection, LaborTransaction transaction);

    /**
     * update the given ledger balance with the given transaction information
     * 
     * @param ledgerBalance the given ledger balance
     * @param transaction the given transaction information
     */
    public <T extends LedgerBalance> void updateLedgerBalance(T ledgerBalance, LaborTransaction transaction);

    /**
     * find the funding by employee
     * 
     * @param fieldValues the given field values
     * @return the funding by employee
     */
    public List<EmployeeFunding> findEmployeeFunding(Map fieldValues, boolean isConsolidated);

    /**
     * find the employee funding with the corresponding CSF trakers
     * 
     * @param fieldValues the given field values
     * @return the employee funding with the corresponding CSF trakers
     */
    public List<EmployeeFunding> findEmployeeFundingWithCSFTracker(Map fieldValues, boolean isConsolidated);

    /**
     * find the summary of the ledger balances for the given fiscal year and balance types
     * 
     * @param fiscalYear the given fiscal year
     * @param balanceTypes the given balance type codes
     * @return the ledger balances for the given fiscal year and balance types
     */
    public List<LaborBalanceSummary> findBalanceSummary(Integer fiscalYear, Collection<String> balanceTypes);

    /**
     * save the given ledger balance into the underlying data store
     * 
     * @param ledgerBalance the given ledger balance
     */
    public void save(LedgerBalance ledgerBalance);

    /**
     * find the accounts (chart of accounts code + account number) in the given fund groups
     * 
     * @param fiscalYear the given fiscal year
     * @param fieldValues the input fields and values
     * @param subFundGroupCodes the given list of qualified sub fund group codes
     * @param fundGroupCodes the given list of qualified group codes
     * @return the accounts (chart of accounts code + account number) in the given fund groups
     */
    public List<List<String>> findAccountsInFundGroups(Integer fiscalYear, Map<String, String> fieldValues, List<String> subFundGroupCodes, List<String> fundGroupCodes);

    /**
     * find all ledger balances matching the given criteria within the given fiscal years
     * 
     * @param fieldValues the given field values
     * @param excludedFieldValues the given field values that must not be matched
     * @param fiscalYears the given fiscal years
     * @param balanceTypeList the given balance type code list
     * @param positionObjectGroupCodes the specified position obejct group codes
     * @return all ledger balances matching the given criteria within the given fiscal years
     */
    public Collection<LedgerBalance> findLedgerBalances(Map<String, List<String>> fieldValues, Map<String, List<String>> excludedFieldValues, Set<Integer> fiscalYears, List<String> balanceTypeList, List<String> positionObjectGroupCodes);

    /**
     * delete the ledger balance records that were posted prior to the given fiscal year
     * 
     * @param fiscalYear the given fiscal year
     * @param chartOfAccountsCode the given chart of account code
     */
    public void deleteLedgerBalancesPriorToYear(Integer fiscalYear, String chartOfAccountsCode);
}
