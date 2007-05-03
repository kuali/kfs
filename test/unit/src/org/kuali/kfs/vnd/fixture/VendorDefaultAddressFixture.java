/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.vendor.fixtures;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.KFSConstants;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorDefaultAddress;

public enum VendorDefaultAddressFixture {

    campusBL("BL"),
    campusKO("KO"),
    campusIN("IN"),
    campusSB("SB"),
    ;
    
    public final String vendorCampusCode;
    
    private VendorDefaultAddressFixture( String vendorCampusCode ) {
        this.vendorCampusCode = vendorCampusCode;
    }

    public VendorDefaultAddress createAddress() {
        VendorDefaultAddress defaultAddress = new VendorDefaultAddress();
        defaultAddress.setVendorCampusCode(this.vendorCampusCode);
        return defaultAddress;
    }
    
}
