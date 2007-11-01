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
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;

/**
 * Customer numbers that may have been assigned by the Vendor to various <code>Chart</code> and/or <code>Org</code>.
 * 
 * @see org.kuali.module.chart.bo.Chart
 * @see org.kuali.module.chart.bo.Org
 */
public class VendorCustomerNumber extends PersistableBusinessObjectBase implements Inactivateable {

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

    public Integer getVendorCustomerNumberGeneratedIdentifier() {

        return vendorCustomerNumberGeneratedIdentifier;
    }

    public void setVendorCustomerNumberGeneratedIdentifier(Integer vendorCustomerNumberGeneratedIdentifier) {
        this.vendorCustomerNumberGeneratedIdentifier = vendorCustomerNumberGeneratedIdentifier;
    }

    public Integer getVendorHeaderGeneratedIdentifier() {

        return vendorHeaderGeneratedIdentifier;
    }

    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }

    public Integer getVendorDetailAssignedIdentifier() {

        return vendorDetailAssignedIdentifier;
    }

    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }

    public String getVendorCustomerNumber() {

        return vendorCustomerNumber;
    }

    public void setVendorCustomerNumber(String vendorCustomerNumber) {
        this.vendorCustomerNumber = vendorCustomerNumber;
    }

    public String getChartOfAccountsCode() {

        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getVendorOrganizationCode() {

        return vendorOrganizationCode;
    }

    public void setVendorOrganizationCode(String vendorOrganizationCode) {
        this.vendorOrganizationCode = vendorOrganizationCode;
    }

    public boolean isActive() {

        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

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
