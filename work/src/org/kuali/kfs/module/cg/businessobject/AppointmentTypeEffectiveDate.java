/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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

package org.kuali.module.kra.budget.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AppointmentTypeEffectiveDate extends BusinessObjectBase {

    private String appointmentTypeCode;
    private Integer universityFiscalYear;
    private Date appointmentTypeBeginDate;
    private Date appointmentTypeEndDate;

    /**
     * Default constructor.
     */
    public AppointmentTypeEffectiveDate() {

    }
    
    public AppointmentTypeEffectiveDate(String appointmentTypeCode, Integer universityFiscalYear) {
        this();
        this.appointmentTypeCode = appointmentTypeCode;
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the appointmentTypeCode attribute.
     * 
     * @return - Returns the appointmentTypeCode
     * 
     */
    public String getAppointmentTypeCode() {
        return appointmentTypeCode;
    }

    /**
     * Sets the universityAppointmentTypeCode attribute.
     * 
     * @param universityAppointmentTypeCode The universityAppointmentTypeCode to set.
     * 
     */
    public void setAppointmentTypeCode(String appointmentTypeCode) {
        this.appointmentTypeCode = appointmentTypeCode;
    }


    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return - Returns the universityFiscalYear
     * 
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     * 
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }


    /**
     * Gets the appointmentTypeBeginDate attribute.
     * 
     * @return - Returns the appointmentTypeBeginDate
     * 
     */
    public Date getAppointmentTypeBeginDate() {
        return appointmentTypeBeginDate;
    }

    /**
     * Sets the appointmentTypeBeginDate attribute.
     * 
     * @param appointmentTypeBeginDate The appointmentTypeBeginDate to set.
     * 
     */
    public void setAppointmentTypeBeginDate(Date appointmentTypeBeginDate) {
        this.appointmentTypeBeginDate = appointmentTypeBeginDate;
    }


    /**
     * Gets the appointmentTypeEndDate attribute.
     * 
     * @return - Returns the appointmentTypeEndDate
     * 
     */
    public Date getAppointmentTypeEndDate() {
        return appointmentTypeEndDate;
    }

    /**
     * Sets the appointmentTypeEndDate attribute.
     * 
     * @param appointmentTypeEndDate The appointmentTypeEndDate to set.
     * 
     */
    public void setAppointmentTypeEndDate(Date appointmentTypeEndDate) {
        this.appointmentTypeEndDate = appointmentTypeEndDate;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        return m;
    }
}
