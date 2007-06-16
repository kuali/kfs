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
//import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.NoteService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.module.purap.dao.PaymentRequestDao;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.vendor.service.VendorService;


public interface PaymentRequestService {

    public void save(PaymentRequestDocument paymentRequestDocument);
    
    public List<PaymentRequestDocument> getPaymentRequestsByPurchaseOrderId(Integer poDocId);
    
    public List getPaymentRequestsByPOIdInvoiceAmountInvoiceDate(Integer poId, KualiDecimal invoiceAmount, Date invoiceDate);
    
    // TODO: Implement me.
    public List getPaymentRequestsByVendorNumberInvoiceNumber(Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, String invoiceNumber);
    
    public boolean isInvoiceDateAfterToday(Date invoiceDate);
    
    public HashMap<String, String> paymentRequestDuplicateMessages(PaymentRequestDocument document);
    
    public HashMap<String, String> expiredOrClosedAccountsList(PaymentRequestDocument document);
    
    public void addContinuationAccountsNote(PaymentRequestDocument document, HashMap<String, String> accounts);
    
    public void addHoldOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception;
    
    public boolean isPaymentRequestHoldable(PaymentRequestDocument document);
    
    public boolean canHoldPaymentRequest(PaymentRequestDocument document, UniversalUser user);
    
    public boolean canRemoveHoldPaymentRequest(PaymentRequestDocument document, UniversalUser user);
    
    public void removeHoldOnPaymentRequest(PaymentRequestDocument document);
       
    public PaymentRequestDocument getPaymentRequestById(Integer poDocId);
    
    public void requestCancelOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception;
    
    public boolean canRequestCancelOnPaymentRequest(PaymentRequestDocument document);
    
    public boolean canUserRequestCancelOnPaymentRequest(PaymentRequestDocument document, UniversalUser user);
    
    public boolean canUserRemoveRequestCancelOnPaymentRequest(PaymentRequestDocument document, UniversalUser user);
    
    public void removeRequestCancelOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception;

    /**
     * Recalculate the payment request
     * 
     * @param pr
     */
    public void calculatePaymentRequest(PaymentRequestDocument pr,boolean updateDiscount);

    
    /* Start Paste from EPIC */
     
    /**
     * Gets the User object for the AP Supervisor
     * user id stored in constant
     * 
     * @return  User
     */
 ///   public User getAccountsPayableSupervisorUser();
    
    /**
     * Create payment request from electronic invoice.  
     * 
     * @param poId               Integer - purchase order id.
     * @param u                  User
     * @return PurchaseOrder or null if not created
     */
 /*
    public PaymentRequestDocument createPaymentRequestFromElectronicInvoice(Integer poID, String invoiceNumber, 
        Timestamp invoiceDate, BigDecimal vendorInvoiceAmount, String specialHandlingInstructionsLine1, 
        String specialHandlingInstructionsLine2, String specialHandlingInstructionsLine3, 
        List closedAccounts, List expiredAccounts);
 */
    /**
     * Create payment request.  
     * 
     * @param poId               Integer - purchase order id.
     * @param u                  User
     * @return PaymentRequest or null if not created
     */
 /*
    public PaymentRequestDocument createPaymentRequest(Integer poID, String invoiceNumber, 
        Timestamp invoiceDate, BigDecimal vendorInvoiceAmount, String specialHandlingInstructionsLine1, 
        String specialHandlingInstructionsLine2, String specialHandlingInstructionsLine3, UniversalUser u,
        List closedAccounts, List expiredAccounts);
*/
    /**
     * Get a payment request by id
     * 
     * @param id PaymentRequest Id
     * @return PaymentRequest or null if not found
     */
 //  public PaymentRequestDocument getPaymentRequestById(Integer id);

    /**
     * Get a payment request document by id
     * 
     * @param id PaymentRequest Id
     * @return PaymentRequestDocument or null if not found
     */
 //   public PaymentRequestDocument getPaymentRequestDocumentById(Integer id, UniversalUser user);
    
