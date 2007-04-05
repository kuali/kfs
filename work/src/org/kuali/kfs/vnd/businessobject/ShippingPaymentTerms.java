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
public class ShippingPaymentTerms extends PersistableBusinessObjectBase {

	private String vendorShippingPaymentTermsCode;
	private String vendorShippingPaymentTermsDescription;
	private boolean vendorShippingPaymentTermsPayIndicator;
    private boolean active;
    
	/**
	 * Default constructor.
	 */
	public ShippingPaymentTerms() {

	}

	/**
	 * Gets the vendorShippingPaymentTermsCode attribute.
	 * 
	 * @return Returns the vendorShippingPaymentTermsCode
	 * 
	 */
	public String getVendorShippingPaymentTermsCode() { 
		return vendorShippingPaymentTermsCode;
	}

	/**
	 * Sets the vendorShippingPaymentTermsCode attribute.
	 * 
	 * @param vendorShippingPaymentTermsCode The vendorShippingPaymentTermsCode to set.
	 * 
	 */
	public void setVendorShippingPaymentTermsCode(String vendorShippingPaymentTermsCode) {
		this.vendorShippingPaymentTermsCode = vendorShippingPaymentTermsCode;
	}


	/**
	 * Gets the vendorShippingPaymentTermsDescription attribute.
	 * 
	 * @return Returns the vendorShippingPaymentTermsDescription
	 * 
	 */
	public String getVendorShippingPaymentTermsDescription() { 
		return vendorShippingPaymentTermsDescription;
	}

	/**
	 * Sets the vendorShippingPaymentTermsDescription attribute.
	 * 
	 * @param vendorShippingPaymentTermsDescription The vendorShippingPaymentTermsDescription to set.
	 * 
	 */
	public void setVendorShippingPaymentTermsDescription(String vendorShippingPaymentTermsDescription) {
		this.vendorShippingPaymentTermsDescription = vendorShippingPaymentTermsDescription;
	}


	/**
	 * Gets the vendorShippingPaymentTermsPayIndicator attribute.
	 * 
	 * @return Returns the vendorShippingPaymentTermsPayIndicator
	 * 
	 */
	public boolean getVendorShippingPaymentTermsPayIndicator() { 
		return vendorShippingPaymentTermsPayIndicator;
	}

	/**
	 * Sets the vendorShippingPaymentTermsPayIndicator attribute.
	 * 
	 * @param vendorShippingPaymentTermsPayIndicator The vendorShippingPaymentTermsPayIndicator to set.
	 * 
	 */
	public void setVendorShippingPaymentTermsPayIndicator(boolean vendorShippingPaymentTermsPayIndicator) {
		this.vendorShippingPaymentTermsPayIndicator = vendorShippingPaymentTermsPayIndicator;
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
        m.put("vendorShippingPaymentTermsCode", this.vendorShippingPaymentTermsCode);
	    return m;
    }
}
