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
public class ShippingTitle extends PersistableBusinessObjectBase {

	private String vendorShippingTitleCode;
	private String vendorShippingTitleDescription;
    private boolean active;
    
	/**
	 * Default constructor.
	 */
	public ShippingTitle() {

	}

	/**
	 * Gets the vendorShippingTitleCode attribute.
	 * 
	 * @return Returns the vendorShippingTitleCode
	 * 
	 */
	public String getVendorShippingTitleCode() { 
		return vendorShippingTitleCode;
	}

	/**
	 * Sets the vendorShippingTitleCode attribute.
	 * 
	 * @param vendorShippingTitleCode The vendorShippingTitleCode to set.
	 * 
	 */
	public void setVendorShippingTitleCode(String vendorShippingTitleCode) {
		this.vendorShippingTitleCode = vendorShippingTitleCode;
	}


	/**
	 * Gets the vendorShippingTitleDescription attribute.
	 * 
	 * @return Returns the vendorShippingTitleDescription
	 * 
	 */
	public String getVendorShippingTitleDescription() { 
		return vendorShippingTitleDescription;
	}

	/**
	 * Sets the vendorShippingTitleDescription attribute.
	 * 
	 * @param vendorShippingTitleDescription The vendorShippingTitleDescription to set.
	 * 
	 */
	public void setVendorShippingTitleDescription(String vendorShippingTitleDescription) {
		this.vendorShippingTitleDescription = vendorShippingTitleDescription;
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
        m.put("vendorShippingTitleCode", this.vendorShippingTitleCode);
	    return m;
    }
}
