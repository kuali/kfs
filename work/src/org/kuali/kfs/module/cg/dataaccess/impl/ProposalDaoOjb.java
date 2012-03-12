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

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.cg.businessobject.Proposal;
import org.kuali.kfs.module.cg.dataaccess.ProposalDao;
import org.kuali.kfs.module.cg.document.ProposalAwardCloseDocument;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * @see ProposalDao
 */
public class ProposalDaoOjb extends PlatformAwareDaoBaseOjb implements ProposalDao {

    /**
     * @see org.kuali.kfs.module.cg.dataaccess.ProposalDao#getProposalsToClose(org.kuali.kfs.module.cg.businessobject.Close)
     */
    public Collection<Proposal> getProposalsToClose(ProposalAwardCloseDocument close) {

        Criteria criteria = new Criteria();
        criteria.addIsNull("proposalClosingDate");
        criteria.addLessOrEqualThan("proposalSubmissionDate", close.getCloseOnOrBeforeDate());

        return (Collection<Proposal>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Proposal.class, criteria));
    }

}
