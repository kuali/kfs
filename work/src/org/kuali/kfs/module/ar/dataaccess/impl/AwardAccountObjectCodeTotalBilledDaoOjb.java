/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ar.dataaccess.impl;

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAwardAccount;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.AwardAccountObjectCodeTotalBilled;
import org.kuali.kfs.module.ar.dataaccess.AwardAccountObjectCodeTotalBilledDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * Implementations of this interface provide access to persisted AwardAccounts.
 */
public class AwardAccountObjectCodeTotalBilledDaoOjb extends PlatformAwareDaoBaseOjb implements AwardAccountObjectCodeTotalBilledDao {

    /*
     * (non-Javadoc)
     * @see org.kuali.kfs.module.ar.dataaccess.AwardAccountObjectCodeTotalBilledDao#save(org.kuali.kfs.module.ar.businessobject.
     * AwardAccountObjectCodeTotalBilled)
     */
    public void save(AwardAccountObjectCodeTotalBilled invoiceAccountTotalBilled) {
        getPersistenceBrokerTemplate().store(invoiceAccountTotalBilled);
    }

    /*
     * (non-Javadoc)
     * @see org.kuali.kfs.module.ar.dataaccess.AwardAccountObjectCodeTotalBilledDao#
     * getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(java.util.List)
     */
    public List<AwardAccountObjectCodeTotalBilled> getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(List<ContractsAndGrantsCGBAwardAccount> awardAccounts) {
        Criteria criteria = new Criteria();

        for (ContractsAndGrantsCGBAwardAccount awardAccount : awardAccounts) {
            Criteria accounts = new Criteria();
            Criteria chartOfAccount = new Criteria();
            Criteria accountNumber = new Criteria();
            Criteria proposalNumber = new Criteria();

            chartOfAccount.addEqualTo(ArPropertyConstants.CustomerInvoiceItemCodes.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
            accountNumber.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount.getAccountNumber());
            proposalNumber.addEqualTo(KFSPropertyConstants.PROPOSAL_NUMBER, awardAccount.getProposalNumber());
            accounts.addAndCriteria(chartOfAccount);
            accounts.addAndCriteria(accountNumber);
            accounts.addAndCriteria(proposalNumber);
            criteria.addOrCriteria(accounts);
        }

        QueryByCriteria queryByCriteria = new QueryByCriteria(AwardAccountObjectCodeTotalBilled.class, criteria);

        return (List<AwardAccountObjectCodeTotalBilled>) getPersistenceBrokerTemplate().getCollectionByQuery(queryByCriteria);
    }
}
