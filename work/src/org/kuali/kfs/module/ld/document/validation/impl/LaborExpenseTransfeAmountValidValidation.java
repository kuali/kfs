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
package org.kuali.kfs.module.ld.document.validation.impl;

import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * determine whether the amount in the account is not zero
 * 
 * @param accountingDocument the given document
 * @param accountingLine the given accounting line
 * @return true determine whether the amount in the account is not zero; otherwise, false
 */
public class LaborExpenseTransfeAmountValidValidation extends GenericValidation {
    private AccountingLine accountingLineForValidation;
    private AccountingDocument accountingDocumentForValidation;
    
    /**
     * Validates that an accounting line whether the expired account in the target accounting line 
     * can be used.
     * <strong>Expects an accounting line as the first a parameter</strong>
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        
        AccountingLine accountingLine = getAccountingLineForValidation();
        AccountingDocument accountingDocumentForValidation = getAccountingDocumentForValidation();
      
        // not allow the zero amount on the account lines.
        if (!isAmountValid(accountingDocumentForValidation,accountingLine)) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.AMOUNT, KFSKeyConstants.ERROR_ZERO_AMOUNT, "an accounting line");
            return false;
        }
        
        return result;
    }

    /**
     * determine whether the amount in the account is not zero.
     * 
     * @param accountingDocument the given accounting line
     * @return true if the amount is not zero; otherwise, false
     */
    
    public boolean isAmountValid(AccountingDocument document, AccountingLine accountingLine) {
        KualiDecimal amount = accountingLine.getAmount();

        // Check for zero amount
        if (amount.isZero()) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.AMOUNT, KFSKeyConstants.ERROR_ZERO_AMOUNT, "an accounting line");
            return false;
        }
        return true;
    }
        
    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
    
    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }
}
