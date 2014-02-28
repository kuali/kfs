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
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsInvoiceReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService;
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
 * Action class for Contracts Grants Invoice Report Lookup.
 */
public class ContractsGrantsInvoiceReportLookupAction extends ContractsGrantsReportLookupAction {

    /**
     * This method implements the print functionality for the Contracts and Grants Invoice Report.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsInvoiceReportLookupForm cgInvoiceReportLookupForm = (ContractsGrantsInvoiceReportLookupForm) form;

        String methodToCall = findMethodToCall(form, request);
        if (methodToCall.equalsIgnoreCase("search")) {
            GlobalVariables.getUserSession().removeObjectsByPrefix(KRADConstants.SEARCH_METHOD);
        }

        Lookupable kualiLookupable = cgInvoiceReportLookupForm.getLookupable();
        if (kualiLookupable == null) {
            throw new RuntimeException("Lookupable is null.");
        }

        List<ContractsGrantsInvoiceReport> displayList = new ArrayList<ContractsGrantsInvoiceReport>();
        List<ResultRow> resultTable = new ArrayList<ResultRow>();

        // validate search parameters
        kualiLookupable.validateSearchParameters(cgInvoiceReportLookupForm.getFields());

        // this is for 200 limit. turn it off for report.
        boolean bounded = false;

        displayList = (List<ContractsGrantsInvoiceReport>) kualiLookupable.performLookup(cgInvoiceReportLookupForm, resultTable, bounded);

        Object sortIndexObject = GlobalVariables.getUserSession().retrieveObject(SORT_INDEX_SESSION_KEY);
        // set default sort index as 0 (Proposal Number)
        if (ObjectUtils.isNull(sortIndexObject)) {
            sortIndexObject = "0";
        }
        // get sort property
        String sortPropertyName = getFieldNameForSorting(Integer.parseInt(sortIndexObject.toString()), "ContractsGrantsInvoiceReport");

        // sort list
        sortReport(displayList, sortPropertyName);

        // check field is valid for subtotal
        boolean isFieldSubtotalRequired = ArConstants.ReportsConstants.cgInvoiceReportSubtotalFieldsList.contains(sortPropertyName);
        Map<String, List<KualiDecimal>> subTotalMap = new HashMap<String, List<KualiDecimal>>();

        if (isFieldSubtotalRequired) {
            subTotalMap = buildSubTotalMap(displayList, sortPropertyName);
        }

        // build report
        ContractsGrantsReportDataHolder cgInvoiceReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ContractsGrantsInvoiceReportDetailDataHolder> details = cgInvoiceReportDataHolder.getDetails();

        for (ContractsGrantsInvoiceReport cgInvoiceReportEntry : displayList) {
            ContractsGrantsInvoiceReportDetailDataHolder reportDetail = new ContractsGrantsInvoiceReportDetailDataHolder();
            // set report data
            setReportDate(cgInvoiceReportEntry, reportDetail);

            if (isFieldSubtotalRequired) {
                // set sortedFieldValue for grouping in the report
                reportDetail.setSortedFieldValue(getPropertyValue(cgInvoiceReportEntry, sortPropertyName));
                reportDetail.setDisplaySubtotalInd(true);
                // set subTotal from subTotalMap
                reportDetail.setInvoiceSubTotal(subTotalMap.get(getPropertyValue(cgInvoiceReportEntry, sortPropertyName)).get(0).bigDecimalValue());
                reportDetail.setPaymentSubTotal(subTotalMap.get(getPropertyValue(cgInvoiceReportEntry, sortPropertyName)).get(1).bigDecimalValue());
                reportDetail.setRemainingSubTotal(subTotalMap.get(getPropertyValue(cgInvoiceReportEntry, sortPropertyName)).get(2).bigDecimalValue());

            }
            else {
                // set this to empty string for not displaying subtotal
                reportDetail.setDisplaySubtotalInd(false);
            }
            details.add(reportDetail);
        }
        cgInvoiceReportDataHolder.setDetails(details);
        // set report name using invoiceReportOption
        String invoiceReportOption = cgInvoiceReportLookupForm.getFields().get(ArConstants.INVOICE_REPORT_OPTION);
        cgInvoiceReportDataHolder.setReportTitle(ArConstants.OUTSTANDING_INVOICE_REPORT);

        // build search criteria for report
        buildReportForSearchCriteia(cgInvoiceReportDataHolder.getSearchCriteria(), cgInvoiceReportLookupForm.getFieldsForLookup());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String reportFileName = SpringContext.getBean(ContractsGrantsInvoiceReportService.class).generateReport(cgInvoiceReportDataHolder, baos);
        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, reportFileName + ReportGeneration.PDF_FILE_EXTENSION);
        return null;
    }

    /**
     * @param displayList
     * @param sortPropertyName
     * @return
     */
    private Map<String, List<KualiDecimal>> buildSubTotalMap(List<ContractsGrantsInvoiceReport> displayList, String sortPropertyName) {
        Map<String, List<KualiDecimal>> returnSubTotalMap = new HashMap<String, List<KualiDecimal>>();
        // get list of sort fields
        List<String> valuesOfsortProperty = getListOfValuesSortedProperties(displayList, sortPropertyName);

        // calculate sub_total and build subTotalMap
        for (String value : valuesOfsortProperty) {
            KualiDecimal invoiceSubTotal = KualiDecimal.ZERO;
            KualiDecimal paymentSubTotal = KualiDecimal.ZERO;
            KualiDecimal remainingSubTotal = KualiDecimal.ZERO;

            for (ContractsGrantsInvoiceReport cgInvoiceReportEntry : displayList) {
                // set fieldValue as "" when it is null
                if (value.equals(getPropertyValue(cgInvoiceReportEntry, sortPropertyName))) {
                    invoiceSubTotal = invoiceSubTotal.add(cgInvoiceReportEntry.getInvoiceAmount());
                    paymentSubTotal = paymentSubTotal.add(cgInvoiceReportEntry.getPaymentAmount());
                    remainingSubTotal = remainingSubTotal.add(cgInvoiceReportEntry.getRemainingAmount());
                }
            }
            List<KualiDecimal> allSubTotal = new ArrayList<KualiDecimal>();
            allSubTotal.add(0, invoiceSubTotal);
            allSubTotal.add(1, paymentSubTotal);
            allSubTotal.add(2, remainingSubTotal);

            returnSubTotalMap.put(value, allSubTotal);
        }
        return returnSubTotalMap;
    }

