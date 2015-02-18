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
package org.kuali.kfs.fp.document.validation.impl;

import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineValueAllowedValidation;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * A validation for the transfer of funds document that makes sure that an object code in any accounting line represents either income or expense.
 */
public class TransferOfFundsObjectCodeValueAllowedValidation extends AccountingLineValueAllowedValidation {
    private DebitDeterminerService debitDeterminerService;

    /**
     * Overrides the parent to make sure that the chosen object code's object code is Income/Expense.
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingLineValueAllowedValidation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        // TODO JAMES DO WE NEED THIS YET?
        boolean isObjectCodeAllowed = super.validate(event);

        if (!debitDeterminerService.isIncome(getAccountingLineForValidation()) && !debitDeterminerService.isExpense(getAccountingLineForValidation())) {
            GlobalVariables.getMessageMap().putError("financialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_TOF_INVALID_OBJECT_TYPE_CODES, new String[] { getAccountingLineForValidation().getObjectCode().getFinancialObjectTypeCode(), getAccountingLineForValidation().getObjectCode().getFinancialObjectSubTypeCode() });
            isObjectCodeAllowed = false;
        }

        return isObjectCodeAllowed;
    }

    /**
     * Gets the debitDeterminerService attribute. 
     * @return Returns the debitDeterminerService.
     */
    public DebitDeterminerService getDebitDeterminerService() {
        return debitDeterminerService;
    }

    /**
     * Sets the debitDeterminerService attribute value.
     * @param debitDeterminerService The debitDeterminerService to set.
     */
    public void setDebitDeterminerService(DebitDeterminerService debitDeterminerService) {
        this.debitDeterminerService = debitDeterminerService;
    }
}
