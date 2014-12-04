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
package org.kuali.kfs.module.ar.fixture;

import java.sql.Date;

import org.kuali.kfs.module.ar.businessobject.InvoiceMilestone;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Fixture class for InvoiceMilestone
 */
public enum InvoiceMilestoneFixture {

    INV_MLSTN_1("5030", new Long(1), new Long(1), "Milestone 1", new KualiDecimal(1), null),
    INV_MLSTN_2("5030", new Long(2), new Long(2), "Milestone 2", new KualiDecimal(1), new Date(System.currentTimeMillis()));

    private String documentNumber;
    private Long milestoneNumber;
    private Long milestoneIdentifier;
    private String milestoneDescription;
    private KualiDecimal milestoneAmount;
    private Date milestoneActualCompletionDate;

    private InvoiceMilestoneFixture(String documentNumber, Long milestoneNumber, Long milestoneIdentifier, String milestoneDescription, KualiDecimal milestoneAmount, Date milestoneActualCompletionDate) {
        this.documentNumber = documentNumber;
        this.milestoneNumber = milestoneNumber;
        this.milestoneDescription = milestoneDescription;
        this.milestoneAmount = milestoneAmount;
        this.milestoneIdentifier = milestoneIdentifier;
        this.milestoneActualCompletionDate = milestoneActualCompletionDate;
    }

    public InvoiceMilestone createInvoiceMilestone() {
        InvoiceMilestone milestone = new InvoiceMilestone();
        milestone.setDocumentNumber(this.documentNumber);
        milestone.setMilestoneNumber(this.milestoneNumber);
        milestone.setMilestoneIdentifier(this.milestoneIdentifier);
        milestone.setMilestoneDescription(this.milestoneDescription);
        milestone.setMilestoneAmount(this.milestoneAmount);
        milestone.setMilestoneActualCompletionDate(this.milestoneActualCompletionDate);
        return milestone;
    }
}
