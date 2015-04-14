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
package org.kuali.kfs.module.purap.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderSensitiveData;
import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.businessobject.SensitiveDataAssignment;
import org.kuali.kfs.module.purap.dataaccess.SensitiveDataDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class SensitiveDataDaoOjb extends PlatformAwareDaoBaseOjb implements SensitiveDataDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SensitiveDataDaoOjb.class);

    public SensitiveDataDaoOjb() {
        super();
    }

    /**
     * @see org.kuali.kfs.integration.service.SensitiveDataDaoe#getSensitiveDatasAssignedByPoId(java.lang.Integer)
     */
    public List<SensitiveData> getSensitiveDatasAssignedByPoId(Integer poId) {
        LOG.debug("getSensitiveDatasAssignedByPoId(Integer) started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("purapDocumentIdentifier", poId);
        Collection<PurchaseOrderSensitiveData> posdColl = getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PurchaseOrderSensitiveData.class, criteria));
        List<SensitiveData> sdList = new ArrayList<SensitiveData>();
        for (PurchaseOrderSensitiveData posd : posdColl) {
            sdList.add(posd.getSensitiveData());
        }
        
        return sdList;
    }

    /**
     * @see org.kuali.kfs.integration.service.SensitiveDataDaoe#getSensitiveDatasAssignedByReqId(java.lang.Integer)
     */
    public List<SensitiveData> getSensitiveDatasAssignedByReqId(Integer reqId) {
        LOG.debug("getSensitiveDatasAssignedByReqId(Integer) started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("requisitionIdentifier", reqId);
        Collection<PurchaseOrderSensitiveData> posdColl = getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PurchaseOrderSensitiveData.class, criteria));
        List<SensitiveData> sdList = new ArrayList<SensitiveData>();
        for (PurchaseOrderSensitiveData posd : posdColl) {
            sdList.add(posd.getSensitiveData());
        }
        
        return sdList;
    }    

    /**
     * @see org.kuali.kfs.integration.service.SensitiveDataDaoe#deletePurchaseOrderSensitiveDatas(java.lang.Integer)
     */
    public void deletePurchaseOrderSensitiveDatas(Integer poId) {
        LOG.debug("deletePurchaseOrderSensitiveDatas(Integer) started");
        Criteria criteria = new Criteria();
        criteria.addEqualTo("purapDocumentIdentifier", poId);
        getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(PurchaseOrderSensitiveData.class, criteria));
    }
    
    /**
     * @see org.kuali.kfs.integration.service.SensitiveDataDaoe#getLastSensitiveDataAssignment(java.lang.Integer)
     */
    public SensitiveDataAssignment getLastSensitiveDataAssignment(Integer poId) {
        LOG.debug("getLastSensitiveDataAssignment(Integer) started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("purapDocumentIdentifier", poId);
        Collection<SensitiveDataAssignment> sdaColl = getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(SensitiveDataAssignment.class, criteria));
        
        // look for the greatest assignment ID, which will be the latest one for this PO
        int max = 0;
        SensitiveDataAssignment lastsda = null;
        for (SensitiveDataAssignment sda : sdaColl) {
            if (sda.getSensitiveDataAssignmentIdentifier() >= max) {
                max = sda.getSensitiveDataAssignmentIdentifier();
                lastsda = sda;
            }
        }
        
        return lastsda;                
    }

    /*
    public Integer getLastSensitiveDataAssignmentId(Integer poId) {
        LOG.debug("getLastSensitiveDataAssignmentId(Integer) started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("purapDocumentIdentifier", poId);
        Collection sdaColl = getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(SensitiveDataAssignment.class, criteria));
        if (sdaColl == null || sdaColl.isEmpty())
            return null;
        else {
            SensitiveDataAssignment sda = (SensitiveDataAssignment)sdaColl.toArray()[0];        
            return sda.getSensitiveDataAssignmentIdentifier();
        }
    }
    */

}
