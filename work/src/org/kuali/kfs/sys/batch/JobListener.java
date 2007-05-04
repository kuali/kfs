/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.kuali.core.mail.InvalidAddressException;
import org.kuali.core.mail.MailMessage;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.MailService;
import org.kuali.core.util.log4j.NDCFilter;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.service.SchedulerService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class JobListener implements org.quartz.JobListener {
    private static final Logger LOG = Logger.getLogger(JobListener.class);
    private static final String NAME = "jobListener";
    public static final String REQUESTOR_EMAIL_ADDRESS_KEY = "requestorEmailAdress";
    private SchedulerService schedulerService;
    private KualiConfigurationService configurationService;
    private MailService mailService;
    private DateTimeService dateTimeService;

    /**
     * @see org.quartz.JobListener#jobWasExecuted(org.quartz.JobExecutionContext, org.quartz.JobExecutionException)
     */
    public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException jobExecutionException) {
        if (!((Job) jobExecutionContext.getJobInstance()).isNotRunnable()) {
            String jobStatus = null;
            String customMessage = null;
            if (jobExecutionException != null) {
                jobStatus = SchedulerService.FAILED_JOB_STATUS_CODE;
                schedulerService.updateStatus(jobExecutionContext.getJobDetail(), jobStatus);
            }
            
            notify(jobExecutionContext, jobExecutionContext.getJobDetail().getJobDataMap().getString(SchedulerService.JOB_STATUS_PARAMETER) );
        }
        completeLogging(jobExecutionContext);
    }

    /**
     * @see org.quartz.JobListener#jobToBeExecuted(org.quartz.JobExecutionContext)
     */
    public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {
        schedulerService.initializeJob(jobExecutionContext.getJobDetail().getName(), (Job) jobExecutionContext.getJobInstance());
        initializeLogging(jobExecutionContext);
        if (schedulerService.shouldNotRun(jobExecutionContext.getJobDetail())) {
            ((Job) jobExecutionContext.getJobInstance()).setNotRunnable(true);
        }
    }

    /**
     * @see org.quartz.JobListener#jobExecutionVetoed(org.quartz.JobExecutionContext)
     */
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {
        throw new UnsupportedOperationException("JobListener does not implement jobExecutionVetoed(JobExecutionContext jobExecutionContext)");
    }

    private void initializeLogging(JobExecutionContext jobExecutionContext) {
        try {
            Calendar startTimeCalendar = dateTimeService.getCurrentCalendar();
            StringBuffer nestedDiagnosticContext = new StringBuffer(jobExecutionContext.getJobDetail().getName()).append("-").append(new SimpleDateFormat("yyyyMMdd-HH-mm-ss-S").format(startTimeCalendar.getTime()));
            ((Job) jobExecutionContext.getJobInstance()).setNdcAppender(new FileAppender(Logger.getRootLogger().getAppender("LogFile").getLayout(), getLogFileName(nestedDiagnosticContext.toString())));
            ((Job) jobExecutionContext.getJobInstance()).getNdcAppender().addFilter(new NDCFilter(nestedDiagnosticContext.toString()));
            Logger.getRootLogger().addAppender(((Job) jobExecutionContext.getJobInstance()).getNdcAppender());
            NDC.push(nestedDiagnosticContext.toString());
        }
        catch (IOException e) {
            LOG.warn("Could not initialize special custom logging for job: " + jobExecutionContext.getJobDetail().getName(), e);
        }
    }

    private void completeLogging(JobExecutionContext jobExecutionContext) {
        ((Job) jobExecutionContext.getJobInstance()).getNdcAppender().close();
        Logger.getRootLogger().removeAppender(((Job) jobExecutionContext.getJobInstance()).getNdcAppender());
        NDC.pop();
    }

    private String getLogFileName(String nestedDiagnosticContext) {
        return new StringBuffer(configurationService.getPropertyString(KFSConstants.LOGS_DIRECTORY_KEY)).append(File.separator).append(nestedDiagnosticContext.toString()).append(".log").toString();
    }

    private String getLogFileUrl(String nestedDiagnosticContext) {
        return new StringBuffer(configurationService.getPropertyString(KFSConstants.HTDOCS_LOGS_URL_KEY)).append(nestedDiagnosticContext.toString()).append(".log").toString();
    }

    private void notify(JobExecutionContext jobExecutionContext, String jobStatus) {
        try {
            StringBuffer mailMessageSubject = new StringBuffer(configurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY)).append(": ").append(jobExecutionContext.getJobDetail().getGroup()).append(": ").append(jobExecutionContext.getJobDetail().getName());
            MailMessage mailMessage = new MailMessage();
            mailMessage.setFromAddress(mailService.getBatchMailingList());
            if (jobExecutionContext.getMergedJobDataMap().containsKey(REQUESTOR_EMAIL_ADDRESS_KEY) && !StringUtils.isBlank( jobExecutionContext.getMergedJobDataMap().getString(REQUESTOR_EMAIL_ADDRESS_KEY) ) ) {
                mailMessage.addToAddress(jobExecutionContext.getMergedJobDataMap().getString(REQUESTOR_EMAIL_ADDRESS_KEY));
            }
            if (SchedulerService.FAILED_JOB_STATUS_CODE.equals(jobStatus) || SchedulerService.CANCELLED_JOB_STATUS_CODE.equals(jobStatus)) {
                mailMessage.addToAddress(mailService.getBatchMailingList());
            }
            mailMessageSubject.append(": ").append(jobStatus);
            mailMessage.setMessage(new StringBuffer("See ").append(getLogFileName(NDC.peek())).append(" or ").append(getLogFileUrl(NDC.peek())).append(" for details").toString());
            if (mailMessage.getToAddresses().size() > 0) {
                mailMessage.setSubject(mailMessageSubject.toString());
                mailService.sendMessage(mailMessage);
            }
        }
        catch (InvalidAddressException iae) {
            LOG.error("Caught exception while trying to send job completion notification e-mail for " + jobExecutionContext.getJobDetail().getName(), iae);
        }
    }

    /**
     * @see org.quartz.JobListener#getName()
     */
    public String getName() {
        return NAME;
    }

    /**
     * Sets the schedulerService attribute value.
     * 
     * @param schedulerService The schedulerService to set.
     */
    public void setSchedulerService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
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
     * Sets the mailService attribute value.
     * 
     * @param mailService The mailService to set.
     */
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
