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
 * Broad categories in which a vendor may be included, exclusively, as opposed to a kind of basic business organization that the
 * vendor may have, which would be an instance of <code>OwnershipType</code>.
 * 
 * @see org.kuali.kfs.vnd.businessobject.OwnershipType
 */
public class OwnershipCategory extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String vendorOwnershipCategoryCode;
    private String vendorOwnershipCategoryDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public OwnershipCategory() {

    }

    public String getVendorOwnershipCategoryCode() {

        return vendorOwnershipCategoryCode;
    }

    public void setVendorOwnershipCategoryCode(String vendorOwnershipCategoryCode) {
        this.vendorOwnershipCategoryCode = vendorOwnershipCategoryCode;
    }

    public String getVendorOwnershipCategoryDescription() {

        return vendorOwnershipCategoryDescription;
    }

    public void setVendorOwnershipCategoryDescription(String vendorOwnershipCategoryDescription) {
        this.vendorOwnershipCategoryDescription = vendorOwnershipCategoryDescription;
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
        m.put("vendorOwnershipCategoryCode", this.vendorOwnershipCategoryCode);

        return m;
    }
}
