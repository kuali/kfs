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
package org.kuali.kfs.vnd.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;
import static org.kuali.kfs.vnd.fixture.VendorAddressFixture.address1;
import static org.kuali.kfs.vnd.fixture.VendorAddressFixture.address2;
import static org.kuali.kfs.vnd.fixture.VendorAddressFixture.address3;
import static org.kuali.kfs.vnd.fixture.VendorAddressFixture.address4;
import static org.kuali.kfs.vnd.fixture.VendorAddressFixture.address5;
import static org.kuali.kfs.vnd.fixture.VendorAddressFixture.address6;
import static org.kuali.kfs.vnd.fixture.VendorAddressFixture.address7;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.kfs.vnd.fixture.VendorAddressFixture;
import org.kuali.kfs.vnd.fixture.VendorRoutingChangesFixture;

@ConfigureContext(session = khuntley)
public class VendorServiceTest extends KualiTestBase {

    public VendorDetail oldVDtl;
    public VendorHeader oldVHdr;
    public VendorDetail newVDtl;
    public VendorHeader newVHdr;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        oldVDtl = new VendorDetail();
        oldVHdr = new VendorHeader();
        newVDtl = new VendorDetail();
        newVHdr = new VendorHeader();
    }

    @Override
    protected void tearDown() throws Exception {
        oldVDtl = null;
        oldVHdr = null;
        newVDtl = null;
        newVHdr = null;
        super.tearDown();
    }

    private List addresses;

    private void testGetVendorDefaultAddress(Integer expectedDefaultId, List addresses, String addressType, String campus) {
        VendorAddress address = SpringContext.getBean(VendorService.class).getVendorDefaultAddress(addresses, addressType, campus);
        Integer resultDefaultId = null;
        if (address != null) {
            resultDefaultId = address.getVendorAddressGeneratedIdentifier();
        }
        assertEquals("Did not get expected default", expectedDefaultId, resultDefaultId);
    }

    public void testGetVendorDefaultAddress_test1() {
        addresses = setupAddresses(new VendorAddressFixture[] { address5, address6 });
        testGetVendorDefaultAddress(address6.vendorAddressGeneratedIdentifier, addresses, "PO", "IN");
    }

    public void testGetVendorDefaultAddress_test2() {
        addresses = setupAddresses(new VendorAddressFixture[] { address1, address2 });
        testGetVendorDefaultAddress(address1.vendorAddressGeneratedIdentifier, addresses, "PO", "BL");
    }

    public void testGetVendorDefaultAddress_test3() {
        addresses = setupAddresses(new VendorAddressFixture[] { address1, address2 });
        testGetVendorDefaultAddress(address1.vendorAddressGeneratedIdentifier, addresses, "PO", "");
    }

    public void testGetVendorDefaultAddress_test4() {
        addresses = setupAddresses(new VendorAddressFixture[] { address1, address2 });
        testGetVendorDefaultAddress(null, addresses, "RM", "BL");
    }

    public void testGetVendorDefaultAddress_test5() {
        addresses = setupAddresses(new VendorAddressFixture[] { address1, address2, address3, address4 });
        testGetVendorDefaultAddress(address3.vendorAddressGeneratedIdentifier, addresses, "RM", "BL");
    }

    public void testGetVendorDefaultAddress_test6() {
        addresses = setupAddresses(new VendorAddressFixture[] { address1, address2, address4, address7 });
        testGetVendorDefaultAddress(address7.vendorAddressGeneratedIdentifier, addresses, "RM", "SB");
    }


    private List setupAddresses(VendorAddressFixture[] fixtures) {
        addresses = new ArrayList();
        for (VendorAddressFixture fixture : fixtures) {
            addresses.add(fixture.createAddress());
        }
        return addresses;
    }

    public void testNoRouteSignificantChangeOccurred_Complete_No_Changes() {
        VendorRoutingChangesFixture fixture = VendorRoutingChangesFixture.COMPLETE_NO_CHANGES;
        fixture.populate(oldVDtl, oldVHdr, newVDtl, newVHdr);
        assertTrue(SpringContext.getBean(VendorService.class).noRouteSignificantChangeOccurred(oldVDtl, oldVHdr, newVDtl, newVHdr));
    }

    public void testNoRouteSignificantChangeOccurred_Complete_VH_Change() {
        VendorRoutingChangesFixture fixture = VendorRoutingChangesFixture.COMPLETE_VH_CHANGE;
        fixture.populate(oldVDtl, oldVHdr, newVDtl, newVHdr);
        assertFalse(SpringContext.getBean(VendorService.class).noRouteSignificantChangeOccurred(oldVDtl, oldVHdr, newVDtl, newVHdr));
    }

    public void testNoRouteSignificantChangeOccurred_Complete_VSD1_Change() {
        VendorRoutingChangesFixture fixture = VendorRoutingChangesFixture.COMPLETE_VSD1_CHANGE;
        fixture.populate(oldVDtl, oldVHdr, newVDtl, newVHdr);
        assertFalse(SpringContext.getBean(VendorService.class).noRouteSignificantChangeOccurred(oldVDtl, oldVHdr, newVDtl, newVHdr));
    }

    public void testNoRouteSignificantChangeOccurred_Complete_VSSC1_Change() {
        VendorRoutingChangesFixture fixture = VendorRoutingChangesFixture.COMPLETE_VSSC1_CHANGE;
        fixture.populate(oldVDtl, oldVHdr, newVDtl, newVHdr);
        assertFalse(SpringContext.getBean(VendorService.class).noRouteSignificantChangeOccurred(oldVDtl, oldVHdr, newVDtl, newVHdr));
    }

    public void testNoRouteSignificantChangeOccurred_Complete_VA2_Change() {
        VendorRoutingChangesFixture fixture = VendorRoutingChangesFixture.COMPLETE_VA2_CHANGE;
        fixture.populate(oldVDtl, oldVHdr, newVDtl, newVHdr);
        assertFalse(SpringContext.getBean(VendorService.class).noRouteSignificantChangeOccurred(oldVDtl, oldVHdr, newVDtl, newVHdr));
    }

    public void testNoRouteSignificantChangeOccurred_Null_Olds() {
        VendorRoutingChangesFixture fixture = VendorRoutingChangesFixture.NULL_OLDS;
        fixture.populate(oldVDtl, oldVHdr, newVDtl, newVHdr);
        assertFalse(SpringContext.getBean(VendorService.class).noRouteSignificantChangeOccurred(oldVDtl, oldVHdr, newVDtl, newVHdr));
    }

    public void testNoRouteSignificantChangeOccurred_Null_News() {
        VendorRoutingChangesFixture fixture = VendorRoutingChangesFixture.NULL_NEWS;
        fixture.populate(oldVDtl, oldVHdr, newVDtl, newVHdr);
        assertFalse(SpringContext.getBean(VendorService.class).noRouteSignificantChangeOccurred(oldVDtl, oldVHdr, newVDtl, newVHdr));
    }

    public void testNoRouteSignificantChangeOccurred_Null_All() {
        VendorRoutingChangesFixture fixture = VendorRoutingChangesFixture.NULL_ALL;
        fixture.populate(oldVDtl, oldVHdr, newVDtl, newVHdr);
        assertTrue(SpringContext.getBean(VendorService.class).noRouteSignificantChangeOccurred(oldVDtl, oldVHdr, newVDtl, newVHdr));
    }
}

