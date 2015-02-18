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

import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.fp.document.CashReceiptFamilyBase;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants.CashReceipt;
import org.kuali.kfs.sys.document.validation.RouteNodeValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;

public class CashReceiptCashManagerValidation extends RouteNodeValidation {

    private CashReceiptFamilyBase cashReceiptDocumentForValidation;

    /**
     * Validates that the confirmed net total amount is equal to the original net total amount when cash manager approves the CR.
     * A cash manager could adjust check, currency, coin, as well as change currency and coin amounts, the sub total amount in
     * each of these categories could change, including the money in total and the change out total, as long as the final net total
     * comes even with the original total. This condition must satisfy because he can't change the total accounting line amount,
     * which must match the document total amount when the document route.
     * For Cash Receipt, since there could be change request, the total amount refers to the net total after change total is taken off.
     * Following are the formula for various total amounts in Cash Receipt:
     *      net total = money in - money out
     *      money in = check + currency + coin
     *      money out = change currency + change coin
     *
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        // As explained above, we need to check the net total instead, with change out total taken off.
        CashReceiptDocument crDoc = (CashReceiptDocument) getCashReceiptDocumentForValidation();
        KualiDecimal originalTotal = crDoc.getTotalNetAmount();

        if (originalTotal.compareTo(crDoc.getTotalConfirmedNetAmount()) != 0) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, CashReceipt.ERROR_CONFIRMED_TOTAL, originalTotal.toString());
            return false;
        }

        return true;
    }

    public CashReceiptFamilyBase getCashReceiptDocumentForValidation() {
        return cashReceiptDocumentForValidation;
    }

    public void setCashReceiptDocumentForValidation(CashReceiptFamilyBase cashReceiptDocumentForValidation) {
        this.cashReceiptDocumentForValidation = cashReceiptDocumentForValidation;
    }


}
