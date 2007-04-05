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
public class OwnershipCategory extends PersistableBusinessObjectBase {

	private String vendorOwnershipCategoryCode;
	private String vendorOwnershipCategoryDescription;
    private boolean active;
    
	/**
	 * Default constructor.
	 */
	public OwnershipCategory() {

	}

	/**
	 * Gets the vendorOwnershipCategoryCode attribute.
	 * 
	 * @return Returns the vendorOwnershipCategoryCode
	 * 
	 */
	public String getVendorOwnershipCategoryCode() { 
		return vendorOwnershipCategoryCode;
	}

	/**
	 * Sets the vendorOwnershipCategoryCode attribute.
	 * 
	 * @param vendorOwnershipCategoryCode The vendorOwnershipCategoryCode to set.
	 * 
	 */
	public void setVendorOwnershipCategoryCode(String vendorOwnershipCategoryCode) {
		this.vendorOwnershipCategoryCode = vendorOwnershipCategoryCode;
	}


	/**
	 * Gets the vendorOwnershipCategoryDescription attribute.
	 * 
	 * @return Returns the vendorOwnershipCategoryDescription
	 * 
	 */
	public String getVendorOwnershipCategoryDescription() { 
		return vendorOwnershipCategoryDescription;
	}

	/**
	 * Sets the vendorOwnershipCategoryDescription attribute.
	 * 
	 * @param vendorOwnershipCategoryDescription The vendorOwnershipCategoryDescription to set.
	 * 
	 */
	public void setVendorOwnershipCategoryDescription(String vendorOwnershipCategoryDescription) {
		this.vendorOwnershipCategoryDescription = vendorOwnershipCategoryDescription;
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
        m.put("vendorOwnershipCategoryCode", this.vendorOwnershipCategoryCode);
	    return m;
    }
}
