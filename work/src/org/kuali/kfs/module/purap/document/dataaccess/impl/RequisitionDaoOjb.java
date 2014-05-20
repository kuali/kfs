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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.dataaccess.RequisitionDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
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
    @Override
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

    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.RequisitionDao#getDocumentsAwaitingContractManagerAssignment()
     */

    @Override
    public List<String> getDocumentNumbersAwaitingContractManagerAssignment() {
        return getDocumentNumbersOfRequisitionsByCriteria(getDocumentsAwaitingContractManagerAssignmentCriteria(), false);
    }

    @Override
    public List<RequisitionDocument> getDocumentsAwaitingContractManagerAssignment() {
        return (List<RequisitionDocument>) getPersistenceBrokerTemplate().
                getCollectionByQuery(new QueryByCriteria(RequisitionDocument.class, getDocumentsAwaitingContractManagerAssignmentCriteria()));
    }

    protected Criteria getDocumentsAwaitingContractManagerAssignmentCriteria () {
        Criteria criteria = new Criteria();

        criteria.addIn(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE,
                Arrays.asList(KFSConstants.DocumentStatusCodes.FINAL, KFSConstants.DocumentStatusCodes.PROCESSED));

        criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.APPLICATION_DOCUMENT_STATUS,
                PurapConstants.RequisitionStatuses.APPDOC_AWAIT_CONTRACT_MANAGER_ASSGN);

        return criteria;
    }


    protected List<String> getDocumentNumbersOfRequisitionsByCriteria(Criteria criteria, boolean orderByAscending) {
        LOG.debug("getDocumentNumberOfRequisitionsByCriteria() started");
        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(RequisitionDocument.class, criteria);
        if (orderByAscending) {
            rqbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }
        else {
            rqbc.addOrderByDescending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }

        List<String> returnList = new ArrayList<String>();

        List<RequisitionDocument> reqDocs = (List<RequisitionDocument>) getPersistenceBrokerTemplate().getCollectionByQuery(rqbc);
        for (RequisitionDocument reqDoc : reqDocs) {
            returnList.add(reqDoc.getDocumentNumber());
        }

        return returnList;
    }

}
