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

import org.kuali.kfs.fp.businessobject.AdvanceDepositDetail;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.BankCodeValidation;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class...
 */
public class AdvanceDepositValidation extends GenericValidation {
    protected AdvanceDepositDetail advanceDepositDetailForValidation;
    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        
        AdvanceDepositDetail advanceDeposit = getAdvanceDepositDetailForValidation();
        boolean isValid = true;

        // check that dollar amount is not zero before continuing
        if (isValid) {
            isValid = !advanceDeposit.getFinancialDocumentAdvanceDepositAmount().isZero();
            if (!isValid) {
                String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(AdvanceDepositDetail.class, KFSPropertyConstants.ADVANCE_DEPOSIT_AMOUNT);
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ADVANCE_DEPOSIT_AMOUNT, KFSKeyConstants.AdvanceDeposit.ERROR_DOCUMENT_ADVANCE_DEPOSIT_ZERO_AMOUNT, label);
            }
        }

        if (isValid) {
            isValid = BankCodeValidation.validate(advanceDeposit.getFinancialDocumentBankCode(), KFSPropertyConstants.FINANCIAL_DOCUMENT_BANK_CODE, true, false);
        }

        return isValid;
    }
    /**
     * Gets the advanceDepositDetailForValidation attribute. 
     * @return Returns the advanceDepositDetailForValidation.
     */
    public AdvanceDepositDetail getAdvanceDepositDetailForValidation() {
        return advanceDepositDetailForValidation;
    }
    /**
     * Sets the advanceDepositDetailForValidation attribute value.
     * @param advanceDepositDetailForValidation The advanceDepositDetailForValidation to set.
     */
    public void setAdvanceDepositDetailForValidation(AdvanceDepositDetail advanceDepositDetailForValidation) {
        this.advanceDepositDetailForValidation = advanceDepositDetailForValidation;
    }
}
