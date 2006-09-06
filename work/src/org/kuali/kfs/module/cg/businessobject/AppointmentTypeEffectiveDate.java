/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
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
