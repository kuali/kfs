/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.coa.dataaccess;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.rice.kim.bo.Person;


/**
 * This interface defines what methods of data retrieval should be allowed for {@link org.kuali.kfs.coa.businessobject.Account}, and
 * {@link org.kuali.kfs.coa.businessobject.AccountDelegate}. It also defines a method for checking if a given User is responsible for an Account
 */
public interface AccountDao {

    /**
     * @param chartOfAccountsCode - part of composite key
     * @param accountNumber - part of composite key
     * @return Account Retrieves an Account object based on primary key.
     */
    public Account getByPrimaryId(String chartOfAccountsCode, String accountNumber);

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getPrimaryDelegationByExample(org.kuali.kfs.coa.businessobject.AccountDelegate,
     *      java.lang.String)
     */
    public List getPrimaryDelegationByExample(AccountDelegate delegateExample, String totalDollarAmount);

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getSecondaryDelegationsByExample(org.kuali.kfs.coa.businessobject.AccountDelegate,
     *      java.lang.String)
     */
    public List getSecondaryDelegationsByExample(AccountDelegate delegateExample, String totalDollarAmount);

    /**
     * fetch the AccountResponsibility objects that the user has associated with them
     * 
     * @param kualiUser
     * @return a list of AccountResponsibility objects
     */
    public List getAccountsThatUserIsResponsibleFor(Person kualiUser);

    /**
     * This method should determine if the given user has any responsibilities on the given account
     * 
     * @param person the user to check responsibilities for
     * @param account the account to check responsibilities on
     * @return true if user is somehow responsible for account, false if otherwise
     */
    public boolean determineUserResponsibilityOnAccount(Person person, Account account);

    /**
     * get all accounts in the system. This is needed by a sufficient funds rebuilder job
     * 
     * @return iterator of all accounts
     */
    public Iterator getAllAccounts();
    
    /**
     * Retrieves all active accounts from the database where the given principal is the fiscal officer
     * @param principalId the principal id of the fiscal officer
     * @return an Iterator of active Accounts
     */
    public abstract Iterator<Account> getActiveAccountsForFiscalOfficer(String principalId);
    
    /**
     * Retrieves all expired accounts from the database where the given principal is the fiscal officer
     * @param principalId the principal id of the fiscal officer
     * @return an Iterator of expired Accounts
     */
    public abstract Iterator<Account> getExpiredAccountsForFiscalOfficer(String principalId);
    
    /**
     * Retrieves all active accounts from the database where the given principal is the account supervisor
     * @param principalId the principal id of the account supervisor
     * @return an Iterator of active Accounts
     */
    public abstract Iterator<Account> getActiveAccountsForAccountSupervisor(String principalId);
    
    /**
     * Retrieves all active accounts from the database where the given principal is the account supervisor
     * @param principalId the principal id of the account supervisor
     * @return an Iterator of expired Accounts
     */
    public abstract Iterator<Account> getExpiredAccountsForAccountSupervisor(String principalId);
    
    /**
     * Determines if the given principal is the fiscal officer of any non-closed account
     * @param principalId the principal to check for the fiscal officer role
     * @return true if the principal is a fiscal officer for any non-closed account, false otherwise
     */
    public abstract boolean isPrincipalInAnyWayShapeOrFormFiscalOfficer(String principalId);
    
    /**
     * Determines if the given principal is the account supervisor of any non-closed account
     * @param principalId the principal to check for the account supervisor role
     * @return true if the principal is a account supervisor for any non-closed account, false otherwise
     */
    public abstract boolean isPrincipalInAnyWayShapeOrFormAccountSupervisor(String principalId);
    
    /**
     * Determines if the given principal is the account manager of any non-closed account
     * @param principalId the principal to check for the account manager role
     * @return true if the principal is a account manager for any non-closed account, false otherwise
     */
    public abstract boolean isPrincipalInAnyWayShapeOrFormAccountManager(String principalId);
    
    public Collection<Account> getAccountsForAccountNumber(String accountNumber);
}

