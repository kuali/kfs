package edu.arizona.kfs.gl.dataaccess.impl;

import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

import edu.arizona.kfs.gl.businessobject.BudgetAdjustmentTransaction;
import edu.arizona.kfs.gl.dataaccess.BudgetAdjustmentTransactionDao;

public class BudgetAdjustmentTransactionDaoOjb extends PlatformAwareDaoBaseOjb implements BudgetAdjustmentTransactionDao{

	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetAdjustmentTransactionDaoOjb.class);
	
	public BudgetAdjustmentTransactionDaoOjb() {
		super();
	}
	
	public int getMaxSequenceNumber(Transaction t) {
		LOG.debug("getSequenceNumber() ");
		
		Criteria crit = new Criteria();
		crit.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, t.getUniversityFiscalYear());
		crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, t.getChartOfAccountsCode());
		crit.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, t.getAccountNumber());
		crit.addEqualTo(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, t.getSubAccountNumber());
		crit.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, t.getFinancialObjectCode());
		crit.addEqualTo(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, t.getFinancialSubObjectCode());
		crit.addEqualTo(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, t.getFinancialBalanceTypeCode());
		crit.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, t.getFinancialObjectTypeCode());
		crit.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, t.getUniversityFiscalPeriodCode());
		crit.addEqualTo(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, t.getFinancialDocumentTypeCode());
		crit.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, t.getDocumentNumber());
		crit.addEqualTo(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER, t.getOrganizationDocumentNumber());
		
		ReportQueryByCriteria q = QueryFactory.newReportQuery(Entry.class, crit);
		q.setAttributes(new String[] { "max(transactionLedgerEntrySequenceNumber)" });
		
		Iterator iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
		if (iter.hasNext()) {
			Object[] data = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iter);
			BigDecimal max = (BigDecimal) data[0]; // Don't know why OJB returns a BigDecimal, but it does
			
			if (max == null) {
				return 0;
			}
			else {
				return max.intValue();
			}
		}
		else {
			return 0;
		}
	}
	
	/**
	 * Queries the database to find the budget adjustment transaction in the database
	 * that would be affected if the given transaction is posted
	 * 
	 * @param t a transaction to find a related budget adjustment transaction for
	 * @return the budget adjustment transaction if found, null otherwise
	 * @see edu.arizona.kfs.gl.dataaccess.BudgetAdjustmentTransactionDao#getByTransaction(org.kuali.kfs.gl.businessobject.Transaction)
	 */
	public BudgetAdjustmentTransaction getByTransaction(Transaction t) {
		LOG.debug("getByTransaction() started");
		
		Criteria crit = new Criteria();
		crit.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, t.getUniversityFiscalYear());
		crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, t.getChartOfAccountsCode());
		crit.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, t.getAccountNumber());
		crit.addEqualTo(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, t.getSubAccountNumber());
		crit.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, t.getFinancialObjectCode());
		crit.addEqualTo(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, t.getFinancialSubObjectCode());
		crit.addEqualTo(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, t.getFinancialBalanceTypeCode());
		crit.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, t.getFinancialObjectTypeCode());
		crit.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, t.getUniversityFiscalPeriodCode());
		crit.addEqualTo(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, t.getFinancialDocumentTypeCode());
		crit.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, t.getDocumentNumber());
		crit.addEqualTo(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER, t.getTransactionLedgerEntrySequenceNumber());
		crit.addEqualTo(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER, t.getOrganizationDocumentNumber());
		
		QueryByCriteria qbc = QueryFactory.newQuery(BudgetAdjustmentTransaction.class, crit);
		return (BudgetAdjustmentTransaction) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
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
	 * Returns all budget adjustment transactions for a particular RBC Number
	 * 
	 * @param rbcNumber
	 * @return an iterator of Budget adjustment Transaction records
	 * @see edu.arizona.kfs.gl.dataaccess.BudgetAdjustmentTransactionDao#getByRBCNumber(String)
	 */
	public Iterator getByRBCNumber(String rbcNumber) {
		LOG.debug("getByRBCNumber() started");
		
		Criteria crit = new Criteria();
		crit.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, rbcNumber);
		
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
