/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
                    if (days <= limitDays && !mil.isBilled() && ObjectUtils.isNull(mil.getMilestoneActualCompletionDate())) {
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
        arEmailService.sendEmailNotificationsForMilestones(milestones, award);
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
