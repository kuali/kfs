/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.dataaccess.impl;

import static org.kuali.kfs.module.ld.LaborPropertyConstants.AccountingPeriodProperties.APRIL;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.AccountingPeriodProperties.AUGUST;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.AccountingPeriodProperties.DECEMBER;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.AccountingPeriodProperties.FEBRUARY;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.AccountingPeriodProperties.JANUARY;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.AccountingPeriodProperties.JULY;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.AccountingPeriodProperties.JUNE;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.AccountingPeriodProperties.MARCH;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.AccountingPeriodProperties.MAY;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.AccountingPeriodProperties.NOVEMBER;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.AccountingPeriodProperties.OCTOBER;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.AccountingPeriodProperties.SEPTEMBER;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.AccountingPeriodProperties.YEAR_END;
import static org.kuali.kfs.module.ld.util.ConsolidationUtil.buildConsolidatedQuery;
import static org.kuali.kfs.module.ld.util.ConsolidationUtil.buildGroupByCollection;
import static org.kuali.kfs.module.ld.util.ConsolidationUtil.sum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.gl.dataaccess.LedgerBalanceBalancingDao;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.businessobject.EmployeeFunding;
import org.kuali.kfs.module.ld.businessobject.LaborBalanceSummary;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.businessobject.LedgerBalanceForYearEndBalanceForward;
import org.kuali.kfs.module.ld.dataaccess.LaborLedgerBalanceDao;
import org.kuali.kfs.module.ld.util.ConsolidationUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * This is the data access object for ledger balance.
 * 
 * @see org.kuali.kfs.module.ld.businessobject.LedgerBalance
 */
