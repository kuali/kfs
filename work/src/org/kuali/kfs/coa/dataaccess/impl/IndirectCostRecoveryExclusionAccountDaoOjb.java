/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.chart.bo.IndirectCostRecoveryExclusionAccount;
import org.kuali.module.chart.dao.IndirectCostRecoveryExclusionAccountDao;

/**
 * This class implements the {@link IndirectCostRecoveryExclusionAccountDao} data access methods using Ojb
 */
public class IndirectCostRecoveryExclusionAccountDaoOjb extends PlatformAwareDaoBaseOjb implements IndirectCostRecoveryExclusionAccountDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IndirectCostRecoveryExclusionAccountDaoOjb.class);

    public IndirectCostRecoveryExclusionAccountDaoOjb() {
        super();
    }

    /**
     * @see org.kuali.module.chart.dao.IndirectCostRecoveryExclusionAccountDao#getByPrimaryKey(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public IndirectCostRecoveryExclusionAccount getByPrimaryKey(String chartOfAccountsCode, String accountNumber, String objectChartOfAccountsCode, String objectCode) {
        LOG.debug("getByPrimaryKey() started");

        Criteria crit = new Criteria();
        crit.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        crit.addEqualTo("accountNumber", accountNumber);
        crit.addEqualTo("financialObjectChartOfAccountCode", objectChartOfAccountsCode);
        crit.addEqualTo("financialObjectCode", objectCode);

        QueryByCriteria qbc = QueryFactory.newQuery(IndirectCostRecoveryExclusionAccount.class, crit);
        return (IndirectCostRecoveryExclusionAccount) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    /**
     * @see org.kuali.module.chart.dao.IndirectCostRecoveryExclusionAccountDao#existByAccount(java.lang.String, java.lang.String)
     */
    public boolean existByAccount(String chartOfAccountsCode, String accountNumber) {
        LOG.debug("existByAccount() started");

        Criteria crit = new Criteria();
        crit.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        crit.addEqualTo("accountNumber", accountNumber);

        ReportQueryByCriteria q = QueryFactory.newReportQuery(IndirectCostRecoveryExclusionAccount.class, crit);
        q.setAttributes(new String[] { "chartOfAccountsCode" });
        q.setDistinct(true);

        Iterator iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        return iter.hasNext();
    }

}
