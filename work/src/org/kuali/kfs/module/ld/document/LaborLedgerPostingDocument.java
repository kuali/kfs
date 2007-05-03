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
package org.kuali.module.labor.document;

import java.util.List;

import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.labor.bo.PendingLedgerEntry;

/**
 * Defines methods that must be implements for a labor ledger posting document.
 */
public interface LaborLedgerPostingDocument extends AccountingDocument{
    
    /**
     * This method retrieves the list of Labor Ledgre Pending Entries for the document.
     * 
     * @return A list of labor ledger pending entries.
     */
    public List<PendingLedgerEntry> getLaborLedgerPendingEntries();

    /**
     * This method sets the list of labor ledger pending entries for the document.
     * 
     * @param laborLedgerPendingEntries the given labor ledger pending entries
     */
    public void setLaborLedgerPendingEntries(List<PendingLedgerEntry> laborLedgerPendingEntries);

    /**
     * Get the pending entry with the given index in the list of labor ledger pending entries
     * @param index the given index
     * @return the pending entry with the given index in the list of labor ledger pending entries
     */
    public PendingLedgerEntry getLaborLedgerPendingEntry(int index);
}
