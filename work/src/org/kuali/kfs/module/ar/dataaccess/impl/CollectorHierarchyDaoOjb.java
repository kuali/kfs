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
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectorHierarchy;
import org.kuali.kfs.module.ar.dataaccess.CollectorHierarchyDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * Implementation class for Collector Hierarchy DAO interface.
 */
public class CollectorHierarchyDaoOjb extends PlatformAwareDaoBaseOjb implements CollectorHierarchyDao {

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CollectorHierarchyDao#getCollectorsByCollectorHead(java.lang.String)
     */
    @Override
    public Collection<CollectorHierarchy> getCollectorHierarchyByCriteria(Criteria criteria) {
        QueryByCriteria qbc = QueryFactory.newQuery(CollectorHierarchy.class, criteria);
        Collection<CollectorHierarchy> collectors = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        return collectors;
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CollectorHierarchyDao#isCollector(java.lang.String)
     */
    @Override
    public boolean isCollector(String collector) {
        boolean flag = false;

        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.PRINCIPAL_ID, collector);
        criteria.addEqualTo(KFSPropertyConstants.ACTIVE, true);

        Criteria anotherCriteria = new Criteria();
        anotherCriteria.addEqualTo(ArPropertyConstants.CollectorHierarchyFields.COLLECTOR_INFORMATIONS + "." + KFSPropertyConstants.ACTIVE, true);
        anotherCriteria.addEqualTo(ArPropertyConstants.CollectorHierarchyFields.COLLECTOR_INFORMATIONS + "." + KFSPropertyConstants.PRINCIPAL_ID, collector);
        anotherCriteria.addEqualTo(ArPropertyConstants.CollectorHierarchyFields.COLLECTOR_INFORMATIONS + "." + ArPropertyConstants.CollectorHierarchyFields.COLLECTOR_HEAD_ACTIVE, true);

        criteria.addOrCriteria(anotherCriteria);

        QueryByCriteria qbc = QueryFactory.newQuery(CollectorHierarchy.class, criteria);
        Collection<CollectorHierarchy> collectors = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        flag = collectors != null && !collectors.isEmpty() && collectors.size() > 0;
        return flag;
    }

}
