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

import org.kuali.kfs.fp.businessobject.CreditCardDetail;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

/**
 * This class...
 */
public class CreditCardReceiptValidation extends GenericValidation {
    private CreditCardDetail creditCardDetailForValidation;
    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        CreditCardDetail creditCardReceipt = getCreditCardDetailForValidation();
        MessageMap errorMap = GlobalVariables.getMessageMap();
        int originalErrorCount = errorMap.getErrorCount();

        // call the DD validation which checks basic data integrity
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(creditCardReceipt);
        boolean isValid = (errorMap.getErrorCount() == originalErrorCount);

        // check that dollar amount is not zero before continuing
        if (isValid) {
            isValid = !creditCardReceipt.getCreditCardAdvanceDepositAmount().isZero();
            if (!isValid) {
                String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(CreditCardDetail.class, KFSPropertyConstants.CREDIT_CARD_ADVANCE_DEPOSIT_AMOUNT);
                errorMap.putError(KFSPropertyConstants.CREDIT_CARD_ADVANCE_DEPOSIT_AMOUNT, KFSKeyConstants.ERROR_ZERO_AMOUNT, label);
            }
        }

//        if (isValid) {
//            isValid = SpringContext.getBean(DictionaryValidationService.class).validateReferenceExists(creditCardReceipt, KFSPropertyConstants.CREDIT_CARD_TYPE);
//            if (!isValid) {
//                String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(CreditCardDetail.class, KFSPropertyConstants.FINANCIAL_DOCUMENT_CREDIT_CARD_TYPE_CODE);
//                errorMap.putError(KFSPropertyConstants.FINANCIAL_DOCUMENT_CREDIT_CARD_TYPE_CODE, KFSKeyConstants.ERROR_EXISTENCE, label);
//            }
//        }
//        if (isValid) {
//            isValid = SpringContext.getBean(DictionaryValidationService.class).validateReferenceExists(creditCardReceipt, KFSPropertyConstants.CREDIT_CARD_VENDOR);
//            if (!isValid) {
//                String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(CreditCardDetail.class, KFSPropertyConstants.FINANCIAL_DOCUMENT_CREDIT_CARD_VENDOR_NUMBER);
//                errorMap.putError(KFSPropertyConstants.FINANCIAL_DOCUMENT_CREDIT_CARD_VENDOR_NUMBER, KFSKeyConstants.ERROR_EXISTENCE, label);
//            }
//        }

        return isValid;
    }
    /**
     * Gets the creditCardDetailForValidation attribute. 
     * @return Returns the creditCardDetailForValidation.
     */
    public CreditCardDetail getCreditCardDetailForValidation() {
        return creditCardDetailForValidation;
    }
    /**
     * Sets the creditCardDetailForValidation attribute value.
     * @param creditCardDetailForValidation The creditCardDetailForValidation to set.
     */
    public void setCreditCardDetailForValidation(CreditCardDetail creditCardDetailForValidation) {
        this.creditCardDetailForValidation = creditCardDetailForValidation;
    }
    

}
