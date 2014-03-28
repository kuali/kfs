/*
 * Copyright 2006-2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
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
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectionActivityReport;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportSearchCriteriaDataHolder;
import org.kuali.kfs.module.ar.report.ReferralToCollectionsReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.service.ReferralToCollectionsReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.datadictionary.control.HiddenControlDefinition;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.datadictionary.control.ControlDefinition;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;


/**
 * This class handles Actions for lookup flow for Referral To Collections Report
 */

public class ReferralToCollectionsReportAction extends ContractsGrantsReportLookupAction {

    private static final String TOTALS_TABLE_KEY = "totalsTable";

    /**
     * Default Constructor.
     */
    public ReferralToCollectionsReportAction() {
        super();
    }

    /**
     * This method implements the print pdf report functionality for the Referral To Collections Report.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ReferralToCollectionsReportForm refToCollReportLookupForm = (ReferralToCollectionsReportForm) form;

        String methodToCall = findMethodToCall(form, request);
        if (methodToCall.equalsIgnoreCase(KRADConstants.SEARCH_METHOD)) {
            GlobalVariables.getUserSession().removeObjectsByPrefix(KRADConstants.SEARCH_METHOD);
        }

        Lookupable kualiLookupable = refToCollReportLookupForm.getLookupable();
        if (ObjectUtils.isNull(kualiLookupable)) {
            throw new RuntimeException("Lookupable is null.");
        }

        List<ReferralToCollectionsReport> displayList = new ArrayList<ReferralToCollectionsReport>();
        List<ResultRow> resultTable = new ArrayList<ResultRow>();

        // validate search parameters
        kualiLookupable.validateSearchParameters(refToCollReportLookupForm.getFields());

        // this is for 200 limit. turn it off for report.
        boolean bounded = false;

        displayList = (List<ReferralToCollectionsReport>) kualiLookupable.performLookup(refToCollReportLookupForm, resultTable, bounded);

        Object sortIndexObject = GlobalVariables.getUserSession().retrieveObject(SORT_INDEX_SESSION_KEY);
        // set default sort index as 0 (Proposal Number)
        if (ObjectUtils.isNull(sortIndexObject)) {
            sortIndexObject = "0";
        }

        // get sort property
        String sortPropertyName = ArPropertyConstants.ReferralToCollectionsReportFields.PDF_SORT_PROPERTY;

        // sort list
        sortReport(displayList, ArPropertyConstants.ReferralToCollectionsReportFields.LIST_SORT_PROPERTY);

        // check field is valid for subtotal
        boolean isFieldSubtotalRequired = true;
        Map<String, List<BigDecimal>> subTotalMap = new HashMap<String, List<BigDecimal>>();

        if (isFieldSubtotalRequired) {
            subTotalMap = buildSubTotalMap(displayList, sortPropertyName);
        }

        BigDecimal invoiceTotal = BigDecimal.ZERO;
        BigDecimal openTotal = BigDecimal.ZERO;

        // build report
        ContractsGrantsReportDataHolder cgInvoiceReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ReferralToCollectionsReportDetailDataHolder> details = cgInvoiceReportDataHolder.getDetails();

        for (ReferralToCollectionsReport refToCollections : displayList) {
            ReferralToCollectionsReportDetailDataHolder reportDetail = new ReferralToCollectionsReportDetailDataHolder();
            // set report data
            reportDetail = new ReferralToCollectionsReportDetailDataHolder(refToCollections);

            if (isFieldSubtotalRequired) {
                // set sortedFieldValue for grouping in the report
                reportDetail.setSortedFieldValue(getPropertyValue(refToCollections, sortPropertyName));
                reportDetail.setDisplaySubtotalInd(true);
                // set subTotal from subTotalMap
                reportDetail.setInvoiceSubTotal(subTotalMap.get(getPropertyValue(refToCollections, sortPropertyName)).get(0));
                reportDetail.setOpenSubTotal(subTotalMap.get(getPropertyValue(refToCollections, sortPropertyName)).get(1));

            }
            else {
                // set this to empty string for not displaying subtotal
                reportDetail.setDisplaySubtotalInd(false);
            }

            invoiceTotal = invoiceTotal.add(reportDetail.getInvoiceAmount());
            openTotal = openTotal.add(reportDetail.getOpenAmount());

            details.add(reportDetail);
        }

        cgInvoiceReportDataHolder.setDetails(details);

        // Avoid generating pdf if there were no search results were returned
        if (CollectionUtils.isEmpty(cgInvoiceReportDataHolder.getDetails())){
            // this is so the message about no search results shows up in the same place as when doing a search
            request.setAttribute(KFSPropertyConstants.REQUEST_SEARCH_RESULTS_ACTUAL_SIZE, 0 );
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // set report name using invoiceReportOption
        cgInvoiceReportDataHolder.setReportTitle("Referral to Collections Report");

        // build search criteria for report
        buildReportForSearchCriteia(cgInvoiceReportDataHolder.getSearchCriteria(), refToCollReportLookupForm.getFieldsForLookup(), CollectionActivityReport.class);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String reportFileName = SpringContext.getBean(ReferralToCollectionsReportService.class).generateReport(cgInvoiceReportDataHolder, baos);
        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, reportFileName + ReportGeneration.PDF_FILE_EXTENSION);
        return null;
    }


    /**
     * This method is used to build pdf report search criteria for Collection activity report
     *
     * @param searchCriteria
     * @param fieldsForLookup
     */
    @Override
    protected void buildReportForSearchCriteia(List<ContractsGrantsReportSearchCriteriaDataHolder> searchCriteria, Map fieldsForLookup, Class dataObjectClass) {
        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        for (Object field : fieldsForLookup.keySet()) {
            String fieldString = (ObjectUtils.isNull(field)) ? "" : field.toString();
            String valueString = (ObjectUtils.isNull(fieldsForLookup.get(field))) ? "" : fieldsForLookup.get(field).toString();
            if (!fieldString.equals("") && !valueString.equals("") && !ArConstants.ReportsConstants.reportSearchCriteriaExceptionList.contains(fieldString)) {
                ControlDefinition controldef = dataDictionaryService.getAttributeControlDefinition(dataObjectClass, fieldString);
                if (!(controldef instanceof HiddenControlDefinition)) {
                    ContractsGrantsReportSearchCriteriaDataHolder criteriaData = new ContractsGrantsReportSearchCriteriaDataHolder();
                    String label = dataDictionaryService.getAttributeLabel(dataObjectClass, fieldString);
                    criteriaData.setSearchFieldLabel(label);
                    criteriaData.setSearchFieldValue(valueString);
                    searchCriteria.add(criteriaData);
                }
            }
        }
    }

