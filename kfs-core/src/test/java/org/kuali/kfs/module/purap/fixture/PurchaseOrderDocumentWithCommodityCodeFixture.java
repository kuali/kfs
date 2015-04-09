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
import org.kuali.kfs.vnd.businessobject.VendorCommodityCode;
import org.kuali.kfs.vnd.fixture.VendorCommodityCodeFixture;

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
    PO_VALID_ACTIVE_COMMODITY_CODE_WITH_VENDOR_COMMODITY_CODE(
            new PurchaseOrderItemFixture[] { PurchaseOrderItemFixture.PO_ITEM_BASIC_ACTIVE_COMMODITY_CODE } , // purchaseOrderItemMultiFixtures
            VendorCommodityCodeFixture.DEFAULT_VENDOR_COMMODITY_CODE_ACTIVE),
    ;
    
    private PurchaseOrderItemFixture[] purchaseOrderItemFixtures;
    private VendorCommodityCodeFixture vendorCommodityCodeFixture;
    
    private PurchaseOrderDocumentWithCommodityCodeFixture(
            PurchaseOrderItemFixture[] purchaseOrderItemFixtures) {
        this.purchaseOrderItemFixtures = purchaseOrderItemFixtures;
    }

    private PurchaseOrderDocumentWithCommodityCodeFixture(
            PurchaseOrderItemFixture[] purchaseOrderItemFixtures,
            VendorCommodityCodeFixture vendorCommodityCodeFixture) {
        this.purchaseOrderItemFixtures = purchaseOrderItemFixtures;
        this.vendorCommodityCodeFixture = vendorCommodityCodeFixture;
    }
    
    public PurchaseOrderDocument createPurchaseOrderDocument() {
        PurchaseOrderDocument doc = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS.createPurchaseOrderDocument();
        //Removes all the existing item from doc, we'll add the appropriate items later.
        doc.getItems().clear();
        for (PurchaseOrderItemFixture purchaseOrderItemFixture : purchaseOrderItemFixtures) { 
            purchaseOrderItemFixture.addTo(doc);
        }
        
        if (vendorCommodityCodeFixture != null) {
            VendorCommodityCode vcc = vendorCommodityCodeFixture.createVendorCommodityCode();
            doc.getVendorDetail().getVendorCommodities().add(vcc);    
        }
        return doc;
    }
}
