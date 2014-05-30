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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.businessobject.ReportPDFHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService;
import org.kuali.kfs.module.ar.report.service.FederalFinancialReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Action class for Federal Financial Report service.
 */
public class FederalFinancialReportAction extends KualiAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FederalFinancialReportAction.class);

    private static volatile FederalFinancialReportService federalFinancialReportService;

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
        String message = getFederalFinancialReportService().validate(ffrForm.getFederalForm(), ffrForm.getProposalNumber(), ffrForm.getFiscalYear(), ffrForm.getReportingPeriod(), ffrForm.getAgencyNumber());
        if (StringUtils.isEmpty(message)) {
            String basePath = getApplicationBaseUrl();
            String docId = ffrForm.getProposalNumber();
            String agencyNumber = ffrForm.getAgencyNumber();
            String formType = ffrForm.getFederalForm();
            String period = ffrForm.getReportingPeriod();
            String year = ffrForm.getFiscalYear();

            String methodToCallPrintInvoicePDF = "printInvoicePDF";
            String methodToCallDocHandler = "start";
            String printInvoicePDFUrl = getFederalFinancialReportService().getUrlForPrintInvoice(basePath, docId, period, year, agencyNumber, formType, methodToCallPrintInvoicePDF);
            String displayInvoiceTabbedPageUrl = getFederalFinancialReportService().getUrlForPrintInvoice(basePath, "", "", "", "", "", methodToCallDocHandler);

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
        String period = request.getParameter(FederalFinancialReportService.REPORTING_PERIOD);
        String year = request.getParameter(FederalFinancialReportService.FISCAL_YEAR);
        String formType = request.getParameter(FederalFinancialReportService.FEDERAL_FORM);
        String agencyNumber = request.getParameter(KFSPropertyConstants.AGENCY_NUMBER);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        ContractsAndGrantsBillingAward award = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAward.class, map);
        map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.AGENCY_NUMBER, agencyNumber);
        ContractsAndGrantsBillingAgency agency = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAgency.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAgency.class, map);

        // get directory of template
        String directory = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_HELP_URL_KEY);

        if (ObjectUtils.isNotNull(award) || ObjectUtils.isNotNull(agency)) {
            ContractsGrantsInvoiceReportService reportService = SpringContext.getBean(ContractsGrantsInvoiceReportService.class);
            File report = reportService.generateFederalFinancialForm(award, period, year, formType, agency);

            final String useJavascriptParameter = request.getParameter("useJavascript");
            final boolean useJavascript = !(StringUtils.isBlank(useJavascriptParameter) || StringUtils.equalsIgnoreCase(useJavascriptParameter, "false"));

            final ReportPDFHolder pdfHolder = getFederalFinancialReportService().pdferizeReport(report, formType, period, proposalNumber, agencyNumber, useJavascript);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", pdfHolder.getContentDisposition());
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentLength(pdfHolder.getReportBytes().size());

            // write to output
            ServletOutputStream sos;
            sos = response.getOutputStream();
            pdfHolder.getReportBytes().writeTo(sos);
            sos.flush();
            sos.close();
        }
        return null;

    }

    public FederalFinancialReportService getFederalFinancialReportService() {
        if (federalFinancialReportService == null) {
            federalFinancialReportService = SpringContext.getBean(FederalFinancialReportService.class);
        }
        return federalFinancialReportService;
    }
}
