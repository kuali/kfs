/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.gl.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.service.impl.PersistenceStructureServiceImpl;
import org.kuali.core.service.BusinessObjectService;

/**
 * 
 * This class wraps BusinessObjectService, in that it takes a class and a key and looks up the associated business
 * object class.  However, it also caches everything as it finds it or does not find it.  It also never flushes;
 * so, this is only appropriate for use in situations where the looked up business objects are guaranteed to
 * survive the lifetime of the using class.
 */
public class CachingLookup {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CachingLookup.class);
    
    private int cacheSize = 7500;
    private Map<String, PersistableBusinessObject> whitelist;
    private Set<String> blacklist;
    private BusinessObjectService businessObjectService;
    
    /**
     * Constructs a CachingLookup
     */
    public CachingLookup() {
        float hashTableLoadFactor = 0.75f;
        int hashTableCapacity = (int)Math.ceil(cacheSize / hashTableLoadFactor) + 1;
        whitelist = new LinkedHashMap<String,PersistableBusinessObject>(hashTableCapacity, hashTableLoadFactor, true) {
            private static final long serialVersionUID = 1;
            @Override protected boolean removeEldestEntry (Map.Entry<String,PersistableBusinessObject> eldest) {
                return size() > CachingLookup.this.cacheSize; 
            }
        };
        blacklist = new HashSet<String>();
    }
    
    /**
     * This method looks up and returns a persistable business object, based on its class and keys  
     * @param boClass the class of the PersistableBusinessObject descendant to return
     * @param key the primary key for that class
     * @return the persistable business object
     */
    public PersistableBusinessObject get(Class boClass, Map key) {
        if (boClass == null || key == null || key.size() == 0) {
            return null;
        }
        String cacheKey = convertClassAndPKToCacheKey(boClass, key);
        if (blacklist.contains(cacheKey)) {
            return null;
        } else if (whitelist.containsKey(cacheKey)) {
            return whitelist.get(cacheKey);
        } else {
            PersistableBusinessObject result = businessObjectService.findByPrimaryKey(boClass, key);
            if (result == null) {
                LOG.debug("Could not find record for BO of class: "+boClass.getName()+" keys: "+key.toString());
                blacklist.add(cacheKey);
            } else {
                whitelist.put(cacheKey, result);
            }
            return result;
        }
    }
    
    /**
     * 
     * This method takes a class and a key to look up that class and turns it into the key format that the cache is using
     * @param boClass class of the business object to cache
     * @param key the primary key of the business object to look up
     * @return a string with the cache key
     */
    private String convertClassAndPKToCacheKey(Class boClass, Map key) {
        StringBuilder cacheKey = new StringBuilder();
        cacheKey.append(boClass.getName());
        cacheKey.append("|");
        cacheKey.append(key.toString());
        return cacheKey.toString();
    }

    /**
     * Gets the businessObjectService attribute. 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the cacheSize attribute. 
     * @return Returns the cacheSize.
     */
    public int getCacheSize() {
        return cacheSize;
    }

    /**
     * Sets the cacheSize attribute value.
     * @param cacheSize The cacheSize to set.
     */
    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }
}
