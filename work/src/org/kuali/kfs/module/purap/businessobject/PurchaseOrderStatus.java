/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/PurchaseOrderStatus.java,v $
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
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PurchaseOrderStatus extends BusinessObjectBase {

	private String purchaseOrderStatusCode;
	private String purchaseOrderStatusDescription;
	private String dataObjectMaintenanceCodeActiveIndicator;

	/**
	 * Default constructor.
	 */
	public PurchaseOrderStatus() {

	}

	/**
	 * Gets the purchaseOrderStatusCode attribute.
	 * 
	 * @return - Returns the purchaseOrderStatusCode
	 * 
	 */
	public String getPurchaseOrderStatusCode() { 
		return purchaseOrderStatusCode;
	}

	/**
	 * Sets the purchaseOrderStatusCode attribute.
	 * 
	 * @param - purchaseOrderStatusCode The purchaseOrderStatusCode to set.
	 * 
	 */
	public void setPurchaseOrderStatusCode(String purchaseOrderStatusCode) {
		this.purchaseOrderStatusCode = purchaseOrderStatusCode;
	}


	/**
	 * Gets the purchaseOrderStatusDescription attribute.
	 * 
	 * @return - Returns the purchaseOrderStatusDescription
	 * 
	 */
	public String getPurchaseOrderStatusDescription() { 
		return purchaseOrderStatusDescription;
	}

	/**
	 * Sets the purchaseOrderStatusDescription attribute.
	 * 
	 * @param - purchaseOrderStatusDescription The purchaseOrderStatusDescription to set.
	 * 
	 */
	public void setPurchaseOrderStatusDescription(String purchaseOrderStatusDescription) {
		this.purchaseOrderStatusDescription = purchaseOrderStatusDescription;
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
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("purchaseOrderStatusCode", this.purchaseOrderStatusCode);
	    return m;
    }
}