    /**
     * @param cgInvoiceReportEntry
     * @param reportDetail
     */
    private void setReportDate(ContractsGrantsInvoiceReport cgInvoiceReportEntry, ContractsGrantsInvoiceReportDetailDataHolder reportDetail) {

        reportDetail.setProposalNumber(cgInvoiceReportEntry.getProposalNumber());
        reportDetail.setDocumentNumber(cgInvoiceReportEntry.getDocumentNumber());
        reportDetail.setInvoiceType(cgInvoiceReportEntry.getInvoiceType());
        reportDetail.setInvoiceDate(cgInvoiceReportEntry.getInvoiceDate());
        reportDetail.setInvoiceDueDate(cgInvoiceReportEntry.getInvoiceDueDate());
        reportDetail.setOpenInvoiceIndicator(cgInvoiceReportEntry.getOpenInvoiceIndicator());
        reportDetail.setCustomerNumber(cgInvoiceReportEntry.getCustomerNumber());
        reportDetail.setCustomerName(cgInvoiceReportEntry.getCustomerName());
        BigDecimal invoiceAmount = (ObjectUtils.isNull(cgInvoiceReportEntry.getInvoiceAmount())) ? BigDecimal.ZERO : cgInvoiceReportEntry.getInvoiceAmount().bigDecimalValue();
        reportDetail.setInvoiceAmount(invoiceAmount);
        BigDecimal paymentAmount = (ObjectUtils.isNull(cgInvoiceReportEntry.getPaymentAmount())) ? BigDecimal.ZERO : cgInvoiceReportEntry.getPaymentAmount().bigDecimalValue();
        reportDetail.setPaymentAmount(paymentAmount);
        BigDecimal remainingAmount = (ObjectUtils.isNull(cgInvoiceReportEntry.getRemainingAmount())) ? BigDecimal.ZERO : cgInvoiceReportEntry.getRemainingAmount().bigDecimalValue();
        reportDetail.setRemainingAmount(remainingAmount);
        reportDetail.setAgeInDays(cgInvoiceReportEntry.getAgeInDays());

    }
}
