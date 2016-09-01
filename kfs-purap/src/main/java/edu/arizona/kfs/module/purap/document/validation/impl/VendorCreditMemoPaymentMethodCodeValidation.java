package edu.arizona.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;

import edu.arizona.kfs.module.purap.PurapKeyConstants;
import edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.util.GlobalVariables;



/**
 * Validates the payment method code used on credit memo document if it was created
 * from a payment request document.
 */
public class VendorCreditMemoPaymentMethodCodeValidation extends GenericValidation {

    public boolean validate(AttributedDocumentEvent event) {
        if ( event.getDocument() instanceof VendorCreditMemoDocument ) {
            VendorCreditMemoDocument doc = (VendorCreditMemoDocument) event.getDocument();
            // check if from a PREQ document
            if ( doc.isSourceDocumentPaymentRequest() ) {
                // load the document
                PaymentRequestDocument preqDoc = doc.getPaymentRequestDocument();
                // if a UA PREQ, get the PMC
                if ( preqDoc instanceof edu.arizona.kfs.module.purap.document.PaymentRequestDocument ) {
                    // check if the PMC on this document is the same
                    String preqPaymentMethodCode = ((edu.arizona.kfs.module.purap.document.PaymentRequestDocument)preqDoc).getPaymentMethodCode();
                    if ( !StringUtils.equals(preqPaymentMethodCode, doc.getPaymentMethodCode() ) ) {
                        GlobalVariables.getMessageMap().putError(AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX + KfsKimAttributes.PAYMENT_METHOD_CODE, PurapKeyConstants.ERROR_PAYMENTMETHODCODE_MUSTMATCHPREQ, preqPaymentMethodCode);
                        return false;
                    }
                }
            }
        }
        // if not (for some reason) the UA CM document, just return true
        return true;
    }

}