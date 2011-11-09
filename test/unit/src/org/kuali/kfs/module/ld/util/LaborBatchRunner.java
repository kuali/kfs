/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.util;

import java.util.Date;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.batch.JobDescriptor;
import org.kuali.kfs.sys.batch.Step;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.datetime.DateTimeService;

@ConfigureContext(session = UserNameFixture.kfs)
public class LaborBatchRunner extends KualiTestBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborBatchRunner.class);

    public void testRunBatch() {
        JobDescriptor laborBatchJob = BatchSpringContext.getJobDescriptor("laborBatchJob");
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        Date jobRunDate = dateTimeService.getCurrentDate();
        for (Step step : laborBatchJob.getSteps()) {
            runStep(step, jobRunDate);
        }
    }

    private void runStep(Step step, Date jobRunDate) {
        try {
            String stepName = step.getName();
            
            long start = System.currentTimeMillis();
            System.out.println(stepName + " started at " + start);
            
            boolean isSuccess = step.execute(getClass().getName(), jobRunDate);

            long elapsedTime = System.currentTimeMillis() - start;
            System.out.println("Execution Time = " + elapsedTime + "(" + isSuccess + ")");
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}

