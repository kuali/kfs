/*
 * Copyright 2006 The Kuali Foundation.
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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.PropertyConstants;
import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.gl.OracleSpecific;
import org.kuali.module.gl.bo.AccountBalance;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.AccountBalanceDao;
import org.kuali.module.gl.util.OJBUtility;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * 
 * 
 */
public class AccountBalanceDaoOjb extends PersistenceBrokerDaoSupport implements AccountBalanceDao, OracleSpecific {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountBalanceDaoOjb.class);

    private DateTimeService dateTimeService;
    private OptionsService optionsService;
    static final private String OBJ_TYP_CD = "financialObject.financialObjectTypeCode";

    public AccountBalanceDaoOjb() {
        super();
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.AccountBalanceDao#getByTransaction(org.kuali.module.gl.bo.Transaction)
     */
    public AccountBalance getByTransaction(Transaction t) {
        LOG.debug("getByTransaction() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR, t.getUniversityFiscalYear());
        crit.addEqualTo(PropertyConstants.CHART_OF_ACCOUNTS_CODE, t.getChartOfAccountsCode());
        crit.addEqualTo(PropertyConstants.ACCOUNT_NUMBER, t.getAccountNumber());
        crit.addEqualTo(PropertyConstants.SUB_ACCOUNT_NUMBER, t.getSubAccountNumber());
        crit.addEqualTo(PropertyConstants.OBJECT_CODE, t.getFinancialObjectCode());
        crit.addEqualTo(PropertyConstants.SUB_OBJECT_CODE, t.getFinancialSubObjectCode());

        QueryByCriteria qbc = QueryFactory.newQuery(AccountBalance.class, crit);
        return (AccountBalance) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    /**
     * 
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

        String[] attributes = new String[] { PropertyConstants.UNIVERSITY_FISCAL_YEAR, PropertyConstants.CHART_OF_ACCOUNTS_CODE, PropertyConstants.ACCOUNT_NUMBER, PropertyConstants.OBJECT_CODE, OBJ_TYP_CD, "sum(currentBudgetLineBalanceAmount)", "sum(accountLineActualsBalanceAmount)", "sum(accountLineEncumbranceBalanceAmount)" };

        String[] groupBy = new String[] { PropertyConstants.UNIVERSITY_FISCAL_YEAR, PropertyConstants.CHART_OF_ACCOUNTS_CODE, PropertyConstants.ACCOUNT_NUMBER, PropertyConstants.OBJECT_CODE, OBJ_TYP_CD };

        query.setAttributes(attributes);
        query.addGroupBy(groupBy);
        OJBUtility.limitResultSize(query);

        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.AccountBalanceDao#getConsolidatedAccountBalanceRecordCount(java.util.Map)
     */
    public Iterator getConsolidatedAccountBalanceRecordCount(Map fieldValues) {
        LOG.debug("getConsolidatedAccountBalanceRecordCount() started");

        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new AccountBalance());
        ReportQueryByCriteria query = QueryFactory.newReportQuery(AccountBalance.class, criteria);

        query.setAttributes(new String[] { "count(*)" });

        String[] groupBy = new String[] { PropertyConstants.UNIVERSITY_FISCAL_YEAR, PropertyConstants.CHART_OF_ACCOUNTS_CODE, PropertyConstants.ACCOUNT_NUMBER, PropertyConstants.OBJECT_CODE, OBJ_TYP_CD };
        query.addGroupBy(groupBy);

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
     * 
     * @see org.kuali.module.gl.dao.AccountBalanceDao#findAccountBalanceByConsolidationByObjectTypes(java.lang.String[],
     *      java.lang.Integer, java.lang.String, java.lang.String, boolean, boolean, int)
     */
    public List findAccountBalanceByConsolidationByObjectTypes(String[] objectTypes, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, boolean isExcludeCostShare, boolean isConsolidated, int pendingEntriesCode) {
        LOG.debug("findAccountBalanceByConsolidationByObjectTypes() started");

        // This is in a new object just to make each class smaller and easier to read
        try {
            Connection c = getPersistenceBroker(true).serviceConnectionManager().getConnection();
            AccountBalanceConsolidation abc = new AccountBalanceConsolidation(this, optionsService, dateTimeService, c);

            return abc.findAccountBalanceByConsolidationObjectTypes(objectTypes, universityFiscalYear, chartOfAccountsCode, accountNumber, isExcludeCostShare, isConsolidated, pendingEntriesCode);
        }
        catch (Exception e) {
            LOG.error("findAccountBalanceByConsolidation() " + e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.AccountBalanceDao#findAccountBalanceByLevel(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.lang.String, boolean, boolean, int)
     */
    public List findAccountBalanceByLevel(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialConsolidationObjectCode, boolean isCostShareExcluded, boolean isConsolidated, int pendingEntriesCode) {
        LOG.debug("findAccountBalanceByLevel() started");

        // This is in a new object just to make each class smaller and easier to read
        try {
            Connection c = getPersistenceBroker(true).serviceConnectionManager().getConnection();
            AccountBalanceLevel abl = new AccountBalanceLevel(this, optionsService, dateTimeService, c);

            return abl.findAccountBalanceByLevel(universityFiscalYear, chartOfAccountsCode, accountNumber, financialConsolidationObjectCode, isCostShareExcluded, isConsolidated, pendingEntriesCode);
        }
        catch (Exception e) {
            LOG.error("findAccountBalanceByLevel() " + e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.AccountBalanceDao#findAccountBalanceByObject(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, boolean, boolean, int)
     */
    public List findAccountBalanceByObject(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectLevelCode, String financialReportingSortCode, boolean isCostShareExcluded, boolean isConsolidated, int pendingEntriesCode) {
        LOG.debug("findAccountBalanceByObject() started");

        // This is in a new object just to make each class smaller and easier to read
        try {
            Connection c = getPersistenceBroker(true).serviceConnectionManager().getConnection();
            AccountBalanceObject abo = new AccountBalanceObject(this, optionsService, dateTimeService, c);

            return abo.findAccountBalanceByObject(universityFiscalYear, chartOfAccountsCode, accountNumber, financialObjectLevelCode, financialReportingSortCode, isCostShareExcluded, isConsolidated, pendingEntriesCode);
        }
        catch (Exception e) {
            LOG.error("findAccountBalanceByObject() " + e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.AccountBalanceDao#purgeYearByChart(java.lang.String, int)
     */
    public void purgeYearByChart(String chartOfAccountsCode, int year) {
        LOG.debug("purgeYearByChart() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(PropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        criteria.addLessThan(PropertyConstants.UNIVERSITY_FISCAL_YEAR, new Integer(year));

        getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(AccountBalance.class, criteria));

        // This is required because if any deleted account balances are in the cache, deleteByQuery doesn't
        // remove them from the cache so a future select will retrieve these deleted account balances from
        // the cache and return them. Clearing the cache forces OJB to go to the database again.
        getPersistenceBrokerTemplate().clearCache();
    }

    /**
     * Delete cost share pending entries.
     * 
     * This method is package protected for a reason
     * 
     */
    void deleteCostSharePendingEntries() {
        sqlCommand("DELETE gl_pending_entry_mt WHERE ROWID IN (SELECT g.ROWID FROM gl_pending_entry_mt g,ca_a21_sub_acct_t a WHERE (a.fin_coa_cd = g.fin_coa_cd " + "AND a.account_nbr = g.account_nbr AND a.sub_acct_nbr = g.sub_acct_nbr AND a.sub_acct_typ_cd = 'CS') AND g.person_unvl_id = USERENV('SESSIONID'))");
    }

    /**
     * Count pending entries.
     * 
     * This method is package protected for a reason
     * 
     * @return count
     */
    int countPendingEntries() {
        List results = sqlSelect("select count(*) as COUNT from gl_pending_entry_mt WHERE person_unvl_id = USERENV('SESSIONID')");
        Map row0 = (Map) results.get(0);
        BigDecimal count = (BigDecimal) row0.get("COUNT");
        return count.intValue();
    }

    /**
     * Clean up bad data in the pending entries
     * 
     * This method is package protected for a reason
     * 
     * @param universityFiscalYear fiscal year
     */
    void cleanUpPendingEntries(Integer universityFiscalYear) {
        // Clean up the data
        sqlCommand("update GL_PENDING_ENTRY_MT set univ_fiscal_yr = " + universityFiscalYear + " where PERSON_UNVL_ID = USERENV('SESSIONID')");
        sqlCommand("update gl_pending_entry_mt set SUB_ACCT_NBR = '-----' where (SUB_ACCT_NBR is null or SUB_ACCT_NBR = '     ')");
    }

    /**
     * Run a sql command
     * 
     * This method is package protected for a reason
     * 
     * @param sql Sql to run
     * 
     * @return number of rows updated
     */
    int sqlCommand(String sql) {
        LOG.info("sqlCommand() started: " + sql);

        Statement stmt = null;

        try {
            Connection c = getPersistenceBroker(true).serviceConnectionManager().getConnection();
            stmt = c.createStatement();
            return stmt.executeUpdate(sql);
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to execute: " + e.getMessage());
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (Exception e) {
                throw new RuntimeException("Unable to close connection: " + e.getMessage());
            }
        }
    }

    /**
     * Run a sql select
     * 
     * This method is package protected for a reason
     * 
     * @param sql Sql to run
     * @return List of rows returned. Each item is a Map where the key is the field name, value is the field value
     */
    List sqlSelect(String sql) {
        LOG.debug("sqlSelect() started");

        Statement stmt = null;

        try {
            Connection c = getPersistenceBroker(true).serviceConnectionManager().getConnection();
            stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery(sql);
            List result = new ArrayList();
            while (rs.next()) {
                Map row = new HashMap();
                int numColumns = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= numColumns; i++) {
                    row.put(rs.getMetaData().getColumnName(i).toUpperCase(), rs.getObject(i));
                }
                result.add(row);
            }
            return result;
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to execute: " + e.getMessage());
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (Exception e) {
                throw new RuntimeException("Unable to close connection: " + e.getMessage());
            }
        }
    }

    public void setDateTimeService(DateTimeService dts) {
        dateTimeService = dts;
    }

    public void setOptionsService(OptionsService os) {
        optionsService = os;
    }
}