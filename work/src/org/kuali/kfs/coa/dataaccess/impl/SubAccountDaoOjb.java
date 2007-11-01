/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.dao.SubAccountDao;


/**
 * This class is the OJB implementation of the SubAccountDao interface.
 */
public class SubAccountDaoOjb extends PlatformAwareDaoBaseOjb implements SubAccountDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SubAccountDaoOjb.class);

    /**
     * Retrieves sub account business object by primary key
     * 
     * @param chartOfAccountsCode - part of composite key
     * @param accountNumber - part of composite key
     * @param subAccountNumber - part of composite key
     * @return SubAccount
     * @see SubAccountDao#getByPrimaryId(String, String, String)
     */
    public SubAccount getByPrimaryId(String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("accountNumber", accountNumber);
        criteria.addEqualTo("subAccountNumber", subAccountNumber);

        return (SubAccount) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(SubAccount.class, criteria));
    }

    /**
     * Retrieves SubAccount objects associated with the given chart-org-subAccount code combination
     * 
     * @param chartOfAccountsCode - 'Reports To' Chart of Accounts Code
     * @param organizationCode - 'Reports To' Organization Code
     * @param subAccountNumber - Sub Account Number
     * @return a list of SubAccount objects
     * @see SubAccountDao#getSubAccountsByReportsToOrganization(String, String, String)
     */
    public List getSubAccountsByReportsToOrganization(String chartOfAccountsCode, String organizationCode, String subAccountNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("financialReportChartCode", chartOfAccountsCode);
        criteria.addEqualTo("finReportOrganizationCode", organizationCode);
        criteria.addEqualTo("subAccountNumber", subAccountNumber);

        return (List) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(SubAccount.class, criteria));
    }
}
