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
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;
import org.kuali.kfs.validation.GenericValidation;

/**
 * Validates that the amount on a given accounting line is not zero
 */
public class AccountingLineAmountNonZeroValidation extends GenericValidation {
    private AccountingLine accountingLineForValidation;
    
    /**
     * Validates that the amount of the given accounting line is not zero
     * <strong>the accounting document must be the first parameter, the accounting line must be the second parameter</strong>
     * @see org.kuali.kfs.validation.GenericValidation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        KualiDecimal amount = accountingLineForValidation.getAmount();

        if (KualiDecimal.ZERO.compareTo(amount) == 0) { // amount == 0
            GlobalVariables.getErrorMap().putError(KFSConstants.AMOUNT_PROPERTY_NAME, KFSKeyConstants.ERROR_ZERO_AMOUNT, "an accounting line");
            return false;
        }

        return true;
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
