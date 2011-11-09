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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

public class PurchasingProcessTradeInValidation extends GenericValidation {
    
    /**
     * Validates that the total cost must be greater or equal to zero.
     * 
     * @param purDocument the purchasing document to be validated
     * @return boolean false if the total cost is less than zero.
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean isAssignedToTradeInItemFound = false;
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument)event.getDocument();
        for (PurApItem item : purapDocument.getItems()) {
            // Refresh the item type for validation.
            item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);

            if (item.getItemType().isLineItemIndicator()) {
                if (item.getItemAssignedToTradeInIndicator()) {
                    isAssignedToTradeInItemFound = true;
                    break;
                }           
            }
        }
        if (!isAssignedToTradeInItemFound) {
            PurApItem tradeInItem = purapDocument.getTradeInItem();
            if (tradeInItem != null && tradeInItem.getItemUnitPrice() != null) {
                GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_TRADE_IN_NEEDS_TO_BE_ASSIGNED);
                return false;
            }
        }
        return true;
    }
}
