/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.kfs.document.authorization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.document.authorization.TransactionalDocumentAuthorizerBase;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.bo.AccountResponsibility;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.workflow.KualiWorkflowUtils.RouteLevelNames;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * DocumentAuthorizer containing common, reusable document-level authorization code for financial (i.e. Transactional) documents
 */
public class AccountingDocumentAuthorizerBase extends TransactionalDocumentAuthorizerBase implements AccountingDocumentAuthorizer{
    private static Log LOG = LogFactory.getLog(AccountingDocumentAuthorizerBase.class);
    
    /**
     * @see org.kuali.core.authorization.FinancialDocumentAuthorizer#getAccountingLineEditableFields(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    public Map getAccountingLineEditableFields(Document document, UniversalUser user) {
        return new HashMap();
    }


    /**
     * The Kuali user interface code automatically reconstructs a complete document from a form's contents when a form is submitted,
     * even if that form's contents are invalid (specifically, the Source and TargetAccountingLines from the form are used even if
     * they contain invalid accountNumbers). Since the accounts referenced in the source and targetAccountingLines are used to
     * determine editMode, when a) the document is enroute; b) the user is an FO, and receives the document for approval; and c) the
     * user changes the accountNumbers on all accountingLines for which they are FO to something different or invalid, the form
     * builds a Document for which the user is FO of none of the accountingLines, which makes the editMode computation incorrectly
     * shift from EXPENSE_ENTRY to ACCOUNT_REVIEW, which makes it impossible for the user to restore the lines which they just
     * rendered invalid. The simplest way to avoid that is to allow the UI to pass in the baselineSource and target accountingLines,
     * so that the editability can be determined from them (since they are, by definition, valid and authoritative).
     * </p>
     * Note that document types which route straight to final will get an edit mode of VIEW_ONLY or FULL_ENTRY, never EXPENSE_ENTRY,
     * because even if the state is briefly enroute, the route level is never ORG_REVIEW or ACCOUNT_REVIEW.
     * 
     *
     * @see org.kuali.module.financial.document.authorization.FinancialDocumentAuthorizer#getEditMode(org.kuali.core.document.Document, org.kuali.core.bo.user.UniversalUser, java.util.List, java.util.List)
     */
    public Map getEditMode(Document document, UniversalUser user, List sourceAccountingLines, List targetAccountingLines) {
        ChartUser chartUser = (ChartUser) user.getModuleUser(ChartUser.MODULE_ID);
        
        String editMode = AuthorizationConstants.TransactionalEditMode.VIEW_ONLY;

        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.stateIsCanceled() || (document.getDocumentHeader().getFinancialDocumentInErrorNumber() != null)) {
            editMode = AuthorizationConstants.TransactionalEditMode.VIEW_ONLY;
        }
        else if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            if (workflowDocument.userIsInitiator(user)) {
                editMode = AuthorizationConstants.TransactionalEditMode.FULL_ENTRY;
            }
        }
        else if (workflowDocument.stateIsEnroute()) {
            List currentRouteLevels = getCurrentRouteLevels(workflowDocument);

            if (currentRouteLevels.contains(RouteLevelNames.ORG_REVIEW)) {
                // The routing level should be linear for Kuali, i.e., assert currentRouteLevels.size() == 1,
                // but in case it becomes parallel, don't allow an account review while an org review is underway.
                editMode = AuthorizationConstants.TransactionalEditMode.VIEW_ONLY;
            }
            else if (currentRouteLevels.contains(RouteLevelNames.ACCOUNT_REVIEW)) {
                List lineList = new ArrayList();
                lineList.addAll(sourceAccountingLines);
                lineList.addAll(targetAccountingLines);

                if (workflowDocument.isApprovalRequested() && userOwnsAnyAccountingLine(chartUser, lineList)) {
                    editMode = AuthorizationConstants.TransactionalEditMode.EXPENSE_ENTRY;
                }
            }
        }

        Map editModeMap = new HashMap();
        editModeMap.put(editMode, "TRUE");

        return editModeMap;
    }

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
    protected boolean userOwnsAnyAccountingLine(ChartUser user, List accountingLines) {
        for (Iterator i = accountingLines.iterator(); i.hasNext();) {
            AccountingLine accountingLine = (AccountingLine) i.next();
            String chartCode = accountingLine.getChartOfAccountsCode();
            String accountNumber = accountingLine.getAccountNumber();

            if (user.isResponsibleForAccount(chartCode, accountNumber)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see org.kuali.module.financial.document.authorization.FinancialDocumentAuthorizer#getEditableAccounts(org.kuali.core.document.TransactionalDocument, org.kuali.module.chart.bo.ChartUser)
     */
    public Map getEditableAccounts(TransactionalDocument document, ChartUser user) {
        Map editableAccounts = new HashMap();

        // convert AccountResponsibilities HashMap into Account Map
        for (Iterator i = user.getAccountResponsibilities().entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            AccountResponsibility accountResponsibility = (AccountResponsibility) e.getValue();
            Account account = accountResponsibility.getAccount();
            editableAccounts.put(account.getAccountKey(), account);
        }

        return editableAccounts;
    }
}