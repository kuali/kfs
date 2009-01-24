/*
 * Copyright 2009 The Kuali Foundation.
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
import java.util.List;
import java.util.Set;

import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.KFSConstants.RouteLevelNames;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class AccountingDocumentPresentationControllerBase extends LedgerPostingDocumentPresentationControllerBase {

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);

        this.addExpenseEntryEditMode(document, editModes);

        return editModes;
    }

    // add expense entry edit mode when the given document is enroute for account review
    private void addExpenseEntryEditMode(Document document, Set<String> editModes) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        List<String> currentRouteLevels = getCurrentRouteLevels(workflowDocument);

        if (workflowDocument.stateIsEnroute() && currentRouteLevels.contains(KFSConstants.RouteLevelNames.ACCOUNT_REVIEW)) {
            AccountingDocument accountingDocument = (AccountingDocument) document;

            List<AccountingLine> lineList = new ArrayList<AccountingLine>();
            lineList.addAll(accountingDocument.getSourceAccountingLines());
            lineList.addAll(accountingDocument.getTargetAccountingLines());

            Person currentUser = GlobalVariables.getUserSession().getPerson();
            if (workflowDocument.isApprovalRequested() && userOwnsAnyAccountingLine(currentUser, lineList)) {
                editModes.add(KfsAuthorizationConstants.TransactionalEditMode.EXPENSE_ENTRY);
            }
        }
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canEdit(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canEdit(Document document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        FinancialSystemDocumentHeader documentheader = (FinancialSystemDocumentHeader) (document.getDocumentHeader());

        if (workflowDocument.stateIsCanceled() || documentheader.getFinancialDocumentInErrorNumber() != null) {
            return false;
        }
        else if (workflowDocument.stateIsEnroute()) {
            List<String> currentRouteLevels = getCurrentRouteLevels(workflowDocument);

            if (currentRouteLevels.contains(RouteLevelNames.ORG_REVIEW)) {
                return false;
            }
        }

        return super.canEdit(document);
    }

    /**
     * A helper method for determining the route levels for a given document.
     * 
     * @param workflowDocument
     * @return List
     */
    protected List<String> getCurrentRouteLevels(KualiWorkflowDocument workflowDocument) {
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
}
