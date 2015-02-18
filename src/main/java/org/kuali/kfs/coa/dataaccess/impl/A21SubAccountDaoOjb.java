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

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.dataaccess.A21SubAccountDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * This class provides data access to {@link A21SubAccount} through OJB
 */
public class A21SubAccountDaoOjb extends PlatformAwareDaoBaseOjb implements A21SubAccountDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(A21SubAccountDaoOjb.class);

    public A21SubAccountDaoOjb() {
        super();
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.A21SubAccountDao#getByPrimaryKey(java.lang.String, java.lang.String, java.lang.String)
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
