/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.document.web.struts;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.service.CustomerCreditMemoDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerCreditMemoDocumentService;
import org.kuali.kfs.module.ar.document.validation.event.ContinueCustomerCreditMemoDocumentEvent;
import org.kuali.kfs.module.ar.document.validation.event.RecalculateCustomerCreditMemoDetailEvent;
import org.kuali.kfs.module.ar.document.validation.event.RecalculateCustomerCreditMemoDocumentEvent;
import org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService;
import org.kuali.kfs.module.ar.service.AccountsReceivablePdfHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.util.KfsWebUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.UrlFactory;

public class CustomerCreditMemoDocumentAction extends KualiTransactionalDocumentActionBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerCreditMemoDocumentAction.class);

    public CustomerCreditMemoDocumentAction() {
        super();
    }

    /**
     * Do initialization for a new customer credit memo.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        ((CustomerCreditMemoDocument) kualiDocumentFormBase.getDocument()).initiateDocument();
    }

    /**
     * This method loads the document by its provided document header id. This has been abstracted out so that it can be overridden
     * in children if the need arises.
     *
     * @param kualiDocumentFormBase
     * @throws WorkflowException
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
       super.loadDocument(kualiDocumentFormBase);
       ((CustomerCreditMemoDocument)kualiDocumentFormBase.getDocument()).populateCustomerCreditMemoDetailsAfterLoad();
    }

    /**
     * Clears out init tab.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward clearInitTab(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerCreditMemoDocumentForm customerCreditMemoDocumentForm = (CustomerCreditMemoDocumentForm) form;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) customerCreditMemoDocumentForm.getDocument();
        customerCreditMemoDocument.clearInitFields();

        return super.refresh(mapping, form, request, response);
    }

    /**
     * Handles continue request. This request comes from the initial screen which gives ref. invoice number.
     * Based on that, the customer credit memo is initially populated.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward continueCreditMemo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerCreditMemoDocumentForm customerCreditMemoDocumentForm = (CustomerCreditMemoDocumentForm) form;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) customerCreditMemoDocumentForm.getDocument();

        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME;
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new ContinueCustomerCreditMemoDocumentEvent(errorPath,customerCreditMemoDocument));
        if (rulePassed) {
            customerCreditMemoDocument.populateCustomerCreditMemoDetails();
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Based on user input this method recalculates a customer credit memo detail
     *
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward recalculateCustomerCreditMemoDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerCreditMemoDocumentForm customerCreditMemoDocumentForm = (CustomerCreditMemoDocumentForm) form;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument)customerCreditMemoDocumentForm.getDocument();

        int indexOfLineToRecalculate = getSelectedLine(request);
        CustomerCreditMemoDetail customerCreditMemoDetail = customerCreditMemoDocument.getCreditMemoDetails().get(indexOfLineToRecalculate);

        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.CUSTOMER_CREDIT_MEMO_DETAIL_PROPERTY_NAME + "[" + indexOfLineToRecalculate + "]";

        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new RecalculateCustomerCreditMemoDetailEvent(errorPath, customerCreditMemoDocument, customerCreditMemoDetail));
        if (rulePassed) {
            CustomerCreditMemoDetailService customerCreditMemoDetailService = SpringContext.getBean(CustomerCreditMemoDetailService.class);
            customerCreditMemoDetailService.recalculateCustomerCreditMemoDetail(customerCreditMemoDetail,customerCreditMemoDocument);
        } else {
            customerCreditMemoDocument.recalculateTotals(customerCreditMemoDetail);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method refreshes a customer credit memo detail
     *
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward refreshCustomerCreditMemoDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerCreditMemoDocumentForm customerCreditMemoDocForm = (CustomerCreditMemoDocumentForm) form;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument)customerCreditMemoDocForm.getDocument();
        int indexOfLineToRefresh = getSelectedLine(request);

        CustomerCreditMemoDetail customerCreditMemoDetail = customerCreditMemoDocument.getCreditMemoDetails().get(indexOfLineToRefresh);

        customerCreditMemoDetail.setCreditMemoItemQuantity(null);
        customerCreditMemoDetail.setCreditMemoItemTotalAmount(null);
        customerCreditMemoDetail.setCreditMemoItemTaxAmount(KualiDecimal.ZERO);
        customerCreditMemoDetail.setCreditMemoLineTotalAmount(KualiDecimal.ZERO);

        customerCreditMemoDocument.recalculateTotals(customerCreditMemoDetail);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method refreshes customer credit memo details and line with totals
     *
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward refreshCustomerCreditMemoDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerCreditMemoDocumentForm customerCreditMemoDocForm = (CustomerCreditMemoDocumentForm) form;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument)customerCreditMemoDocForm.getDocument();

        List<CustomerCreditMemoDetail> customerCreditMemoDetails = customerCreditMemoDocument.getCreditMemoDetails();
        for( CustomerCreditMemoDetail customerCreditMemoDetail : customerCreditMemoDetails ){
            customerCreditMemoDetail.setCreditMemoItemQuantity(null);
            customerCreditMemoDetail.setCreditMemoItemTotalAmount(null);
            customerCreditMemoDetail.setCreditMemoItemTaxAmount(KualiDecimal.ZERO);
            customerCreditMemoDetail.setCreditMemoLineTotalAmount(KualiDecimal.ZERO);
            customerCreditMemoDetail.setDuplicateCreditMemoItemTotalAmount(null);
        }
        customerCreditMemoDocument.setCrmTotalItemAmount(KualiDecimal.ZERO);
        customerCreditMemoDocument.setCrmTotalTaxAmount(KualiDecimal.ZERO);
        customerCreditMemoDocument.setCrmTotalAmount(KualiDecimal.ZERO);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Based on user input this method recalculates customer credit memo document <=> all customer credit memo details
     *
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward recalculateCustomerCreditMemoDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerCreditMemoDocumentForm customerCreditMemoDocumentForm = (CustomerCreditMemoDocumentForm) form;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument)customerCreditMemoDocumentForm.getDocument();

        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME;
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new RecalculateCustomerCreditMemoDocumentEvent(errorPath,customerCreditMemoDocument,false));
        if (rulePassed) {
            CustomerCreditMemoDocumentService customerCreditMemoDocumentService = SpringContext.getBean(CustomerCreditMemoDocumentService.class);
            customerCreditMemoDocumentService.recalculateCustomerCreditMemoDocument(customerCreditMemoDocument,false);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     *
     * This method...
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String basePath = getApplicationBaseUrl();
        String docId = ((CustomerCreditMemoDocumentForm) form).getDocument().getDocumentNumber();
        String printCreditMemoPDFUrl = getUrlForPrintCreditMemo(basePath, docId, ArConstants.PRINT_CREDIT_MEMO_PDF_METHOD);
        String displayInvoiceTabbedPageUrl = getUrlForPrintCreditMemo(basePath, docId, KFSConstants.DOC_HANDLER_METHOD);

        request.setAttribute(ArPropertyConstants.PRINT_PDF_URL, printCreditMemoPDFUrl);
        request.setAttribute(ArPropertyConstants.DISPLAY_TABBED_PAGE_URL, displayInvoiceTabbedPageUrl);
        request.setAttribute(KFSConstants.PARAMETER_DOC_ID, docId);
        String label = SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByTypeName(KFSConstants.FinancialDocumentTypeCodes.CUSTOMER_CREDIT_MEMO);
        request.setAttribute(ArPropertyConstants.PRINT_LABEL, label);
        return mapping.findForward(ArConstants.MAPPING_PRINT_PDF);
    }

    /**
     * This method generates the Customer Credit Memo PDF
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward printCreditMemoPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String creditMemoDocId = request.getParameter(KFSConstants.PARAMETER_DOC_ID);
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(creditMemoDocId);

        AccountsReceivableReportService reportService = SpringContext.getBean(AccountsReceivableReportService.class);
        File report = reportService.generateCreditMemo(customerCreditMemoDocument);

        if (report.length() == 0) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        byte[] content = Files.readAllBytes(report.toPath());
        ByteArrayOutputStream baos = SpringContext.getBean(AccountsReceivablePdfHelperService.class).buildPdfOutputStream(content);

        StringBuilder fileName = new StringBuilder();
        fileName.append(customerCreditMemoDocument.getFinancialDocumentReferenceInvoiceNumber());
        fileName.append(KFSConstants.DASH);
        fileName.append(customerCreditMemoDocument.getDocumentNumber());
        fileName.append(KFSConstants.ReportGeneration.PDF_FILE_EXTENSION);

        KfsWebUtils.saveMimeOutputStreamAsFile(response, KFSConstants.ReportGeneration.PDF_MIME_TYPE, baos, fileName.toString(), Boolean.parseBoolean(request.getParameter(KFSConstants.ReportGeneration.USE_JAVASCRIPT)));

        return null;
    }

    /**
     * Creates a URL to be used in printing the customer credit memo.
     *
     * @param basePath String: The base path of the current URL
     * @param docId String: The document ID of the document to be printed
     * @param methodToCall String: The name of the method that will be invoked to do this particular print
     * @return The URL
     */
    protected String getUrlForPrintCreditMemo(String basePath, String docId, String methodToCall) {
        String baseUrl = basePath + "/" + ArConstants.UrlActions.CUSTOMER_CREDIT_MEMO_DOCUMENT;
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, methodToCall);
        parameters.put(KFSConstants.PARAMETER_DOC_ID, docId);
        parameters.put(KFSConstants.PARAMETER_COMMAND, KewApiConstants.ACTIONLIST_COMMAND);

        return UrlFactory.parameterizeUrl(baseUrl, parameters);
    }

}
