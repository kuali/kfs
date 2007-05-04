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
package org.kuali.kfs.batch;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.kuali.core.UserSession;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.service.SchedulerService;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.quartz.UnableToInterruptJobException;

public class Job implements StatefulJob, InterruptableJob {

    public static final String JOB_RUN_START_STEP = "JOB_RUN_START_STEP";
    public static final String JOB_RUN_END_STEP = "JOB_RUN_END_STEP";
    private static final Logger LOG = Logger.getLogger(Job.class);
    protected static final String STEP_RUN_INDICATOR_PARAMETER_SUFFIX = "_FLAG";
    protected static final String STEP_USER_PARAMETER_SUFFIX = "_USER";
    private KualiConfigurationService configurationService;
    private List<Step> steps;
    private Step currentStep;
    private Appender ndcAppender;
    private boolean notRunnable;
    private transient Thread workerThread;

    /**
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        workerThread = Thread.currentThread();
        if (isNotRunnable()) {
            LOG.info("Skipping job because doNotRun is true: " + jobExecutionContext.getJobDetail().getName());
            return;
        }
        int startStep = 0;
        try {
            startStep = Integer.parseInt( jobExecutionContext.getMergedJobDataMap().getString( JOB_RUN_START_STEP ) );
        } catch (NumberFormatException ex) {
            // not present, do nothing
        }
        int endStep = 0;
        try {
            endStep = Integer.parseInt( jobExecutionContext.getMergedJobDataMap().getString( JOB_RUN_END_STEP ) );
        } catch (NumberFormatException ex) {
            // not present, do nothing
        }
        int currentStepNumber = 0;
        try {
            LOG.info("Executing job: " + jobExecutionContext.getJobDetail() + "\n" + jobExecutionContext.getJobDetail().getJobDataMap() );
            for (Step step : getSteps()) {
                currentStepNumber++;
                // prevent starting of the next step if the thread has an interrupted status
                if ( workerThread.isInterrupted() ) {
                    LOG.warn( "Aborting Job execution due to manual interruption" );
                    jobExecutionContext.getJobDetail().getJobDataMap().put(SchedulerService.JOB_STATUS_PARAMETER, SchedulerService.CANCELLED_JOB_STATUS_CODE);                    
                    return;                    
                }
                if ( startStep > 0 && currentStepNumber < startStep ) {
                    LOG.info( "Skipping step " + currentStepNumber + " - startStep=" + startStep );
                    continue; // skip to next step
                } else if ( endStep > 0 && currentStepNumber > endStep ) {
                    LOG.info( "Ending step loop - currentStepNumber=" + currentStepNumber + " - endStep = " + endStep );
                    break;
                }
                currentStep = step;
                LOG.info(new StringBuffer("Started processing step: ").append(currentStepNumber).append("=").append(step.getName()));
                String stepRunIndicatorParameter = step.getName() + STEP_RUN_INDICATOR_PARAMETER_SUFFIX;
                if (getConfigurationService().hasApplicationParameter(KFSConstants.ParameterGroups.SYSTEM, stepRunIndicatorParameter) && !getConfigurationService().getApplicationParameterIndicator(KFSConstants.ParameterGroups.SYSTEM, stepRunIndicatorParameter)) {
                    LOG.info("Skipping step due to system parameter: " + stepRunIndicatorParameter);
                }
                else {
                    GlobalVariables.setErrorMap(new ErrorMap());
                    GlobalVariables.setMessageList(new ArrayList());
                    String stepUserParameter = step.getName() + STEP_USER_PARAMETER_SUFFIX;
                    if (getConfigurationService().hasApplicationParameter(KFSConstants.ParameterGroups.SYSTEM, stepUserParameter)) {
                        LOG.info(new StringBuffer("Creating user session for step: ").append(stepUserParameter).append("=").append(getConfigurationService().getApplicationParameterValue(KFSConstants.ParameterGroups.SYSTEM, stepUserParameter)));
                        GlobalVariables.setUserSession(new UserSession(getConfigurationService().getApplicationParameterValue(KFSConstants.ParameterGroups.SYSTEM, stepUserParameter)));
                    }
                    LOG.info(new StringBuffer("Executing step: ").append(step.getName()).append("=").append(step.getClass()));
                    step.setInterrupted( false );
                    try {
                        if (step.execute()) {
                            LOG.info("Continuing after successful step execution");
                        }
                        else {
                            LOG.info("Stopping after successful step execution");
                            break;
                        }
                    } catch ( InterruptedException ex ) {
                        LOG.warn("Stopping after step interruption");
                        jobExecutionContext.getJobDetail().getJobDataMap().put(SchedulerService.JOB_STATUS_PARAMETER, SchedulerService.CANCELLED_JOB_STATUS_CODE);                    
                        return;
                    }
                    if ( step.isInterrupted() ) {
                        LOG.warn("attempt to interrupt step failed, step continued to completion");
                        LOG.warn("cancelling remainder of job due to step interruption");
                        jobExecutionContext.getJobDetail().getJobDataMap().put(SchedulerService.JOB_STATUS_PARAMETER, SchedulerService.CANCELLED_JOB_STATUS_CODE);
                        //jobExecutionContext.getScheduler().addJob( jobExecutionContext.getJobDetail(), true);
                        return;
                    }
                }
                LOG.info(new StringBuffer("Finished processing step ").append(currentStepNumber).append(": ").append(step.getName()));
            }
        }
        catch (Exception e) {
            jobExecutionContext.getJobDetail().getJobDataMap().put(SchedulerService.JOB_STATUS_PARAMETER, SchedulerService.FAILED_JOB_STATUS_CODE);
            throw new JobExecutionException("Caught exception in " + jobExecutionContext.getJobDetail().getName(), e, false);
        }
        LOG.info("Finished executing job: " + jobExecutionContext.getJobDetail().getName());
        jobExecutionContext.getJobDetail().getJobDataMap().put(SchedulerService.JOB_STATUS_PARAMETER, SchedulerService.SUCCEEDED_JOB_STATUS_CODE);
    }

    /**
     * 
     * 
     * @throws UnableToInterruptJobException
     */
    public void interrupt() throws UnableToInterruptJobException {
        // ask the step to interrupt
        if ( currentStep != null ) {
            currentStep.interrupt();
        }
        // also attempt to interrupt the thread, to cause an InterruptedException if the step ever waits or sleeps
        workerThread.interrupt();        
    }
    
    /**
     * Sets the configurationService attribute value.
     * 
     * @param configurationService The configurationService to set.
     */
    public void setConfigurationService(KualiConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Sets the steps attribute value.
     * 
     * @param steps The steps to set.
     */
    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    /**
     * Gets the ndcAppender attribute.
     * 
     * @return Returns the ndcAppender.
     */
    public Appender getNdcAppender() {
        return ndcAppender;
    }

    /**
     * Sets the ndcAppender attribute value.
     * 
     * @param ndcAppender The ndcAppender to set.
     */
    public void setNdcAppender(Appender ndcAppender) {
        this.ndcAppender = ndcAppender;
    }

    /**
     * Sets the notRunnable attribute value.
     * 
     * @param notRunnable The notRunnable to set.
     */
    public void setNotRunnable(boolean notRunnable) {
        this.notRunnable = notRunnable;
    }

    /**
     * Gets the notRunnable attribute. 
     * @return Returns the notRunnable.
     */
    protected boolean isNotRunnable() {
        return notRunnable;
    }

    public KualiConfigurationService getConfigurationService() {
        return configurationService;
    }

    public List<Step> getSteps() {
        return steps;
    }
}