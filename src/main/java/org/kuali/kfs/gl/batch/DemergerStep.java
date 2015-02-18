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
package org.kuali.kfs.gl.batch;

import org.kuali.kfs.gl.service.ScrubberService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;
import org.springframework.util.StopWatch;

/**
 * A step to run the scrubber process.
 */
public class DemergerStep extends AbstractWrappedBatchStep {
    private ScrubberService scrubberService;
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DemergerStep.class);
    
    /**
     * Overridden to run the scrubber demerger process.
     * @see org.kuali.kfs.batch.Step#execute(java.lang.String)
     */
    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                final String jobName = "demerger";
                StopWatch stopWatch = new StopWatch();
                stopWatch.start(jobName);
                scrubberService.performDemerger();

                stopWatch.stop();
                LOG.info("scrubber step of " + jobName + " took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("scrubber step of " + jobName + " took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");
                }
                return true;
            }
        };
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
