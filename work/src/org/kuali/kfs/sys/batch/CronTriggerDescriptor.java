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
package org.kuali.kfs.sys.batch;

import java.text.ParseException;

import org.quartz.CronTrigger;
import org.quartz.Trigger;

public class CronTriggerDescriptor extends TriggerDescriptor {
    private String cronExpression;

    /**
     * @see org.kuali.kfs.sys.batch.TriggerDescriptor#completeTriggerDescription(org.quartz.Trigger)
     */
    protected void completeTriggerDescription(Trigger trigger) {
        // prevent setting of the trigger information in test mode
        try {
            ((CronTrigger) trigger).setTimeZone(getDateTimeService().getCurrentCalendar().getTimeZone());
            if (!isTestMode()) {
                ((CronTrigger) trigger).setCronExpression(cronExpression);
            }
            else {
                ((CronTrigger) trigger).setCronExpression("0 59 23 31 12 ? 2099");
            }
        }
        catch (ParseException e) {
            throw new RuntimeException("Caught exception while trying to set the cronExpression attribute of a CronTrigger: " + getJobName(), e);
        }
    }

    /**
     * Sets the cronExpression attribute value.
     * 
     * @param cronExpression The cronExpression to set.
     */
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
}
