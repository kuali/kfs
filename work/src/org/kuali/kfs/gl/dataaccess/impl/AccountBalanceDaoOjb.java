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
package org.kuali.module.gl.dao.ojb;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.bo.AccountBalance;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.AccountBalanceConsolidationDao;
import org.kuali.module.gl.dao.AccountBalanceDao;
import org.kuali.module.gl.dao.AccountBalanceLevelDao;
import org.kuali.module.gl.dao.AccountBalanceObjectDao;
import org.kuali.module.gl.util.OJBUtility;

public class AccountBalanceDaoOjb extends PlatformAwareDaoBaseOjb implements AccountBalanceDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountBalanceDaoOjb.class);

    private AccountBalanceConsolidationDao accountBalanceConsolidationDao;
    private AccountBalanceLevelDao accountBalanceLevelDao;
    private AccountBalanceObjectDao accountBalanceObjectDao;

    static final private String OBJ_TYP_CD = "financialObject.financialObjectTypeCode";

    /**
     * @see org.kuali.module.gl.dao.AccountBalanceDao#getByTransaction(org.kuali.module.gl.bo.Transaction)
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
     * @see org.kuali.module.gl.dao.AccountBalanceDao#save(org.kuali.module.gl.bo.AccountBalance)
     */
    public void save(AccountBalance ab) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(ab);
    }

    /**
     * @see org.kuali.module.gl.dao.AccountBalanceDao#findAvailableAccountBalance(java.util.Map, boolean)
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
     * @see org.kuali.module.gl.dao.AccountBalanceDao#findAvailableAccountBalance(java.util.Map)
     */
    public Iterator findAvailableAccountBalance(Map fieldValues) {
        LOG.debug("findAvailableAccountBalance(Map) started");

        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new AccountBalance());
        QueryByCriteria query = QueryFactory.newReportQuery(AccountBalance.class, criteria);
        OJBUtility.limitResultSize(query);

        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.AccountBalanceDao#findAccountBalanceByConsolidationByObjectTypes(java.lang.String[],
     *      java.lang.Integer, java.lang.String, java.lang.String, boolean, boolean, int)
     */
    public List findAccountBalanceByConsolidationByObjectTypes(String[] objectTypes, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, boolean isExcludeCostShare, boolean isConsolidated, int pendingEntriesCode) {
        LOG.debug("findAccountBalanceByConsolidationByObjectTypes() started");

        // This is in a new object just to make each class smaller and easier to read
        try {
            return accountBalanceConsolidationDao.findAccountBalanceByConsolidationObjectTypes(objectTypes, universityFiscalYear, chartOfAccountsCode, accountNumber, isExcludeCostShare, isConsolidated, pendingEntriesCode);
        }
        catch (Exception e) {
            LOG.error("findAccountBalanceByConsolidation() " + e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @see org.kuali.module.gl.dao.AccountBalanceDao#findAccountBalanceByLevel(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.lang.String, boolean, boolean, int)
     */
    public List findAccountBalanceByLevel(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialConsolidationObjectCode, boolean isCostShareExcluded, boolean isConsolidated, int pendingEntriesCode) {
        LOG.debug("findAccountBalanceByLevel() started");

        // This is in a new object just to make each class smaller and easier to read
        try {
            return accountBalanceLevelDao.findAccountBalanceByLevel(universityFiscalYear, chartOfAccountsCode, accountNumber, financialConsolidationObjectCode, isCostShareExcluded, isConsolidated, pendingEntriesCode);
        }
        catch (Exception ex) {
            LOG.error("findAccountBalanceByLevel() " + ex.getMessage(), ex);
            throw new RuntimeException("error executing findAccountBalanceByLevel()", ex);
        }
    }

    /**
     * @see org.kuali.module.gl.dao.AccountBalanceDao#findAccountBalanceByObject(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, boolean, boolean, int)
     */
    public List findAccountBalanceByObject(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectLevelCode, String financialReportingSortCode, boolean isCostShareExcluded, boolean isConsolidated, int pendingEntriesCode) {
        LOG.debug("findAccountBalanceByObject() started");

        // This is in a new object just to make each class smaller and easier to read
        try {
            return accountBalanceObjectDao.findAccountBalanceByObject(universityFiscalYear, chartOfAccountsCode, accountNumber, financialObjectLevelCode, financialReportingSortCode, isCostShareExcluded, isConsolidated, pendingEntriesCode);
        }
        catch (Exception ex) {
            LOG.error("findAccountBalanceByObject() " + ex.getMessage(), ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    /**
     * @see org.kuali.module.gl.dao.AccountBalanceDao#purgeYearByChart(java.lang.String, int)
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