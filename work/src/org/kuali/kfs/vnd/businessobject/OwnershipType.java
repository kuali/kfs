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
 * Exclusive kinds of basic business organization that a vendor may have. Other broad exclusive categories for vendors can be
 * instances of <code>OwnershipCategory</code>.
 * 
 * @see org.kuali.module.vendor.bo.OwnershipCategory
 */
public class OwnershipType extends PersistableBusinessObjectBase {

    private String vendorOwnershipCode;
    private String vendorOwnershipDescription;
    private boolean vendorOwnershipCategoryAllowedIndicator;
    private boolean active;

    /**
     * Default constructor.
     */
    public OwnershipType() {

    }

    public String getVendorOwnershipCode() {

        return vendorOwnershipCode;
    }

    public void setVendorOwnershipCode(String vendorOwnershipCode) {
        this.vendorOwnershipCode = vendorOwnershipCode;
    }

    public String getVendorOwnershipDescription() {

        return vendorOwnershipDescription;
    }

    public void setVendorOwnershipDescription(String vendorOwnershipDescription) {
        this.vendorOwnershipDescription = vendorOwnershipDescription;
    }

    public boolean getVendorOwnershipCategoryAllowedIndicator() {

        return vendorOwnershipCategoryAllowedIndicator;
    }

    public void setVendorOwnershipCategoryAllowedIndicator(boolean vendorOwnershipCategoryAllowedIndicator) {
        this.vendorOwnershipCategoryAllowedIndicator = vendorOwnershipCategoryAllowedIndicator;
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
        m.put("vendorOwnershipCode", this.vendorOwnershipCode);

        return m;
    }
}
