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
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;
import org.kuali.kfs.validation.GenericValidation;
import org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine;
import org.kuali.module.financial.document.BudgetAdjustmentDocument;
import org.kuali.module.financial.service.FiscalYearFunctionControlService;

/**
 * Validates an accounting line on a budget adjustment document whether the base amount on the line can be changed or not
 */
public class BudgetAdjustmentAccountingLineBaseAmountValidation extends GenericValidation {
    private BudgetAdjustmentDocument accountingDocumentForValidation;
    private BudgetAdjustmentAccountingLine accountingLineForValidation;
    private FiscalYearFunctionControlService fiscalYearFunctionControlService;

    /**
     * Validate that, if a base amount is entered for a line, that it can be adjusted for the posting year
     * @see org.kuali.kfs.validation.Validation#validate(org.kuali.kfs.rule.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean allow = true;
        if (getAccountingLineForValidation().getBaseBudgetAdjustmentAmount().isNonZero() && fiscalYearFunctionControlService.isBaseAmountChangeAllowed(getAccountingDocumentForValidation().getPostingYear())) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.BASE_BUDGET_ADJUSTMENT_AMOUNT, KFSKeyConstants.ERROR_DOCUMENT_BA_BASE_AMOUNT_CHANGE_NOT_ALLOWED);
            allow = false;
        }
        return allow;
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

    /**
     * Gets the fiscalYearFunctionControlService attribute. 
     * @return Returns the fiscalYearFunctionControlService.
     */
    public FiscalYearFunctionControlService getFiscalYearFunctionControlService() {
        return fiscalYearFunctionControlService;
    }

    /**
     * Sets the fiscalYearFunctionControlService attribute value.
     * @param fiscalYearFunctionControlService The fiscalYearFunctionControlService to set.
     */
    public void setFiscalYearFunctionControlService(FiscalYearFunctionControlService fiscalYearFunctionControlService) {
        this.fiscalYearFunctionControlService = fiscalYearFunctionControlService;
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
