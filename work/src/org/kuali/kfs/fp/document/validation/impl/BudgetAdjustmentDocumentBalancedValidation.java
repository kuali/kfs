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

import java.util.Iterator;
import java.util.Map;

import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;
import org.kuali.kfs.validation.GenericValidation;
import org.kuali.module.financial.document.BudgetAdjustmentDocument;

/**
 * A validation which checks if a Budget Adjustment document is balanced before heading to routing
 */
public class BudgetAdjustmentDocumentBalancedValidation extends GenericValidation {
    public BudgetAdjustmentDocument accountingDocumentForValidation;

    /**
     * Validates that the budget adjustment document is balanced, based on whether the source base amount equals the target base amount
     * and that the income stream balance map has no non-zero values.
     * @see org.kuali.kfs.validation.Validation#validate(org.kuali.kfs.rule.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        boolean balanced = true;

        // check base amounts are equal
        if (getAccountingDocumentForValidation().getSourceBaseBudgetTotal().compareTo(getAccountingDocumentForValidation().getTargetBaseBudgetTotal()) != 0) {
            GlobalVariables.getErrorMap().putError(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_BA_BASE_AMOUNTS_BALANCED);
            balanced = false;
        }

        // check current amounts balance, income stream balance Map should add to 0
        Map incomeStreamMap = getAccountingDocumentForValidation().buildIncomeStreamBalanceMap();
        KualiDecimal totalCurrentAmount = new KualiDecimal(0);
        for (Iterator iter = incomeStreamMap.values().iterator(); iter.hasNext();) {
            KualiDecimal streamAmount = (KualiDecimal) iter.next();
            totalCurrentAmount = totalCurrentAmount.add(streamAmount);
        }

        if (totalCurrentAmount.isNonZero()) {
            GlobalVariables.getErrorMap().putError(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_BA_CURRENT_AMOUNTS_BALANCED);
            balanced = false;
        }

        return balanced;
    }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public BudgetAdjustmentDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(BudgetAdjustmentDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
}
