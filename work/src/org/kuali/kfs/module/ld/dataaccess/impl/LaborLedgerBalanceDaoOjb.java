/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.dao.ojb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.bo.SufficientFundBalances;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.util.OJBUtility;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.dao.LaborLedgerBalanceDao;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

public class LaborLedgerBalanceDaoOjb extends PersistenceBrokerDaoSupport implements LaborLedgerBalanceDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborLedgerBalanceDaoOjb.class);
    private KualiConfigurationService kualiConfigurationService;

    public LaborLedgerBalanceDaoOjb() {
        super();
    }

    /**
     * @see org.kuali.module.gl.dao.LaborLedgerBalanceDao#getGlSummary(int, java.util.List)
     */
    public Iterator getGlSummary(int universityFiscalYear, List<String> balanceTypeCodes) {
        LOG.debug("getGlSummary() started");

        Criteria c = new Criteria();
        c.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        c.addIn(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, balanceTypeCodes);

        String[] attributes = new String[] { "account.subFundGroup.fundGroupCode", "sum(accountLineAnnualBalanceAmount)", "sum(financialBeginningBalanceLineAmount)", "sum(contractsGrantsBeginningBalanceAmount)", "sum(month1AccountLineAmount)", "sum(month2AccountLineAmount)", "sum(month3AccountLineAmount)", "sum(month4AccountLineAmount)", "sum(month5AccountLineAmount)", "sum(month6AccountLineAmount)", "sum(month7AccountLineAmount)", "sum(month8AccountLineAmount)", "sum(month9AccountLineAmount)", "sum(month10AccountLineAmount)", "sum(month11AccountLineAmount)", "sum(month12AccountLineAmount)", "sum(month13AccountLineAmount)" };

        String[] groupby = new String[] { "account.subFundGroup.fundGroupCode" };

        ReportQueryByCriteria query = new ReportQueryByCriteria(LedgerBalance.class, c);

        query.setAttributes(attributes);
        query.addGroupBy(groupby);
        query.addOrderByAscending("account.subFundGroup.fundGroupCode");

        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.dao.LaborLedgerBalanceDao#findBalancesForFiscalYear(java.lang.Integer)
     */
    public Iterator<LedgerBalance> findBalancesForFiscalYear(Integer year) {
        LOG.debug("findBalancesForFiscalYear() started");

        Criteria c = new Criteria();
        c.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR, year);

        QueryByCriteria query = QueryFactory.newQuery(LedgerBalance.class, c);
        query.addOrderByAscending(PropertyConstants.CHART_OF_ACCOUNTS_CODE);
        query.addOrderByAscending(PropertyConstants.ACCOUNT_NUMBER);
        query.addOrderByAscending(PropertyConstants.SUB_ACCOUNT_NUMBER);
        query.addOrderByAscending(PropertyConstants.FINANCIAL_OBJECT_CODE);
        query.addOrderByAscending(PropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        query.addOrderByAscending(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        query.addOrderByAscending(PropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);

        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.dao.LaborLedgerBalanceDao#save(org.kuali.module.gl.bo.Balance)
     */
    public void save(LedgerBalance b) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(b);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.dao.LaborLedgerBalanceDao#getBalanceByTransaction(org.kuali.module.gl.bo.Transaction)
     */
    public LedgerBalance getBalanceByTransaction(Transaction t) {
        LOG.debug("getBalanceByTransaction() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR, t.getUniversityFiscalYear());
        crit.addEqualTo(PropertyConstants.CHART_OF_ACCOUNTS_CODE, t.getChartOfAccountsCode());
        crit.addEqualTo(PropertyConstants.ACCOUNT_NUMBER, t.getAccountNumber());
        crit.addEqualTo(PropertyConstants.SUB_ACCOUNT_NUMBER, t.getSubAccountNumber());
        crit.addEqualTo(PropertyConstants.FINANCIAL_OBJECT_CODE, t.getFinancialObjectCode());
        crit.addEqualTo(PropertyConstants.FINANCIAL_SUB_OBJECT_CODE, t.getFinancialSubObjectCode());
        crit.addEqualTo(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, t.getFinancialBalanceTypeCode());
        crit.addEqualTo(PropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, t.getFinancialObjectTypeCode());

        QueryByCriteria qbc = QueryFactory.newQuery(LedgerBalance.class, crit);
        return (LedgerBalance) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    /**
     * This method adds to the given criteria if the given collection is non-empty. It uses an EQUALS if there is exactly one
     * element in the collection; otherwise, its uses an IN
     * 
     * @param criteria - the criteria that might have a criterion appended
     * @param name - name of the attribute
     * @param collection - the collection to inspect
     * 
     */
    private void criteriaBuilder(Criteria criteria, String name, Collection collection) {
        criteriaBuilderHelper(criteria, name, collection, false);
    }

    /**
     * Similar to criteriaBuilder, this adds a negative criterion (NOT EQUALS, NOT IN)
     * 
     */
    private void negatedCriteriaBuilder(Criteria criteria, String name, Collection collection) {
        criteriaBuilderHelper(criteria, name, collection, true);
    }


    /**
     * This method provides the implementation for the conveniences methods criteriaBuilder & negatedCriteriaBuilder
     * 
     * @param negate - the criterion will be negated (NOT EQUALS, NOT IN) when this is true
     * 
     */
    private void criteriaBuilderHelper(Criteria criteria, String name, Collection collection, boolean negate) {
        if (collection != null) {
            int size = collection.size();
            if (size == 1) {
                if (negate) {
                    criteria.addNotEqualTo(name, collection.iterator().next());
                }
                else {
                    criteria.addEqualTo(name, collection.iterator().next());
                }
            }
            if (size > 1) {
                if (negate) {
                    criteria.addNotIn(name, collection);
                }
                else {
                    criteria.addIn(name, collection);

                }
            }
        }

    }

    public Iterator<LedgerBalance> findBalances(Account account, Integer fiscalYear, Collection includedObjectCodes, Collection excludedObjectCodes, Collection objectTypeCodes, Collection balanceTypeCodes) {
        LOG.debug("findBalances() started");

        Criteria criteria = new Criteria();

        criteria.addEqualTo(PropertyConstants.ACCOUNT_NUMBER, account.getAccountNumber());
        criteria.addEqualTo(PropertyConstants.CHART_OF_ACCOUNTS_CODE, account.getChartOfAccountsCode());

        criteria.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);

        criteriaBuilder(criteria, GLConstants.ColumnNames.OBJECT_TYPE_CODE, objectTypeCodes);
        criteriaBuilder(criteria, GLConstants.ColumnNames.BALANCE_TYPE_CODE, balanceTypeCodes);
        criteriaBuilder(criteria, GLConstants.ColumnNames.OBJECT_CODE, includedObjectCodes);
        negatedCriteriaBuilder(criteria, GLConstants.ColumnNames.OBJECT_CODE, excludedObjectCodes);

        ReportQueryByCriteria query = new ReportQueryByCriteria(LedgerBalance.class, criteria);

        // returns an iterator of all matching balances
        Iterator balances = getPersistenceBrokerTemplate().getIteratorByQuery(query);
        return balances;
    }

    /**
     * @see org.kuali.module.gl.dao.LaborLedgerBalanceDao#findCashBalance(java.util.Map, boolean)
     */
    public Iterator<LedgerBalance> findCashBalance(Map fieldValues, boolean isConsolidated) {
        LOG.debug("findCashBalance() started");

        Query query = this.getCashBalanceQuery(fieldValues, isConsolidated);
        OJBUtility.limitResultSize(query);
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.LaborLedgerBalanceDao#getCashBalanceRecordCount(java.util.Map, boolean)
     */
    public Integer getDetailedCashBalanceRecordCount(Map fieldValues) {
        LOG.debug("getDetailedCashBalanceRecordCount() started");

        Query query = this.getCashBalanceQuery(fieldValues, false);
        return getPersistenceBrokerTemplate().getCount(query);
    }

    /**
     * @see org.kuali.module.gl.dao.LaborLedgerBalanceDao#getCashBalanceRecordSize(java.util.Map, boolean)
     */
    public Iterator getConsolidatedCashBalanceRecordCount(Map fieldValues) {
        LOG.debug("getCashBalanceRecordCount() started");

        ReportQueryByCriteria query = this.getCashBalanceCountQuery(fieldValues);
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.LaborLedgerBalanceDao#findBalance(java.util.Map, boolean)
     */
    public Iterator<LedgerBalance> findBalance(Map fieldValues, boolean isConsolidated) {
        LOG.debug("findBalance() started");

        Query query = this.getBalanceQuery(fieldValues, isConsolidated);
        OJBUtility.limitResultSize(query);

        if (isConsolidated) {
            return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        }
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.LaborLedgerBalanceDao#getConsolidatedBalanceRecordCount(java.util.Map)
     */
    public Iterator getConsolidatedBalanceRecordCount(Map fieldValues) {
        LOG.debug("getBalanceRecordCount() started");

        ReportQueryByCriteria query = this.getBalanceCountQuery(fieldValues);
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    private ReportQueryByCriteria getCashBalanceCountQuery(Map fieldValues) {
        Criteria criteria = buildCriteriaFromMap(fieldValues, new LedgerBalance());
        criteria.addEqualTo(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, Constants.BALANCE_TYPE_ACTUAL);
        criteria.addEqualToField("chart.financialCashObjectCode", PropertyConstants.FINANCIAL_OBJECT_CODE);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(LedgerBalance.class, criteria);

        List groupByList = buildGroupByList();
        groupByList.remove(PropertyConstants.SUB_ACCOUNT_NUMBER);
        groupByList.remove(PropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        groupByList.remove(PropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);

        // add the group criteria into the selection statement
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);

        // set the selection attributes
        query.setAttributes(new String[] { "count(*)" });

        return query;
    }

    // build the query for cash balance search
    private Query getCashBalanceQuery(Map fieldValues, boolean isConsolidated) {
        Criteria criteria = buildCriteriaFromMap(fieldValues, new LedgerBalance());
        criteria.addEqualTo(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, Constants.BALANCE_TYPE_ACTUAL);
        criteria.addEqualToField("chart.financialCashObjectCode", PropertyConstants.FINANCIAL_OBJECT_CODE);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(LedgerBalance.class, criteria);
        List attributeList = buildAttributeList(false);
        List groupByList = buildGroupByList();

        // if consolidated, then ignore the following fields
        if (isConsolidated) {
            attributeList.remove(PropertyConstants.SUB_ACCOUNT_NUMBER);
            groupByList.remove(PropertyConstants.SUB_ACCOUNT_NUMBER);
            attributeList.remove(PropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
            groupByList.remove(PropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
            attributeList.remove(PropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
            groupByList.remove(PropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
        }

        // add the group criteria into the selection statement
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);

        // set the selection attributes
        String[] attributes = (String[]) attributeList.toArray(new String[attributeList.size()]);
        query.setAttributes(attributes);

        return query;
    }

    // build the query for balance search
    private Query getBalanceQuery(Map fieldValues, boolean isConsolidated) {
        LOG.debug("getBalanceQuery(Map, boolean) started");

        Criteria criteria = buildCriteriaFromMap(fieldValues, new LedgerBalance());
        ReportQueryByCriteria query = QueryFactory.newReportQuery(LedgerBalance.class, criteria);

        // if consolidated, then ignore subaccount number and balance type code
        if (isConsolidated) {
            List attributeList = buildAttributeList(true);
            List groupByList = buildGroupByList();

            // ignore subaccount number, sub object code and object type code
            attributeList.remove(PropertyConstants.SUB_ACCOUNT_NUMBER);
            groupByList.remove(PropertyConstants.SUB_ACCOUNT_NUMBER);
            attributeList.remove(PropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
            groupByList.remove(PropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
            attributeList.remove(PropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
            groupByList.remove(PropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);

            // set the selection attributes
            String[] attributes = (String[]) attributeList.toArray(new String[attributeList.size()]);
            query.setAttributes(attributes);

            // add the group criteria into the selection statement
            String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
            query.addGroupBy(groupBy);
        }

        return query;
    }

    // build the query for balance search
    private ReportQueryByCriteria getBalanceCountQuery(Map fieldValues) {
        Criteria criteria = buildCriteriaFromMap(fieldValues, new LedgerBalance());
        ReportQueryByCriteria query = QueryFactory.newReportQuery(LedgerBalance.class, criteria);

        // set the selection attributes
        query.setAttributes(new String[] { "count(*)" });

        List groupByList = buildGroupByList();
        groupByList.remove(PropertyConstants.SUB_ACCOUNT_NUMBER);
        groupByList.remove(PropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        groupByList.remove(PropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);

        // add the group criteria into the selection statement
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);
        return query;
    }

    /**
     * This method builds the query criteria based on the input field map
     * 
     * @param fieldValues
     * @param balance
     * @return a query criteria
     */
    private Criteria buildCriteriaFromMap(Map fieldValues, LedgerBalance balance) {
        Map localFieldValues = new HashMap();        
        localFieldValues.putAll(fieldValues);
        
        Criteria criteria = new Criteria();

        // handle encumbrance balance type
        String propertyName = PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE;
        if (localFieldValues.containsKey(propertyName)) {
            String propertyValue = (String) localFieldValues.get(propertyName);
            if (Constants.AGGREGATE_ENCUMBRANCE_BALANCE_TYPE_CODE.equals(propertyValue)) {
                localFieldValues.remove(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
                criteria.addIn(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, this.getEncumbranceBalanceTypeCodeList());
            }
        }

        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(localFieldValues, new LedgerBalance()));
        return criteria;
    }

    private List<String> getEncumbranceBalanceTypeCodeList() {
        String[] balanceTypesAsArray = kualiConfigurationService.getApplicationParameterValues("Kuali.GeneralLedger.AvailableBalanceInquiry", "GeneralLedger.BalanceInquiry.AvailableBalances.EncumbranceDrillDownBalanceTypes");
        return Arrays.asList(balanceTypesAsArray);
    }

    /**
     * This method builds the atrribute list used by balance searching
     * 
     * @param isExtended
     * @return List an attribute list
     */
    private List<String> buildAttributeList(boolean isExtended) {
        List attributeList = this.buildGroupByList();

        attributeList.add("sum(accountLineAnnualBalanceAmount)");
        attributeList.add("sum(financialBeginningBalanceLineAmount)");
        attributeList.add("sum(contractsGrantsBeginningBalanceAmount)");

        // add the entended elements into the list
        if (isExtended) {
            attributeList.add("sum(month1AccountLineAmount)");
            attributeList.add("sum(month2AccountLineAmount)");
            attributeList.add("sum(month3AccountLineAmount)");
            attributeList.add("sum(month4AccountLineAmount)");
            attributeList.add("sum(month5AccountLineAmount)");
            attributeList.add("sum(month6AccountLineAmount)");
            attributeList.add("sum(month7AccountLineAmount)");
            attributeList.add("sum(month8AccountLineAmount)");
            attributeList.add("sum(month9AccountLineAmount)");
            attributeList.add("sum(month10AccountLineAmount)");
            attributeList.add("sum(month11AccountLineAmount)");
            attributeList.add("sum(month12AccountLineAmount)");
            attributeList.add("sum(month13AccountLineAmount)");
        }
        return attributeList;
    }

    /**
     * This method builds group by attribute list used by balance searching
     * 
     * @return List an group by attribute list
     */
    private List<String> buildGroupByList() {
        List attributeList = new ArrayList();

        attributeList.add(PropertyConstants.UNIVERSITY_FISCAL_YEAR);
        attributeList.add(PropertyConstants.CHART_OF_ACCOUNTS_CODE);
        attributeList.add(PropertyConstants.ACCOUNT_NUMBER);
        attributeList.add(PropertyConstants.SUB_ACCOUNT_NUMBER);
        attributeList.add(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        attributeList.add(PropertyConstants.FINANCIAL_OBJECT_CODE);
        attributeList.add(PropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        attributeList.add(PropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);

        return attributeList;
    }

    public LedgerBalance getBalanceByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber) {
        LOG.debug("getBalanceByPrimaryId() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        crit.addEqualTo(PropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        crit.addEqualTo(PropertyConstants.ACCOUNT_NUMBER, accountNumber);

        QueryByCriteria qbc = QueryFactory.newQuery(SufficientFundBalances.class, crit);
        return (LedgerBalance) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }


    /**
     * @see org.kuali.module.gl.dao.LaborLedgerBalanceDao#getCurrentBudgetForObjectCode(java.lang.Integer, java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public LedgerBalance getCurrentBudgetForObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String objectCode) {
        LOG.debug("getCurrentBudgetForObjectCode() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        crit.addEqualTo(PropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        crit.addEqualTo(PropertyConstants.ACCOUNT_NUMBER, accountNumber);
        crit.addEqualTo(PropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);
        crit.addEqualTo(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, Constants.BALANCE_TYPE_CURRENT_BUDGET);

        QueryByCriteria qbc = QueryFactory.newQuery(LedgerBalance.class, crit);
        return (LedgerBalance) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    /**
     * Find all matching account balances.
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @return balances sorted by object code
     */
    public Iterator<LedgerBalance> findAccountBalances(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber) {
        LOG.debug("findAccountBalances() started");
        return this.findAccountBalances(universityFiscalYear, chartOfAccountsCode, accountNumber, Constants.SF_TYPE_OBJECT);
    }

    /**
     * Find all matching account balances. The Sufficient funds code is used to determine the sort of the results.
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param sfCode
     * @return
     */
    public Iterator<LedgerBalance> findAccountBalances(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String sfCode) {
        LOG.debug("findAccountBalances() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        crit.addEqualTo(PropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        crit.addEqualTo(PropertyConstants.ACCOUNT_NUMBER, accountNumber);

        QueryByCriteria qbc = QueryFactory.newQuery(LedgerBalance.class, crit);
        if (Constants.SF_TYPE_OBJECT.equals(sfCode)) {
            qbc.addOrderByAscending(PropertyConstants.FINANCIAL_OBJECT_CODE);
        }
        else if (Constants.SF_TYPE_LEVEL.equals(sfCode)) {
            qbc.addOrderByAscending(GLConstants.BalanceInquiryDrillDowns.OBJECT_LEVEL_CODE);
        }
        else if (Constants.SF_TYPE_CONSOLIDATION.equals(sfCode)) {
            qbc.addOrderByAscending(GLConstants.BalanceInquiryDrillDowns.CONSOLIDATION_OBJECT_CODE);
        }
        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }

    /**
     * Purge the sufficient funds balance table by year/chart
     * 
     * @param chart
     * @param year
     */
    public void purgeYearByChart(String chartOfAccountsCode, int year) {
        LOG.debug("purgeYearByChart() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(PropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        criteria.addLessThan(PropertyConstants.UNIVERSITY_FISCAL_YEAR, new Integer(year));

        getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(LedgerBalance.class, criteria));

        // This is required because if any deleted account balances are in the cache, deleteByQuery doesn't
        // remove them from the cache so a future select will retrieve these deleted account balances from
        // the cache and return them. Clearing the cache forces OJB to go to the database again.
        getPersistenceBrokerTemplate().clearCache();
    }

    /**
     * @param kualiConfigurationService
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

}
