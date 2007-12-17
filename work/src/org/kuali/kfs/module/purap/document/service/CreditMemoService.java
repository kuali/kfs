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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.util.VendorGroupingHelper;

/**
 * Defines methods that must be implemented by a CreditMemoService implementation.
 */
public interface CreditMemoService extends AccountsPayableDocumentSpecificService {

    /**
     * Gets the Credit memos that can be extracted.
     * 
     * @param chartCode Chart to select from.
     * @return Iterator of credit memos.
     */
    public Iterator<CreditMemoDocument> getCreditMemosToExtract(String chartCode);

    /**
     * Pulls a distinct list of all vendors on CM documents which are ready for extraction.
     * 
     * @param chartCode
     * @return
     */
    public Set<VendorGroupingHelper> getVendorsOnCreditMemosToExtract( String chartCode);
    
    /**
     * Pulls all extractable credit memo documents for a given vendor.
     * 
     * @param chartCode
     * @param vendor
     * @return
     */
    public Iterator<CreditMemoDocument> getCreditMemosToExtractByVendor( String chartCode, VendorGroupingHelper vendor );
    
    /**
     * Get a credit memo by document number.
     * 
     * @param documentNumber  The document number of the credit memo to be retrieved.
     * @return                The credit memo document whose document number matches the input parameter.
     */
    public CreditMemoDocument getCreditMemoByDocumentNumber(String documentNumber);

    /**
     * Retrieves the Credit Memo document by the purapDocumentIdentifier.
     * 
     * @param purchasingDocumentIdentifier  The purapDocumentIdentifier of the credit memo to be retrieved.
     * @return                              The credit memo document whose purapDocumentIdentifier matches the input parameter.
     */
    public CreditMemoDocument getCreditMemoDocumentById(Integer purchasingDocumentIdentifier);

    /**
     * Makes call to dao to check for duplicate credit memos, and if one is found a message is returned. A duplicate error happens
     * if there is an existing credit memo with the same vendor number and credit memo number as the one which is being created, or
     * same vendor number and credit memo date.
     * 
     * @param cmDocument - CreditMemoDocument to run duplicate check on.
     * @return String - message indicating a duplicate was found.
     */
    public String creditMemoDuplicateMessages(CreditMemoDocument cmDocument);

    /**
     * Iterates through the items of the purchase order document and checks for items that have been invoiced.
     * 
     * @param poDocument - purchase order document containing the lines to check.
     * @return List<PurchaseOrderItem> - list of invoiced items found.
     */
    public List<PurchaseOrderItem> getPOInvoicedItems(PurchaseOrderDocument poDocument);

    /**
     * Persists the credit memo without business rule checks.
     * 
     * @param creditMemoDocument - credit memo document to save.
     */
    public void saveDocumentWithoutValidation(CreditMemoDocument creditMemoDocument);

    /**
     * Persists the credit memo with business rule checks.
     * 
     * @param creditMemoDocument - credit memo document to save.
     */
    public void populateAndSaveCreditMemo(CreditMemoDocument creditMemoDocument);

    /**
     * Performs the credit memo item extended price calculation.
     * 
     * @param cmDocument - credit memo document to calculate.
     */
    public void calculateCreditMemo(CreditMemoDocument cmDocument);

    /**
     * Marks a credit memo as on hold.
     * 
     * @param cmDocument - credit memo document to hold.
     * @param note - note explaining why the document is being put on hold.
     * @throws Exception
     */
    public void addHoldOnCreditMemo(CreditMemoDocument cmDocument, String note) throws Exception;

    /**
     * Determines if the document can be put on hold and if the user has permission to do so.
     * Must be an Accounts Payable user, credit memo not already on hold, extracted date is null, and credit memo 
     * status approved or complete.
     * 
     * 
     * @param cmDocument - credit memo document to hold.
     * @param user - user requesting the hold.
     * @return boolean - true if hold can occur, false if not allowed.
     */
    public boolean canHoldCreditMemo(CreditMemoDocument cmDocument, UniversalUser user);

    /**
     * Removes a hold on the credit memo document.
     * 
     * @param cmDocument - credit memo document to remove hold on.
     * @param note - note explaining why the credit memo is being taken off hold.
     */
    public void removeHoldOnCreditMemo(CreditMemoDocument cmDocument, String note) throws Exception;

    /**
     * Determines if the document can be taken off hold and if the given user has permission to do so.
     * Must be person who put credit memo on hold or accounts payable supervisor and credit memo must be on hold.
     * 
     * @param cmDocument - credit memo document that is on hold.
     * @param user - user requesting to remove the hold.
     * @return boolean - true if user can take document off hold, false if they cannot.
     */
    public boolean canRemoveHoldCreditMemo(CreditMemoDocument cmDocument, UniversalUser user);

    /**
     * Determines if the document can be canceled and if the given user has permission to do so.
     * Document can be canceled if not in canceled status already, extracted date is null, hold indicator is false, and user is
     * member of the accounts payable workgroup.
     * 
     * @param cmDocument - credit memo document to cancel.
     * @param user - user requesting the cancel.
     * @return boolean - true if document can be canceled, false if it cannot be.
     */
    public boolean canCancelCreditMemo(CreditMemoDocument cmDocument, UniversalUser user);

    /**
     * This is called by PDP to cancel a CreditMemoDocument that has already been extracted     
     * @param cmDocument  The credit memo document to be resetted.
     * @param note        The note to be added to the credit memo document.
     */
    public void resetExtractedCreditMemo(CreditMemoDocument cmDocument, String note);

    /**
     * This is called by PDP to cancel a CreditMemoDocument that has already been extracted
     * 
     * @param cmDocument  The credit memo document to be canceled.
     * @param note        The note to be added to the document to be canceled.
     */
    public void cancelExtractedCreditMemo(CreditMemoDocument cmDocument, String note);

    /**
     * Reopens the purchase order document related to the given credit memo
     * document if it is closed.
     * 
     * @param cmDocument  The credit memo document to be used to obtained the 
     *                    purchase order document to be closed.
     */
    public void reopenClosedPO(CreditMemoDocument cmDocument);

    /**
     * Mark a credit memo is being used on a payment
     * 
     * @param cm           The credit memo document to be marked as paid.
     * @param processDate  The date to be set as the credit memo's paid timestamp.
     */
    public void markPaid(CreditMemoDocument cm, Date processDate);
}
