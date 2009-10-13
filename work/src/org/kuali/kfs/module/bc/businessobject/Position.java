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
package org.kuali.kfs.module.bc.businessobject;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Provides attributes that describe a specific job located in a particular department (organization).
 */
public interface Position {

    /**
     * Gets the positionNumber attribute.
     * 
     * @return Returns the positionNumber
     */
    public abstract String getPositionNumber();

    /**
     * Sets the positionNumber attribute.
     * 
     * @param positionNumber The positionNumber to set.
     */
    public abstract void setPositionNumber(String positionNumber);

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear
     */
    public abstract Integer getUniversityFiscalYear();

    /**
     * Sets the universityFiscalYear attribute.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public abstract void setUniversityFiscalYear(Integer universityFiscalYear);

    /**
     * Gets the positionEffectiveDate attribute.
     * 
     * @return Returns the positionEffectiveDate
     */
    public abstract Date getPositionEffectiveDate();

    /**
     * Sets the positionEffectiveDate attribute.
     * 
     * @param positionEffectiveDate The positionEffectiveDate to set.
     */
    public abstract void setPositionEffectiveDate(Date positionEffectiveDate);

    /**
     * Gets the positionEffectiveStatus attribute.
     * 
     * @return Returns the positionEffectiveStatus.
     */
    public abstract String getPositionEffectiveStatus();

    /**
     * Sets the positionEffectiveStatus attribute value.
     * 
     * @param positionEffectiveStatus The positionEffectiveStatus to set.
     */
    public abstract void setPositionEffectiveStatus(String positionEffectiveStatus);

    /**
     * Gets the positionStatus attribute.
     * 
     * @return Returns the positionStatus.
     */
    public abstract String getPositionStatus();

    /**
     * Sets the positionStatus attribute value.
     * 
     * @param positionStatus The positionStatus to set.
     */
    public abstract void setPositionStatus(String positionStatus);

    /**
     * Gets the budgetedPosition attribute.
     * 
     * @return Returns the budgetedPosition.
     */
    public abstract boolean isBudgetedPosition();

    /**
     * Sets the budgetedPosition attribute value.
     * 
     * @param budgetedPosition The budgetedPosition to set.
     */
    public abstract void setBudgetedPosition(boolean budgetedPosition);

    /**
     * Gets the confidentialPosition attribute.
     * 
     * @return Returns the confidentialPosition.
     */
    public abstract boolean isConfidentialPosition();

    /**
     * Sets the confidentialPosition attribute value.
     * 
     * @param confidentialPosition The confidentialPosition to set.
     */
    public abstract void setConfidentialPosition(boolean confidentialPosition);

    /**
     * Gets the positionStandardHoursDefault attribute.
     * 
     * @return Returns the positionStandardHoursDefault
     */
    public abstract BigDecimal getPositionStandardHoursDefault();

    /**
     * Sets the positionStandardHoursDefault attribute.
     * 
     * @param positionStandardHoursDefault The positionStandardHoursDefault to set.
     */
    public abstract void setPositionStandardHoursDefault(BigDecimal positionStandardHoursDefault);

    /**
     * Gets the positionRegularTemporary attribute.
     * 
     * @return Returns the positionRegularTemporary
     */
    public abstract String getPositionRegularTemporary();

    /**
     * Sets the positionRegularTemporary attribute.
     * 
     * @param positionRegularTemporary The positionRegularTemporary to set.
     */
    public abstract void setPositionRegularTemporary(String positionRegularTemporary);

    /**
     * Gets the positionFullTimeEquivalency attribute.
     * 
     * @return Returns the positionFullTimeEquivalency
     */
    public abstract BigDecimal getPositionFullTimeEquivalency();

    /**
     * Sets the positionFullTimeEquivalency attribute.
     * 
     * @param positionFullTimeEquivalency The positionFullTimeEquivalency to set.
     */
    public abstract void setPositionFullTimeEquivalency(BigDecimal positionFullTimeEquivalency);

    /**
     * Gets the iuNormalWorkMonths attribute.
     * 
     * @return Returns the iuNormalWorkMonths
     */
    public abstract Integer getIuNormalWorkMonths();

    /**
     * Sets the iuNormalWorkMonths attribute.
     * 
     * @param iuNormalWorkMonths The iuNormalWorkMonths to set.
     */
    public abstract void setIuNormalWorkMonths(Integer iuNormalWorkMonths);

    /**
     * Gets the iuPayMonths attribute.
     * 
     * @return Returns the iuPayMonths
     */
    public abstract Integer getIuPayMonths();

    /**
     * Sets the iuPayMonths attribute.
     * 
     * @param iuPayMonths The iuPayMonths to set.
     */
    public abstract void setIuPayMonths(Integer iuPayMonths);

    /**
     * Gets the positionDescription attribute.
     * 
     * @return Returns the positionDescription
     */
    public abstract String getPositionDescription();

    /**
     * Sets the positionDescription attribute.
     * 
     * @param positionDescription The positionDescription to set.
     */
    public abstract void setPositionDescription(String positionDescription);

