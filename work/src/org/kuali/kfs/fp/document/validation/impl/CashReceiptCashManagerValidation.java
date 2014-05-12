/*
 * Copyright 2011 The Kuali Foundation.
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
        /* FIXME FIXED by KFSCNTRB-1793
         * The previous code didn't include change request when comparing the original vs confirmed total.
         * As explained above, we need to check the net total instead, with change out total taken off.
         */
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
