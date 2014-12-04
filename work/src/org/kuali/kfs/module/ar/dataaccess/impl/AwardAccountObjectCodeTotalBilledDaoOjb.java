/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.dataaccess.impl;

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
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
     * @see org.kuali.kfs.module.ar.dataaccess.AwardAccountObjectCodeTotalBilledDao#
     * getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(java.util.List)
     */
    @Override
    public List<AwardAccountObjectCodeTotalBilled> getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(List<ContractsAndGrantsBillingAwardAccount> awardAccounts) {
        Criteria criteria = new Criteria();

        for (ContractsAndGrantsBillingAwardAccount awardAccount : awardAccounts) {
            Criteria accounts = new Criteria();
            Criteria chartOfAccount = new Criteria();
            Criteria accountNumber = new Criteria();
            Criteria proposalNumber = new Criteria();

            chartOfAccount.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
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
