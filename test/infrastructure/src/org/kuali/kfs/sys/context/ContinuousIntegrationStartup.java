/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.sys.context;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.batch.Job;
import org.kuali.kfs.sys.suite.TestSuiteBuilder;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * This class preps for the tests to run
 */
@TestSuiteBuilder.Exclude
@ConfigureContext(session = khuntley, shouldCommitTransactions = true)
public class ContinuousIntegrationStartup extends KualiTestBase {

    @Override
    protected void tearDown() throws Exception {
        super.setUp();
        Job.runStep(SpringContext.getBean(ParameterService.class), "genesisBatchJob", 1, BatchSpringContext.getStep("genesisBatchStep"), SpringContext.getBean(DateTimeService.class).getCurrentDate());
    }

    public void testNothing() throws Exception {
    }
}

