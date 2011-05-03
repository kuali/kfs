/*
 * Copyright 2005-2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.coa.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.kfs.coa.dataaccess.AccountDao;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.spring.Cached;

/**
 * This class is the service implementation for the Account structure. This is the default, Kuali provided implementation.
 */

@NonTransactional
public class AccountServiceImpl implements AccountService {
    private static final Logger LOG = Logger.getLogger(AccountServiceImpl.class);

    private AccountDao accountDao;
    protected DateTimeService dateTimeService;
    protected DocumentTypeService documentTypeService;

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
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        keys.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        Account account = (Account) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Account.class, keys);

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
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        keys.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        return (Account) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Account.class, keys);
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getAccountsThatUserIsResponsibleFor(org.kuali.bo.user.KualiUser)
     */
    public List getAccountsThatUserIsResponsibleFor(Person person) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieving accountsResponsible list for user " + person.getName());
        }

        // gets the list of accounts that the user is the Fiscal Officer of
        List accountList = accountDao.getAccountsThatUserIsResponsibleFor(person, dateTimeService.getCurrentDate());
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
        return accountDao.determineUserResponsibilityOnAccount(kualiUser, account, dateTimeService.getCurrentSqlDate());
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getPrimaryDelegationByExample(org.kuali.kfs.coa.businessobject.AccountDelegate,
     *      java.lang.String)
     */

    public AccountDelegate getPrimaryDelegationByExample(AccountDelegate delegateExample, String totalDollarAmount) {
        String documentTypeName = delegateExample.getFinancialDocumentTypeCode();
        Date currentSqlDate = dateTimeService.getCurrentSqlDate();
        List primaryDelegations = filterAccountDelegates(delegateExample, accountDao.getPrimaryDelegationByExample(delegateExample, currentSqlDate, totalDollarAmount));
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
        return (AccountDelegate) primaryDelegations.iterator().next();
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getSecondaryDelegationsByExample(org.kuali.kfs.coa.businessobject.AccountDelegate,
     *      java.lang.String)
     */
    public List getSecondaryDelegationsByExample(AccountDelegate delegateExample, String totalDollarAmount) {
        Date currentSqlDate = dateTimeService.getCurrentSqlDate();
        List secondaryDelegations = accountDao.getSecondaryDelegationsByExample(delegateExample, currentSqlDate, totalDollarAmount);
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
    protected List<AccountDelegate> filterAccountDelegates(AccountDelegate delegateExample, List<AccountDelegate> accountDelegatesToFilterFrom) {
        String documentTypeName = delegateExample.getFinancialDocumentTypeCode();
        AccountDelegate delegate;
        List<AccountDelegate> filteredAccountDelegates = filterAccountDelegates(accountDelegatesToFilterFrom, documentTypeName);
        if (filteredAccountDelegates.size() == 0) {
            Set<String> potentialParentDocumentTypeNames = getPotentialParentDocumentTypeNames(accountDelegatesToFilterFrom);
            String closestParentDocumentTypeName = KimCommonUtils.getClosestParentDocumentTypeName(documentTypeService.findByName(documentTypeName), potentialParentDocumentTypeNames);
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
    protected List<AccountDelegate> filterAccountDelegates(List<AccountDelegate> delegations, String documentTypeNameToFilterOn) {
        AccountDelegate delegate;
        List<AccountDelegate> filteredSecondaryDelegations = new ArrayList<AccountDelegate>();
        for (Object delegateObject : delegations) {
            delegate = (AccountDelegate) delegateObject;
            if (StringUtils.equals(delegate.getFinancialDocumentTypeCode(), documentTypeNameToFilterOn)) {
                filteredSecondaryDelegations.add(delegate);
            }
        }
        return filteredSecondaryDelegations;
    }

    /**
     * This method gets a list of potential parent document type names by collecting the unique doc type names from the list of
     * account delegations
     * 
     * @param delegations
     * @return
     */
    protected Set<String> getPotentialParentDocumentTypeNames(List<AccountDelegate> delegations) {
        AccountDelegate delegate;
        Set<String> potentialParentDocumentTypeNames = new HashSet<String>();
        for (Object delegateObject : delegations) {
            delegate = (AccountDelegate) delegateObject;
            if (!potentialParentDocumentTypeNames.contains(delegate.getFinancialDocumentTypeCode()))
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
        return accountDao.getActiveAccountsForAccountSupervisor(principalId, dateTimeService.getCurrentSqlDate());
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getActiveAccountsForFiscalOfficer(java.lang.String)
     */
    public Iterator<Account> getActiveAccountsForFiscalOfficer(String principalId) {
        return accountDao.getActiveAccountsForFiscalOfficer(principalId, dateTimeService.getCurrentSqlDate());
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getExpiredAccountsForAccountSupervisor(java.lang.String)
     */
    public Iterator<Account> getExpiredAccountsForAccountSupervisor(String principalId) {
        return accountDao.getExpiredAccountsForAccountSupervisor(principalId, dateTimeService.getCurrentSqlDate());
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getExpiredAccountsForFiscalOfficer(java.lang.String)
     */
    public Iterator<Account> getExpiredAccountsForFiscalOfficer(String principalId) {
        return accountDao.getExpiredAccountsForFiscalOfficer(principalId, dateTimeService.getCurrentSqlDate());
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
     * @see org.kuali.kfs.coa.service.AccountService#getAccountsForAccountNumber(java.lang.String)
     */
    public Collection<Account> getAccountsForAccountNumber(String accountNumber) {
        return accountDao.getAccountsForAccountNumber(accountNumber);
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getUniqueAccountForAccountNumber(java.lang.String)
     */
    public Account getUniqueAccountForAccountNumber(String accountNumber) {
        Iterator<Account> accounts = accountDao.getAccountsForAccountNumber(accountNumber).iterator();
        Account account = null;
        // there should be only one account in the collection
        if (accounts.hasNext()) {
            account = (Account) accounts.next();
        }
        return account;
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#accountsCanCrossCharts()
     */
    public boolean accountsCanCrossCharts() {
        return SpringContext.getBean(ParameterService.class).getIndicatorParameter(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, SystemGroupParameterNames.ACCOUNTS_CAN_CROSS_CHARTS_IND);
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#accountsCanCrossCharts()
     */
    public void populateAccountingLineChartIfNeeded(AccountingLine line) {
        if (!accountsCanCrossCharts() /* && line.getChartOfAccountsCode() == null */) {
            Account account = getUniqueAccountForAccountNumber(line.getAccountNumber());
            if (ObjectUtils.isNotNull(account)) {
                line.setChartOfAccountsCode(account.getChartOfAccountsCode());
            }
        }
    }

    /**
     * @param accountDao The accountDao to set.
     */
    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    /**
     * Sets the dateTimeService.
     * 
     * @param dateTimeService
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the documentTypeService.
     * 
     * @param documentTypeService
     */
    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    /**
     * Gets documentTypeService
     */
    public DocumentTypeService getDocumentTypeService() {
        if (documentTypeService == null) {
            documentTypeService = SpringContext.getBean(DocumentTypeService.class);
        }
        return documentTypeService;
    }
    
}
