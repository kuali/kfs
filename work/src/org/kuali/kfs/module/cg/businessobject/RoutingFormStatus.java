/*
 * Copyright 2006 The Kuali Foundation.
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
 * 
 */
public class RoutingFormStatus extends BusinessObjectBase {

	private String routingFormStatusCode;
	private boolean dataObjectMaintenanceCodeActiveIndicator;
	private String routingFormStatusDescription;

	/**
	 * Default constructor.
	 */
	public RoutingFormStatus() {

	}

	/**
	 * Gets the routingFormStatusCode attribute.
	 * 
	 * @return Returns the routingFormStatusCode
	 * 
	 */
	public String getRoutingFormStatusCode() { 
		return routingFormStatusCode;
	}

	/**
	 * Sets the routingFormStatusCode attribute.
	 * 
	 * @param routingFormStatusCode The routingFormStatusCode to set.
	 * 
	 */
	public void setRoutingFormStatusCode(String routingFormStatusCode) {
		this.routingFormStatusCode = routingFormStatusCode;
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
	 * Gets the routingFormStatusDescription attribute.
	 * 
	 * @return Returns the routingFormStatusDescription
	 * 
	 */
	public String getRoutingFormStatusDescription() { 
		return routingFormStatusDescription;
	}

	/**
	 * Sets the routingFormStatusDescription attribute.
	 * 
	 * @param routingFormStatusDescription The routingFormStatusDescription to set.
	 * 
	 */
	public void setRoutingFormStatusDescription(String routingFormStatusDescription) {
		this.routingFormStatusDescription = routingFormStatusDescription;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("routingFormStatusCode", this.routingFormStatusCode);
	    return m;
    }
}
