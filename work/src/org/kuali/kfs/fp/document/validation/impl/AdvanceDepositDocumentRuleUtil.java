/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.financial.rules;

import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.bo.AdvanceDepositDetail;

/**
 * Common Advance Deposit Document rule utilities.
 * 
 * 
 */
public class AdvanceDepositDocumentRuleUtil {
    /**
     * This method method will invoke the data dictionary validation for a AdvanceDepositDetail bo instance, in addition to checking
     * existence of the Bank and BankAccount attributes that hang off of it. This method assumes that the document hierarchy for the
     * error map path is managed outside of this call.
     * 
     * @param advanceDeposit
     * @return boolean
     */
    public static boolean validateAdvanceDeposit(AdvanceDepositDetail advanceDeposit) {
        ErrorMap errorMap = GlobalVariables.getErrorMap();
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
            isValid = SpringContext.getBean(DictionaryValidationService.class).validateReferenceExists(advanceDeposit, KFSPropertyConstants.FINANCIAL_DOCUMENT_BANK);
            if (!isValid) {
                String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(AdvanceDepositDetail.class, KFSPropertyConstants.FINANCIAL_DOCUMENT_BANK_CODE);
                errorMap.putError(KFSPropertyConstants.FINANCIAL_DOCUMENT_BANK_CODE, KFSKeyConstants.ERROR_EXISTENCE, label);
            }
        }
        if (isValid) {
            isValid = SpringContext.getBean(DictionaryValidationService.class).validateReferenceExists(advanceDeposit, KFSPropertyConstants.FINANCIAL_DOCUMENT_BANK_ACCOUNT);
            if (!isValid) {
                String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(AdvanceDepositDetail.class, KFSPropertyConstants.FINANCIAL_DOCUMENT_BANK_ACCOUNT_NUMBER);
                errorMap.putError(KFSPropertyConstants.FINANCIAL_DOCUMENT_BANK_ACCOUNT_NUMBER, KFSKeyConstants.ERROR_EXISTENCE, label);
            }
        }

        return isValid;
    }
}