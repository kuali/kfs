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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.util.VendorGroupingHelper;

/**
 * Defines methods that must be implemented by a CreditMemoService implementation.
 */
public interface CreditMemoService extends AccountsPayableDocumentSpecificService {

    /**
     * Populates the document from either the associated payment request document, purchase order document, or vendor detail based
     * on the credit memo type.
     * 
     * @param cmDocument - Credit Memo Document to Populate
     */
    public void populateDocumentAfterInit(VendorCreditMemoDocument cmDocument);

    /**
     * Gets the Credit memos that can be extracted.
     * 
     * @param chartCode Chart to select from.
     * @return Iterator of credit memos.
     */
    public List<VendorCreditMemoDocument> getCreditMemosToExtract(String chartCode);

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
    public Collection<VendorCreditMemoDocument> getCreditMemosToExtractByVendor( String chartCode, VendorGroupingHelper vendor );
    
    /**
     * Get a credit memo by document number.
     * 
     * @param documentNumber  The document number of the credit memo to be retrieved.
     * @return                The credit memo document whose document number matches the input parameter.
     */
    public VendorCreditMemoDocument getCreditMemoByDocumentNumber(String documentNumber);

    /**
     * Retrieves the Credit Memo document by the purapDocumentIdentifier.
     * 
     * @param purchasingDocumentIdentifier  The purapDocumentIdentifier of the credit memo to be retrieved.
     * @return                              The credit memo document whose purapDocumentIdentifier matches the input parameter.
     */
    public VendorCreditMemoDocument getCreditMemoDocumentById(Integer purchasingDocumentIdentifier);

    /**
     * Makes call to dao to check for duplicate credit memos, and if one is found a message is returned. A duplicate error happens
     * if there is an existing credit memo with the same vendor number and credit memo number as the one which is being created, or
     * same vendor number and credit memo date.
     * 
     * @param cmDocument - CreditMemoDocument to run duplicate check on.
     * @return String - message indicating a duplicate was found.
     */
    public String creditMemoDuplicateMessages(VendorCreditMemoDocument cmDocument);

    /**
     * Iterates through the items of the purchase order document and checks for items that have been invoiced.
     * 
     * @param poDocument - purchase order document containing the lines to check.
     * @return List<PurchaseOrderItem> - list of invoiced items found.
     */
    public List<PurchaseOrderItem> getPOInvoicedItems(PurchaseOrderDocument poDocument);

    /**
     * Persists the credit memo with business rule checks.
     * 
     * @param creditMemoDocument - credit memo document to save.
     */
    public void populateAndSaveCreditMemo(VendorCreditMemoDocument creditMemoDocument);

    /**
     * Performs the credit memo item extended price calculation.
     * 
     * @param cmDocument - credit memo document to calculate.
     */
    public void calculateCreditMemo(VendorCreditMemoDocument cmDocument);

    /**
     * Marks a credit memo as on hold.
     * 
     * @param cmDocument - credit memo document to hold.
     * @param note - note explaining why the document is being put on hold.
     * @return the CreditMemoDocument with updated information.
     * @throws Exception
     */
    public VendorCreditMemoDocument addHoldOnCreditMemo(VendorCreditMemoDocument cmDocument, String note) throws Exception;

    /**
     * Removes a hold on the credit memo document.
     * 
     * @param cmDocument - credit memo document to remove hold on.
     * @param note - note explaining why the credit memo is being taken off hold.
     * @return the CreditMemoDocument with updated information.
     */
    public VendorCreditMemoDocument removeHoldOnCreditMemo(VendorCreditMemoDocument cmDocument, String note) throws Exception;

    /**
     * This is called by PDP to cancel a CreditMemoDocument that has already been extracted     
     * @param cmDocument  The credit memo document to be resetted.
     * @param note        The note to be added to the credit memo document.
     */
    public void resetExtractedCreditMemo(VendorCreditMemoDocument cmDocument, String note);

    /**
     * This is called by PDP to cancel a CreditMemoDocument that has already been extracted
     * 
     * @param cmDocument  The credit memo document to be canceled.
     * @param note        The note to be added to the document to be canceled.
     */
    public void cancelExtractedCreditMemo(VendorCreditMemoDocument cmDocument, String note);

    /**
     * Reopens the purchase order document related to the given credit memo
     * document if it is closed.
     * 
     * @param cmDocument  The credit memo document to be used to obtained the 
     *                    purchase order document to be closed.
     */
    public void reopenClosedPO(VendorCreditMemoDocument cmDocument);

    /**
     * Mark a credit memo is being used on a payment
     * 
     * @param cm           The credit memo document to be marked as paid.
     * @param processDate  The date to be set as the credit memo's paid timestamp.
     */
    public void markPaid(VendorCreditMemoDocument cm, Date processDate);
    
    /**
     * Determines if there are active credit memos for a purchase order.
     * 
     * @param purchaseOrderIdentifier
     * @return
     */
    public boolean hasActiveCreditMemosForPurchaseOrder(Integer purchaseOrderIdentifier);

}

