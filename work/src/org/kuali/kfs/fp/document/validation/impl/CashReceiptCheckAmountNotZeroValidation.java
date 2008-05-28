/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.financial.document.validation.impl;

import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;
import org.kuali.kfs.validation.GenericValidation;
import org.kuali.module.financial.bo.Check;

/**
 * Validation that checks that a check's amount is not zero.
 */
public class CashReceiptCheckAmountNotZeroValidation extends GenericValidation {
    private Check checkForValidation;

    /**
     * Verifies that a check amount is not zero.
     * @see org.kuali.kfs.validation.Validation#validate(org.kuali.kfs.rule.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        if (getCheckForValidation().getAmount().isZero()) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CHECK_AMOUNT, KFSKeyConstants.CashReceipt.ERROR_ZERO_CHECK_AMOUNT, KFSPropertyConstants.CHECKS);
            return false;
        }
        return true;
    }

    /**
     * Gets the checkForValidation attribute. 
     * @return Returns the checkForValidation.
     */
    public Check getCheckForValidation() {
        return checkForValidation;
    }

    /**
     * Sets the checkForValidation attribute value.
     * @param checkForValidation The checkForValidation to set.
     */
    public void setCheckForValidation(Check checkForValidation) {
        this.checkForValidation = checkForValidation;
    }
}
