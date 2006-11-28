/*
 * Copyright 2006 The Kuali Foundation.
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

	private String routingFormDueDateTypeCode;
	private boolean dataObjectMaintenanceCodeActiveIndicator;
	private Integer routingFormApprovalLeadTime;
	private String routingFormDueDateDescription;

	/**
	 * Default constructor.
	 */
	public RoutingFormDueDateType() {

	}

	/**
	 * Gets the routingFormDueDateTypeCode attribute.
	 * 
	 * @return Returns the routingFormDueDateTypeCode
	 * 
	 */
	public String getRoutingFormDueDateTypeCode() { 
		return routingFormDueDateTypeCode;
	}

	/**
	 * Sets the routingFormDueDateTypeCode attribute.
	 * 
	 * @param routingFormDueDateTypeCode The routingFormDueDateTypeCode to set.
	 * 
	 */
	public void setRoutingFormDueDateTypeCode(String routingFormDueDateTypeCode) {
		this.routingFormDueDateTypeCode = routingFormDueDateTypeCode;
	}


	/**
	 * Gets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @return Returns the dataObjectMaintenanceCodeActiveIndicator
	 * 
	 */
	public boolean getDataObjectMaintenanceCodeActiveIndicator() { 
		return dataObjectMaintenanceCodeActiveIndicator;
	}

	/**
	 * Sets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @param dataObjectMaintenanceCodeActiveIndicator The dataObjectMaintenanceCodeActiveIndicator to set.
	 * 
	 */
	public void setDataObjectMaintenanceCodeActiveIndicator(boolean dataObjectMaintenanceCodeActiveIndicator) {
		this.dataObjectMaintenanceCodeActiveIndicator = dataObjectMaintenanceCodeActiveIndicator;
	}


	/**
	 * Gets the routingFormApprovalLeadTime attribute.
	 * 
	 * @return Returns the routingFormApprovalLeadTime
	 * 
	 */
	public Integer getRoutingFormApprovalLeadTime() { 
		return routingFormApprovalLeadTime;
	}

	/**
	 * Sets the routingFormApprovalLeadTime attribute.
	 * 
	 * @param routingFormApprovalLeadTime The routingFormApprovalLeadTime to set.
	 * 
	 */
	public void setRoutingFormApprovalLeadTime(Integer routingFormApprovalLeadTime) {
		this.routingFormApprovalLeadTime = routingFormApprovalLeadTime;
	}


	/**
	 * Gets the routingFormDueDateDescription attribute.
	 * 
	 * @return Returns the routingFormDueDateDescription
	 * 
	 */
	public String getRoutingFormDueDateDescription() { 
		return routingFormDueDateDescription;
	}

	/**
	 * Sets the routingFormDueDateDescription attribute.
	 * 
	 * @param routingFormDueDateDescription The routingFormDueDateDescription to set.
	 * 
	 */
	public void setRoutingFormDueDateDescription(String routingFormDueDateDescription) {
		this.routingFormDueDateDescription = routingFormDueDateDescription;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("routingFormDueDateTypeCode", this.routingFormDueDateTypeCode);
	    return m;
    }
}
