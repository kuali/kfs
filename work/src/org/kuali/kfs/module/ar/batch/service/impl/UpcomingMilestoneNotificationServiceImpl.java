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

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.batch.UpcomingMilestoneNotificationStep;
import org.kuali.kfs.module.ar.batch.service.UpcomingMilestoneNotificationService;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.service.AREmailService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @see org.kuali.kfs.pdp.batch.service.AchAdviceNotificationService
 */
@Transactional
public class UpcomingMilestoneNotificationServiceImpl implements UpcomingMilestoneNotificationService {
    protected AREmailService arEmailService;
    protected DateTimeService dateTimeService;
    protected BusinessObjectService businessObjectService;
    protected ParameterService parameterService;

    /**
     * @see org.kuali.kfs.module.ar.batch.service.UpcomingMilestoneNotificationService#sendNotificationsForMilestones()
     */
    @Override
    public void sendNotificationsForMilestones() {
        // Get the limit value to check for upcoming milestones
        double limitDays = new Double(parameterService.getParameterValueAsString(UpcomingMilestoneNotificationStep.class, ArConstants.CHECK_LIMIT_DAYS));

        // Get todays date for comparison.
        Timestamp ts = new Timestamp(new java.util.Date().getTime());
        Date today = new Date(ts.getTime());

        // To retrieve all milestones.
        List<Milestone> milestones = (List<Milestone>) businessObjectService.findAll(Milestone.class);
        List<Milestone> milestonesToNotify = new ArrayList<Milestone>();
        if (CollectionUtils.isNotEmpty(milestones)) {
            for (Milestone mil : milestones) {
                if (mil.isActive() && ObjectUtils.isNotNull(mil.getMilestoneExpectedCompletionDate())) {
                    Date milestoneDate = mil.getMilestoneExpectedCompletionDate();
                    double days = (today.getTime() - milestoneDate.getTime()) / (double)KFSConstants.MILLSECONDS_PER_DAY;
                    if (days <= limitDays && !mil.isBilledIndicator() && ObjectUtils.isNull(mil.getMilestoneActualCompletionDate())) {
                        milestonesToNotify.add(mil);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(milestonesToNotify)) {
                // get the award from the milestones
                sendAdviceNotifications(milestonesToNotify, milestonesToNotify.get(0).getAward());
            }
        }
    }

    /**
     * @see org.kuali.kfs.pdp.batch.service.AchAdviceNotificationService#sendAdviceNotifications()
     */
    protected void sendAdviceNotifications(List<Milestone> milestones, ContractsAndGrantsBillingAward award) {
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

    @NonTransactional
    public ParameterService getParameterService() {
        return parameterService;
    }

    @NonTransactional
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
