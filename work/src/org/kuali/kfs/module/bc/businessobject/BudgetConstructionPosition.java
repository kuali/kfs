/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.budget.bo;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ResponsibilityCenter;

/**
 * 
 */
public class BudgetConstructionPosition extends PersistableBusinessObjectBase {

	private String positionNumber;
	private Integer universityFiscalYear;
	private Date positionEffectiveDate;
	private String positionEffectiveStatus;
	private String positionStatus;
	private boolean budgetedPosition;
	private boolean confidentialPosition;
	private BigDecimal positionStandardHoursDefault;
	private String positionRegularTemporary;
	private BigDecimal positionFullTimeEquivalency;
	private Integer iuNormalWorkMonths;
	private Integer iuPayMonths;
	private String positionDescription;
	private String setidDepartment;
	private String positionDepartmentIdentifier;
	private String responsibilityCenterCode;
	private String positionUnionCode;
	private String positionSalaryPlanDefault;
	private String positionGradeDefault;
	private String setidJobCode;
	private String jobCode;
	private String jobCodeDescription;
	private String setidSalary;
	private String iuDefaultObjectCode;
	private String iuPositionType;
	private String positionLockUserIdentifier;

    private Options universityFiscal;
    private List pendingBudgetConstructionAppointmentFunding;
    private List budgetConstructionPositionSelect;
    private ResponsibilityCenter responsibilityCenter;
    private UniversalUser positionLockUser;
    
	/**
	 * Default constructor.
	 */
	public BudgetConstructionPosition() {
        budgetConstructionPositionSelect = new ArrayList();
        pendingBudgetConstructionAppointmentFunding = new ArrayList();;

	}

    /**
     * Computes the positionFullTimeEquivalency attribute.
     * 
     * @return Returns the compute positionFullTimeEquivalency
     * 
     */
    public static BigDecimal getCalculatedBCPositionFTE(BigDecimal positionStandardHoursDefault, Integer iuNormalWorkMonths, Integer iuPayMonths ) { 
        if (iuPayMonths > 0){
            BigDecimal temp1 = positionStandardHoursDefault.divide(new BigDecimal(40),4, BigDecimal.ROUND_DOWN );
            BigDecimal temp2 = new BigDecimal(iuNormalWorkMonths).divide(new BigDecimal(iuPayMonths), 4, BigDecimal.ROUND_DOWN );
            BigDecimal result = temp1.multiply(temp2) ;
            result = result.setScale(2, BigDecimal.ROUND_DOWN);
            return result;
        }else{
            return BigDecimal.valueOf(0.0);
        }
    }

 
    
	/**
	 * Gets the positionNumber attribute.
	 * 
	 * @return Returns the positionNumber
	 * 
	 */
	public String getPositionNumber() { 
		return positionNumber;
	}

	/**
	 * Sets the positionNumber attribute.
	 * 
	 * @param positionNumber The positionNumber to set.
	 * 
	 */
	public void setPositionNumber(String positionNumber) {
		this.positionNumber = positionNumber;
	}


	/**
	 * Gets the universityFiscalYear attribute.
	 * 
	 * @return Returns the universityFiscalYear
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
	 * Gets the positionEffectiveDate attribute.
	 * 
	 * @return Returns the positionEffectiveDate
	 * 
	 */
	public Date getPositionEffectiveDate() { 
		return positionEffectiveDate;
	}

	/**
	 * Sets the positionEffectiveDate attribute.
	 * 
	 * @param positionEffectiveDate The positionEffectiveDate to set.
	 * 
	 */
	public void setPositionEffectiveDate(Date positionEffectiveDate) {
		this.positionEffectiveDate = positionEffectiveDate;
	}

   
    /**
     * Gets the positionEffectiveStatus attribute. 
     * @return Returns the positionEffectiveStatus.
     */
    public String getPositionEffectiveStatus() {
        return positionEffectiveStatus;
    }

    /**
     * Sets the positionEffectiveStatus attribute value.
     * @param positionEffectiveStatus The positionEffectiveStatus to set.
     */
    public void setPositionEffectiveStatus(String positionEffectiveStatus) {
        this.positionEffectiveStatus = positionEffectiveStatus;
    }

    /**
     * Gets the positionStatus attribute. 
     * @return Returns the positionStatus.
     */
    public String getPositionStatus() {
        return positionStatus;
    }

    /**
     * Sets the positionStatus attribute value.
     * @param positionStatus The positionStatus to set.
     */
    public void setPositionStatus(String positionStatus) {
        this.positionStatus = positionStatus;
    }

