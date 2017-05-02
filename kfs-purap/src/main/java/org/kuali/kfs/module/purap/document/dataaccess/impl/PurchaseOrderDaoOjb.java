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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.joda.time.DateTime;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.AutoClosePurchaseOrderView;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.dataaccess.PurchaseOrderDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.springframework.transaction.annotation.Transactional;

/**
 * OJB implementation of PurchaseOrderDao.
 */
@Transactional
public class PurchaseOrderDaoOjb extends PlatformAwareDaoBaseOjb implements PurchaseOrderDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderDaoOjb.class);

    @Override
    public Integer getPurchaseOrderIdForCurrentPurchaseOrderByRelatedDocId(Integer accountsPayablePurchasingDocumentLinkIdentifier) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountsPayablePurchasingDocumentLinkIdentifier", accountsPayablePurchasingDocumentLinkIdentifier);
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_CURRENT_INDICATOR, "Y");

        Collection<PurchaseOrderDocument> poList = getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PurchaseOrderDocument.class, criteria));
        for (PurchaseOrderDocument purchaseOrderDocument : poList) {
            //should be only one
            return purchaseOrderDocument.getPurapDocumentIdentifier();
        }

        return null;
    }

    @Override
    public PurchaseOrderDocument getCurrentPurchaseOrder(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, id);
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_CURRENT_INDICATOR, "Y");

        return (PurchaseOrderDocument) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(PurchaseOrderDocument.class, criteria));
    }


    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.PurchaseOrderDao#getDocumentNumberForPurchaseOrderId(java.lang.Integer)
     */
    @Override
    public String getDocumentNumberForPurchaseOrderId(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, id);

        return getDocumentNumberUsingPurchaseOrderCriteria(criteria);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.PurchaseOrderDao#getDocumentNumberForCurrentPurchaseOrder(java.lang.Integer)
     */
    @Override
    public String getDocumentNumberForCurrentPurchaseOrder(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, id);
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_CURRENT_INDICATOR, "Y");

        return getDocumentNumberUsingPurchaseOrderCriteria(criteria);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.PurchaseOrderDao#getOldestPurchaseOrderDocumentNumber(java.lang.Integer)
     */
    @Override
    public String getOldestPurchaseOrderDocumentNumber(Integer id) {
        Criteria criteria = new Criteria();
        List<String> relatedPurchaseOrderDocNumForPO = new ArrayList<String>();
        DateTime oldestDocumentsCreationDate = DateTime.now();
        String oldestDocumentNumber = null;

        criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, id);
        // we only need the document number, no need to get the entire document object
        ReportQueryByCriteria query = new ReportQueryByCriteria(PurchaseOrderDocument.class, new String[]{PurapPropertyConstants.DOCUMENT_NUMBER}, criteria);
        query.addOrderByAscending(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_CREATE_DATE);
        java.util.Iterator<Object[]> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        if (iter.hasNext()) {
            final Object[] res = iter.next();
            oldestDocumentNumber = (String)res[0];
        }
        TransactionalServiceUtils.exhaustIterator(iter);

        return oldestDocumentNumber;
    }

    /**
     * Retrieves the document number of the purchase order returned by the passed in criteria.
     *
     * @param criteria - list of criteria to use in the retrieve
     * @return Document number string if a valid purchase order is found, null if no purchase order is found
     */
    protected String getDocumentNumberUsingPurchaseOrderCriteria(Criteria criteria) {
        List<String> returnList = getDocumentNumbersUsingPurchaseOrderCriteria(criteria);

        if (returnList.isEmpty()) {
            return null;
        }

        if (returnList.size() > 1) {
            // the list should have held only a single doc id of data but it holds 2 or more
            String errorMsg = "Expected single document number for given criteria but multiple (at least 2) were returned";
            LOG.error(errorMsg);
            throw new RuntimeException();

        } else {
            // at this part of the code, we know there's no more elements in iterator
            return returnList.get(0);
        }
    }

    /**
     * Retrieves a list of document numbers of the purchase order returned by the passed in criteria.
     *
     * @param criteria - list of criteria to use in the retrieve
     * @return Iterator of document numbers
     */
    protected List<String> getDocumentNumbersUsingPurchaseOrderCriteria(Criteria criteria) {
        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(PurchaseOrderDocument.class, criteria);
        List<String> returnList = new ArrayList<String>();

        rqbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);

        List<PurchaseOrderDocument> poDocs = (List<PurchaseOrderDocument>) getPersistenceBrokerTemplate().getCollectionByQuery(rqbc);

        for (PurchaseOrderDocument poDoc : poDocs) {
            returnList.add(poDoc.getDocumentNumber());
        }

        return returnList;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.PurchaseOrderDao#itemExistsOnPurchaseOrder(java.lang.Integer, java.lang.String)
     */
    @Override
    public boolean itemExistsOnPurchaseOrder(Integer poItemLineNumber, String docNumber){
        boolean existsInPo = false;

        Criteria criteria = new Criteria();
        criteria.addEqualTo("documentNumber", docNumber);
        criteria.addEqualTo("itemLineNumber", poItemLineNumber);

        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(PurchaseOrderItem.class, criteria);
      //  rqbc.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER });
        rqbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);

        List<PurchaseOrderItem> poItems = (List<PurchaseOrderItem>) getPersistenceBrokerTemplate().getCollectionByQuery(rqbc);
        if (!poItems.isEmpty()) {
            existsInPo = true;
        }

        return existsInPo;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.PurchaseOrderDao#getAllOpenPurchaseOrders(java.util.List)
     */
    @Override
    public List<AutoClosePurchaseOrderView> getAllOpenPurchaseOrders(List<String> excludedVendorChoiceCodes) {
        LOG.debug("getAllOpenPurchaseOrdersForAutoClose() started");
        Criteria criteria = new Criteria();
        criteria.addIsNull(PurapPropertyConstants.RECURRING_PAYMENT_TYPE_CODE);
        criteria.addEqualTo(PurapPropertyConstants.TOTAL_ENCUMBRANCE, new KualiDecimal(0));
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_CURRENT_INDICATOR, true);
        for (String excludeCode : excludedVendorChoiceCodes) {
            criteria.addNotEqualTo(PurapPropertyConstants.VENDOR_CHOICE_CODE, excludeCode);
        }
        criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.APPLICATION_DOCUMENT_STATUS, PurapConstants.PurchaseOrderStatuses.APPDOC_OPEN);
        QueryByCriteria qbc = new QueryByCriteria(AutoClosePurchaseOrderView.class, criteria);
        if (LOG.isDebugEnabled()) {
            LOG.debug("getAllOpenPurchaseOrdersForAutoClose() Query criteria is " + criteria.toString());
        }
        List<AutoClosePurchaseOrderView> l = (List<AutoClosePurchaseOrderView>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        LOG.debug("getAllOpenPurchaseOrdersForAutoClose() ended.");

        return l;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.PurchaseOrderDao#getAutoCloseRecurringPurchaseOrders(java.util.List)
     */
    @Override
    public List<AutoClosePurchaseOrderView> getAutoCloseRecurringPurchaseOrders(List<String> excludedVendorChoiceCodes) {
        LOG.debug("getAutoCloseRecurringPurchaseOrders() started.");
        Criteria criteria = new Criteria();
        criteria.addNotNull(PurapPropertyConstants.RECURRING_PAYMENT_TYPE_CODE);
        //PURCHASE_ORDER_STATUS_CODE does not exist in tables anymore but it is on workflowdocument.
        //the checking for open status is done in PurchaseOrderServiceImpl class - autoCloseRecurringOrders method.
        for (String excludeCode : excludedVendorChoiceCodes) {
            criteria.addNotEqualTo(PurapPropertyConstants.VENDOR_CHOICE_CODE, excludeCode);
        }
        QueryByCriteria qbc = new QueryByCriteria(AutoClosePurchaseOrderView.class, criteria);
        if (LOG.isDebugEnabled()) {
            LOG.debug("getAutoCloseRecurringPurchaseOrders() Query criteria is " + criteria.toString());
        }
        List<AutoClosePurchaseOrderView> l = (List<AutoClosePurchaseOrderView>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        //we need to include in this list only those whose workflowdocument appDocStatus = APPDOC_OPEN

        LOG.debug("getAutoCloseRecurringPurchaseOrders() ended.");

        return l;
    }

    @Override
    public List<PurchaseOrderDocument> getPendingPurchaseOrdersForFaxing() {
        LOG.debug("Getting pending purchase orders for faxing");
        Criteria criteria = new Criteria();
        QueryByCriteria qbc = new QueryByCriteria(PurchaseOrderDocument.class,criteria);
        List<PurchaseOrderDocument> l = (List<PurchaseOrderDocument>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);

        return l;
   }
}
