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
