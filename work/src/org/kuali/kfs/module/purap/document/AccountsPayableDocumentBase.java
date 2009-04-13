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
package org.kuali.kfs.module.purap.document;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.NodeDetails;
import org.kuali.kfs.module.purap.businessobject.AccountsPayableItem;
import org.kuali.kfs.module.purap.businessobject.PurApItemUseTax;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.dto.DocumentRouteLevelChangeDTO;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.Campus;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Accounts Payable Document Base
 */
public abstract class AccountsPayableDocumentBase extends PurchasingAccountsPayableDocumentBase implements AccountsPayableDocument {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsPayableDocumentBase.class);

    // SHARED FIELDS BETWEEN PAYMENT REQUEST AND CREDIT MEMO
    private Timestamp accountsPayableApprovalTimestamp;
    private String lastActionPerformedByPersonId;
    private String accountsPayableProcessorIdentifier;
    private boolean holdIndicator;
    private Timestamp extractedTimestamp;
    private Integer purchaseOrderIdentifier;
    private String processingCampusCode;
    private String noteLine1Text;
    private String noteLine2Text;
    private String noteLine3Text;
    private boolean continuationAccountIndicator;
    private boolean closePurchaseOrderIndicator;
    private boolean reopenPurchaseOrderIndicator;
    private String bankCode;
    
    private boolean unmatchedOverride; // not persisted
    
    // NOT PERSISTED IN DB
    // BELOW USED BY ROUTING
    private String chartOfAccountsCode;
    private String organizationCode;

    // NOT PERSISTED IN DB
    // BELOW USED BY GL ENTRY CREATION
    private boolean generateEncumbranceEntries;
    private String debitCreditCodeForGLEntries;
    protected PurApItemUseTax offsetUseTax;

    // REFERENCE OBJECTS
    private Campus processingCampus;
    private transient PurchaseOrderDocument purchaseOrderDocument;
    private Bank bank;
    
    /**
     * Constructs a AccountsPayableDocumentBase
     */
    public AccountsPayableDocumentBase() {
        super();
        setUnmatchedOverride(false);
    }

    public void setLineItemTotal(KualiDecimal total) {
        // do nothing, this is so that the jsp won't complain about lineItemTotal have no setter method.
    }

    public void setGrandTotal(KualiDecimal total) {
        // do nothing, this is so that the jsp won't complain about grandTotal have no setter method.
    }

    /**
     * Overriding to stop the deleting of general ledger entries.
     * 
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase#removeGeneralLedgerPendingEntries()
     */
    @Override
    protected void removeGeneralLedgerPendingEntries() {
        // do not delete entries for PREQ or CM (hjs)
    }

    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocument#requiresAccountsPayableReviewRouting()
     */
    public boolean requiresAccountsPayableReviewRouting() {
        return !approvalAtAccountsPayableReviewAllowed();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocument#approvalAtAccountsPayableReviewAllowed()
     */
    public boolean approvalAtAccountsPayableReviewAllowed() {
        return !(isAttachmentRequired() && documentHasNoImagesAttached());
    }

    /**
     * Checks whether an attachment is required
     * 
     * @return - true if attachment is required, otherwise false
     */
    protected abstract boolean isAttachmentRequired();

    /**
     * Checks all documents notes for attachments.
     * 
     * @return - true if document does not have an image attached, false otherwise
     */
    private boolean documentHasNoImagesAttached() {
        List boNotes = this.getDocumentBusinessObject().getBoNotes();
        if (ObjectUtils.isNotNull(boNotes)) {
            for (Object obj : boNotes) {
                Note note = (Note) obj;
                // may need to refresh this attachment because of a bug - see see KULPURAP-1397
                note.refreshReferenceObject("attachment");
                if (ObjectUtils.isNotNull(note.getAttachment())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#populateDocumentForRouting()
     */
    @Override
    public void populateDocumentForRouting() {
        if (ObjectUtils.isNotNull(getPurchaseOrderDocument())) {
            this.setChartOfAccountsCode(getPurchaseOrderDocument().getChartOfAccountsCode());
            this.setOrganizationCode(getPurchaseOrderDocument().getOrganizationCode());
            if (ObjectUtils.isNull(this.getPurchaseOrderDocument().getDocumentHeader().getDocumentNumber())) {
                this.getPurchaseOrderDocument().refreshReferenceObject(KFSPropertyConstants.DOCUMENT_HEADER);
            }
        }
        super.populateDocumentForRouting();
    }

    /**
     * Calls a custom prepare for save method, as the super class does GL entry creation that causes problems with AP documents.
     * 
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#prepareForSave(org.kuali.rice.kns.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {

        // copied from super because we can't call super for AP docs
        customPrepareForSave(event);

        // DO NOT CALL SUPER HERE!! Cannot call super because it will mess up the GL entry creation process (hjs)
        // super.prepareForSave(event);
    }

    /**
     * Helper method to be called from custom prepare for save and to be overriden by sub class.
     * 
     * @return - Po Document Type
     */
    public abstract String getPoDocumentTypeForAccountsPayableDocumentCancel();

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#handleRouteLevelChange(org.kuali.rice.kew.clientapp.vo.DocumentRouteLevelChangeDTO)
     */
    @Override
    public void handleRouteLevelChange(DocumentRouteLevelChangeDTO levelChangeEvent) {
        LOG.debug("handleRouteLevelChange() started");
        super.handleRouteLevelChange(levelChangeEvent);
        String newNodeName = levelChangeEvent.getNewNodeName();
        if (processNodeChange(newNodeName, levelChangeEvent.getOldNodeName())) {
            if (StringUtils.isNotBlank(newNodeName)) {
                NodeDetails nodeDetailEnum = getNodeDetailEnum(newNodeName);
                if (ObjectUtils.isNotNull(nodeDetailEnum)) {
                    String statusCode = nodeDetailEnum.getAwaitingStatusCode();
                    if (StringUtils.isNotBlank(statusCode)) {
                        SpringContext.getBean(PurapService.class).updateStatus(this, statusCode);
                        saveDocumentFromPostProcessing();
                    }
                    else {
                        LOG.debug("Document with id " + getDocumentNumber() + " will stop in route node '" + newNodeName + "' but no awaiting status found to set");
                    }
                }
            }
        }
    }

    /**
     * Hook to allow processing after a route level is passed.
     * 
     * @param newNodeName - current route level
     * @param oldNodeName - previous route level
     * @return - true if process completes to valid state
     */
    public abstract boolean processNodeChange(String newNodeName, String oldNodeName);

    /**
     * Retrieves node details object based on name.
     * 
     * @param nodeName - route level
     * @return - Information about the supplied route level
     */
    public abstract NodeDetails getNodeDetailEnum(String nodeName);

    /**
     * Hook point to allow processing after a save.
     */
    public abstract void saveDocumentFromPostProcessing();

    // GETTERS AND SETTERS
    public Integer getPurchaseOrderIdentifier() {
        return purchaseOrderIdentifier;
    }

    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
    }

    public String getAccountsPayableProcessorIdentifier() {
        return accountsPayableProcessorIdentifier;
    }

    public void setAccountsPayableProcessorIdentifier(String accountsPayableProcessorIdentifier) {
        this.accountsPayableProcessorIdentifier = accountsPayableProcessorIdentifier;
    }

    public String getLastActionPerformedByPersonId() {
        return lastActionPerformedByPersonId;
    }

    public void setLastActionPerformedByPersonId(String lastActionPerformedByPersonId) {
        this.lastActionPerformedByPersonId = lastActionPerformedByPersonId;
    }

    public String getProcessingCampusCode() {
        return processingCampusCode;
    }

    public void setProcessingCampusCode(String processingCampusCode) {
        this.processingCampusCode = processingCampusCode;
    }

    public Timestamp getAccountsPayableApprovalTimestamp() {
        return accountsPayableApprovalTimestamp;
    }

    public void setAccountsPayableApprovalTimestamp(Timestamp accountsPayableApprovalTimestamp) {
        this.accountsPayableApprovalTimestamp = accountsPayableApprovalTimestamp;
    }

    public Timestamp getExtractedTimestamp() {
        return extractedTimestamp;
    }

    public void setExtractedTimestamp(Timestamp extractedTimestamp) {
        this.extractedTimestamp = extractedTimestamp;
    }

    public boolean isHoldIndicator() {
        return holdIndicator;
    }

    public void setHoldIndicator(boolean holdIndicator) {
        this.holdIndicator = holdIndicator;
    }

    public String getNoteLine1Text() {
        return noteLine1Text;
    }

    public void setNoteLine1Text(String noteLine1Text) {
        this.noteLine1Text = noteLine1Text;
    }

    public String getNoteLine2Text() {
        return noteLine2Text;
    }

    public void setNoteLine2Text(String noteLine2Text) {
        this.noteLine2Text = noteLine2Text;
    }

    public String getNoteLine3Text() {
        return noteLine3Text;
    }

    public void setNoteLine3Text(String noteLine3Text) {
        this.noteLine3Text = noteLine3Text;
    }

    public Campus getProcessingCampus() {
        return processingCampus = (Campus) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(Campus.class).retrieveExternalizableBusinessObjectIfNecessary(this, processingCampus, "processingCampus");
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public boolean isGenerateEncumbranceEntries() {
        return generateEncumbranceEntries;
    }

    public void setGenerateEncumbranceEntries(boolean generateEncumbranceEntries) {
        this.generateEncumbranceEntries = generateEncumbranceEntries;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocument#getPurchaseOrderDocument()
     */
    public PurchaseOrderDocument getPurchaseOrderDocument() {
        if ((ObjectUtils.isNull(purchaseOrderDocument) || ObjectUtils.isNull(purchaseOrderDocument.getPurapDocumentIdentifier())) && (ObjectUtils.isNotNull(getPurchaseOrderIdentifier()))) {
            setPurchaseOrderDocument(SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(this.getPurchaseOrderIdentifier()));
        }
        return purchaseOrderDocument;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocument#setPurchaseOrderDocument(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
     */
    public void setPurchaseOrderDocument(PurchaseOrderDocument purchaseOrderDocument) {
        if (ObjectUtils.isNull(purchaseOrderDocument)) {
            // KUALI-PURAP 1185 PO Id not being set to null, instead throwing error on main screen that value is invalid.
            // setPurchaseOrderIdentifier(null);
            this.purchaseOrderDocument = null;
        }
        else {
            if (ObjectUtils.isNotNull(purchaseOrderDocument.getPurapDocumentIdentifier())) {
                setPurchaseOrderIdentifier(purchaseOrderDocument.getPurapDocumentIdentifier());
            }
            this.purchaseOrderDocument = purchaseOrderDocument;
        }
    }

    public boolean isClosePurchaseOrderIndicator() {
        return closePurchaseOrderIndicator;
    }

    public void setClosePurchaseOrderIndicator(boolean closePurchaseOrderIndicator) {
        this.closePurchaseOrderIndicator = closePurchaseOrderIndicator;
    }

    public boolean isReopenPurchaseOrderIndicator() {
        return reopenPurchaseOrderIndicator;
    }

    public void setReopenPurchaseOrderIndicator(boolean reopenPurchaseOrderIndicator) {
        this.reopenPurchaseOrderIndicator = reopenPurchaseOrderIndicator;
    }
    
    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    /**
     * Sets the processing campus.
     * @deprecated
     * @param processingCampus
     */
    public void setProcessingCampus(Campus processingCampus) {
        this.processingCampus = processingCampus;
    }

    // Helper methods
    /**
     * Retrieves the universal user object for the last person to perform an action on the document.
     */
    public Person getLastActionPerformedByUser() {
    	return SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).getPerson(getLastActionPerformedByPersonId());
    }

    /**
     * Retrieves the person name for the last person to perform an action on the document.
     * 
     * @return - the person's name who last performed an action on the document.
     */
    public String getLastActionPerformedByPersonName() {
        Person user = getLastActionPerformedByUser();
        if (ObjectUtils.isNull(user)) {
            return "";
        }
        else {
            return user.getName();
        }
    }

    public String getDebitCreditCodeForGLEntries() {
        return debitCreditCodeForGLEntries;
    }

    public void setDebitCreditCodeForGLEntries(String debitCreditCodeForGLEntries) {
        this.debitCreditCodeForGLEntries = debitCreditCodeForGLEntries;
    }

    public boolean isUnmatchedOverride() {
        return unmatchedOverride;
    }

    public void setUnmatchedOverride(boolean unmatchedOverride) {
        this.unmatchedOverride = unmatchedOverride;
    }
 
    public boolean getExtractedIndicatorForSearching() {
        return extractedTimestamp != null;
    }
    
    public boolean isHoldIndicatorForSearching() {
        return holdIndicator;
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocument#getGrandTotal()
     */
    public abstract KualiDecimal getGrandTotal();

    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocument#getInitialAmount()
     */
    public abstract KualiDecimal getInitialAmount();

    public boolean isContinuationAccountIndicator() {
        return continuationAccountIndicator;
    }

    public void setContinuationAccountIndicator(boolean continuationAccountIndicator) {
        this.continuationAccountIndicator = continuationAccountIndicator;
    }

    public boolean isExtracted() {
        return (ObjectUtils.isNotNull(getExtractedTimestamp()));
    }

    public abstract AccountsPayableDocumentSpecificService getDocumentSpecificService();

    public AccountsPayableItem getAPItemFromPOItem(PurchaseOrderItem poi) {
        for (AccountsPayableItem preqItem : (List<AccountsPayableItem>) this.getItems()) {
            if (preqItem.getItemType().isLineItemIndicator()) {
                if (preqItem.getItemLineNumber().compareTo(poi.getItemLineNumber()) == 0) {
                    return preqItem;
                }
            }
            else {
                return (AccountsPayableItem) SpringContext.getBean(PurapService.class).getBelowTheLineByType(this, poi.getItemType());
            }
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#getItemClass()
     */
    public Class getItemClass() {
        return null;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentIfPossible()
     */
    public PurchasingAccountsPayableDocument getPurApSourceDocumentIfPossible() {
        return null;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentLabelIfPossible()
     */
    public String getPurApSourceDocumentLabelIfPossible() {
        return null;
    }

    /**
     * @see org.kuali.rice.kns.bo.PersistableBusinessObjectBase#afterLookup(org.apache.ojb.broker.PersistenceBroker)
     */
    @Override
    public void afterLookup(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        super.afterLookup(persistenceBroker);
        if ((ObjectUtils.isNull(purchaseOrderDocument) || ObjectUtils.isNull(purchaseOrderDocument.getPurapDocumentIdentifier())) && (ObjectUtils.isNotNull(getPurchaseOrderIdentifier()))) {
            this.setPurchaseOrderDocument(SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(this.getPurchaseOrderIdentifier()));
            if (ObjectUtils.isNull(this.getPurchaseOrderDocument().getDocumentHeader().getDocumentNumber())) {
                this.getPurchaseOrderDocument().refreshReferenceObject(KFSPropertyConstants.DOCUMENT_HEADER);
            }
        }
    }

    public void updateExtendedPriceOnItems() {
        for (AccountsPayableItem item : (List<AccountsPayableItem>) getItems()) {
            item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);
    
            final KualiDecimal itemExtendedPrice = (item.getExtendedPrice()==null)?KualiDecimal.ZERO:item.getExtendedPrice();;
            if ( (KualiDecimal.ZERO.compareTo(itemExtendedPrice)==0) && item.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                KualiDecimal newExtendedPrice = item.calculateExtendedPrice();
                item.setExtendedPrice(newExtendedPrice);
            }
        }
    }

    /**
     * 
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocument#getTotalRemitAmount()
     */
    public KualiDecimal getTotalRemitTax() {
        if(!this.isUseTaxIndicator()) {
            return (KualiDecimal.ZERO.equals(this.getTotalTaxAmount()))?null:this.getTotalTaxAmount();
        }
        return null;
    }

    @Override
    public boolean customizeOffsetGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        boolean value = super.customizeOffsetGeneralLedgerPendingEntry(accountingLine, explicitEntry, offsetEntry);
        if(offsetEntry != null && this.offsetUseTax != null) {
            offsetEntry.setChartOfAccountsCode(this.offsetUseTax.getChartOfAccountsCode());
            offsetEntry.refreshReferenceObject(KFSPropertyConstants.CHART);
            offsetEntry.setAccountNumber(this.offsetUseTax.getAccountNumber());
            offsetEntry.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
            offsetEntry.setFinancialObjectCode(this.offsetUseTax.getFinancialObjectCode());
            offsetEntry.refreshReferenceObject(KFSPropertyConstants.FINANCIAL_OBJECT);
        } else {
            value=false;
        }
        return value;
    }

    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, PurApItemUseTax offsetUseTax) {
        this.offsetUseTax = offsetUseTax; 
        boolean value = this.generateGeneralLedgerPendingEntries(glpeSourceDetail, sequenceHelper);
        this.offsetUseTax = null;
        return value;
    }
    
    public String getHoldIndicatorForResult(){
        return isHoldIndicator() ? "Yes" : "No";
    }
    
}

