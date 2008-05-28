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

import static org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.KFSKeyConstants.CashReceipt;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;
import org.kuali.kfs.validation.GenericValidation;

/**
 * Validation for the Cash Receipt family of documents which validates the sum amount of all
 * accounting lines on the document.
 */
public class CashReceiptFamilyAccountingLineTotalValidation extends GenericValidation {
    private AccountingDocument accountingDocumentForValidation;

    /**
     * Return true if source total is non-zero
     * @see org.kuali.kfs.validation.Validation#validate(org.kuali.kfs.rule.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean isValid = true;

        if (getAccountingDocumentForValidation().getSourceTotal().isZero()) {
            String errorProperty = DOCUMENT_ERROR_PREFIX + KFSPropertyConstants.SOURCE_ACCOUNTING_LINES;

            isValid = false;
            GlobalVariables.getErrorMap().putError(errorProperty, CashReceipt.ERROR_ZERO_TOTAL, "Accounting Line Total");
        }

        return isValid;
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
