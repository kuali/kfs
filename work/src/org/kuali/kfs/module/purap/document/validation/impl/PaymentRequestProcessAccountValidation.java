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

import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.ObjectUtils;

public class PaymentRequestProcessAccountValidation extends GenericValidation {

    private PurchasingAccountsPayableHasAccountsValidation hasAccountsValidation;
    private PurchasingAccountsPayableAccountPercentValidation accountPercentValidation;
    private PurchasingAccountsPayableAccountTotalValidation accountTotalValidation;
    private PurchasingAccountsPayableUniqueAccountingStringsValidation accountingStringsValidation;
    private PurApItem itemForValidation;
    private PurchasingAccountsPayableAccountAtleastOneLineHasPercentValidation accountHasAtleastOnePercentValidation;
    private PurchasingAccountingLineAmountValidation accountLineAmountValidation;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;

        if (ObjectUtils.isNull(itemForValidation)) {
            return valid;
        }

        hasAccountsValidation.setItemForValidation(itemForValidation);
        valid &= hasAccountsValidation.validate(event);

        if(valid){
            accountPercentValidation.setItemForValidation(itemForValidation);
            valid &= accountPercentValidation.validate(event);
        }

        if(valid){
                accountTotalValidation.setItemForValidation(itemForValidation);
                valid &= accountTotalValidation.validate(event);
        }

        accountingStringsValidation.setItemForValidation(itemForValidation);
        valid &= accountingStringsValidation.validate(event);

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

        return valid;
    }

    public PurchasingAccountsPayableHasAccountsValidation getHasAccountsValidation() {
        return hasAccountsValidation;
    }

    public void setHasAccountsValidation(PurchasingAccountsPayableHasAccountsValidation hasAccountsValidation) {
        this.hasAccountsValidation = hasAccountsValidation;
    }


    public PurchasingAccountsPayableAccountTotalValidation getAccountTotalValidation() {
        return accountTotalValidation;
    }

    public void setAccountTotalValidation(PurchasingAccountsPayableAccountTotalValidation accountTotalValidation) {
        this.accountTotalValidation = accountTotalValidation;
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

    public PurchasingAccountsPayableAccountPercentValidation getAccountPercentValidation() {
        return accountPercentValidation;
    }

    public void setAccountPercentValidation(PurchasingAccountsPayableAccountPercentValidation accountPercentValidation) {
        this.accountPercentValidation = accountPercentValidation;
    }

}
