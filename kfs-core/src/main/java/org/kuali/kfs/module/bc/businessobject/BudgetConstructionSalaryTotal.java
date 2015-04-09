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

package org.kuali.kfs.module.bc.businessobject;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class BudgetConstructionSalaryTotal extends PersistableBusinessObjectBase {

    private String organizationChartOfAccountsCode;
    private String organizationCode;
    private KualiInteger csfAmount;
    private KualiInteger appointmentRequestedAmount;
    private BigDecimal appointmentRequestedFteQuantity;
    private KualiInteger initialRequestedAmount;
    private BigDecimal initialRequestedFteQuantity;
    private String principalId;

    private Chart organizationChartOfAccounts;
    private Organization organization;

    /**
     * Default constructor.
     */
    public BudgetConstructionSalaryTotal() {

    }

    /**
     * Gets the organizationChartOfAccountsCode attribute.
     * 
     * @return Returns the organizationChartOfAccountsCode
     */
    public String getOrganizationChartOfAccountsCode() {
        return organizationChartOfAccountsCode;
    }

    /**
     * Sets the organizationChartOfAccountsCode attribute.
     * 
     * @param organizationChartOfAccountsCode The organizationChartOfAccountsCode to set.
     */
    public void setOrganizationChartOfAccountsCode(String organizationChartOfAccountsCode) {
        this.organizationChartOfAccountsCode = organizationChartOfAccountsCode;
    }


    /**
     * Gets the organizationCode attribute.
     * 
     * @return Returns the organizationCode
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute.
     * 
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }


    /**
     * Gets the csfAmount attribute.
     * 
     * @return Returns the csfAmount.
     */
    public KualiInteger getCsfAmount() {
        return csfAmount;
    }

    /**
     * Sets the csfAmount attribute value.
     * 
     * @param csfAmount The csfAmount to set.
     */
    public void setCsfAmount(KualiInteger csfAmount) {
        this.csfAmount = csfAmount;
    }

    /**
     * Gets the appointmentRequestedAmount attribute.
     * 
     * @return Returns the appointmentRequestedAmount.
     */
    public KualiInteger getAppointmentRequestedAmount() {
        return appointmentRequestedAmount;
    }

    /**
     * Sets the appointmentRequestedAmount attribute value.
     * 
     * @param appointmentRequestedAmount The appointmentRequestedAmount to set.
     */
    public void setAppointmentRequestedAmount(KualiInteger appointmentRequestedAmount) {
        this.appointmentRequestedAmount = appointmentRequestedAmount;
    }

    /**
     * Gets the appointmentRequestedFteQuantity attribute.
     * 
     * @return Returns the appointmentRequestedFteQuantity
     */
    public BigDecimal getAppointmentRequestedFteQuantity() {
        return appointmentRequestedFteQuantity;
    }

    /**
     * Sets the appointmentRequestedFteQuantity attribute.
     * 
     * @param appointmentRequestedFteQuantity The appointmentRequestedFteQuantity to set.
     */
    public void setAppointmentRequestedFteQuantity(BigDecimal appointmentRequestedFteQuantity) {
        this.appointmentRequestedFteQuantity = appointmentRequestedFteQuantity;
    }


    /**
     * Gets the initialRequestedAmount attribute.
     * 
     * @return Returns the initialRequestedAmount.
     */
    public KualiInteger getInitialRequestedAmount() {
        return initialRequestedAmount;
    }

    /**
     * Sets the initialRequestedAmount attribute value.
     * 
     * @param initialRequestedAmount The initialRequestedAmount to set.
     */
    public void setInitialRequestedAmount(KualiInteger initialRequestedAmount) {
        this.initialRequestedAmount = initialRequestedAmount;
    }

    /**
     * Gets the initialRequestedFteQuantity attribute.
     * 
     * @return Returns the initialRequestedFteQuantity
     */
    public BigDecimal getInitialRequestedFteQuantity() {
        return initialRequestedFteQuantity;
    }

    /**
     * Sets the initialRequestedFteQuantity attribute.
     * 
     * @param initialRequestedFteQuantity The initialRequestedFteQuantity to set.
     */
    public void setInitialRequestedFteQuantity(BigDecimal initialRequestedFteQuantity) {
        this.initialRequestedFteQuantity = initialRequestedFteQuantity;
    }


    /**
     * Gets the principalId attribute.
     * 
     * @return Returns the principalId.
     */
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Sets the principalId attribute value.
     * 
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /**
     * Gets the organizationChartOfAccounts attribute.
     * 
     * @return Returns the organizationChartOfAccounts
     */
    public Chart getOrganizationChartOfAccounts() {
        return organizationChartOfAccounts;
    }

    /**
     * Sets the organizationChartOfAccounts attribute.
     * 
     * @param organizationChartOfAccounts The organizationChartOfAccounts to set.
     * @deprecated
     */
    public void setOrganizationChartOfAccounts(Chart organizationChartOfAccounts) {
        this.organizationChartOfAccounts = organizationChartOfAccounts;
    }

    /**
     * Gets the organization attribute.
     * 
     * @return Returns the organization
     */
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

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("principalId", this.principalId);
        m.put("organizationChartOfAccountsCode", this.organizationChartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);
        return m;
    }
}

