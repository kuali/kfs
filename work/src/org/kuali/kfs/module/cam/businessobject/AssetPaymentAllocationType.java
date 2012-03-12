/*
 * Copyright 2011 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.cam.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Asset payment Allocation type is used to select the method of Allocation of payments. In other words, the type determines how
 * source line payments are distributed/allocated amonth the target assets.
 */
public class AssetPaymentAllocationType extends PersistableBusinessObjectBase implements MutableInactivatable {
    private boolean active;
    private String allocationCode;
    private String allocationName;
    private String allocationColumnName;
    private boolean allocationEditable;


    /**
     * Gets the Allocation code
     */
    public String getAllocationCode() {
    	
        return allocationCode;
    }


    /**
     * Gets the Allocation codes descriptive name
     */
    public String getAllocationName() {
        return allocationName;
    }


    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#isActive()
     */
    public boolean isActive() {
        return active;
    }


    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#setActive(boolean)
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Sets the Allocation code
     */
    public void setAllocationCode(String allocationCode) {
        this.allocationCode = allocationCode;
    }


    /**
     * Sets the Allocation codes descriptive name
     */
    public void setAllocationName(String allocationName) {
        this.allocationName = allocationName;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put("AllocationCode", allocationCode);
        return m;
    }


    /**
     * Returns the column name displayed to the user 
     */
    public String getAllocationColumnName() {
        return allocationColumnName;
    }


    /**
     * Sets the column name displayed to the user 
     */
    public void setAllocationColumnName(String allocationColumnName) {
        this.allocationColumnName = allocationColumnName;
    }


	/**
	 * Sets whether the allocation column is editable by the user
	 */
	public void setAllocationEditable(boolean allocationEditable) {
		this.allocationEditable = allocationEditable;
	}


	/**
	 * Returns true if the allocation column is editable by the user
	 */
	public boolean isAllocationEditable() {
		return allocationEditable;
	}

}
