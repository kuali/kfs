/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/businessobject/OrganizationRoutingModelName.java,v $
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

package org.kuali.module.chart.bo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class OrganizationRoutingModelName extends BusinessObjectBase {

    private String chartOfAccountsCode;
    private String organizationCode;
    private String organizationRoutingModelName;
    private List organizationRoutingModel;

    private Org organization;
    private Chart chartOfAccounts;

    /**
     * Default constructor.
     */
    public OrganizationRoutingModelName() {
        organizationRoutingModel = new ArrayList();

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
     * Gets the organizationRoutingModelName attribute.
     * 
     * @return Returns the organizationRoutingModelName
     * 
     */
    public String getOrganizationRoutingModelName() {
        return organizationRoutingModelName;
    }

    /**
     * Sets the organizationRoutingModelName attribute.
     * 
     * @param organizationRoutingModelName The organizationRoutingModelName to set.
     * 
     */
    public void setOrganizationRoutingModelName(String organizationRoutingModelName) {
        this.organizationRoutingModelName = organizationRoutingModelName;
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
     * Gets the organizationRoutingModel attribute.
     * 
     * @return Returns the organizationRoutingModel.
     */
    public List getOrganizationRoutingModel() {
        return organizationRoutingModel;
    }

    /**
     * Sets the organizationRoutingModel attribute value.
     * 
     * @param organizationRoutingModel The organizationRoutingModel to set.
     */
    public void setOrganizationRoutingModel(List organizationRoutingModel) {
        this.organizationRoutingModel = organizationRoutingModel;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);
        m.put("organizationRoutingModelName", this.organizationRoutingModelName);
        return m;
    }


}
