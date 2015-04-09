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
