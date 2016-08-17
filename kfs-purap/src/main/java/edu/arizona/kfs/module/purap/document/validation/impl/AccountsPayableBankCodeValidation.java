package edu.arizona.kfs.module.purap.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.AccountsPayableDocumentBase;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants;

import edu.arizona.kfs.sys.document.validation.impl.BankCodeValidation;
import edu.arizona.kfs.module.purap.document.PaymentRequestDocument;
import edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument;



/**
 * Override the validator from baseline with one which is aware of the new documents with a payment method code.
 */
public class AccountsPayableBankCodeValidation extends org.kuali.kfs.module.purap.document.validation.impl.AccountsPayableBankCodeValidation {


    public boolean validate(AttributedDocumentEvent event) {
        AccountsPayableDocumentBase apDocument = (AccountsPayableDocumentBase) getAccountingDocumentForValidation();

        // check if one of the extended UA documents, if so, take the payment method into account, otherwise, revert to baseline behavior
        boolean isValid = true;
        if ( apDocument instanceof PaymentRequestDocument ) {
            isValid = BankCodeValidation.validate(apDocument.getBankCode(), AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX + PurapPropertyConstants.BANK_CODE, ((PaymentRequestDocument)apDocument).getPaymentMethodCode(), false, true);            
            if ( isValid ) {
                // clear out the bank code on the document if not needed (per the message set by the call above)
                if ( StringUtils.isNotBlank(apDocument.getBankCode())
                        && !BankCodeValidation.doesBankCodeNeedToBePopulated(((PaymentRequestDocument)apDocument).getPaymentMethodCode()) ) {
                    apDocument.setBank(null);
                    apDocument.setBankCode(null);                
                }
            }
        } else if ( apDocument instanceof VendorCreditMemoDocument ) {
            isValid = BankCodeValidation.validate(apDocument.getBankCode(), AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX + PurapPropertyConstants.BANK_CODE,  ((VendorCreditMemoDocument)apDocument).getPaymentMethodCode(), false, true);                        
            if ( isValid ) {
                // clear out the bank code on the document if not needed (per the message set by the call above)
                if ( StringUtils.isNotBlank(apDocument.getBankCode())
                        && !BankCodeValidation.doesBankCodeNeedToBePopulated(((VendorCreditMemoDocument)apDocument).getPaymentMethodCode()) ) {
                    apDocument.setBank(null);
                    apDocument.setBankCode(null);                
                }
            }
        } else {
        	if (isDocumentTypeUsingBankCode(apDocument)) {
        		isValid = BankCodeValidation.validate(apDocument.getBankCode(), AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX + PurapPropertyConstants.BANK_CODE, false, true);
        	}
        }

        return isValid;
    }
}

