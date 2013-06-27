/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.coa.dataaccess.impl;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.coa.businessobject.ObjectLevel;
import org.kuali.kfs.coa.dataaccess.ObjectLevelDao;
import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * This class is the OJB implementation of the ObjectLevelDao interface.
 */
public class ObjectLevelDaoOjb extends PlatformAwareDaoBaseOjb implements ObjectLevelDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ObjectLevelDaoOjb.class);

    public Collection<ObjectLevel> getObjectLevelsByCriteria(Criteria criteria) {
        QueryByCriteria qbc = QueryFactory.newQuery(ObjectLevel.class, criteria);
        Collection<ObjectLevel> objectLevels = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        return objectLevels;
    }
}
