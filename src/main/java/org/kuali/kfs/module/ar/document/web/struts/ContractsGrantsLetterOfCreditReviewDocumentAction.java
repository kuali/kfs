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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDocumentErrorLog;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLetterOfCreditReviewDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsLetterOfCreditReviewDocument;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService;
import org.kuali.kfs.module.ar.service.AccountsReceivablePdfHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.util.KfsWebUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Action class for Contracts & Grants LetterOfCredit Review Document.
 */
public class ContractsGrantsLetterOfCreditReviewDocumentAction extends KualiTransactionalDocumentActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsLetterOfCreditReviewDocumentAction.class);

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
     * details Based on that, the Contracts & Grants LetterOfCredit Review document is initially populated.
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

        if (StringUtils.isBlank(contractsGrantsLetterOfCreditReviewDocument.getLetterOfCreditFundCode()) && StringUtils.isBlank(contractsGrantsLetterOfCreditReviewDocument.getLetterOfCreditFundGroupCode())) {
            GlobalVariables.getMessageMap().putError(ArConstants.LETTER_OF_CREDIT_REVIEW_INIT_SECTION, ArKeyConstants.ERROR_LOC_REVIEW_FUND_OR_FUND_GROUP_REQUIRED);
        } else if (!StringUtils.isBlank(contractsGrantsLetterOfCreditReviewDocument.getLetterOfCreditFundCode()) && !StringUtils.isBlank(contractsGrantsLetterOfCreditReviewDocument.getLetterOfCreditFundGroupCode())) {
            GlobalVariables.getMessageMap().putError(ArConstants.LETTER_OF_CREDIT_REVIEW_INIT_SECTION, ArKeyConstants.ERROR_LOC_REVIEW_ONLY_ONE_FUND_OR_FUND_GROUP);
        }
        else {
            ContractsGrantsLetterOfCreditReviewDocument document = (ContractsGrantsLetterOfCreditReviewDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(contractsGrantsLetterOfCreditReviewDocument.getDocumentNumber());
            if (ObjectUtils.isNull(document)) {
                contractsGrantsLetterOfCreditReviewDocument.getDocumentHeader().setDocumentDescription(ArConstants.LETTER_OF_CREDIT_REVIEW_DOCUMENT);
                Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();
                if (contractsGrantsLetterOfCreditReviewDocument.populateContractsGrantsLOCReviewDetails(contractsGrantsInvoiceDocumentErrorLogs)) {
                    saveDocumentAndNote(contractsGrantsLetterOfCreditReviewDocument, contractsGrantsInvoiceDocumentErrorLogs);
                }
            }
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
        contractsGrantsLetterOfCreditReviewDetail.setAmountToDraw(KualiDecimal.ZERO); // clear the cca amount to draw.
        contractsGrantsLetterOfCreditReviewDetail.setClaimOnCashBalance(KualiDecimal.ZERO); // clear the cca claim on cash balance
        for (ContractsGrantsLetterOfCreditReviewDetail detail : contractsGrantsLetterOfCreditReviewDocument.getAccountReviewDetails()) {
            // To set amount to Draw to 0 if there are blank values, to avoid exceptions.
            if (ObjectUtils.isNull(detail.getAmountToDraw())) {
                detail.setAmountToDraw(KualiDecimal.ZERO);
            }

            if (detail.getProposalNumber().equals(contractsGrantsLetterOfCreditReviewDetail.getProposalNumber())) { // To get the appropriate individual award account rows.
                contractsGrantsLetterOfCreditReviewDetail.setAmountToDraw(contractsGrantsLetterOfCreditReviewDetail.getAmountToDraw().add(detail.getAmountToDraw()));
                contractsGrantsLetterOfCreditReviewDetail.setClaimOnCashBalance(contractsGrantsLetterOfCreditReviewDetail.getClaimOnCashBalance().add(detail.getClaimOnCashBalance()));

            }

            // Now to set funds Not Drawn as a difference betwen amountToDraw and hiddenAmountToDraw.
            detail.setFundsNotDrawn(detail.getHiddenAmountToDraw().subtract(detail.getAmountToDraw()));
            if (detail.getFundsNotDrawn().isNegative()) {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.FUNDS_NOT_DRAWN, ArKeyConstants.ContractsGrantsInvoiceConstants.ERROR_DOCUMENT_AMOUNT_TO_DRAW_INVALID);
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

        ContractsGrantsInvoiceReportService reportService = SpringContext.getBean(ContractsGrantsInvoiceReportService.class);
        byte[] report = reportService.convertLetterOfCreditReviewToCSV(document);
        if (report.length == 0) {
            throw new RuntimeException("Error: non-existent file or directory provided");
        }

        final String fundforFile = !StringUtils.isBlank(document.getLetterOfCreditFundGroupCode())
                ? document.getLetterOfCreditFundGroupCode()
                : document.getLetterOfCreditFundCode();
        WebUtils.saveMimeInputStreamAsFile(response, KFSConstants.ReportGeneration.CSV_MIME_TYPE, new ByteArrayInputStream(report), "CSV-Export-" + document.getDocumentNumber() + "-" + fundforFile + KFSConstants.ReportGeneration.CSV_FILE_EXTENSION, report.length);
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

        String printInvoicePDFUrl = getUrlForPrintInvoice(basePath, document.getDocumentNumber(), ArConstants.PRINT_INVOICE_PDF_METHOD);
        String displayInvoiceTabbedPageUrl = getUrlForPrintInvoice(basePath, document.getDocumentNumber(), KFSConstants.DOC_HANDLER_METHOD);

        request.setAttribute(ArPropertyConstants.PRINT_PDF_URL, printInvoicePDFUrl);
        request.setAttribute(ArPropertyConstants.DISPLAY_TABBED_PAGE_URL, displayInvoiceTabbedPageUrl);
        return mapping.findForward(ArConstants.MAPPING_PRINT_PDF);
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
            byte[] report = reportService.generateLOCReviewAsPdf(contractsGrantsLOCReviewDocument);

            if (report.length == 0) {
                LOG.warn("No Report Generated");
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }

            ByteArrayOutputStream baos = SpringContext.getBean(AccountsReceivablePdfHelperService.class).buildPdfOutputStream(report);

            StringBuilder fileName = new StringBuilder();
            fileName.append(contractsGrantsLOCReviewDocument.getLetterOfCreditFundCode());
            fileName.append(KFSConstants.DASH);
            fileName.append(contractsGrantsLOCReviewDocument.getDocumentNumber());
            fileName.append(KFSConstants.ReportGeneration.PDF_FILE_EXTENSION);

            KfsWebUtils.saveMimeOutputStreamAsFile(response, KFSConstants.ReportGeneration.PDF_MIME_TYPE, baos, fileName.toString(), Boolean.parseBoolean(request.getParameter(KFSConstants.ReportGeneration.USE_JAVASCRIPT)));
        }

        return null;
    }

    /**
     * Saves the ContractsGrantsLetterOfCreditReviewDocument and if necessary (i.e. there were validation errors for some
     * of the awards) saves a note indicating some awards didn't pass validation and aren't included on the document.
     *
     * @param contractsGrantsLetterOfCreditReviewDocument document to save
     * @param contractsGrantsInvoiceDocumentErrorLogs Collection of validation errors, if any
     * @throws WorkflowException
     */
    protected void saveDocumentAndNote(ContractsGrantsLetterOfCreditReviewDocument contractsGrantsLetterOfCreditReviewDocument, Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs) throws WorkflowException {
        SpringContext.getBean(DocumentService.class).saveDocument(contractsGrantsLetterOfCreditReviewDocument);
        if (contractsGrantsInvoiceDocumentErrorLogs.size() > 0) {
            String noteText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceConstants.ERROR_SOME_AWARDS_INVALID);
            Note note = SpringContext.getBean(DocumentService.class).createNoteFromDocument(contractsGrantsLetterOfCreditReviewDocument, noteText);
            note.setAuthorUniversalIdentifier(SpringContext.getBean(IdentityService.class).getPrincipalByPrincipalName(KFSConstants.SYSTEM_USER).getPrincipalId());
            contractsGrantsLetterOfCreditReviewDocument.addNote(note);
            SpringContext.getBean(NoteService.class).save(note);
        }
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
        String baseUrl = basePath + "/" + ArConstants.UrlActions.CONTRACTS_GRANTS_LETTER_OF_CREDIT_REVIEW_DOCUMENT;
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, methodToCall);
        parameters.put(KFSConstants.PARAMETER_DOC_ID, docId);
        parameters.put(KFSConstants.PARAMETER_COMMAND, KewApiConstants.ACTIONLIST_COMMAND);

        return UrlFactory.parameterizeUrl(baseUrl, parameters);
    }
}
