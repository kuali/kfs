/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.chart.rules;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.core.service.DocumentAuthorizationService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.service.AccountService;

/**
 * General PreRules checks for all Maintenance docs that needs to occur while still in the Struts processing.
 */
public class MaintenancePreRulesBase extends PreRulesContinuationBase {

    private KualiConfigurationService configService;
    private AccountService accountService;
    private DocumentAuthorizationService documentAuthorizationService;

    /**
     * Constructs a MaintenancePreRulesBase class and injects some services through setters
     * 
     * @TODO: should be fixed in the future to use Spring to inject these services
     */
    public MaintenancePreRulesBase() {
        // Pseudo-inject some services.
        //
        // This approach is being used to make it simpler to convert the Rule classes
        // to spring-managed with these services injected by Spring at some later date.
        // When this happens, just remove these calls to the setters with
        // SpringContext, and configure the bean defs for spring.
        setAccountService(SpringContext.getBean(AccountService.class));
        setConfigService(SpringContext.getBean(KualiConfigurationService.class));
        setDocumentAuthorizationService(SpringContext.getBean(DocumentAuthorizationService.class));
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setConfigService(KualiConfigurationService configService) {
        this.configService = configService;
    }

    /**
     * This is called from the rules service to execute our rules A hook is provided here for sub-classes to override the
     * {@link MaintenancePreRulesBase#doCustomPreRules(MaintenanceDocument)}
     * 
     * @see org.kuali.core.rules.PreRulesContinuationBase#doRules(org.kuali.core.document.Document)
     */
    public boolean doRules(Document document) {
        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
        return doCustomPreRules(maintenanceDocument);
    }

    /**
     * This is a hook for sub-classes to implement their own pre-rules. Override to get hooked into main class
     * 
     * @param maintenanceDocument
     * @return true if rules pass
     */
    protected boolean doCustomPreRules(MaintenanceDocument maintenanceDocument) {
        return true;
    }

    /**
     * This method checks for continuation accounts, returns the continuation account if it is found, null otherwise
     * 
     * @param accName
     * @param chart
     * @param accountNumber
     * @param accountName
     * @param allowExpiredAccount
     * @return the continuation account if it is found, null otherwise
     */
    protected Account checkForContinuationAccount(String accName, String chart, String accountNumber, String accountName, boolean allowExpiredAccount) {
        Account result = checkForContinuationAccount(accName, chart, accountNumber, accountName);
        if (!allowExpiredAccount) {
            if (result.isExpired()) {
                return null;
            }
        }
        return result;
    }

    /**
     * This method checks for continuation accounts and presents the user with a question regarding their use on this account.
     * 
     * @param accName
     * @param chart
     * @param accountNumber
     * @param accountName
     * @return
     */
    protected Account checkForContinuationAccount(String accName, String chart, String accountNumber, String accountName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("entering checkForContinuationAccounts(" + accountNumber + ")");
        }
        if (StringUtils.isBlank(accountNumber) || StringUtils.isBlank(chart))
            return null;

        Account account = accountService.getByPrimaryId(chart, accountNumber);

        if (ObjectUtils.isNotNull(account) && !account.isExpired()) { // no need for a continuation account
            return null;
        }

        boolean useContinuationAccount = true;

        while (ObjectUtils.isNotNull(account) && account.isExpired() && useContinuationAccount) {
            LOG.debug("Expired account: " + accountNumber);
            String continuationAccountNumber = account.getContinuationAccountNumber();

            useContinuationAccount = askOrAnalyzeYesNoQuestion("ContinuationAccount" + accName + accountNumber, buildContinuationConfirmationQuestion(accName, accountNumber, continuationAccountNumber));
            if (useContinuationAccount) {
                String continuationChart = account.getContinuationFinChrtOfAcctCd();
                account = accountService.getByPrimaryId(continuationChart, continuationAccountNumber);

                if (ObjectUtils.isNotNull(account)) {
                    accountNumber = account.getAccountNumber();
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Selected continuation account: " + account);
                }
            }
        }
        return account;

    }


    /**
     * This method builds up the continuation account confirmation question that will be presented to the user
     * 
     * @param accName
     * @param expiredAccount
     * @param continuationAccount
     * @return the question to the user about the continuation account
     */
    protected String buildContinuationConfirmationQuestion(String accName, String expiredAccount, String continuationAccount) {
        String result = configService.getPropertyString(KFSKeyConstants.QUESTION_CONTINUATION_ACCOUNT_SELECTION);
        result = StringUtils.replace(result, "{0}", accName);
        result = StringUtils.replace(result, "{1}", expiredAccount);
        result = StringUtils.replace(result, "{2}", continuationAccount);
        return result;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public KualiConfigurationService getConfigService() {
        return configService;
    }


    /**
     * Gets the documentAuthorizationService attribute.
     * 
     * @return Returns the documentAuthorizationService.
     */
    protected final DocumentAuthorizationService getDocumentAuthorizationService() {
        return documentAuthorizationService;
    }

    /**
     * Sets the documentAuthorizationService attribute value.
     * 
     * @param documentAuthorizationService The documentAuthorizationService to set.
     */
    public final void setDocumentAuthorizationService(DocumentAuthorizationService documentAuthorizationService) {
        this.documentAuthorizationService = documentAuthorizationService;
    }

}
