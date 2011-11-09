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
