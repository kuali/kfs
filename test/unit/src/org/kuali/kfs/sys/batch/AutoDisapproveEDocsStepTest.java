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
package org.kuali.kfs.sys.batch;

import java.io.File;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.batch.Step;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.ProxyUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.rice.kns.service.ParameterService;

/**
 * Tests the CollecterStep. DEPENDENCIES: Collector card xml file transaction1.xml must be in /opt/kuali/dev/staging/collector/ this
 * file can be obtained by running the project's ant dist-local, or copying from
 * build/externalConfigDirectory/static/staging/collector/
 */
@ConfigureContext
public class AutoDisapproveEDocsStepTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AutoDisapproveEDocsStepTest.class);
    
    /**
     * Constructs a CollectorStepTest instance
     */
    public AutoDisapproveEDocsStepTest() {
        super();
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Tests the whole step completes successfully.
     */
    public void testAutoDisapproveEDocs() throws Exception {
        LOG.debug("testAutoDisapproveEDocs() started");
        Step step = BatchSpringContext.getStep("autoDisapproveEDocsStep");
        AutoDisapproveEDocsStep autoDisapproveEDocsStep = (AutoDisapproveEDocsStep) ProxyUtils.getTargetIfProxied(step);
           
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
          
        boolean goodExit =  autoDisapproveEDocsStep.execute(getClass().getName(), dateTimeService.getCurrentDate());
        assertTrue("autoDisapprove step did not exit with pass", goodExit);
    }
}
