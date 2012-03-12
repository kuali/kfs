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
