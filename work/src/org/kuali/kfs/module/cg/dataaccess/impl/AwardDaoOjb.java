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

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.dataaccess.AwardDao;
import org.kuali.kfs.module.cg.document.ProposalAwardCloseDocument;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * @see AwardDao
 */
public class AwardDaoOjb extends PlatformAwareDaoBaseOjb implements AwardDao {

    /**
     * @see org.kuali.kfs.module.cg.dataaccess.AwardDao#deleteAll()
     */
    public void deleteAll() {
        getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(Award.class, new Criteria()));
    }

    /**
     * @see org.kuali.kfs.module.cg.dataaccess.AwardDao#getAwardsToClose(org.kuali.kfs.module.cg.businessobject.Close)
     */
    public Collection<Award> getAwardsToClose(ProposalAwardCloseDocument close) {

        Criteria criteria = new Criteria();
        criteria.addIsNull("awardClosingDate");
        criteria.addLessOrEqualThan("awardEntryDate", close.getCloseOnOrBeforeDate());
        criteria.addNotEqualTo("awardStatusCode", "U");

        return (Collection<Award>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Award.class, criteria));
    }

}
