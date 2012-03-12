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
import org.kuali.kfs.module.bc.document.service.BudgetConstructionOrganizationReportsService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class BudgetConstructionPullup extends PersistableBusinessObjectBase {

    private String chartOfAccountsCode;
    private String organizationCode;
    private String reportsToChartOfAccountsCode;
    private String reportsToOrganizationCode;
    private Integer pullFlag;
    private String principalId;

    public boolean isLeaf;

    private Chart chartOfAccounts;
    private Organization organization;
    private Organization reportsToOrganization;
    private Chart reportsToChartOfAccounts;

    /**
     * Default constructor.
     */
    public BudgetConstructionPullup() {
        pullFlag = new Integer(0);
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
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
     * Gets the reportsToChartOfAccountsCode attribute.
     * 
     * @return Returns the reportsToChartOfAccountsCode
     */
    public String getReportsToChartOfAccountsCode() {
        return reportsToChartOfAccountsCode;
    }

    /**
     * Sets the reportsToChartOfAccountsCode attribute.
     * 
     * @param reportsToChartOfAccountsCode The reportsToChartOfAccountsCode to set.
     */
    public void setReportsToChartOfAccountsCode(String reportsToChartOfAccountsCode) {
        this.reportsToChartOfAccountsCode = reportsToChartOfAccountsCode;
    }


    /**
     * Gets the reportsToOrganizationCode attribute.
     * 
     * @return Returns the reportsToOrganizationCode
     */
    public String getReportsToOrganizationCode() {
        return reportsToOrganizationCode;
    }

    /**
     * Sets the reportsToOrganizationCode attribute.
     * 
     * @param reportsToOrganizationCode The reportsToOrganizationCode to set.
     */
    public void setReportsToOrganizationCode(String reportsToOrganizationCode) {
        this.reportsToOrganizationCode = reportsToOrganizationCode;
    }


    /**
     * Gets the pullFlag attribute.
     * 
     * @return Returns the pullFlag
     */
    public Integer getPullFlag() {
        return pullFlag;
    }

    /**
     * Sets the pullFlag attribute.
     * 
     * @param pullFlag The pullFlag to set.
     */
    public void setPullFlag(Integer pullFlag) {
        this.pullFlag = pullFlag;
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
     * Gets the isLeaf attribute.
     * 
     * @return Returns the isLeaf.
     */
    public boolean isLeaf() {
        return SpringContext.getBean(BudgetConstructionOrganizationReportsService.class).isLeafOrg(this.chartOfAccountsCode, this.organizationCode);
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
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
     * Gets the reportsToOrganization attribute.
     * 
     * @return Returns the reportsToOrganization
     */
    public Organization getReportsToOrganization() {
        return reportsToOrganization;
    }

    /**
     * Sets the reportsToOrganization attribute.
     * 
     * @param reportsToOrganization The reportsToOrganization to set.
     * @deprecated
     */
    public void setReportsToOrganization(Organization reportsToOrganization) {
        this.reportsToOrganization = reportsToOrganization;
    }

    /**
     * Gets the reportsToChartOfAccounts attribute.
     * 
     * @return Returns the reportsToChartOfAccounts
     */
    public Chart getReportsToChartOfAccounts() {
        return reportsToChartOfAccounts;
    }

    /**
     * Sets the reportsToChartOfAccounts attribute.
     * 
     * @param reportsToChartOfAccounts The reportsToChartOfAccounts to set.
     * @deprecated
     */
    public void setReportsToChartOfAccounts(Chart reportsToChartOfAccounts) {
        this.reportsToChartOfAccounts = reportsToChartOfAccounts;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    
    public boolean equals(Object obj) {
        boolean isEqual = true;
        if (obj == null || !(obj instanceof BudgetConstructionPullup)) {
            isEqual = false;
        }
        else {
            if (!this.toString().equals(obj.toString())) {
               isEqual = false; 
            }
        }
        
        return isEqual;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("principalId", this.principalId);
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);
        return m;
    }
}

