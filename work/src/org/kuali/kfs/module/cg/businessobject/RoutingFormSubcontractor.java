/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/RoutingFormSubcontractor.java,v $
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
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class RoutingFormSubcontractor extends BusinessObjectBase {

	private String researchDocumentNumber;
	private Integer proposalSubcontractorSequenceNumber;
	private BigDecimal proposalSubcontractorAmount;
	private String proposalSubcontractorNumber;
	private String proposalSubcontractorName;

	/**
	 * Default constructor.
	 */
	public RoutingFormSubcontractor() {

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
	 * Gets the proposalSubcontractorSequenceNumber attribute.
	 * 
	 * @return - Returns the proposalSubcontractorSequenceNumber
	 * 
	 */
	public Integer getProposalSubcontractorSequenceNumber() { 
		return proposalSubcontractorSequenceNumber;
	}

	/**
	 * Sets the proposalSubcontractorSequenceNumber attribute.
	 * 
	 * @param - proposalSubcontractorSequenceNumber The proposalSubcontractorSequenceNumber to set.
	 * 
	 */
	public void setProposalSubcontractorSequenceNumber(Integer proposalSubcontractorSequenceNumber) {
		this.proposalSubcontractorSequenceNumber = proposalSubcontractorSequenceNumber;
	}


	/**
	 * Gets the proposalSubcontractorAmount attribute.
	 * 
	 * @return - Returns the proposalSubcontractorAmount
	 * 
	 */
	public BigDecimal getProposalSubcontractorAmount() { 
		return proposalSubcontractorAmount;
	}

	/**
	 * Sets the proposalSubcontractorAmount attribute.
	 * 
	 * @param - proposalSubcontractorAmount The proposalSubcontractorAmount to set.
	 * 
	 */
	public void setProposalSubcontractorAmount(BigDecimal proposalSubcontractorAmount) {
		this.proposalSubcontractorAmount = proposalSubcontractorAmount;
	}


	/**
	 * Gets the proposalSubcontractorNumber attribute.
	 * 
	 * @return - Returns the proposalSubcontractorNumber
	 * 
	 */
	public String getProposalSubcontractorNumber() { 
		return proposalSubcontractorNumber;
	}

	/**
	 * Sets the proposalSubcontractorNumber attribute.
	 * 
	 * @param - proposalSubcontractorNumber The proposalSubcontractorNumber to set.
	 * 
	 */
	public void setProposalSubcontractorNumber(String proposalSubcontractorNumber) {
		this.proposalSubcontractorNumber = proposalSubcontractorNumber;
	}


	/**
	 * Gets the proposalSubcontractorName attribute.
	 * 
	 * @return - Returns the proposalSubcontractorName
	 * 
	 */
	public String getProposalSubcontractorName() { 
		return proposalSubcontractorName;
	}

	/**
	 * Sets the proposalSubcontractorName attribute.
	 * 
	 * @param - proposalSubcontractorName The proposalSubcontractorName to set.
	 * 
	 */
	public void setProposalSubcontractorName(String proposalSubcontractorName) {
		this.proposalSubcontractorName = proposalSubcontractorName;
	}


	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("researchDocumentNumber", this.researchDocumentNumber);
        if (this.proposalSubcontractorSequenceNumber != null) {
            m.put("proposalSubcontractorSequenceNumber", this.proposalSubcontractorSequenceNumber.toString());
        }
	    return m;
    }
}
