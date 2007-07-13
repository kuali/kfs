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
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.bo.Country;
import org.kuali.kfs.bo.State;

/**
 * 
 */
public class VendorContact extends PersistableBusinessObjectBase {

	private Integer vendorContactGeneratedIdentifier;
	private Integer vendorHeaderGeneratedIdentifier;
	private Integer vendorDetailAssignedIdentifier;
	private String vendorContactTypeCode;
	private String vendorContactName;
	private String vendorContactEmailAddress;
	private String vendorContactCommentText;
    private String vendorLine1Address;
    private String vendorLine2Address;
    private String vendorCityName;
    private String vendorStateCode;
    private String vendorZipCode;
    private String vendorCountryCode;
    private String vendorAttentionName;
    private String vendorAddressInternationalProvinceName;
    private boolean active;
    
    //These aren't persisted in db, only for lookup page
    private String phoneNumberForLookup;
    private String tollFreeForLookup;
    private String faxForLookup;
    
    private List<VendorContactPhoneNumber> vendorContactPhoneNumbers;

    private VendorDetail vendorDetail;
	private ContactType vendorContactType;
    private State vendorState;
    private Country vendorCountry;
    
	/**
	 * Default constructor.
	 */
	public VendorContact() {
        vendorContactPhoneNumbers = new TypedArrayList(VendorContactPhoneNumber.class);
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
	 * Gets the vendorContactTypeCode attribute.
	 * 
	 * @return Returns the vendorContactTypeCode
	 * 
	 */
	public String getVendorContactTypeCode() { 
		return vendorContactTypeCode;
	}

	/**
	 * Sets the vendorContactTypeCode attribute.
	 * 
	 * @param vendorContactTypeCode The vendorContactTypeCode to set.
	 * 
	 */
	public void setVendorContactTypeCode(String vendorContactTypeCode) {
		this.vendorContactTypeCode = vendorContactTypeCode;
	}


	/**
	 * Gets the vendorContactName attribute.
	 * 
	 * @return Returns the vendorContactName
	 * 
	 */
	public String getVendorContactName() { 
		return vendorContactName;
	}

	/**
	 * Sets the vendorContactName attribute.
	 * 
	 * @param vendorContactName The vendorContactName to set.
	 * 
	 */
	public void setVendorContactName(String vendorContactName) {
		this.vendorContactName = vendorContactName;
	}


	/**
	 * Gets the vendorContactEmailAddress attribute.
	 * 
	 * @return Returns the vendorContactEmailAddress
	 * 
	 */
	public String getVendorContactEmailAddress() { 
		return vendorContactEmailAddress;
	}

	/**
	 * Sets the vendorContactEmailAddress attribute.
	 * 
	 * @param vendorContactEmailAddress The vendorContactEmailAddress to set.
	 * 
	 */
	public void setVendorContactEmailAddress(String vendorContactEmailAddress) {
		this.vendorContactEmailAddress = vendorContactEmailAddress;
	}


	/**
	 * Gets the vendorContactCommentText attribute.
	 * 
	 * @return Returns the vendorContactCommentText
	 * 
	 */
	public String getVendorContactCommentText() { 
		return vendorContactCommentText;
	}

	/**
	 * Sets the vendorContactCommentText attribute.
	 * 
	 * @param vendorContactCommentText The vendorContactCommentText to set.
	 * 
	 */
	public void setVendorContactCommentText(String vendorContactCommentText) {
		this.vendorContactCommentText = vendorContactCommentText;
	}

	/**
	 * Gets the vendorContactType attribute.
	 * 
	 * @return Returns the vendorContactType
	 * 
	 */
	public ContactType getVendorContactType() { 
		return vendorContactType;
	}
    

    /**
	 * Sets the vendorContactType attribute.
	 * 
	 * @param vendorContactType The vendorContactType to set.
	 * @deprecated
	 */
	public void setVendorContactType(ContactType vendorContactType) {
		this.vendorContactType = vendorContactType;
	}
    

	/**
     * Gets the vendorDetail attribute. 
     * @return Returns the vendorDetail.
     */
    public VendorDetail getVendorDetail() {
        return vendorDetail;
    }

    /**
     * Sets the vendorDetail attribute value.
     * @param vendorDetail The vendorDetail to set.
     */
    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    /**
     * Gets the vendorAddressInternationalProvinceName attribute. 
     * @return Returns the vendorAddressInternationalProvinceName.
     */
    public String getVendorAddressInternationalProvinceName() {
        return vendorAddressInternationalProvinceName;
    }

    /**
     * Sets the vendorAddressInternationalProvinceName attribute value.
     * @param vendorAddressInternationalProvinceName The vendorAddressInternationalProvinceName to set.
     */
    public void setVendorAddressInternationalProvinceName(String vendorAddressInternationalProvinceName) {
        this.vendorAddressInternationalProvinceName = vendorAddressInternationalProvinceName;
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
     * Gets the vendorAttentionName attribute. 
     * @return Returns the vendorAttentionName.
     */
    public String getVendorAttentionName() {
        return vendorAttentionName;
    }

    /**
     * Sets the vendorAttentionName attribute value.
     * @param vendorAttentionName The vendorAttentionName to set.
     */
    public void setVendorAttentionName(String vendorAttentionName) {
        this.vendorAttentionName = vendorAttentionName;
    }

    /**
     * Gets the vendorCityName attribute. 
     * @return Returns the vendorCityName.
     */
    public String getVendorCityName() {
        return vendorCityName;
    }

    /**
     * Sets the vendorCityName attribute value.
     * @param vendorCityName The vendorCityName to set.
     */
    public void setVendorCityName(String vendorCityName) {
        this.vendorCityName = vendorCityName;
    }

    /**
     * Gets the vendorCountryCode attribute. 
     * @return Returns the vendorCountryCode.
     */
    public String getVendorCountryCode() {
        return vendorCountryCode;
    }

    /**
     * Sets the vendorCountryCode attribute value.
     * @param vendorCountryCode The vendorCountryCode to set.
     */
    public void setVendorCountryCode(String vendorCountryCode) {
        this.vendorCountryCode = vendorCountryCode;
    }

    /**
     * Gets the vendorLine1Address attribute. 
     * @return Returns the vendorLine1Address.
     */
    public String getVendorLine1Address() {
        return vendorLine1Address;
    }

    /**
     * Sets the vendorLine1Address attribute value.
     * @param vendorLine1Address The vendorLine1Address to set.
     */
    public void setVendorLine1Address(String vendorLine1Address) {
        this.vendorLine1Address = vendorLine1Address;
    }

    /**
     * Gets the vendorLine2Address attribute. 
     * @return Returns the vendorLine2Address.
     */
    public String getVendorLine2Address() {
        return vendorLine2Address;
    }

    /**
     * Sets the vendorLine2Address attribute value.
     * @param vendorLine2Address The vendorLine2Address to set.
     */
    public void setVendorLine2Address(String vendorLine2Address) {
        this.vendorLine2Address = vendorLine2Address;
    }

    /**
     * Gets the vendorStateCode attribute. 
     * @return Returns the vendorStateCode.
     */
    public String getVendorStateCode() {
        return vendorStateCode;
    }

    /**
     * Sets the vendorStateCode attribute value.
     * @param vendorStateCode The vendorStateCode to set.
     */
    public void setVendorStateCode(String vendorStateCode) {
        this.vendorStateCode = vendorStateCode;
    }

    /**
     * Gets the vendorZipCode attribute. 
     * @return Returns the vendorZipCode.
     */
    public String getVendorZipCode() {
        return vendorZipCode;
    }

    /**
     * Sets the vendorZipCode attribute value.
     * @param vendorZipCode The vendorZipCode to set.
     */
    public void setVendorZipCode(String vendorZipCode) {
        this.vendorZipCode = vendorZipCode;
    }

    /**
     * Gets the vendorCountry attribute. 
     * @return Returns the vendorCountry.
     */
    public Country getVendorCountry() {
        return vendorCountry;
    }

    /**
     * Sets the vendorCountry attribute value.
     * @param vendorCountry The vendorCountry to set.
     * @deprecated
     */
    public void setVendorCountry(Country vendorCountry) {
        this.vendorCountry = vendorCountry;
    }

    /**
     * Gets the vendorState attribute. 
     * @return Returns the vendorState.
     */
    public State getVendorState() {
        return vendorState;
    }

    /**
     * Sets the vendorState attribute value.
     * @param vendorState The vendorState to set.
     * @deprecated
     */
    public void setVendorState(State vendorState) {
        this.vendorState = vendorState;
    }

    public String getFaxForLookup() {
        return faxForLookup;
    }

    public void setFaxForLookup(String faxForLookup) {
        this.faxForLookup = faxForLookup;
    }

    public String getPhoneNumberForLookup() {
        return phoneNumberForLookup;
    }

    public void setPhoneNumberForLookup(String phoneNumberForLookup) {
        this.phoneNumberForLookup = phoneNumberForLookup;
    }

    public String getTollFreeForLookup() {
        return tollFreeForLookup;
    }

    public void setTollFreeForLookup(String tollFreeForLookup) {
        this.tollFreeForLookup = tollFreeForLookup;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        if (this.vendorContactGeneratedIdentifier != null) {
            m.put("vendorContactGeneratedIdentifier", this.vendorContactGeneratedIdentifier.toString());
        }
        return m;
    }

    public List<VendorContactPhoneNumber> getVendorContactPhoneNumbers() {
        return vendorContactPhoneNumbers;
    }

    public void setVendorContactPhoneNumbers(List<VendorContactPhoneNumber> vendorContactPhoneNumbers) {
        this.vendorContactPhoneNumbers = vendorContactPhoneNumbers;
    }
    
}
