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
package org.kuali.module.purap.fixtures;

import java.sql.Date;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.document.PaymentRequestDocument;

public enum PaymentRequestDocumentFixture {

    PREQ_ONLY_REQUIRED_FIELDS(
            SpringContext.getBean(DateTimeService.class).getCurrentSqlDate(),   //invoiceDate
            "123456",   //invoiceNumber,
            new KualiDecimal(100),  //vendorInvoiceAmount,
            "00N10",    //vendorPaymentTermsCode,
            "CL",   //vendorShippingPaymentTermsCode,
            SpringContext.getBean(DateTimeService.class).getCurrentSqlDate(),   //paymentRequestPayDate,
            "EST",  //paymentRequestCostSourceCode,
            false,  //paymentRequestedCancelIndicator,
            false,  //paymentAttachmentIndicator,
            false,  //immediatePaymentIndicator,
            null,   //specialHandlingInstructionLine1Text,
            null,   //specialHandlingInstructionLine2Text,
            null,   //specialHandlingInstructionLine3Text,
            null,   //paymentPaidDate,
            false,  //paymentRequestElectronicInvoiceIndicator,
            null,   //accountsPayableRequestCancelIdentifier,
            1010,   //originalVendorHeaderGeneratedIdentifier,
            2,      //originalVendorDetailAssignedIdentifier,
            null,           //alternateVendorHeaderGeneratedIdentifier,
            null,           //alternateVendorDetailAssignedIdentifier,
            null,           //purchaseOrderNotes,
            null,           //recurringPaymentTypeCode,            
            PurchasingAccountsPayableDocumentFixture.PREQ_ONLY_REQUIRED_FIELDS,  // purapDocumentFixture
            AccountsPayableDocumentFixture.PREQ_ONLY_REQUIRED_FIELDS,             // apDocumentFixture
            new PaymentRequestItemFixture[] {PaymentRequestItemFixture.PREQ_QTY_UNRESTRICTED_ITEM_1} // requisitionItemMultiFixtures
    );
            
    public final Date invoiceDate;
    public final String invoiceNumber;
    public final KualiDecimal vendorInvoiceAmount;
    public final String vendorPaymentTermsCode;
    public final String vendorShippingPaymentTermsCode;
    public final Date paymentRequestPayDate;
    public final String paymentRequestCostSourceCode;
    public final boolean paymentRequestedCancelIndicator;
    public final boolean paymentAttachmentIndicator;
    public final boolean immediatePaymentIndicator;
    public final String specialHandlingInstructionLine1Text;
    public final String specialHandlingInstructionLine2Text;
    public final String specialHandlingInstructionLine3Text;
    public final Date paymentPaidDate;
    public final boolean paymentRequestElectronicInvoiceIndicator;
    public final String accountsPayableRequestCancelIdentifier;
    public final Integer originalVendorHeaderGeneratedIdentifier;
    public final Integer originalVendorDetailAssignedIdentifier;
    public final Integer alternateVendorHeaderGeneratedIdentifier;
    public final Integer alternateVendorDetailAssignedIdentifier;
    public final String purchaseOrderNotes;
    public final String recurringPaymentTypeCode;

    private PurchasingAccountsPayableDocumentFixture purapDocumentFixture;
    private AccountsPayableDocumentFixture apDocumentFixture;
    private PaymentRequestItemFixture[] paymentRequestItemFixtures;
    