    /**
     * Gets the budgetedPosition attribute. 
     * @return Returns the budgetedPosition.
     */
    public boolean isBudgetedPosition() {
        return budgetedPosition;
    }

    /**
     * Sets the budgetedPosition attribute value.
     * @param budgetedPosition The budgetedPosition to set.
     */
    public void setBudgetedPosition(boolean budgetedPosition) {
        this.budgetedPosition = budgetedPosition;
    }

    /**
     * Gets the confidentialPosition attribute. 
     * @return Returns the confidentialPosition.
     */
    public boolean isConfidentialPosition() {
        return confidentialPosition;
    }

    /**
     * Sets the confidentialPosition attribute value.
     * @param confidentialPosition The confidentialPosition to set.
     */
    public void setConfidentialPosition(boolean confidentialPosition) {
        this.confidentialPosition = confidentialPosition;
    }

    /**
	 * Gets the positionStandardHoursDefault attribute.
	 * 
	 * @return Returns the positionStandardHoursDefault
	 * 
	 */
	public BigDecimal getPositionStandardHoursDefault() { 
		return positionStandardHoursDefault;
	}

	/**
	 * Sets the positionStandardHoursDefault attribute.
	 * 
	 * @param positionStandardHoursDefault The positionStandardHoursDefault to set.
	 * 
	 */
	public void setPositionStandardHoursDefault(BigDecimal positionStandardHoursDefault) {
		this.positionStandardHoursDefault = positionStandardHoursDefault;
	}


	/**
	 * Gets the positionRegularTemporary attribute.
	 * 
	 * @return Returns the positionRegularTemporary
	 * 
	 */
	public String getPositionRegularTemporary() { 
		return positionRegularTemporary;
	}

	/**
	 * Sets the positionRegularTemporary attribute.
	 * 
	 * @param positionRegularTemporary The positionRegularTemporary to set.
	 * 
	 */
	public void setPositionRegularTemporary(String positionRegularTemporary) {
		this.positionRegularTemporary = positionRegularTemporary;
	}


	/**
	 * Gets the positionFullTimeEquivalency attribute.
	 * 
	 * @return Returns the positionFullTimeEquivalency
	 * 
	 */
	public BigDecimal getPositionFullTimeEquivalency() { 
		return positionFullTimeEquivalency;
	}

	/**
	 * Sets the positionFullTimeEquivalency attribute.
	 * 
	 * @param positionFullTimeEquivalency The positionFullTimeEquivalency to set.
	 * 
	 */
	public void setPositionFullTimeEquivalency(BigDecimal positionFullTimeEquivalency) {
		this.positionFullTimeEquivalency = positionFullTimeEquivalency;
	}


	/**
	 * Gets the iuNormalWorkMonths attribute.
	 * 
	 * @return Returns the iuNormalWorkMonths
	 * 
	 */
	public Integer getIuNormalWorkMonths() { 
		return iuNormalWorkMonths;
	}

	/**
	 * Sets the iuNormalWorkMonths attribute.
	 * 
	 * @param iuNormalWorkMonths The iuNormalWorkMonths to set.
	 * 
	 */
	public void setIuNormalWorkMonths(Integer iuNormalWorkMonths) {
		this.iuNormalWorkMonths = iuNormalWorkMonths;
	}


	/**
	 * Gets the iuPayMonths attribute.
	 * 
	 * @return Returns the iuPayMonths
	 * 
	 */
	public Integer getIuPayMonths() { 
		return iuPayMonths;
	}

	/**
	 * Sets the iuPayMonths attribute.
	 * 
	 * @param iuPayMonths The iuPayMonths to set.
	 * 
	 */
	public void setIuPayMonths(Integer iuPayMonths) {
		this.iuPayMonths = iuPayMonths;
	}


	/**
	 * Gets the positionDescription attribute.
	 * 
	 * @return Returns the positionDescription
	 * 
	 */
	public String getPositionDescription() { 
		return positionDescription;
	}

	/**
	 * Sets the positionDescription attribute.
	 * 
	 * @param positionDescription The positionDescription to set.
	 * 
	 */
	public void setPositionDescription(String positionDescription) {
		this.positionDescription = positionDescription;
	}


	/**
	 * Gets the setidDepartment attribute.
	 * 
	 * @return Returns the setidDepartment
	 * 
	 */
	public String getSetidDepartment() { 
		return setidDepartment;
	}

	/**
	 * Sets the setidDepartment attribute.
	 * 
	 * @param setidDepartment The setidDepartment to set.
	 * 
	 */
	public void setSetidDepartment(String setidDepartment) {
		this.setidDepartment = setidDepartment;
	}