    /**
     * Get a payment request document by id without calling
     * workflow
     * 
     * @param id PaymentRequest Id
     * @return PaymentRequestDocument or null if not found
     */
  //  public PaymentRequestDocument getPaymentRequestDocumentByIdNoWorkflow(Integer id, UniversalUser user);
    
    /**
     * Get a payment request by Workflow Doc id
     * 
     * @param l Workflow Doc Id
     * @return PaymentRequest or null if not found
     */
 //   public PaymentRequestDocument getPaymentRequestByDocId(Long l);

    /**
     * Get a bundle of payment request info by req id
     * 
     * @param reqId requisition id
     * @return A bunch of cool stuff
     */
 //  public List getPaymentRequestDocumentsByReqID(Integer reqId, UniversalUser u);

    /**
     * Get a bundle of payment request info by PO id
     * 
     * @param poId purchase order id
     * @return A bunch of cool stuff
     */
  //  public List getPaymentRequestDocumentsByPOID(Integer poId, UniversalUser u);

 //   public List getAllPREQsByPOIdAndStatus(Integer purchaseOrderID,Collection statusCodes);
    
    /**
     * Check a payment request to make sure accounting strings and asset
     * numbers are valid as well as validating reference fields.
     * 
     * @param paymentRequest PaymentRequest
     * @return A collection of ServiceError objects.  
     */
 //   public Collection validatePaymentRequestReview(PaymentRequestDocument paymentRequestDocument);

    /**
     * Check a payment request to make sure accounting strings and asset
     * numbers are valid as well as validating reference fields. This
     * also will calculate the payment request if formatters are valid.
     * 
     * @param paymentRequest PaymentRequest
     * @param closePO        value to tell whether to close the PO or not
     * @return A collection of ServiceError objects.  
     */
  //  public Collection validateAndCalculatePayReqReview(PaymentRequestDocument paymentRequestDocument, boolean closePO);
    
    /**
     * Check a payment request for validation that is normally run
     * in the PaymentRequestForm object.  This is used for electronic
     * invoicing.  Currently mimiks following methods:
     * 
     * PaymentRequestForm.validateFormatters()
     * PaymentRequestForm.validateRequired()
     * PaymentRequestForm.validateTotalOverZero()
     * 
     * @param paymentRequest PaymentRequest
     * @return A collection of ServiceError objects.  
     */
 //   public Collection validateElecInvoiceUsingFormValidation(PaymentRequestDocument paymentRequest);
    
    /**
     * Check a payment request to make sure accounting strings and asset
     * numbers are valid as well as validating reference fields. It also 
     * checks to make sure every item has at least one account and that 
     * all percents in each account are filled in and total 100%/item. It
     * does this only where the account object has the 'isFiscallyEditable'
     * flag set to true.  If the flag is set to null then it validates it
     * only if the user is the fiscal officer or a delegate of that 
     * accounting string.
     * 
     * @param paymentRequest PaymentRequest
     * @param user User calling this
     * @return A collection of ServiceError objects.  
     */
 //   public Collection validateRouteFiscal(PaymentRequestDocument paymentRequest, UniversalUser user);
    
    /**
     * Check to see if a PREQ is allowed to be Cancelled.  
     * 
     * @param paymentRequest PaymentRequest to save
     * @return errors Collection of ServiceErrors
     */
 //   public Collection validateCancel(PaymentRequestDocument paymentRequest);
    
    /**
     * Save a payment request.  This saves it only if the 
     * accounting information is valid.  This saves it to the
     * tables.
     * 
     * @param paymentRequest PaymentRequest to save
     * @param u User saving the Req
     */
    /* I don't think this method is being used at all, can we remove this ?
    public Collection savePaymentRequestForReview(PaymentRequest paymentRequest, User u);
    */
    
