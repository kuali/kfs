/*
 * Copyright 2008 The Kuali Foundation
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
