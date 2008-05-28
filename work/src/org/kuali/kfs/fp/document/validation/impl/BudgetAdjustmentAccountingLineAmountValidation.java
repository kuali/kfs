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
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;
import org.kuali.kfs.service.DebitDeterminerService;
import org.kuali.kfs.validation.GenericValidation;
import org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine;

/**
 * Validation that checks the amounts on budget adjustment document accounting lines.
 */
public class BudgetAdjustmentAccountingLineAmountValidation extends GenericValidation {
    private BudgetAdjustmentAccountingLine accountingLineForValidation;
    private AccountingDocument accountingDocumentForValidation;
    private DebitDeterminerService debitDeterminerService;

    /**
     * Validates the amounts on a budget adjustment accounting line, making sure that either the current adjustment amount or the base adjustment amount are not zero,
     * and that all given amounts are positive.
     * @see org.kuali.kfs.validation.Validation#validate(org.kuali.kfs.rule.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean amountValid = true;

        // check amounts both current and base amounts are not zero
        if (getAccountingLineForValidation().getCurrentBudgetAdjustmentAmount().isZero() && getAccountingLineForValidation().getBaseBudgetAdjustmentAmount().isZero()) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.BASE_BUDGET_ADJUSTMENT_AMOUNT, KFSKeyConstants.ERROR_BA_AMOUNT_ZERO);
            amountValid = false;
        }

        // if not an error correction, all amounts must be positive
        if (!debitDeterminerService.isErrorCorrection(getAccountingDocumentForValidation())) {
            amountValid &= checkAmountSign(getAccountingLineForValidation().getCurrentBudgetAdjustmentAmount(), KFSPropertyConstants.CURRENT_BUDGET_ADJUSTMENT_AMOUNT, "Current");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getBaseBudgetAdjustmentAmount().kualiDecimalValue(), KFSPropertyConstants.BASE_BUDGET_ADJUSTMENT_AMOUNT, "Base");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth1LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_1_LINE_AMOUNT, "Month 1");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth2LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_2_LINE_AMOUNT, "Month 2");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth3LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_3_LINE_AMOUNT, "Month 3");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth4LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_4_LINE_AMOUNT, "Month 4");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth5LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_5_LINE_AMOUNT, "Month 5");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth6LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_6_LINE_AMOUNT, "Month 6");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth7LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_7_LINE_AMOUNT, "Month 7");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth8LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_8_LINE_AMOUNT, "Month 8");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth8LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_9_LINE_AMOUNT, "Month 9");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth10LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_10_LINE_AMOUNT, "Month 10");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth10LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_11_LINE_AMOUNT, "Month 11");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth12LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_12_LINE_AMOUNT, "Month 12");
        }

        return amountValid;
    }

    /**
     * Helper method to check if an amount is negative and add an error if not.
     * 
     * @param amount to check
     * @param propertyName to add error under
     * @param label for error
     * @return boolean indicating if the value has the requested sign
     */
    protected boolean checkAmountSign(KualiDecimal amount, String propertyName, String label) {
        boolean correctSign = true;

        if (amount.isNegative()) {
            GlobalVariables.getErrorMap().putError(propertyName, KFSKeyConstants.ERROR_BA_AMOUNT_NEGATIVE, label);
            correctSign = false;
        }

        return correctSign;
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
     * Gets the debitDeterminerService attribute. 
     * @return Returns the debitDeterminerService.
     */
    public DebitDeterminerService getDebitDeterminerService() {
        return debitDeterminerService;
    }

    /**
     * Sets the debitDeterminerService attribute value.
     * @param debitDeterminerService The debitDeterminerService to set.
     */
    public void setDebitDeterminerService(DebitDeterminerService debitDeterminerService) {
        this.debitDeterminerService = debitDeterminerService;
    }
}
