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
package org.kuali.module.purap.service;

import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.vendor.bo.PaymentTermType;

import edu.iu.uis.eden.exception.WorkflowException;


public interface PaymentRequestService extends AccountsPayableDocumentSpecificService {

    public void saveDocumentWithoutValidation(PaymentRequestDocument paymentRequestDocument);
    
    public List<PaymentRequestDocument> getPaymentRequestsByPurchaseOrderId(Integer poDocId);
    
    public List getPaymentRequestsByPOIdInvoiceAmountInvoiceDate(Integer poId, KualiDecimal invoiceAmount, Date invoiceDate);
    
    public List getPaymentRequestsByVendorNumberInvoiceNumber(Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, String invoiceNumber);
    
    public boolean isInvoiceDateAfterToday(Date invoiceDate);
    
    public HashMap<String, String> paymentRequestDuplicateMessages(PaymentRequestDocument document);
        
    public Date calculatePayDate(Date invoiceDate,PaymentTermType terms);
        
    public void addHoldOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception;
    
    public boolean isPaymentRequestHoldable(PaymentRequestDocument document);
    
    public boolean canHoldPaymentRequest(PaymentRequestDocument document, UniversalUser user);
    
    public boolean canRemoveHoldPaymentRequest(PaymentRequestDocument document, UniversalUser user);
    
    public void removeHoldOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception;
       
    public PaymentRequestDocument getPaymentRequestById(Integer poDocId);
    
    public void requestCancelOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception;
    
    public boolean canRequestCancelOnPaymentRequest(PaymentRequestDocument document);
    
    /**
     * 
     * This method returns true if the payment request has been extracted
     * @param document
     * @return
     */
    public boolean isExtracted(PaymentRequestDocument document);
    
    public boolean canUserRequestCancelOnPaymentRequest(PaymentRequestDocument document, UniversalUser user);
    
    public boolean canUserRemoveRequestCancelOnPaymentRequest(PaymentRequestDocument document, UniversalUser user);
    
    public void removeRequestCancelOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception;

    public void resetExtractedPaymentRequest(PaymentRequestDocument paymentRequest, String note);
    
    public void cancelExtractedPaymentRequest(PaymentRequestDocument paymentRequest, String note);

    /**
     * Get all the payment requests that are immediate and need to be extracted to PDP
     * 
     * @param chartCode
     * @return
     */
    public Iterator<PaymentRequestDocument> getImmediatePaymentRequestsToExtract(String chartCode);

    /**
     * Get all the payment requests that match a credit memo
     * 
     * @param cmd
     * @return Iterator of payment requests
     */
    public Iterator<PaymentRequestDocument> getPaymentRequestsToExtractByCM(String campusCode,CreditMemoDocument cmd);

    /**
     * Get all the payment requests that need to be extracted
     * 
     * @return Iterator of payment requests
     */
    public Iterator<PaymentRequestDocument> getPaymentRequestsToExtract();

    /**
     * Get all the special payment requests for a single chart that need to be extracted
     * 
     * @param chartCode
     * @return
     */
    public Iterator<PaymentRequestDocument> getPaymentRequestsToExtractSpecialPayments(String chartCode);

    /**
     * Get all the regular payment requests for a single campus
     * 
     * @param chartCode
     * @return
     */
    public Iterator<PaymentRequestDocument> getPaymentRequestToExtractByChart(String chartCode);

    /**
     * Recalculate the payment request
     * 
     * @param pr
     */
    public void calculatePaymentRequest(PaymentRequestDocument pr,boolean updateDiscount);

    /**
     * populate payment request.  
     * 
     * @param preq paymentrequestdocument
     */
    public void populatePaymentRequest(PaymentRequestDocument preq);
    
    /**
     * populate and save payment request.  
     * 
     * @param preq paymentrequestdocument
     */
    public void populateAndSavePaymentRequest(PaymentRequestDocument preq) throws WorkflowException;
    
    
    /**
     * Retrieve a list of PREQs that aren't approved, check to see if they match specific
     * requirements, then auto-approve them if possible.
     */
    public boolean autoApprovePaymentRequests();
    public boolean autoApprovePaymentRequest(PaymentRequestDocument doc, KualiDecimal defaultMinimumLimit);


    public void deleteSummaryAccounts(Integer purapDocumentIdentifier);

    /**
     * Mark a payment request as being paid
     * 
     * @param pr
     * @param processDate
     */
    public void markPaid(PaymentRequestDocument pr,Date processDate);
    
    /**
     * 
     * This method specifies whether a doc has a discount item
     * @param preq
     */
    public boolean hasDiscountItem(PaymentRequestDocument preq);
}
