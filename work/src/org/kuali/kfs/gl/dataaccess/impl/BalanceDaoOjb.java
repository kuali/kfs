/*
 * Created on Oct 14, 2005
 *
 */
package org.kuali.module.gl.dao.ojb;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.BalanceDao;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * @author jsissom
 *  
 */
public class BalanceDaoOjb extends PersistenceBrokerDaoSupport implements BalanceDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(BalanceDaoOjb.class);

    public BalanceDaoOjb() {
        super();
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
     * This method adds to the given criteria if the given collection is non-empty. It
     * uses an EQUALS if there is exactly one element in the collection; otherwise, its
     * uses an IN
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
    private void negatedCriteriaBuilder(Criteria criteria, String name,
            Collection collection) {
        criteriaBuilderHelper(criteria, name, collection, true);
    }


    /**
     * This method provides the implementation for the conveniences methods
     * criteriaBuilder & negatedCriteriaBuilder
     * 
     * @param negate - the criterion will be negated (NOT EQUALS, NOT IN) when this is
     *        true
     *  
     */
    private void criteriaBuilderHelper(Criteria criteria, String name,
            Collection collection, boolean negate) {
        if (collection != null) {
            int size = collection.size();
            if (size == 1) {
                if (negate) {
                    criteria.addNotEqualTo("", collection.iterator().next());
                }
                else {
                    criteria.addEqualTo("", collection.iterator().next());
                }
            }
            if (size > 1) {
                if (negate) {
                    criteria.addNotIn("", collection);
                }
                else {
                    criteria.addIn("", collection);

                }
            }
        }

    }

    public Iterator findBalances(Account account, Integer fiscalYear,
            Collection includedObjectCodes, Collection excludedObjectCodes,
            Collection objectTypeCodes, Collection balanceTypeCodes) {

        Criteria criteria = new Criteria();

        criteria.addEqualTo("accountNumber", account.getAccountNumber());
        criteria.addEqualTo("chartOfAccountsCode", account.getChartOfAccountsCode());

        criteria.addEqualTo("universityFiscalYear", fiscalYear);

        criteriaBuilder(criteria, "objectTypeCode", objectTypeCodes);
        criteriaBuilder(criteria, "balanceTypeCode", balanceTypeCodes);
        criteriaBuilder(criteria, "objectCode", includedObjectCodes);
        negatedCriteriaBuilder(criteria, "objectCode", excludedObjectCodes);

        ReportQueryByCriteria query = new ReportQueryByCriteria(
            GeneralLedgerPendingEntry.class, criteria);

        // returns an iterator of all matching balances
        Iterator balances = getPersistenceBrokerTemplate().getIteratorByQuery(query);
        return balances;
    }

    /**
     * @see org.kuali.module.gl.dao.BalanceDao#findBalanceSummary(java.util.Map)
     */
    public Iterator findBalanceSummary(Map fieldValues) {
        Criteria criteria = new Criteria();

        Iterator propsIter = fieldValues.keySet().iterator();
        while (propsIter.hasNext()) {
            String propertyName = (String) propsIter.next();
            String propertyValue = (String) fieldValues.get(propertyName);

            // if searchValue is empty and the key is not a valid property ignore
            if (StringUtils.isBlank(propertyValue)
                    || !(PropertyUtils.isWriteable(Balance.class, propertyName))) {
                continue;
            }

            criteria.addEqualTo(propertyName, propertyValue);
        }

        ReportQueryByCriteria query;
        query = new ReportQueryByCriteria(Balance.class, criteria);

        // set the selection attributes
        query.setAttributes(new String[] { "universityFiscalYear", "chartOfAccountsCode",
                "accountNumber", "subAccountNumber",
                "sum(accountLineAnnualBalanceAmount)", "sum(beginningBalanceLineAmount)",
                "sum(contractsGrantsBeginningBalanceAmount)" });

        // add the group criteria into the selection statement
        query.addGroupBy(new String[] { "universityFiscalYear", "chartOfAccountsCode",
                "accountNumber" });

        Iterator balanceSummary = getPersistenceBrokerTemplate()
                .getReportQueryIteratorByQuery(query);

        return balanceSummary;
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

}
