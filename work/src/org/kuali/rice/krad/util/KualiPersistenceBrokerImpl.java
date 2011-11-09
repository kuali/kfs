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

import org.apache.log4j.Logger;
import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.TransactionAbortedException;
import org.apache.ojb.broker.TransactionInProgressException;
import org.apache.ojb.broker.TransactionNotInProgressException;
import org.apache.ojb.broker.core.PersistenceBrokerFactoryIF;
import org.apache.ojb.broker.core.PersistenceBrokerImpl;

public class KualiPersistenceBrokerImpl extends PersistenceBrokerImpl {
    private static final Logger LOG = Logger.getLogger(KualiPersistenceBrokerImpl.class);


    private boolean fresh = true;

    public KualiPersistenceBrokerImpl(PBKey key, PersistenceBrokerFactoryIF pbf) {
        super(key, pbf);
    }

    public boolean isFresh() {
        return fresh;
    }


    /**
     * @see org.apache.ojb.broker.core.PersistenceBrokerImpl#beginTransaction()
     */
    public synchronized void beginTransaction() throws TransactionInProgressException, TransactionAbortedException {
        LOG.debug("beginning transaction for persistenceBroker " + getClass().getName() + "@" + hashCode());

        super.beginTransaction();
    }

    /**
     * @see org.apache.ojb.broker.core.PersistenceBrokerImpl#abortTransaction()
     */
    public synchronized void abortTransaction() throws TransactionNotInProgressException {
        LOG.debug("aborting transaction for persistenceBroker " + getClass().getName() + "@" + hashCode());

        super.abortTransaction();
    }

    /**
     * @see org.apache.ojb.broker.core.PersistenceBrokerImpl#commitTransaction()
     */
    public synchronized void commitTransaction() throws TransactionNotInProgressException, TransactionAbortedException {
        LOG.debug("committing transaction for persistenceBroker " + getClass().getName() + "@" + hashCode());

        super.commitTransaction();
    }

    /**
     * @see org.apache.ojb.broker.core.PersistenceBrokerImpl#close()
     */
    public boolean close() {
        LOG.debug("closing persistenceBroker " + getClass().getName() + "@" + hashCode());
        fresh = false;

        return super.close();
    }
}
