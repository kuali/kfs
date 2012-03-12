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

package org.kuali.kfs.vnd.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Address Types for Vendor Addresses. Used to distinguish the intended use of this address among the various addresses that a
 * vendor might give out.
 * 
 * @see org.kuali.kfs.vnd.businessobject.VendorAddress
 */
public class AddressType extends PersistableBusinessObjectBase implements MutableInactivatable {

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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
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
