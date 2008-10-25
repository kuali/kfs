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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.businessobject.AccountResponsibility;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.businessobject.ChartOrgHolderImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.KNSAuthorizationService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSPropertyConstants;

public class KNSAuthorizationServiceImpl implements KNSAuthorizationService {

    protected BusinessObjectService businessObjectService;
    protected IdentityManagementService identityManagementService;

    public BusinessObjectService getBusinessObjectService() {
        if ( businessObjectService == null ) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }
    
    public IdentityManagementService getIdentityManagementService() {
        if (identityManagementService == null) {
            identityManagementService = KIMServiceLocator.getIdentityManagementService();
        }
        return identityManagementService;
    }        
    
    public ChartOrgHolder getPrimaryChartOrganization( Person p ) {
        HashMap<String,Object> pk = new HashMap<String, Object>(1);
        pk.put(KNSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, p.getPrincipalId());
        ChartOrgHolder chartOrg = (ChartOrgHolderImpl) getBusinessObjectService().findByPrimaryKey(ChartOrgHolderImpl.class, pk);
        return chartOrg;
    }

    public boolean isActive( Person p ) {
        return getIdentityManagementService().getPrincipal(p.getPrincipalId()).isActive();
    }
     
    public ChartOrgHolder getOrganizationByModuleId( String moduleId ) {
        return getOrganizationByModuleId(GlobalVariables.getUserSession().getPerson(), moduleId);    
    }

    public ChartOrgHolder getOrganizationByModuleId( Person p, String moduleId ) {
        //HashMap<String,Object> pk = new HashMap<String, Object>(1);
        //pk.put(KNSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, p.getPrincipalId());
        //ChartOrgHolderImpl chartOrg = (ChartOrgHolderImpl) getBusinessObjectService().findByPrimaryKey(ChartOrgHolderImpl.class, pk);
        //return chartOrg.getPrimaryOrganizationByModuleId(moduleId);
        return getPrimaryChartOrganization(p);
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
    
}

