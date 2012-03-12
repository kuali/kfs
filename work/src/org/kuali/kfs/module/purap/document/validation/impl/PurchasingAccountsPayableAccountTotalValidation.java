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
