/*
 * Copyright 2007 The Kuali Foundation.
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
 * Types of Contacts for Vendor Contacts. Typically, these types reflect the Contact's position and/or function within the Vendor's
 * organization.
 * 
 * @see org.kuali.module.vendor.bo.VendorContact
 */
public class ContactType extends PersistableBusinessObjectBase {

    private String vendorContactTypeCode;
    private String vendorContactTypeDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public ContactType() {

    }

    public String getVendorContactTypeCode() {

        return vendorContactTypeCode;
    }

    public void setVendorContactTypeCode(String vendorContactTypeCode) {
        this.vendorContactTypeCode = vendorContactTypeCode;
    }

    public String getVendorContactTypeDescription() {

        return vendorContactTypeDescription;
    }

    public void setVendorContactTypeDescription(String vendorContactTypeDescription) {
        this.vendorContactTypeDescription = vendorContactTypeDescription;
    }

    public boolean isActive() {

        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("vendorContactTypeCode", this.vendorContactTypeCode);

        return m;
    }
}
