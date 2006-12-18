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
package org.kuali.module.labor.dao;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;

/**
 * This interface defines basic methods that GeneralLedgerPendingEntry Dao's must provide
 * 
 * 
 */
public interface LaborLedgerPendingEntryDao {


    /**
     * Find Pending Entries
     * 
     * @param fieldValues
     * @param isApproved
     * @return
     */
    public Collection findPendingEntries(Map fieldValues, boolean isApproved);

    /**
     * This method retrieves all approved pending ledger entries
     * 
     * @return all approved pending ledger entries
     */
    public Iterator findApprovedPendingLedgerEntries();

    /**
     * This method counts all approved pending ledger entries by account
     * 
     * @param account the given account
     * @return count of entries
     */
    public int countPendingLedgerEntries(Account account);

    /**
     * 
     * This method retrieves all pending ledger entries for the given encumbrance
     * 
     * @param encumbrance the encumbrance entry in the GL_Encumbrance_T table
     * @param isApproved the flag that indicates whether the pending entries are approved or don't care
     * @return all pending ledger entries of the given encumbrance
     */
    public Iterator findPendingLedgerEntries(Encumbrance encumbrance, boolean isApproved);

    /**
     * 
     * This method retrieves all pending ledger entries for the given encumbrance
     * 
     * @param balance the balance entry
     * @param isApproved the flag that indicates whether the pending entries are approved or don't care
     * @param isConsolidated consolidation option is applied or not
     * @return all pending ledger entries of the given balance
     */
    public Iterator findPendingLedgerEntries(Balance balance, boolean isApproved, boolean isConsolidated);

    /**
     * 
     * This method retrieves all pending ledger entries matching the given entry criteria
     * 
     * @param isApproved the flag that indicates whether the pending entries are approved or don't care
     * @param fieldValues the input fields and values
     * @return all pending ledger entries matching the given balance criteria
     */
    public Iterator findPendingLedgerEntriesForEntry(Map fieldValues, boolean isApproved);
}