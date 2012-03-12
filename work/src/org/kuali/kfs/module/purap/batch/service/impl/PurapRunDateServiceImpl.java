/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.batch.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.batch.service.PurapRunDateService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants.PURCHASING_BATCH;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

public class PurapRunDateServiceImpl implements PurapRunDateService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapRunDateServiceImpl.class);

    private ParameterService parameterService;
    
    /**
     * Determines the date to assume when running the batch processes
     */
    public Date calculateRunDate(Date executionDate) {
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(executionDate);

        CutoffTime cutoffTime = parseCutoffTime(retrieveCutoffTimeValue());

        if (isCurrentDateAfterCutoff(currentCal, cutoffTime)) {
            // go back one day
            currentCal.add(Calendar.DAY_OF_MONTH, 1);
            adjustTimeOfDay(currentCal, true);
        }
        else {
            adjustTimeOfDay(currentCal, false);
        }
        
        return currentCal.getTime();
    }

    /**
     * Adjusts the time of day if necessary, possibly depending on whether the execution time was before or after cutoff time
     * 
     * @param calendar
     * @param appliedCutoff true if the execution time was before the cutoff
     */
    protected void adjustTimeOfDay(Calendar calendar, boolean appliedCutoff) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
    /**
     * Determines if the given calendar time is before the given cutoff time
     * 
     * @param currentCal the current time
     * @param cutoffTime the "start of the day" cut off time
     * @return true if the current time is before the cutoff, false otherwise
     */
    protected boolean isCurrentDateAfterCutoff(Calendar currentCal, CutoffTime cutoffTime) {
        if (cutoffTime != null) {
            // if cutoff date is not properly defined
            // 24 hour clock (i.e. hour is 0 - 23)

            // clone the calendar so we get the same month, day, year
            // then change the hour, minute, second fields
            // then see if the cutoff is before or after
            Calendar cutoffCal = (Calendar) currentCal.clone();
            cutoffCal.setLenient(false);
            cutoffCal.set(Calendar.HOUR_OF_DAY, cutoffTime.hour);
            cutoffCal.set(Calendar.MINUTE, cutoffTime.minute);
            cutoffCal.set(Calendar.SECOND, cutoffTime.second);
            cutoffCal.set(Calendar.MILLISECOND, 0);

            return currentCal.after(cutoffCal);
        }
        // if cutoff date is not properly defined, then it is considered to be before the cutoff, that is, no cutoff date will be applied
        return false;
    }

    /**
     * Holds the hour, minute, and second of a given cut off time
     */
    protected class CutoffTime {
        /**
         * 24 hour time, from 0-23, inclusive
         */
        protected int hour;

        /**
         * From 0-59, inclusive
         */
        protected int minute;

        /**
         * From 0-59, inclusive
         */
        protected int second;

        /**
         * Constructs a RunDateServiceImpl instance
         * @param hour the cutoff hour
         * @param minute the cutoff minute
         * @param second the cutoff second
         */
        protected CutoffTime(int hour, int minute, int second) {
            this.hour = hour;
            this.minute = minute;
            this.second = second;
        }
    }

    /**
     * Parses a String representation of the cutoff time
     * 
     * @param cutoffTime the cutoff time String to parse
     * @return a record holding the cutoff time
     */
    protected CutoffTime parseCutoffTime(String cutoffTime) {
        if (StringUtils.isBlank(cutoffTime)) {
            return null;
        }
        else {
            cutoffTime = cutoffTime.trim();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Cutoff time value found: " + cutoffTime);
            }
            StringTokenizer st = new StringTokenizer(cutoffTime, ":", false);

            try {
                String hourStr = st.nextToken();
                String minuteStr = st.nextToken();
                String secondStr = st.nextToken();

                int hourInt = Integer.parseInt(hourStr, 10);
                int minuteInt = Integer.parseInt(minuteStr, 10);
                int secondInt = Integer.parseInt(secondStr, 10);

                if (hourInt < 0 || hourInt > 23 || minuteInt < 0 || minuteInt > 59 || secondInt < 0 || secondInt > 59) {
                    throw new IllegalArgumentException("Cutoff time must be in the format \"HH:mm:ss\", where HH, mm, ss are defined in the java.text.SimpleDateFormat class.  In particular, 0 <= hour <= 23, 0 <= minute <= 59, and 0 <= second <= 59");
                }
                return new CutoffTime(hourInt, minuteInt, secondInt);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Cutoff time should either be null, or in the format \"HH:mm:ss\", where HH, mm, ss are defined in the java.text.SimpleDateFormat class.");
            }
        }
    }

    /**
     * Retrieves the cutoff time from a repository.
     * 
     * @return a time of day in the format "HH:mm:ss", where HH, mm, ss are defined in the java.text.SimpleDateFormat class. In
     *         particular, 0 <= hour <= 23, 0 <= minute <= 59, and 0 <= second <= 59
     */
    protected String retrieveCutoffTimeValue() {
        String value = parameterService.getParameterValueAsString(PURCHASING_BATCH.class, PurapParameterConstants.PRE_DISBURSEMENT_EXTRACT_CUTOFF_TIME);
        if (StringUtils.isBlank(value)) {
            LOG.info("Unable to retrieve parameter for PURAP process cutoff date.  Defaulting to no cutoff time (i.e. midnight)");
            value = null;
        }
        return value;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
