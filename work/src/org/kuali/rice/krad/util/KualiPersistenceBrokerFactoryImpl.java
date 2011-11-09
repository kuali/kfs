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
import org.apache.ojb.broker.PBFactoryException;
import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.PersistenceBrokerInternal;
import org.apache.ojb.broker.core.PersistenceBrokerFactoryDefaultImpl;
import org.apache.ojb.broker.core.PersistenceBrokerHandle;

public class KualiPersistenceBrokerFactoryImpl extends PersistenceBrokerFactoryDefaultImpl {
    private static final Logger LOG = Logger.getLogger(KualiPersistenceBrokerFactoryImpl.class);

    /**
     * @see org.apache.ojb.broker.core.PersistenceBrokerFactoryDefaultImpl#createPersistenceBroker(org.apache.ojb.broker.PBKey)
     */
    public PersistenceBrokerInternal createPersistenceBroker(PBKey pbKey) throws PBFactoryException {
        PersistenceBrokerInternal pb = super.createPersistenceBroker(pbKey);

        PersistenceBrokerHandle pbh = (PersistenceBrokerHandle) pb;
        KualiPersistenceBrokerImpl realBroker = (KualiPersistenceBrokerImpl) pbh.getInnermostDelegate();

        LOG.debug((realBroker.isFresh() ? "created " : "reusing ") + "persistence broker " + pb.getClass().getName() + "@" + pb.hashCode());

        return pb;
    }
}