    /**
     * Save a payment request with a status change.  This saves 
     * it only if the accounting information is valid.  This saves 
     * it to the tables.
     * 
     * @param paymentRequest PaymentRequest to save
     * @param newStatusCode PaymentRequestStatus to set
     * @param u User saving the Req
     */
    /* I don't think this method is being used at all, can we remove this ?
    public void savePaymentRequestWithStatusChange(PaymentRequest paymentRequest, String newStatusCode, User u);
    */
    
    /**
     * Cancel a PREQ if allowed.  
     * 
     * @param paymentRequest PaymentRequest to save
     * @param note String for status change note
     * @param u User saving the Req
     */
//    public Collection cancelPaymentRequest(PaymentRequestDocument paymentRequestDocument, UniversalUser u, String note, boolean openPO, String sendNoteEmailAddress);

    /**
     * Cancel a PREQ that has already been extracted if required.  
     * 
     * @param paymentRequest   PaymentRequest to cancel
     * @param note             String for cancel note
     * @param u                User cancelling the PREQ
     */
 //   public void cancelExtractedPaymentRequest(PaymentRequestDocument paymentRequestDocument, UniversalUser u, String note);
    
    /**
     * Reset a Payment Request that had an associated Payment Request
     * or Credit Memo cancelled externally.  
     * 
     * @param paymentRequest   PaymentRequest to reset
     * @param note             String for the status change note
     * @param u                User resetting the PREQ
     */
//    public void resetExtractedPaymentRequest(PaymentRequestDocument paymentRequestDocument, UniversalUser u, String note);
    
    /**
     * Mark a payment request paid in external payment application.  
     * 
     * @param paymentRequest   PaymentRequest to mark as paid
     * @param u                User marking the PREQ
     */
 //   public void markPaymentRequestPaid(PaymentRequestDocument paymentRequestDocument, UniversalUser u);
    
    /**
     * Save a pay req.  This saves it to the
     * tables.  
     * 
     * @param pr PaymentRequest to save
     */
 //   public PaymentRequestDocument savePaymentRequest(PaymentRequestDocument pr, UniversalUser user, Collection serviceErrors, String sendNoteEmailAddress);

    /**
     * Save a pay req.  This saves it to the
     * tables.  
     * 
     * @param pr PaymentRequest to save
     */
 //   public PaymentRequestDocument savePaymentRequestNoRetrieveReferences(PaymentRequestDocument pr, UniversalUser user, Collection serviceErrors, String sendNoteEmailAddress);

    /**
     * This method is needed to retrofit the old savePaymentRequest, which did not have PaymentRequestDocument nor 
     * sendNoteEmailAddress, in the cases where we might not need to send emails. I'll delete this method if we 
     * decide later that it's not necessary
     * 
     * @param pr
     * @param user
     * @return
     */
  //  public PaymentRequestDocument savePaymentRequest(PaymentRequestDocument pr, UniversalUser user);
    

    /**
     * Saves a Doc Note and sends e-mail if necessary.
     * 
     * @param docNote  DocumentNote to save
     */
  ////  public ServiceError savePaymentRequestDocumentNote(DocumentNote docNote);

    /**
     * Retreives a list of Pay Reqs with the given PO Id.
     * 
     * @param id
     * @return List of Pay Reqs.
     */
 //   public List getPaymentRequestsByPOId(Integer id);
    
    /**
     * Retreives a list of Pay Reqs with the given PO Id.
     * 
     * @param id
     * @return List of Pay Reqs.
     */
  //  public List getPaymentRequestsByPOId(Integer id, Integer returnListMax);

