/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.vnd.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public class VendorCommodityCode extends PersistableBusinessObjectBase implements Inactivateable {

	private Integer vendorHeaderGeneratedIdentifier;
	private Integer vendorDetailAssignedIdentifier;
	private String purchasingCommodityCode;
	private boolean commodityDefaultIndicator;
	private boolean active;

    private VendorDetail vendorDetail;
    private CommodityCode commodityCode;
    
	/**
	 * Default constructor.
	 */
	public VendorCommodityCode() {

	}

	public VendorCommodityCode(Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, CommodityCode commodityCode, boolean active) {
	    this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
	    this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
	    this.commodityCode = commodityCode;
	    if (commodityCode != null) {
	        this.purchasingCommodityCode = commodityCode.getPurchasingCommodityCode();
	    }
	    this.active = active;
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
	 * Gets the commodityCode attribute.
	 * 
	 * @return Returns the commodityCode
	 * 
	 */
	public String getPurchasingCommodityCode() { 
		return purchasingCommodityCode;
	}

	/**
	 * Sets the commodityCode attribute.
	 * 
	 * @param commodityCode The commodityCode to set.
	 * 
	 */
	public void setPurchasingCommodityCode(String purchasingCommodityCode) {
		this.purchasingCommodityCode = purchasingCommodityCode;
	}


	/**
	 * Gets the commodityDefaultIndicator attribute.
	 * 
	 * @return Returns the commodityDefaultIndicator
	 * 
	 */
	public boolean isCommodityDefaultIndicator() { 
		return commodityDefaultIndicator;
	}

	/**
	 * Sets the commodityDefaultIndicator attribute.
	 * 
	 * @param commodityDefaultIndicator The commodityDefaultIndicator to set.
	 * 
	 */
	public void setCommodityDefaultIndicator(boolean commodityDefaultIndicator) {
		this.commodityDefaultIndicator = commodityDefaultIndicator;
	}


	/**
	 * Gets the active attribute.
	 * 
	 * @return Returns the active
	 * 
	 */
	public boolean isActive() { 
		return active;
	}

	/**
	 * Sets the active attribute.
	 * 
	 * @param active The active to set.
	 * 
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

    public CommodityCode getCommodityCode() {
        return commodityCode;
    }

    public void setCommodityCode(CommodityCode commodityCode) {
        this.commodityCode = commodityCode;
    }

    /**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.vendorHeaderGeneratedIdentifier != null) {
            m.put(VendorPropertyConstants.VENDOR_HEADER_GENERATED_ID, this.vendorHeaderGeneratedIdentifier.toString());
        }
        if (this.vendorDetailAssignedIdentifier != null) {
            m.put(VendorPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, this.vendorDetailAssignedIdentifier.toString());
        }
        m.put(VendorPropertyConstants.PURCHASING_COMMODITY_CODE, this.purchasingCommodityCode);
	    return m;
    }
	
    /**
     * This method overrides the superclass method to return the description of the commodity code.
     * 
     * @param mapper A LinkedHashMap
     * @return A String rendition of this object.
     */
    @Override
    public String toStringBuilder(LinkedHashMap mapper) {
        if (commodityCode != null) {

            return commodityCode.getCommodityDescription();
        }
        else {

            return super.toStringBuilder(mapper);
        }
    }
}
