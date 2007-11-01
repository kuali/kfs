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
 * Possible reasons why a Vendor may become inactivated.
 */
public class VendorInactiveReason extends PersistableBusinessObjectBase {

    private String vendorInactiveReasonCode;
    private String vendorInactiveReasonDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public VendorInactiveReason() {

    }

    public String getVendorInactiveReasonCode() {

        return vendorInactiveReasonCode;
    }

    public void setVendorInactiveReasonCode(String vendorInactiveReasonCode) {
        this.vendorInactiveReasonCode = vendorInactiveReasonCode;
    }

    public String getVendorInactiveReasonDescription() {

        return vendorInactiveReasonDescription;
    }

    public void setVendorInactiveReasonDescription(String vendorInactiveReasonDescription) {
        this.vendorInactiveReasonDescription = vendorInactiveReasonDescription;
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
        m.put("vendorInactiveReasonCode", this.vendorInactiveReasonCode);

        return m;
    }
}
