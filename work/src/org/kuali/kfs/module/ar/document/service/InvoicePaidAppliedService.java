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

import org.kuali.kfs.module.ar.businessobject.AppliedPayment;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.rice.kns.util.KualiDecimal;

public interface InvoicePaidAppliedService<T extends AppliedPayment> {
    
    /**
     * This method doesn't go to the database to get related invoice paid applieds.
     * It looks at a specific document to get the relations worked out.
     * 
     * @param customerInvoiceDetail
     * @param paymentApplicationDocument
     * @return
     */
//    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsForCustomerInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail, PaymentApplicationDocument paymentApplicationDocument);
    
    /**
     * This method takes a list of invoice paid applied moves and uses them to save invoicePaidAppliedMoves
     * @param invoicePaidAppliedMoves
     */
    public void saveInvoicePaidApplieds(List<T> appliedPayments, String documentNumberForDocumentApplyingPayments);
    
    /**
     * This method saves one paid applied
     * @param appliedPayment
     */
    public void saveInvoicePaidApplied(T appliedPayment, Integer paidAppliedItemNumber, String documentNumberForDocumentApplyingPayments);
    
    /**
     * This method returns the total amount applied to a document
     * @param appliedPayments
     */
    public KualiDecimal getTotalAmountApplied(List<T> appliedPayments);
    
    /**
     * This method returns true if invoice has applied amounts (i.e. from application, credit memo, etc), not including
     * discounts
     * 
     * @param document
     * @return
     */
    public boolean doesInvoiceHaveAppliedAmounts(CustomerInvoiceDocument document);

    /**
     * @param documentNumber
     * @return
     */
    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsForInvoice(String documentNumber);

    /**
     * @param documentNumber
     * @return
     */
    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsForInvoice(CustomerInvoiceDocument invoice);
    
    /**
     * @param customerInvoiceDetail
     * @return
     */
//    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsForCustomerInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail);
//    public Collection<InvoicePaidApplied> getApprovedInvoicePaidAppliedsForCustomerInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail);
//    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsForCustomerInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail, String applicationDocNumber);
//    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsFromSpecificDocument(String documentNumber, String referenceCustomerInvoiceDocumentNumber);
    
}
