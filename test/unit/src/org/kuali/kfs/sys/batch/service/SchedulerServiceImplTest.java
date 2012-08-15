/*
 * Copyright 2007 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.kfs.sys.batch.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.BatchJobStatus;
import org.kuali.kfs.sys.batch.Job;
import org.kuali.kfs.sys.batch.JobDescriptor;
import org.kuali.kfs.sys.batch.JobListener;
import org.kuali.kfs.sys.batch.SimpleTriggerDescriptor;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

@ConfigureContext(session = UserNameFixture.kfs, initializeBatchSchedule = true)
public class SchedulerServiceImplTest extends KualiTestBase {

    // tests added to make sure that the scheduler was available during the tests
    public void testGetJobs_unscheduled() {
        SchedulerService s = SpringContext.getBean(SchedulerService.class);
        List<BatchJobStatus> jobs = s.getJobs("unscheduled");
        for (BatchJobStatus job : jobs) {
            System.out.println(job);
        }
    }

    public void testGetJobs_scheduled() {
        SchedulerService s = SpringContext.getBean(SchedulerService.class);
        List<BatchJobStatus> jobs = s.getJobs("scheduled");
        for (BatchJobStatus job : jobs) {
            System.out.println(job);
        }
    }

    /*
     * // this job seems to be missing from the system during the test runs public void testScheduledJobTrigger() throws Exception {
     * SchedulerService s = SpringContext.getBean(SchedulerService.class); BatchJobStatus job = s.getJob( "scheduled", "scheduleJob"
     * ); assertNotNull( "job must not be null", job ); System.out.println( "scheduleJob Next Run Time: " + job.getNextRunDate() );
     * Calendar cal = Calendar.getInstance(); cal.setTime( job.getNextRunDate() ); assertEquals( "year must be 2099", 2099, cal.get(
     * Calendar.YEAR ) ); }
     */


    /**
     * Test the running of a job. There is not much to test on the results, except that this does not cause an error.
     */
    public void testRunJob() throws Exception {
        SchedulerService s = SpringContext.getBean(SchedulerService.class);
        BatchJobStatus job = s.getJob(SchedulerService.UNSCHEDULED_GROUP, "scrubberJob");
        assertNotNull("job must not be null", job);
        System.out.println(job);
        job.runJob(null);
        System.out.println(s.getRunningJobs());
        System.out.println(s.getJob(SchedulerService.UNSCHEDULED_GROUP, "scrubberJob"));
        Thread.sleep(1000);
        System.out.println(s.getRunningJobs());
        System.out.println(s.getJob(SchedulerService.UNSCHEDULED_GROUP, "scrubberJob"));
        Thread.sleep(1000);
        System.out.println(s.getRunningJobs());
        System.out.println(s.getJob(SchedulerService.UNSCHEDULED_GROUP, "scrubberJob"));
        Thread.sleep(1000);
        System.out.println(s.getRunningJobs());
        System.out.println(s.getJob(SchedulerService.UNSCHEDULED_GROUP, "scrubberJob"));
        Thread.sleep(1000);
        System.out.println(s.getRunningJobs());
        System.out.println(s.getJob(SchedulerService.UNSCHEDULED_GROUP, "scrubberJob"));
        Thread.sleep(1000);
        System.out.println(s.getRunningJobs());
        System.out.println(s.getJob(SchedulerService.UNSCHEDULED_GROUP, "scrubberJob"));
    }

    /**
     * Test that the unschedule job function works and removes the job from the standard scheduled group. Assumes: scrubberJob
     * exists as a job in the scheduled group.
     */
    public void testUnscheduleJob() throws Exception {
        SchedulerService s = SpringContext.getBean(SchedulerService.class);
        BatchJobStatus job = s.getJob(SchedulerService.SCHEDULED_GROUP, "autoDisapproveJob");
        assertNotNull("job must not be null", job);

        assertTrue("must return isScheduled == true", job.isScheduled());

        s.removeScheduled(job.getName());
        job = s.getJob(SchedulerService.SCHEDULED_GROUP, "autoDisapproveJob");
        assertNull("new attempt to retrieve job must be null", job);

        job = s.getJob(SchedulerService.UNSCHEDULED_GROUP, "autoDisapproveJob");
        assertNotNull("job must not be null", job);
        assertFalse("must return isScheduled == false", job.isScheduled());
    }

    /**
     * Test that the schedule job function works and puts the job into the standard scheduled group. Also tests to make sure that
     * BatchJobStatus detects the scheduled status even if it is in the unscheduled group. Assumes: clearOldOriginEntriesJob exists
     * as a job in the unscheduled group.
     */
    public void testScheduleJob() throws Exception {
        SchedulerService s = SpringContext.getBean(SchedulerService.class);
        BatchJobStatus job = s.getJob(SchedulerService.UNSCHEDULED_GROUP, "manualPurgeJob");
        assertNotNull("job must not be null", job);

        assertFalse("must return isScheduled == false", job.isScheduled());

        job.schedule();

        job = s.getJob(SchedulerService.UNSCHEDULED_GROUP, "manualPurgeJob");
        assertNotNull("job (in unsched group) must not be null", job);
        assertTrue("must return isScheduled == true", job.isScheduled());

        job = s.getJob(SchedulerService.SCHEDULED_GROUP, "manualPurgeJob");
        assertNotNull("job (in sched group) must not be null", job);
        assertTrue("must return isScheduled == true", job.isScheduled());

    }

    protected void scheduleJob(String groupName, String jobName, int startStep, int endStep, Date startTime, String requestorEmailAddress, Map<String,String> additionalJobData ) {
        Scheduler scheduler = (Scheduler) SpringContext.getService("scheduler");
        try {
            JobDetail jobDetail = scheduler.getJobDetail(jobName, groupName);
            if ( jobDetail == null ) {
                fail( "Unable to retrieve JobDetail object for " + groupName + " : " + jobName );
            }
            if ( jobDetail.getJobDataMap() == null ) {
                jobDetail.setJobDataMap( new JobDataMap() );
            }
            jobDetail.getJobDataMap().put(SchedulerService.JOB_STATUS_PARAMETER, SchedulerService.SCHEDULED_JOB_STATUS_CODE);
            scheduler.addJob(jobDetail, true);

            SimpleTriggerDescriptor trigger = new SimpleTriggerDescriptor(jobName+startTime, groupName, jobName, SpringContext.getBean(DateTimeService.class));
            trigger.setStartTime(startTime);
            Trigger qTrigger = trigger.getTrigger();
            qTrigger.getJobDataMap().put(JobListener.REQUESTOR_EMAIL_ADDRESS_KEY, requestorEmailAddress);
            qTrigger.getJobDataMap().put(Job.JOB_RUN_START_STEP, String.valueOf(startStep));
            qTrigger.getJobDataMap().put(Job.JOB_RUN_END_STEP, String.valueOf(endStep));
            if ( additionalJobData != null ) {
                qTrigger.getJobDataMap().putAll(additionalJobData);
            }
            scheduler.scheduleJob(qTrigger);
        }
        catch (SchedulerException e) {
            throw new RuntimeException("Caught exception while scheduling job: " + jobName, e);
        }
    }


    /*
     * This test has problems with timing. It needs a job that it can rely on running for a specified period of time before being
     * included in the automated tests.
     */

    public void testJobInterrupt() throws Exception {
        // need to clear out the dependencies
        JobDescriptor jd = SpringContext.getBean(JobDescriptor.class, "scrubberJob");
        jd.getDependencies().clear();

        SchedulerService s = SpringContext.getBean(SchedulerService.class);
        // need to ensure that the job does not already have a status (from prior tests)
//        s.updateStatus(jd.getJobDetail(), null);

        System.err.println( "About to run scrubber job in testJobInterrupt" );
        scheduleJob(SchedulerService.SCHEDULED_GROUP, "scrubberJob", 0, 0, new Date(), null, null);
        BatchJobStatus job = s.getJob(SchedulerService.SCHEDULED_GROUP, "scrubberJob");
        // wait for job to enter running status
        int waitCount = 0;
        System.err.println( "Waiting for it to enter running status" );
        // provide an "out" in case things fail badly
        while (!job.isRunning() && waitCount < 500) {
            Thread.sleep(50);
            waitCount++;
        }
        // stop the job
        System.err.println( "Interrupting the job" );
        job.interrupt();
        waitCount = 0;
        System.err.println( "Waiting for it to exit running status" );
        while (job.isRunning() && waitCount < 500) {
            Thread.sleep(50);
            waitCount++;
        }
        Thread.sleep(2000);
        job = s.getJob(SchedulerService.SCHEDULED_GROUP, "scrubberJob");
        List<BatchJobStatus> jobs = s.getJobs();
        for (BatchJobStatus b : jobs) {
            if ( b.getName().equals( "scrubberJob" ) ) {
                System.out.println(b);
            }
        }
        assertEquals("job status not correct", SchedulerService.CANCELLED_JOB_STATUS_CODE, job.getStatus());
    }

    public void test2ndExecutionOfJobAfterJobInterrupt() throws Exception {
        JobDescriptor jd = SpringContext.getBean(JobDescriptor.class, "scrubberJob");
        SchedulerService s = SpringContext.getBean(SchedulerService.class);
        // need to ensure that the job does not already have a status
//        s.updateStatus(jd.getJobDetail(), null);
        // this will put scrubberJob into a Cancelled state

        // We need to give this next part 30 seconds - scheduling this for a future execution
        Date secondRunTime = new Date( System.currentTimeMillis() + 30000L );
        scheduleJob(SchedulerService.SCHEDULED_GROUP, "scrubberJob", 0, 0, secondRunTime, null, null);

        testJobInterrupt();

        // Ensure it is properly cancelled
        BatchJobStatus job = s.getJob(SchedulerService.SCHEDULED_GROUP, "scrubberJob");
        assertEquals("job status not correct", SchedulerService.CANCELLED_JOB_STATUS_CODE, job.getStatus());

        // now, wait until the next run is supposed to start
        if ( secondRunTime.getTime() - System.currentTimeMillis() > 0 ) {
            Thread.sleep( secondRunTime.getTime() - System.currentTimeMillis() );
        }
        // now, we try to run the job again, previously, this would result in the job being in a Cancelled state and refusing to run again
        System.err.println( "Attempting to run the job a 2nd time" );
        for (BatchJobStatus b : s.getJobs() ) {
            if ( b.getName().equals( "scrubberJob" ) ) {
                System.err.println(b);
            }
        }
        System.err.println( "Waiting for it to enter running status" );
        // provide an "out" in case things fail badly
        int waitCount = 0;
        while (!job.isRunning() && waitCount < 100) {
            Thread.sleep(50);
            waitCount++;
        }
        job = s.getJob(SchedulerService.SCHEDULED_GROUP, "scrubberJob");
        if ( StringUtils.equals( SchedulerService.CANCELLED_JOB_STATUS_CODE, job.getStatus() ) ) {
            fail( "Job should not have been in cancelled status");
        }
        assertEquals("job status not correct", SchedulerService.RUNNING_JOB_STATUS_CODE, job.getStatus());

    }

    /**
     * Verify that dropDependenciesNotScheduled drops unscheduled dependencies. It's worthwhile to note that spring-sys-test-xml was altered to include a fake
     * dependency of purgeReportsAndStagingJob on dailyEmailJob. This fake dependency was added to have a scheduled job dependend on an unscheduled one so that
     * we have something to test for.
     * @throws Exception
     */
    public void testDropDependenciesNotScheduled() throws Exception {
        SchedulerService schedulerService = SpringContext.getBean(SchedulerService.class);

        BatchJobStatus purgeReportsAndStagingJob = schedulerService.getJob(SchedulerService.SCHEDULED_GROUP, "purgeReportsAndStagingJob");
        for (Entry<String, String> dependency : purgeReportsAndStagingJob.getDependencies().entrySet()) {
            String dependencyJobName = dependency.getKey();
            assertFalse("Job was expected to be unscheduled so dropDependenciesNotScheduled should have removed this dependency.", "dailyEmailJob".equals(dependencyJobName));
        }
    }
}
