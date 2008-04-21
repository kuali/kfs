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
package org.kuali.kfs.context;

import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.batch.BatchSpringContext;
import org.kuali.kfs.batch.Job;
import org.kuali.kfs.service.ParameterService;

public class BatchStepRunner {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchStepRunner.class);

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("ERROR: You must pass the name of the step to run on the command line.");
            System.exit(8);
        }
        try {
            Log4jConfigurer.configureLogging(false);
            SpringContext.initializeBatchApplicationContext();
            String[] stepNames;
            if (args[0].indexOf(",") > 0) {
                stepNames = StringUtils.split(args[0], ",");
            }
            else {
                stepNames = new String[] { args[0] };
            }
            ParameterService parameterService = SpringContext.getBean(ParameterService.class);
            DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
            
            String jobName = args.length >= 2 ? args[1] : KFSConstants.BATCH_STEP_RUNNER_JOB_NAME;
            Date jobRunDate = dateTimeService.getCurrentDate();
            LOG.info("Executing job: " + jobName + " steps: " + Arrays.toString(stepNames));
            for (int i = 0; i < stepNames.length; ++i) {
                if (!Job.runStep(parameterService, jobName, i, BatchSpringContext.getStep(stepNames[i]), jobRunDate)) {
                    System.exit(4);
                }
            }
            LOG.info("Finished executing job: " + jobName + " steps: " + Arrays.toString(stepNames));
            System.exit(0);
        }
        catch (Throwable t) {
            System.err.println("ERROR: Exception caught: ");
            t.printStackTrace(System.err);
            System.exit(8);
        }
    }
}