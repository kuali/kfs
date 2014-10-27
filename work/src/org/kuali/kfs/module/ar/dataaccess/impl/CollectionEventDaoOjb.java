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
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.kfs.module.ar.dataaccess.CollectionEventDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * Implementation class for Collection Event DAO interface.
 */
public class CollectionEventDaoOjb extends PlatformAwareDaoBaseOjb implements CollectionEventDao {

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.EventDao#getEventsByCriteria(org.apache.ojb.broker.query.Criteria)
     */
    @Override
    public Collection<CollectionEvent> getMatchingEventsByCollection(Map fieldValues, String documentNumberToExclude) {
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new CollectionEvent());

        // Factor in document number to exclude
        if (StringUtils.isNotEmpty(documentNumberToExclude)){
            criteria.addNotEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumberToExclude);
        }

        QueryByCriteria qbc = QueryFactory.newQuery(CollectionEvent.class, criteria);
        Collection<CollectionEvent> events = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);

        return events;
    }

}
