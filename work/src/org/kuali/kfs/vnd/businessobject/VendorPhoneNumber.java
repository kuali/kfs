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
public class VendorPhoneNumber extends PersistableBusinessObjectBase {

	private Integer vendorPhoneGeneratedIdentifier;
	private Integer vendorHeaderGeneratedIdentifier;
	private Integer vendorDetailAssignedIdentifier;
	private String vendorPhoneTypeCode;
	private String vendorPhoneNumber;
	private String vendorPhoneExtensionNumber;
    private boolean active;
    
    private VendorDetail vendorDetail;
	private VendorContact vendorContact;
	private VendorAddress vendorAddress;
	private PhoneType vendorPhoneType;

	/**
	 * Default constructor.
	 */
	public VendorPhoneNumber() {

	}

	/**
	 * Gets the vendorPhoneGeneratedIdentifier attribute.
	 * 
	 * @return Returns the vendorPhoneGeneratedIdentifier
	 * 
	 */
	public Integer getVendorPhoneGeneratedIdentifier() { 
		return vendorPhoneGeneratedIdentifier;
	}

	/**
	 * Sets the vendorPhoneGeneratedIdentifier attribute.
	 * 
	 * @param vendorPhoneGeneratedIdentifier The vendorPhoneGeneratedIdentifier to set.
	 * 
	 */
	public void setVendorPhoneGeneratedIdentifier(Integer vendorPhoneGeneratedIdentifier) {
		this.vendorPhoneGeneratedIdentifier = vendorPhoneGeneratedIdentifier;
	}


	/**
	 * Gets the vendorHeaderGeneratedIdentifier attribute.
	 * 
	 * @return Returns the vendorHeaderGeneratedIdentifier
	 * 
	 */
	public Integer getVendorHeaderGeneratedIdentifier() { 
		return vendorHeaderGeneratedIdentifier;
	}

	/**
	 * Sets the vendorHeaderGeneratedIdentifier attribute.
	 * 
	 * @param vendorHeaderGeneratedIdentifier The vendorHeaderGeneratedIdentifier to set.
	 * 
	 */
	public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
		this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
	}


	/**
	 * Gets the vendorDetailAssignedIdentifier attribute.
	 * 
	 * @return Returns the vendorDetailAssignedIdentifier
	 * 
	 */
	public Integer getVendorDetailAssignedIdentifier() { 
		return vendorDetailAssignedIdentifier;
	}

	/**
	 * Sets the vendorDetailAssignedIdentifier attribute.
	 * 
	 * @param vendorDetailAssignedIdentifier The vendorDetailAssignedIdentifier to set.
	 * 
	 */
	public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
		this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
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
	 * Gets the vendorDetail attribute.
	 * 
	 * @return Returns the vendorDetail
	 * 
	 */
	public VendorDetail getVendorDetail() { 
		return vendorDetail;
	}

	/**
	 * Sets the vendorDetail attribute.
	 * 
	 * @param vendorDetail The vendorDetail to set.
	 * @deprecated
	 */
	public void setVendorDetail(VendorDetail vendorDetail) {
		this.vendorDetail = vendorDetail;
	}

	/**
	 * Gets the vendorContact attribute.
	 * 
	 * @return Returns the vendorContact
	 * 
	 */
	public VendorContact getVendorContact() { 
		return vendorContact;
	}

	/**
	 * Sets the vendorContact attribute.
	 * 
	 * @param vendorContact The vendorContact to set.
	 * @deprecated
	 */
	public void setVendorContact(VendorContact vendorContact) {
		this.vendorContact = vendorContact;
	}

	/**
	 * Gets the vendorAddress attribute.
	 * 
	 * @return Returns the vendorAddress
	 * 
	 */
	public VendorAddress getVendorAddress() { 
		return vendorAddress;
	}

	/**
	 * Sets the vendorAddress attribute.
	 * 
	 * @param vendorAddress The vendorAddress to set.
	 * @deprecated
	 */
	public void setVendorAddress(VendorAddress vendorAddress) {
		this.vendorAddress = vendorAddress;
	}

	/**
	 * Gets the vendorPhoneType attribute.
	 * 
	 * @return Returns the vendorPhoneType
	 * 
	 */
	public PhoneType getVendorPhoneType() { 
		return vendorPhoneType;
	}

	/**
	 * Sets the vendorPhoneType attribute.
	 * 
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
        if (this.vendorPhoneGeneratedIdentifier != null) {
            m.put("vendorPhoneGeneratedIdentifier", this.vendorPhoneGeneratedIdentifier.toString());
        }
	    return m;
    }
}
