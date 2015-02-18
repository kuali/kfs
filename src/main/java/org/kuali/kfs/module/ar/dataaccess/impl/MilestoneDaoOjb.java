/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2015 The Kuali Foundation
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
package org.kuali.kfs.module.ar.dataaccess.impl;

import java.util.Collection;
import java.util.Date;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.dataaccess.MilestoneDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * Milestone DAO OJB object that implements MilestoneDao
 */
public class MilestoneDaoOjb extends PlatformAwareDaoBaseOjb implements MilestoneDao {

    @Override
    public Collection<Milestone> getMilestonesForNotification(Date expectedCompletionLimitDate) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.ACTIVE, true);
        criteria.addEqualTo(ArPropertyConstants.BILLED, false);
        criteria.addIsNull(ArPropertyConstants.MilestoneFields.MILESTONE_ACTUAL_COMPLETION_DATE);
        criteria.addLessOrEqualThan(ArPropertyConstants.MilestoneFields.MILESTONE_EXPECTED_COMPLETION_DATE, expectedCompletionLimitDate);

        QueryByCriteria queryByCriteria = new QueryByCriteria(Milestone.class, criteria);
        queryByCriteria.addOrderByAscending(KFSPropertyConstants.PROPOSAL_NUMBER);
        queryByCriteria.addOrderByAscending(ArPropertyConstants.MilestoneFields.MILESTONE_NUMBER);

        return getPersistenceBrokerTemplate().getCollectionByQuery(queryByCriteria);
    }

}