	/**
	 * Gets the positionDepartmentIdentifier attribute.
	 * 
	 * @return Returns the positionDepartmentIdentifier
	 * 
	 */
	public String getPositionDepartmentIdentifier() { 
		return positionDepartmentIdentifier;
	}

	/**
	 * Sets the positionDepartmentIdentifier attribute.
	 * 
	 * @param positionDepartmentIdentifier The positionDepartmentIdentifier to set.
	 * 
	 */
	public void setPositionDepartmentIdentifier(String positionDepartmentIdentifier) {
		this.positionDepartmentIdentifier = positionDepartmentIdentifier;
	}


	/**
	 * Gets the responsibilityCenterCode attribute.
	 * 
	 * @return Returns the responsibilityCenterCode
	 * 
	 */
	public String getResponsibilityCenterCode() { 
		return responsibilityCenterCode;
	}

	/**
	 * Sets the responsibilityCenterCode attribute.
	 * 
	 * @param responsibilityCenterCode The responsibilityCenterCode to set.
	 * 
	 */
	public void setResponsibilityCenterCode(String responsibilityCenterCode) {
		this.responsibilityCenterCode = responsibilityCenterCode;
	}


	/**
	 * Gets the positionUnionCode attribute.
	 * 
	 * @return Returns the positionUnionCode
	 * 
	 */
	public String getPositionUnionCode() { 
		return positionUnionCode;
	}

	/**
	 * Sets the positionUnionCode attribute.
	 * 
	 * @param positionUnionCode The positionUnionCode to set.
	 * 
	 */
	public void setPositionUnionCode(String positionUnionCode) {
		this.positionUnionCode = positionUnionCode;
	}


	/**
	 * Gets the positionSalaryPlanDefault attribute.
	 * 
	 * @return Returns the positionSalaryPlanDefault
	 * 
	 */
	public String getPositionSalaryPlanDefault() { 
		return positionSalaryPlanDefault;
	}

	/**
	 * Sets the positionSalaryPlanDefault attribute.
	 * 
	 * @param positionSalaryPlanDefault The positionSalaryPlanDefault to set.
	 * 
	 */
	public void setPositionSalaryPlanDefault(String positionSalaryPlanDefault) {
		this.positionSalaryPlanDefault = positionSalaryPlanDefault;
	}


	/**
	 * Gets the positionGradeDefault attribute.
	 * 
	 * @return Returns the positionGradeDefault
	 * 
	 */
	public String getPositionGradeDefault() { 
		return positionGradeDefault;
	}

	/**
	 * Sets the positionGradeDefault attribute.
	 * 
	 * @param positionGradeDefault The positionGradeDefault to set.
	 * 
	 */
	public void setPositionGradeDefault(String positionGradeDefault) {
		this.positionGradeDefault = positionGradeDefault;
	}


	/**
	 * Gets the setidJobCode attribute.
	 * 
	 * @return Returns the setidJobCode
	 * 
	 */
	public String getSetidJobCode() { 
		return setidJobCode;
	}

	/**
	 * Sets the setidJobCode attribute.
	 * 
	 * @param setidJobCode The setidJobCode to set.
	 * 
	 */
	public void setSetidJobCode(String setidJobCode) {
		this.setidJobCode = setidJobCode;
	}


	/**
	 * Gets the jobCode attribute.
	 * 
	 * @return Returns the jobCode
	 * 
	 */
	public String getJobCode() { 
		return jobCode;
	}

