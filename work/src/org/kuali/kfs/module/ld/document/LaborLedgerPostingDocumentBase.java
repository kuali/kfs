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

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocumentBase;
import org.kuali.module.labor.bo.LaborLedgerPendingEntry;
import org.kuali.module.labor.service.LaborLedgerPendingEntryService;

/**
 * Labor Document base class implementation for all labor eDocs that are transactional, meaning implementing
 * TransactionalDocumentBase. Additional functionality for labor is provided by this class, suchc as retrieving labor ledger pending
 * entries.
 */
public abstract class LaborLedgerPostingDocumentBase extends AccountingDocumentBase implements LaborLedgerPostingDocument {
    protected List<LaborLedgerPendingEntry> laborLedgerPendingEntries;
    
    private final static String LABOR_LEDGER_GENERAL_LEDGER_POSTING_HELPER_BEAN_ID = "kfsDoNothingGeneralLedgerPostingHelper";

    /**
     * Initializes the pending entries.
     */
    public LaborLedgerPostingDocumentBase() {
        super();
        setLaborLedgerPendingEntries(new ArrayList<LaborLedgerPendingEntry>());
    }

    /**
     * @see org.kuali.module.labor.document.LaborLedgerPostingDocument#getLaborLedgerPendingEntries()
     */
    public List<LaborLedgerPendingEntry> getLaborLedgerPendingEntries() {
        return this.laborLedgerPendingEntries;
    }

    /**
     * @see org.kuali.module.labor.document.LaborLedgerPostingDocument#setLaborLedgerPendingEntries(java.util.List)
     */
    public void setLaborLedgerPendingEntries(List<LaborLedgerPendingEntry> laborLedgerPendingEntries) {
        this.laborLedgerPendingEntries = laborLedgerPendingEntries;
    }

    /**
     * Override to call super and then iterate over all GLPEs and update the approved code appropriately.
     * 
     * @see Document#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();
        if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            changeLedgerPendingEntriesApprovedStatusCode();
        }
        else if (getDocumentHeader().getWorkflowDocument().stateIsCanceled() || getDocumentHeader().getWorkflowDocument().stateIsDisapproved()) {
            removeLedgerPendingEntries();
        }
    }

    /**
     * This method iterates over all of the pending entries for a document and sets their approved status code to APPROVED "A".
     */
    private void changeLedgerPendingEntriesApprovedStatusCode() {
        for (LaborLedgerPendingEntry pendingEntry : laborLedgerPendingEntries) {
            pendingEntry.setFinancialDocumentApprovedCode(KFSConstants.DocumentStatusCodes.APPROVED);
        }
    }

    /**
     * This method calls the service to remove all of the pending entries associated with this document
     */
    private void removeLedgerPendingEntries() {
        LaborLedgerPendingEntryService laborLedgerPendingEntryService = SpringContext.getBean(LaborLedgerPendingEntryService.class);
        laborLedgerPendingEntryService.delete(getDocumentHeader().getDocumentNumber());
    }

    /**
     * This implementation is coupled tightly with some underlying issues that the Struts PojoProcessor plugin has with how objects
     * get instantiated within lists. The first three lines are required otherwise when the PojoProcessor tries to automatically
     * inject values into the list, it will get an index out of bounds error if the instance at an index is being called and prior
     * instances at indices before that one are not being instantiated. So changing the code below will cause things to break.
     * 
     * @param index of Labor Ledger Pending Entry to retrieve
     * @return LaborLedgerPendingEntry
     */
    public LaborLedgerPendingEntry getLaborLedgerPendingEntry(int index) {
        while (laborLedgerPendingEntries.size() <= index) {
            laborLedgerPendingEntries.add(new LaborLedgerPendingEntry());
        }
        return laborLedgerPendingEntries.get(index);
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#prepareForSave(org.kuali.core.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        if (!SpringContext.getBean(LaborLedgerPendingEntryService.class).generateLaborLedgerPendingEntries(this)) {
            logErrors();
            throw new ValidationException("labor ledger LLPE generation failed");
        }
        super.prepareForSave(event);
    }

    /**
     * This is a "do nothing" version of the method - it just won't create GLPEs
     * @see org.kuali.kfs.document.AccountingDocumentBase#generateGeneralLedgerPendingEntries(org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail, org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        return true;
    }

    /**
     * Labor docs never generate general ledger pending entries, and therefore, this method is never called, but we always return true, since
     * we're required to have a concrete representation
     * @see org.kuali.kfs.document.AccountingDocumentBase#isDebit(org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        return true;
    }
    
}
