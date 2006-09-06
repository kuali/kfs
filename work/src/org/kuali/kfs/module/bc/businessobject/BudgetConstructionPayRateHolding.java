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

package org.kuali.module.budget.bo;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
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
	 * @return - Returns the emplid
	 * 
	 */
	public String getEmplid() { 
		return emplid;
	}

	/**
	 * Sets the emplid attribute.
	 * 
	 * @param - emplid The emplid to set.
	 * 
	 */
	public void setEmplid(String emplid) {
		this.emplid = emplid;
	}


	/**
	 * Gets the positionNumber attribute.
	 * 
	 * @return - Returns the positionNumber
	 * 
	 */
	public String getPositionNumber() { 
		return positionNumber;
	}

	/**
	 * Sets the positionNumber attribute.
	 * 
	 * @param - positionNumber The positionNumber to set.
	 * 
	 */
	public void setPositionNumber(String positionNumber) {
		this.positionNumber = positionNumber;
	}


	/**
	 * Gets the personName attribute.
	 * 
	 * @return - Returns the personName
	 * 
	 */
	public String getPersonName() { 
		return personName;
	}

	/**
	 * Sets the personName attribute.
	 * 
	 * @param - personName The personName to set.
	 * 
	 */
	public void setPersonName(String personName) {
		this.personName = personName;
	}


	/**
	 * Gets the setidSalary attribute.
	 * 
	 * @return - Returns the setidSalary
	 * 
	 */
	public String getSetidSalary() { 
		return setidSalary;
	}

	/**
	 * Sets the setidSalary attribute.
	 * 
	 * @param - setidSalary The setidSalary to set.
	 * 
	 */
	public void setSetidSalary(String setidSalary) {
		this.setidSalary = setidSalary;
	}


	/**
	 * Gets the salaryAdministrationPlan attribute.
	 * 
	 * @return - Returns the salaryAdministrationPlan
	 * 
	 */
	public String getSalaryAdministrationPlan() { 
		return salaryAdministrationPlan;
	}

	/**
	 * Sets the salaryAdministrationPlan attribute.
	 * 
	 * @param - salaryAdministrationPlan The salaryAdministrationPlan to set.
	 * 
	 */
	public void setSalaryAdministrationPlan(String salaryAdministrationPlan) {
		this.salaryAdministrationPlan = salaryAdministrationPlan;
	}


	/**
	 * Gets the grade attribute.
	 * 
	 * @return - Returns the grade
	 * 
	 */
	public String getGrade() { 
		return grade;
	}

	/**
	 * Sets the grade attribute.
	 * 
	 * @param - grade The grade to set.
	 * 
	 */
	public void setGrade(String grade) {
		this.grade = grade;
	}


	/**
	 * Gets the unionCode attribute.
	 * 
	 * @return - Returns the unionCode
	 * 
	 */
	public String getUnionCode() { 
		return unionCode;
	}

	/**
	 * Sets the unionCode attribute.
	 * 
	 * @param - unionCode The unionCode to set.
	 * 
	 */
	public void setUnionCode(String unionCode) {
		this.unionCode = unionCode;
	}


	/**
	 * Gets the appointmentRequestedPayRate attribute.
	 * 
	 * @return - Returns the appointmentRequestedPayRate
	 * 
	 */
	public BigDecimal getAppointmentRequestedPayRate() { 
		return appointmentRequestedPayRate;
	}

	/**
	 * Sets the appointmentRequestedPayRate attribute.
	 * 
	 * @param - appointmentRequestedPayRate The appointmentRequestedPayRate to set.
	 * 
	 */
	public void setAppointmentRequestedPayRate(BigDecimal appointmentRequestedPayRate) {
		this.appointmentRequestedPayRate = appointmentRequestedPayRate;
	}


	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("emplid", this.emplid);
        m.put("positionNumber", this.positionNumber);
	    return m;
    }
}
