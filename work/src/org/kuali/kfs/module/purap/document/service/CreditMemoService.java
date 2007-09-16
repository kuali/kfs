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

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;

/**
 * Defines methods that must be implemented by a CreditMemoService implementation.
 */
public interface CreditMemoService extends AccountsPayableDocumentSpecificService {

    /**
     * Gets the Credit memos that can be extracted
     * 
     * @param chartCode Chart to select from
     * @return Iterator of credit memos
     */
    public Iterator<CreditMemoDocument> getCreditMemosToExtract(String chartCode);

    /**
     * Retrieves the Credit Memo document by document number.
     */
    public CreditMemoDocument getCreditMemoDocumentById(Integer purchasingDocumentIdentifier);

    /**
     * Makes call to dao to check for duplicate credit memos, and if one is found a message is returned. A duplicate error happens
     * if there is an existing credit memo with the same vendor number and credit memo number as the one which is being created, or
     * same vendor number and credit memo date.
     * 
     * @param cmDocument - CreditMemoDocument to run duplicate check on
     * @return String - message indicating a duplicate was found
     */
    public String creditMemoDuplicateMessages(CreditMemoDocument cmDocument);

    /**
     * Iterates through the items of the purchase order document and checks for items that have been invoiced.
     * 
     * @param poDocument - purchase order document containing the lines to check
     * @return List<PurchaseOrderItem> - list of invoiced items found
     */
    public List<PurchaseOrderItem> getPOInvoicedItems(PurchaseOrderDocument poDocument);

    /**
     * Persists the credit memo without business rule checks.
     * 
     * @param creditMemoDocument - credit memo document to save
     */
    public void saveDocumentWithoutValidation(CreditMemoDocument creditMemoDocument);

    /**
     * Persists the credit memo with business rule checks.
     * 
     * @param creditMemoDocument - credit memo document to save
     */
    public void populateAndSaveCreditMemo(CreditMemoDocument creditMemoDocument);

    /**
     * Performs the credit memo item extended price calculation.
     * 
     * @param cmDocument - credit memo document to calculate
     */
    public void calculateCreditMemo(CreditMemoDocument cmDocument);

    /**
     * Marks a credit memo as on hold.
     * 
     * @param cmDocument - credit memo document to hold
     * @param note - note explaining why the document is being put on hold
     * @throws Exception
     */
    public void addHoldOnCreditMemo(CreditMemoDocument cmDocument, String note) throws Exception;

    /**
     * Determines if the document can be put on hold and if the user has permission to do so.
     * 
     * @param cmDocument - credit memo document to hold
     * @param user - user requesting the hold
     * @return boolean - true if hold can occur, false if not allowed
     */
    public boolean canHoldCreditMemo(CreditMemoDocument cmDocument, UniversalUser user);

    /**
     * Removes a hold on the credit memo document.
     * 
     * @param cmDocument - credit memo document to remove hold on
     * @param note - note explaining why the cm is being taken off hold
     */
    public void removeHoldOnCreditMemo(CreditMemoDocument cmDocument, String note) throws Exception;

    /**
     * Determines if the document can be taken off hold and if the given user has permission to do so.
     * 
     * @param cmDocument - credit memo document that is on hold
     * @param user - user requesting to remove the hold
     * @return boolean - true if user can take document off hold, false if they cannot
     */
    public boolean canRemoveHoldCreditMemo(CreditMemoDocument cmDocument, UniversalUser user);

    /**
     * Determines if the document can be canceled and if the given user has permission to do so.
     * 
     * @param cmDocument - credit memo document to cancel
     * @param user - user requesting the cancel
     * @return boolean - true if document can be canceled, false if it cannot be
     */
    public boolean canCancelCreditMemo(CreditMemoDocument cmDocument, UniversalUser user);

    public void resetExtractedCreditMemo(CreditMemoDocument cmDocument, String note);

    public void cancelExtractedCreditMemo(CreditMemoDocument cmDocument, String note);

    public void reopenClosedPO(CreditMemoDocument cmDocument);

    /**
     * Mark a credit memo is being used on a payment
     * 
     * @param cm
     * @param processDate
     */
    public void markPaid(CreditMemoDocument cm,Date processDate);
}
