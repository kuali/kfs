/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/DeliveryRequiredDateReason.java,v $
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

package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class DeliveryRequiredDateReason extends BusinessObjectBase {

	private String deliveryRequiredDateReasonCode;
	private String deliveryRequiredDateReasonDescription;
	private boolean dataObjectMaintenanceCodeActiveIndicator;

	/**
	 * Default constructor.
	 */
	public DeliveryRequiredDateReason() {

	}

	/**
	 * Gets the deliveryRequiredDateReasonCode attribute.
	 * 
	 * @return Returns the deliveryRequiredDateReasonCode
	 * 
	 */
	public String getDeliveryRequiredDateReasonCode() { 
		return deliveryRequiredDateReasonCode;
	}

	/**
	 * Sets the deliveryRequiredDateReasonCode attribute.
	 * 
	 * @param deliveryRequiredDateReasonCode The deliveryRequiredDateReasonCode to set.
	 * 
	 */
	public void setDeliveryRequiredDateReasonCode(String deliveryRequiredDateReasonCode) {
		this.deliveryRequiredDateReasonCode = deliveryRequiredDateReasonCode;
	}


	/**
	 * Gets the deliveryRequiredDateReasonDescription attribute.
	 * 
	 * @return Returns the deliveryRequiredDateReasonDescription
	 * 
	 */
	public String getDeliveryRequiredDateReasonDescription() { 
		return deliveryRequiredDateReasonDescription;
	}

	/**
	 * Sets the deliveryRequiredDateReasonDescription attribute.
	 * 
	 * @param deliveryRequiredDateReasonDescription The deliveryRequiredDateReasonDescription to set.
	 * 
	 */
	public void setDeliveryRequiredDateReasonDescription(String deliveryRequiredDateReasonDescription) {
		this.deliveryRequiredDateReasonDescription = deliveryRequiredDateReasonDescription;
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
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("deliveryRequiredDateReasonCode", this.deliveryRequiredDateReasonCode);
	    return m;
    }
}
