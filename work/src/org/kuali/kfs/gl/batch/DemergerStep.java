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
package org.kuali.kfs.gl.batch;

import java.util.Comparator;
import java.util.Date;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.BatchSortService;
import org.kuali.kfs.gl.exception.LoadException;
import org.kuali.kfs.gl.service.ScrubberService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;
import org.kuali.rice.kns.util.GlobalVariables;
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
