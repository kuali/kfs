/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.document.service;

import java.sql.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.util.VendorGroupingHelper;
import org.kuali.kfs.vnd.businessobject.PaymentTermType;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;

/**
 * Defines methods that must be implemented by a PaymentRequestService implementation.
 */
public interface PaymentRequestService extends AccountsPayableDocumentSpecificService {

    /**
     * Obtains a list of payment request documents given the purchase order id.
     * 
     * @param poDocId  The purchase order id to be used to obtain a list of payment request documents.
     * @return         The List of payment request documents given the purchase order id.
     */
    public List<PaymentRequestDocument> getPaymentRequestsByPurchaseOrderId(Integer poDocId);

    /**
     * Obtains a list of payment request documents given the purchase order id, the invoice amount
     * and the invoice date.
     * 
     * @param poId           The purchase order id used to obtain the payment request documents.
     * @param invoiceAmount  The invoice amount used to obtain the payment request documents.
     * @param invoiceDate    The invoice date used to obtain the payment request documents.
     * @return               The List of payment request documents that match the given criterias (purchase order id, invoice amount and invoice date).
     */
    public List getPaymentRequestsByPOIdInvoiceAmountInvoiceDate(Integer poId, KualiDecimal invoiceAmount, Date invoiceDate);

    /**
     * Obtains a list of payment request documents given the vendorHeaderGeneratedIdentifier, vendorDetailAssignedIdentifier and the invoice number.
     *
     * @param vendorHeaderGeneratedIdentifier The vendorHeaderGeneratedIdentifier used to obtain the payment request documents.
     * @param vendorDetailAssignedIdentifier  The vendorDetailAssignedIdentifier used to obtain the payment request documents.
     * @return                                The List of payment request documents that match the given criterias.
     */
    public List getPaymentRequestsByVendorNumber(Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier);

    /**
     * Obtains a list of payment request documents given the vendorHeaderGeneratedIdentifier, vendorDetailAssignedIdentifier and the invoice number.
     *
     * @param vendorHeaderGeneratedIdentifier The vendorHeaderGeneratedIdentifier used to obtain the payment request documents.
     * @param vendorDetailAssignedIdentifier  The vendorDetailAssignedIdentifier used to obtain the payment request documents.
     * @param invoiceNumber                   The invoice number used to obtain the payment request documents.
     * @return                                The List of payment request documents that match the given criterias.
     */
    public List getPaymentRequestsByVendorNumberInvoiceNumber(Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, String invoiceNumber);

    
    
    /**
     * Determines whether the invoice date is after today.
     * 
     * @param invoiceDate  The invoice date to be determined whether it's after today.
     * @return             boolean true if the given invoice date is after today.
     */
    public boolean isInvoiceDateAfterToday(Date invoiceDate);

    /**
     * Performs the processing to check whether the payment request is a duplicate and if so, adds
     * the information about the type of duplication into the resulting HashMap to be returned by this method.
     * 
     * @param document  The payment request document to be processed/checked for duplicates.
     * @return          The HashMap containing "PREQDuplicateInvoice" as the key and the string
     *                  describing the types of duplication as the value.
     */
    public HashMap<String, String> paymentRequestDuplicateMessages(PaymentRequestDocument document);

    /**
     * Calculate based on the terms and calculate a date 10 days from today. Pick the one that is the farthest out. We always
     * calculate the discount date, if there is one.
     * 
     * @param invoiceDate  The invoice date to be used in the pay date calculation.
     * @param terms        The payment term type to be used in the pay date calculation.
     * @return             The resulting pay date given the invoice date and the terms.
     */
    public java.sql.Date calculatePayDate(Date invoiceDate, PaymentTermType terms);

    /**
     * Marks a payment request on hold.
     * 
     * @param document    The payment request document to be marked as on hold.
     * @param note        The note to be added to the payment request document while being marked as on hold.
     * @return            The PaymentRequestDocument with updated information.
     * @throws Exception
     */
    public PaymentRequestDocument addHoldOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception;

    /**
     * Removes a hold on a payment request.
     * 
     * @param document    The payment request document whose hold is to be removed.
     * @param note        The note to be added to the payment request document while its hold is being removed.
     * @return            The PaymentRequestDocument with updated information.
     * @throws Exception
     */
    public PaymentRequestDocument removeHoldOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception;

    /**
     * Obtains the payment request document given the purapDocumentIdentifier.
     * 
     * @param poDocId  The purapDocumentIdentifier of the payment request document to be obtained.
     * @return         The payment request document whose purapDocumentIdentifier matches with the input parameter.
     */
    public PaymentRequestDocument getPaymentRequestById(Integer poDocId);

    /**
     * Obtains the payment request document given the document number.
     * 
     * @param documentNumber  The document number to be used to obtain the payment request document.
     * @return                The payment request document whose document number matches with the given input parameter.
     */
    public PaymentRequestDocument getPaymentRequestByDocumentNumber(String documentNumber);

