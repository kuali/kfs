/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/RoutingFormOtherCostShare.java,v $
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
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class RoutingFormOtherCostShare extends BusinessObjectBase {

	private Integer proposalCostShareSequenceNumber;
	private String researchDocumentNumber;
	private String proposalCostShareSourceName;
	private BigDecimal proposalCostShareAmount;

	/**
	 * Default constructor.
	 */
	public RoutingFormOtherCostShare() {

	}

	/**
	 * Gets the proposalCostShareSequenceNumber attribute.
	 * 
	 * @return - Returns the proposalCostShareSequenceNumber
	 * 
	 */
	public Integer getProposalCostShareSequenceNumber() { 
		return proposalCostShareSequenceNumber;
	}

	/**
	 * Sets the proposalCostShareSequenceNumber attribute.
	 * 
	 * @param - proposalCostShareSequenceNumber The proposalCostShareSequenceNumber to set.
	 * 
	 */
	public void setProposalCostShareSequenceNumber(Integer proposalCostShareSequenceNumber) {
		this.proposalCostShareSequenceNumber = proposalCostShareSequenceNumber;
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
	 * Gets the proposalCostShareSourceName attribute.
	 * 
	 * @return - Returns the proposalCostShareSourceName
	 * 
	 */
	public String getProposalCostShareSourceName() { 
		return proposalCostShareSourceName;
	}

	/**
	 * Sets the proposalCostShareSourceName attribute.
	 * 
	 * @param - proposalCostShareSourceName The proposalCostShareSourceName to set.
	 * 
	 */
	public void setProposalCostShareSourceName(String proposalCostShareSourceName) {
		this.proposalCostShareSourceName = proposalCostShareSourceName;
	}

	/**
	 * Gets the proposalCostShareAmount attribute.
	 * 
	 * @return - Returns the proposalCostShareAmount
	 * 
	 */
	public BigDecimal getProposalCostShareAmount() { 
		return proposalCostShareAmount;
	}

	/**
	 * Sets the proposalCostShareAmount attribute.
	 * 
	 * @param - proposalCostShareAmount The proposalCostShareAmount to set.
	 * 
	 */
	public void setProposalCostShareAmount(BigDecimal proposalCostShareAmount) {
		this.proposalCostShareAmount = proposalCostShareAmount;
	}


	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.proposalCostShareSequenceNumber != null) {
            m.put("proposalCostShareSequenceNumber", this.proposalCostShareSequenceNumber.toString());
        }
        m.put("researchDocumentNumber", this.researchDocumentNumber);
	    return m;
    }
}
