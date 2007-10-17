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
 * Arbitrary categories per Vendor Contract for the purpose of tracking various
 * kinds of costs that may be incurred by Purchase Orders which use the Vendors
 * which have these Contracts.
 * 
 * @see org.kuali.module.vendor.bo.VendorContract
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

	public String getPurchaseOrderCostSourceCode() {
        
		return purchaseOrderCostSourceCode;
	}

	public void setPurchaseOrderCostSourceCode(String purchaseOrderCostSourceCode) {
		this.purchaseOrderCostSourceCode = purchaseOrderCostSourceCode;
	}

	public String getPurchaseOrderCostSourceDescription() {
        
		return purchaseOrderCostSourceDescription;
	}

	public void setPurchaseOrderCostSourceDescription(String purchaseOrderCostSourceDescription) {
		this.purchaseOrderCostSourceDescription = purchaseOrderCostSourceDescription;
	}

    public boolean isActive() {
        
        return active;
    }

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
