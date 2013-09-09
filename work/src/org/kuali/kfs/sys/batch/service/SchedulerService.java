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
package org.kuali.kfs.sys.batch.service;

import java.util.Date;
import java.util.List;

import org.kuali.kfs.sys.batch.BatchJobStatus;
import org.kuali.kfs.sys.batch.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;

public interface SchedulerService {
    public static final String SCHEDULE_JOB_NAME = "scheduleJob";

    public static final String PENDING_JOB_STATUS_CODE = "Pending";
    public static final String SCHEDULED_JOB_STATUS_CODE = "Scheduled";
    public static final String RUNNING_JOB_STATUS_CODE = "Running";
    public static final String SUCCEEDED_JOB_STATUS_CODE = "Succeeded";
    public static final String FAILED_JOB_STATUS_CODE = "Failed";
    public static final String CANCELLED_JOB_STATUS_CODE = "Cancelled";

    public static final String JOB_STATUS_PARAMETER = "status";

    public static final String SCHEDULED_GROUP = "scheduled";
    public static final String UNSCHEDULED_GROUP = "unscheduled";

    public void initialize();

    public void initializeJob(String jobName, Job job);

    /**
     * This method checks whether any jobs in the SCHEDULED job group are pending or currently scheduled.
     *
     * @return hasIncompleteJob
     */
    public boolean hasIncompleteJob();

    /**
     * This method should be used to determine when the daily batch schedule should terminate. It compares the start time of the
     * schedule job from quartz with a time specified by the scheduleStep_CUTOFF_TIME system parameter in the SYSTEM security group
     * on the day after the schedule job started running.
     *
     * @return pastScheduleCutoffTime
     */
    public boolean isPastScheduleCutoffTime();

    public void processWaitingJobs();

    public void logScheduleResults();

    public boolean shouldNotRun(JobDetail jobDetail);

    public String getStatus(JobDetail jobDetail);

    public void updateStatus(JobDetail jobDetail, String jobStatus);

    public void setScheduler(Scheduler scheduler);

    public List<BatchJobStatus> getJobs(String groupName);

    /**
     * Get all jobs known to the scheduler wrapped within a BusinessObject-derived class.
     *
     * @return
     */
    public List<BatchJobStatus> getJobs();

    /**
     * Gets a single job based on its name and group.
     *
     * @param groupName
     * @param jobName
     * @return
     */
    public BatchJobStatus getJob(String groupName, String jobName);

    /**
     * Immediately runs the specified job.
     *
     * @param jobName
     * @param startStep
     * @param stopStep
     * @param requestorEmailAddress
     */
    public void runJob(String jobName, int startStep, int stopStep, Date startTime, String requestorEmailAddress);


    public void runJob(String groupName, String jobName, int startStep, int stopStep, Date jobStartTime, String requestorEmailAddress);
    /**
     * Immediately runs the specified job.
     *
     * @param jobName
     * @param requestorEmailAddress
     */
    public void runJob(String jobName, String requestorEmailAddress);

    /**
     * Returns the list of job currently running within the scheduler.
     *
     * @return
     */
    public List<JobExecutionContext> getRunningJobs();

    /**
     * Removes a job from the scheduled group.
     *
     * @param jobName
     */
    public void removeScheduled(String jobName);

    /**
     * Adds the given job to the "scheduled" group.
     *
     * @param job
     */
    public void addScheduled(JobDetail job);

    /**
     * Adds the given job to the "unscheduled" group.
     *
     * @param job
     */
    public void addUnscheduled(JobDetail job);

    /**
     * Returns a list of all groups defined in the scheduler.
     *
     * @return
     */
    public List<String> getSchedulerGroups();

    /**
     * Returns a list of all possible statuses.
     *
     * @return
     */
    public List<String> getJobStatuses();

    /**
     * Requests that the given job be stopped as soon as possble. It is up to the job to watch for this request and terminiate. Long
     * running steps may not end unless they check for the interrupted status on their current Thread ot Step instance.
     *
     * @param jobName
     */
    public void interruptJob(String jobName);

    /**
     * Tests whether the referenced job name is running, regardless of group.
     *
     * @param jobName
     * @return
     */
    public boolean isJobRunning(String jobName);

    /**
     * Returns the next start time for the given job.
     *
     * @param job
     * @return
     */
    public Date getNextStartTime(BatchJobStatus job);

    /**
     * Returns the next start time for the given job.
     *
     * @param groupName
     * @param jobName
     * @return
     */
    public Date getNextStartTime(String groupName, String jobName);

    public void reinitializeScheduledJobs();

    /**
     * Checks if the next valid date for the given cronExpression string matches today's date.
     *
     * @param cronExpressionString cron expression used to obtain next valid date
     * @return boolean true if next valid date for cron expression matches today, false otherwise
     */
    public boolean cronConditionMet(String cronExpressionString);
}
