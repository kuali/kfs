/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.purap.rules;

import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapConstants.ItemFields;
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;

public class AccountsPayableDocumentRuleBase extends PurchasingAccountsPayableDocumentRuleBase {
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);
        // TODO: Add more validations in here
        return valid;
    }

    public boolean processItemValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processItemValidation(purapDocument);
       
        for (PurchasingApItem item : purapDocument.getItems()) {
            String identifierString = (item.getItemType().isItemTypeAboveTheLineIndicator() ? "Item " + item.getItemLineNumber().toString() : item.getItemType().getItemTypeDescription());
            if (item.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE)) {
                valid &= validateItemTypeItems((PaymentRequestItem) item, identifierString);
            }
        }
        
        return valid;
    }

    private boolean validateItemTypeItems(PaymentRequestItem item, String identifierString) {
        boolean valid = true;

        // Currently Invoice Unit Price is not allowed to be NULL on screen; 
        // must be either a positive number or NULL for DB        
        if (ObjectUtils.isNotNull(item.getItemUnitPrice()) && item.getItemUnitPrice().signum() == -1) {
           // if unit price is negative give an error                       
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_AMOUNT_BELOW_ZERO, ItemFields.UNIT_COST, identifierString);
        }
        if (ObjectUtils.isNotNull(item.getExtendedPrice()) && item.getExtendedPrice().isNegative()) {
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_AMOUNT_BELOW_ZERO, ItemFields.INVOICE_EXTENDED_PRICE, identifierString);
        }
        return valid;
    }
}
