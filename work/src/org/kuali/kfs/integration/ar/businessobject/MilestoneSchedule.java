/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.integration.ar.businessobject;

import java.util.List;

import org.kuali.kfs.integration.ar.AccountsReceivableMilestone;
import org.kuali.kfs.integration.ar.AccountsReceivableMilestoneSchedule;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.integration.ar.businessobject.Milestone;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class MilestoneSchedule implements AccountsReceivableMilestoneSchedule {

    private Long proposalNumber;

    private KualiDecimal totalAmountScheduled;
    private KualiDecimal totalAmountRemaining;
    private String milestoneScheduleInquiryTitle;

    private List<Milestone> milestones;
    private ContractsAndGrantsCGBAward award;

    @Override
    public Long getProposalNumber() {
        return proposalNumber;
    }

    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    @Override
    public KualiDecimal getTotalAmountScheduled() {
        return totalAmountScheduled;
    }

    public void setTotalAmountScheduled(KualiDecimal totalAmountScheduled) {
        this.totalAmountScheduled = totalAmountScheduled;
    }

    @Override
    public KualiDecimal getTotalAmountRemaining() {
        return totalAmountRemaining;
    }

    public void setTotalAmountRemaining(KualiDecimal totalAmountRemaining) {
        this.totalAmountRemaining = totalAmountRemaining;
    }

    @Override
    public String getMilestoneScheduleInquiryTitle() {
        return milestoneScheduleInquiryTitle;
    }

    public void setMilestoneScheduleInquiryTitle(String milestoneScheduleInquiryTitle) {
        this.milestoneScheduleInquiryTitle = milestoneScheduleInquiryTitle;
    }

    public List<Milestone> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<Milestone> milestones) {
        this.milestones = milestones;
    }

    @Override
    public ContractsAndGrantsCGBAward getAward() {
        return award;
    }

    public void setAward(ContractsAndGrantsCGBAward award) {
        this.award = award;
    }

    public void prepareForWorkflow() {

    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub

    }

}
