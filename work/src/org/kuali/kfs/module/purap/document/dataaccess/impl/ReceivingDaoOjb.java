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
package org.kuali.kfs.module.purap.document.dataaccess.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.CorrectionReceivingDocument;
import org.kuali.kfs.module.purap.document.LineItemReceivingDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.dataaccess.ReceivingDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.exception.InfrastructureException;
import org.kuali.rice.kns.service.DocumentService;

/**
 * OJB implementation of PurchaseOrderDao.
 */
public class ReceivingDaoOjb extends PlatformAwareDaoBaseOjb implements ReceivingDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReceivingDaoOjb.class);

    public List<String> getDocumentNumbersByPurchaseOrderId(Integer id) {        

        List<String> returnList = new ArrayList<String>();
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, id);        
        Iterator<Object[]> iter = getDocumentNumbersOfReceivingLineByCriteria(criteria, false);
        while (iter.hasNext()) {
            Object[] cols = (Object[]) iter.next();
            returnList.add((String) cols[0]);
        }
        return returnList;

    }

    public List<String> getCorrectionReceivingDocumentNumbersByPurchaseOrderId(Integer id) {

        List<String> returnList = new ArrayList<String>();
        Criteria criteria = new Criteria();
        criteria.addEqualTo("lineItemReceivingDocument.purchaseOrderIdentifier", id);        
        Iterator<Object[]> iter = getDocumentNumbersOfCorrectionReceivingByCriteria(criteria, false);
        while (iter.hasNext()) {
            Object[] cols = (Object[]) iter.next();
            returnList.add((String) cols[0]);
        }
        return returnList;

    }

    public List<String> getCorrectionReceivingDocumentNumbersByReceivingLineNumber(String receivingDocumentNumber) {

        List<String> returnList = new ArrayList<String>();
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.LINE_ITEM_RECEIVING_DOCUMENT_NUMBER, receivingDocumentNumber);        
        Iterator<Object[]> iter = getDocumentNumbersOfCorrectionReceivingByCriteria(criteria, false);
        while (iter.hasNext()) {
            Object[] cols = (Object[]) iter.next();
            returnList.add((String) cols[0]);
        }
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
    private Iterator<Object[]> getDocumentNumbersOfReceivingLineByCriteria(Criteria criteria, boolean orderByAscending) {
        
        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(LineItemReceivingDocument.class, criteria);
        rqbc.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER });
        if (orderByAscending) {
            rqbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }
        else {
            rqbc.addOrderByDescending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);
    }

    private Iterator<Object[]> getDocumentNumbersOfCorrectionReceivingByCriteria(Criteria criteria, boolean orderByAscending) {
        
        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(CorrectionReceivingDocument.class, criteria);
        rqbc.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER });
        if (orderByAscending) {
            rqbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }
        else {
            rqbc.addOrderByDescending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);
    }

    public List<String> duplicateBillOfLadingNumber(Integer poId, String billOfLadingNumber) {
        
        List<String> returnList = new ArrayList<String>();
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, poId);
        criteria.addEqualTo(PurapPropertyConstants.SHIPMENT_BILL_OF_LADING_NUMBER, billOfLadingNumber);        
        Iterator<Object[]> iter = getDocumentNumbersOfReceivingLineByCriteria(criteria, false);

        while (iter.hasNext()) {
            Object[] cols = (Object[]) iter.next();
            returnList.add((String) cols[0]);
        }
        
        return returnList;
    }

    public List<String> duplicatePackingSlipNumber(Integer poId, String packingSlipNumber) {      

        List<String> returnList = new ArrayList<String>();
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, poId);
        criteria.addEqualTo(PurapPropertyConstants.SHIPMENT_PACKING_SLIP_NUMBER, packingSlipNumber);        
        Iterator<Object[]> iter = getDocumentNumbersOfReceivingLineByCriteria(criteria, false);

        while (iter.hasNext()) {
            Object[] cols = (Object[]) iter.next();
            returnList.add((String) cols[0]);
        }
        
        return returnList;
    }

    public List<String> duplicateVendorDate(Integer poId, Date vendorDate) {
 
        List<String> returnList = new ArrayList<String>();
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, poId);
        criteria.addEqualTo(PurapPropertyConstants.SHIPMENT_RECEIVED_DATE, vendorDate);        
        Iterator<Object[]> iter = getDocumentNumbersOfReceivingLineByCriteria(criteria, false);

        while (iter.hasNext()) {
            Object[] cols = (Object[]) iter.next();
            returnList.add((String) cols[0]);
        }
        
        return returnList;
    }

    public List<LineItemReceivingDocument> getReceivingDocumentsForPOAmendment() {
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo("lineItemReceivingStatusCode", PurapConstants.LineItemReceivingStatus.AWAITING_PO_OPEN_STATUS);

        Query query = new QueryByCriteria(LineItemReceivingDocument.class, criteria);
        Iterator<LineItemReceivingDocument> documents = (Iterator<LineItemReceivingDocument>) getPersistenceBrokerTemplate().getIteratorByQuery(query);
        ArrayList<String> documentHeaderIds = new ArrayList<String>();
        while (documents.hasNext()) {
            LineItemReceivingDocument document = (LineItemReceivingDocument) documents.next();
            documentHeaderIds.add(document.getDocumentNumber());
        }

        if (documentHeaderIds.size() > 0) {
            try {
                return SpringContext.getBean(DocumentService.class).getDocumentsByListOfDocumentHeaderIds(LineItemReceivingDocument.class, documentHeaderIds);
            }
            catch (WorkflowException e) {
                throw new InfrastructureException("unable to retrieve LineItemReceivingDocuments", e);
            }
        }
        else {
            return null;
        }

    }
    
}
