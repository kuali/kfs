/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/PurchaseOrderQuoteListVendor.java,v $
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

package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class PurchaseOrderQuoteListVendor extends BusinessObjectBase {

	private Integer purchaseOrderQuoteListIdentifier;
	private Integer vendorHeaderGeneratedIdentifier;
	private Integer vendorDetailAssignedIdentifier;

    private PurchaseOrderQuoteList purchaseOrderQuoteList;
    
	/**
	 * Default constructor.
	 */
	public PurchaseOrderQuoteListVendor() {

	}

	/**
	 * Gets the purchaseOrderQuoteListIdentifier attribute.
	 * 
	 * @return Returns the purchaseOrderQuoteListIdentifier
	 * 
	 */
	public Integer getPurchaseOrderQuoteListIdentifier() { 
		return purchaseOrderQuoteListIdentifier;
	}

	/**
	 * Sets the purchaseOrderQuoteListIdentifier attribute.
	 * 
	 * @param purchaseOrderQuoteListIdentifier The purchaseOrderQuoteListIdentifier to set.
	 * 
	 */
	public void setPurchaseOrderQuoteListIdentifier(Integer purchaseOrderQuoteListIdentifier) {
		this.purchaseOrderQuoteListIdentifier = purchaseOrderQuoteListIdentifier;
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
     * Gets the purchaseOrderQuoteList attribute. 
     * @return Returns the purchaseOrderQuoteList.
     */
    public PurchaseOrderQuoteList getPurchaseOrderQuoteList() {
        return purchaseOrderQuoteList;
    }

    /**
     * Sets the purchaseOrderQuoteList attribute value.
     * @param purchaseOrderQuoteList The purchaseOrderQuoteList to set.
     * @deprecated
     */
    public void setPurchaseOrderQuoteList(PurchaseOrderQuoteList purchaseOrderQuoteList) {
        this.purchaseOrderQuoteList = purchaseOrderQuoteList;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        if (this.purchaseOrderQuoteListIdentifier != null) {
            m.put("purchaseOrderQuoteListIdentifier", this.purchaseOrderQuoteListIdentifier.toString());
        }
        if (this.vendorHeaderGeneratedIdentifier != null) {
            m.put("vendorHeaderGeneratedIdentifier", this.vendorHeaderGeneratedIdentifier.toString());
        }
        if (this.vendorDetailAssignedIdentifier != null) {
            m.put("vendorDetailAssignedIdentifier", this.vendorDetailAssignedIdentifier.toString());
        }
        return m;
    }

}
