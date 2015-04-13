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
package org.kuali.kfs.module.tem.batch;

import java.util.Date;

import org.kuali.kfs.module.tem.service.TravelEncumbranceService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.springframework.util.StopWatch;

public class TemReleaseHeldEncumbranceStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TemReleaseHeldEncumbranceStep.class);
    protected TravelEncumbranceService travelEncumbranceService;

    /**
     * This step looks for any gl pending entries that are currently in the 'H' (Hold) status
     * and puts it in the 'A' (Approved) status then calls the nightly out service to copy the
     * new approved pending entries.
     *
     * @param jobName the name of the job that this step is being run as part of
     * @param jobRunDate the time/date the job is run
     * @return that the job completed successfully
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
	@Override
    public boolean execute(String jobName, Date jobRunDate) {
		StopWatch stopWatch = new StopWatch();
        stopWatch.start("TemReleaseHeldEncumbranceStep");

        getTravelEncumbranceService().releaseHeldEncumbrances();

        stopWatch.stop();
        LOG.info("TemReleaseHeldEncumbranceStep took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");

        return true;
    }

    public TravelEncumbranceService getTravelEncumbranceService() {
        return travelEncumbranceService;
    }

    public void setTravelEncumbranceService(TravelEncumbranceService travelEncumbranceService) {
        this.travelEncumbranceService = travelEncumbranceService;
    }
}
