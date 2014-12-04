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
package org.kuali.kfs.module.cg.dataaccess.impl;

import java.sql.Date;
import java.util.Collection;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.Proposal;
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
    @Override
    public String getMaxApprovedClose(Date today) {

        Criteria criteria = new Criteria();
        criteria.addEqualTo(CGPropertyConstants.PROPOSAL_AWARD_CLOSE_DOC_USER_INITIATED_CLOSE_DATE, today);
        criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.ENROUTE);

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
    @Override
    public String getMostRecentClose(Date today) {

        Criteria criteria = new Criteria();
        criteria.addEqualTo(CGPropertyConstants.PROPOSAL_AWARD_CLOSE_DOC_USER_INITIATED_CLOSE_DATE, today);
        criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);
        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(ProposalAwardCloseDocument.class, criteria);
        rqbc.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER });

        rqbc.addOrderByDescending(KFSPropertyConstants.DOCUMENT_NUMBER);
        getPersistenceBrokerTemplate().clearCache();
        if ( getPersistenceBrokerTemplate().getCount(rqbc) == 0) {
            return null;
        }

        Iterator<?> documentHeaderIdsIterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);


        Object[] result = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(documentHeaderIdsIterator);
        if (result[0] != null) {
            return result[0].toString();
        }
        else {
            return null;
        }
    }

    /**
     * @see org.kuali.kfs.module.cg.dataaccess.AwardDao#getAwardsToClose(org.kuali.kfs.module.cg.businessobject.Close)
     */
    @Override
    public Collection<Award> getAwardsToClose(ProposalAwardCloseDocument close) {

        Criteria criteria = new Criteria();
        criteria.addIsNull("awardClosingDate");
        criteria.addLessOrEqualThan("awardEntryDate", close.getCloseOnOrBeforeDate());
        criteria.addNotEqualTo("awardStatusCode", "U");

        return  getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Award.class, criteria));
    }

    /**
     * @see org.kuali.kfs.module.cg.dataaccess.ProposalDao#getProposalsToClose(org.kuali.kfs.module.cg.businessobject.Close)
     */
    @Override
    public Collection<Proposal> getProposalsToClose(ProposalAwardCloseDocument close) {

        Criteria criteria = new Criteria();
        criteria.addIsNull("proposalClosingDate");
        criteria.addLessOrEqualThan("proposalSubmissionDate", close.getCloseOnOrBeforeDate());

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Proposal.class, criteria));
    }

}
