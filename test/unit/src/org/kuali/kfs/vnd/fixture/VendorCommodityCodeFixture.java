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
