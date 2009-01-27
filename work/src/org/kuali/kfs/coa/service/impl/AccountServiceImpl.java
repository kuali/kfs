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
package org.kuali.kfs.coa.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.kfs.coa.dataaccess.AccountDao;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.spring.Cached;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * This class is the service implementation for the Account structure. This is the default, Kuali provided implementation.
 */

@NonTransactional
public class AccountServiceImpl implements AccountService {
    private static final Logger LOG = Logger.getLogger(AccountServiceImpl.class);

    private AccountDao accountDao;

    /**
     * Retrieves an Account object based on primary key.
     * 
     * @param chartOfAccountsCode - Chart of Accounts Code
     * @param accountNumber - Account Number
     * @return Account
     * @see AccountService
     */
    public Account getByPrimaryId(String chartOfAccountsCode, String accountNumber) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieving account by primaryId (" + chartOfAccountsCode + "," + accountNumber + ")");
        }

        Account account = accountDao.getByPrimaryId(chartOfAccountsCode, accountNumber);

        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieved account by primaryId (" + chartOfAccountsCode + "," + accountNumber + ")");
        }
        return account;
    }

    /**
     * Method is used by KualiAccountAttribute to enable caching of accounts for routing.
     * 
     * @see org.kuali.kfs.coa.service.impl.AccountServiceImpl#getByPrimaryId(java.lang.String, java.lang.String)
     */
    @Cached
    public Account getByPrimaryIdWithCaching(String chartOfAccountsCode, String accountNumber) {
        return accountDao.getByPrimaryId(chartOfAccountsCode, accountNumber);
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getAccountsThatUserIsResponsibleFor(org.kuali.bo.user.KualiUser)
     */
    public List getAccountsThatUserIsResponsibleFor(Person person) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieving accountsResponsible list for user " + person.getName());
        }

        // gets the list of accounts that the user is the Fiscal Officer of
        List accountList = accountDao.getAccountsThatUserIsResponsibleFor(person);
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieved accountsResponsible list for user " + person.getName());
        }
        return accountList;
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#hasResponsibilityOnAccount(org.kuali.rice.kim.bo.Person,
     *      org.kuali.kfs.coa.businessobject.Account)
     */
    public boolean hasResponsibilityOnAccount(Person kualiUser, Account account) {
        return accountDao.determineUserResponsibilityOnAccount(kualiUser, account);
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getPrimaryDelegationByExample(org.kuali.kfs.coa.businessobject.AccountDelegate,
     *      java.lang.String)
     */

    public AccountDelegate getPrimaryDelegationByExample(AccountDelegate delegateExample, String totalDollarAmount) {
        return accountDao.getPrimaryDelegationByExample(delegateExample, totalDollarAmount);
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getSecondaryDelegationsByExample(org.kuali.kfs.coa.businessobject.AccountDelegate,
     *      java.lang.String)
     */
    public List getSecondaryDelegationsByExample(AccountDelegate delegateExample, String totalDollarAmount) {
        return accountDao.getSecondaryDelegationsByExample(delegateExample, totalDollarAmount);
    }

    /**
     * get all accounts in the system. This is needed by a sufficient funds rebuilder job
     * 
     * @return iterator of all accounts
     */
    public Iterator getAllAccounts() {
        LOG.debug("getAllAccounts() started");

        Iterator accountIter = accountDao.getAllAccounts();
        List accountList = new ArrayList();
        while (accountIter.hasNext()) {
            accountList.add(accountIter.next());
        }
        return accountList.iterator();
    }

    /**
     * @param accountDao The accountDao to set.
     */
    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

}
