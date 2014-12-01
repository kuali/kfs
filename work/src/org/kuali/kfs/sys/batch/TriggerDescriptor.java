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

import org.kuali.rice.core.api.datetime.DateTimeService;
import org.quartz.CronTrigger;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.beans.factory.BeanNameAware;

public abstract class TriggerDescriptor implements BeanNameAware {
    private String name;
    private String group;
    private String jobName;
    private DateTimeService dateTimeService;
    private boolean testMode = false;

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

    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }
}
