/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.dataaccess.impl;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.gl.businessobject.SufficientFundBalances;
import org.kuali.kfs.gl.dataaccess.SufficientFundBalancesDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * An OJB implementation of the SufficientFundBalancesDao
 */
public class SufficientFundBalancesDaoOjb extends PlatformAwareDaoBaseOjb implements SufficientFundBalancesDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundBalancesDaoOjb.class);

    /**
     * Builds an OJB query based on the parameter values and returns all the sufficient fund balances that match that record
     * 
     * @param universityFiscalYear the university fiscal year of sufficient fund balances to find
     * @param chartOfAccountsCode the chart of accounts code of sufficient fund balances to find
     * @param financialObjectCode the object code of sufficient fund balances to find
     * @return a Collection of sufficient fund balances, qualified by the parameter values
     * @see org.kuali.kfs.gl.dataaccess.SufficientFundBalancesDao#getByObjectCode(java.lang.Integer, java.lang.String, java.lang.String)
     */
    public Collection getByObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode) {
        LOG.debug("getByObjectCode() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        crit.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, financialObjectCode);

        QueryByCriteria qbc = QueryFactory.newQuery(SufficientFundBalances.class, crit);
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * Deletes sufficient fund balances associated with a given year, chart, and account number
     * 
     * @param universityFiscalYear the university fiscal year of sufficient fund balances to delete
     * @param chartOfAccountsCode the chart code of sufficient fund balances to delete
     * @param accountNumber the account number of sufficient fund balances to delete
     * @return the number of records deleted
     * @see org.kuali.kfs.gl.dataaccess.SufficientFundBalancesDao#deleteByAccountNumber(java.lang.Integer, java.lang.String, java.lang.String)
     */
    public int deleteByAccountNumber(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber) {
        LOG.debug("deleteByAccountNumber() started");
        
        Criteria crit = new Criteria();
        crit.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        crit.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);

        QueryByCriteria qbc = QueryFactory.newQuery(SufficientFundBalances.class, crit);
        int count = getPersistenceBrokerTemplate().getCount(qbc);
        getPersistenceBrokerTemplate().deleteByQuery(qbc);

        // This has to be done because deleteByQuery deletes the rows from the table,
        // but it doesn't delete them from the cache. If the cache isn't cleared,
        // later on, you could get an Optimistic Lock Exception because OJB thinks rows
        // exist when they really don't.
        getPersistenceBrokerTemplate().clearCache();
        
        return count;
    }

    /**
     * This method should only be used in unit tests. It loads all the gl_sf_balances_t rows in memory into a collection. This won't
     * sace for production.
     * 
     * @return a Collection of all sufficient funds records in the database
     */
    public Collection testingGetAllEntries() {
        LOG.debug("testingGetAllEntries() started");

        Criteria criteria = new Criteria();
        QueryByCriteria qbc = QueryFactory.newQuery(SufficientFundBalances.class, criteria);
        qbc.addOrderBy(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, true);
        qbc.addOrderBy(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, true);
        qbc.addOrderBy(KFSPropertyConstants.ACCOUNT_NUMBER, true);
        qbc.addOrderBy(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, true);

        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }
}
