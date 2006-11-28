/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/bc/businessobject/CalculatedSalaryFoundationEarnCode.java,v $
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

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class CalculatedSalaryFoundationEarnCode extends BusinessObjectBase {

	private String payGroup;
	private Date effectiveDate;
	private String hrmsCompany;
	private String earnCodeRegularEarnings;
	private String description;
	private String csfEligibleFlag;

    private CalculatedSalaryFoundationCompany company;
    
	/**
	 * Default constructor.
	 */
	public CalculatedSalaryFoundationEarnCode() {

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
	 * Gets the hrmsCompany attribute.
	 * 
	 * @return Returns the hrmsCompany
	 * 
	 */
	public String getHrmsCompany() { 
		return hrmsCompany;
	}

	/**
	 * Sets the hrmsCompany attribute.
	 * 
	 * @param hrmsCompany The hrmsCompany to set.
	 * 
	 */
	public void setHrmsCompany(String hrmsCompany) {
		this.hrmsCompany = hrmsCompany;
	}


	/**
	 * Gets the earnCodeRegularEarnings attribute.
	 * 
	 * @return Returns the earnCodeRegularEarnings
	 * 
	 */
	public String getEarnCodeRegularEarnings() { 
		return earnCodeRegularEarnings;
	}

	/**
	 * Sets the earnCodeRegularEarnings attribute.
	 * 
	 * @param earnCodeRegularEarnings The earnCodeRegularEarnings to set.
	 * 
	 */
	public void setEarnCodeRegularEarnings(String earnCodeRegularEarnings) {
		this.earnCodeRegularEarnings = earnCodeRegularEarnings;
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
	 * Gets the csfEligibleFlag attribute.
	 * 
	 * @return Returns the csfEligibleFlag
	 * 
	 */
	public String getCsfEligibleFlag() { 
		return csfEligibleFlag;
	}

	/**
	 * Sets the csfEligibleFlag attribute.
	 * 
	 * @param csfEligibleFlag The csfEligibleFlag to set.
	 * 
	 */
	public void setCsfEligibleFlag(String csfEligibleFlag) {
		this.csfEligibleFlag = csfEligibleFlag;
	}

    /**
     * Gets the company attribute. 
     * @return Returns the company.
     */
    public CalculatedSalaryFoundationCompany getCompany() {
        return company;
    }

    /**
     * Sets the company attribute value.
     * @param company The company to set.
     * @deprecated
     */
    public void setCompany(CalculatedSalaryFoundationCompany company) {
        this.company = company;
    }   
    
	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("payGroup", this.payGroup);
        if (this.effectiveDate != null) {
            m.put("effectiveDate", this.effectiveDate.toString());
        }
        m.put("hrmsCompany", this.hrmsCompany);
	    return m;
    }
}
