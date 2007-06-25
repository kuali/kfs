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

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapConstants.CreditMemoTypes;
import org.kuali.module.purap.bo.CreditMemoItem;
import org.kuali.module.purap.bo.CreditMemoStatusHistory;
import org.kuali.module.vendor.util.VendorUtils;

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

    private PurchaseOrderDocument purchaseOrder;
    private PaymentRequestDocument paymentRequest;

    /**
     * Default constructor.
     */
    public CreditMemoDocument() {
        super();
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
     * Populates the document from either the associated payment request document, purchase order document, or vendor
     * detail based on the credit memo type.
     */
    public void populateDocumentByType() {
        // retrieve preq, po, or vendor for population
        if (StringUtils.equals(getCreditMemoType(), CreditMemoTypes.TYPE_PREQ)) {
            setPaymentRequest(SpringServiceLocator.getPaymentRequestService().getPaymentRequestById(getPaymentRequestIdentifier()));
            setPurchaseOrder(getPaymentRequest().getPurchaseOrderDocument());
        }
        else if (StringUtils.equals(getCreditMemoType(), CreditMemoTypes.TYPE_PO)) {
            setPurchaseOrder(SpringServiceLocator.getPurchaseOrderService().getCurrentPurchaseOrder(getPurchaseOrderIdentifier()));
        }
        else {
            setVendorDetail(SpringServiceLocator.getVendorService().getVendorDetail(VendorUtils.getVendorHeaderId(getVendorNumber()), VendorUtils.getVendorDetailId(getVendorNumber())));
        }
        
        populateVendorFields();
    }
    
    /**
     * Populates the credit memo vendor fields based on the type of credit memo.
     */
    private void populateVendorFields() {
        if (StringUtils.equals(getCreditMemoType(), CreditMemoTypes.TYPE_PREQ) || StringUtils.equals(getCreditMemoType(), CreditMemoTypes.TYPE_PO)) {
            PurchasingAccountsPayableDocument purchasingAPDocument = null;
            if (StringUtils.equals(getCreditMemoType(), CreditMemoTypes.TYPE_PREQ)) {
                purchasingAPDocument = getPaymentRequest();
            }
            else {
                purchasingAPDocument = getPurchaseOrder();
            }
            
            setVendorHeaderGeneratedIdentifier(purchasingAPDocument.getVendorHeaderGeneratedIdentifier());
            setVendorDetailAssignedIdentifier(purchasingAPDocument.getVendorDetailAssignedIdentifier());
            setVendorAddressGeneratedIdentifier(purchasingAPDocument.getVendorAddressGeneratedIdentifier());
            setVendorCustomerNumber(purchasingAPDocument.getVendorCustomerNumber());
            setVendorName(purchasingAPDocument.getVendorName());
            setVendorLine1Address(purchasingAPDocument.getVendorLine1Address());
            setVendorLine2Address(purchasingAPDocument.getVendorLine2Address());
            setVendorCityName(purchasingAPDocument.getVendorCityName());
            setVendorStateCode(purchasingAPDocument.getVendorStateCode());
            setVendorPostalCode(purchasingAPDocument.getVendorPostalCode());
            setVendorCountryCode(purchasingAPDocument.getVendorCountryCode());
        }
        else {
            setVendorHeaderGeneratedIdentifier(getVendorDetail().getVendorHeaderGeneratedIdentifier());
            setVendorDetailAssignedIdentifier(getVendorDetail().getVendorDetailAssignedIdentifier());
            // TODO: check how handled in EPIC
            // setVendorAddressGeneratedIdentifier(getVendorDetail().getVendorAddressGeneratedIdentifier());
            setVendorCustomerNumber(getVendorDetail().getVendorNumber());
            setVendorName(getVendorDetail().getVendorName());
            setVendorLine1Address(getVendorDetail().getDefaultAddressLine1());
            setVendorLine2Address(getVendorDetail().getDefaultAddressLine2());
            setVendorCityName(getVendorDetail().getDefaultAddressCity());
            setVendorStateCode(getVendorDetail().getDefaultAddressStateCode());
            setVendorPostalCode(getVendorDetail().getDefaultAddressPostalCode());
            setVendorCountryCode(getVendorDetail().getDefaultAddressCountryCode());
        }
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
        if (getPaymentRequestIdentifier() != null) {
            return CreditMemoTypes.TYPE_PREQ;
        }
        else if (getPurchaseOrderIdentifier() != null) {
            return CreditMemoTypes.TYPE_PO;
        }
        else {
            return CreditMemoTypes.TYPE_VENDOR;
        }
    }

    /**
     * @see org.kuali.core.bo.PersistableBusinessObjectBase#isBoNotesSupport()
     */
    @Override
    public boolean isBoNotesSupport() {
        return true;
    }
    
    /**
     * @see org.kuali.core.document.DocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        LOG.debug("handleRouteStatusChange() started");
        super.handleRouteStatusChange();

    }

    @Override
    public void handleRouteLevelChange() {
        LOG.debug("handleRouteLevelChange() started");
        super.handleRouteLevelChange();
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


    /**
     * Gets the paymentRequest attribute.
     * 
     * @return Returns the paymentRequest
     */
    public PaymentRequestDocument getPaymentRequest() {
        return paymentRequest;
    }
    
    /**
     * Sets the paymentRequest attribute value.
     * @param paymentRequest The paymentRequest to set.
     */
    public void setPaymentRequest(PaymentRequestDocument paymentRequest) {
        this.paymentRequest = paymentRequest;
    }

    /**
     * Gets the purchaseOrder attribute.
     * 
     * @return Returns the purchaseOrder.
     */
    public PurchaseOrderDocument getPurchaseOrder() {
        return purchaseOrder;
    }

    /**
     * Sets the purchaseOrder attribute value.
     * @param purchaseOrder The purchaseOrder to set.
     */
    public void setPurchaseOrder(PurchaseOrderDocument purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    /**
     * Gets the purchaseOrderNotes attribute.
     * 
     * @return Returns the purchaseOrderNotes.
     */
    public String getPurchaseOrderNotes() {
        ArrayList poNotes = SpringServiceLocator.getNoteService().getByRemoteObjectId((this.getPurchaseOrderIdentifier()).toString());

        if (poNotes.size() > 0) {
            return "Yes";
        }
        return "No";
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
}
