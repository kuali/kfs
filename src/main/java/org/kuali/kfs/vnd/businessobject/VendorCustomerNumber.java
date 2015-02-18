/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kuali.kfs.vnd.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Customer numbers that may have been assigned by the Vendor to various <code>Chart</code> and/or <code>Org</code>.
 * 
 * @see org.kuali.kfs.coa.businessobject.Chart
 * @see org.kuali.kfs.coa.businessobject.Org
 */
public class VendorCustomerNumber extends PersistableBusinessObjectBase implements MutableInactivatable {

    private Integer vendorCustomerNumberGeneratedIdentifier;
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String vendorCustomerNumber;
    private String chartOfAccountsCode;
    private String vendorOrganizationCode;
    private boolean active;

    private VendorDetail vendorDetail;
    private Organization vendorOrganization;
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

    public Organization getVendorOrganization() {

        return vendorOrganization;
    }

    /**
     * Sets the vendorOrganization attribute.
     * 
     * @param vendorOrganization The vendorOrganization to set.
     * @deprecated
     */
    public void setVendorOrganization(Organization vendorOrganization) {
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.vendorCustomerNumberGeneratedIdentifier != null) {
            m.put("vendorCustomerNumberGeneratedIdentifier", this.vendorCustomerNumberGeneratedIdentifier.toString());
        }

        return m;
    }
}
