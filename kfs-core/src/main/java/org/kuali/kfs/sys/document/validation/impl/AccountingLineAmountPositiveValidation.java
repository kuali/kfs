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
package org.kuali.kfs.sys.document.validation.impl;

import static org.kuali.kfs.sys.KFSConstants.AMOUNT_PROPERTY_NAME;
import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_INVALID_NEGATIVE_AMOUNT_NON_CORRECTION;
import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_ZERO_AMOUNT;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validates an accounting line that, if the line is not a correction document, the line amount is a positive amount
 */
public class AccountingLineAmountPositiveValidation extends GenericValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountingLineAmountPositiveValidation.class);

    private AccountingDocument accountingDocumentForValidation;
    private AccountingLine accountingLineForValidation;

    /**
     * Check for zero amount, or negative on original (non-correction) document; no sign check for documents that are
     * corrections to previous documents
     * <strong>the accounting document must be the first parameter, the accounting line must be the second parameter</strong>
     * @see org.kuali.kfs.sys.document.validation.GenericValidation#validate(java.lang.Object[])
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        KualiDecimal amount = accountingLineForValidation.getAmount();
        String correctsDocumentId = accountingDocumentForValidation.getFinancialSystemDocumentHeader().getFinancialDocumentInErrorNumber();

        if (amount != null) {
            if (KualiDecimal.ZERO.compareTo(amount) == 0) { // amount == 0
                GlobalVariables.getMessageMap().putError(AMOUNT_PROPERTY_NAME, ERROR_ZERO_AMOUNT, "an accounting line");
                return false;
            }
            else {
                if (null == correctsDocumentId && KualiDecimal.ZERO.compareTo(amount) == 1) { // amount < 0
                    GlobalVariables.getMessageMap().putError(AMOUNT_PROPERTY_NAME, ERROR_INVALID_NEGATIVE_AMOUNT_NON_CORRECTION);
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Gets the accountingDocumentForValidation attribute.
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
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
