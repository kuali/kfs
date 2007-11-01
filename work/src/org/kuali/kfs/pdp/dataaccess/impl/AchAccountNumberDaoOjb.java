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
/*
 * Created on Aug 19, 2004
 */
package org.kuali.module.pdp.dao.ojb;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.pdp.bo.AchAccountNumber;
import org.kuali.module.pdp.dao.AchAccountNumberDao;


/**
 * @author HSTAPLET
 */
public class AchAccountNumberDaoOjb extends PlatformAwareDaoBaseOjb implements AchAccountNumberDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AchAccountNumberDaoOjb.class);

    public AchAccountNumber get(Integer id) {
        LOG.debug("get(id) started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("id", id);

        return (AchAccountNumber) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(AchAccountNumber.class, criteria));
    }

    public void delete(AchAccountNumber achAccountNumber) {
        LOG.debug("delete() enter method");
        getPersistenceBrokerTemplate().delete(achAccountNumber);
    }
}
