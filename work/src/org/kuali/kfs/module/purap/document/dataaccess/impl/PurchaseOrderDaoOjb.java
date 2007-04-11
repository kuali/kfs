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

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.dao.PurchaseOrderDao;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;


/**
 * This class is the OJB implementation of the ProjectCodeDao interface.
 * 
 * 
 */
public class PurchaseOrderDaoOjb extends PersistenceBrokerDaoSupport implements PurchaseOrderDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderDaoOjb.class);

    /**
     * 
     * @param PurchaseOrderDocument - a populated PurchaseOrder object to be saved
     * @throws IllegalObjectStateException
     * @throws ValidationErrorList
     */
    public void save(PurchaseOrderDocument PurchaseOrderDocument) {
        getPersistenceBrokerTemplate().store(PurchaseOrderDocument);
    }

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
    public PurchaseOrderDocument getOldestPurchaseOrder(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, id);
        QueryByCriteria qbc = new QueryByCriteria(PurchaseOrderDocument.class, criteria);
        qbc.addOrderByAscending(PurapPropertyConstants.DOCUMENT_NUMBER);
        PurchaseOrderDocument oldestPO = (PurchaseOrderDocument)getPersistenceBrokerTemplate().getObjectByQuery(qbc);
        if (ObjectUtils.isNotNull(oldestPO)) {
            oldestPO.refreshAllReferences();
        }
        return oldestPO;
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
}
