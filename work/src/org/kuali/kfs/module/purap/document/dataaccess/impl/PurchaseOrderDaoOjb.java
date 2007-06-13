/*
 * Copyright 2007 The Kuali Foundation.
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

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.dao.PurchaseOrderDao;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.rice.KNSServiceLocator;


/**
 * This class is the OJB implementation of the ProjectCodeDao interface.
 */
public class PurchaseOrderDaoOjb extends PlatformAwareDaoBaseOjb implements PurchaseOrderDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderDaoOjb.class);

    /**
     * 
     * @see org.kuali.module.purap.dao.PurchaseOrderDao#getPurchaseOrderById(java.lang.Integer)
     */
    public PurchaseOrderDocument getPurchaseOrderById(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, id);
        return getPurchaseOrder(criteria);
      }
    
    /**
     * 
     * @see org.kuali.module.purap.dao.PurchaseOrderDao#getCurrentPurchaseOrder(java.lang.Integer)
     */
    public PurchaseOrderDocument getCurrentPurchaseOrder(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, id );
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_CURRENT_INDICATOR, "Y");
        return getPurchaseOrder(criteria);
    }
    
    /**
     * 
     * This method returns a PurchaseOrderDocument object if you give the
     * criteria in the input parameter
     * 
     * @param criteria
     * @return PurchaseOrderDocument
     */
    private PurchaseOrderDocument getPurchaseOrder (Criteria criteria) {
        PurchaseOrderDocument po = (PurchaseOrderDocument) getPersistenceBrokerTemplate().getObjectByQuery(
            new QueryByCriteria(PurchaseOrderDocument.class, criteria));
        if (ObjectUtils.isNotNull(po)) {
            po.refreshAllReferences();
        }
        return po;        
    }
    
    /**
     * 
     * @see org.kuali.module.purap.dao.PurchaseOrderDao#getOldestPurchaseOrder(java.lang.Integer)
     */
    public PurchaseOrderDocument getOldestPurchaseOrder(Integer id, PurchaseOrderDocument po) {
        /*
         * This method takes in a poid and a document and returns the document if oldest, else it returns the 
         * po document that is oldest
         */
        //get oldest docid
        String oldestDocumentNumber = getOldestPODocId(id); 
        //compare to po doc number if oldest return po
        if(ObjectUtils.isNotNull(po) && 
           StringUtils.equals(oldestDocumentNumber, po.getDocumentNumber())){
            //manually set bo notes - this is mainly done for performance reasons (preferably we could call
            //retrieve doc notes in PersistableBusinessObjectBase but that is private)
            po.setBoNotes(KNSServiceLocator.getNoteService().getByRemoteObjectId(po.getObjectId()));
            return po;
        }
        //po not oldest, using the oldest doc number return oldest po
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, id);
        criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, oldestDocumentNumber);
        QueryByCriteria qbc = new QueryByCriteria(PurchaseOrderDocument.class, criteria);
        
        PurchaseOrderDocument oldestPO = (PurchaseOrderDocument)getPersistenceBrokerTemplate().getObjectByQuery(qbc);
        if (ObjectUtils.isNotNull(oldestPO)) {
            oldestPO.refreshAllReferences();
        }
        return oldestPO;
    }

    /**
     * This method finds the oldest doc #
     * @param id
     * @return
     */
    private String getOldestPODocId(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, id);
        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(PurchaseOrderDocument.class, criteria);
        rqbc.setAttributes(new String[] {KFSPropertyConstants.DOCUMENT_NUMBER});
        rqbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
        Object [] cols = (Object [])(getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc).next());
        String oldestDocumentNumber = (String)cols[0];
        return oldestDocumentNumber;
    }
    
    /**
     * 
     * @see org.kuali.module.purap.dao.PurchaseOrderDao#getPurchaseOrderInPendingPrintStatus(java.lang.Integer)
     */
    public PurchaseOrderDocument getPurchaseOrderInPendingPrintStatus(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, id);
        criteria.addEqualTo(PurapPropertyConstants.STATUS_CODE, PurapConstants.PurchaseOrderStatuses.PENDING_PRINT);
        PurchaseOrderDocument thePO = (PurchaseOrderDocument)getPurchaseOrder(criteria);
        return thePO;
    }
    
    public PurchaseOrderDocument getPurchaseOrderByDocumentNumber(String documentNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        PurchaseOrderDocument thePO = (PurchaseOrderDocument)getPurchaseOrder(criteria);
        return thePO;
    }
}
