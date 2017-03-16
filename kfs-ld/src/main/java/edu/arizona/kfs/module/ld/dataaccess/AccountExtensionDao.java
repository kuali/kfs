package edu.arizona.kfs.module.ld.dataaccess;

import org.kuali.kfs.coa.businessobject.Account;

import edu.arizona.kfs.coa.businessobject.AccountExtension;

public interface AccountExtensionDao {
	
	/**
	 * 
	 * Finds a matching AccountExtension by primary key(s)
	 * 
	 * @param accountNumber
	 * @param finCoaCd
	 * @return AccountExtension object
	 */
	public AccountExtension getAccountExtensionByPrimaryKey(String accountNumber, String finCoaCd);
	
	/**
	 * 
	 * Finds a matching Account by primary key(s)
	 * 
	 * @param accountNumber
	 * @param chartCode
	 * @return Account object
	 */
	public Account getAccountByPrimaryKey(String accountNumber, String chartCode);
	
	/**
	 * 
	 * Find a matching Account by Sub Fund Group Code
	 * 
	 * @param fringeAccount
	 * @param objectCode
	 * @param includedSubFunds
	 * @return Account object
	 */
	public Account getAccountBySubFundGroupCd(String fringeAccount, String objectCode, String[] includedSubFunds);
	
}
