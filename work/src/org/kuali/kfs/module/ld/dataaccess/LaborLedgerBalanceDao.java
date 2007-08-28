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
package org.kuali.module.labor.dao;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.module.labor.bo.EmployeeFunding;
import org.kuali.module.labor.bo.LaborBalanceSummary;
import org.kuali.module.labor.bo.LedgerBalance;

public interface LaborLedgerBalanceDao {

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
     * @param fiscalYear the given fiscal year 
     * @return an iterator over all balances for a given fiscal year
     */
    public Iterator<LedgerBalance> findBalancesForFiscalYear(Integer fiscalYear);

    /**
     * retrieve the current funds according to the given field values
     * @param fieldValues the given field values
     * @return the current funds according to the given field values
     */
    public List<LedgerBalance> findCurrentFunds(Map fieldValues);

    /**
     * retrieve the encumbrance funds according to the given field values
     * @param fieldValues the given field values
     * @return the encumbrance funds according to the given field values
     */
    public List<LedgerBalance> findEncumbranceFunds(Map fieldValues);
    
    /**
     * retrieve the current funds according to the given field values
     * @param fieldValues the given field values
     * @return the current funds according to the given field values
     */
    public List<EmployeeFunding> findCurrentEmployeeFunds(Map fieldValues);

    /**
     * retrieve the encumbrance funds according to the given field values
     * @param fieldValues the given field values
     * @return the encumbrance funds according to the given field values
     */
    public List<EmployeeFunding> findEncumbranceEmployeeFunds(Map fieldValues);

    /**
     * find the summary of the ledger balances for the given fiscal year and balance types
     * @param fiscalYear the given fiscal year
     * @param balanceTypes the given balance type codes
     * @return the ledger balances for the given fiscal year and balance types
     */
    public List<LaborBalanceSummary> findBalanceSummary(Integer fiscalYear, Collection<String> balanceTypes);
}