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

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetConstructionActiveJobMove extends BusinessObjectBase {

	private String personUniversalIdentifier;
	private String emplid;
	private Integer employeeRecord;
	private Date effectiveDate;
	private Integer effectiveSequence;
	private String positionNumber;
	private String employeeStatus;
	private String departmentIdentifier;

	/**
	 * Default constructor.
	 */
	public BudgetConstructionActiveJobMove() {

	}

	/**
	 * Gets the personUniversalIdentifier attribute.
	 * 
	 * @return - Returns the personUniversalIdentifier
	 * 
	 */
	public String getPersonUniversalIdentifier() { 
		return personUniversalIdentifier;
	}

	/**
	 * Sets the personUniversalIdentifier attribute.
	 * 
	 * @param - personUniversalIdentifier The personUniversalIdentifier to set.
	 * 
	 */
	public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
		this.personUniversalIdentifier = personUniversalIdentifier;
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
	 * Gets the employeeRecord attribute.
	 * 
	 * @return - Returns the employeeRecord
	 * 
	 */
	public Integer getEmployeeRecord() { 
		return employeeRecord;
	}

	/**
	 * Sets the employeeRecord attribute.
	 * 
	 * @param - employeeRecord The employeeRecord to set.
	 * 
	 */
	public void setEmployeeRecord(Integer employeeRecord) {
		this.employeeRecord = employeeRecord;
	}


	/**
	 * Gets the effectiveDate attribute.
	 * 
	 * @return - Returns the effectiveDate
	 * 
	 */
	public Date getEffectiveDate() { 
		return effectiveDate;
	}

	/**
	 * Sets the effectiveDate attribute.
	 * 
	 * @param - effectiveDate The effectiveDate to set.
	 * 
	 */
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}


	/**
	 * Gets the effectiveSequence attribute.
	 * 
	 * @return - Returns the effectiveSequence
	 * 
	 */
	public Integer getEffectiveSequence() { 
		return effectiveSequence;
	}

	/**
	 * Sets the effectiveSequence attribute.
	 * 
	 * @param - effectiveSequence The effectiveSequence to set.
	 * 
	 */
	public void setEffectiveSequence(Integer effectiveSequence) {
		this.effectiveSequence = effectiveSequence;
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
	 * Gets the employeeStatus attribute.
	 * 
	 * @return - Returns the employeeStatus
	 * 
	 */
	public String getEmployeeStatus() { 
		return employeeStatus;
	}

	/**
	 * Sets the employeeStatus attribute.
	 * 
	 * @param - employeeStatus The employeeStatus to set.
	 * 
	 */
	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
	}


	/**
	 * Gets the departmentIdentifier attribute.
	 * 
	 * @return - Returns the departmentIdentifier
	 * 
	 */
	public String getDepartmentIdentifier() { 
		return departmentIdentifier;
	}

	/**
	 * Sets the departmentIdentifier attribute.
	 * 
	 * @param - departmentIdentifier The departmentIdentifier to set.
	 * 
	 */
	public void setDepartmentIdentifier(String departmentIdentifier) {
		this.departmentIdentifier = departmentIdentifier;
	}


	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("personUniversalIdentifier", this.personUniversalIdentifier);
        m.put("emplid", this.emplid);
        if (this.employeeRecord != null) {
            m.put("employeeRecord", this.employeeRecord.toString());
        }
        if (this.effectiveDate != null) {
            m.put("effectiveDate", this.effectiveDate.toString());
        }
        if (this.effectiveSequence != null) {
            m.put("effectiveSequence", this.effectiveSequence.toString());
        }
	    return m;
    }
}
