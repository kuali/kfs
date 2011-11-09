/*
 * Copyright 2009 The Kuali Foundation
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
