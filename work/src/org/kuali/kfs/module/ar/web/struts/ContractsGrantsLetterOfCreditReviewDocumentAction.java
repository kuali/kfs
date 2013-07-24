/*
 * Copyright 2007-2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.web.struts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLetterOfCreditReviewDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsLetterOfCreditReviewDocument;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;

/**
 * Action class for Contracts Grants LetterOfCredit Review Document.
 */
public class ContractsGrantsLetterOfCreditReviewDocumentAction extends KualiTransactionalDocumentActionBase {

    public ContractsGrantsLetterOfCreditReviewDocumentAction() {
        super();
    }

    /**
     * Do initialization for a new Contracts Grants LetterOfCredit Review Document
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        ((ContractsGrantsLetterOfCreditReviewDocument) kualiDocumentFormBase.getDocument()).initiateDocument();
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

        ContractsGrantsLetterOfCreditReviewDocumentForm contractsGrantsLetterOfCreditReviewDocumentForm = (ContractsGrantsLetterOfCreditReviewDocumentForm) form;
        ContractsGrantsLetterOfCreditReviewDocument contractsGrantsLetterOfCreditReviewDocument = (ContractsGrantsLetterOfCreditReviewDocument) contractsGrantsLetterOfCreditReviewDocumentForm.getDocument();
        contractsGrantsLetterOfCreditReviewDocument.clearInitFields();

        return super.refresh(mapping, form, request, response);
    }

    /**
     * Handles continue request. This request comes from the initial screen which gives Letter of Credit Fund Group and other
     * details Based on that, the contracts grants LetterOfCredit Review document is initially populated.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward continueLOCReview(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ContractsGrantsLetterOfCreditReviewDocumentForm contractsGrantsLetterOfCreditReviewDocumentForm = (ContractsGrantsLetterOfCreditReviewDocumentForm) form;
        ContractsGrantsLetterOfCreditReviewDocument contractsGrantsLetterOfCreditReviewDocument = (ContractsGrantsLetterOfCreditReviewDocument) contractsGrantsLetterOfCreditReviewDocumentForm.getDocument();

        if (StringUtils.isEmpty(contractsGrantsLetterOfCreditReviewDocument.getLetterOfCreditFundGroupCode()) || StringUtils.isBlank(contractsGrantsLetterOfCreditReviewDocument.getLetterOfCreditFundGroupCode())) {
            GlobalVariables.getMessageMap().putError("letterOfCreditFundGroup", KFSKeyConstants.ERROR_REQUIRED, "Letter of Credit Fund Group");
        }
        else {
            ContractsGrantsLetterOfCreditReviewDocument document = (ContractsGrantsLetterOfCreditReviewDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(contractsGrantsLetterOfCreditReviewDocument.getDocumentNumber());
            if (ObjectUtils.isNull(document)) {
                contractsGrantsLetterOfCreditReviewDocument.getDocumentHeader().setDocumentDescription("Letter Of Credit Review Document.");
                contractsGrantsLetterOfCreditReviewDocument.populateContractsGrantsLOCReviewDetails();
                SpringContext.getBean(DocumentService.class).saveDocument(contractsGrantsLetterOfCreditReviewDocument);
            }
            // To set the initial view to hide details.
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * To recalculate the amount to Draw for every contract control account
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward recalculateAmountToDraw(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ContractsGrantsLetterOfCreditReviewDocumentForm contractsGrantsLetterOfCreditReviewDocumentForm = (ContractsGrantsLetterOfCreditReviewDocumentForm) form;
        ContractsGrantsLetterOfCreditReviewDocument contractsGrantsLetterOfCreditReviewDocument = (ContractsGrantsLetterOfCreditReviewDocument) contractsGrantsLetterOfCreditReviewDocumentForm.getDocument();

        int indexOfLineToRecalculate = getSelectedLine(request);
        ContractsGrantsLetterOfCreditReviewDetail contractsGrantsLetterOfCreditReviewDetail = contractsGrantsLetterOfCreditReviewDocument.getHeaderReviewDetails().get(indexOfLineToRecalculate);
        contractsGrantsLetterOfCreditReviewDetail.setAmountToDraw(KualiDecimal.ZERO);// clear the cca amount to draw.
        contractsGrantsLetterOfCreditReviewDetail.setClaimOnCashBalance(KualiDecimal.ZERO);// clear the cca claim on cash balance
        for (ContractsGrantsLetterOfCreditReviewDetail detail : contractsGrantsLetterOfCreditReviewDocument.getAccountReviewDetails()) {
            // To set amount to Draw to 0 if there are blank values, to avoid exceptions.
            if (ObjectUtils.isNull(detail.getAmountToDraw()) || detail.getAmountToDraw().isNegative()) {
                detail.setAmountToDraw(KualiDecimal.ZERO);
            }

            if (detail.getProposalNumber().equals(contractsGrantsLetterOfCreditReviewDetail.getProposalNumber())) {// To get the appropriate
                                                                                                        // individual award account
                                                                                                        // rows.
                contractsGrantsLetterOfCreditReviewDetail.setAmountToDraw(contractsGrantsLetterOfCreditReviewDetail.getAmountToDraw().add(detail.getAmountToDraw()));
                contractsGrantsLetterOfCreditReviewDetail.setClaimOnCashBalance(contractsGrantsLetterOfCreditReviewDetail.getClaimOnCashBalance().add(detail.getClaimOnCashBalance()));

            }

            // Now to set funds Not Drawn as a difference betwen amountToDraw and hiddenAmountToDraw.
            detail.setFundsNotDrawn(detail.getHiddenAmountToDraw().subtract(detail.getAmountToDraw()));
            if (detail.getFundsNotDrawn().isNegative()) {
                GlobalVariables.getMessageMap().putError("fundsNotDrawn", ArKeyConstants.ContractsGrantsInvoiceConstants.ERROR_DOCUMENT_AMOUNT_TO_DRAW_INVALID);
                detail.setFundsNotDrawn(KualiDecimal.ZERO);
                detail.setAmountToDraw(detail.getHiddenAmountToDraw().subtract(detail.getFundsNotDrawn()));
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method is called when export button is clicked and export a csv file.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsLetterOfCreditReviewDocumentForm locForm = (ContractsGrantsLetterOfCreditReviewDocumentForm) form;
        ContractsGrantsLetterOfCreditReviewDocument document = (ContractsGrantsLetterOfCreditReviewDocument) locForm.getDocument();
        String docId = document.getDocumentNumber();
        String fundCode = document.getLetterOfCreditFundCode();
        String groupCode = document.getLetterOfCreditFundGroupCode();
        if (StringUtils.isEmpty(groupCode) || StringUtils.isBlank(groupCode)) {
            GlobalVariables.getMessageMap().putError("letterOfCreditFundGroup", KFSKeyConstants.ERROR_REQUIRED, "Letter of Credit Fund Group");
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        ContractsGrantsLetterOfCreditReviewDocument contractsGrantsLOCReviewDocument = (ContractsGrantsLetterOfCreditReviewDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        if (ObjectUtils.isNull(contractsGrantsLOCReviewDocument)) {
            document.getDocumentHeader().setDocumentDescription("LOC Review Document");
            document.populateContractsGrantsLOCReviewDetails();
            SpringContext.getBean(DocumentService.class).saveDocument(document);
        }

        ContractsGrantsInvoiceReportService reportService = SpringContext.getBean(ContractsGrantsInvoiceReportService.class);
        byte[] report = reportService.generateCSVToExport(document);
        if (report.length == 0) {
            throw new RuntimeException("Error: non-existent file or directory provided");
        }
        response.setContentType("text/csv");
        response.setHeader("Content-disposition", "attachment; filename=CSV-Export-" + document.getDocumentNumber() + "-" + document.getLetterOfCreditFundGroupCode() + ".csv");
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setContentLength(report.length);
        InputStream fis = new ByteArrayInputStream(report);
        IOUtils.copy(fis, response.getOutputStream());
        response.getOutputStream().flush();
        return null;
    }

    /**
     * This method forward the action to printInvoicePDF after checking the JS compatibility with the browser.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String basePath = getApplicationBaseUrl();
        ContractsGrantsLetterOfCreditReviewDocumentForm locForm = (ContractsGrantsLetterOfCreditReviewDocumentForm) form;
        ContractsGrantsLetterOfCreditReviewDocument document = (ContractsGrantsLetterOfCreditReviewDocument) locForm.getDocument();
        String docId = document.getDocumentNumber();
        String fundCode = document.getLetterOfCreditFundCode();
        String groupCode = document.getLetterOfCreditFundGroupCode();
        if (StringUtils.isEmpty(groupCode) || StringUtils.isBlank(groupCode)) {
            GlobalVariables.getMessageMap().putError("letterOfCreditFundGroup", KFSKeyConstants.ERROR_REQUIRED, "Letter of Credit Fund Group");
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        ContractsGrantsLetterOfCreditReviewDocument contractsGrantsLOCReviewDocument = (ContractsGrantsLetterOfCreditReviewDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        if (ObjectUtils.isNull(contractsGrantsLOCReviewDocument)) {
            document.getDocumentHeader().setDocumentDescription("LOC Review Document");
            document.populateContractsGrantsLOCReviewDetails();
            SpringContext.getBean(DocumentService.class).saveDocument(document);
        }
        String methodToCallPrintInvoicePDF = "printInvoicePDF";
        String methodToCallDocHandler = "docHandler";
        String printInvoicePDFUrl = getUrlForPrintInvoice(basePath, docId, methodToCallPrintInvoicePDF);
        String displayInvoiceTabbedPageUrl = getUrlForPrintInvoice(basePath, docId, methodToCallDocHandler);

        request.setAttribute("printPDFUrl", printInvoicePDFUrl);
        request.setAttribute("displayTabbedPageUrl", displayInvoiceTabbedPageUrl);
        return mapping.findForward("arPrintPDF");

    }

    /**
     * This method generates the invoice and provides it to the user to Print it.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward printInvoicePDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String invoiceDocId = request.getParameter(KFSConstants.PARAMETER_DOC_ID);
        ContractsGrantsLetterOfCreditReviewDocument contractsGrantsLOCReviewDocument = (ContractsGrantsLetterOfCreditReviewDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(invoiceDocId);
        if (ObjectUtils.isNotNull(contractsGrantsLOCReviewDocument)) {
            ContractsGrantsInvoiceReportService reportService = SpringContext.getBean(ContractsGrantsInvoiceReportService.class);
            byte[] report = reportService.generateInvoice(contractsGrantsLOCReviewDocument);

            StringBuilder fileName = new StringBuilder();
            fileName.append(contractsGrantsLOCReviewDocument.getLetterOfCreditFundCode());
            fileName.append("-");
            fileName.append(contractsGrantsLOCReviewDocument.getDocumentNumber());
            fileName.append(".pdf");

            if (report.length == 0) {
                System.out.println("No Report Generated");
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String contentDisposition = "";
            try {
                ArrayList master = new ArrayList();
                PdfCopy writer = null;

                // create a reader for the document
                PdfReader reader = new PdfReader(new ByteArrayInputStream(report));
                reader.consolidateNamedDestinations();

                // retrieve the total number of pages
                int n = reader.getNumberOfPages();
                List bookmarks = SimpleBookmark.getBookmark(reader);
                if (bookmarks != null) {
                    master.addAll(bookmarks);
                }

                // step 1: create a document-object
                Document document = new Document(reader.getPageSizeWithRotation(1));
                // step 2: create a writer that listens to the document
                writer = new PdfCopy(document, baos);
                // step 3: open the document
                document.open();
                // step 4: add content
                PdfImportedPage page;
                for (int i = 0; i < n;) {
                    ++i;
                    page = writer.getImportedPage(reader, i);
                    writer.addPage(page);
                }
                writer.freeReader(reader);
                if (!master.isEmpty()) {
                    writer.setOutlines(master);
                }
                // step 5: we close the document
                document.close();

                StringBuffer sbContentDispValue = new StringBuffer();
                String useJavascript = request.getParameter("useJavascript");
                if (useJavascript == null || useJavascript.equalsIgnoreCase("false")) {
                    sbContentDispValue.append("attachment");
                }
                else {
                    sbContentDispValue.append("inline");
                }
                sbContentDispValue.append("; filename=");
                sbContentDispValue.append(fileName);

                contentDisposition = sbContentDispValue.toString();

            }
            catch (Exception e) {
                e.printStackTrace();
            }

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", contentDisposition);
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentLength(baos.size());

            // write to output
            ServletOutputStream sos;
            sos = response.getOutputStream();
            baos.writeTo(sos);
            sos.flush();
            sos.close();
        }
        return null;
    }

    /**
     * Creates a URL to be used in printing the customer invoice document.
     *
     * @param basePath String: The base path of the current URL
     * @param docId String: The document ID of the document to be printed
     * @param methodToCall String: The name of the method that will be invoked to do this particular print
     * @return The URL
     */
    protected String getUrlForPrintInvoice(String basePath, String docId, String methodToCall) {
        StringBuffer result = new StringBuffer(basePath);
        result.append("/arContractsGrantsLOCReviewDocument.do?methodToCall=");
        result.append(methodToCall);
        if (ObjectUtils.isNotNull(docId)) {
            result.append("&docId=");
            result.append(docId);
        }
        result.append("&command=displayDocSearchView");

        return result.toString();
    }
}
