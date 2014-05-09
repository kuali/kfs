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
package org.kuali.kfs.module.ar.batch;

import org.kuali.kfs.module.ar.batch.service.UpcomingMilestoneNotificationService;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * Batch step for sending ACH Advice notifications to payees receiving an ACH payment
 */
public class UpcomingMilestoneNotificationStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UpcomingMilestoneNotificationStep.class);
    protected UpcomingMilestoneNotificationService upcomingMilestoneNotificationService;

    /**
     *
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    @Override
    public boolean execute(String jobName, java.util.Date jobRunDate) throws InterruptedException {
        LOG.debug("UpcomingMilestoneNotificationStep: execute() started");
        getUpcomingMilestoneNotificationService().sendNotificationsForMilestones();

        return true;
    }

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
}
