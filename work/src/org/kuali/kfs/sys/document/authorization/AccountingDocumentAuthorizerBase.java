/*
 * Copyright 2007 The Kuali Foundation.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.workflow.KualiWorkflowUtils.RouteLevelNames;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.TransactionalDocument;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * DocumentAuthorizer containing common, reusable document-level authorization code for financial (i.e. Transactional) documents
 */
public class AccountingDocumentAuthorizerBase extends FinancialSystemTransactionalDocumentAuthorizerBase implements AccountingDocumentAuthorizer {
    private static Log LOG = LogFactory.getLog(AccountingDocumentAuthorizerBase.class);
// TODO fix for kim
//    /**
//     * The Kuali user interface code automatically reconstructs a complete document from a form's contents when a form is submitted,
//     * even if that form's contents are invalid (specifically, the Source and TargetAccountingLines from the form are used even if
//     * they contain invalid accountNumbers). Since the accounts referenced in the source and targetAccountingLines are used to
//     * determine editMode, when a) the document is enroute; b) the user is an FO, and receives the document for approval; and c) the
//     * user changes the accountNumbers on all accountingLines for which they are FO to something different or invalid, the form
//     * builds a Document for which the user is FO of none of the accountingLines, which makes the editMode computation incorrectly
//     * shift from EXPENSE_ENTRY to ACCOUNT_REVIEW, which makes it impossible for the user to restore the lines which they just
//     * rendered invalid. The simplest way to avoid that is to allow the UI to pass in the baselineSource and target accountingLines,
//     * so that the editability can be determined from them (since they are, by definition, valid and authoritative).
//     * </p>
//     * Note that document types which route straight to final will get an edit mode of VIEW_ONLY or FULL_ENTRY, never EXPENSE_ENTRY,
//     * because even if the state is briefly enroute, the route level is never ORG_REVIEW or ACCOUNT_REVIEW.
//     * 
//     * @see org.kuali.module.financial.document.authorization.FinancialDocumentAuthorizer#getEditMode(org.kuali.rice.kns.document.Document,
//     *      org.kuali.rice.kim.bo.Person, java.util.List, java.util.List)
//     */
//    @Override
//    public Map getEditMode(Document document, Person user) {
//        Person financialSystemUser = user;
//
//        String editMode = KfsAuthorizationConstants.TransactionalEditMode.VIEW_ONLY;
//
//        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
//
//        if (workflowDocument.stateIsCanceled() || (((FinancialSystemDocumentHeader)document.getDocumentHeader()).getFinancialDocumentInErrorNumber() != null)) {
//            editMode = KfsAuthorizationConstants.TransactionalEditMode.VIEW_ONLY;
//        }
//        else if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
//            if (workflowDocument.userIsInitiator(user)) {
//                editMode = KfsAuthorizationConstants.TransactionalEditMode.FULL_ENTRY;
//            }
//        }
//        else if (workflowDocument.stateIsEnroute()) {
//            List currentRouteLevels = getCurrentRouteLevels(workflowDocument);
//
//            if (currentRouteLevels.contains(RouteLevelNames.ORG_REVIEW)) {
//                // The routing level should be linear for Kuali, i.e., assert currentRouteLevels.size() == 1,
//                // but in case it becomes parallel, don't allow an account review while an org review is underway.
//                editMode = KfsAuthorizationConstants.TransactionalEditMode.VIEW_ONLY;
//            }
//            else if (currentRouteLevels.contains(RouteLevelNames.ACCOUNT_REVIEW)) {
//                List lineList = new ArrayList();
//                lineList.addAll(((AccountingDocument)document).getSourceAccountingLines());
//                lineList.addAll(((AccountingDocument)document).getTargetAccountingLines());
//
//                if (workflowDocument.isApprovalRequested() && userOwnsAnyAccountingLine(financialSystemUser, lineList)) {
//                    editMode = KfsAuthorizationConstants.TransactionalEditMode.EXPENSE_ENTRY;
//                }
//            }
//        }
//
//        Map editModeMap = new HashMap();
//        editModeMap.put(editMode, "TRUE");
//
//        return editModeMap;
//    }

