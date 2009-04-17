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

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.module.ar.businessobject.NonInvoiced;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class PaymentApplicationDocumentRuleUtil {

    public static boolean validateAllAmounts(PaymentApplicationDocument applicationDocument, List<CustomerInvoiceDetail> invoiceDetails, NonInvoiced newNonInvoiced, KualiDecimal totalFromControl) throws WorkflowException {
        boolean isValid = validateApplieds(invoiceDetails, applicationDocument, totalFromControl);
        isValid &= validateNonAppliedHolding(applicationDocument, totalFromControl);
        isValid &= validateNonInvoiced(newNonInvoiced, applicationDocument, totalFromControl);
        return isValid;
    }
    
    /**
     * This method checks that an invoice paid applied is for a valid amount.
     * 
     * @param invoicePaidApplied
     * @return
     */
    public static boolean validateInvoicePaidApplied(InvoicePaidApplied invoicePaidApplied, String fieldName) {
        boolean isValid = true;
        
        invoicePaidApplied.refreshReferenceObject("invoiceDetail");
        if(ObjectUtils.isNull(invoicePaidApplied) || ObjectUtils.isNull(invoicePaidApplied.getInvoiceDetail())) { return true; }
        KualiDecimal amountOwed = invoicePaidApplied.getInvoiceDetail().getAmountOpen();
        KualiDecimal amountPaid = invoicePaidApplied.getInvoiceItemAppliedAmount();
        
        if(ObjectUtils.isNull(amountOwed)) {
            amountOwed = KualiDecimal.ZERO;
        }
        if(ObjectUtils.isNull(amountPaid)) {
            amountPaid = KualiDecimal.ZERO;
        }
        
        // Can't pay more than you owe.
        if(!amountPaid.isLessEqual(amountOwed)) {
            isValid = false;
            GlobalVariables.getErrorMap().putError(
                fieldName, ArKeyConstants.PaymentApplicationDocumentErrors.AMOUNT_TO_BE_APPLIED_EXCEEDS_AMOUNT_OUTSTANDING);
        }
        
        // Paying zero means nothing.
//        if(!amountPaid.isGreaterThan(KualiDecimal.ZERO)) {
//            isValid = false;
//            GlobalVariables.getErrorMap().putError(
//                fieldName, ArKeyConstants.PaymentApplicationDocumentErrors.AMOUNT_TO_BE_APPLIED_MUST_BE_GREATER_THAN_ZERO);
//        }
        
        // Can't apply more than the amount received via the related CashControlDocument
        String docNumber = invoicePaidApplied.getDocumentNumber();
        DocumentService docService = SpringContext.getBean(DocumentService.class);
        PaymentApplicationDocument paymentApplicationDocument;
        try {
            paymentApplicationDocument = (PaymentApplicationDocument) docService.getByDocumentHeaderId(docNumber);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("A WorkflowException was thrown while trying to retrieve PayApp doc #" + docNumber + ".", e);
        }
        if (null != paymentApplicationDocument) {
            KualiDecimal totalFromControl = paymentApplicationDocument.getTotalFromControl();
            if (amountPaid.isGreaterThan(totalFromControl)) {
                isValid = false;
                GlobalVariables.getErrorMap().putError(
                    fieldName,ArKeyConstants.PaymentApplicationDocumentErrors.CANNOT_APPLY_MORE_THAN_CASH_CONTROL_TOTAL_AMOUNT);
            }
        }
        
        return isValid;
    }
    
    /**
     * The sum of invoice paid applied amounts cannot exceed the cash control total amount
     * 
     * @param paymentApplicationDocument
     * @return
     * @throws WorkflowException
     */
    public static boolean validateCumulativeSumOfInvoicePaidAppliedDoesntExceedCashControlTotal(PaymentApplicationDocument paymentApplicationDocument) throws WorkflowException {
        KualiDecimal appliedTotal = new KualiDecimal(0);
        for(InvoicePaidApplied invoicePaidApplied : paymentApplicationDocument.getInvoicePaidApplieds()) {
            invoicePaidApplied.refreshReferenceObject("invoiceDetail");
            appliedTotal = appliedTotal.add(invoicePaidApplied.getInvoiceItemAppliedAmount());
        }
        return paymentApplicationDocument.getTotalFromControl().isGreaterEqual(appliedTotal);
    }

    /**
     * The sum of invoice paid applied amounts cannot be less than zero.
     * 
     * @param paymentApplicationDocument
     * @return
     * @throws WorkflowException
     */
    public static boolean validateCumulativeSumOfInvoicePaidAppliedsIsGreaterThanOrEqualToZero(PaymentApplicationDocument paymentApplicationDocument) throws WorkflowException {
        KualiDecimal appliedTotal = new KualiDecimal(0);
        for(InvoicePaidApplied invoicePaidApplied : paymentApplicationDocument.getInvoicePaidApplieds()) {
            invoicePaidApplied.refreshReferenceObject("invoiceDetail");
            appliedTotal = appliedTotal.add(invoicePaidApplied.getInvoiceItemAppliedAmount());
        }
        return KualiDecimal.ZERO.isLessEqual(appliedTotal);
    }
    
    /**
     * The sum of non invoiceds must be less than or equal to the cash control total amount
     * 
     * @param paymentApplicationDocument
     * @return
     * @throws WorkflowException
     */
    public static boolean validateNonInvoicedAmountDoesntExceedCashControlTotal(PaymentApplicationDocument paymentApplicationDocument) throws WorkflowException {
        return paymentApplicationDocument.getTotalFromControl().isGreaterEqual(paymentApplicationDocument.getSumOfNonInvoiceds());
    }
    
    /**
     * The unapplied amount can't be negative
     * 
     * @param paymentApplicationDocument
     * @return
     * @throws WorkflowException
     */
    public static boolean validateNonInvoicedAmountIsGreaterThanOrEqualToZero(PaymentApplicationDocument paymentApplicationDocument) throws WorkflowException {
        return KualiDecimal.ZERO.isLessEqual(paymentApplicationDocument.getSumOfNonInvoiceds());
    }

    /**
     * The unapplied amount must be less than or equal to the cash control total amount
     * 
     * @param paymentApplicationDocument
     * @return
     * @throws WorkflowException
     */
    public static boolean validateUnappliedAmountDoesntExceedCashControlTotal(PaymentApplicationDocument paymentApplicationDocument) throws WorkflowException {
        KualiDecimal a = paymentApplicationDocument.getNonAppliedHoldingAmount();
        if(null == a) {
            return true;
        }
        return paymentApplicationDocument.getTotalFromControl().isGreaterEqual(a);
    }
    
    /**
     * The unapplied amount can't be negative
     * 
     * @param paymentApplicationDocument
     * @return
     * @throws WorkflowException
     */
    public static boolean validateUnappliedAmountIsGreaterThanOrEqualToZero(PaymentApplicationDocument paymentApplicationDocument) throws WorkflowException {
        if(null == paymentApplicationDocument.getNonAppliedHoldingAmount()) { return true; }
        return KualiDecimal.ZERO.isLessEqual(paymentApplicationDocument.getNonAppliedHoldingAmount());
    }

    /**
     * Validate non-ar/non-invoice line items on a PaymentApplicationDocument.
     * 
     * @param nonInvoiced
     * @return
     */
    public static boolean validateNonInvoiced(NonInvoiced nonInvoiced, PaymentApplicationDocument paymentApplicationDocument, KualiDecimal totalFromControl) throws WorkflowException {
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
            KualiDecimal cashControlBalanceToBeApplied = totalFromControl;
            cashControlBalanceToBeApplied = cashControlBalanceToBeApplied.add(paymentApplicationDocument.getTotalFromControl());
            cashControlBalanceToBeApplied.subtract(paymentApplicationDocument.getTotalApplied());
            cashControlBalanceToBeApplied.subtract(paymentApplicationDocument.getNonAppliedHoldingAmount());
            
            if (nonArLineAmount.isZero()) {
                isValid = false;
                errorMap.putError(
                    ArPropertyConstants.PaymentApplicationDocumentFields.NON_INVOICED_LINE_AMOUNT,
                    ArKeyConstants.PaymentApplicationDocumentErrors.AMOUNT_TO_BE_APPLIED_CANNOT_BE_ZERO);
            }
            //  check that we're not trying to apply more funds to the invoice than the invoice has balance (ie, over-applying)
            else if (KualiDecimal.ZERO.isGreaterThan(cashControlBalanceToBeApplied.subtract(nonArLineAmount))) {
                isValid = false;
                errorMap.putError(
                    ArPropertyConstants.PaymentApplicationDocumentFields.NON_INVOICED_LINE_AMOUNT,
                    ArKeyConstants.PaymentApplicationDocumentErrors.NON_AR_AMOUNT_EXCEEDS_BALANCE_TO_BE_APPLIED);
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
    public static boolean validateApplieds(List<CustomerInvoiceDetail> customerInvoiceDetails, PaymentApplicationDocument paymentAplicationDocument, KualiDecimal totalFromControl) throws WorkflowException {

        // Indicates whether the validation succeeded
        boolean isValid = true;

        // Figure out the maximum we should be able to apply.
        for (CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDetails) {
            isValid &= validateAmountAppliedToCustomerInvoiceDetailByPaymentApplicationDocument(customerInvoiceDetail, paymentAplicationDocument, totalFromControl);
        }

        return isValid;
    }
    
    /**
     * @param customerInvoiceDetail
     * @param paymentApplicationDocument
     * @return
     */
    public static boolean validateAmountAppliedToCustomerInvoiceDetailByPaymentApplicationDocument(CustomerInvoiceDetail customerInvoiceDetail, PaymentApplicationDocument paymentApplicationDocument, KualiDecimal totalFromControl) throws WorkflowException {
        
        boolean isValid = true;
        
        // This let's us highlight a specific invoice detail line
        String propertyName = 
            MessageFormat.format(ArPropertyConstants.PaymentApplicationDocumentFields.AMOUNT_TO_BE_APPLIED_LINE_N, customerInvoiceDetail.getSequenceNumber().toString());
        
        KualiDecimal amountAppliedByAllOtherDocuments = 
            customerInvoiceDetail.getAmountAppliedExcludingAnyAmountAppliedBy(paymentApplicationDocument.getDocumentNumber());
        KualiDecimal amountAppliedByThisDocument = 
            customerInvoiceDetail.getAmountAppliedBy(paymentApplicationDocument.getDocumentNumber());
        KualiDecimal totalAppliedAmount = 
            amountAppliedByAllOtherDocuments.add(amountAppliedByThisDocument);
        
        // Can't apply more than the total amount of the detail
        if(!totalAppliedAmount.isLessEqual(totalFromControl)) {
            isValid = false;
            GlobalVariables.getErrorMap().putError(propertyName, ArKeyConstants.PaymentApplicationDocumentErrors.AMOUNT_TO_BE_APPLIED_EXCEEDS_AMOUNT_OUTSTANDING);
        }
        
        // Can't apply a negative amount.
        if(KualiDecimal.ZERO.isGreaterThan(amountAppliedByThisDocument)) {
            isValid = false;
            GlobalVariables.getErrorMap().putError(propertyName, ArKeyConstants.PaymentApplicationDocumentErrors.AMOUNT_TO_BE_APPLIED_MUST_BE_GREATER_THAN_ZERO);
        }
        
        // Can't apply more than the total amount outstanding on the cash control document.
        CashControlDocument cashControlDocument = paymentApplicationDocument.getCashControlDocument();
        if(ObjectUtils.isNotNull(cashControlDocument)) {
            if(cashControlDocument.getCashControlTotalAmount().isLessThan(amountAppliedByThisDocument)) {
                isValid = false;
                GlobalVariables.getErrorMap().putError(propertyName, ArKeyConstants.PaymentApplicationDocumentErrors.CANNOT_APPLY_MORE_THAN_BALANCE_TO_BE_APPLIED);
            }
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
    public static boolean validateNonAppliedHolding(PaymentApplicationDocument applicationDocument, KualiDecimal totalFromControl) throws WorkflowException {
        NonAppliedHolding nonAppliedHolding = applicationDocument.getNonAppliedHolding();
        if(ObjectUtils.isNull(nonAppliedHolding)) { return true; }
        if(StringUtils.isNotEmpty(nonAppliedHolding.getCustomerNumber())) {
            KualiDecimal nonAppliedAmount = nonAppliedHolding.getFinancialDocumentLineAmount();
            if(null == nonAppliedAmount) { nonAppliedAmount = KualiDecimal.ZERO; }
            boolean isValid = totalFromControl.isGreaterEqual(nonAppliedAmount);
            if(!isValid) {
                String propertyName = ArPropertyConstants.PaymentApplicationDocumentFields.UNAPPLIED_AMOUNT;
                String errorKey = ArKeyConstants.PaymentApplicationDocumentErrors.UNAPPLIED_AMOUNT_CANNOT_EXCEED_AVAILABLE_AMOUNT;
                GlobalVariables.getErrorMap().putError(propertyName, errorKey);
            }
            // The amount of the unapplied can't exceed the remaining balance to be applied 
            KualiDecimal totalBalanceToBeApplied = applicationDocument.getUnallocatedBalance();
            isValid = KualiDecimal.ZERO.isLessEqual(totalBalanceToBeApplied);
            if(!isValid) {
                String propertyName = ArPropertyConstants.PaymentApplicationDocumentFields.UNAPPLIED_AMOUNT;
                String errorKey = ArKeyConstants.PaymentApplicationDocumentErrors.UNAPPLIED_AMOUNT_CANNOT_EXCEED_BALANCE_TO_BE_APPLIED;
                GlobalVariables.getErrorMap().putError(propertyName, errorKey);
            }
            return isValid;
        } else {
            if(ObjectUtils.isNull(nonAppliedHolding.getFinancialDocumentLineAmount()) || KualiDecimal.ZERO.equals(nonAppliedHolding.getFinancialDocumentLineAmount())) {
                // All's OK. Both customer number and amount are empty/null.
                return true;
            } else {
                // Error. Customer number is empty but amount wasn't.
                String propertyName = ArPropertyConstants.PaymentApplicationDocumentFields.UNAPPLIED_CUSTOMER_NUMBER;
                String errorKey = ArKeyConstants.PaymentApplicationDocumentErrors.UNAPPLIED_AMOUNT_CANNOT_BE_EMPTY_OR_ZERO;
                GlobalVariables.getErrorMap().putError(propertyName, errorKey);
                return false;
            }
        }
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
