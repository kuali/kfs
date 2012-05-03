/*
 * Copyright 2007-2008 The Kuali Foundation
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

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.CorrectionReceivingDocument;
import org.kuali.kfs.module.purap.document.LineItemReceivingDocument;
import org.kuali.kfs.module.purap.document.dataaccess.ReceivingDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.springframework.transaction.annotation.Transactional;

/**
 * OJB implementation of PurchaseOrderDao.
 */
@Transactional
public class ReceivingDaoOjb extends PlatformAwareDaoBaseOjb implements ReceivingDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReceivingDaoOjb.class);

    public List<String> getDocumentNumbersByPurchaseOrderId(Integer id) {        
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, id);
        
        List<String> returnList = getDocumentNumbersOfReceivingLineByCriteria(criteria, false);

        return returnList;
    }

    public List<String> getCorrectionReceivingDocumentNumbersByPurchaseOrderId(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("lineItemReceivingDocument.purchaseOrderIdentifier", id);        

        List<String> returnList =  getDocumentNumbersOfCorrectionReceivingByCriteria(criteria, false);
        
        return returnList;
    }

    public List<String> getCorrectionReceivingDocumentNumbersByReceivingLineNumber(String receivingDocumentNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.LINE_ITEM_RECEIVING_DOCUMENT_NUMBER, receivingDocumentNumber);        

        List<String> returnList = getDocumentNumbersOfCorrectionReceivingByCriteria(criteria, false);

        return returnList;
    }
    
    /**
     * Retrieves a document number for a payment request by user defined criteria and sorts the values ascending if orderByAscending
     * parameter is true, descending otherwise.
     * 
     * @param criteria - list of criteria to use in the retrieve
     * @param orderByAscending - boolean to sort results ascending if true, descending otherwise
     * @return - Iterator of document numbers
     */
    protected List<String> getDocumentNumbersOfReceivingLineByCriteria(Criteria criteria, boolean orderByAscending) {
        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(LineItemReceivingDocument.class, criteria);
        if (orderByAscending) {
            rqbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }
        else {
            rqbc.addOrderByDescending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }
        
        List<LineItemReceivingDocument> lineItemRecvDocs = (List<LineItemReceivingDocument>) getPersistenceBrokerTemplate().getCollectionByQuery(rqbc);
        
        List<String> returnList = new ArrayList<String>();
        
        for (LineItemReceivingDocument lineItemRecvDoc : lineItemRecvDocs) {
            returnList.add(lineItemRecvDoc.getDocumentNumber());
        }
        
        return returnList;
    }

    protected List<String> getDocumentNumbersOfCorrectionReceivingByCriteria(Criteria criteria, boolean orderByAscending) {
        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(CorrectionReceivingDocument.class, criteria);
        if (orderByAscending) {
            rqbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }
        else {
            rqbc.addOrderByDescending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }
        
        List<CorrectionReceivingDocument> correctionRecvDocs = (List<CorrectionReceivingDocument>) getPersistenceBrokerTemplate().getCollectionByQuery(rqbc);
        
        List<String> returnList = new ArrayList<String>();
        
        for (CorrectionReceivingDocument correctionRecvDoc : correctionRecvDocs) {
            returnList.add(correctionRecvDoc.getDocumentNumber());
        }
        
        return returnList;
    }

    public List<String> duplicateBillOfLadingNumber(Integer poId, String billOfLadingNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, poId);
        criteria.addEqualTo(PurapPropertyConstants.SHIPMENT_BILL_OF_LADING_NUMBER, billOfLadingNumber);        

        List<String> returnList = getDocumentNumbersOfReceivingLineByCriteria(criteria, false);
        
        return returnList;
    }

    public List<String> duplicatePackingSlipNumber(Integer poId, String packingSlipNumber) {      
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, poId);
        criteria.addEqualTo(PurapPropertyConstants.SHIPMENT_PACKING_SLIP_NUMBER, packingSlipNumber);        
        List<String> returnList = getDocumentNumbersOfReceivingLineByCriteria(criteria, false);
        
        return returnList;
    }

    public List<String> duplicateVendorDate(Integer poId, Date vendorDate) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, poId);
        criteria.addEqualTo(PurapPropertyConstants.SHIPMENT_RECEIVED_DATE, vendorDate);        
        List<String> returnList = getDocumentNumbersOfReceivingLineByCriteria(criteria, false);
        
        return returnList;
    }
}
