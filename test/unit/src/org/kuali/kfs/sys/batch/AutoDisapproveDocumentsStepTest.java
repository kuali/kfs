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

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.ProxyUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.kns.service.DateTimeService;
import org.springframework.aop.support.AopUtils;
import org.kuali.kfs.sys.batch.AutoDisapproveDocumentsStep;

/**
 * Tests the AutoDisapproveDocumentsStep.
 */
@ConfigureContext
public class AutoDisapproveDocumentsStepTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AutoDisapproveDocumentsStepTest.class);
    
    private DateTimeService dateTimeService;

    /**
     * Constructs a AutoDisapproveDocumentsStepTest instance
     */
    public AutoDisapproveDocumentsStepTest() {
        super();
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        dateTimeService = SpringContext.getBean(DateTimeService.class);
    }

    /**
     * This method tests if the system parameters have been setup.  If they have not been setup, accessing them will cause a NPE
     */
    public final void testSystemParametersExist() {
        LOG.debug("testSystemParametersExist() started");  
        
        Step step = BatchSpringContext.getStep("autoDisapproveDocumentsStep");
        AutoDisapproveDocumentsStep autoDisapproveDocumentsStep = (AutoDisapproveDocumentsStep) ProxyUtils.getTargetIfProxied(step);

        if (autoDisapproveDocumentsStep.systemParametersForAutoDisapproveDocumentsJobExist()) {
            LOG.info("The System Parameters for this Job have been setup.");
        }
        else {
            LOG.warn("The test should not continue as the System Parameters for this Job have not been setup yet.");
        }
    }
    
    /**
     * This method will set the system parameter to today and executes the method canAutoDisapproveJobRun
     */
    public final void testCheckIfJobCanRun() {
        LOG.debug("testCheckIfJobCanRun() started");
        
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        
        Step step = BatchSpringContext.getStep("autoDisapproveDocumentsStep");
        AutoDisapproveDocumentsStep autoDisapproveDocumentsStep = (AutoDisapproveDocumentsStep) ProxyUtils.getTargetIfProxied(step);
        
        //check to make sure the system parameters exist before the test is run again setting the run date to today's date
        if (autoDisapproveDocumentsStep.systemParametersForAutoDisapproveDocumentsJobExist()) {
            // change the run date in the system parameter to today and test the canAutoDisapproveJobRun()....
            String today = dateTimeService.toDateString(dateTimeService.getCurrentDate());        
            
            TestUtils.setSystemParameter(AopUtils.getTargetClass(autoDisapproveDocumentsStep), KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_DOCUMENTS_STEP_RUN_DATE, today);
            boolean jobCanRun =  autoDisapproveDocumentsStep.canAutoDisapproveJobRun();
            assertTrue("autoDisapprove step did not exit with pass", jobCanRun);
        }
    }
    
    /**
     * Tests the job without changing the system parameter run date.  Then it changes the run date to today and run the job
     * which should execute the job successfully.
     */
    public void testAutoDisapproveDocuments() throws Exception {
        LOG.debug("testAutoDisapproveDocuments() started");
        
        Step step = BatchSpringContext.getStep("autoDisapproveDocumentsStep");
        AutoDisapproveDocumentsStep autoDisapproveDocumentsStep = (AutoDisapproveDocumentsStep) ProxyUtils.getTargetIfProxied(step);
           
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        
        boolean goodExit =  autoDisapproveDocumentsStep.execute(getClass().getName(), dateTimeService.getCurrentDate());
        assertTrue("autoDisapprove step did not exit with pass", goodExit);
        
        LOG.info("testAutoDisapproveDocuments() is executed again after it sets the run date to today's date");
        
        //check to make sure the system parameters exist before the test is run again setting the run date to today's date
        if (autoDisapproveDocumentsStep.systemParametersForAutoDisapproveDocumentsJobExist()) {
            // change the run date in the system parameter and test the step again....
            String today = dateTimeService.toDateString(dateTimeService.getCurrentDate());        
            
            TestUtils.setSystemParameter(AopUtils.getTargetClass(autoDisapproveDocumentsStep), KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_DOCUMENTS_STEP_RUN_DATE, today);
            goodExit =  autoDisapproveDocumentsStep.execute(getClass().getName(), dateTimeService.getCurrentDate());
            assertTrue("autoDisapprove step did not exit with pass", goodExit);
        }
    }
}
