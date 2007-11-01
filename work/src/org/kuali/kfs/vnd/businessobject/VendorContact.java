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
import java.util.List;

import org.kuali.core.bo.Inactivateable;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.bo.Country;
import org.kuali.kfs.bo.State;

/**
 * Container for information about how to get in Contact with a person at a Vendor for a particular purpose.
 */
public class VendorContact extends PersistableBusinessObjectBase implements Inactivateable {

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

    // These aren't persisted in db, only for lookup page
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

    public Integer getVendorContactGeneratedIdentifier() {
        return vendorContactGeneratedIdentifier;
    }

    public void setVendorContactGeneratedIdentifier(Integer vendorContactGeneratedIdentifier) {
        this.vendorContactGeneratedIdentifier = vendorContactGeneratedIdentifier;
    }

    public Integer getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }

    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }

    public Integer getVendorDetailAssignedIdentifier() {
        return vendorDetailAssignedIdentifier;
    }

    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }

    public String getVendorContactTypeCode() {
        return vendorContactTypeCode;
    }

    public void setVendorContactTypeCode(String vendorContactTypeCode) {
        this.vendorContactTypeCode = vendorContactTypeCode;
    }

    public String getVendorContactName() {
        return vendorContactName;
    }

    public void setVendorContactName(String vendorContactName) {
        this.vendorContactName = vendorContactName;
    }

    public String getVendorContactEmailAddress() {
        return vendorContactEmailAddress;
    }

    public void setVendorContactEmailAddress(String vendorContactEmailAddress) {
        this.vendorContactEmailAddress = vendorContactEmailAddress;
    }

    public String getVendorContactCommentText() {
        return vendorContactCommentText;
    }

    public void setVendorContactCommentText(String vendorContactCommentText) {
        this.vendorContactCommentText = vendorContactCommentText;
    }

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

    public VendorDetail getVendorDetail() {
        return vendorDetail;
    }

    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    public String getVendorAddressInternationalProvinceName() {
        return vendorAddressInternationalProvinceName;
    }

    public void setVendorAddressInternationalProvinceName(String vendorAddressInternationalProvinceName) {
        this.vendorAddressInternationalProvinceName = vendorAddressInternationalProvinceName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getVendorAttentionName() {
        return vendorAttentionName;
    }

    public void setVendorAttentionName(String vendorAttentionName) {
        this.vendorAttentionName = vendorAttentionName;
    }

    public String getVendorCityName() {
        return vendorCityName;
    }

    public void setVendorCityName(String vendorCityName) {
        this.vendorCityName = vendorCityName;
    }

    public String getVendorCountryCode() {
        return vendorCountryCode;
    }

    public void setVendorCountryCode(String vendorCountryCode) {
        this.vendorCountryCode = vendorCountryCode;
    }

    public String getVendorLine1Address() {
        return vendorLine1Address;
    }

    public void setVendorLine1Address(String vendorLine1Address) {
        this.vendorLine1Address = vendorLine1Address;
    }

    public String getVendorLine2Address() {
        return vendorLine2Address;
    }

    public void setVendorLine2Address(String vendorLine2Address) {
        this.vendorLine2Address = vendorLine2Address;
    }

    public String getVendorStateCode() {
        return vendorStateCode;
    }

    public void setVendorStateCode(String vendorStateCode) {
        this.vendorStateCode = vendorStateCode;
    }

    public String getVendorZipCode() {
        return vendorZipCode;
    }

    public void setVendorZipCode(String vendorZipCode) {
        this.vendorZipCode = vendorZipCode;
    }

    public Country getVendorCountry() {
        return vendorCountry;
    }

    /**
     * Sets the vendorCountry attribute value.
     * 
     * @param vendorCountry The vendorCountry to set.
     * @deprecated
     */
    public void setVendorCountry(Country vendorCountry) {
        this.vendorCountry = vendorCountry;
    }

    public State getVendorState() {
        return vendorState;
    }

    /**
     * Sets the vendorState attribute value.
     * 
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
