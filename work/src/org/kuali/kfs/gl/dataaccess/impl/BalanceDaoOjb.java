/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.OptionsService;
import org.kuali.kfs.service.ParameterEvaluator;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.OrganizationReversion;
import org.kuali.module.chart.service.BalanceTypService;
import org.kuali.module.chart.service.ObjectTypeService;
import org.kuali.module.chart.service.SubFundGroupService;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.batch.BalanceForwardStep;
import org.kuali.module.gl.batch.closing.year.service.FilteringBalanceIterator;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.SufficientFundBalances;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.BalanceDao;
import org.kuali.module.gl.util.OJBUtility;

/**
 * An OJB implementation of BalanceDao
 */
public class BalanceDaoOjb extends PlatformAwareDaoBaseOjb implements BalanceDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceDaoOjb.class);
    private ParameterService parameterService;
    private OptionsService optionsService;
    private BalanceTypService balanceTypService;

    /**
     * Does a ReportQuery to summarize GL balance data
     * 
     * @param universityFiscalYear the fiscal year of balances to search for
     * @param balanceTypeCodes a list of balance type codes of balances to search for
     * @return iterator of reported on java.lang.Object arrays with the report data
     * @see org.kuali.module.gl.dao.BalanceDao#getGlSummary(int, java.util.List)
     */
    public Iterator getGlSummary(int universityFiscalYear, List<String> balanceTypeCodes) {
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
     * @see org.kuali.module.gl.dao.BalanceDao#findBalancesForFiscalYear(java.lang.Integer)
     */
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
     * Saves a balance
     * @param b a balance to save
     * @see org.kuali.module.gl.dao.BalanceDao#save(org.kuali.module.gl.bo.Balance)
     */
    public void save(Balance b) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(b);
    }

    /**
     * Using values from the transaction as keys, lookup the balance the transaction would affect were it posted
     * 
     * @t a transaction to look up the related balance for
     * @return a Balance that the given transaction would affect
     * @see org.kuali.module.gl.dao.BalanceDao#getBalanceByTransaction(org.kuali.module.gl.bo.Transaction)
     */
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
    private void criteriaBuilder(Criteria criteria, String name, Collection collection) {
        criteriaBuilderHelper(criteria, name, collection, false);
    }

    /**
     * Similar to criteriaBuilder, this adds a negative criterion (NOT EQUALS, NOT IN)
     * 
     * @param criteria - the criteria that might have a criterion appended
     * @param name - name of the attribute
     * @param collection - the collection to inspect
     */
    private void negatedCriteriaBuilder(Criteria criteria, String name, Collection collection) {
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
     * @see org.kuali.module.gl.dao.BalanceDao#findBalances(org.kuali.module.chart.bo.Account, java.lang.Integer, java.util.Collection, java.util.Collection, java.util.Collection, java.util.Collection)
     */
    public Iterator<Balance> findBalances(Account account, Integer fiscalYear, Collection includedObjectCodes, Collection excludedObjectCodes, Collection objectTypeCodes, Collection balanceTypeCodes) {
        LOG.debug("findBalances() started");

        Criteria criteria = new Criteria();

        criteria.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, account.getAccountNumber());
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, account.getChartOfAccountsCode());

        criteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);

        criteriaBuilder(criteria, GLConstants.ColumnNames.OBJECT_TYPE_CODE, objectTypeCodes);
        criteriaBuilder(criteria, GLConstants.ColumnNames.BALANCE_TYPE_CODE, balanceTypeCodes);
        criteriaBuilder(criteria, GLConstants.ColumnNames.OBJECT_CODE, includedObjectCodes);
        negatedCriteriaBuilder(criteria, GLConstants.ColumnNames.OBJECT_CODE, excludedObjectCodes);

        ReportQueryByCriteria query = new ReportQueryByCriteria(Balance.class, criteria);

        // returns an iterator of all matching balances
        Iterator balances = getPersistenceBrokerTemplate().getIteratorByQuery(query);
        return balances;
    }

    /**
     * Using the given fieldValues as keys, return all cash balance records
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the records of cash balance entries
     * @see org.kuali.module.gl.dao.BalanceDao#findCashBalance(java.util.Map, boolean)
     */
    public Iterator<Balance> findCashBalance(Map fieldValues, boolean isConsolidated) {
        LOG.debug("findCashBalance() started");

        Query query = this.getCashBalanceQuery(fieldValues, isConsolidated);
        OJBUtility.limitResultSize(query);
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    /**
     * Get the number of detailed cash balance records that would be returned, were we to do a query based on the given fieldValues
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the size collection of cash balance entry groups
     * @see org.kuali.module.gl.dao.BalanceDao#getCashBalanceRecordCount(java.util.Map, boolean)
     */
    public Integer getDetailedCashBalanceRecordCount(Map fieldValues) {
        LOG.debug("getDetailedCashBalanceRecordCount() started");

        Query query = this.getCashBalanceQuery(fieldValues, false);
        return getPersistenceBrokerTemplate().getCount(query);
    }

    /**
     * Given a map of keys, return all of the report data about qualifying cash balances
     * 
     * @param fieldValues the input fields and values
     * @return the size collection of cash balance entry groups
     * @see org.kuali.module.gl.dao.BalanceDao#getCashBalanceRecordSize(java.util.Map, boolean)
     */
    public Iterator getConsolidatedCashBalanceRecordCount(Map fieldValues) {
        LOG.debug("getCashBalanceRecordCount() started");

        ReportQueryByCriteria query = this.getCashBalanceCountQuery(fieldValues);
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    /**
     * Given a map of values, build a query out of those and find all the balances that qualify
     * 
     * @param fieldValues a Map of fieldValues to use as keys in the query
     * @param isConsolidated should the results be consolidated?
     * @return an Iterator of Balances
     * @see org.kuali.module.gl.dao.BalanceDao#findBalance(java.util.Map, boolean)
     */
    public Iterator<Balance> findBalance(Map fieldValues, boolean isConsolidated) {
        LOG.debug("findBalance() started");

        Query query = this.getBalanceQuery(fieldValues, isConsolidated);
        OJBUtility.limitResultSize(query);

        if (isConsolidated) {
            return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        }
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * Given a Map of keys to use as a query, if we performed that query as a consolidated query...
     * how many records would we get back?
     * 
     * @param fieldValues a Map of values to use as keys to build the query
     * @return an Iterator of counts...
     * @see org.kuali.module.gl.dao.BalanceDao#getConsolidatedBalanceRecordCount(java.util.Map)
     */
    public Iterator getConsolidatedBalanceRecordCount(Map fieldValues) {
        LOG.debug("getBalanceRecordCount() started");

        ReportQueryByCriteria query = this.getBalanceCountQuery(fieldValues);
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    /**
     * Builds a query for cash balances, based on the given field values
     * 
     * @param fieldValues a map of keys to use when building the query
     * @return an OJB ReportQuery to use as the query 
     */
    private ReportQueryByCriteria getCashBalanceCountQuery(Map fieldValues) {
        Criteria criteria = buildCriteriaFromMap(fieldValues, new Balance());
        criteria.addEqualTo(KFSPropertyConstants.BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_ACTUAL);
        criteria.addEqualToField("chart.financialCashObjectCode", KFSPropertyConstants.OBJECT_CODE);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(Balance.class, criteria);

        List groupByList = buildGroupByList();
        groupByList.remove(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        groupByList.remove(KFSPropertyConstants.SUB_OBJECT_CODE);
        groupByList.remove(KFSPropertyConstants.OBJECT_TYPE_CODE);

        // add the group criteria into the selection statement
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);

        // set the selection attributes
        query.setAttributes(new String[] { "count(*)" });

        return query;
    }

    /**
     * build the query for cash balance search
     * 
     * @param fieldValues Map of keys to use for the query
     * @param isConsolidated should the results be consolidated?
     * @return the OJB query to perform
     */
    private Query getCashBalanceQuery(Map fieldValues, boolean isConsolidated) {
        Criteria criteria = buildCriteriaFromMap(fieldValues, new Balance());
        criteria.addEqualTo(KFSPropertyConstants.BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_ACTUAL);
        criteria.addEqualToField("chart.financialCashObjectCode", KFSPropertyConstants.OBJECT_CODE);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(Balance.class, criteria);
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
    private Query getBalanceQuery(Map fieldValues, boolean isConsolidated) {
        LOG.debug("getBalanceQuery(Map, boolean) started");

        Criteria criteria = buildCriteriaFromMap(fieldValues, new Balance());
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
    private ReportQueryByCriteria getBalanceCountQuery(Map fieldValues) {
        Criteria criteria = buildCriteriaFromMap(fieldValues, new Balance());
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
    private Criteria buildCriteriaFromMap(Map fieldValues, Balance balance) {
        Map localFieldValues = new HashMap();
        localFieldValues.putAll(fieldValues);

        Criteria criteria = new Criteria();

        // handle encumbrance balance type
        String propertyName = KFSPropertyConstants.BALANCE_TYPE_CODE;
        if (localFieldValues.containsKey(propertyName)) {
            String propertyValue = (String) localFieldValues.get(propertyName);
            if (KFSConstants.AGGREGATE_ENCUMBRANCE_BALANCE_TYPE_CODE.equals(propertyValue)) {
                localFieldValues.remove(KFSPropertyConstants.BALANCE_TYPE_CODE);

                // the year should be part of the results for both the cash balance and regular balance lookupables
                String universityFiscalYearStr = (String) localFieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
                Integer universityFiscalYear = new Integer(universityFiscalYearStr);

                criteria.addIn(KFSPropertyConstants.BALANCE_TYPE_CODE, balanceTypService.getEncumbranceBalanceTypes(universityFiscalYear));
            }
        }

        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(localFieldValues, new Balance()));
        return criteria;
    }

    /**
     * This method builds the atrribute list used by balance searching
     * 
     * @param isExtended should we add the attributes to sum each of the monthly totals?
     * @return List an attribute list
     */
    private List<String> buildAttributeList(boolean isExtended) {
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
    private List<String> buildGroupByList() {
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
     * Whoa!  This method is seemingly not called in the code base right now, and you know what?  You shouldn't call it
     * First of all, we're not even sending in all the primary keys for Balance, and second of all, we're
     * returning a SufficientFundsBalance, which we cast to a Balance, which is *always* going to throw a 
     * ClassCastException.  Don't call this method.  Just...just step away.
     * 
     * @see org.kuali.module.gl.dao.BalanceDao#getBalanceByPrimaryId(java.lang.Integer, java.lang.String, java.lang.String)
     */
    public Balance getBalanceByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber) {
        // TODO just kill this
        LOG.debug("getBalanceByPrimaryId() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        crit.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);

        QueryByCriteria qbc = QueryFactory.newQuery(SufficientFundBalances.class, crit);
        return (Balance) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }


    /**
     * Since SubAccountNumber, SubObjectCode, and ObjectType are all part of the primary key of Balance, you're guaranteed to get one of those
     * records when you call this method.  Let's hope the right one.
     * 
     * @param universityFiscalYear the fiscal year of the CB balance to return
     * @param chartOfAccountsCode the chart of the accounts code of the CB balanes to return
     * @param accountNumber the account number of the CB balance to return
     * @param objectCode the object code of the CB balance to return
     * @return the CB Balance record
     * @see org.kuali.module.gl.dao.BalanceDao#getCurrentBudgetForObjectCode(java.lang.Integer, java.lang.String, java.lang.String,
     *      java.lang.String)
     */
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
            qbc.addOrderByAscending(GLConstants.BalanceInquiryDrillDowns.OBJECT_LEVEL_CODE);
        }
        else if (KFSConstants.SF_TYPE_CONSOLIDATION.equals(sfCode)) {
            qbc.addOrderByAscending(GLConstants.BalanceInquiryDrillDowns.CONSOLIDATION_OBJECT_CODE);
        }
        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }

    /**
     * Purge the sufficient funds balance table by year/chart
     * 
     * @param chart the chart of balances to purge
     * @param year the university fiscal year of balances to purge
     */
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
     * @param year the university fiscal year to count balances for
     * @return an int with the count of balances for that fiscal year
     * @see org.kuali.module.gl.dao.BalanceDao#countBalancesForFiscalYear(java.lang.Integer)
     */
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
     * @see org.kuali.module.gl.dao.BalanceDao#findNominalActivityBalancesForFiscalYear(java.lang.Integer)
     */
    public Iterator<Balance> findNominalActivityBalancesForFiscalYear(Integer year) {
        LOG.debug("findNominalActivityBalancesForFiscalYear() started");

        Options currentYearOptions = optionsService.getCurrentYearOptions();

        // generate List of nominal activity object type codes
        ObjectTypeService objectTypeService = SpringContext.getBean(ObjectTypeService.class);

        Criteria c = new Criteria();
        c.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
        c.addEqualTo(KFSPropertyConstants.BALANCE_TYPE_CODE, currentYearOptions.getActualFinancialBalanceTypeCd());
        c.addIn(KFSPropertyConstants.OBJECT_TYPE_CODE, objectTypeService.getNominalActivityClosingAllowedObjectTypes(year));
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
     * Returns all of the balances that should be procesed by the BalanceForward year end job under the general rule
     * 
     * @param the university fiscal year to find balances for
     * @return an Iterator of Balances to process
     * @see org.kuali.module.gl.dao.BalanceDao#findCumulativeBalancesToForwardForFiscalYear(java.lang.Integer)
     */
    public Iterator<Balance> findGeneralBalancesToForwardForFiscalYear(Integer year) {
        ObjectTypeService objectTypeService = SpringContext.getBean(ObjectTypeService.class);

        String[] generalBalanceForwardBalanceTypesArray = parameterService.getParameterValues(BalanceForwardStep.class, GLConstants.BalanceForwardRule.BALANCE_TYPES_TO_ROLL_FORWARD_FOR_BALANCE_SHEET).toArray(new String[] {});
        List<String> generalBalanceForwardBalanceTypes = new ArrayList<String>();
        for (String bt : generalBalanceForwardBalanceTypesArray) {
            generalBalanceForwardBalanceTypes.add(bt);
        }

        Criteria c = new Criteria();
        c.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
        c.addIn(KFSPropertyConstants.BALANCE_TYPE_CODE, generalBalanceForwardBalanceTypes);
        c.addIn(KFSPropertyConstants.OBJECT_TYPE_CODE, objectTypeService.getGeneralForwardBalanceObjectTypes(year));

        QueryByCriteria query = QueryFactory.newQuery(Balance.class, c);
        query.addOrderByAscending(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        query.addOrderByAscending(KFSPropertyConstants.ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.OBJECT_CODE);
        query.addOrderByAscending(KFSPropertyConstants.SUB_OBJECT_CODE);
        query.addOrderByAscending(KFSPropertyConstants.BALANCE_TYPE_CODE);
        query.addOrderByAscending(KFSPropertyConstants.OBJECT_TYPE_CODE);

        Iterator<Balance> balances = getPersistenceBrokerTemplate().getIteratorByQuery(query);

        Map<String, FilteringBalanceIterator> balanceIterators = SpringContext.getBeansOfType(FilteringBalanceIterator.class);
        FilteringBalanceIterator filteredBalances = balanceIterators.get("glBalanceTotalNotZeroIterator");
        filteredBalances.setBalancesSource(balances);

        return filteredBalances;
    }

    /**
     * Returns all of the balances that should be procesed by the BalanceForward year end job under the active rule
     * 
     * @param the university fiscal year to find balances for
     * @return an Iterator of Balances to process
     * @see org.kuali.module.gl.dao.BalanceDao#findGeneralBalancesToForwardForFiscalYear(java.lang.Integer)
     */
    public Iterator<Balance> findCumulativeBalancesToForwardForFiscalYear(Integer year) {
        ObjectTypeService objectTypeService = SpringContext.getBean(ObjectTypeService.class);
        SubFundGroupService subFundGroupService = SpringContext.getBean(SubFundGroupService.class);

        final String[] subFundGroupsForCumulativeBalanceForwardingArray = parameterService.getParameterValues(BalanceForwardStep.class, GLConstants.BalanceForwardRule.SUB_FUND_GROUPS_FOR_INCEPTION_TO_DATE_REPORTING).toArray(new String[] {});
        List<String> subFundGroupsForCumulativeBalanceForwarding = new ArrayList<String>();
        for (String subFundGroup : subFundGroupsForCumulativeBalanceForwardingArray) {
            subFundGroupsForCumulativeBalanceForwarding.add(subFundGroup);
        }

        String[] cumulativeBalanceForwardBalanceTypesArray = parameterService.getParameterValues(BalanceForwardStep.class, GLConstants.BalanceForwardRule.BALANCE_TYPES_TO_ROLL_FORWARD_FOR_INCOME_EXPENSE).toArray(new String[] {});
        List<String> cumulativeBalanceForwardBalanceTypes = new ArrayList<String>();
        for (String bt : cumulativeBalanceForwardBalanceTypesArray) {
            cumulativeBalanceForwardBalanceTypes.add(bt);
        }

        Criteria c = new Criteria();
        c.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
        c.addIn(KFSPropertyConstants.BALANCE_TYPE_CODE, cumulativeBalanceForwardBalanceTypes);
        c.addIn(KFSPropertyConstants.OBJECT_TYPE_CODE, objectTypeService.getCumulativeForwardBalanceObjectTypes(year));

        Criteria forCGCrit = new Criteria();
        if (parameterService.getIndicatorParameter(Account.class, KFSConstants.ChartApcParms.ACCOUNT_FUND_GROUP_DENOTES_CG)) {
            forCGCrit.addEqualTo("priorYearAccount.subFundGroup.fundGroupCode", subFundGroupService.getContractsAndGrantsDenotingValue());
        }
        else {
            forCGCrit.addEqualTo("priorYearAccount.subFundGroupCode", subFundGroupService.getContractsAndGrantsDenotingValue());
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

        Map<String, FilteringBalanceIterator> balanceIterators = SpringContext.getBeansOfType(FilteringBalanceIterator.class);
        FilteringBalanceIterator filteredBalances = balanceIterators.get("glBalanceAnnualAndCGTotalNotZeroIterator");
        filteredBalances.setBalancesSource(balances);

        return filteredBalances;
    }

    private static final String PARAMETER_PREFIX = "SELECTION_";

    /**
     * Returns a list of balances to return for the Organization Reversion year end job to process
     * 
     * @param the university fiscal year to find balances for
     * @param endOfYear if true, use currrent year accounts, otherwise use prior year accounts
     * @return an Iterator of Balances to process
     * @see org.kuali.module.gl.dao.BalanceDao#findOrganizationReversionBalancesForFiscalYear(java.lang.Integer, boolean)
     */
    public Iterator<Balance> findOrganizationReversionBalancesForFiscalYear(Integer year, boolean endOfYear) {
        LOG.debug("findOrganizationReversionBalancesForFiscalYear() started");
        Criteria c = new Criteria();
        c.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        Map<Integer, String> parsedRules = new TreeMap<Integer, String>();
        int i = 1;
        boolean moreParams = true;
        while (moreParams) {
            if (parameterService.parameterExists(OrganizationReversion.class, PARAMETER_PREFIX + i)) {
                ParameterEvaluator parameterEvaluator = parameterService.getParameterEvaluator(OrganizationReversion.class, PARAMETER_PREFIX + i);
                String currentRule = parameterEvaluator.getValue();
                if (!endOfYear) {
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
            else {
                moreParams = false;
            }
            i++;
        }
        // we only ever calculate on CB, AC, and encumbrance types, so let's only select those
        Options options = SpringContext.getBean(OptionsService.class).getOptions(year);
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

    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setBalanceTypService(BalanceTypService balanceTypService) {
        this.balanceTypService = balanceTypService;
    }
}
