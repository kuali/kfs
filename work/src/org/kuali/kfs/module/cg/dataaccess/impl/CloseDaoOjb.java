/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.cg.dataaccess.impl;

import java.sql.Date;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.dataaccess.CloseDao;
import org.kuali.kfs.module.cg.document.ProposalAwardCloseDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * @see CloseDao
 */
public class CloseDaoOjb extends PlatformAwareDaoBaseOjb implements CloseDao {

    /**
     * @see org.kuali.kfs.module.cg.dataaccess.CloseDao#getMaxApprovedClose()
     */
    public String getMaxApprovedClose(Date today) {

        Criteria criteria = new Criteria();
        criteria.addEqualTo(CGPropertyConstants.PROPOSAL_AWARD_CLOSE_DOC_USER_INITIATED_CLOSE_DATE, today);
        criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.ENROUTE);

        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(ProposalAwardCloseDocument.class, criteria);
        rqbc.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER });
        rqbc.addOrderByDescending(KFSPropertyConstants.DOCUMENT_NUMBER);

        Iterator<?> documentHeaderIdsIterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);

        if (documentHeaderIdsIterator.hasNext()) {
            Object[] result = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(documentHeaderIdsIterator);
            if (result[0] != null) {
                return result[0].toString();
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    /**
     * @see org.kuali.kfs.module.cg.dataaccess.CloseDao#getMostRecentClose(java.sql.Date)
     */
    public String getMostRecentClose(Date today) {

        Criteria criteria = new Criteria();
        criteria.addEqualTo(CGPropertyConstants.PROPOSAL_AWARD_CLOSE_DOC_USER_INITIATED_CLOSE_DATE, today);
        criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);
        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(ProposalAwardCloseDocument.class, criteria);
        rqbc.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER });

        rqbc.addOrderByDescending(KFSPropertyConstants.DOCUMENT_NUMBER);
        getPersistenceBrokerTemplate().clearCache();
        if ( getPersistenceBrokerTemplate().getCount(rqbc) == 0) return null;

        Iterator<?> documentHeaderIdsIterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);


        Object[] result = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(documentHeaderIdsIterator);
        if (result[0] != null) {
            return result[0].toString();
        }
        else {
            return null;
        }
    }

}