    /**
     * Gets the setidDepartment attribute.
     * 
     * @return Returns the setidDepartment
     */
    public abstract String getSetidDepartment();

    /**
     * Sets the setidDepartment attribute.
     * 
     * @param setidDepartment The setidDepartment to set.
     */
    public abstract void setSetidDepartment(String setidDepartment);

    /**
     * Gets the positionDepartmentIdentifier attribute.
     * 
     * @return Returns the positionDepartmentIdentifier
     */
    public abstract String getPositionDepartmentIdentifier();

    /**
     * Sets the positionDepartmentIdentifier attribute.
     * 
     * @param positionDepartmentIdentifier The positionDepartmentIdentifier to set.
     */
    public abstract void setPositionDepartmentIdentifier(String positionDepartmentIdentifier);

    /**
     * Gets the responsibilityCenterCode attribute.
     * 
     * @return Returns the responsibilityCenterCode
     */
    public abstract String getResponsibilityCenterCode();

    /**
     * Sets the responsibilityCenterCode attribute.
     * 
     * @param responsibilityCenterCode The responsibilityCenterCode to set.
     */
    public abstract void setResponsibilityCenterCode(String responsibilityCenterCode);

    /**
     * Gets the positionUnionCode attribute.
     * 
     * @return Returns the positionUnionCode
     */
    public abstract String getPositionUnionCode();

    /**
     * Sets the positionUnionCode attribute.
     * 
     * @param positionUnionCode The positionUnionCode to set.
     */
    public abstract void setPositionUnionCode(String positionUnionCode);

    /**
     * Gets the positionSalaryPlanDefault attribute.
     * 
     * @return Returns the positionSalaryPlanDefault
     */
    public abstract String getPositionSalaryPlanDefault();

    /**
     * Sets the positionSalaryPlanDefault attribute.
     * 
     * @param positionSalaryPlanDefault The positionSalaryPlanDefault to set.
     */
    public abstract void setPositionSalaryPlanDefault(String positionSalaryPlanDefault);

    /**
     * Gets the positionGradeDefault attribute.
     * 
     * @return Returns the positionGradeDefault
     */
    public abstract String getPositionGradeDefault();

    /**
     * Sets the positionGradeDefault attribute.
     * 
     * @param positionGradeDefault The positionGradeDefault to set.
     */
    public abstract void setPositionGradeDefault(String positionGradeDefault);

    /**
     * Gets the setidJobCode attribute.
     * 
     * @return Returns the setidJobCode
     */
    public abstract String getSetidJobCode();

    /**
     * Sets the setidJobCode attribute.
     * 
     * @param setidJobCode The setidJobCode to set.
     */
    public abstract void setSetidJobCode(String setidJobCode);

    /**
     * Gets the jobCode attribute.
     * 
     * @return Returns the jobCode
     */
    public abstract String getJobCode();

    /**
     * Sets the jobCode attribute.
     * 
     * @param jobCode The jobCode to set.
     */
    public abstract void setJobCode(String jobCode);

    /**
     * Gets the jobCodeDescription attribute.
     * 
     * @return Returns the jobCodeDescription
     */
    public abstract String getJobCodeDescription();

    /**
     * Sets the jobCodeDescription attribute.
     * 
     * @param jobCodeDescription The jobCodeDescription to set.
     */
    public abstract void setJobCodeDescription(String jobCodeDescription);

    /**
     * Gets the setidSalary attribute.
     * 
     * @return Returns the setidSalary
     */
    public abstract String getSetidSalary();

    /**
     * Sets the setidSalary attribute.
     * 
     * @param setidSalary The setidSalary to set.
     */
    public abstract void setSetidSalary(String setidSalary);

    /**
     * Gets the iuDefaultObjectCode attribute.
     * 
     * @return Returns the iuDefaultObjectCode
     */
    public abstract String getIuDefaultObjectCode();

    /**
     * Sets the iuDefaultObjectCode attribute.
     * 
     * @param iuDefaultObjectCode The iuDefaultObjectCode to set.
     */
    public abstract void setIuDefaultObjectCode(String iuDefaultObjectCode);

    /**
     * Gets the iuPositionType attribute.
     * 
     * @return Returns the iuPositionType
     */
    public abstract String getIuPositionType();

    /**
     * Sets the iuPositionType attribute.
     * 
     * @param iuPositionType The iuPositionType to set.
     */
    public abstract void setIuPositionType(String iuPositionType);

    /**
     * Gets the positionLockUserIdentifier attribute.
     * 
     * @return Returns the positionLockUserIdentifier
     */
    public abstract String getPositionLockUserIdentifier();

    /**
     * Sets the positionLockUserIdentifier attribute.
     * 
     * @param positionLockUserIdentifier The positionLockUserIdentifier to set.
     */
    public abstract void setPositionLockUserIdentifier(String positionLockUserIdentifier);

    /**
     * determine whether the current budget position is effective
     * 
     * @return true if the current budget position is effective; otherwise, false
     */
    public abstract boolean isEffective();

}
