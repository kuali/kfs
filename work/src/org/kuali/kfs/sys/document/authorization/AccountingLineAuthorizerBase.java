/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.authorization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.rice.kim.bo.Person;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.web.AccountingLineViewAction;
import org.kuali.kfs.sys.document.workflow.KualiWorkflowUtils.RouteLevelNames;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * The default implementation of AccountingLineAuthorizer
 */
public class AccountingLineAuthorizerBase implements AccountingLineAuthorizer {
    
    /**
     * Returns the basic actions - add for new lines, delete and balance inquiry for existing lines
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer#getActions(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String, boolean, java.lang.String)
     */
    public List<AccountingLineViewAction> getActions(AccountingDocument accountingDocument, AccountingLine line, String accountingLineProperty, Integer accountingLineIndex, Person currentUser, Map editModesForDocument, String groupTitle) {
        List<AccountingLineViewAction> actions = new ArrayList<AccountingLineViewAction>();

        String editMode = this.getEditModeForAccountingLine(accountingDocument, line, (accountingLineIndex == null), currentUser, editModesForDocument);        
        if (AuthorizationConstants.EditMode.FULL_ENTRY.equals(editMode)) {
            String riceImagesPath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString("kr.externalizable.images.url");
            String kfsImagesPath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString("externalizable.images.url");
            
            if (accountingLineIndex == null || accountingLineIndex.intValue() < 0) {
                actions.add(new AccountingLineViewAction(getAddMethod(line, accountingLineProperty), getAddLabel(groupTitle), riceImagesPath+"tinybutton-add1.gif"));
            } else {
                String groupName = getActionInfixForExtantAccountingLine(line, accountingLineProperty);
                actions.add(new AccountingLineViewAction(getDeleteLineMethod(line, accountingLineProperty, accountingLineIndex), getDeleteLineLabel(accountingLineIndex, groupTitle), riceImagesPath+"tinybutton-delete1.gif"));
                actions.add(new AccountingLineViewAction(getBalanceInquiryMethod(line, accountingLineProperty, accountingLineIndex), getBalanceInquiryLabel(accountingLineIndex, groupTitle), kfsImagesPath+"tinybutton-balinquiry.gif"));
            }
        }
        
        return actions;
    }
    
    /**
     * Builds the action method name of the method that adds accounting lines for this group
     * @param accountingLine the accounting line an action is being checked for
     * @param accountingLinePropertyName the property name of the accounting line
     * @return the action method name of the method that adds accounting lines for this group
     */
    protected String getAddMethod(AccountingLine accountingLine, String accountingLineProperty) {
        final String infix = getActionInfixForNewAccountingLine(accountingLine, accountingLineProperty);
        return "insert"+infix+"Line.anchoraccounting"+infix+"Anchor";
    }
    
    /**
     * Builds the label for the button that adds accounting lines to this group
     * @param groupTitle title of the group from the data dictionary
     * @return the label for the button that adds accounting lines to this group
     */
    protected String getAddLabel(String groupTitle) {
        return "Add "+groupTitle+" Accounting Line";
    }
    
    /**
     * Builds the action method name of the method that deletes accounting lines for this group
     * @param accountingLine the accounting line an action is being checked for
     * @param accountingLinePropertyName the property name of the accounting line
     * @param accountingLineIndex the index of the given accounting line within the the group being rendered
     * @return the action method name of the method that deletes accounting lines for this group
     */
    protected String getDeleteLineMethod(AccountingLine accountingLine, String accountingLineProperty, Integer accountingLineIndex) {
        final String infix = getActionInfixForExtantAccountingLine(accountingLine, accountingLineProperty);
        return "delete"+infix+"Line.line"+accountingLineIndex.toString()+".anchoraccounting"+infix+"Anchor";
    }
    
    /**
     * Builds the label for the buttons that delete accounting lines in this group
     * @param accountingLineIndex the index of the given accounting line within the the group being rendered
     * @param groupTitle title of the group from the data dictionary
     * @return the label for the button that deletes accounting lines in this group
     */
    protected String getDeleteLineLabel(Integer accountingLineIndex, String groupTitle) {
        return "Delete "+groupTitle+" Accounting Line "+(accountingLineIndex.intValue()+1);
    }
    
