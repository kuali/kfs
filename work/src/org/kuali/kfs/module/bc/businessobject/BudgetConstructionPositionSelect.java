/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/bc/businessobject/BudgetConstructionPositionSelect.java,v $
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

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

public class BudgetConstructionPositionSelect extends BusinessObjectBase {

	private String personUniversalIdentifier;
	private String positionNumber;
	private Integer universityFiscalYear;
	private String emplid;
	private String iuPositionType;
	private String positionDepartmentIdentifier;
	private String setidSalary;
	private String salaryAdministrationPlan;
	private String grade;
	private String positionDescription;
	private String personName;
    
    private BudgetConstructionPosition budgetConstructionPosition;

	/**
	 * Default constructor.
	 */
	public BudgetConstructionPositionSelect() {

	}

	/**
	 * Gets the personUniversalIdentifier attribute.
	 * 
	 * @return Returns the personUniversalIdentifier
	 * 
	 */
	public String getPersonUniversalIdentifier() { 
		return personUniversalIdentifier;
	}

	/**
	 * Sets the personUniversalIdentifier attribute.
	 * 
	 * @param personUniversalIdentifier The personUniversalIdentifier to set.
	 * 
	 */
	public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
		this.personUniversalIdentifier = personUniversalIdentifier;
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
	 * Gets the emplid attribute.
	 * 
	 * @return Returns the emplid
	 * 
	 */
	public String getEmplid() { 
		return emplid;
	}

	/**
	 * Sets the emplid attribute.
	 * 
	 * @param emplid The emplid to set.
	 * 
	 */
	public void setEmplid(String emplid) {
		this.emplid = emplid;
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
	 * Gets the salaryAdministrationPlan attribute.
	 * 
	 * @return Returns the salaryAdministrationPlan
	 * 
	 */
	public String getSalaryAdministrationPlan() { 
		return salaryAdministrationPlan;
	}

	/**
	 * Sets the salaryAdministrationPlan attribute.
	 * 
	 * @param salaryAdministrationPlan The salaryAdministrationPlan to set.
	 * 
	 */
	public void setSalaryAdministrationPlan(String salaryAdministrationPlan) {
		this.salaryAdministrationPlan = salaryAdministrationPlan;
	}


	/**
	 * Gets the grade attribute.
	 * 
	 * @return Returns the grade
	 * 
	 */
	public String getGrade() { 
		return grade;
	}

	/**
	 * Sets the grade attribute.
	 * 
	 * @param grade The grade to set.
	 * 
	 */
	public void setGrade(String grade) {
		this.grade = grade;
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
	 * Gets the personName attribute.
	 * 
	 * @return Returns the personName
	 * 
	 */
	public String getPersonName() { 
		return personName;
	}

	/**
	 * Sets the personName attribute.
	 * 
	 * @param personName The personName to set.
	 * 
	 */
	public void setPersonName(String personName) {
		this.personName = personName;
	}


    /**
     * Gets the budgetConstructionPosition attribute.
     * 
     * @return Returns the budgetConstructionPosition
     * 
     */
    public BudgetConstructionPosition getBudgetConstructionPosition() { 
        return budgetConstructionPosition;
    }

    /**
     * Sets the budgetConstructionPosition attribute.
     * 
     * @param budgetConstructionPosition The budgetConstructionPosition to set.
     * @deprecated
     */
    public void setBudgetConstructionPosition(BudgetConstructionPosition budgetConstructionPosition) {
        this.budgetConstructionPosition = budgetConstructionPosition;
    }

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("personUniversalIdentifier", this.personUniversalIdentifier);
        m.put("positionNumber", this.positionNumber);
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("emplid", this.emplid);
	    return m;
    }
}
