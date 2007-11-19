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
package org.kuali.module.chart.service;

import java.util.Iterator;
import java.util.List;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Delegate;


/**
 * This interface defines methods that an Account Service must provide
 */
public interface AccountService {
    /**
     * Retrieves an Account object based on primary key.
     * 
     * @param chartOfAccountsCode - Chart of Accounts Code
     * @param accountNumber - Account Number
     * @return Account
     */
    public Account getByPrimaryId(String chartOfAccountsCode, String accountNumber);

    /**
     * Method is used by KualiAccountAttribute to enable caching of orgs for routing.
     * 
     * @see org.kuali.module.chart.service.AccountService#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public Account getByPrimaryIdWithCaching(String chartOfAccountsCode, String accountNumber);

    /**
     * This method retrieves the fiscal officers primary delegate based on the chart, account, and document type specified on the
     * example, along with the total dollar amount
     * 
     * @param delegateExample The object that contains the chart, account, and document type that should be used to query the
     *        account delegate table
     * @param totalDollarAmount The amount that should be compared to the from and to amount on the account delegate table
     * @return The primary delegate for this account, document type, and amount
     */
    public Delegate getPrimaryDelegationByExample(Delegate delegateExample, String totalDollarAmount);

    /**
     * This method retrieves the fiscal officers secondary delegates based on the chart, account, and document type specified on the
     * example, along with the total dollar amount
     * 
     * @param delegateExample The object that contains the chart, account, and document type that should be used to query the
     *        account delegate table
     * @param totalDollarAmount The amount that should be compared to the from and to amount on the account delegate table
     * @return The primary delegate for this account, document type, and amount
     */
    public List getSecondaryDelegationsByExample(Delegate delegateExample, String totalDollarAmount);

    /**
     * Fetches the accounts that the user is either the fiscal officer or fiscal officer delegate for.
     * 
     * @param kualiUser
     * @return a list of Accounts that the user has responsibility for
     */
    public List getAccountsThatUserIsResponsibleFor(UniversalUser kualiUser);

    /**
     * Does the given user have responsibilities on the given account?
     * 
     * @param kualiUser the universal user to check responsibilities for
     * @param account the account to check responsibilities on
     * @return true if user does have responsibilities, false if otherwise
     */
    public boolean hasResponsibilityOnAccount(UniversalUser kualiUser, Account account);

    /**
     * get all accounts in the system. This is needed by a sufficient funds rebuilder job
     * 
     * @return iterator of all accounts
     */
    public Iterator getAllAccounts();
}