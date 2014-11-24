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

import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.kuali.kfs.fp.document.AdvanceDepositDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class...
 */
public class AdvanceDepositMinimumAdvanceDepositValidation extends GenericValidation {
    private AdvanceDepositDocument accountingDocumentForValidation;
    private int requiredMinimumCount;
    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        AdvanceDepositDocument ad = getAccountingDocumentForValidation();
        if (ad.getAdvanceDeposits().size() < requiredMinimumCount) {
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX, KFSKeyConstants.AdvanceDeposit.ERROR_DOCUMENT_ADVANCE_DEPOSIT_REQ_NUMBER_DEPOSITS_NOT_MET);
            return false;
        }
        return true;
    }
    /**
     * Gets the documentForValidation attribute. 
     * @return Returns the documentForValidation.
     */
    public AdvanceDepositDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }
    /**
     * Sets the documentForValidation attribute value.
     * @param documentForValidation The documentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AdvanceDepositDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
    /**
     * Gets the requiredMinimumCount attribute. 
     * @return Returns the requiredMinimumCount.
     */
    public int getRequiredMinimumCount() {
        return requiredMinimumCount;
    }
    /**
     * Sets the requiredMinimumCount attribute value.
     * @param requiredMinimumCount The requiredMinimumCount to set.
     */
    public void setRequiredMinimumCount(int requiredMinimumCount) {
        this.requiredMinimumCount = requiredMinimumCount;
    }

}
