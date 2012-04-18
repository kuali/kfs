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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.JournalVoucherDocument;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborConstants.JournalVoucherOffsetType;
import org.kuali.kfs.module.ld.businessobject.LaborJournalVoucherAccountingLineParser;
import org.kuali.kfs.module.ld.businessobject.LaborJournalVoucherDetail;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.service.LaborLedgerPendingEntryService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineParser;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.HomeOriginationService;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;

// @latex.ClassSignatureStart
/**
 * Labor Document class for the Labor Ledger Journal Voucher.
 */
public class LaborJournalVoucherDocument extends JournalVoucherDocument implements LaborLedgerPostingDocument, AmountTotaling {
    // @latex.ClassSignatureStop
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborJournalVoucherDocument.class);
    protected String offsetTypeCode = JournalVoucherOffsetType.NO_OFFSET.typeCode;
    protected List<LaborLedgerPendingEntry> laborLedgerPendingEntries;
    protected DocumentTypeEBO financialSystemDocumentTypeCode;

    /**
     * Constructs a LaborJournalVoucherDocument.java.
     */
    public LaborJournalVoucherDocument() {
        super();
        setLaborLedgerPendingEntries(new ArrayList<LaborLedgerPendingEntry>());
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getSourceAccountingLineClass()
     */
    @Override
    public Class getSourceAccountingLineClass() {
        return LaborJournalVoucherDetail.class;
    }

    /**
     * @see org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument#getLaborLedgerPendingEntry(int)
     */
    @Override
    public LaborLedgerPendingEntry getLaborLedgerPendingEntry(int index) {
        while (laborLedgerPendingEntries.size() <= index) {
            laborLedgerPendingEntries.add(new LaborLedgerPendingEntry());
        }
        return laborLedgerPendingEntries.get(index);
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
    @Override
    public List<LaborLedgerPendingEntry> getLaborLedgerPendingEntries() {
        return laborLedgerPendingEntries;
    }

    /**
     * Sets the laborLedgerPendingEntries attribute value.
     *
     * @param laborLedgerPendingEntries The laborLedgerPendingEntries to set.
     */
    @Override
    public void setLaborLedgerPendingEntries(List<LaborLedgerPendingEntry> laborLedgerPendingEntries) {
        this.laborLedgerPendingEntries = laborLedgerPendingEntries;
    }

    /**
     * Gets the financialSystemDocumentTypeCode attribute.
     *
     * @return Returns the financialSystemDocumentTypeCode.
     */
    public DocumentTypeEBO getFinancialSystemDocumentTypeCode() {
        if ( financialSystemDocumentTypeCode == null || !StringUtils.equals(financialSystemDocumentTypeCode.getName(), documentHeader.getWorkflowDocument().getDocumentTypeName() ) ) {
            financialSystemDocumentTypeCode = null;
            if ( StringUtils.isNotBlank(documentHeader.getWorkflowDocument().getDocumentTypeName()) ) {
                DocumentType docType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(documentHeader.getWorkflowDocument().getDocumentTypeName());
                if ( docType != null ) {
                    financialSystemDocumentTypeCode = org.kuali.rice.kew.doctype.bo.DocumentType.from(docType);
                }
            }
        }
        return financialSystemDocumentTypeCode;
    }

    /**
     * Used to get the appropriate <code>{@link AccountingLineParser}</code> for the <code>Document</code>
     *
     * @return AccountingLineParser
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new LaborJournalVoucherAccountingLineParser(getBalanceTypeCode());
    }

    /**
     * Override to call super and then iterate over all GLPEs and update the approved code appropriately.
     *
     * @see Document#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        if (getDocumentHeader().getWorkflowDocument().isProcessed()) {
            changeLedgerPendingEntriesApprovedStatusCode();
        }
        else if (getDocumentHeader().getWorkflowDocument().isCanceled() || getDocumentHeader().getWorkflowDocument().isDisapproved()) {
            removeLedgerPendingEntries();
        }
    }

    /**
     * This method iterates over all of the pending entries for a document and sets their approved status code to APPROVED "A".
     */
    protected void changeLedgerPendingEntriesApprovedStatusCode() {
        for (LaborLedgerPendingEntry pendingEntry : laborLedgerPendingEntries) {
            pendingEntry.setFinancialDocumentApprovedCode(KFSConstants.DocumentStatusCodes.APPROVED);
        }
    }

    /**
     * This method calls the service to remove all of the pending entries associated with this document
     */
    protected void removeLedgerPendingEntries() {
        LaborLedgerPendingEntryService laborLedgerPendingEntryService = SpringContext.getBean(LaborLedgerPendingEntryService.class);
        laborLedgerPendingEntryService.delete(getDocumentHeader().getDocumentNumber());
    }

    /**
     * @see org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument#generateLaborLedgerBenefitClearingPendingEntries(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateLaborLedgerBenefitClearingPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        return true;
    }

    /**
     * @see org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument#generateLaborLedgerPendingEntries(org.kuali.kfs.sys.businessobject.AccountingLine,
     *      org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateLaborLedgerPendingEntries(AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LOG.debug("processGenerateLaborLedgerPendingEntries() started");

        try {
            LaborLedgerPendingEntry pendingLedgerEntry = new LaborLedgerPendingEntry();

            // populate the explicit entry
            ObjectUtil.buildObject(pendingLedgerEntry, accountingLine);

            GeneralLedgerPendingEntryService pendingEntryService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
            pendingEntryService.populateExplicitGeneralLedgerPendingEntry(this, accountingLine, sequenceHelper, pendingLedgerEntry);

            // apply the labor JV specific information
            this.customizeExplicitGeneralLedgerPendingEntry(accountingLine, pendingLedgerEntry);
            pendingLedgerEntry.setFinancialDocumentTypeCode(this.getOffsetTypeCode());

            if (StringUtils.isBlank(((LaborJournalVoucherDetail) accountingLine).getEmplid())) {
                pendingLedgerEntry.setEmplid(LaborConstants.getDashEmplId());
            }

            if (StringUtils.isBlank(((LaborJournalVoucherDetail) accountingLine).getPositionNumber())) {
                pendingLedgerEntry.setPositionNumber(LaborConstants.getDashPositionNumber());
            }

            String originationCode = SpringContext.getBean(HomeOriginationService.class).getHomeOrigination().getFinSystemHomeOriginationCode();
            pendingLedgerEntry.setFinancialSystemOriginationCode(originationCode);

            pendingLedgerEntry.setTransactionLedgerEntrySequenceNumber(sequenceHelper.getSequenceCounter());

            pendingLedgerEntry.refreshReferenceObject("financialObject");
            
            this.getLaborLedgerPendingEntries().add(pendingLedgerEntry);

            sequenceHelper.increment();
        }
        catch (Exception e) {
            LOG.error("Cannot add a Labor Ledger Pending Entry into the list");
            return false;
        }

        return true;
    }

    /**
     * If the document has a total amount, call method on document to get the total and set in doc header.
     *
     * @see org.kuali.rice.krad.document.Document#prepareForSave()
     */
    @Override
    public void prepareForSave() {
        if (!SpringContext.getBean(LaborLedgerPendingEntryService.class).generateLaborLedgerPendingEntries(this)) {
            logErrors();
            throw new ValidationException("labor ledger LLPE generation failed");
        }

        super.prepareForSave();
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#prepareForSave(org.kuali.rice.krad.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        this.prepareForSave();
        super.prepareForSave(event);
    }

    /**
     * This is a "do nothing" version of the method - it just won't create GLPEs
     *
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#generateGeneralLedgerPendingEntries(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail,
     *      org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        return true;
    }

    /**
     * Labor docs never generate general ledger pending entries, and therefore, this method is never called, but we always return
     * true, since we're required to have a concrete representation
     *
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#isDebit(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        return true;
    }

    @Override
    public List getLaborLedgerPendingEntriesForSearching() {
        return getLaborLedgerPendingEntries();
    }
}
