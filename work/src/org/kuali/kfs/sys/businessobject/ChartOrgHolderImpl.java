/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.businessobject;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.TypedArrayList;

public class ChartOrgHolderImpl extends PersistableBusinessObjectBase implements ChartOrgHolder {

    protected String principalId;
    protected String moduleId;
    protected boolean active = true;

    protected String chartOfAccountsCode;
    protected String organizationCode;

    protected Chart chartOfAccounts;
    protected Org organization;

    protected static transient OrganizationService organizationService;
    protected static transient ChartService chartService;

    protected List<ChartOrgHolderModuleImpl> primaryOrganizations = new TypedArrayList(ChartOrgHolderModuleImpl.class);

    public ChartOrgHolderImpl() {}
    
    public List<ChartOrgHolderModuleImpl> getPrimaryOrganizations() {
        return primaryOrganizations;
    }

    public void setPrimaryOrganizations(List<ChartOrgHolderModuleImpl> primaryOrganizations) {
        this.primaryOrganizations = primaryOrganizations;
    }
    
    public ChartOrgHolder getPrimaryOrganizationByModuleId( String moduleId ) {
        if ( moduleId == null ) {
            return null;
        }
        for ( ChartOrgHolderModuleImpl org : getPrimaryOrganizations() ) {
            if ( org.getModuleId().equals(moduleId)) {
                return org;
            }
        }
        return null;
    }
       
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }


    public String getOrganizationCode() {
        return organizationCode;
    }

    public Chart getChartOfAccounts() {
        if ( chartOfAccounts == null ) {
            chartOfAccounts = getChartService().getByPrimaryId(chartOfAccountsCode);
        }
        return chartOfAccounts;
    }

    public Org getOrganization() {
        if ( organization == null ) {
            organization = getOrganizationService().getByPrimaryId(chartOfAccountsCode, organizationCode);
        }
        return organization;
    }

    private static OrganizationService getOrganizationService() {
        if ( organizationService == null ) {
            organizationService = SpringContext.getBean(OrganizationService.class);
        }
        return organizationService;
    }

    private static ChartService getChartService() {
        if ( chartService == null ) {
            chartService = SpringContext.getBean(ChartService.class);
        }
        return chartService;
    }
    
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, Object> hashMap = new LinkedHashMap<String, Object>();
        hashMap.put("principalId", principalId);
        hashMap.put("chartOfAccountsCode", chartOfAccountsCode);
        hashMap.put("organizationCode", organizationCode);
        return hashMap;
    }
        
}