    /**
     * A helper method for determining the route levels for a given document.
     * 
     * @param workflowDocument
     * @return List
     */
    protected static List getCurrentRouteLevels(KualiWorkflowDocument workflowDocument) {
        try {
            return Arrays.asList(workflowDocument.getNodeNames());
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param accountingLines
     * @param user
     * @return true if the given user is responsible for any accounting line of the given transactionalDocument
     */
    protected boolean userOwnsAnyAccountingLine(Person user, List<AccountingLine> accountingLines) {
        for (AccountingLine accountingLine : accountingLines) {
            if (SpringContext.getBean(AccountService.class).hasResponsibilityOnAccount(user, accountingLine.getAccount())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Determines if the line is editable; if so, it adds the line to the editableAccounts map
     * @param line the line to determine editability of
     * @param currentUser the current session user to check permissions for
     * @param accountService the accountService
     * @return true if the line is editable, false otherwise 
     */
    private boolean determineLineEditability(AccountingLine line, Person currentUser, AccountService accountService) {
        Account acct = accountService.getByPrimaryId(line.getChartOfAccountsCode(), line.getAccountNumber());
        if (ObjectUtils.isNull(acct)) return true;
        if (accountService.hasResponsibilityOnAccount(currentUser, acct)) return true;
        return false;
    }

    /**
     * @see org.kuali.module.financial.document.authorization.FinancialDocumentAuthorizer#getEditableAccounts(org.kuali.rice.kns.document.TransactionalDocument,
     *      org.kuali.module.chart.bo.KfsUser)
     */
    public Map getEditableAccounts(TransactionalDocument document, Person user) {

        Map editableAccounts = new HashMap();
        AccountingDocument acctDoc = (AccountingDocument) document;
        AccountService accountService = SpringContext.getBean(AccountService.class);

        // for every source accounting line, decide if account should be in map
        for (Object acctLineAsObj: acctDoc.getSourceAccountingLines()) {
            if (determineLineEditability((AccountingLine)acctLineAsObj, user, accountService)) {
                editableAccounts.put(((AccountingLine)acctLineAsObj).getAccountKey(), "TRUE");
            }
        }

        // for every target accounting line, decide if account should be in map
        for (Object acctLineAsObj: acctDoc.getTargetAccountingLines()) {
            AccountingLine acctLine = (AccountingLine)acctLineAsObj;
            if (determineLineEditability((AccountingLine)acctLineAsObj, user, accountService)) {
                editableAccounts.put(((AccountingLine)acctLineAsObj).getAccountKey(), "TRUE");
            }
        }

        return editableAccounts;
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizer#getEditableAccounts(java.util.List,
     *      org.kuali.module.chart.bo.KfsUser)
     */
    public Map getEditableAccounts(List<AccountingLine> lines, Person user) {
        Map editableAccounts = new HashMap();
        AccountService accountService = SpringContext.getBean(AccountService.class);
        
        for (AccountingLine line: lines) {
            if (determineLineEditability(line, user, accountService)) {
                editableAccounts.put(line.getAccountKey(), "TRUE");
            }
        }

        return editableAccounts;
    }
    
    @Override
    protected void addRoleQualification(BusinessObject businessObject, Map<String,String> attributes) {
        super.addRoleQualification(businessObject, attributes);
        Document document = (Document)businessObject;
        // add the document amount
        if ( ((AccountingDocument)document).getSourceTotal() != null && ((FinancialSystemDocumentHeader)document.getDocumentHeader()).getFinancialDocumentTotalAmount() != null ) {
            attributes.put(KfsKimAttributes.DOCUMENT_AMOUNT, ((FinancialSystemDocumentHeader)document.getDocumentHeader()).getFinancialDocumentTotalAmount().toString());
        } else {
            attributes.put(KfsKimAttributes.DOCUMENT_AMOUNT, "0" );
        }
    }
}

