package edu.arizona.kfs.gl.dataaccess.impl;

import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

import edu.arizona.kfs.gl.businessobject.BudgetAdjustmentTransaction;
import edu.arizona.kfs.gl.dataaccess.BudgetAdjustmentTransactionDao;

public class BudgetAdjustmentTransactionDaoOjb extends PlatformAwareDaoBaseOjb implements BudgetAdjustmentTransactionDao{

	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetAdjustmentTransactionDaoOjb.class);
	
	public BudgetAdjustmentTransactionDaoOjb() {
		super();
	}
	
	/**
	 * Fetches all budget adjustment transactions currently in the database
	 * 
	 * @return an Iterator with all budget adjustment transactions from the database
	 * @see edu.arizona.kfs.gl.dataaccess.BudgetAdjustmentTransactionDao#getAllBudgetAdjustmentTransactions()
	 */
	public Iterator getAllBudgetAdjustmentTransactions() {
		LOG.debug("getAllBudgetAdjustmentTransactions() started");
		try {
			Criteria crit = new Criteria();
			
			// We want them all so no criteria is added
			
			QueryByCriteria qbc = QueryFactory.newQuery(BudgetAdjustmentTransaction.class, crit);
			return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Returns all budget adjustment transactions for a particular document Number
	 * 
	 * @param docNumber
	 * @return an iterator of Budget adjustment Transaction records
	 * @see edu.arizona.kfs.gl.dataaccess.BudgetAdjustmentTransactionDao#getByDocNumber(String)
	 */
	public Iterator getByDocNumber(String docNumber) {
		LOG.debug("getByRBCNumber() started");
		
		Criteria crit = new Criteria();
		crit.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, docNumber);
		
		QueryByCriteria qbc = QueryFactory.newQuery(BudgetAdjustmentTransaction.class, crit);
		return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
	}
	
	/**
	 * Deletes the given budget adjustment transaction
	 * 
	 * @param et the budget adjustment transaction that will be removed, as such, from the database
	 * @see edu.arizona.kfs.gl.dataaccess.BudgetAdjustmentTransactionDao#delete(edu.arizona.kfs.gl.businessobject.BudgetAdjustmentTransaction)
	 */
	public void delete(BudgetAdjustmentTransaction et) {
		LOG.debug("delete() started");
		
		getPersistenceBrokerTemplate().delete(et);
	}
	
	/**
	 * Saves an budget adjustment transaction
	 * 
	 * @param et the budget adjustment transaction to save
	 * @see edu.arizona.kfs.gl.dataaccess.BudgetAdjustmentTransactionDao#save(edu.arizona.kfs.gl.businessobject.BudgetAdjustmentTransaction)
	 */
	public void save(BudgetAdjustmentTransaction et) {
		LOG.debug("save() started");
		
		getPersistenceBrokerTemplate().store(et);
	}
	
	/**
	 * Since budget adjustment transactions are temporary, just like flies that live for a mere day, this method removes all of the
	 * currently existing budget adjustment transactions from the database, all budget adjustment transactions having run through the poster and
	 * fulfilled their lifecycle
	 * 
	 * @see edu.arizona.kfs.gl.dataaccess.BudgetAdjustmentTransactionDao#deleteAllBudgetAdjustmentTransactions()
	 */
	public void deleteAllBudgetAdjustmentTransactions() {
		QueryByCriteria queryId = new QueryByCriteria(BudgetAdjustmentTransaction.class, QueryByCriteria.CRITERIA_SELECT_ALL);
		getPersistenceBrokerTemplate().deleteByQuery(queryId);
		getPersistenceBrokerTemplate().clearCache();
	}
}
