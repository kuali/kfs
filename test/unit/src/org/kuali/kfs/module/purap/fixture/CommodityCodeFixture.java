/*
 * Copyright 2008 The Kuali Foundation.
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

import org.kuali.module.vendor.bo.CommodityCode;

public enum CommodityCodeFixture {

    COMMODITY_CODE_BASIC_ACTIVE ( 
            "517211",  //purchasingCommodityCode
            "Paging", //commodityDescription
            false,    //salesTaxIndicator
            false,    //restrictedItemsIndicator
            null,     //restrictedMaterialCode
            true      //active
            ),
    COMMODITY_CODE_BASIC_ACTIVE_2 ( 
                    "311311",  //purchasingCommodityCode
                    "Sugarcane Mills", //commodityDescription
                    false,    //salesTaxIndicator
                    false,    //restrictedItemsIndicator
                    null,     //restrictedMaterialCode
                    true      //active
                    ),            
    COMMODITY_CODE_BASIC_INACTIVE ( 
            "516110",  //purchasingCommodityCode
            "Internet Publishing and Broadcasting", //commodityDescription
            false,    //salesTaxIndicator
            false,    //restrictedItemsIndicator
            null,     //restrictedMaterialCode
            false      //active
            ),      
    COMMODITY_CODE_NON_EXISTENCE ( 
            "asdfg",  //purchasingCommodityCode
            null, //commodityDescription
            false,    //salesTaxIndicator
            false,    //restrictedItemsIndicator
            null,     //restrictedMaterialCode
            false      //active
            ),         
    COMMODITY_CODE_WITH_RESTRICTED_MATERIAL("516110", // purchasingCommodityCode
            "Internet Publishing and Broadcasting", // commodityDescription
            false, // salesTaxIndicator
            true, // restrictedItemsIndicator
            null, // restrictedMaterialCode
            true // active
            ),               
            ;
    
    private String purchasingCommodityCode;
    private String commodityDescription;
    private boolean salesTaxIndicator;
    private boolean restrictedItemsIndicator;
    private String restrictedMaterialCode;
    private boolean active;
    
    private CommodityCodeFixture (String purchasingCommodityCode, String commodityDescription, boolean salesTaxIndicator, boolean restrictedItemsIndicator, String restrictedMaterialCode, boolean active) {
        this.purchasingCommodityCode = purchasingCommodityCode;
        this.commodityDescription = commodityDescription;
        this.salesTaxIndicator = salesTaxIndicator;
        this.restrictedItemsIndicator = restrictedItemsIndicator;
        this.restrictedMaterialCode = restrictedMaterialCode;
        this.active = active;
    }
    
    public CommodityCode createCommodityCode() {
        CommodityCode cc = new CommodityCode();
        cc.setPurchasingCommodityCode(purchasingCommodityCode);
        cc.setCommodityDescription(commodityDescription);
        cc.setSalesTaxIndicator(salesTaxIndicator);
        cc.setRestrictedItemsIndicator(restrictedItemsIndicator);
        cc.setRestrictedMaterialCode(restrictedMaterialCode);
        cc.setActive(active);
        cc.refreshNonUpdateableReferences();
        
        return cc;
    }
}
