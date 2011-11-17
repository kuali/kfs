/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.sys.batch.service.impl;

import org.kuali.kfs.sys.batch.service.CacheService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.common.template.Template;
import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.location.framework.campus.CampusValuesFinder;
import org.springframework.cache.annotation.CacheEvict;

/**
 * @see org.kuali.kfs.sys.batch.service.CacheService
 */
@NonTransactional
public class CacheServiceImpl implements CacheService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CacheServiceImpl.class);

    private RoleService roleManagementService;
    private IdentityManagementService identityManagementService;
    private ParameterService parameterService;

    /**
     * @see org.kuali.kfs.sys.batch.service.CacheService#clearSystemCache()
     */
    public void clearSystemCache() {
        clearMethodCache();
        clearKIMCache();
        clearParameterCache();
        new CampusValuesFinder().clearInternalCache();
    }

    /**
     * Clears out service methods cache by calling adminstrators flushAll
     */
    @CacheEvict(allEntries=true, value = { "" })
    protected void clearMethodCache() {
        LOG.info("clearing spring method cache ...");
    }

    /**
     * Clears out KIM cache by calling flush methods on role and identity services
     */
    @CacheEvict(value={Responsibility.Cache.NAME, Template.Cache.NAME + "{Responsibility}"}, allEntries = true)
    protected void clearKIMCache() {
        LOG.info("clearing KIM role & identity service cache ...");

        roleManagementService.flushRoleCaches();
        identityManagementService.flushAllCaches();
        //RICE20 need to find out how to flush the Responsibility Cache in rice; can we flush this on our own?? 
        //       added @CacheEvict to function (ref - org.kuali.rice.kim.api.responsibility.ResponsibilityService) 
        //SpringContext.getBean(RiceCacheAdministrator.class).flushGroup("ResponsibilityImpl");
    }

    /**
     * Clears out parameter cache by calling flush method on parameter service
     */
    protected void clearParameterCache() {
        LOG.info("clearing parameter cache ...");

        parameterService.clearCache();
    }
    
    /**
     * Gets the roleManagementService attribute.
     * 
     * @return Returns the roleManagementService.
     */
    protected RoleService getRoleService() {
        return roleManagementService;
    }

    /**
     * Sets the roleManagementService attribute value.
     * 
     * @param roleManagementService The roleManagementService to set.
     */
    public void setRoleService(RoleService roleManagementService) {
        this.roleManagementService = roleManagementService;
    }

    /**
     * Gets the identityManagementService attribute.
     * 
     * @return Returns the identityManagementService.
     */
    protected IdentityManagementService getIdentityManagementService() {
        return identityManagementService;
    }

    /**
     * Sets the identityManagementService attribute value.
     * 
     * @param identityManagementService The identityManagementService to set.
     */
    public void setIdentityManagementService(IdentityManagementService identityManagementService) {
        this.identityManagementService = identityManagementService;
    }

    /**
     * Gets the parameterService attribute.
     * 
     * @return Returns the parameterService.
     */
    protected ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
