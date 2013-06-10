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
package org.kuali.kfs.module.cg.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.Agency;
import org.kuali.kfs.module.cg.businessobject.AgencyAddress;
import org.kuali.kfs.module.cg.fixture.AgencyFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.MaintenanceRuleTestBase;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class tests the AgencyRule validation Class
 */
@ConfigureContext(session = khuntley)
public class AgencyRuleTest extends MaintenanceRuleTestBase {

    private AgencyRule rule;
    private Agency agency;
    private AgencyAddress agencyAddress;
    private BusinessObjectService boService;
    private Long agencyNumber;

    private static final String AGENCY_ADDRESS_NAME = "Address Name";
    private static final String AGENCY_ADDRESS_COUNTRY_CODE_US = "US";
    private static final String AGENCY_ADDRESS_COUNTRY_CODE_RO = "RO";
    private static final String AGENCY_ADDRESS_STATE_CODE = "NY";
    private static final String AGENCY_ADDRESS_ZIP_CODE = "14850";
    private static final String AGENCY_ADDRESS_PROVINCE = "Iasi";

    public void setUp() throws Exception {
        super.setUp();
        agencyNumber = new Long(12500);
        boService = SpringContext.getBean(BusinessObjectService.class);
        agency = new Agency();
        agencyAddress = new AgencyAddress();
        rule = (AgencyRule) setupMaintDocRule(newMaintDoc(agency), AgencyRule.class);
    }


    public void testCheckAgencyReportsTo_True() {
        agency = (Agency) boService.findBySinglePrimaryKey(Agency.class, agencyNumber);
        assertTrue(rule.checkAgencyReportsTo(newMaintDoc(agency)));
    }

    public void testValidateAgencyType_True() {
        agency = AgencyFixture.CG_AGENCY1.createAgency();
        rule.newAgency = agency;
        assertTrue(rule.validateAgencyType(newMaintDoc(agency)));
    }

    public void testValidateAgencyReportingName_True() {
        agency = AgencyFixture.CG_AGENCY1.createAgency();
        rule.newAgency = agency;
        assertTrue(rule.validateAgencyReportingName(newMaintDoc(agency)));
    }


    /**
     * This method if checkAddressIsValid returns false when country code is US and state code and zip code are empty
     */
    public void testCheckAddressIsValid_CountryUS_False() {
        agencyAddress.setAgencyAddressName(AGENCY_ADDRESS_NAME);
        agencyAddress.setAgencyCountryCode(AGENCY_ADDRESS_COUNTRY_CODE_US);
        agencyAddress.setAgencyStateCode("");
        agencyAddress.setAgencyZipCode("");
        //To set customer exists value to "Create New Customer"
        agency.setCustomerCreated(CGConstants.AGENCY_CREATE_NEW_CUSTOMER_CODE);
        rule.newAgency = agency;        
        AgencyRule rule = (AgencyRule) setupMaintDocRule(newMaintDoc(agency), AgencyRule.class);
        boolean result = rule.checkAddressIsValid(agencyAddress);
        assertEquals("When agency address has country code " + AGENCY_ADDRESS_COUNTRY_CODE_US + " and state code and zip code are empty checkAddressIsValid should return false. ", false, result);
    }


    /**
     * This method checks that checkAddressIsValid returns false when country code is not US and InternationalProvinceName and
     * InternationalMailCode are not set.
     */
    public void testCheckAddressIsValid_CountryNonUS_False() {
        agencyAddress.setAgencyAddressName(AGENCY_ADDRESS_NAME);
        agencyAddress.setAgencyCountryCode(AGENCY_ADDRESS_COUNTRY_CODE_RO);
        agencyAddress.setAgencyAddressInternationalProvinceName("");
        agencyAddress.setAgencyZipCode("");
        //To set customer exists value to "Create New Customer"
        agency.setCustomerCreated(CGConstants.AGENCY_CREATE_NEW_CUSTOMER_CODE);
        rule.newAgency = agency;   
        AgencyRule rule = (AgencyRule) setupMaintDocRule(newMaintDoc(agency), AgencyRule.class);
        boolean result = rule.checkAddressIsValid(agencyAddress);
        assertEquals("When agency address has country code " + AGENCY_ADDRESS_COUNTRY_CODE_RO + " and province and International Mail Code are empty checkAddressIsValid should return false. ", false, result);
    }
}
