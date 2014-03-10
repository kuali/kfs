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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.CollectionActivityReport;
import org.kuali.kfs.module.ar.report.CollectionActivityReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportSearchCriteriaDataHolder;
import org.kuali.kfs.module.ar.report.service.CollectionActivityReportService;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.datadictionary.control.HiddenControlDefinition;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.datadictionary.control.ControlDefinition;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.ui.ResultRow;


/**
 * This class handles Actions for Collection activity report.
 */

public class CollectionActivityReportAction extends ContractsGrantsReportLookupAction {

    private static final String TOTALS_TABLE_KEY = "totalsTable";

    /**
     * Default Constructor.
     */
    public CollectionActivityReportAction() {
        super();
    }

    /**
     * This method implements the print functionality for the Collection Activity Report.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CollectionActivityReportForm collActReportLookupForm = (CollectionActivityReportForm) form;

        String methodToCall = findMethodToCall(form, request);
        if (methodToCall.equalsIgnoreCase(KRADConstants.SEARCH_METHOD)) {
            GlobalVariables.getUserSession().removeObjectsByPrefix(KRADConstants.SEARCH_METHOD);
        }

        Lookupable kualiLookupable = collActReportLookupForm.getLookupable();
        if (ObjectUtils.isNull(kualiLookupable)) {
            throw new RuntimeException("Lookupable is null.");
        }

        List<CollectionActivityReport> displayList = new ArrayList<CollectionActivityReport>();
        List<ResultRow> resultTable = new ArrayList<ResultRow>();

        // this is for 200 limit. turn it off for report.
        boolean bounded = false;

        displayList = (List<CollectionActivityReport>) kualiLookupable.performLookup(collActReportLookupForm, resultTable, bounded);

        Object sortIndexObject = GlobalVariables.getUserSession().retrieveObject(SORT_INDEX_SESSION_KEY);

        // set default sort index as 0 (Proposal Number)
        if (ObjectUtils.isNull(sortIndexObject)) {
            sortIndexObject = "0";
        }

        // get sort property
        String sortPropertyName = getFieldNameForSorting(Integer.parseInt(sortIndexObject.toString()), "CollectionActivityReport");

        // sort list
        sortReport(displayList, sortPropertyName);

        // build report
        ContractsGrantsReportDataHolder cgInvoiceReportDataHolder = new ContractsGrantsReportDataHolder();
        List<CollectionActivityReportDetailDataHolder> details = cgInvoiceReportDataHolder.getDetails();

        for (CollectionActivityReport collActReportEntry : displayList) {
            CollectionActivityReportDetailDataHolder reportDetail = new CollectionActivityReportDetailDataHolder();
            // set report data
            reportDetail = new CollectionActivityReportDetailDataHolder(collActReportEntry);
            details.add(reportDetail);
        }

        cgInvoiceReportDataHolder.setDetails(details);

        // set report name using invoiceReportOption
        cgInvoiceReportDataHolder.setReportTitle("Collection Activity Report");

        // build search criteria for report
        buildReportForSearchCriteia(cgInvoiceReportDataHolder.getSearchCriteria(), collActReportLookupForm.getFieldsForLookup(), CollectionActivityReportAction.class);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String reportFileName = SpringContext.getBean(CollectionActivityReportService.class).generateReport(cgInvoiceReportDataHolder, baos);
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
                ControlDefinition controldef = dataDictionaryService.getAttributeControlDefinition(CollectionActivityReport.class, fieldString);
                if (!(controldef instanceof HiddenControlDefinition)) {
                    ContractsGrantsReportSearchCriteriaDataHolder criteriaData = new ContractsGrantsReportSearchCriteriaDataHolder();
                    String label = dataDictionaryService.getAttributeLabel(CollectionActivityReport.class, fieldString);
                    criteriaData.setSearchFieldLabel(label);
                    criteriaData.setSearchFieldValue(valueString);
                    searchCriteria.add(criteriaData);
                }
            }
        }
    }

}
