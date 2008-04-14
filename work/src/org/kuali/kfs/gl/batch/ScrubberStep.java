/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.gl.batch;

import java.util.Date;

import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.gl.service.ScrubberService;
import org.springframework.util.StopWatch;

/**
 * A step to run the scrubber process.
 */
public class ScrubberStep extends AbstractStep {
    private ScrubberService scrubberService;
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberStep.class);

    /**
     * Runs the scrubber process.
     * 
     * @param jobName the name of the job this step is being run as part of
     * @param jobRunDate the time/date the job was started
     * @return true if the job completed successfully, false if otherwise
     * @see org.kuali.kfs.batch.Step#execute(java.lang.String)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start(jobName);

        scrubberService.scrubEntries();


        stopWatch.stop();
        if (LOG.isDebugEnabled()) {
            LOG.debug("scrubber step of " + jobName + " took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");
        }
        return true;
    }

    /**
     * Sets the scrubberSerivce, allowing the injection of an implementation of that service
     * 
     * @param scrubberService the scrubberServiceService implementation to set
     * @see org.kuali.module.gl.service.ScrubberService
     */
    public void setScrubberService(ScrubberService ss) {
        scrubberService = ss;
    }
}
