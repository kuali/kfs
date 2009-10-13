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
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.module.purap.document.dataaccess.BulkReceivingDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

public class BulkReceivingDaoOjb  extends PlatformAwareDaoBaseOjb implements BulkReceivingDao {
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BulkReceivingDaoOjb.class);

    public List<String> getDocumentNumbersByPurchaseOrderId(Integer id) {        

        List<String> returnList = new ArrayList<String>();
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, id);        
        Iterator<Object[]> iter = getDocumentNumbersOfBulkReceivingByCriteria(criteria, false);
        while (iter.hasNext()) {
            Object[] cols = (Object[]) iter.next();
            returnList.add((String) cols[0]);
        }
        return returnList;

    }
    
    public List<String> duplicateBillOfLadingNumber(Integer poId, 
                                                    String billOfLadingNumber) {
        
        List<String> returnList = new ArrayList<String>();
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, poId);
        criteria.addEqualTo(PurapPropertyConstants.SHIPMENT_BILL_OF_LADING_NUMBER, billOfLadingNumber);        
        Iterator<Object[]> iter = getDocumentNumbersOfBulkReceivingByCriteria(criteria, false);

        while (iter.hasNext()) {
            Object[] cols = (Object[]) iter.next();
            returnList.add((String) cols[0]);
        }
        
        return returnList;
    }

    public List<String> duplicatePackingSlipNumber(Integer poId, 
                                                   String packingSlipNumber) {      

        List<String> returnList = new ArrayList<String>();
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, poId);
        criteria.addEqualTo(PurapPropertyConstants.SHIPMENT_PACKING_SLIP_NUMBER, packingSlipNumber);        
        Iterator<Object[]> iter = getDocumentNumbersOfBulkReceivingByCriteria(criteria, false);

        while (iter.hasNext()) {
            Object[] cols = (Object[]) iter.next();
            returnList.add((String) cols[0]);
        }
        
        return returnList;
    }

    public List<String> duplicateVendorDate(Integer poId, 
                                            Date vendorDate) {
 
        List<String> returnList = new ArrayList<String>();
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, poId);
        criteria.addEqualTo(PurapPropertyConstants.SHIPMENT_RECEIVED_DATE, vendorDate);        
        Iterator<Object[]> iter = getDocumentNumbersOfBulkReceivingByCriteria(criteria, false);

        while (iter.hasNext()) {
            Object[] cols = (Object[]) iter.next();
            returnList.add((String) cols[0]);
        }
        
        return returnList;
    }
    
    protected Iterator<Object[]> getDocumentNumbersOfBulkReceivingByCriteria(Criteria criteria, 
                                                                           boolean orderByAscending) {
        
        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(BulkReceivingDocument.class, criteria);
        rqbc.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER });
        if (orderByAscending) {
            rqbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }else {
            rqbc.addOrderByDescending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);
    }
    
}
