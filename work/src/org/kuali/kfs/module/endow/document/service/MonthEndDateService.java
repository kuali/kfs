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
package org.kuali.kfs.module.endow.document.service;

import java.util.Date;
import java.util.List;

import org.kuali.rice.core.api.util.type.KualiInteger;

/**
 * MonthEndDateService interface to provide the method to get month end date id
 */
public interface MonthEndDateService {

    /**
     * gets month end id date for a given monthEndId
     * 
     * @param monthEndId
     * @return monthEndIdDate
     */
    public java.sql.Date getByPrimaryKey(KualiInteger monthEndId);
    
    /**
     * Gets the most recent date.  it is basically the last record in END_ME_DT_T, 
     * where the ID is the highest number
     * @return mostRecentDate
     */
    public java.sql.Date getMostRecentDate();
    
    /**
     * gets month end id
     * 
     * @param monthEndDate
     * @return monthEndDateId
     */
    public KualiInteger getMonthEndId(Date monthEndDate);
    
    /**
     * gets the next month end id for the new record
     * 
     * @return monthEndDateId
     */
    public KualiInteger getNextMonthEndIdForNewRecord();
    
    /**
     * gets the first dates of month 
     * 
     * @return
     */
    public List<String> getBeginningDates();
    
    /**
     * gets the month end dates
     * 
     * @return
     */
    public List<String> getEndingDates();
        
}
