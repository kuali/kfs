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
import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.module.purap.document.dataaccess.BulkReceivingDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class BulkReceivingDaoOjb  extends PlatformAwareDaoBaseOjb implements BulkReceivingDao {
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BulkReceivingDaoOjb.class);

    public List<String> getDocumentNumbersByPurchaseOrderId(Integer id) {        

        List<String> returnList = new ArrayList<String>();
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, id);        
        returnList = getDocumentNumbersOfBulkReceivingByCriteria(criteria, false);

        return returnList;
    }
    
    public List<String> duplicateBillOfLadingNumber(Integer poId, String billOfLadingNumber) {
        List<String> returnList = new ArrayList<String>();
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, poId);
        criteria.addEqualTo(PurapPropertyConstants.SHIPMENT_BILL_OF_LADING_NUMBER, billOfLadingNumber);        
        returnList = getDocumentNumbersOfBulkReceivingByCriteria(criteria, false);
        
        return returnList;
    }

    public List<String> duplicatePackingSlipNumber(Integer poId, String packingSlipNumber) {      
        List<String> returnList = new ArrayList<String>();
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, poId);
        criteria.addEqualTo(PurapPropertyConstants.SHIPMENT_PACKING_SLIP_NUMBER, packingSlipNumber);        
        returnList = getDocumentNumbersOfBulkReceivingByCriteria(criteria, false);
        
        return returnList;
    }

    public List<String> duplicateVendorDate(Integer poId, Date vendorDate) {
        List<String> returnList = new ArrayList<String>();
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, poId);
        criteria.addEqualTo(PurapPropertyConstants.SHIPMENT_RECEIVED_DATE, vendorDate);        
        returnList = getDocumentNumbersOfBulkReceivingByCriteria(criteria, false);
        
        return returnList;
    }
    
    protected List<String> getDocumentNumbersOfBulkReceivingByCriteria(Criteria criteria, boolean orderByAscending) {
        List<String> returnList = new ArrayList<String>();
        
        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(BulkReceivingDocument.class, criteria);
        if (orderByAscending) {
            rqbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }else {
            rqbc.addOrderByDescending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }
        
        List<BulkReceivingDocument> bulkReceivingDocs = (List<BulkReceivingDocument>) getPersistenceBrokerTemplate().getCollectionByQuery(rqbc);
        
        for (BulkReceivingDocument bulkReceivingDoc : bulkReceivingDocs) {
            returnList.add(bulkReceivingDoc.getDocumentNumber());    
        }
        
        return returnList;
    }
}
