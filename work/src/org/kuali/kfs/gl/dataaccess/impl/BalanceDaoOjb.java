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
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.gl.bo.Balance;
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

    public Iterator findBalances(Account account, Integer fiscalYear,
            Collection includedObjectCodes, Collection excludedObjectCodes,
            Collection objectTypeCodes, Collection balanceTypeCodes) {

        Criteria criteria = new Criteria();

        criteria.addEqualTo("accountNumber", account.getAccountNumber());
        criteria.addEqualTo("chartOfAccountsCode", account.getChartOfAccountsCode());

        criteria.addEqualTo("universityFiscalYear", fiscalYear);

        criteriaBuilder(criteria, "FIN_OBJ_TYP_CD", objectTypeCodes);
        criteriaBuilder(criteria, "FIN_BALANCE_TYP_CD", balanceTypeCodes);
        criteriaBuilder(criteria, "FIN_OBJECT_CD", includedObjectCodes);
        negatedCriteriaBuilder(criteria, "FIN_OBJECT_CD", excludedObjectCodes);

        ReportQueryByCriteria query = new ReportQueryByCriteria(
            Balance.class, criteria);

        // returns an iterator of all matching balances
        Iterator balances = getPersistenceBrokerTemplate().getIteratorByQuery(query);
        return balances;
    }

    /**
     * @see org.kuali.module.gl.dao.BalanceDao#findCashBalance(java.util.Map, boolean)
     */
    public Iterator findCashBalance(Map fieldValues, boolean isConsolidated) {
        Criteria criteria = new Criteria();

        Iterator propsIter = fieldValues.keySet().iterator();
        while (propsIter.hasNext()) {
            String propertyName = (String) propsIter.next();
            String propertyValue = (String) fieldValues.get(propertyName);

            // if searchValue is empty and the key is not a valid property ignore
            if (StringUtils.isBlank(propertyValue)
                    || !(PropertyUtils.isWriteable(new Balance(), propertyName))) {
                continue;
            }
            criteria.addEqualTo(propertyName, propertyValue);
        }
        criteria.addEqualTo("balanceTypeCode", "AC");
        criteria.addEqualToField("chart.financialCashObjectCode", "objectCode");

        ReportQueryByCriteria query = new ReportQueryByCriteria(Balance.class, criteria);
        
        // set the selection attributes
        // if consolidated, then ignore subaccount number
        String [] attributes = null;
        if(isConsolidated){
	        attributes = new String[] 
	               {"universityFiscalYear", "chartOfAccountsCode",
	                "accountNumber", "balanceTypeCode", "objectCode",
	                "sum(accountLineAnnualBalanceAmount)", 
	                "sum(beginningBalanceLineAmount)",
	                "sum(contractsGrantsBeginningBalanceAmount)" 
	             	};
        }
        else{
            attributes = new String[] 
                   {"universityFiscalYear", "chartOfAccountsCode",
                    "accountNumber", "subAccountNumber",
                    "balanceTypeCode", "objectCode",
                    "sum(accountLineAnnualBalanceAmount)", 
                    "sum(beginningBalanceLineAmount)",
                    "sum(contractsGrantsBeginningBalanceAmount)" 
                 	};
        }      
        query.setAttributes(attributes);

        // add the group criteria into the selection statement
        query.addGroupBy("universityFiscalYear");
        query.addGroupBy("chartOfAccountsCode");
        query.addGroupBy("accountNumber");
        query.addGroupBy("balanceTypeCode");
        query.addGroupBy("objectCode");
        if(!isConsolidated) query.addGroupBy("subAccountNumber");

        Iterator cashBalance = getPersistenceBrokerTemplate()
                .getReportQueryIteratorByQuery(query);

        return cashBalance;
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
