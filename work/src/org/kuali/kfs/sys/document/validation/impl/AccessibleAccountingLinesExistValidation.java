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
package org.kuali.kfs.validation;

import static org.kuali.kfs.KFSConstants.ACCOUNTING_LINE_ERRORS;
import static org.kuali.kfs.KFSKeyConstants.ERROR_ACCOUNTINGLINE_LASTACCESSIBLE_DELETE;

import java.util.Iterator;

import org.kuali.core.util.GlobalVariables;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.service.AccountService;

/**
 * A validation that checks that once a given accounting line is deleted, there will still exist
 * accessible accounting lines on the document
 */
public class AccessibleAccountingLinesExistValidation extends GenericValidation {
    private AccountService accountService;
    private AccountingDocument accountingDocumentForValidation;
    private boolean lineWasAlreadyDeletedFromDocumentForValidation;

    /**
     * <strong>Expects an accounting document as the first parameter and a boolean, signifying whether the line was already deleted or not from the document, as the second</param>
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        // verify that other accountingLines will exist after the deletion which are accessible to this user
        int minimumRemainingAccessibleLines = 1 + (lineWasAlreadyDeletedFromDocumentForValidation ? 0 : 1);
        boolean sufficientLines = hasAccessibleAccountingLines(accountingDocumentForValidation, minimumRemainingAccessibleLines);
        if (!sufficientLines) {
            GlobalVariables.getErrorMap().putError(ACCOUNTING_LINE_ERRORS, ERROR_ACCOUNTINGLINE_LASTACCESSIBLE_DELETE);
        }
        return sufficientLines;
    }

    /**
     * @param financialDocument
     * @param min
     * @return true if the document has n (or more) accessible accountingLines
     */
    protected boolean hasAccessibleAccountingLines(AccountingDocument financialDocument, int min) {
        boolean hasLines = false;

        // only count if the doc is enroute
        KualiWorkflowDocument workflowDocument = financialDocument.getDocumentHeader().getWorkflowDocument();
        ChartUser currentUser = (ChartUser) GlobalVariables.getUserSession().getUniversalUser().getModuleUser(ChartUser.MODULE_ID);
        if (workflowDocument.stateIsEnroute()) {
            int accessibleLines = 0;
            for (Iterator i = financialDocument.getSourceAccountingLines().iterator(); (accessibleLines < min) && i.hasNext();) {
                AccountingLine line = (AccountingLine) i.next();
                if (accountService.accountIsAccessible(financialDocument, line, currentUser)) {
                    accessibleLines += 1;
                }
            }
            for (Iterator i = financialDocument.getTargetAccountingLines().iterator(); (accessibleLines < min) && i.hasNext();) {
                AccountingLine line = (AccountingLine) i.next();
                if (accountService.accountIsAccessible(financialDocument, line, currentUser)) {
                    accessibleLines += 1;
                }
            }

            hasLines = (accessibleLines >= min);
        }
        else {
            if (workflowDocument.stateIsException() && currentUser.getUniversalUser().isWorkflowExceptionUser()) {
                hasLines = true;
            }
            else {
                if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
                    hasLines = true;
                }
                else {
                    hasLines = false;
                }
            }
        }

        return hasLines;
    }

    /**
     * Sets the accountService attribute value.
     * @param accountService The accountService to set.
     */
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    /**
     * Gets the lineWasAlreadyDeletedFromDocumentForValidation attribute. 
     * @return Returns the lineWasAlreadyDeletedFromDocumentForValidation.
     */
    public boolean isLineWasAlreadyDeletedFromDocumentForValidation() {
        return lineWasAlreadyDeletedFromDocumentForValidation;
    }

    /**
     * Sets the lineWasAlreadyDeletedFromDocumentForValidation attribute value.
     * @param lineWasAlreadyDeletedFromDocumentForValidation The lineWasAlreadyDeletedFromDocumentForValidation to set.
     */
    public void setLineWasAlreadyDeletedFromDocumentForValidation(boolean lineWasAlreadyDeletedFromDocumentForValidation) {
        this.lineWasAlreadyDeletedFromDocumentForValidation = lineWasAlreadyDeletedFromDocumentForValidation;
    }
}
