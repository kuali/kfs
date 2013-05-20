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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsSuspendedInvoiceReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsSuspendedInvoiceReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsSuspendedInvoiceReportService;
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
 * Action Class for Contracts Grants Suspended Invoice Report Lookup.
 */
public class ContractsGrantsSuspendedInvoiceReportLookupAction extends ContractsGrantsReportLookupAction {

    /**
     * implementation for the print service for Contracts Grants Suspended Invoice Report Lookup.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsSuspendedInvoiceReportLookupForm cgSuspendedInvoiceReportLookupForm = (ContractsGrantsSuspendedInvoiceReportLookupForm) form;

        String methodToCall = findMethodToCall(form, request);
        if (methodToCall.equalsIgnoreCase("search")) {
            GlobalVariables.getUserSession().removeObjectsByPrefix(KRADConstants.SEARCH_METHOD);
        }

        Lookupable kualiLookupable = cgSuspendedInvoiceReportLookupForm.getLookupable();
        if (kualiLookupable == null) {
            throw new RuntimeException("Lookupable is null.");
        }

        List<ContractsGrantsSuspendedInvoiceReport> displayList = new ArrayList<ContractsGrantsSuspendedInvoiceReport>();
        List<ResultRow> resultTable = new ArrayList<ResultRow>();

        // validate search parameters
        kualiLookupable.validateSearchParameters(cgSuspendedInvoiceReportLookupForm.getFields());

        // this is for 200 limit. turn it off for report.
        boolean bounded = false;

        displayList = (List<ContractsGrantsSuspendedInvoiceReport>) kualiLookupable.performLookup(cgSuspendedInvoiceReportLookupForm, resultTable, bounded);

        Object sortIndexObject = GlobalVariables.getUserSession().retrieveObject(SORT_INDEX_SESSION_KEY);
        // set default sort index as 0
        if (ObjectUtils.isNull(sortIndexObject)) {
            sortIndexObject = "0";
        }
        // get sort property
        String sortPropertyName = getFieldNameForSorting(Integer.parseInt(sortIndexObject.toString()), "ContractsGrantsSuspendedInvoiceReport");

        // sort list
        sortReport(displayList, sortPropertyName);

        // check field is valid for subtotal
        boolean isFieldSubtotalRequired = ArConstants.ReportsConstants.cgSuspendedInvoiceReportSubtotalFieldsList.contains(sortPropertyName);
        Map<String, KualiDecimal> subTotalMap = new HashMap<String, KualiDecimal>();

        if (isFieldSubtotalRequired) {
            subTotalMap = buildSubTotalMap(displayList, sortPropertyName);
        }

        // build report
        ContractsGrantsReportDataHolder cgPaymentHistoryReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ContractsGrantsSuspendedInvoiceReportDetailDataHolder> details = cgPaymentHistoryReportDataHolder.getDetails();

        for (ContractsGrantsSuspendedInvoiceReport cgSuspendedInvoiceReportEntry : displayList) {
            ContractsGrantsSuspendedInvoiceReportDetailDataHolder reportDetail = new ContractsGrantsSuspendedInvoiceReportDetailDataHolder();
            // set report data
            setReportDate(cgSuspendedInvoiceReportEntry, reportDetail);

            if (isFieldSubtotalRequired) {
                // set sortedFieldValue for grouping in the report
                reportDetail.setSortedFieldValue(getPropertyValue(cgSuspendedInvoiceReportEntry, sortPropertyName));
                reportDetail.setDisplaySubtotalInd(true);
                // set subTotal from subTotalMap
                reportDetail.setSubTotal(subTotalMap.get(getPropertyValue(cgSuspendedInvoiceReportEntry, sortPropertyName)).bigDecimalValue());
            }
            else {
                // set this to empty string for not displaying subtotal
                reportDetail.setDisplaySubtotalInd(false);
            }
            details.add(reportDetail);
        }
        cgPaymentHistoryReportDataHolder.setDetails(details);

        // build search criteria for report
        buildReportForSearchCriteia(cgPaymentHistoryReportDataHolder.getSearchCriteria(), cgSuspendedInvoiceReportLookupForm.getFieldsForLookup());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String reportFileName = SpringContext.getBean(ContractsGrantsSuspendedInvoiceReportService.class).generateReport(cgPaymentHistoryReportDataHolder, baos);
        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, reportFileName + ReportGeneration.PDF_FILE_EXTENSION);
        return null;
    }

    /**
     * @param displayList
     * @param sortPropertyName
     * @return
     */
    private Map<String, KualiDecimal> buildSubTotalMap(List<ContractsGrantsSuspendedInvoiceReport> displayList, String sortPropertyName) {
        Map<String, KualiDecimal> returnSubTotalMap = new HashMap<String, KualiDecimal>();
        // get list of sort fields
        List<String> valuesOfsortProperty = getListOfValuesSortedProperties(displayList, sortPropertyName);

        // calculate sub_total and build subTotalMap

        for (String value : valuesOfsortProperty) {
            KualiDecimal subTotal = KualiDecimal.ZERO;
            for (ContractsGrantsSuspendedInvoiceReport cgSuspendedInvoiceReportEntry : displayList) {
                // set fieldValue as "" when it is null
                if (value.equals(getPropertyValue(cgSuspendedInvoiceReportEntry, sortPropertyName))) {
                    subTotal = subTotal.add(cgSuspendedInvoiceReportEntry.getAwardTotal());
                }
            }
            returnSubTotalMap.put(value, subTotal);
        }
        return returnSubTotalMap;
    }

    /**
     * @param cgSuspendedInvoiceReportEntry
     * @param reportDetail
     */
    private void setReportDate(ContractsGrantsSuspendedInvoiceReport cgSuspendedInvoiceReportEntry, ContractsGrantsSuspendedInvoiceReportDetailDataHolder reportDetail) {
        reportDetail.setSuspenseCategory(cgSuspendedInvoiceReportEntry.getSuspensionCategoryCode());
        reportDetail.setDocumentNumber(cgSuspendedInvoiceReportEntry.getDocumentNumber());
        reportDetail.setLetterOfCreditFundGroupCode(cgSuspendedInvoiceReportEntry.getLetterOfCreditFundGroupCode());
        reportDetail.setFundManager(cgSuspendedInvoiceReportEntry.getFundManagerPrincipalName());
        reportDetail.setProjectDirector(cgSuspendedInvoiceReportEntry.getProjectDirectorPrincipalName());
        BigDecimal awardTotal = (ObjectUtils.isNull(cgSuspendedInvoiceReportEntry.getAwardTotal())) ? BigDecimal.ZERO : cgSuspendedInvoiceReportEntry.getAwardTotal().bigDecimalValue();
        reportDetail.setAwardTotal(awardTotal);
    }
}
