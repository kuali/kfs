/*
 * Copyright 2006 The Kuali Foundation.
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
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.dao.A21SubAccountDao;

/**
 * 
 * This class provides data access to {@link A21SubAccount} through OJB
 */
public class A21SubAccountDaoOjb extends PlatformAwareDaoBaseOjb implements A21SubAccountDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(A21SubAccountDaoOjb.class);

    public A21SubAccountDaoOjb() {
        super();
    }

    /**
     * 
     * @see org.kuali.module.chart.dao.A21SubAccountDao#getByPrimaryKey(java.lang.String, java.lang.String, java.lang.String)
     */
    public A21SubAccount getByPrimaryKey(String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        LOG.debug("getByPrimaryKey() started");

        Criteria crit = new Criteria();
        crit.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        crit.addEqualTo("accountNumber", accountNumber);
        crit.addEqualTo("subAccountNumber", subAccountNumber);

        QueryByCriteria qbc = QueryFactory.newQuery(A21SubAccount.class, crit);
        return (A21SubAccount) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }
}
