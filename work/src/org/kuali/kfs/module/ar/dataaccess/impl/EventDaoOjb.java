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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectLevel;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.kfs.module.ar.dataaccess.EventDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kew.api.KewApiConstants;

/**
 * Implementation class for Event DAO interface.
 */
public class EventDaoOjb extends PlatformAwareDaoBaseOjb implements EventDao {

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.EventDao#getEventsByCriteria(org.apache.ojb.broker.query.Criteria)
     */
    @Override
    public Collection<Event> getMatchingEventsByCollection(Map fieldValues, boolean isSavedRouteStatus, String documentNumberToExclude) {
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new Event());

        // Factor in saved route status
        if (isSavedRouteStatus){
            criteria.addEqualTo(ArPropertyConstants.EventFields.EVENT_ROUTE_STATUS, KewApiConstants.ROUTE_HEADER_SAVED_CD);
        }
        else {
            criteria.addNotEqualTo(ArPropertyConstants.EventFields.EVENT_ROUTE_STATUS, KewApiConstants.ROUTE_HEADER_SAVED_CD);
        }

        // Factor in document number to exclude
        if (StringUtils.isNotEmpty(documentNumberToExclude)){
            criteria.addNotEqualTo(ArPropertyConstants.EventFields.DOCUMENT_NUMBER, documentNumberToExclude);
        }

        QueryByCriteria qbc = QueryFactory.newQuery(Event.class, criteria);
        Collection<Event> events = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);

        return events;
    }

    @Override
    public Collection<ObjectCode> getObjectCodesByLevelCodes(List<String> levelCodes) {

        Criteria criteria = new Criteria();
        criteria.addIn(KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE, levelCodes);
        QueryByCriteria qbc = QueryFactory.newQuery(ObjectLevel.class, criteria);
        Collection<ObjectCode> objectCodes = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);

        return objectCodes;
    }
}
