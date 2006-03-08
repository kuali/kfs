/*
 * Created on Oct 14, 2005
 *
 */
package org.kuali.module.gl.dao.ojb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.Constants;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.SufficientFundBalances;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.BalanceDao;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * @author jsissom
 *  
 */
public class BalanceDaoOjb extends PersistenceBrokerDaoSupport implements BalanceDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceDaoOjb.class);

    public BalanceDaoOjb() {
        super();
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

    public Iterator findBalances(Account account, Integer fiscalYear, Collection includedObjectCodes,
            Collection excludedObjectCodes, Collection objectTypeCodes, Collection balanceTypeCodes) {

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
    public Iterator findCashBalance(Map fieldValues, boolean isConsolidated) {

        Criteria criteria = buildCriteriaFromMap(fieldValues, new Balance(), false);
        criteria.addEqualTo("balanceTypeCode", "AC");
        criteria.addEqualToField("chart.financialCashObjectCode", "objectCode");

        ReportQueryByCriteria query = new ReportQueryByCriteria(Balance.class, criteria);

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

        // set the selection attributes
        String[] attributes = (String[]) attributeList.toArray(new String[attributeList.size()]);
        query.setAttributes(attributes);

        // add the group criteria into the selection statement
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);

        Iterator cashBalance = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);

        return cashBalance;
    }

    /**
     * @see org.kuali.module.gl.dao.BalanceDao#findBalance(java.util.Map, boolean)
     */
    public Iterator findBalance(Map fieldValues, boolean isConsolidated) {

        Criteria criteria = buildCriteriaFromMap(fieldValues, new Balance(), false);
        ReportQueryByCriteria query = new ReportQueryByCriteria(Balance.class, criteria);

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
            return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        }

        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * This method builds the query criteria based on the input field map
     * 
     * @param fieldValues
     * @param balance
     * @return a query criteria
     */
    private Criteria buildCriteriaFromMap(Map fieldValues, Balance balance, boolean isBalanceTypeHandled) {
        Criteria criteria = new Criteria();

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
            else {
                criteria.addEqualTo(propertyName, propertyValue);
            }
        }
        return criteria;
    }
    
    /**
     * This method builds an balance type code list
     * 
     * @return List a balance type code list
     */
    private List buildBalanceTypeCodeList() {
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
    private List buildAttributeList(boolean isExtended) {
        List attributeList = new ArrayList();

        attributeList.add("universityFiscalYear");
        attributeList.add("chartOfAccountsCode");
        attributeList.add("accountNumber");
        attributeList.add("subAccountNumber");
        attributeList.add("balanceTypeCode");
        attributeList.add("objectCode");
        attributeList.add("subObjectCode");
        attributeList.add("objectTypeCode");
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
    private List buildGroupByList() {
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
     * @see org.kuali.module.gl.dao.BalanceDao#getCurrentBudgetForObjectCode(java.lang.Integer, java.lang.String, java.lang.String, java.lang.String)
     */
    public Balance getCurrentBudgetForObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber,
            String objectCode) {
        Criteria crit = new Criteria();
        crit.addEqualTo("universityFiscalYear", universityFiscalYear);
        crit.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        crit.addEqualTo("accountNumber", accountNumber);
        crit.addEqualTo("objectCode", objectCode);
        crit.addEqualTo("balanceTypeCode", Constants.BALANCE_TYPE_BUDGET);
        
        QueryByCriteria qbc = QueryFactory.newQuery(Balance.class, crit);
        return (Balance) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }
}
