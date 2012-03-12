/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.fp.document.validation.impl;

import static org.kuali.kfs.sys.KFSConstants.AUXILIARY_LINE_HELPER_PROPERTY_NAME;
import static org.kuali.kfs.sys.KFSConstants.CREDIT_AMOUNT_PROPERTY_NAME;
import static org.kuali.kfs.sys.KFSConstants.DEBIT_AMOUNT_PROPERTY_NAME;
import static org.kuali.kfs.sys.KFSConstants.GL_DEBIT_CODE;
import static org.kuali.kfs.sys.KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME;
import static org.kuali.kfs.sys.KFSConstants.SQUARE_BRACKET_LEFT;
import static org.kuali.kfs.sys.KFSConstants.SQUARE_BRACKET_RIGHT;
import static org.kuali.kfs.sys.KFSConstants.VOUCHER_LINE_HELPER_CREDIT_PROPERTY_NAME;
import static org.kuali.kfs.sys.KFSConstants.VOUCHER_LINE_HELPER_DEBIT_PROPERTY_NAME;
import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_ZERO_OR_NEGATIVE_AMOUNT;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * The Auxiliary Voucher's customization of the accounting line amount validation.
 */
public class AuxiliaryVoucherAccountingLineAmountValidation extends GenericValidation {
    private AccountingLine accountingLineForValidation;

    /**
     * Accounting lines for Auxiliary Vouchers can only be positive non-zero numbers
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean retval = true;
        KualiDecimal amount = accountingLineForValidation.getAmount();

        // check for negative or zero amounts
        if (KualiDecimal.ZERO.equals(amount)) { // if 0
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(buildMessageMapKeyPathForDebitCreditAmount(true), ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(buildMessageMapKeyPathForDebitCreditAmount(false), ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");

            retval = false;
        }
        else if (amount.isNegative()) { // entered a negative number
            String debitCreditCode = accountingLineForValidation.getDebitCreditCode();
            if (StringUtils.isNotBlank(debitCreditCode) && GL_DEBIT_CODE.equals(debitCreditCode)) {
                GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(buildMessageMapKeyPathForDebitCreditAmount(true), ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");
            }
            else {
                GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(buildMessageMapKeyPathForDebitCreditAmount(false), ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");
            }

            retval = false;
        }

        return retval;
    }
    
    /**
     * This method looks at the current full key path that exists in the MessageMap structure to determine how to build the error map
     * for the special journal voucher credit and debit fields since they don't conform to the standard pattern of accounting lines.
     * 
     * @param isDebit boolean to determine whether or not value isDebit or not
     * @return String represents error map key to use
     */
    protected String buildMessageMapKeyPathForDebitCreditAmount(boolean isDebit) {
        // determine if we are looking at a new line add or an update
        boolean isNewLineAdd = GlobalVariables.getMessageMap().getErrorPath().contains(NEW_SOURCE_ACCT_LINE_PROPERTY_NAME);
        isNewLineAdd |= GlobalVariables.getMessageMap().getErrorPath().contains(NEW_SOURCE_ACCT_LINE_PROPERTY_NAME);

        if (isNewLineAdd) {
            if (isDebit) {
                return DEBIT_AMOUNT_PROPERTY_NAME;
            }
            else {
                return CREDIT_AMOUNT_PROPERTY_NAME;
            }
        }
        else {
            String index = StringUtils.substringBetween(GlobalVariables.getMessageMap().getKeyPath("", true), SQUARE_BRACKET_LEFT, SQUARE_BRACKET_RIGHT);
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
