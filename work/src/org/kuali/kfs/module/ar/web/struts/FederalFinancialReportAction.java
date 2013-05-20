/*
 * Copyright 2011 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.web.struts;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.document.service.CashReceiptCoverSheetService;

import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.module.ar.document.ContractsGrantsLOCReviewDocument;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.kns.web.struts.action.KualiAction;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;

/**
 * Action class for Federal Financial Report service.
 */
public class FederalFinancialReportAction extends KualiAction {

    private static final String REPORTING_PERIOD = "reportingPeriod";
    private static final String FEDERAL_FORM = "federalForm";
    private static final String FISCAL_YEAR = "fiscalYear";
    private static final String FINANCIAL_FORM_REQUIRED = "Please select a Financial Form to generate.";
    private static final Object FEDERAL_FORM_425 = "425";
    private static final Object FEDERAL_FORM_425A = "425A";
    private static final String FISCAL_YEAR_AND_PERIOD_REQUIRED = "Enter both period and fiscal year.";
    private static final String PROPOSAL_NUMBER_REQUIRED = "Please enter a proposal Number for SF425.";
    private static final String AGENCY_REQUIRED = "Please enter an Agency for SF425A.";


    /**
     * Constructs a FederalFinancialReportAction.java.
     */
    public FederalFinancialReportAction() {
        super();
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        FederalFinancialReportForm ffrForm = (FederalFinancialReportForm) form;
        ffrForm.setReportingPeriod(null);
        ffrForm.setFiscalYear(null);
        ffrForm.setProposalNumber(null);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method receives the print action and forwards it accordingly.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        FederalFinancialReportForm ffrForm = (FederalFinancialReportForm) form;
        String message = this.validate(ffrForm);
        if (StringUtils.isEmpty(message)) {
            String basePath = getBasePath(request);
            String docId = ffrForm.getProposalNumber();
            String agencyNumber = ffrForm.getAgencyNumber();
            String formType = ffrForm.getFederalForm();
            String period = ffrForm.getReportingPeriod();
            String year = ffrForm.getFiscalYear();

            String methodToCallPrintInvoicePDF = "printInvoicePDF";
            String methodToCallDocHandler = "start";
            String printInvoicePDFUrl = getUrlForPrintInvoice(basePath, docId, period, year, agencyNumber, formType, methodToCallPrintInvoicePDF);
            String displayInvoiceTabbedPageUrl = getUrlForPrintInvoice(basePath, "", "", "", "", "", methodToCallDocHandler);

            request.setAttribute("printPDFUrl", printInvoicePDFUrl);
            request.setAttribute("displayTabbedPageUrl", displayInvoiceTabbedPageUrl);
            return mapping.findForward("arPrintPDF");
        }
        else {
            ffrForm.setError(message);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
    }

    /**
     * Validates the user input.
     * 
     * @param form
     * @return
     */
    private String validate(FederalFinancialReportForm form) {
        if (StringUtils.isNotEmpty(form.getFederalForm())) {
            if (FEDERAL_FORM_425.equals(form.getFederalForm()) && ObjectUtils.isNotNull(form.getProposalNumber())) {
                if (StringUtils.isEmpty(form.getFiscalYear()) || StringUtils.isEmpty(form.getReportingPeriod()))
                    return FISCAL_YEAR_AND_PERIOD_REQUIRED;
            }
            else if (FEDERAL_FORM_425.equals(form.getFederalForm()) && ObjectUtils.isNull(form.getProposalNumber())) {
                return PROPOSAL_NUMBER_REQUIRED;
            }
            else if (FEDERAL_FORM_425A.equals(form.getFederalForm()) && ObjectUtils.isNotNull(form.getAgencyNumber())) {
                if (StringUtils.isEmpty(form.getFiscalYear()) || StringUtils.isEmpty(form.getReportingPeriod()))
                    return FISCAL_YEAR_AND_PERIOD_REQUIRED;
            }
            else if (FEDERAL_FORM_425A.equals(form.getFederalForm()) && ObjectUtils.isNull(form.getAgencyNumber())) {
                return AGENCY_REQUIRED;
            }
            return "";
        }
        return FINANCIAL_FORM_REQUIRED;
    }

    /**
     * This method generates the pdf file and provides it to the user to Print it.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward printInvoicePDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String proposalNumber = request.getParameter(KFSConstants.PARAMETER_DOC_ID);
        String period = request.getParameter(REPORTING_PERIOD);
        String year = request.getParameter(FISCAL_YEAR);
        String formType = request.getParameter(FEDERAL_FORM);
        String agencyNumber = request.getParameter(KFSPropertyConstants.AGENCY_NUMBER);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        ContractsAndGrantsCGBAward award = (ContractsAndGrantsCGBAward) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsCGBAward.class).getExternalizableBusinessObject(ContractsAndGrantsCGBAward.class, map);
        map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.AGENCY_NUMBER, agencyNumber);
        ContractsAndGrantsCGBAgency agency = (ContractsAndGrantsCGBAgency) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsCGBAgency.class).getExternalizableBusinessObject(ContractsAndGrantsCGBAgency.class, map);

        // get directory of template
        String directory = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_HELP_URL_KEY);

        if (ObjectUtils.isNotNull(award) || ObjectUtils.isNotNull(agency)) {
            ContractsGrantsInvoiceReportService reportService = SpringContext.getBean(ContractsGrantsInvoiceReportService.class);
            File report = reportService.generateFederalFinancialForm(award, period, year, formType, agency);

            StringBuilder fileName = new StringBuilder();
            fileName.append(formType);
            fileName.append("-");
            fileName.append(period);
            if (ObjectUtils.isNotNull(proposalNumber)) {
                fileName.append("-");
                fileName.append(proposalNumber);
            }
            else if (ObjectUtils.isNotNull(agencyNumber)) {
                fileName.append("-");
                fileName.append(agencyNumber);
            }
            fileName.append(".pdf");

            if (report.length() == 0) {
                System.out.println("No Report Generated");
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String contentDisposition = "";
            try {
                ArrayList master = new ArrayList();
                PdfCopy writer = null;

                // create a reader for the document
                String reportName = report.getAbsolutePath();
                PdfReader reader = new PdfReader(reportName);
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
     * Creates a URL to be used in printing the federal forms.
     * 
     * @param basePath String: The base path of the current URL
     * @param docId String: The document ID of the document to be printed
     * @param methodToCall String: The name of the method that will be invoked to do this particular print
     * @return The URL
     */
    protected String getUrlForPrintInvoice(String basePath, String docId, String period, String year, String agencyNumber, String formType, String methodToCall) {
        StringBuffer result = new StringBuffer(basePath);
        result.append("/arFederalFinancialReport.do?methodToCall=");
        result.append(methodToCall);
        if (StringUtils.isNotEmpty(formType)) {
            result.append("&" + FEDERAL_FORM + "=");
            result.append(formType);
        }
        if (StringUtils.isNotEmpty(agencyNumber)) {
            result.append("&" + KFSPropertyConstants.AGENCY_NUMBER + "=");
            result.append(agencyNumber);
        }
        if (StringUtils.isNotEmpty(period)) {
            result.append("&" + REPORTING_PERIOD + "=");
            result.append(period);
        }
        if (StringUtils.isNotEmpty(year)) {
            result.append("&" + FISCAL_YEAR + "=");
            result.append(year);
        }
        if (StringUtils.isNotEmpty(docId)) {
            result.append("&docId=");
            result.append(docId);
            result.append("&command=displayDocSearchView");
        }

        return result.toString();
    }
}
