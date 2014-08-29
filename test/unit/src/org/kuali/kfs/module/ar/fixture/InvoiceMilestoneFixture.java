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
package org.kuali.kfs.module.ar.fixture;

import java.sql.Date;

import org.kuali.kfs.module.ar.businessobject.InvoiceMilestone;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Fixture class for InvoiceMilestone
 */
public enum InvoiceMilestoneFixture {

    INV_MLSTN_1("5030", new Long(111), new Long(111), new Long(1), "Milestone 1", new KualiDecimal(1), false, null, null),
    INV_MLSTN_2("5030", new Long(111), new Long(111), new Long(1), "Milestone 1", new KualiDecimal(1), false, new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()));

    private String documentNumber;
    private Long proposalNumber;
    private Long milestoneNumber;
    private Long milestoneIdentifier;
    private String milestoneDescription;
    private KualiDecimal milestoneAmount;
    private boolean isBilledIndicator;
    private Date milestoneActualCompletionDate;
    private Date milestoneExpectedCompletionDate;

    private InvoiceMilestoneFixture(String documentNumber, Long proposalNumber, Long milestoneNumber, Long milestoneIdentifier, String milestoneDescription, KualiDecimal milestoneAmount, boolean isBilledIndicator, Date milestoneActualCompletionDate, Date milestoneExpectedCompletionDate) {
        this.documentNumber = documentNumber;
        this.proposalNumber = proposalNumber;
        this.milestoneNumber = milestoneNumber;
        this.milestoneDescription = milestoneDescription;
        this.milestoneAmount = milestoneAmount;
        this.milestoneIdentifier = milestoneIdentifier;
        this.isBilledIndicator = isBilledIndicator;
        this.milestoneActualCompletionDate = milestoneActualCompletionDate;
        this.milestoneExpectedCompletionDate = milestoneExpectedCompletionDate;
    }

    public InvoiceMilestone createInvoiceMilestone() {
        InvoiceMilestone milestone = new InvoiceMilestone();
        milestone.setDocumentNumber(this.documentNumber);
        milestone.setProposalNumber(this.proposalNumber);
        milestone.setMilestoneNumber(this.milestoneNumber);
        milestone.setMilestoneIdentifier(this.milestoneIdentifier);
        milestone.setMilestoneDescription(this.milestoneDescription);
        milestone.setMilestoneAmount(this.milestoneAmount);
        milestone.setBilled(this.isBilledIndicator);
        milestone.setMilestoneActualCompletionDate(this.milestoneActualCompletionDate);
        milestone.setMilestoneExpectedCompletionDate(this.milestoneExpectedCompletionDate);
        return milestone;
    }
}
