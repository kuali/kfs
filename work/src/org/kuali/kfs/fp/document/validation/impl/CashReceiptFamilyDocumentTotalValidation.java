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

import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.fp.document.CashReceiptFamilyBase;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validation for the Cash Receipt family of documents that checks the total amount of the document.
 */
public class CashReceiptFamilyDocumentTotalValidation extends GenericValidation {
    private CashReceiptFamilyBase cashReceiptFamilyDocumentForValidation;

    /**
     * For Cash Receipt documents, the document is balanced if the sum total of checks and cash and coin equals the sum total of the
     * accounting lines. In addition, the sum total of checks and cash and coin must be greater than zero.
     * 
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        // make sure that cash reconciliation total is greater than zero
        boolean isValid = getCashReceiptFamilyDocumentForValidation().getTotalDollarAmount().compareTo(KualiDecimal.ZERO) > 0;
        if (!isValid) {
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + KFSPropertyConstants.SUM_TOTAL_AMOUNT, KFSKeyConstants.CashReceipt.ERROR_DOCUMENT_CASH_RECEIPT_NO_CASH_RECONCILIATION_TOTAL);
        }

        if (isValid) {
            // make sure the document is in balance
            isValid = getCashReceiptFamilyDocumentForValidation().getSourceTotal().compareTo(getCashReceiptFamilyDocumentForValidation().getTotalDollarAmount().subtract( 
                    ((CashReceiptDocument)getCashReceiptFamilyDocumentForValidation()).getTotalChangeAmount())) == 0;

            if (!isValid) {
                GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + KFSPropertyConstants.SUM_TOTAL_AMOUNT, KFSKeyConstants.CashReceipt.ERROR_DOCUMENT_CASH_RECEIPT_BALANCE);
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
