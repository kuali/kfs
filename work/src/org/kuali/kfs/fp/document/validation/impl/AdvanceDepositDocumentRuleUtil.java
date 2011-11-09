/*
 * Copyright 2006 The Kuali Foundation
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
import org.kuali.kfs.sys.document.validation.impl.BankCodeValidation;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

/**
 * Common Advance Deposit Document rule utilities.
 */
public class AdvanceDepositDocumentRuleUtil {
    /**
     * This method method will invoke the data dictionary validation for a AdvanceDepositDetail bo instance, in addition to checking
     * existence of the Bank and BankAccount attributes that hang off of it. This method assumes that the document hierarchy for the
     * error map path is managed outside of this call.
     * 
     * @param advanceDeposit advanceDeposit object being validated
     * @return boolean returns true if dollar amount is not 0 and bank-related references (i.e. bank and bank account) are valid
     */
    public static boolean validateAdvanceDeposit(AdvanceDepositDetail advanceDeposit) {
        MessageMap errorMap = GlobalVariables.getMessageMap();
        int originalErrorCount = errorMap.getErrorCount();

        // call the DD validation which checks basic data integrity
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(advanceDeposit);
        boolean isValid = (errorMap.getErrorCount() == originalErrorCount);

        // check that dollar amount is not zero before continuing
        if (isValid) {
            isValid = !advanceDeposit.getFinancialDocumentAdvanceDepositAmount().isZero();
            if (!isValid) {
                String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(AdvanceDepositDetail.class, KFSPropertyConstants.ADVANCE_DEPOSIT_AMOUNT);
                errorMap.putError(KFSPropertyConstants.ADVANCE_DEPOSIT_AMOUNT, KFSKeyConstants.AdvanceDeposit.ERROR_DOCUMENT_ADVANCE_DEPOSIT_ZERO_AMOUNT, label);
            }
        }

        if (isValid) {
            isValid = BankCodeValidation.validate(advanceDeposit.getFinancialDocumentBankCode(), KFSPropertyConstants.FINANCIAL_DOCUMENT_BANK_CODE, true, false);
        }

        return isValid;
    }
}
