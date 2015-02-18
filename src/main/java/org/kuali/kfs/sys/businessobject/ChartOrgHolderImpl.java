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
package org.kuali.kfs.sys.businessobject;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.sys.context.SpringContext;

public class ChartOrgHolderImpl implements ChartOrgHolder {

    protected String chartOfAccountsCode;
    protected String organizationCode;

    protected Chart chartOfAccounts;
    protected Organization organization;

    protected static transient OrganizationService organizationService;
    protected static transient ChartService chartService;

    public ChartOrgHolderImpl() {}
    
    public ChartOrgHolderImpl( String chartOfAccountsCode, String organizationCode ) {
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.organizationCode = organizationCode;
    }

    public ChartOrgHolderImpl( Organization org ) {
        this.chartOfAccountsCode = org.getChartOfAccountsCode();
        this.organizationCode = org.getOrganizationCode();
        this.organization = org;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }


    public String getOrganizationCode() {
        return organizationCode;
    }

    public Chart getChartOfAccounts() {
        if ( chartOfAccounts == null && StringUtils.isNotBlank(chartOfAccountsCode) ) {
            chartOfAccounts = getChartService().getByPrimaryId(chartOfAccountsCode);
        }
        return chartOfAccounts;
    }

    public Organization getOrganization() {
        if ( organization == null && StringUtils.isNotBlank(chartOfAccountsCode) && StringUtils.isNotBlank(organizationCode) ) {
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
        if ( obj == null || !(obj instanceof ChartOrgHolder) ) {
            return false;
        }
        return StringUtils.equals( chartOfAccountsCode, ((ChartOrgHolder)obj).getChartOfAccountsCode() )
                && StringUtils.equals( organizationCode, ((ChartOrgHolder)obj).getOrganizationCode() );
    }
    
    @Override
    public int hashCode() {
        return String.valueOf(chartOfAccountsCode).hashCode() + String.valueOf(organizationCode).hashCode();
    }
    
    @Override
    public String toString() {        
        return String.valueOf(chartOfAccountsCode) + "-" + String.valueOf(organizationCode);
    }
}
