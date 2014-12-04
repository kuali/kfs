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

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class PurchasingBelowTheLineItemNoUnitCostValidation extends GenericValidation {

    private PurApItem itemForValidation;
    
    /**
     * Validates that if the item unit price is null and the source accounting lines is not empty, add error message and return
     * false.
     */
    public boolean validate(AttributedDocumentEvent event) {
        if (ObjectUtils.isNull(itemForValidation.getItemUnitPrice()) && ObjectUtils.isNotNull(itemForValidation.getSourceAccountingLines()) && !itemForValidation.getSourceAccountingLines().isEmpty()) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE_NO_UNIT_COST, itemForValidation.getItemIdentifierString());

            return false;
        }

        return true;
    }

    public PurApItem getItemForValidation() {
        return itemForValidation;
    }

    public void setItemForValidation(PurApItem itemForValidation) {
        this.itemForValidation = itemForValidation;
    }

}
