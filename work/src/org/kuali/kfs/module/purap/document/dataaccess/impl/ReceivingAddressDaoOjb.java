/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.purap.dao.ojb;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.ReceivingAddress;
import org.kuali.module.purap.dao.ReceivingAddressDao;

/**
 * OJB Implementation of ReceivingAddressDao.
 */
public class ReceivingAddressDaoOjb extends PlatformAwareDaoBaseOjb implements ReceivingAddressDao {
    private static Logger LOG = Logger.getLogger(ReceivingAddressDaoOjb.class);

    /**
     * @see org.kuali.module.purap.dao.ReceivingAddressDao#findActiveByChartOrg(java.lang.String,java.lang.String)
     */
    public Collection<ReceivingAddress> findActiveByChartOrg(String chartCode, String orgCode) {
        LOG.debug("Entering findActiveByChartOrg(String,String)");
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartCode);
        if ( orgCode == null )
            criteria.addIsNull(KFSPropertyConstants.ORGANIZATION_CODE);
        else
            criteria.addEqualTo(KFSPropertyConstants.ORGANIZATION_CODE, orgCode);
        criteria.addEqualTo(PurapPropertyConstants.RCVNG_ADDR_ACTIVE, true);
        Query query = new QueryByCriteria(ReceivingAddress.class, criteria);
        
        LOG.debug("Leaving findActiveByChartOrg(String,String)");
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.module.purap.dao.ReceivingAddressDao#findDefaultByChartOrg(java.lang.String,java.lang.String)
     */
    public Collection<ReceivingAddress> findDefaultByChartOrg(String chartCode, String orgCode) {
        LOG.debug("Entering findDefaultByChartOrg(String,String)");
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartCode);
        if ( orgCode == null )
            criteria.addIsNull(KFSPropertyConstants.ORGANIZATION_CODE);
        else
            criteria.addEqualTo(KFSPropertyConstants.ORGANIZATION_CODE, orgCode);
        criteria.addEqualTo(PurapPropertyConstants.RCVNG_ADDR_DFLT_IND, true);
        criteria.addEqualTo(PurapPropertyConstants.RCVNG_ADDR_ACTIVE, true);
        Query query = new QueryByCriteria(ReceivingAddress.class, criteria);
        
        LOG.debug("Leaving findDefaultByChartOrg(String,String)");
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }    
    
    /**
     * @see org.kuali.module.purap.dao.ReceivingAddressDao#countActiveByChartOrg(java.lang.String,java.lang.String)
     */
    public int countActiveByChartOrg(String chartCode, String orgCode) {
        LOG.debug("Entering countActiveByChartOrg(String,String)");
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartCode);
        if ( orgCode == null )
            criteria.addIsNull(KFSPropertyConstants.ORGANIZATION_CODE);
        else
            criteria.addEqualTo(KFSPropertyConstants.ORGANIZATION_CODE, orgCode);
        criteria.addEqualTo(PurapPropertyConstants.RCVNG_ADDR_ACTIVE, true);
        Query query = new QueryByCriteria(ReceivingAddress.class, criteria);
        
        LOG.debug("Leaving countActiveByChartOrg(String,String)");
        return getPersistenceBrokerTemplate().getCount(query);
    }

}
