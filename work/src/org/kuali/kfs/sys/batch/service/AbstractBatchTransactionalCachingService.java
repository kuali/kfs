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
package org.kuali.kfs.sys.batch.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class CANNOT be used by 2 processes simultaneously. It is for very specific batch processes that should not run at the same
 * time, and initialize and destroy must be called and the beginning and end of each process that uses it.
 */
@Transactional
public abstract class AbstractBatchTransactionalCachingService implements WrappingBatchService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AbstractBatchTransactionalCachingService.class);

    protected Map<String,BusinessObject> referenceValueCache;
    protected Map<Class,PreviousValueReference> previousValueCache;

    public void initialize() {
        referenceValueCache = new HashMap<String,BusinessObject>();
        previousValueCache = new HashMap<Class,PreviousValueReference>();
    }

    public void destroy() {
        referenceValueCache = null;
        previousValueCache = null;
    }

    static final class NonExistentReferenceBusinessObject extends PersistableBusinessObjectBase {

        protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
            throw new UnsupportedOperationException();
        }
    }
    protected static final BusinessObject NON_EXISTENT_REFERENCE_CACHE_VALUE = new NonExistentReferenceBusinessObject();
    protected String getCacheKey(Class clazz, Object...objects) {
        StringBuffer cacheKey = new StringBuffer(clazz.getName());
        for (int i = 0; i < objects.length; i++) {
            cacheKey.append("-").append(objects[i]);
        }
        return cacheKey.toString();
    }
    protected abstract class ReferenceValueRetriever<T extends BusinessObject> {
        public T get(Class<T> type, Object...keys) {
            String cacheKey = getCacheKey(type, keys);
            BusinessObject businessObject = referenceValueCache.get(cacheKey);
            if (businessObject == null) {
                try {
                    businessObject = useDao();
                }
                catch (Exception e) {
                    throw new RuntimeException("Unable to getBusinessObject in AccountingCycleCachingServiceImpl: " + cacheKey, e);
                }
                if (businessObject == null) {
                    referenceValueCache.put(cacheKey, NON_EXISTENT_REFERENCE_CACHE_VALUE);
                }
                else {
                    referenceValueCache.put(cacheKey, businessObject);
                    retrieveReferences((T)businessObject);
                }
            }
            else if (businessObject instanceof NonExistentReferenceBusinessObject) {
                businessObject = null;
            }
            return (T)businessObject;
        }
        protected abstract T useDao();
        protected abstract void retrieveReferences(T object);
    }
    public class PreviousValueReference<T extends BusinessObject> {
        protected String key = "";
        protected T value;
        public T getValue() {
            return value;
        }
        public void update(T value, String key) {
            this.key = key;
            this.value = value;
        }
        public void update(T value, Object...keys) {
            update (value, getCacheKey(value.getClass(), keys));
        }
    }
//    protected abstract class PreviousValueRetriever<T extends BusinessObject> {
//        public T get(Class<T> type, Object...keys) {
//            String cacheKey = getCacheKey(type, keys);
//            if (!cacheKey.equals(previousValueCache.get(AccountBalance.class).key.equals(cacheKey))) {
//                previousValueCache.get(type).update(useDao(), cacheKey);
//            }
//            return (T)previousValueCache.get(type).getValue();
//        }
//        protected abstract T useDao();
//    }

    protected abstract class PreviousValueRetriever<T extends BusinessObject> {
        public T get(Class<T> type, Object...keys) {
            // this should never happen, but in did, so just in case
            if (ObjectUtils.isNull(previousValueCache)) {
                LOG.error("previousValueCache is null. This shouldn't have happened.");
                previousValueCache = new HashMap<Class,PreviousValueReference>();
            }
            PreviousValueReference<T> pvr = previousValueCache.get(type);
            if (ObjectUtils.isNull(pvr)) {
                LOG.warn("PreviousValueReference for type " + type + " is not initialized; adding a new one to previousValueCache for it.");
                pvr = new PreviousValueReference<T>();
                previousValueCache.put(type, pvr);
            }
            String cacheKey = getCacheKey(type, keys);
            if (!StringUtils.equals(cacheKey, pvr.key)) {
                pvr.update(useDao(), cacheKey);
            }
            return pvr.getValue();
        }
        protected abstract T useDao();
    }
}
