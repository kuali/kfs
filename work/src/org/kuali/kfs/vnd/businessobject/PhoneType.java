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
 * Phone Types for Vendors. These types may be based on technical distinctions, the Vendor's organization, or the phone's intended
 * purpose.
 */
public class PhoneType extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String vendorPhoneTypeCode;
    private String vendorPhoneTypeDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public PhoneType() {

    }

    public String getVendorPhoneTypeCode() {

        return vendorPhoneTypeCode;
    }

    public void setVendorPhoneTypeCode(String vendorPhoneTypeCode) {
        this.vendorPhoneTypeCode = vendorPhoneTypeCode;
    }

    public String getVendorPhoneTypeDescription() {

        return vendorPhoneTypeDescription;
    }

    public void setVendorPhoneTypeDescription(String vendorPhoneTypeDescription) {
        this.vendorPhoneTypeDescription = vendorPhoneTypeDescription;
    }

    public boolean isActive() {

        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("vendorPhoneTypeCode", this.vendorPhoneTypeCode);

        return m;
    }
}
