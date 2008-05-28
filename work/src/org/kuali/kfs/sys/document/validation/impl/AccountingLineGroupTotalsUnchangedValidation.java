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

import org.kuali.core.service.DocumentService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * A validation, used on accounting document approval, that accounting line totals are unchanged
 */
public class AccountingLineGroupTotalsUnchangedValidation extends GenericValidation {
    private AccountingDocument accountingDocumentForValidation;
    
    /**
     * Checks that the source and total amounts on the current version of the accounting document
     * are equal to the persisted source and total totals.
     * <strong>Expects a document to be sent in as the first parameter</strong>
     * @see org.kuali.kfs.validation.GenericValidation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        AccountingDocument persistedDocument = null;

        persistedDocument = retrievePersistedDocument(accountingDocumentForValidation);

        boolean isUnchanged = true;
        if (persistedDocument == null) {
            handleNonExistentDocumentWhenApproving(accountingDocumentForValidation);
        }
        else {
            // retrieve the persisted totals
            KualiDecimal persistedSourceLineTotal = persistedDocument.getSourceTotal();
            KualiDecimal persistedTargetLineTotal = persistedDocument.getTargetTotal();

            // retrieve the updated totals
            KualiDecimal currentSourceLineTotal = accountingDocumentForValidation.getSourceTotal();
            KualiDecimal currentTargetLineTotal = accountingDocumentForValidation.getTargetTotal();

            // make sure that totals have remained unchanged, if not, recognize that, and
            // generate appropriate error messages
            if (currentSourceLineTotal.compareTo(persistedSourceLineTotal) != 0) {
                isUnchanged = false;

                // build out error message
                buildTotalChangeErrorMessage(KFSConstants.SOURCE_ACCOUNTING_LINE_ERRORS, persistedSourceLineTotal, currentSourceLineTotal);
            }

            if (currentTargetLineTotal.compareTo(persistedTargetLineTotal) != 0) {
                isUnchanged = false;

                // build out error message
                buildTotalChangeErrorMessage(KFSConstants.TARGET_ACCOUNTING_LINE_ERRORS, persistedTargetLineTotal, currentTargetLineTotal);
            }
        }

        return isUnchanged;
    }

    /**
     * attempt to retrieve the document from the DB for comparison
     * 
     * @param accountingDocument
     * @return AccountingDocument
     */
    protected AccountingDocument retrievePersistedDocument(AccountingDocument accountingDocument) {
        AccountingDocument persistedDocument = null;

        try {
            persistedDocument = (AccountingDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(accountingDocument.getDocumentNumber());
        }
        catch (WorkflowException we) {
            handleNonExistentDocumentWhenApproving(accountingDocument);
        }

        return persistedDocument;
    }
    
    /**
     * This method builds out the error message for when totals have changed.
     * 
     * @param propertyName
     * @param persistedSourceLineTotal
     * @param currentSourceLineTotal
     */
    protected void buildTotalChangeErrorMessage(String propertyName, KualiDecimal persistedSourceLineTotal, KualiDecimal currentSourceLineTotal) {
        String persistedTotal = (String) new CurrencyFormatter().format(persistedSourceLineTotal);
        String currentTotal = (String) new CurrencyFormatter().format(currentSourceLineTotal);
        GlobalVariables.getErrorMap().putError(propertyName, KFSKeyConstants.ERROR_DOCUMENT_SINGLE_ACCOUNTING_LINE_SECTION_TOTAL_CHANGED, new String[] { persistedTotal, currentTotal });
    }

    /**
     * Handles the case when a non existent document is attempted to be retrieve and that if it's in an initiated state, it's ok.
     * 
     * @param accountingDocument
     */
    protected final void handleNonExistentDocumentWhenApproving(AccountingDocument accountingDocument) {
        // check to make sure this isn't an initiated document being blanket approved
        if (!accountingDocument.getDocumentHeader().getWorkflowDocument().stateIsInitiated()) {
            throw new IllegalStateException("Document " + accountingDocument.getDocumentNumber() + " is not a valid document that currently exists in the system.");
        }
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
}
