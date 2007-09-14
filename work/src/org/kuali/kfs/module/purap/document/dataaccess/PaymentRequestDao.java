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
package org.kuali.module.purap.dao;

import java.sql.Date;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.document.PaymentRequestDocument;


public interface PaymentRequestDao {

    /**
     * Get all the payment requests that need to be extracted that match a credit memo
     * 
     * @param campusCode limit results to a single chart
     * @param paymentRequestIdentifier Payment Request Identifier (can be null)
     * @param purchaseOrderIdentifier PO Identifier (can be null)
     * @param vendorHeaderGeneratedIdentifier Vendor Header ID
     * @param vendorDetailAssignedIdentifier Vendor Detail ID
     * @return
     */
    public Iterator<PaymentRequestDocument> getPaymentRequestsToExtract(String campusCode,Integer paymentRequestIdentifier,Integer purchaseOrderIdentifier,Integer vendorHeaderGeneratedIdentifier,Integer vendorDetailAssignedIdentifier);

    /**
     * Get all the payment requests that need to be extracted to PDP
     * 
     * @param onlySpecialPayments True - only include special payments, False - include all
     * @param chartCode if not null, limit results to a single chart
     * @return Iterator of payment requests
     */
    public Iterator<PaymentRequestDocument> getPaymentRequestsToExtract(boolean onlySpecialPayments,String chartCode);

    /**
     * Get all the payment requests that are marked immediate that need to be extracted to PDP
     * 
     * @param chartCode
     * @return
     */
    public Iterator<PaymentRequestDocument> getImmediatePaymentRequestsToExtract(String chartCode);

    /**
     * Get all payment request documents that are eligible for auto-approval.
     * Whether or not a document is eligible for auto-approval is determined
     * according to whether or not the document total is below a pre-determined
     * minimum amount. This amount is derived from the accounts, charts and/or
     * organizations associated with a given document. If no minimum amount can
     * be determined from chart associations a default minimum specified as a 
     * system parameter is used to determine the minimum amount threshold.
     * 
     * @return an Iterator over all payment request documents eligible for automatic approval
     */
    public Iterator<PaymentRequestDocument> getEligibleForAutoApproval();

    /**
     * Get a payment request document number by id
     * 
     * @param id PaymentRequest Id
     * @return PaymentRequest or null if not found
     */
    public String getDocumentNumberByPaymentRequestId(Integer id);

    public List<String> getDocumentNumbersByPurchaseOrderId(Integer id);

    /**
     * Retreives a list of Pay Reqs with the given Req Id.
     * 
     * @param id
     * @return List of Pay Reqs.
     */
//    public List getPaymentRequestsByRequisitionId(Integer id);

    /**
     * Retreives a list of Pay Reqs with the given PO Id.
     * 
     * @param id
     * @return List of Pay Reqs.
     */
//    public List getPaymentRequestsByPOId(Integer id);
    
    /**
     * Retreives a list of Pay Reqs with the given PO Id.
     * 
     * @param id
     * @return List of Pay Reqs.
     */
//    public List getPaymentRequestsByPOId(Integer id, Integer returnListMax);
    
    /**
     * Retreives a list of Pay Reqs with the given vendor id and invoice number.
     * 
     * @param vendorHeaderGeneratedId  header id of the vendor id
     * @param vendorDetailAssignedId   detail id of the vendor id
     * @param invoiceNumber            invoice number as entered by AP
     * @return List of Pay Reqs.
     */
    public List getActivePaymentRequestsByVendorNumberInvoiceNumber(Integer vendorHeaderGeneratedId,
        Integer vendorDetailAssignedId,String invoiceNumber);
    
//    public List getAllPREQsByPOIdAndStatus(Integer purchaseOrderID,Collection statusCodes);
    
    /**
     * Retreives a list of Pay Reqs with the given PO Id, invoice amount, and invoice date.
     * 
     * @param poId           purchase order ID
     * @param invoiceAmount  amount of the invoice as entered by AP
     * @param invoiceDate    date of the invoice as entered by AP
     * @return List of Pay Reqs.
     */
    public List getActivePaymentRequestsByPOIdInvoiceAmountInvoiceDate(Integer poId, KualiDecimal invoiceAmount, Date invoiceDate);

    /**
     * Get all the payment requests for a set of statuses
     * 
     * @param statuses
     * @return
     */
//    public Collection getByStatuses(String statuses[]);
    
    public void deleteSummaryAccounts(Integer purapDocumentIdentifier);
    
}
