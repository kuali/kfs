package edu.arizona.kfs.pdp.service;

import java.util.List;

import org.kuali.rice.kim.api.identity.Person;

public interface PayeeAchAccountService {

	/**
	 * After getting the file contents, this delegates to the ACH payee bank
	 * account input file type to parse the file.
	 */
	boolean loadAchPayeeAccountFile(String inputFileName);

	/**
	 * This method checks to see if payee is in override group, defined in the
	 * BANKING_INFORMATION_OVERRIDE_GROUP
	 * 
	 * @param currentPayeeAcct
	 * @return true if the payee is in the override group; false otherwise
	 */
	boolean isPayeeInOverrideGroup(Person payee);

	/**
	 * gets the "override" groupId, and the override users
	 * 
	 * @return list of payeeId's
	 */
	List<String> getPrincipalIdsInOverrideGroup();

}
