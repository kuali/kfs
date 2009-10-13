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
package org.kuali.kfs.module.purap.fixture;

import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;

public enum SystemParameterBelowTheLineItemPurchaseOrderFixture {
    PO_VALID_BELOW_LINE_ITEMS(
            new PurchaseOrderItemFixture[] { PurchaseOrderItemFixture.PO_VALID_FREIGHT_ITEM, PurchaseOrderItemFixture.PO_VALID_SHIPPING_AND_HANDLING_ITEM } // purchaseOrderItemMultiFixtures
    ),
    PO_INVALID_BELOW_LINE_ITEMS(
            new PurchaseOrderItemFixture[] { PurchaseOrderItemFixture.PO_WITH_MISC_CREDIT_ITEM } // purchaseOrderItemMultiFixtures
    ),
    PO_WITH_NEGATIVE_BELOW_LINE_ITEMS(
            new PurchaseOrderItemFixture[] { PurchaseOrderItemFixture.PO_WITH_NEGATIVE_FREIGHT_ITEM, PurchaseOrderItemFixture.PO_WITH_NEGATIVE_SHIPPING_AND_HANDLING_ITEM } // purchaseOrderItemMultiFixtures
    ),    
    PO_WITH_ZERO_BELOW_LINE_ITEMS(
            new PurchaseOrderItemFixture[] { PurchaseOrderItemFixture.PO_WITH_ZERO_FREIGHT_ITEM, PurchaseOrderItemFixture.PO_WITH_ZERO_SHIPPING_AND_HANDLING_ITEM } // purchaseOrderItemMultiFixtures
    ),    
    PO_WITH_BELOW_LINE_ITEMS_WITHOUT_DESCRIPTION(
            new PurchaseOrderItemFixture[] { PurchaseOrderItemFixture.PO_WITH_MISC_ITEM_NO_DESC } // purchaseOrderItemMultiFixtures
    ),
    PO_WITH_BELOW_LINE_ITEMS_WITH_DESCRIPTION(
            new PurchaseOrderItemFixture[] { PurchaseOrderItemFixture.PO_VALID_FREIGHT_ITEM, PurchaseOrderItemFixture.PO_VALID_SHIPPING_AND_HANDLING_ITEM } // purchaseOrderItemMultiFixtures
    )
    ;
    private PurchaseOrderItemFixture[] purchaseOrderItemFixtures;
    
    
    private SystemParameterBelowTheLineItemPurchaseOrderFixture(
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
