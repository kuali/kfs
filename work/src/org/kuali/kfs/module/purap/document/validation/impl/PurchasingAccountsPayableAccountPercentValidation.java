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

public class PurchasingAccountsPayableAccountPercentValidation extends GenericValidation {

    private PurApItem itemForValidation;
    
    /** 
     * Verifies account percent. If the total percent does not equal 100, the validation fails. 
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        boolean isAnyAccountingLineZero = false;
        // validate that the percents total 100 for each item
        BigDecimal totalPercent = BigDecimal.ZERO;
        BigDecimal desiredPercent = new BigDecimal("100");
        for (PurApAccountingLine account : itemForValidation.getSourceAccountingLines()) {
            if(account.getAccountLinePercent() != null && account.getAccountLinePercent().compareTo(BigDecimal.ZERO) == 0){
                isAnyAccountingLineZero = true;
            }
            if (account.getAccountLinePercent() != null) {
                totalPercent = totalPercent.add(account.getAccountLinePercent());
            }
            else {
                totalPercent = totalPercent.add(BigDecimal.ZERO);
            }
        }
        if (desiredPercent.compareTo(totalPercent) != 0) {
            GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_TOTAL, itemForValidation.getItemIdentifierString());
            valid = false;
        }
        
        //MSU Contribution KFSMI-8470 DTT-3147 KFSCNTRB-962
        if(isAnyAccountingLineZero && itemForValidation.getSourceAccountingLines().size() > 1){
            GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_ZERO, itemForValidation.getItemIdentifierString());
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
