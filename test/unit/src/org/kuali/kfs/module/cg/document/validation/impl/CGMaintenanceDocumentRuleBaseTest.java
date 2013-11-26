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

import java.sql.Date;

import org.kuali.kfs.module.cg.businessobject.Agency;
import org.kuali.kfs.module.cg.businessobject.AgencyAddress;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.businessobject.AwardFundManager;
import org.kuali.kfs.module.cg.businessobject.AwardProjectDirector;
import org.kuali.kfs.module.cg.fixture.AgencyAddressFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.MaintenanceRuleTestBase;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class tests CGMaintenanceDocumentRuleBase Validation Class
 */
@ConfigureContext(session = khuntley)
public class CGMaintenanceDocumentRuleBaseTest extends MaintenanceRuleTestBase {

    private CGMaintenanceDocumentRuleBase rule;
    private Agency agency;
    private long agencyNumber;
    private BusinessObjectService boService;
    private Award award;
    private Long proposalNumber;

    @Override
    public void setUp() throws Exception {
        rule = new CGMaintenanceDocumentRuleBase();
        agencyNumber = new Long(12851);
        proposalNumber = new Long(39603);
        boService = SpringContext.getBean(BusinessObjectService.class);
        award = boService.findBySinglePrimaryKey(Award.class, proposalNumber);
        agency = boService.findBySinglePrimaryKey(Agency.class, agencyNumber);
        // save a test agency address since we don't have those in our test data
        AgencyAddress agencyAddress = AgencyAddressFixture.CG_AGENCY_ADD3.createAgencyAddress();
        agencyAddress.setAgencyNumber(String.valueOf(agencyNumber));
        boService.save(agencyAddress);
    }

    public void testCheckEndAfterBegin() {
        Date begin = new Date(11111);
        Date end = new Date(11115);
        assertFalse(rule.checkEndAfterBegin(begin, begin, ""));
        assertTrue(rule.checkEndAfterBegin(begin, end, ""));
    }

    public void testAll() {
        assertTrue(rule.checkPrimary(agency.getAgencyAddresses(), AgencyAddress.class, KFSPropertyConstants.AGENCY_ADDRESSES, Agency.class));
        assertTrue(rule.checkProjectDirectorsExist(award.getAwardProjectDirectors(), AwardProjectDirector.class, KFSPropertyConstants.AWARD_PROJECT_DIRECTORS));
        assertTrue(rule.checkFundManagersExist(award.getAwardFundManagers(), AwardFundManager.class, KFSPropertyConstants.AWARD_FUND_MANAGERS));
        assertTrue(rule.checkProjectDirectorsExist(award.getAwardAccounts(), AwardAccount.class, KFSPropertyConstants.AWARD_ACCOUNTS));
        assertTrue(rule.checkProjectDirectorsStatuses(award.getAwardProjectDirectors(), AwardProjectDirector.class, KFSPropertyConstants.AWARD_PROJECT_DIRECTORS));
        assertTrue(rule.checkFederalPassThrough(award.getFederalPassThroughIndicator(), award.getAgency(), award.getFederalPassThroughAgencyNumber(), Award.class, KFSPropertyConstants.FEDERAL_PASS_THROUGH_INDICATOR));
        assertTrue(rule.checkAgencyNotEqualToFederalPassThroughAgency(award.getAgency(), award.getFederalPassThroughAgency(), KFSPropertyConstants.AGENCY_NUMBER, KFSPropertyConstants.FEDERAL_PASS_THROUGH_AGENCY_NUMBER));
    }
}
