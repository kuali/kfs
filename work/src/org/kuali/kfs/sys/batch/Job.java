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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.service.SchedulerService;
import org.kuali.kfs.sys.context.ProxyUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.quartz.UnableToInterruptJobException;
import org.springframework.util.StopWatch;

public class Job implements StatefulJob, InterruptableJob {

    public static final String JOB_RUN_START_STEP = "JOB_RUN_START_STEP";
    public static final String JOB_RUN_END_STEP = "JOB_RUN_END_STEP";
    public static final String MASTER_JOB_NAME = "MASTER_JOB_NAME";
    public static final String STEP_RUN_PARM_NM = "RUN_IND";
    public static final String STEP_RUN_ON_DATE_PARM_NM = "RUN_DATE";
    public static final String STEP_USER_PARM_NM = "USER";
    public static final String RUN_DATE_CUTOFF_PARM_NM = "RUN_DATE_CUTOFF_TIME";
    private static final Logger LOG = Logger.getLogger(Job.class);
    private SchedulerService schedulerService;
    private ParameterService parameterService;
    private DateTimeService dateTimeService;
    private List<Step> steps;
    private Step currentStep;
    private Appender ndcAppender;
    private boolean notRunnable;
    private transient Thread workerThread;

    /**
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
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
        Date jobRunDate = dateTimeService.getCurrentDate();
        int currentStepNumber = 0;
        try {
            LOG.info("Executing job: " + jobExecutionContext.getJobDetail() + " on machine " + getMachineName() + " scheduler instance id " + jobExecutionContext.getScheduler().getSchedulerInstanceId() + "\n" + jobDataMapToString(jobExecutionContext.getJobDetail().getJobDataMap()));
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
                    if (!runStep(parameterService, jobExecutionContext.getJobDetail().getFullName(), currentStepNumber, step, jobRunDate)) {
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

    public static boolean runStep(ParameterService parameterService, String jobName, int currentStepNumber, Step step, Date jobRunDate) throws InterruptedException, WorkflowException {
        boolean continueJob = true;
        if (GlobalVariables.getUserSession() == null) {
            LOG.info(new StringBuffer("Started processing step: ").append(currentStepNumber).append("=").append(step.getName()).append(" for user <unknown>"));
        }
        else {
            LOG.info(new StringBuffer("Started processing step: ").append(currentStepNumber).append("=").append(step.getName()).append(" for user ").append(GlobalVariables.getUserSession().getPrincipalName()));
        }

        if (!skipStep(parameterService, step, jobRunDate)) {

            Step unProxiedStep = (Step) ProxyUtils.getTargetIfProxied(step);
            Class<?> stepClass = unProxiedStep.getClass();
            GlobalVariables.clear();

            String stepUserName = KFSConstants.SYSTEM_USER;
            if (parameterService.parameterExists(stepClass, STEP_USER_PARM_NM)) {
                stepUserName = parameterService.getParameterValueAsString(stepClass, STEP_USER_PARM_NM);
            }
            if (LOG.isInfoEnabled()) {
                LOG.info(new StringBuffer("Creating user session for step: ").append(step.getName()).append("=").append(stepUserName));
            }
            GlobalVariables.setUserSession(new UserSession(stepUserName));
            if (LOG.isInfoEnabled()) {
                LOG.info(new StringBuffer("Executing step: ").append(step.getName()).append("=").append(stepClass));
            }
            StopWatch stopWatch = new StopWatch();
            stopWatch.start(jobName);
            try {
                continueJob = step.execute(jobName, jobRunDate);
            }
            catch (InterruptedException e) {
                LOG.error("Exception occured executing step", e);
                throw e;
            }
            catch (RuntimeException e) {
                LOG.error("Exception occured executing step", e);
                throw e;
            }
            stopWatch.stop();
            LOG.info(new StringBuffer("Step ").append(step.getName()).append(" of ").append(jobName).append(" took ").append(stopWatch.getTotalTimeSeconds() / 60.0).append(" minutes to complete").toString());
            if (!continueJob) {
                LOG.info("Stopping job after successful step execution");
            }
        }
        LOG.info(new StringBuffer("Finished processing step ").append(currentStepNumber).append(": ").append(step.getName()));
        return continueJob;
    }


    /**
     * This method determines whether the Job should not run the Step based on the RUN_IND and RUN_DATE Parameters.
     * When RUN_IND exists and equals 'Y' it takes priority and does not consult RUN_DATE.
     * If RUN_DATE exists, but contains an empty value the step will not be skipped.
     */
    protected static boolean skipStep(ParameterService parameterService, Step step, Date jobRunDate) {
        Step unProxiedStep = (Step) ProxyUtils.getTargetIfProxied(step);
        Class<?> stepClass = unProxiedStep.getClass();

        //RUN_IND takes priority: when RUN_IND exists and RUN_IND=Y always run the Step
        //RUN_DATE: when RUN_DATE exists, but the value is empty run the Step

        final boolean runIndExists = parameterService.parameterExists(stepClass, STEP_RUN_PARM_NM);
        if (runIndExists) {
            final boolean runInd = parameterService.getParameterValueAsBoolean(stepClass, STEP_RUN_PARM_NM);
            if (!runInd) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Skipping step due to system parameter: " + STEP_RUN_PARM_NM +" for "+ stepClass.getName());
                }
                return true; // RUN_IND is false - let's skip
            }
        }

        final boolean runDateExists = parameterService.parameterExists(stepClass, STEP_RUN_ON_DATE_PARM_NM);
        if (runDateExists) {
            final boolean runDateIsEmpty = StringUtils.isEmpty(parameterService.getParameterValueAsString(stepClass, STEP_RUN_ON_DATE_PARM_NM));
            if (runDateIsEmpty) {
                return false; // run date param is empty, so run the step
            }
        
            final DateTimeService dTService = SpringContext.getBean(DateTimeService.class);
    
            final Collection<String> runDates = parameterService.getParameterValuesAsString(stepClass, STEP_RUN_ON_DATE_PARM_NM);
            boolean matchedRunDate = false;
            final String[] cutOffTime = parameterService.parameterExists(KfsParameterConstants.FINANCIAL_SYSTEM_BATCH.class, RUN_DATE_CUTOFF_PARM_NM) ?
                    StringUtils.split(parameterService.getParameterValueAsString(KfsParameterConstants.FINANCIAL_SYSTEM_BATCH.class, RUN_DATE_CUTOFF_PARM_NM), ':') :
                    new String[] { "00", "00", "00"}; // no cutoff time param?  Then default to midnight of tomorrow
            for (String runDate: runDates) {
                try {
                    if (withinCutoffWindowForDate(jobRunDate, dTService.convertToDate(runDate), dTService, cutOffTime)) {
                        matchedRunDate = true;
                    }
                }
                catch (ParseException pe) {
                    LOG.error("ParseException occured parsing " + runDate, pe);
                }
            }
            // did we fail to match a run date?  then skip this step
            if (!matchedRunDate) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Skipping step due to system parameters: " + STEP_RUN_PARM_NM + ", " + STEP_RUN_ON_DATE_PARM_NM + " and " + RUN_DATE_CUTOFF_PARM_NM + " for "+ stepClass.getName());
                }
                return true;
            }
        }

        //run step
        return false;
    }
    
    /**
     * Checks if the current jobRunDate is within the cutoff window for the given run date from the RUN_DATE parameter.
     * The window is defined as midnight of the date specified in the parameter to the RUN_DATE_CUTOFF_TIME of the next day.
     * 
     * @param jobRunDate the time the job is attempting to start
     * @param runDateToCheck the current member of the appropriate RUN_DATE to check
     * @param dateTimeService an instance of the DateTimeService
     * @return true if jobRunDate is within the current runDateToCheck window, false otherwise
     */
    protected static boolean withinCutoffWindowForDate(Date jobRunDate, Date runDateToCheck, DateTimeService dateTimeService, String[] cutOffWindow) {
        final Calendar jobRunCalendar = dateTimeService.getCalendar(jobRunDate);
        final Calendar beginWindow = getCutoffWindowBeginning(runDateToCheck, dateTimeService);
        final Calendar endWindow = getCutoffWindowEnding(runDateToCheck, dateTimeService, cutOffWindow);
        return jobRunCalendar.after(beginWindow) && jobRunCalendar.before(endWindow);
    }
    
    /**
     * Defines the beginning of the cut off window
     * 
     * @param runDateToCheck the run date which defines the cut off window
     * @param dateTimeService an implementation of the DateTimeService
     * @return the begin date Calendar of the cutoff window
     */
    protected static Calendar getCutoffWindowBeginning(Date runDateToCheck, DateTimeService dateTimeService) {
        Calendar beginWindow = dateTimeService.getCalendar(runDateToCheck);
        beginWindow.set(Calendar.HOUR_OF_DAY, 0);
        beginWindow.set(Calendar.MINUTE, 0);
        beginWindow.set(Calendar.SECOND, 0);
        beginWindow.set(Calendar.MILLISECOND, 0);
        return beginWindow;
    }
    
    /**
     * Defines the end of the cut off window
     * 
     * @param runDateToCheck the run date which defines the cut off window
     * @param dateTimeService an implementation of the DateTimeService
     * @param cutOffTime an Array in the form of [hour, minute, second] when the cutoff window ends
     * @return the end date Calendar of the cutoff window
     */
    protected static Calendar getCutoffWindowEnding(Date runDateToCheck, DateTimeService dateTimeService, String[] cutOffTime) {
        Calendar endWindow = dateTimeService.getCalendar(runDateToCheck);
        endWindow.add(Calendar.DAY_OF_YEAR, 1);
        endWindow.set(Calendar.HOUR_OF_DAY, Integer.parseInt(cutOffTime[0]));
        endWindow.set(Calendar.MINUTE, Integer.parseInt(cutOffTime[1]));
        endWindow.set(Calendar.SECOND, Integer.parseInt(cutOffTime[2]));
        return endWindow;
    }

    /* This code is likely no longer reference, but was not removed, due to the fact that institutions may be calling */
    /**
     * @deprecated "Implementing institutions likely want to call Job#withinCutoffWindowForDate"
     */
    public static boolean isPastCutoffWindow(Date date, Collection<String> runDates) {
        DateTimeService dTService = SpringContext.getBean(DateTimeService.class);
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        Calendar jobRunDate = dTService.getCalendar(date);
        if (parameterService.parameterExists(KfsParameterConstants.FINANCIAL_SYSTEM_BATCH.class, RUN_DATE_CUTOFF_PARM_NM)) {
            String[] cutOffTime = StringUtils.split(parameterService.getParameterValueAsString(KfsParameterConstants.FINANCIAL_SYSTEM_BATCH.class, RUN_DATE_CUTOFF_PARM_NM), ':');
            Calendar runDate = null;
            for (String runDateStr : runDates) {
                try {
                    runDate = dTService.getCalendar(dTService.convertToDate(runDateStr));
                    runDate.add(Calendar.DAY_OF_YEAR, 1);
                    runDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(cutOffTime[0]));
                    runDate.set(Calendar.MINUTE, Integer.parseInt(cutOffTime[1]));
                    runDate.set(Calendar.SECOND, Integer.parseInt(cutOffTime[2]));
                }
                catch (ParseException e) {
                    LOG.error("ParseException occured parsing " + runDateStr, e);
                }
                if (jobRunDate.before(runDate)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @throws UnableToInterruptJobException
     */
    @Override
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

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    protected String jobDataMapToString(JobDataMap jobDataMap) {
        StringBuilder buf = new StringBuilder();
        buf.append("{");
        Iterator keys = jobDataMap.keySet().iterator();
        boolean hasNext = keys.hasNext();
        while (hasNext) {
            String key = (String) keys.next();
            Object value = jobDataMap.get(key);
            buf.append(key).append("=");
            if (value == jobDataMap) {
                buf.append("(this map)");
            }
            else {
                buf.append(value);
            }
            hasNext = keys.hasNext();
            if (hasNext) {
                buf.append(", ");
            }
        }
        buf.append("}");
        return buf.toString();
    }

    protected String getMachineName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e) {
            return "Unknown";
        }
    }
}
