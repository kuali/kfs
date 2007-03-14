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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.Constants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.AccountResponsibility;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Delegate;
import org.kuali.module.chart.dao.AccountDao;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * This class is the OJB implementation of the AccountDao interface.
 * 
 * 
 */

public class AccountDaoOjb extends PersistenceBrokerDaoSupport implements AccountDao {
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
     * @see org.kuali.module.chart.dao.AccountDao#getPrimaryDelegationByExample(org.kuali.module.chart.bo.Delegate,
     *      java.lang.String)
     */
    public Delegate getPrimaryDelegationByExample(Delegate delegateExample, String totalDollarAmount) {
        return (Delegate) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(Delegate.class, getDelegateByExampleCriteria(delegateExample, totalDollarAmount, "Y")));
    }

    /**
     * @see org.kuali.module.chart.dao.AccountDao#getSecondaryDelegationsByExample(org.kuali.module.chart.bo.Delegate,
     *      java.lang.String)
     */
    public List getSecondaryDelegationsByExample(Delegate delegateExample, String totalDollarAmount) {
        return new ArrayList(getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Delegate.class, getDelegateByExampleCriteria(delegateExample, totalDollarAmount, "N"))));
    }

    private Criteria getDelegateByExampleCriteria(Delegate delegateExample, String totalDollarAmount, String accountsDelegatePrmrtIndicator) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, delegateExample.getChartOfAccountsCode());
        criteria.addEqualTo(Constants.ACCOUNT_NUMBER_PROPERTY_NAME, delegateExample.getAccountNumber());
        Criteria docTypeMatchCriteria = new Criteria();
        docTypeMatchCriteria.addEqualTo(Constants.FINANCIAL_DOCUMENT_TYPE_CODE, delegateExample.getFinancialDocumentTypeCode());
        Criteria docTypeAllCriteria = new Criteria();
        docTypeAllCriteria.addEqualTo(Constants.FINANCIAL_DOCUMENT_TYPE_CODE, "ALL");
        Criteria docTypeOrCriteria = new Criteria();
        docTypeOrCriteria.addOrCriteria(docTypeMatchCriteria);
        docTypeOrCriteria.addOrCriteria(docTypeAllCriteria);
        criteria.addAndCriteria(docTypeOrCriteria);
        criteria.addEqualTo("accountDelegateActiveIndicator", "Y");
        criteria.addLessOrEqualThan("accountDelegateStartDate", SpringServiceLocator.getDateTimeService().getCurrentTimestamp());
        criteria.addEqualTo("accountsDelegatePrmrtIndicator", accountsDelegatePrmrtIndicator);
        if (totalDollarAmount != null) {
            Criteria totalDollarAmountInRangeCriteria = new Criteria();
            totalDollarAmountInRangeCriteria.addLessOrEqualThan("finDocApprovalFromThisAmt", totalDollarAmount);
            totalDollarAmountInRangeCriteria.addGreaterOrEqualThan("finDocApprovalToThisAmount", totalDollarAmount);
            Criteria totalDollarAmountZeroCriteria = new Criteria();
            totalDollarAmountZeroCriteria.addEqualTo("finDocApprovalToThisAmount", "0");
            Criteria totalDollarAmountOrCriteria = new Criteria();
            totalDollarAmountOrCriteria.addOrCriteria(totalDollarAmountInRangeCriteria);
            totalDollarAmountOrCriteria.addOrCriteria(totalDollarAmountZeroCriteria);
            criteria.addAndCriteria(totalDollarAmountOrCriteria);
        }
        return criteria;
    }

    /**
     * method to get the fo responsibilities for the account
     */
    private List getFiscalOfficerResponsibilities(UniversalUser universalUser) {
        List fiscalOfficerResponsibilities = new ArrayList();
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountFiscalOfficerSystemIdentifier", universalUser.getPersonUniversalIdentifier());
        Collection accounts = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Account.class, criteria));
        for (Iterator iter = accounts.iterator(); iter.hasNext();) {
            Account account = (Account) iter.next();
            AccountResponsibility accountResponsibility = new AccountResponsibility(AccountResponsibility.FISCAL_OFFICER_RESPONSIBILITY, new KualiDecimal("0"), new KualiDecimal("0"), "", account);
            fiscalOfficerResponsibilities.add(accountResponsibility);
        }
        return fiscalOfficerResponsibilities;
    }

    /**
     * method to get the fo delegated responsibilities for the account
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

    public Iterator getAllAccounts() {
        LOG.debug("getAllAccounts() started");

        Criteria criteria = new Criteria();
        return getPersistenceBrokerTemplate().getIteratorByQuery(QueryFactory.newQuery(Account.class, criteria));
    }
    

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}