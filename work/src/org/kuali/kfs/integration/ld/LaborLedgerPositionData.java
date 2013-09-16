/*
 * Copyright 2013 The Kuali Foundation.
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