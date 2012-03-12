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

import static org.kuali.kfs.sys.KFSKeyConstants.AuxiliaryVoucher.ERROR_INVALID_ACCRUAL_REVERSAL_DATE;
import static org.kuali.kfs.sys.KFSPropertyConstants.REVERSAL_DATE;

import org.kuali.kfs.fp.document.AuxiliaryVoucherDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * A validation that checks the reversal date on the AuxiliaryVoucher.
 */
public class AuxiliaryVoucherReversalDateValidation extends GenericValidation {
    private AuxiliaryVoucherDocument auxiliaryVoucherDocumentForValidation;
    
    /**
     * This method verifies that the user entered a reversal date, but only if it's an accrual.
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        if (getAuxiliaryVoucherDocumentForValidation().isAccrualType() && getAuxiliaryVoucherDocumentForValidation().getReversalDate() == null) {
            GlobalVariables.getMessageMap().putError(REVERSAL_DATE, ERROR_INVALID_ACCRUAL_REVERSAL_DATE);
            return false;
        }

        return true;
    }

    /**
     * Gets the auxiliaryVoucherDocumentForValidation attribute. 
     * @return Returns the auxiliaryVoucherDocumentForValidation.
     */
    public AuxiliaryVoucherDocument getAuxiliaryVoucherDocumentForValidation() {
        return auxiliaryVoucherDocumentForValidation;
    }

    /**
     * Sets the auxiliaryVoucherDocumentForValidation attribute value.
     * @param auxiliaryVoucherDocumentForValidation The auxiliaryVoucherDocumentForValidation to set.
     */
    public void setAuxiliaryVoucherDocumentForValidation(AuxiliaryVoucherDocument auxiliaryVoucherForValidation) {
        this.auxiliaryVoucherDocumentForValidation = auxiliaryVoucherForValidation;
    }
}
