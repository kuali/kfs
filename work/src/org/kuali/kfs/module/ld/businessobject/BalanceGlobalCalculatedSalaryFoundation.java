
/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/ld/businessobject/BalanceGlobalCalculatedSalaryFoundation.java,v $
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

package org.kuali.module.labor.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.ObjectCode;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BalanceGlobalCalculatedSalaryFoundation extends BusinessObjectBase {

	private String personUniversalIdentifier;
	private String subAccountNumber;
	private String financialObjectCode;
	private String financialSubObjectCode;
	private KualiDecimal budgetAmount;
	private KualiDecimal calculatedSalaryFoundationAmount;

	/**
	 * Default constructor.
	 */
	public BalanceGlobalCalculatedSalaryFoundation() {

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
	 * Gets the subAccountNumber attribute.
	 * 
	 * @return Returns the subAccountNumber
	 * 
	 */
	public String getSubAccountNumber() { 
		return subAccountNumber;
	}

	/**
	 * Sets the subAccountNumber attribute.
	 * 
	 * @param subAccountNumber The subAccountNumber to set.
	 * 
	 */
	public void setSubAccountNumber(String subAccountNumber) {
		this.subAccountNumber = subAccountNumber;
	}


	/**
	 * Gets the financialObjectCode attribute.
	 * 
	 * @return Returns the financialObjectCode
	 * 
	 */
	public String getFinancialObjectCode() { 
		return financialObjectCode;
	}

	/**
	 * Sets the financialObjectCode attribute.
	 * 
	 * @param financialObjectCode The financialObjectCode to set.
	 * 
	 */
	public void setFinancialObjectCode(String financialObjectCode) {
		this.financialObjectCode = financialObjectCode;
	}


	/**
	 * Gets the financialSubObjectCode attribute.
	 * 
	 * @return Returns the financialSubObjectCode
	 * 
	 */
	public String getFinancialSubObjectCode() { 
		return financialSubObjectCode;
	}

	/**
	 * Sets the financialSubObjectCode attribute.
	 * 
	 * @param financialSubObjectCode The financialSubObjectCode to set.
	 * 
	 */
	public void setFinancialSubObjectCode(String financialSubObjectCode) {
		this.financialSubObjectCode = financialSubObjectCode;
	}


	/**
	 * Gets the budgetAmount attribute.
	 * 
	 * @return Returns the budgetAmount
	 * 
	 */
	public KualiDecimal getBudgetAmount() { 
		return budgetAmount;
	}

	/**
	 * Sets the budgetAmount attribute.
	 * 
	 * @param budgetAmount The budgetAmount to set.
	 * 
	 */
	public void setBudgetAmount(KualiDecimal budgetAmount) {
		this.budgetAmount = budgetAmount;
	}


	/**
	 * Gets the calculatedSalaryFoundationAmount attribute.
	 * 
	 * @return Returns the calculatedSalaryFoundationAmount
	 * 
	 */
	public KualiDecimal getCalculatedSalaryFoundationAmount() { 
		return calculatedSalaryFoundationAmount;
	}

	/**
	 * Sets the calculatedSalaryFoundationAmount attribute.
	 * 
	 * @param calculatedSalaryFoundationAmount The calculatedSalaryFoundationAmount to set.
	 * 
	 */
	public void setCalculatedSalaryFoundationAmount(KualiDecimal calculatedSalaryFoundationAmount) {
		this.calculatedSalaryFoundationAmount = calculatedSalaryFoundationAmount;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("personUniversalIdentifier", this.personUniversalIdentifier);
        m.put("subAccountNumber", this.subAccountNumber);
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("financialSubObjectCode", this.financialSubObjectCode);
	    return m;
    }
}
