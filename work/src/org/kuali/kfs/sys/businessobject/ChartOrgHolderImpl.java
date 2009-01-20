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
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.TypedArrayList;

public class ChartOrgHolderImpl implements ChartOrgHolder {

    protected String chartOfAccountsCode;
    protected String organizationCode;

    protected Chart chartOfAccounts;
    protected Organization organization;

    protected static transient OrganizationService organizationService;
    protected static transient ChartService chartService;

    public ChartOrgHolderImpl() {
        // TODO Auto-generated constructor stub
    }
    
    public ChartOrgHolderImpl( String chartOfAccountsCode, String organizationCode ) {
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.organizationCode = organizationCode;
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

    public Organization getOrganization() {
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


    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }
    
    @Override
    public boolean equals(Object obj) {
        if ( !(obj instanceof ChartOrgHolder) ) {
            return false;
        }
        return chartOfAccountsCode.equals(((ChartOrgHolder)obj).getChartOfAccountsCode())
                && organizationCode.equals(((ChartOrgHolder)obj).getOrganizationCode());
    }
    
    @Override
    public int hashCode() {
        return chartOfAccountsCode.hashCode() + organizationCode.hashCode();
    }
}
