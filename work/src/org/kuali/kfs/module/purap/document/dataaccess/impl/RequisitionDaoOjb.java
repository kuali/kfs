/*
 * Copyright 2006 The Kuali Foundation
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

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.dataaccess.RequisitionDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.springframework.transaction.annotation.Transactional;

/**
 * OJB implementation of RequisitionDao.
 */
@Transactional
public class RequisitionDaoOjb extends PlatformAwareDaoBaseOjb implements RequisitionDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RequisitionDaoOjb.class);

    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.RequisitionDao#getDocumentNumberForRequisitionId(java.lang.Integer)
     */
    public String getDocumentNumberForRequisitionId(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, id);

        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(RequisitionDocument.class, criteria);
        rqbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);

        List<RequisitionDocument> reqs = (List<RequisitionDocument>) getPersistenceBrokerTemplate().getCollectionByQuery(rqbc);
        
        if (reqs.isEmpty()) {
            return null;
        }
        if (reqs.size() > 1) {
            String errorMsg = "Expected single document number for given criteria but multiple (at least 2) were returned";
            LOG.error(errorMsg);
            throw new RuntimeException();
        } else {
            RequisitionDocument req = reqs.get(0);
            return req.getDocumentNumber();
        }
    }
}
