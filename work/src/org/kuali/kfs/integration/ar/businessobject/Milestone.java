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
package org.kuali.kfs.integration.ar.businessobject;

import java.sql.Date;

import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.integration.ar.AccountsReceivableMilestone;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Integration class for Milestone
 */
public class Milestone implements AccountsReceivableMilestone {

    private Long proposalNumber;
    private Long milestoneNumber;
    private Long milestoneIdentifier;
    private String milestoneDescription;

    private KualiDecimal milestoneAmount;

    private Date milestoneActualCompletionDate;
    private Date milestoneExpectedCompletionDate;
    private boolean billedIndicator;
    private ContractsAndGrantsCGBAward award;

    /**
     * Constructs a Milestones.java.
     */
    public Milestone() {

    }

    /**
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber.
     */
    @Override
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute value.
     *
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * Gets the milestoneNumber attribute.
     *
     * @return Returns the milestoneNumber.
     */
    @Override
    public Long getMilestoneNumber() {
        return milestoneNumber;
    }

    /**
     * Sets the milestoneNumber attribute value.
     *
     * @param milestoneNumber The milestoneNumber to set.
     */
    public void setMilestoneNumber(Long milestoneNumber) {
        this.milestoneNumber = milestoneNumber;
    }

    /**
     * Gets the milestoneDescription attribute.
     *
     * @return Returns the milestoneDescription.
     */
    @Override
    public String getMilestoneDescription() {
        return milestoneDescription;
    }

    /**
     * Sets the milestoneDescription attribute value.
     *
     * @param milestoneDescription The milestoneDescription to set.
     */
    public void setMilestoneDescription(String milestoneDescription) {
        this.milestoneDescription = milestoneDescription;
    }

    /**
     * Gets the milestoneAmount attribute.
     *
     * @return Returns the milestoneAmount.
     */
    @Override
    public KualiDecimal getMilestoneAmount() {
        return milestoneAmount;
    }

    /**
     * Sets the milestoneAmount attribute value.
     *
     * @param milestoneAmount The milestoneAmount to set.
     */
    public void setMilestoneAmount(KualiDecimal milestoneAmount) {
        this.milestoneAmount = milestoneAmount;
    }

    public boolean isBilledIndicator() {
        return billedIndicator;
    }

    public void setBilledIndicator(boolean billedIndicator) {
        this.billedIndicator = billedIndicator;
    }

    /**
     * Gets the milestoneActualCompletionDate attribute.
     *
     * @return Returns the milestoneActualCompletionDate.
     */
    @Override
    public Date getMilestoneActualCompletionDate() {
        return milestoneActualCompletionDate;
    }

    /**
     * Sets the milestoneActualCompletionDate attribute value.
     *
     * @param milestoneActualCompletionDate The milestoneActualCompletionDate to set.
     */
    public void setMilestoneActualCompletionDate(Date milestoneActualCompletionDate) {
        this.milestoneActualCompletionDate = milestoneActualCompletionDate;
    }

    /**
     * Gets the milestoneExpectedCompletionDate attribute.
     *
     * @return Returns the milestoneExpectedCompletionDate.
     */
    @Override
    public Date getMilestoneExpectedCompletionDate() {
        return milestoneExpectedCompletionDate;
    }

    /**
     * Sets the milestoneExpectedCompletionDate attribute value.
     *
     * @param milestoneExpectedCompletionDate The milestoneExpectedCompletionDate to set.
     */
    public void setMilestoneExpectedCompletionDate(Date milestoneExpectedCompletionDate) {
        this.milestoneExpectedCompletionDate = milestoneExpectedCompletionDate;
    }

    /**
     * Gets the milestoneIdentifier attribute.
     *
     * @return Returns the milestoneIdentifier.
     */
    @Override
    public Long getMilestoneIdentifier() {
        return milestoneIdentifier;
    }

    /**
     * Sets the milestoneIdentifier attribute value.
     *
     * @param milestoneIdentifier The milestoneIdentifier to set.
     */
    public void setMilestoneIdentifier(Long milestoneIdentifier) {
        this.milestoneIdentifier = milestoneIdentifier;
    }


    /**
     * Gets the award attribute.
     *
     * @return Returns the award.
     */
    @Override
    public ContractsAndGrantsCGBAward getAward() {
        return award;
    }

    /**
     * Sets the award attribute value.
     *
     * @param award The award to set.
     */
    public void setAward(ContractsAndGrantsCGBAward award) {
        this.award = award;
    }

    public void prepareForWorkflow() {

    }

    @Override
    public void refresh() {

    }


}
