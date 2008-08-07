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
package org.kuali.kfs.module.ar.document.validation.impl;

import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.fp.businessobject.AdvanceDepositDetail;
import org.kuali.kfs.module.ar.businessobject.NonInvoiced;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;

public class PaymentApplicationDocumentRuleUtil {
    public static boolean validateNonInvoiced(NonInvoiced nonInvoiced) {
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        int originalErrorCount = errorMap.getErrorCount();
        
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(nonInvoiced);
        boolean isValid = (errorMap.getErrorCount() == originalErrorCount);
        
        // check that dollar amount is not zero before continuing
        if (isValid) {
            isValid = !nonInvoiced.getFinancialDocumentLineAmount().isZero();
            if (!isValid) {
                String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(NonInvoiced.class, KFSPropertyConstants.NON_INVOICED_LINE_AMOUNT);
                errorMap.putError(KFSPropertyConstants.NON_INVOICED_LINE_AMOUNT, KFSKeyConstants.AdvanceDeposit.ERROR_DOCUMENT_ADVANCE_DEPOSIT_ZERO_AMOUNT, label);
            }
        }
        
        return isValid;
    }
}
