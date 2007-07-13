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
import org.apache.log4j.Logger;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.vendor.util.VendorRoutingComparable;

/**
 * 
 */
public class VendorSupplierDiversity extends PersistableBusinessObjectBase implements VendorRoutingComparable {
    private static Logger LOG = Logger.getLogger(VendorSupplierDiversity.class);
    
	private Integer vendorHeaderGeneratedIdentifier;
	private String vendorSupplierDiversityCode;
    private boolean active;
    
    private VendorHeader vendorHeader;
    private SupplierDiversity vendorSupplierDiversity;

    
	/**
	 * Default constructor.
	 */
	public VendorSupplierDiversity() {

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
	 * Gets the vendorSupplierDiversityCode attribute.
	 * 
	 * @return Returns the vendorSupplierDiversityCode
	 * 
	 */
	public String getVendorSupplierDiversityCode() { 
		return vendorSupplierDiversityCode;
	}

	/**
	 * Sets the vendorSupplierDiversityCode attribute.
	 * 
	 * @param vendorSupplierDiversityCode The vendorSupplierDiversityCode to set.
	 * 
	 */
	public void setVendorSupplierDiversityCode(String vendorSupplierDiversityCode) {
		this.vendorSupplierDiversityCode = vendorSupplierDiversityCode;
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
     * Gets the vendorHeader attribute. 
     * @return Returns the vendorHeader.
     */
    public VendorHeader getVendorHeader() {
        return vendorHeader;
    }

    /**
     * Sets the vendorHeader attribute value.
     * @param vendorHeader The vendorHeader to set.
     * @deprecated
     */
    public void setVendorHeader(VendorHeader vendorHeader) {
        this.vendorHeader = vendorHeader;
    }

    /**
     * Gets the vendorSupplierDiversity attribute. 
     * @return Returns the vendorSupplierDiversity.
     */
    public SupplierDiversity getVendorSupplierDiversity() {
        return vendorSupplierDiversity;
    }

    /**
     * Sets the vendorSupplierDiversity attribute value.
     * @param vendorSupplierDiversity The vendorSupplierDiversity to set.
     * @deprecated
     */
    public void setVendorSupplierDiversity(SupplierDiversity vendorSupplierDiversity) {
        this.vendorSupplierDiversity = vendorSupplierDiversity;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();  

        if (this.vendorHeaderGeneratedIdentifier != null) {
            m.put("vendorHeaderGeneratedIdentifier", this.vendorHeaderGeneratedIdentifier.toString());
        }

        m.put("vendorSupplierDiversityCode", this.vendorSupplierDiversityCode);
        return m;
    }
    
    /**
     * @see org.kuali.module.vendor.util.VendorRoutingComparable#isEqualForRouting(java.lang.Object)
     */
    public boolean isEqualForRouting( Object toCompare ) {
        LOG.debug( "Entering isEqualForRouting." ); 
        if( ( ObjectUtils.isNull( toCompare ) ) || !( toCompare instanceof VendorSupplierDiversity ) ) {
            return false;
        } else {
            VendorSupplierDiversity vsd = (VendorSupplierDiversity)toCompare;
            return new EqualsBuilder()
                .append( this.getVendorHeaderGeneratedIdentifier(), vsd.getVendorHeaderGeneratedIdentifier() )
                .append( this.getVendorSupplierDiversityCode(), vsd.getVendorSupplierDiversityCode() )
                .isEquals();
        }
    }
    
    @Override
    /**
     *  This method overrides the superclass method to return the description of
     *  the supplier diversity.
     */
    public String toStringBuilder(LinkedHashMap mapper) {
        if (vendorSupplierDiversity != null) {
            return vendorSupplierDiversity.getVendorSupplierDiversityDescription();
        } else {
            return super.toStringBuilder(mapper);
        }
    }
}
