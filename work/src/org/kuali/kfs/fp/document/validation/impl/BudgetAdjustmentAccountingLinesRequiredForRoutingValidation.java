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
package org.kuali.module.financial.document.validation.impl;

import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;
import org.kuali.kfs.validation.GenericValidation;

/**
 * The Budget Adjustment's variation on the lines required for routing met validation - here, we just need
 * to make sure that the total number of accounting lines on the document is greater than 0.
 */
public class BudgetAdjustmentAccountingLinesRequiredForRoutingValidation extends GenericValidation {
    private AccountingDocument accountingDocumentForValidation;

    /**
     * The BA document does not have to have source accounting lines. In the case of setting up a budget for a new account, only targets
     * line (increase section) are setup.
     * @see org.kuali.kfs.validation.Validation#validate(org.kuali.kfs.rule.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        // check that both source and target are not empty, in which case is an error
        if (getAccountingDocumentForValidation().getSourceAccountingLines().isEmpty() && getAccountingDocumentForValidation().getTargetAccountingLines().isEmpty()) {
            GlobalVariables.getErrorMap().putError(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_NO_ACCOUNTING_LINES);
            return false;
        }

        return true;
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
