/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/RoutingFormDueDateType.java,v $
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
 * 
 */
public class RoutingFormDueDateType extends BusinessObjectBase {

	private String proposalDueDateTypeCode;
	private boolean dataObjectMaintenanceCodeActiveIndicator;
	private Integer proposalApprovalLeadTime;
	private String proposalDueDateDescription;

	/**
	 * Default constructor.
	 */
	public RoutingFormDueDateType() {

	}

	/**
	 * Gets the proposalDueDateTypeCode attribute.
	 * 
	 * @return - Returns the proposalDueDateTypeCode
	 * 
	 */
	public String getProposalDueDateTypeCode() { 
		return proposalDueDateTypeCode;
	}

	/**
	 * Sets the proposalDueDateTypeCode attribute.
	 * 
	 * @param - proposalDueDateTypeCode The proposalDueDateTypeCode to set.
	 * 
	 */
	public void setProposalDueDateTypeCode(String proposalDueDateTypeCode) {
		this.proposalDueDateTypeCode = proposalDueDateTypeCode;
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
	 * Gets the proposalApprovalLeadTime attribute.
	 * 
	 * @return - Returns the proposalApprovalLeadTime
	 * 
	 */
	public Integer getProposalApprovalLeadTime() { 
		return proposalApprovalLeadTime;
	}

	/**
	 * Sets the proposalApprovalLeadTime attribute.
	 * 
	 * @param - proposalApprovalLeadTime The proposalApprovalLeadTime to set.
	 * 
	 */
	public void setProposalApprovalLeadTime(Integer proposalApprovalLeadTime) {
		this.proposalApprovalLeadTime = proposalApprovalLeadTime;
	}


	/**
	 * Gets the proposalDueDateDescription attribute.
	 * 
	 * @return - Returns the proposalDueDateDescription
	 * 
	 */
	public String getProposalDueDateDescription() { 
		return proposalDueDateDescription;
	}

	/**
	 * Sets the proposalDueDateDescription attribute.
	 * 
	 * @param - proposalDueDateDescription The proposalDueDateDescription to set.
	 * 
	 */
	public void setProposalDueDateDescription(String proposalDueDateDescription) {
		this.proposalDueDateDescription = proposalDueDateDescription;
	}


	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("proposalDueDateTypeCode", this.proposalDueDateTypeCode);
	    return m;
    }
}