    /**
     * Marks a payment request as requested to be canceled.
     * 
     * @param document    The payment request document to be marked as requested to be canceled.
     * @param note        The note to be added to the payment request document while being marked as requested to be canceled.
     * @throws Exception
     */
    public void requestCancelOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception;

    /**
     * Returns true if the payment request has been extracted
     * 
     * @param document  The payment request document to be determined whether it is extracted.
     * @return          boolean true if the payment request document is extracted.
     */
    public boolean isExtracted(PaymentRequestDocument document);

    /**
     * Removes a request cancel on payment request.
     * 
     * @param document    The payment request document to be used for request cancel.
     * @param note        The note to be added to the payment request document upon the removal of the request cancel.
     * @throws Exception
     */
    public void removeRequestCancelOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception;

    /**
     * Resets a Payment Request that had an associated Payment Request or Credit Memo cancelled externally.
     * 
     * @param paymentRequest  The extracted payment request document to be resetted.
     * @param note            The note to be added to the payment request document upon its reset.
     */
    public void resetExtractedPaymentRequest(PaymentRequestDocument paymentRequest, String note);

    /**
     * Cancels a PREQ that has already been extracted if allowed.
     * 
     * @param paymentRequest  The extracted payment request document to be canceled.
     * @param note            The note to be added to the payment request document.
     */
    public void cancelExtractedPaymentRequest(PaymentRequestDocument paymentRequest, String note);

    /**
     * Get all the payment requests that are immediate and need to be extracted to PDP.
     * 
     * @param chartCode  The chart code to be used as one of the criterias to retrieve the payment request documents. 
     * @return           The iterator of the list of the resulting payment request documents returned by the paymentRequestDao.
     */
    public Collection<PaymentRequestDocument> getImmediatePaymentRequestsToExtract(String chartCode);

    /**
     * Get all the payment requests that match a credit memo.
     * 
     * @param cmd   The credit memo document to be used to obtain the payment requests.
     * @return      The iterator of the resulting payment request documents returned by the paymentRequestDao.
     */
    public Collection<PaymentRequestDocument> getPaymentRequestsToExtractByCM(String campusCode, VendorCreditMemoDocument cmd);

    /**
     * Get all the payment requests that match a vendor.
     * 
     * @param vendor
     * @param onOrBeforePaymentRequestPayDate only payment requests with a pay date on or before this date will be extracted
     * @return Collection of the resulting payment request documents returned by the paymentRequestDao.
     */
    public Collection<PaymentRequestDocument> getPaymentRequestsToExtractByVendor(String campusCode, VendorGroupingHelper vendor, Date onOrBeforePaymentRequestPayDate);
    
    /**
     * Get all the payment requests that need to be extracted.
     * 
     * @return The Collection of the resulting payment request documents returned by the paymentRequestDao.
     */
    public Collection<PaymentRequestDocument> getPaymentRequestsToExtract(Date onOrBeforePaymentRequestPayDate);

    /**
     * Get all the special payment requests for a single chart that need to be extracted.
     * 
     * @param chartCode  The chart code to be used as one of the criterias to retrieve the payment request documents. 
     * @return           The Collection of the resulting payment request documents returned by the paymentRequestDao.
     */
    public Collection<PaymentRequestDocument> getPaymentRequestsToExtractSpecialPayments(String chartCode, Date onOrBeforePaymentRequestPayDate);

    /**
     * Get all the regular payment requests for a single campus.
     * 
     * @param chartCode  The chart code to be used as one of the criterias to retrieve the payment request documents. 
     * @return           The collection of the resulting payment request documents returned by the paymentRequestDao.
     */
    public Collection<PaymentRequestDocument> getPaymentRequestToExtractByChart(String chartCode, Date onOrBeforePaymentRequestPayDate);

    /**
     * Recalculate the payment request.
     * 
     * @param pr              The payment request document to be calculated.
     * @param updateDiscount  boolean true if we also want to calculate the discount items for the payment request.
     */
    public void calculatePaymentRequest(PaymentRequestDocument pr, boolean updateDiscount);

    /**
     * Performs calculations on the tax edit area, generates and adds NRA tax charge items as below the line items, with their accounting lines; 
     * the calculation will activate updates on the account summary tab and the general ledger entries as well.
     *
     * The non-resident alien (NRA) tax lines consist of four possible sets of tax lines: 
     * - Federal tax lines
     * - Federal Gross up tax lines
     * - State tax lines
     * - State Gross up tax lines
     * 
     * Federal tax lines are generated if the federal tax rate in the payment request is not zero.
     * State tax lines are generated if the state tax rate in the payment request is not zero.
     * Gross up tax lines are generated if the tax gross up indicator is set on the payment request and the tax rate is not zero.
     * 
     * @param preq The payment request the NRA tax lines will be added to.
     * 
     */
    public void calculateTaxArea(PaymentRequestDocument preq);
    
