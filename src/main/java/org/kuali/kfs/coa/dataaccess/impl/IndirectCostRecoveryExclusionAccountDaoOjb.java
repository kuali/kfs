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
package org.kuali.kfs.coa.dataaccess.impl;

import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryExclusionAccount;
import org.kuali.kfs.coa.dataaccess.IndirectCostRecoveryExclusionAccountDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * This class implements the {@link IndirectCostRecoveryExclusionAccountDao} data access methods using Ojb
 */
public class IndirectCostRecoveryExclusionAccountDaoOjb extends PlatformAwareDaoBaseOjb implements IndirectCostRecoveryExclusionAccountDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IndirectCostRecoveryExclusionAccountDaoOjb.class);

    public IndirectCostRecoveryExclusionAccountDaoOjb() {
        super();
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.IndirectCostRecoveryExclusionAccountDao#getByPrimaryKey(java.lang.String, java.lang.String,
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
     * @see org.kuali.kfs.coa.dataaccess.IndirectCostRecoveryExclusionAccountDao#existByAccount(java.lang.String, java.lang.String)
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
