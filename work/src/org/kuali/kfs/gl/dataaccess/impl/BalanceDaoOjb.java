/*
 * Copyright 2005 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.CashBalance;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.dataaccess.BalanceDao;
import org.kuali.kfs.gl.dataaccess.LedgerBalanceBalancingDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * An OJB implementation of BalanceDao
 */
public class BalanceDaoOjb extends PlatformAwareDaoBaseOjb implements BalanceDao, LedgerBalanceBalancingDao {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceDaoOjb.class);

    /**
     * Does a ReportQuery to summarize GL balance data
     *
     * @param universityFiscalYear the fiscal year of balances to search for
     * @param balanceTypeCodes a list of balance type codes of balances to search for
     * @return iterator of reported on java.lang.Object arrays with the report data
     * @see org.kuali.kfs.gl.dataaccess.BalanceDao#getGlSummary(int, java.util.List)
     */
    @Override
    public Iterator<Object[]> getGlSummary(int universityFiscalYear, Collection<String> balanceTypeCodes) {
        LOG.debug("getGlSummary() started");

        Criteria c = new Criteria();
        c.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        c.addIn(KFSPropertyConstants.BALANCE_TYPE_CODE, balanceTypeCodes);

        String[] attributes = new String[] { "account.subFundGroup.fundGroupCode", "sum(accountLineAnnualBalanceAmount)", "sum(beginningBalanceLineAmount)", "sum(contractsGrantsBeginningBalanceAmount)", "sum(month1Amount)", "sum(month2Amount)", "sum(month3Amount)", "sum(month4Amount)", "sum(month5Amount)", "sum(month6Amount)", "sum(month7Amount)", "sum(month8Amount)", "sum(month9Amount)", "sum(month10Amount)", "sum(month11Amount)", "sum(month12Amount)", "sum(month13Amount)" };

        String[] groupby = new String[] { "account.subFundGroup.fundGroupCode" };

        ReportQueryByCriteria query = new ReportQueryByCriteria(Balance.class, c);

        query.setAttributes(attributes);
        query.addGroupBy(groupby);
        query.addOrderByAscending("account.subFundGroup.fundGroupCode");

        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    /**
     * Queries the database for all the balances for a given fiscal year
     *
     * @param year the university fiscal year of balances to return
     * @return an iterator over all balances for a given fiscal year
     * @see org.kuali.kfs.gl.dataaccess.BalanceDao#findBalancesForFiscalYear(java.lang.Integer)
     */
    @Override
    public Iterator<Balance> findBalancesForFiscalYear(Integer year) {
        LOG.debug("findBalancesForFiscalYear() started");

        Criteria c = new Criteria();
        c.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);

        QueryByCriteria query = QueryFactory.newQuery(Balance.class, c);
        query.addOrderByAscending(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        query.addOrderByAscending(KFSPropertyConstants.ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.OBJECT_CODE);
        query.addOrderByAscending(KFSPropertyConstants.SUB_OBJECT_CODE);
        query.addOrderByAscending(KFSPropertyConstants.BALANCE_TYPE_CODE);
        query.addOrderByAscending(KFSPropertyConstants.OBJECT_TYPE_CODE);

        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * Using values from the transaction as keys, lookup the balance the transaction would affect were it posted
     *
     * @t a transaction to look up the related balance for
     * @return a Balance that the given transaction would affect
     * @see org.kuali.kfs.gl.dataaccess.BalanceDao#getBalanceByTransaction(org.kuali.kfs.gl.businessobject.Transaction)
     */
    @Override
    public Balance getBalanceByTransaction(Transaction t) {
        LOG.debug("getBalanceByTransaction() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, t.getUniversityFiscalYear());
        crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, t.getChartOfAccountsCode());
        crit.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, t.getAccountNumber());
        crit.addEqualTo(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, t.getSubAccountNumber());
        crit.addEqualTo(KFSPropertyConstants.OBJECT_CODE, t.getFinancialObjectCode());
        crit.addEqualTo(KFSPropertyConstants.SUB_OBJECT_CODE, t.getFinancialSubObjectCode());
        crit.addEqualTo(KFSPropertyConstants.BALANCE_TYPE_CODE, t.getFinancialBalanceTypeCode());
        crit.addEqualTo(KFSPropertyConstants.OBJECT_TYPE_CODE, t.getFinancialObjectTypeCode());

        QueryByCriteria qbc = QueryFactory.newQuery(Balance.class, crit);
        return (Balance) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    /**
     * This method adds to the given criteria if the given collection is non-empty. It uses an EQUALS if there is exactly one
     * element in the collection; otherwise, its uses an IN
     *
     * @param criteria - the criteria that might have a criterion appended
     * @param name - name of the attribute
     * @param collection - the collection to inspect
     */
    protected void criteriaBuilder(Criteria criteria, String name, Collection collection) {
        criteriaBuilderHelper(criteria, name, collection, false);
    }

    /**
     * Similar to criteriaBuilder, this adds a negative criterion (NOT EQUALS, NOT IN)
     *
     * @param criteria - the criteria that might have a criterion appended
     * @param name - name of the attribute
     * @param collection - the collection to inspect
     */
    protected void negatedCriteriaBuilder(Criteria criteria, String name, Collection collection) {
        criteriaBuilderHelper(criteria, name, collection, true);
    }


    /**
     * This method provides the implementation for the conveniences methods criteriaBuilder & negatedCriteriaBuilder
     *
     * @param criteria - the criteria that might have a criterion appended
     * @param name - name of the attribute
     * @param collection - the collection to inspect
     * @param negate - the criterion will be negated (NOT EQUALS, NOT IN) when this is true
     */
    protected void criteriaBuilderHelper(Criteria criteria, String name, Collection collection, boolean negate) {
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

    /**
     * Build a query based on all the parameters, and return an Iterator of all Balances from the database that qualify
     *
     * @param account the account of balances to find
     * @param fiscalYear the fiscal year of balances to find
     * @param includedObjectCodes a Collection of object codes found balances should have one of
     * @param excludedObjectCodes a Collection of object codes found balances should not have one of
     * @param objectTypeCodes a Collection of object type codes found balances should have one of
     * @param balanceTypeCodes a Collection of balance type codes found balances should have one of
     * @return an Iterator of Balances
     * @see org.kuali.kfs.gl.dataaccess.BalanceDao#findBalances(org.kuali.kfs.coa.businessobject.Account, java.lang.Integer,
     *      java.util.Collection, java.util.Collection, java.util.Collection, java.util.Collection)
     */
    @Override
    public Iterator<Balance> findBalances(Account account, Integer fiscalYear, Collection includedObjectCodes, Collection excludedObjectCodes, Collection objectTypeCodes, Collection balanceTypeCodes) {
        LOG.debug("findBalances() started");

        Criteria criteria = new Criteria();

        criteria.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, account.getAccountNumber());
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, account.getChartOfAccountsCode());

        criteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);

        criteriaBuilder(criteria, GeneralLedgerConstants.ColumnNames.OBJECT_TYPE_CODE, objectTypeCodes);
        criteriaBuilder(criteria, GeneralLedgerConstants.ColumnNames.BALANCE_TYPE_CODE, balanceTypeCodes);
        criteriaBuilder(criteria, GeneralLedgerConstants.ColumnNames.OBJECT_CODE, includedObjectCodes);
        negatedCriteriaBuilder(criteria, GeneralLedgerConstants.ColumnNames.OBJECT_CODE, excludedObjectCodes);

        ReportQueryByCriteria query = new ReportQueryByCriteria(Balance.class, criteria);

        // returns an iterator of all matching balances
        Iterator balances = getPersistenceBrokerTemplate().getIteratorByQuery(query);
        return balances;
    }

    /**
     * Using the given fieldValues as keys, return all cash balance records. The results will be limited to the system lookup
     * results limit.
     *
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the records of cash balance entries
     * @see org.kuali.kfs.gl.dataaccess.BalanceDao#lookupCashBalance(Map, boolean, List)
     */
    @Override
    public Iterator<Balance> lookupCashBalance(Map fieldValues, boolean isConsolidated, Collection<String> encumbranceBalanceTypes) {
        LOG.debug("findCashBalance() started");

        Query query = this.getCashBalanceQuery(fieldValues, isConsolidated, encumbranceBalanceTypes);
        OJBUtility.limitResultSize(query);
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    /**
     * Get the number of detailed cash balance records that would be returned, were we to do a query based on the given fieldValues
     *
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the size collection of cash balance entry groups
     * @see org.kuali.kfs.gl.dataaccess.BalanceDao#getDetailedCashBalanceRecordCount(Map, List)
     */
    @Override
    public Integer getDetailedCashBalanceRecordCount(Map fieldValues, Collection<String> encumbranceBalanceTypes) {
        LOG.debug("getDetailedCashBalanceRecordCount() started");

        Query query = this.getCashBalanceQuery(fieldValues, false, encumbranceBalanceTypes);
        return getPersistenceBrokerTemplate().getCount(query);
    }

    /**
     * Given a map of keys, return all of the report data about qualifying cash balances
     *
     * @param fieldValues the input fields and values
     * @return the size collection of cash balance entry groups
     * @see org.kuali.kfs.gl.dataaccess.BalanceDao#getConsolidatedCashBalanceRecordCount(Map, List)
     */
    @Override
    public int getConsolidatedCashBalanceRecordCount(Map fieldValues, Collection<String> encumbranceBalanceTypes) {
        LOG.debug("getCashBalanceRecordCount() started");

        ReportQueryByCriteria query = this.getCashBalanceCountQuery(fieldValues, encumbranceBalanceTypes);
        return getPersistenceBrokerTemplate().getCount(query);
    }

    /**
     * Given a map of values, build a query out of those and find all the balances that qualify
     *
     * @param fieldValues a Map of fieldValues to use as keys in the query
     * @param isConsolidated should the results be consolidated?
     * @return an Iterator of Balances
     * @see org.kuali.kfs.gl.dataaccess.BalanceDao#findBalance(java.util.Map, boolean)
     */
    @Override
    public Iterator<Balance> findBalance(Map fieldValues, boolean isConsolidated, Collection<String> encumbranceBalanceTypes) {
        LOG.debug("findBalance() started");

        Query query = this.getBalanceQuery(fieldValues, isConsolidated, encumbranceBalanceTypes);
        OJBUtility.limitResultSize(query);

        if (isConsolidated) {
            return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        }
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * Given a Map of keys to use as a query, if we performed that query as a consolidated query... how many records would we get
     * back?
     *
     * @param fieldValues a Map of values to use as keys to build the query
     * @return an Iterator of counts...
     * @see org.kuali.kfs.gl.dataaccess.BalanceDao#getConsolidatedBalanceRecordCount(Map, List)
     */
    @Override
    public Iterator getConsolidatedBalanceRecordCount(Map fieldValues, Collection<String> encumbranceBalanceTypes) {
        LOG.debug("getBalanceRecordCount() started");

        ReportQueryByCriteria query = this.getBalanceCountQuery(fieldValues, encumbranceBalanceTypes);
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    /**
     * Builds a query for cash balances, based on the given field values
     *
     * @param fieldValues a map of keys to use when building the query
     * @return an OJB ReportQuery to use as the query
     */
    protected ReportQueryByCriteria getCashBalanceCountQuery(Map fieldValues, Collection<String> encumbranceBalanceTypes) {
        Criteria criteria = buildCriteriaFromMap(fieldValues, new CashBalance(), encumbranceBalanceTypes);
        criteria.addEqualTo(KFSPropertyConstants.BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_ACTUAL);
        criteria.addEqualToField("chart.financialCashObjectCode", KFSPropertyConstants.OBJECT_CODE);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(CashBalance.class, criteria);

        List groupByList = buildGroupByList();
        groupByList.remove(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        groupByList.remove(KFSPropertyConstants.SUB_OBJECT_CODE);
        groupByList.remove(KFSPropertyConstants.OBJECT_TYPE_CODE);

        // add the group criteria into the selection statement
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);

        return query;
    }

    /**
     * build the query for cash balance search
     *
     * @param fieldValues Map of keys to use for the query
     * @param isConsolidated should the results be consolidated?
     * @return the OJB query to perform
     */
    protected Query getCashBalanceQuery(Map fieldValues, boolean isConsolidated, Collection<String> encumbranceBalanceTypes) {
        Criteria criteria = buildCriteriaFromMap(fieldValues, new CashBalance(), encumbranceBalanceTypes);
        criteria.addEqualTo(KFSPropertyConstants.BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_ACTUAL);
        criteria.addEqualToField("chart.financialCashObjectCode", KFSPropertyConstants.OBJECT_CODE);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(CashBalance.class, criteria);
        List attributeList = buildAttributeList(false);
        List groupByList = buildGroupByList();

        // if consolidated, then ignore the following fields
        if (isConsolidated) {
            attributeList.remove(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
            groupByList.remove(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
            attributeList.remove(KFSPropertyConstants.SUB_OBJECT_CODE);
            groupByList.remove(KFSPropertyConstants.SUB_OBJECT_CODE);
            attributeList.remove(KFSPropertyConstants.OBJECT_TYPE_CODE);
            groupByList.remove(KFSPropertyConstants.OBJECT_TYPE_CODE);
        }

        // add the group criteria into the selection statement
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);

        // set the selection attributes
        String[] attributes = (String[]) attributeList.toArray(new String[attributeList.size()]);
        query.setAttributes(attributes);

        return query;
    }

    /**
     * build the query for balance search
     *
     * @param fieldValues Map of keys to use for the query
     * @param isConsolidated should the results be consolidated?
     * @return an OJB query to perform
     */
    protected Query getBalanceQuery(Map fieldValues, boolean isConsolidated, Collection<String> encumbranceBalanceTypes) {
        LOG.debug("getBalanceQuery(Map, boolean) started");

        Criteria criteria = buildCriteriaFromMap(fieldValues, new Balance(), encumbranceBalanceTypes);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(Balance.class, criteria);

        // if consolidated, then ignore subaccount number and balance type code
        if (isConsolidated) {
            List attributeList = buildAttributeList(true);
            List groupByList = buildGroupByList();

            // ignore subaccount number, sub object code and object type code
            attributeList.remove(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
            groupByList.remove(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
            attributeList.remove(KFSPropertyConstants.SUB_OBJECT_CODE);
            groupByList.remove(KFSPropertyConstants.SUB_OBJECT_CODE);
            attributeList.remove(KFSPropertyConstants.OBJECT_TYPE_CODE);
            groupByList.remove(KFSPropertyConstants.OBJECT_TYPE_CODE);

            // set the selection attributes
            String[] attributes = (String[]) attributeList.toArray(new String[attributeList.size()]);
            query.setAttributes(attributes);

            // add the group criteria into the selection statement
            String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
            query.addGroupBy(groupBy);
        }

        return query;
    }

    /**
     * build the query for balance search
     *
     * @param fieldValues Map of keys to use for the query
     * @return an OJB ReportQuery to perform
     */
    protected ReportQueryByCriteria getBalanceCountQuery(Map fieldValues, Collection<String> encumbranceBalanceTypes) {
        Criteria criteria = buildCriteriaFromMap(fieldValues, new Balance(), encumbranceBalanceTypes);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(Balance.class, criteria);

        // set the selection attributes
        query.setAttributes(new String[] { "count(*)" });

        List groupByList = buildGroupByList();
        groupByList.remove(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        groupByList.remove(KFSPropertyConstants.SUB_OBJECT_CODE);
        groupByList.remove(KFSPropertyConstants.OBJECT_TYPE_CODE);

        // add the group criteria into the selection statement
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);
        return query;
    }

    /**
     * This method builds the query criteria based on the input field map
     *
     * @param fieldValues Map of keys to use for the query
     * @param balance this really usen't used in the method
     * @return a query criteria
     */
    protected Criteria buildCriteriaFromMap(Map fieldValues, Balance balance, Collection<String> encumbranceBalanceTypes) {
        Map localFieldValues = new HashMap();
        localFieldValues.putAll(fieldValues);

        Criteria criteria = new Criteria();

        // handle encumbrance balance type
        String propertyName = KFSPropertyConstants.BALANCE_TYPE_CODE;
        if (localFieldValues.containsKey(propertyName)) {
            String propertyValue = (String) localFieldValues.get(propertyName);
            if (KFSConstants.AGGREGATE_ENCUMBRANCE_BALANCE_TYPE_CODE.equals(propertyValue)) {
                localFieldValues.remove(KFSPropertyConstants.BALANCE_TYPE_CODE);

                criteria.addIn(KFSPropertyConstants.BALANCE_TYPE_CODE, encumbranceBalanceTypes);
            }
        }

        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(localFieldValues, balance));
        return criteria;
    }

    /**
     * This method builds the atrribute list used by balance searching
     *
     * @param isExtended should we add the attributes to sum each of the monthly totals?
     * @return List an attribute list
     */
    protected List<String> buildAttributeList(boolean isExtended) {
        List attributeList = this.buildGroupByList();

        attributeList.add("sum(accountLineAnnualBalanceAmount)");
        attributeList.add("sum(beginningBalanceLineAmount)");
        attributeList.add("sum(contractsGrantsBeginningBalanceAmount)");

        // add the entended elements into the list
        if (isExtended) {
            attributeList.add("sum(month1Amount)");
            attributeList.add("sum(month2Amount)");
            attributeList.add("sum(month3Amount)");
            attributeList.add("sum(month4Amount)");
            attributeList.add("sum(month5Amount)");
            attributeList.add("sum(month6Amount)");
            attributeList.add("sum(month7Amount)");
            attributeList.add("sum(month8Amount)");
            attributeList.add("sum(month9Amount)");
            attributeList.add("sum(month10Amount)");
            attributeList.add("sum(month11Amount)");
            attributeList.add("sum(month12Amount)");
            attributeList.add("sum(month13Amount)");
        }
        return attributeList;
    }

    /**
     * This method builds group by attribute list used by balance searching
     *
     * @return List an group by attribute list
     */
    protected List<String> buildGroupByList() {
        List attributeList = new ArrayList();

        attributeList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        attributeList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        attributeList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        attributeList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        attributeList.add(KFSPropertyConstants.BALANCE_TYPE_CODE);
        attributeList.add(KFSPropertyConstants.OBJECT_CODE);
        attributeList.add(KFSPropertyConstants.SUB_OBJECT_CODE);
        attributeList.add(KFSPropertyConstants.OBJECT_TYPE_CODE);

        return attributeList;
    }

    /**
     * Since SubAccountNumber, SubObjectCode, and ObjectType are all part of the primary key of Balance, you're guaranteed to get
     * one of those records when you call this method. Let's hope the right one.
     *
     * @param universityFiscalYear the fiscal year of the CB balance to return
     * @param chartOfAccountsCode the chart of the accounts code of the CB balanes to return
     * @param accountNumber the account number of the CB balance to return
     * @param objectCode the object code of the CB balance to return
     * @return the CB Balance record
     * @see org.kuali.kfs.gl.dataaccess.BalanceDao#getCurrentBudgetForObjectCode(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public Balance getCurrentBudgetForObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String objectCode) {
        LOG.debug("getCurrentBudgetForObjectCode() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        crit.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        crit.addEqualTo(KFSPropertyConstants.OBJECT_CODE, objectCode);
        crit.addEqualTo(KFSPropertyConstants.BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_CURRENT_BUDGET);

        QueryByCriteria qbc = QueryFactory.newQuery(Balance.class, crit);
        return (Balance) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    /**
     * Find all matching account balances.
     *
     * @param universityFiscalYear the university fiscal year of balances to return
     * @param chartOfAccountsCode the chart of accounts code of balances to return
     * @param accountNumber the account number of balances to return
     * @return balances sorted by object code
     */
    @Override
    public Iterator<Balance> findAccountBalances(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber) {
        LOG.debug("findAccountBalances() started");
        return this.findAccountBalances(universityFiscalYear, chartOfAccountsCode, accountNumber, KFSConstants.SF_TYPE_OBJECT);
    }

    /**
     * Find all matching account balances. The Sufficient funds code is used to determine the sort of the results.
     *
     * @param universityFiscalYear the university fiscal year of balances to return
     * @param chartOfAccountsCode the chart of accounts code of balances to return
     * @param accountNumber the account number of balances to return
     * @param sfCode the sufficient funds code, used to sort on
     * @return an Iterator of balances
     */
    @Override
    public Iterator<Balance> findAccountBalances(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String sfCode) {
        LOG.debug("findAccountBalances() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        crit.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);

        QueryByCriteria qbc = QueryFactory.newQuery(Balance.class, crit);
        if (KFSConstants.SF_TYPE_OBJECT.equals(sfCode)) {
            qbc.addOrderByAscending(KFSPropertyConstants.OBJECT_CODE);
        }
        else if (KFSConstants.SF_TYPE_LEVEL.equals(sfCode)) {
            qbc.addOrderByAscending(GeneralLedgerConstants.BalanceInquiryDrillDowns.OBJECT_LEVEL_CODE);
        }
        else if (KFSConstants.SF_TYPE_CONSOLIDATION.equals(sfCode)) {
            qbc.addOrderByAscending(GeneralLedgerConstants.BalanceInquiryDrillDowns.CONSOLIDATION_OBJECT_CODE);
        }
        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }

    /**
     * Purge the sufficient funds balance table by year/chart
     *
     * @param chart the chart of balances to purge
     * @param year the university fiscal year of balances to purge
     */
    @Override
    public void purgeYearByChart(String chartOfAccountsCode, int year) {
        LOG.debug("purgeYearByChart() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        criteria.addLessThan(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, new Integer(year));

        getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(Balance.class, criteria));

        // This is required because if any deleted account balances are in the cache, deleteByQuery doesn't
        // remove them from the cache so a future select will retrieve these deleted account balances from
        // the cache and return them. Clearing the cache forces OJB to go to the database again.
        getPersistenceBrokerTemplate().clearCache();
    }

    /**
     * Returns the count of balances for a given fiscal year; this method is used for year end job reporting
     *
     * @param year the university fiscal year to count balances for
     * @return an int with the count of balances for that fiscal year
     * @see org.kuali.kfs.gl.dataaccess.BalanceDao#countBalancesForFiscalYear(java.lang.Integer)
     */
    @Override
    public int countBalancesForFiscalYear(Integer year) {
        LOG.debug("countBalancesForFiscalYear() started");

        Criteria c = new Criteria();
        c.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
        QueryByCriteria query = QueryFactory.newQuery(Balance.class, c);

        return getPersistenceBrokerTemplate().getCount(query);
    }

    /**
     * Finds all of the balances for the fiscal year that should be processed by nominal activity closing
     *
     * @param year the university fiscal year of balances to find
     * @return an Iterator of Balances to process
     * @see org.kuali.kfs.gl.dataaccess.BalanceDao#findNominalActivityBalancesForFiscalYear(Integer, List, SystemOptions)
     */
    @Override
    public Iterator<Balance> findNominalActivityBalancesForFiscalYear(Integer year, Collection<String> nominalActivityObjectTypeCodes, SystemOptions currentYearOptions) {
        LOG.debug("findNominalActivityBalancesForFiscalYear() started");

        Criteria c = new Criteria();
        c.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
        c.addEqualTo(KFSPropertyConstants.BALANCE_TYPE_CODE, currentYearOptions.getActualFinancialBalanceTypeCd());
        c.addIn(KFSPropertyConstants.OBJECT_TYPE_CODE, nominalActivityObjectTypeCodes);
        c.addNotEqualTo("accountLineAnnualBalanceAmount", KualiDecimal.ZERO);

        QueryByCriteria query = QueryFactory.newQuery(Balance.class, c);
        query.addOrderByAscending(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        query.addOrderByAscending(KFSPropertyConstants.ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.OBJECT_CODE);
        query.addOrderByAscending(KFSPropertyConstants.SUB_OBJECT_CODE);
        query.addOrderByAscending(KFSPropertyConstants.BALANCE_TYPE_CODE);
        query.addOrderByAscending(KFSPropertyConstants.OBJECT_TYPE_CODE);

        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.kfs.gl.dataaccess.BalanceDao#findGeneralBalancesToForwardForFiscalYear(java.lang.Integer, java.util.List,
     *      java.lang.String[])
     */
    @Override
    public Iterator<Balance> findGeneralBalancesToForwardForFiscalYear(Integer year, Collection<String> generalForwardBalanceObjectTypes, Collection<String> generalBalanceForwardBalanceTypes) {

        Criteria c = new Criteria();
        c.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
        c.addIn(KFSPropertyConstants.BALANCE_TYPE_CODE, generalBalanceForwardBalanceTypes);
        c.addIn(KFSPropertyConstants.OBJECT_TYPE_CODE, generalForwardBalanceObjectTypes);

        QueryByCriteria query = QueryFactory.newQuery(Balance.class, c);
        query.addOrderByAscending(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        query.addOrderByAscending(KFSPropertyConstants.ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.OBJECT_CODE);
        query.addOrderByAscending(KFSPropertyConstants.SUB_OBJECT_CODE);
        query.addOrderByAscending(KFSPropertyConstants.BALANCE_TYPE_CODE);
        query.addOrderByAscending(KFSPropertyConstants.OBJECT_TYPE_CODE);

        Iterator<Balance> balances = getPersistenceBrokerTemplate().getIteratorByQuery(query);

        return balances;
    }

    /**
     * @see org.kuali.kfs.gl.dataaccess.BalanceDao#findCumulativeBalancesToForwardForFiscalYear(java.lang.Integer, java.util.List,
     *      java.util.List, java.lang.String[], java.lang.String[])
     */
    @Override
    public Iterator<Balance> findCumulativeBalancesToForwardForFiscalYear(Integer year, Collection<String> cumulativeForwardBalanceObjectTypes, Collection<String> contractsAndGrantsDenotingValues, Collection<String> subFundGroupsForCumulativeBalanceForwarding, Collection<String> cumulativeBalanceForwardBalanceTypes, boolean fundGroupDenotesCGInd) {
        Criteria c = new Criteria();
        c.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
        c.addIn(KFSPropertyConstants.BALANCE_TYPE_CODE, cumulativeBalanceForwardBalanceTypes);
        c.addIn(KFSPropertyConstants.OBJECT_TYPE_CODE, cumulativeForwardBalanceObjectTypes);

        Criteria forCGCrit = new Criteria();
        if (fundGroupDenotesCGInd) {
            for (String value : contractsAndGrantsDenotingValues) {
                forCGCrit.addEqualTo("priorYearAccount.subFundGroup.fundGroupCode", value);
            }
        }
        else {
            for (String value : contractsAndGrantsDenotingValues) {
                forCGCrit.addEqualTo("priorYearAccount.subFundGroupCode", value);
            }
        }

        Criteria subFundGroupCrit = new Criteria();
        subFundGroupCrit.addIn("priorYearAccount.subFundGroupCode", subFundGroupsForCumulativeBalanceForwarding);
        forCGCrit.addOrCriteria(subFundGroupCrit);
        c.addAndCriteria(forCGCrit);

        QueryByCriteria query = QueryFactory.newQuery(Balance.class, c);
        query.addOrderByAscending(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        query.addOrderByAscending(KFSPropertyConstants.ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.OBJECT_CODE);
        query.addOrderByAscending(KFSPropertyConstants.SUB_OBJECT_CODE);
        query.addOrderByAscending(KFSPropertyConstants.BALANCE_TYPE_CODE);
        query.addOrderByAscending(KFSPropertyConstants.OBJECT_TYPE_CODE);

        Iterator<Balance> balances = getPersistenceBrokerTemplate().getIteratorByQuery(query);

        return balances;
    }

    /**
     * Returns a list of balances to return for the Organization Reversion year end job to process
     *
     * @param the university fiscal year to find balances for
     * @param endOfYear if true, use current year accounts, otherwise use prior year accounts
     * @return an Iterator of Balances to process
     * @see org.kuali.kfs.gl.dataaccess.BalanceDao#findOrganizationReversionBalancesForFiscalYear(Integer, boolean, SystemOptions)
     */
    @Override
    public Iterator<Balance> findOrganizationReversionBalancesForFiscalYear(Integer year, boolean endOfYear, SystemOptions options, List<ParameterEvaluator> parameterEvaluators) {
        LOG.debug("findOrganizationReversionBalancesForFiscalYear() started");
        Criteria c = new Criteria();
        c.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);

        for (ParameterEvaluator parameterEvaluator : parameterEvaluators) {

            String currentRule = parameterEvaluator.getValue();
            if (endOfYear) {
                currentRule = currentRule.replaceAll("account\\.", "priorYearAccount.");
            }
            if (StringUtils.isNotBlank(currentRule)) {
                String propertyName = StringUtils.substringBefore(currentRule, "=");
                List<String> ruleValues = Arrays.asList(StringUtils.substringAfter(currentRule, "=").split(";"));
                if (propertyName != null && propertyName.length() > 0 && ruleValues.size() > 0 && !StringUtils.isBlank(ruleValues.get(0))) {
                    if (parameterEvaluator.constraintIsAllow()) {
                        c.addIn(propertyName, ruleValues);
                    }
                    else {
                        c.addNotIn(propertyName, ruleValues);
                    }
                }
            }
        }
        // we only ever calculate on CB, AC, and encumbrance types, so let's only select those
        List organizationReversionBalancesToSelect = new ArrayList();
        organizationReversionBalancesToSelect.add(options.getActualFinancialBalanceTypeCd());
        organizationReversionBalancesToSelect.add(options.getFinObjTypeExpenditureexpCd());
        organizationReversionBalancesToSelect.add(options.getCostShareEncumbranceBalanceTypeCd());
        organizationReversionBalancesToSelect.add(options.getIntrnlEncumFinBalanceTypCd());
        organizationReversionBalancesToSelect.add(KFSConstants.BALANCE_TYPE_CURRENT_BUDGET);
        c.addIn(KFSPropertyConstants.BALANCE_TYPE_CODE, organizationReversionBalancesToSelect);
        QueryByCriteria query = QueryFactory.newQuery(Balance.class, c);
        query.addOrderByAscending(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        query.addOrderByAscending(KFSPropertyConstants.ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.OBJECT_CODE);
        query.addOrderByAscending(KFSPropertyConstants.SUB_OBJECT_CODE);
        query.addOrderByAscending(KFSPropertyConstants.BALANCE_TYPE_CODE);
        query.addOrderByAscending(KFSPropertyConstants.OBJECT_TYPE_CODE);

        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.kfs.gl.dataaccess.BalancingDao#findCountGreaterOrEqualThan(java.lang.Integer)
     */
    @Override
    public Integer findCountGreaterOrEqualThan(Integer year) {
        Criteria criteria = new Criteria();
        criteria.addGreaterOrEqualThan(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(Balance.class, criteria);

        return getPersistenceBrokerTemplate().getCount(query);
    }

}
