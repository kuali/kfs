/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.gl.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.JobDescriptor;
import org.kuali.kfs.sys.batch.Step;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;

/**
 * This runs both GL and CAB jobs in order
 */
public class RunGLAndCabJobs extends KualiTestBase {
    private Map<String, JobDescriptor> jobDescriptors;

    @ConfigureContext(session = UserNameFixture.khuntley, shouldCommitTransactions = true)
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        jobDescriptors = SpringContext.getBeansOfType(JobDescriptor.class);

    }

    public void testExecute() throws Exception {
        runSteps("enterpriseFeedJob");
        runSteps("collectorJob");
        runSteps("processPdpCancelsAndPaidJob");
        runSteps("nightlyOutJob");
        runSteps("laborNightlyOutJob");
        runSteps("laborScrubberJob");
        runSteps("laborPosterJob");
        runSteps("laborFeedJob");
        runSteps("clearLaborPendingEntriesJob");
        runSteps("pdpExtractGlTransactionsStepJob");
        runSteps("scrubberJob");
        runSteps("posterJob");
        runSteps("sufficientFundsJob");
        runSteps("posterSummaryReportJob");
        runSteps("clearPendingEntriesJob");
        runSteps("preAssetTaggingExtractJob");
        runSteps("cabExtractJob");
    }
    
    public void runGlBatch() throws Exception {
        //runSteps("enterpriseFeedJob");
        //runSteps("collectorJob");
        //runSteps("nightlyOutJob");
        runSteps("scrubberJob");
        runSteps("posterJob");
        runSteps("sufficientFundsJob");
        
        //already reported in Jira - KFSMI-2735
        //runSteps("posterSummaryReportJob");
        
        runSteps("clearPendingEntriesJob");
        
        //below 5 steps are year-end,  not working because of a problem at line 156 in EncumbranceClosingRuleHelper. 
        //runSteps("encumbranceForwardJob");
        //runSteps("balanceForwardJob");
        //runSteps("nominalActivityClosingJob");
        //runSteps("organizationReversionClosingJob");
        //runSteps("organizationReversionOpeningJob");
        
        
        runSteps("sufficientFundsSyncJob");
        runSteps("manualPurgeJob");
        
        
        //working on
        //runSteps("clearOldOriginEntriesJob");
        //runSteps("purgeCorrectionProcessFilesJob");
    }
    
    public void runLdBatch() throws Exception {
        runSteps("laborNightlyOutJob");
        runSteps("laborScrubberJob");
        runSteps("laborPosterJob");
        runSteps("laborFeedJob");
        runSteps("clearLaborPendingEntriesJob");
        runSteps("laborBalanceForwardJob");
        //working on
        //runSteps("laborPurgeJob");
    }
    

    private void runSteps(String jobName) throws InterruptedException {
        JobDescriptor job = jobDescriptors.get(jobName);
        System.out.println("Running " + job.getName());
        for (Step step : job.getSteps()) {
            System.out.println("Step - " + step.getName());
            step.execute(job.getName(), new Date());
        }
    }

    private void findJobOrder(JobDescriptor job, List<String> jobOrder) {
        Map<String, String> dependencies = job.getDependencies();
        if (dependencies != null && !dependencies.isEmpty()) {
            for (String dependentJob : dependencies.keySet()) {
                findJobOrder(jobDescriptors.get(dependentJob), jobOrder);
            }
        }
        if (!jobOrder.contains(job.getName())) {
            jobOrder.add(job.getName());
        }
        
    }

    public void testJobDependency() throws Exception {

        List<String> jobOrder = new ArrayList<String>();
        findJobOrder(jobDescriptors.get("clearPendingEntriesJob"), jobOrder);
        for (String jobName : jobOrder) {
            System.out.println("Job - " + jobName);
        }
    }
}