    /**
     * Populate payment request.
     * 
     * @param preq  The payment request document to be populated.
     */
    public void populatePaymentRequest(PaymentRequestDocument preq);

    /**
     * Populate and save payment request.
     * 
     * @param preq  The payment request document to be populated and saved.
     */
    public void populateAndSavePaymentRequest(PaymentRequestDocument preq) throws WorkflowException;

    /**
     * Retrieve a list of PREQs that aren't approved, check to see if they match specific requirements, then auto-approve them if
     * possible.
     * 
     * @return  boolean true if the auto approval of payment requests has at least one error.
     */
    public boolean autoApprovePaymentRequests();
    /**
     * Checks whether the payment request document is eligible for auto approval. If so, then updates
     * the status of the document to auto approved and calls the documentService to blanket approve
     * the document, then returns false.
     * If the document is not eligible for auto approval then returns true.
     * 
     * @param docNumber            The payment request document number (not the payment request ID) to be auto approved.
     * @param defaultMinimumLimit  The default minimum limit amount to be used in determining the eligibility of the document to be auto approved.
     * @return                     boolean true if the payment request document is not eligible for auto approval.
     * @throws RuntimeException    To indicate to Spring transactional management that the transaction for this document should be rolled back
     */
    public boolean autoApprovePaymentRequest(String docNumber, KualiDecimal defaultMinimumLimit);
    
    /**
     * Checks whether the payment request document is eligible for auto approval. If so, then updates
     * the status of the document to auto approved and calls the documentService to blanket approve
     * the document, then returns false.
     * If the document is not eligible for auto approval then returns true.
     * 
     * @param doc                  The payment request document to be auto approved.
     * @param defaultMinimumLimit  The default minimum limit amount to be used in determining the eligibility of the document to be auto approved.
     * @return                     boolean true if the payment request document is not eligible for auto approval.
     * @throws RuntimeException    To indicate to Spring transactional management that the transaction for this document should be rolled back
     */
    public boolean autoApprovePaymentRequest(PaymentRequestDocument doc, KualiDecimal defaultMinimumLimit);

    /**
     * Mark a payment request as being paid and set the payment request's paid date as the processDate.
     * 
     * @param pr           The payment request document to be marked as paid and paid date to be set.
     * @param processDate  The date to be set as the payment request's paid date.
     */
    public void markPaid(PaymentRequestDocument pr, Date processDate);

    /**
     * This method specifies whether the payment request document has a discount item.
     * 
     * @param preq  The payment request document to be verified whether it has a discount item.
     * @return      boolean true if the payment request document has at least one discount item.
     */
    public boolean hasDiscountItem(PaymentRequestDocument preq);
    
    /**
     * Changes the current vendor to the vendor passed in.
     * 
     * @param preq
     * @param headerId
     * @param detailId
     * @param primaryHeaderId
     * @param primaryDetailId
     */
    public void changeVendor(PaymentRequestDocument preq, Integer headerId, Integer detailId);
    
    /**
     * A method to create the description for the payment request document.
     * 
     * @param purchaseOrderIdentifier  The purchase order identifier to be used as part of the description.
     * @param vendorName               The vendor name to be used as part of the description.
     * @return                         The resulting description string for the payment request document.
     */
    public String createPreqDocumentDescription(Integer purchaseOrderIdentifier, String vendorName);
    
    /**
     * Determines if there are active payment requests for a purchase order.
     * 
     * @param purchaseOrderIdentifier
     * @return
     */
    public boolean hasActivePaymentRequestsForPurchaseOrder(Integer purchaseOrderIdentifier);
    
    public void processPaymentRequestInReceivingStatus();
    
    /**
     * Payment Requests created in the previous fiscal year get backdated if we're at the beginning of the new fiscal year (i.e.
     * prior to first closing)
     * 
     * @param paymentRequestDocument
     * @return
     */
    public boolean allowBackpost(PaymentRequestDocument paymentRequestDocument);
    
    public boolean isPurchaseOrderValidForPaymentRequestDocumentCreation(PaymentRequestDocument paymentRequestDocument,PurchaseOrderDocument po);
    
    /**
     * Removes additional charge items that are not eligible on the payment request document.
     * 
     * @param document
     */
    public void removeIneligibleAdditionalCharges(PaymentRequestDocument document);
    
    public boolean encumberedItemExistsForInvoicing(PurchaseOrderDocument document);

    /**
     * Clears tax info.
     * 
     * @param document The disbursement voucher document being modified.
     */
    public void clearTax(PaymentRequestDocument document);
    
    /**
     * fetches list of document numebrs of payment requests for a given purchase order id.
     * It filters the records by application document status by including if include is true
     * else not including.
     * 
     * @param applicationDocumentStatus
     * @param include
     * @param purchaseOrderId
     * @return List<String> document numbers
     */
    public List<String> getPaymentRequestsByStatusAndPurchaseOrderId(String applicationDocumentStatus, boolean include, Integer purchaseOrderId);
}

