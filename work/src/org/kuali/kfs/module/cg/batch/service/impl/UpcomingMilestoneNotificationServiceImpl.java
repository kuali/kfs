/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.cg.batch.service.impl;

import java.util.List;

import org.kuali.kfs.module.cg.batch.service.UpcomingMilestoneNotificationService;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.Milestone;
import org.kuali.kfs.module.cg.service.CGEmailService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.core.api.datetime.DateTimeService;

/**
 * @see org.kuali.kfs.pdp.batch.service.AchAdviceNotificationService
 */
public class UpcomingMilestoneNotificationServiceImpl implements UpcomingMilestoneNotificationService {

    private CGEmailService cgEmailService;
    private DateTimeService dateTimeService;
    private BusinessObjectService businessObjectService;

    /**
     * Set to NonTransactional
     * 
     * @see org.kuali.kfs.pdp.batch.service.AchAdviceNotificationService#sendAdviceNotifications()
     */
    @NonTransactional
    public void sendAdviceNotifications(List<Milestone> milestones, Award award) {

        cgEmailService.sendEmail(milestones, award);

    }

    /**
     * Sets the pdpEmailService attribute value.
     * 
     * @param pdpEmailService The pdpEmailService to set.
     */
    @NonTransactional
    public void setCgEmailService(CGEmailService cgEmailService) {
        this.cgEmailService = cgEmailService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    @NonTransactional
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    @NonTransactional
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


}
