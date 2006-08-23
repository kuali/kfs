/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.dao.ojb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.Constants;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.SufficientFundBalances;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.BalanceDao;
import org.kuali.module.gl.util.OJBUtility;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * @author jsissom
 * @author Laran Evans <lc278@cornell.edu>
 * @version $Id: BalanceDaoOjb.java,v 1.37 2006-08-23 20:22:52 larevans Exp $
 */
public class BalanceDaoOjb extends PersistenceBrokerDaoSupport implements BalanceDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceDaoOjb.class);

    public BalanceDaoOjb() {
        super();
    }

    /**
     * @see org.kuali.module.gl.dao.BalanceDao#getGlSummary(int, java.util.List)
     */
    public Iterator getGlSummary(int universityFiscalYear, List<String> balanceTypeCodes) {
        LOG.debug("getGlSummary() started");

        Criteria c = new Criteria();
        c.addEqualTo("universityFiscalYear", universityFiscalYear);
        c.addIn("balanceTypeCode", balanceTypeCodes);

        String[] attributes = new String[] { "account.subFundGroup.fundGroupCode", "sum(accountLineAnnualBalanceAmount)", "sum(beginningBalanceLineAmount)", "sum(contractsGrantsBeginningBalanceAmount)", "sum(month1Amount)", "sum(month2Amount)", "sum(month3Amount)", "sum(month4Amount)", "sum(month5Amount)", "sum(month6Amount)", "sum(month7Amount)", "sum(month8Amount)", "sum(month9Amount)", "sum(month10Amount)", "sum(month11Amount)", "sum(month12Amount)", "sum(month13Amount)" };

        String[] groupby = new String[] { "account.subFundGroup.fundGroupCode" };

        ReportQueryByCriteria query = new ReportQueryByCriteria(Balance.class, c);

        query.setAttributes(attributes);
        query.addGroupBy(groupby);
        query.addOrderByAscending("account.subFundGroup.fundGroupCode");

        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.dao.BalanceDao#findBalancesForFiscalYear(java.lang.Integer)
     */
    public Iterator<Balance> findBalancesForFiscalYear(Integer year) {
        LOG.debug("findBalancesForFiscalYear() started");

        // from gleacbfb (balance forward) cobol program

        // 744 002750 DECLARE GLBL_CURSOR CURSOR FOR
        // 745 002760 SELECT UNIV_FISCAL_YR,
        // 746 002770 FIN_COA_CD,
        // 747 002780 ACCOUNT_NBR,
        // 748 002790 SUB_ACCT_NBR,
        // 749 002800 FIN_OBJECT_CD,
        // 750 002810 FIN_SUB_OBJ_CD,
        // 751 002820 FIN_BALANCE_TYP_CD,
        // 752 002830 FIN_OBJ_TYP_CD,
        // 753 002840 ACLN_ANNL_BAL_AMT,
        // 754 002850 FIN_BEG_BAL_LN_AMT,
        // 755 002860 CONTR_GR_BB_AC_AMT,
        // 756 002870 MO1_ACCT_LN_AMT,
        // 757 002880 MO2_ACCT_LN_AMT,
        // 758 002890 MO3_ACCT_LN_AMT,
        // 759 002900 MO4_ACCT_LN_AMT,
        // 760 002910 MO5_ACCT_LN_AMT,
        // 761 002920 MO6_ACCT_LN_AMT,
        // 762 002930 MO7_ACCT_LN_AMT,
        // 763 002940 MO8_ACCT_LN_AMT,
        // 764 002950 MO9_ACCT_LN_AMT,
        // 765 002960 MO10_ACCT_LN_AMT,
        // 766 002970 MO11_ACCT_LN_AMT,
        // 767 002980 MO12_ACCT_LN_AMT,
        // 768 002990 MO13_ACCT_LN_AMT
        // 769 003000 FROM GL_BALANCE_T
        // 770 003010 WHERE UNIV_FISCAL_YR = RTRIM(:GLGLBL-UNIV-FISCAL-YR)
        // 771 003020 ORDER BY FIN_COA_CD,
        // 772 003030 ACCOUNT_NBR,
        // 773 003040 SUB_ACCT_NBR,
        // 774 003050 FIN_OBJECT_CD,
        // 775 003060 FIN_SUB_OBJ_CD,
        // 776 003070 FIN_BALANCE_TYP_CD,
        // 777 003080 FIN_OBJ_TYP_CD
        // 778 003090 END-EXEC.

        Criteria c = new Criteria();
        c.addEqualTo("universityFiscalYear", year);

        QueryByCriteria query = QueryFactory.newQuery(Balance.class, c);
        query.addOrderByAscending("chartOfAccountsCode");
        query.addOrderByAscending("accountNumber");
        query.addOrderByAscending("subAccountNumber");
        query.addOrderByAscending("objectCode");
        query.addOrderByAscending("subObjectCode");
        query.addOrderByAscending("balanceTypeCode");
        query.addOrderByAscending("objectTypeCode");

        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.dao.BalanceDao#save(org.kuali.module.gl.bo.Balance)
     */
    public void save(Balance b) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(b);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.dao.BalanceDao#getBalanceByTransaction(org.kuali.module.gl.bo.Transaction)
     */
    public Balance getBalanceByTransaction(Transaction t) {
        LOG.debug("getBalanceByTransaction() started");

        Criteria crit = new Criteria();
        crit.addEqualTo("universityFiscalYear", t.getUniversityFiscalYear());
        crit.addEqualTo("chartOfAccountsCode", t.getChartOfAccountsCode());
        crit.addEqualTo("accountNumber", t.getAccountNumber());
        crit.addEqualTo("subAccountNumber", t.getSubAccountNumber());
        crit.addEqualTo("objectCode", t.getFinancialObjectCode());
        crit.addEqualTo("subObjectCode", t.getFinancialSubObjectCode());
        crit.addEqualTo("balanceTypeCode", t.getFinancialBalanceTypeCode());
        crit.addEqualTo("objectTypeCode", t.getFinancialObjectTypeCode());

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

    public Iterator<Balance> findBalances(Account account, Integer fiscalYear, Collection includedObjectCodes, Collection excludedObjectCodes, Collection objectTypeCodes, Collection balanceTypeCodes) {
        LOG.debug("findBalances() started");

        Criteria criteria = new Criteria();

        criteria.addEqualTo("accountNumber", account.getAccountNumber());
        criteria.addEqualTo("chartOfAccountsCode", account.getChartOfAccountsCode());

        criteria.addEqualTo("universityFiscalYear", fiscalYear);

        criteriaBuilder(criteria, "FIN_OBJ_TYP_CD", objectTypeCodes);
        criteriaBuilder(criteria, "FIN_BALANCE_TYP_CD", balanceTypeCodes);
        criteriaBuilder(criteria, "FIN_OBJECT_CD", includedObjectCodes);
        negatedCriteriaBuilder(criteria, "FIN_OBJECT_CD", excludedObjectCodes);

        ReportQueryByCriteria query = new ReportQueryByCriteria(Balance.class, criteria);

        // returns an iterator of all matching balances
        Iterator balances = getPersistenceBrokerTemplate().getIteratorByQuery(query);
        return balances;
    }

    /**
     * @see org.kuali.module.gl.dao.BalanceDao#findCashBalance(java.util.Map, boolean)
     */
    public Iterator<Balance> findCashBalance(Map fieldValues, boolean isConsolidated) {
        LOG.debug("findCashBalance() started");

        Query query = this.getCashBalanceQuery(fieldValues, isConsolidated);
        OJBUtility.limitResultSize(query);       
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }
    
    /**
     * @see org.kuali.module.gl.dao.BalanceDao#getCashBalanceRecordCount(java.util.Map, boolean)
     */
    public Integer getDetailedCashBalanceRecordCount(Map fieldValues) {
        LOG.debug("getDetailedCashBalanceRecordCount() started");

        Query query = this.getCashBalanceQuery(fieldValues, false);
        return getPersistenceBrokerTemplate().getCount(query);
    }

    /**
     * @see org.kuali.module.gl.dao.BalanceDao#getCashBalanceRecordSize(java.util.Map, boolean)
     */
    public Iterator getConsolidatedCashBalanceRecordCount(Map fieldValues) {
        LOG.debug("getCashBalanceRecordCount() started");

        ReportQueryByCriteria query = this.getCashBalanceCountQuery(fieldValues);
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    /**
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
     * @see org.kuali.module.gl.dao.BalanceDao#getConsolidatedBalanceRecordCount(java.util.Map)
     */
    public Iterator getConsolidatedBalanceRecordCount(Map fieldValues) {
        LOG.debug("getBalanceRecordCount() started");

        ReportQueryByCriteria query = this.getBalanceCountQuery(fieldValues);
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    private ReportQueryByCriteria getCashBalanceCountQuery(Map fieldValues) {
        Criteria criteria = buildCriteriaFromMap(fieldValues, new Balance(), false);
        criteria.addEqualTo("balanceTypeCode", "AC");
        criteria.addEqualToField("chart.financialCashObjectCode", "objectCode");

        ReportQueryByCriteria query = QueryFactory.newReportQuery(Balance.class, criteria);

        List groupByList = buildGroupByList();
        groupByList.remove("subAccountNumber");
        groupByList.remove("subObjectCode");
        groupByList.remove("objectTypeCode");

        // add the group criteria into the selection statement
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);

        // set the selection attributes
        query.setAttributes(new String[] { "count(*)" });

        return query;
    }

    // build the query for cash balance search
    private Query getCashBalanceQuery(Map fieldValues, boolean isConsolidated) {
        Criteria criteria = buildCriteriaFromMap(fieldValues, new Balance(), false);
        criteria.addEqualTo("balanceTypeCode", "AC");
        criteria.addEqualToField("chart.financialCashObjectCode", "objectCode");

        ReportQueryByCriteria query = QueryFactory.newReportQuery(Balance.class, criteria);
        List attributeList = buildAttributeList(false);
        List groupByList = buildGroupByList();

        // if consolidated, then ignore the following fields
        if (isConsolidated) {
            attributeList.remove("subAccountNumber");
            groupByList.remove("subAccountNumber");
            attributeList.remove("subObjectCode");
            groupByList.remove("subObjectCode");
            attributeList.remove("objectTypeCode");
            groupByList.remove("objectTypeCode");
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

        Criteria criteria = buildCriteriaFromMap(fieldValues, new Balance(), false);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(Balance.class, criteria);

        // if consolidated, then ignore subaccount number and balance type code
        if (isConsolidated) {
            List attributeList = buildAttributeList(true);
            List groupByList = buildGroupByList();

            // ignore subaccount number, sub object code and object type code
            attributeList.remove("subAccountNumber");
            groupByList.remove("subAccountNumber");
            attributeList.remove("subObjectCode");
            groupByList.remove("subObjectCode");
            attributeList.remove("objectTypeCode");
            groupByList.remove("objectTypeCode");

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
        Criteria criteria = buildCriteriaFromMap(fieldValues, new Balance(), false);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(Balance.class, criteria);

        // set the selection attributes
        query.setAttributes(new String[] { "count(*)" });
        
        List groupByList = buildGroupByList();
        groupByList.remove("subAccountNumber");
        groupByList.remove("subObjectCode");
        groupByList.remove("objectTypeCode");

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
    private Criteria buildCriteriaFromMap(Map fieldValues, Balance balance, boolean isBalanceTypeHandled) {
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new Balance());

        Iterator propsIter = fieldValues.keySet().iterator();
        while (propsIter.hasNext()) {
            String propertyName = (String) propsIter.next();
            String propertyValue = (String) fieldValues.get(propertyName);

            // if searchValue is empty and the key is not a valid property ignore
            if (StringUtils.isBlank(propertyValue) || !(PropertyUtils.isWriteable(balance, propertyName))) {
                continue;
            }

            // with this option, the method becomes specific and bad
            if (isBalanceTypeHandled && propertyValue.equals("EN") && propertyName.equals("balanceTypeCode")) {
                List balanceTypeCodeList = buildBalanceTypeCodeList();
                criteria.addIn("balanceTypeCode", balanceTypeCodeList);
            }
        }
        return criteria;
    }

    /**
     * This method builds an balance type code list
     * 
     * @return List a balance type code list
     */
    private List<String> buildBalanceTypeCodeList() {
        List balanceTypeCodeList = new ArrayList();

        balanceTypeCodeList.add("EX");
        balanceTypeCodeList.add("IE");
        balanceTypeCodeList.add("PE");
        balanceTypeCodeList.add("CE");

        return balanceTypeCodeList;
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

        attributeList.add("universityFiscalYear");
        attributeList.add("chartOfAccountsCode");
        attributeList.add("accountNumber");
        attributeList.add("subAccountNumber");
        attributeList.add("balanceTypeCode");
        attributeList.add("objectCode");
        attributeList.add("subObjectCode");
        attributeList.add("objectTypeCode");

        return attributeList;
    }

    public Balance getBalanceByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber) {
        LOG.debug("getBalanceByPrimaryId() started");

        Criteria crit = new Criteria();
        crit.addEqualTo("universityFiscalYear", universityFiscalYear);
        crit.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        crit.addEqualTo("accountNumber", accountNumber);

        QueryByCriteria qbc = QueryFactory.newQuery(SufficientFundBalances.class, crit);
        return (Balance) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }


    /**
     * @see org.kuali.module.gl.dao.BalanceDao#getCurrentBudgetForObjectCode(java.lang.Integer, java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public Balance getCurrentBudgetForObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String objectCode) {
        LOG.debug("getCurrentBudgetForObjectCode() started");

        Criteria crit = new Criteria();
        crit.addEqualTo("universityFiscalYear", universityFiscalYear);
        crit.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        crit.addEqualTo("accountNumber", accountNumber);
        crit.addEqualTo("objectCode", objectCode);
        crit.addEqualTo("balanceTypeCode", Constants.BALANCE_TYPE_CURRENT_BUDGET);

        QueryByCriteria qbc = QueryFactory.newQuery(Balance.class, crit);
        return (Balance) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    /**
     * Find all matching account balances.
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @return balances sorted by object code
     */
    public Iterator<Balance> findAccountBalances(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber) {
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
    public Iterator<Balance> findAccountBalances(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String sfCode) {
        LOG.debug("findAccountBalances() started");

        Criteria crit = new Criteria();
        crit.addEqualTo("universityFiscalYear", universityFiscalYear);
        crit.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        crit.addEqualTo("accountNumber", accountNumber);

        QueryByCriteria qbc = QueryFactory.newQuery(Balance.class, crit);
        if (Constants.SF_TYPE_OBJECT.equals(sfCode)) {
            qbc.addOrderByAscending("objectCode");
        }
        else if (Constants.SF_TYPE_LEVEL.equals(sfCode)) {
            qbc.addOrderByAscending("financialObject.financialObjectLevel.financialObjectLevelCode");
        }
        else if (Constants.SF_TYPE_CONSOLIDATION.equals(sfCode)) {
            qbc.addOrderByAscending("financialObject.financialObjectLevel.financialConsolidationObjectCode");
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
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addLessThan("universityFiscalYear", new Integer(year));

        getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(Balance.class, criteria));

        // This is required because if any deleted account balances are in the cache, deleteByQuery doesn't
        // remove them from the cache so a future select will retrieve these deleted account balances from
        // the cache and return them. Clearing the cache forces OJB to go to the database again.
        getPersistenceBrokerTemplate().clearCache();
    }
}
