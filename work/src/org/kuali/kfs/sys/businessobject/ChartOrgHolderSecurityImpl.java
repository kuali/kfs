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

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.ModuleService;

public class ChartOrgHolderSecurityImpl extends PersistableBusinessObjectBase implements ChartOrgHolder, Inactivateable {

    private transient static KualiModuleService kualiModuleService;

    private String principalId;
    private String moduleId;
    private String chartOfAccountsCode;
    private String organizationCode;
    private boolean descendOrgHierarchy = false;
    private boolean active = true;

    private Chart chartOfAccounts;
    private Org organization;
    private transient ModuleService moduleService;

    public ChartOrgHolderSecurityImpl() {}
    
    @Override
    public void refresh() {
        super.refresh();
        moduleService = null;
        if ( moduleId != null ) {
            getModuleService();
        }
    }
    
    @Override
    public void refreshNonUpdateableReferences() {
        super.refreshNonUpdateableReferences();
        moduleService = null;
        if ( moduleId != null ) {
            getModuleService();
        }
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, Object> hashMap = new LinkedHashMap<String, Object>();
        hashMap.put("principalId", principalId);
        hashMap.put("moduleId", moduleId);
        hashMap.put("chartOfAccountsCode", chartOfAccountsCode);
        hashMap.put("organizationCode", organizationCode);
        return hashMap;
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

    public boolean isDescendOrgHierarchy() {
        return descendOrgHierarchy;
    }

    public void setDescendOrgHierarchy(boolean descendOrgHierarchy) {
        this.descendOrgHierarchy = descendOrgHierarchy;
    }

    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    public Org getOrganization() {
        return organization;
    }

    public void setOrganization(Org organization) {
        this.organization = organization;
    }

    public ModuleService getModuleService() {
        if ( moduleService == null ) {
            moduleService = getKualiModuleService().getModuleService(getModuleId());
        }
        return moduleService;
    }

    public void setModuleService(ModuleService moduleService) {
        this.moduleService = moduleService;
    }


    public static KualiModuleService getKualiModuleService() {
        if ( kualiModuleService == null ) {
            kualiModuleService = SpringContext.getBean(KualiModuleService.class);
        }
        return kualiModuleService;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
