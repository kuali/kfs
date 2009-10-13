/*
 * Copyright 2007-2008 The Kuali Foundation
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
