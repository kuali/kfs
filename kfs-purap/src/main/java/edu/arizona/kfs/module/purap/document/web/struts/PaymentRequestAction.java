package edu.arizona.kfs.module.purap.document.web.struts;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurApItemUseTax;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

import edu.arizona.kfs.fp.service.DisbursementVoucherInvoiceService;
import edu.arizona.kfs.module.purap.PurapConstants;
import edu.arizona.kfs.module.purap.PurapKeyConstants;
import edu.arizona.kfs.module.purap.businessobject.PaymentRequestIncomeType;
import edu.arizona.kfs.module.purap.document.PaymentRequestDocument;
import edu.arizona.kfs.sys.document.IncomeTypeContainer;

@SuppressWarnings("deprecation")
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


    protected ActionForward performDuplicatePaymentRequestAndEncumberFiscalYearCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, PaymentRequestDocument paymentRequestDocument) throws Exception {
        ActionForward forwardIfDuplicatePREQ = super.performDuplicatePaymentRequestAndEncumberFiscalYearCheck(mapping, form, request, response, paymentRequestDocument);
        if (forwardIfDuplicatePREQ != null) {
            return forwardIfDuplicatePREQ;
        }

        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
        if (PurapConstants.PREQ_DUPLICATE_DV_QUESTION.equals(question)) {
            if (ConfirmationQuestion.NO.equals(buttonClicked)) {
                paymentRequestDocument.updateAndSaveAppDocStatus(PurapConstants.PaymentRequestStatuses.APPDOC_INITIATE);
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            } else {
                return null;
            }
        }

        PaymentRequestDocument preq = ((PaymentRequestForm) form).getPaymentRequestDocument();
        ArrayList<String> matchingDVs = findMatchingDisbursementVouchersWithInvoiceNumber(preq);
        if (matchingDVs != null && !matchingDVs.isEmpty()) {
            String questionText = getConfigurationService().getPropertyValueAsString(PurapKeyConstants.MESSAGE_PREQ_DUPLICATE_DV_INVOICE);

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

    /**
     * Adds a PaymentRequestIncomeType instance created from the current "newIncomeType" to the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward newIncomeType(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        getIncomeTypeContainer(form).getIncomeTypeHandler().addNewIncomeType();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Deletes the selected PaymentRequestIncomeType from the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteIncomeType(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        getIncomeTypeContainer(form).getIncomeTypeHandler().removeIncomeType(getLineToDelete(request));
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method refreshes 1099 income types based on summary accounting lines
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward refreshIncomeTypesFromAccountLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        loadIncomeTypesFromAccountLines(form);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method forces update to account line amounts then populates income types
     * 
     * @param form
     */
    @SuppressWarnings("unchecked")
    private void loadIncomeTypesFromAccountLines(ActionForm form) {
        PaymentRequestForm preqForm = (PaymentRequestForm) form;

        PaymentRequestDocument doc = (PaymentRequestDocument) preqForm.getDocument();
        SpringContext.getBean(PurapAccountingService.class).updateAccountAmounts(doc);
        preqForm.refreshAccountSummmary();

        List<PurApItem> items = doc.getItems();
        List<AccountingLine> accountingLines = new ArrayList<AccountingLine>();
        List<PurApItemUseTax> useTaxItems = null;
        for (PurApItem item : items) {
            accountingLines.addAll(item.getSourceAccountingLines());

            if (doc.isUseTaxIndicator() && (item.getUseTaxItems() != null) && !item.getUseTaxItems().isEmpty()) {
                if (useTaxItems == null) {
                    useTaxItems = new ArrayList<PurApItemUseTax>();
                }

                useTaxItems.addAll(item.getUseTaxItems());
            }
        }

        doc.getIncomeTypeHandler().populateIncomeTypes(accountingLines, useTaxItems);
    }

    @SuppressWarnings("unchecked")
    private IncomeTypeContainer<PaymentRequestIncomeType, Integer> getIncomeTypeContainer(ActionForm form) {
        PaymentRequestForm preqForm = (PaymentRequestForm) form;
        return (IncomeTypeContainer<PaymentRequestIncomeType, Integer>) preqForm.getDocument();
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);
        // if we have no DocumentIncomeTypes on the document then lets pre-populate
        IncomeTypeContainer ict = getIncomeTypeContainer(kualiDocumentFormBase);

        if (ict.getIncomeTypes().isEmpty() && ict.getIncomeTypeHandler().isEditableRouteStatus()) {
            loadIncomeTypesFromAccountLines(kualiDocumentFormBase);
        }
    }
}
