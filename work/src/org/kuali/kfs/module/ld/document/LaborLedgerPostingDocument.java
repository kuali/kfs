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
