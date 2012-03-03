/*
 * Copyright 2008-2009 The Kuali Foundation
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

import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.ObjectUtils;

public class PurchasingAccountsPayableProcessAccountValidation extends GenericValidation {

    private PurchasingAccountsPayableHasAccountsValidation hasAccountsValidation;
    private PurchasingAccountsPayableAccountPercentValidation accountPercentValidation;
    private PurchasingAccountsPayableUniqueAccountingStringsValidation accountingStringsValidation;
    private PurApItem itemForValidation;
    private PurchasingAccountsPayableAccountAtleastOneLineHasPercentValidation accountHasAtleastOnePercentValidation;
    private PurchasingAccountingLineAmountValidation accountLineAmountValidation;
    private PurchasingAccountsPayableAccountTotalValidation accountTotalValidation;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;

        if (ObjectUtils.isNull(itemForValidation)) {
            return valid;
        }

        hasAccountsValidation.setItemForValidation(itemForValidation);
        valid &= hasAccountsValidation.validate(event);

        if (valid) {
            getAccountHasAtlestOnePercentValidation().setItemForValidation(itemForValidation);
            valid &= getAccountHasAtlestOnePercentValidation().validate(event);
        }

        if(valid){
            for (PurApAccountingLine account : itemForValidation.getSourceAccountingLines()) {
                getAccountLineAmountValidation().setUpdatedAccountingLine(account);
                valid &= getAccountLineAmountValidation().validate(event);
                if (!valid) {
                    break;
                }
            }
        }

        if(valid){
            accountPercentValidation.setItemForValidation(itemForValidation);
            valid &= accountPercentValidation.validate(event);
        }

        if(valid){
            accountTotalValidation.setItemForValidation(itemForValidation);
            valid &= accountTotalValidation.validate(event);
        }

        if(valid){
            accountingStringsValidation.setItemForValidation(itemForValidation);
            valid &= accountingStringsValidation.validate(event);
        }

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

    /**
     * @return Returns the accountHasAtleastOnePercentValidation
     */

    public PurchasingAccountsPayableAccountAtleastOneLineHasPercentValidation getAccountHasAtlestOnePercentValidation() {
        return accountHasAtleastOnePercentValidation;
    }

    /**
     * Sets the accountHasAtleastOnePercentValidation attribute.
     *
     * @param accountHasAtleastOnePercentValidation The accountHasAtleastOnePercentValidation to set.
     */
    public void setAccountHasAtleastOnePercentValidation(PurchasingAccountsPayableAccountAtleastOneLineHasPercentValidation accountHasAtleastOnePercentValidation) {
        this.accountHasAtleastOnePercentValidation = accountHasAtleastOnePercentValidation;
    }

    /**
     * Gets the accountLineAmountValidation attribute.
     *
     * @return Returns the accountLineAmountValidation
     */

    public PurchasingAccountingLineAmountValidation getAccountLineAmountValidation() {
        return accountLineAmountValidation;
    }

    /**
     * Sets the accountLineAmountValidation attribute.
     *
     * @param accountLineAmountValidation The accountLineAmountValidation to set.
     */
    public void setAccountLineAmountValidation(PurchasingAccountingLineAmountValidation accountLineAmountValidation) {
        this.accountLineAmountValidation = accountLineAmountValidation;
    }

    /**
     * Gets the accountTotalValidation attribute.
     *
     * @return Returns the accountTotalValidation
     */

    public PurchasingAccountsPayableAccountTotalValidation getAccountTotalValidation() {
        return accountTotalValidation;
    }

    /**
     * Sets the accountTotalValidation attribute.
     *
     * @param accountTotalValidation The accountTotalValidation to set.
     */
    public void setAccountTotalValidation(PurchasingAccountsPayableAccountTotalValidation accountTotalValidation) {
        this.accountTotalValidation = accountTotalValidation;
    }
}
