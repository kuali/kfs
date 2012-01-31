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
package org.kuali.kfs.module.cg.businessobject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.TypedArrayList;


/**
 * Created a Milestone Schedule maintenance Document parameter
 */
public class MilestoneSchedule extends PersistableBusinessObjectBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MilestoneSchedule.class);

    private static final String MILESTONE_SCHEDULE_INQUIRY_TITLE_PROPERTY = "message.inquiry.milestone.schedule.title";
    private Long proposalNumber;

    private KualiDecimal totalAmountScheduled;
    private KualiDecimal totalAmountRemaining;
    private String milestoneScheduleInquiryTitle;

    private List<Milestone> milestones;
    private Award award;

    public MilestoneSchedule() {
        // Must use TypedArrayList because its get() method automatically grows the array for Struts.
        milestones = new TypedArrayList(Milestone.class);
    }


    /**
     * Constructs an Milestone Schedule with paramter Award
     * 
     * @param proposal
     */
    public MilestoneSchedule(Award award) {
        this();
    }


    /**
     * Gets the proposalNumber attribute.
     * 
     * @return Returns the proposalNumber.
     */
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
    public KualiDecimal getTotalAmountScheduled() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (Iterator i = getMilestones().iterator(); i.hasNext();) {
            Milestone m = (Milestone) i.next();
            if (null != m.getMilestoneAmount()) {
                total = total.add(m.getMilestoneAmount());
            }
        }
        return total;
    }

    /**
     * Sets the totalAmountScheduled attribute value.
     * 
     * @param totalAmountScheduled The totalAmountScheduled to set.
     */
    @Deprecated
    public void setTotalAmountScheduled(KualiDecimal totalAmountScheduled) {
        this.totalAmountScheduled = totalAmountScheduled;
    }

    /**
     * Gets the totalAmountRemaining attribute.
     * 
     * @return Returns the totalAmountRemaining.
     */
    public KualiDecimal getTotalAmountRemaining() {
        KualiDecimal total = KualiDecimal.ZERO;
        if (award != null) {
            total = award.getAwardTotalAmount().subtract(getTotalAmountScheduled());
        }
        return total;
    }

    /**
     * Sets the totalAmountRemaining attribute value.
     * 
     * @param totalAmountRemaining The totalAmountRemaining to set.
     */
    @Deprecated
    public void setTotalAmountRemaining(KualiDecimal totalAmountRemaining) {
        this.totalAmountRemaining = totalAmountRemaining;
    }

    /**
     * OJB calls this method as the first operation before this BO is inserted into the database. The field is read-only in the data
     * dictionary and so the value does not persist in the DB. So this method makes sure that the values are stored in the DB.
     * 
     * @param persistenceBroker from OJB
     * @throws PersistenceBrokerException Thrown by call to super.beforeInsert();
     * @see org.kuali.rice.kns.bo.PersistableBusinessObjectBase#beforeInsert(org.apache.ojb.broker.PersistenceBroker)
     */
    @Override
    public void beforeInsert(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        super.beforeInsert(persistenceBroker);
        totalAmountScheduled = getTotalAmountScheduled();
        totalAmountRemaining = getTotalAmountRemaining();
    }

    /**
     * OJB calls this method as the first operation before this BO is updated to the database. The field is read-only in the data
     * dictionary and so the value does not persist in the DB. So this method makes sure that the values are stored in the DB.
     * 
     * @param persistenceBroker from OJB
     * @throws PersistenceBrokerException Thrown by call to super.beforeUpdate();
     * @see org.kuali.rice.kns.bo.PersistableBusinessObjectBase#beforeUpdate(org.apache.ojb.broker.PersistenceBroker)
     */
    @Override
    public void beforeUpdate(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        super.beforeUpdate(persistenceBroker);
        totalAmountScheduled = getTotalAmountScheduled();
        totalAmountRemaining = getTotalAmountRemaining();
    }


    /**
     * Gets the milestoneScheduleInquiryTitle attribute.
     * 
     * @return Returns the milestoneScheduleInquiryTitle.
     */
    public String getMilestoneScheduleInquiryTitle() {
        return SpringContext.getBean(KualiConfigurationService.class).getPropertyString(MILESTONE_SCHEDULE_INQUIRY_TITLE_PROPERTY);

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
    public Award getAward() {
        return award;
    }


    /**
     * Sets the award attribute value.
     * 
     * @param award The award to set.
     */
    public void setAward(Award award) {
        this.award = award;
    }


    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put("proposalNumber", this.proposalNumber.toString());
        m.put("totalAmountScheduled", this.totalAmountScheduled.toString());
        m.put("totalAmountRemaining", this.totalAmountRemaining.toString());
        return m;
    }


}
