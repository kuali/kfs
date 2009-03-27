/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sys.batch.service.impl;

import java.util.List;

import org.kuali.kfs.gl.dataaccess.CachingDao;
import org.kuali.kfs.sys.batch.service.CacheService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.RoleManagementService;

import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**
 * @see com.rsmart.kuali.kfs.sys.batch.service.CacheService
 */ 
@NonTransactional
public class CacheServiceImpl implements CacheService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CacheServiceImpl.class);
    
    private List<GeneralCacheAdministrator> cacheAdminstrators;
    private RoleManagementService roleManagementService;
    private IdentityManagementService identityManagementService;
    private CachingDao cachingDao;

    /**
     * @see com.rsmart.kuali.kfs.sys.batch.service.CacheService#clearSystemCache()
     */
    public void clearSystemCache() {
        clearMethodCache();
        clearKIMCache();
        clearCachingDaoCache();
    }

    /**
     * Clears out service methods cache by calling adminstrators flushAll
     */
    protected void clearMethodCache() {
        LOG.info("clearing spring method cache ...");
        
        if (cacheAdminstrators != null) {
            for (GeneralCacheAdministrator cache : cacheAdminstrators) {
                cache.flushAll();
            }
        }
    }

    /**
     * Clears out KIM cache by calling flush methods on role and identity services
     */
    protected void clearKIMCache() {
        LOG.info("clearing KIM role & identity service cache ...");
        
        roleManagementService.flushRoleCaches();
        identityManagementService.flushAllCaches();
    }

    /**
     * Clears out GL batch job cache by calling flush method on caching DAO
     */
    protected void clearCachingDaoCache() {
        LOG.info("clearing caching dao jdbc cache ...");
        
        cachingDao.flushCache();
    }

    /**
     * Gets the cacheAdminstrators attribute.
     * 
     * @return Returns the cacheAdminstrators.
     */
    protected List<GeneralCacheAdministrator> getCacheAdminstrators() {
        return cacheAdminstrators;
    }

    /**
     * Sets the cacheAdminstrators attribute value.
     * 
     * @param cacheAdminstrators The cacheAdminstrators to set.
     */
    public void setCacheAdminstrators(List<GeneralCacheAdministrator> cacheAdminstrators) {
        this.cacheAdminstrators = cacheAdminstrators;
    }

    /**
     * Gets the roleManagementService attribute.
     * 
     * @return Returns the roleManagementService.
     */
    protected RoleManagementService getRoleManagementService() {
        return roleManagementService;
    }

    /**
     * Sets the roleManagementService attribute value.
     * 
     * @param roleManagementService The roleManagementService to set.
     */
    public void setRoleManagementService(RoleManagementService roleManagementService) {
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
     * Gets the cachingDao attribute.
     * 
     * @return Returns the cachingDao.
     */
    protected CachingDao getCachingDao() {
        return cachingDao;
    }

    /**
     * Sets the cachingDao attribute value.
     * 
     * @param cachingDao The cachingDao to set.
     */
    public void setCachingDao(CachingDao cachingDao) {
        this.cachingDao = cachingDao;
    }
}
