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

import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.sys.document.validation.BranchingValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class PurchasingAccountsPayableLineItemIndicatorBranchingValidation extends BranchingValidation{

    protected static final String IS_LINE_ITEM_INDICATOR = "isLineItemIndicator";
    protected static final String IS_NOT_LINE_ITEM_INDICATOR = "isNotLineItemIndicator";
    private PurApItem itemForValidation;
    
    @Override
    protected String determineBranch(AttributedDocumentEvent event) {
        if (itemForValidation.getItemType().isLineItemIndicator()) {
            return IS_LINE_ITEM_INDICATOR;
        } else {
            return IS_NOT_LINE_ITEM_INDICATOR;
        }
    }

    public PurApItem getItemForValidation() {
        return itemForValidation;
    }

    public void setItemForValidation(PurApItem itemForValidation) {
        this.itemForValidation = itemForValidation;
    }

}
