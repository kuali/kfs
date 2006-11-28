/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/PurchaseOrderTransmissionMethod.java,v $
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
public class PurchaseOrderTransmissionMethod extends BusinessObjectBase {

	private String purchaseOrderTransmissionMethodCode;
	private String purchaseOrderTransmissionMethodDescription;
	private boolean dataObjectMaintenanceCodeActiveIndicator;

	/**
	 * Default constructor.
	 */
	public PurchaseOrderTransmissionMethod() {

	}

	/**
	 * Gets the purchaseOrderTransmissionMethodCode attribute.
	 * 
	 * @return Returns the purchaseOrderTransmissionMethodCode
	 * 
	 */
	public String getPurchaseOrderTransmissionMethodCode() { 
		return purchaseOrderTransmissionMethodCode;
	}

	/**
	 * Sets the purchaseOrderTransmissionMethodCode attribute.
	 * 
	 * @param purchaseOrderTransmissionMethodCode The purchaseOrderTransmissionMethodCode to set.
	 * 
	 */
	public void setPurchaseOrderTransmissionMethodCode(String purchaseOrderTransmissionMethodCode) {
		this.purchaseOrderTransmissionMethodCode = purchaseOrderTransmissionMethodCode;
	}


	/**
	 * Gets the purchaseOrderTransmissionMethodDescription attribute.
	 * 
	 * @return Returns the purchaseOrderTransmissionMethodDescription
	 * 
	 */
	public String getPurchaseOrderTransmissionMethodDescription() { 
		return purchaseOrderTransmissionMethodDescription;
	}

	/**
	 * Sets the purchaseOrderTransmissionMethodDescription attribute.
	 * 
	 * @param purchaseOrderTransmissionMethodDescription The purchaseOrderTransmissionMethodDescription to set.
	 * 
	 */
	public void setPurchaseOrderTransmissionMethodDescription(String purchaseOrderTransmissionMethodDescription) {
		this.purchaseOrderTransmissionMethodDescription = purchaseOrderTransmissionMethodDescription;
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
        m.put("purchaseOrderTransmissionMethodCode", this.purchaseOrderTransmissionMethodCode);
	    return m;
    }
}
