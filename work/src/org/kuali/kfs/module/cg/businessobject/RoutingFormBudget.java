/*
 * Copyright 2005-2006 The Kuali Foundation.
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

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class RoutingFormBudget extends BusinessObjectBase {

	private String proposalBudgetTypeCode;
	private String researchDocumentNumber;
	private Integer proposalBudgetMaximumPeriodNumber;
	private Integer proposalBudgetMinimumPeriodNumber;
	private BigDecimal proposalBudgetDirectAmount;
	private Date proposalBudgetEndDate;
	private BigDecimal proposalBudgetIndirectCostAmount;
	private String proposalBudgetIndirectCostDescription;
	private Date proposalBudgetStartDate;

	/**
	 * Default constructor.
	 */
	public RoutingFormBudget() {

	}

	/**
	 * Gets the proposalBudgetTypeCode attribute.
	 * 
	 * @return - Returns the proposalBudgetTypeCode
	 * 
	 */
	public String getProposalBudgetTypeCode() { 
		return proposalBudgetTypeCode;
	}

	/**
	 * Sets the proposalBudgetTypeCode attribute.
	 * 
	 * @param - proposalBudgetTypeCode The proposalBudgetTypeCode to set.
	 * 
	 */
	public void setProposalBudgetTypeCode(String proposalBudgetTypeCode) {
		this.proposalBudgetTypeCode = proposalBudgetTypeCode;
	}


	/**
	 * Gets the researchDocumentNumber attribute.
	 * 
	 * @return - Returns the researchDocumentNumber
	 * 
	 */
	public String getResearchDocumentNumber() { 
		return researchDocumentNumber;
	}

	/**
	 * Sets the researchDocumentNumber attribute.
	 * 
	 * @param - researchDocumentNumber The researchDocumentNumber to set.
	 * 
	 */
	public void setResearchDocumentNumber(String researchDocumentNumber) {
		this.researchDocumentNumber = researchDocumentNumber;
	}


	/**
	 * Gets the proposalBudgetMaximumPeriodNumber attribute.
	 * 
	 * @return - Returns the proposalBudgetMaximumPeriodNumber
	 * 
	 */
	public Integer getProposalBudgetMaximumPeriodNumber() { 
		return proposalBudgetMaximumPeriodNumber;
	}

	/**
	 * Sets the proposalBudgetMaximumPeriodNumber attribute.
	 * 
	 * @param - proposalBudgetMaximumPeriodNumber The proposalBudgetMaximumPeriodNumber to set.
	 * 
	 */
	public void setProposalBudgetMaximumPeriodNumber(Integer proposalBudgetMaximumPeriodNumber) {
		this.proposalBudgetMaximumPeriodNumber = proposalBudgetMaximumPeriodNumber;
	}


	/**
	 * Gets the proposalBudgetMinimumPeriodNumber attribute.
	 * 
	 * @return - Returns the proposalBudgetMinimumPeriodNumber
	 * 
	 */
	public Integer getProposalBudgetMinimumPeriodNumber() { 
		return proposalBudgetMinimumPeriodNumber;
	}

	/**
	 * Sets the proposalBudgetMinimumPeriodNumber attribute.
	 * 
	 * @param - proposalBudgetMinimumPeriodNumber The proposalBudgetMinimumPeriodNumber to set.
	 * 
	 */
	public void setProposalBudgetMinimumPeriodNumber(Integer proposalBudgetMinimumPeriodNumber) {
		this.proposalBudgetMinimumPeriodNumber = proposalBudgetMinimumPeriodNumber;
	}


	/**
	 * Gets the proposalBudgetDirectAmount attribute.
	 * 
	 * @return - Returns the proposalBudgetDirectAmount
	 * 
	 */
	public BigDecimal getProposalBudgetDirectAmount() { 
		return proposalBudgetDirectAmount;
	}

	/**
	 * Sets the proposalBudgetDirectAmount attribute.
	 * 
	 * @param - proposalBudgetDirectAmount The proposalBudgetDirectAmount to set.
	 * 
	 */
	public void setProposalBudgetDirectAmount(BigDecimal proposalBudgetDirectAmount) {
		this.proposalBudgetDirectAmount = proposalBudgetDirectAmount;
	}


	/**
	 * Gets the proposalBudgetEndDate attribute.
	 * 
	 * @return - Returns the proposalBudgetEndDate
	 * 
	 */
	public Date getProposalBudgetEndDate() { 
		return proposalBudgetEndDate;
	}

	/**
	 * Sets the proposalBudgetEndDate attribute.
	 * 
	 * @param - proposalBudgetEndDate The proposalBudgetEndDate to set.
	 * 
	 */
	public void setProposalBudgetEndDate(Date proposalBudgetEndDate) {
		this.proposalBudgetEndDate = proposalBudgetEndDate;
	}


	/**
	 * Gets the proposalBudgetIndirectCostAmount attribute.
	 * 
	 * @return - Returns the proposalBudgetIndirectCostAmount
	 * 
	 */
	public BigDecimal getProposalBudgetIndirectCostAmount() { 
		return proposalBudgetIndirectCostAmount;
	}

	/**
	 * Sets the proposalBudgetIndirectCostAmount attribute.
	 * 
	 * @param - proposalBudgetIndirectCostAmount The proposalBudgetIndirectCostAmount to set.
	 * 
	 */
	public void setProposalBudgetIndirectCostAmount(BigDecimal proposalBudgetIndirectCostAmount) {
		this.proposalBudgetIndirectCostAmount = proposalBudgetIndirectCostAmount;
	}


	/**
	 * Gets the proposalBudgetIndirectCostDescription attribute.
	 * 
	 * @return - Returns the proposalBudgetIndirectCostDescription
	 * 
	 */
	public String getProposalBudgetIndirectCostDescription() { 
		return proposalBudgetIndirectCostDescription;
	}

	/**
	 * Sets the proposalBudgetIndirectCostDescription attribute.
	 * 
	 * @param - proposalBudgetIndirectCostDescription The proposalBudgetIndirectCostDescription to set.
	 * 
	 */
	public void setProposalBudgetIndirectCostDescription(String proposalBudgetIndirectCostDescription) {
		this.proposalBudgetIndirectCostDescription = proposalBudgetIndirectCostDescription;
	}


	/**
	 * Gets the proposalBudgetStartDate attribute.
	 * 
	 * @return - Returns the proposalBudgetStartDate
	 * 
	 */
	public Date getProposalBudgetStartDate() { 
		return proposalBudgetStartDate;
	}

	/**
	 * Sets the proposalBudgetStartDate attribute.
	 * 
	 * @param - proposalBudgetStartDate The proposalBudgetStartDate to set.
	 * 
	 */
	public void setProposalBudgetStartDate(Date proposalBudgetStartDate) {
		this.proposalBudgetStartDate = proposalBudgetStartDate;
	}


	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("proposalBudgetTypeCode", this.proposalBudgetTypeCode);
        m.put("researchDocumentNumber", this.researchDocumentNumber);
	    return m;
    }
}
