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
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;

/**
 * 
 */
public class VendorCustomerNumber extends PersistableBusinessObjectBase {

	private Integer vendorCustomerNumberGeneratedIdentifier;
	private Integer vendorHeaderGeneratedIdentifier;
	private Integer vendorDetailAssignedIdentifier;
	private String vendorCustomerNumber;
	private String chartOfAccountsCode;
	private String vendorOrganizationCode;
    private boolean active;
    
    private VendorDetail vendorDetail;
	private Org vendorOrganization;
	private Chart chartOfAccounts;

	/**
	 * Default constructor.
	 */
	public VendorCustomerNumber() {

	}

	/**
	 * Gets the vendorCustomerNumberGeneratedIdentifier attribute.
	 * 
	 * @return Returns the vendorCustomerNumberGeneratedIdentifier
	 * 
	 */
	public Integer getVendorCustomerNumberGeneratedIdentifier() { 
		return vendorCustomerNumberGeneratedIdentifier;
	}

	/**
	 * Sets the vendorCustomerNumberGeneratedIdentifier attribute.
	 * 
	 * @param vendorCustomerNumberGeneratedIdentifier The vendorCustomerNumberGeneratedIdentifier to set.
	 * 
	 */
	public void setVendorCustomerNumberGeneratedIdentifier(Integer vendorCustomerNumberGeneratedIdentifier) {
		this.vendorCustomerNumberGeneratedIdentifier = vendorCustomerNumberGeneratedIdentifier;
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
	 * Gets the vendorCustomerNumber attribute.
	 * 
	 * @return Returns the vendorCustomerNumber
	 * 
	 */
	public String getVendorCustomerNumber() { 
		return vendorCustomerNumber;
	}

	/**
	 * Sets the vendorCustomerNumber attribute.
	 * 
	 * @param vendorCustomerNumber The vendorCustomerNumber to set.
	 * 
	 */
	public void setVendorCustomerNumber(String vendorCustomerNumber) {
		this.vendorCustomerNumber = vendorCustomerNumber;
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
	 * Gets the vendorOrganizationCode attribute.
	 * 
	 * @return Returns the vendorOrganizationCode
	 * 
	 */
	public String getVendorOrganizationCode() { 
		return vendorOrganizationCode;
	}

	/**
	 * Sets the vendorOrganizationCode attribute.
	 * 
	 * @param vendorOrganizationCode The vendorOrganizationCode to set.
	 * 
	 */
	public void setVendorOrganizationCode(String vendorOrganizationCode) {
		this.vendorOrganizationCode = vendorOrganizationCode;
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
	 * 
	 * @return Returns the vendorDetail
	 * 
	 */
	public VendorDetail getVendorDetail() { 
		return vendorDetail;
	}

	/**
	 * Sets the vendorDetail attribute.
	 * 
	 * @param vendorDetail The vendorDetail to set.
	 * @deprecated
	 */
	public void setVendorDetail(VendorDetail vendorDetail) {
		this.vendorDetail = vendorDetail;
	}

	/**
	 * Gets the vendorOrganization attribute.
	 * 
	 * @return Returns the vendorOrganization
	 * 
	 */
	public Org getVendorOrganization() { 
		return vendorOrganization;
	}

	/**
	 * Sets the vendorOrganization attribute.
	 * 
	 * @param vendorOrganization The vendorOrganization to set.
	 * @deprecated
	 */
	public void setVendorOrganization(Org vendorOrganization) {
		this.vendorOrganization = vendorOrganization;
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
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.vendorCustomerNumberGeneratedIdentifier != null) {
            m.put("vendorCustomerNumberGeneratedIdentifier", this.vendorCustomerNumberGeneratedIdentifier.toString());
        }
	    return m;
    }
}
