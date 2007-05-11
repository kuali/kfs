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
import java.util.List;

import org.kuali.core.bo.Campus;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.bo.PaymentRequestAccount;
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.bo.PaymentRequestStatusHistory;
import org.kuali.module.purap.bo.PaymentRequestView;
import org.kuali.module.vendor.bo.PaymentTermType;
import org.kuali.module.vendor.bo.ShippingPaymentTerms;


/**
 * Payment Request Document
 */
public class PaymentRequestDocument extends AccountsPayableDocumentBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestDocument.class);

    private Integer purchaseOrderIdentifier;
    private String purchaseOrderClassificationTypeDescription;
    private Date invoiceDate;
    private String invoiceNumber;
    private KualiDecimal vendorInvoiceAmount;
    private String vendorPaymentTermsCode;
    private String vendorShippingPaymentTermsCode;
    private String vendorShippingTitleCode;  //not persisted in DB
    private Date paymentRequestPayDate;
    private String paymentRequestCostSourceCode;
    private boolean paymentRequestedCancelIndicator;
    private boolean paymentAttachmentIndicator;
    private boolean immediatePaymentIndicator;
    private boolean paymentHoldIndicator;
    private String checkStubNoteLine1Text;
    private String checkStubNoteLine2Text;
    private String checkStubNoteLine3Text;
    private String specialHandlingInstructionLine1Text;
    private String specialHandlingInstructionLine2Text;
    private String specialHandlingInstructionLine3Text;
    private Date paymentPaidDate;
    private boolean paymentRequestElectronicInvoiceIndicator;
    private Date paymentExtractedDate;
    private String accountsPayableProcessorIdentifier;
    private String accountsPayableRequestCancelIdentifier;
    private String accountsPayableHoldIdentifier;
    private String processingCampusCode;
    private Date accountsPayableApprovalDate;
    private Integer originalVendorHeaderGeneratedIdentifier;
    private Integer originalVendorDetailAssignedIdentifier;
    private Integer alternateVendorHeaderGeneratedIdentifier;
    private Integer alternateVendorDetailAssignedIdentifier;
    private boolean continuationAccountIndicator;
    private String purchaseOrderNotes;
    private Date purchaseOrderEndDate;

    private Campus processingCampus;
    private PurchaseOrderDocument purchaseOrderDocument;
    private PaymentTermType vendorPaymentTerms;
    private ShippingPaymentTerms vendorShippingPaymentTerms;
   
    // private ShippingTitle vendorShippingTitle;
    // private RecurringPaymentFrequency recurringPaymentFrequency;

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
        // TODO Auto-generated method stub
        return true;
    }


    /**
     * Gets the purchaseOrderIdentifier attribute. 
     * @return Returns the purchaseOrderIdentifier.
     */
    public Integer getPurchaseOrderIdentifier() {
        return purchaseOrderIdentifier;
    }

    /**
     * Sets the purchaseOrderIdentifier attribute value.
     * @param purchaseOrderIdentifier The purchaseOrderIdentifier to set.
     */
    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
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


    /**
     * Gets the paymentHoldIndicator attribute.
     * 
     * @return Returns the paymentHoldIndicator
     * 
     */
    public boolean getPaymentHoldIndicator() { 
        return paymentHoldIndicator;
    }

    /**
     * Sets the paymentHoldIndicator attribute.
     * 
     * @param paymentHoldIndicator The paymentHoldIndicator to set.
     * 
     */
    public void setPaymentHoldIndicator(boolean paymentHoldIndicator) {
        this.paymentHoldIndicator = paymentHoldIndicator;
    }


    public String getCheckStubNoteLine1Text() {
        return checkStubNoteLine1Text;
    }


    public void setCheckStubNoteLine1Text(String checkStubNoteLine1Text) {
        this.checkStubNoteLine1Text = checkStubNoteLine1Text;
    }


    public String getCheckStubNoteLine2Text() {
        return checkStubNoteLine2Text;
    }


    public void setCheckStubNoteLine2Text(String checkStubNoteLine2Text) {
        this.checkStubNoteLine2Text = checkStubNoteLine2Text;
    }


    public String getCheckStubNoteLine3Text() {
        return checkStubNoteLine3Text;
    }


    public void setCheckStubNoteLine3Text(String checkStubNoteLine3Text) {
        this.checkStubNoteLine3Text = checkStubNoteLine3Text;
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
     * Gets the paymentExtractedDate attribute.
     * 
     * @return Returns the paymentExtractedDate
     * 
     */
    public Date getPaymentExtractedDate() { 
        return paymentExtractedDate;
    }

    /**
     * Sets the paymentExtractedDate attribute.
     * 
     * @param paymentExtractedDate The paymentExtractedDate to set.
     * 
     */
    public void setPaymentExtractedDate(Date paymentExtractedDate) {
        this.paymentExtractedDate = paymentExtractedDate;
    }


    /**
     * Gets the accountsPayableProcessorIdentifier attribute.
     * 
     * @return Returns the accountsPayableProcessorIdentifier
     * 
     */
    public String getAccountsPayableProcessorIdentifier() { 
        return accountsPayableProcessorIdentifier;
    }

    /**
     * Sets the accountsPayableProcessorIdentifier attribute.
     * 
     * @param accountsPayableProcessorIdentifier The accountsPayableProcessorIdentifier to set.
     * 
     */
    public void setAccountsPayableProcessorIdentifier(String accountsPayableProcessorIdentifier) {
        this.accountsPayableProcessorIdentifier = accountsPayableProcessorIdentifier;
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
     * Gets the accountsPayableHoldIdentifier attribute.
     * 
     * @return Returns the accountsPayableHoldIdentifier
     * 
     */
    public String getAccountsPayableHoldIdentifier() { 
        return accountsPayableHoldIdentifier;
    }

    /**
     * Sets the accountsPayableHoldIdentifier attribute.
     * 
     * @param accountsPayableHoldIdentifier The accountsPayableHoldIdentifier to set.
     * 
     */
    public void setAccountsPayableHoldIdentifier(String accountsPayableHoldIdentifier) {
        this.accountsPayableHoldIdentifier = accountsPayableHoldIdentifier;
    }


    /**
     * Gets the processingCampusCode attribute.
     * 
     * @return Returns the processingCampusCode
     * 
     */
    public String getProcessingCampusCode() { 
        return processingCampusCode;
    }

    /**
     * Sets the processingCampusCode attribute.
     * 
     * @param processingCampusCode The processingCampusCode to set.
     * 
     */
    public void setProcessingCampusCode(String processingCampusCode) {
        this.processingCampusCode = processingCampusCode;
    }


    /**
     * Gets the accountsPayableApprovalDate attribute.
     * 
     * @return Returns the accountsPayableApprovalDate
     * 
     */
    public Date getAccountsPayableApprovalDate() { 
        return accountsPayableApprovalDate;
    }

    /**
     * Sets the accountsPayableApprovalDate attribute.
     * 
     * @param accountsPayableApprovalDate The accountsPayableApprovalDate to set.
     * 
     */
    public void setAccountsPayableApprovalDate(Date accountsPayableApprovalDate) {
        this.accountsPayableApprovalDate = accountsPayableApprovalDate;
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
     * Gets the continuationAccountIndicator attribute. 
     * @return Returns the continuationAccountIndicator.
     */
    public boolean isContinuationAccountIndicator() {
        return continuationAccountIndicator;
    }

    /**
     * Sets the continuationAccountIndicator attribute value.
     * @param continuationAccountIndicator The continuationAccountIndicator to set.
     */
    public void setContinuationAccountIndicator(boolean continuationAccountIndicator) {
        this.continuationAccountIndicator = continuationAccountIndicator;
    }

    /**
     * Gets the purchaseOrderDocument attribute. 
     * @return Returns the purchaseOrderDocument.
     */
    public PurchaseOrderDocument getPurchaseOrderDocument() {
        return purchaseOrderDocument;
    }

    /**
     * Sets the purchaseOrderDocument attribute value.
     * @param purchaseOrderDocument The purchaseOrderDocument to set.
     */
    public void setPurchaseOrderDocument(PurchaseOrderDocument purchaseOrderDocument) {
        this.purchaseOrderDocument = purchaseOrderDocument;
    }

    /**
     * Gets the processingCampus attribute.
     * 
     * @return Returns the processingCampus
     * 
     */
    public Campus getProcessingCampus() { 
        return processingCampus;
    }

    /**
     * Sets the processingCampus attribute.
     * 
     * @param processingCampus The processingCampus to set.
     * @deprecated
     */
    public void setProcessingCampus(Campus processingCampus) {
        this.processingCampus = processingCampus;
    }
    
   
    /**
     * Gets the recurringPaymentFrequency attribute. 
     * @return Returns the recurringPaymentFrequency.
     */
   /*
    public RecurringPaymentFrequency getRecurringPaymentFrequency() {
        return recurringPaymentFrequency;
    }

*/
    /**
     * Sets the recurringPaymentFrequency attribute value.
     * @param recurringPaymentFrequency The recurringPaymentFrequency to set.
     */
    /*
    public void setRecurringPaymentFrequency(RecurringPaymentFrequency recurringPaymentFrequency) {
        this.recurringPaymentFrequency = recurringPaymentFrequency;
    }
*/

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
        if (ObjectUtils.isNotNull(this.purchaseOrderDocument)) {
            return this.purchaseOrderDocument.getVendorShippingTitleCode();
        }
        return vendorShippingTitleCode;
    }


    public void setVendorShippingTitleCode(String vendorShippingTitleCode) {
        this.vendorShippingTitleCode = vendorShippingTitleCode;
    }
    
    public void refreshAllReferences() {
        super.refreshAllReferences();
    }
    
    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#addToStatusHistories(java.lang.String, java.lang.String)
     */
    public void addToStatusHistories( String oldStatus, String newStatus, Note statusHistoryNote ) {
        PaymentRequestStatusHistory prsh = new PaymentRequestStatusHistory( oldStatus, newStatus );
        this.addStatusHistoryNote( prsh, statusHistoryNote );
        this.getStatusHistories().add( prsh );
    }
    
    /**
     * Perform logic needed to initiate PREQ Document
     */
    public void initiateDocument() {
        LOG.debug("initiateDocument() started");
        this.setStatusCode( PurapConstants.PaymentRequestStatuses.INITIATE );
  
        //TODO: Change this one:
        this.setAccountsPayableProcessorIdentifier("TBD");
        UniversalUser currentUser = (UniversalUser)GlobalVariables.getUserSession().getUniversalUser();
        this.setAccountsPayableProcessorIdentifier(currentUser.getPersonUniversalIdentifier());
        // paymentRequest.setProcessedCampusCode(u.getCampusCd());
        //paymentRequest.setAccountsPayableProcessorId(u.getId());
        //this.setStatusCode( PurapConstants.PaymentRequestStatuses.IN_PROCESS )
       // this.setInitialized(true);
        this.refreshAllReferences();
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
  
    public void populatePaymentRequestFromPurchaseOrder(PurchaseOrderDocument po) {
        this.setPurchaseOrderDocument(po);
        this.setPurchaseOrderIdentifier(po.getPurapDocumentIdentifier());
        //this.purchaseOrderEncumbranceFiscalYear = po.getEncumbranceFiscalYear();
        
        //this.requisitionNumber = po.getRequisitionId();
       // this.setPurchaseOrderEncumbranceFiscalYear(po.getPostingYear());
        this.setPostingYear(po.getPostingYear());
        this.setVendorCustomerNumber(po.getVendorCustomerNumber());
        //this.costSource = po.getCostSource();
        this.setPaymentRequestCostSourceCode(po.getPurchaseOrderCostSourceCode());
        this.setVendorShippingPaymentTerms(po.getVendorShippingPaymentTerms());
        if(this.getVendorShippingPaymentTerms()!=null) {
            this.setVendorShippingPaymentTermsCode(this.getVendorShippingPaymentTerms().getVendorShippingPaymentTermsCode());
        }
        
        this.setRecurringPaymentType(po.getRecurringPaymentType());
        if(this.getRecurringPaymentType()!=null) {
            this.setRecurringPaymentTypeCode(this.getRecurringPaymentType().getRecurringPaymentTypeCode());
        }
        
        //this.setRecurringPaymentTypeCode(po.getRecurringPaymentTypeCode());
        this.setVendorHeaderGeneratedIdentifier(po.getVendorHeaderGeneratedIdentifier());
        this.setVendorDetailAssignedIdentifier(po.getVendorDetailAssignedIdentifier());
        this.setVendorName(po.getVendorName());
        //this.vendorRemitLine1Address = po.getVendorLine1Address();
        this.setVendorLine1Address(po.getVendorLine1Address());
        this.setVendorLine2Address(po.getVendorLine2Address());
        //this.vendorRemitLine3Address = po.getVendorLine3Address();
        this.setVendorCityName(po.getVendorCityName());
        this.setVendorStateCode(po.getVendorStateCode());
        this.setVendorPostalCode(po.getVendorPostalCode());
        this.setVendorCountryCode(po.getVendorCountryCode());
        if ((po.getVendorPaymentTerms() == null) || ("".equals(po.getVendorPaymentTerms().getVendorPaymentTermsCode())) ) {
        //if ( (po.getVendorPaymentTerms() == null) || ("".equals(po.getVendorPaymentTermsCode())) ) {
          //this.vendorPaymentTerms = new PaymentTermsType();
          //this.vendorPaymentTerms.setCode("");
            this.setVendorPaymentTerms(new PaymentTermType());
            this.vendorPaymentTerms.setVendorPaymentTermsCode("");
        } else {
          this.setVendorPaymentTerms(po.getVendorPaymentTerms());
        }

        
        /*
        items.clear();
        if (po.getItems() != null) {
          for (Iterator i = po.getItems().iterator(); i.hasNext(); ) {
            //Jira kulapp 1258: only save the first 500 characters of the description to the item
            PurchaseOrderItem poi = (PurchaseOrderItem) i.next();
            //Jira kulapp 1383, we should only copy the active po items.
            if (poi.getActive().booleanValue()) {
              String desc = poi.getDescription();
              if (desc != null &&(desc.length() > 500)) {
                poi.setDescription(desc.substring(0, 500));
              }
              items.add(new PaymentRequestItem(this, poi));
            }//end of if (poi.getActive()..      
          }//end of for (Iterator i = po.getItems()...
        }//end of if (po.getItems() != null) 
        */
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
     * 
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getItemClass()
     */
    @Override
    public Class getItemClass() {
        return PaymentRequestItem.class;
    }


    /**
     * Gets the purchaseOrderNotes attribute. 
     * @return Returns the purchaseOrderNotes.
     */
    public String getPurchaseOrderNotes() {
        ArrayList poNotes = SpringServiceLocator.getNoteService().getByRemoteObjectId((this.getPurchaseOrderIdentifier()).toString());
        
        if (poNotes.size() > 0){
            return "Yes";
            
        }else{
            
        }
        return "No";
    }

    /**
     * Sets the purchaseOrderNotes attribute value.
     * @param purchaseOrderNotes The purchaseOrderNotes to set.
     */
    public void setPurchaseOrderNotes(String purchaseOrderNotes) {
        this.purchaseOrderNotes = purchaseOrderNotes;
    }


    @Override
    public List<PaymentRequestView> getRelatedPaymentRequestViews() {
        return null;
    }
    
    /**
     * Gets the purchaseOrderEndDate attribute. 
     * @return Returns the purchaseOrderEndDate.
     */

    public Date getPurchaseOrderEndDate() {
        return purchaseOrderEndDate;
    }

    /**
     * Gets the DisplayInitiateTab attribute for JSP 
     * @return Returns the DisplayInitiateTab.
     */
  /*
    public boolean isPaymentRequestInitiated() { 
        LOG.info("isPaymentRequestInitiated() started");
        String stat = this.getStatusCode();
        boolean bol = StringUtils.equals(this.getStatusCode(),PurapConstants.PaymentRequestStatuses.INITIATE);
        return bol;
        //return (StringUtils.equals(this.getStatusCode(),PurapConstants.PaymentRequestStatuses.INITIATE));
      } 
*/
    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getSourceAccountingLineClass()
     */
//    @Override
//    public Class getSourceAccountingLineClass() {
//        return PaymentRequestAccount.class;
//    }
    
}
