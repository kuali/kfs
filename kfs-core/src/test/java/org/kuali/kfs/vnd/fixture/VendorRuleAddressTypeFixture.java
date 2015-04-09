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
package org.kuali.kfs.vnd.fixture;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorType;
import org.kuali.kfs.vnd.fixture.VendorTestConstants.AddressRequiredFields;
import org.kuali.kfs.vnd.fixture.VendorTestConstants.AddressTypes;
import org.kuali.kfs.vnd.fixture.VendorTestConstants.StatesZips;

/**
 * This is an enumeration of configurations of 'type codes' and 'address type codes' which should be useful for testing the code
 * validating the implementation of the business rules involving Vendor address types.
 */
public enum VendorRuleAddressTypeFixture {

    WITH_PO_TYPE_AND_PO_ADDR_TYPES(AddressTypes.poType, AddressTypes.poType, true, AddressTypes.poType, false), WITH_DV_TYPE_AND_RM_ADDR_TYPES(AddressTypes.dvType, AddressTypes.rmType, true, AddressTypes.rmType, false), WITH_PO_TYPE_AND_RM_ADDR_TYPES(AddressTypes.poType, AddressTypes.rmType, true, AddressTypes.rmType, false), WITH_PO_TYPE_AND_ONE_PO_AND_ONE_RM_ADDR_TYPES(AddressTypes.poType, AddressTypes.poType, true, AddressTypes.rmType, true), ;

    private String typeCode;
    private String addrTypeCode1;
    private String addrTypeCode2;
    private boolean defaultAddr1;
    private boolean defaultAddr2;

    VendorRuleAddressTypeFixture(String typeCode, String addrTypeCode1, boolean default1, String addrTypeCode2, boolean default2) {
        this.typeCode = typeCode;
        this.addrTypeCode1 = addrTypeCode1;
        this.defaultAddr1 = default1;
        this.addrTypeCode2 = addrTypeCode2;
        this.defaultAddr2 = default2;
    }

    @SuppressWarnings("deprecation")
    public VendorDetail populateVendor(VendorDetail vndr) {
        vndr.getVendorHeader().setVendorTypeCode(this.typeCode);
        VendorType type = new VendorType();
        type.setVendorAddressTypeRequiredCode(this.addrTypeCode1);
        vndr.getVendorHeader().setVendorType(type);
        List<VendorAddress> addrList = new ArrayList();
        
        VendorAddress addr1 = new VendorAddress();
        VendorAddress addr2 = new VendorAddress();
        
        addr1.setVendorAddressTypeCode(this.addrTypeCode1);
        addr1.setVendorDefaultAddressIndicator(this.defaultAddr1);
        addr1.setVendorLine1Address(AddressRequiredFields.line1Address);
        addr1.setVendorCityName(AddressRequiredFields.cityName);
        addr1.setVendorStateCode(StatesZips.stateCd);
        addr1.setVendorZipCode(StatesZips.zipCode);
        addr1.setVendorCountryCode(KFSConstants.COUNTRY_CODE_UNITED_STATES);
        
        addr2.setVendorAddressTypeCode(this.addrTypeCode2);
        addr2.setVendorDefaultAddressIndicator(this.defaultAddr2);
        addr2.setVendorLine1Address(AddressRequiredFields.line1Address);
        addr2.setVendorCityName(AddressRequiredFields.cityName);
        addr2.setVendorStateCode(StatesZips.stateCd);
        addr2.setVendorZipCode(StatesZips.zipCode);
        addr2.setVendorCountryCode(KFSConstants.COUNTRY_CODE_UNITED_STATES);
        
        addrList.add(addr1);
        addrList.add(addr2);
        vndr.setVendorAddresses(addrList);
        return vndr;
    }
}
