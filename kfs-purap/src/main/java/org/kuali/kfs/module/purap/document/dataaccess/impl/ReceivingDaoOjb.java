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
