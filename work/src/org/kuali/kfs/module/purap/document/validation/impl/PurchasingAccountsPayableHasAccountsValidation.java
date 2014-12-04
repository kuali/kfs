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
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

public class PurchasingAccountsPayableHasAccountsValidation extends GenericValidation {

    private PurApItem itemForValidation;
    
    /** 
     * Verifies that the item has accounts.
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;                
        
        
        if (itemForValidation.getSourceAccountingLines().isEmpty()) {
            valid = false;
            GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_INCOMPLETE, itemForValidation.getItemIdentifierString());
        }

      //refresh the accounts if they do exist...
        for (PurApAccountingLine account : itemForValidation.getSourceAccountingLines()) {
            account.refreshNonUpdateableReferences();
        }
        
        return valid;
    }

    public PurApItem getItemForValidation() {
        return itemForValidation;
    }

    public void setItemForValidation(PurApItem itemForValidation) {
        this.itemForValidation = itemForValidation;
    }

}
