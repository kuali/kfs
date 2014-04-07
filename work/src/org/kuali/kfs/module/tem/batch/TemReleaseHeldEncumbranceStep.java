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
