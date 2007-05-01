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

import org.kuali.core.service.DateTimeService;
import org.quartz.CronTrigger;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.beans.factory.BeanNameAware;

public abstract class TriggerDescriptor implements BeanNameAware {
    private String name;
    private String group;
    private String jobName;
    private DateTimeService dateTimeService;

    protected abstract void completeTriggerDescription(Trigger trigger);

    public Trigger getTrigger() {
        Trigger trigger = null;
        if (getClass().equals(SimpleTriggerDescriptor.class)) {
            trigger = new SimpleTrigger(name, group);
        }
        else {
            trigger = new CronTrigger(name, group);
        }
        trigger.setJobName(jobName);
        trigger.setJobGroup(group);
        trigger.setStartTime(dateTimeService.getCurrentDate());
        completeTriggerDescription(trigger);
        return trigger;
    }

    /**
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    public void setBeanName(String name) {
        this.name = name;
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
     * Sets the jobName attribute value.
     * 
     * @param jobName The jobName to set.
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    protected String getJobName() {
        return jobName;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    protected DateTimeService getDateTimeService() {
        return dateTimeService;
    }
}
