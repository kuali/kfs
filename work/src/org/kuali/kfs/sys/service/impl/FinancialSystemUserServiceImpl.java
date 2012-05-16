/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.identity.FinancialSystemUserRoleTypeServiceImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.krad.service.BusinessObjectService;

public class FinancialSystemUserServiceImpl implements FinancialSystemUserService {

    private BusinessObjectService businessObjectService;
    private IdentityManagementService identityManagementService;
    private RoleService roleManagementService;
    private PersonService personService;
    private String userRoleId;
    private List<String> userRoleIdList = new ArrayList<String>(1);

    protected BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }

    protected IdentityManagementService getIdentityManagementService() {
        if (identityManagementService == null) {
            identityManagementService = SpringContext.getBean(IdentityManagementService.class);
        }
        return identityManagementService;
    }

    protected RoleService getRoleService() {
        if (roleManagementService == null) {
            roleManagementService = SpringContext.getBean(RoleService.class);
        }
        return roleManagementService;
    }
    
    protected PersonService getPersonService() {
        if (personService == null) {
            personService = SpringContext.getBean(PersonService.class);
        }
        return personService;        
    }

    protected String getUserRoleId() {
        if (userRoleId == null) {
            userRoleId = getRoleService().getRoleIdByNamespaceCodeAndName(KFSConstants.ParameterNamespaces.KFS, FinancialSystemUserRoleTypeServiceImpl.FINANCIAL_SYSTEM_USER_ROLE_NAME);
        }
        return userRoleId;
    }

    protected List<String> getUserRoleIdAsList() {
        if (userRoleIdList.isEmpty()) {
            userRoleIdList.add(getUserRoleId());
        }
        return userRoleIdList;
    }

    /**
     * @see org.kuali.kfs.sys.service.FinancialSystemUserService#isActiveFinancialSystemUser(org.kuali.rice.kim.api.identity.Person)
     */
    public boolean isActiveFinancialSystemUser(Person p) {
        return getRoleService().principalHasRole(p.getPrincipalId(), getUserRoleIdAsList(), new HashMap<String, String>());
    }

    /**
     * @see org.kuali.kfs.sys.service.FinancialSystemUserService#isActiveFinancialSystemUser(java.lang.String)
     */
    public boolean isActiveFinancialSystemUser(String principalId) {
        return getRoleService().principalHasRole(principalId, getUserRoleIdAsList(), new HashMap<String, String>());
    }

    /**
     * @see org.kuali.kfs.sys.service.FinancialSystemUserService#getPrimaryOrganization(org.kuali.rice.kim.api.identity.Person,
     *      java.lang.String)
     */
    public ChartOrgHolder getPrimaryOrganization(Person person, String namespaceCode) {
        if (person == null) {
            return null;
        }
        ChartOrgHolder chartOrgHolder = getOrganizationForFinancialSystemUser(person.getPrincipalId(), namespaceCode);
        if (chartOrgHolder == null) {
            chartOrgHolder = getOrganizationForNonFinancialSystemUser(person);
        }
        return (chartOrgHolder == null) ? new ChartOrgHolderImpl() : chartOrgHolder;
    }

    /**
     * @see org.kuali.kfs.sys.service.FinancialSystemUserService#getOrganizationByNamespaceCode(org.kuali.rice.kim.api.identity.Person,
     *      java.lang.String)
     */
    public ChartOrgHolder getPrimaryOrganization(String principalId, String namespaceCode) {
        ChartOrgHolder chartOrgHolder = getOrganizationForFinancialSystemUser(principalId, namespaceCode);
        if (chartOrgHolder == null) {
            chartOrgHolder = getOrganizationForNonFinancialSystemUser(getPersonService().getPerson(principalId));
        }
        return (chartOrgHolder == null) ? new ChartOrgHolderImpl() : chartOrgHolder;
    }
    
    protected ChartOrgHolder getOrganizationForFinancialSystemUser(String principalId, String namespaceCode) {
        if (principalId == null) {
            return null;
        }
        Map<String,String> qualification = new HashMap<String,String>(2);
        qualification.put(FinancialSystemUserRoleTypeServiceImpl.PERFORM_QUALIFIER_MATCH, "true");
        qualification.put(KimConstants.AttributeConstants.NAMESPACE_CODE, namespaceCode);
        List<Map<String,String>> roleQualifiers = getRoleService().getRoleQualifersForPrincipalByNamespaceAndRolename(principalId, KFSConstants.ParameterNamespaces.KFS, FinancialSystemUserRoleTypeServiceImpl.FINANCIAL_SYSTEM_USER_ROLE_NAME, qualification); 
        if ((roleQualifiers != null) && !roleQualifiers.isEmpty()) {
            return new ChartOrgHolderImpl(roleQualifiers.get(0).get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE), roleQualifiers.get(0).get(KfsKimAttributes.ORGANIZATION_CODE));
        }
        return null;
    }
    
    protected ChartOrgHolder getOrganizationForNonFinancialSystemUser(Person person) {
        if (person.getPrimaryDepartmentCode().contains("-")) {
            return new ChartOrgHolderImpl(StringUtils.substringBefore(person.getPrimaryDepartmentCode(), "-"), StringUtils.substringAfter(person.getPrimaryDepartmentCode(), "-"));
        }
        return null;
    }
    
    public Collection<String> getPrincipalIdsForFinancialSystemOrganizationUsers( String namespaceCode, ChartOrgHolder chartOrg) {
        Map<String,String> qualification = new HashMap<String,String>(4);
        qualification.put(FinancialSystemUserRoleTypeServiceImpl.PERFORM_QUALIFIER_MATCH, "true");
        qualification.put(KimConstants.AttributeConstants.NAMESPACE_CODE, namespaceCode);
        qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chartOrg.getChartOfAccountsCode());
        qualification.put(KfsKimAttributes.ORGANIZATION_CODE, chartOrg.getOrganizationCode());
        return getRoleService().getRoleMemberPrincipalIds(KFSConstants.ParameterNamespaces.KFS, FinancialSystemUserRoleTypeServiceImpl.FINANCIAL_SYSTEM_USER_ROLE_NAME, qualification);
    }
    
    public Collection<String> getPrincipalIdsForFinancialSystemOrganizationUsers(String namespaceCode, List<ChartOrgHolder> chartOrgs) {
        List<String> principalIds = new ArrayList<String>();
        for ( ChartOrgHolder chartOrg : chartOrgs ) {
            principalIds.addAll( getPrincipalIdsForFinancialSystemOrganizationUsers(namespaceCode, chartOrg));
        }
        return principalIds;
    }
    
    public class ChartOrgHolderImpl implements ChartOrgHolder {
        private String chartOfAccountsCode;
        private String organizationCode;
        
        
        public ChartOrgHolderImpl() {
        }

        public ChartOrgHolderImpl(String chartOfAccountsCode, String organizationCode) {
            this.chartOfAccountsCode = chartOfAccountsCode;
            this.organizationCode = organizationCode;
        }

        public Chart getChartOfAccounts() {
            Map<String, String> pk = new HashMap<String, String>(2);
            pk.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
            return (Chart) getBusinessObjectService().findByPrimaryKey(Chart.class, pk);
        }

        public Organization getOrganization() {
            Map<String, String> pk = new HashMap<String, String>(2);
            pk.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
            pk.put(KFSPropertyConstants.ORGANIZATION_CODE, organizationCode);
            return (Organization) getBusinessObjectService().findByPrimaryKey(Organization.class, pk);
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
}
