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
package org.kuali.kfs.coa.dataaccess.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.kfs.coa.dataaccess.AccountDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountResponsibility;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class is the OJB implementation of the AccountDao interface.
 */
public class AccountDaoOjb extends PlatformAwareDaoBaseOjb implements AccountDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountDaoOjb.class);

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
    public List getAccountsThatUserIsResponsibleFor(Person person, java.util.Date currentDate) {
        LOG.debug("getAccountsThatUserIsResponsibleFor() started");

        List accountResponsibilities = new ArrayList();
        accountResponsibilities.addAll(getFiscalOfficerResponsibilities(person));
        accountResponsibilities.addAll(getDelegatedResponsibilities(person, currentDate));
        return accountResponsibilities;
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.AccountDao#determineUserResponsibilityOnAccount(org.kuali.rice.kim.api.identity.Person,
     *      org.kuali.kfs.coa.businessobject.Account, java.sql.Date)
     */
    public boolean determineUserResponsibilityOnAccount(Person person, Account account, Date currentSqlDate) {
        boolean result = hasFiscalOfficerResponsibility(person, account);
        if (!result) {
            result = hasDelegatedResponsibility(person, account, currentSqlDate);
        }
        return result;
    }

    /**
     * Resolves the Primary Delegate for the given delegate example. If the primary delegate exists for a specific Document Type
     * Code and for a Document Type Code of "KFS", the delegate for the specific document type code is returned;
     * 
     * @see org.kuali.kfs.coa.dataaccess.AccountDao#getPrimaryDelegationByExample(org.kuali.kfs.coa.businessobject.AccountDelegate,
     *      java.lang.String)
     */
    public List getPrimaryDelegationByExample(AccountDelegate delegateExample, Date currentSqlDate, String totalDollarAmount) {
        return new ArrayList(getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(AccountDelegate.class, getDelegateByExampleCriteria(delegateExample, currentSqlDate, totalDollarAmount, "Y"))));
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.AccountDao#getSecondaryDelegationsByExample(org.kuali.kfs.coa.businessobject.AccountDelegate,
     *      java.lang.String)
     */
    public List getSecondaryDelegationsByExample(AccountDelegate delegateExample, Date currentSqlDate, String totalDollarAmount) {
        return new ArrayList(getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(AccountDelegate.class, getDelegateByExampleCriteria(delegateExample, currentSqlDate, totalDollarAmount, "N"))));
    }

    /**
     * This method creates a {@link Criteria} based on {@link Delegate}, dollar amount and whether or not it is the primary delegate
     * 
     * @param delegateExample
     * @param totalDollarAmount
     * @param accountsDelegatePrmrtIndicator
     * @return example {@link Delegate} {@link Criteria}
     */
    protected Criteria getDelegateByExampleCriteria(AccountDelegate delegateExample, Date currentSqlDate, String totalDollarAmount, String accountsDelegatePrmrtIndicator) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, delegateExample.getChartOfAccountsCode());
        criteria.addEqualTo(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, delegateExample.getAccountNumber());
        criteria.addEqualTo("active", "Y");
        criteria.addLessOrEqualThan("accountDelegateStartDate", currentSqlDate);
        criteria.addEqualTo("accountsDelegatePrmrtIndicator", accountsDelegatePrmrtIndicator);
        if (totalDollarAmount != null) {
            // (toAmt is nullish and (fromAmt is nullish or fromAmt <= total)) or (fromAmt is nullish and (toAmt is nullish or toAmt
            // >= total)) or (fromAmt <= total and toAmount >= total)

            /* to not active clause: (toAmt is nullish and (fromAmt is nullish or fromAmt <= total)) */
            Criteria toAmountIsNullish = new Criteria();
            toAmountIsNullish.addIsNull(KFSPropertyConstants.FIN_DOC_APPROVAL_TO_THIS_AMOUNT);
            Criteria toAmountIsZero1 = new Criteria();
            toAmountIsZero1.addEqualTo(KFSPropertyConstants.FIN_DOC_APPROVAL_TO_THIS_AMOUNT, "0");
            toAmountIsNullish.addOrCriteria(toAmountIsZero1);

            Criteria fromMatchesClause = new Criteria();
            fromMatchesClause.addIsNull(KFSPropertyConstants.FIN_DOC_APPROVAL_FROM_THIS_AMT);
            Criteria fromAmountIsLessThanTotal = new Criteria();
            fromAmountIsLessThanTotal.addLessOrEqualThan(KFSPropertyConstants.FIN_DOC_APPROVAL_FROM_THIS_AMT, totalDollarAmount);
            fromMatchesClause.addOrCriteria(fromAmountIsLessThanTotal);

            Criteria toNotActiveClause = new Criteria();
            toNotActiveClause.addAndCriteria(toAmountIsNullish);
            toNotActiveClause.addAndCriteria(fromMatchesClause);

            /* from not active clause: (fromAmt is nullish and (toAmt is nullish or toAmt >= total)) */
            Criteria toMatchesClause = new Criteria();
            toMatchesClause.addIsNull(KFSPropertyConstants.FIN_DOC_APPROVAL_TO_THIS_AMOUNT);
            Criteria toAmountIsZero2 = new Criteria();
            toAmountIsZero2.addEqualTo(KFSPropertyConstants.FIN_DOC_APPROVAL_TO_THIS_AMOUNT, "0");
            toMatchesClause.addOrCriteria(toAmountIsZero2);
            Criteria toAmountIsGreaterThanTotal = new Criteria();
            toAmountIsGreaterThanTotal.addGreaterOrEqualThan(KFSPropertyConstants.FIN_DOC_APPROVAL_TO_THIS_AMOUNT, totalDollarAmount);
            toMatchesClause.addOrCriteria(toAmountIsGreaterThanTotal);

            Criteria fromIsNullClause = new Criteria();
            fromIsNullClause.addIsNull(KFSPropertyConstants.FIN_DOC_APPROVAL_FROM_THIS_AMT);
            Criteria fromIsZeroClause = new Criteria();
            fromIsZeroClause.addEqualTo(KFSPropertyConstants.FIN_DOC_APPROVAL_FROM_THIS_AMT, "0");
            Criteria fromIsNullishClause = new Criteria();
            fromIsNullishClause.addOrCriteria(fromIsNullClause);
            fromIsNullishClause.addOrCriteria(fromIsZeroClause);
            Criteria fromNotActiveClause = new Criteria();
            fromNotActiveClause.addAndCriteria(fromIsNullishClause);
            fromNotActiveClause.addAndCriteria(toMatchesClause);

            Criteria bothActive = new Criteria();
            bothActive.addLessOrEqualThan(KFSPropertyConstants.FIN_DOC_APPROVAL_FROM_THIS_AMT, totalDollarAmount);
            bothActive.addGreaterOrEqualThan(KFSPropertyConstants.FIN_DOC_APPROVAL_TO_THIS_AMOUNT, totalDollarAmount);

            Criteria totalDollarAmountCriteria = new Criteria();
            totalDollarAmountCriteria.addOrCriteria(toNotActiveClause);
            totalDollarAmountCriteria.addOrCriteria(fromNotActiveClause);
            totalDollarAmountCriteria.addOrCriteria(bothActive);

            criteria.addAndCriteria(totalDollarAmountCriteria);
        }
        return criteria;
    }

    /**
     * method to get the fo responsibilities for the account
     * 
     * @param person - fiscal officer to check for
     * @return list of {@link AccountResponsibility} for this fiscal officer
     */
    protected List getFiscalOfficerResponsibilities(Person person) {
        List fiscalOfficerResponsibilities = new ArrayList();
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountFiscalOfficerSystemIdentifier", person.getPrincipalId());
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
     * @param person the user to check responsibilities for
     * @param account the account to check responsibilities on
     * @return true if user does have fiscal officer responsibility on account, false if otherwise
     */
    protected boolean hasFiscalOfficerResponsibility(Person person, Account account) {
        boolean hasFiscalOfficerResponsibility = false;
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountFiscalOfficerSystemIdentifier", person.getPrincipalId());
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
     * @param person - user to check against
     * @return a list of {@link AccountResponsibility} objects for a delegate
     */
    protected List getDelegatedResponsibilities(Person person, java.util.Date currentDate) {
        List delegatedResponsibilities = new ArrayList();
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountDelegateSystemId", person.getPrincipalId());
        Collection accountDelegates = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(AccountDelegate.class, criteria));
        for (Iterator iter = accountDelegates.iterator(); iter.hasNext();) {
            AccountDelegate accountDelegate = (AccountDelegate) iter.next();
            if (accountDelegate.isActive()) {
                // the start_date should never be null in the real world, but
                // there is some test data that
                // contains null startDates, therefore this check.
                if (ObjectUtils.isNotNull(accountDelegate.getAccountDelegateStartDate())) {
                    if (!accountDelegate.getAccountDelegateStartDate().after(currentDate)) {
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
     * @param person the user to check responsibilities for
     * @param account the account to check responsibilities on
     * @return true if user has delegated responsibilities
     */
    protected boolean hasDelegatedResponsibility(Person person, Account account, java.util.Date currentDate) {
        boolean hasResponsibility = false;
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountDelegateSystemId", person.getPrincipalId());
        criteria.addEqualTo("chartOfAccountsCode", account.getChartOfAccountsCode());
        criteria.addEqualTo("accountNumber", account.getAccountNumber());
        Collection accountDelegates = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(AccountDelegate.class, criteria));
        for (Iterator iter = accountDelegates.iterator(); iter.hasNext() && !hasResponsibility;) {
            AccountDelegate accountDelegate = (AccountDelegate) iter.next();
            if (accountDelegate.isActive()) {
                // the start_date should never be null in the real world, but
                // there is some test data that
                // contains null startDates, therefore this check.
                if (ObjectUtils.isNotNull(accountDelegate.getAccountDelegateStartDate())) {
                    if (!accountDelegate.getAccountDelegateStartDate().after(currentDate)) {
                        hasResponsibility = true;
                    }
                }
            }
        }
        return hasResponsibility;
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.AccountDao#getAllAccounts()
     * @return an iterator for all accounts
     */
    public Iterator getAllAccounts() {
        LOG.debug("getAllAccounts() started");

        Criteria criteria = new Criteria();
        return getPersistenceBrokerTemplate().getIteratorByQuery(QueryFactory.newQuery(Account.class, criteria));
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.AccountDao#getActiveAccountsForAccountSupervisor(java.lang.String, java.sql.Date)
     */
    public Iterator<Account> getActiveAccountsForAccountSupervisor(String principalId, Date currentSqlDate) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountsSupervisorySystemsIdentifier", principalId);
        criteria.addEqualTo("active", true);
        criteria.addAndCriteria(getAccountNotExpiredCriteria(currentSqlDate));
        return (Iterator<Account>) getPersistenceBrokerTemplate().getIteratorByQuery(QueryFactory.newQuery(Account.class, criteria));
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.AccountDao#getActiveAccountsForFiscalOfficer(java.lang.String)
     */
    public Iterator<Account> getActiveAccountsForFiscalOfficer(String principalId, Date currentSqlDate) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountFiscalOfficerSystemIdentifier", principalId);
        criteria.addEqualTo("active", true);
        criteria.addAndCriteria(getAccountNotExpiredCriteria(currentSqlDate));
        return (Iterator<Account>) getPersistenceBrokerTemplate().getIteratorByQuery(QueryFactory.newQuery(Account.class, criteria));
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.AccountDao#getExpiredAccountsForAccountSupervisor(java.lang.String)
     */
    public Iterator<Account> getExpiredAccountsForAccountSupervisor(String principalId, Date currentSqlDate) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountsSupervisorySystemsIdentifier", principalId);
        criteria.addEqualTo("active", true);
        criteria.addAndCriteria(getAccountExpiredCriteria(currentSqlDate));
        return (Iterator<Account>) getPersistenceBrokerTemplate().getIteratorByQuery(QueryFactory.newQuery(Account.class, criteria));
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.AccountDao#getExpiredAccountsForFiscalOfficer(java.lang.String)
     */
    public Iterator<Account> getExpiredAccountsForFiscalOfficer(String principalId, Date currentSqlDate) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountFiscalOfficerSystemIdentifier", principalId);
        criteria.addEqualTo("active", true);
        criteria.addAndCriteria(getAccountExpiredCriteria(currentSqlDate));
        return (Iterator<Account>) getPersistenceBrokerTemplate().getIteratorByQuery(QueryFactory.newQuery(Account.class, criteria));
    }

    /**
     * Builds a criteria to find expired accounts
     * 
     * @return a Criteria for expired accounts
     */
    protected Criteria getAccountExpiredCriteria(Date currentSqlDate) {
        Criteria criteria = new Criteria();
        criteria.addNotNull("accountExpirationDate");
        criteria.addLessOrEqualThan("accountExpirationDate", currentSqlDate);
        return criteria;
    }

    /**
     * Builds a criteria to find non-expired accounts
     * 
     * @return a Criteria for non-expired accounts
     */
    protected Criteria getAccountNotExpiredCriteria(Date currentSqlDate) {
        Criteria criteria = new Criteria();
        criteria.addIsNull("accountExpirationDate");

        Criteria notYetExpiredCriteria = new Criteria();
        notYetExpiredCriteria.addGreaterThan("accountExpirationDate", currentSqlDate);

        criteria.addOrCriteria(notYetExpiredCriteria);
        return criteria;
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.AccountDao#isPrincipalInAnyWayShapeOrFormAccountManager(java.lang.String)
     */
    public boolean isPrincipalInAnyWayShapeOrFormAccountManager(String principalId) {
        return queryPrincipalHasAccountRole(principalId, "accountManagerSystemIdentifier");
    }

    /**
     * Determines if any non-closed accounts exist where the principal id is in the role of the role name
     * 
     * @param principalId the principal id to check
     * @param principalRoleName the name of the field on account to check for the principal id in
     * @return true if the principal has that account role, false otherwise
     */
    protected boolean queryPrincipalHasAccountRole(String principalId, String principalRoleName) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(principalRoleName, principalId);
        criteria.addEqualTo("active", "Y");

        ReportQueryByCriteria reportQuery = QueryFactory.newReportQuery(Account.class, criteria);
        reportQuery.setAttributes(new String[] { "count(*)" });

        int resultCount = 0;
        Iterator iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportQuery);
        while (iter.hasNext()) {
            final Object[] results = (Object[]) iter.next();
            resultCount = (results[0] instanceof Number) ? ((Number) results[0]).intValue() : new Integer(results[0].toString()).intValue();
        }
        return resultCount > 0;
    }


    /**
     * @see org.kuali.kfs.coa.dataaccess.AccountDao#isPrincipalInAnyWayShapeOrFormAccountSupervisor(java.lang.String)
     */
    public boolean isPrincipalInAnyWayShapeOrFormAccountSupervisor(String principalId) {
        return queryPrincipalHasAccountRole(principalId, "accountsSupervisorySystemsIdentifier");
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.AccountDao#isPrincipalInAnyWayShapeOrFormFiscalOfficer(java.lang.String)
     */
    public boolean isPrincipalInAnyWayShapeOrFormFiscalOfficer(String principalId) {
        return queryPrincipalHasAccountRole(principalId, "accountFiscalOfficerSystemIdentifier");
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.AccountDao#getAccountsForAccountNumber(java.lang.String)
     */
    public Collection<Account> getAccountsForAccountNumber(String accountNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Account.class, criteria));
    }
}
