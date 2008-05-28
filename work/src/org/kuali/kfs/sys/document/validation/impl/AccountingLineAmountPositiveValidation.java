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

import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;

/**
 * Validates an accounting line that, if the line is not a correction document, the line amount is a positive amount
 */
public class AccountingLineAmountPositiveValidation extends GenericValidation {
    private AccountingDocument accountingDocumentForValidation;
    private AccountingLine accountingLineForValidation;
    
    /**
     * Check for zero amount, or negative on original (non-correction) document; no sign check for documents that are
     * corrections to previous documents
     * <strong>the accounting document must be the first parameter, the accounting line must be the second parameter</strong>
     * @see org.kuali.kfs.validation.GenericValidation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {        
        KualiDecimal amount = accountingLineForValidation.getAmount();
        String correctsDocumentId = accountingDocumentForValidation.getDocumentHeader().getFinancialDocumentInErrorNumber();
        
        if (null == correctsDocumentId && KualiDecimal.ZERO.compareTo(amount) == 1) { // amount < 0
            GlobalVariables.getErrorMap().putError(KFSConstants.AMOUNT_PROPERTY_NAME, KFSKeyConstants.ERROR_INVALID_NEGATIVE_AMOUNT_NON_CORRECTION);
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

    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }
}