    private PaymentRequestDocumentFixture(
            Date invoiceDate,
            String invoiceNumber,
            KualiDecimal vendorInvoiceAmount,
            String vendorPaymentTermsCode,
            String vendorShippingPaymentTermsCode,
            Date paymentRequestPayDate,
            String paymentRequestCostSourceCode,
            boolean paymentRequestedCancelIndicator,
            boolean paymentAttachmentIndicator,
            boolean immediatePaymentIndicator,
            String specialHandlingInstructionLine1Text,
            String specialHandlingInstructionLine2Text,
            String specialHandlingInstructionLine3Text,
            Date paymentPaidDate,
            boolean paymentRequestElectronicInvoiceIndicator,
            String accountsPayableRequestCancelIdentifier,
            Integer originalVendorHeaderGeneratedIdentifier,
            Integer originalVendorDetailAssignedIdentifier,
            Integer alternateVendorHeaderGeneratedIdentifier,
            Integer alternateVendorDetailAssignedIdentifier,
            String purchaseOrderNotes,
            String recurringPaymentTypeCode,            
            PurchasingAccountsPayableDocumentFixture purapDocumentFixture,
            AccountsPayableDocumentFixture apDocumentFixture,
            PaymentRequestItemFixture[] paymentRequestItemFixtures) {
        
        this.invoiceDate = invoiceDate;
        this.invoiceNumber = invoiceNumber;
        this.vendorInvoiceAmount = vendorInvoiceAmount;
        this.vendorPaymentTermsCode = vendorPaymentTermsCode;
        this.vendorShippingPaymentTermsCode = vendorShippingPaymentTermsCode;
        this.paymentRequestPayDate = paymentRequestPayDate;
        this.paymentRequestCostSourceCode = paymentRequestCostSourceCode;
        this.paymentRequestedCancelIndicator = paymentRequestedCancelIndicator;
        this.paymentAttachmentIndicator = paymentAttachmentIndicator;
        this.immediatePaymentIndicator = immediatePaymentIndicator;
        this.specialHandlingInstructionLine1Text = specialHandlingInstructionLine1Text;
        this.specialHandlingInstructionLine2Text = specialHandlingInstructionLine2Text;
        this.specialHandlingInstructionLine3Text = specialHandlingInstructionLine3Text;
        this.paymentPaidDate = paymentPaidDate;
        this.paymentRequestElectronicInvoiceIndicator = paymentRequestElectronicInvoiceIndicator;
        this.accountsPayableRequestCancelIdentifier = accountsPayableRequestCancelIdentifier;
        this.originalVendorHeaderGeneratedIdentifier = originalVendorHeaderGeneratedIdentifier;
        this.originalVendorDetailAssignedIdentifier = originalVendorDetailAssignedIdentifier;
        this.alternateVendorHeaderGeneratedIdentifier = alternateVendorHeaderGeneratedIdentifier;
        this.alternateVendorDetailAssignedIdentifier = alternateVendorDetailAssignedIdentifier;
        this.purchaseOrderNotes = purchaseOrderNotes;
        this.recurringPaymentTypeCode = recurringPaymentTypeCode;

        this.purapDocumentFixture = purapDocumentFixture;
        this.apDocumentFixture = apDocumentFixture;     
        this.paymentRequestItemFixtures = paymentRequestItemFixtures;
    }

    public PaymentRequestDocument createPaymentRequestDocument() {
        
        PaymentRequestDocument doc = apDocumentFixture.createPaymentRequestDocument(purapDocumentFixture);
        
        doc.setInvoiceDate(this.invoiceDate);
        doc.setInvoiceNumber(this.invoiceNumber);
        doc.setVendorInvoiceAmount(this.vendorInvoiceAmount);
        doc.setVendorPaymentTermsCode(this.vendorPaymentTermsCode);
        doc.setVendorShippingPaymentTermsCode(this.vendorShippingPaymentTermsCode);
        doc.setPaymentRequestPayDate(this.paymentRequestPayDate);
        doc.setPaymentRequestCostSourceCode(this.paymentRequestCostSourceCode);
        doc.setPaymentRequestedCancelIndicator(this.paymentRequestedCancelIndicator);
        doc.setPaymentAttachmentIndicator(this.paymentAttachmentIndicator);
        doc.setImmediatePaymentIndicator(this.immediatePaymentIndicator);
        doc.setSpecialHandlingInstructionLine1Text(this.specialHandlingInstructionLine1Text);
        doc.setSpecialHandlingInstructionLine2Text(this.specialHandlingInstructionLine2Text);
        doc.setSpecialHandlingInstructionLine3Text(this.specialHandlingInstructionLine3Text);
        doc.setPaymentPaidDate(this.paymentPaidDate);
        doc.setPaymentRequestElectronicInvoiceIndicator(this.paymentRequestElectronicInvoiceIndicator);
        doc.setAccountsPayableRequestCancelIdentifier(this.accountsPayableRequestCancelIdentifier);
        doc.setOriginalVendorHeaderGeneratedIdentifier(this.originalVendorHeaderGeneratedIdentifier);
        doc.setOriginalVendorDetailAssignedIdentifier(this.originalVendorDetailAssignedIdentifier);
        doc.setAlternateVendorHeaderGeneratedIdentifier(this.alternateVendorHeaderGeneratedIdentifier);
        doc.setAlternateVendorDetailAssignedIdentifier(this.alternateVendorDetailAssignedIdentifier);
        doc.setPurchaseOrderNotes(this.purchaseOrderNotes);
        doc.setRecurringPaymentTypeCode(this.recurringPaymentTypeCode);
        doc.setPostingYear(2008);
        
        for (PaymentRequestItemFixture paymentRequestItemFixture : paymentRequestItemFixtures) {
            paymentRequestItemFixture.addTo(doc);
        }

        return doc;
    }
    
}
