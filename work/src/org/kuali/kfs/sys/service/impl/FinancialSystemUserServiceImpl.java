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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountResponsibility;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KimAttributes;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.GlobalVariables;

public class FinancialSystemUserServiceImpl implements FinancialSystemUserService {

    private BusinessObjectService businessObjectService;
    private IdentityManagementService identityManagementService;
    private RoleManagementService roleManagementService;
    private String userRoleId;
    private List<String> userRoleIdList = new ArrayList<String>( 1 );

    protected BusinessObjectService getBusinessObjectService() {
        if ( businessObjectService == null ) {
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
        if ( userRoleId == null ) {
            userRoleId = getRoleManagementService().getRoleIdByName(KFSConstants.ParameterNamespaces.KFS, KimConstants.KIM_ROLE_NAME_USER);
        }
        return userRoleId;
    }
    
    protected List<String> getUserRoleIdAsList() {
        if ( userRoleIdList.isEmpty() ) {
            userRoleIdList.add(getUserRoleId());
        }
        return userRoleIdList;
    }
    
    public ChartOrgHolder getPrimaryChartOrganization( Person p ) {
        return getOrganizationByNamespaceCode(p, "");
    }

    public boolean isActiveFinancialSystemUser( Person p ) {
        return getRoleManagementService().principalHasRole(p.getPrincipalId(), getUserRoleIdAsList(), null);
    }
     
    public ChartOrgHolder getOrganizationByModuleId( String moduleId ) {
        return getOrganizationByNamespaceCode(GlobalVariables.getUserSession().getPerson(), moduleId);    
    }

    public ChartOrgHolder getOrganizationByNamespaceCode( Person person, String namespaceCode ) {
        if ( person == null ) {
            return null;
        }
        AttributeSet qualification = new AttributeSet( 1 );
        qualification.put(KimAttributes.NAMESPACE_CODE, namespaceCode);
        List<AttributeSet> roleQualifiers = getRoleManagementService().getRoleQualifiersForPrincipal(person.getPrincipalId(), KFSConstants.ParameterNamespaces.KFS, KimConstants.KIM_ROLE_NAME_USER, qualification);
        ChartOrgHolderImpl result = new ChartOrgHolderImpl();
        if ( !roleQualifiers.isEmpty() ) {
            result.setChartOfAccountsCode( roleQualifiers.get(0).get(KimAttributes.CHART_OF_ACCOUNTS_CODE));
            result.setOrganizationCode( roleQualifiers.get(0).get(KimAttributes.ORGANIZATION_CODE));
        }
        return result;
    }
    
    @Deprecated
    public boolean isResponsibleForAccount(String id, String chartCode, String accountNumber) {
        boolean isResponsible = false;

        if (!StringUtils.isEmpty(chartCode) && !StringUtils.isEmpty(accountNumber)) {
            String accountKey = buildAccountKey(chartCode, accountNumber);

            isResponsible = getAccountResponsibilities(id).containsKey(accountKey);
        }

        return isResponsible;
    }
    
    private String buildAccountKey(String chartCode, String accountNumber) {
        if (StringUtils.isEmpty(chartCode)) {
            throw new IllegalArgumentException("invalid (blank) chartCode");
        }
        if (StringUtils.isEmpty(accountNumber)) {
            throw new IllegalArgumentException("invalid (blank) accountNumber");
        }

        String accountKey = chartCode + "::" + accountNumber;
        return accountKey;
    }
    
    private Map getAccountResponsibilities(String id) {
        Map accountMap = new HashMap();
        List accountList = SpringContext.getBean(AccountService.class).getAccountsThatUserIsResponsibleFor(SpringContext.getBean(PersonService.class).getPerson(id));
        for (Iterator i = accountList.iterator(); i.hasNext();) {
            AccountResponsibility accountResponsibility = (AccountResponsibility) i.next();
            Account account = accountResponsibility.getAccount();

            accountMap.put(buildAccountKey(account.getChartOfAccountsCode(), account.getAccountNumber()), accountResponsibility);
        }
        return accountMap;
    }

    public class ChartOrgHolderImpl implements ChartOrgHolder {
        private String chartOfAccountsCode;
        private String organizationCode;
        
        public Chart getChartOfAccounts() {
            Map<String,String> pk = new HashMap<String, String>( 2 );
            pk.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
            return (Chart)getBusinessObjectService().findByPrimaryKey(Chart.class, pk);
        }

        public Organization getOrganization() {
            Map<String,String> pk = new HashMap<String, String>( 2 );
            pk.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
            pk.put(KFSPropertyConstants.ORGANIZATION_CODE, organizationCode);
            return (Organization)getBusinessObjectService().findByPrimaryKey(Organization.class, pk);
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
        
        
    }
}

