/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/RoutingFormConflictOfInterest.java,v $
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

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class RoutingFormConflictOfInterest extends BusinessObjectBase {

	private String researchDocumentNumber;
	private String proposalConflictOfInterestDescription;

	/**
	 * Default constructor.
	 */
	public RoutingFormConflictOfInterest() {

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
	 * Gets the proposalConflictOfInterestDescription attribute.
	 * 
	 * @return - Returns the proposalConflictOfInterestDescription
	 * 
	 */
	public String getProposalConflictOfInterestDescription() { 
		return proposalConflictOfInterestDescription;
	}

	/**
	 * Sets the proposalConflictOfInterestDescription attribute.
	 * 
	 * @param - proposalConflictOfInterestDescription The proposalConflictOfInterestDescription to set.
	 * 
	 */
	public void setProposalConflictOfInterestDescription(String proposalConflictOfInterestDescription) {
		this.proposalConflictOfInterestDescription = proposalConflictOfInterestDescription;
	}


	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("researchDocumentNumber", this.researchDocumentNumber);
	    return m;
    }
}
