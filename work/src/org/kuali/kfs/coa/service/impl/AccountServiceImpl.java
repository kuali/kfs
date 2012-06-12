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

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.kfs.coa.dataaccess.AccountDao;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.cache.annotation.Cacheable;

/**
 * This class is the service implementation for the Account structure. This is the default, Kuali provided implementation.
 */

@NonTransactional
public class AccountServiceImpl implements AccountService {
    private static final Logger LOG = Logger.getLogger(AccountServiceImpl.class);

    protected ParameterService parameterService;
    protected AccountDao accountDao;
    protected DateTimeService dateTimeService;
    protected DocumentTypeService documentTypeService;
    protected BusinessObjectService businessObjectService;

    /**
     * Retrieves an Account object based on primary key.
     *
     * @param chartOfAccountsCode - Chart of Accounts Code
     * @param accountNumber - Account Number
     * @return Account
     * @see AccountService
     */
    @Override
    @Cacheable(value=Account.CACHE_NAME, key="#chartOfAccountsCode+'-'+#accountNumber")
    public Account getByPrimaryId(String chartOfAccountsCode, String accountNumber) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieving account by primaryId (" + chartOfAccountsCode + "," + accountNumber + ")");
        }
        Map<String, Object> keys = new HashMap<String, Object>(2);
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        keys.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        Account account = businessObjectService.findByPrimaryKey(Account.class, keys);

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
    @Override
    @Cacheable(value=Account.CACHE_NAME, key="#chartOfAccountsCode+'-'+#accountNumber")
    public Account getByPrimaryIdWithCaching(String chartOfAccountsCode, String accountNumber) {
        Account account = getByPrimaryId(chartOfAccountsCode, accountNumber);
        account.getChartOfAccounts().getChartOfAccountsCode();
        return account;
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getAccountsThatUserIsResponsibleFor(org.kuali.bo.user.KualiUser)
     */
    @Override
    @Cacheable(value=Account.CACHE_NAME, key="'ResponsibleForAccounts'+#person.principalId")
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
     * @see org.kuali.kfs.coa.service.AccountService#hasResponsibilityOnAccount(org.kuali.rice.kim.api.identity.Person,
     *      org.kuali.kfs.coa.businessobject.Account)
     */
    @Override
    @Cacheable(value=Account.CACHE_NAME, key="'ResponsibilityOnAccount'+#kualiUser.principalId+'-'+#account.chartOfAccountsCode+'-'+#account.accountNumber")
    public boolean hasResponsibilityOnAccount(Person kualiUser, Account account) {
        return accountDao.determineUserResponsibilityOnAccount(kualiUser, account, dateTimeService.getCurrentSqlDate());
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getPrimaryDelegationByExample(org.kuali.kfs.coa.businessobject.AccountDelegate,
     *      java.lang.String)
     */

    @Override
    public AccountDelegate getPrimaryDelegationByExample(AccountDelegate delegateExample, String totalDollarAmount) {
        String documentTypeName = delegateExample.getFinancialDocumentTypeCode();
        Date currentSqlDate = dateTimeService.getCurrentSqlDate();
        List<AccountDelegate> primaryDelegations = filterAccountDelegates(delegateExample, accountDao.getPrimaryDelegationByExample(delegateExample, currentSqlDate, totalDollarAmount));
        if (primaryDelegations.isEmpty()) {
            return null;
        }
        for (Iterator<AccountDelegate> iterator = primaryDelegations.iterator(); iterator.hasNext();) {
            AccountDelegate delegate = iterator.next();
            if (!KFSConstants.ROOT_DOCUMENT_TYPE.equals(delegate.getFinancialDocumentTypeCode())) {
                return delegate;
            }
        }
        return primaryDelegations.iterator().next();
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getSecondaryDelegationsByExample(org.kuali.kfs.coa.businessobject.AccountDelegate,
     *      java.lang.String)
     */
    @Override
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
        List<AccountDelegate> filteredAccountDelegates = filterAccountDelegates(accountDelegatesToFilterFrom, documentTypeName);
        if (filteredAccountDelegates.size() == 0) {
            Set<String> potentialParentDocumentTypeNames = getPotentialParentDocumentTypeNames(accountDelegatesToFilterFrom);
            String closestParentDocumentTypeName = KimCommonUtils.getClosestParentDocumentTypeName(documentTypeService.getDocumentTypeByName(documentTypeName), potentialParentDocumentTypeNames);
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
        List<AccountDelegate> filteredSecondaryDelegations = new ArrayList<AccountDelegate>();
        for (Object delegateObject : delegations) {
            AccountDelegate delegate = (AccountDelegate) delegateObject;
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
    @Override
    public Iterator getAllAccounts() {
        LOG.debug("getAllAccounts() started");

        Iterator accountIter = accountDao.getAllAccounts();
        // FIXME: this loads all accounts into memory - could blow server
        return IteratorUtils.toList(accountIter).iterator();
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getActiveAccountsForAccountSupervisor(java.lang.String)
     */
    @Override
    public Iterator<Account> getActiveAccountsForAccountSupervisor(String principalId) {
        return accountDao.getActiveAccountsForAccountSupervisor(principalId, dateTimeService.getCurrentSqlDate());
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getActiveAccountsForFiscalOfficer(java.lang.String)
     */
    @Override
    public Iterator<Account> getActiveAccountsForFiscalOfficer(String principalId) {
        return accountDao.getActiveAccountsForFiscalOfficer(principalId, dateTimeService.getCurrentSqlDate());
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getExpiredAccountsForAccountSupervisor(java.lang.String)
     */
    @Override
    public Iterator<Account> getExpiredAccountsForAccountSupervisor(String principalId) {
        return accountDao.getExpiredAccountsForAccountSupervisor(principalId, dateTimeService.getCurrentSqlDate());
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getExpiredAccountsForFiscalOfficer(java.lang.String)
     */
    @Override
    public Iterator<Account> getExpiredAccountsForFiscalOfficer(String principalId) {
        return accountDao.getExpiredAccountsForFiscalOfficer(principalId, dateTimeService.getCurrentSqlDate());
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#isPrincipalInAnyWayShapeOrFormAccountManager(java.lang.String)
     */
    @Override
    public boolean isPrincipalInAnyWayShapeOrFormAccountManager(String principalId) {
        return accountDao.isPrincipalInAnyWayShapeOrFormAccountManager(principalId);
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#isPrincipalInAnyWayShapeOrFormAccountSupervisor(java.lang.String)
     */
    @Override
    public boolean isPrincipalInAnyWayShapeOrFormAccountSupervisor(String principalId) {
        return accountDao.isPrincipalInAnyWayShapeOrFormAccountSupervisor(principalId);
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#isPrincipalInAnyWayShapeOrFormFiscalOfficer(java.lang.String)
     */
    @Override
    public boolean isPrincipalInAnyWayShapeOrFormFiscalOfficer(String principalId) {
        return accountDao.isPrincipalInAnyWayShapeOrFormFiscalOfficer(principalId);
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getAccountsForAccountNumber(java.lang.String)
     */
    @Override
    @Cacheable(value=Account.CACHE_NAME, key="'AccountsForAccountNumber'+#accountNumber")
    public Collection<Account> getAccountsForAccountNumber(String accountNumber) {
        return accountDao.getAccountsForAccountNumber(accountNumber);
    }


    @Override
    public String getDefaultLaborBenefitRateCategoryCodeForAccountType(String accountTypeCode) {
        String benefitRateCategory = parameterService.getSubParameterValueAsString(Account.class, "DEFAULT_BENEFIT_RATE_CATEGORY_CODE_BY_ACCOUNT_TYPE", accountTypeCode);
        if ( StringUtils.isBlank(benefitRateCategory) ) {
            benefitRateCategory = parameterService.getParameterValueAsString(Account.class, "DEFAULT_BENEFIT_RATE_CATEGORY_CODE");
        }
        return StringUtils.trimToEmpty(benefitRateCategory);
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#isFridgeBenefitCalculationEnable()
     */
    @Override
    public Boolean isFridgeBenefitCalculationEnable(){
        Boolean isFringeBeneCalcEnable = null;
        
        //make sure the parameter exists
        if(parameterService.parameterExists(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, "ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND")){
          //check the system param to see if the labor benefit rate category should be editable
            isFringeBeneCalcEnable = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, "ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND");
            LOG.debug("System Parameter retrieved: " + isFringeBeneCalcEnable);
        }
        
        return (Boolean)org.apache.commons.lang.ObjectUtils.defaultIfNull(isFringeBeneCalcEnable, false);
    }
    

    /**
     * @see org.kuali.kfs.coa.service.AccountService#getUniqueAccountForAccountNumber(java.lang.String)
     */
    @Override
    @Cacheable(value=Account.CACHE_NAME, key="'UniqueAccountForAccountNumber'+#accountNumber")
    public Account getUniqueAccountForAccountNumber(String accountNumber) {
        Iterator<Account> accounts = accountDao.getAccountsForAccountNumber(accountNumber).iterator();
        Account account = null;
        // there should be only one account in the collection
        if (accounts.hasNext()) {
            account = accounts.next();
        }
        return account;
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#accountsCanCrossCharts()
     */
    @Override
    public boolean accountsCanCrossCharts() {
        return parameterService.getParameterValueAsBoolean(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, SystemGroupParameterNames.ACCOUNTS_CAN_CROSS_CHARTS_IND);
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountService#accountsCanCrossCharts()
     */
    @Override
    public void populateAccountingLineChartIfNeeded(AccountingLine line) {
        if (!accountsCanCrossCharts() /* && line.getChartOfAccountsCode() == null */) {
            Account account = getUniqueAccountForAccountNumber(line.getAccountNumber());
            if (ObjectUtils.isNotNull(account)) {
                line.setChartOfAccountsCode(account.getChartOfAccountsCode());
            }
        }
    }

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
