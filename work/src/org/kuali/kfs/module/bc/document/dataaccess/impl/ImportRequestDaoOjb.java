/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.budget.dao.ojb;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.bo.BudgetConstructionRequestMove;
import org.kuali.module.budget.dao.ImportRequestDao;

public class ImportRequestDaoOjb extends PlatformAwareDaoBaseOjb  implements ImportRequestDao {
    /**
     * 
     * @see org.kuali.module.budget.dao.ImportRequestDao#isNonBudgetedAccounts()
     */
    public boolean isNonBudgetedAccount(BudgetConstructionRequestMove record) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", record.getChartOfAccountsCode());
        criteria.addEqualTo("accountNumber", record.getAccountNumber());
        criteria.addEqualTo("subAccountNumber", record.getSubAccountNumber());
        
        Collection headerList = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(BudgetConstructionHeader.class, criteria));
        
        if (headerList.size() == 0)  return true;
        
        return false;
    }

}
