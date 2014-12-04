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
package org.kuali.kfs.module.endow.dataaccess.impl;

import java.sql.Date;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.PooledFundValue;
import org.kuali.kfs.module.endow.dataaccess.PooledFundValueDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class PooledFundValueDaoOjb extends PlatformAwareDaoBaseOjb implements PooledFundValueDao {

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.PooledFundValueDao#getPooledFundValueWhereSTProcessOnDateIsCurrentDate(Date)
     */
    public List<PooledFundValue> getPooledFundValueWhereSTProcessOnDateIsCurrentDate(Date currentDate) {
        Criteria criteria = new Criteria();

        criteria.addEqualTo(EndowPropertyConstants.DISTRIBUTE_SHORT_TERM_GAIN_LOSS_ON_DATE, currentDate);
        criteria.addEqualTo(EndowPropertyConstants.ST_GAIN_LOSS_DISTR_COMPL, false);

        QueryByCriteria qbc = QueryFactory.newQuery(PooledFundValue.class, criteria);
        qbc.addOrderByAscending(EndowPropertyConstants.POOL_SECURITY_ID);
        qbc.addOrderByDescending(EndowPropertyConstants.VALUE_EFFECTIVE_DATE);

        return (List<PooledFundValue>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }


    /**
     * @see org.kuali.kfs.module.endow.dataaccess.PooledFundValueDao#getPooledFundValueWhereLTProcessOnDateIsCurrentDate(Date)
     */
    public List<PooledFundValue> getPooledFundValueWhereLTProcessOnDateIsCurrentDate(Date currentDate) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.DISTRIBUTE_LONG_TERM_GAIN_LOSS_ON_DATE, currentDate);
        criteria.addEqualTo(EndowPropertyConstants.LT_GAIN_LOSS_DISTR_COMPL, false);

        QueryByCriteria qbc = QueryFactory.newQuery(PooledFundValue.class, criteria);
        qbc.addOrderByAscending(EndowPropertyConstants.POOL_SECURITY_ID);
        qbc.addOrderByDescending(EndowPropertyConstants.VALUE_EFFECTIVE_DATE);

        return (List<PooledFundValue>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.PooledFundValueDao#getPooledFundValueWhereLTProcessOnDateIsCurrentDate(Date)
     */
    public List<PooledFundValue> getPooledFundValueWhereDistributionIncomeOnDateIsCurrentDate(Date currentDate) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.DISTRIBUTE_INCOME_ON_DATE, currentDate);
        criteria.addEqualTo(EndowPropertyConstants.INCOME_DISTRIBUTION_COMPLETE, false);
        return (List<PooledFundValue>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(PooledFundValue.class, criteria));
    }

}
