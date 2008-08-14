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
import org.kuali.rice.kns.KualiModule;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.KualiModuleService;

public class FinancialSystemUserPrimaryOrganization extends PersistableBusinessObjectBase implements ChartOrgHolder, Inactivateable {

    private transient static KualiModuleService moduleService;
    
    private String personUniversalIdentifier;
    private String moduleId;
    private String chartOfAccountsCode;
    private String organizationCode;
    private boolean active = true;

    private Chart chartOfAccounts;
    private Org organization;
    private transient KualiModule module;

    @Override
    public void refresh() {
        super.refresh();
        module = null;
        if ( moduleId != null ) {
            getModule();
        }
    }
    
    @Override
    public void refreshNonUpdateableReferences() {
        super.refreshNonUpdateableReferences();
        module = null;
        if ( moduleId != null ) {
            getModule();
        }
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, Object> hashMap = new LinkedHashMap<String, Object>();
        hashMap.put("personUniversalIdentifier", personUniversalIdentifier);
        hashMap.put("moduleId", moduleId);
        hashMap.put("chartOfAccountsCode", chartOfAccountsCode);
        hashMap.put("organizationCode", organizationCode);
        return hashMap;
    }


    public String getPersonUniversalIdentifier() {
        return personUniversalIdentifier;
    }


    public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
        this.personUniversalIdentifier = personUniversalIdentifier;
    }


    public String getModuleId() {
        return moduleId;
    }


    public void setModuleId(String moduleId) {
        this.module = null;
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


    public KualiModule getModule() {
        if ( module == null ) {
            module = getModuleService().getModule(getModuleId());
        }
        return module;
    }


    public void setModule(KualiModule module) {
        this.module = module;
    }


    public static KualiModuleService getModuleService() {
        if ( moduleService == null ) {
            moduleService = SpringContext.getBean(KualiModuleService.class);
        }
        return moduleService;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
