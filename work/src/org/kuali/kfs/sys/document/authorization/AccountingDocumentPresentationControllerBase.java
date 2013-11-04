/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.sys.document.authorization;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.RouteLevelNames;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class AccountingDocumentPresentationControllerBase extends LedgerPostingDocumentPresentationControllerBase {

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.krad.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);

        this.addExpenseEntryEditMode(document, editModes);

        return editModes;
    }

    // add expense entry edit mode when the given document is enroute for account review
    protected void addExpenseEntryEditMode(Document document, Set<String> editModes) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        Set<String> currentRouteLevels = workflowDocument.getCurrentNodeNames();

        if (workflowDocument.isEnroute() && CollectionUtils.isNotEmpty(currentRouteLevels) && currentRouteLevels.contains(KFSConstants.RouteLevelNames.ACCOUNT)) {
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
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canEdit(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canEdit(Document document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        FinancialSystemDocumentHeader documentheader = (FinancialSystemDocumentHeader) (document.getDocumentHeader());

        if (workflowDocument.isCanceled() || documentheader.getFinancialDocumentInErrorNumber() != null) {
            return false;
        }
        else if (workflowDocument.isEnroute()) {
            Set<String> currentRouteLevels = workflowDocument.getCurrentNodeNames();

            if (CollectionUtils.isNotEmpty(currentRouteLevels) && currentRouteLevels.contains(RouteLevelNames.ACCOUNTING_ORGANIZATION_HIERARCHY)) {
                return false;
            }
        }

        return super.canEdit(document);
    }

    /**
     * @param accountingLines
     * @param user
     * @return true if the given user is responsible for any accounting line of the given transactionalDocument
     */
    protected boolean userOwnsAnyAccountingLine(Person user, List<AccountingLine> accountingLines) {
        for (AccountingLine accountingLine : accountingLines) {
            if (StringUtils.isNotEmpty(accountingLine.getAccountNumber()) && ObjectUtils.isNotNull(accountingLine.getAccount())) {
                if (SpringContext.getBean(AccountService.class).hasResponsibilityOnAccount(user, accountingLine.getAccount())) {
                    return true;
            }
            }
        }
        return false;
    }
}
