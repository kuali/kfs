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
