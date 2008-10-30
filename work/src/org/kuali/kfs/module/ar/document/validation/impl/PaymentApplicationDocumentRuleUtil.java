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

import java.util.List;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.NonInvoiced;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class PaymentApplicationDocumentRuleUtil {

    public static boolean validateAllAmounts(PaymentApplicationDocument applicationDocument, List<CustomerInvoiceDetail> invoiceDetails, NonInvoiced newNonInvoiced, KualiDecimal selectedInvoiceBalance) throws WorkflowException {
        boolean isValid = validateApplieds(invoiceDetails, applicationDocument);
        isValid &= validateUnapplied(applicationDocument);
        isValid &= validateNonInvoiced(newNonInvoiced, selectedInvoiceBalance);
        return isValid;
    }
    
    /**
     * Validate non-ar/non-invoice line items on a PaymentApplicationDocument.
     * 
     * @param nonInvoiced
     * @return
     */
    public static boolean validateNonInvoiced(NonInvoiced nonInvoiced, KualiDecimal selectedInvoiceBalance) {
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        int originalErrorCount = errorMap.getErrorCount();
        
        //  validate the NonInvoiced BO
        String sNonInvoicedErrorPath = "nonInvoicedAddLine";
        errorMap.addToErrorPath(sNonInvoicedErrorPath);
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(nonInvoiced);
        errorMap.removeFromErrorPath(sNonInvoicedErrorPath);

        if (errorMap.getErrorCount() != originalErrorCount) {
            return false;
        }
        
        boolean isValid = true;
        KualiDecimal nonArLineAmount = nonInvoiced.getFinancialDocumentLineAmount();
        // check that dollar amount is not zero before continuing
        if(ObjectUtils.isNull(nonArLineAmount)) {
            isValid = false;
            errorMap.putError(
                ArPropertyConstants.PaymentApplicationDocumentFields.NON_INVOICED_LINE_AMOUNT,
                ArKeyConstants.PaymentApplicationDocumentErrors.NON_AR_AMOUNT_REQUIRED);
        } else {
            if (nonArLineAmount.isZero()) {
                isValid = false;
                errorMap.putError(
                    ArPropertyConstants.PaymentApplicationDocumentFields.NON_INVOICED_LINE_AMOUNT,
                    ArKeyConstants.PaymentApplicationDocumentErrors.NON_AR_AMOUNT_MUST_BE_POSITIVE);
            }
            //  check that we're not trying to apply more funds to the invoice than the invoice has balance (ie, over-applying)
            else if (nonArLineAmount.isGreaterThan(selectedInvoiceBalance)) {
                isValid = false;
                errorMap.putError(
                    ArPropertyConstants.PaymentApplicationDocumentFields.NON_INVOICED_LINE_AMOUNT,
                    ArKeyConstants.PaymentApplicationDocumentErrors.NON_AR_AMOUNT_EXCEEDS_SELECTED_INVOICE_BALANCE);
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
    public static boolean validateApplieds(List<CustomerInvoiceDetail> customerInvoiceDetails, PaymentApplicationDocument document) throws WorkflowException {

        ErrorMap errorMap = GlobalVariables.getErrorMap();

        // Figure out the maximum we should be able to apply.
        Double outstandingAmount = new Double(0);
        Double amountWeWouldApply = new Double(0);
        for (CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDetails) {
            KualiDecimal detailAmount = customerInvoiceDetail.getAmount();
            KualiDecimal detailAppliedAmount = customerInvoiceDetail.getAppliedAmount();
            outstandingAmount  += detailAmount.subtract(detailAppliedAmount).doubleValue();
            amountWeWouldApply += customerInvoiceDetail.getAmountToBeApplied().doubleValue();
        }

        // Amount to be applied is valid only if it's less than or equal to the outstanding amount on the invoice.
        boolean isValid = true;

        // If invalid, indicate an error in the UI.
        
        // Can't apply more than the total amount of the detail
        if(!(amountWeWouldApply <= outstandingAmount)) {
            isValid = false;
            errorMap.putError(
                ArPropertyConstants.PaymentApplicationDocumentFields.AMOUNT_TO_BE_APPLIED,
                ArKeyConstants.PaymentApplicationDocumentErrors.AMOUNT_TO_BE_APPLIED_EXCEEDS_AMOUNT_OUTSTANDING);
        }

        // Can't apply zero or negative.
        if(KualiDecimal.ZERO.doubleValue() >= amountWeWouldApply) {
            isValid = false;
            errorMap.putError(
                ArPropertyConstants.PaymentApplicationDocumentFields.AMOUNT_TO_BE_APPLIED,
                ArKeyConstants.PaymentApplicationDocumentErrors.AMOUNT_TO_BE_APPLIED_MUST_BE_GREATER_THAN_ZERO);
        }
        
        // Can't apply more than the total amount outstanding on the cash control document.
        if(document.getCashControlTotalAmount().doubleValue() < amountWeWouldApply) {
            isValid = false;
            errorMap.putError(
                ArPropertyConstants.PaymentApplicationDocumentFields.AMOUNT_TO_BE_APPLIED,
                ArKeyConstants.PaymentApplicationDocumentErrors.CANNOT_APPLY_MORE_THAN_CASH_CONTROL_TOTAL_AMOUNT);
        }
        return isValid;
    }
    
    /**
     * This method validates the unapplied attribute of the document.
     * 
     * @param document
     * @return
     * @throws WorkflowException
     */
    public static boolean validateUnapplied(PaymentApplicationDocument applicationDocument) throws WorkflowException {
        boolean isValid = applicationDocument.getCashControlTotalAmount().isGreaterThan(applicationDocument.getTotalUnapplied());
        if(!isValid) {
            String propertyName = ArPropertyConstants.PaymentApplicationDocumentFields.UNAPPLIED_AMOUNT;
            String errorKey = ArKeyConstants.PaymentApplicationDocumentErrors.UNAPPLIED_AMOUNT_CANNOT_EXCEED_AVAILABLE_AMOUNT;
            GlobalVariables.getErrorMap().putError(propertyName, errorKey);
        }
        return isValid;
    }
    
    /**
     * This method sums the amounts for a List of NonInvoiceds.
     * This is used separately from PaymentApplicationDocument.getTotalUnapplied()
     * 
     * @return
     */
    private static KualiDecimal getSumOfNonInvoiceds(List<NonInvoiced> nonInvoiceds) {
        KualiDecimal sum = new KualiDecimal(0);
        for(NonInvoiced nonInvoiced : nonInvoiceds) {
            sum = sum.add(nonInvoiced.getFinancialDocumentLineAmount());
        }
        return sum;
    }

}
