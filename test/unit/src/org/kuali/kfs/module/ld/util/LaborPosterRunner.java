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

import org.kuali.kfs.module.ld.batch.service.LaborPosterService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.datetime.DateTimeService;

@ConfigureContext(session = UserNameFixture.kfs)
public class LaborPosterRunner extends KualiTestBase {
    private LaborPosterService laborPosterService;

    protected void setUp() throws Exception {
        super.setUp();
        laborPosterService = SpringContext.getBean(LaborPosterService.class);
    }

    public void testPoster() throws Exception {
        System.out.println("Labor Poster started");
        long start = System.currentTimeMillis();
        System.out.println("Labor Poster is running ...");
        System.out.printf("Starting Time = %d (ms)\n", start);
        Date jobRunDate = SpringContext.getBean(DateTimeService.class).getCurrentDate();
        BatchSpringContext.getJobDescriptor("laborBatchJob").getSteps().get(2).execute("laborBatchJob", jobRunDate);
        long elapsedTime = System.currentTimeMillis() - start;
        System.out.printf("Execution Time = %d (ms)\n", elapsedTime);
        System.out.println("Labor Poster stopped");
    }
}

