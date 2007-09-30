/*
 * Copyright 2006-2007 The Kuali Foundation.
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
import java.util.Map;

import org.kuali.kfs.dao.GeneralLedgerPendingEntryDao;
import org.kuali.module.labor.bo.LaborLedgerPendingEntry;

/**
 * LaborLedgerPendingEntryDao DAO Interface.
 */
public interface LaborLedgerPendingEntryDao extends GeneralLedgerPendingEntryDao {

    /**
     * This method retrieves all pending ledger entries with the given search criteria
     * 
     * @param fieldValues the input fields and values
     * @param isApproved the flag that indicates whether the pending entries are approved or don't care
     * @return all pending ledger entries that may belong to encumbrance table
     */
    public Iterator<LaborLedgerPendingEntry> findPendingLedgerEntriesForLedgerBalance(Map fieldValues, boolean isApproved);

    /**
     * This method retrieves all pending ledger entries with the given search criteria
     * 
     * @param fieldValues the input fields and values
     * @param businessObject
     * @return all pending ledger entries that may belong to pendingentry table
     */
    public Collection<LaborLedgerPendingEntry> hasPendingLaborLedgerEntry(Map fieldValues, Object businessObject);
}