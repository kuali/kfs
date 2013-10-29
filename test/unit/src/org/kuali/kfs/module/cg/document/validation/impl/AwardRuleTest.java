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
        award = (Award) boService.findBySinglePrimaryKey(Award.class, proposalNumber);
        rule.newAwardCopy = award;
        assertTrue(rule.checkAccounts());
    }

    public void testCheckProposal_True() {
        award = (Award) boService.findBySinglePrimaryKey(Award.class, proposalNumber);
        rule.newAwardCopy = award;
        assertTrue(rule.checkProposal());
    }

    public void testCheckFederalPassThrough_True() {
        award = (Award) boService.findBySinglePrimaryKey(Award.class, proposalNumber);
        rule.newAwardCopy = award;
        assertTrue(rule.checkFederalPassThrough());
    }

    public void testAward() {
        award = (Award) boService.findBySinglePrimaryKey(Award.class, proposalNumber);
        assertTrue(rule.checkAwardOrganization(award.getPrimaryAwardOrganization()));
        assertTrue(rule.checkAwardSubcontractor(award.getAwardSubcontractors().get(0)));
        assertTrue(rule.checkAwardAccount(award.getAwardAccounts().get(0)));
        assertTrue(rule.checkAwardProjectDirector(award.getAwardProjectDirectors().get(0)));
        assertTrue(rule.checkAwardFundManager(award.getAwardFundManagers().get(0)));
    }
    
    public void testCheckSuspendedAwardInvoicing_True() {
        award = AwardFixture.CG_AWARD_INV_ACCOUNT.createAward();
        rule.newAwardCopy = award;
        assertTrue(rule.checkSuspendedAwardInvoicing());
    }
    
    public void testCheckInvoicingOptions_True() {
        award = AwardFixture.CG_AWARD_INV_ACCOUNT.createAward();
        rule.newAwardCopy = award;
        assertTrue(rule.checkInvoicingOptions());
    }
}
