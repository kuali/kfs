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
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Phone number for a Vendor Contact.
 */
public class VendorContactPhoneNumber extends PersistableBusinessObjectBase implements MutableInactivatable {

    private Integer vendorContactPhoneGeneratedIdentifier;
    private Integer vendorContactGeneratedIdentifier;
    private String vendorPhoneTypeCode;
    private String vendorPhoneNumber;
    private String vendorPhoneExtensionNumber;
    private boolean active;

    private PhoneType vendorPhoneType;

    /**
     * Default constructor.
     */
    public VendorContactPhoneNumber() {

    }

    public Integer getVendorContactPhoneGeneratedIdentifier() {

        return vendorContactPhoneGeneratedIdentifier;
    }

    public void setVendorContactPhoneGeneratedIdentifier(Integer vendorContactPhoneGeneratedIdentifier) {
        this.vendorContactPhoneGeneratedIdentifier = vendorContactPhoneGeneratedIdentifier;
    }

    public Integer getVendorContactGeneratedIdentifier() {

        return vendorContactGeneratedIdentifier;
    }

    public void setVendorContactGeneratedIdentifier(Integer vendorContactGeneratedIdentifier) {
        this.vendorContactGeneratedIdentifier = vendorContactGeneratedIdentifier;
    }

    public String getVendorPhoneTypeCode() {

        return vendorPhoneTypeCode;
    }

    public void setVendorPhoneTypeCode(String vendorPhoneTypeCode) {
        this.vendorPhoneTypeCode = vendorPhoneTypeCode;
    }

    public String getVendorPhoneNumber() {

        return vendorPhoneNumber;
    }

    public void setVendorPhoneNumber(String vendorPhoneNumber) {
        this.vendorPhoneNumber = vendorPhoneNumber;
    }

    public String getVendorPhoneExtensionNumber() {

        return vendorPhoneExtensionNumber;
    }

    public void setVendorPhoneExtensionNumber(String vendorPhoneExtensionNumber) {
        this.vendorPhoneExtensionNumber = vendorPhoneExtensionNumber;
    }

    public boolean isActive() {

        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public PhoneType getVendorPhoneType() {

        return vendorPhoneType;
    }

    /**
     * Sets the vendorPhoneType attribute value.
     * 
     * @param vendorPhoneType The vendorPhoneType to set.
     * @deprecated
     */
    public void setVendorPhoneType(PhoneType vendorPhoneType) {
        this.vendorPhoneType = vendorPhoneType;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.vendorContactPhoneGeneratedIdentifier != null) {
            m.put("vendorContactPhoneGeneratedIdentifier", this.vendorContactPhoneGeneratedIdentifier.toString());
        }

        return m;
    }

}
