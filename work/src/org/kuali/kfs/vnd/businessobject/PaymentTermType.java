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

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class PaymentTermType extends PersistableBusinessObjectBase {

	private String vendorPaymentTermsCode;
	private String vendorDiscountDueTypeDescription;
	private Integer vendorDiscountDueNumber;
	private BigDecimal vendorPaymentTermsPercent;
	private String vendorNetDueTypeDescription;
	private Integer vendorNetDueNumber;
	private String vendorPaymentTermsDescription;
    private boolean active;
    
	/**
	 * Default constructor.
	 */
	public PaymentTermType() {

	}

	/**
	 * Gets the vendorPaymentTermsCode attribute.
	 * 
	 * @return Returns the vendorPaymentTermsCode
	 * 
	 */
	public String getVendorPaymentTermsCode() { 
		return vendorPaymentTermsCode;
	}

	/**
	 * Sets the vendorPaymentTermsCode attribute.
	 * 
	 * @param vendorPaymentTermsCode The vendorPaymentTermsCode to set.
	 * 
	 */
	public void setVendorPaymentTermsCode(String vendorPaymentTermsCode) {
		this.vendorPaymentTermsCode = vendorPaymentTermsCode;
	}


	/**
	 * Gets the vendorDiscountDueTypeDescription attribute.
	 * 
	 * @return Returns the vendorDiscountDueTypeDescription
	 * 
	 */
	public String getVendorDiscountDueTypeDescription() { 
		return vendorDiscountDueTypeDescription;
	}

	/**
	 * Sets the vendorDiscountDueTypeDescription attribute.
	 * 
	 * @param vendorDiscountDueTypeDescription The vendorDiscountDueTypeDescription to set.
	 * 
	 */
	public void setVendorDiscountDueTypeDescription(String vendorDiscountDueTypeDescription) {
		this.vendorDiscountDueTypeDescription = vendorDiscountDueTypeDescription;
	}


	/**
	 * Gets the vendorDiscountDueNumber attribute.
	 * 
	 * @return Returns the vendorDiscountDueNumber
	 * 
	 */
	public Integer getVendorDiscountDueNumber() { 
		return vendorDiscountDueNumber;
	}

	/**
	 * Sets the vendorDiscountDueNumber attribute.
	 * 
	 * @param vendorDiscountDueNumber The vendorDiscountDueNumber to set.
	 * 
	 */
	public void setVendorDiscountDueNumber(Integer vendorDiscountDueNumber) {
		this.vendorDiscountDueNumber = vendorDiscountDueNumber;
	}


	/**
	 * Gets the vendorPaymentTermsPercent attribute.
	 * 
	 * @return Returns the vendorPaymentTermsPercent
	 * 
	 */
	public BigDecimal getVendorPaymentTermsPercent() { 
		return vendorPaymentTermsPercent;
	}

	/**
	 * Sets the vendorPaymentTermsPercent attribute.
	 * 
	 * @param vendorPaymentTermsPercent The vendorPaymentTermsPercent to set.
	 * 
	 */
	public void setVendorPaymentTermsPercent(BigDecimal vendorPaymentTermsPercent) {
		this.vendorPaymentTermsPercent = vendorPaymentTermsPercent;
	}


	/**
	 * Gets the vendorNetDueTypeDescription attribute.
	 * 
	 * @return Returns the vendorNetDueTypeDescription
	 * 
	 */
	public String getVendorNetDueTypeDescription() { 
		return vendorNetDueTypeDescription;
	}

	/**
	 * Sets the vendorNetDueTypeDescription attribute.
	 * 
	 * @param vendorNetDueTypeDescription The vendorNetDueTypeDescription to set.
	 * 
	 */
	public void setVendorNetDueTypeDescription(String vendorNetDueTypeDescription) {
		this.vendorNetDueTypeDescription = vendorNetDueTypeDescription;
	}


	/**
	 * Gets the vendorNetDueNumber attribute.
	 * 
	 * @return Returns the vendorNetDueNumber
	 * 
	 */
	public Integer getVendorNetDueNumber() { 
		return vendorNetDueNumber;
	}

	/**
	 * Sets the vendorNetDueNumber attribute.
	 * 
	 * @param vendorNetDueNumber The vendorNetDueNumber to set.
	 * 
	 */
	public void setVendorNetDueNumber(Integer vendorNetDueNumber) {
		this.vendorNetDueNumber = vendorNetDueNumber;
	}


	/**
	 * Gets the vendorPaymentTermsDescription attribute.
	 * 
	 * @return Returns the vendorPaymentTermsDescription
	 * 
	 */
	public String getVendorPaymentTermsDescription() { 
		return vendorPaymentTermsDescription;
	}

	/**
	 * Sets the vendorPaymentTermsDescription attribute.
	 * 
	 * @param vendorPaymentTermsDescription The vendorPaymentTermsDescription to set.
	 * 
	 */
	public void setVendorPaymentTermsDescription(String vendorPaymentTermsDescription) {
		this.vendorPaymentTermsDescription = vendorPaymentTermsDescription;
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
        m.put("vendorPaymentTermsCode", this.vendorPaymentTermsCode);
	    return m;
    }
}
