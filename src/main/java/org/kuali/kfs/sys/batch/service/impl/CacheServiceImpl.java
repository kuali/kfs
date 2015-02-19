/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.batch.service.impl;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.service.CacheService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
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

        try {
            CacheManager cm = CoreImplServiceLocator.getCacheManagerRegistry().getCacheManagerByCacheName(cacheName);
            if ( cm != null ) {
                Cache cache = cm.getCache(cacheName);
                if (cache != null) {
                    cache.clear();
                    if ( LOG.isDebugEnabled() ) {
                        LOG.debug( "Cleared " + cacheName + " cache." );
                    }
                } else {
                    // this is at debug level intentionally, since not all BOs have caches
                    LOG.debug( "Unable to find cache for " + cacheName + ".");
                }
            } else {
                LOG.info( "Unable to find cache manager when attempting to clear " + cacheName );
            }
        } catch (RiceIllegalArgumentException e) {
            LOG.info( "Cache manager not found when attempting to clear " + cacheName );
        }

    }

    @Override
    public void clearKfsBusinessObjectCache(Class boClass) {
        String cacheName = KFSConstants.APPLICATION_NAMESPACE_CODE + "/" + boClass.getSimpleName();
        clearNamedCache(cacheName);
    }

}
