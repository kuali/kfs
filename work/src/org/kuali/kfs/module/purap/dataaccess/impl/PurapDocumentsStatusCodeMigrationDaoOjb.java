/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.purap.dataaccess.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.dataaccess.PurapDocumentsStatusCodeMigrationDao;
import org.kuali.kfs.module.purap.document.LineItemReceivingDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * A class to do the database queries needed to fetch requisitions, po, preq etc., for 
 * migration of status code from purap documents to the document header's workflowdocument.
 */
public class PurapDocumentsStatusCodeMigrationDaoOjb extends PlatformAwareDaoBaseOjb {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapDocumentsStatusCodeMigrationDaoOjb.class);
    
    public List<RequisitionDocument> getRequisitionDocumentsForStatusCodeMigration() {
        LOG.debug("getRequisitionDocumentsForStatusCodeMigration() started");
        
        Criteria criteria = new Criteria();
        criteria.addNotNull("REQS_STAT_CD");
        
        QueryByCriteria query = QueryFactory.newQuery(RequisitionDocument.class, criteria);
        return (List<RequisitionDocument>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
    
    /**
     * @see org.kuali.kfs.module.purap.dataaccess.PurapDocumentsStatusCodeMigrationDao#getPurchaseOrderDocumentsForStatusCodeMigration()
     */
    public List<PurchaseOrderDocument> getPurchaseOrderDocumentsForStatusCodeMigration() {
        LOG.debug("getPurchaseOrderDocumentsForStatusCodeMigration() started");
        
        Criteria criteria = new Criteria();
        criteria.addNotNull("PO_STAT_CD");
        
        QueryByCriteria query = QueryFactory.newQuery(PurchaseOrderDocument.class, criteria);
        return (List<PurchaseOrderDocument>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
    /**
     * @see org.kuali.kfs.module.purap.dataaccess.PurapDocumentsStatusCodeMigrationDao#getPurchaseOrderVendorQuoteDocumentsForStatusCodeMigration()
     */
    public List<PurchaseOrderVendorQuote> getPurchaseOrderVendorQuoteDocumentsForStatusCodeMigration() {
        LOG.debug("getPurchaseOrderVendorQuoteDocumentsForStatusCodeMigration() started");
        
        Criteria criteria = new Criteria();
        criteria.addNotNull("PO_QT_STAT_CD");
        
        QueryByCriteria query = QueryFactory.newQuery(PurchaseOrderVendorQuote.class, criteria);
        return (List<PurchaseOrderVendorQuote>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
    
    /**
     * @see org.kuali.kfs.module.purap.dataaccess.PurapDocumentsStatusCodeMigrationDao#getPaymentRequestDocumentsForStatusCodeMigration()
     */
    public List<PaymentRequestDocument> getPaymentRequestDocumentsForStatusCodeMigration() {
        LOG.debug("getPaymentRequestDocumentsForStatusCodeMigration() started");
        
        Criteria criteria = new Criteria();
        criteria.addNotNull("PMT_RQST_STAT_CD");
        
        QueryByCriteria query = QueryFactory.newQuery(PaymentRequestDocument.class, criteria);
        return (List<PaymentRequestDocument>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
    
    /**
     * @see org.kuali.kfs.module.purap.dataaccess.PurapDocumentsStatusCodeMigrationDao#getVendorCreditMemoDocumentsForStatusCodeMigration()
     */
    public List<VendorCreditMemoDocument> getVendorCreditMemoDocumentsForStatusCodeMigration() {
        LOG.debug("getVendorCreditMemoDocumentsForStatusCodeMigration() started");
        
        Criteria criteria = new Criteria();
        criteria.addNotNull("CRDT_MEMO_STAT_CD");
        
        QueryByCriteria query = QueryFactory.newQuery(VendorCreditMemoDocument.class, criteria);
        return (List<VendorCreditMemoDocument>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
    
    /**
     * @see org.kuali.kfs.module.purap.dataaccess.PurapDocumentsStatusCodeMigrationDao#getLineItemReceivingDocumentDocumentsForStatusCodeMigration()
     */
    public List<LineItemReceivingDocument> getLineItemReceivingDocumentDocumentsForStatusCodeMigration() {
        LOG.debug("getLineItemReceivingDocumentDocumentsForStatusCodeMigration() started");
        
        Criteria criteria = new Criteria();
        criteria.addNotNull("RCVNG_LN_STAT_CD");
        
        QueryByCriteria query = QueryFactory.newQuery(LineItemReceivingDocument.class, criteria);
        return (List<LineItemReceivingDocument>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
}   
