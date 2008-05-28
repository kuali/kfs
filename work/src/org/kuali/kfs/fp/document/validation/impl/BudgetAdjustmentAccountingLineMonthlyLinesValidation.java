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
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;
import org.kuali.kfs.validation.GenericValidation;
import org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine;

/**
 * Validation for a budget adjustment accounting line that validates the information in the monthly lines
 */
public class BudgetAdjustmentAccountingLineMonthlyLinesValidation extends GenericValidation {
    private BudgetAdjustmentAccountingLine accountingLineForValidation;

    /**
     * Validates the total of the monthly amount fields (if not 0) equals the current budget amount. If current budget is 0, then
     * total of monthly fields must be 0.
     * 
     * @see org.kuali.kfs.validation.Validation#validate(org.kuali.kfs.rule.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean validMonthlyLines = true;

        KualiDecimal monthlyTotal = getAccountingLineForValidation().getMonthlyLinesTotal();
        if (monthlyTotal.isNonZero() && monthlyTotal.compareTo(getAccountingLineForValidation().getCurrentBudgetAdjustmentAmount()) != 0) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CURRENT_BUDGET_ADJUSTMENT_AMOUNT, KFSKeyConstants.ERROR_DOCUMENT_BA_MONTH_TOTAL_NOT_EQUAL_CURRENT);
            validMonthlyLines = false;
        }

        return validMonthlyLines;
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
