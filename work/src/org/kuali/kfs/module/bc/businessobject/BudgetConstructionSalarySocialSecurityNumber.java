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

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class BudgetConstructionSalarySocialSecurityNumber extends PersistableBusinessObjectBase {

    private String principalId;
    private String organizationChartOfAccountsCode;
    private String organizationCode;
    private String name;
    private String emplid;

    private Chart organizationChartOfAccounts;
    private Organization organization;

    /**
     * Default constructor.
     */
    public BudgetConstructionSalarySocialSecurityNumber() {

    }

    /**
     * Gets the principalId attribute.
     * 
     * @return Returns the principalId
     */
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Sets the principalId attribute.
     * 
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
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
     * Gets the name attribute.
     * 
     * @return Returns the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name attribute.
     * 
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Gets the emplid attribute.
     * 
     * @return Returns the emplid
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid attribute.
     * 
     * @param emplid The emplid to set.
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
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
        m.put("name", this.name);
        m.put("emplid", this.emplid);
        return m;
    }
}

