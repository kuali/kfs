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
package org.kuali.module.purap.fixtures;

import org.kuali.module.purap.document.PurchaseOrderDocument;

public enum PurchaseOrderDocumentWithCommodityCodeFixture {

    PO_VALID_ACTIVE_COMMODITY_CODE(
            new PurchaseOrderItemFixture[] { PurchaseOrderItemFixture.PO_ITEM_BASIC_ACTIVE_COMMODITY_CODE} // purchaseOrderItemMultiFixtures
    ),  
    PO_VALID_INACTIVE_COMMODITY_CODE(
            new PurchaseOrderItemFixture[] { PurchaseOrderItemFixture.PO_ITEM_BASIC_INACTIVE_COMMODITY_CODE} // purchaseOrderItemMultiFixtures
    ),   
    PO_NON_EXISTENCE_COMMODITY_CODE(
            new PurchaseOrderItemFixture[] { PurchaseOrderItemFixture.PO_ITEM_NON_EXISTENCE_COMMODITY_CODE} // purchaseOrderItemMultiFixtures
    ),     
    ;
    
    private PurchaseOrderItemFixture[] purchaseOrderItemFixtures;
    
    
    private PurchaseOrderDocumentWithCommodityCodeFixture(
            PurchaseOrderItemFixture[] purchaseOrderItemFixtures) {
        this.purchaseOrderItemFixtures = purchaseOrderItemFixtures;
    }
    
    public PurchaseOrderDocument createPurchaseOrderDocument() {
        PurchaseOrderDocument doc = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS.createPurchaseOrderDocument();
        //Removes all the existing item from doc, we'll add the appropriate items later.
        doc.getItems().clear();
        for (PurchaseOrderItemFixture purchaseOrderItemFixture : purchaseOrderItemFixtures) { 
            purchaseOrderItemFixture.addTo(doc);
        }
        
        return doc;
    }
}
