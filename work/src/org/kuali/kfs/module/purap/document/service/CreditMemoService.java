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

import java.util.HashMap;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.NoteService;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.module.purap.dao.CreditMemoDao;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.vendor.service.VendorService;


public interface CreditMemoService {

    public void setBusinessObjectService(BusinessObjectService boService);

    public void setDateTimeService(DateTimeService dateTimeService) ;
   

    public void setDocumentService(DocumentService documentService) ;
    
    public void setNoteService(NoteService noteService);
    

    public void setGeneralLedgerService(GeneralLedgerService generalLedgerService) ;
    
    public void setPurapService(PurapService purapService) ;

    public void setCreditMemoDao(CreditMemoDao creditMemoDao) ;

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) ;
    
    public void setVendorService(VendorService vendorService) ;
    
    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService);
    
    public HashMap<String, String> creditMemoDuplicateMessages(CreditMemoDocument document);
    
    

    public void save(CreditMemoDocument creditMemoDocument);
    /*   
    public List<PaymentRequestDocument> getPaymentRequestsByPurchaseOrderId(Integer poDocId);
    
    public List getPaymentRequestsByPOIdInvoiceAmountInvoiceDate(Integer poId, KualiDecimal invoiceAmount, Date invoiceDate);
    
    public boolean isInvoiceDateAfterToday(Date invoiceDate);
    
    
    
    public HashMap<String, String> ExpiredOrClosedAccountsList(PaymentRequestDocument document);
    
    public void addContinuationAccountsNote(PaymentRequestDocument document, HashMap<String, String> accounts);
  */  
    
    /* Start Paste from EPIC */
    /**
     * Verify the initialization screen information to make sure it is OK to 
     * create a Credit Memo with these parameters; then create a credit memo in memory
     * 
     * @param creditMemoNumber
     * @param creditMemoDate
     * @param creditMemoAmount
     * @param poId
     * @param vendorNumber
     * @param preqNumber
     * @return
     */
  /*  public CreditMemoDocument create(String creditMemoNumber,Date creditMemoDate,BigDecimal creditMemoAmount,
        Integer poId, String vendorNumber,Integer preqNumber, User u, boolean checkForDuplicate) 
        throws PurExceptionWithCollection ;
*/
    /**
     * Get a credit memo by ID
     * 
     * @param id
     * @return
     */
 ///   public CreditMemoDocument getById(Integer id);

    /**
     * get Credit Memo Document
     * @param id
     * @param user
     * @return
     */
  //  public CreditMemoDocument getCreditMemoDocumentByID(Integer id, User user);
    
    /**
     * Get all credit Memo Documents associated with this Req ID
     * 
     * @param requistionId  ID of the Req associated with the Credit Memo Documents
     * @return
     */
 //   public List getCreditMemoDocumentsByReqID(Integer requisitionId, User user);
    
    /**
     * Get all credit Memo Documents associated with this PO ID
     * 
     * @param poID  ID of the PO associated with the Credit Memo Documents
     * @return
     */
  //  public List getCreditMemoDocumentsByPOID(Integer poID, User user);

    /**
     * Get all credit Memo associated with this PO ID
     * 
     * @param poID  ID of the PO associated with the Credit Memo
     * @return
     */
///    public List getCreditMemosByPOID(Integer poID);

    /**
     * Get all credit Memo associated with this PO ID
     * 
     * @param poID  ID of the PO associated with the Credit Memo
     * @return
     */
 ///   public List getCreditMemosByPOID(Integer poID, Integer returnListMax);

///    public List getAllCMsByPOIdAndStatus(Integer purchaseOrderID,Collection statusCodes);

    /**
     * Save the credit memo without updating anything. Currently called from workflow.
     * 
     * @param cm
     * @param user
     * @return
     */
  //  public CreditMemoDocument saveCreditMemo(CreditMemoDocument cm, User user);

    /**
     * Save the credit memo from the tab page.  Includes logic to do a straight
     * save when the status is empty or 'INPR' or save with modifying GL entries
     * if the status is 'APAD' or 'CMPT'.  Also, further methods do some work
     * on the items and accounts.
     * 
     * @param cm
     * @param user
     * @param serviceErrors        Collection a collection of ServiceError objects
     * @param sendNoteEmailAddress String the email address to send the note email
     * @return A CreditMemoDocument object 
     */
  //  public CreditMemoDocument saveForEdit(CreditMemoDocument cm, User user, Collection serviceErrors, String sendNoteEmailAddress);

    /**
     * Calculate the credit memo
     * 
     * @param cm
     */
 /*  public void calculate(CreditMemoDocument cm);

    public boolean duplicateExists(String vendorNumber, String creditMemoNumber);
    
    public boolean duplicateExists(Integer vendorNumberHeaderId, Integer vendorNumberDetailId,
            String creditMemoNumber);
    
  //  public Collection approve(CreditMemoDocument cm, User u, boolean overRideWarnings, String sendNoteEmailAddress);
    
    public Collection validate(CreditMemoDocument cm);
  */  
    /**
     * check that a credit memo with this ID can be cancelled
     * @param creditMemoID the CreditMemo's ID
     * @return errors (if any)
     */
