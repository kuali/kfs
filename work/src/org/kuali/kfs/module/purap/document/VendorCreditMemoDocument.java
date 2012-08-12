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

package org.kuali.kfs.module.purap.document;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapWorkflowConstants;
import org.kuali.kfs.module.purap.PurapConstants.CREDIT_MEMO_TYPE_LABELS;
import org.kuali.kfs.module.purap.PurapConstants.CreditMemoStatuses;
import org.kuali.kfs.module.purap.PurapConstants.PurapDocTypeCodes;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItemUseTax;
import org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService;
import org.kuali.kfs.module.purap.document.service.AccountsPayableService;
import org.kuali.kfs.module.purap.document.service.CreditMemoService;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.validation.event.AttributedContinuePurapEvent;
import org.kuali.kfs.module.purap.service.PurapGeneralLedgerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

/**
 * Credit Memo Document Business Object. Contains the fields associated with the main document table.
 */
public class VendorCreditMemoDocument extends AccountsPayableDocumentBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VendorCreditMemoDocument.class);

    protected Integer paymentRequestIdentifier;
    protected String creditMemoNumber;
    protected Date creditMemoDate;
    protected KualiDecimal creditMemoAmount;
    protected Timestamp creditMemoPaidTimestamp;
    protected String itemMiscellaneousCreditDescription;
    protected Date purchaseOrderEndDate;
    protected String vendorAttentionName;
    
    protected PaymentRequestDocument paymentRequestDocument;

    /**
     * Default constructor.
     */
    public VendorCreditMemoDocument() {
        super();
    }

    public boolean isSourceDocumentPaymentRequest() {
        return getPaymentRequestIdentifier() != null;
    }

    public boolean isSourceDocumentPurchaseOrder() {
        return (!isSourceDocumentPaymentRequest()) && (getPurchaseOrderIdentifier() != null);
    }

    public boolean isSourceVendor() {
        return (!isSourceDocumentPaymentRequest()) && (!isSourceDocumentPurchaseOrder());
    }

    /**
     * Overrides the method in PurchasingAccountsPayableDocumentBase to add the criteria
     * specific to Credit Memo Document.
     * 
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#isInquiryRendered()
     */
    @Override
    public boolean isInquiryRendered() {
        if ( isPostingYearPrior() && 
             ( getApplicationDocumentStatus().equals(PurapConstants.CreditMemoStatuses.APPDOC_COMPLETE) ||
               getApplicationDocumentStatus().equals(PurapConstants.PaymentRequestStatuses.APPDOC_CANCELLED_POST_AP_APPROVE) ||
               getApplicationDocumentStatus().equals(PurapConstants.PaymentRequestStatuses.APPDOC_CANCELLED_IN_PROCESS) ) )  {
               return false;            
        }
        else {
            return true;
        }
    }
    
    /**
     * Initializes the values for a new document.
     */
    public void initiateDocument() {
        LOG.debug("initiateDocument() started");
        try {
            updateAndSaveAppDocStatus(PurapConstants.CreditMemoStatuses.APPDOC_INITIATE);
        }
        catch (WorkflowException e) {
            logAndThrowRuntimeException("Error saving routing data while saving document with id " + getDocumentNumber(), e);
        }
 
        Person currentUser = (Person) GlobalVariables.getUserSession().getPerson();
        setAccountsPayableProcessorIdentifier(currentUser.getPrincipalId());
        setProcessingCampusCode(currentUser.getCampusCode());
    }

    /**
     * Clear out the initially populated fields.
     */
    public void clearInitFields() {
        LOG.debug("clearDocument() started");

        // Clearing document overview fields
        getDocumentHeader().setDocumentDescription(null);
        getDocumentHeader().setExplanation(null);
        getFinancialSystemDocumentHeader().setFinancialDocumentTotalAmount(null);
        getDocumentHeader().setOrganizationDocumentNumber(null);

        // Clearing document Init fields
        setPurchaseOrderIdentifier(null);
        setCreditMemoNumber(null);
        setCreditMemoDate(null);
        setCreditMemoAmount(null);
        setVendorNumber(null);
        setPaymentRequestIdentifier(null);
    }

    /**
     * Returns the type of the Credit Memo that was selected on the init screen. It is based on them entering the Vendor, PO or PREQ #.
     * 
     * @return Vendor, PO or PREQ
     */
    public String getCreditMemoType() {
        String type = CREDIT_MEMO_TYPE_LABELS.TYPE_VENDOR;
        if (isSourceDocumentPaymentRequest()) {
            type = CREDIT_MEMO_TYPE_LABELS.TYPE_PREQ;
        }
        else if (isSourceDocumentPurchaseOrder()) {
            type = CREDIT_MEMO_TYPE_LABELS.TYPE_PO;
        }
        return type;
    }

    public boolean isBoNotesSupport() {
        return true;
    }

    /**
     * Determines if the purchase order has notes, using the note service.
     * 
     * @return - true if po has notes, false if po does not have notes
     */
    public boolean getPurchaseOrderNotes() {
        boolean hasNotes = false;

        if (this.getNotes().size() > 0) {
            hasNotes = true;
        }

        return hasNotes;
    }

    /**
     * Determines the indicator text that will appear in the workflow document title
     * 
     * @return - Text of hold
     */
    protected String getTitleIndicator() {
        if (isHoldIndicator()) {
            return PurapConstants.PaymentRequestIndicatorText.HOLD;
        }
        else return "";
    }
    
    /**
     * @see org.kuali.rice.krad.document.DocumentBase#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        LOG.debug("doRouteStatusChange() started");
        super.doRouteStatusChange(statusChangeEvent);
        
        try {
            // DOCUMENT PROCESSED
            if (this.getFinancialSystemDocumentHeader().getWorkflowDocument().isProcessed()) {
                updateAndSaveAppDocStatus(PurapConstants.CreditMemoStatuses.APPDOC_COMPLETE);

                return;
            }
            // DOCUMENT DISAPPROVED
            else if (this.getFinancialSystemDocumentHeader().getWorkflowDocument().isDisapproved()) {
                String nodeName = SpringContext.getBean(WorkflowDocumentService.class).getCurrentRouteLevelName(this.getFinancialSystemDocumentHeader().getWorkflowDocument());
                
                String disapprovalStatus = CreditMemoStatuses.getCreditMemoAppDocDisapproveStatuses().get(nodeName);
                                
                if (((StringUtils.isBlank(disapprovalStatus)) && ((CreditMemoStatuses.APPDOC_INITIATE.equals(getApplicationDocumentStatus())) || (CreditMemoStatuses.APPDOC_IN_PROCESS.equals(getApplicationDocumentStatus()))))) {
                    disapprovalStatus = CreditMemoStatuses.APPDOC_CANCELLED_IN_PROCESS;
                    updateAndSaveAppDocStatus(disapprovalStatus);                    
                }
                if (StringUtils.isNotBlank(disapprovalStatus)) {
                    SpringContext.getBean(AccountsPayableService.class).cancelAccountsPayableDocument(this, nodeName);
                }else{                
                    logAndThrowRuntimeException("No status found to set for document being disapproved in node '" + nodeName + "'");
                }
            }
            // DOCUMENT CANCELED
            else if (this.getFinancialSystemDocumentHeader().getWorkflowDocument().isCanceled()) {
                String currentNodeName = this.getFinancialSystemDocumentHeader().getWorkflowDocument().getCurrentNodeNames().iterator().next();
                SpringContext.getBean(AccountsPayableService.class).cancelAccountsPayableDocument(this, currentNodeName);
            }
        }
        catch (Exception e) {
            logAndThrowRuntimeException("Error saving routing data while saving document with id " + getDocumentNumber(), e);
        }
    }

    /**
     * Hook point for performing actions that occur after a route level change, in this case; Performs logic necessary after full
     * entry has been completed when past Adhoc Review, or sets the AP approval date when past AP review.
     * 
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocumentBase#preProcessNodeChange(java.lang.String, java.lang.String)
     */
    public boolean processNodeChange(String newNodeName, String oldNodeName) {
        if (CreditMemoStatuses.NODE_ADHOC_REVIEW.equals(oldNodeName)) {
            SpringContext.getBean(AccountsPayableService.class).performLogicForFullEntryCompleted(this);
        }

        // if we've hit Account node then reopen po
        else if (CreditMemoStatuses.NODE_ACCOUNT_REVIEW.equals(newNodeName) && this.isReopenPurchaseOrderIndicator()) {
            SpringContext.getBean(PurapService.class).performLogicForCloseReopenPO(this);
        }        
        return true;
    }

    /**
     * @see org.kuali.rice.krad.document.DocumentBase#getDocumentTitle()
     */
    @Override
    public String getDocumentTitle() {
        if (SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(VendorCreditMemoDocument.class, PurapParameterConstants.PURAP_OVERRIDE_CM_DOC_TITLE)) {
            return getCustomDocumentTitle();
        }
        return super.getDocumentTitle();
    }

    /**
     * Returns a custom document title based on the workflow document title. 
     * Depending on the document status, the PO, vendor, amount, etc may be added to the documents title.
     * 
     * @return - Customized document title text dependent upon route level.
     */
    protected String getCustomDocumentTitle() {
        String popreq = "";
        if (this.isSourceDocumentPurchaseOrder() || this.isSourceDocumentPaymentRequest()) {
            String poNumber = getPurchaseOrderIdentifier().toString();
            popreq = new StringBuffer("PO: ").append(poNumber).toString();
        }

        String vendorName = StringUtils.trimToEmpty(getVendorName());
        String cmAmount = getGrandTotal().toString();
        String indicator = getTitleIndicator();
        String documentTitle = new StringBuffer(popreq).append(" Vendor: ").append(vendorName).append(" Amount: ").append(cmAmount).append(" ").append(indicator).toString();
        return documentTitle;
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocumentBase#saveDocumentFromPostProcessing()
     */
    public void saveDocumentFromPostProcessing() {
        SpringContext.getBean(PurapService.class).saveDocumentNoValidation(this);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#getItemClass()
     */
    @Override
    public Class<CreditMemoItem> getItemClass() {
        return CreditMemoItem.class;
    }

    @Override
    public Class getItemUseTaxClass() {
        return CreditMemoItemUseTax.class;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentIfPossible()
     */
    @Override
    public PurchasingAccountsPayableDocument getPurApSourceDocumentIfPossible() {
        PurchasingAccountsPayableDocument sourceDocument = null;
        if (isSourceDocumentPaymentRequest()) {
            sourceDocument = getPaymentRequestDocument();
        }
        else if (isSourceDocumentPurchaseOrder()) {
            sourceDocument = getPurchaseOrderDocument();
        }
        return sourceDocument;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentLabelIfPossible()
     */
    @Override
    public String getPurApSourceDocumentLabelIfPossible() {
        PurchasingAccountsPayableDocument document = getPurApSourceDocumentIfPossible();
        if (ObjectUtils.isNotNull(document)) {
            return SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByClass(document.getClass());
        }
        return null;
    }

    /**
     * Calculates the pretax total of the above the line items
     * 
     * @return KualiDecimal - above the line item pretax total
     */
    public KualiDecimal getLineItemPreTaxTotal() {
        KualiDecimal lineItemPreTaxTotal = KualiDecimal.ZERO;

        for (CreditMemoItem item : (List<CreditMemoItem>) getItems()) {
            item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);
            
            if (item.getItemType().isLineItemIndicator() && item.getExtendedPrice() != null) {
                lineItemPreTaxTotal = lineItemPreTaxTotal.add(item.getExtendedPrice());
            }
        }

        return lineItemPreTaxTotal;
    }

    /**
     * Calculates the total of the above the line items
     * 
     * @return KualiDecimal - above the line item total
     */
    public KualiDecimal getLineItemTotal() {
        KualiDecimal lineItemTotal = KualiDecimal.ZERO;

        for (CreditMemoItem item : (List<CreditMemoItem>) getItems()) {
            item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);
            
            if (item.getItemType().isLineItemIndicator() && item.getTotalAmount() != null) {
                lineItemTotal = lineItemTotal.add(item.getTotalAmount());
            }
        }

        return lineItemTotal;
    }

    /**
     * Calculates the credit memo total: Sum of above the line - restocking fees + misc amount
     * 
     * @return KualiDecimal - credit memo document total
     */
    public KualiDecimal getGrandTotal() {
        KualiDecimal grandTotal = KualiDecimal.ZERO;

        for (CreditMemoItem item : (List<CreditMemoItem>) getItems()) {
            item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);

            if (item.getTotalAmount() != null) {
                // make sure restocking fee is negative
                if (StringUtils.equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_RESTCK_FEE_CODE, item.getItemTypeCode())) {
                    if( ObjectUtils.isNotNull(item.getExtendedPrice()) ){
                        item.setExtendedPrice(item.getExtendedPrice().abs().negated());
                    }else{
                        item.setExtendedPrice(KualiDecimal.ZERO);
                    }
                }
                grandTotal = grandTotal.add(item.getTotalAmount());
            }
        }

        return grandTotal;
    }

    /**
     * Calculates the credit memo pretax total: Sum of above the line - restocking fees + misc amount
     * 
     * @return KualiDecimal - credit memo document total
     */
    public KualiDecimal getGrandPreTaxTotal() {
        KualiDecimal grandTotal = KualiDecimal.ZERO;

        for (CreditMemoItem item : (List<CreditMemoItem>) getItems()) {
            item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);

            if (item.getExtendedPrice() != null) {
                // make sure restocking fee is negative
                if (StringUtils.equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_RESTCK_FEE_CODE, item.getItemTypeCode())) {
                    item.setExtendedPrice(item.getExtendedPrice().abs().negated());
                }
                grandTotal = grandTotal.add(item.getExtendedPrice());
            }
        }

        return grandTotal;
    }

    /**
     * Calculates the credit memo tax amount: Sum of above the line - 
     * 
     * @return KualiDecimal - credit memo document total
     */
    public KualiDecimal getGrandTaxAmount() {
        KualiDecimal grandTotal = KualiDecimal.ZERO;

        for (CreditMemoItem item : (List<CreditMemoItem>) getItems()) {
            item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);

            if (item.getItemTaxAmount() != null) {
                // make sure restocking fee is negative
                if (StringUtils.equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_RESTCK_FEE_CODE, item.getItemTypeCode())) {
                    item.setExtendedPrice(item.getItemTaxAmount().abs().negated());
                }
                grandTotal = grandTotal.add(item.getItemTaxAmount());
            }
        }

        return grandTotal;
    }

    public KualiDecimal getGrandPreTaxTotalExcludingRestockingFee() {
        String[] restockingFeeCode = new String[] { PurapConstants.ItemTypeCodes.ITEM_TYPE_RESTCK_FEE_CODE };
        return this.getTotalPreTaxDollarAmountWithExclusions(restockingFeeCode, true);
    }

    public KualiDecimal getGrandTotalExcludingRestockingFee() {
        String[] restockingFeeCode = new String[] { PurapConstants.ItemTypeCodes.ITEM_TYPE_RESTCK_FEE_CODE };
        return this.getTotalDollarAmountWithExclusions(restockingFeeCode, true);
    }

    public Integer getPaymentRequestIdentifier() {
        return paymentRequestIdentifier;
    }

    public void setPaymentRequestIdentifier(Integer paymentRequestIdentifier) {
        this.paymentRequestIdentifier = paymentRequestIdentifier;
    }

    public String getCreditMemoNumber() {
        return creditMemoNumber;
    }

    public void setCreditMemoNumber(String creditMemoNumber) {
        if (creditMemoNumber != null) {
            creditMemoNumber = creditMemoNumber.toUpperCase();
        }

        this.creditMemoNumber = creditMemoNumber;
    }

    public Date getCreditMemoDate() {
        return creditMemoDate;
    }

    public void setCreditMemoDate(Date creditMemoDate) {
        this.creditMemoDate = creditMemoDate;
    }

    public KualiDecimal getCreditMemoAmount() {
        return creditMemoAmount;
    }

    public void setCreditMemoAmount(KualiDecimal creditMemoAmount) {
        this.creditMemoAmount = creditMemoAmount;
    }

    public String getItemMiscellaneousCreditDescription() {
        return itemMiscellaneousCreditDescription;
    }

    public void setItemMiscellaneousCreditDescription(String itemMiscellaneousCreditDescription) {
        this.itemMiscellaneousCreditDescription = itemMiscellaneousCreditDescription;
    }

    public Timestamp getCreditMemoPaidTimestamp() {
        return creditMemoPaidTimestamp;
    }

    public void setCreditMemoPaidTimestamp(Timestamp creditMemoPaidTimestamp) {
        this.creditMemoPaidTimestamp = creditMemoPaidTimestamp;
    }

    public PaymentRequestDocument getPaymentRequestDocument() {
        if ((ObjectUtils.isNull(paymentRequestDocument)) && (ObjectUtils.isNotNull(getPaymentRequestIdentifier()))) {
            setPaymentRequestDocument(SpringContext.getBean(PaymentRequestService.class).getPaymentRequestById(getPaymentRequestIdentifier()));
        }
        return this.paymentRequestDocument;
    }

    public void setPaymentRequestDocument(PaymentRequestDocument paymentRequestDocument) {
        if (ObjectUtils.isNull(paymentRequestDocument)) {
            // do not blank out input, instead throw an error
            // setPaymentRequestIdentifier(null);
            this.paymentRequestDocument = null;
        }
        else {
            setPaymentRequestIdentifier(paymentRequestDocument.getPurapDocumentIdentifier());
            this.paymentRequestDocument = paymentRequestDocument;
        }
    }

    /**
     * AS A REPLACEMENT USE getPaymentRequestDocument()
     * 
     * @deprecated
     */
    public PaymentRequestDocument getPaymentRequest() {
        return getPaymentRequestDocument();
    }

    /**
     * AS A REPLACEMENT USE setPaymentRequestDocument(PaymentRequestDocument)
     * 
     * @deprecated
     */
    public void setPaymentRequest(PaymentRequestDocument paymentRequest) {
        setPaymentRequestDocument(paymentRequest);
    }

    /**
     * AS A REPLACEMENT USE getPurchaseOrderDocument()
     * 
     * @deprecated
     */
    public PurchaseOrderDocument getPurchaseOrder() {
        return getPurchaseOrderDocument();
    }

    /**
     * AS A REPLACEMENT USE setPurchaseOrderDocument(PurchaseOrderDocument)
     * 
     * @deprecated
     */
    public void setPurchaseOrder(PurchaseOrderDocument purchaseOrder) {
        setPurchaseOrderDocument(purchaseOrder);
    }

    public Date getPurchaseOrderEndDate() {
        return purchaseOrderEndDate;
    }

    public void setPurchaseOrderEndDate(Date purchaseOrderEndDate) {
        this.purchaseOrderEndDate = purchaseOrderEndDate;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocumentBase#getPoDocumentTypeForAccountsPayableDocumentApprove()
     */
    public String getPoDocumentTypeForAccountsPayableDocumentCancel() {
        return PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocumentBase#getInitialAmount()
     */
    public KualiDecimal getInitialAmount() {
        return this.getCreditMemoAmount();
    }

    /**
     * Credit Memo document is first populated on Continue AP Event, and then prepareForSave continues.
     * 
     * @see org.kuali.rice.krad.document.Document#prepareForSave(org.kuali.rice.krad.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {

        // first populate, then call super
        if (event instanceof AttributedContinuePurapEvent) {
            SpringContext.getBean(CreditMemoService.class).populateDocumentAfterInit(this);
        }

        super.prepareForSave(event);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocumentBase#isAttachmentRequired()
     */
    @Override
    protected boolean isAttachmentRequired() {
        return StringUtils.equalsIgnoreCase("Y", SpringContext.getBean(ParameterService.class).getParameterValueAsString(VendorCreditMemoDocument.class, PurapParameterConstants.PURAP_CM_REQUIRE_ATTACHMENT));
    }

    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocument#getDocumentSpecificService()
     */
    @Override
    public AccountsPayableDocumentSpecificService getDocumentSpecificService() {
        return SpringContext.getBean(CreditMemoService.class);
    }

    /**
     * Forces GL entries to be approved before document final approval.
     * 
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        super.customizeExplicitGeneralLedgerPendingEntry(postable, explicitEntry);
        
        SpringContext.getBean(PurapGeneralLedgerService.class).customizeGeneralLedgerPendingEntry(this, (AccountingLine)postable, explicitEntry, getPurchaseOrderIdentifier(), getDebitCreditCodeForGLEntries(), PurapDocTypeCodes.CREDIT_MEMO_DOCUMENT, isGenerateEncumbranceEntries());

        // CMs do not wait for document final approval to post GL entries; here we are forcing them to be APPROVED
        explicitEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
    }

    public Date getTransactionTaxDate() {
        return getCreditMemoDate();
    }

    public String getVendorAttentionName() {
        return vendorAttentionName;
    }

    public void setVendorAttentionName(String vendorAttentionName) {
        this.vendorAttentionName = vendorAttentionName;
    }

    /**
     * Provides answers to the following splits:
     * RequiresInvoiceAttachment
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(PurapWorkflowConstants.REQUIRES_IMAGE_ATTACHMENT)) return requiresAccountsPayableReviewRouting();
        throw new UnsupportedOperationException("Cannot answer split question for this node you call \""+nodeName+"\"");
    }
    
    public String getPaidIndicatorForResult(){
        return getCreditMemoPaidTimestamp() != null ? "Yes" : "No";
    }
    
    /**
     * Checks all documents notes for attachments.
     * 
     * @return - true if document does not have an image attached, false otherwise
     */
    public boolean documentHasNoImagesAttached() {
        List boNotes = this.getNotes();
        if (ObjectUtils.isNotNull(boNotes)) {
            for (Object obj : boNotes) {
                Note note = (Note) obj;
                
                note.refreshReferenceObject("attachment");
                if (ObjectUtils.isNotNull(note.getAttachment()) && PurapConstants.AttachmentTypeCodes.ATTACHMENT_TYPE_CM_IMAGE.equals(note.getAttachment().getAttachmentTypeCode())) {
                    return false;
                }
            }
        }
        return true;
    }
    
}

