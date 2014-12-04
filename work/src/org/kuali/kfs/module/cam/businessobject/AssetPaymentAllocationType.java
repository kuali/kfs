/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
