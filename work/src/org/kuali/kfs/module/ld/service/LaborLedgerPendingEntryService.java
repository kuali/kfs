/*
 * Copyright 2006 The Kuali Foundation
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
