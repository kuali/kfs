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
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;
import org.kuali.kfs.validation.GenericValidation;
import org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine;

/**
 * Validation that checks Budget Adjustment accounting lines to make sure that non-zero adjustments have related income stream accounts.
 */
public class BudgetAdjustmentAccountingLineAccountIncomeStreamValidation extends GenericValidation {
    private BudgetAdjustmentAccountingLine accountingLineForValidation;

    /**
     * Validate that, if current adjustment amount is non zero, account has an associated income stream chart and account
     * @see org.kuali.kfs.validation.Validation#validate(org.kuali.kfs.rule.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean accountNumberAllowed = true;
        if (getAccountingLineForValidation().getCurrentBudgetAdjustmentAmount().isNonZero()) {
            getAccountingLineForValidation().refreshReferenceObject("account");
            if (ObjectUtils.isNull(getAccountingLineForValidation().getAccount().getIncomeStreamAccount())) {
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, KFSKeyConstants.ERROR_DOCUMENT_BA_NO_INCOME_STREAM_ACCOUNT, getAccountingLineForValidation().getAccountNumber());
                accountNumberAllowed = false;
            }
        }
        return accountNumberAllowed;
    }

    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public BudgetAdjustmentAccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(BudgetAdjustmentAccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }
}
