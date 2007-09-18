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
package org.kuali.module.pdp.dao.ojb;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.pdp.bo.AchBank;
import org.kuali.module.pdp.dao.AchBankDao;

public class AchBankDaoOjb extends PlatformAwareDaoBaseOjb implements AchBankDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AchBankDaoOjb.class);

    /**
     * @see org.kuali.module.pdp.dao.AchBankDao#save(org.kuali.module.pdp.bo.AchBank)
     */
    public void save(AchBank ab) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(ab);
    }

    /**
     * @see org.kuali.module.pdp.dao.AchBankDao#emptyTable()
     */
    public void emptyTable() {
        LOG.debug("emptyTable() started");

        Criteria criteria = new Criteria();
        QueryByCriteria qbc = new QueryByCriteria(AchBank.class,criteria);
        getPersistenceBrokerTemplate().deleteByQuery(qbc);

        // You must always clear the cache after a delete by query or you risk 
        // getting bad data.  See the OJB docs for more information.
        getPersistenceBrokerTemplate().clearCache();
    }
}
