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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.dao.ReceivingDao;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.ReceivingLineDocument;

import edu.iu.uis.eden.clientapp.WorkflowDocument;

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
    
    /**
     * Retrieves a document number for a payment request by user defined criteria and sorts the values ascending if orderByAscending
     * parameter is true, descending otherwise.
     * 
     * @param criteria - list of criteria to use in the retrieve
     * @param orderByAscending - boolean to sort results ascending if true, descending otherwise
     * @return - Iterator of document numbers
     */
    private Iterator<Object[]> getDocumentNumbersOfReceivingLineByCriteria(Criteria criteria, boolean orderByAscending) {
        
        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(ReceivingLineDocument.class, criteria);
        rqbc.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER });
        if (orderByAscending) {
            rqbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }
        else {
            rqbc.addOrderByDescending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);
    }

}
