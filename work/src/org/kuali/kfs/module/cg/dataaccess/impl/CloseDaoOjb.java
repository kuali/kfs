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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.cg.businessobject.CFDAClose;
import org.kuali.kfs.module.cg.dataaccess.CloseDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.exception.InfrastructureException;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.TransactionalServiceUtils;
import org.springmodules.orm.ojb.PersistenceBrokerTemplate;

/**
 * @see CloseDao
 */
public class CloseDaoOjb extends PlatformAwareDaoBaseOjb implements CloseDao {

    /**
     * @see org.kuali.kfs.module.cg.dataaccess.CloseDao#getMaxApprovedClose()
     */
    public CFDAClose getMaxApprovedClose() {
        CFDAClose returnVal = null;

        Criteria criteria = new Criteria();
        criteria.addEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.ENROUTE);
        QueryByCriteria query = QueryFactory.newQuery(CFDAClose.class, criteria);
        query.addOrderByDescending("documentNumber");

        Iterator<CFDAClose> documents = (Iterator<CFDAClose>) getPersistenceBrokerTemplate().getIteratorByQuery(query);
        ArrayList<String> documentHeaderIds = new ArrayList<String>();
        while (documents.hasNext()) {
            CFDAClose document = (CFDAClose) documents.next();
            documentHeaderIds.add(document.getDocumentNumber());
        }

        if (documentHeaderIds.size() > 0) {

            List<CFDAClose> docs  = null;

            try {
                docs = SpringContext.getBean(DocumentService.class).getDocumentsByListOfDocumentHeaderIds(CFDAClose.class, documentHeaderIds);

                if (docs.size() >= 1) {
                    returnVal = docs.get(0);
                    if (null == returnVal.getAwardClosedCount()) {
                        returnVal.setAwardClosedCount(0L);
                    }
                    if (null == returnVal.getProposalClosedCount()) {
                        returnVal.setProposalClosedCount(0L);
                    }
                }
//                for (CFDAClose close : docs) {
//
//                    close.getDocumentHeader().getWorkflowDocument().disapprove("Was not most recent Close doc.");
//                }
            }catch (WorkflowException e) {
                throw new InfrastructureException("unable to process CFDAClose", e);
            }

        }
        
        return returnVal;
      
    }

    /**
     * @see org.kuali.kfs.module.cg.dataaccess.CloseDao#save(org.kuali.kfs.module.cg.businessobject.Close)
     */
    public void save(CFDAClose close) {
        getPersistenceBrokerTemplate().store(close);
    }

}