//    public Collection validateCancel(Integer creditMemoID);
    
    /**
     * check that a credit memo can be cancelled
     * @param creditMemo the credit memo
     * @return errors (if any)
     */
//    public Collection validateCancel(CreditMemoDocument creditMemo);

    /**
     * Cancel a credit memo
     * @param cm
     * @param user
     * @return collection of errors
     */
 /*
    public CreditMemoDocument cancelCreditMemo(CreditMemoDocument cm, User user, String note);
    
    public void cancelExtractedCreditMemo(CreditMemoDocument cm, User user, String Note);
    
    public void resetExtractedCreditMemo(CreditMemoDocument cm, User user, String Note);
 */   
    /**
     * Mark a credit memo paid in external payment application.  
     * 
     * @param creditMemo       CreditMemo to mark as paid
     * @param u                User marking the CM
     */
 /*
    public void markCreditMemoPaid(CreditMemoDocument creditMemo, User u);
    
    public CreditMemoDocument removeHoldDocument(Integer c,User u, SecurityRecord sr, String note);
    
    public CreditMemoDocument holdDocument(Integer c,User u, SecurityRecord sr, String note);
    
    public Collection validateHold(Integer c, User u);
  */  
    /**
     * Set the Vendor address of the given ID.
     * 
     * @param addressID   ID of the address to set
     * @param cm          CreditMemo to set in
     * @return            New CreditMemo to use
     */
 //   public CreditMemoDocument setVendorAddress(Long addressID, CreditMemoDocument cm);
    
//    public void matchCreditMemosWithImage();

    /**
     * Route a CM (currently used for testing only)
     * 
     * @param creditMemoID   ID of CM
     * @return
     */
//    public void routeCreditMemo(Integer creditMemoID);
    
    /**
     * @param docId
     * @return
     */
//    public CreditMemoDocument getCreditMemoByDocId(Long docId);
    
    /**
     * Saves a Doc Note 
     * 
     * @param docNote  DocumentNote to save
     * @param cmID   CreditMemo ID relating to Note
     * @param user     User saving note and sending email
     */
 //   public ServiceError saveCreditMemoDocumentNote(Integer cmID,DocumentNote docNote,User user);
    
    /**
     * Saves a Doc Note 
     * 
     * @param docNote  DocumentNote to save
     * @param cm       CreditMemo relating to Note
     * @param user     User saving note and sending email
     */
 //   public ServiceError saveCreditMemoDocumentNote(CreditMemoDocument cm,DocumentNote docNote,User user);

    /**
     * Save a credit memo with a status change.  This saves 
     * it to the tables and creates a status history
     * 
     * @param creditMemo CreditMemo to save
     * @param newSatus CreditMemoStatus to set
     * @param u User saving the CreditMemo
     */
 //   public void saveCreditMemoWithStatusChange(CreditMemoDocument creditMemo, String newStatusCode, User u);
    
    /**
     * This send the note emails to the addresses entered on the tabbed page 
     * 
     * @param cm             CreditMemo object for this note
     * @param emailAddresses String the email address to send note emails
     * @param user           User current user doing the note emailing
     * @return A collection of ServiceError objects
     * 
     */
  //  public Collection sendNoteEmails(CreditMemoDocument cm, String emailAddresses, User user);

    /**
     * This send the note emails to the addresses entered on the display doc page 
     * 
     * @param cmId           Integer The credit memo id of the credit memo for this note
     * @param emailAddresses String the email address to send note emails
     * @param user           User current user doing the note emailing
     * @return A collection of ServiceError objects
     * 
     */
 //   public Collection sendNoteEmailsFromDisplayDoc(Integer cmId, String emailAddresses, User user);
}
