/*
 * Copyright 2005-2006 The Kuali Foundation
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

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.dataaccess.SubAccountDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;


/**
 * This class is the OJB implementation of the SubAccountDao interface.
 */
public class SubAccountDaoOjb extends PlatformAwareDaoBaseOjb implements SubAccountDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SubAccountDaoOjb.class);

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
