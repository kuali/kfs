/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.document.service.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.cg.businessobject.AgencyAddress;
import org.kuali.kfs.module.cg.dataaccess.AgencyAddressDao;
import org.kuali.kfs.module.cg.document.service.AgencyAddressService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class tests the AgencyAddressServiceImpl and AgencyAddressDaoOjb service.
 */
@ConfigureContext(session = khuntley)
public class AgencyAddressServiceDaoImplTest extends KualiTestBase {

    private AgencyAddressService service;
    private AgencyAddress agencyAddress;
    private BusinessObjectService boService;
    private Long agencyAddressIdentifier;
    private String agencyAddressName = "Agency Name";
    private String agencyCityName = "San Diego";
    private String agencyContactName = "Eric";
    private String address = "Line 1";
    private String state = "CA";
    private String zipcode = "92115";
    private String agencyNumber = "11500";
    private AgencyAddressDao daoService;

    public void setUp() throws Exception {
        super.setUp();
        daoService = SpringContext.getBean(AgencyAddressDao.class);
        service = SpringContext.getBean(AgencyAddressService.class);
        boService = SpringContext.getBean(BusinessObjectService.class);
        agencyAddress = new AgencyAddress();
    }

    public void testService() {
        buildAgencyAddress();
        assertNull("Book id should be null.", agencyAddress.getAgencyAddressIdentifier());
        assertNull("Book objectId should be null.", agencyAddress.getObjectId());
        assertNull("Book versionNumber should be null.", agencyAddress.getVersionNumber());
        boService.save(agencyAddress);
        assertNotNull("Book should have an id now.", agencyAddress.getAgencyAddressIdentifier());
        assertNotNull("Book should have an object id now.", agencyAddress.getObjectId());
        assertNotNull("Book should have a versionNumber now.", agencyAddress.getVersionNumber());
        agencyAddressIdentifier = agencyAddress.getAgencyAddressIdentifier();
        AgencyAddress test = service.getByPrimaryKey(agencyNumber, agencyAddressIdentifier.intValue());
        assertNotNull(test);
        assertTrue(compare(test));
        assertTrue(service.agencyAddressActive(agencyNumber, agencyAddressIdentifier.intValue()));
        test = service.getPrimaryAddress(agencyNumber);
        assertNotNull(test);
        assertTrue(compare(test));
    }

    public void testGetPrimaryAddress() {
        buildAgencyAddress();
        boService.save(agencyAddress);
        AgencyAddress test = daoService.getPrimaryAddress(agencyNumber);
        assertTrue(compare(test));
    }

    public boolean compare(AgencyAddress comparator) {
        boolean similar = true;
        similar &= comparator.getAgencyAddressName().equals(agencyAddressName);
        similar &= comparator.getAgencyCityName().equals(agencyCityName);
        similar &= comparator.getAgencyContactName().equals(agencyContactName);
        similar &= comparator.getAgencyLine1StreetAddress().equals(address);
        similar &= comparator.getAgencyStateCode().equals(state);
        similar &= comparator.getAgencyZipCode().equals(zipcode);
        return similar;
    }

    private void buildAgencyAddress() {
        agencyAddress.setAgencyAddressName(agencyAddressName);
        agencyAddress.setAgencyCityName(agencyCityName);
        agencyAddress.setAgencyContactName(agencyContactName);
        agencyAddress.setAgencyLine1StreetAddress(address);
        agencyAddress.setAgencyStateCode(state);
        agencyAddress.setAgencyZipCode(zipcode);
        agencyAddress.setAgencyNumber(agencyNumber);
        agencyAddress.setCustomerAddressTypeCode("P");
    }
}
