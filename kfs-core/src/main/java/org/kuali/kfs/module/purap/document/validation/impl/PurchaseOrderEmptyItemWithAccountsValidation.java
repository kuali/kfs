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
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

public class PurchaseOrderEmptyItemWithAccountsValidation extends GenericValidation {

    private PurchaseOrderItem itemForValidation;

    /**
     * Validates that the item detail must not be empty if its account is not empty and its item type is ITEM.
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        if (itemForValidation.getItemType().isLineItemIndicator() && itemForValidation.isItemDetailEmpty() && !itemForValidation.isAccountListEmpty()) {
            valid = false;
            GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_NOT_ALLOWED, getItemForValidation().getItemIdentifierString());
        }

        return valid;
    }

    public PurchaseOrderItem getItemForValidation() {
        return itemForValidation;
    }

    public void setItemForValidation(PurchaseOrderItem itemForValidation) {
        this.itemForValidation = itemForValidation;
    }

}
