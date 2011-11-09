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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.dataaccess.AccountBalanceConsolidationDao;
import org.kuali.kfs.gl.dataaccess.AccountBalanceDao;
import org.kuali.kfs.gl.dataaccess.AccountBalanceLevelDao;
import org.kuali.kfs.gl.dataaccess.AccountBalanceObjectDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * An OJB implmentation of the AccountBalanceDao
 */
public class AccountBalanceDaoOjb extends PlatformAwareDaoBaseOjb implements AccountBalanceDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountBalanceDaoOjb.class);

    private AccountBalanceConsolidationDao accountBalanceConsolidationDao;
    private AccountBalanceLevelDao accountBalanceLevelDao;
    private AccountBalanceObjectDao accountBalanceObjectDao;

    static final private String OBJ_TYP_CD = "financialObject.financialObjectTypeCode";

    /**
     * Given a transaction, finds a matching account balance in the database
     * 
     * @param t a transaction to find an appropriate related account balance for
     * @return an appropriate account balance
     * @see org.kuali.kfs.gl.dataaccess.AccountBalanceDao#getByTransaction(org.kuali.kfs.gl.businessobject.Transaction)
     */
    public AccountBalance getByTransaction(Transaction t) {
        LOG.debug("getByTransaction() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, t.getUniversityFiscalYear());
        crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, t.getChartOfAccountsCode());
        crit.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, t.getAccountNumber());
        crit.addEqualTo(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, t.getSubAccountNumber());
        crit.addEqualTo(KFSPropertyConstants.OBJECT_CODE, t.getFinancialObjectCode());
        crit.addEqualTo(KFSPropertyConstants.SUB_OBJECT_CODE, t.getFinancialSubObjectCode());

        QueryByCriteria qbc = QueryFactory.newQuery(AccountBalance.class, crit);
        return (AccountBalance) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    /**
     * This method finds the available account balances according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @return the summary records of account balance entries
     * @see org.kuali.kfs.gl.dataaccess.AccountBalanceDao#findAvailableAccountBalance(java.util.Map, boolean)
     */
    public Iterator findConsolidatedAvailableAccountBalance(Map fieldValues) {
        LOG.debug("findConsolidatedAvailableAccountBalance() started");

        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new AccountBalance());
        ReportQueryByCriteria query = QueryFactory.newReportQuery(AccountBalance.class, criteria);

        String[] attributes = new String[] { KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ACCOUNT_NUMBER, KFSPropertyConstants.OBJECT_CODE, OBJ_TYP_CD, "sum(currentBudgetLineBalanceAmount)", "sum(accountLineActualsBalanceAmount)", "sum(accountLineEncumbranceBalanceAmount)" };

        String[] groupBy = new String[] { KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ACCOUNT_NUMBER, KFSPropertyConstants.OBJECT_CODE, OBJ_TYP_CD };

        query.setAttributes(attributes);
        query.addGroupBy(groupBy);
        OJBUtility.limitResultSize(query);

        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    /**
     * This method finds the available account balances according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @return account balance entries
     * @see org.kuali.kfs.gl.dataaccess.AccountBalanceDao#findAvailableAccountBalance(java.util.Map)
     */
    public Iterator findAvailableAccountBalance(Map fieldValues) {
        LOG.debug("findAvailableAccountBalance(Map) started");

        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new AccountBalance());
        QueryByCriteria query = QueryFactory.newReportQuery(AccountBalance.class, criteria);
        OJBUtility.limitResultSize(query);

        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * Get available balances by consolidation for specific object types
     * 
     * @param objectTypes the object types that reported account balances must have
     * @param universityFiscalYear the university fiscal year of account balances to find
     * @param chartOfAccountsCode the chart of accounts of account balances to find
     * @param accountNumber the account number of account balances to find
     * @param isExcludeCostShare whether cost share entries should be excluded from this inquiry
     * @param isConsolidated whether the results of this should be consolidated or not
     * @param pendingEntriesCode whether to include no pending entries, approved pending entries, or all pending entries
     * @return a List of Maps with the appropriate query results
     * @see org.kuali.kfs.gl.dataaccess.AccountBalanceDao#findAccountBalanceByConsolidationByObjectTypes(java.lang.String[],
     *      java.lang.Integer, java.lang.String, java.lang.String, boolean, boolean, int)
     */
    public List findAccountBalanceByConsolidationByObjectTypes(String[] objectTypes, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, boolean isExcludeCostShare, boolean isConsolidated, int pendingEntriesCode, SystemOptions options, UniversityDate today) {
        LOG.debug("findAccountBalanceByConsolidationByObjectTypes() started");

        // This is in a new object just to make each class smaller and easier to read
        try {
            return accountBalanceConsolidationDao.findAccountBalanceByConsolidationObjectTypes(objectTypes, universityFiscalYear, chartOfAccountsCode, accountNumber, isExcludeCostShare, isConsolidated, pendingEntriesCode, options, today);
        }
        catch (Exception e) {
            LOG.error("findAccountBalanceByConsolidation() " + e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @see org.kuali.kfs.gl.dataaccess.AccountBalanceDao#findAccountBalanceByLevel(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.lang.String, boolean, boolean, int, org.kuali.kfs.sys.businessobject.UniversityDate)
     */
    public List findAccountBalanceByLevel(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialConsolidationObjectCode, boolean isCostShareExcluded, boolean isConsolidated, int pendingEntriesCode, UniversityDate today, SystemOptions options) {
        LOG.debug("findAccountBalanceByLevel() started");

        // This is in a new object just to make each class smaller and easier to read
        try {
            return accountBalanceLevelDao.findAccountBalanceByLevel(universityFiscalYear, chartOfAccountsCode, accountNumber, financialConsolidationObjectCode, isCostShareExcluded, isConsolidated, pendingEntriesCode, today, options);
        }
        catch (Exception ex) {
            LOG.error("findAccountBalanceByLevel() " + ex.getMessage(), ex);
            throw new RuntimeException("error executing findAccountBalanceByLevel()", ex);
        }
    }

    /**
     * @see org.kuali.kfs.gl.dataaccess.AccountBalanceDao#findAccountBalanceByObject(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, boolean, boolean, int,
     *      org.kuali.kfs.sys.businessobject.UniversityDate)
     */
    public List findAccountBalanceByObject(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectLevelCode, String financialReportingSortCode, boolean isCostShareExcluded, boolean isConsolidated, int pendingEntriesCode, UniversityDate today, SystemOptions options) {
        LOG.debug("findAccountBalanceByObject() started");

        // This is in a new object just to make each class smaller and easier to read
        try {
            return accountBalanceObjectDao.findAccountBalanceByObject(universityFiscalYear, chartOfAccountsCode, accountNumber, financialObjectLevelCode, financialReportingSortCode, isCostShareExcluded, isConsolidated, pendingEntriesCode, today, options);
        }
        catch (Exception ex) {
            LOG.error("findAccountBalanceByObject() " + ex.getMessage(), ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    /**
     * Purge an entire fiscal year for a single chart.
     * 
     * @param chartOfAccountsCode the chart of accounts code of account balances to purge
     * @param year the fiscal year of account balances to purge
     * @see org.kuali.kfs.gl.dataaccess.AccountBalanceDao#purgeYearByChart(java.lang.String, int)
     */
    public void purgeYearByChart(String chartOfAccountsCode, int year) {
        LOG.debug("purgeYearByChart() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        criteria.addLessThan(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, new Integer(year));

        getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(AccountBalance.class, criteria));

        // This is required because if any deleted account balances are in the cache, deleteByQuery doesn't
        // remove them from the cache so a future select will retrieve these deleted account balances from
        // the cache and return them. Clearing the cache forces OJB to go to the database again.
        getPersistenceBrokerTemplate().clearCache();
    }

    /**
     * @see org.kuali.kfs.gl.dataaccess.AccountBalanceDao#findCountGreaterOrEqualThan(java.lang.Integer)
     */
    public Integer findCountGreaterOrEqualThan(Integer year) {
        Criteria criteria = new Criteria();
        criteria.addGreaterOrEqualThan(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(AccountBalance.class, criteria);

        return getPersistenceBrokerTemplate().getCount(query);
    }

    public AccountBalanceConsolidationDao getAccountBalanceConsolidationDao() {
        return accountBalanceConsolidationDao;
    }

    public void setAccountBalanceConsolidationDao(AccountBalanceConsolidationDao accountBalanceConsolidationDao) {
        this.accountBalanceConsolidationDao = accountBalanceConsolidationDao;
    }

    public AccountBalanceLevelDao getAccountBalanceLevelDao() {
        return accountBalanceLevelDao;
    }

    public void setAccountBalanceLevelDao(AccountBalanceLevelDao accountBalanceLevelDao) {
        this.accountBalanceLevelDao = accountBalanceLevelDao;
    }

    public AccountBalanceObjectDao getAccountBalanceObjectDao() {
        return accountBalanceObjectDao;
    }

    public void setAccountBalanceObjectDao(AccountBalanceObjectDao accountBalanceObjectDao) {
        this.accountBalanceObjectDao = accountBalanceObjectDao;
    }
}
