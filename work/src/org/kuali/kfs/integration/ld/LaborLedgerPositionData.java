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
package org.kuali.kfs.integration.ld;

import java.math.BigDecimal;
import java.sql.Date;

import org.kuali.kfs.gl.businessobject.TransientBalanceInquiryAttributes;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;


public interface LaborLedgerPositionData extends ExternalizableBusinessObject {

    /**
     * Gets the positionNumber
     *
     * @return Returns the positionNumber
     */
    public String getPositionNumber();

    /**
     * Gets the jobCode
     *
     * @return Returns the jobCode
     */
    public String getJobCode();

    /**
     * Gets the effectiveDate
     *
     * @return Returns the effectiveDate
     */
    public Date getEffectiveDate();

    /**
     * Gets the positionEffectiveStatus
     *
     * @return Returns the positionEffectiveStatus
     */
    public String getPositionEffectiveStatus();

    /**
     * Gets the description
     *
     * @return Returns the description
     */
    public String getDescription();

    /**
     * Gets the shortDescription
     *
     * @return Returns the shortDescription
     */
    public String getShortDescription();

    /**
     * Gets the businessUnit
     *
     * @return Returns the businessUnit
     */
    public String getBusinessUnit();

    /**
     * Gets the departmentId
     *
     * @return Returns the departmentId
     */
    public String getDepartmentId();

    /**
     * Gets the positionStatus
     *
     * @return Returns the positionStatus
     */
    public String getPositionStatus();

    /**
     * Gets the statusDate
     *
     * @return Returns the statusDate
     */
    public Date getStatusDate();

    /**
     * Gets the budgetedPosition
     *
     * @return Returns the budgetedPosition
     */
    public String getBudgetedPosition();

    /**
     * Gets the standardHoursFrequency
     *
     * @return Returns the standardHoursFrequency
     */
    public String getStandardHoursFrequency();

    /**
     * Gets the positionRegularTemporary
     *
     * @return Returns the positionRegularTemporary
     */
    public String getPositionRegularTemporary();

    /**
     * Gets the positionFullTimeEquivalency
     *
     * @return Returns the positionFullTimeEquivalency
     */
    public BigDecimal getPositionFullTimeEquivalency();

    /**
     * Gets the positionSalaryPlanDefault
     *
     * @return Returns the positionSalaryPlanDefault
     */
    public String getPositionSalaryPlanDefault();

    /**
     * Gets the positionGradeDefault
     *
     * @return Returns the positionGradeDefault
     */
    public String getPositionGradeDefault();

    /**
     * Gets the dummyBusinessObject
     *
     * @return Returns the dummyBusinessObject.
     */
    public TransientBalanceInquiryAttributes getDummyBusinessObject();

}
