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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.WebUtils;

/**
 * Action class for Contracts Grants Invoice Report Lookup.
 */
public class ContractsGrantsInvoiceReportLookupAction extends ContractsGrantsReportLookupAction {
    /**
     * This method implements the print functionality for the Contracts and Grants Invoice Report.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsInvoiceReportLookupForm cgInvoiceReportLookupForm = (ContractsGrantsInvoiceReportLookupForm) form;

        List<ContractsGrantsInvoiceReport> displayList = lookupReportValues(cgInvoiceReportLookupForm, request, true);
        final String sortPropertyName = sortReportValues(displayList, "ContractsGrantsInvoiceReport");

        ContractsGrantsReportDataHolder cgInvoiceReportDataHolder = getContractsGrantsReportDataBuilderService().buildReportDataHolder(displayList, sortPropertyName);
        // set report name using invoiceReportOption
        String invoiceReportOption = cgInvoiceReportLookupForm.getFields().get(ArConstants.INVOICE_REPORT_OPTION);
        cgInvoiceReportDataHolder.setReportTitle(ArConstants.OUTSTANDING_INVOICE_REPORT);

        // build search criteria for report
        buildReportForSearchCriteria(cgInvoiceReportDataHolder.getSearchCriteria(), cgInvoiceReportLookupForm.getFieldsForLookup(), ContractsGrantsInvoiceReport.class);

        //To avoid opening pdf if search results = none.
        if(CollectionUtils.isEmpty(cgInvoiceReportDataHolder.getDetails())){
            // this is so the message about no search results shows up in the same place as when doing a search
            request.setAttribute(KFSPropertyConstants.REQUEST_SEARCH_RESULTS_ACTUAL_SIZE, 0 );
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String reportFileName = SpringContext.getBean(ContractsGrantsInvoiceReportService.class).generateReport(cgInvoiceReportDataHolder, baos);
        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, reportFileName + ReportGeneration.PDF_FILE_EXTENSION);
        return null;
    }

    /**
     * Returns "contractsGrantsInvoiceReportBuilderService"
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#getReportBuilderServiceBeanName()
     */
    @Override
    public String getReportBuilderServiceBeanName() {
        return ArConstants.ReportBuilderDataServiceBeanNames.CONTRACTS_GRANTS_INVOICE;
    }
}