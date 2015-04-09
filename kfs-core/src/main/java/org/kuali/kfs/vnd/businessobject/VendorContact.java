/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kuali.kfs.vnd.businessobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.framework.country.CountryEbo;
import org.kuali.rice.location.framework.state.StateEbo;

/**
 * Container for information about how to get in Contact with a person at a Vendor for a particular purpose.
 */
public class VendorContact extends PersistableBusinessObjectBase implements MutableInactivatable {

    protected Integer vendorContactGeneratedIdentifier;
    protected Integer vendorHeaderGeneratedIdentifier;
    protected Integer vendorDetailAssignedIdentifier;
    protected String vendorContactTypeCode;
    protected String vendorContactName;
    protected String vendorContactEmailAddress;
    protected String vendorContactCommentText;
    protected String vendorLine1Address;
    protected String vendorLine2Address;
    protected String vendorCityName;
    protected String vendorStateCode;
    protected String vendorZipCode;
    protected String vendorCountryCode;
    protected String vendorAttentionName;
    protected String vendorAddressInternationalProvinceName;
    protected boolean active;

    // These aren't persisted in db, only for lookup page
    protected String phoneNumberForLookup;
    protected String tollFreeForLookup;
    protected String faxForLookup;

    protected List<VendorContactPhoneNumber> vendorContactPhoneNumbers;

    protected VendorDetail vendorDetail;
    protected ContactType vendorContactType;
    protected StateEbo vendorState;
    protected CountryEbo vendorCountry;

    public VendorContact() {
        vendorContactPhoneNumbers = new ArrayList<VendorContactPhoneNumber>();
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

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
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

    public CountryEbo getVendorCountry() {
        if ( StringUtils.isBlank(vendorCountryCode) ) {
            vendorCountry = null;
        } else {
            if ( vendorCountry == null || !StringUtils.equals( vendorCountry.getCode(),vendorCountryCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CountryEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, vendorCountryCode);
                    vendorCountry = moduleService.getExternalizableBusinessObject(CountryEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return vendorCountry;
    }

    public void setVendorCountry(CountryEbo vendorCountry) {
        this.vendorCountry = vendorCountry;
    }

    public StateEbo getVendorState() {
        if ( StringUtils.isBlank(vendorStateCode) || StringUtils.isBlank(vendorCountryCode ) ) {
            vendorState = null;
        } else {
            if ( vendorState == null || !StringUtils.equals( vendorState.getCode(),vendorStateCode) || !StringUtils.equals(vendorState.getCountryCode(), vendorCountryCode ) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(StateEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, vendorCountryCode);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, vendorStateCode);
                    vendorState = moduleService.getExternalizableBusinessObject(StateEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return vendorState;
    }

    public void setVendorState(StateEbo vendorState) {
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

    public List<VendorContactPhoneNumber> getVendorContactPhoneNumbers() {
        return vendorContactPhoneNumbers;
    }

    public void setVendorContactPhoneNumbers(List<VendorContactPhoneNumber> vendorContactPhoneNumbers) {
        this.vendorContactPhoneNumbers = vendorContactPhoneNumbers;
    }

}
