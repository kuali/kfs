/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
