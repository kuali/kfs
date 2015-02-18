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
package org.kuali.kfs.vnd.fixture;

import org.kuali.kfs.vnd.businessobject.CommodityCode;

public enum CommodityCodeFixture {

    COMMODITY_CODE_BASIC_ACTIVE ( 
            "517211",  //purchasingCommodityCode
            "Paging", //commodityDescription
            false,    //salesTaxIndicator
            false,    //restrictedItemsIndicator
            null,     //sensitiveDataCode
            true      //active
            ),
    COMMODITY_CODE_BASIC_ACTIVE_2 ( 
                    "311311",  //purchasingCommodityCode
                    "Sugarcane Mills", //commodityDescription
                    false,    //salesTaxIndicator
                    false,    //restrictedItemsIndicator
                    null,     //sensitiveDataCode
                    true      //active
                    ),            
    COMMODITY_CODE_BASIC_INACTIVE ( 
            "516110",  //purchasingCommodityCode
            "Internet Publishing and Broadcasting", //commodityDescription
            false,    //salesTaxIndicator
            false,    //restrictedItemsIndicator
            null,     //sensitiveDataCode
            false      //active
            ),      
    COMMODITY_CODE_NON_EXISTENCE ( 
            "asdfg",  //purchasingCommodityCode
            null, //commodityDescription
            false,    //salesTaxIndicator
            false,    //restrictedItemsIndicator
            null,     //sensitiveDataCode
            false      //active
            ),         
    COMMODITY_CODE_WITH_SENSITIVE_DATA("516110", // purchasingCommodityCode
            "Internet Publishing and Broadcasting", // commodityDescription
            false, // salesTaxIndicator
            true, // restrictedItemsIndicator
            null, // sensitiveDataCode
            true // active
            ),               
            ;
    
    private String purchasingCommodityCode;
    private String commodityDescription;
    private boolean salesTaxIndicator;
    private boolean restrictedItemsIndicator;
    private String sensitiveDataCode;
    private boolean active;
    
    private CommodityCodeFixture (String purchasingCommodityCode, String commodityDescription, boolean salesTaxIndicator, boolean restrictedItemsIndicator, String sensitiveDataCode, boolean active) {
        this.purchasingCommodityCode = purchasingCommodityCode;
        this.commodityDescription = commodityDescription;
        this.salesTaxIndicator = salesTaxIndicator;
        this.restrictedItemsIndicator = restrictedItemsIndicator;
        this.sensitiveDataCode = sensitiveDataCode;
        this.active = active;
    }
    
    public CommodityCode createCommodityCode() {
        CommodityCode cc = new CommodityCode();
        cc.setPurchasingCommodityCode(purchasingCommodityCode);
        cc.setCommodityDescription(commodityDescription);
        cc.setSalesTaxIndicator(salesTaxIndicator);
        cc.setRestrictedItemsIndicator(restrictedItemsIndicator);
        cc.setSensitiveDataCode(sensitiveDataCode);
        cc.setActive(active);
        cc.refreshNonUpdateableReferences();
        
        return cc;
    }
}
