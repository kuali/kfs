package edu.arizona.kfs.module.purap.document.web.struts;

import java.text.MessageFormat;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.web.struts.PaymentRequestForm;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.krad.util.GlobalVariables;

import edu.arizona.kfs.fp.service.DisbursementVoucherInvoiceService;
import edu.arizona.kfs.module.purap.PurapConstants;
import edu.arizona.kfs.module.purap.PurapKeyConstants;
import edu.arizona.kfs.sys.KFSKeyConstants;

public class PaymentRequestAction extends org.kuali.kfs.module.purap.document.web.struts.PaymentRequestAction {
    @SuppressWarnings("unused")
    private static Logger LOG = Logger.getLogger(PaymentRequestAction.class);

    private static transient volatile DisbursementVoucherInvoiceService disbursementVoucherInvoiceService;
    private static transient volatile ConfigurationService configurationService;

    private static DisbursementVoucherInvoiceService getDisbursementVoucherInvoiceService() {
        if (disbursementVoucherInvoiceService == null) {
            disbursementVoucherInvoiceService = SpringContext.getBean(DisbursementVoucherInvoiceService.class);
        }
        return disbursementVoucherInvoiceService;
    }

    private static ConfigurationService getConfigurationService() {
        if (configurationService == null) {
            configurationService = SpringContext.getBean(ConfigurationService.class);
        }
        return configurationService;
    }

    @Override
    protected ActionForward performDuplicatePaymentRequestAndEncumberFiscalYearCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, PaymentRequestDocument paymentRequestDocument) throws Exception {
        ActionForward forwardIfDuplicatePREQ = super.performDuplicatePaymentRequestAndEncumberFiscalYearCheck(mapping, form, request, response, paymentRequestDocument);
        if (forwardIfDuplicatePREQ != null) {
            return forwardIfDuplicatePREQ;
        }

        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
        if (PurapConstants.PREQ_DUPLICATE_DV_QUESTION.equals(question)) {
            if (ConfirmationQuestion.NO.equals(buttonClicked)) {
                paymentRequestDocument.getFinancialSystemDocumentHeader().setWorkflowDocumentStatusCode(PurapConstants.PaymentRequestStatuses.APPDOC_INITIATE);
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.INVOICE_NUMBER, PurapKeyConstants.MESSAGE_PREQ_DUPLICATE_DV_INVOICE);
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            } else {
                return null;
            }
        }

        PaymentRequestDocument preq = ((PaymentRequestForm) form).getPaymentRequestDocument();
        ArrayList<String> matchingDVs = findMatchingDisbursementVouchersWithInvoiceNumber(preq);
        if (matchingDVs != null && !matchingDVs.isEmpty()) {
            String questionText = getConfigurationService().getPropertyValueAsString(KFSKeyConstants.MESSAGE_DV_DUPLICATE_INVOICE);

            String args = toCommaDelimitedString(matchingDVs);
            questionText = MessageFormat.format(questionText, args);

            return this.performQuestionWithoutInput(mapping, form, request, response, PurapConstants.PREQ_DUPLICATE_DV_QUESTION, questionText, KFSConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "");

        }
        return null;
    }

    private String toCommaDelimitedString(ArrayList<String> matchingDvs) {
        if (matchingDvs == null || matchingDvs.isEmpty()) {
            return KFSConstants.NOT_AVAILABLE_STRING;
        }
        return StringUtils.join(matchingDvs, KFSConstants.COMMA);
    }

    private ArrayList<String> findMatchingDisbursementVouchersWithInvoiceNumber(PaymentRequestDocument paymentRequestDocument) {
        String invoiceNumber = paymentRequestDocument.getInvoiceNumber();
        PurchaseOrderDocument po = paymentRequestDocument.getPurchaseOrderDocument();
        String vendorId = po.getVendorHeaderGeneratedIdentifier().toString() + KFSConstants.DASH + po.getVendorDetailAssignedIdentifier().toString();
        ArrayList<String> listDisbursementVouchers = (ArrayList<String>) getDisbursementVoucherInvoiceService().findDisbursementVouchersWithInvoiceNumber(vendorId, KFSConstants.WILDCARD_CHARACTER, invoiceNumber);
        return listDisbursementVouchers;
    }
}
