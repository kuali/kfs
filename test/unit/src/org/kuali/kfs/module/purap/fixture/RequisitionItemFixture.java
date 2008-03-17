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

import org.kuali.module.purap.bo.PurchasingItem;
import org.kuali.module.purap.bo.RequisitionItem;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.vendor.bo.CommodityCode;

public enum RequisitionItemFixture {

    REQ_QTY_UNRESTRICTED_ITEM_1(false, // itemRestrictedIndicator
            PurApItemFixture.BASIC_QTY_ITEM_1, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_1 } // requisitionAccountMultiFixtures
    ),
    REQ_QTY_UNRESTRICTED_ITEM_2(false, // itemRestrictedIndicator
            PurApItemFixture.BASIC_QTY_ITEM_1, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_2 } // requisitionAccountMultiFixtures
    ),
    REQ_QTY_APO_ITEM_1(false, // itemRestrictedIndicator
            PurApItemFixture.APO_QTY_ITEM_1, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.APO_REQ_ACCOUNT_1 } // requisitionAccountMultiFixtures
    ), REQ_SERVICE_APO_ITEM_1(false, // itemRestrictedIndicator
            PurApItemFixture.APO_SERVICE_ITEM_1, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.APO_REQ_ACCOUNT_2, RequisitionAccountingLineFixture.APO_REQ_ACCOUNT_3 } // requisitionAccountMultiFixtures
    ), REQ_FREIGHT_ITEM_1(false, // itemRestrictedIndicator
            PurApItemFixture.APO_FREIGHT_ITEM_1, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.APO_REQ_ACCOUNT_4 } // requisitionAccountMultiFixtures
    ),
    
    REQ_MULTI_ITEM_QUANTITY(false, // itemRestrictedIndicator
            PurApItemFixture.REQ_MULTI_ITEM_QUANTITY, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.REQ_ACCOUNT_MULTI_QUANTITY } // requisitionAccountMultiFixtures
    ),
    REQ_MULTI_ITEM_NON_QUANTITY(false, // itemRestrictedIndicator
            PurApItemFixture.REQ_MULTI_ITEM_NON_QUANTITY, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.REQ_ACCOUNT_MULTI_NON_QUANTITY } // requisitionAccountMultiFixtures
    ),
    REQ_ITEM_NO_APO(false, // itemRestrictedIndicator
            PurApItemFixture.BASIC_QTY_ITEM_NO_APO, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_1 } // requisitionAccountMultiFixtures
    ),
    REQ_ITEM_NO_APO_2(false, // itemRestrictedIndicator
            PurApItemFixture.BASIC_QTY_ITEM_NO_APO, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_2 } // requisitionAccountMultiFixtures
    ),
    REQ_ITEM_NO_APO_TOTAL_NOT_GREATER_THAN_ZERO(false, // itemRestrictedIndicator
            PurApItemFixture.BASIC_QTY_ITEM_NO_APO_TOTAL_NOT_GREATER_THAN_ZERO, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_1 } // requisitionAccountMultiFixtures
    ), 
    REQ_ITEM_NO_APO_RESTRICTED_ITEM (
            true,                              // itemRestrictedIndicator
            PurApItemFixture.APO_QTY_ITEM_1,  // purApItemFixture
            new RequisitionAccountingLineFixture[] {RequisitionAccountingLineFixture.APO_REQ_ACCOUNT_1}   // requisitionAccountMultiFixtures
	),
    REQ_ITEM_NO_APO_CONTAIN_RESTRICTED_ITEM(true, // itemRestrictedIndicator
            PurApItemFixture.BASIC_QTY_ITEM_1, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_1 } // requisitionAccountMultiFixtures
    ),
    REQ_VALID_FREIGHT_ITEM(false, // itemRestrictedIndicator
            PurApItemFixture.VALID_FREIGHT_ITEM, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_1 } // requisitionAccountMultiFixtures
    ),
    REQ_VALID_SHIPPING_AND_HANDLING_ITEM(false, // itemRestrictedIndicator
            PurApItemFixture.VALID_SHIPPING_AND_HANDLING_ITEM, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_1 } // requisitionAccountMultiFixtures
    ),
    REQ_WITH_MISC_CREDIT_ITEM(false, // itemRestrictedIndicator
            PurApItemFixture.VALID_MISC_CREDIT_ITEM, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_1 } // requisitionAccountMultiFixtures
    ),
    REQ_WITH_NEGATIVE_FREIGHT_ITEM(false, // itemRestrictedIndicator
            PurApItemFixture.NEGATIVE_FREIGHT_ITEM, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_1 } // requisitionAccountMultiFixtures
    ),
    REQ_WITH_NEGATIVE_SHIPPING_AND_HANDLING_ITEM(false, // itemRestrictedIndicator
            PurApItemFixture.NEGATIVE_SHIPPING_AND_HANDLING_ITEM, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_1 } // requisitionAccountMultiFixtures
    ),
    REQ_WITH_ZERO_FREIGHT_ITEM(false, // itemRestrictedIndicator
            PurApItemFixture.ZERO_FREIGHT_ITEM, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_1 } // requisitionAccountMultiFixtures
    ),
    REQ_WITH_ZERO_SHIPPING_AND_HANDLING_ITEM(false, // itemRestrictedIndicator
            PurApItemFixture.ZERO_SHIPPING_AND_HANDLING_ITEM, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_1 } // requisitionAccountMultiFixtures
    ),
    REQ_WITH_MISC_ITEM_NO_DESC(false, // itemRestrictedIndicator
            PurApItemFixture.MISC_ITEM_NO_DESC, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_1 } // requisitionAccountMultiFixtures
    ),
    REQ_ITEM_NO_APO_BASIC_ACTIVE_COMMODITY_CODE(false, // itemRestrictedIndicator
            PurApItemFixture.BASIC_QTY_ITEM_NO_APO, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_1 }, // requisitionAccountMultiFixtures
            CommodityCodeFixture.COMMODITY_CODE_BASIC_ACTIVE  //commodityCodeFixture
    ),
    REQ_ITEM_NO_APO_BASIC_INACTIVE_COMMODITY_CODE(false, // itemRestrictedIndicator
            PurApItemFixture.BASIC_QTY_ITEM_NO_APO, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_1 }, // requisitionAccountMultiFixtures
            CommodityCodeFixture.COMMODITY_CODE_BASIC_INACTIVE  //commodityCodeFixture
    ),
    REQ_ITEM_NO_APO_NON_EXISTENCE_COMMODITY_CODE(false, // itemRestrictedIndicator
            PurApItemFixture.BASIC_QTY_ITEM_NO_APO, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_1 }, // requisitionAccountMultiFixtures
            CommodityCodeFixture.COMMODITY_CODE_NON_EXISTENCE  //commodityCodeFixture
    ),
    ;

    private boolean itemRestrictedIndicator;
    private PurApItemFixture purApItemFixture;
    private RequisitionAccountingLineFixture[] requisitionAccountingLineFixtures;
    private CommodityCodeFixture commodityCodeFixture;


    private RequisitionItemFixture(boolean itemRestrictedIndicator, PurApItemFixture purApItemFixture, RequisitionAccountingLineFixture[] requisitionAccountingLineFixtures) {
        this.itemRestrictedIndicator = itemRestrictedIndicator;
        this.purApItemFixture = purApItemFixture;
        this.requisitionAccountingLineFixtures = requisitionAccountingLineFixtures;
    }
    
    private RequisitionItemFixture(boolean itemRestrictedIndicator, PurApItemFixture purApItemFixture, RequisitionAccountingLineFixture[] requisitionAccountingLineFixtures, CommodityCodeFixture commodityCodeFixture) {
        this.itemRestrictedIndicator = itemRestrictedIndicator;
        this.purApItemFixture = purApItemFixture;
        this.requisitionAccountingLineFixtures = requisitionAccountingLineFixtures;
        this.commodityCodeFixture = commodityCodeFixture;
    }


    public void addTo(RequisitionDocument requisitionDocument) {
        RequisitionItem item = null;
        boolean active = true;
        item = (RequisitionItem) this.createRequisitionItem();
        if (item.getCommodityCode() != null) {
            active = item.getCommodityCode().isActive();
        }
        requisitionDocument.addItem(item);
        //Just for unit tests, we need these following lines so that we could set the commodity codes active status to true/false
        //as we need to.
        if (item.getCommodityCode() != null) {
            item.getCommodityCode().setActive(active);
        }
        // iterate over the accounts
        for (RequisitionAccountingLineFixture requisitionAccountMultiFixture : requisitionAccountingLineFixtures) {
            requisitionAccountMultiFixture.addTo(item);
        }
    }

    public PurchasingItem createRequisitionItem() {
        RequisitionItem item = (RequisitionItem) purApItemFixture.createPurApItem(RequisitionItem.class);
        item.setItemRestrictedIndicator(itemRestrictedIndicator);
        if (commodityCodeFixture != null) {
            CommodityCode commodityCode = commodityCodeFixture.createCommodityCode();
            item.setCommodityCode(commodityCode);
            item.setPurchasingCommodityCode(commodityCode.getPurchasingCommodityCode());
        }
        return item;
    }

    /**
     * @return
     */
    // public PurApItem createRequisitionItem() {
    // return createRequisitionItem(defaultItemFixture);
    // }
    //
    //    
    //    
    // /**
    // *
    // * This method adds an item to a document
    // * @param document
    // * @param purApItemFixture
    // * @throws IllegalAccessException
    // * @throws InstantiationException
    // */
    // public RequisitionItem addTo(PurchasingAccountsPayableDocument document, PurApItemFixture purApItemFixture)
    // throws IllegalAccessException, InstantiationException {
    // RequisitionItem item = (RequisitionItem)this.createRequisitionItem(purApItemFixture);
    // document.addItem(item);
    // return item;
    //        
    // }
}
