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
package org.kuali.module.chart.dao.ojb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountResponsibility;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Delegate;
import org.kuali.module.chart.dao.AccountDao;

/**
 * This class is the OJB implementation of the AccountDao interface.
 */
public class AccountDaoOjb extends PlatformAwareDaoBaseOjb implements AccountDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountDaoOjb.class);

    private DateTimeService dateTimeService;

    /**
     * Retrieves account business object by primary key
     * 
     * @param chartOfAccountsCode - the FIN_COA_CD of the Chart Code that is part of the composite key of Account
     * @param accountNumber - the ACCOUNT_NBR part of the composite key of Accont
     * @return Account
     * @see AccountDao
     */
    public Account getByPrimaryId(String chartOfAccountsCode, String accountNumber) {
        LOG.debug("getByPrimaryId() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("accountNumber", accountNumber);

        return (Account) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(Account.class, criteria));
    }

    /**
     * fetch the accounts that the user is either the fiscal officer or a delegate of the fiscal officer
     * 
     * @param kualiUser
     * @return a list of Accounts that the user has responsibility for
     */
    public List getAccountsThatUserIsResponsibleFor(UniversalUser universalUser) {
        LOG.debug("getAccountsThatUserIsResponsibleFor() started");

        List accountResponsibilities = new ArrayList();
        accountResponsibilities.addAll(getFiscalOfficerResponsibilities(universalUser));
        accountResponsibilities.addAll(getDelegatedResponsibilities(universalUser));
        return accountResponsibilities;
    }

    /**
     * This method determines if the given user has any responsibilities on the given account
     * 
     * @param universalUser the user to check responsibilities for
     * @param account the account to check responsibilities on
     * @return true if user is somehow responsible for account, false if otherwise
     */
    public boolean determineUserResponsibilityOnAccount(UniversalUser universalUser, Account account) {
        boolean result = hasFiscalOfficerResponsibility(universalUser, account);
        if (!result) {
            result = hasDelegatedResponsibility(universalUser, account);
        }
        return result;
    }

    /**
     * Resolves the Primary Delegate for the given delegate example. If the primary delegate exists for a specific Document Type
     * Code and for a Document Type Code of "ALL", the delegate for the specific document type code is returned;
     * 
     * @see org.kuali.module.chart.dao.AccountDao#getPrimaryDelegationByExample(org.kuali.module.chart.bo.Delegate,
     *      java.lang.String)
     */
    public Delegate getPrimaryDelegationByExample(Delegate delegateExample, String totalDollarAmount) {
        Collection collection = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Delegate.class, getDelegateByExampleCriteria(delegateExample, totalDollarAmount, "Y")));
        if (collection.isEmpty()) {
            return null;
        }
        for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
            Delegate delegate = (Delegate) iterator.next();
            if (!"ALL".equals(delegate.getFinancialDocumentTypeCode())) {
                return delegate;
            }
        }
        return (Delegate) collection.iterator().next();
    }

    /**
     * @see org.kuali.module.chart.dao.AccountDao#getSecondaryDelegationsByExample(org.kuali.module.chart.bo.Delegate,
     *      java.lang.String)
     */
    public List getSecondaryDelegationsByExample(Delegate delegateExample, String totalDollarAmount) {
        return new ArrayList(getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Delegate.class, getDelegateByExampleCriteria(delegateExample, totalDollarAmount, "N"))));
    }

    /**
     * This method creates a {@link Criteria} based on {@link Delegate}, dollar amount and whether or not it is the primary
     * delegate
     * 
     * @param delegateExample
     * @param totalDollarAmount
     * @param accountsDelegatePrmrtIndicator
     * @return example {@link Delegate} {@link Criteria}
     */
    private Criteria getDelegateByExampleCriteria(Delegate delegateExample, String totalDollarAmount, String accountsDelegatePrmrtIndicator) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, delegateExample.getChartOfAccountsCode());
        criteria.addEqualTo(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, delegateExample.getAccountNumber());
        Criteria docTypeMatchCriteria = new Criteria();
        docTypeMatchCriteria.addEqualTo(KFSConstants.FINANCIAL_DOCUMENT_TYPE_CODE, delegateExample.getFinancialDocumentTypeCode());
        Criteria docTypeAllCriteria = new Criteria();
        docTypeAllCriteria.addEqualTo(KFSConstants.FINANCIAL_DOCUMENT_TYPE_CODE, "ALL");
        Criteria docTypeOrCriteria = new Criteria();
        docTypeOrCriteria.addOrCriteria(docTypeMatchCriteria);
        docTypeOrCriteria.addOrCriteria(docTypeAllCriteria);
        criteria.addAndCriteria(docTypeOrCriteria);
        criteria.addEqualTo("accountDelegateActiveIndicator", "Y");
        criteria.addLessOrEqualThan("accountDelegateStartDate", SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
        criteria.addEqualTo("accountsDelegatePrmrtIndicator", accountsDelegatePrmrtIndicator);
        if (totalDollarAmount != null) {
            Criteria totalDollarAmountInRangeCriteria = new Criteria();
            totalDollarAmountInRangeCriteria.addLessOrEqualThan("finDocApprovalFromThisAmt", totalDollarAmount);
            totalDollarAmountInRangeCriteria.addGreaterOrEqualThan("finDocApprovalToThisAmount", totalDollarAmount);
            Criteria totalDollarAmountZeroCriteria = new Criteria();
            totalDollarAmountZeroCriteria.addEqualTo("finDocApprovalToThisAmount", "0");
            totalDollarAmountZeroCriteria.addLessOrEqualThan("finDocApprovalFromThisAmt", totalDollarAmount);
            Criteria totalDollarAmountOrCriteria = new Criteria();
            totalDollarAmountOrCriteria.addOrCriteria(totalDollarAmountInRangeCriteria);
            totalDollarAmountOrCriteria.addOrCriteria(totalDollarAmountZeroCriteria);
            criteria.addAndCriteria(totalDollarAmountOrCriteria);
        }
        return criteria;
    }

    /**
     * method to get the fo responsibilities for the account
     * 
     * @param universalUser - fiscal officer to check for
     * @return list of {@link AccountResponsibility} for this fiscal officer
     */
    private List getFiscalOfficerResponsibilities(UniversalUser universalUser) {
        List fiscalOfficerResponsibilities = new ArrayList();
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountFiscalOfficerSystemIdentifier", universalUser.getPersonUniversalIdentifier());
        Collection accounts = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Account.class, criteria));
        for (Iterator iter = accounts.iterator(); iter.hasNext();) {
            Account account = (Account) iter.next();
            AccountResponsibility accountResponsibility = new AccountResponsibility(AccountResponsibility.FISCAL_OFFICER_RESPONSIBILITY, KualiDecimal.ZERO, KualiDecimal.ZERO, "", account);
            fiscalOfficerResponsibilities.add(accountResponsibility);
        }
        return fiscalOfficerResponsibilities;
    }

    /**
     * This method determines if a given user has fiscal officer responsiblity on a given account.
     * 
     * @param universalUser the user to check responsibilities for
     * @param account the account to check responsibilities on
     * @return true if user does have fiscal officer responsibility on account, false if otherwise
     */
    private boolean hasFiscalOfficerResponsibility(UniversalUser universalUser, Account account) {
        boolean hasFiscalOfficerResponsibility = false;
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountFiscalOfficerSystemIdentifier", universalUser.getPersonUniversalIdentifier());
        criteria.addEqualTo("chartOfAccountsCode", account.getChartOfAccountsCode());
        criteria.addEqualTo("accountNumber", account.getAccountNumber());
        Collection accounts = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Account.class, criteria));
        if (accounts != null && accounts.size() > 0) {
            Account retrievedAccount = (Account) accounts.iterator().next();
            if (ObjectUtils.isNotNull(retrievedAccount)) {
                hasFiscalOfficerResponsibility = true;
            }
        }
        return hasFiscalOfficerResponsibility;
    }

    /**
     * method to get the fo delegated responsibilities for the account
     * 
     * @param universalUser - user to check against
     * @return a list of {@link AccountResponsibility} objects for a delegate
     */
    private List getDelegatedResponsibilities(UniversalUser universalUser) {
        List delegatedResponsibilities = new ArrayList();
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountDelegateSystemId", universalUser.getPersonUniversalIdentifier());
        Collection accountDelegates = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Delegate.class, criteria));
        for (Iterator iter = accountDelegates.iterator(); iter.hasNext();) {
            Delegate accountDelegate = (Delegate) iter.next();
            if (accountDelegate.isAccountDelegateActiveIndicator()) {
                // the start_date should never be null in the real world, but
                // there is some test data that
                // contains null startDates, therefore this check.
                if (ObjectUtils.isNotNull(accountDelegate.getAccountDelegateStartDate())) {
                    if (!accountDelegate.getAccountDelegateStartDate().after(dateTimeService.getCurrentDate())) {
                        Account account = getByPrimaryId(accountDelegate.getChartOfAccountsCode(), accountDelegate.getAccount().getAccountNumber());
                        AccountResponsibility accountResponsibility = new AccountResponsibility(AccountResponsibility.DELEGATED_RESPONSIBILITY, accountDelegate.getFinDocApprovalFromThisAmt(), accountDelegate.getFinDocApprovalToThisAmount(), accountDelegate.getFinancialDocumentTypeCode(), account);
                        delegatedResponsibilities.add(accountResponsibility);
                    }
                }
            }
        }
        return delegatedResponsibilities;
    }

    /**
     * This method determines if a user has delegated responsibilities on a given account.
     * 
     * @param universalUser the user to check responsibilities for
     * @param account the account to check responsibilities on
     * @return true if user has delegated responsibilities
     */
    private boolean hasDelegatedResponsibility(UniversalUser universalUser, Account account) {
        boolean hasResponsibility = false;
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountDelegateSystemId", universalUser.getPersonUniversalIdentifier());
        criteria.addEqualTo("chartOfAccountsCode", account.getChartOfAccountsCode());
        criteria.addEqualTo("accountNumber", account.getAccountNumber());
        Collection accountDelegates = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Delegate.class, criteria));
        for (Iterator iter = accountDelegates.iterator(); iter.hasNext() && !hasResponsibility;) {
            Delegate accountDelegate = (Delegate) iter.next();
            if (accountDelegate.isAccountDelegateActiveIndicator()) {
                // the start_date should never be null in the real world, but
                // there is some test data that
                // contains null startDates, therefore this check.
                if (ObjectUtils.isNotNull(accountDelegate.getAccountDelegateStartDate())) {
                    if (!accountDelegate.getAccountDelegateStartDate().after(dateTimeService.getCurrentDate())) {
                        hasResponsibility = true;
                    }
                }
            }
        }
        return hasResponsibility;
    }

    /**
     * @see org.kuali.module.chart.dao.AccountDao#getAllAccounts()
     * @return an iterator for all accounts
     */
    public Iterator getAllAccounts() {
        LOG.debug("getAllAccounts() started");

        Criteria criteria = new Criteria();
        return getPersistenceBrokerTemplate().getIteratorByQuery(QueryFactory.newQuery(Account.class, criteria));
    }


    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}