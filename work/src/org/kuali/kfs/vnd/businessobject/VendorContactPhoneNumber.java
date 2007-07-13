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
public class VendorContactPhoneNumber extends PersistableBusinessObjectBase {

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

	/**
	 * Gets the vendorContactPhoneGeneratedIdentifier attribute.
	 * 
	 * @return Returns the vendorContactPhoneGeneratedIdentifier
	 * 
	 */
	public Integer getVendorContactPhoneGeneratedIdentifier() { 
		return vendorContactPhoneGeneratedIdentifier;
	}

	/**
	 * Sets the vendorContactPhoneGeneratedIdentifier attribute.
	 * 
	 * @param vendorContactPhoneGeneratedIdentifier The vendorContactPhoneGeneratedIdentifier to set.
	 * 
	 */
	public void setVendorContactPhoneGeneratedIdentifier(Integer vendorContactPhoneGeneratedIdentifier) {
		this.vendorContactPhoneGeneratedIdentifier = vendorContactPhoneGeneratedIdentifier;
	}


	/**
	 * Gets the vendorContactGeneratedIdentifier attribute.
	 * 
	 * @return Returns the vendorContactGeneratedIdentifier
	 * 
	 */
	public Integer getVendorContactGeneratedIdentifier() { 
		return vendorContactGeneratedIdentifier;
	}

	/**
	 * Sets the vendorContactGeneratedIdentifier attribute.
	 * 
	 * @param vendorContactGeneratedIdentifier The vendorContactGeneratedIdentifier to set.
	 * 
	 */
	public void setVendorContactGeneratedIdentifier(Integer vendorContactGeneratedIdentifier) {
		this.vendorContactGeneratedIdentifier = vendorContactGeneratedIdentifier;
	}


	/**
	 * Gets the vendorPhoneTypeCode attribute.
	 * 
	 * @return Returns the vendorPhoneTypeCode
	 * 
	 */
	public String getVendorPhoneTypeCode() { 
		return vendorPhoneTypeCode;
	}

	/**
	 * Sets the vendorPhoneTypeCode attribute.
	 * 
	 * @param vendorPhoneTypeCode The vendorPhoneTypeCode to set.
	 * 
	 */
	public void setVendorPhoneTypeCode(String vendorPhoneTypeCode) {
		this.vendorPhoneTypeCode = vendorPhoneTypeCode;
	}


	/**
	 * Gets the vendorPhoneNumber attribute.
	 * 
	 * @return Returns the vendorPhoneNumber
	 * 
	 */
	public String getVendorPhoneNumber() { 
		return vendorPhoneNumber;
	}

	/**
	 * Sets the vendorPhoneNumber attribute.
	 * 
	 * @param vendorPhoneNumber The vendorPhoneNumber to set.
	 * 
	 */
	public void setVendorPhoneNumber(String vendorPhoneNumber) {
		this.vendorPhoneNumber = vendorPhoneNumber;
	}


	/**
	 * Gets the vendorPhoneExtensionNumber attribute.
	 * 
	 * @return Returns the vendorPhoneExtensionNumber
	 * 
	 */
	public String getVendorPhoneExtensionNumber() { 
		return vendorPhoneExtensionNumber;
	}

	/**
	 * Sets the vendorPhoneExtensionNumber attribute.
	 * 
	 * @param vendorPhoneExtensionNumber The vendorPhoneExtensionNumber to set.
	 * 
	 */
	public void setVendorPhoneExtensionNumber(String vendorPhoneExtensionNumber) {
		this.vendorPhoneExtensionNumber = vendorPhoneExtensionNumber;
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
     * Gets the vendorPhoneType attribute. 
     * @return Returns the vendorPhoneType.
     */
    public PhoneType getVendorPhoneType() {
        return vendorPhoneType;
    }

    /**
     * Sets the vendorPhoneType attribute value.
     * @param vendorPhoneType The vendorPhoneType to set.
     * @deprecated
     */
    public void setVendorPhoneType(PhoneType vendorPhoneType) {
        this.vendorPhoneType = vendorPhoneType;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        if (this.vendorContactPhoneGeneratedIdentifier != null) {
            m.put("vendorContactPhoneGeneratedIdentifier", this.vendorContactPhoneGeneratedIdentifier.toString());
        }
        return m;
    }

}
