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

import java.util.List;

import org.kuali.module.purap.document.PurchaseOrderDocument;

public interface PurchaseOrderDao {

    /**
     * This method gets the Purchase Order Document's document number
     * using the purapDocumentIdentifier as criteria
     * 
     * @param id Integer the purapDocumentIdentifier.
     * @return the document number of the purchase order found or null if no purchase order found 
     */
    public String getDocumentNumberForPurchaseOrderId(Integer id);
    
    /**
     * This method gets the current Purchase Order Document's document number
     * by the purapDocumentIdentifier.
     * 
     * @param id Integer the purapDocumentIdentifier.
     * @return the document number of the purchase order found or null if no purchase order found 
     */
    public String getDocumentNumberForCurrentPurchaseOrder(Integer id);

    /**
     * This method gets the oldest purchase order's (defined by the one having the smallest
     * document number) document number.
     * 
     * @param id Integer the purapDocumentIdentifier.
     * @return the document numbers of the purchase order found or null if none found
     */
    public String getOldestPurchaseOrderDocumentNumber(Integer id);
}
