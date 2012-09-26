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
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.role.RoleService;

public class FinancialSystemUserServiceImpl implements FinancialSystemUserService {

    protected ChartService chartService;
    protected OrganizationService organizationService;
    protected RoleService roleService;
    protected PersonService personService;
    protected String userRoleId;
    private final List<String> userRoleIdList = new ArrayList<String>(1);

    protected String getUserRoleId() {
        if (userRoleId == null) {
            userRoleId = roleService.getRoleIdByNamespaceCodeAndName(KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.SysKimApiConstants.KFS_USER_ROLE_NAME);
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
    @Override
    public boolean isActiveFinancialSystemUser(Person p) {
        if ( p == null ) {
            return false;
        }
        return isActiveFinancialSystemUser(p.getPrincipalId());
    }

    /**
     * @see org.kuali.kfs.sys.service.FinancialSystemUserService#isActiveFinancialSystemUser(java.lang.String)
     */
    @Override
    public boolean isActiveFinancialSystemUser(String principalId) {
        if ( StringUtils.isBlank(principalId)) {
            return false;
        }
        return roleService.principalHasRole(principalId, getUserRoleIdAsList(), new HashMap<String, String>());
    }

    /**
     * @see org.kuali.kfs.sys.service.FinancialSystemUserService#getPrimaryOrganization(org.kuali.rice.kim.api.identity.Person,
     *      java.lang.String)
     */
    @Override
    public ChartOrgHolder getPrimaryOrganization(Person person, String namespaceCode) {
        if ( person == null ) {
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
    @Override
    public ChartOrgHolder getPrimaryOrganization(String principalId, String namespaceCode) {
        if ( StringUtils.isBlank(principalId) ) {
            return new ChartOrgHolderImpl();
        }
        ChartOrgHolder chartOrgHolder = getOrganizationForFinancialSystemUser(principalId, namespaceCode);
        if (chartOrgHolder == null) {
            chartOrgHolder = getOrganizationForNonFinancialSystemUser(personService.getPerson(principalId));
        }
        return (chartOrgHolder == null) ? new ChartOrgHolderImpl() : chartOrgHolder;
    }

    protected ChartOrgHolder getOrganizationForFinancialSystemUser(String principalId, String namespaceCode) {
        if ( StringUtils.isBlank( principalId ) ) {
            return null;
        }
        Map<String,String> qualification = new HashMap<String,String>(2);
        qualification.put(FinancialSystemUserRoleTypeServiceImpl.PERFORM_QUALIFIER_MATCH, "true");
        qualification.put(KimConstants.AttributeConstants.NAMESPACE_CODE, namespaceCode);
        List<Map<String,String>> roleQualifiers = roleService.getRoleQualifersForPrincipalByNamespaceAndRolename(principalId, KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.SysKimApiConstants.KFS_USER_ROLE_NAME, qualification);
        if ((roleQualifiers != null) && !roleQualifiers.isEmpty()) {
            return new ChartOrgHolderImpl(roleQualifiers.get(0).get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE), roleQualifiers.get(0).get(KfsKimAttributes.ORGANIZATION_CODE));
        }
        return null;
    }

    @Deprecated
    protected ChartOrgHolder getOrganizationForNonFinancialSystemUser(Person person) {
        // HACK ALERT!!!!! - This is to support the original universal user table setup where the home department
        // was encoded as CAMPUS-ORG (Where campus happened to match the chart) in the original FS_UNIVERSAL_USR_T table.
        // This **REALLY** needs a new implementation
        if ( person != null && person.getPrimaryDepartmentCode().contains("-")) {
            return new ChartOrgHolderImpl(StringUtils.substringBefore(person.getPrimaryDepartmentCode(), "-"), StringUtils.substringAfter(person.getPrimaryDepartmentCode(), "-"));
        }
        return null;
    }

    @Override
    public Collection<String> getPrincipalIdsForFinancialSystemOrganizationUsers( String namespaceCode, ChartOrgHolder chartOrg) {
        Map<String,String> qualification = new HashMap<String,String>(4);
        qualification.put(FinancialSystemUserRoleTypeServiceImpl.PERFORM_QUALIFIER_MATCH, "true");
        qualification.put(KimConstants.AttributeConstants.NAMESPACE_CODE, namespaceCode);
        qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chartOrg.getChartOfAccountsCode());
        qualification.put(KfsKimAttributes.ORGANIZATION_CODE, chartOrg.getOrganizationCode());
        return roleService.getRoleMemberPrincipalIds(KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.SysKimApiConstants.KFS_USER_ROLE_NAME, qualification);
    }

    @Override
    public Collection<String> getPrincipalIdsForFinancialSystemOrganizationUsers(String namespaceCode, List<ChartOrgHolder> chartOrgs) {
        List<String> principalIds = new ArrayList<String>();
        for ( ChartOrgHolder chartOrg : chartOrgs ) {
            principalIds.addAll( getPrincipalIdsForFinancialSystemOrganizationUsers(namespaceCode, chartOrg));
        }
        return principalIds;
    }

    public class ChartOrgHolderImpl implements ChartOrgHolder {
        protected String chartOfAccountsCode;
        protected String organizationCode;


        public ChartOrgHolderImpl() {}

        public ChartOrgHolderImpl(String chartOfAccountsCode, String organizationCode) {
            this.chartOfAccountsCode = chartOfAccountsCode;
            this.organizationCode = organizationCode;
        }

        @Override
        public Chart getChartOfAccounts() {
            return chartService.getByPrimaryId(chartOfAccountsCode);
        }

        @Override
        public Organization getOrganization() {
            return organizationService.getByPrimaryIdWithCaching(chartOfAccountsCode, organizationCode);
        }

        @Override
        public String getChartOfAccountsCode() {
            return chartOfAccountsCode;
        }

        public void setChartOfAccountsCode(String chartOfAccountsCode) {
            this.chartOfAccountsCode = chartOfAccountsCode;
        }

        @Override
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

    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }
}
