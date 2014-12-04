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

import java.math.BigDecimal;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

public class PurchasingAccountsPayableAccountTotalValidation extends GenericValidation {

    private PurApItem itemForValidation;
    
    /** 
     * Verifies account percent. If the total percent does not equal 100, the validation fails. 
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        
     // validate that the amount total 
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal desiredAmount = 
            (itemForValidation.getTotalAmount() == null) ? new BigDecimal(0) : itemForValidation.getTotalAmount().bigDecimalValue();
        for (PurApAccountingLine account : itemForValidation.getSourceAccountingLines()) {
            if (account.getAmount() != null) {
                totalAmount = totalAmount.add(account.getAmount().bigDecimalValue());
            }
            else {
                totalAmount = totalAmount.add(BigDecimal.ZERO);
            }
        }
        if (desiredAmount.compareTo(totalAmount) != 0) {
            GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_TOTAL_AMOUNT, itemForValidation.getItemIdentifierString(),desiredAmount.toString());
            valid = false;
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
