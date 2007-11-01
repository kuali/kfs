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
package org.kuali.module.chart.dao;

import java.util.Iterator;
import java.util.List;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Delegate;


/**
 * This interface defines what methods of data retrieval should be allowed for {@link org.kuali.module.chart.bo.Account}, and
 * {@link org.kuali.module.chart.bo.Delegate}. It also defines a method for checking if a given User is responsible for an Account
 */
public interface AccountDao {

    /**
     * @param chartOfAccountsCode - part of composite key
     * @param accountNumber - part of composite key
     * @return Account Retrieves an Account object based on primary key.
     */
    public Account getByPrimaryId(String chartOfAccountsCode, String accountNumber);

    /**
     * @see org.kuali.module.chart.service.AccountService#getPrimaryDelegationByExample(org.kuali.module.chart.bo.Delegate,
     *      java.lang.String)
     */
    public Delegate getPrimaryDelegationByExample(Delegate delegateExample, String totalDollarAmount);

    /**
     * @see org.kuali.module.chart.service.AccountService#getSecondaryDelegationsByExample(org.kuali.module.chart.bo.Delegate,
     *      java.lang.String)
     */
    public List getSecondaryDelegationsByExample(Delegate delegateExample, String totalDollarAmount);

    /**
     * fetch the AccountResponsibility objects that the user has associated with them
     * 
     * @param kualiUser
     * @return a list of AccountResponsibility objects
     */
    public List getAccountsThatUserIsResponsibleFor(UniversalUser kualiUser);

    /**
     * This method should determine if the given user has any responsibilities on the given account
     * 
     * @param universalUser the user to check responsibilities for
     * @param account the account to check responsibilities on
     * @return true if user is somehow responsible for account, false if otherwise
     */
    public boolean determineUserResponsibilityOnAccount(UniversalUser universalUser, Account account);

    /**
     * get all accounts in the system. This is needed by a sufficient funds rebuilder job
     * 
     * @return iterator of all accounts
     */
    public Iterator getAllAccounts();
}