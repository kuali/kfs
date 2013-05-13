/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.bo.KualiCodeBase;

/**
 * Business Object for Frequency Code table.
 */
public class FrequencyCode extends KualiCodeBase {

    private String frequencyType;
    private String frequencyWeekDay;
    private String frequencyMonth;
    private String dayInMonth;
    private String monthlyOccurence;

    /**
     * This method gets the day of the week for the frequency code.
     * 
     * @return day of week
     */
    public String getFrequencyWeekDay() {
        return frequencyWeekDay;
    }

    /**
     * This method sets the day of the week for the frequency code.
     * 
     * @param frequencyDay
     */
    public void setFrequencyWeekDay(String frequencyDay) {
        this.frequencyWeekDay = frequencyDay;
    }

    /**
     * This method gets the month for the frequency code.
     * 
     * @return month
     */
    public String getFrequencyMonth() {
        return frequencyMonth;
    }

    /**
     * This method sets the month for the frequency code
     * 
     * @param frequencyMonth
     */
    public void setFrequencyMonth(String frequencyMonth) {
        this.frequencyMonth = frequencyMonth;
    }

    /**
     * This method sets the monthly occurence: date or monthe end
     * 
     * @return monthlyOccurence
     */
    public String getMonthlyOccurence() {
        return monthlyOccurence;
    }

    /**
     * This method sets the monthly occurence.
     * 
     * @param monthlyOccurence
     */
    public void setMonthlyOccurence(String monthlyOccurence) {
        this.monthlyOccurence = monthlyOccurence;
    }

    /**
     * This method gets the frequency type: annual, etc.
     * 
     * @return
     */
    public String getFrequencyType() {
        return frequencyType;
    }

    /**
     * This method sets the frequency type
     * 
     * @param frequencyType
     */
    public void setFrequencyType(String frequencyType) {
        this.frequencyType = frequencyType;
    }


    /**
     * This method gets the day of month: 1st, 2nd, etc.
     * 
     * @return dayInMonth
     */
    public String getDayInMonth() {
        return dayInMonth;
    }

    /**
     * This method sets the day of month.
     * 
     * @param dayInMonth
     */
    public void setDayInMonth(String dayInMonth) {
        this.dayInMonth = dayInMonth;
    }

    /**
     * @see org.kuali.rice.krad.bo.KualiCodeBase#getCodeAndDescription()
     */
    @Override
    public String getCodeAndDescription() {
        if (StringUtils.isEmpty(code)) {
            return KFSConstants.EMPTY_STRING;
        }
        return super.getCodeAndDescription();
    }
}
