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
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLOCAmountsNotDrawnReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsLOCAmountsNotDrawnReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsLOCAmountsNotDrawnReportService;
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
 * Action Class for the Contracts Grants LOC Amounts Not Drawn Report Lookup.
 */
public class ContractsGrantsLOCAmountsNotDrawnReportLookupAction extends ContractsGrantsReportLookupAction {

    /**
     * This method implements the print functionality for the Contracts Grants LOC Amounts Not Drawn Report Lookup.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsLOCAmountsNotDrawnReportLookupForm cgLOCAmountsNotDrawnReportLookupForm = (ContractsGrantsLOCAmountsNotDrawnReportLookupForm) form;

        String methodToCall = findMethodToCall(form, request);
        if (methodToCall.equalsIgnoreCase("search")) {
            GlobalVariables.getUserSession().removeObjectsByPrefix(KRADConstants.SEARCH_METHOD);
        }

        Lookupable kualiLookupable = cgLOCAmountsNotDrawnReportLookupForm.getLookupable();
        if (kualiLookupable == null) {
            throw new RuntimeException("Lookupable is null.");
        }

        List<ContractsGrantsLOCAmountsNotDrawnReport> displayList = new ArrayList<ContractsGrantsLOCAmountsNotDrawnReport>();
        List<ResultRow> resultTable = new ArrayList<ResultRow>();

        // validate search parameters
        kualiLookupable.validateSearchParameters(cgLOCAmountsNotDrawnReportLookupForm.getFields());

        // this is for 200 limit. turn it off for report.
        boolean bounded = false;

        displayList = (List<ContractsGrantsLOCAmountsNotDrawnReport>) kualiLookupable.performLookup(cgLOCAmountsNotDrawnReportLookupForm, resultTable, bounded);

        Object sortIndexObject = GlobalVariables.getUserSession().retrieveObject(SORT_INDEX_SESSION_KEY);
        // set default sort index as 0 (Proposal Number)
        if (ObjectUtils.isNull(sortIndexObject)) {
            sortIndexObject = "0";
        }
        // get sort property
        String sortPropertyName = getFieldNameForSorting(Integer.parseInt(sortIndexObject.toString()), "ContractsGrantsLOCAmountsNotDrawnReport");

        // sort list
        sortReport(displayList, sortPropertyName);

        // check field is valid for subtotal
        boolean isFieldSubtotalRequired = ArConstants.ReportsConstants.cgLOCAmountsNotDrawnReportSubtotalFieldsList.contains(sortPropertyName);
        Map<String, KualiDecimal> subTotalMap = new HashMap<String, KualiDecimal>();

        if (isFieldSubtotalRequired) {
            subTotalMap = buildSubTotalMap(displayList, sortPropertyName);
        }

        // build report
        ContractsGrantsReportDataHolder cgLOCAmountsNotDrawnReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ContractsGrantsLOCAmountsNotDrawnReportDetailDataHolder> details = cgLOCAmountsNotDrawnReportDataHolder.getDetails();

        for (ContractsGrantsLOCAmountsNotDrawnReport cgLOCAmountsNotDrawnReportEntry : displayList) {
            ContractsGrantsLOCAmountsNotDrawnReportDetailDataHolder reportDetail = new ContractsGrantsLOCAmountsNotDrawnReportDetailDataHolder();
            // set report data
            setReportDate(cgLOCAmountsNotDrawnReportEntry, reportDetail);

            if (isFieldSubtotalRequired) {
                // set sortedFieldValue for grouping in the report
                reportDetail.setSortedFieldValue(getPropertyValue(cgLOCAmountsNotDrawnReportEntry, sortPropertyName));
                reportDetail.setDisplaySubtotalInd(true);
                // set subTotal from subTotalMap
                reportDetail.setSubTotal(subTotalMap.get(getPropertyValue(cgLOCAmountsNotDrawnReportEntry, sortPropertyName)).bigDecimalValue());
            }
            else {
                // set this to empty string for not displaying subtotal
                reportDetail.setDisplaySubtotalInd(false);
            }
            details.add(reportDetail);
        }
        cgLOCAmountsNotDrawnReportDataHolder.setDetails(details);

        // build search criteria for report
        buildReportForSearchCriteia(cgLOCAmountsNotDrawnReportDataHolder.getSearchCriteria(), cgLOCAmountsNotDrawnReportLookupForm.getFieldsForLookup(), ContractsGrantsLOCAmountsNotDrawnReport.class);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String reportFileName = SpringContext.getBean(ContractsGrantsLOCAmountsNotDrawnReportService.class).generateReport(cgLOCAmountsNotDrawnReportDataHolder, baos);
        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, reportFileName + ReportGeneration.PDF_FILE_EXTENSION);
        return null;
    }

    /**
     * @param displayList
     * @param sortPropertyName
     * @return
     */
    private Map<String, KualiDecimal> buildSubTotalMap(List<ContractsGrantsLOCAmountsNotDrawnReport> displayList, String sortPropertyName) {
        Map<String, KualiDecimal> returnSubTotalMap = new HashMap<String, KualiDecimal>();
        // get list of sort fields
        List<String> valuesOfsortProperty = getListOfValuesSortedProperties(displayList, sortPropertyName);

        // calculate sub_total and build subTotalMap
        for (String value : valuesOfsortProperty) {
            KualiDecimal subTotal = KualiDecimal.ZERO;
            for (ContractsGrantsLOCAmountsNotDrawnReport cgLOCAmountsNotDrawnReportEntry : displayList) {
                // set fieldValue as "" when it is null
                if (value.equals(getPropertyValue(cgLOCAmountsNotDrawnReportEntry, sortPropertyName))) {
                    subTotal = subTotal.add(cgLOCAmountsNotDrawnReportEntry.getAmountToDraw());
                }
            }
            returnSubTotalMap.put(value, subTotal);
        }
        return returnSubTotalMap;
    }

