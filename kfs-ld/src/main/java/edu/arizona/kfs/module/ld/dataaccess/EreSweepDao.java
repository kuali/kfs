package edu.arizona.kfs.module.ld.dataaccess;

import java.util.List;

import org.kuali.kfs.module.ld.businessobject.LedgerBalance;

import edu.arizona.kfs.module.ld.util.EreSweepBalanceHelper;

public interface EreSweepDao {

	/**
	 * 
	 * Retrieves a list of LedgerBalance that match parameters given
	 * 
	 * @param employeeBalance
	 * @param includedSubFunds
	 * @param includedObjectCodes
	 * @return A list of LedgerBalance objects
	 */
	public List<LedgerBalance> getMatchingBalances (EreSweepBalanceHelper employeeBalance, List<String> includedSubFunds, List<String> includedObjectCodes);

	/**
	 * With the help of EreSweepBalanceHelper, this method returns a list of unique LedgerBalance objects
	 * 
	 * @param includedSubFunds
	 * @param includedObjectCodes
	 * @return A list of distinct Ledger balances
	 */
	public List<EreSweepBalanceHelper> getDistinctBalance(List<String> includedSubFunds, List<String> includedObjectCodes);

	
}