public class LaborLedgerBalanceDaoOjb extends PlatformAwareDaoBaseOjb implements LaborLedgerBalanceDao, LedgerBalanceBalancingDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborLedgerBalanceDaoOjb.class);

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborLedgerBalanceDao#findBalancesForFiscalYear(java.lang.Integer)
     */
    public Iterator<LedgerBalance> findBalancesForFiscalYear(Integer year) {
        LOG.debug("findBalancesForFiscalYear() started");

        Criteria c = new Criteria();
        c.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);

        QueryByCriteria query = QueryFactory.newQuery(LedgerBalance.class, c);
        query.addOrderByAscending(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        query.addOrderByAscending(KFSPropertyConstants.ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        query.addOrderByAscending(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        query.addOrderByAscending(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        query.addOrderByAscending(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);

        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborLedgerBalanceDao#findBalancesForFiscalYear(Integer, Map, List, List)
     */
    public Iterator<LedgerBalance> findBalancesForFiscalYear(Integer fiscalYear, Map<String, String> fieldValues, List<String> encumbranceBalanceTypes) {

        Criteria criteria = buildCriteriaFromMap(fieldValues, new LedgerBalance(), encumbranceBalanceTypes, false);
        criteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);

        QueryByCriteria query = QueryFactory.newQuery(LedgerBalance.class, criteria);

        query.addOrderByAscending(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        query.addOrderByAscending(KFSPropertyConstants.ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        query.addOrderByAscending(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        query.addOrderByAscending(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        query.addOrderByAscending(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);

        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborLedgerBalanceDao#findBalance(java.util.Map, boolean)
     */
    public Iterator<LedgerBalance> findBalance(Map fieldValues, boolean isConsolidated, List<String> encumbranceBalanceTypes, boolean noZeroAmounts) {
        LOG.debug("findBalance() started");

        Query query = this.getBalanceQuery(fieldValues, isConsolidated, encumbranceBalanceTypes, noZeroAmounts);
        OJBUtility.limitResultSize(query);

        if (isConsolidated) {
            return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        }
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborLedgerBalanceDao#getConsolidatedBalanceRecordCount(java.util.Map)
     */
    public Iterator getConsolidatedBalanceRecordCount(Map fieldValues, List<String> encumbranceBalanceTypes, boolean noZeroAmounts) {
        LOG.debug("getBalanceRecordCount() started");

        ReportQueryByCriteria query = this.getBalanceCountQuery(fieldValues, encumbranceBalanceTypes, noZeroAmounts);
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    // build the query for balance search
    protected Query getBalanceQuery(Map fieldValues, boolean isConsolidated, List<String> encumbarnceBalanceTypes, boolean noZeroAmounts) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Building criteria from map fields: " + fieldValues.keySet());
            LOG.debug("getBalanceQuery(Map, boolean) started");
        }

        Criteria criteria = buildCriteriaFromMap(fieldValues, new LedgerBalance(), encumbarnceBalanceTypes, noZeroAmounts);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(LedgerBalance.class, criteria);

        // if consolidated, then ignore subaccount number and balance type code
        if (isConsolidated) {
            buildConsolidatedQuery(query, sum(JULY.propertyName), sum(AUGUST.propertyName), sum(SEPTEMBER.propertyName), sum(OCTOBER.propertyName), sum(NOVEMBER.propertyName), sum(DECEMBER.propertyName), sum(JANUARY.propertyName), sum(FEBRUARY.propertyName), sum(MARCH.propertyName), sum(APRIL.propertyName), sum(MAY.propertyName), sum(JUNE.propertyName), sum(YEAR_END.propertyName));
        }

        return query;
    }

    // build the query for balance search
    protected ReportQueryByCriteria getBalanceCountQuery(Map fieldValues, List<String> encumbranceBalanceTypes, boolean noZeroAmounts) {
        Criteria criteria = buildCriteriaFromMap(fieldValues, new LedgerBalance(), encumbranceBalanceTypes, noZeroAmounts);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(LedgerBalance.class, criteria);

        // set the selection attributes
        query.setAttributes(new String[] { "count(*)" });

        Collection<String> groupByList = buildGroupByCollection();
        groupByList.remove(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        groupByList.remove(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        groupByList.remove(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);

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
     * @param encumbranceBalanceTypes
     * @param noZeroAmounts makes sure at least one of the 13 monthly buckets has an amount not equals to zero
     * @return a query criteria
     */
    protected Criteria buildCriteriaFromMap(Map fieldValues, LedgerBalance balance, List<String> encumbranceBalanceTypes, boolean noZeroAmounts) {
        Map localFieldValues = new HashMap();
        localFieldValues.putAll(fieldValues);

        Criteria criteria = new Criteria();

        // handle encumbrance balance type
        String propertyName = KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE;
        if (localFieldValues.containsKey(propertyName)) {
            String propertyValue = (String) localFieldValues.get(propertyName);
            if (KFSConstants.AGGREGATE_ENCUMBRANCE_BALANCE_TYPE_CODE.equals(propertyValue)) {
                localFieldValues.remove(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);

                criteria.addIn(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, encumbranceBalanceTypes);
            }
        }

        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(localFieldValues, new LedgerBalance()));
        if (noZeroAmounts) {
            Criteria noZeroAmountsCriteria = new Criteria();
            noZeroAmountsCriteria.addEqualTo(KFSPropertyConstants.MONTH1_AMOUNT, 0);
            noZeroAmountsCriteria.addEqualTo(KFSPropertyConstants.MONTH2_AMOUNT, 0);
            noZeroAmountsCriteria.addEqualTo(KFSPropertyConstants.MONTH3_AMOUNT, 0);
            noZeroAmountsCriteria.addEqualTo(KFSPropertyConstants.MONTH4_AMOUNT, 0);
            noZeroAmountsCriteria.addEqualTo(KFSPropertyConstants.MONTH5_AMOUNT, 0);
            noZeroAmountsCriteria.addEqualTo(KFSPropertyConstants.MONTH6_AMOUNT, 0);
            noZeroAmountsCriteria.addEqualTo(KFSPropertyConstants.MONTH7_AMOUNT, 0);
            noZeroAmountsCriteria.addEqualTo(KFSPropertyConstants.MONTH8_AMOUNT, 0);
            noZeroAmountsCriteria.addEqualTo(KFSPropertyConstants.MONTH9_AMOUNT, 0);
            noZeroAmountsCriteria.addEqualTo(KFSPropertyConstants.MONTH10_AMOUNT, 0);
            noZeroAmountsCriteria.addEqualTo(KFSPropertyConstants.MONTH11_AMOUNT, 0);
            noZeroAmountsCriteria.addEqualTo(KFSPropertyConstants.MONTH12_AMOUNT, 0);
            noZeroAmountsCriteria.addEqualTo(KFSPropertyConstants.MONTH13_AMOUNT, 0);
            noZeroAmountsCriteria.setNegative(true);
            criteria.addAndCriteria(noZeroAmountsCriteria);
        }
        return criteria;
    }

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborLedgerBalanceDao#findCurrentFunds(java.util.Map)
     */
    public List<LedgerBalance> findCurrentFunds(Map fieldValues) {
        LOG.debug("Start findCurrentFunds()");

        Iterator<Object[]> queryResults = this.findCurrentFundsRawData(fieldValues);
        List<LedgerBalance> currentFundsCollection = new ArrayList<LedgerBalance>();
        while (queryResults != null && queryResults.hasNext()) {
            currentFundsCollection.add(this.marshalFundsAsLedgerBalance(queryResults.next()));
        }
        return currentFundsCollection;
    }

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborLedgerBalanceDao#findEncumbranceFunds(java.util.Map)
     */
    public List<LedgerBalance> findEncumbranceFunds(Map fieldValues) {
        LOG.debug("Start findEncumbranceFunds()");

        Iterator<Object[]> queryResults = this.findEncumbranceFundsRawData(fieldValues);
        List<LedgerBalance> currentFundsCollection = new ArrayList<LedgerBalance>();
        while (queryResults != null && queryResults.hasNext()) {
            currentFundsCollection.add(this.marshalFundsAsLedgerBalance(queryResults.next()));
        }
        return currentFundsCollection;
    }

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborLedgerBalanceDao#findCurrentEmployeeFunds(java.util.Map)
     */
    public List<EmployeeFunding> findCurrentEmployeeFunds(Map fieldValues) {
        LOG.debug("Start findCurrentEmployeeFunds()");

        Iterator<Object[]> queryResults = this.findCurrentFundsRawData(fieldValues);
        List<EmployeeFunding> currentFundsCollection = new ArrayList<EmployeeFunding>();
        while (queryResults != null && queryResults.hasNext()) {
            currentFundsCollection.add(this.marshalFundsAsEmployeeFunding(queryResults.next()));
        }
        return currentFundsCollection;
    }

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborLedgerBalanceDao#findEncumbranceEmployeeFunds(java.util.Map)
     */
    public List<EmployeeFunding> findEncumbranceEmployeeFunds(Map fieldValues) {
        LOG.debug("Start findCurrentEmployeeFunds()");

        Iterator<Object[]> queryResults = this.findEncumbranceFundsRawData(fieldValues);
        List<EmployeeFunding> currentFundsCollection = new ArrayList<EmployeeFunding>();
        while (queryResults != null && queryResults.hasNext()) {
            currentFundsCollection.add(this.marshalFundsAsEmployeeFunding(queryResults.next()));
        }
        return currentFundsCollection;
    }

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborLedgerBalanceDao#findBalanceSummary(java.lang.Integer, java.util.Collection)
     */
    public List<LaborBalanceSummary> findBalanceSummary(Integer fiscalYear, Collection<String> balanceTypes) {
        LOG.debug("Start findBalanceSummary()");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        criteria.addIn(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, balanceTypes);

        Iterator<Object[]> queryResults = this.findBalanceSummaryRawData(criteria);
        List<LaborBalanceSummary> balanceSummaryCollection = new ArrayList<LaborBalanceSummary>();
        while (queryResults != null && queryResults.hasNext()) {
            balanceSummaryCollection.add(this.marshalFundsAsLaborBalanceSummary(queryResults.next()));
        }
        return balanceSummaryCollection;
    }

    // get the current funds according to the given criteria
    protected Iterator<Object[]> findCurrentFundsRawData(Map fieldValues) {
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new LedgerBalance());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_ACTUAL);

        List<String> objectTypeCodes = new ArrayList<String>();
        objectTypeCodes.add(LaborConstants.BalanceInquiries.EMPLOYEE_FUNDING_EXPENSE_OBJECT_TYPE_CODE);
        objectTypeCodes.add(LaborConstants.BalanceInquiries.EMPLOYEE_FUNDING_NORMAL_OP_EXPENSE_OBJECT_TYPE_CODE);
        criteria.addIn(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, objectTypeCodes);

        return this.findFundsRawData(criteria);
    }

    // get the encumbrance funds according to the given criteria
    protected Iterator<Object[]> findEncumbranceFundsRawData(Map fieldValues) {
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new LedgerBalance());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_INTERNAL_ENCUMBRANCE);

        return this.findFundsRawData(criteria);
    }

    // get the funds based on the given criteria
    protected Iterator<Object[]> findFundsRawData(Criteria criteria) {
        ReportQueryByCriteria query = QueryFactory.newReportQuery(LedgerBalance.class, criteria);

        List<String> groupByList = this.getGroupByListForFundingInquiry();
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);

        List<String> getAttributeList = getAttributeListForFundingInquiry(false);
        String[] attributes = (String[]) getAttributeList.toArray(new String[getAttributeList.size()]);
        query.setAttributes(attributes);

        OJBUtility.limitResultSize(query);
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    // get the balance summary based on the given criteria
    protected Iterator<Object[]> findBalanceSummaryRawData(Criteria criteria) {
        ReportQueryByCriteria query = QueryFactory.newReportQuery(LedgerBalance.class, criteria);

        List<String> groupByList = this.getGroupByListForBalanceSummary();
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);

        List<String> getAttributeList = this.getAttributeListForBalanceSummary(false);
        String[] attributes = (String[]) getAttributeList.toArray(new String[getAttributeList.size()]);
        query.setAttributes(attributes);

        query.addOrderByAscending(groupByList.get(0));
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    // marshal into AccountStatusBaseFunds from the query result
    protected LedgerBalance marshalFundsAsLedgerBalance(Object[] queryResult) {
        LedgerBalance ledgerBalance = new LedgerBalance();
        List<String> keyFields = this.getAttributeListForFundingInquiry(true);

        ObjectUtil.buildObject(ledgerBalance, queryResult, keyFields);
        return ledgerBalance;
    }

    // marshal into AccountStatusBaseFunds from the query result
    protected EmployeeFunding marshalFundsAsEmployeeFunding(Object[] queryResult) {
        EmployeeFunding employeeFunding = new EmployeeFunding();
        List<String> keyFields = this.getAttributeListForFundingInquiry(true);

        ObjectUtil.buildObject(employeeFunding, queryResult, keyFields);
        return employeeFunding;
    }

    // marshal into LaborBalanceSummary from the query result
    protected LaborBalanceSummary marshalFundsAsLaborBalanceSummary(Object[] queryResult) {
        return new LaborBalanceSummary(queryResult);
    }

    // define the attribute list that can be used to group the search results
    protected List<String> getGroupByListForFundingInquiry() {
        List<String> groupByList = new ArrayList<String>();
        groupByList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        groupByList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        groupByList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        groupByList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        groupByList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        groupByList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        groupByList.add(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
        groupByList.add(KFSPropertyConstants.POSITION_NUMBER);
        groupByList.add(KFSPropertyConstants.EMPLID);
        return groupByList;
    }

    // define the return attribute list for funding query
    protected List<String> getAttributeListForFundingInquiry(boolean isAttributeNameNeeded) {
        List<String> attributeList = getGroupByListForFundingInquiry();
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.ACCOUNTING_LINE_ANNUAL_BALANCE_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.FINANCIAL_BEGINNING_BALANCE_LINE_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.CONTRACTS_GRANTS_BEGINNING_BALANCE_AMOUNT, isAttributeNameNeeded));
        return attributeList;
    }

    // define the attribute list that can be used to group the search results
    protected List<String> getGroupByListForBalanceSummary() {
        List<String> groupByList = new ArrayList<String>();
        groupByList.add("account.subFundGroup.fundGroupCode");
        return groupByList;
    }

    // define the return attribute list for balance summary
    protected List<String> getAttributeListForBalanceSummary(boolean isAttributeNameNeeded) {
        List<String> attributeList = getGroupByListForBalanceSummary();
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.ACCOUNTING_LINE_ANNUAL_BALANCE_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.FINANCIAL_BEGINNING_BALANCE_LINE_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.CONTRACTS_GRANTS_BEGINNING_BALANCE_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH1_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH2_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH3_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH4_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH5_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH6_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH7_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH8_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH9_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH10_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH11_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH12_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH13_AMOUNT, isAttributeNameNeeded));
        return attributeList;
    }

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborLedgerBalanceDao#findBalancesForFiscalYear(java.lang.Integer, java.util.Map,
     *      java.util.List, java.util.List)
     */
    public Iterator<LedgerBalanceForYearEndBalanceForward> findBalancesForFiscalYear(Integer fiscalYear, Map<String, String> fieldValues, List<String> subFundGroupCodes, List<String> fundGroupCodes) {
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new LedgerBalanceForYearEndBalanceForward());
        criteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);

        String chartAccountsCode = fieldValues.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        String accountNumber = fieldValues.get(KFSPropertyConstants.ACCOUNT_NUMBER);

        // add subfund criteria if the account is not provided
        if (StringUtils.isEmpty(chartAccountsCode) || StringUtils.isEmpty(accountNumber)) {
            if (subFundGroupCodes != null && !subFundGroupCodes.isEmpty()) {
                Criteria criteriaForSubFundGroup = new Criteria();
                String subFundGroupFieldName = KFSPropertyConstants.ACCOUNT + "." + KFSPropertyConstants.SUB_FUND_GROUP_CODE;
                criteriaForSubFundGroup.addIn(subFundGroupFieldName, subFundGroupCodes);

                if (fundGroupCodes != null && !fundGroupCodes.isEmpty()) {

                    Criteria criteriaForFundGroup = new Criteria();
                    String fundGroupFieldName = KFSPropertyConstants.ACCOUNT + "." + KFSPropertyConstants.SUB_FUND_GROUP + "." + KFSPropertyConstants.FUND_GROUP_CODE;
                    criteriaForFundGroup.addIn(fundGroupFieldName, fundGroupCodes);

                    criteriaForSubFundGroup.addOrCriteria(criteriaForFundGroup);
                }

                criteria.addAndCriteria(criteriaForSubFundGroup);
            }
        }

        QueryByCriteria query = QueryFactory.newQuery(LedgerBalanceForYearEndBalanceForward.class, criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborLedgerBalanceDao#findAccountsInFundGroups(java.lang.Integer, java.util.Map,
     *      java.util.List, java.util.List)
     */
    public List<List<String>> findAccountsInFundGroups(Integer fiscalYear, Map<String, String> fieldValues, List<String> subFundGroupCodes, List<String> fundGroupCodes) {
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new LedgerBalanceForYearEndBalanceForward());
        criteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);

        if (subFundGroupCodes != null && !subFundGroupCodes.isEmpty()) {
            Criteria criteriaForSubFundGroup = new Criteria();
            String subFundGroupFieldName = KFSPropertyConstants.ACCOUNT + "." + KFSPropertyConstants.SUB_FUND_GROUP_CODE;
            criteriaForSubFundGroup.addIn(subFundGroupFieldName, subFundGroupCodes);

            if (fundGroupCodes != null && !fundGroupCodes.isEmpty()) {

                Criteria criteriaForFundGroup = new Criteria();
                String fundGroupFieldName = KFSPropertyConstants.ACCOUNT + "." + KFSPropertyConstants.SUB_FUND_GROUP + "." + KFSPropertyConstants.FUND_GROUP_CODE;
                criteriaForFundGroup.addIn(fundGroupFieldName, fundGroupCodes);

                criteriaForSubFundGroup.addOrCriteria(criteriaForFundGroup);
            }
            criteria.addAndCriteria(criteriaForSubFundGroup);
        }

        ReportQueryByCriteria query = QueryFactory.newReportQuery(LedgerBalanceForYearEndBalanceForward.class, criteria);

        query.setAttributes(LaborConstants.ACCOUNT_FIELDS);
        query.setDistinct(true);

        Iterator<Object[]> accountIterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);

        List<List<String>> accounts = new ArrayList<List<String>>();
        while (accountIterator != null && accountIterator.hasNext()) {
            Object[] accountObject = accountIterator.next();

            List<String> account = new ArrayList<String>();
            account.add(accountObject[0].toString());
            account.add(accountObject[1].toString());

            accounts.add(account);
        }
        return accounts;
    }

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborLedgerBalanceDao#findLedgerBalances(java.util.Map, java.util.Map, java.util.Set,
     *      java.util.List, java.util.List)
     */
    public Collection<LedgerBalance> findLedgerBalances(Map<String, List<String>> fieldValues, Map<String, List<String>> excludedFieldValues, Set<Integer> fiscalYears, List<String> balanceTypeList, List<String> positionObjectGroupCodes) {
        Criteria criteria = new Criteria();

        for (String fieldName : fieldValues.keySet()) {
            Criteria criteriaForIncludedFields = new Criteria();
            criteria.addIn(fieldName, fieldValues.get(fieldName));
            criteria.addAndCriteria(criteriaForIncludedFields);
        }

        for (String fieldName : excludedFieldValues.keySet()) {
            Criteria criteriaForExcludedFields = new Criteria();
            criteria.addNotIn(fieldName, excludedFieldValues.get(fieldName));
            criteria.addAndCriteria(criteriaForExcludedFields);
        }

        if (fiscalYears != null && !fiscalYears.isEmpty()) {
            Criteria criteriaForFiscalyear = new Criteria();
            criteriaForFiscalyear.addIn(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYears);
            criteria.addAndCriteria(criteriaForFiscalyear);
        }

        if (balanceTypeList != null && !balanceTypeList.isEmpty()) {
            Criteria criteriaForBalanceTypes = new Criteria();
            criteriaForBalanceTypes.addIn(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, balanceTypeList);
            criteria.addAndCriteria(criteriaForBalanceTypes);
        }

        if (positionObjectGroupCodes != null && !positionObjectGroupCodes.isEmpty()) {
            Criteria criteriaForLaborObjects = new Criteria();
            criteriaForLaborObjects.addIn(KFSPropertyConstants.LABOR_OBJECT + "." + KFSPropertyConstants.POSITION_OBJECT_GROUP_CODE, positionObjectGroupCodes);
            criteria.addAndCriteria(criteriaForLaborObjects);
        }

        QueryByCriteria query = QueryFactory.newQuery(LedgerBalance.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborLedgerBalanceDao#deleteLedgerBalancesPriorToYear(java.lang.Integer,
     *      java.lang.String)
     */
    public void deleteLedgerBalancesPriorToYear(Integer fiscalYear, String chartOfAccountsCode) {
        LOG.debug("deleteLedgerBalancesPriorToYear() started");

        Criteria criteria = new Criteria();
        criteria.addLessThan(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);

        QueryByCriteria query = new QueryByCriteria(LedgerBalance.class, criteria);
        getPersistenceBrokerTemplate().deleteByQuery(query);
    }

    /**
     * @see org.kuali.kfs.gl.dataaccess.BalancingDao#findCountGreaterOrEqualThan(java.lang.Integer)
     */
    public Integer findCountGreaterOrEqualThan(Integer year) {
        Criteria criteria = new Criteria();
        criteria.addGreaterOrEqualThan(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(LedgerBalance.class, criteria);

        return getPersistenceBrokerTemplate().getCount(query);
    }
}
