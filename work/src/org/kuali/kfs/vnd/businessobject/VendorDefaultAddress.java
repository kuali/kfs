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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.kuali.core.bo.Campus;
import org.kuali.core.bo.Inactivateable;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.vendor.util.VendorRoutingComparable;

/**
 * 
 */
public class VendorDefaultAddress extends PersistableBusinessObjectBase implements VendorRoutingComparable, Inactivateable {

    private Integer vendorDefaultAddressGeneratedIdentifier;
    private Integer vendorAddressGeneratedIdentifier;
	private String vendorCampusCode;
    private boolean active;
    
    private VendorAddress vendorAddress;
	private Campus vendorCampus;
    
	/**
	 * Default constructor.
	 */
	public VendorDefaultAddress() {

	}

	/**
	 * Gets the vendorCampusCode attribute.
	 * 
	 * @return Returns the vendorCampusCode
	 * 
	 */
	public String getVendorCampusCode() { 
		return vendorCampusCode;
	}

	/**
	 * Sets the vendorCampusCode attribute.
	 * 
	 * @param vendorCampusCode The vendorCampusCode to set.
	 * 
	 */
	public void setVendorCampusCode(String vendorCampusCode) {
		this.vendorCampusCode = vendorCampusCode;
	}


	/**
	 * Gets the vendorAddressGeneratedIdentifier attribute.
	 * 
	 * @return Returns the vendorAddressGeneratedIdentifier
	 * 
	 */
	public Integer getVendorAddressGeneratedIdentifier() { 
		return vendorAddressGeneratedIdentifier;
	}

	/**
	 * Sets the vendorAddressGeneratedIdentifier attribute.
	 * 
	 * @param vendorAddressGeneratedIdentifier The vendorAddressGeneratedIdentifier to set.
	 * 
	 */
	public void setVendorAddressGeneratedIdentifier(Integer vendorAddressGeneratedIdentifier) {
		this.vendorAddressGeneratedIdentifier = vendorAddressGeneratedIdentifier;
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
	 * Gets the vendorAddress attribute.
	 * 
	 * @return Returns the vendorAddress
	 * 
	 */
	public VendorAddress getVendorAddress() { 
		return vendorAddress;
	}

	/**
	 * Sets the vendorAddress attribute.
	 * 
	 * @param vendorAddress The vendorAddress to set.
	 * @deprecated
	 */
	public void setVendorAddress(VendorAddress vendorAddress) {
		this.vendorAddress = vendorAddress;
	}

	/**
	 * Gets the vendorCampus attribute.
	 * 
	 * @return Returns the vendorCampus
	 * 
	 */
	public Campus getVendorCampus() { 
		return vendorCampus;
	}

	/**
	 * Sets the vendorCampus attribute.
	 * 
	 * @param vendorCampus The vendorCampus to set.
	 * @deprecated
	 */
	public void setVendorCampus(Campus vendorCampus) {
		this.vendorCampus = vendorCampus;
	}

    /**
     * Gets the vendorDefaultAddressGeneratedIdentifier attribute. 
     * @return Returns the vendorDefaultAddressGeneratedIdentifier.
     */
    public Integer getVendorDefaultAddressGeneratedIdentifier() {
        return vendorDefaultAddressGeneratedIdentifier;
    }

    /**
     * Sets the vendorDefaultAddressGeneratedIdentifier attribute value.
     * @param vendorDefaultAddressGeneratedIdentifier The vendorDefaultAddressGeneratedIdentifier to set.
     */
    public void setVendorDefaultAddressGeneratedIdentifier(Integer vendorDefaultAddressGeneratedIdentifier) {
        this.vendorDefaultAddressGeneratedIdentifier = vendorDefaultAddressGeneratedIdentifier;
    }
    
    /**
     * @see org.kuali.module.vendor.util.VendorRoutingComparable#isEqualForRouting(java.lang.Object)
     */
    public boolean isEqualForRouting( Object toCompare ) {
        if( ( ObjectUtils.isNull( toCompare ) ) || !( toCompare instanceof VendorDefaultAddress ) ) {
            return false;
        } else {
            VendorDefaultAddress vda = (VendorDefaultAddress)toCompare;
            return new EqualsBuilder()
                .append( this.getVendorDefaultAddressGeneratedIdentifier(), 
                        vda.getVendorDefaultAddressGeneratedIdentifier() )
                .append( this.getVendorAddressGeneratedIdentifier(),
                        vda.getVendorAddressGeneratedIdentifier() )
                .append( this.getVendorCampusCode(), vda.getVendorCampusCode() )
                .isEquals();
        }
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        if (this.vendorAddressGeneratedIdentifier != null) {
            m.put("vendorAddressGeneratedIdentifier", this.vendorAddressGeneratedIdentifier.toString());
        }
        m.put("vendorCampusCode", this.vendorCampusCode);
        return m;
    }


}
