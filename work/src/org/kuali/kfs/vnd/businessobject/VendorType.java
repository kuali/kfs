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
public class VendorType extends PersistableBusinessObjectBase {

	private String vendorTypeCode;
	private String vendorTypeDescription;
	private boolean active;
    private boolean vendorTaxNumberRequiredIndicator;
    private boolean vendorTypeChangeAllowedIndicator;
    private String vendorAddressTypeRequiredCode;
    private boolean vendorContractAllowedIndicator;
    
    private AddressType addressType;

	/**
	 * Default constructor.
	 */
	public VendorType() {

	}

	/**
	 * Gets the vendorTypeCode attribute.
	 * 
	 * @return Returns the vendorTypeCode
	 * 
	 */
	public String getVendorTypeCode() { 
		return vendorTypeCode;
	}

	/**
	 * Sets the vendorTypeCode attribute.
	 * 
	 * @param vendorTypeCode The vendorTypeCode to set.
	 * 
	 */
	public void setVendorTypeCode(String vendorTypeCode) {
		this.vendorTypeCode = vendorTypeCode;
	}


	/**
	 * Gets the vendorTypeDescription attribute.
	 * 
	 * @return Returns the vendorTypeDescription
	 * 
	 */
	public String getVendorTypeDescription() { 
		return vendorTypeDescription;
	}

	/**
	 * Sets the vendorTypeDescription attribute.
	 * 
	 * @param vendorTypeDescription The vendorTypeDescription to set.
	 * 
	 */
	public void setVendorTypeDescription(String vendorTypeDescription) {
		this.vendorTypeDescription = vendorTypeDescription;
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
     * Gets the vendorAddressTypeRequiredCode attribute. 
     * @return Returns the vendorAddressTypeRequiredCode.
     */
    public String getVendorAddressTypeRequiredCode() {
        return vendorAddressTypeRequiredCode;
    }

    /**
     * Sets the vendorAddressTypeRequiredCode attribute value.
     * @param vendorAddressTypeRequiredCode The vendorAddressTypeRequiredCode to set.
     */
    public void setVendorAddressTypeRequiredCode(String vendorAddressTypeRequiredCode) {
        this.vendorAddressTypeRequiredCode = vendorAddressTypeRequiredCode;
    }

    /**
     * Gets the vendorTaxNumberRequiredIndicator attribute. 
     * @return Returns the vendorTaxNumberRequiredIndicator.
     */
    public boolean isVendorTaxNumberRequiredIndicator() {
        return vendorTaxNumberRequiredIndicator;
    }

    /**
     * Sets the vendorTaxNumberRequiredIndicator attribute value.
     * @param vendorTaxNumberRequiredIndicator The vendorTaxNumberRequiredIndicator to set.
     */
    public void setVendorTaxNumberRequiredIndicator(boolean vendorTaxNumberRequiredIndicator) {
        this.vendorTaxNumberRequiredIndicator = vendorTaxNumberRequiredIndicator;
    }

    /**
     * Gets the vendorTypeChangeAllowedIndicator attribute. 
     * @return Returns the vendorTypeChangeAllowedIndicator.
     */
    public boolean isVendorTypeChangeAllowedIndicator() {
        return vendorTypeChangeAllowedIndicator;
    }

    /**
     * Sets the vendorTypeChangeAllowedIndicator attribute value.
     * @param vendorTypeChangeAllowedIndicator The vendorTypeChangeAllowedIndicator to set.
     */
    public void setVendorTypeChangeAllowedIndicator(boolean vendorTypeChangeAllowedIndicator) {
        this.vendorTypeChangeAllowedIndicator = vendorTypeChangeAllowedIndicator;
    }

    /**
     * Gets the vendorContractAllowedIndicator attribute. 
     * @return Returns the vendorContractAllowedIndicator.
     */
    public boolean isVendorContractAllowedIndicator() {
        return vendorContractAllowedIndicator;
    }

    /**
     * Sets the vendorContractAllowedIndicator attribute value.
     * @param vendorContractAllowedIndicator The vendorContractAllowedIndicator to set.
     */
    public void setVendorContractAllowedIndicator(boolean vendorContractAllowedIndicator) {
        this.vendorContractAllowedIndicator = vendorContractAllowedIndicator;
    }
    
    public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("vendorTypeCode", this.vendorTypeCode);
        return m;
    }
    
}
