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
package org.kuali.kfs.module.ar.web.struts;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService;
import org.kuali.kfs.module.ar.report.service.FederalFinancialReportService;
import org.kuali.kfs.module.ar.service.AccountsReceivablePdfHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.util.KfsWebUtils;
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
        return mapping.findForward(KFSConstants.MAPPING_CANCEL);
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

            String printInvoicePDFUrl = getFederalFinancialReportService().getUrlForPrintInvoice(basePath, docId, period, year, agencyNumber, formType, ArConstants.PRINT_INVOICE_PDF_METHOD);
            String displayInvoiceTabbedPageUrl = getFederalFinancialReportService().getUrlForPrintInvoice(basePath, "", "", "", "", "", KFSConstants.START_METHOD);

            request.setAttribute(ArPropertyConstants.PRINT_PDF_URL, printInvoicePDFUrl);
            request.setAttribute(ArPropertyConstants.DISPLAY_TABBED_PAGE_URL, displayInvoiceTabbedPageUrl);
            return mapping.findForward(ArConstants.MAPPING_PRINT_PDF);
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

        if (ObjectUtils.isNotNull(award) || ObjectUtils.isNotNull(agency)) {
            ContractsGrantsInvoiceReportService reportService = SpringContext.getBean(ContractsGrantsInvoiceReportService.class);
            File report = reportService.generateFederalFinancialForm(award, period, year, formType, agency);

            byte[] content = Files.readAllBytes(report.toPath());
            ByteArrayOutputStream baos = SpringContext.getBean(AccountsReceivablePdfHelperService.class).buildPdfOutputStream(content);

            StringBuilder fileName = new StringBuilder();
            fileName.append(formType);
            fileName.append(KFSConstants.DASH);
            fileName.append(period);
            fileName.append(KFSConstants.DASH);
            if (StringUtils.equals(formType, ArConstants.FEDERAL_FORM_425)) {
                fileName.append(proposalNumber);
            } else {
                fileName.append(agencyNumber);
            }
            fileName.append(KFSConstants.ReportGeneration.PDF_FILE_EXTENSION);

            KfsWebUtils.saveMimeOutputStreamAsFile(response, KFSConstants.ReportGeneration.PDF_MIME_TYPE, baos, fileName.toString(), Boolean.parseBoolean(request.getParameter(KFSConstants.ReportGeneration.USE_JAVASCRIPT)));
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
