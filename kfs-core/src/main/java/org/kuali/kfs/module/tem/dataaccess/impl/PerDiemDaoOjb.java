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
