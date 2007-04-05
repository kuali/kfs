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
 * 
 */
public class SupplierDiversity extends PersistableBusinessObjectBase {

    private String vendorSupplierDiversityCode;
    private String vendorSupplierDiversityDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public SupplierDiversity() {

    }

    /**
     * Gets the vendorSupplierDiversityCode attribute.
     * 
     * @return Returns the vendorSupplierDiversityCode
     */
    public String getVendorSupplierDiversityCode() {
        return vendorSupplierDiversityCode;
    }

    /**
     * Sets the vendorSupplierDiversityCode attribute.
     * 
     * @param vendorSupplierDiversityCode The vendorSupplierDiversityCode to set.
     */
    public void setVendorSupplierDiversityCode(String vendorSupplierDiversityCode) {
        this.vendorSupplierDiversityCode = vendorSupplierDiversityCode;
    }


    /**
     * Gets the vendorSupplierDiversityDescription attribute.
     * 
     * @return Returns the vendorSupplierDiversityDescription
     */
    public String getVendorSupplierDiversityDescription() {
        return vendorSupplierDiversityDescription;
    }

    /**
     * Sets the vendorSupplierDiversityDescription attribute.
     * 
     * @param vendorSupplierDiversityDescription The vendorSupplierDiversityDescription to set.
     */
    public void setVendorSupplierDiversityDescription(String vendorSupplierDiversityDescription) {
        this.vendorSupplierDiversityDescription = vendorSupplierDiversityDescription;
    }

    /**
     * Gets the active attribute. 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("vendorSupplierDiversityCode", this.vendorSupplierDiversityCode);
        return m;
    }
}
