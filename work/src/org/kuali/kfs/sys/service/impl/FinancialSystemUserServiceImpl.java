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
package org.kuali.kfs.sys.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.identity.FinancialSystemUserRoleTypeServiceImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kns.service.BusinessObjectService;

public class FinancialSystemUserServiceImpl implements FinancialSystemUserService {

    private BusinessObjectService businessObjectService;
    private IdentityManagementService identityManagementService;
    private RoleManagementService roleManagementService;
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

    protected RoleManagementService getRoleManagementService() {
        if (roleManagementService == null) {
            roleManagementService = SpringContext.getBean(RoleManagementService.class);
        }
        return roleManagementService;
    }

    protected String getUserRoleId() {
        if (userRoleId == null) {
            userRoleId = getRoleManagementService().getRoleIdByName(KFSConstants.ParameterNamespaces.KFS, FinancialSystemUserRoleTypeServiceImpl.FINANCIAL_SYSTEM_USER_ROLE_NAME);
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
     * @see org.kuali.kfs.sys.service.FinancialSystemUserService#isActiveFinancialSystemUser(org.kuali.rice.kim.bo.Person)
     */
    public boolean isActiveFinancialSystemUser(Person p) {
        return getRoleManagementService().principalHasRole(p.getPrincipalId(), getUserRoleIdAsList(), null);
    }

    /**
     * @see org.kuali.kfs.sys.service.FinancialSystemUserService#getOrganizationByNamespaceCode(org.kuali.rice.kim.bo.Person,
     *      java.lang.String)
     */
    public ChartOrgHolder getOrganizationByNamespaceCode(Person person, String namespaceCode) {
        if (person == null) {
            return null;
        }
        return getOrganizationByNamespaceCode(person.getPrincipalId(), namespaceCode);
    }

    /**
     * @see org.kuali.kfs.sys.service.FinancialSystemUserService#getOrganizationByNamespaceCode(org.kuali.rice.kim.bo.Person,
     *      java.lang.String)
     */
    public ChartOrgHolder getOrganizationByNamespaceCode(String principalId, String namespaceCode) {
        if (principalId == null) {
            return null;
        }
        AttributeSet qualification = new AttributeSet(2);
        qualification.put(FinancialSystemUserRoleTypeServiceImpl.PERFORM_QUALIFIER_MATCH, "true");
        qualification.put(KfsKimAttributes.NAMESPACE_CODE, namespaceCode);
        List<AttributeSet> roleQualifiers = getRoleManagementService().getRoleQualifiersForPrincipal(principalId, KFSConstants.ParameterNamespaces.KFS, FinancialSystemUserRoleTypeServiceImpl.FINANCIAL_SYSTEM_USER_ROLE_NAME, qualification);
        ChartOrgHolderImpl result = new ChartOrgHolderImpl();
        if (!roleQualifiers.isEmpty()) {
            result.setChartOfAccountsCode(roleQualifiers.get(0).get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE));
            result.setOrganizationCode(roleQualifiers.get(0).get(KfsKimAttributes.ORGANIZATION_CODE));
        }
        return result;
    }
    
    public Collection<String> getPrincipalIdsForOrganizationUsers( String namespaceCode, ChartOrgHolder chartOrg) {
        AttributeSet qualification = new AttributeSet(4);
        qualification.put(FinancialSystemUserRoleTypeServiceImpl.PERFORM_QUALIFIER_MATCH, "true");
        qualification.put(KfsKimAttributes.NAMESPACE_CODE, namespaceCode);
        qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chartOrg.getChartOfAccountsCode());
        qualification.put(KfsKimAttributes.ORGANIZATION_CODE, chartOrg.getOrganizationCode());
        return getRoleManagementService().getRoleMemberPrincipalIds(KFSConstants.ParameterNamespaces.KFS, FinancialSystemUserRoleTypeServiceImpl.FINANCIAL_SYSTEM_USER_ROLE_NAME, qualification);
    }
    
    public Collection<String> getPrincipalIdsForOrganizationUsers(String namespaceCode, List<ChartOrgHolder> chartOrgs) {
        List<String> principalIds = new ArrayList<String>();
        for ( ChartOrgHolder chartOrg : chartOrgs ) {
            principalIds.addAll( getPrincipalIdsForOrganizationUsers(namespaceCode, chartOrg));
        }
        return principalIds;
    }
    
    public class ChartOrgHolderImpl implements ChartOrgHolder {
        private String chartOfAccountsCode;
        private String organizationCode;

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
