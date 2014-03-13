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

import org.apache.commons.collections.CollectionUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsSuspendedInvoiceDetailReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsSuspendedInvoiceDetailReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsSuspendedInvoiceDetailReportService;
import org.kuali.kfs.sys.KFSConstants;
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
 * Action Class for Contracts Grants Suspended Invoice Detail Report Lookup.
 */
public class ContractsGrantsSuspendedInvoiceDetailReportLookupAction extends ContractsGrantsReportLookupAction {

    /**
     * implementation for the print service for Contracts Grants Suspended Invoice Detail Report Lookup.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsSuspendedInvoiceDetailReportLookupForm cgSuspendedInvoiceDetailReportLookupForm = (ContractsGrantsSuspendedInvoiceDetailReportLookupForm) form;

        String methodToCall = findMethodToCall(form, request);
        if (methodToCall.equalsIgnoreCase("search")) {
            GlobalVariables.getUserSession().removeObjectsByPrefix(KRADConstants.SEARCH_METHOD);
        }

        Lookupable kualiLookupable = cgSuspendedInvoiceDetailReportLookupForm.getLookupable();
        if (kualiLookupable == null) {
            throw new RuntimeException("Lookupable is null.");
        }

        List<ContractsGrantsSuspendedInvoiceDetailReport> displayList = new ArrayList<ContractsGrantsSuspendedInvoiceDetailReport>();
        List<ResultRow> resultTable = new ArrayList<ResultRow>();

        // validate search parameters
        kualiLookupable.validateSearchParameters(cgSuspendedInvoiceDetailReportLookupForm.getFields());

        // this is for 200 limit. turn it off for report.
        boolean bounded = false;

        displayList = (List<ContractsGrantsSuspendedInvoiceDetailReport>) kualiLookupable.performLookup(cgSuspendedInvoiceDetailReportLookupForm, resultTable, bounded);

        Object sortIndexObject = GlobalVariables.getUserSession().retrieveObject(SORT_INDEX_SESSION_KEY);
        // set default sort index as 0
        if (ObjectUtils.isNull(sortIndexObject)) {
            sortIndexObject = "0";
        }
        // get sort property
        String sortPropertyName = getFieldNameForSorting(Integer.parseInt(sortIndexObject.toString()), "ContractsGrantsSuspendedInvoiceDetailReport");

        // sort list
        sortReport(displayList, sortPropertyName);

        // check field is valid for subtotal
        boolean isFieldSubtotalRequired = ArConstants.ReportsConstants.cgSuspendedInvoiceDetailReportSubtotalFieldsList.contains(sortPropertyName);
        Map<String, KualiDecimal> subTotalMap = new HashMap<String, KualiDecimal>();

        if (isFieldSubtotalRequired) {
            subTotalMap = buildSubTotalMap(displayList, sortPropertyName);
        }

        // build report
        ContractsGrantsReportDataHolder cgPaymentHistoryReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ContractsGrantsSuspendedInvoiceDetailReportDetailDataHolder> details = cgPaymentHistoryReportDataHolder.getDetails();

        for (ContractsGrantsSuspendedInvoiceDetailReport cgSuspendedInvoiceDetailReportEntry : displayList) {
            ContractsGrantsSuspendedInvoiceDetailReportDetailDataHolder reportDetail = new ContractsGrantsSuspendedInvoiceDetailReportDetailDataHolder();
            // set report data
            setReportDate(cgSuspendedInvoiceDetailReportEntry, reportDetail);

            if (isFieldSubtotalRequired) {
                // set sortedFieldValue for grouping in the report
                reportDetail.setSortedFieldValue(getPropertyValue(cgSuspendedInvoiceDetailReportEntry, sortPropertyName));
                reportDetail.setDisplaySubtotalInd(true);
                // set subTotal from subTotalMap
                reportDetail.setSubTotal(subTotalMap.get(getPropertyValue(cgSuspendedInvoiceDetailReportEntry, sortPropertyName)).bigDecimalValue());
            }
            else {
                // set this to empty string for not displaying subtotal
                reportDetail.setDisplaySubtotalInd(false);
            }
            details.add(reportDetail);
        }
        cgPaymentHistoryReportDataHolder.setDetails(details);

        // Avoid generating pdf if there were no search results were returned
        if (CollectionUtils.isEmpty(cgPaymentHistoryReportDataHolder.getDetails())){
            GlobalVariables.getMessageMap().putInfo(KFSConstants.DOCUMENT_ERRORS, ArKeyConstants.NO_VALUES_RETURNED);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // build search criteria for report
        buildReportForSearchCriteia(cgPaymentHistoryReportDataHolder.getSearchCriteria(), cgSuspendedInvoiceDetailReportLookupForm.getFieldsForLookup(), ContractsGrantsSuspendedInvoiceDetailReport.class);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String reportFileName = SpringContext.getBean(ContractsGrantsSuspendedInvoiceDetailReportService.class).generateReport(cgPaymentHistoryReportDataHolder, baos);
        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, reportFileName + ReportGeneration.PDF_FILE_EXTENSION);
        return null;
    }

    /**
     * @param displayList
     * @param sortPropertyName
     * @return
     */
    private Map<String, KualiDecimal> buildSubTotalMap(List<ContractsGrantsSuspendedInvoiceDetailReport> displayList, String sortPropertyName) {
        Map<String, KualiDecimal> returnSubTotalMap = new HashMap<String, KualiDecimal>();
        // get list of sort fields
        List<String> valuesOfsortProperty = getListOfValuesSortedProperties(displayList, sortPropertyName);

        // calculate sub_total and build subTotalMap

        for (String value : valuesOfsortProperty) {
            KualiDecimal subTotal = KualiDecimal.ZERO;
            for (ContractsGrantsSuspendedInvoiceDetailReport cgSuspendedInvoiceDetailReportEntry : displayList) {
                // set fieldValue as "" when it is null
                if (value.equals(getPropertyValue(cgSuspendedInvoiceDetailReportEntry, sortPropertyName))) {
                    subTotal = subTotal.add(cgSuspendedInvoiceDetailReportEntry.getAwardTotal());
                }
            }
            returnSubTotalMap.put(value, subTotal);
        }
        return returnSubTotalMap;
    }

    /**
     * @param cgSuspendedInvoiceDetailReportEntry
     * @param reportDetail
     */
    private void setReportDate(ContractsGrantsSuspendedInvoiceDetailReport cgSuspendedInvoiceDetailReportEntry, ContractsGrantsSuspendedInvoiceDetailReportDetailDataHolder reportDetail) {
        reportDetail.setSuspenseCategory(cgSuspendedInvoiceDetailReportEntry.getSuspensionCategoryCode());
        reportDetail.setDocumentNumber(cgSuspendedInvoiceDetailReportEntry.getDocumentNumber());
        reportDetail.setLetterOfCreditFundGroupCode(cgSuspendedInvoiceDetailReportEntry.getLetterOfCreditFundGroupCode());
        reportDetail.setFundManager(cgSuspendedInvoiceDetailReportEntry.getFundManagerPrincipalName());
        reportDetail.setProjectDirector(cgSuspendedInvoiceDetailReportEntry.getProjectDirectorPrincipalName());
        BigDecimal awardTotal = (ObjectUtils.isNull(cgSuspendedInvoiceDetailReportEntry.getAwardTotal())) ? BigDecimal.ZERO : cgSuspendedInvoiceDetailReportEntry.getAwardTotal().bigDecimalValue();
        reportDetail.setAwardTotal(awardTotal);
    }
}
