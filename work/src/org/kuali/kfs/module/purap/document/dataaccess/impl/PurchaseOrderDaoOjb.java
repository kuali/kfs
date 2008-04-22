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

import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.util.TransactionalServiceUtils;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.dao.PurchaseOrderDao;
import org.kuali.module.purap.document.PurchaseOrderDocument;

/**
 * OJB implementation of PurchaseOrderDao.
 */
public class PurchaseOrderDaoOjb extends PlatformAwareDaoBaseOjb implements PurchaseOrderDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderDaoOjb.class);

    /**
     * @see org.kuali.module.purap.dao.PurchaseOrderDao#getDocumentNumberForPurchaseOrderId(java.lang.Integer)
     */
    public String getDocumentNumberForPurchaseOrderId(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, id);
        return getDocumentNumberUsingPurchaseOrderCriteria(criteria);
    }

    /**
     * @see org.kuali.module.purap.dao.PurchaseOrderDao#getDocumentNumberForCurrentPurchaseOrder(java.lang.Integer)
     */
    public String getDocumentNumberForCurrentPurchaseOrder(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, id);
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_CURRENT_INDICATOR, "Y");
        return getDocumentNumberUsingPurchaseOrderCriteria(criteria);
    }

    /**
     * @see org.kuali.module.purap.dao.PurchaseOrderDao#getOldestPurchaseOrderDocumentNumber(java.lang.Integer)
     */
    public String getOldestPurchaseOrderDocumentNumber(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, id);
        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(PurchaseOrderDocument.class, criteria);
        rqbc.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER });
        rqbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
        Iterator<Object[]> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);
        String oldestDocumentNumber = null;
        if (iter.hasNext()) {
            oldestDocumentNumber = (String) (iter.next())[0];
        }
        return oldestDocumentNumber;
    }

    /**
     * Retrieves the document number of the purchase order returned by the passed in criteria.
     * 
     * @param criteria - list of criteria to use in the retrieve
     * @return Document number string if a valid purchase order is found, null if no purchase order is found
     */
    private String getDocumentNumberUsingPurchaseOrderCriteria(Criteria criteria) {
        Iterator<Object[]> iter = getDocumentNumbersUsingPurchaseOrderCriteria(criteria);
        if (iter.hasNext()) {
            Object[] cols = iter.next();
            if (iter.hasNext()) {
                // the iterator should have held only a single doc id of data but it holds 2 or more
                String errorMsg = "Expected single document number for given criteria but multiple (at least 2) were returned";
                LOG.error(errorMsg);
                TransactionalServiceUtils.exhaustIterator(iter);
                throw new RuntimeException(errorMsg);
            }
            // at this part of the code, we know there's no more elements in iterator
            return (String) cols[0];
        }
        return null;
    }

    /**
     * Retrieves a list of document numbers of the purchase order returned by the passed in criteria.
     * 
     * @param criteria - list of criteria to use in the retrieve
     * @return Iterator of document numbers
     */
    private Iterator<Object[]> getDocumentNumbersUsingPurchaseOrderCriteria(Criteria criteria) {
        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(PurchaseOrderDocument.class, criteria);
        rqbc.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER });
        rqbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);
    }

    /**
     * @see org.kuali.module.purap.dao.PurchaseOrderDao#itemExistsOnPurchaseOrder(java.lang.Integer, java.lang.String)
     */
    public boolean itemExistsOnPurchaseOrder(Integer poItemIdentifier, String docNumber){
        boolean existsInPo = false;
                
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, docNumber);
        criteria.addEqualTo(PurapPropertyConstants.ITEM_IDENTIFIER, poItemIdentifier);

        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(PurchaseOrderItem.class, criteria);
        rqbc.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER });
        rqbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
        Iterator<Object[]> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);
        
        if (iter.hasNext()) {
            existsInPo = true;
        }
        
        return existsInPo;
    }
}
