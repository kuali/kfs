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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceSuspenseActivityReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsInvoiceSuspenseActivityReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceSuspenseActivityReportService;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.ui.ResultRow;

/**
 * Action class for for the Contracts and Grants Invoice Suspense Activity Report Lookup.
 */
public class ContractsGrantsInvoiceSuspenseActivityReportLookupAction extends ContractsGrantsReportLookupAction {

    /**
     * This method implements the print functionality for the Contracts and Grants Invoice Suspense Activity Report Lookup.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsInvoiceSuspenseActivityReportLookupForm cgInvoiceSuspenseActivityReportLookupForm = (ContractsGrantsInvoiceSuspenseActivityReportLookupForm) form;

        String methodToCall = findMethodToCall(form, request);
        if (methodToCall.equalsIgnoreCase("search")) {
            GlobalVariables.getUserSession().removeObjectsByPrefix(KRADConstants.SEARCH_METHOD);
        }

        Lookupable kualiLookupable = cgInvoiceSuspenseActivityReportLookupForm.getLookupable();
        if (kualiLookupable == null) {
            throw new RuntimeException("Lookupable is null.");
        }

        List<ContractsGrantsInvoiceSuspenseActivityReport> displayList = new ArrayList<ContractsGrantsInvoiceSuspenseActivityReport>();
        List<ResultRow> resultTable = new ArrayList<ResultRow>();

        // validate search parameters
        kualiLookupable.validateSearchParameters(cgInvoiceSuspenseActivityReportLookupForm.getFields());

        // this is for 200 limit. turn it off for report.
        boolean bounded = false;

        displayList = (List<ContractsGrantsInvoiceSuspenseActivityReport>) kualiLookupable.performLookup(cgInvoiceSuspenseActivityReportLookupForm, resultTable, bounded);

        Object sortIndexObject = GlobalVariables.getUserSession().retrieveObject(SORT_INDEX_SESSION_KEY);
        // set default sort index as 0
        if (ObjectUtils.isNull(sortIndexObject)) {
            sortIndexObject = "0";
        }
        // get sort property
        String sortPropertyName = getFieldNameForSorting(Integer.parseInt(sortIndexObject.toString()), "ContractsGrantsInvoiceSuspenseActivityReport");

        // sort list
        sortReport(displayList, sortPropertyName);

        // check field is valid for subtotal
        // this report doesn't have subtotal
        boolean isFieldSubtotalRequired = false;
        Map<String, KualiDecimal> subTotalMap = new HashMap<String, KualiDecimal>();

        // build report
        ContractsGrantsReportDataHolder cgInvoiceSuspenseActivityReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ContractsGrantsInvoiceSuspenseActivityReportDetailDataHolder> details = cgInvoiceSuspenseActivityReportDataHolder.getDetails();

        for (ContractsGrantsInvoiceSuspenseActivityReport cgInvoiceSuspenseActivityReportEntry : displayList) {
            ContractsGrantsInvoiceSuspenseActivityReportDetailDataHolder reportDetail = new ContractsGrantsInvoiceSuspenseActivityReportDetailDataHolder();
            // set report data
            setReportDate(cgInvoiceSuspenseActivityReportEntry, reportDetail);

            reportDetail.setDisplaySubtotalInd(false);

            details.add(reportDetail);
        }
        cgInvoiceSuspenseActivityReportDataHolder.setDetails(details);

        // build search criteria for report
        buildReportForSearchCriteia(cgInvoiceSuspenseActivityReportDataHolder.getSearchCriteria(), cgInvoiceSuspenseActivityReportLookupForm.getFieldsForLookup());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String reportFileName = SpringContext.getBean(ContractsGrantsInvoiceSuspenseActivityReportService.class).generateReport(cgInvoiceSuspenseActivityReportDataHolder, baos);
        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, reportFileName + ReportGeneration.PDF_FILE_EXTENSION);
        return null;
    }

    /**
     * @param cgInvoiceSuspenseActivityReportEntry
     * @param reportDetail
     */
    private void setReportDate(ContractsGrantsInvoiceSuspenseActivityReport cgInvoiceSuspenseActivityReportEntry, ContractsGrantsInvoiceSuspenseActivityReportDetailDataHolder reportDetail) {
        // reportDetail.setFundManager(cgInvoiceSuspenseActivityReportEntry.getFundManager());


        reportDetail.setSuspenseCategory(cgInvoiceSuspenseActivityReportEntry.getSuspensionCategoryCode());

        reportDetail.setCategoryDescription(cgInvoiceSuspenseActivityReportEntry.getCategoryDescription());
        reportDetail.setTotalInvoicesSuspended(cgInvoiceSuspenseActivityReportEntry.getTotalInvoicesSuspended());


    }
}
