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

import java.util.Date;

import org.kuali.core.service.DateTimeService;
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
     * @see org.kuali.kfs.batch.TriggerDescriptor#completeTriggerDescription(org.quartz.Trigger)
     */
    protected void completeTriggerDescription(Trigger trigger) {
        if (startTime == null) {
            startTime = trigger.getStartTime();
        }
        trigger.setStartTime(new Date(startTime.getTime() + startDelay));
        ((SimpleTrigger) trigger).setRepeatCount(repeatCount);
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
     * @param startDelay The startDelay to set.
     */
    public void setStartDelay(long startDelay) {
        this.startDelay = startDelay;
    }
}
