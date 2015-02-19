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
