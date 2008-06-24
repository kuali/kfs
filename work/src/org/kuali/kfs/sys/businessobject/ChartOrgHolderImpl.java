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

import org.kuali.core.KualiModule;
import org.kuali.core.bo.Inactivateable;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.service.KualiModuleService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.OrganizationService;

public class ChartOrgHolderImpl implements ChartOrgHolder {

    private String chartOfAccountsCode;
    private String organizationCode;

    private Chart chartOfAccounts;
    private Org organization;

    private static transient OrganizationService organizationService;
    private static transient ChartService chartService;
    
    public ChartOrgHolderImpl( String chart, String orgCode ) {
        this.chartOfAccountsCode = chart;
        this.organizationCode = orgCode;
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
}
