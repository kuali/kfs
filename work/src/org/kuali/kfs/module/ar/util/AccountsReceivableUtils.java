package org.kuali.kfs.module.ar.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.NonInvoiced;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;

/**
 * Utility class for Accounts Receivables.
 */
public class AccountsReceivableUtils {

    /**
     * This method returns true if the CashControlDetail is correctable. Correctable is defined as 1. Payment Application must be
     * final 2. and Not a correction document itself 3. and has not already been corrected 4. and there is actually values to
     * correct (excluding refunded amount).
     * 
     * @param cashControlDetail
     * @return
     */
    public static boolean canCorrectDetail(CashControlDetail cashControlDetail) {
        String paymentApplicationDocumentNumber = cashControlDetail.getReferenceFinancialDocumentNumber();

        try {
            PaymentApplicationDocument paymentApplicationDocument = (PaymentApplicationDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(paymentApplicationDocumentNumber);
            WorkflowDocument workflowDocument = paymentApplicationDocument.getDocumentHeader().getWorkflowDocument();

            // if state is final and not a payment reversal and hasn't been corrected yet
            if (workflowDocument.isFinal() && !paymentApplicationDocument.isPaymentApplicationCorrection() && StringUtils.isBlank(paymentApplicationDocument.getDocumentHeader().getCorrectedByDocumentId()) && hasDataValuesToCorrect(paymentApplicationDocument)) {
                return true;
            }

            return false;
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method returns true if there is something in the paymentApplicationDocument that can be corrected. Basically, if there
     * exist a value in the payment application (not including refunds).
     * 
     * @param paymentApplicationDocument
     * @return
     */
    public static boolean hasDataValuesToCorrect(PaymentApplicationDocument paymentApplicationDocument) {
        // if there is anything that is Applied return true
        if (paymentApplicationDocument.getInvoicePaidApplieds().size() > 0) {
            return true;
        }

        // if there is anything in nonInvoiced (NON-AR) that is not a refund, return true
        for (NonInvoiced nonInvoiced : paymentApplicationDocument.getNonInvoiceds()) {
            if (!nonInvoiced.isRefundIndicator()) {
                return true;
            }
        }

        // if there is any value in the nonApplied Holding (Unapplied)
        if (paymentApplicationDocument.getNonAppliedHoldingAmount() != null && paymentApplicationDocument.getNonAppliedHoldingAmount() != KualiDecimal.ZERO) {
            return true;
        }

        return false;
    }
}