    /**
     * Verify's that a PREQ can be created against the given information and
     * warns against possible duplicates.
     * 
     * @param purchaseOrderId
     * @param invoiceNumber
     * @param invoiceAmount
     * @param invoiceDate
     * @return List of messages describing problems with verification.
     */
  /*
    public PaymentRequestInitializationValidationErrors verifyPreqInitialization(
        Integer purchaseOrderId, String invoiceNumber, BigDecimal invoiceAmount, Timestamp invoiceDate,
        List expiredAccounts, List closedAccounts, UniversalUser u);
*/
    /**
     * Verify's that a PREQ can be created against the given information and
     * warns against possible duplicates.
     * 
     * @param legacyPurchaseOrderID
     * @param invoiceNumber
     * @param invoiceAmount
     * @param invoiceDate
     * @return List of messages describing problems with verification.
     */
/* 
    public PaymentRequestInitializationValidationErrors verifyPreqInitialization(
        String legacyPurchaseOrderID, String invoiceNumber, BigDecimal invoiceAmount, Timestamp invoiceDate,
        List expiredAccounts, List closedAccounts, UniversalUser u);
*/
    /**
     * Checks PREQ for errors. If no errors then calculates and approves
     * If no errors but warnings checks for overridden indicator and 
     * approves if overriden and always returns warnings.
     * 
     * @param pr Payment Request
     * @param u User that did it
     * @param overRideWarnings override any warnings
     * @param closePO Close the PO if true
     * @return collection of ServiceErrors
     */
 //   public Collection reviewCalculateAndApprovePREQ(PaymentRequestDocument pr,UniversalUser u, boolean overRideWarnings, boolean closePO, String sendNoteEmail);

    /**
     * Method takes existing Payment Request and marks is as Approved by AP
     * which can be two different statuses depending on whether the PREQ
     * is from an e-invoice or not
     * 
     * @param pr                     Payment Request to process
     * @param u                      User making approval
     * @param sendNoteEmailAddress   String of multiple addresses to send note notification to
     * @return  Collection of ServiceError messages relating to e-mail errors encountered
     */
 //   public Collection apApprove(PaymentRequestDocument pr,UniversalUser u, String sendNoteEmailAddress);
    
    /**
     * Saves a PaymentRequestStatusHistory object with a note
     * 
     * @param user        User performing the action
     * @param request     PaymentRequest
     * @param oldStatus   the current PaymentRequestStatus
     * @param newStatus   the PaymentRequestStatus being changed to
     * @param note        a text that should be saved as a note
     * @return List of messages describing problems with verification.
     */
  /*
    public void savePaymentRequestStatusHistory(UniversalUser user, PaymentRequestDocument request,
        PaymentRequestStatus oldStatus, PaymentRequestStatus newStatus,
        String note);
*/
    /**
     * Saves a PaymentRequestStatusHistory object
     * 
     * @param user        User performing the action
     * @param request     PaymentRequest
     * @param oldStatus   the current PaymentRequestStatus
     * @param newStatus   the PaymentRequestStatus being changed to
     * @return List of messages describing problems with verification.
     */
 /*
    public void savePaymentRequestStatusHistory(UniversalUser user, PaymentRequestDocument request,
            PaymentRequestStatus oldStatus, PaymentRequestStatus newStatus);
*/
    /**
     * Set the Vendor address of the given ID.
     * 
     * @param addressID   ID of the address to set
     * @param pr          PaymentRequest to set in
     * @return            New PaymentRequest to use
     */
//    public PaymentRequestDocument setVendorAddress(Long addressID, PaymentRequestDocument pr);
    
    /**
     * Retrieve a list of PREQs that aren't approved, check to see if they match specific
     * requirements, then auto-approve them if possible.
     *
     */
//    public void autoApprovePaymentRequests();

    /**
     * Retrieve all initiated PREQs and check for onbase images.  If an image is found,
     * route the PREQ.
     *
     */
//    public void routePreqsWithImage();
    
    /**
     * This should ONLY be called for testing.  NEVER EVER CALL IT IN PROD!
     * 
     * @param preqId
     */
//    public void routePaymentRequestAsEpicWorkflowUser(PaymentRequestDocument preq);
    
//    public void fakeRoutePaymentRequest(Integer preqID);
    
