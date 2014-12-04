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
package org.kuali.kfs.sys.batch;

import java.util.Date;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.service.SchedulerService;

public class ScheduleStep extends AbstractStep {
    private static final Logger LOG = Logger.getLogger(ScheduleStep.class);
    private SchedulerService schedulerService;

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        boolean isPastScheduleCutoffTime = false;
        schedulerService.reinitializeScheduledJobs();
        while (schedulerService.hasIncompleteJob() && !isPastScheduleCutoffTime) {
            schedulerService.processWaitingJobs();
            isPastScheduleCutoffTime = schedulerService.isPastScheduleCutoffTime();
            try {
                Thread.sleep(Integer.parseInt(getParameterService().getParameterValueAsString(getClass(), KFSConstants.SystemGroupParameterNames.BATCH_SCHEDULE_STATUS_CHECK_INTERVAL)));
            }
            catch (InterruptedException e) {
                throw new RuntimeException("Schedule step encountered interrupt exception while trying to wait for the specified batch schedule status check interval", e);
            }
        }
        if (isPastScheduleCutoffTime) {
            LOG.info("Schedule exceeded cutoff time, so it was terminated before completion");
        }
        schedulerService.logScheduleResults();
        return !isPastScheduleCutoffTime;
    }

    /**
     * Sets the schedulerService attribute value.
     * 
     * @param schedulerService The schedulerService to set.
     */
    public void setSchedulerService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }
}
