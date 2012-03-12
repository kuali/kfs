/*
 * Copyright 2009 The Kuali Foundation
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

import org.kuali.kfs.fp.document.CreditCardReceiptDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validation which tests that the amount in credit card lines equals the amount in accounting lines
 * on the document.
 */
public class CreditCardReceiptDocumentTotalValidation extends GenericValidation {
    private CreditCardReceiptDocument creditCardReceiptDocumentForValidation;

    /**
     * For Credit Card Receipt documents, the document is balanced if the sum total of credit card receipts equals the sum total of
     * the accounting lines.
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        // make sure the document is in balance
        boolean isValid = creditCardReceiptDocumentForValidation.getSourceTotal().equals(creditCardReceiptDocumentForValidation.getTotalDollarAmount());

        if (!isValid) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.NEW_CREDIT_CARD_RECEIPT, KFSKeyConstants.CreditCardReceipt.ERROR_DOCUMENT_CREDIT_CARD_RECEIPT_OUT_OF_BALANCE);
        }

        return isValid;
    }

    /**
     * Gets the creditCardReceiptDocumentForValidation attribute. 
     * @return Returns the creditCardReceiptDocumentForValidation.
     */
    public CreditCardReceiptDocument getCreditCardReceiptDocumentForValidation() {
        return creditCardReceiptDocumentForValidation;
    }

    /**
     * Sets the creditCardReceiptDocumentForValidation attribute value.
     * @param creditCardReceiptDocumentForValidation The creditCardReceiptDocumentForValidation to set.
     */
    public void setCreditCardReceiptDocumentForValidation(CreditCardReceiptDocument creditCardReceiptDocumentForValidation) {
        this.creditCardReceiptDocumentForValidation = creditCardReceiptDocumentForValidation;
    }

}
