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
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLOCDrawDetailsReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsLOCDrawDetailsReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsLOCDrawDetailsReportService;
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
 * Action Class for the Contracts Grants LOC Draw Details Report Lookup.
 */
public class ContractsGrantsLOCDrawDetailsReportLookupAction extends ContractsGrantsReportLookupAction {

    /**
     * This method implements the print functionality for the Contracts Grants LOC Draw Details Report Lookup.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsLOCDrawDetailsReportLookupForm cgLOCDrawDetailsReportLookupForm = (ContractsGrantsLOCDrawDetailsReportLookupForm) form;

        String methodToCall = findMethodToCall(form, request);
        if (methodToCall.equalsIgnoreCase("search")) {
            GlobalVariables.getUserSession().removeObjectsByPrefix(KRADConstants.SEARCH_METHOD);
        }

        Lookupable kualiLookupable = cgLOCDrawDetailsReportLookupForm.getLookupable();
        if (kualiLookupable == null) {
            throw new RuntimeException("Lookupable is null.");
        }

        List<ContractsGrantsLOCDrawDetailsReport> displayList = new ArrayList<ContractsGrantsLOCDrawDetailsReport>();
        List<ResultRow> resultTable = new ArrayList<ResultRow>();

        // validate search parameters
        kualiLookupable.validateSearchParameters(cgLOCDrawDetailsReportLookupForm.getFields());

        // this is for 200 limit. turn it off for report.
        boolean bounded = false;

        displayList = (List<ContractsGrantsLOCDrawDetailsReport>) kualiLookupable.performLookup(cgLOCDrawDetailsReportLookupForm, resultTable, bounded);

        Object sortIndexObject = GlobalVariables.getUserSession().retrieveObject(SORT_INDEX_SESSION_KEY);
        // set default sort index as 0 (Proposal Number)
        if (ObjectUtils.isNull(sortIndexObject)) {
            sortIndexObject = "0";
        }
        // get sort property
        String sortPropertyName = getFieldNameForSorting(Integer.parseInt(sortIndexObject.toString()), "ContractsGrantsLOCDrawDetailsReport");

        // sort list
        sortReport(displayList, sortPropertyName);

        // check field is valid for subtotal
        boolean isFieldSubtotalRequired = ArConstants.ReportsConstants.cgLOCDrawDetailsReportSubtotalFieldsList.contains(sortPropertyName);
        Map<String, KualiDecimal> subTotalMap = new HashMap<String, KualiDecimal>();

        if (isFieldSubtotalRequired) {
            subTotalMap = buildSubTotalMap(displayList, sortPropertyName);
        }

        // build report
        ContractsGrantsReportDataHolder cgLOCDrawDetailsReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ContractsGrantsLOCDrawDetailsReportDetailDataHolder> details = cgLOCDrawDetailsReportDataHolder.getDetails();

        for (ContractsGrantsLOCDrawDetailsReport cgLOCDrawDetailsReportEntry : displayList) {
            ContractsGrantsLOCDrawDetailsReportDetailDataHolder reportDetail = new ContractsGrantsLOCDrawDetailsReportDetailDataHolder();
            // set report data
            setReportDate(cgLOCDrawDetailsReportEntry, reportDetail);

            if (isFieldSubtotalRequired) {
                // set sortedFieldValue for grouping in the report
                reportDetail.setSortedFieldValue(getPropertyValue(cgLOCDrawDetailsReportEntry, sortPropertyName));
                reportDetail.setDisplaySubtotalInd(true);
                // set subTotal from subTotalMap
                reportDetail.setSubTotal(subTotalMap.get(getPropertyValue(cgLOCDrawDetailsReportEntry, sortPropertyName)).bigDecimalValue());
            }
            else {
                // set this to empty string for not displaying subtotal
                reportDetail.setDisplaySubtotalInd(false);
            }
            details.add(reportDetail);
        }
        cgLOCDrawDetailsReportDataHolder.setDetails(details);

        // build search criteria for report
        buildReportForSearchCriteia(cgLOCDrawDetailsReportDataHolder.getSearchCriteria(), cgLOCDrawDetailsReportLookupForm.getFieldsForLookup(), ContractsGrantsLOCDrawDetailsReport.class);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String reportFileName = SpringContext.getBean(ContractsGrantsLOCDrawDetailsReportService.class).generateReport(cgLOCDrawDetailsReportDataHolder, baos);
        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, reportFileName + ReportGeneration.PDF_FILE_EXTENSION);
        return null;
    }


    /**
     * @param displayList
     * @param sortPropertyName
     * @return
     */
    private Map<String, KualiDecimal> buildSubTotalMap(List<ContractsGrantsLOCDrawDetailsReport> displayList, String sortPropertyName) {
        Map<String, KualiDecimal> returnSubTotalMap = new HashMap<String, KualiDecimal>();
        // get list of sort fields
        List<String> valuesOfsortProperty = getListOfValuesSortedProperties(displayList, sortPropertyName);

        // calculate sub_total and build subTotalMap
        for (String value : valuesOfsortProperty) {
            KualiDecimal subTotal = KualiDecimal.ZERO;
            for (ContractsGrantsLOCDrawDetailsReport cgLOCDrawDetailsReportEntry : displayList) {
                // set fieldValue as "" when it is null
                if (value.equals(getPropertyValue(cgLOCDrawDetailsReportEntry, sortPropertyName))) {
                    subTotal = subTotal.add(cgLOCDrawDetailsReportEntry.getAmountToDraw());
                }
            }
            returnSubTotalMap.put(value, subTotal);
        }
        return returnSubTotalMap;
    }

