/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/RoutingFormPurpose.java,v $
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
public class RoutingFormPurpose extends BusinessObjectBase {

	private String proposalPurposeCode;
	private String dataObjectMaintenanceCodeActiveIndicator;
	private String proposalPurposeDescription;

	/**
	 * Default constructor.
	 */
	public RoutingFormPurpose() {

	}

	/**
	 * Gets the proposalPurposeCode attribute.
	 * 
	 * @return - Returns the proposalPurposeCode
	 * 
	 */
	public String getProposalPurposeCode() { 
		return proposalPurposeCode;
	}

	/**
	 * Sets the proposalPurposeCode attribute.
	 * 
	 * @param - proposalPurposeCode The proposalPurposeCode to set.
	 * 
	 */
	public void setProposalPurposeCode(String proposalPurposeCode) {
		this.proposalPurposeCode = proposalPurposeCode;
	}


	/**
	 * Gets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @return - Returns the dataObjectMaintenanceCodeActiveIndicator
	 * 
	 */
	public String getDataObjectMaintenanceCodeActiveIndicator() { 
		return dataObjectMaintenanceCodeActiveIndicator;
	}

	/**
	 * Sets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @param - dataObjectMaintenanceCodeActiveIndicator The dataObjectMaintenanceCodeActiveIndicator to set.
	 * 
	 */
	public void setDataObjectMaintenanceCodeActiveIndicator(String dataObjectMaintenanceCodeActiveIndicator) {
		this.dataObjectMaintenanceCodeActiveIndicator = dataObjectMaintenanceCodeActiveIndicator;
	}


	/**
	 * Gets the proposalPurposeDescription attribute.
	 * 
	 * @return - Returns the proposalPurposeDescription
	 * 
	 */
	public String getProposalPurposeDescription() { 
		return proposalPurposeDescription;
	}

	/**
	 * Sets the proposalPurposeDescription attribute.
	 * 
	 * @param - proposalPurposeDescription The proposalPurposeDescription to set.
	 * 
	 */
	public void setProposalPurposeDescription(String proposalPurposeDescription) {
		this.proposalPurposeDescription = proposalPurposeDescription;
	}


	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("proposalPurposeCode", this.proposalPurposeCode);
	    return m;
    }
}