    /**
     * This method is used to build map for total according to sort property
     *
     * @param displayList
     * @param sortPropertyName
     * @return
     */
    private Map<String, List<BigDecimal>> buildSubTotalMap(List<ReferralToCollectionsReport> displayList, String sortPropertyName) {
        Map<String, List<BigDecimal>> returnSubTotalMap = new HashMap<String, List<BigDecimal>>();
        // get list of sort fields
        List<String> valuesOfsortProperty = getListOfValuesSortedProperties(displayList, sortPropertyName);

        // calculate sub_total and build subTotalMap
        for (String value : valuesOfsortProperty) {
            BigDecimal invoiceSubTotal = BigDecimal.ZERO;
            BigDecimal openSubTotal = BigDecimal.ZERO;

            for (ReferralToCollectionsReport refToCollReport : displayList) {
                if (value.equals(getPropertyValue(refToCollReport, sortPropertyName))) {
                    BigDecimal totalAmount = refToCollReport.getInvoiceAmount();
                    BigDecimal openAmount = refToCollReport.getOpenAmount();
                    invoiceSubTotal = invoiceSubTotal.add(totalAmount);
                    openSubTotal = openSubTotal.add(openAmount);
                }
            }
            List<BigDecimal> allSubTotal = new ArrayList<BigDecimal>();
            allSubTotal.add(0, invoiceSubTotal);
            allSubTotal.add(1, openSubTotal);

            returnSubTotalMap.put(value, allSubTotal);
        }
        return returnSubTotalMap;
    }

}
