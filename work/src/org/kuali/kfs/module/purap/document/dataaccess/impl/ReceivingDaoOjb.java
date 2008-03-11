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
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.dao.ReceivingDao;
import org.kuali.module.purap.document.ReceivingLineDocument;

/**
 * OJB implementation of PurchaseOrderDao.
 */
public class ReceivingDaoOjb extends PlatformAwareDaoBaseOjb implements ReceivingDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReceivingDaoOjb.class);

    public boolean isReceivingLineDocumentInProcessForPurchaseOrder(Integer id) {
        boolean isInProcess = false;

        //TODO: Create a list of receiving line documents and loop through and check the workflow status 
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, id);        
        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(ReceivingLineDocument.class, criteria);

        Collection rlDocs = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(ReceivingLineDocument.class, criteria));        
        ReceivingLineDocument rlDoc = null;
                
        for (Iterator<ReceivingLineDocument> iter = rlDocs.iterator(); iter.hasNext();) {
            rlDoc = iter.next();

            //if not in one of the final states, then is in process, so return true and break
            if(!(rlDoc.getDocumentHeader().getWorkflowDocument().stateIsCanceled() ||
               rlDoc.getDocumentHeader().getWorkflowDocument().stateIsException() ||
               rlDoc.getDocumentHeader().getWorkflowDocument().stateIsFinal()) ){
                
                isInProcess = false;
                break;
            }
        }

        return isInProcess;
    }
}
