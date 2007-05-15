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

import org.kuali.module.purap.document.PurchaseOrderDocument;

public interface PurchaseOrderDao {

    /**
     * 
     * This method saves a PurchaseOrderDocument to the database
     * @param purchaseOrderDocument
     */
    public void save(PurchaseOrderDocument purchaseOrderDocument);
   
    /**
     * 
     * This method gets a PurchaseOrderDocument by the
     * purapDocumentIdentifier.
     * 
     * @param id Integer the purapDocumentIdentifier.
     * @return PurchaseOrderDocument 
     */
    public PurchaseOrderDocument getPurchaseOrderById(Integer id);
    
    /**
     * 
     * This method gets the current PurchaseOrderDocument by the
     * purapDocumentIdentifier.
     * 
     * @param id Integer the purapDocumentIdentifier.
     * @return PurchaseOrderDocument 
     */
    public PurchaseOrderDocument getCurrentPurchaseOrder(Integer id);

    /**
     * 
     * This method gets the oldest, which is the first PurchaseOrderDocument
     * that had been created in the database given the purapDocumentIdentifier.
     * 
     * @param id Integer the purapDocumentIdentifier.
     * @param po the Purchase Order
     * @return PurchaseOrderDocument
     */
    public PurchaseOrderDocument getOldestPurchaseOrder(Integer id, PurchaseOrderDocument po);
    
    /**
     * 
     * This method gets the PurchaseOrderDocument whose status is Pending Print
     * and purapDocumentIdentifier is the same as the id in the input parameter.
     * 
     * @param id Integer the purapDocumentIdentifier.
     * @return PurchaseOrderDocument
     */
    public PurchaseOrderDocument getPurchaseOrderInPendingPrintStatus(Integer id);
    
    /**
     * 
     * This method gets the PurchaseOrderDocument whose document number is the
     * same as the document number in the input parameter.
     * 
     * @param documentNumber the document number of the PO we're looking for
     * @return PurchaseOrderDocument
     */
    public PurchaseOrderDocument getPurchaseOrderByDocumentNumber(String documentNumber); 
}
