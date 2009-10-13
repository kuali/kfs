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
package org.kuali.kfs.pdp.batch;

import java.util.Date;

import org.kuali.kfs.pdp.batch.service.AchAdviceNotificationService;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * Batch step for sending ACH Advice notifications to payees receiving an ACH payment
 */
public class SendAchAdviceNotificationsStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SendAchAdviceNotificationsStep.class);

    private AchAdviceNotificationService achAdviceNotificationService;


    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        LOG.debug("execute() started");

        achAdviceNotificationService.sendAdviceNotifications();

        return true;
    }


    /**
     * Sets the achAdviceNotificationService attribute value.
     * 
     * @param achAdviceNotificationService The achAdviceNotificationService to set.
     */
    public void setAchAdviceNotificationService(AchAdviceNotificationService achAdviceNotificationService) {
        this.achAdviceNotificationService = achAdviceNotificationService;
    }

}
