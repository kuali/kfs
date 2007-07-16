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

import org.kuali.core.bo.Inactivateable;
import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class VendorAlias extends PersistableBusinessObjectBase implements Inactivateable {

	private String vendorAliasName;
	private Integer vendorDetailAssignedIdentifier;
	private Integer vendorHeaderGeneratedIdentifier;
    private boolean active;
    
    VendorDetail vendorDetail;
    
	/**
	 * Default constructor.
	 */
	public VendorAlias() {

	}

	/**
	 * Gets the vendorAliasName attribute.
	 * 
	 * @return Returns the vendorAliasName
	 * 
	 */
	public String getVendorAliasName() { 
		return vendorAliasName;
	}

	/**
	 * Sets the vendorAliasName attribute.
	 * 
	 * @param vendorAliasName The vendorAliasName to set.
	 * 
	 */
	public void setVendorAliasName(String vendorAliasName) {
		this.vendorAliasName = vendorAliasName;
	}


	/**
	 * Gets the vendorDetailAssignedIdentifier attribute.
	 * 
	 * @return Returns the vendorDetailAssignedIdentifier
	 * 
	 */
	public Integer getVendorDetailAssignedIdentifier() { 
		return vendorDetailAssignedIdentifier;
	}

	/**
	 * Sets the vendorDetailAssignedIdentifier attribute.
	 * 
	 * @param vendorDetailAssignedIdentifier The vendorDetailAssignedIdentifier to set.
	 * 
	 */
	public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
		this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
	}


	/**
	 * Gets the vendorHeaderGeneratedIdentifier attribute.
	 * 
	 * @return Returns the vendorHeaderGeneratedIdentifier
	 * 
	 */
	public Integer getVendorHeaderGeneratedIdentifier() { 
		return vendorHeaderGeneratedIdentifier;
	}

	/**
	 * Sets the vendorHeaderGeneratedIdentifier attribute.
	 * 
	 * @param vendorHeaderGeneratedIdentifier The vendorHeaderGeneratedIdentifier to set.
	 * 
	 */
	public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
		this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
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
     * Gets the vendorDetail attribute. 
     * @return Returns the vendorDetail.
     */
    public VendorDetail getVendorDetail() {
        return vendorDetail;
    }

    /**
     * Sets the vendorDetail attribute value.
     * @param vendorDetail The vendorDetail to set.
     * @deprecated
     */
    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("vendorAliasName", this.vendorAliasName);
        if (this.vendorDetailAssignedIdentifier != null) {
            m.put("vendorDetailAssignedIdentifier", this.vendorDetailAssignedIdentifier.toString());
        }
        if (this.vendorHeaderGeneratedIdentifier != null) {
            m.put("vendorHeaderGeneratedIdentifier", this.vendorHeaderGeneratedIdentifier.toString());
        }
        return m;
    }

    /**
     *  This method has to be overriden so that we will print only the vendorAliasName in the
     *  vendor lookup result.
     * @see org.kuali.core.bo.BusinessObjectBase#toStringBuilder(java.util.LinkedHashMap)
     */
    @Override
    public String toStringBuilder(LinkedHashMap mapper) {
        return vendorAliasName;
    }
}
