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
package org.kuali.kfs.batch;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.kuali.core.UserSession;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.SchedulerService;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.quartz.UnableToInterruptJobException;

import edu.iu.uis.eden.exception.WorkflowException;

public class Job implements StatefulJob, InterruptableJob {

    public static final String JOB_RUN_START_STEP = "JOB_RUN_START_STEP";
    public static final String JOB_RUN_END_STEP = "JOB_RUN_END_STEP";
    public static final String STEP_RUN_PARM_NM = "RUN_IND";
    public static final String STEP_USER_PARM_NM = "USER";
    private static final Logger LOG = Logger.getLogger(Job.class);
    private SchedulerService schedulerService;
    private ParameterService parameterService;
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
            if (LOG.isInfoEnabled()) {
                LOG.info("Skipping job because doNotRun is true: " + jobExecutionContext.getJobDetail().getName());
            }
            return;
        }
        int startStep = 0;
        try {
            startStep = Integer.parseInt(jobExecutionContext.getMergedJobDataMap().getString(JOB_RUN_START_STEP));
        }
        catch (NumberFormatException ex) {
            // not present, do nothing
        }
        int endStep = 0;
        try {
            endStep = Integer.parseInt(jobExecutionContext.getMergedJobDataMap().getString(JOB_RUN_END_STEP));
        }
        catch (NumberFormatException ex) {
            // not present, do nothing
        }
        int currentStepNumber = 0;
        try {
            LOG.info("Executing job: " + jobExecutionContext.getJobDetail() + "\n" + jobExecutionContext.getJobDetail().getJobDataMap());
            for (Step step : getSteps()) {
                currentStepNumber++;
                // prevent starting of the next step if the thread has an interrupted status
                if (workerThread.isInterrupted()) {
                    LOG.warn("Aborting Job execution due to manual interruption");
                    schedulerService.updateStatus(jobExecutionContext.getJobDetail(), SchedulerService.CANCELLED_JOB_STATUS_CODE);
                    return;
                }
                if (startStep > 0 && currentStepNumber < startStep) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Skipping step " + currentStepNumber + " - startStep=" + startStep);
                    }
                    continue; // skip to next step
                }
                else if (endStep > 0 && currentStepNumber > endStep) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Ending step loop - currentStepNumber=" + currentStepNumber + " - endStep = " + endStep);
                    }
                    break;
                }
                step.setInterrupted(false);
                try {
                    if (!runStep(parameterService, jobExecutionContext.getJobDetail().getFullName(), currentStepNumber, step)) {
                        break;
                    }
                }
                catch (InterruptedException ex) {
                    LOG.warn("Stopping after step interruption");
                    schedulerService.updateStatus(jobExecutionContext.getJobDetail(), SchedulerService.CANCELLED_JOB_STATUS_CODE);
                    return;
                }
                if (step.isInterrupted()) {
                    LOG.warn("attempt to interrupt step failed, step continued to completion");
                    LOG.warn("cancelling remainder of job due to step interruption");
                    schedulerService.updateStatus(jobExecutionContext.getJobDetail(), SchedulerService.CANCELLED_JOB_STATUS_CODE);
                    return;
                }
            }
        }
        catch (Exception e) {
            schedulerService.updateStatus(jobExecutionContext.getJobDetail(), SchedulerService.FAILED_JOB_STATUS_CODE);
            throw new JobExecutionException("Caught exception in " + jobExecutionContext.getJobDetail().getName(), e, false);
        }
        LOG.info("Finished executing job: " + jobExecutionContext.getJobDetail().getName());
        schedulerService.updateStatus(jobExecutionContext.getJobDetail(), SchedulerService.SUCCEEDED_JOB_STATUS_CODE);
    }

    public static boolean runStep(ParameterService parameterService, String jobName, int currentStepNumber, Step step) throws InterruptedException, UserNotFoundException, WorkflowException {
        boolean continueJob = true;
        LOG.info(new StringBuffer("Started processing step: ").append(currentStepNumber).append("=").append(step.getName()));
        if (parameterService.parameterExists(step.getClass(), STEP_RUN_PARM_NM) && !parameterService.getIndicatorParameter(step.getClass(), STEP_RUN_PARM_NM)) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Skipping step due to system parameter: " + STEP_RUN_PARM_NM);
            }
        }
        else {
            GlobalVariables.setErrorMap(new ErrorMap());
            GlobalVariables.setMessageList(new ArrayList());
            String stepUserName = KFSConstants.SYSTEM_USER;
            if (parameterService.parameterExists(step.getClass(), STEP_USER_PARM_NM)) {
                stepUserName = parameterService.getParameterValue(step.getClass(), STEP_USER_PARM_NM);
            }
            if (LOG.isInfoEnabled()) {
                LOG.info(new StringBuffer("Creating user session for step: ").append(step.getName()).append("=").append(stepUserName));
            }
            GlobalVariables.setUserSession(new UserSession(stepUserName));
            if (LOG.isInfoEnabled()) {
                LOG.info(new StringBuffer("Executing step: ").append(step.getName()).append("=").append(step.getClass()));
            }
            if (!step.execute(jobName)) {
                continueJob = false;
                LOG.info("Stopping job after successful step execution");
            }
        }
        LOG.info(new StringBuffer("Finished processing step ").append(currentStepNumber).append(": ").append(step.getName()));
        return continueJob;
    }

    /**
     * @throws UnableToInterruptJobException
     */
    public void interrupt() throws UnableToInterruptJobException {
        // ask the step to interrupt
        if (currentStep != null) {
            currentStep.interrupt();
        }
        // also attempt to interrupt the thread, to cause an InterruptedException if the step ever waits or sleeps
        workerThread.interrupt();
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public Appender getNdcAppender() {
        return ndcAppender;
    }

    public void setNdcAppender(Appender ndcAppender) {
        this.ndcAppender = ndcAppender;
    }

    public void setNotRunnable(boolean notRunnable) {
        this.notRunnable = notRunnable;
    }

    protected boolean isNotRunnable() {
        return notRunnable;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSchedulerService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }
}