    /**
     * @param cgLOCDrawDetailsReportEntry
     * @param reportDetail
     */
    private void setReportDate(ContractsGrantsLOCDrawDetailsReport cgLOCDrawDetailsReportEntry, ContractsGrantsLOCDrawDetailsReportDetailDataHolder reportDetail) {

        reportDetail.setDocumentNumber(cgLOCDrawDetailsReportEntry.getDocumentNumber());
        reportDetail.setLetterOfCreditFundCode(cgLOCDrawDetailsReportEntry.getLetterOfCreditFundCode());
        reportDetail.setLetterOfCreditFundGroupCode(cgLOCDrawDetailsReportEntry.getLetterOfCreditFundGroupCode());
        reportDetail.setLetterOfCreditReviewCreateDate(cgLOCDrawDetailsReportEntry.getLetterOfCreditReviewCreateDate());
        BigDecimal amountAvailableToDraw = (ObjectUtils.isNull(cgLOCDrawDetailsReportEntry.getAmountAvailableToDraw())) ? BigDecimal.ZERO : cgLOCDrawDetailsReportEntry.getAmountAvailableToDraw().bigDecimalValue();
        reportDetail.setAmountAvailableToDraw(amountAvailableToDraw);
        BigDecimal claimOnCashBalance = (ObjectUtils.isNull(cgLOCDrawDetailsReportEntry.getClaimOnCashBalance())) ? BigDecimal.ZERO : cgLOCDrawDetailsReportEntry.getClaimOnCashBalance().bigDecimalValue();
        reportDetail.setClaimOnCashBalance(claimOnCashBalance);
        BigDecimal amountToDraw = (ObjectUtils.isNull(cgLOCDrawDetailsReportEntry.getAmountToDraw())) ? BigDecimal.ZERO : cgLOCDrawDetailsReportEntry.getAmountToDraw().bigDecimalValue();
        reportDetail.setAmountToDraw(amountToDraw);
        BigDecimal fundsNotDrawn = (ObjectUtils.isNull(cgLOCDrawDetailsReportEntry.getFundsNotDrawn())) ? BigDecimal.ZERO : cgLOCDrawDetailsReportEntry.getFundsNotDrawn().bigDecimalValue();
        reportDetail.setFundsNotDrawn(fundsNotDrawn);
    }
}
