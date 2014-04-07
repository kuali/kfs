/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.dataaccess.impl;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.module.tem.dataaccess.PerDiemDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.util.OjbCollectionAware;

public class PerDiemDaoOjb extends PlatformAwareDaoBaseOjb implements PerDiemDao, OjbCollectionAware {

    @Override
    public Collection<PerDiem> findAllPerDiemsOrderedBySeasonAndDest() {
        Criteria criteria = new Criteria();
        QueryByCriteria query = QueryFactory.newQuery(PerDiem.class, criteria);
        query.addOrderByAscending("pri_dest_id");
        query.addOrderByAscending("ssn_bgn_month_day");

        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }


    @Override
    public Collection<PerDiem> findSimilarPerDiems(PerDiem perdiem) {
        Criteria criteria = new Criteria();
        criteria.addLessOrEqualThan("effectiveFromDate", perdiem.getEffectiveFromDate());
        criteria.addLessOrEqualThan("effectiveToDate", perdiem.getEffectiveToDate());
        criteria.addEqualTo("primaryDestinationId", perdiem.getPrimaryDestinationId());
        QueryByCriteria query = QueryFactory.newQuery(PerDiem.class, criteria);

        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
}