    /**
     * Builds the action method name of the method that performs a balance inquiry on accounting lines for this group
     * @param accountingLine the accounting line an action is being checked for
     * @param accountingLinePropertyName the property name of the accounting line
     * @param accountingLineIndex the index of the given accounting line within the the group being rendered
     * @return the action method name of the method that performs a balance inquiry on accounting lines for this group
     */
    protected String getBalanceInquiryMethod(AccountingLine accountingLine, String accountingLineProperty, Integer accountingLineIndex) {
        final String infix = getActionInfixForNewAccountingLine(accountingLine, accountingLineProperty);
        return "performBalanceInquiryFor"+infix+"Line.line"+accountingLineIndex.toString()+".anchoraccounting"+infix+"existingLineLineAnchor"+accountingLineIndex.toString();
    }
    
    /**
     * Builds the label for the buttons that perform balance inquiries accounting lines in this group
     * @param accountingLineIndex the index of the given accounting line within the the group being rendered
     * @param groupTitle title of the group from the data dictionary
     * @return the label for the buttons that perform balance inquiries accounting lines in this group
     */
    protected String getBalanceInquiryLabel(Integer accountingLineIndex, String groupTitle) {
        return "Perform Balance Inquiry for "+groupTitle+" Accounting Line "+(accountingLineIndex.intValue()+1);
    }
    
    /**
     * Gets the "action infix" for the given accounting line, so that the action knows it is supposed to add to source vs. target
     * @param accountingLine the accounting line an action is being checked for
     * @param accountingLinePropertyName the property name of the accounting line
     * @return the name of the action infix
     */
    protected String getActionInfixForNewAccountingLine(AccountingLine accountingLine, String accountingLinePropertyName) {
        if (accountingLine.isSourceAccountingLine()) return KFSConstants.SOURCE;
        if (accountingLine.isTargetAccountingLine()) return KFSConstants.TARGET;
        return KFSConstants.EMPTY_STRING;
    }
    
    /**
     * Gets the "action infix" for the given accounting line which already exists on the document, so that the action knows it is supposed to add to source vs. target
     * @param accountingLine the accounting line an action is being checked for
     * @param accountingLinePropertyName the property name of the accounting line
     * @return the name of the action infix
     */
    protected String getActionInfixForExtantAccountingLine(AccountingLine accountingLine, String accountingLinePropertyName) {
        if (accountingLine.isSourceAccountingLine()) return KFSConstants.SOURCE;
        if (accountingLine.isTargetAccountingLine()) return KFSConstants.TARGET;
        return KFSConstants.EMPTY_STRING;
    }

    /**
     * Returns a new empty HashSet
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer#getReadOnlyBlocks(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String, boolean)
     */
    public Set<String> getReadOnlyBlocks(AccountingDocument accountingDocument, AccountingLine accountingLine, boolean newLine, Person currentUser) {
        return new HashSet<String>();
    }

