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
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsPaymentHistoryReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsPaymentHistoryReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsPaymentHistoryReportService;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Action class for Contracts Grants Payment History Report Lookup.
 */
public class ContractsGrantsPaymentHistoryReportLookupAction extends ContractsGrantsReportLookupAction {

    /**
     * implementation of the print method for Contracts Grants Payment History Report Lookup.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsPaymentHistoryReportLookupForm cgPaymentHistoryReportLookupForm = (ContractsGrantsPaymentHistoryReportLookupForm) form;

        String methodToCall = findMethodToCall(form, request);
        if (methodToCall.equalsIgnoreCase("search")) {
            GlobalVariables.getUserSession().removeObjectsByPrefix(KRADConstants.SEARCH_METHOD);
        }

        Lookupable kualiLookupable = cgPaymentHistoryReportLookupForm.getLookupable();
        if (kualiLookupable == null) {
            throw new RuntimeException("Lookupable is null.");
        }

        List<ContractsGrantsPaymentHistoryReport> displayList = new ArrayList<ContractsGrantsPaymentHistoryReport>();
        List<ResultRow> resultTable = new ArrayList<ResultRow>();

        // validate search parameters
        kualiLookupable.validateSearchParameters(cgPaymentHistoryReportLookupForm.getFields());

        // this is for 200 limit. turn it off for report.
        boolean bounded = false;

        displayList = (List<ContractsGrantsPaymentHistoryReport>) kualiLookupable.performLookup(cgPaymentHistoryReportLookupForm, resultTable, bounded);

        Object sortIndexObject = GlobalVariables.getUserSession().retrieveObject(SORT_INDEX_SESSION_KEY);
        // set default sort index as 0 (payment Number)
        if (ObjectUtils.isNull(sortIndexObject)) {
            sortIndexObject = "0";
        }
        // get sort property
        String sortPropertyName = getFieldNameForSorting(Integer.parseInt(sortIndexObject.toString()), "ContractsGrantsPaymentHistoryReport");

        // sort list
        sortReport(displayList, sortPropertyName);

        // check field is valid for subtotal
        boolean isFieldSubtotalRequired = ArConstants.ReportsConstants.cgPaymentHistoryReportSubtotalFieldsList.contains(sortPropertyName);
        Map<String, List<KualiDecimal>> subTotalMap = new HashMap<String, List<KualiDecimal>>();

        if (isFieldSubtotalRequired) {
            subTotalMap = buildSubTotalMap(displayList, sortPropertyName);
        }

        // build report
        ContractsGrantsReportDataHolder cgPaymentHistoryReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ContractsGrantsPaymentHistoryReportDetailDataHolder> details = cgPaymentHistoryReportDataHolder.getDetails();

        for (ContractsGrantsPaymentHistoryReport cgPaymentHistoryReportEntry : displayList) {
            ContractsGrantsPaymentHistoryReportDetailDataHolder reportDetail = new ContractsGrantsPaymentHistoryReportDetailDataHolder();
            // set report data
            setReportDate(cgPaymentHistoryReportEntry, reportDetail);

            if (isFieldSubtotalRequired) {
                // set sortedFieldValue for grouping in the report
                reportDetail.setSortedFieldValue(getPropertyValue(cgPaymentHistoryReportEntry, sortPropertyName));
                reportDetail.setDisplaySubtotalInd(true);
                // set subTotal from subTotalMap
                reportDetail.setInvoiceSubTotal(subTotalMap.get(getPropertyValue(cgPaymentHistoryReportEntry, sortPropertyName)).get(0).bigDecimalValue());
                reportDetail.setPaymentSubTotal(subTotalMap.get(getPropertyValue(cgPaymentHistoryReportEntry, sortPropertyName)).get(1).bigDecimalValue());
            }
            else {
                // set this to empty string for not displaying subtotal
                reportDetail.setDisplaySubtotalInd(false);
            }
            details.add(reportDetail);
        }
        cgPaymentHistoryReportDataHolder.setDetails(details);

        // build search criteria for report
        buildReportForSearchCriteia(cgPaymentHistoryReportDataHolder.getSearchCriteria(), cgPaymentHistoryReportLookupForm.getFieldsForLookup(),ContractsGrantsPaymentHistoryReport.class);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String reportFileName = SpringContext.getBean(ContractsGrantsPaymentHistoryReportService.class).generateReport(cgPaymentHistoryReportDataHolder, baos);
        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, reportFileName + ReportGeneration.PDF_FILE_EXTENSION);
        return null;
    }

    /**
     * @param displayList
     * @param sortPropertyName
     * @return
     */
    private Map<String, List<KualiDecimal>> buildSubTotalMap(List<ContractsGrantsPaymentHistoryReport> displayList, String sortPropertyName) {
        Map<String, List<KualiDecimal>> returnSubTotalMap = new HashMap<String, List<KualiDecimal>>();
        // get list of sort fields
        List<String> valuesOfsortProperty = getListOfValuesSortedProperties(displayList, sortPropertyName);

        // calculate sub_total and build subTotalMap

        for (String value : valuesOfsortProperty) {
            KualiDecimal invoiceSubTotal = KualiDecimal.ZERO;
            KualiDecimal paymentSubTotal = KualiDecimal.ZERO;

            for (ContractsGrantsPaymentHistoryReport cgPaymentHistoryReportEntry : displayList) {
                // set fieldValue as "" when it is null
                if (value.equals(getPropertyValue(cgPaymentHistoryReportEntry, sortPropertyName))) {
                    invoiceSubTotal = invoiceSubTotal.add(cgPaymentHistoryReportEntry.getInvoiceAmount());
                    paymentSubTotal = paymentSubTotal.add(cgPaymentHistoryReportEntry.getPaymentAmount());
                }
            }

            List<KualiDecimal> allSubTotal = new ArrayList<KualiDecimal>();
            allSubTotal.add(0, invoiceSubTotal);
            allSubTotal.add(1, paymentSubTotal);
            returnSubTotalMap.put(value, allSubTotal);
        }
        return returnSubTotalMap;
    }

