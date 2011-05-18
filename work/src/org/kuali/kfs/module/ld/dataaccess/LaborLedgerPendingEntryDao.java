/*
 * Copyright 2006-2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.dataaccess;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.sys.dataaccess.GeneralLedgerPendingEntryDao;

/**
 * LaborLedgerPendingEntryDao DAO Interface.
 */
public interface LaborLedgerPendingEntryDao extends GeneralLedgerPendingEntryDao {

    /**
     * This method retrieves all pending ledger entries with the given search criteria
     * 
     * @param fieldValues the input fields and values
     * @param isApproved the flag that indicates whether the pending entries are approved or don't care
     * @param currentFYPeriod current FY period code
     * @param currentFY current fiscal year
     * @param encumbranceBalanceTypes a list of encumbrance balance types
     * @return all pending ledger entries that may belong to encumbrance table
     */
    public Iterator<LaborLedgerPendingEntry> findPendingLedgerEntriesForLedgerBalance(Map fieldValues, boolean isApproved, String currentFYPeriod, int currentFY, List<String> encumbranceBalanceTypes);

    /**
     * This method retrieves all pending ledger entries with the given search criteria
     * 
     * @param fieldValues the input fields and values
     * @param businessObject
     * @return all pending ledger entries that may belong to pendingentry table
     */
    public Collection<LaborLedgerPendingEntry> hasPendingLaborLedgerEntry(Map fieldValues, Object businessObject);
}
