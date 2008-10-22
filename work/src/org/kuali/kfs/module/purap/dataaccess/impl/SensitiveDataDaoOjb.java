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
package org.kuali.kfs.module.purap.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoadSummary;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderSensitiveData;
import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.businessobject.SensitiveDataAssignment;
import org.kuali.kfs.module.purap.businessobject.SensitiveDataAssignmentDetail;
import org.kuali.kfs.module.purap.dataaccess.SensitiveDataDao;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

public class SensitiveDataDaoOjb extends PlatformAwareDaoBaseOjb implements SensitiveDataDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SensitiveDataDaoOjb.class);

    public SensitiveDataDaoOjb() {
        super();
    }

    public List<SensitiveData> getSensitiveDatasAssignedByPoId(Integer poId) {
        LOG.debug("getSensitiveDatasAssignedByPoId(Integer) started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("purapDocumentIdentifier", poId);
        Collection sdColl = getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PurchaseOrderSensitiveData.class, criteria));
        List<SensitiveData> sdList = new ArrayList<SensitiveData>();
        for (Iterator iter = sdColl.iterator(); iter.hasNext();) {
            PurchaseOrderSensitiveData posd = (PurchaseOrderSensitiveData)iter.next();
            sdList.add(posd.getSensitiveData());
        }
        
        return sdList;
    }

    public List<SensitiveData> getSensitiveDatasAssignedByReqId(Integer reqId) {
        LOG.debug("getSensitiveDatasAssignedByReqId(Integer) started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("requisitionIdentifier", reqId);
        Collection sdColl = getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PurchaseOrderSensitiveData.class, criteria));
        List<SensitiveData> sdList = new ArrayList<SensitiveData>();
        for (Iterator iter = sdColl.iterator(); iter.hasNext();) {
            PurchaseOrderSensitiveData posd = (PurchaseOrderSensitiveData)iter.next();
            sdList.add(posd.getSensitiveData());
        }
        
        return sdList;
    }    

    public void deletePurchaseOrderSensitiveDatas(Integer poId) {
        LOG.debug("deletePurchaseOrderSensitiveDatas(Integer) started");
        Criteria criteria = new Criteria();
        criteria.addEqualTo("purapDocumentIdentifier", poId);
        getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(PurchaseOrderSensitiveData.class, criteria));
    }
    
    public void savePurchaseOrderSensitiveDatas(List<PurchaseOrderSensitiveData> posds) {
        LOG.debug("savePurchaseOrderSensitiveDatas(List<PurchaseOrderSensitiveData>) started");
        if (posds == null)
            return;
        for (PurchaseOrderSensitiveData posd : posds) {
            getPersistenceBrokerTemplate().store(posd);
        }
    }
    
    public SensitiveDataAssignment getLastSensitiveDataAssignment(Integer poId) {
        LOG.debug("getLastSensitiveDataAssignment(Integer) started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("purapDocumentIdentifier", poId);
        Collection sdaColl = getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(SensitiveDataAssignment.class, criteria));
        
        // look for the greatest assignment ID, which will be the latest one for this PO
        int max = 0;
        SensitiveDataAssignment lastsda = null;
        for (Iterator iter = sdaColl.iterator(); iter.hasNext();) {
            SensitiveDataAssignment sda = (SensitiveDataAssignment)iter.next();
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

    public void saveSensitiveDataAssignment(SensitiveDataAssignment sda) {
        LOG.debug("saveSensitiveDataAssignment(SensitiveDataAssignment) started");
        getPersistenceBrokerTemplate().store(sda);        
    }

    public void saveSensitiveDataAssignmentDetails(List<SensitiveDataAssignmentDetail> sdads) {
        LOG.debug("saveSensitiveDataAssignmentDetails(List<SensitiveDataAssignmentDetail>) started");
        if (sdads == null)
            return;
        for (SensitiveDataAssignmentDetail sda : sdads) {
            getPersistenceBrokerTemplate().store(sda);
        }
    }

}
