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
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.CREDIT_MEMO_TYPE_LABELS;
import org.kuali.module.purap.bo.CreditMemoItem;
import org.kuali.module.purap.bo.CreditMemoStatusHistory;
import org.kuali.module.purap.service.PurapGeneralLedgerService;

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

    private PaymentRequestDocument paymentRequestDocument;

    private boolean unmatchedOverride; // not persisted

    /**
     * Default constructor.
     */
    public CreditMemoDocument() {
        super();
        unmatchedOverride = false;
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
     * Iniatilizes the values for a new document.
     */
    public void initiateDocument() {
        LOG.debug("initiateDocument() started");
        setStatusCode(PurapConstants.CreditMemoStatuses.INITIATE);

        UniversalUser currentUser = (UniversalUser) GlobalVariables.getUserSession().getUniversalUser();
        setAccountsPayableProcessorIdentifier(currentUser.getPersonUniversalIdentifier());
        setProcessingCampusCode(currentUser.getCampusCode());
    }

    /**
     * Clear out the initial population fields.
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
     * This returns the type of the Credit Memo that was selected on the init screen. It is based on them entering the Vendor, PO or
     * PREQ #.
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
     * Determines if the purchase order has notes.
     * 
     * @return true if po has notes, false if po does not have notes
     */
    public boolean getPurchaseOrderNotes() {
        boolean hasNotes = false;

        ArrayList poNotes = SpringServiceLocator.getNoteService().getByRemoteObjectId((this.getPurchaseOrderIdentifier()).toString());
        if (poNotes.size() > 0) {
            hasNotes = true;
        }

        return hasNotes;
    }

    /**
     * Performs extended price calculation and sets on item if extended price is empty.
     */
    public void updateExtendedPriceOnItems() {
        for (CreditMemoItem item : (List<CreditMemoItem>) getItems()) {
            item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);

            if (ObjectUtils.isNull(item.getExtendedPrice())) {
                KualiDecimal newExtendedPrice = item.calculateExtendedPrice();
                item.setExtendedPrice(newExtendedPrice);
            }
        }
    }

    /**
     * @see org.kuali.core.document.DocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        LOG.debug("handleRouteStatusChange() started");
        super.handleRouteStatusChange();

    }
    
    /**
     * @see org.kuali.module.purap.document.AccountsPayableDocumentBase#preProcessNodeChange(java.lang.String, java.lang.String)
     */
    public boolean processNodeChange(String newNodeName, String oldNodeName) {
        if (PurapConstants.WorkflowConstants.CreditMemoDocument.NodeDetails.ACCOUNTS_PAYABLE_REVIEW.equals(oldNodeName)) {
            setAccountsPayableApprovalDate(SpringServiceLocator.getDateTimeService().getCurrentSqlDate());
            ((PurapGeneralLedgerService) SpringServiceLocator.getService(SpringServiceLocator.PURAP_GENERAL_LEDGER_SERVICE)).generateEntriesCreditMemo(this, PurapConstants.CREATE_CREDIT_MEMO);
        }
        return true;
    }
    
    /**
     * @see org.kuali.module.purap.document.AccountsPayableDocumentBase#getNodeDetailsStatusByNodeNameMap()
     */
    public Map<String, String> getNodeDetailsStatusByNodeNameMap() {
        return PurapConstants.WorkflowConstants.CreditMemoDocument.NodeDetails.STATUS_BY_NODE_NAME;
    }
    
    /**
     * @see org.kuali.module.purap.document.AccountsPayableDocumentBase#saveDocumentFromPostProcessing()
     */
    public void saveDocumentFromPostProcessing() {
        SpringServiceLocator.getCreditMemoService().saveDocumentWithoutValidation(this);
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#addToStatusHistories(java.lang.String,
     *      java.lang.String)
     */
    public void addToStatusHistories(String oldStatus, String newStatus, Note statusHistoryNote) {
        CreditMemoStatusHistory cmsh = new CreditMemoStatusHistory(oldStatus, newStatus);
        addStatusHistoryNote(cmsh, statusHistoryNote);
        getStatusHistories().add(cmsh);
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
        } else if (isSourceDocumentPurchaseOrder()) {
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
            return SpringServiceLocator.getDataDictionaryService().getDocumentLabelByClass(document.getClass());
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

    /**
     * Gets the paymentRequestIdentifier attribute.
     * 
     * @return Returns the paymentRequestIdentifier
     */
    public Integer getPaymentRequestIdentifier() {
        return paymentRequestIdentifier;
    }

    /**
     * Sets the paymentRequestIdentifier attribute.
     * 
     * @param paymentRequestIdentifier The paymentRequestIdentifier to set.
     */
    public void setPaymentRequestIdentifier(Integer paymentRequestIdentifier) {
        this.paymentRequestIdentifier = paymentRequestIdentifier;
    }


    /**
     * Gets the creditMemoNumber attribute.
     * 
     * @return Returns the creditMemoNumber
     */
    public String getCreditMemoNumber() {
        return creditMemoNumber;
    }

    /**
     * Sets the creditMemoNumber attribute.
     * 
     * @param creditMemoNumber The creditMemoNumber to set.
     */
    public void setCreditMemoNumber(String creditMemoNumber) {
        if (creditMemoNumber != null) {
            creditMemoNumber = creditMemoNumber.toUpperCase();
        }

        this.creditMemoNumber = creditMemoNumber;
    }


    /**
     * Gets the creditMemoDate attribute.
     * 
     * @return Returns the creditMemoDate
     */
    public Date getCreditMemoDate() {
        return creditMemoDate;
    }

    /**
     * Sets the creditMemoDate attribute.
     * 
     * @param creditMemoDate The creditMemoDate to set.
     */
    public void setCreditMemoDate(Date creditMemoDate) {
        this.creditMemoDate = creditMemoDate;
    }

    /**
     * Gets the creditMemoAmount attribute.
     * 
     * @return Returns the creditMemoAmount
     */
    public KualiDecimal getCreditMemoAmount() {
        return creditMemoAmount;
    }

    /**
     * Sets the creditMemoAmount attribute.
     * 
     * @param creditMemoAmount The creditMemoAmount to set.
     */
    public void setCreditMemoAmount(KualiDecimal creditMemoAmount) {
        this.creditMemoAmount = creditMemoAmount;
    }

    /**
     * Gets the itemMiscellaneousCreditDescription attribute.
     * 
     * @return Returns the itemMiscellaneousCreditDescription
     */
    public String getItemMiscellaneousCreditDescription() {
        return itemMiscellaneousCreditDescription;
    }

    /**
     * Sets the itemMiscellaneousCreditDescription attribute.
     * 
     * @param itemMiscellaneousCreditDescription The itemMiscellaneousCreditDescription to set.
     */
    public void setItemMiscellaneousCreditDescription(String itemMiscellaneousCreditDescription) {
        this.itemMiscellaneousCreditDescription = itemMiscellaneousCreditDescription;
    }


    /**
     * Gets the creditMemoPaidTimestamp attribute.
     * 
     * @return Returns the creditMemoPaidTimestamp.
     */
    public Timestamp getCreditMemoPaidTimestamp() {
        return creditMemoPaidTimestamp;
    }

    /**
     * Sets the creditMemoPaidTimestamp attribute value.
     * 
     * @param creditMemoPaidTimestamp The creditMemoPaidTimestamp to set.
     */
    public void setCreditMemoPaidTimestamp(Timestamp creditMemoPaidTimestamp) {
        this.creditMemoPaidTimestamp = creditMemoPaidTimestamp;
    }

    public PaymentRequestDocument getPaymentRequestDocument() {
        if ( (ObjectUtils.isNull(paymentRequestDocument)) && (ObjectUtils.isNotNull(getPaymentRequestIdentifier())) ) {
            setPaymentRequestDocument(SpringServiceLocator.getPaymentRequestService().getPaymentRequestById(getPaymentRequestIdentifier()));
        }
        return this.paymentRequestDocument;
    }
    
    public void setPaymentRequestDocument(PaymentRequestDocument paymentRequestDocument) {
        if (ObjectUtils.isNull(paymentRequestDocument)) {
            //KULPURAP-1185 - do not blank out input, instead throw an error
            //setPaymentRequestIdentifier(null);            
            this.paymentRequestDocument = null;
        } else {
            setPaymentRequestIdentifier(paymentRequestDocument.getPurapDocumentIdentifier());
            this.paymentRequestDocument = paymentRequestDocument;
        }
    }

    /**
     * AS A REPLACEMENT USE getPaymentRequestDocument()
     * @deprecated
     */
    public PaymentRequestDocument getPaymentRequest() {
        return getPaymentRequestDocument();
    }

    /**
     * AS A REPLACEMENT USE setPaymentRequestDocument(PaymentRequestDocument)
     * @deprecated 
     */
    public void setPaymentRequest(PaymentRequestDocument paymentRequest) {
        setPaymentRequestDocument(paymentRequest);
    }

    /**
     * AS A REPLACEMENT USE getPurchaseOrderDocument()
     * @deprecated
     */
    public PurchaseOrderDocument getPurchaseOrder() {
        return getPurchaseOrderDocument();
    }

    /**
     * AS A REPLACEMENT USE setPurchaseOrderDocument(PurchaseOrderDocument)
     * @deprecated
     */
    public void setPurchaseOrder(PurchaseOrderDocument purchaseOrder) {
        setPurchaseOrderDocument(purchaseOrder);
    }

    /**
     * Gets the purchaseOrderEndDate attribute.
     * 
     * @return Returns the purchaseOrderEndDate.
     */
    public Date getPurchaseOrderEndDate() {
        return purchaseOrderEndDate;
    }

    /**
     * Sets the purchaseOrderEndDate attribute value.
     * 
     * @param purchaseOrderEndDate The purchaseOrderEndDate to set.
     */
    public void setPurchaseOrderEndDate(Date purchaseOrderEndDate) {
        this.purchaseOrderEndDate = purchaseOrderEndDate;
    }

    /**
     * Gets the unmatchedOverride attribute.
     * 
     * @return Returns the unmatchedOverride.
     */
    public boolean isUnmatchedOverride() {
        return unmatchedOverride;
    }

    /**
     * Sets the unmatchedOverride attribute value.
     * 
     * @param unmatchedOverride The unmatchedOverride to set.
     */
    public void setUnmatchedOverride(boolean unmatchedOverride) {
        this.unmatchedOverride = unmatchedOverride;
    }
    
    /**
     * USED FOR ROUTING ONLY
     * @deprecated
     */
    public String getStatusDescription() {
        return "";
    }

    /**
     * USED FOR ROUTING ONLY
     * @deprecated
     */
    public void setStatusDescription(String statusDescription) {
    }

    /**
     * @see org.kuali.module.purap.document.AccountsPayableDocumentBase#getPoDocumentTypeForAccountsPayableDocumentApprove()
     */
    public String getPoDocumentTypeForAccountsPayableDocumentApprove() {
        return PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT;
    }

}
