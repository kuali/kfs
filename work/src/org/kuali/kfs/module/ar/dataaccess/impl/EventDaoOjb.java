/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.dataaccess.impl;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.kfs.module.ar.dataaccess.EventDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * Implementation class for Event DAO interface.
 */
public class EventDaoOjb extends PlatformAwareDaoBaseOjb implements EventDao {

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.EventDao#getEventsByCriteria(org.apache.ojb.broker.query.Criteria)
     */
    @Override
    public Collection<Event> getEventsByCriteria(Criteria criteria) {
        QueryByCriteria qbc = QueryFactory.newQuery(Event.class, criteria);
        Collection<Event> events = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        return events;
    }

}
