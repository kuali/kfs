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
package org.kuali.kfs.module.purap.document.dataaccess;

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.AutoClosePurchaseOrderView;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;



/**
 * Purchase Order DAO Interface.
 */
public interface PurchaseOrderDao {

    public Integer getPurchaseOrderIdForCurrentPurchaseOrderByRelatedDocId(Integer accountsPayablePurchasingDocumentLinkIdentifier);
    
    public PurchaseOrderDocument getCurrentPurchaseOrder(Integer id);
    
    /**
     * Retrieves the Purchase Order Document's document number using the purapDocumentIdentifier as criteria
     * 
     * @param id - purapDocument Identifier
     * @return - the document number of the purchase order found or null if no purchase order found
     */
    public String getDocumentNumberForPurchaseOrderId(Integer id);

    /**
     * Retrieves the current Purchase Order Document's document number by the purapDocumentIdentifier.
     * 
     * @param id - purapDocument Identifier
     * @return - the document number of the purchase order found or null if no purchase order found
     */
    public String getDocumentNumberForCurrentPurchaseOrder(Integer id);

    /**
     * Retrieves the oldest purchase order's (defined by the one having the smallest document number) document number.
     * 
     * @param id - the purapDocumentIdentifier.
     * @return - the document numbers of the purchase order found or null if none found
     */
    public String getOldestPurchaseOrderDocumentNumber(Integer id);
    
    /**
     * Determines if the purchase order item exists on the current purchase order.
     * 
     * @param poItemLineNumber
     * @param docNumber
     * @return
     */
    public boolean itemExistsOnPurchaseOrder(Integer poItemLineNumber, String docNumber);

    /**
     * This method gets all the PurchaseOrderView objects that relate to POs
     * with no recurring payment type, status of 'OPEN', and total encumbrance
     * of 0 that do not have any of the excluded vendor choice codes.
     * 
     * @param excludedVendorChoiceCodes - list of strings of excluded vendor choice codes
     * @return List of PurchaseOrderAutoClose objects
     */
    public List<AutoClosePurchaseOrderView> getAllOpenPurchaseOrders(List<String> excludedVendorChoiceCodes);    
    
    /**
     * This method gets all the PurchaseOrderView objects that relate to POs
     * with a recurring payment type, status of 'OPEN', and that do not have any 
     * of the excluded vendor choice codes.
     * 
     * @param excludedVendorChoiceCodes - list of strings of excluded vendor choice codes
     * @return List of PurchaseOrderAutoClose objects
     */
    public List<AutoClosePurchaseOrderView> getAutoCloseRecurringPurchaseOrders(List<String> excludedVendorChoiceCodes);
    
    /**
     * This method gets all the Purchase orders that are waiting for faxing
     * @return List of POs
     */
    public List<PurchaseOrderDocument> getPendingPurchaseOrdersForFaxing();
}
