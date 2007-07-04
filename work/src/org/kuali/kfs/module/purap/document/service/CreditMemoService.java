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

import java.util.Collection;
import java.util.List;

import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;

/**
 * Defines methods that must be implemented by a CreditMemoService implementation.
 */
public interface CreditMemoService {

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
     * Get all credit Memo associated with the given purchasing order
     * 
     * @param purchaseOrderIdentifier ID of the PO associated with the Credit Memo
     * @return List - List of found CreditMemoDocuments
     */
   // public List<CreditMemoDocument> getCreditMemosByPurchaseOrder(Integer purchaseOrderIdentifier);

    /**
     * Get all credit Memo associated with the given purchasing order with search result limit
     * 
     * @param purchaseOrderIdentifier ID of the PO associated with the Credit Memo
     * @param returnListMax - Max search results to return
     * @return List - List of found CreditMemoDocuments
     */
    //public List<CreditMemoDocument> getCreditMemosByPurchaseOrder(Integer purchaseOrderIdentifier, Integer returnListMax);

    /**
     * Gets all credit memo documents associated with the given purchase order and has the status equal to one of the given status
     * codes.
     * 
     * @param purchaseOrderIdentifier ID of the PO associated with the Credit Memo
     * @param statusCodes - List of credit memo status codes which returned documents can have
     * @return List - List of found CreditMemoDocuments
     */
 //   public List<CreditMemoDocument> getCreditMemosByPurchaseOrderAndStatus(Integer purchaseOrderIdentifier, Collection<String> statusCodes);

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
     * @param cmDocument - credit memo document to save
     */
    public void save(CreditMemoDocument cmDocument);
    
    /**
     * Performs the credit memo item extended price calculation.
     * 
     * @param cmDocument - credit memo document to calculate
     */
    public void calculateCreditMemo(CreditMemoDocument cmDocument);
}
