package edu.arizona.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;

import edu.arizona.kfs.module.purap.businessobject.CreditMemoIncomeType;
import edu.arizona.kfs.module.purap.businessobject.PaymentRequestIncomeType;
import edu.arizona.kfs.module.purap.document.PaymentRequestDocument;
import edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument;
import edu.arizona.kfs.sys.KFSKeyConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;

/**
 * Validate Income Types (1099 Classification tab) total against document total.
 */
public class PurapIncomeTypeTotalsValidation extends GenericValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapIncomeTypeTotalsValidation.class);

    public boolean validate(AttributedDocumentEvent event) {
        boolean isValid = true;
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);

        PurchasingAccountsPayableDocument purchasingDocument = (PurchasingAccountsPayableDocument) event.getDocument();
        if (purchasingDocument instanceof PaymentRequestDocument) {
            isValid = validatePaymentRequest((PaymentRequestDocument) purchasingDocument);
        } else if (purchasingDocument instanceof VendorCreditMemoDocument) {
            isValid = validateCreditMemo((VendorCreditMemoDocument) purchasingDocument);
        }

        GlobalVariables.getMessageMap().removeFromErrorPath(KFSPropertyConstants.DOCUMENT);

        // if validation in batch, just log message, don't fail
        if (!isValid && GlobalVariables.getUserSession().getPrincipalName().equals(KFSConstants.SYSTEM_USER)) {
            LOG.info("validateIncomeTypeTotals() Document " + purchasingDocument.getPurapDocumentIdentifier() + ", " + KFSKeyConstants.ERROR_INVALID_INCOME_TYPES_TOTAL_AMOUNT);
            isValid = true;
        }
        return isValid;
    }

    protected boolean validatePaymentRequest(PaymentRequestDocument preqDocument) {
        boolean isValid = true;
        boolean hasIncomeTypes = false;
        KualiDecimal incomeTypesTotal = KualiDecimal.ZERO;
        String[] excludeArray = new String[] {};

        for (PaymentRequestIncomeType incomeType : preqDocument.getIncomeTypes()) {
            if (incomeType.getAmount() != null) {
                incomeTypesTotal = incomeTypesTotal.add(incomeType.getAmount());
                hasIncomeTypes = true;
            }
        }

        // Don't do compare if no Income Types
        if (hasIncomeTypes) {
            // if UseTax is included, then the incomeTypesTotal should be compared against the total amount NOT INCLUDING tax
            if (preqDocument.isUseTaxIndicator()) {
                if (preqDocument.getTotalPreTaxDollarAmountAllItems(excludeArray).compareTo(incomeTypesTotal) != 0) {
                    GlobalVariables.getMessageMap().putError(KFSPropertyConstants.IncomeTypeFields.DOCUMENT_INCOME_TYPES, KFSKeyConstants.ERROR_INVALID_INCOME_TYPES_TOTAL_AMOUNT);
                    isValid = false;
                }
            }
            // if NO UseTax, then the incomeTypesTotal should be compared against the
            // total amount INCLUDING sales tax (since if the vendor invoices with sales tax, then we pay it)
            else {
                if (preqDocument.getTotalDollarAmountAllItems(excludeArray).compareTo(incomeTypesTotal) != 0) {
                    GlobalVariables.getMessageMap().putError(KFSPropertyConstants.IncomeTypeFields.DOCUMENT_INCOME_TYPES, KFSKeyConstants.ERROR_INVALID_INCOME_TYPES_TOTAL_AMOUNT);
                    isValid = false;
                }
            }
        }

        return isValid;
    }

    protected boolean validateCreditMemo(VendorCreditMemoDocument cmDocument) {
        boolean isValid = true;
        KualiDecimal incomeTypesTotal = KualiDecimal.ZERO;

        for (CreditMemoIncomeType incomeType : cmDocument.getIncomeTypes()) {
            if (incomeType.getAmount() != null) {
                incomeTypesTotal = incomeTypesTotal.add(incomeType.getAmount());
            }
        }

        // if UseTax is included, then the incomeTypesTotal should be compared against the total amount NOT INCLUDING tax
        if (cmDocument.isUseTaxIndicator()) {
            if (cmDocument.getGrandPreTaxTotal().compareTo(incomeTypesTotal) != 0) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.IncomeTypeFields.DOCUMENT_INCOME_TYPES, KFSKeyConstants.ERROR_INVALID_INCOME_TYPES_TOTAL_AMOUNT);
                isValid = false;
            }
        }
        // if NO UseTax, then the incomeTypesTotal should be compared against the
        // total amount INCLUDING sales tax (since if the vendor invoices with sales tax, then we pay it)
        else {
            if (cmDocument.getGrandTotal().compareTo(incomeTypesTotal) != 0) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.IncomeTypeFields.DOCUMENT_INCOME_TYPES, KFSKeyConstants.ERROR_INVALID_INCOME_TYPES_TOTAL_AMOUNT);
                isValid = false;
            }
        }

        return isValid;
    }
}
