/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/RoutingFormStatus.java,v $
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
public class RoutingFormStatus extends BusinessObjectBase {

	private String proposalStatusCode;
	private boolean dataObjectMaintenanceCodeActiveIndicator;
	private String proposalStatusDescription;

	/**
	 * Default constructor.
	 */
	public RoutingFormStatus() {

	}

	/**
	 * Gets the proposalStatusCode attribute.
	 * 
	 * @return - Returns the proposalStatusCode
	 * 
	 */
	public String getProposalStatusCode() { 
		return proposalStatusCode;
	}

	/**
	 * Sets the proposalStatusCode attribute.
	 * 
	 * @param - proposalStatusCode The proposalStatusCode to set.
	 * 
	 */
	public void setProposalStatusCode(String proposalStatusCode) {
		this.proposalStatusCode = proposalStatusCode;
	}


	/**
	 * Gets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @return - Returns the dataObjectMaintenanceCodeActiveIndicator
	 * 
	 */
	public boolean getDataObjectMaintenanceCodeActiveIndicator() { 
		return dataObjectMaintenanceCodeActiveIndicator;
	}

	/**
	 * Sets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @param - dataObjectMaintenanceCodeActiveIndicator The dataObjectMaintenanceCodeActiveIndicator to set.
	 * 
	 */
	public void setDataObjectMaintenanceCodeActiveIndicator(boolean dataObjectMaintenanceCodeActiveIndicator) {
		this.dataObjectMaintenanceCodeActiveIndicator = dataObjectMaintenanceCodeActiveIndicator;
	}


	/**
	 * Gets the proposalStatusDescription attribute.
	 * 
	 * @return - Returns the proposalStatusDescription
	 * 
	 */
	public String getProposalStatusDescription() { 
		return proposalStatusDescription;
	}

	/**
	 * Sets the proposalStatusDescription attribute.
	 * 
	 * @param - proposalStatusDescription The proposalStatusDescription to set.
	 * 
	 */
	public void setProposalStatusDescription(String proposalStatusDescription) {
		this.proposalStatusDescription = proposalStatusDescription;
	}


	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("proposalStatusCode", this.proposalStatusCode);
	    return m;
    }
}
