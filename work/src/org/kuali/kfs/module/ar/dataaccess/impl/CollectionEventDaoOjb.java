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
