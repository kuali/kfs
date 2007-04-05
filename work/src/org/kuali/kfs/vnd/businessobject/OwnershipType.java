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

	/**
	 * Gets the vendorOwnershipCode attribute.
	 * 
	 * @return Returns the vendorOwnershipCode
	 * 
	 */
	public String getVendorOwnershipCode() { 
		return vendorOwnershipCode;
	}

	/**
	 * Sets the vendorOwnershipCode attribute.
	 * 
	 * @param vendorOwnershipCode The vendorOwnershipCode to set.
	 * 
	 */
	public void setVendorOwnershipCode(String vendorOwnershipCode) {
		this.vendorOwnershipCode = vendorOwnershipCode;
	}


	/**
	 * Gets the vendorOwnershipDescription attribute.
	 * 
	 * @return Returns the vendorOwnershipDescription
	 * 
	 */
	public String getVendorOwnershipDescription() { 
		return vendorOwnershipDescription;
	}

	/**
	 * Sets the vendorOwnershipDescription attribute.
	 * 
	 * @param vendorOwnershipDescription The vendorOwnershipDescription to set.
	 * 
	 */
	public void setVendorOwnershipDescription(String vendorOwnershipDescription) {
		this.vendorOwnershipDescription = vendorOwnershipDescription;
	}


	/**
	 * Gets the vendorOwnershipCategoryAllowedIndicator attribute.
	 * 
	 * @return Returns the vendorOwnershipCategoryAllowedIndicator
	 * 
	 */
	public boolean getVendorOwnershipCategoryAllowedIndicator() { 
		return vendorOwnershipCategoryAllowedIndicator;
	}

	/**
	 * Sets the vendorOwnershipCategoryAllowedIndicator attribute.
	 * 
	 * @param vendorOwnershipCategoryAllowedIndicator The vendorOwnershipCategoryAllowedIndicator to set.
	 * 
	 */
	public void setVendorOwnershipCategoryAllowedIndicator(boolean vendorOwnershipCategoryAllowedIndicator) {
		this.vendorOwnershipCategoryAllowedIndicator = vendorOwnershipCategoryAllowedIndicator;
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
        m.put("vendorOwnershipCode", this.vendorOwnershipCode);
	    return m;
    }
}
