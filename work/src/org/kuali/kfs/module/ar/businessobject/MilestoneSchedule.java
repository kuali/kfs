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
package org.kuali.kfs.module.ar.businessobject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.integration.ar.AccountsReceivableMilestoneSchedule;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleBillingService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;



/**
 * Created a Milestone Schedule maintenance Document parameter
 */
public class MilestoneSchedule extends PersistableBusinessObjectBase implements AccountsReceivableMilestoneSchedule {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MilestoneSchedule.class);

    private static final String MILESTONE_SCHEDULE_INQUIRY_TITLE_PROPERTY = "message.inquiry.milestone.schedule.title";
    private Long proposalNumber;

    private String milestoneScheduleInquiryTitle;

    private List<Milestone> milestones;
    private ContractsAndGrantsBillingAward award;

    public MilestoneSchedule() {
        // Must use ArrayList because its get() method automatically grows the array for Struts.
        milestones = new ArrayList<Milestone>();
    }


    /**
     * Constructs an Milestone Schedule with paramter Award
     *
     * @param proposal
     */
    public MilestoneSchedule(ContractsAndGrantsBillingAward award) {
        this();
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
     * Gets the totalAmountScheduled attribute.
     *
     * @return Returns the totalAmountScheduled.
     */
    @Override
    public KualiDecimal getTotalAmountScheduled() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (Milestone milestone: milestones) {
            if (ObjectUtils.isNotNull(milestone.getMilestoneAmount()) && milestone.isActive()) {
                total = total.add(milestone.getMilestoneAmount());
            }
        }
        return total;
    }

    /**
     * Gets the totalAmountRemaining attribute.
     *
     * @return Returns the totalAmountRemaining.
     */
    @Override
    public KualiDecimal getTotalAmountRemaining() {
        KualiDecimal total = KualiDecimal.ZERO;
        if (ObjectUtils.isNotNull(award) && ObjectUtils.isNotNull(award.getAwardTotalAmount())) {
            total = award.getAwardTotalAmount().subtract(getTotalAmountScheduled());
        }
        return total;
    }

    /**
     * Gets the milestoneScheduleInquiryTitle attribute.
     *
     * @return Returns the milestoneScheduleInquiryTitle.
     */
    @Override
    public String getMilestoneScheduleInquiryTitle() {
        return SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(MILESTONE_SCHEDULE_INQUIRY_TITLE_PROPERTY);
    }

    /**
     * Sets the milestoneScheduleInquiryTitle attribute value.
     *
     * @param milestoneScheduleInquiryTitle The milestoneScheduleInquiryTitle to set.
     */
    public void setMilestoneScheduleInquiryTitle(String milestoneScheduleInquiryTitle) {
        this.milestoneScheduleInquiryTitle = milestoneScheduleInquiryTitle;
    }


    /**
     * Gets the milestones attribute.
     *
     * @return Returns the milestones.
     */
    public List<Milestone> getMilestones() {
        return milestones;
    }


    /**
     * Sets the milestones attribute value.
     *
     * @param milestones The milestones to set.
     */
    public void setMilestones(List<Milestone> milestones) {
        this.milestones = milestones;
    }


    /**
     * Gets the award attribute.
     *
     * @return Returns the award.
     */
    @Override
    public ContractsAndGrantsBillingAward getAward() {
        award = SpringContext.getBean(ContractsAndGrantsModuleBillingService.class).updateAwardIfNecessary(proposalNumber, award);
        return award;
    }


    /**
     * Sets the award attribute value.
     *
     * @param award The award to set.
     */
    public void setAward(ContractsAndGrantsBillingAward award) {
        this.award = award;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.proposalNumber.toString());
        m.put("totalAmountScheduled", getTotalAmountScheduled().toString());
        m.put("totalAmountRemaining", getTotalAmountRemaining().toString());
        return m;
    }


}
