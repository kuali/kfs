/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/PurchaseOrderVendorChoice.java,v $
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
public class PurchaseOrderVendorChoice extends BusinessObjectBase {

	private String purchaseOrderVendorChoiceCode;
	private String purchaseOrderVendorChoiceDescription;
	private boolean dataObjectMaintenanceCodeActiveIndicator;

	/**
	 * Default constructor.
	 */
	public PurchaseOrderVendorChoice() {

	}

	/**
	 * Gets the purchaseOrderVendorChoiceCode attribute.
	 * 
	 * @return Returns the purchaseOrderVendorChoiceCode
	 * 
	 */
	public String getPurchaseOrderVendorChoiceCode() { 
		return purchaseOrderVendorChoiceCode;
	}

	/**
	 * Sets the purchaseOrderVendorChoiceCode attribute.
	 * 
	 * @param purchaseOrderVendorChoiceCode The purchaseOrderVendorChoiceCode to set.
	 * 
	 */
	public void setPurchaseOrderVendorChoiceCode(String purchaseOrderVendorChoiceCode) {
		this.purchaseOrderVendorChoiceCode = purchaseOrderVendorChoiceCode;
	}


	/**
	 * Gets the purchaseOrderVendorChoiceDescription attribute.
	 * 
	 * @return Returns the purchaseOrderVendorChoiceDescription
	 * 
	 */
	public String getPurchaseOrderVendorChoiceDescription() { 
		return purchaseOrderVendorChoiceDescription;
	}

	/**
	 * Sets the purchaseOrderVendorChoiceDescription attribute.
	 * 
	 * @param purchaseOrderVendorChoiceDescription The purchaseOrderVendorChoiceDescription to set.
	 * 
	 */
	public void setPurchaseOrderVendorChoiceDescription(String purchaseOrderVendorChoiceDescription) {
		this.purchaseOrderVendorChoiceDescription = purchaseOrderVendorChoiceDescription;
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
        m.put("purchaseOrderVendorChoiceCode", this.purchaseOrderVendorChoiceCode);
	    return m;
    }
}