    /**
     * To Do Nasser
     * Returns POId given legacyPOId 
     * 
     * @param legacyPOId
     */ 
  /*
    public Integer getPoIdFromLegacyId(Integer legacyPOId); 

    }

  */
    
    /**
     * @param paymentRequest PaymentRequest to save
     * @param u User saving the Req
     */
//    public Collection convertMoneyToPercent(PaymentRequestDocument pr);

    /**
     * This send the note emails to the addresses entered on the tabbed page 
     * 
     * @param pr             PaymentRequest object for this note
     * @param emailAddresses String the email address to send note emails
     * @param user           User current user doing the note emailing
     * @return A collection of ServiceError objects
     * 
     */  
//    public Collection sendNoteEmails(PaymentRequestDocument pr, String emailAddresses, UniversalUser user);  

    /**
     * This send the note emails to the addresses entered on the display doc page 
     * 
     * @param prId           Integer The payment request id of the payment request for this note
     * @param emailAddresses String the email address to send note emails
     * @param user           User current user doing the note emailing
     * @return A collection of ServiceError objects
     * 
     */
//    public Collection sendNoteEmailsFromDisplayDoc(Integer prId, String emailAddresses, UniversalUser user);
    
    /* (non-Javadoc)
     * @see edu.iu.uis.pur.service.PaymentRequestService#updateAlternateVendorFromVendorSearch
     * (edu.iu.uis.pur.po.bo.PaymentRequest, edu.iu.uis.services.user.User, 
     *  java.lang.Integer, java.lang.Integer)
     */
//    public PaymentRequestDocument useAlternateVendor(PaymentRequestDocument preq, UniversalUser u, Integer headerId, Integer detailId);
    
//    public PaymentRequestDocument changeVendor(PaymentRequestDocument preq, UniversalUser u, Integer headerId, Integer detailId, Integer primaryHeaderId, Integer primaryDetailId);
        
    /**
     * This validates an electronic invoice and makes sure it can be turned into a Payment Request
     * 
     * @param purchaseOrderID  PO ID from E-Invoice
     * @param invoiceDate      invoice date from E-Invoice
     * @param invoiceNumber    invoice number of the electronic invoice file
     * @param filename         filename of electronic invoice file
     * @return A PaymentRequestInitializationValidationErrors object containing continuation accounting
     *             and error messages if need be
     */
  //  public PaymentRequestInitializationValidationErrors validateElectronicInvoicePaymentRequest(Integer purchaseOrderID, 
  //      Date invoiceDate, String invoiceNumber, String invoiceFilename);
    
    /**
     * This method takes the payment request and uses the tax values in it to calculate
     * the necessary item changes for the tax edits and then saves the payment request
     * 
     * @param pr    Payment Request to calculate taxes on and save if possible
     * @param user  user requesting the calculation
     * @return      Collection of error messages in ServiceError objects
     */
//    public PaymentRequestDocument calculateAndSaveTaxEdits(PaymentRequestDocument pr, UniversalUser user);
    
    /**
     * This method takes the payment request and uses the tax values in it to calculate
     * the necessary item changes for the tax edits
     * 
     * @param pr    Payment Request to calculate taxes on
     * @param user  user requesting the calculation
     * @return      Collection of error messages in ServiceError objects
     */
//    public PaymentRequestDocument calculateTaxEdits(PaymentRequestDocument pr, UniversalUser user);
    
    /**
     * This method is for legacy PREQs that were created prior to the new tax 
     * routing edits.  These preqs will not have existing tax items on them 
     * and they must be created for the tax edit methods to work correctly
     * 
     * @param paymentRequest  Payment request with all tax items included
     */
//    public void addTaxItemsIfNecessary(PaymentRequestDocument paymentRequestDocument);
    
    
    
    /* End of Paste from EPIC */
}
