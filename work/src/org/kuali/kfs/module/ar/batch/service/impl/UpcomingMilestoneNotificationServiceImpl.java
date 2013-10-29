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
package org.kuali.kfs.module.ar.batch.service.impl;

import java.util.List;

import org.kuali.kfs.module.ar.batch.service.UpcomingMilestoneNotificationService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.service.AREmailService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.core.api.datetime.DateTimeService;

/**
 * @see org.kuali.kfs.pdp.batch.service.AchAdviceNotificationService
 */
public class UpcomingMilestoneNotificationServiceImpl implements UpcomingMilestoneNotificationService {

    private AREmailService arEmailService;
    private DateTimeService dateTimeService;
    private BusinessObjectService businessObjectService;

    /**
     * Set to NonTransactional
     *
     * @see org.kuali.kfs.pdp.batch.service.AchAdviceNotificationService#sendAdviceNotifications()
     */
    @Override
    @NonTransactional
    public void sendAdviceNotifications(List<Milestone> milestones, ContractsAndGrantsBillingAward award) {

        arEmailService.sendEmail(milestones, award);

    }

    /**
     * Sets the arEmailService attribute value.
     *
     * @param arEmailService The arEmailService to set.
     */
    @NonTransactional
    public void setArEmailService(AREmailService arEmailService) {
        this.arEmailService = arEmailService;
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
