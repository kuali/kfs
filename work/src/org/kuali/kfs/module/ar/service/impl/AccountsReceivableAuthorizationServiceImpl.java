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
package org.kuali.kfs.module.ar.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.defaultvalue.ValueFinderUtil;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.document.service.SystemInformationService;
import org.kuali.kfs.module.ar.service.AccountsReceivableAuthorizationService;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.BusinessObjectService;

public class AccountsReceivableAuthorizationServiceImpl implements AccountsReceivableAuthorizationService {

    private static final String KULUSER_CHART_CD = "UA";
    private static final String KULUSER_ORG_CD = "AR";
    
    private FinancialSystemUserService knsAuthzService;
    private BusinessObjectService boService;
    private OrganizationService orgService;
    private UniversityDateService universityDateService;
    private SystemInformationService sysInfoService;
    
    public boolean currentUserBelongsToBillingOrg() {
        Person currentUser = ValueFinderUtil.getCurrentPerson();
        if (currentUser == null) {
            throw new IllegalArgumentException("No user session is currently setup, so there is no Current User.");
        }
        return personBelongsToBillingOrg(currentUser);
    }

    public boolean personBelongsToBillingOrg(Person person) {
        
        //  get the person's org from the kns authz system
        Organization personHomeOrg = personHomeOrg(person);
        
        //  if the person's home org doesn't exist or is not setup right, then fail
        if (personHomeOrg == null) {
            return false;
        }
        else if (StringUtils.isBlank(personHomeOrg.getOrganizationCode()) || StringUtils.isBlank(personHomeOrg.getChartOfAccountsCode())) {
            return false;
        }
        
        return isOrgABillingOrg(personHomeOrg);
    }

    public Organization personHomeOrg(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("A null or invalid person object was passed in.");
        }
        
        ChartOrgHolder personChartOrg = knsAuthzService.getOrganizationByNamespaceCode(person, ArConstants.AR_NAMESPACE_CODE);
        
        // *************************************************************
        // SPECIAL CASE HANDLING WHILE KIM AUTH IS IN PROGRESS
        //
        //TODO remove this later when KIM stuff is stabilized
        //
        if (personChartOrg == null || StringUtils.isBlank(personChartOrg.getChartOfAccountsCode()) || StringUtils.isBlank(personChartOrg.getOrganizationCode())) {
            return orgService.getByPrimaryId(KULUSER_CHART_CD, KULUSER_ORG_CD);
        }

        if (personChartOrg == null) {
            return null;
        }
        return personChartOrg.getOrganization();
    }
    
    public Organization currentUserHomeOrg() {
        Person currentUser = ValueFinderUtil.getCurrentPerson();
        if (currentUser == null) {
            throw new IllegalArgumentException("No user session is currently setup, so there is no Current User.");
        }
        return personHomeOrg(currentUser);
    }
    
    public boolean isOrgABillingOrg(Organization org) {
        return isOrgABillingOrg(org.getChartOfAccountsCode(), org.getOrganizationCode());
    }
    
    public boolean isOrgABillingOrg(String chartOfAccountsCode, String organizationCode) {
        
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode", chartOfAccountsCode);
        criteria.put("organizationCode", organizationCode);
        OrganizationOptions organizationOptions = (OrganizationOptions) boService.findByPrimaryKey(OrganizationOptions.class, criteria);
        
        return (organizationOptions != null);
    }
    
    public boolean isOrgAProcessingOrgInCurrentFiscalYear(Organization org) {
        return isOrgAProcessingOrgInCurrentFiscalYear(org.getChartOfAccountsCode(), org.getOrganizationCode());
    }

    public boolean isOrgAProcessingOrgInCurrentFiscalYear(String chartOfAccountsCode, String organizationCode) {
        SystemInformation sysInfo = sysInfoService.getByProcessingChartOrgAndFiscalYear(chartOfAccountsCode, organizationCode, universityDateService.getCurrentFiscalYear());
        return (sysInfo != null);
    }
    
    public boolean getBillingOrgsInProcessingOrg(Organization org) {
        throw new UnsupportedOperationException("This method is not yet implemented.");
    }
    
    public boolean getBillingOrgsInProcessingOrg(String chartOfAccountsCode, String organizationCode) {
        throw new UnsupportedOperationException("This method is not yet implemented.");
    }
    
    public void setKnsAuthzService(FinancialSystemUserService knsAuthzService) {
        this.knsAuthzService = knsAuthzService;
    }

    public void setBoService(BusinessObjectService boService) {
        this.boService = boService;
    }

    public void setOrgService(OrganizationService orgService) {
        this.orgService = orgService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public void setSysInfoService(SystemInformationService sysInfoService) {
        this.sysInfoService = sysInfoService;
    }

}
