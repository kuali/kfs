package edu.arizona.kfs.fp.document.validation.impl;

import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

import edu.arizona.kfs.fp.document.DisbursementVoucherDocument;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSKeyConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;

public class DisbursementVoucherPaymentReasonValidation extends org.kuali.kfs.fp.document.validation.impl.DisbursementVoucherPaymentReasonValidation {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherPaymentReasonValidation.class);

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = super.validate(event);

        DisbursementVoucherDocument document = (DisbursementVoucherDocument) event.getDocument();
        valid &= validatePaymentReasonIsActive(document);
        return valid;
    }

    private boolean validatePaymentReasonIsActive(DisbursementVoucherDocument document) {
        if (!document.getDvPayeeDetail().getDisbVchrPaymentReason().isActive()) {
            GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSPropertyConstants.DV_PAYEE_DETAIL + "." + KFSPropertyConstants.DISB_VCHR_PAYMENT_REASON_CODE, KFSKeyConstants.ERROR_DISBURSEMENT_VOUCHER_PAYMENT_REASON_CODE_INACTIVE);
            return false;
        }
        return true;
    }

}
