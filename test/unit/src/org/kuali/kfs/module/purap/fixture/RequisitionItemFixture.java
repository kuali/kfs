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

import org.kuali.kfs.module.purap.businessobject.PurchasingItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.vnd.businessobject.CommodityCode;
import org.kuali.kfs.vnd.fixture.CommodityCodeFixture;
import org.kuali.rice.krad.util.ObjectUtils;

public enum RequisitionItemFixture {

    REQ_QTY_UNRESTRICTED_ITEM_1(false, // itemRestrictedIndicator
            PurApItemFixture.BASIC_QTY_ITEM_1, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_1 } // requisitionAccountMultiFixtures
    ),
    REQ_QTY_UNRESTRICTED_ITEM_2(false, // itemRestrictedIndicator
            PurApItemFixture.BASIC_QTY_ITEM_1, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_2 } // requisitionAccountMultiFixtures
    ),
    REQ_QTY_UNRESTRICTED_ITEM_3(false, // itemRestrictedIndicator
            PurApItemFixture.BASIC_QTY_ITEM_1, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.APO_REQ_ACCOUNT_2, RequisitionAccountingLineFixture.APO_REQ_ACCOUNT_3 } // requisitionAccountMultiFixtures
    ),
    REQ_QTY_ITEM_NEGATIVE_AMOUNT(false, // itemRestrictedIndicator
            PurApItemFixture.BASIC_QTY_ITEM_NEGATIVE, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.REQ_ACCOUNT_NEGATIVE_AMOUNT } // requisitionAccountMultiFixtures
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
    REQ_ITEM_PERFORMANCE(false, // itemRestrictedIndicator
            PurApItemFixture.BASIC_QTY_ITEM_PERFORMANCE, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.PERFORMANCE_ACCOUNT } // requisitionAccountMultiFixtures
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
    REQ_ITEM_APO_BASIC_INACTIVE_COMMODITY_CODE(false, // itemRestrictedIndicator
            PurApItemFixture.APO_QTY_ITEM_1, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_1 }, // requisitionAccountMultiFixtures
            CommodityCodeFixture.COMMODITY_CODE_BASIC_INACTIVE  //commodityCodeFixture
    ),
    REQ_ITEM_APO_COMMODITY_CODE_WITH_SENSITIVE_DATA(false, // itemRestrictedIndicator
            PurApItemFixture.APO_QTY_ITEM_1, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_1 }, // requisitionAccountMultiFixtures
            CommodityCodeFixture.COMMODITY_CODE_WITH_SENSITIVE_DATA  //commodityCodeFixture
    ),
    REQ_ITEM_INVALID_QUANTITY_BASED_NO_QUANTITY(false, // itemRestrictedIndicator
            PurApItemFixture.INVALID_QTY_ITEM_NULL_QUANTITY, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_1 } // requisitionAccountMultiFixtures
    ),
    REQ_ITEM_VALID_CAPITAL_ASSET(false, // itemRestrictedIndicator
            PurApItemFixture.APO_QTY_ITEM_1, // purApItemFixture
            new RequisitionAccountingLineFixture[] { 
                RequisitionAccountingLineFixture.APO_ACCOUNT_VALID_CAPITAL_ASSET_OBJECT_CODE } // requisitionAccountMultiFixtures
    ),
    REQ_ITEM_INVALID_CAPITAL_ASSET(false, // itemRestrictedIndicator
            PurApItemFixture.APO_QTY_ITEM_1, // purApItemFixture
            new RequisitionAccountingLineFixture[] { 
                RequisitionAccountingLineFixture.APO_ACCOUNT_VALID_CAPITAL_ASSET_OBJECT_CODE_50_PERCENT,
                RequisitionAccountingLineFixture.APO_ACCOUNT_VALID_EXPENSE_OBJECT_CODE_50_PERCENT } // requisitionAccountMultiFixtures
    ),   
    REQ_QTY_B2B_ITEM_1(false, // itemRestrictedIndicator
            PurApItemFixture.BASIC_B2B_QTY_ITEM_1, // purApItemFixture
            new RequisitionAccountingLineFixture[] { RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_1 } // requisitionAccountMultiFixtures
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
        boolean restrictedItemsIndicator = true;
        item = (RequisitionItem) this.createRequisitionItem();
        if (item.getCommodityCode() != null) {
            active = item.getCommodityCode().isActive();
            restrictedItemsIndicator = item.getCommodityCode().isRestrictedItemsIndicator();
        }
        requisitionDocument.addItem(item);
        //Just for unit tests, we need these following lines so that we could set the commodity codes active status to true/false
        //and set the restricted indicator to true/false as we need to.
        if (ObjectUtils.isNotNull(item.getCommodityCode())) {
            item.getCommodityCode().setActive(active);
            item.getCommodityCode().setRestrictedItemsIndicator(restrictedItemsIndicator);
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

    public PurchasingItem createRequisitionItemForCapitalAsset() {
        RequisitionItem item = (RequisitionItem)createRequisitionItem();
        if (requisitionAccountingLineFixtures.length > 0) {
            for(int i = 0; i < requisitionAccountingLineFixtures.length ; i++) {
                requisitionAccountingLineFixtures[i].addTo(item);
            }
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
