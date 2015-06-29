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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.kuali.kfs.sys.batch.service.SchedulerService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;
import org.quartz.JobDetail;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BatchJobStatus extends TransientBusinessObjectBase {

    // private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchJobStatus.class);

    private JobDescriptor jobDescriptor;

    private JobDetail jobDetail;

    @JsonIgnore
    private static SchedulerService schedulerService;

    private SchedulerService getSchedulerService() {
        if (schedulerService == null) {
            schedulerService = SpringContext.getBean(SchedulerService.class);
        }
        return schedulerService;
    }

    // for DD purposes only
    public BatchJobStatus() {
    }

    public BatchJobStatus(JobDescriptor jobDescriptor, JobDetail jobDetail) {
        this.jobDescriptor = jobDescriptor;
        this.jobDetail = jobDetail;
    }

    public String getName() {
        return jobDetail.getName();
    }

    public String getGroup() {
        return jobDetail.getGroup();
    }

    public String getFullName() {
        return jobDetail.getGroup() + "." + jobDetail.getName();
    }

    public String getNamespaceCode() {
        if(jobDescriptor == null) return null;
        return jobDescriptor.getNamespaceCode();
    }

    public Map<String, String> getDependencies() {
        if(jobDescriptor == null) return null;
        return jobDescriptor.getDependencies();
    }

    @JsonIgnore
    public List<Step> getSteps() {
        if(jobDescriptor == null) return null;
        return jobDescriptor.getSteps();
    }

    public String getStatus() {
        if (isRunning()) {
            return SchedulerService.RUNNING_JOB_STATUS_CODE;
        }
        String tempStatus = schedulerService.getStatus(jobDetail);
        if (tempStatus == null) {
            if (getNextRunDate() != null) {
                return SchedulerService.SCHEDULED_JOB_STATUS_CODE;
            }
            else if (getGroup().equals(SchedulerService.SCHEDULED_GROUP)) {
                return SchedulerService.PENDING_JOB_STATUS_CODE;
            }
        }
        return tempStatus;
    }

    public String getDependencyList() {
        StringBuffer sb = new StringBuffer(200);
        for (Map.Entry<String, String> entry : getDependencies().entrySet()) {
            sb.append(entry.getKey() + " (" + entry.getValue() + ") \n");
        }
        return sb.toString();
    }

    public String getStepList() {
        StringBuffer sb = new StringBuffer(200);
        for (Step step : getSteps()) {
            sb.append(step.getName() + " \n");
        }
        return sb.toString();
    }

    public int getNumSteps() {
        return getSteps().size();
    }

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("name", getName());
        m.put("group", getGroup());
        m.put("status", getStatus());
        for (Object key : jobDetail.getJobDataMap().keySet()) {
            m.put("jobDataMap." + key, jobDetail.getJobDataMap().get(key));
        }

        return m;
    }

    public boolean isScheduled() {
        // is this instance in the scheuled group?
        if (getGroup().equals(SchedulerService.SCHEDULED_GROUP)) {
            return true;
        }
        // does this job exist in the scheduled group?
        if (getSchedulerService().getJob(SchedulerService.SCHEDULED_GROUP, getName()) != null) {
            return true;
        }
        return false;
    }

    public boolean isRunning() {
        return getSchedulerService().isJobRunning(getName());
    }

    public void runJob(String requestorEmailAddress) {
        getSchedulerService().runJob(getName(), requestorEmailAddress);
    }

    public void runJob(int startStep, int endStep, Date startTime, String requestorEmailAddress) {
        getSchedulerService().runJob(getName(), startStep, endStep, startTime, requestorEmailAddress);
    }

    public void interrupt() {
        getSchedulerService().interruptJob(getName());
    }

    public void schedule() {
        // if not already in scheduled group
        if (!isScheduled()) {
            // make a copy and add to the scheduled group
            getSchedulerService().addScheduled(jobDetail);
        }
    }

    public void unschedule() {
        // if in scheduled group and scheduled group, remove it
        List<BatchJobStatus> jobs = getSchedulerService().getJobs(SchedulerService.UNSCHEDULED_GROUP);
        boolean inUnscheduledGroup = false;
        for (BatchJobStatus detail : jobs) {
            if (detail.getName().equals(getName())) {
                inUnscheduledGroup = true;
            }
        }

        // if only in scheduled group, move it
        if (!inUnscheduledGroup) {
            getSchedulerService().addUnscheduled(jobDetail);
        }
        getSchedulerService().removeScheduled(getName());
    }

    public Date getNextRunDate() {
        return getSchedulerService().getNextStartTime(this);
    }
}
