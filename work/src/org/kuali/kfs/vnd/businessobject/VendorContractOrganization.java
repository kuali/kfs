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
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.vendor.util.VendorRoutingComparable;

/**
 * 
 */
public class VendorContractOrganization extends PersistableBusinessObjectBase implements VendorRoutingComparable {

	private Integer vendorContractGeneratedIdentifier;
	private String chartOfAccountsCode;
	private String organizationCode;
	private KualiDecimal vendorContractPurchaseOrderLimitAmount;
	private boolean vendorContractExcludeIndicator;
    private boolean active;
    
    private VendorContract vendorContract;
    private Org organization;
	private Chart chartOfAccounts;

	/**
	 * Default constructor.
	 */
	public VendorContractOrganization() {

	}

	/**
	 * Gets the vendorContractGeneratedIdentifier attribute.
	 * 
	 * @return Returns the vendorContractGeneratedIdentifier
	 * 
	 */
	public Integer getVendorContractGeneratedIdentifier() { 
		return vendorContractGeneratedIdentifier;
	}

	/**
	 * Sets the vendorContractGeneratedIdentifier attribute.
	 * 
	 * @param vendorContractGeneratedIdentifier The vendorContractGeneratedIdentifier to set.
	 * 
	 */
	public void setVendorContractGeneratedIdentifier(Integer vendorContractGeneratedIdentifier) {
		this.vendorContractGeneratedIdentifier = vendorContractGeneratedIdentifier;
	}


	/**
	 * Gets the chartOfAccountsCode attribute.
	 * 
	 * @return Returns the chartOfAccountsCode
	 * 
	 */
	public String getChartOfAccountsCode() { 
		return chartOfAccountsCode;
	}

	/**
	 * Sets the chartOfAccountsCode attribute.
	 * 
	 * @param chartOfAccountsCode The chartOfAccountsCode to set.
	 * 
	 */
	public void setChartOfAccountsCode(String chartOfAccountsCode) {
		this.chartOfAccountsCode = chartOfAccountsCode;
	}


	/**
	 * Gets the organizationCode attribute.
	 * 
	 * @return Returns the organizationCode
	 * 
	 */
	public String getOrganizationCode() { 
		return organizationCode;
	}

	/**
	 * Sets the organizationCode attribute.
	 * 
	 * @param organizationCode The organizationCode to set.
	 * 
	 */
	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}


	/**
	 * Gets the vendorContractPurchaseOrderLimitAmount attribute.
	 * 
	 * @return Returns the vendorContractPurchaseOrderLimitAmount
	 * 
	 */
	public KualiDecimal getVendorContractPurchaseOrderLimitAmount() { 
		return vendorContractPurchaseOrderLimitAmount;
	}

	/**
	 * Sets the vendorContractPurchaseOrderLimitAmount attribute.
	 * 
	 * @param vendorContractPurchaseOrderLimitAmount The vendorContractPurchaseOrderLimitAmount to set.
	 * 
	 */
	public void setVendorContractPurchaseOrderLimitAmount(KualiDecimal vendorContractPurchaseOrderLimitAmount) {
		this.vendorContractPurchaseOrderLimitAmount = vendorContractPurchaseOrderLimitAmount;
	}


	/**
	 * Gets the vendorContractExcludeIndicator attribute.
	 * 
	 * @return Returns the vendorContractExcludeIndicator
	 * 
	 */
	public boolean isVendorContractExcludeIndicator() { 
		return vendorContractExcludeIndicator;
	}
	

	/**
	 * Sets the vendorContractExcludeIndicator attribute.
	 * 
	 * @param vendorContractExcludeIndicator The vendorContractExcludeIndicator to set.
	 * 
	 */
	public void setVendorContractExcludeIndicator(boolean vendorContractExcludeIndicator) {
		this.vendorContractExcludeIndicator = vendorContractExcludeIndicator;
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
	 * Gets the organization attribute.
	 * 
	 * @return Returns the organization
	 * 
	 */
	public Org getOrganization() { 
		return organization;
	}

	/**
	 * Sets the organization attribute.
	 * 
	 * @param organization The organization to set.
	 * @deprecated
	 */
	public void setOrganization(Org organization) {
		this.organization = organization;
	}

	/**
	 * Gets the chartOfAccounts attribute.
	 * 
	 * @return Returns the chartOfAccounts
	 * 
	 */
	public Chart getChartOfAccounts() { 
		return chartOfAccounts;
	}

	/**
	 * Sets the chartOfAccounts attribute.
	 * 
	 * @param chartOfAccounts The chartOfAccounts to set.
	 * @deprecated
	 */
	public void setChartOfAccounts(Chart chartOfAccounts) {
		this.chartOfAccounts = chartOfAccounts;
	}

    /**
     * Gets the vendorContract attribute. 
     * @return Returns the vendorContract.
     */
    public VendorContract getVendorContract() {
        return vendorContract;
    }

    /**
     * Sets the vendorContract attribute value.
     * @param vendorContract The vendorContract to set.
     * @deprecated
     */
    public void setVendorContract(VendorContract vendorContract) {
        this.vendorContract = vendorContract;
    }

    /**
     * @see org.kuali.module.vendor.util.VendorRoutingComparable#isEqualForRouting(java.lang.Object)
     */
    public boolean isEqualForRouting( Object toCompare ) {
        if( ( ObjectUtils.isNull( toCompare ) ) || !( toCompare instanceof VendorContractOrganization ) ) {
            return false;
        } else {
            VendorContractOrganization vco = (VendorContractOrganization)toCompare;
            return new EqualsBuilder()
                .append( this.getVendorContractGeneratedIdentifier(), 
                        vco.getVendorContractGeneratedIdentifier() )
                .append( this.getChartOfAccountsCode(), vco.getChartOfAccountsCode() )
                .append( this.getOrganizationCode(), vco.getOrganizationCode() )
                .append( this.getVendorContractPurchaseOrderLimitAmount(),
                        vco.getVendorContractPurchaseOrderLimitAmount() )
                .append( this.isVendorContractExcludeIndicator(), vco.isVendorContractExcludeIndicator() )
                .isEquals();
        }
    }
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        if (this.vendorContractGeneratedIdentifier != null) {
            m.put("vendorContractGeneratedIdentifier", this.vendorContractGeneratedIdentifier.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);
        return m;
    }

}