    /**
     * Returns a new empty HashSet
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer#getUnviewableBlocks(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String, boolean)
     */
    public Set<String> getUnviewableBlocks(AccountingDocument accountingDocument, AccountingLine accountingLine, boolean newLine, Person currentUser) {
        return new HashSet<String>();
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer#renderNewLine(org.kuali.kfs.sys.document.AccountingDocument, java.lang.String)
     */
    public boolean renderNewLine(AccountingDocument accountingDocument, String accountingGroupProperty, Person currentUser) {
        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer#editModeForAccountingLine(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, boolean, org.kuali.rice.kim.bo.Person, java.util.Map)
     */
    public String getEditModeForAccountingLine(AccountingDocument accountingDocument, AccountingLine accountingLine, boolean newLine, Person currentUser, Map<String, String> editModesForDocument) {
        if (editModesForDocument.containsKey(AuthorizationConstants.EditMode.UNVIEWABLE)) {
            return AuthorizationConstants.EditMode.UNVIEWABLE;
        }
        
        final boolean isAccountingLineEditable = this.isAccountingLineEditable(accountingDocument, accountingLine, currentUser);
        if (editModesForDocument.containsKey(AuthorizationConstants.EditMode.FULL_ENTRY) || isAccountingLineEditable) {
            return AuthorizationConstants.EditMode.FULL_ENTRY;
        }
        
        return AuthorizationConstants.EditMode.VIEW_ONLY;
    }

    /**
     * If the account does not exist then it's editable (so the current user can correct it!!) or if the user has responsiblity on the account, it's editable; it's not editable otherwise
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer#isAccountLineEditable(org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.rice.kim.bo.Person)
     */
    protected boolean isAccountingLineEditable(AccountingDocument document, AccountingLine accountingLine, Person currentUser) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        
        if (workflowDocument.stateIsCanceled() || (((FinancialSystemDocumentHeader)document.getDocumentHeader()).getFinancialDocumentInErrorNumber() != null)) {
            return false;  // are we cancelled or in error? then we're read only
        }
        
        if ((workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) && workflowDocument.userIsInitiator(currentUser)) {
            return true; // the state is initiated or saved and we created the doc?  then we get to edit everything
        }
        
        try {
            if (workflowDocument.stateIsEnroute()) {
                List<String> currentRouteLevels = (List<String>)Arrays.asList(workflowDocument.getNodeNames());
                
                if (currentRouteLevels.contains(RouteLevelNames.ORG_REVIEW)) {
                    return isAccountingLineEditableOnOrgReview(document, accountingLine, currentUser);
                }
                
                if (currentRouteLevels.contains(RouteLevelNames.ACCOUNT_REVIEW)) {
                    return isAccountingLineEditableOnAccountReview(document, accountingLine, currentUser);
                }
            }
        }
        catch (WorkflowException we) {
            throw new IllegalStateException("Workflow document in some illegal state", we);
        }
        
        return false; // default?  you can't edit the line none!
    }
    
    /**
     * Determines if the accounting line can be edited at org review level.  The default implementation always returns false
     * @param document the accounting document the accounting line to check lives on
     * @param accountingLine the accounting line to check
     * @param currentUser the user who is viewing this accounting line
     * @return true if the line is editable, false otherwise
     */
    protected boolean isAccountingLineEditableOnOrgReview(AccountingDocument document, AccountingLine accountingLine, Person currentUser) {
        return false;
    }
    
    /**
     * Determines if the accounting line can be edited at account review level.  The default implementation makes sure that the user is responsible - either fiscal officer
     * or account supervisor - on the account.
     * @param document the accounting document the accounting line to check lives on
     * @param accountingLine the accounting line to check
     * @param currentUser the user who is viewing this accounting line
     * @return true if the line is editable, false otherwise
     */
    protected boolean isAccountingLineEditableOnAccountReview(AccountingDocument document, AccountingLine accountingLine, Person currentUser) {
        final AccountService accountService = SpringContext.getBean(AccountService.class);
        final Account acct = accountService.getByPrimaryId(accountingLine.getChartOfAccountsCode(), accountingLine.getAccountNumber());
        
        if (ObjectUtils.isNull(acct)) return true; // the account doesn't exist?  whoever was editing it made a mistake - let them fix it
        if (accountService.hasResponsibilityOnAccount(currentUser, acct)) return true; // we own the account? then we can do whatever we want
        return false;
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer#isGroupReadOnly(org.kuali.kfs.sys.document.AccountingDocument, java.lang.String, org.kuali.rice.kim.bo.Person, java.util.Map)
     */
    public boolean isGroupReadOnly(AccountingDocument accountingDocument, String accountingLineCollectionProperty, Person currentUser, Map<String, String> editModesForDocument) {
        return editModesForDocument.containsKey(AuthorizationConstants.EditMode.VIEW_ONLY);
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer#getEditableBlocksInReadOnlyLine(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.rice.kim.bo.Person)
     */
    public Set<String> getEditableBlocksInReadOnlyLine(AccountingDocument accountingDocument, AccountingLine accountingLine, Person currentUser) {
        return new HashSet<String>();
    }
}

