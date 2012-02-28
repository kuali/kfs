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
