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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.service.SchedulerService;
import org.quartz.JobDetail;
import org.springframework.beans.factory.BeanNameAware;

public class JobDescriptor implements BeanNameAware {
    private String name;
    private String group;
    private Map<String, String> dependencies;
    private List<Step> steps;
    private SchedulerService schedulerService;
    private boolean durable = true;

    public JobDescriptor() {
        dependencies = new HashMap();
        steps = new ArrayList();
    }
    
    public JobDescriptor( String name, String group, Step step, boolean durable ) {
        this();
        this.name = name;
        this.group = group;
        this.durable = durable;
        steps.add(step);
    }

    /**
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    public void setBeanName(String name) {
        this.name = name;
    }

    /**
     * Constructs a non-volatile, durable, non-recoverable JobDetail w/ org.kuali.kfs.batch.Job as the job class and the specified
     * name and group from this instance. Also adds status=Pending to the JobDataMap, if this is a scheduled job.
     * 
     * @return the org.quartz.JobDetail corresponding to this instance
     */
    public JobDetail getJobDetail() {
        return new JobDetail(name, group, Job.class, false, durable, false);
    }

    /**
     * Sets the group attribute value.
     * 
     * @param group The group to set.
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Sets the dependencies attribute value.
     * 
     * @param dependencies The dependencies to set.
     */
    public void setDependencies(Map<String, String> dependencies) {
        this.dependencies = dependencies;
    }

    /**
     * Gets the dependencies attribute.
     * 
     * @return Returns the dependencies.
     */
    public Map<String, String> getDependencies() {
        return dependencies;
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
     * Gets the steps attribute.
     * 
     * @return Returns the steps.
     */
    public List<Step> getSteps() {
        return steps;
    }

    /**
     * Sets the schedulerService attribute value.
     * 
     * @param schedulerService The schedulerService to set.
     */
    public void setSchedulerService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public SchedulerService getSchedulerService() {
        return schedulerService;
    }

    public boolean isDurable() {
        return durable;
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }
}
