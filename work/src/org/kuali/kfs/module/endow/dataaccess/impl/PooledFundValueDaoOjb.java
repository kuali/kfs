/*
 * Copyright 2010 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.endow.dataaccess.impl;

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.PooledFundValue;
import org.kuali.kfs.module.endow.dataaccess.PooledFundValueDao;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

public class PooledFundValueDaoOjb extends PlatformAwareDaoBaseOjb implements PooledFundValueDao {

    private KEMService kemService;

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.PooledFundValueDao#getPooledFundValueWhereSTProcessOnDateIsCurrentDate()
     */
    public List<PooledFundValue> getPooledFundValueWhereSTProcessOnDateIsCurrentDate() {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.DISTRIBUTE_SHORT_TERM_GAIN_LOSS_ON_DATE, kemService.getCurrentDate());
        criteria.addEqualTo(EndowPropertyConstants.ST_GAIN_LOSS_DISTR_COMPL, false);

        QueryByCriteria qbc = QueryFactory.newQuery(PooledFundValue.class, criteria);
        qbc.addOrderByAscending(EndowPropertyConstants.POOL_SECURITY_ID);
        qbc.addOrderByDescending(EndowPropertyConstants.VALUE_EFFECTIVE_DATE);

        return (List<PooledFundValue>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }


    /**
     * @see org.kuali.kfs.module.endow.dataaccess.PooledFundValueDao#getPooledFundValueWhereLTProcessOnDateIsCurrentDate()
     */
    public List<PooledFundValue> getPooledFundValueWhereLTProcessOnDateIsCurrentDate() {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.DISTRIBUTE_LONG_TERM_GAIN_LOSS_ON_DATE, kemService.getCurrentDate());
        criteria.addEqualTo(EndowPropertyConstants.LT_GAIN_LOSS_DISTR_COMPL, false);

        QueryByCriteria qbc = QueryFactory.newQuery(PooledFundValue.class, criteria);
        qbc.addOrderByAscending(EndowPropertyConstants.POOL_SECURITY_ID);
        qbc.addOrderByDescending(EndowPropertyConstants.VALUE_EFFECTIVE_DATE);

        return (List<PooledFundValue>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.PooledFundValueDao#getPooledFundValueWhereLTProcessOnDateIsCurrentDate()
     */
    public List<PooledFundValue> getPooledFundValueWhereDistributionIncomeOnDateIsCurrentDate() {
        Criteria criteria = new Criteria();
        // criteria.addEqualTo(EndowPropertyConstants.DISTRIBUTE_INCOME_ON_DATE, kemService.getCurrentDate());
        criteria.addLessThan(EndowPropertyConstants.DISTRIBUTE_INCOME_ON_DATE, kemService.getCurrentDate());
        // criteria.addEqualTo(EndowPropertyConstants.INCOME_DISTRIBUTION_COMPLETE, false);
        return (List<PooledFundValue>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(PooledFundValue.class, criteria));
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.PooledFundValueDao#setIncomeDistributionCompleted(java.util.List, boolean)
     */
    public void setIncomeDistributionCompleted(List<PooledFundValue> pooledFundValueList, boolean completed) {
        for (PooledFundValue pooledFundValue : pooledFundValueList) {
            getPersistenceBrokerTemplate().store(pooledFundValue);
        }
    }

    /**
     * Sets the kemService.
     * 
     * @param kemService
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

}
