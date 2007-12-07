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
import java.util.Set;

import org.kuali.module.labor.bo.LaborObject;
import org.kuali.module.labor.bo.LedgerEntry;

/**
 * This is the data access object for ledger entry.
 * 
 * @see org.kuali.module.labor.bo.LedgerEntry
 */
public interface LaborLedgerEntryDao {

    /**
     * The sequence number is one of the primary keys of ledger entry. The entries can be grouped by other keys. This method is used
     * to get the maximum sequence number in the group of entries.
     * 
     * @param ledgerEntry the given ledger entry
     * @return the maximum sequence number in a group of entries. If the group doesn't exist, return 0.
     */
    Integer getMaxSquenceNumber(LedgerEntry ledgerEntry);

    /**
     * Find the ledger entries that satisfy the all entries in the given field-value pair
     * 
     * @param fieldValues the given field-value pair
     * @return the ledger entries that satisfy the all entries in the given field-value pair
     */
    Iterator<LedgerEntry> find(Map<String, String> fieldValues);

    /**
     * save the given ledger entry into the underlying data store
     * 
     * @param ledgerEntry the given ledger entry
     */
    void save(LedgerEntry ledgerEntry);

    /**
     * find the 12 Month employees who were paid within the given pay periods. 
     * @param payPeriods the given pay periods
     * @param balanceTypes the specified balance type codes
     * @param earnCodePayGroupMap the combination of earn codes and pay groups, where pay group is the key and earn code set is the value
     * @return the 12 Month employees who were paid within the given pay periods
     */
    List<String> findEmployeesWith12MonthPay(Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap);
}