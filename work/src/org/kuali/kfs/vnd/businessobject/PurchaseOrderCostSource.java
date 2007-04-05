/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.vendor.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class PurchaseOrderCostSource extends PersistableBusinessObjectBase {

	private String purchaseOrderCostSourceCode;
	private String purchaseOrderCostSourceDescription;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public PurchaseOrderCostSource() {

	}

	/**
	 * Gets the purchaseOrderCostSourceCode attribute.
	 * 
	 * @return Returns the purchaseOrderCostSourceCode
	 * 
	 */
	public String getPurchaseOrderCostSourceCode() { 
		return purchaseOrderCostSourceCode;
	}

	/**
	 * Sets the purchaseOrderCostSourceCode attribute.
	 * 
	 * @param purchaseOrderCostSourceCode The purchaseOrderCostSourceCode to set.
	 * 
	 */
	public void setPurchaseOrderCostSourceCode(String purchaseOrderCostSourceCode) {
		this.purchaseOrderCostSourceCode = purchaseOrderCostSourceCode;
	}


	/**
	 * Gets the purchaseOrderCostSourceDescription attribute.
	 * 
	 * @return Returns the purchaseOrderCostSourceDescription
	 * 
	 */
	public String getPurchaseOrderCostSourceDescription() { 
		return purchaseOrderCostSourceDescription;
	}

	/**
	 * Sets the purchaseOrderCostSourceDescription attribute.
	 * 
	 * @param purchaseOrderCostSourceDescription The purchaseOrderCostSourceDescription to set.
	 * 
	 */
	public void setPurchaseOrderCostSourceDescription(String purchaseOrderCostSourceDescription) {
		this.purchaseOrderCostSourceDescription = purchaseOrderCostSourceDescription;
	}

	/**
     * Gets the active attribute. 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("purchaseOrderCostSourceCode", this.purchaseOrderCostSourceCode);
	    return m;
    }
}
