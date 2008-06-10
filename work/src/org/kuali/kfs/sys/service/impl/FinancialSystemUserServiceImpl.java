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
package org.kuali.kfs.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.RicePropertyConstants;
import org.kuali.core.KualiModule;
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.PersonPayrollId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.bo.user.UserId;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.impl.UniversalUserServiceImpl;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.authorization.FinancialSystemModuleAuthorizerBase;
import org.kuali.kfs.bo.ChartOrgHolder;
import org.kuali.kfs.bo.ChartOrgHolderImpl;
import org.kuali.kfs.bo.FinancialSystemUser;
import org.kuali.kfs.bo.FinancialSystemUserPrimaryOrganization;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.FinancialSystemUserService;
import org.kuali.kfs.service.ParameterService;

public class FinancialSystemUserServiceImpl extends UniversalUserServiceImpl implements FinancialSystemUserService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FinancialSystemUserServiceImpl.class);

    protected BusinessObjectService businessObjectService;
    protected String administrationWorkgroupName;
    protected ParameterService parameterService;
    
    public BusinessObjectService getBusinessObjectService() {
        if ( businessObjectService == null ) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }


    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


    /**
     * @see org.kuali.kfs.service.KfsUniversalUserService#getUniversalUserByPersonPayrollIdentifier(java.lang.String)
     */
    public UniversalUser getUniversalUserByPersonPayrollIdentifier(String personPayrollIdentifier) {
        try {
            return getUniversalUser(new PersonPayrollId(personPayrollIdentifier));
        }
        catch (UserNotFoundException e) {
            LOG.debug("Cannot find UniversalUser for payroll identifier: " + personPayrollIdentifier);
            return null;
        }
    }

    public FinancialSystemUser getFinancialSystemUser(String personUniversalIdentifier) {
        try {
            return convertUniversalUserToFinancialSystemUser( getUniversalUser(personUniversalIdentifier) );
        } catch ( UserNotFoundException ex ) {
            return null;
        }
    }
    
    public FinancialSystemUser getFinancialSystemUser(UserId userId) throws UserNotFoundException {
        return convertUniversalUserToFinancialSystemUser( getUniversalUser( userId ) );
    }
    
    /** Converts a given universal user into a FinancialSystemUser object.  If the object passed in is a FinancialSystemUser
     * it is simply returned. 
     */
    public FinancialSystemUser convertUniversalUserToFinancialSystemUser( UniversalUser user ) {
        if ( user == null ) {
            return null;
        }
        if ( user instanceof FinancialSystemUser ) {
            return (FinancialSystemUser)user;
        }
        HashMap<String,Object> pk = new HashMap<String, Object>(1);
        pk.put(RicePropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, user.getPersonUniversalIdentifier());
        FinancialSystemUser financialSystemUser = (FinancialSystemUser)getBusinessObjectService().findByPrimaryKey(FinancialSystemUser.class, pk);
        if ( financialSystemUser != null ) {
            // TODO:  replace with UU BO set
            financialSystemUser.setPersonUniversalIdentifier(user.getPersonUniversalIdentifier());
        }
        return financialSystemUser;
    }

    /** Checks if the user is an active KFS User.  Performs the conversion from UniversalUser to FinancialSystemUser automatically.
     * 
     */
    public boolean isActiveFinancialSystemUser( UniversalUser user ) {
        FinancialSystemUser financialSystemUser = convertUniversalUserToFinancialSystemUser(user);
        if ( financialSystemUser == null ) {
            return false;
        }
        return financialSystemUser.isActiveFinancialSystemUser();
    }
    
    /** Get the default organization for the given module for the currently logged in user. */
    public ChartOrgHolder getOrganizationByModuleId( String moduleId ) {
        return getOrganizationByModuleId( GlobalVariables.getUserSession().getFinancialSystemUser(), moduleId);
    }
    
    /** Get the user's default organization for the given module.  If no record exists for that module,
     * then the default from their 
     * 
     * @param user
     * @param moduleId
     * @return
     */
    public ChartOrgHolder getOrganizationByModuleId( UniversalUser user, String moduleId ) {
        FinancialSystemUser financialSystemUser = convertUniversalUserToFinancialSystemUser(user);
        if ( financialSystemUser == null ) {
            return null;
        }
        // attempt to get a default organization record
        ChartOrgHolder defOrg = null;
        defOrg = financialSystemUser.getPrimaryOrganizationByModuleId(moduleId);
        if ( defOrg == null ) {
            if ( financialSystemUser.getOrganizationCode() != null && financialSystemUser.getChartOfAccountsCode() != null ) {
                defOrg = new ChartOrgHolderImpl( financialSystemUser.getChartOfAccountsCode(), financialSystemUser.getOrganizationCode() );
            } else if ( financialSystemUser.getPrimaryDepartmentCode().contains( "-" ) ) {
                defOrg = new ChartOrgHolderImpl(
                        StringUtils.substringBefore(financialSystemUser.getPrimaryDepartmentCode(), "-"),
                        StringUtils.substringAfter(financialSystemUser.getPrimaryDepartmentCode(), "-") );
            }
        }
        return defOrg;
    }

    /** Get the security organizations for the given module for the currently logged in user. */
    public List<? extends ChartOrgHolder> getSecurityOrganizationsByModuleId( String moduleId ) {
        return getSecurityOrganizationsByModuleId( GlobalVariables.getUserSession().getFinancialSystemUser(), moduleId);
    }
    
    /** Get the user's security organizations for the given module.
     * 
     * @param user
     * @param moduleId
     * @return
     */
    public List<? extends ChartOrgHolder> getSecurityOrganizationsByModuleId( UniversalUser user, String moduleId ) {
        FinancialSystemUser financialSystemUser = null;
        if ( user instanceof FinancialSystemUser ) {
            financialSystemUser = (FinancialSystemUser)user;
        } else {
            financialSystemUser = convertUniversalUserToFinancialSystemUser(user);
        }
        // attempt to get a default organization record
        return financialSystemUser.getOrganizationSecurityByModuleId(moduleId);
    }
    
    /**
     * 
     * @see org.kuali.module.chart.service.ChartUserService#isAdministratorUser(org.kuali.module.chart.bo.ChartUser)
     */
    public boolean isAdministratorUser(FinancialSystemUser user) {
        if (administrationWorkgroupName == null) {
            administrationWorkgroupName = getParameterService().getParameterValue(ParameterConstants.CHART_DOCUMENT.class, KFSConstants.MAINTENANCE_ADMIN_WORKGROUP_PARM_NM);
        }
        return isMember(user, administrationWorkgroupName);
    }


    public ParameterService getParameterService() {
        // JHK: Since this service is initialized as part of the KNS, it can not reference (in Spring)
        // a service that is only part of KFS - so we need to use SpringContext to use it after startup
        if ( parameterService == null ) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }


    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
    public boolean isActiveForAnyModule( FinancialSystemUser user ) {
        for ( KualiModule module : getKualiModuleService().getInstalledModules() ) {
            if ( module.getModuleAuthorizer() != null ) {
                if ( ((FinancialSystemModuleAuthorizerBase)module.getModuleAuthorizer()).canAccessModule(user) ) {
                    return true;
                }
            } else {
                LOG.error( "ModuleAuthorizer for module " + module.getModuleId() + " is null!" );
            }
        }
        return false;
    }
    
    public boolean isActiveForModule( FinancialSystemUser user, String moduleId ) {
        for ( KualiModule module : getKualiModuleService().getInstalledModules() ) {
            if ( module.getModuleId().equals(moduleId) ) {
                if ( module.getModuleAuthorizer() != null ) {
                    if ( ((FinancialSystemModuleAuthorizerBase)module.getModuleAuthorizer()).canAccessModule(user) ) {
                        return true;
                    }
                } else {
                    LOG.error( "ModuleAuthorizer for module " + module.getModuleId() + " is null!" );
                }
            }
        }
        return false;
    }
    
    /**
     * Override this method to skip behavior when the main BO is FinancialSystemUser.  (Since FinancialSystemUser is a UniversalUser.)
     * 
     * @see org.kuali.core.service.impl.UniversalUserServiceImpl#resolveUserIdentifiersToUniversalIdentifiers(org.kuali.core.bo.PersistableBusinessObject, java.util.Map)
     */
    @Override
    public Map resolveUserIdentifiersToUniversalIdentifiers(PersistableBusinessObject businessObject, Map fieldValues) {
        // don't do this for the FinancialSystemUser object - interferes with the document
        if ( businessObject instanceof FinancialSystemUser ) {
            return fieldValues;
        }
        return super.resolveUserIdentifiersToUniversalIdentifiers(businessObject, fieldValues);
    }
}
