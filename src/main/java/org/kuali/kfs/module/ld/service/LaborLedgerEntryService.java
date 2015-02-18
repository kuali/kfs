/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ld.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.ld.businessobject.LedgerEntry;

/**
 * This interface provides its clients with access to labor leger entries in the backend data store.
 */
public interface LaborLedgerEntryService {

    /**
     * Save the given ledger entry or update it if it exsits
     * 
     * @param ledgerEntry the given ledger entry
     */
    void save(LedgerEntry ledgerEntry);

    /**
     * The sequence number is one of the primary keys of ledger entry. The entries can be grouped by other keys. This method is used
     * to get the maximum sequence number in the group of entries.
     * 
     * @param ledgerEntry the given ledger entry
     * @return the maximum sequence number in a group of entries. If the group doesn't exist, return 0.
     */
    Integer getMaxSequenceNumber(LedgerEntry ledgerEntry);

    /**
     * Find the ledger entries that satisfy the all entries in the given field-value pair
     * 
     * @param fieldValues the given field-value pair
     * @return the ledger entries that satisfy the all entries in the given field-value pair
     */
    Iterator<LedgerEntry> find(Map<String, String> fieldValues);

    /**
     * find the employees who were paid based on a set of specified pay type within the given report periods. Here, a pay type can
     * be determined by earn code and pay group.
     * 
     * @param payPeriods the given pay periods
     * @param balanceTypes the specified balance type codes
     * @param earnCodePayGroupMap the combination of earn codes and pay groups, where pay group is the key and earn code set is the value
     * @return the employees who were paid based on a set of specified pay type within the given report periods
     */
    List<String> findEmployeesWithPayType(Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap);
    
    /**
     * determine whether the given employee was paid based on a set of specified pay type within the given report periods. Here, a pay type can
     * be determined by earn code and pay group.
     * 
     * @param emplid the given employee id
     * @param payPeriods the given pay periods
     * @param balanceTypes the specified balance type codes
     * @param earnCodePayGroupMap the combination of earn codes and pay groups, where pay group is the key and earn code set is the
     *        value
     * @return true if the given employee was paid based on a set of specified pay type within the given report periods; otherwise, false
     */
    boolean isEmployeeWithPayType(String emplid, Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap);

    /**
     * delete the ledger entry records that were posted prior to the given fiscal year
     * 
     * @param fiscalYear the given fiscal year
     * @param chartOfAccountsCode the given chart of account code
     */
    void deleteLedgerEntriesPriorToYear(Integer fiscalYear, String chartOfAccountsCode);
}
