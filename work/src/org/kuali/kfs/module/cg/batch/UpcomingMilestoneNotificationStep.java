/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.cg.batch;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.batch.service.UpcomingMilestoneNotificationService;
import org.kuali.kfs.module.cg.businessobject.Milestone;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Batch step for sending ACH Advice notifications to payees receiving an ACH payment
 */
public class UpcomingMilestoneNotificationStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UpcomingMilestoneNotificationStep.class);
    public final static double MILLISECONDS_IN_DAY = 1000 * 60 * 60 * 24;
    private UpcomingMilestoneNotificationService upcomingMilestoneNotificationService;
    protected ParameterService parameterService;


    /**
     * Gets the upcomingMilestoneNotificationService attribute.
     * 
     * @return Returns the upcomingMilestoneNotificationService.
     */
    public UpcomingMilestoneNotificationService getUpcomingMilestoneNotificationService() {
        return upcomingMilestoneNotificationService;
    }

    /**
     * Sets the upcomingMilestoneNotificationService attribute value.
     * 
     * @param upcomingMilestoneNotificationService The upcomingMilestoneNotificationService to set.
     */
    public void setUpcomingMilestoneNotificationService(UpcomingMilestoneNotificationService upcomingMilestoneNotificationService) {
        this.upcomingMilestoneNotificationService = upcomingMilestoneNotificationService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * 
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    public boolean execute(String jobName, java.util.Date jobRunDate) throws InterruptedException {
        LOG.debug("UpcomingMilestoneNotificationStep: execute() started");
        // Get the limit value to check for upcoming milestones
        double limitDays = new Double(parameterService.getParameterValueAsString(UpcomingMilestoneNotificationStep.class, CGConstants.AWARD_MILESTONE_CHECK_LIMIT_DAYS));

        // Get todays date for comparison.
        Timestamp ts = new Timestamp(new java.util.Date().getTime());
        Date today = new Date(ts.getTime());


        // To retrieve all milestones. Using key value service here so it retreives the externalizable business object.

        List<Milestone> milestones = (List<Milestone>) SpringContext.getBean(BusinessObjectService.class).findAll(Milestone.class);
        List<Milestone> milestonesToNotify = new ArrayList<Milestone>();
        if (CollectionUtils.isNotEmpty(milestones)) {
            for (Milestone mil : milestones) {
                if (ObjectUtils.isNotNull(mil.getMilestoneExpectedCompletionDate())) {
                    Date milestoneDate = mil.getMilestoneExpectedCompletionDate();
                    double days = (today.getTime() - milestoneDate.getTime()) / MILLISECONDS_IN_DAY;
                    if (days <= limitDays && mil.getIsItBilled().equals(KFSConstants.ParameterValues.STRING_NO) && ObjectUtils.isNull(mil.getMilestoneActualCompletionDate())) {
                        milestonesToNotify.add(mil);
                    }
                }

            }

            if (CollectionUtils.isNotEmpty(milestonesToNotify)) {
                // get the award from the milestones
                upcomingMilestoneNotificationService.sendAdviceNotifications(milestonesToNotify, milestonesToNotify.get(0).getAward());
            }


        }


        return true;
    }


}
