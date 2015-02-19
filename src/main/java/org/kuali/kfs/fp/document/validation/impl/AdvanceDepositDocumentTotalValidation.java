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

import org.kuali.kfs.fp.document.AdvanceDepositDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validation for the Advance Deposit document that checks the total amount of the document.
 */
public class AdvanceDepositDocumentTotalValidation extends GenericValidation {
    private AdvanceDepositDocument advanceDepositDocumentForValidation;

    /**
     * For the Advance Deposit document, the document is balanced if the sum total of deposits - positive or negative - equals the sum total of the
     * accounting lines.
     * 
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        // make sure the document is in balance
        boolean isValid = advanceDepositDocumentForValidation.getSourceTotal().equals(advanceDepositDocumentForValidation.getTotalDollarAmount());

        if (!isValid) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.NEW_ADVANCE_DEPOSIT, KFSKeyConstants.AdvanceDeposit.ERROR_DOCUMENT_ADVANCE_DEPOSIT_OUT_OF_BALANCE);
        }

        return isValid;
    }

    /**
     * Gets the advanceDepositDocumentForValidation attribute. 
     * @return Returns the advanceDepositDocumentForValidation.
     */
    public AdvanceDepositDocument getAdvanceDepositDocumentForValidation() {
        return advanceDepositDocumentForValidation;
    }

    /**
     * Sets the advanceDepositDocumentForValidation attribute value.
     * @param advanceDepositDocumentForValidation The advanceDepositDocumentForValidation to set.
     */
    public void setAdvanceDepositDocumentForValidation(AdvanceDepositDocument advanceDepositDocumentForValidation) {
        this.advanceDepositDocumentForValidation = advanceDepositDocumentForValidation;
    }

}