	/**
	 * Sets the jobCode attribute.
	 * 
	 * @param jobCode The jobCode to set.
	 * 
	 */
	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
	}


	/**
	 * Gets the jobCodeDescription attribute.
	 * 
	 * @return Returns the jobCodeDescription
	 * 
	 */
	public String getJobCodeDescription() { 
		return jobCodeDescription;
	}

	/**
	 * Sets the jobCodeDescription attribute.
	 * 
	 * @param jobCodeDescription The jobCodeDescription to set.
	 * 
	 */
	public void setJobCodeDescription(String jobCodeDescription) {
		this.jobCodeDescription = jobCodeDescription;
	}


	/**
	 * Gets the setidSalary attribute.
	 * 
	 * @return Returns the setidSalary
	 * 
	 */
	public String getSetidSalary() { 
		return setidSalary;
	}

	/**
	 * Sets the setidSalary attribute.
	 * 
	 * @param setidSalary The setidSalary to set.
	 * 
	 */
	public void setSetidSalary(String setidSalary) {
		this.setidSalary = setidSalary;
	}


	/**
	 * Gets the iuDefaultObjectCode attribute.
	 * 
	 * @return Returns the iuDefaultObjectCode
	 * 
	 */
	public String getIuDefaultObjectCode() { 
		return iuDefaultObjectCode;
	}

	/**
	 * Sets the iuDefaultObjectCode attribute.
	 * 
	 * @param iuDefaultObjectCode The iuDefaultObjectCode to set.
	 * 
	 */
	public void setIuDefaultObjectCode(String iuDefaultObjectCode) {
		this.iuDefaultObjectCode = iuDefaultObjectCode;
	}


	/**
	 * Gets the iuPositionType attribute.
	 * 
	 * @return Returns the iuPositionType
	 * 
	 */
	public String getIuPositionType() { 
		return iuPositionType;
	}

	/**
	 * Sets the iuPositionType attribute.
	 * 
	 * @param iuPositionType The iuPositionType to set.
	 * 
	 */
	public void setIuPositionType(String iuPositionType) {
		this.iuPositionType = iuPositionType;
	}


	/**
	 * Gets the positionLockUserIdentifier attribute.
	 * 
	 * @return Returns the positionLockUserIdentifier
	 * 
	 */
	public String getPositionLockUserIdentifier() { 
		return positionLockUserIdentifier;
	}

	/**
	 * Sets the positionLockUserIdentifier attribute.
	 * 
	 * @param positionLockUserIdentifier The positionLockUserIdentifier to set.
	 * 
	 */
	public void setPositionLockUserIdentifier(String positionLockUserIdentifier) {
		this.positionLockUserIdentifier = positionLockUserIdentifier;
	}


    /**
     * Gets the pendingBudgetConstructionAppointmentFunding list.
     * 
     * @return Returns the pendingBudgetConstructionAppointmentFunding list
     * 
     */
    public List getPendingBudgetConstructionAppointmentFunding() { 
        return pendingBudgetConstructionAppointmentFunding;
    }

    /**
     * Sets the pendingBudgetConstructionAppointmentFunding list.
     * 
     * @param pendingBudgetConstructionAppointmentFunding The pendingBudgetConstructionAppointmentFunding list to set.
     * @deprecated
     */
    public void setPendingBudgetConstructionAppointmentFunding(List pendingBudgetConstructionAppointmentFunding) {
        this.pendingBudgetConstructionAppointmentFunding = pendingBudgetConstructionAppointmentFunding;
    }

	/**
	 * Gets the budgetConstructionPositionSelect list.
	 * 
	 * @return Returns the budgetConstructionPositionSelect list
	 * 
	 */
	public List getBudgetConstructionPositionSelect() { 
		return budgetConstructionPositionSelect;
	}

	/**
	 * Sets the budgetConstructionPositionSelect list.
	 * 
	 * @param budgetConstructionPositionSelect The budgetConstructionPositionSelect list to set.
	 * @deprecated
	 */
	public void setBudgetConstructionPositionSelect(List budgetConstructionPositionSelect) {
		this.budgetConstructionPositionSelect = budgetConstructionPositionSelect;
	}

    /**
     * Gets the responsibilityCenter attribute. 
     * @return Returns the responsibilityCenter.
     */
    public ResponsibilityCenter getResponsibilityCenter() {
        return responsibilityCenter;
    }

    /**
     * Sets the responsibilityCenter attribute value.
     * @param responsibilityCenter The responsibilityCenter to set.
     * @deprecated
     */
    public void setResponsibilityCenter(ResponsibilityCenter responsibilityCenter) {
        this.responsibilityCenter = responsibilityCenter;
    }

    /**
     * Gets the positionLockUser attribute value. 
     * @return Returns the positionLockUser
     */
    public UniversalUser getPositionLockUser() {
        positionLockUser = SpringServiceLocator.getUniversalUserService().updateUniversalUserIfNecessary(positionLockUserIdentifier, positionLockUser);
        return positionLockUser;
    }

    /**
     * Sets the positionLockUser attribute.
     * 
     * @param positionLockUser The positionLockUser to set.
     * @deprecated
     */
    public void setPositionLockUser(UniversalUser positionLockUser) {
        this.positionLockUser = positionLockUser;
    }

    /**
     * Gets the universityFiscal attribute. 
     * @return Returns the universityFiscal.
     */
    public Options getUniversityFiscal() {
        return universityFiscal;
    }

    /**
     * Sets the universityFiscal attribute value.
     * @param universityFiscal The universityFiscal to set.
     */
    public void setUniversityFiscal(Options universityFiscal) {
        this.universityFiscal = universityFiscal;
    }
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("positionNumber", this.positionNumber);
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        return m;
    }


}
