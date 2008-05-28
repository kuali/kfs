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

import static org.kuali.kfs.KFSConstants.AUXILIARY_LINE_HELPER_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.CREDIT_AMOUNT_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.DEBIT_AMOUNT_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.GL_DEBIT_CODE;
import static org.kuali.kfs.KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.SQUARE_BRACKET_LEFT;
import static org.kuali.kfs.KFSConstants.SQUARE_BRACKET_RIGHT;
import static org.kuali.kfs.KFSConstants.VOUCHER_LINE_HELPER_CREDIT_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.VOUCHER_LINE_HELPER_DEBIT_PROPERTY_NAME;
import static org.kuali.kfs.KFSKeyConstants.ERROR_ZERO_OR_NEGATIVE_AMOUNT;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;
import org.kuali.kfs.validation.GenericValidation;
import org.kuali.module.financial.document.AuxiliaryVoucherDocument;

/**
 * The Auxiliary Voucher's customization of the accounting line amount validation.
 */
public class AuxiliaryVoucherAccountingLineAmountValidation extends GenericValidation {
    private AccountingLine accountingLineForValidation;

    /**
     * Accounting lines for Auxiliary Vouchers can only be positive non-zero numbers
     * @see org.kuali.kfs.validation.Validation#validate(org.kuali.kfs.rule.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean retval = true;
        KualiDecimal amount = accountingLineForValidation.getAmount();

        // check for negative or zero amounts
        if (KualiDecimal.ZERO.equals(amount)) { // if 0
            GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(buildErrorMapKeyPathForDebitCreditAmount(true), ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");
            GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(buildErrorMapKeyPathForDebitCreditAmount(false), ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");

            retval = false;
        }
        else if (amount.isNegative()) { // entered a negative number
            String debitCreditCode = accountingLineForValidation.getDebitCreditCode();
            if (StringUtils.isNotBlank(debitCreditCode) && GL_DEBIT_CODE.equals(debitCreditCode)) {
                GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(buildErrorMapKeyPathForDebitCreditAmount(true), ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");
            }
            else {
                GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(buildErrorMapKeyPathForDebitCreditAmount(false), ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");
            }

            retval = false;
        }

        return retval;
    }
    
    /**
     * This method looks at the current full key path that exists in the ErrorMap structure to determine how to build the error map
     * for the special journal voucher credit and debit fields since they don't conform to the standard pattern of accounting lines.
     * 
     * @param isDebit boolean to determine whether or not value isDebit or not
     * @return String represents error map key to use
     */
    private String buildErrorMapKeyPathForDebitCreditAmount(boolean isDebit) {
        // determine if we are looking at a new line add or an update
        boolean isNewLineAdd = GlobalVariables.getErrorMap().getErrorPath().contains(NEW_SOURCE_ACCT_LINE_PROPERTY_NAME);
        isNewLineAdd |= GlobalVariables.getErrorMap().getErrorPath().contains(NEW_SOURCE_ACCT_LINE_PROPERTY_NAME);

        if (isNewLineAdd) {
            if (isDebit) {
                return DEBIT_AMOUNT_PROPERTY_NAME;
            }
            else {
                return CREDIT_AMOUNT_PROPERTY_NAME;
            }
        }
        else {
            String index = StringUtils.substringBetween(GlobalVariables.getErrorMap().getKeyPath("", true), SQUARE_BRACKET_LEFT, SQUARE_BRACKET_RIGHT);
            String indexWithParams = SQUARE_BRACKET_LEFT + index + SQUARE_BRACKET_RIGHT;
            if (isDebit) {
                return AUXILIARY_LINE_HELPER_PROPERTY_NAME + indexWithParams + VOUCHER_LINE_HELPER_DEBIT_PROPERTY_NAME;
            }
            else {
                return AUXILIARY_LINE_HELPER_PROPERTY_NAME + indexWithParams + VOUCHER_LINE_HELPER_CREDIT_PROPERTY_NAME;
            }
        }
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
