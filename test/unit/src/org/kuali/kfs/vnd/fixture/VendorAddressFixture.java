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

import static org.kuali.kfs.vnd.fixture.VendorDefaultAddressFixture.campusIN;
import static org.kuali.kfs.vnd.fixture.VendorDefaultAddressFixture.campusKO;
import static org.kuali.kfs.vnd.fixture.VendorDefaultAddressFixture.campusSB;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDefaultAddress;

public enum VendorAddressFixture {

    address1(new Integer(1), "PO", "line1", "line2", "thisCity", "IN", "44444", "US", "attentionTo", "", "knoreceipt-l@indiana.edu", "", "555-555-5555", true,true, null),
    address2(new Integer(2), "PO", "line1", "line2", "thisCity", "IN", "44444", "US", "attentionTo", "", "knoreceipt-l@indiana.edu", "", "555-555-5555", false,true, new VendorDefaultAddressFixture[] { campusKO, campusIN }),
    address3(new Integer(3), "RM", "line1", "line2", "thisCity", "IN", "44444", "US", "attentionTo", "", "knoreceipt-l@indiana.edu", "", "555-555-5555", true,true, null),
    address4(new Integer(4), "RM", "line1", "line2", "thisCity", "IN", "44444", "US", "attentionTo", "", "knoreceipt-l@indiana.edu", "", "555-555-5555", false,false, new VendorDefaultAddressFixture[] { campusSB }),
    address5(new Integer(5), "PO", "line1", "line2", "thisCity", "IN", "44444", "US", "attentionTo", "", "knoreceipt-l@indiana.edu", "", "555-555-5555", false,true, null),
    address6(new Integer(6), "PO", "line1", "line2", "thisCity", "IN", "44444", "US", "attentionTo", "", "knoreceipt-l@indiana.edu", "", "555-555-5555", true,true, new VendorDefaultAddressFixture[] { campusKO, campusIN }),
    address7(new Integer(7), "RM", "line1", "line2", "thisCity", "IN", "44444", "US", "attentionTo", "", "knoreceipt-l@indiana.edu", "", "555-555-5555", true,true, new VendorDefaultAddressFixture[] { campusSB }), ;

    public final Integer vendorAddressGeneratedIdentifier;
    public final String vendorAddressTypeCode;
    public final String vendorLine1Address;
    public final String vendorLine2Address;
    public final String vendorCityName;
    public final String vendorStateCode;
    public final String vendorZipCode;
    public final String vendorCountryCode;
    public final String vendorAttentionName;
    public final String vendorAddressInternationalProvinceName;
    public final String vendorAddressEmailAddress;
    public final String vendorBusinessToBusinessUrlAddress;
    public final String vendorFaxNumber;
    public final boolean vendorDefaultAddressIndicator;
    public final boolean active;
    public final List defaultAddresses = new ArrayList();

    private VendorAddressFixture(Integer vendorAddressGeneratedIdentifier, String vendorAddressTypeCode, String vendorLine1Address, String vendorLine2Address, String vendorCityName, String vendorStateCode, String vendorZipCode, String vendorCountryCode, String vendorAttentionName, String vendorAddressInternationalProvinceName, String vendorAddressEmailAddress, String vendorBusinessToBusinessUrlAddress, String vendorFaxNumber, boolean vendorDefaultAddressIndicator,boolean active, VendorDefaultAddressFixture[] campuses) {
        this.vendorAddressGeneratedIdentifier = vendorAddressGeneratedIdentifier;
        this.vendorAddressTypeCode = vendorAddressTypeCode;
        this.vendorLine1Address = vendorLine1Address;
        this.vendorLine2Address = vendorLine2Address;
        this.vendorCityName = vendorCityName;
        this.vendorStateCode = vendorStateCode;
        this.vendorZipCode = vendorZipCode;
        this.vendorCountryCode = vendorCountryCode;
        this.vendorAttentionName = vendorAttentionName;
        this.vendorAddressInternationalProvinceName = vendorAddressInternationalProvinceName;
        this.vendorAddressEmailAddress = vendorAddressEmailAddress;
        this.vendorBusinessToBusinessUrlAddress = vendorBusinessToBusinessUrlAddress;
        this.vendorFaxNumber = vendorFaxNumber;
        this.vendorDefaultAddressIndicator = vendorDefaultAddressIndicator;
        this.active=active;
        if (campuses != null) {
            for (VendorDefaultAddressFixture campus : campuses) {
                VendorDefaultAddress vda = new VendorDefaultAddress();
                vda.setVendorCampusCode(campus.vendorCampusCode);
                vda.setActive(campus.active);
                defaultAddresses.add(vda);
            }
        }
    }

    public VendorAddress createAddress() {
        VendorAddress address = new VendorAddress();
        address.setVendorAddressGeneratedIdentifier(this.vendorAddressGeneratedIdentifier);
        address.setVendorAddressTypeCode(this.vendorAddressTypeCode);
        address.setVendorLine1Address(this.vendorLine1Address);
        address.setVendorLine2Address(this.vendorLine2Address);
        address.setVendorCityName(this.vendorCityName);
        address.setVendorStateCode(this.vendorStateCode);
        address.setVendorZipCode(this.vendorZipCode);
        address.setVendorCountryCode(this.vendorCountryCode);
        address.setVendorAttentionName(this.vendorAttentionName);
        address.setVendorAddressInternationalProvinceName(this.vendorAddressInternationalProvinceName);
        address.setVendorAddressEmailAddress(this.vendorAddressEmailAddress);
        address.setVendorBusinessToBusinessUrlAddress(this.vendorBusinessToBusinessUrlAddress);
        address.setVendorFaxNumber(this.vendorFaxNumber);
        address.setVendorDefaultAddressIndicator(this.vendorDefaultAddressIndicator);
        address.setVendorDefaultAddresses(defaultAddresses);
        address.setActive(this.active);
        return address;
    }

}
