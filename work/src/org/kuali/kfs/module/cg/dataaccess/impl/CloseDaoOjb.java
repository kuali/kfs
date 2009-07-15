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
package org.kuali.kfs.module.cg.dataaccess.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.cg.dataaccess.CloseDao;
import org.kuali.kfs.module.cg.document.ProposalAwardCloseDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;

/**
 * @see CloseDao
 */
public class CloseDaoOjb extends PlatformAwareDaoBaseOjb implements CloseDao {

    /**
     * @see org.kuali.kfs.module.cg.dataaccess.CloseDao#getMaxApprovedClose()
     */
    public ProposalAwardCloseDocument getMaxApprovedClose() {

        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        Date today = dateTimeService.getCurrentSqlDateMidnight();
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo("userInitiatedCloseDate", today);
        criteria.addEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.ENROUTE);
        QueryByCriteria query = QueryFactory.newQuery(ProposalAwardCloseDocument.class, criteria);
        query.addOrderByDescending("documentNumber");

        Iterator<ProposalAwardCloseDocument> documents = (Iterator<ProposalAwardCloseDocument>) getPersistenceBrokerTemplate().getIteratorByQuery(query);
        ArrayList<String> documentHeaderIds = new ArrayList<String>();
        while (documents.hasNext()) {
            ProposalAwardCloseDocument document = (ProposalAwardCloseDocument) documents.next();
            documentHeaderIds.add(document.getDocumentNumber());
        }

        List<ProposalAwardCloseDocument> docs = null;
        
        if (documentHeaderIds.size() > 0) { 
            
            try {
            docs = SpringContext.getBean(DocumentService.class).getDocumentsByListOfDocumentHeaderIds(ProposalAwardCloseDocument.class, documentHeaderIds);
            } catch (WorkflowException we) {
                throw new RuntimeException(we);
            }
            
            if (docs.size() > 1) {
                ProposalAwardCloseDocument close = docs.remove(0);
                Date closeDate = close.getCloseOnOrBeforeDate();
                for (ProposalAwardCloseDocument cfdaClose: docs) {
                    if (cfdaClose.getCloseOnOrBeforeDate().equals(closeDate)) {
                        //disapprove docs with same close date??
                    }
                        
                }
                return close;
                
            } else if (docs.size() == 1) {
                return docs.get(0);
            } else
                return null;
            
        } else {
            return null;
        }
        
      
    }

    public ProposalAwardCloseDocument getMostRecentClose() {
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        Date today = dateTimeService.getCurrentSqlDateMidnight();
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo("userInitiatedCloseDate", today);
        criteria.addEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
        QueryByCriteria query = QueryFactory.newQuery(ProposalAwardCloseDocument.class, criteria);
        query.addOrderByDescending("documentNumber");

        Iterator<ProposalAwardCloseDocument> documents = (Iterator<ProposalAwardCloseDocument>) getPersistenceBrokerTemplate().getIteratorByQuery(query);
        ArrayList<String> documentHeaderIds = new ArrayList<String>();
        while (documents.hasNext()) {
            ProposalAwardCloseDocument document = (ProposalAwardCloseDocument) documents.next();
            documentHeaderIds.add(document.getDocumentNumber());
        }

        List<ProposalAwardCloseDocument> docs = null;
        
        if (documentHeaderIds.size() > 0) { 
            
            try {
            docs = SpringContext.getBean(DocumentService.class).getDocumentsByListOfDocumentHeaderIds(ProposalAwardCloseDocument.class, documentHeaderIds);
            } catch (WorkflowException we) {
                throw new RuntimeException(we);
            }
            
            if (docs.size() > 1) {
                ProposalAwardCloseDocument close = docs.remove(0);
                return close;
                
            } else if (docs.size() == 1) {
                return docs.get(0);
            } else
                return null;
            
        } else {
            return null;
        }
    }
    
    
    /**
     * @see org.kuali.kfs.module.cg.dataaccess.CloseDao#save(org.kuali.kfs.module.cg.businessobject.Close)
     */
    public void save(ProposalAwardCloseDocument close) {
        getPersistenceBrokerTemplate().store(close);
    }

}
