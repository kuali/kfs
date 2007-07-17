/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.gl.service.impl;

import java.util.Calendar;
import java.sql.Date;
import java.util.StringTokenizer;

import org.kuali.core.bo.FinancialSystemParameter;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.service.RunDateService;
import org.springframework.util.StringUtils;

public class RunDateServiceImpl implements RunDateService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RunDateServiceImpl.class);
    
    private KualiConfigurationService kualiConfigurationService;
    
    public Date calculateRunDate(Date executionDate) {
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(executionDate);
        
        CutoffTime cutoffTime = parseCutoffTime(retrieveCutoffTimeValue());
        
        if (isCurrentDateBeforeCutoff(currentCal, cutoffTime)) {
            // time to set the date to the previous day's last minute/second
            currentCal.add(Calendar.DAY_OF_MONTH, -1);
            // per old COBOL code (see https://test.kuali.org/jira/browse/KULRNE-70),
            // the time is set to 23:59:59 (assuming 0 ms)
            currentCal.set(Calendar.HOUR_OF_DAY, 23);
            currentCal.set(Calendar.MINUTE, 59);
            currentCal.set(Calendar.SECOND, 59);
            currentCal.set(Calendar.MILLISECOND, 0);
            return new Date(currentCal.getTimeInMillis());
        }
        return new Date(executionDate.getTime());
    }

    protected boolean isCurrentDateBeforeCutoff(Calendar currentCal, CutoffTime cutoffTime) {
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
            
            return currentCal.before(cutoffCal);
        }
        // if cutoff date is not properly defined, then it is considered to be after the cutoff
        return false;
    }
    
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
        
        protected CutoffTime(int hour, int minute, int second) {
            this.hour = hour;
            this.minute = minute;
            this.second = second;
        }
    }
    
    protected CutoffTime parseCutoffTime(String cutoffTime) {
        if (!StringUtils.hasText(cutoffTime)) {
            return new CutoffTime(0, 0, 0);
        }
        else {
            cutoffTime = cutoffTime.trim();
            LOG.debug("Cutoff time value found: " + cutoffTime);
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
     * @return a time of day in the format "HH:mm:ss", where HH, mm, ss are defined in the java.text.SimpleDateFormat class.  
     * In particular, 0 <= hour <= 23, 0 <= minute <= 59, and 0 <= second <= 59
     */
    protected String retrieveCutoffTimeValue() {
        if (kualiConfigurationService.hasApplicationParameter(GLConstants.GL_SCRUBBER_GROUP, GLConstants.GlScrubberGroupParameters.SCRUBBER_CUTOFF_TIME)) {
            try {
                return kualiConfigurationService.getApplicationParameterValue(GLConstants.GL_SCRUBBER_GROUP, GLConstants.GlScrubberGroupParameters.SCRUBBER_CUTOFF_TIME);
            }
            catch (RuntimeException e) {
                LOG.error("Exception occured trying to retrieve parameter for GL process cutoff date.  Defaulting to no cutoff time (i.e. midnight)", e);
            }
        }
        return null;
    }

    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}
