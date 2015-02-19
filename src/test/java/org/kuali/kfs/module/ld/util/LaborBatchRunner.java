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

