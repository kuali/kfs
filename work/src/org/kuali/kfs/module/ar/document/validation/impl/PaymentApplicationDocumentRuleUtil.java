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

import java.util.Collection;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.NonInvoiced;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;

public class PaymentApplicationDocumentRuleUtil {

    /**
     * Validate non-ar/non-invoice line items on a PaymentApplicationDocument.
     * 
     * @param nonInvoiced
     * @return
     */
    public static boolean validateNonInvoiced(NonInvoiced nonInvoiced) {
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        int originalErrorCount = errorMap.getErrorCount();
        
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(nonInvoiced);
        boolean isValid = (errorMap.getErrorCount() == originalErrorCount);
        
        // check that dollar amount is not zero before continuing
        if (isValid) {
            KualiDecimal amount = nonInvoiced.getFinancialDocumentLineAmount();
            if(null == amount) {
                isValid = false;
                errorMap.putError(
                    ArPropertyConstants.PaymentApplicationDocumentFields.NON_INVOICED_LINE_AMOUNT,
                    ArKeyConstants.PaymentApplicationDocumentErrors.NON_AR_AMOUNT_REQUIRED);
            } else {
                if (amount.isZero()) {
                    isValid = false;
                    errorMap.putError(
                        ArPropertyConstants.PaymentApplicationDocumentFields.NON_INVOICED_LINE_AMOUNT,
                        ArKeyConstants.PaymentApplicationDocumentErrors.NON_AR_AMOUNT_MUST_BE_POSITIVE);
                }
            }
        }
        
        return isValid;
    }
    
    /**
     * This method determines whether or not the amount to be applied to an invoice is acceptable.
     * 
     * @param customerInvoiceDetails
     * @return
     */
    public static boolean validateAmountToBeApplied(Collection<CustomerInvoiceDetail> customerInvoiceDetails) {
        
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        int originalErrorCount = errorMap.getErrorCount();
        
        // Figure out the maximum we should be able to apply.
        Double outstandingAmount = new Double(0);
        Double amountWeWouldApply = new Double(0);
        for (CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDetails) {
            outstandingAmount  += customerInvoiceDetail.getAmount().subtract(customerInvoiceDetail.getAppliedAmount()).doubleValue();
            amountWeWouldApply += customerInvoiceDetail.getAmountToBeApplied().doubleValue();
        }
        
        // Amount to be applied is valid only if it's less than or equal to the outstanding amount on the invoice.
        boolean isValid = amountWeWouldApply <= outstandingAmount;
        
        // If invalid, indicate an error in the UI.
        if(!(amountWeWouldApply <= outstandingAmount)) {
            isValid = false;
            errorMap.putError(
                ArPropertyConstants.PaymentApplicationDocumentFields.AMOUNT_TO_BE_APPLIED,
                ArKeyConstants.PaymentApplicationDocumentErrors.AMOUNT_TO_BE_APPLIED_EXCEEDS_AMOUNT_OUTSTANDING);
        }

        if(KualiDecimal.ZERO.doubleValue() == amountWeWouldApply) {
            isValid = false;
            errorMap.putError(
                ArPropertyConstants.PaymentApplicationDocumentFields.AMOUNT_TO_BE_APPLIED,
                ArKeyConstants.PaymentApplicationDocumentErrors.AMOUNT_TO_BE_APPLIED_CANNOT_BE_ZERO);
        }
        return isValid;
    }
    
}
