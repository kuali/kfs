/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/RoutingFormBudget.java,v $
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

package org.kuali.module.kra.routingform.bo;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.PropertyConstants;

/**
 * 
 */
public class RoutingFormBudget extends BusinessObjectBase {

	private String documentNumber;
	private Integer routingFormBudgetMaximumPeriodNumber;
	private Integer routingFormBudgetMinimumPeriodNumber;
	private BigDecimal routingFormBudgetDirectAmount;
	private Date routingFormBudgetEndDate;
	private BigDecimal routingFormBudgetIndirectCostAmount;
	private String routingFormBudgetIndirectCostDescription;
	private Date routingFormBudgetStartDate;

	/**
	 * Default constructor.
	 */
	public RoutingFormBudget() {

	}

	/**
	 * Gets the documentNumber attribute.
	 * 
	 * @return Returns the documentNumber
	 * 
	 */
	public String getDocumentNumber() { 
		return documentNumber;
	}

	/**
	 * Sets the documentNumber attribute.
	 * 
	 * @param documentNumber The documentNumber to set.
	 * 
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}


	/**
	 * Gets the routingFormBudgetMaximumPeriodNumber attribute.
	 * 
	 * @return Returns the routingFormBudgetMaximumPeriodNumber
	 * 
	 */
	public Integer getRoutingFormBudgetMaximumPeriodNumber() { 
		return routingFormBudgetMaximumPeriodNumber;
	}

	/**
	 * Sets the routingFormBudgetMaximumPeriodNumber attribute.
	 * 
	 * @param routingFormBudgetMaximumPeriodNumber The routingFormBudgetMaximumPeriodNumber to set.
	 * 
	 */
	public void setRoutingFormBudgetMaximumPeriodNumber(Integer routingFormBudgetMaximumPeriodNumber) {
		this.routingFormBudgetMaximumPeriodNumber = routingFormBudgetMaximumPeriodNumber;
	}


	/**
	 * Gets the routingFormBudgetMinimumPeriodNumber attribute.
	 * 
	 * @return Returns the routingFormBudgetMinimumPeriodNumber
	 * 
	 */
	public Integer getRoutingFormBudgetMinimumPeriodNumber() { 
		return routingFormBudgetMinimumPeriodNumber;
	}

	/**
	 * Sets the routingFormBudgetMinimumPeriodNumber attribute.
	 * 
	 * @param routingFormBudgetMinimumPeriodNumber The routingFormBudgetMinimumPeriodNumber to set.
	 * 
	 */
	public void setRoutingFormBudgetMinimumPeriodNumber(Integer routingFormBudgetMinimumPeriodNumber) {
		this.routingFormBudgetMinimumPeriodNumber = routingFormBudgetMinimumPeriodNumber;
	}


	/**
	 * Gets the routingFormBudgetDirectAmount attribute.
	 * 
	 * @return Returns the routingFormBudgetDirectAmount
	 * 
	 */
	public BigDecimal getRoutingFormBudgetDirectAmount() { 
		return routingFormBudgetDirectAmount;
	}

	/**
	 * Sets the routingFormBudgetDirectAmount attribute.
	 * 
	 * @param routingFormBudgetDirectAmount The routingFormBudgetDirectAmount to set.
	 * 
	 */
	public void setRoutingFormBudgetDirectAmount(BigDecimal routingFormBudgetDirectAmount) {
		this.routingFormBudgetDirectAmount = routingFormBudgetDirectAmount;
	}


	/**
	 * Gets the routingFormBudgetEndDate attribute.
	 * 
	 * @return Returns the routingFormBudgetEndDate
	 * 
	 */
	public Date getRoutingFormBudgetEndDate() { 
		return routingFormBudgetEndDate;
	}

	/**
	 * Sets the routingFormBudgetEndDate attribute.
	 * 
	 * @param routingFormBudgetEndDate The routingFormBudgetEndDate to set.
	 * 
	 */
	public void setRoutingFormBudgetEndDate(Date routingFormBudgetEndDate) {
		this.routingFormBudgetEndDate = routingFormBudgetEndDate;
	}


	/**
	 * Gets the routingFormBudgetIndirectCostAmount attribute.
	 * 
	 * @return Returns the routingFormBudgetIndirectCostAmount
	 * 
	 */
	public BigDecimal getRoutingFormBudgetIndirectCostAmount() { 
		return routingFormBudgetIndirectCostAmount;
	}

	/**
	 * Sets the routingFormBudgetIndirectCostAmount attribute.
	 * 
	 * @param routingFormBudgetIndirectCostAmount The routingFormBudgetIndirectCostAmount to set.
	 * 
	 */
	public void setRoutingFormBudgetIndirectCostAmount(BigDecimal routingFormBudgetIndirectCostAmount) {
		this.routingFormBudgetIndirectCostAmount = routingFormBudgetIndirectCostAmount;
	}


	/**
	 * Gets the routingFormBudgetIndirectCostDescription attribute.
	 * 
	 * @return Returns the routingFormBudgetIndirectCostDescription
	 * 
	 */
	public String getRoutingFormBudgetIndirectCostDescription() { 
		return routingFormBudgetIndirectCostDescription;
	}

	/**
	 * Sets the routingFormBudgetIndirectCostDescription attribute.
	 * 
	 * @param routingFormBudgetIndirectCostDescription The routingFormBudgetIndirectCostDescription to set.
	 * 
	 */
	public void setRoutingFormBudgetIndirectCostDescription(String routingFormBudgetIndirectCostDescription) {
		this.routingFormBudgetIndirectCostDescription = routingFormBudgetIndirectCostDescription;
	}


	/**
	 * Gets the routingFormBudgetStartDate attribute.
	 * 
	 * @return Returns the routingFormBudgetStartDate
	 * 
	 */
	public Date getRoutingFormBudgetStartDate() { 
		return routingFormBudgetStartDate;
	}

	/**
	 * Sets the routingFormBudgetStartDate attribute.
	 * 
	 * @param routingFormBudgetStartDate The routingFormBudgetStartDate to set.
	 * 
	 */
	public void setRoutingFormBudgetStartDate(Date routingFormBudgetStartDate) {
		this.routingFormBudgetStartDate = routingFormBudgetStartDate;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
	    return m;
    }
}
