/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class PurchasingAccountsPayableProcessAccountValidation extends GenericValidation {

    private PurchasingAccountsPayableHasAccountsValidation hasAccountsValidation;
    private PurchasingAccountsPayableAccountPercentValidation accountPercentValidation;
    private PurchasingAccountsPayableUniqueAccountingStringsValidation accountingStringsValidation;          
    private PurApItem itemForValidation;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        
        hasAccountsValidation.setItemForValidation(itemForValidation);
        valid &= hasAccountsValidation.validate(event);
        
        if(valid){
            accountPercentValidation.setItemForValidation(itemForValidation);
            valid &= accountPercentValidation.validate(event);
        }
        
        accountingStringsValidation.setItemForValidation(itemForValidation);
        valid &= accountingStringsValidation.validate(event);
        
        return valid;
    }

    public PurchasingAccountsPayableHasAccountsValidation getHasAccountsValidation() {
        return hasAccountsValidation;
    }

    public void setHasAccountsValidation(PurchasingAccountsPayableHasAccountsValidation hasAccountsValidation) {
        this.hasAccountsValidation = hasAccountsValidation;
    }

    public PurchasingAccountsPayableAccountPercentValidation getAccountPercentValidation() {
        return accountPercentValidation;
    }

    public void setAccountPercentValidation(PurchasingAccountsPayableAccountPercentValidation accountPercentValidation) {
        this.accountPercentValidation = accountPercentValidation;
    }

    public PurchasingAccountsPayableUniqueAccountingStringsValidation getAccountingStringsValidation() {
        return accountingStringsValidation;
    }

    public void setAccountingStringsValidation(PurchasingAccountsPayableUniqueAccountingStringsValidation accountingStringsValidation) {
        this.accountingStringsValidation = accountingStringsValidation;
    }

    public PurApItem getItemForValidation() {
        return itemForValidation;
    }

    public void setItemForValidation(PurApItem itemForValidation) {
        this.itemForValidation = itemForValidation;
    }

}
