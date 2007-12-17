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

package org.kuali.module.purap.document;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.NoteService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ConciseXmlDocumentConversionService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.CREDIT_MEMO_TYPE_LABELS;
import org.kuali.module.purap.PurapConstants.CreditMemoStatuses;
import org.kuali.module.purap.PurapWorkflowConstants.NodeDetails;
import org.kuali.module.purap.PurapWorkflowConstants.CreditMemoDocument.NodeDetailEnum;
import org.kuali.module.purap.bo.CreditMemoItem;
import org.kuali.module.purap.rule.event.ContinueAccountsPayableEvent;
import org.kuali.module.purap.service.AccountsPayableDocumentSpecificService;
import org.kuali.module.purap.service.AccountsPayableService;
import org.kuali.module.purap.service.CreditMemoCreateService;
import org.kuali.module.purap.service.CreditMemoService;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PurapService;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Credit Memo Document Business Object. Contains the fields associated with the main document table.
 */
public class CreditMemoDocument extends AccountsPayableDocumentBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreditMemoDocument.class);

    private Integer paymentRequestIdentifier;
    private String creditMemoNumber;
    private Date creditMemoDate;
    private KualiDecimal creditMemoAmount;
    private Timestamp creditMemoPaidTimestamp;
    private String itemMiscellaneousCreditDescription;
    private Date purchaseOrderEndDate;
    private String vendorAddressInternationalProvinceName;
    
    private PaymentRequestDocument paymentRequestDocument;

    /**
     * Default constructor.
     */
    public CreditMemoDocument() {
        super();
    }

    /**
     * @see org.kuali.core.document.DocumentBase#getDocumentRepresentationForSerialization()
     */
    @Override
    protected Document getDocumentRepresentationForSerialization() {
        return SpringContext.getBean(ConciseXmlDocumentConversionService.class).getDocumentForSerialization(this);
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
     * Initializes the values for a new document.
     */
    public void initiateDocument() {
        LOG.debug("initiateDocument() started");
        setStatusCode(PurapConstants.CreditMemoStatuses.INITIATE);

        UniversalUser currentUser = (UniversalUser) GlobalVariables.getUserSession().getUniversalUser();
        setAccountsPayableProcessorIdentifier(currentUser.getPersonUniversalIdentifier());
        setProcessingCampusCode(currentUser.getCampusCode());
    }

    /**
     * Clear out the initially populated fields.
     */
    public void clearInitFields() {
        LOG.debug("clearDocument() started");

        // Clearing document overview fields
        getDocumentHeader().setFinancialDocumentDescription(null);
        getDocumentHeader().setExplanation(null);
        getDocumentHeader().setFinancialDocumentTotalAmount(null);
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

    /**
     * @see org.kuali.core.bo.PersistableBusinessObjectBase#isBoNotesSupport()
     */
    @Override
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

        ArrayList poNotes = SpringContext.getBean(NoteService.class).getByRemoteObjectId((this.getPurchaseOrderIdentifier()).toString());
        if (poNotes.size() > 0) {
            hasNotes = true;
        }

        return hasNotes;
    }

    /**
     * @see org.kuali.core.document.DocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        LOG.debug("handleRouteStatusChange() started");
        super.handleRouteStatusChange();
        try {
            // DOCUMENT PROCESSED
            if (this.getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
                SpringContext.getBean(PurapService.class).updateStatus(this, PurapConstants.CreditMemoStatuses.COMPLETE);
                SpringContext.getBean(CreditMemoService.class).saveDocumentWithoutValidation(this);

                return;
            }
            // DOCUMENT DISAPPROVED
            else if (this.getDocumentHeader().getWorkflowDocument().stateIsDisapproved()) {
                String nodeName = SpringContext.getBean(WorkflowDocumentService.class).getCurrentRouteLevelName(getDocumentHeader().getWorkflowDocument());
                NodeDetails currentNode = NodeDetailEnum.getNodeDetailEnumByName(nodeName);
                if (ObjectUtils.isNotNull(currentNode)) {
                    String newStatusCode = currentNode.getDisapprovedStatusCode();
                    if ((StringUtils.isBlank(newStatusCode)) && ((StringUtils.isBlank(currentNode.getDisapprovedStatusCode())) && ((CreditMemoStatuses.INITIATE.equals(getStatusCode())) || (CreditMemoStatuses.IN_PROCESS.equals(getStatusCode()))))) {
                        newStatusCode = CreditMemoStatuses.CANCELLED_IN_PROCESS;
                    }
                    if (StringUtils.isNotBlank(newStatusCode)) {
                        SpringContext.getBean(AccountsPayableService.class).cancelAccountsPayableDocument(this, nodeName);
                        return;
                    }
                }
                logAndThrowRuntimeException("No status found to set for document being disapproved in node '" + nodeName + "'");
            }
            // DOCUMENT CANCELED
            else if (this.getDocumentHeader().getWorkflowDocument().stateIsCanceled()) {
                String currentNodeName = SpringContext.getBean(WorkflowDocumentService.class).getCurrentRouteLevelName(getDocumentHeader().getWorkflowDocument());
                SpringContext.getBean(AccountsPayableService.class).cancelAccountsPayableDocument(this, currentNodeName);
            }
        }
        catch (WorkflowException e) {
            logAndThrowRuntimeException("Error saving routing data while saving document with id " + getDocumentNumber(), e);
        }
    }

    /**
     * Hook point for performing actions that occur after a route level change, in this case; Performs logic necessary after full
     * entry has been completed when past Adhoc Review, or sets the AP approval date when past AP review.
     * 
     * @see org.kuali.module.purap.document.AccountsPayableDocumentBase#preProcessNodeChange(java.lang.String, java.lang.String)
     */
    public boolean processNodeChange(String newNodeName, String oldNodeName) {
        if (NodeDetailEnum.ADHOC_REVIEW.getName().equals(oldNodeName)) {
            SpringContext.getBean(PurapService.class).performLogicForFullEntryCompleted(this);
        }
        else if (NodeDetailEnum.ACCOUNTS_PAYABLE_REVIEW.getName().equals(oldNodeName)) {
            setAccountsPayableApprovalDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
        }
        return true;
    }

    /**
     * @see org.kuali.module.purap.document.AccountsPayableDocumentBase#getNodeDetailEnum(java.lang.String)
     */
    public NodeDetails getNodeDetailEnum(String nodeName) {
        return NodeDetailEnum.getNodeDetailEnumByName(nodeName);
    }

    /**
     * @see org.kuali.module.purap.document.AccountsPayableDocumentBase#saveDocumentFromPostProcessing()
     */
    public void saveDocumentFromPostProcessing() {
        SpringContext.getBean(CreditMemoService.class).saveDocumentWithoutValidation(this);
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getItemClass()
     */
    @Override
    public Class<CreditMemoItem> getItemClass() {
        return CreditMemoItem.class;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentIfPossible()
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
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentLabelIfPossible()
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
     * Calculates the total of the above the line items
     * 
     * @return KualiDecimal - above the line item total
     */
    public KualiDecimal getLineItemTotal() {
        KualiDecimal lineItemTotal = new KualiDecimal(0);

        for (CreditMemoItem item : (List<CreditMemoItem>) getItems()) {
            item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);
            if (item.getItemType().isItemTypeAboveTheLineIndicator() && item.getExtendedPrice() != null) {
                lineItemTotal = lineItemTotal.add(item.getExtendedPrice());
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
        KualiDecimal grandTotal = new KualiDecimal(0);

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
            // KULPURAP-1185 - do not blank out input, instead throw an error
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
     * Gets the vendorAddressInternationalProvinceName attribute. 
     * @return Returns the vendorAddressInternationalProvinceName.
     */
    public String getVendorAddressInternationalProvinceName() {
        return vendorAddressInternationalProvinceName;
    }

    /**
     * Sets the vendorAddressInternationalProvinceName attribute value.
     * @param vendorAddressInternationalProvinceName The vendorAddressInternationalProvinceName to set.
     */
    public void setVendorAddressInternationalProvinceName(String vendorAddressInternationalProvinceName) {
        this.vendorAddressInternationalProvinceName = vendorAddressInternationalProvinceName;
    }

    /**
     * USED FOR ROUTING ONLY
     * 
     * @deprecated
     */
    public String getStatusDescription() {
        return "";
    }

    /**
     * USED FOR ROUTING ONLY
     * 
     * @deprecated
     */
    public void setStatusDescription(String statusDescription) {
    }

    /**
     * @see org.kuali.module.purap.document.AccountsPayableDocumentBase#getPoDocumentTypeForAccountsPayableDocumentApprove()
     */
    public String getPoDocumentTypeForAccountsPayableDocumentCancel() {
        return PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT;
    }

    /**
     * @see org.kuali.module.purap.document.AccountsPayableDocumentBase#getInitialAmount()
     */
    public KualiDecimal getInitialAmount() {
        return this.getCreditMemoAmount();
    }

    /**
     * Credit Memo document is first populated on Continue AP Event, and then prepareForSave continues.
     * 
     * @see org.kuali.core.document.Document#prepareForSave(org.kuali.core.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {

        // first populate, then call super
        if (event instanceof ContinueAccountsPayableEvent) {
            SpringContext.getBean(CreditMemoCreateService.class).populateDocumentAfterInit(this);
        }

        super.prepareForSave(event);
    }

    /**
     * @see org.kuali.module.purap.document.AccountsPayableDocumentBase#isAttachmentRequired()
     */
    @Override
    protected boolean isAttachmentRequired() {
        return StringUtils.equalsIgnoreCase("Y", SpringContext.getBean(ParameterService.class).getParameterValue(CreditMemoDocument.class, PurapParameterConstants.PURAP_CM_REQUIRE_ATTACHMENT));
    }

    /**
     * @see org.kuali.module.purap.document.AccountsPayableDocument#getDocumentSpecificService()
     */
    @Override
    public AccountsPayableDocumentSpecificService getDocumentSpecificService() {
        return SpringContext.getBean(CreditMemoService.class);
    }

}
