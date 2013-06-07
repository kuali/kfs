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
package org.kuali.kfs.vnd.fixture;

import org.kuali.kfs.vnd.businessobject.VendorDefaultAddress;

public enum VendorDefaultAddressFixture {

    campusBL("BL",false), campusKO("KO",false), campusIN("IN",true), campusSB("SB",true), ;

    public final String vendorCampusCode;
    public final boolean active;

    private VendorDefaultAddressFixture(String vendorCampusCode,boolean active) {
        this.vendorCampusCode = vendorCampusCode;
        this.active=active;
    }

    public VendorDefaultAddress createAddress() {
        VendorDefaultAddress defaultAddress = new VendorDefaultAddress();
        defaultAddress.setVendorCampusCode(this.vendorCampusCode);
        defaultAddress.setActive(this.active);
        return defaultAddress;
    }

}
