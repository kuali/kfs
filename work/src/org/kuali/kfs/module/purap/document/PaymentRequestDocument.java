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

import org.kuali.core.bo.Campus;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.bo.PaymentRequestStatus;
import org.kuali.module.purap.bo.PaymentRequestStatusHistory;


/**
 * Payment Request Document
 */
public class PaymentRequestDocument extends AccountsPayableDocumentBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestDocument.class);
    private Integer purchaseOrderEncumbranceFiscalYear;
    private Integer purchaseOrderIdentifier;
    private String purchaseOrderClassificationTypeDescription;
    private Date invoiceDate;
    private String invoiceNumber;
    private KualiDecimal vendorInvoiceAmount;
    private String vendorPaymentTermsCode;
    private String vendorShippingPaymentTermsCode;
    private String vendorCustomerNumber;
    private Date paymentRequestPayDate;
    private String paymentRequestCostSourceCode;
    private boolean paymentRequestedCancelIndicator;
    private boolean paymentAttachmentIndicator;
    private boolean immediatePaymentIndicator;
    private boolean paymentHoldIndicator;
    private String paymentNoteLine1Text;
    private String paymentNoteLine2Text;
    private String paymentNoteLine3Text;
    private String paymentSpecialHandlingInstructionLine1Text;
    private String paymentSpecialHandlingInstructionLine2Text;
    private String paymentSpecialHandlingInstructionLine3Text;
    private Date paymentPaidDate;
    private boolean paymentRequestElectronicInvoiceIndicator;
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String vendorName;
    private String vendorLine1Address;
    private String vendorLine2Address;
    private String vendorCityName;
    private String vendorStateCode;
    private String vendorPostalCode;
    private String vendorCountryCode;
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

    private PaymentRequestStatus paymentRequestStatus;
    private Campus processingCampus;

    /**
	 * Default constructor.
	 */
	public PaymentRequestDocument() {
        super();
    }

    /**
     * Gets the purchaseOrderEncumbranceFiscalYear attribute. 
     * @return Returns the purchaseOrderEncumbranceFiscalYear.
     */
    public Integer getPurchaseOrderEncumbranceFiscalYear() {
        return purchaseOrderEncumbranceFiscalYear;
    }





    /**
     * Sets the purchaseOrderEncumbranceFiscalYear attribute value.
     * @param purchaseOrderEncumbranceFiscalYear The purchaseOrderEncumbranceFiscalYear to set.
     */
    public void setPurchaseOrderEncumbranceFiscalYear(Integer purchaseOrderEncumbranceFiscalYear) {
        this.purchaseOrderEncumbranceFiscalYear = purchaseOrderEncumbranceFiscalYear;
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
     * Gets the vendorCustomerNumber attribute.
     * 
     * @return Returns the vendorCustomerNumber
     * 
     */
    public String getVendorCustomerNumber() { 
        return vendorCustomerNumber;
    }

    /**
     * Sets the vendorCustomerNumber attribute.
     * 
     * @param vendorCustomerNumber The vendorCustomerNumber to set.
     * 
     */
    public void setVendorCustomerNumber(String vendorCustomerNumber) {
        this.vendorCustomerNumber = vendorCustomerNumber;
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


    /**
     * Gets the paymentNoteLine1Text attribute.
     * 
     * @return Returns the paymentNoteLine1Text
     * 
     */
    public String getPaymentNoteLine1Text() { 
        return paymentNoteLine1Text;
    }

    /**
     * Sets the paymentNoteLine1Text attribute.
     * 
     * @param paymentNoteLine1Text The paymentNoteLine1Text to set.
     * 
     */
    public void setPaymentNoteLine1Text(String paymentNoteLine1Text) {
        this.paymentNoteLine1Text = paymentNoteLine1Text;
    }


    /**
     * Gets the paymentNoteLine2Text attribute.
     * 
     * @return Returns the paymentNoteLine2Text
     * 
     */
    public String getPaymentNoteLine2Text() { 
        return paymentNoteLine2Text;
    }

    /**
     * Sets the paymentNoteLine2Text attribute.
     * 
     * @param paymentNoteLine2Text The paymentNoteLine2Text to set.
     * 
     */
    public void setPaymentNoteLine2Text(String paymentNoteLine2Text) {
        this.paymentNoteLine2Text = paymentNoteLine2Text;
    }


    /**
     * Gets the paymentNoteLine3Text attribute.
     * 
     * @return Returns the paymentNoteLine3Text
     * 
     */
    public String getPaymentNoteLine3Text() { 
        return paymentNoteLine3Text;
    }

    /**
     * Sets the paymentNoteLine3Text attribute.
     * 
     * @param paymentNoteLine3Text The paymentNoteLine3Text to set.
     * 
     */
    public void setPaymentNoteLine3Text(String paymentNoteLine3Text) {
        this.paymentNoteLine3Text = paymentNoteLine3Text;
    }


    /**
     * Gets the paymentSpecialHandlingInstructionLine1Text attribute.
     * 
     * @return Returns the paymentSpecialHandlingInstructionLine1Text
     * 
     */
    public String getPaymentSpecialHandlingInstructionLine1Text() { 
        return paymentSpecialHandlingInstructionLine1Text;
    }

    /**
     * Sets the paymentSpecialHandlingInstructionLine1Text attribute.
     * 
     * @param paymentSpecialHandlingInstructionLine1Text The paymentSpecialHandlingInstructionLine1Text to set.
     * 
     */
    public void setPaymentSpecialHandlingInstructionLine1Text(String paymentSpecialHandlingInstructionLine1Text) {
        this.paymentSpecialHandlingInstructionLine1Text = paymentSpecialHandlingInstructionLine1Text;
    }


    /**
     * Gets the paymentSpecialHandlingInstructionLine2Text attribute.
     * 
     * @return Returns the paymentSpecialHandlingInstructionLine2Text
     * 
     */
    public String getPaymentSpecialHandlingInstructionLine2Text() { 
        return paymentSpecialHandlingInstructionLine2Text;
    }

    /**
     * Sets the paymentSpecialHandlingInstructionLine2Text attribute.
     * 
     * @param paymentSpecialHandlingInstructionLine2Text The paymentSpecialHandlingInstructionLine2Text to set.
     * 
     */
    public void setPaymentSpecialHandlingInstructionLine2Text(String paymentSpecialHandlingInstructionLine2Text) {
        this.paymentSpecialHandlingInstructionLine2Text = paymentSpecialHandlingInstructionLine2Text;
    }


    /**
     * Gets the paymentSpecialHandlingInstructionLine3Text attribute.
     * 
     * @return Returns the paymentSpecialHandlingInstructionLine3Text
     * 
     */
    public String getPaymentSpecialHandlingInstructionLine3Text() { 
        return paymentSpecialHandlingInstructionLine3Text;
    }

    /**
     * Sets the paymentSpecialHandlingInstructionLine3Text attribute.
     * 
     * @param paymentSpecialHandlingInstructionLine3Text The paymentSpecialHandlingInstructionLine3Text to set.
     * 
     */
    public void setPaymentSpecialHandlingInstructionLine3Text(String paymentSpecialHandlingInstructionLine3Text) {
        this.paymentSpecialHandlingInstructionLine3Text = paymentSpecialHandlingInstructionLine3Text;
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
     * Gets the vendorHeaderGeneratedIdentifier attribute.
     * 
     * @return Returns the vendorHeaderGeneratedIdentifier
     * 
     */
    public Integer getVendorHeaderGeneratedIdentifier() { 
        return vendorHeaderGeneratedIdentifier;
    }

    /**
     * Sets the vendorHeaderGeneratedIdentifier attribute.
     * 
     * @param vendorHeaderGeneratedIdentifier The vendorHeaderGeneratedIdentifier to set.
     * 
     */
    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }


    /**
     * Gets the vendorDetailAssignedIdentifier attribute.
     * 
     * @return Returns the vendorDetailAssignedIdentifier
     * 
     */
    public Integer getVendorDetailAssignedIdentifier() { 
        return vendorDetailAssignedIdentifier;
    }

    /**
     * Sets the vendorDetailAssignedIdentifier attribute.
     * 
     * @param vendorDetailAssignedIdentifier The vendorDetailAssignedIdentifier to set.
     * 
     */
    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }


    /**
     * Gets the vendorName attribute.
     * 
     * @return Returns the vendorName
     * 
     */
    public String getVendorName() { 
        return vendorName;
    }

    /**
     * Sets the vendorName attribute.
     * 
     * @param vendorName The vendorName to set.
     * 
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }
  
    /**
     * Gets the vendorLine1Address attribute.
     * 
     * @return Returns the vendorLine1Address
     * 
     */
    public String getVendorLine1Address() { 
        return vendorLine1Address;
    }

    /**
     * Sets the vendorLine1Address attribute.
     * 
     * @param vendorLine1Address The vendorLine1Address to set.
     * 
     */
    public void setVendorLine1Address(String vendorLine1Address) {
        this.vendorLine1Address = vendorLine1Address;
    }


    /**
     * Gets the vendorLine2Address attribute.
     * 
     * @return Returns the vendorLine2Address
     * 
     */
    public String getVendorLine2Address() { 
        return vendorLine2Address;
    }

    /**
     * Sets the vendorLine2Address attribute.
     * 
     * @param vendorLine2Address The vendorLine2Address to set.
     * 
     */
    public void setVendorLine2Address(String vendorLine2Address) {
        this.vendorLine2Address = vendorLine2Address;
    }

    /**
     * Gets the vendorCityName attribute.
     * 
     * @return Returns the vendorCityName
     * 
     */
    public String getVendorCityName() { 
        return vendorCityName;
    }

    /**
     * Sets the vendorCityName attribute.
     * 
     * @param vendorCityName The vendorCityName to set.
     * 
     */
    public void setVendorCityName(String vendorCityName) {
        this.vendorCityName = vendorCityName;
    }


    /**
     * Gets the vendorStateCode attribute.
     * 
     * @return Returns the vendorStateCode
     * 
     */
    public String getVendorStateCode() { 
        return vendorStateCode;
    }

    /**
     * Sets the vendorStateCode attribute.
     * 
     * @param vendorStateCode The vendorStateCode to set.
     * 
     */
    public void setVendorStateCode(String vendorStateCode) {
        this.vendorStateCode = vendorStateCode;
    }


    /**
     * Gets the vendorPostalCode attribute.
     * 
     * @return Returns the vendorPostalCode
     * 
     */
    public String getVendorPostalCode() { 
        return vendorPostalCode;
    }

    /**
     * Sets the vendorPostalCode attribute.
     * 
     * @param vendorPostalCode The vendorPostalCode to set.
     * 
     */
    public void setVendorPostalCode(String vendorPostalCode) {
        this.vendorPostalCode = vendorPostalCode;
    }


    /**
     * Gets the vendorCountryCode attribute.
     * 
     * @return Returns the vendorCountryCode
     * 
     */
    public String getVendorCountryCode() { 
        return vendorCountryCode;
    }

    /**
     * Sets the vendorCountryCode attribute.
     * 
     * @param vendorCountryCode The vendorCountryCode to set.
     * 
     */
    public void setVendorCountryCode(String vendorCountryCode) {
        this.vendorCountryCode = vendorCountryCode;
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
     * Gets the paymentRequestStatus attribute.
     * 
     * @return Returns the paymentRequestStatus
     * 
     */
    public PaymentRequestStatus getPaymentRequestStatus() { 
        return paymentRequestStatus;
    }

    /**
     * Sets the paymentRequestStatus attribute.
     * 
     * @param paymentRequestStatus The paymentRequestStatus to set.
     * 
     */
    public void setPaymentRequestStatus(PaymentRequestStatus paymentRequestStatus) {
        this.paymentRequestStatus = paymentRequestStatus;
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
    
    public void refreshAllReferences() {
        super.refreshAllReferences();
    }
    
    /**
     * Perform logic needed to initiate PREQ Document
     */
    public void initiateDocument() {

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

}
