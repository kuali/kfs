/*
 * Copyright 2006 The Kuali Foundation
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

