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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * A relation between a particular <code>Org</code> and a <code>VendorContract</code> indicating that the Org uses this Vendor
 * Contract.
 * 
 * @see org.kuali.kfs.vnd.businessobject.VendorContract
 * @see org.kuali.kfs.coa.businessobject.Org
 */
public class VendorContractOrganization extends PersistableBusinessObjectBase implements VendorRoutingComparable, MutableInactivatable {

    private Integer vendorContractGeneratedIdentifier;
    private String chartOfAccountsCode;
    private String organizationCode;
    private KualiDecimal vendorContractPurchaseOrderLimitAmount;
    private boolean vendorContractExcludeIndicator;
    private boolean active;

    private VendorContract vendorContract;
    private Organization organization;
    private Chart chartOfAccounts;

    /**
     * Default constructor.
     */
    public VendorContractOrganization() {

    }

    public Integer getVendorContractGeneratedIdentifier() {

        return vendorContractGeneratedIdentifier;
    }

    public void setVendorContractGeneratedIdentifier(Integer vendorContractGeneratedIdentifier) {
        this.vendorContractGeneratedIdentifier = vendorContractGeneratedIdentifier;
    }

    public String getChartOfAccountsCode() {

        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getOrganizationCode() {

        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public KualiDecimal getVendorContractPurchaseOrderLimitAmount() {

        return vendorContractPurchaseOrderLimitAmount;
    }

    public void setVendorContractPurchaseOrderLimitAmount(KualiDecimal vendorContractPurchaseOrderLimitAmount) {
        this.vendorContractPurchaseOrderLimitAmount = vendorContractPurchaseOrderLimitAmount;
    }

    public boolean isVendorContractExcludeIndicator() {

        return vendorContractExcludeIndicator;
    }

    public void setVendorContractExcludeIndicator(boolean vendorContractExcludeIndicator) {
        this.vendorContractExcludeIndicator = vendorContractExcludeIndicator;
    }

    public boolean isActive() {

        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Organization getOrganization() {

        return organization;
    }

    /**
     * Sets the organization attribute.
     * 
     * @param organization The organization to set.
     * @deprecated
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
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

    public VendorContract getVendorContract() {

        return vendorContract;
    }

    /**
     * Sets the vendorContract attribute value.
     * 
     * @param vendorContract The vendorContract to set.
     * @deprecated
     */
    public void setVendorContract(VendorContract vendorContract) {
        this.vendorContract = vendorContract;
    }

    /**
     * @see org.kuali.kfs.vnd.document.routing.VendorRoutingComparable#isEqualForRouting(java.lang.Object)
     */
    public boolean isEqualForRouting(Object toCompare) {
        if ((ObjectUtils.isNull(toCompare)) || !(toCompare instanceof VendorContractOrganization)) {

            return false;
        }
        else {
            VendorContractOrganization vco = (VendorContractOrganization) toCompare;

            return new EqualsBuilder().append(this.getVendorContractGeneratedIdentifier(), vco.getVendorContractGeneratedIdentifier()).append(this.getChartOfAccountsCode(), vco.getChartOfAccountsCode()).append(this.getOrganizationCode(), vco.getOrganizationCode()).append(this.getVendorContractPurchaseOrderLimitAmount(), vco.getVendorContractPurchaseOrderLimitAmount()).append(this.isVendorContractExcludeIndicator(), vco.isVendorContractExcludeIndicator()).isEquals();
        }
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.vendorContractGeneratedIdentifier != null) {
            m.put("vendorContractGeneratedIdentifier", this.vendorContractGeneratedIdentifier.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);

        return m;
    }

}
