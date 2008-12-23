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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.List;

import org.kuali.kfs.integration.purap.PurApItem;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;

public class VendorCreditMemoCalculateValidation extends GenericValidation {

    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        VendorCreditMemoDocument cmDocument = (VendorCreditMemoDocument) event.getDocument();

        // flag line just gives warnings
        flagLineItemTotals(cmDocument.getItems());

        valid = validateTotalMatchesVendorAmount(cmDocument);
        valid = valid && validateTotalOverZero(cmDocument);

        return valid;
    }

    /**
     * Compares the extended price of each item to the calculated price and if different adds a warning message.
     * 
     * @param itemList - list of items to check
     */
    private void flagLineItemTotals(List<PurApItem> itemList) {
        for (int i = 0; i < itemList.size(); i++) {
            CreditMemoItem item = (CreditMemoItem) itemList.get(i);
            if (item.getItemQuantity() != null && item.getExtendedPrice()!=null && item.calculateExtendedPrice().compareTo(item.getExtendedPrice()) != 0) {
                String errorKey = KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM + "[" + Integer.toString(i) + "]." + PurapPropertyConstants.EXTENDED_PRICE;
                GlobalVariables.getErrorMap().putError(errorKey, PurapKeyConstants.ERROR_PAYMENT_REQUEST_ITEM_TOTAL_NOT_EQUAL);
            }
        }
    }

    /**
     * Validates the credit memo total matches the vendor credit memo amount. If the unmatched override is set to true, user has
     * choosen to accept the difference and there should be no error added.
     * 
     * @param cmDocument - credit memo document
     * @return boolean - true if amounts match, false if they do not match
     */
    public boolean validateTotalMatchesVendorAmount(VendorCreditMemoDocument cmDocument) {
        boolean valid = true;

        if (cmDocument.getGrandTotal().compareTo(cmDocument.getCreditMemoAmount()) != 0 && !cmDocument.isUnmatchedOverride()) {
            GlobalVariables.getMessageList().add(PurapKeyConstants.ERROR_CREDIT_MEMO_INVOICE_AMOUNT_NONMATCH);
            valid = false;
        }

        return valid;
    }

    /**
     * Validates the credit memo total is over zero.
     * 
     * @param cmDocument - credit memo document
     * @return boolean - true if amount is over zero, false if not
     */
    public boolean validateTotalOverZero(VendorCreditMemoDocument cmDocument) {
        boolean valid = true;

        if (!cmDocument.getGrandTotal().isPositive()) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM, PurapKeyConstants.ERROR_CREDIT_MEMO_TOTAL_ZERO);
            valid = false;
        }

        return valid;
    }

}
