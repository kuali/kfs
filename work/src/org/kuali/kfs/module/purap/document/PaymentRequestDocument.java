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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.module.purap.PurapWorkflowConstants.NodeDetails;
import org.kuali.module.purap.PurapWorkflowConstants.PaymentRequestDocument.NodeDetailEnum;
import org.kuali.module.purap.bo.ItemType;
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.bo.PaymentRequestStatusHistory;
import org.kuali.module.purap.bo.PaymentRequestView;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.RecurringPaymentType;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.purap.util.ExpiredOrClosedAccountEntry;
import org.kuali.module.vendor.VendorConstants;
import org.kuali.module.vendor.VendorPropertyConstants;
import org.kuali.module.vendor.bo.PaymentTermType;
import org.kuali.module.vendor.bo.PurchaseOrderCostSource;
import org.kuali.module.vendor.bo.ShippingPaymentTerms;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.service.VendorService;

import edu.iu.uis.eden.clientapp.vo.ActionTakenEventVO;
import edu.iu.uis.eden.exception.WorkflowException;


/**
 * Payment Request Document
 */
public class PaymentRequestDocument extends AccountsPayableDocumentBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestDocument.class);

    private String purchaseOrderClassificationTypeDescription;
    private Date invoiceDate;
    private String invoiceNumber;
    private KualiDecimal vendorInvoiceAmount;
    private String vendorPaymentTermsCode;
    private String vendorShippingPaymentTermsCode;
    private Date paymentRequestPayDate;
    private String paymentRequestCostSourceCode;
    private boolean paymentRequestedCancelIndicator;
    private boolean paymentAttachmentIndicator;
    private boolean immediatePaymentIndicator;
    private String specialHandlingInstructionLine1Text;
    private String specialHandlingInstructionLine2Text;
    private String specialHandlingInstructionLine3Text;
    private Date paymentPaidDate;
    private boolean paymentRequestElectronicInvoiceIndicator;
    private String accountsPayableRequestCancelIdentifier;
    private Integer originalVendorHeaderGeneratedIdentifier;
    private Integer originalVendorDetailAssignedIdentifier;
    private Integer alternateVendorHeaderGeneratedIdentifier;
    private Integer alternateVendorDetailAssignedIdentifier;
    private String purchaseOrderNotes;

    // NOT PERSISTED IN DB
    private String recurringPaymentTypeCode;
    private String vendorShippingTitleCode;
    private Date purchaseOrderEndDate;
    
    // BELOW USED BY ROUTING
    private Integer requisitionIdentifier;
    
    // REFERENCE OBJECTS
    private PaymentTermType vendorPaymentTerms;
    private ShippingPaymentTerms vendorShippingPaymentTerms;
    private PurchaseOrderCostSource paymentRequestCostSource;
    private RecurringPaymentType recurringPaymentType;
    
   
    /**
	 * Default constructor.
	 */
	public PaymentRequestDocument() {
        super();
    }

    /**
     * @see org.kuali.core.bo.PersistableBusinessObjectBase#isBoNotesSupport()
     */
    @Override
    public boolean isBoNotesSupport() {
        return true;
    }

    /**
     * Gets the requisitionIdentifier attribute. 
     * @return Returns the requisitionIdentifier.
     */
    public Integer getRequisitionIdentifier() {
        return requisitionIdentifier;
    }

    /**
     * Sets the requisitionIdentifier attribute value.
     * @param requisitionIdentifier The requisitionIdentifier to set.
     */
    public void setRequisitionIdentifier(Integer requisitionIdentifier) {
        this.requisitionIdentifier = requisitionIdentifier;
    }
    
    /**
     * @see org.kuali.module.purap.document.AccountsPayableDocumentBase#populateDocumentForRouting()
     */
    @Override
    public void populateDocumentForRouting() {
        this.setRequisitionIdentifier(getPurchaseOrderDocument().getRequisitionIdentifier());
        super.populateDocumentForRouting();
    }

    /**
     * Gets the purchaseOrderClassificationTypeDescription attribute. 
     * @return Returns the purchaseOrderClassificationTypeDescription.
     */
    public String getPurchaseOrderClassificationTypeDescription() {
        return purchaseOrderClassificationTypeDescription;
    }

    /**
     * Sets the purchaseOrderClassificationTypeDescription attribute value.
     * @param purchaseOrderClassificationTypeDescription The purchaseOrderClassificationTypeDescription to set.
     */
    public void setPurchaseOrderClassificationTypeDescription(String purchaseOrderClassificationTypeDescription) {
        this.purchaseOrderClassificationTypeDescription = purchaseOrderClassificationTypeDescription;
    }
    
    /**
     * Gets the invoiceDate attribute.
     * 
     * @return Returns the invoiceDate
     * 
     */
    public Date getInvoiceDate() { 
        return invoiceDate;
    }

    /**
     * Sets the invoiceDate attribute.
     * 
     * @param invoiceDate The invoiceDate to set.
     * 
     */
    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }


    /**
     * Gets the invoiceNumber attribute.
     * 
     * @return Returns the invoiceNumber
     * 
     */
    public String getInvoiceNumber() { 
        return invoiceNumber;
    }

    /**
     * Sets the invoiceNumber attribute.
     * 
     * @param invoiceNumber The invoiceNumber to set.
     * 
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }


    /**
     * Gets the vendorInvoiceAmount attribute.
     * 
     * @return Returns the vendorInvoiceAmount
     * 
     */
    public KualiDecimal getVendorInvoiceAmount() { 
        return vendorInvoiceAmount;
    }

    /**
     * Sets the vendorInvoiceAmount attribute.
     * 
     * @param vendorInvoiceAmount The vendorInvoiceAmount to set.
     * 
     */
    public void setVendorInvoiceAmount(KualiDecimal vendorInvoiceAmount) {
        this.vendorInvoiceAmount = vendorInvoiceAmount;
    }


    /**
     * Gets the vendorPaymentTermsCode attribute.
     * 
     * @return Returns the vendorPaymentTermsCode
     * 
     */
    public String getVendorPaymentTermsCode() { 
        return vendorPaymentTermsCode;
    }

    /**
     * Sets the vendorPaymentTermsCode attribute.
     * 
     * @param vendorPaymentTermsCode The vendorPaymentTermsCode to set.
     * 
     */
    public void setVendorPaymentTermsCode(String vendorPaymentTermsCode) {
        this.vendorPaymentTermsCode = vendorPaymentTermsCode;
    }
    /**
     * Gets the vendorPaymentTerms attribute.
     * 
     * @return Returns the vendorPaymentTerms
     * 
     */
    public PaymentTermType getVendorPaymentTerms() {
        if (ObjectUtils.isNull(vendorPaymentTerms)) {
            refreshReferenceObject(VendorPropertyConstants.VENDOR_PAYMENT_TERMS);
        }
        return vendorPaymentTerms;
    }
    /**
     * Sets the vendorPaymentTerms attribute.
     * 
     * @param vendorPaymentTerms The vendorPaymentTerms to set.
     * 
     */
    public void setVendorPaymentTerms(PaymentTermType vendorPaymentTerms) {
        this.vendorPaymentTerms = vendorPaymentTerms;
    }
    
    /**
     * Gets the vendorShippingPaymentTermsCode attribute.
     * 
     * @return Returns the vendorShippingPaymentTermsCode
     * 
     */
    public String getVendorShippingPaymentTermsCode() { 
        if (ObjectUtils.isNull(vendorPaymentTerms)) {
            refreshReferenceObject(VendorPropertyConstants.VENDOR_SHIPPING_PAYMENT_TERMS);
        }
        return vendorShippingPaymentTermsCode;
    }

    /**
     * Sets the vendorShippingPaymentTermsCode attribute.
     * 
     * @param vendorShippingPaymentTermsCode The vendorShippingPaymentTermsCode to set.
     * 
     */
    public void setVendorShippingPaymentTermsCode(String vendorShippingPaymentTermsCode) {
        this.vendorShippingPaymentTermsCode = vendorShippingPaymentTermsCode;
    }


    /**
     * Gets the paymentRequestPayDate attribute.
     * 
     * @return Returns the paymentRequestPayDate
     * 
     */
    public Date getPaymentRequestPayDate() { 
        return paymentRequestPayDate;
    }

    /**
     * Sets the paymentRequestPayDate attribute.
     * 
     * @param paymentRequestPayDate The paymentRequestPayDate to set.
     * 
     */
    public void setPaymentRequestPayDate(Date paymentRequestPayDate) {
        this.paymentRequestPayDate = paymentRequestPayDate;
    }


    /**
     * Gets the paymentRequestCostSourceCode attribute.
     * 
     * @return Returns the paymentRequestCostSourceCode
     * 
     */
    public String getPaymentRequestCostSourceCode() { 
        return paymentRequestCostSourceCode;
    }

    /**
     * Sets the paymentRequestCostSourceCode attribute.
     * 
     * @param paymentRequestCostSourceCode The paymentRequestCostSourceCode to set.
     * 
     */
    public void setPaymentRequestCostSourceCode(String paymentRequestCostSourceCode) {
        this.paymentRequestCostSourceCode = paymentRequestCostSourceCode;
    }


    /**
     * Gets the paymentRequestedCancelIndicator attribute.
     * 
     * @return Returns the paymentRequestedCancelIndicator
     * 
     */
    public boolean getPaymentRequestedCancelIndicator() { 
        return paymentRequestedCancelIndicator;
    }

    /**
     * Gets the paymentRequestedCancelIndicator attribute.
     * 
     * @return Returns the paymentRequestedCancelIndicator
     * 
     */
    public boolean isPaymentRequestedCancelIndicator() { 
        return paymentRequestedCancelIndicator;
    }

    /**
     * Sets the paymentRequestedCancelIndicator attribute.
     * 
     * @param paymentRequestedCancelIndicator The paymentRequestedCancelIndicator to set.
     * 
     */
    public void setPaymentRequestedCancelIndicator(boolean paymentRequestedCancelIndicator) {
        this.paymentRequestedCancelIndicator = paymentRequestedCancelIndicator;
    }


    /**
     * Gets the paymentAttachmentIndicator attribute.
     * 
     * @return Returns the paymentAttachmentIndicator
     * 
     */
    public boolean getPaymentAttachmentIndicator() { 
        return paymentAttachmentIndicator;
    }

    /**
     * Sets the paymentAttachmentIndicator attribute.
     * 
     * @param paymentAttachmentIndicator The paymentAttachmentIndicator to set.
     * 
     */
    public void setPaymentAttachmentIndicator(boolean paymentAttachmentIndicator) {
        this.paymentAttachmentIndicator = paymentAttachmentIndicator;
    }


    /**
     * Gets the immediatePaymentIndicator attribute.
     * 
     * @return Returns the immediatePaymentIndicator
     * 
     */
    public boolean getImmediatePaymentIndicator() { 
        return immediatePaymentIndicator;
    }

    /**
     * Sets the immediatePaymentIndicator attribute.
     * 
     * @param immediatePaymentIndicator The immediatePaymentIndicator to set.
     * 
     */
    public void setImmediatePaymentIndicator(boolean immediatePaymentIndicator) {
        this.immediatePaymentIndicator = immediatePaymentIndicator;
    }

    public String getSpecialHandlingInstructionLine1Text() {
        return specialHandlingInstructionLine1Text;
    }

    public void setSpecialHandlingInstructionLine1Text(String specialHandlingInstructionLine1Text) {
        this.specialHandlingInstructionLine1Text = specialHandlingInstructionLine1Text;
    }

    public String getSpecialHandlingInstructionLine2Text() {
        return specialHandlingInstructionLine2Text;
    }

    public void setSpecialHandlingInstructionLine2Text(String specialHandlingInstructionLine2Text) {
        this.specialHandlingInstructionLine2Text = specialHandlingInstructionLine2Text;
    }

    public String getSpecialHandlingInstructionLine3Text() {
        return specialHandlingInstructionLine3Text;
    }

    public void setSpecialHandlingInstructionLine3Text(String specialHandlingInstructionLine3Text) {
        this.specialHandlingInstructionLine3Text = specialHandlingInstructionLine3Text;
    }


    /**
     * Gets the paymentPaidDate attribute. 
     * @return Returns the paymentPaidDate.
     */
    public Date getPaymentPaidDate() {
        return paymentPaidDate;
    }

    /**
     * Sets the paymentPaidDate attribute value.
     * @param paymentPaidDate The paymentPaidDate to set.
     */
    public void setPaymentPaidDate(Date paymentPaidDate) {
        this.paymentPaidDate = paymentPaidDate;
    }

    /**
     * Gets the paymentRequestElectronicInvoiceIndicator attribute. 
     * @return Returns the paymentRequestElectronicInvoiceIndicator.
     */
    public boolean getPaymentRequestElectronicInvoiceIndicator() {
        return paymentRequestElectronicInvoiceIndicator;
    }

    /**
     * Sets the paymentRequestElectronicInvoiceIndicator attribute value.
     * @param paymentRequestElectronicInvoiceIndicator The paymentRequestElectronicInvoiceIndicator to set.
     */
    public void setPaymentRequestElectronicInvoiceIndicator(boolean paymentRequestElectronicInvoiceIndicator) {
        this.paymentRequestElectronicInvoiceIndicator = paymentRequestElectronicInvoiceIndicator;
    }


    /**
     * Gets the accountsPayableRequestCancelIdentifier attribute.
     * 
     * @return Returns the accountsPayableRequestCancelIdentifier
     * 
     */
    public String getAccountsPayableRequestCancelIdentifier() { 
        return accountsPayableRequestCancelIdentifier;
    }

    /**
     * Sets the accountsPayableRequestCancelIdentifier attribute.
     * 
     * @param accountsPayableRequestCancelIdentifier The accountsPayableRequestCancelIdentifier to set.
     * 
     */
    public void setAccountsPayableRequestCancelIdentifier(String accountsPayableRequestCancelIdentifier) {
        this.accountsPayableRequestCancelIdentifier = accountsPayableRequestCancelIdentifier;
    }


    /**
     * Gets the originalVendorHeaderGeneratedIdentifier attribute.
     * 
     * @return Returns the originalVendorHeaderGeneratedIdentifier
     * 
     */
    public Integer getOriginalVendorHeaderGeneratedIdentifier() { 
        return originalVendorHeaderGeneratedIdentifier;
    }

    /**
     * Sets the originalVendorHeaderGeneratedIdentifier attribute.
     * 
     * @param originalVendorHeaderGeneratedIdentifier The originalVendorHeaderGeneratedIdentifier to set.
     * 
     */
    public void setOriginalVendorHeaderGeneratedIdentifier(Integer originalVendorHeaderGeneratedIdentifier) {
        this.originalVendorHeaderGeneratedIdentifier = originalVendorHeaderGeneratedIdentifier;
    }


    /**
     * Gets the originalVendorDetailAssignedIdentifier attribute.
     * 
     * @return Returns the originalVendorDetailAssignedIdentifier
     * 
     */
    public Integer getOriginalVendorDetailAssignedIdentifier() { 
        return originalVendorDetailAssignedIdentifier;
    }

    /**
     * Sets the originalVendorDetailAssignedIdentifier attribute.
     * 
     * @param originalVendorDetailAssignedIdentifier The originalVendorDetailAssignedIdentifier to set.
     * 
     */
    public void setOriginalVendorDetailAssignedIdentifier(Integer originalVendorDetailAssignedIdentifier) {
        this.originalVendorDetailAssignedIdentifier = originalVendorDetailAssignedIdentifier;
    }


    /**
     * Gets the alternateVendorHeaderGeneratedIdentifier attribute.
     * 
     * @return Returns the alternateVendorHeaderGeneratedIdentifier
     * 
     */
    public Integer getAlternateVendorHeaderGeneratedIdentifier() { 
        return alternateVendorHeaderGeneratedIdentifier;
    }

    /**
     * Sets the alternateVendorHeaderGeneratedIdentifier attribute.
     * 
     * @param alternateVendorHeaderGeneratedIdentifier The alternateVendorHeaderGeneratedIdentifier to set.
     * 
     */
    public void setAlternateVendorHeaderGeneratedIdentifier(Integer alternateVendorHeaderGeneratedIdentifier) {
        this.alternateVendorHeaderGeneratedIdentifier = alternateVendorHeaderGeneratedIdentifier;
    }


    /**
     * Gets the alternateVendorDetailAssignedIdentifier attribute.
     * 
     * @return Returns the alternateVendorDetailAssignedIdentifier
     * 
     */
    public Integer getAlternateVendorDetailAssignedIdentifier() { 
        return alternateVendorDetailAssignedIdentifier;
    }

    /**
     * Sets the alternateVendorDetailAssignedIdentifier attribute.
     * 
     * @param alternateVendorDetailAssignedIdentifier The alternateVendorDetailAssignedIdentifier to set.
     * 
     */
    public void setAlternateVendorDetailAssignedIdentifier(Integer alternateVendorDetailAssignedIdentifier) {
        this.alternateVendorDetailAssignedIdentifier = alternateVendorDetailAssignedIdentifier;
    }

    /**
     * Gets the vendorShippingPaymentTerms attribute. 
     * @return Returns the vendorShippingPaymentTerms.
     */
    public ShippingPaymentTerms getVendorShippingPaymentTerms() {
        return vendorShippingPaymentTerms;
    }


    /**
     * Sets the vendorShippingPaymentTerms attribute value.
     * @param vendorShippingPaymentTerms The vendorShippingPaymentTerms to set.
     */
    public void setVendorShippingPaymentTerms(ShippingPaymentTerms vendorShippingPaymentTerms) {
        this.vendorShippingPaymentTerms = vendorShippingPaymentTerms;
    }

    public String getVendorShippingTitleCode() {
        //TODO f2f: this should be printing the decription instead of the code; do we need the reference object?
        if (ObjectUtils.isNotNull(this.getPurchaseOrderDocument())) {
            return this.getPurchaseOrderDocument().getVendorShippingTitleCode();
        }
        return vendorShippingTitleCode;
    }


    public void setVendorShippingTitleCode(String vendorShippingTitleCode) {
        this.vendorShippingTitleCode = vendorShippingTitleCode;
    }
    
    /**
     * Gets the purchaseOrderEndDate attribute. 
     * @return Returns the purchaseOrderEndDate.
     */

    public Date getPurchaseOrderEndDate() {
        return purchaseOrderEndDate;
    }

    /**
     * Sets the purchaseOrderEndDate attribute value.
     * @param purchaseOrderEndDate The purchaseOrderEndDate to set.
     */
    public void setPurchaseOrderEndDate(Date purchaseOrderEndDate) {
        this.purchaseOrderEndDate = purchaseOrderEndDate;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#addToStatusHistories(java.lang.String, java.lang.String)
     */
    public void addToStatusHistories( String oldStatus, String newStatus ) {
        PaymentRequestStatusHistory prsh = new PaymentRequestStatusHistory( oldStatus, newStatus );
        this.getStatusHistories().add( prsh );
    }
    
    /**
     * Perform logic needed to initiate PREQ Document
     */
    public void initiateDocument() {
        LOG.debug("initiateDocument() started");
        UniversalUser currentUser = (UniversalUser)GlobalVariables.getUserSession().getUniversalUser();
        this.setStatusCode( PurapConstants.PaymentRequestStatuses.INITIATE );
        this.setAccountsPayableProcessorIdentifier(currentUser.getPersonUniversalIdentifier());
        this.setProcessingCampusCode(currentUser.getCampusCode());
        this.refreshNonUpdateableReferences();
    }
    
    /**
     * Perform logic needed to initiate PREQ Document
     */
    public void clearInitFields() {
        LOG.debug("clearDocument() started");
        // Clearing document overview fields 
        this.getDocumentHeader().setFinancialDocumentDescription(null);
        this.getDocumentHeader().setExplanation(null);
        this.getDocumentHeader().setFinancialDocumentTotalAmount(null);
        this.getDocumentHeader().setOrganizationDocumentNumber(null);
    
        // Clearing document Init fields
        this.setPurchaseOrderIdentifier(null);
        this.setInvoiceNumber(null);
        this.setInvoiceDate(null);
        this.setVendorInvoiceAmount(null);
        this.setSpecialHandlingInstructionLine1Text(null);
        this.setSpecialHandlingInstructionLine2Text(null);
        this.setSpecialHandlingInstructionLine3Text(null);
    }
  
    /**
     * TODO: this should be cleaned up
     * This method populates a preq from po
     * @param po
     */
    public void populatePaymentRequestFromPurchaseOrder(PurchaseOrderDocument po) {
        this.setPurchaseOrderIdentifier(po.getPurapDocumentIdentifier());
        this.setPostingYear(po.getPostingYear());
        this.setVendorCustomerNumber(po.getVendorCustomerNumber());
        if (po.getPurchaseOrderCostSource() != null ){
            this.setPaymentRequestCostSource(po.getPurchaseOrderCostSource());
            this.setPaymentRequestCostSourceCode(po.getPurchaseOrderCostSourceCode()); 
        }
        if (po.getVendorShippingPaymentTerms()!= null){
            this.setVendorShippingPaymentTerms(po.getVendorShippingPaymentTerms());
            this.setVendorShippingPaymentTermsCode(po.getVendorShippingPaymentTermsCode());
        }
        
        if (po.getRecurringPaymentType() !=null){
            this.setRecurringPaymentType(po.getRecurringPaymentType());
            this.setRecurringPaymentTypeCode(po.getRecurringPaymentTypeCode());
        }
        
        this.setVendorHeaderGeneratedIdentifier(po.getVendorHeaderGeneratedIdentifier());
        this.setVendorDetailAssignedIdentifier(po.getVendorDetailAssignedIdentifier());
        this.setVendorCustomerNumber(po.getVendorCustomerNumber());
        this.setVendorName(po.getVendorName());

        // populate preq vendor address with the default remit address type for the vendor if found
        String userCampus = GlobalVariables.getUserSession().getUniversalUser().getCampusCode();
        VendorAddress vendorAddress = SpringContext.getBean(VendorService.class).getVendorDefaultAddress(po.getVendorHeaderGeneratedIdentifier(), po.getVendorDetailAssignedIdentifier(), VendorConstants.AddressTypes.REMIT, userCampus);
        if (vendorAddress != null) {
            this.templateVendorAddress(vendorAddress);
            this.setVendorAddressGeneratedIdentifier(vendorAddress.getVendorAddressGeneratedIdentifier());
        }
        else {
            // set address from PO
            this.setVendorAddressGeneratedIdentifier(po.getVendorAddressGeneratedIdentifier());
        this.setVendorLine1Address(po.getVendorLine1Address());
        this.setVendorLine2Address(po.getVendorLine2Address());
        this.setVendorCityName(po.getVendorCityName());
        this.setVendorStateCode(po.getVendorStateCode());
        this.setVendorPostalCode(po.getVendorPostalCode());
        this.setVendorCountryCode(po.getVendorCountryCode());
        }

        if ((po.getVendorPaymentTerms() == null) || ("".equals(po.getVendorPaymentTerms().getVendorPaymentTermsCode())) ) {
            this.setVendorPaymentTerms(new PaymentTermType());
            this.vendorPaymentTerms.setVendorPaymentTermsCode("");
        } else {
            this.setVendorPaymentTermsCode(po.getVendorPaymentTermsCode());
            this.setVendorPaymentTerms(po.getVendorPaymentTerms());
        }
        this.setPaymentRequestPayDate(SpringContext.getBean(PaymentRequestService.class).calculatePayDate(this.getInvoiceDate(), this.getVendorPaymentTerms()));
        for (PurchaseOrderItem poi : (List<PurchaseOrderItem>)po.getItems()) {
            //TODO: add this back if we end up building the list of items at every load (see KULPURAP-1393)
//            if(poi.isItemActiveIndicator()) {
                this.getItems().add(new PaymentRequestItem(poi,this));                
//            }
        }
        //add missing below the line
        SpringContext.getBean(PurapService.class).addBelowLineItems(this);
        this.setAccountsPayablePurchasingDocumentLinkIdentifier(po.getAccountsPayablePurchasingDocumentLinkIdentifier());

        this.refreshNonUpdateableReferences();
    }

    /**
     * TODO: this should be cleaned up.. it is also a replica of the method above except it performs account replacement
     * This method populates a preq from po
     * @param po
     */
    public void populatePaymentRequestFromPurchaseOrder(PurchaseOrderDocument po, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        this.setPurchaseOrderIdentifier(po.getPurapDocumentIdentifier());
        this.setPostingYear(po.getPostingYear());
        this.setVendorCustomerNumber(po.getVendorCustomerNumber());
        if (po.getPurchaseOrderCostSource() != null ){
            this.setPaymentRequestCostSource(po.getPurchaseOrderCostSource());
            this.setPaymentRequestCostSourceCode(po.getPurchaseOrderCostSourceCode()); 
        }
        if (po.getVendorShippingPaymentTerms()!= null){
            this.setVendorShippingPaymentTerms(po.getVendorShippingPaymentTerms());
            this.setVendorShippingPaymentTermsCode(po.getVendorShippingPaymentTermsCode());
        }
        
        if (po.getRecurringPaymentType() !=null){
            this.setRecurringPaymentType(po.getRecurringPaymentType());
            this.setRecurringPaymentTypeCode(po.getRecurringPaymentTypeCode());
        }
        
        this.setVendorHeaderGeneratedIdentifier(po.getVendorHeaderGeneratedIdentifier());
        this.setVendorDetailAssignedIdentifier(po.getVendorDetailAssignedIdentifier());
        this.setVendorCustomerNumber(po.getVendorCustomerNumber());
        this.setVendorName(po.getVendorName());

        // populate preq vendor address with the default remit address type for the vendor if found
        String userCampus = GlobalVariables.getUserSession().getUniversalUser().getCampusCode();
        VendorAddress vendorAddress = SpringContext.getBean(VendorService.class).getVendorDefaultAddress(po.getVendorHeaderGeneratedIdentifier(), po.getVendorDetailAssignedIdentifier(), VendorConstants.AddressTypes.REMIT, userCampus);
        if (vendorAddress != null) {
            this.templateVendorAddress(vendorAddress);
            this.setVendorAddressGeneratedIdentifier(vendorAddress.getVendorAddressGeneratedIdentifier());
        }
        else {
            // set address from PO
            this.setVendorAddressGeneratedIdentifier(po.getVendorAddressGeneratedIdentifier());
        this.setVendorLine1Address(po.getVendorLine1Address());
        this.setVendorLine2Address(po.getVendorLine2Address());
        this.setVendorCityName(po.getVendorCityName());
        this.setVendorStateCode(po.getVendorStateCode());
        this.setVendorPostalCode(po.getVendorPostalCode());
        this.setVendorCountryCode(po.getVendorCountryCode());
        }

        if ((po.getVendorPaymentTerms() == null) || ("".equals(po.getVendorPaymentTerms().getVendorPaymentTermsCode())) ) {
            this.setVendorPaymentTerms(new PaymentTermType());
            this.vendorPaymentTerms.setVendorPaymentTermsCode("");
        } else {
            this.setVendorPaymentTermsCode(po.getVendorPaymentTermsCode());
            this.setVendorPaymentTerms(po.getVendorPaymentTerms());
        }
        this.setPaymentRequestPayDate(SpringContext.getBean(PaymentRequestService.class).calculatePayDate(this.getInvoiceDate(), this.getVendorPaymentTerms()));
        for (PurchaseOrderItem poi : (List<PurchaseOrderItem>)po.getItems()) {
            //TODO: add this back if we end up building the list of items at every load (see KULPURAP-1393)
//            if(poi.isItemActiveIndicator()) {
                this.getItems().add(new PaymentRequestItem(poi,this, expiredOrClosedAccountList));                
//              }
        }
        //add missing below the line
        SpringContext.getBean(PurapService.class).addBelowLineItems(this);
        this.setAccountsPayablePurchasingDocumentLinkIdentifier(po.getAccountsPayablePurchasingDocumentLinkIdentifier());

        this.refreshNonUpdateableReferences();
    }
   

    /**
     * @see org.kuali.core.document.DocumentBase#getDocumentTitle()
     */
    @Override
    public String getDocumentTitle() {
        String specificTitle = SpringContext.getBean(KualiConfigurationService.class).getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP,PurapParameterConstants.PURAP_OVERRIDE_PREQ_DOC_TITLE);
        if (StringUtils.equalsIgnoreCase(specificTitle,Boolean.TRUE.toString())) {
            return getCustomDocumentTitle();
        }
        return super.getDocumentTitle();
    }
    
    private String getCustomDocumentTitle() {
        try{
            //set the workflow document title
            String poNumber = getPurchaseOrderIdentifier().toString();
            String vendorName = StringUtils.trimToEmpty( getVendorName() );
            String preqAmount = getGrandTotal().toString();
            String indicator = getTitleIndicator();        
            
            String documentTitle = new StringBuffer("PO: ").append(poNumber).append(" Vendor: ").append(vendorName).append(" Amount: ").append(preqAmount).append(" ").append(indicator).toString();
            String[] nodeNames = getDocumentHeader().getWorkflowDocument().getNodeNames();
            if ( (nodeNames.length == 1) && (NodeDetailEnum.VENDOR_TAX_REVIEW.getName().equals(nodeNames[0])) ) {
                // tax review
                //grab the first account
                PurApAccountingLine theAccount = getFirstAccount();
                //setup variables
                String deliveryCampus = StringUtils.trimToEmpty( (getProcessingCampus() != null ? getProcessingCampus().getCampusShortName() : "") );        
                String accountNumber = (theAccount != null ? StringUtils.trimToEmpty(theAccount.getAccountNumber()) : "");
                String department = (theAccount != null ? StringUtils.trimToEmpty((( (theAccount.getAccount() != null) && (theAccount.getAccount().getOrganization() != null) ) ? theAccount.getAccount().getOrganization().getOrganizationName() : "")) : "");
                documentTitle = new StringBuffer("Vendor: ").append(vendorName).append(" PO: ").append(poNumber).append(" Account Number: ").append(accountNumber).append(" Dept: ").append(department).append(" Delivery Campus: ").append(deliveryCampus).toString();
            }
            return documentTitle;
        }catch (WorkflowException e) {
            LOG.error("Error updating Payment Request document: " + e.getMessage());
            throw new RuntimeException("Error updating Payment Request document: " + e.getMessage());
        }
    }

    /**
     * This method returns the first payment item's first account (assuming the item list is sequentially ordered).
     * 
     * @return
     */
    private PurApAccountingLine getFirstAccount(){
        // loop through items, and pick the first item
        if ( (getItems() != null) && (!getItems().isEmpty()) ) {
            PaymentRequestItem itemToUse = null;
            for (Iterator iter = getItems().iterator(); iter.hasNext();) {
                PaymentRequestItem item = (PaymentRequestItem) iter.next();
                if ( (item.isConsideredEntered()) && ( (item.getSourceAccountingLines() != null) && (!item.getSourceAccountingLines().isEmpty()) ) ) {
                    // accounting lines are not empty so pick the first account
                    PurApAccountingLine accountLine = item.getSourceAccountingLine(0);
                    accountLine.refreshNonUpdateableReferences();
                    return accountLine;
                }
            }
        }
        return null;
    }

    /**
     * This method determines the indicator text that will appear in the workflow document title
     * 
     * @return
     */
    private String getTitleIndicator(){
        if (isHoldIndicator()) {
            return PurapConstants.PaymentRequestIndicatorText.HOLD;
        }
        else if (isPaymentRequestedCancelIndicator()) {
            return PurapConstants.PaymentRequestIndicatorText.REQUEST_CANCEL;
        }
        return "";
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
                if (!PaymentRequestStatuses.AUTO_APPROVED.equals(getStatusCode())) {
                    SpringContext.getBean(PurapService.class).updateStatusAndStatusHistory(this, PurapConstants.PaymentRequestStatuses.DEPARTMENT_APPROVED);
                    populateDocumentForRouting();
                    SpringContext.getBean(PaymentRequestService.class).saveDocumentWithoutValidation(this);
                    return;
                }
            }
            // DOCUMENT DISAPPROVED
            else if (this.getDocumentHeader().getWorkflowDocument().stateIsDisapproved()) {
                String nodeName = SpringContext.getBean(WorkflowDocumentService.class).getCurrentRouteLevelName(getDocumentHeader().getWorkflowDocument());
                NodeDetails currentNode = NodeDetailEnum.getNodeDetailEnumByName(nodeName);
                if (ObjectUtils.isNotNull(currentNode)) {
                    String newStatusCode = currentNode.getDisapprovedStatusCode();
                    if ( (StringUtils.isBlank(newStatusCode)) && 
                         ( (StringUtils.isBlank(currentNode.getDisapprovedStatusCode())) && ( (PaymentRequestStatuses.INITIATE.equals(getStatusCode())) || (PaymentRequestStatuses.IN_PROCESS.equals(getStatusCode())) ) ) ) {
                        newStatusCode = PaymentRequestStatuses.CANCELLED_IN_PROCESS;
                    }
                    if (StringUtils.isNotBlank(newStatusCode)) {
                        SpringContext.getBean(PurapService.class).updateStatusAndStatusHistory(this, newStatusCode);
                        SpringContext.getBean(PaymentRequestService.class).saveDocumentWithoutValidation(this);
                        return;
                    }
                }
                // TODO PURAP/delyea - what to do in a disapproval where no status to set exists?
                logAndThrowRuntimeException("No status found to set for document being disapproved in node '" + nodeName + "'");
            }
            // DOCUMENT CANCELED
            else if (this.getDocumentHeader().getWorkflowDocument().stateIsCanceled()) {
                String currentNodeName = SpringContext.getBean(WorkflowDocumentService.class).getCurrentRouteLevelName(this.getDocumentHeader().getWorkflowDocument());
                NodeDetails currentNode = NodeDetailEnum.getNodeDetailEnumByName(currentNodeName);
                if (ObjectUtils.isNotNull(currentNode)) {
                    String cancelledStatusCode = currentNode.getDisapprovedStatusCode();
                    if (StringUtils.isNotBlank(cancelledStatusCode)) {
                        SpringContext.getBean(PurapService.class).updateStatusAndStatusHistory(this, cancelledStatusCode);
                        SpringContext.getBean(PaymentRequestService.class).saveDocumentWithoutValidation(this);
                        return;
                    }
                }
                // TODO PURAP/delyea - what to do in a cancel where no status to set exists?
                LOG.warn("No status found to set for document being disapproved in node '" + currentNodeName + "'");
            }
        }
        catch (WorkflowException e) {
            logAndThrowRuntimeException("Error saving routing data while saving document with id " + getDocumentNumber(), e);
        }
    }

    @Override
    public void doActionTaken(ActionTakenEventVO event) {
        super.doActionTaken(event);
        KualiWorkflowDocument workflowDocument = getDocumentHeader().getWorkflowDocument();
        try {
            String currentNode = workflowDocument.getNodeNames()[0];

            // everything in the below list requires correcting entries to be written to the GL
            if (NodeDetailEnum.getNodesRequiringCorrectingGeneralLedgerEntries().contains(currentNode)) {
                if (NodeDetailEnum.ACCOUNT_REVIEW.getName().equals(currentNode)) {
                    //FIXME this is not working right now becuase the document has already been saved before reaching this point...that is a problem :(
//                    SpringContext.getBean(PurapGeneralLedgerService.class).generateEntriesModifyPreq(this);
                }
            }
        }
        catch (WorkflowException e) {
            logAndThrowRuntimeException("Error saving routing data while saving document with id " + getDocumentNumber(), e);
        }
    }

    /**
     * @see org.kuali.module.purap.document.AccountsPayableDocumentBase#preProcessNodeChange(java.lang.String, java.lang.String)
     */
    public boolean processNodeChange(String newNodeName, String oldNodeName) {
        if (PaymentRequestStatuses.AUTO_APPROVED.equals(getStatusCode())) {
            // do nothing for an auto approval
            return false;
        }
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
        SpringContext.getBean(PaymentRequestService.class).saveDocumentWithoutValidation(this);
    }

    /**
     * 
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getItemClass()
     */
    @Override
    public Class getItemClass() {
        return PaymentRequestItem.class;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentIfPossible()
     */
    @Override
    public PurchaseOrderDocument getPurApSourceDocumentIfPossible() {
        return getPurchaseOrderDocument();
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentLabelIfPossible()
     */
    @Override
    public String getPurApSourceDocumentLabelIfPossible() {
        return SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByClass(PurchaseOrderDocument.class);
    }

    /**
     * Gets the purchaseOrderNotes attribute. 
     * @return Returns the purchaseOrderNotes.
     */
    public String getPurchaseOrderNotes() {
        
        ArrayList poNotes = SpringContext.getBean(PurchaseOrderService.class).getPurchaseOrderNotes(this.getPurchaseOrderIdentifier());
        
        if (poNotes.size() > 0) {
            return "Yes";
        }
        return "No";
    }

    /**
     * Sets the purchaseOrderNotes attribute value.
     * 
     * @param purchaseOrderNotes The purchaseOrderNotes to set.
     */
    public void setPurchaseOrderNotes(String purchaseOrderNotes) {
        this.purchaseOrderNotes = purchaseOrderNotes;
    }


    @Override
    public List<PaymentRequestView> getRelatedPaymentRequestViews() {
        return null;
    }
    
    public String getRecurringPaymentTypeCode() {
        return recurringPaymentTypeCode;
    }

    public void setRecurringPaymentTypeCode(String recurringPaymentTypeCode) {
        this.recurringPaymentTypeCode = recurringPaymentTypeCode;
    }

    /**
     * Get the total encumbered amount from the purchase order excluding 
     * below the line
     * @return Total cost excluding below the line
     */
    public KualiDecimal getItemTotalPoEncumbranceAmount() {
      //get total from po excluding below the line and inactive
      return this.getPurchaseOrderDocument().getTotalDollarAmount(false, false);
    }    
    
    public KualiDecimal getItemTotalPoEncumbranceAmountRelieved() {
        return getItemTotalPoEncumbranceAmountRelieved(false);
    }
    /**
     * 
     * This method is just a stub for now
     * @return
     */
    public KualiDecimal getItemTotalPoEncumbranceAmountRelieved(boolean includeBelowTheLine) {
        
        KualiDecimal total = KualiDecimal.ZERO;
        
        for (PaymentRequestItem item : (List<PaymentRequestItem>)getItems()) {
            ItemType it = item.getItemType();
            if(includeBelowTheLine || it.isItemTypeAboveTheLineIndicator()) {
                total = total.add(item.getPurchaseOrderItemEncumbranceRelievedAmount());
            }
        }
        return total;
    }
    
    /**
     * 
     * Get the total for just the line items
     * @return the line item total
     */
    public KualiDecimal getLineItemTotal() {
        return this.getTotalDollarAmountAboveLineItems();
    }
    
    
    /**
     * 
     * This method gets the grand total
     * @return
     */    
    public KualiDecimal getGrandTotal() {
        return this.getTotalDollarAmount();
    }
    
    /**
     * The total that was paid on the po excluding below the line
     * @return total paid
     * 
     */
    public KualiDecimal getItemTotalPoPaidAmount() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (PaymentRequestItem item : (List<PaymentRequestItem>)getItems()) {
            ItemType iT = item.getItemType();
            if(iT.isItemTypeAboveTheLineIndicator()) {
                KualiDecimal itemPaid = item.getPurchaseOrderItemPaidAmount();
                total=total.add(itemPaid);
            }
        }
        return total;
    }

    //Helper methods
    public String getAccountsPayableRequestCancelPersonName(){
        String personName = null;
        try {
            UniversalUser user = SpringContext.getBean(UniversalUserService.class).getUniversalUser(getAccountsPayableRequestCancelIdentifier());
            personName = user.getPersonName();
        }
        catch (UserNotFoundException unfe) {
            personName = "";
        }
        
        return personName;
    }

    /** 
     * This method is here due to a setter requirement by the htmlControlAttribute
     * @param amount
     */
    public void setItemTotalPoPaidAmount(KualiDecimal amount){
        //do nothing
    }

    /** 
     * This method is here due to a setter requirement by the htmlControlAttribute
     * @param amount
     */
    public void setItemTotalPoEncumbranceAmount(KualiDecimal amount) {
        //do nothing
    }    

    /** 
     * This method is here due to a setter requirement by the htmlControlAttribute
     * @param amount
     */
    public void setItemTotalPoEncumbranceAmountRelieved(KualiDecimal amount) {
        //do nothing
    }

    /**
     * A helper method for determining the route levels for a given document.
     * 
     * @param workflowDocument
     * @return List
     */
    protected List getCurrentRouteLevels(KualiWorkflowDocument workflowDocument) {
        try {
            return Arrays.asList(workflowDocument.getNodeNames());
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
    }

    
    public void updateExtendedPriceOnItems() {
        for (PaymentRequestItem item : (List<PaymentRequestItem>)this.getItems()) {
            if((ObjectUtils.isNull(item.getExtendedPrice())||(KualiDecimal.ZERO.compareTo(item.getExtendedPrice())==0)) &&
               item.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                KualiDecimal newExtendedPrice = item.calculateExtendedPrice();
                item.setExtendedPrice(newExtendedPrice);
            }
        }
    }
    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getSourceAccountingLineClass()
     */
//    @Override
//    public Class getSourceAccountingLineClass() {
//        return PaymentRequestAccount.class;
//    }
    
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
     * Gets the recurringPaymentType attribute. 
     * @return Returns the recurringPaymentType.
     */
    public RecurringPaymentType getRecurringPaymentType() {
        return recurringPaymentType;
    }

    /**
     * Sets the recurringPaymentType attribute value.
     * @param recurringPaymentType The recurringPaymentType to set.
     */
    public void setRecurringPaymentType(RecurringPaymentType recurringPaymentType) {
        this.recurringPaymentType = recurringPaymentType;
    }

    /**
     * Gets the paymentRequestCostSource attribute. 
     * @return Returns the paymentRequestCostSource.
     */
    public PurchaseOrderCostSource getPaymentRequestCostSource() {
        return paymentRequestCostSource;
    }

    /**
     * Sets the paymentRequestCostSource attribute value.
     * @param paymentRequestCostSource The paymentRequestCostSource to set.
     */
    public void setPaymentRequestCostSource(PurchaseOrderCostSource paymentRequestCostSource) {
        this.paymentRequestCostSource = paymentRequestCostSource;
    }

    /**
     * @see org.kuali.module.purap.document.AccountsPayableDocumentBase#getPoDocumentTypeForAccountsPayableDocumentApprove()
     */
    public String getPoDocumentTypeForAccountsPayableDocumentApprove() {
        return PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT;
    }

    /**
     * 
     * @see org.kuali.module.purap.document.AccountsPayableDocumentBase#getInitialAmount()
     */
    public KualiDecimal getInitialAmount(){
        return this.getVendorInvoiceAmount();
    }
}