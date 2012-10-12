/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineOverride;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupDefinition;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemTransactionalDocumentEntry;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;


import org.kuali.rice.krad.document.Document;
import org.kuali.rice.kns.rule.event.PromptBeforeValidationEvent;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.kns.service.DataDictionaryService;

import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;



import org.kuali.kfs.fp.service.AccountingDocumentPreRuleService;



/**
 * This service interface defines methods that a AccountingDocumentPreRuleService implementation must provide.
 */

public class AccountingDocumentPreRuleServiceImpl implements AccountingDocumentPreRuleService {
    protected static Logger LOG = Logger.getLogger(AccountingDocumentPreRuleServiceImpl.class);

    /**
     * Access the account override question for all accounting document
     * 
     * @param document
     * @param preRule
     * @return
     */
    public boolean expiredAccountOverrideQuestion(AccountingDocumentBase document, PromptBeforeValidationBase preRule, PromptBeforeValidationEvent event) {
        boolean tabStatesOK = true;
        List<AccountingLine> accountLineList = getOverrideQuestionAccount(document);
        if (accountLineList != null && !accountLineList.isEmpty()) {
            String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.QUESTION_NEED_OVERRIDE_ACCOUNT_FOR_EXPIRED);
            
            StringBuffer expiredAccounts = new StringBuffer();
            for (AccountingLine accountingLine : accountLineList) {
                expiredAccounts.append(accountingLine.getChartOfAccountsCode());
                expiredAccounts.append("-");
                expiredAccounts.append(accountingLine.getAccountNumber());
                expiredAccounts.append(" ");
                
            }
            questionText = StringUtils.replace(questionText, "{0}", expiredAccounts.toString());

            boolean overrideAccount = preRule.askOrAnalyzeYesNoQuestion(KFSConstants.OVERRIDE_ACCOUNT_FOR_EXPIRED_QUESTION_ID, questionText);

            if (overrideAccount) {
                Set overrideInputComponents = new HashSet();
                overrideInputComponents.add(AccountingLineOverride.COMPONENT.EXPIRED_ACCOUNT);
                for (AccountingLine accountingLine : accountLineList) {
                    setAccountOverride(document, accountingLine.getAccount(), AccountingLineOverride.valueOf(overrideInputComponents).getCode());
                }
            }
            else {
                // return to document if the user selects No
                event.setActionForwardName(KFSConstants.MAPPING_BASIC);
                tabStatesOK = false;
            }
        }
        return tabStatesOK;
    }

    /**
     * Set up override for all accounting line with the same account number
     * 
     * @param document
     * @param accountLine
     * @param code
     */
    protected void setAccountOverride(AccountingDocumentBase document, Account overrideAccount, String code) {
        List accountLinesFromDoc = new ArrayList<AccountingLine>();
        accountLinesFromDoc.addAll(document.getSourceAccountingLines());
        accountLinesFromDoc.addAll(document.getTargetAccountingLines());

        for (Iterator iter = accountLinesFromDoc.iterator(); iter.hasNext();) {
            AccountingLine currentLine = (AccountingLine) iter.next();

            if (overrideAccount.getChartOfAccountsCode().equals(currentLine.getChartOfAccountsCode()) && overrideAccount.getAccountNumber().equals(currentLine.getAccountNumber())) {
                currentLine.setOverrideCode(code);
            }
        }
    }

    /**
     * DTT-3163: Walk through all source and target accounting lines to identify if there is account which is expired and requires
     * approver to override but approver does not have the edit permission
     * 
     * @param document
     * @return
     */
    protected List<AccountingLine> getOverrideQuestionAccount(Document document) {
        final Person currentUser = GlobalVariables.getUserSession().getPerson();
        List<AccountingLine> questionAccounts = new ArrayList<AccountingLine>();
        HashMap questionAccountsMap = new HashMap<String, Object>();
        String accountKey = null;
        
        // expiration warning should be triggered only when document is enrouting and waiting for approval; accounting
        // line changed from active to inactive due to expiration date; the current approval does not have the permission on editing
        // accounting line
        if (ObjectUtils.isNotNull(document.getDocumentHeader())) {
            WorkflowDocument workflowDoc = document.getDocumentHeader().getWorkflowDocument();
            if (ObjectUtils.isNotNull(workflowDoc) && workflowDoc.isEnroute() && workflowDoc.isApprovalRequested()) {
                AccountingDocumentBase acctDoc = (AccountingDocumentBase) document;

                List accountLinesFromDoc = new ArrayList<AccountingLine>();
                accountLinesFromDoc.addAll(acctDoc.getSourceAccountingLines());
                accountLinesFromDoc.addAll(acctDoc.getTargetAccountingLines());

                Map<String, AccountingLineAuthorizer> authorizerMap = new HashMap<String, AccountingLineAuthorizer>();


                for (Iterator iter = accountLinesFromDoc.iterator(); iter.hasNext();) {
                    AccountingLine currentLine = (AccountingLine) iter.next();
                    accountKey = currentLine.getChartOfAccountsCode() + "-" + currentLine.getAccountNumber();
                    // if account is a known expired account, skip it.
                    if (questionAccountsMap.containsKey(accountKey)) {
                        continue;
                    }
                    
                    AccountingLineOverride override = AccountingLineOverride.valueOf(currentLine.getOverrideCode());

                    if (AccountingLineOverride.needsExpiredAccountOverride(currentLine.getAccount()) && !override.hasComponent(AccountingLineOverride.COMPONENT.EXPIRED_ACCOUNT)) {
                        // check if the approver has the permission
                        String groupName = getGroupName(currentLine);
                        AccountingLineAuthorizer accountingLineAuthorizer = authorizerMap.get(groupName);
                        if (accountingLineAuthorizer == null) {
                            accountingLineAuthorizer = lookupAccountingLineAuthorizer(currentLine, document, groupName);
                            authorizerMap.put(groupName, accountingLineAuthorizer);
                        }

                        if (accountingLineAuthorizer != null) {
                            boolean lineIsAccessible = accountingLineAuthorizer.hasEditPermissionOnAccountingLine(acctDoc, currentLine, getAccountingLineCollectionProperty(currentLine), currentUser, true);
                            boolean isAccessible = accountingLineAuthorizer.hasEditPermissionOnField(acctDoc, currentLine, getAccountingLineCollectionProperty(currentLine), KFSPropertyConstants.ACCOUNT_NUMBER, lineIsAccessible, true, currentUser);

                            if (!isAccessible) {
                                questionAccounts.add(currentLine);
                                questionAccountsMap.put(accountKey, currentLine);
                            }
                        }
                    }
                }

            }

        }

        return questionAccounts;
    }

    /**
     * Determines the property of the accounting line collection from the error prefixes
     * 
     * @return the accounting line collection property
     */
    protected String getAccountingLineCollectionProperty(AccountingLine account) {
        String propertyName = null;
        propertyName = account.isSourceAccountingLine() ? KFSConstants.PermissionAttributeValue.SOURCE_ACCOUNTING_LINES.value : KFSConstants.PermissionAttributeValue.TARGET_ACCOUNTING_LINES.value;

        if (propertyName.equals("newSourceLine"))
            return KFSConstants.PermissionAttributeValue.SOURCE_ACCOUNTING_LINES.value;
        if (propertyName.equals("newTargetLine"))
            return KFSConstants.PermissionAttributeValue.TARGET_ACCOUNTING_LINES.value;
        return propertyName;
    }

    /**
     * @param accountingLines
     * @return Map containing accountingLines from the given List, indexed by their sequenceNumber
     */
    protected Map buildAccountingLineMap(List accountingLines) {
        Map lineMap = new HashMap();

        for (Iterator i = accountingLines.iterator(); i.hasNext();) {
            AccountingLine accountingLine = (AccountingLine) i.next();
            Integer sequenceNumber = accountingLine.getSequenceNumber();

            lineMap.put(sequenceNumber, accountingLine);
        }

        return lineMap;
    }

    /**
     * @return hopefully, the best accounting line authorizer implementation to do the KIM check for to see if lines are accessible
     */
    protected AccountingLineAuthorizer lookupAccountingLineAuthorizer(AccountingLine account, Document document, String groupName) {
        Map<String, AccountingLineGroupDefinition> groups = ((FinancialSystemTransactionalDocumentEntry) SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDictionaryObjectEntry(document.getClass().getName())).getAccountingLineGroups();

        if (groups.isEmpty())
            return new AccountingLineAuthorizerBase(); // no groups? just use the default...
        if (groups.containsKey(groupName))
            return groups.get(groupName).getAccountingLineAuthorizer(); // we've got the group

        Set<String> groupNames = groups.keySet(); // we've got groups, just not the proper name; try our luck and get the
        // first group iterator
        Iterator<String> groupNameIterator = groupNames.iterator();
        String firstGroupName = groupNameIterator.next();
        return groups.get(firstGroupName).getAccountingLineAuthorizer();
    }

    /**
     * Returns the name of the accounting line group which holds the proper authorizer to do the KIM check
     * 
     * @return the name of the accouting line group to get the authorizer from
     */
    protected String getGroupName(AccountingLine line) {
        return (line.isSourceAccountingLine() ? KFSConstants.SOURCE_ACCOUNTING_LINES_GROUP_NAME : KFSConstants.TARGET_ACCOUNTING_LINES_GROUP_NAME);
    }
}