    /**
     * @param cgLOCAmountsNotDrawnReportEntry
     * @param reportDetail
     */
    private void setReportDate(ContractsGrantsLOCAmountsNotDrawnReport cgLOCAmountsNotDrawnReportEntry, ContractsGrantsLOCAmountsNotDrawnReportDetailDataHolder reportDetail) {

        reportDetail.setDocumentNumber(cgLOCAmountsNotDrawnReportEntry.getDocumentNumber());
        reportDetail.setLetterOfCreditFundCode(cgLOCAmountsNotDrawnReportEntry.getLetterOfCreditFundCode());
        reportDetail.setLetterOfCreditFundGroupCode(cgLOCAmountsNotDrawnReportEntry.getLetterOfCreditFundGroupCode());
        reportDetail.setLetterOfCreditReviewCreateDate(cgLOCAmountsNotDrawnReportEntry.getLetterOfCreditReviewCreateDate());
        BigDecimal amountAvailableToDraw = (ObjectUtils.isNull(cgLOCAmountsNotDrawnReportEntry.getAmountAvailableToDraw())) ? BigDecimal.ZERO : cgLOCAmountsNotDrawnReportEntry.getAmountAvailableToDraw().bigDecimalValue();
        reportDetail.setAmountAvailableToDraw(amountAvailableToDraw);
        BigDecimal claimOnCashBalance = (ObjectUtils.isNull(cgLOCAmountsNotDrawnReportEntry.getClaimOnCashBalance())) ? BigDecimal.ZERO : cgLOCAmountsNotDrawnReportEntry.getClaimOnCashBalance().bigDecimalValue();
        reportDetail.setClaimOnCashBalance(claimOnCashBalance);
        BigDecimal amountToDraw = (ObjectUtils.isNull(cgLOCAmountsNotDrawnReportEntry.getAmountToDraw())) ? BigDecimal.ZERO : cgLOCAmountsNotDrawnReportEntry.getAmountToDraw().bigDecimalValue();
        reportDetail.setAmountToDraw(amountToDraw);
        BigDecimal fundsNotDrawn = (ObjectUtils.isNull(cgLOCAmountsNotDrawnReportEntry.getFundsNotDrawn())) ? BigDecimal.ZERO : cgLOCAmountsNotDrawnReportEntry.getFundsNotDrawn().bigDecimalValue();
        reportDetail.setFundsNotDrawn(fundsNotDrawn);
    }
}
