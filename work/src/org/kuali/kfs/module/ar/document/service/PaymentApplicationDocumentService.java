/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.document.service;

import java.util.List;

import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;

/**
 * Service methods for Payment Application Document.
 */
public interface PaymentApplicationDocumentService {

    /**
     * Retrieves the CashControlDetail line associated with the passed-in PaymentApplication Document.
     *
     * @param document A valid PaymentApplication Document
     * @return The associated CashControlDetail, if exists, or null if not.
     */
    public CashControlDetail getCashControlDetailForPaymentApplicationDocument(PaymentApplicationDocument document);

    /**
     * Retrieves the CashControlDetail line associated with the passed-in PaymentApplication Document number.
     *
     * @param payAppDocNumber A valid PaymentApplication Document Number
     * @return The associated CashControlDetail, if exists, or null if not.
     */
    public CashControlDetail getCashControlDetailForPayAppDocNumber(String payAppDocNumber);

    /**
     * Retrieves the CashControlDocument associated with the passed-in PaymentApplication Document.
     *
     * @param document A valid PaymentApplication Document
     * @return The associated CashControlDocument, if exists, or null if not.
     */
    public CashControlDocument getCashControlDocumentForPaymentApplicationDocument(PaymentApplicationDocument document);

    /**
     * Retrieves the CashControlDocument associated with the passed-in PaymentApplication Document number.
     *
     * @param payAppDocNumber A valid PaymentApplication Document number
     * @return The associated CashControlDocument, if exists, or null if not.
     */
    public CashControlDocument getCashControlDocumentForPayAppDocNumber(String payAppDocNumber);

    /**
     * Creates PaidApplieds for all the invoice lines on the passed in InvoiceDocument, on the passed in PaymentApplicationDocument.
     * This method will overwrite any existing PaidApplieds on the document, it assumes an empty PayApp doc with no paidapplieds.
     * This method does no checking to prevent over or under applying, it assumes that the documents have been setup such that it
     * will work correctly. So if this method is used to over or under apply, then the resulting PaymentApplicationDocument will
     * fail business rules validation.
     *
     * @param customerInvoiceDocument
     * @param paymentApplicationDocument
     * @return
     */
    public PaymentApplicationDocument createInvoicePaidAppliedsForEntireInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument, PaymentApplicationDocument paymentApplicationDocument);

    /**
     * This method creates an invoice paid applied for the given customer invoice detail. This method assumes that no existing
     * paidApplieds are already on the document.
     *
     * @param customerInvoiceDetail the customer invoice detail for which we want to create the invoice paid applied
     * @param applicationDocNbr the payment application document number
     * @param universityFiscalYear the university fiscal year
     * @param universityFiscalPeriodCode the university fiscal period code
     * @param amount the amount to be applied
     * @return the created invoice paid applied if it did not exist, null otherwise
     */
    public InvoicePaidApplied createInvoicePaidAppliedForInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail, PaymentApplicationDocument paymentApplicationDocument, Integer paidAppliedItemNumber);

    /**
     * This method is used in the lockbox process to create a PA document which is then auto-approved when the amount on the invoice matches
     * the amount on the lockbox.
     *
     * @param customerInvoiceDocument
     * @return
     */
    public PaymentApplicationDocument createPaymentApplicationToMatchInvoice(CustomerInvoiceDocument customerInvoiceDocument) throws WorkflowException;

    /**
     * This method creates, saves and approves a PaymentApplication document for a given invoice with the
     * given approval annotation and adhoc recipients.
     *
     * @param customerInvoiceDocument invoice to create PaymentApplication from
     * @param approvalAnnotation annotation for approval action
     * @param workflowNotificationRecipients adhoc notification recipients
     * @return PaymentApplicationDocument that was created
     * @throws WorkflowException if there's a problem
     */
    public PaymentApplicationDocument createSaveAndApprovePaymentApplicationToMatchInvoice(CustomerInvoiceDocument customerInvoiceDocument, String approvalAnnotation, List workflowNotificationRecipients) throws WorkflowException;

    /**
     * This method creates, saves and approves a PaymentApplication document for a given invoice.
     *
     * @param customerInvoiceDocument invoice to create PaymentApplication from
     * @return PaymentApplicationDocument that was created
     * @throws WorkflowException if there's a problem
     */
    public PaymentApplicationDocument createAndSavePaymentApplicationToMatchInvoice(CustomerInvoiceDocument customerInvoiceDocument) throws WorkflowException;

    /**
     * This method returns true if invoicePaidApplied is the applied payment for
     * the customer invoice detail based on document number and item/sequence number.
     *
     * @param customerInvoiceDetail
     * @param invoicePaidApplied
     * @return
     */
    public boolean customerInvoiceDetailPairsWithInvoicePaidApplied(CustomerInvoiceDetail customerInvoiceDetail, InvoicePaidApplied invoicePaidApplied);

}
