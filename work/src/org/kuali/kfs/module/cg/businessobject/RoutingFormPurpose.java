/*
 * Copyright 2006 The Kuali Foundation.
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
 * 
 */
public class RoutingFormPurpose extends BusinessObjectBase {

	private String routingFormPurposeCode;
	private String dataObjectMaintenanceCodeActiveIndicator;
	private String routingFormPurposeDescription;

	/**
	 * Default constructor.
	 */
	public RoutingFormPurpose() {

	}

	/**
	 * Gets the routingFormPurposeCode attribute.
	 * 
	 * @return Returns the routingFormPurposeCode
	 * 
	 */
	public String getRoutingFormPurposeCode() { 
		return routingFormPurposeCode;
	}

	/**
	 * Sets the routingFormPurposeCode attribute.
	 * 
	 * @param routingFormPurposeCode The routingFormPurposeCode to set.
	 * 
	 */
	public void setRoutingFormPurposeCode(String routingFormPurposeCode) {
		this.routingFormPurposeCode = routingFormPurposeCode;
	}


	/**
	 * Gets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @return Returns the dataObjectMaintenanceCodeActiveIndicator
	 * 
	 */
	public String getDataObjectMaintenanceCodeActiveIndicator() { 
		return dataObjectMaintenanceCodeActiveIndicator;
	}

	/**
	 * Sets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @param dataObjectMaintenanceCodeActiveIndicator The dataObjectMaintenanceCodeActiveIndicator to set.
	 * 
	 */
	public void setDataObjectMaintenanceCodeActiveIndicator(String dataObjectMaintenanceCodeActiveIndicator) {
		this.dataObjectMaintenanceCodeActiveIndicator = dataObjectMaintenanceCodeActiveIndicator;
	}


	/**
	 * Gets the routingFormPurposeDescription attribute.
	 * 
	 * @return Returns the routingFormPurposeDescription
	 * 
	 */
	public String getRoutingFormPurposeDescription() { 
		return routingFormPurposeDescription;
	}

	/**
	 * Sets the routingFormPurposeDescription attribute.
	 * 
	 * @param routingFormPurposeDescription The routingFormPurposeDescription to set.
	 * 
	 */
	public void setRoutingFormPurposeDescription(String routingFormPurposeDescription) {
		this.routingFormPurposeDescription = routingFormPurposeDescription;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("routingFormPurposeCode", this.routingFormPurposeCode);
	    return m;
    }
}
