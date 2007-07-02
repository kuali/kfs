package org.kuali.module.labor.bo;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PositionData extends PersistableBusinessObjectBase {

	private Integer positionIdentifierSequence;
	private String positionNumber;
	private String jobCode;
	private String payGroup;
	private String employeeType;
	private Date effectiveDate;
	private String positionEffectiveStatus;
	private String description;
	private String shortDescription;
	private String businessUnit;
	private String departmentId;
	private String positionStatus;
	private Date statusDate;
	private String budgetedPosition;
	private BigDecimal standardHoursDefault;
	private String standardHoursFrequency;
	private String positionRegularTemporary;
	private BigDecimal positionFullTimeEquivalency;
	private String positionSalaryPlanDefault;
	private String positionGradeDefault;

	/**
	 * Default constructor.
	 */
	public PositionData() {

	}

	/**
	 * Gets the positionIdentifierSequence attribute.
	 * 
	 * @return Returns the positionIdentifierSequence
	 * 
	 */
	public Integer getPositionIdentifierSequence() { 
		return positionIdentifierSequence;
	}

	/**
	 * Sets the positionIdentifierSequence attribute.
	 * 
	 * @param positionIdentifierSequence The positionIdentifierSequence to set.
	 * 
	 */
	public void setPositionIdentifierSequence(Integer positionIdentifierSequence) {
		this.positionIdentifierSequence = positionIdentifierSequence;
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
	 * Gets the payGroup attribute.
	 * 
	 * @return Returns the payGroup
	 * 
	 */
	public String getPayGroup() { 
		return payGroup;
	}

	/**
	 * Sets the payGroup attribute.
	 * 
	 * @param payGroup The payGroup to set.
	 * 
	 */
	public void setPayGroup(String payGroup) {
		this.payGroup = payGroup;
	}


	/**
	 * Gets the employeeType attribute.
	 * 
	 * @return Returns the employeeType
	 * 
	 */
	public String getEmployeeType() { 
		return employeeType;
	}

	/**
	 * Sets the employeeType attribute.
	 * 
	 * @param employeeType The employeeType to set.
	 * 
	 */
	public void setEmployeeType(String employeeType) {
		this.employeeType = employeeType;
	}


	/**
	 * Gets the effectiveDate attribute.
	 * 
	 * @return Returns the effectiveDate
	 * 
	 */
	public Date getEffectiveDate() { 
		return effectiveDate;
	}

	/**
	 * Sets the effectiveDate attribute.
	 * 
	 * @param effectiveDate The effectiveDate to set.
	 * 
	 */
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}


	/**
	 * Gets the positionEffectiveStatus attribute.
	 * 
	 * @return Returns the positionEffectiveStatus
	 * 
	 */
	public String getPositionEffectiveStatus() { 
		return positionEffectiveStatus;
	}

	/**
	 * Sets the positionEffectiveStatus attribute.
	 * 
	 * @param positionEffectiveStatus The positionEffectiveStatus to set.
	 * 
	 */
	public void setPositionEffectiveStatus(String positionEffectiveStatus) {
		this.positionEffectiveStatus = positionEffectiveStatus;
	}


	/**
	 * Gets the description attribute.
	 * 
	 * @return Returns the description
	 * 
	 */
	public String getDescription() { 
		return description;
	}

	/**
	 * Sets the description attribute.
	 * 
	 * @param description The description to set.
	 * 
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * Gets the shortDescription attribute.
	 * 
	 * @return Returns the shortDescription
	 * 
	 */
	public String getShortDescription() { 
		return shortDescription;
	}

	/**
	 * Sets the shortDescription attribute.
	 * 
	 * @param shortDescription The shortDescription to set.
	 * 
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}


	/**
	 * Gets the businessUnit attribute.
	 * 
	 * @return Returns the businessUnit
	 * 
	 */
	public String getBusinessUnit() { 
		return businessUnit;
	}

	/**
	 * Sets the businessUnit attribute.
	 * 
	 * @param businessUnit The businessUnit to set.
	 * 
	 */
	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}


	/**
	 * Gets the departmentId attribute.
	 * 
	 * @return Returns the departmentId
	 * 
	 */
	public String getDepartmentId() { 
		return departmentId;
	}

	/**
	 * Sets the departmentId attribute.
	 * 
	 * @param departmentId The departmentId to set.
	 * 
	 */
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}


	/**
	 * Gets the positionStatus attribute.
	 * 
	 * @return Returns the positionStatus
	 * 
	 */
	public String getPositionStatus() { 
		return positionStatus;
	}

	/**
	 * Sets the positionStatus attribute.
	 * 
	 * @param positionStatus The positionStatus to set.
	 * 
	 */
	public void setPositionStatus(String positionStatus) {
		this.positionStatus = positionStatus;
	}


	/**
	 * Gets the statusDate attribute.
	 * 
	 * @return Returns the statusDate
	 * 
	 */
	public Date getStatusDate() { 
		return statusDate;
	}

	/**
	 * Sets the statusDate attribute.
	 * 
	 * @param statusDate The statusDate to set.
	 * 
	 */
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}


	/**
	 * Gets the budgetedPosition attribute.
	 * 
	 * @return Returns the budgetedPosition
	 * 
	 */
	public String getBudgetedPosition() { 
		return budgetedPosition;
	}

	/**
	 * Sets the budgetedPosition attribute.
	 * 
	 * @param budgetedPosition The budgetedPosition to set.
	 * 
	 */
	public void setBudgetedPosition(String budgetedPosition) {
		this.budgetedPosition = budgetedPosition;
	}


	/**
	 * Gets the standardHoursDefault attribute.
	 * 
	 * @return Returns the standardHoursDefault
	 * 
	 */
	public BigDecimal getStandardHoursDefault() { 
		return standardHoursDefault;
	}

	/**
	 * Sets the standardHoursDefault attribute.
	 * 
	 * @param standardHoursDefault The standardHoursDefault to set.
	 * 
	 */
	public void setStandardHoursDefault(BigDecimal standardHoursDefault) {
		this.standardHoursDefault = standardHoursDefault;
	}


	/**
	 * Gets the standardHoursFrequency attribute.
	 * 
	 * @return Returns the standardHoursFrequency
	 * 
	 */
	public String getStandardHoursFrequency() { 
		return standardHoursFrequency;
	}

	/**
	 * Sets the standardHoursFrequency attribute.
	 * 
	 * @param standardHoursFrequency The standardHoursFrequency to set.
	 * 
	 */
	public void setStandardHoursFrequency(String standardHoursFrequency) {
		this.standardHoursFrequency = standardHoursFrequency;
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
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.positionIdentifierSequence != null) {
            m.put("positionIdentifierSequence", this.positionIdentifierSequence.toString());
        }
	    return m;
    }
}
