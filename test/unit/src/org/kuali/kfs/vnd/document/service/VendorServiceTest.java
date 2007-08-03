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
package org.kuali.module.vendor.service;

import static org.kuali.kfs.util.SpringServiceLocator.getVendorService;
import static org.kuali.module.vendor.fixtures.VendorAddressFixture.address1;
import static org.kuali.module.vendor.fixtures.VendorAddressFixture.address2;
import static org.kuali.module.vendor.fixtures.VendorAddressFixture.address3;
import static org.kuali.module.vendor.fixtures.VendorAddressFixture.address4;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.context.KualiTestBase;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.bo.VendorHeader;
import org.kuali.module.vendor.fixtures.VendorAddressFixture;
import org.kuali.module.vendor.fixtures.VendorRoutingChangesFixture;
import org.kuali.test.RequiresSpringContext;

@RequiresSpringContext(session = KHUNTLEY)
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
        VendorAddress address = getVendorService().getVendorDefaultAddress(addresses, addressType, campus);
        Integer resultDefaultId = null;
        if (address != null) {
            resultDefaultId = address.getVendorAddressGeneratedIdentifier();
        }
        assertEquals("Did not get expected default", expectedDefaultId, resultDefaultId);
    }
    
    public void testGetVendorDefaultAddress_test1() {
        addresses = setupAddresses(new VendorAddressFixture[] {address1, address2});
        testGetVendorDefaultAddress(address2.vendorAddressGeneratedIdentifier, addresses, "PO", "IN");
    }
    
    public void testGetVendorDefaultAddress_test2() {
        addresses = setupAddresses(new VendorAddressFixture[] {address1, address2});
        testGetVendorDefaultAddress(address1.vendorAddressGeneratedIdentifier, addresses, "PO", "BL");
    }
    
    public void testGetVendorDefaultAddress_test3() {
        addresses = setupAddresses(new VendorAddressFixture[] {address1, address2});
        testGetVendorDefaultAddress(address1.vendorAddressGeneratedIdentifier, addresses, "PO", "");
    }
    
    public void testGetVendorDefaultAddress_test4() {
        addresses = setupAddresses(new VendorAddressFixture[] {address1, address2});
        testGetVendorDefaultAddress(null, addresses, "RM", "BL");
    }
    
    public void testGetVendorDefaultAddress_test5() {
        addresses = setupAddresses(new VendorAddressFixture[] {address1, address2, address3, address4});
        testGetVendorDefaultAddress(address3.vendorAddressGeneratedIdentifier, addresses, "RM", "BL");
    }
    
    public void testGetVendorDefaultAddress_test6() {
        addresses = setupAddresses(new VendorAddressFixture[] {address1, address2, address3, address4});
        testGetVendorDefaultAddress(address4.vendorAddressGeneratedIdentifier, addresses, "RM", "SB");
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
        fixture.populate( oldVDtl, oldVHdr, newVDtl, newVHdr );
        assertTrue( getVendorService().noRouteSignificantChangeOccurred( oldVDtl, oldVHdr, newVDtl, newVHdr ) );
    }
    
    public void testNoRouteSignificantChangeOccurred_Complete_VH_Change() {
        VendorRoutingChangesFixture fixture = VendorRoutingChangesFixture.COMPLETE_VH_CHANGE;
        fixture.populate( oldVDtl, oldVHdr, newVDtl, newVHdr );
        assertFalse( getVendorService().noRouteSignificantChangeOccurred( oldVDtl, oldVHdr, newVDtl, newVHdr ) );
    }
    
    public void testNoRouteSignificantChangeOccurred_Complete_VSD1_Change() {
        VendorRoutingChangesFixture fixture = VendorRoutingChangesFixture.COMPLETE_VSD1_CHANGE;
        fixture.populate( oldVDtl, oldVHdr, newVDtl, newVHdr );
        assertFalse( getVendorService().noRouteSignificantChangeOccurred( oldVDtl, oldVHdr, newVDtl, newVHdr ) );
    }
    
    public void testNoRouteSignificantChangeOccurred_Complete_VSSC1_Change() {
        VendorRoutingChangesFixture fixture = VendorRoutingChangesFixture.COMPLETE_VSSC1_CHANGE;
        fixture.populate( oldVDtl, oldVHdr, newVDtl, newVHdr );
        assertFalse( getVendorService().noRouteSignificantChangeOccurred( oldVDtl, oldVHdr, newVDtl, newVHdr ) );
    }
    
    public void testNoRouteSignificantChangeOccurred_Complete_VA2_Change() {
        VendorRoutingChangesFixture fixture = VendorRoutingChangesFixture.COMPLETE_VA2_CHANGE;
        fixture.populate( oldVDtl, oldVHdr, newVDtl, newVHdr );
        assertFalse( getVendorService().noRouteSignificantChangeOccurred( oldVDtl, oldVHdr, newVDtl, newVHdr ) );
    }
    
    public void testNoRouteSignificantChangeOccurred_Null_Olds() {
        VendorRoutingChangesFixture fixture = VendorRoutingChangesFixture.NULL_OLDS;
        fixture.populate( oldVDtl, oldVHdr, newVDtl, newVHdr );
        assertFalse( getVendorService().noRouteSignificantChangeOccurred( oldVDtl, oldVHdr, newVDtl, newVHdr ) );
    }
    
    public void testNoRouteSignificantChangeOccurred_Null_News() {
        VendorRoutingChangesFixture fixture = VendorRoutingChangesFixture.NULL_NEWS;
        fixture.populate( oldVDtl, oldVHdr, newVDtl, newVHdr );
        assertFalse( getVendorService().noRouteSignificantChangeOccurred( oldVDtl, oldVHdr, newVDtl, newVHdr ) );
    }
    
    public void testNoRouteSignificantChangeOccurred_Null_All() {
        VendorRoutingChangesFixture fixture = VendorRoutingChangesFixture.NULL_ALL;
        fixture.populate( oldVDtl, oldVHdr, newVDtl, newVHdr );
        assertTrue( getVendorService().noRouteSignificantChangeOccurred( oldVDtl, oldVHdr, newVDtl, newVHdr ) );
    }    
}
