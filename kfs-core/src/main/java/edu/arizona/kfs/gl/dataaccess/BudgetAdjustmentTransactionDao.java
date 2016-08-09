package edu.arizona.kfs.gl.dataaccess;

import java.util.Iterator;

import org.kuali.kfs.gl.businessobject.Transaction;

import edu.arizona.kfs.gl.businessobject.BudgetAdjustmentTransaction;

public interface BudgetAdjustmentTransactionDao {

	/**
	 * Saves a budget adjustment transaction record
	 * @param ba a budget adjustment transaction to save
	 */
	public void save(BudgetAdjustmentTransaction ba);
	
	/**
	 * Find the maximum transactionLedgerEntrySequenceNumber 
	 * in the entry table for a specific transaction.  This is 
	 * used to make sure that rows added have a unique primary key.
	 * 
	 * @param t a transaction to find the maximum sequence number for
	 * @return the max sequence number for the given transaction
	 */
	public int getMaxSequenceNumber(Transaction t);
	
	/**
	 * Looks up the budget adjustment transaction that matches the keys 
	 * from the given transaction.
	 * 
	 * @param t the given transaction
	 * @return the budget adjustment transaction that matches the keys of that transaction
	 */
	public BudgetAdjustmentTransaction getByTransaction(Transaction t);
	
	/**
	 * Returns all budget adjustment transactions currently in the database
	 * 
	 * @return an Iterator with all budget adjustment transactions from the database
	 */
	public Iterator getAllBudgetAdjustmentTransactions();
	
	/**
	 * Returns all budget adjustment transactions for a particular RBC Number
	 * 
	 * @param rbcNumber
	 * @return an iterator of budget adjustment transaction records
	 */
	public Iterator getByRBCNumber(String rbcNumber);
	
	/**
	 * Deletes a budget adjustment transaction record
	 * 
	 * @param ba a budget adjustment transaction to delete
	 */
	public void delete(BudgetAdjustmentTransaction ba);
	
	/**
	 * Since budget adjustment transactions are temporary, this method
	 * removes all of the currently existing budget adjustment
	 * transactions from the database
	 */
	public void deleteAllBudgetAdjustmentTransactions();
}
