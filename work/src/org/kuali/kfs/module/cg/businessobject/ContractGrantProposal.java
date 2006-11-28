/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/ContractGrantProposal.java,v $
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

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.PropertyConstants;

/**
 * 
 */
public class ContractGrantProposal extends BusinessObjectBase {

	private String documentNumber;
	private Long proposalNumber;
	private Date proposalCreateDate;

	/**
	 * Default constructor.
	 */
	public ContractGrantProposal() {

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
	 * Gets the proposalNumber attribute.
	 * 
	 * @return Returns the proposalNumber
	 * 
	 */
	public Long getProposalNumber() { 
		return proposalNumber;
	}

	/**
	 * Sets the proposalNumber attribute.
	 * 
	 * @param proposalNumber The proposalNumber to set.
	 * 
	 */
	public void setProposalNumber(Long proposalNumber) {
		this.proposalNumber = proposalNumber;
	}


	/**
	 * Gets the proposalCreateDate attribute.
	 * 
	 * @return Returns the proposalCreateDate
	 * 
	 */
	public Date getProposalCreateDate() { 
		return proposalCreateDate;
	}

	/**
	 * Sets the proposalCreateDate attribute.
	 * 
	 * @param proposalCreateDate The proposalCreateDate to set.
	 * 
	 */
	public void setProposalCreateDate(Date proposalCreateDate) {
		this.proposalCreateDate = proposalCreateDate;
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
