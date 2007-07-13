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
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.vendor.util.VendorRoutingComparable;

/**
 * 
 */
public class VendorShippingSpecialCondition extends PersistableBusinessObjectBase implements VendorRoutingComparable {

	private Integer vendorHeaderGeneratedIdentifier;
	private Integer vendorDetailAssignedIdentifier;
	private String vendorShippingSpecialConditionCode;
    private boolean active;
    
    private VendorDetail vendorDetail;
    private ShippingSpecialCondition vendorShippingSpecialCondition;
    
	/**
	 * Default constructor.
	 */
	public VendorShippingSpecialCondition() {

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
	 * Gets the vendorShippingSpecialConditionCode attribute.
	 * 
	 * @return Returns the vendorShippingSpecialConditionCode
	 * 
	 */
	public String getVendorShippingSpecialConditionCode() { 
		return vendorShippingSpecialConditionCode;
	}

	/**
	 * Sets the vendorShippingSpecialConditionCode attribute.
	 * 
	 * @param vendorShippingSpecialConditionCode The vendorShippingSpecialConditionCode to set.
	 * 
	 */
	public void setVendorShippingSpecialConditionCode(String vendorShippingSpecialConditionCode) {
		this.vendorShippingSpecialConditionCode = vendorShippingSpecialConditionCode;
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
     * Gets the vendorShippingSpecialCondition attribute. 
     * @return Returns the vendorShippingSpecialCondition.
     */
    public ShippingSpecialCondition getVendorShippingSpecialCondition() {
        return vendorShippingSpecialCondition;
    }

    /**
     * Sets the vendorShippingSpecialCondition attribute value.
     * @param vendorShippingSpecialCondition The vendorShippingSpecialCondition to set.
     * @deprecated
     */
    public void setVendorShippingSpecialCondition(ShippingSpecialCondition vendorShippingSpecialCondition) {
        this.vendorShippingSpecialCondition = vendorShippingSpecialCondition;
    }

    /**
     * @see org.kuali.module.vendor.util.VendorRoutingComparable#isEqualForRouting(java.lang.Object)
     */
    public boolean isEqualForRouting( Object toCompare ) {
        if( ( ObjectUtils.isNull( toCompare ) ) || !( toCompare instanceof VendorShippingSpecialCondition ) ) {
            return false;
        } else {
            VendorShippingSpecialCondition vssc = (VendorShippingSpecialCondition)toCompare;
            return new EqualsBuilder()
                .append( this.getVendorHeaderGeneratedIdentifier(), vssc.getVendorHeaderGeneratedIdentifier() )
                .append( this.getVendorDetailAssignedIdentifier(), vssc.getVendorDetailAssignedIdentifier() )
                .append( this.getVendorShippingSpecialConditionCode(), vssc.getVendorShippingSpecialConditionCode() )
                .isEquals();
        }
    }
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        if (this.vendorHeaderGeneratedIdentifier != null) {
            m.put("vendorHeaderGeneratedIdentifier", this.vendorHeaderGeneratedIdentifier.toString());
        }
        if (this.vendorDetailAssignedIdentifier != null) {
            m.put("vendorDetailAssignedIdentifier", this.vendorDetailAssignedIdentifier.toString());
        }
        m.put("vendorShippingSpecialConditionCode", this.vendorShippingSpecialConditionCode);
        return m;
    }
}
