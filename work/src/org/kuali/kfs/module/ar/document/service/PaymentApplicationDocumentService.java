/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.service;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.web.struts.PaymentApplicationDocumentForm;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.util.KualiDecimal;

public interface PaymentApplicationDocumentService {
    
    public CashControlDocument getCashControlDocumentForPaymentApplicationDocumentNumber(String paymentApplicationDocumentNumber);

    public CashControlDocument getCashControlDocumentForPaymentApplicationDocument(PaymentApplicationDocument document);

    public KualiDecimal getTotalUnappliedFundsForPaymentApplicationDocument(PaymentApplicationDocument document);

    public KualiDecimal getTotalUnappliedFundsToBeAppliedForPaymentApplicationDocument(PaymentApplicationDocument document);

    public KualiDecimal getTotalToBeAppliedForPaymentApplicationDocument(String paymentApplicationDocumentNumber);

    public KualiDecimal getTotalAppliedAmountForPaymentApplicationDocument(PaymentApplicationDocument document);

    /**
     * This method creates an invoice paid applied for the given customer invoice detail. If an invoice paid applied already exists for this 
     * customer invoice detail than it will only update the applied amount and it will return null.
     * @param customerInvoiceDetail the customer invoice detail for which we want to create the invoice paid applied
     * @param applicationDocNbr the payment application document number
     * @param universityFiscalYear the university fiscal year
     * @param universityFiscalPeriodCode the university fiscal period code
     * @param amount the amount to be applied
     * @return the created invoice paid applied if it did not exist, null otherwise
     */
    public InvoicePaidApplied createInvoicePaidAppliedForInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail, String applicationDocNbr, Integer universityFiscalYear, String universityFiscalPeriodCode, KualiDecimal amount, Integer invoicePaidAppliedItemNbr);
    
    /**
     * This method is used in the lockbox process to create a PA document which is then auto-approved when the amount on the invoice matches 
     * the amount on the lockbox.
     * 
     * @param customerInvoiceDocument
     * @return
     */
    public PaymentApplicationDocument createPaymentApplicationToMatchInvoice(CustomerInvoiceDocument customerInvoiceDocument);

    public void updateAmountAppliedOnDetail(PaymentApplicationDocument applicationDocument, CustomerInvoiceDetail customerInvoiceDetail);
    public void updateCustomerInvoiceDetailAppliedPayments(PaymentApplicationDocument applicationDocument, CustomerInvoiceDetail customerInvoiceDetail);
    public void updateCustomerInvoiceDetailBalance(CustomerInvoiceDetail customerInvoiceDetail);
    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsForDetail(Collection<InvoicePaidApplied> appliedPayments, CustomerInvoiceDetail customerInvoiceDetail);
    public void updateCustomerInvoiceDetailInfo(PaymentApplicationDocument applicationDocument, CustomerInvoiceDetail customerInvoiceDetail);
    public PaymentApplicationDocument createSaveAndApprovePaymentApplicationToMatchInvoice(CustomerInvoiceDocument customerInvoiceDocument, String approvalAnnotation, List workflowNotificationRecipients) throws WorkflowException;
    public PaymentApplicationDocument createAndSavePaymentApplicationToMatchInvoice(CustomerInvoiceDocument customerInvoiceDocument) throws WorkflowException;
    
}
