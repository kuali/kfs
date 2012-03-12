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
package org.kuali.kfs.module.ld.document;

import java.util.List;

import org.kuali.kfs.integration.ld.LaborLedgerPostingDocumentForSearching;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.document.AccountingDocument;

/**
 * Labor Document Defines methods that must be implements for a labor ledger posting document.
 */
public interface LaborLedgerPostingDocument extends AccountingDocument, LaborLedgerPostingDocumentForSearching {

    
    /**
     * Retrieves the list of Labor Ledger Pending Entries for the document. 
     * 
     * @return A list of labor ledger pending entries.
     */
    public List getLaborLedgerPendingEntriesForSearching();

    
    /**
     * Retrieves the list of Labor Ledger Pending Entries for the document. 
     * 
     * @return A list of labor ledger pending entries.
     */
    public List<LaborLedgerPendingEntry> getLaborLedgerPendingEntries();

    /**
     * Sets the list of labor ledger pending entries for the document.
     * 
     * @param laborLedgerPendingEntries the given labor ledger pending entries
     */
    public void setLaborLedgerPendingEntries(List<LaborLedgerPendingEntry> laborLedgerPendingEntries);

    /**
     * Get the pending entry with the given index in the list of labor ledger pending entries
     * 
     * @param index the given index
     * @return the pending entry with the given index in the list of labor ledger pending entries
     */
    public LaborLedgerPendingEntry getLaborLedgerPendingEntry(int index);
    
    /**
     * creating a list of Expense Pending entries and Benefit pending Entries
     * 
     * @param accountingLine the accounting line being used to generate pending entries
     * @param sequenceHelper the sequence number generator
     * @return true after creating a list of Expense Pending entries and Benefit pending Entries
     */
    public boolean generateLaborLedgerPendingEntries(AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper);

    /**
     * creating a list of benefit clearing Pending entries
     * 
     * @param sequenceHelper the sequence number generator
     * @return true after creating a list of benefit clearing Pending entries
     */
    public boolean generateLaborLedgerBenefitClearingPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper);
}
