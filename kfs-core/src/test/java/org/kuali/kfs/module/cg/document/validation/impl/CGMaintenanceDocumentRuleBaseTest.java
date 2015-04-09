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
package org.kuali.kfs.module.cg.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.sql.Date;

import org.kuali.kfs.module.cg.businessobject.Agency;
import org.kuali.kfs.module.cg.businessobject.AgencyAddress;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.businessobject.AwardProjectDirector;
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
        agencyNumber = new Long(55076);
        proposalNumber = new Long(39603);
        boService = SpringContext.getBean(BusinessObjectService.class);
        award = boService.findBySinglePrimaryKey(Award.class, proposalNumber);
        agency = boService.findBySinglePrimaryKey(Agency.class, agencyNumber);
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
        assertTrue(rule.checkFundManagersExist(award.getAwardFundManagers(), KFSPropertyConstants.AWARD_FUND_MANAGERS));
        assertTrue(rule.checkProjectDirectorsExist(award.getAwardAccounts(), AwardAccount.class, KFSPropertyConstants.AWARD_ACCOUNTS));
        assertTrue(rule.checkProjectDirectorsStatuses(award.getAwardProjectDirectors(), AwardProjectDirector.class, KFSPropertyConstants.AWARD_PROJECT_DIRECTORS));
        assertTrue(rule.checkFederalPassThrough(award.getFederalPassThroughIndicator(), award.getAgency(), award.getFederalPassThroughAgencyNumber(), Award.class, KFSPropertyConstants.FEDERAL_PASS_THROUGH_INDICATOR));
        assertTrue(rule.checkAgencyNotEqualToFederalPassThroughAgency(award.getAgency(), award.getFederalPassThroughAgency(), KFSPropertyConstants.AGENCY_NUMBER, KFSPropertyConstants.FEDERAL_PASS_THROUGH_AGENCY_NUMBER));
    }
}
