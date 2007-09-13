/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.service;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.module.labor.bo.LaborBalanceSummary;
import org.kuali.module.labor.bo.LaborTransaction;
import org.kuali.module.labor.bo.EmployeeFunding;
import org.kuali.module.labor.bo.LedgerBalance;

public interface LaborLedgerBalanceService {

    /**
     * @param fiscalYear the given fiscal year
     * @return an Iterator over all balances for a given year
     */
    public Iterator<LedgerBalance> findBalancesForFiscalYear(Integer fiscalYear);

    /**
     * This method gets the size of balance entries according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the size of balance entries
     */
    public Iterator findBalance(Map fieldValues, boolean isConsolidated);

    /**
     * This method finds the summary records of balance entries according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the summary records of balance entries
     */
    public Integer getBalanceRecordCount(Map fieldValues, boolean isConsolidated);
    
    /**
     * find a ledger balance from the given ledger balance collection with the given transaction information
     * 
     * @param ledgerBalanceCollection the given ledger balance collection
     * @param transaction the given transaction information
     * @param keyList the given list of keys that need to be compared
     * @return a matching ledger balance from the given ledger balance
     */
    public LedgerBalance findLedgerBalance(Collection<LedgerBalance> ledgerBalanceCollection, LaborTransaction transaction, List<String> keyList);

    /**
     * find a ledger balance from the given ledger balance collection with the given transaction information
     * 
     * @param ledgerBalanceCollection the given ledger balance collection
     * @param transaction the given transaction information
     * @return a matching ledger balance from the given ledger balance
     */
    public LedgerBalance findLedgerBalance(Collection<LedgerBalance> ledgerBalanceCollection, LaborTransaction transaction);

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
    public void updateLedgerBalance(LedgerBalance ledgerBalance, LaborTransaction transaction);

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
     * @param fiscalYear the given fiscal year
     * @param balanceTypes the given balance type codes
     * @return the ledger balances for the given fiscal year and balance types
     */
    public List<LaborBalanceSummary> findBalanceSummary(Integer fiscalYear, Collection<String> balanceTypes);
}
