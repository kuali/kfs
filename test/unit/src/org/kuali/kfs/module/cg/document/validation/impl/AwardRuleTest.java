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

import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.fixture.AwardFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.MaintenanceRuleTestBase;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class tests AwardRule validation class
 */
@ConfigureContext(session = khuntley)
public class AwardRuleTest extends MaintenanceRuleTestBase {

    private AwardRule rule;
    private Award award;
    private BusinessObjectService boService;
    private Long proposalNumber;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        proposalNumber = new Long(39603);
        boService = SpringContext.getBean(BusinessObjectService.class);
        award = new Award();
        rule = (AwardRule) setupMaintDocRule(newMaintDoc(award), AwardRule.class);
    }

    public void testCheckAccounts_False() {
        rule.newAwardCopy = award;
        assertFalse(rule.checkAccounts());
    }


    public void testCheckAccounts_True() {
        award = boService.findBySinglePrimaryKey(Award.class, proposalNumber);
        rule.newAwardCopy = award;
        assertTrue(rule.checkAccounts());
    }

    public void testCheckProposal_True() {
        award = boService.findBySinglePrimaryKey(Award.class, proposalNumber);
        rule.newAwardCopy = award;
        assertTrue(rule.checkProposal());
    }

    public void testCheckFederalPassThrough_True() {
        award = boService.findBySinglePrimaryKey(Award.class, proposalNumber);
        rule.newAwardCopy = award;
        assertTrue(rule.checkFederalPassThrough());
    }

    public void testAward() {
        award = boService.findBySinglePrimaryKey(Award.class, proposalNumber);
        assertTrue(rule.checkAwardOrganization(award.getPrimaryAwardOrganization()));
        assertTrue(rule.checkAwardSubcontractor(award.getAwardSubcontractors().get(0)));
        assertTrue(rule.checkAwardAccount(award.getAwardAccounts().get(0)));
        assertTrue(rule.checkAwardProjectDirector(award.getAwardProjectDirectors().get(0)));
        assertTrue(rule.checkAwardFundManager(award.getAwardFundManagers().get(0)));
    }

    public void testCheckSuspendedAwardInvoicing_True() {
        award = AwardFixture.CG_AWARD_INV_ACCOUNT.createAward();
        rule.newAwardCopy = award;
        assertTrue(rule.checkExcludedFromInvoicing());
    }

    public void testCheckInvoicingOption_True() {
        award = AwardFixture.CG_AWARD_INV_ACCOUNT.createAward();
        rule.newAwardCopy = award;
        assertTrue(rule.checkInvoicingOption());
    }
}
