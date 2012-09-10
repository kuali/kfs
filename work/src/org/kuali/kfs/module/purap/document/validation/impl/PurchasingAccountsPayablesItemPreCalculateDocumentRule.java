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

import java.math.BigDecimal;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.validation.PurchasingAccountsPayableItemPreCalculationRule;
import org.kuali.rice.kns.rules.DocumentRuleBase;
import org.kuali.rice.krad.util.GlobalVariables;

public class PurchasingAccountsPayablesItemPreCalculateDocumentRule extends DocumentRuleBase implements PurchasingAccountsPayableItemPreCalculationRule {
    
    public boolean checkPercentOrTotalAmountsEqual(PurApItem item) {
        boolean valid = true;
        
        valid &= validatePercent(item);
        
        if (valid) {
            valid &= validateTotalAmount(item);
        }
        
        return valid;
    }
    
    /** 
     * Verifies account percent. If the total percent does not equal 100, the validation fails. 
     */
    public boolean validatePercent(PurApItem item) {
        boolean valid = true;
        
        // validate that the percents total 100 for each item
        BigDecimal totalPercent = BigDecimal.ZERO;
        BigDecimal desiredPercent = new BigDecimal("100");
        for (PurApAccountingLine account : item.getSourceAccountingLines()) {
            if (account.getAccountLinePercent() != null) {
                totalPercent = totalPercent.add(account.getAccountLinePercent());
            }
            else {
                totalPercent = totalPercent.add(BigDecimal.ZERO);
            }
        }
        if (desiredPercent.compareTo(totalPercent) != 0) {
            GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_TOTAL, item.getItemIdentifierString());
            valid = false;
        }

        return valid;
    }
    
    /** 
     * Verifies account total. If the total does not equal item total,
     *  the validation fails. 
     */
    public boolean validateTotalAmount(PurApItem item) {
        boolean valid = true;
        
     // validate that the amount total 
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal desiredAmount = 
            (item.getTotalAmount() == null) ? new BigDecimal(0) : item.getTotalAmount().bigDecimalValue();
        for (PurApAccountingLine account : item.getSourceAccountingLines()) {
            if (account.getAmount() != null) {
                totalAmount = totalAmount.add(account.getAmount().bigDecimalValue());
            }
            else {
                totalAmount = totalAmount.add(BigDecimal.ZERO);
            }
        }
        if (desiredAmount.compareTo(totalAmount) != 0) {
            GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_TOTAL_AMOUNT, item.getItemIdentifierString(),desiredAmount.toString());
            valid = false;
        }

        return valid;
    }
}
