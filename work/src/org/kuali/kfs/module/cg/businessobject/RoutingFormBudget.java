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
