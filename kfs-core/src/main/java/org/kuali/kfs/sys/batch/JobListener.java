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
package org.kuali.kfs.sys.batch;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.log4j.PatternLayout;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.service.SchedulerService;
import org.kuali.kfs.sys.context.NDCFilter;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.krad.service.MailService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class JobListener implements org.quartz.JobListener {
    private static final Logger LOG = Logger.getLogger(JobListener.class);
    protected static final String NAME = "jobListener";
    public static final String REQUESTOR_EMAIL_ADDRESS_KEY = "requestorEmailAdress";
    protected SchedulerService schedulerService;
    protected ConfigurationService configurationService;
    protected MailService mailService;
    protected DateTimeService dateTimeService;

    /**
     * @see org.quartz.JobListener#jobWasExecuted(org.quartz.JobExecutionContext, org.quartz.JobExecutionException)
     */
    @Override
    public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException jobExecutionException) {
        if (jobExecutionContext.getJobInstance() instanceof Job) {
            try {
                if (!((Job) jobExecutionContext.getJobInstance()).isNotRunnable()) {
                    notify(jobExecutionContext, schedulerService.getStatus(jobExecutionContext.getJobDetail()));
                }
            } finally {
                completeLogging(jobExecutionContext);
            }
        }
    }

    /**
     * @see org.quartz.JobListener#jobToBeExecuted(org.quartz.JobExecutionContext)
     */
    @Override
    public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {
        if (jobExecutionContext.getJobInstance() instanceof Job) {
            schedulerService.initializeJob(jobExecutionContext.getJobDetail().getName(), (Job) jobExecutionContext.getJobInstance());
            initializeLogging(jobExecutionContext);
            // We only want to auto-cancel executions if they are part of a master scheduling job
            // Otherwise, this is a standalone job and should fire, regardless of prior status
            if ( jobExecutionContext.getMergedJobDataMap().containsKey(Job.MASTER_JOB_NAME) ) {
                if (schedulerService.shouldNotRun(jobExecutionContext.getJobDetail())) {
                    ((Job) jobExecutionContext.getJobInstance()).setNotRunnable(true);
                }
            } else {
                ((Job) jobExecutionContext.getJobInstance()).setNotRunnable(false);
            }
        }
    }

    /**
     * @see org.quartz.JobListener#jobExecutionVetoed(org.quartz.JobExecutionContext)
     */
    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {
        if (jobExecutionContext.getJobInstance() instanceof Job) {
            throw new UnsupportedOperationException("JobListener does not implement jobExecutionVetoed(JobExecutionContext jobExecutionContext)");
        }
    }

    protected void initializeLogging(JobExecutionContext jobExecutionContext) {
        try {
        	if(Logger.getRootLogger().getAppender("StdOut") == null) {
        		Appender appender = new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN));
            	appender.setName("StdOut");
            	Logger.getRootLogger().addAppender(appender);
        	}
            Calendar startTimeCalendar = dateTimeService.getCurrentCalendar();
            StringBuilder nestedDiagnosticContext = new StringBuilder(StringUtils.substringAfter(BatchSpringContext.getJobDescriptor(jobExecutionContext.getJobDetail().getName()).getNamespaceCode(), "-").toLowerCase()).append(File.separator).append(jobExecutionContext.getJobDetail().getName()).append("-").append(dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate()));
            ((Job) jobExecutionContext.getJobInstance()).setNdcAppender(new FileAppender(Logger.getRootLogger().getAppender("StdOut").getLayout(), getLogFileName(nestedDiagnosticContext.toString())));
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

    protected String getLogFileName(String nestedDiagnosticContext) {
        return new StringBuilder(configurationService.getPropertyValueAsString(KFSConstants.REPORTS_DIRECTORY_KEY)).append(File.separator).append(nestedDiagnosticContext.toString()).append(".log").toString();
    }

    protected void notify(JobExecutionContext jobExecutionContext, String jobStatus) {
        try {
            StringBuilder mailMessageSubject = new StringBuilder(jobExecutionContext.getJobDetail().getGroup()).append(": ").append(jobExecutionContext.getJobDetail().getName());
            MailMessage mailMessage = new MailMessage();
            mailMessage.setFromAddress(mailService.getBatchMailingList());
            if (jobExecutionContext.getMergedJobDataMap().containsKey(REQUESTOR_EMAIL_ADDRESS_KEY) && !StringUtils.isBlank(jobExecutionContext.getMergedJobDataMap().getString(REQUESTOR_EMAIL_ADDRESS_KEY))) {
                mailMessage.addToAddress(jobExecutionContext.getMergedJobDataMap().getString(REQUESTOR_EMAIL_ADDRESS_KEY));
            }
            if (SchedulerService.FAILED_JOB_STATUS_CODE.equals(jobStatus) || SchedulerService.CANCELLED_JOB_STATUS_CODE.equals(jobStatus)) {
                mailMessage.addToAddress(mailService.getBatchMailingList());
            }
            mailMessageSubject.append(": ").append(jobStatus);
            String messageText = MessageFormat.format(configurationService.getPropertyValueAsString(KFSKeyConstants.MESSAGE_BATCH_FILE_LOG_EMAIL_BODY), getLogFileName(NDC.peek()));
            mailMessage.setMessage(messageText);
            if (mailMessage.getToAddresses().size() > 0) {
                mailMessage.setSubject(mailMessageSubject.toString());
                mailService.sendMessage(mailMessage);
            }
        }
        catch (Exception iae) {
            LOG.error("Caught exception while trying to send job completion notification e-mail for " + jobExecutionContext.getJobDetail().getName(), iae);
        }
    }

    /**
     * @see org.quartz.JobListener#getName()
     */
    @Override
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
    public void setConfigurationService(ConfigurationService configurationService) {
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
