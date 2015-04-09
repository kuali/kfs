/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