    /**
     * @param cgPaymentHistoryReportEntry
     * @param reportDetail
     */
    private void setReportDate(ContractsGrantsPaymentHistoryReport cgPaymentHistoryReportEntry, ContractsGrantsPaymentHistoryReportDetailDataHolder reportDetail) {

        reportDetail.setPaymentNumber(cgPaymentHistoryReportEntry.getPaymentNumber());
        reportDetail.setPaymentDate(cgPaymentHistoryReportEntry.getPaymentDate());
        reportDetail.setCustomerNumber(cgPaymentHistoryReportEntry.getCustomerNumber());
        reportDetail.setCustomerName(cgPaymentHistoryReportEntry.getCustomerName());

        BigDecimal paymentAmount = (ObjectUtils.isNull(cgPaymentHistoryReportEntry.getPaymentAmount())) ? BigDecimal.ZERO : cgPaymentHistoryReportEntry.getPaymentAmount().bigDecimalValue();
        reportDetail.setPaymentAmount(paymentAmount);


        reportDetail.setInvoiceNumber(cgPaymentHistoryReportEntry.getInvoiceNumber());

        BigDecimal invoiceAmount = (ObjectUtils.isNull(cgPaymentHistoryReportEntry.getInvoiceAmount())) ? BigDecimal.ZERO : cgPaymentHistoryReportEntry.getInvoiceAmount().bigDecimalValue();
        reportDetail.setInvoiceAmount(invoiceAmount);


        reportDetail.setAwardNumber(cgPaymentHistoryReportEntry.getAwardNumber().toString());
        reportDetail.setReversedIndicator(cgPaymentHistoryReportEntry.isReversedIndicator() ? "Yes" : "No");
        reportDetail.setAppliedIndicator(cgPaymentHistoryReportEntry.isAppliedIndicator() ? "Yes" : "No");

    }
}
