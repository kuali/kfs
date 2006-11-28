/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/PurchaseOrderQuoteStatus.java,v $
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
public class PurchaseOrderQuoteStatus extends BusinessObjectBase {

	private String purchaseOrderQuoteStatusCode;
	private String purchaseOrderQuoteStatusDescription;
	private boolean dataObjectMaintenanceCodeActiveIndicator;

	/**
	 * Default constructor.
	 */
	public PurchaseOrderQuoteStatus() {

	}

	/**
	 * Gets the purchaseOrderQuoteStatusCode attribute.
	 * 
	 * @return Returns the purchaseOrderQuoteStatusCode
	 * 
	 */
	public String getPurchaseOrderQuoteStatusCode() { 
		return purchaseOrderQuoteStatusCode;
	}

	/**
	 * Sets the purchaseOrderQuoteStatusCode attribute.
	 * 
	 * @param purchaseOrderQuoteStatusCode The purchaseOrderQuoteStatusCode to set.
	 * 
	 */
	public void setPurchaseOrderQuoteStatusCode(String purchaseOrderQuoteStatusCode) {
		this.purchaseOrderQuoteStatusCode = purchaseOrderQuoteStatusCode;
	}


	/**
	 * Gets the purchaseOrderQuoteStatusDescription attribute.
	 * 
	 * @return Returns the purchaseOrderQuoteStatusDescription
	 * 
	 */
	public String getPurchaseOrderQuoteStatusDescription() { 
		return purchaseOrderQuoteStatusDescription;
	}

	/**
	 * Sets the purchaseOrderQuoteStatusDescription attribute.
	 * 
	 * @param purchaseOrderQuoteStatusDescription The purchaseOrderQuoteStatusDescription to set.
	 * 
	 */
	public void setPurchaseOrderQuoteStatusDescription(String purchaseOrderQuoteStatusDescription) {
		this.purchaseOrderQuoteStatusDescription = purchaseOrderQuoteStatusDescription;
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
        m.put("purchaseOrderQuoteStatusCode", this.purchaseOrderQuoteStatusCode);
	    return m;
    }
}
