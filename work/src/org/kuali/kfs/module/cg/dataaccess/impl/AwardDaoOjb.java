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
package org.kuali.module.cg.dao.ojb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.module.cg.bo.Award;
import org.kuali.module.cg.bo.ProposalClose;
import org.kuali.module.cg.dao.AwardDao;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

public class AwardDaoOjb extends PersistenceBrokerDaoSupport implements AwardDao {

    public void deleteAll() {
        getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(Award.class, new Criteria()));
    }
    
    public Collection<Award> getAwardsToClose(ProposalClose close) {

        Criteria criteria = new Criteria();
        criteria.addIsNull("awardClosingDate");
        criteria.addLessOrEqualThan("awardEntryDate", close.getLastClosedDate());
        criteria.addNotEqualTo("awardStatusCode", "U");
        
        return (Collection<Award>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Award.class, criteria));
        
    }

    public void save(Award award) {
        getPersistenceBrokerTemplate().store(award);
    }
    
}
