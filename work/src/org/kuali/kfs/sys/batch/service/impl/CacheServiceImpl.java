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

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.service.CacheService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.impl.services.CoreImplServiceLocator;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * @see org.kuali.kfs.sys.batch.service.CacheService
 */
@NonTransactional
public class CacheServiceImpl implements CacheService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CacheServiceImpl.class);

    /**
     * @see org.kuali.kfs.sys.batch.service.CacheService#clearSystemCache()
     */
    @Override
    public void clearSystemCaches() {
        for ( CacheManager cm :  CoreImplServiceLocator.getCacheManagerRegistry().getCacheManagers() ) {
            for ( String cacheName : cm.getCacheNames() ) {
                cm.getCache(cacheName).clear();
            }
        }
    }

    @Override
    public void clearNamedCache(String cacheName) {
        CacheManager cm = CoreImplServiceLocator.getCacheManagerRegistry().getCacheManager(KFSConstants.KFS_CORE_DISTRIBUTED_CACHE_MANAGER);
        if ( cm != null ) {
            Cache cache = cm.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( "Cleared " + cacheName + " cache in cache manager " + KFSConstants.KFS_CORE_DISTRIBUTED_CACHE_MANAGER + "." );
                }
            } else {
                // this is at debug level intentionally, since not all BOs have caches
                LOG.debug( "Unable to find cache for " + cacheName + " in cache manager " + KFSConstants.KFS_CORE_DISTRIBUTED_CACHE_MANAGER);
            }
        } else {
            LOG.info( "Unable to find cache manager " + KFSConstants.KFS_CORE_DISTRIBUTED_CACHE_MANAGER + " when attempting to clear " + cacheName );
        }
    }

    @Override
    public void clearKfsBusinessObjectCache(Class boClass) {
        String cacheName = KFSConstants.APPLICATION_NAMESPACE_CODE + "/" + boClass.getSimpleName();
        clearNamedCache(cacheName);
    }
    
}
