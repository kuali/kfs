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
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;
import org.kuali.kfs.validation.GenericValidation;
import org.kuali.module.financial.document.CashReceiptFamilyBase;

/**
 * Validation for the Cash Receipt family of documents that checks the total amount of the document.
 */
public class CashReceiptFamilyDocumentTotalValidation extends GenericValidation {
    private CashReceiptFamilyBase cashReceiptFamilyDocumentForValidation;

    /**
     * For Cash Receipt documents, the document is balanced if the sum total of checks and cash and coin equals the sum total of the
     * accounting lines. In addition, the sum total of checks and cash and coin must be greater than zero.
     * 
     * @see org.kuali.kfs.validation.Validation#validate(org.kuali.kfs.rule.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        // make sure that cash reconciliation total is greater than zero
        boolean isValid = getCashReceiptFamilyDocumentForValidation().getTotalDollarAmount().compareTo(KualiDecimal.ZERO) > 0;
        if (!isValid) {
            GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX + KFSPropertyConstants.SUM_TOTAL_AMOUNT, KFSKeyConstants.CashReceipt.ERROR_DOCUMENT_CASH_RECEIPT_NO_CASH_RECONCILIATION_TOTAL);
        }

        if (isValid) {
            // make sure the document is in balance
            isValid = getCashReceiptFamilyDocumentForValidation().getSourceTotal().compareTo(getCashReceiptFamilyDocumentForValidation().getTotalDollarAmount()) == 0;

            if (!isValid) {
                GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX + KFSPropertyConstants.SUM_TOTAL_AMOUNT, KFSKeyConstants.CashReceipt.ERROR_DOCUMENT_CASH_RECEIPT_BALANCE);
            }
        }

        return isValid;
    }

    /**
     * Gets the cashReceiptFamilyDocumentForValidation attribute. 
     * @return Returns the cashReceiptFamilyDocumentForValidation.
     */
    public CashReceiptFamilyBase getCashReceiptFamilyDocumentForValidation() {
        return cashReceiptFamilyDocumentForValidation;
    }

    /**
     * Sets the cashReceiptFamilyDocumentForValidation attribute value.
     * @param cashReceiptFamilyDocumentForValidation The cashReceiptFamilyDocumentForValidation to set.
     */
    public void setCashReceiptFamilyDocumentForValidation(CashReceiptFamilyBase cashReceiptFamilyDocumentForValidation) {
        this.cashReceiptFamilyDocumentForValidation = cashReceiptFamilyDocumentForValidation;
    }
}
