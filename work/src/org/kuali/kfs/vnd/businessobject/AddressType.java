/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.vendor.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * Address Types for Vendor Addresses. Used to distinguish the intended use of this address among the various addresses that a
 * vendor might give out.
 * 
 * @see org.kuali.module.vendor.bo.VendorAddress
 */
public class AddressType extends PersistableBusinessObjectBase {

    private String vendorAddressTypeCode;
    private String vendorAddressTypeDescription;
    private boolean vendorDefaultIndicator;
    private boolean active;

    /**
     * Default constructor.
     */
    public AddressType() {

    }

    public String getVendorAddressTypeCode() {

        return vendorAddressTypeCode;
    }

    public void setVendorAddressTypeCode(String vendorAddressTypeCode) {
        this.vendorAddressTypeCode = vendorAddressTypeCode;
    }

    public String getVendorAddressTypeDescription() {

        return vendorAddressTypeDescription;
    }

    public void setVendorAddressTypeDescription(String vendorAddressTypeDescription) {
        this.vendorAddressTypeDescription = vendorAddressTypeDescription;
    }

    public boolean getVendorDefaultIndicator() {

        return vendorDefaultIndicator;
    }

    public void setVendorDefaultIndicator(boolean vendorDefaultIndicator) {
        this.vendorDefaultIndicator = vendorDefaultIndicator;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("vendorAddressTypeCode", this.vendorAddressTypeCode);

        return m;
    }

    public boolean isActive() {

        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
