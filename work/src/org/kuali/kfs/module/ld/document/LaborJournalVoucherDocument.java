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

import org.kuali.core.bo.DocumentType;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.kfs.bo.AccountingLineParser;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.financial.document.JournalVoucherDocument;
import org.kuali.module.labor.LaborConstants.JournalVoucherOffsetType;
import org.kuali.module.labor.bo.ExpenseTransferSourceAccountingLine;
import org.kuali.module.labor.bo.LaborJournalVoucherDetail;
import org.kuali.module.labor.bo.LaborLedgerAccountingLineParser;
import org.kuali.module.labor.bo.PendingLedgerEntry;
import org.kuali.module.labor.bo.TestSourceAccountingLine;

public class LaborJournalVoucherDocument extends JournalVoucherDocument implements LaborLedgerPostingDocument{
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborJournalVoucherDocument.class);

    private String offsetTypeCode = JournalVoucherOffsetType.NO_OFFSET.typeCode;
    private List<PendingLedgerEntry> laborLedgerPendingEntries;   
    private DocumentType documentType;
       
    /**
     * Constructs a LaborJournalVoucherDocument.java.
     */
    public LaborJournalVoucherDocument() {
        super();
        setLaborLedgerPendingEntries(new ArrayList<PendingLedgerEntry>());
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getSourceAccountingLineClass()
     */
    @Override
    public Class getSourceAccountingLineClass() { 
        return LaborJournalVoucherDetail.class;
    }

    /**
     * @see org.kuali.module.labor.document.LaborLedgerPostingDocument#getLaborLedgerPendingEntry(int)
     */
    public PendingLedgerEntry getLaborLedgerPendingEntry(int index) {
        while (laborLedgerPendingEntries.size() <= index) {
            laborLedgerPendingEntries.add(new PendingLedgerEntry());
        }
        return laborLedgerPendingEntries.get(index);
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#prepareForSave(org.kuali.core.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        super.prepareForSave(event);
        if (!SpringServiceLocator.getLaborLedgerPendingEntryService().generateLaborLedgerPendingEntries(this)) {
            logErrors();
            throw new ValidationException("labor ledger LLPE generation failed");
        }
    }
    
    /**
     * Gets the offsetTypeCode attribute.
     * 
     * @return Returns the offsetTypeCode.
     */
    public String getOffsetTypeCode() {
        return offsetTypeCode;
    }

    /**
     * Sets the offsetTypeCode attribute value.
     * 
     * @param offsetTypeCode The offsetTypeCode to set.
     */
    public void setOffsetTypeCode(String offsetTypeCode) {
        this.offsetTypeCode = offsetTypeCode;
    }

    /**
     * Gets the laborLedgerPendingEntries attribute.
     * 
     * @return Returns the laborLedgerPendingEntries.
     */
    public List<PendingLedgerEntry> getLaborLedgerPendingEntries() {
        return laborLedgerPendingEntries;
    }

    /**
     * Sets the laborLedgerPendingEntries attribute value.
     * 
     * @param laborLedgerPendingEntries The laborLedgerPendingEntries to set.
     */
    public void setLaborLedgerPendingEntries(List<PendingLedgerEntry> laborLedgerPendingEntries) {
        this.laborLedgerPendingEntries = laborLedgerPendingEntries;
    }

    /**
     * Gets the documentType attribute. 
     * @return Returns the documentType.
     */
    public DocumentType getDocumentType() {
        return documentType;
    }

    /**
     * Sets the documentType attribute value.
     * @param documentType The documentType to set.
     */
    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }
}