/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.dataaccess.impl;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.ReceivingAddress;
import org.kuali.kfs.module.purap.document.dataaccess.ReceivingAddressDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.springframework.transaction.annotation.Transactional;

/**
 * OJB Implementation of ReceivingAddressDao.
 */
@Transactional
public class ReceivingAddressDaoOjb extends PlatformAwareDaoBaseOjb implements ReceivingAddressDao {
    private static Logger LOG = Logger.getLogger(ReceivingAddressDaoOjb.class);

    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.ReceivingAddressDao#findActiveByChartOrg(java.lang.String,java.lang.String)
     */
    public Collection<ReceivingAddress> findActiveByChartOrg(String chartCode, String orgCode) {
        LOG.debug("Entering findActiveByChartOrg(String,String)");
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartCode);
        if ( orgCode == null )
            criteria.addIsNull(KFSPropertyConstants.ORGANIZATION_CODE);
        else
            criteria.addEqualTo(KFSPropertyConstants.ORGANIZATION_CODE, orgCode);
        criteria.addEqualTo(PurapPropertyConstants.BO_ACTIVE, true);
        Query query = new QueryByCriteria(ReceivingAddress.class, criteria);
        
        LOG.debug("Leaving findActiveByChartOrg(String,String)");

        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.ReceivingAddressDao#findDefaultByChartOrg(java.lang.String,java.lang.String)
     */
    public Collection<ReceivingAddress> findDefaultByChartOrg(String chartCode, String orgCode) {
        LOG.debug("Entering findDefaultByChartOrg(String,String)");
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartCode);
        if ( orgCode == null )
            criteria.addIsNull(KFSPropertyConstants.ORGANIZATION_CODE);
        else
            criteria.addEqualTo(KFSPropertyConstants.ORGANIZATION_CODE, orgCode);
        criteria.addEqualTo(PurapPropertyConstants.RECEIVING_ADDRESS_DEFAULT_INDICATOR, true);
        criteria.addEqualTo(PurapPropertyConstants.BO_ACTIVE, true);
        Query query = new QueryByCriteria(ReceivingAddress.class, criteria);
        
        LOG.debug("Leaving findDefaultByChartOrg(String,String)");

        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }    
    
    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.ReceivingAddressDao#countActiveByChartOrg(java.lang.String,java.lang.String)
     */
    public int countActiveByChartOrg(String chartCode, String orgCode) {
        LOG.debug("Entering countActiveByChartOrg(String,String)");
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartCode);
        if ( orgCode == null )
            criteria.addIsNull(KFSPropertyConstants.ORGANIZATION_CODE);
        else
            criteria.addEqualTo(KFSPropertyConstants.ORGANIZATION_CODE, orgCode);
        criteria.addEqualTo(PurapPropertyConstants.BO_ACTIVE, true);
        Query query = new QueryByCriteria(ReceivingAddress.class, criteria);
        
        LOG.debug("Leaving countActiveByChartOrg(String,String)");

        return getPersistenceBrokerTemplate().getCount(query);
    }
}
