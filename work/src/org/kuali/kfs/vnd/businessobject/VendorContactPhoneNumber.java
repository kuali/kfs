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
