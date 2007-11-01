/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.module.chart.dao.ojb;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.SubFundGroup;
import org.kuali.module.chart.dao.SubFundGroupDao;

/**
 * 
 * This class implements the {@link SubFundGroupDao} data access methods using Ojb
 */
public class SubFundGroupDaoOjb extends PlatformAwareDaoBaseOjb implements SubFundGroupDao {

    /**
     * 
     * @see org.kuali.module.chart.dao.SubFundGroupDao#getByPrimaryId(java.lang.String)
     */
    public SubFundGroup getByPrimaryId(String subFundGroupCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("subFundGroupCode", subFundGroupCode);

        return (SubFundGroup) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(SubFundGroup.class, criteria));
    }

    /**
     * 
     * @see org.kuali.module.chart.dao.SubFundGroupDao#getByChartAndAccount(java.lang.String, java.lang.String)
     */
    public SubFundGroup getByChartAndAccount(String chartCode, String accountNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartCode);
        criteria.addEqualTo("accountNumber", accountNumber);

        Account account = (Account) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(Account.class, criteria));
        criteria = new Criteria();
        criteria.addEqualTo("subFundGroupCode", account.getSubFundGroupCode());

        return (SubFundGroup) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(SubFundGroup.class, criteria));
    }

}
