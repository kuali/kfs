/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.rice.krad.util;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.cache.ObjectCachePerBrokerImpl;

public class KualiObjectCachePerBrokerImpl extends ObjectCachePerBrokerImpl {
    private static final Logger LOG = Logger.getLogger(KualiObjectCachePerBrokerImpl.class);


    private final String brokerId;

    public KualiObjectCachePerBrokerImpl(PersistenceBroker broker, Properties prop) {
        super(broker, prop);
        brokerId = broker.getClass().getName() + "@" + broker.hashCode();

        LOG.debug("created objectCache for broker " + brokerId);
    }

    /**
     * Clear ObjectCache. I.e. remove all entries for classes and objects.
     */
    public void clear() {
        super.clear();

        LOG.debug("cleared objectCache for broker " + brokerId);
    }

    /**
     * @see org.apache.ojb.broker.cache.ObjectCachePerBrokerImpl#cache(org.apache.ojb.broker.Identity, java.lang.Object)
     */
    public void cache(Identity oid, Object obj) {
        super.cache(oid, obj);

        boolean cached = (super.lookup(oid) != null);
        LOG.debug((cached ? "cached oid " : "unable to cache oid ") + oid + " in objectCache for broker " + brokerId);
    }

    /**
     * @see org.apache.ojb.broker.cache.ObjectCachePerBrokerImpl#cacheIfNew(org.apache.ojb.broker.Identity, java.lang.Object)
     */
    public boolean cacheIfNew(Identity oid, Object obj) {
        boolean cachedIfNew = super.cacheIfNew(oid, obj);

        boolean cached = (super.lookup(oid) != null);
        LOG.debug((cached ? "cached new oid " : "unable to cache new oid ") + oid + " in objectCache for broker " + brokerId);

        return cachedIfNew;
    }

    /**
     * @see org.apache.ojb.broker.cache.ObjectCachePerBrokerImpl#lookup(org.apache.ojb.broker.Identity)
     */
    public Object lookup(Identity oid) {
        Object o = super.lookup(oid);

        LOG.debug((o != null ? "found oid " : "cannot find oid ") + oid + " in objectCache for broker " + brokerId);

        return o;
    }
}
