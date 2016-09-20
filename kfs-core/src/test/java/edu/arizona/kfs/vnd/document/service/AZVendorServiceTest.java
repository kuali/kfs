/*
 * Copyright 2009 The Kuali Foundation.
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
package edu.arizona.kfs.vnd.document.service;

import edu.arizona.kfs.vnd.businessobject.VendorDetailExtension;
import edu.arizona.kfs.vnd.document.service.impl.VendorServiceImpl;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.kfs.vnd.fixture.VendorRoutingChangesFixture;

@ConfigureContext
public class AZVendorServiceTest extends KualiTestBase {
    public VendorDetail oldVDtl;
    public VendorHeader oldVHdr;
    public VendorDetail newVDtl;
    public VendorHeader newVHdr;
    private VendorRoutingChangesFixture fixture;
    private VendorDetailExtension oldVDtlExtension;
    private VendorDetailExtension newVDtlExtension;

    protected void setUp() throws Exception {
        super.setUp();
        oldVDtl = new VendorDetail();
        oldVHdr = new VendorHeader();
        newVDtl = new VendorDetail();
        newVHdr = new VendorHeader();
        fixture = VendorRoutingChangesFixture.COMPLETE_NO_CHANGES;
        fixture.populate(oldVDtl, oldVHdr, newVDtl, newVHdr);
        oldVDtlExtension = new VendorDetailExtension();
        newVDtlExtension = new VendorDetailExtension();
    }

    protected void tearDown() throws Exception {
        oldVDtl = null;
        oldVHdr = null;
        newVDtl = null;
        newVHdr = null;
        oldVDtlExtension = null;
        newVDtlExtension = null;
        super.tearDown();
    }

    public void testNoRouteSignificantChangeOccurredForExtensions_No_Changes() {
        oldVDtlExtension.setConflictOfInterest(null);
        oldVDtlExtension.setAzSalesTaxLicense(null);
        oldVDtlExtension.setTucSalesTaxLicense(null);
        newVDtlExtension.setConflictOfInterest(null);
        newVDtlExtension.setAzSalesTaxLicense(null);
        newVDtlExtension.setTucSalesTaxLicense(null);

        assertTrue(SpringContext.getBean(VendorServiceImpl.class).noRouteSignificantChangeOccurredForExtensions(oldVDtlExtension, newVDtlExtension));
    }

    public void testNoRouteSignificantChangeOccurredForExtensions_ConflictOfInterest_Change() {
        oldVDtlExtension.setConflictOfInterest(null);
        oldVDtlExtension.setAzSalesTaxLicense(null);
        oldVDtlExtension.setTucSalesTaxLicense(null);
        newVDtlExtension.setConflictOfInterest("None");
        newVDtlExtension.setAzSalesTaxLicense(null);
        newVDtlExtension.setTucSalesTaxLicense(null);

        assertFalse(SpringContext.getBean(VendorServiceImpl.class).noRouteSignificantChangeOccurredForExtensions(oldVDtlExtension, newVDtlExtension));
    }

    public void testNoRouteSignificantChangeOccurredForExtensions_AzSalesTax_Change() {
        oldVDtlExtension.setConflictOfInterest(null);
        oldVDtlExtension.setAzSalesTaxLicense(null);
        oldVDtlExtension.setTucSalesTaxLicense(null);
        newVDtlExtension.setConflictOfInterest(null);
        newVDtlExtension.setAzSalesTaxLicense("12345");
        newVDtlExtension.setTucSalesTaxLicense(null);

        assertFalse(SpringContext.getBean(VendorServiceImpl.class).noRouteSignificantChangeOccurredForExtensions(oldVDtlExtension, newVDtlExtension));
    }

    public void testNoRouteSignificantChangeOccurredForExtensions_TucSalesTax_Change() {
        oldVDtlExtension.setConflictOfInterest(null);
        oldVDtlExtension.setAzSalesTaxLicense(null);
        oldVDtlExtension.setTucSalesTaxLicense(null);
        newVDtlExtension.setConflictOfInterest(null);
        newVDtlExtension.setAzSalesTaxLicense(null);
        newVDtlExtension.setTucSalesTaxLicense("67890");

        assertFalse(SpringContext.getBean(VendorServiceImpl.class).noRouteSignificantChangeOccurredForExtensions(oldVDtlExtension, newVDtlExtension));
    }
}
