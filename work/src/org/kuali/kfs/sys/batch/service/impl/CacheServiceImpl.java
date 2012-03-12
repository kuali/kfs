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
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.common.template.Template;
import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.location.framework.campus.CampusValuesFinder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

/**
 * @see org.kuali.kfs.sys.batch.service.CacheService
 */
@NonTransactional
public class CacheServiceImpl implements CacheService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CacheServiceImpl.class);

    /**
     * @see org.kuali.kfs.sys.batch.service.CacheService#clearSystemCache()
     */
    public void clearSystemCache() {
        clearMethodCache();
    }

    /**
     * Clears out service methods cache by calling adminstrators flushAll
     */
    @CacheEvict(allEntries=true, value = { "" })
    protected void clearMethodCache() {
        LOG.info("clearing spring method cache ...");
    }

    /**
     * Clears out parameter cache by calling flush method on parameter service
     */
    @CacheEvict(value={Parameter.Cache.NAME}, allEntries=true)
    protected void clearParameterCache() {
        LOG.info("clearing parameter cache ...");
    }
    
}
