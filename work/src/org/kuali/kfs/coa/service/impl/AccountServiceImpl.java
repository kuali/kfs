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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.kfs.coa.dataaccess.AccountDao;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kns.util.spring.Cached;

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
        String documentTypeName = delegateExample.getFinancialDocumentTypeCode();
        List primaryDelegations = filterAccountDelegates(delegateExample, accountDao.getPrimaryDelegationByExample(delegateExample, totalDollarAmount));
        if (primaryDelegations.isEmpty()) {
            return null;
        }
        AccountDelegate delegate;
        for (Iterator iterator = primaryDelegations.iterator(); iterator.hasNext();) {
            delegate = (AccountDelegate) iterator.next();
            if (!KFSConstants.ROOT_DOCUMENT_TYPE.equals(delegate.getFinancialDocumentTypeCode())) {
                return delegate;
            }
        }
        return (AccountDelegate)primaryDelegations.iterator().next();
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getSecondaryDelegationsByExample(org.kuali.kfs.coa.businessobject.AccountDelegate,
     *      java.lang.String)
     */
    public List getSecondaryDelegationsByExample(AccountDelegate delegateExample, String totalDollarAmount) {
        List secondaryDelegations = accountDao.getSecondaryDelegationsByExample(delegateExample, totalDollarAmount);
        return filterAccountDelegates(delegateExample, secondaryDelegations);
    }

    /**
     * This method filters account delegates by 
     * 1) performing an exact match on the document type name of delegateExample
     * 2) if no match is found for 1), then by performing an exact match on 
     * the closest parent document type name of delegateExample document type name.
     * 
     * @param delegateExample
     * @param accountDelegatesToFilterFrom
     * @return
     */
    private List<AccountDelegate> filterAccountDelegates(AccountDelegate delegateExample, List<AccountDelegate> accountDelegatesToFilterFrom){
        String documentTypeName = delegateExample.getFinancialDocumentTypeCode();
        AccountDelegate delegate;
        List<AccountDelegate> filteredAccountDelegates = filterAccountDelegates(accountDelegatesToFilterFrom, documentTypeName);
        if(filteredAccountDelegates.size()==0){
            Set<String> potentialParentDocumentTypeNames = getPotentialParentDocumentTypeNames(accountDelegatesToFilterFrom);
            String closestParentDocumentTypeName = KimCommonUtils.getClosestParentDocumentTypeName(
                    KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName), 
                    potentialParentDocumentTypeNames);
            filteredAccountDelegates = filterAccountDelegates(accountDelegatesToFilterFrom, closestParentDocumentTypeName);
        }
        return filteredAccountDelegates;
    }
    
    /**
     * This method filters account delegates by performing an exact match on the document type name passed in.
     * 
     * @param delegations
     * @param documentTypeNameToFilterOn
     * @return
     */
    private List<AccountDelegate> filterAccountDelegates(List<AccountDelegate> delegations, String documentTypeNameToFilterOn){
        AccountDelegate delegate;
        List<AccountDelegate> filteredSecondaryDelegations = new ArrayList<AccountDelegate>();
        for(Object delegateObject: delegations){
            delegate = (AccountDelegate)delegateObject;
            if(StringUtils.equals(delegate.getFinancialDocumentTypeCode(), documentTypeNameToFilterOn)){
                filteredSecondaryDelegations.add(delegate);
            }
        }
        return filteredSecondaryDelegations;
    }

    /**
     * This method gets a list of potential parent document type names 
     * by collecting the unique doc type names from the list of account delegations
     * 
     * @param delegations
     * @return
     */
    private Set<String> getPotentialParentDocumentTypeNames(List<AccountDelegate> delegations){
        AccountDelegate delegate;
        Set<String> potentialParentDocumentTypeNames = new HashSet<String>();
        for(Object delegateObject: delegations){
            delegate = (AccountDelegate)delegateObject;
            if(!potentialParentDocumentTypeNames.contains(delegate.getFinancialDocumentTypeCode()))
                potentialParentDocumentTypeNames.add(delegate.getFinancialDocumentTypeCode());
        }
        return potentialParentDocumentTypeNames;
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
     * @see org.kuali.kfs.coa.service.AccountService#getActiveAccountsForAccountSupervisor(java.lang.String)
     */
    public Iterator<Account> getActiveAccountsForAccountSupervisor(String principalId) {
        return accountDao.getActiveAccountsForAccountSupervisor(principalId);
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getActiveAccountsForFiscalOfficer(java.lang.String)
     */
    public Iterator<Account> getActiveAccountsForFiscalOfficer(String principalId) {
        return accountDao.getActiveAccountsForFiscalOfficer(principalId);
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getExpiredAccountsForAccountSupervisor(java.lang.String)
     */
    public Iterator<Account> getExpiredAccountsForAccountSupervisor(String principalId) {
        return accountDao.getExpiredAccountsForAccountSupervisor(principalId);
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getExpiredAccountsForFiscalOfficer(java.lang.String)
     */
    public Iterator<Account> getExpiredAccountsForFiscalOfficer(String principalId) {
        return accountDao.getExpiredAccountsForFiscalOfficer(principalId);
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#isPrincipalInAnyWayShapeOrFormAccountManager(java.lang.String)
     */
    public boolean isPrincipalInAnyWayShapeOrFormAccountManager(String principalId) {
        return accountDao.isPrincipalInAnyWayShapeOrFormAccountManager(principalId);
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#isPrincipalInAnyWayShapeOrFormAccountSupervisor(java.lang.String)
     */
    public boolean isPrincipalInAnyWayShapeOrFormAccountSupervisor(String principalId) {
        return accountDao.isPrincipalInAnyWayShapeOrFormAccountSupervisor(principalId);
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#isPrincipalInAnyWayShapeOrFormFiscalOfficer(java.lang.String)
     */
    public boolean isPrincipalInAnyWayShapeOrFormFiscalOfficer(String principalId) {
        return accountDao.isPrincipalInAnyWayShapeOrFormFiscalOfficer(principalId);
    }

    /**
     * @param accountDao The accountDao to set.
     */
    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

}
