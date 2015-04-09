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

import java.util.Date;

import org.kuali.rice.core.api.datetime.DateTimeService;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

public class SimpleTriggerDescriptor extends TriggerDescriptor {
    private Date startTime;
    private long startDelay;
    private int repeatCount;

    public SimpleTriggerDescriptor() {
    }

    public SimpleTriggerDescriptor(String name, String group, String jobName, DateTimeService dateTimeService) {
        setBeanName(name);
        setGroup(group);
        setJobName(jobName);
        setDateTimeService(dateTimeService);
    }

    /**
     * @see org.kuali.kfs.sys.batch.TriggerDescriptor#completeTriggerDescription(org.quartz.Trigger)
     */
    protected void completeTriggerDescription(Trigger trigger) {
        if (startTime == null) {
            startTime = trigger.getStartTime();
        }
        // prevent setting of the trigger information in test mode
        if (!isTestMode()) {
            trigger.setStartTime(new Date(startTime.getTime() + startDelay));
            ((SimpleTrigger) trigger).setRepeatCount(repeatCount);
        }
        else {
            trigger.setStartTime(new Date(new Date().getTime() + 525600000L));
        }
    }

    /**
     * Sets the repeatCount attribute value.
     * 
     * @param repeatCount The repeatCount to set.
     */
    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    /**
     * Sets the startTime attribute value.
     * 
     * @param startTime The startTime to set.
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * Sets the startDelay attribute value.
     * 
     * @param startDelay The startDelay to set.
     */
    public void setStartDelay(long startDelay) {
        this.startDelay = startDelay;
    }
}
