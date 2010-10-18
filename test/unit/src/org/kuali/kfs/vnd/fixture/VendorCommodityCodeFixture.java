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
package org.kuali.kfs.vnd.fixture;

import org.kuali.kfs.vnd.businessobject.CommodityCode;
import org.kuali.kfs.vnd.businessobject.VendorCommodityCode;

public enum VendorCommodityCodeFixture {

    DEFAULT_VENDOR_COMMODITY_CODE_ACTIVE (1000, 0, true, true, CommodityCodeFixture.COMMODITY_CODE_BASIC_ACTIVE_2),;

    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private boolean commodityDefaultIndicator;
    private boolean active;

    private CommodityCodeFixture commodityCodeFixture;
    private VendorCommodityCodeFixture(Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, boolean commodityDefaultIndicator, boolean active, CommodityCodeFixture commodityCodeFixture) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
        this.commodityDefaultIndicator = commodityDefaultIndicator;
        this.active = active;
        this.commodityCodeFixture = commodityCodeFixture;
    }

    public VendorCommodityCode createVendorCommodityCode() {
        VendorCommodityCode vcc = new VendorCommodityCode();
        vcc.setVendorHeaderGeneratedIdentifier(this.vendorHeaderGeneratedIdentifier);
        vcc.setVendorDetailAssignedIdentifier(this.vendorDetailAssignedIdentifier);
        vcc.setCommodityDefaultIndicator(this.commodityDefaultIndicator);
        vcc.setActive(this.active);
        if (commodityCodeFixture != null) {
            CommodityCode cc = commodityCodeFixture.createCommodityCode();
            vcc.setCommodityCode(cc);
            vcc.setPurchasingCommodityCode(cc.getPurchasingCommodityCode());
        }
        return vcc;
    }

}
