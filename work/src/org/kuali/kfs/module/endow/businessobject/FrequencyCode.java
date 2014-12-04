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
