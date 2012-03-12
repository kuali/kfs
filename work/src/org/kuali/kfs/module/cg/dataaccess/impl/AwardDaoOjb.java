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
