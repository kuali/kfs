/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/bc/businessobject/BudgetConstructionPayRateHolding.java,v $
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
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class BudgetConstructionPayRateHolding extends BusinessObjectBase {

	private String emplid;
	private String positionNumber;
	private String personName;
	private String setidSalary;
	private String salaryAdministrationPlan;
	private String grade;
	private String unionCode;
	private BigDecimal appointmentRequestedPayRate;

	/**
	 * Default constructor.
	 */
	public BudgetConstructionPayRateHolding() {

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
	 * Gets the unionCode attribute.
	 * 
	 * @return Returns the unionCode
	 * 
	 */
	public String getUnionCode() { 
		return unionCode;
	}

	/**
	 * Sets the unionCode attribute.
	 * 
	 * @param unionCode The unionCode to set.
	 * 
	 */
	public void setUnionCode(String unionCode) {
		this.unionCode = unionCode;
	}


	/**
	 * Gets the appointmentRequestedPayRate attribute.
	 * 
	 * @return Returns the appointmentRequestedPayRate
	 * 
	 */
	public BigDecimal getAppointmentRequestedPayRate() { 
		return appointmentRequestedPayRate;
	}

	/**
	 * Sets the appointmentRequestedPayRate attribute.
	 * 
	 * @param appointmentRequestedPayRate The appointmentRequestedPayRate to set.
	 * 
	 */
	public void setAppointmentRequestedPayRate(BigDecimal appointmentRequestedPayRate) {
		this.appointmentRequestedPayRate = appointmentRequestedPayRate;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("emplid", this.emplid);
        m.put("positionNumber", this.positionNumber);
	    return m;
    }
}
