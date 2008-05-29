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
package org.kuali.module.chart.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.spring.Cached;
import org.kuali.kfs.annotation.NonTransactional;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.bo.Delegate;
import org.kuali.module.chart.dao.AccountDao;
import org.kuali.module.chart.service.AccountService;

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
     * @see org.kuali.module.chart.service.impl.AccountServiceImpl#getByPrimaryId(java.lang.String, java.lang.String)
     */
    @Cached
    public Account getByPrimaryIdWithCaching(String chartOfAccountsCode, String accountNumber) {
        return accountDao.getByPrimaryId(chartOfAccountsCode, accountNumber);
    }

    /**
     * @see org.kuali.module.chart.service.AccountService#getAccountsThatUserIsResponsibleFor(org.kuali.bo.user.KualiUser)
     */
    public List getAccountsThatUserIsResponsibleFor(UniversalUser universalUser) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieving accountsResponsible list for user " + universalUser.getPersonName());
        }

        // gets the list of accounts that the user is the Fiscal Officer of
        List accountList = accountDao.getAccountsThatUserIsResponsibleFor(universalUser);
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieved accountsResponsible list for user " + universalUser.getPersonName());
        }
        return accountList;
    }

    /**
     * @see org.kuali.module.chart.service.AccountService#hasResponsibilityOnAccount(org.kuali.core.bo.user.UniversalUser,
     *      org.kuali.module.chart.bo.Account)
     */
    public boolean hasResponsibilityOnAccount(UniversalUser kualiUser, Account account) {
        return accountDao.determineUserResponsibilityOnAccount(kualiUser, account);
    }

    /**
     * @see org.kuali.module.chart.service.AccountService#getPrimaryDelegationByExample(org.kuali.module.chart.bo.Delegate,
     *      java.lang.String)
     */
    public Delegate getPrimaryDelegationByExample(Delegate delegateExample, String totalDollarAmount) {
        return accountDao.getPrimaryDelegationByExample(delegateExample, totalDollarAmount);
    }

    /**
     * @see org.kuali.module.chart.service.AccountService#getSecondaryDelegationsByExample(org.kuali.module.chart.bo.Delegate,
     *      java.lang.String)
     */
    public List getSecondaryDelegationsByExample(Delegate delegateExample, String totalDollarAmount) {
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
     * @see org.kuali.module.chart.service.AccountService#accountIsAccessible(org.kuali.kfs.document.AccountingDocument, org.kuali.kfs.bo.AccountingLine, org.kuali.module.chart.bo.ChartUser)
     */
    public boolean accountIsAccessible(AccountingDocument financialDocument, AccountingLine accountingLine, ChartUser user) {
        LOG.debug("accountIsAccessible(AccountingDocument, AccountingLine) - start");

        boolean isAccessible = false;

        KualiWorkflowDocument workflowDocument = financialDocument.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            isAccessible = true;
        }
        else {
            if (workflowDocument.stateIsEnroute()) {
                String chartCode = accountingLine.getChartOfAccountsCode();
                String accountNumber = accountingLine.getAccountNumber();

                // if a document is enroute, user can only refer to for accounts for which they are responsible
                isAccessible = user.isResponsibleForAccount(chartCode, accountNumber);
            }
            else {
                if (workflowDocument.stateIsApproved() || workflowDocument.stateIsFinal() || workflowDocument.stateIsDisapproved()) {
                    isAccessible = false;
                }
                else {
                    if (workflowDocument.stateIsException() && user.getUniversalUser().isWorkflowExceptionUser()) {
                        isAccessible = true;
                    }
                }
            }
        }

        LOG.debug("accountIsAccessible(AccountingDocument, AccountingLine) - end");
        return isAccessible;
    }

    /**
     * @param accountDao The accountDao to set.
     */
    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

}