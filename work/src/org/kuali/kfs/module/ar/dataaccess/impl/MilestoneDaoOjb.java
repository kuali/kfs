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
package org.kuali.kfs.module.ar.dataaccess.impl;

import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.dataaccess.MilestoneDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * Milestone DAO OJB object that implements MilestoneDao
 */
public class MilestoneDaoOjb extends PlatformAwareDaoBaseOjb implements MilestoneDao {

    @Override
    public Collection<Milestone> getMatchingMilestoneByProposalIdAndInListOfMilestoneId(Long proposalNumber, List<Long> milestoneIds) throws Exception {
        if (milestoneIds == null) {
            throw new Exception("List<Long> milestoneIds cannot be null");
        }

        Criteria mainCriteria = new Criteria();
        Criteria proposalIdCriteria = new Criteria();

        proposalIdCriteria.addEqualTo("proposalNumber", proposalNumber);

        for (Long milestoneId : milestoneIds) {
            Criteria criteria = new Criteria();
            criteria.addEqualTo("milestoneIdentifier", milestoneId);
            mainCriteria.addOrCriteria(criteria);
        }

        mainCriteria.addAndCriteria(proposalIdCriteria);
        QueryByCriteria query = new QueryByCriteria(Milestone.class, mainCriteria);

        Collection<Milestone> milestones = getPersistenceBrokerTemplate().getCollectionByQuery(query);

        return milestones;
    }


}
