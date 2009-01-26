/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.batch;

import java.util.Date;

import org.kuali.kfs.gl.batch.ScrubberStep;
import org.kuali.kfs.module.ld.batch.service.LaborScrubberService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.springframework.util.StopWatch;

/**
 * Labor scrubber Batch Step.
 */
public class LaborScrubberStep extends AbstractStep {
    private LaborScrubberService laborScrubberService;
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborScrubberStep.class);

    /**
     * @param jobName
     * @param jobRunDate
     * @return boolean when success
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start(jobName);
        
        laborScrubberService.scrubEntries();

        stopWatch.stop();
        if (LOG.isDebugEnabled()) {
            LOG.debug("scrubber step of " + jobName + " took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");
        }

        return true;
    }

    /**
     * Sets the labor scrubber service
     * 
     * @param laborScrubberService
     */
    public void setLaborScrubberService(LaborScrubberService laborScrubberService) {
        this.laborScrubberService = laborScrubberService;
    }

}
