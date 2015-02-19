/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
