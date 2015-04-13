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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument;

/**
 * Defines methods that must be implemented by classes providing a LaborLedgerPendingEntryServiceImpl.
 */
public interface LaborLedgerPendingEntryService {

    /**
     * Does the given account have any labor ledger entries? It is necessary to check this before closing an account.
     * 
     * @param account
     * @return
     */
    public boolean hasPendingLaborLedgerEntry(String chartOfAccountsCode, String accountNumber);

    /**
     * determine if there is any pending entry that has not been processed for the given criteria
     * 
     * @param fieldValues the given search criteria
     * @return true if there is one or more pending entries that have not been processed for the given criteria; otherwise, false
     */
    public boolean hasPendingLaborLedgerEntry(Map fieldValues);

    /**
     * This method generates labor ledger pending entries.
     * 
     * @param document
     * @return
     */
    public boolean generateLaborLedgerPendingEntries(LaborLedgerPostingDocument document);

    /**
     * Get all entries that have been approved but still in pending entry queue
     * 
     * @return all approved pending entries
     */
    public Iterator<LaborLedgerPendingEntry> findApprovedPendingLedgerEntries();

    /**
     * Delete the pending entries with the given financial document approved code
     * 
     * @param approvedCode
     */
    public void deleteByFinancialDocumentApprovedCode(String financialDocumentApprovedCode);

    /**
     * This method checks for pending ledger entries that match the current balance inquiry
     * 
     * @param emplid
     * @return
     */
    public Iterator findPendingLedgerEntriesForLedgerBalance(Map fieldValues, boolean isApproved);

    /**
     * Use fieldValues to create a query for matching records of <code>{@link LaborLedgerPendingEntry}</code> instances
     * 
     * @param fieldValues properties to match against
     * @param isApproved Retrieve approved or unapproved entries?
     */
    public Collection findPendingEntries(Map fieldValues, boolean isApproved);

    /**
     * delete pending entries with the given document header id
     * 
     * @param documentHeaderId
     */
    public void delete(String documentHeaderId);
}
