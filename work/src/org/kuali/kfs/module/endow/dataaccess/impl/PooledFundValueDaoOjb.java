/*
 * Copyright 2010 The Kuali Foundation.
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
