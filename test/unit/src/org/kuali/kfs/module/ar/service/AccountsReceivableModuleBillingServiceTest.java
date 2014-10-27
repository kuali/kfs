/*
 * Copyright 2014 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.integration.ar.AccountsReceivableModuleBillingService;
import org.kuali.kfs.module.ar.businessobject.Bill;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.businessobject.MilestoneSchedule;
import org.kuali.kfs.module.ar.businessobject.PredeterminedBillingSchedule;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * Tests the AccountsReceivableModuleBillingService
 */
@ConfigureContext(session = khuntley)
public class AccountsReceivableModuleBillingServiceTest extends KualiTestBase {
    protected AccountsReceivableModuleBillingService accountsReceivableModuleBillingService;
    protected BusinessObjectService businessObjectService;

    @Override
    public void setUp() {
        accountsReceivableModuleBillingService = SpringContext.getBean(AccountsReceivableModuleBillingService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    }

    public void testHasActiveBills() {
        setupPredeterminedBillingSchedule(1L, true);
        setupPredeterminedBillingSchedule(2L, false);

        assertTrue(accountsReceivableModuleBillingService.hasActiveBills(1L));
        assertFalse(accountsReceivableModuleBillingService.hasActiveBills(2L));
        assertFalse(accountsReceivableModuleBillingService.hasActiveBills(111L));
    }

    public void testHasActiveMilestones() {
        setupMilestoneSchedule(1L, true);
        setupMilestoneSchedule(2L, false);

        assertTrue(accountsReceivableModuleBillingService.hasActiveMilestones(1L));
        assertFalse(accountsReceivableModuleBillingService.hasActiveMilestones(2L));
        assertFalse(accountsReceivableModuleBillingService.hasActiveMilestones(111L));
    }

    protected void setupMilestoneSchedule(Long proposalNumber, boolean active) {
        Milestone milestone = new Milestone();
        milestone.setProposalNumber(proposalNumber);
        milestone.setMilestoneNumber(proposalNumber);
        milestone.setMilestoneIdentifier(proposalNumber + 1000L);
        milestone.setActive(active);

        MilestoneSchedule milestoneSchedule = new MilestoneSchedule();
        milestoneSchedule.setProposalNumber(proposalNumber);
        List<Milestone> milestones = new ArrayList<Milestone>();
        milestones.add(milestone);
        milestoneSchedule.setMilestones(milestones);
        businessObjectService.save(milestoneSchedule);
        businessObjectService.save(milestone);
    }

    protected void setupPredeterminedBillingSchedule(Long proposalNumber, boolean active) {
        Bill bill = new Bill();
        bill.setProposalNumber(proposalNumber);
        bill.setBillNumber(proposalNumber);
        bill.setBillIdentifier(proposalNumber + 1000L);
        bill.setActive(active);

        PredeterminedBillingSchedule predeterminedBillingSchedule = new PredeterminedBillingSchedule();
        predeterminedBillingSchedule.setProposalNumber(proposalNumber);
        List<Bill> bills = new ArrayList<Bill>();
        bills.add(bill);
        predeterminedBillingSchedule.setBills(bills);
        businessObjectService.save(predeterminedBillingSchedule);
        businessObjectService.save(bill);
    }

}
