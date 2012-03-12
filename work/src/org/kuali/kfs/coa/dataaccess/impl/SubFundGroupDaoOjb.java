/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.coa.dataaccess.impl;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.coa.dataaccess.SubFundGroupDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * This class implements the {@link SubFundGroupDao} data access methods using Ojb
 */
public class SubFundGroupDaoOjb extends PlatformAwareDaoBaseOjb implements SubFundGroupDao {

    /**
     * @see org.kuali.kfs.coa.dataaccess.SubFundGroupDao#getByChartAndAccount(java.lang.String, java.lang.String)
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
