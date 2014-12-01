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

import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validation that checks that a check's amount is not zero.
 */
public class CashReceiptCheckAmountNotZeroValidation extends GenericValidation {
    private Check checkForValidation;

    /**
     * Verifies that a check amount is not zero.
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        if (getCheckForValidation().getAmount().isZero()) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.CHECK_AMOUNT, KFSKeyConstants.CashReceipt.ERROR_ZERO_CHECK_AMOUNT, KFSPropertyConstants.CHECKS);
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
