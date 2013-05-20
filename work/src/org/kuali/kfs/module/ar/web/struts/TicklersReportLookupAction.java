/*
 * Copyright 2012 The Kuali Foundation.
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
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.TicklersReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportSearchCriteriaDataHolder;
import org.kuali.kfs.module.ar.report.TicklersReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.service.TicklersReportService;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.ui.ResultRow;

/**
 * LookupAction file for Ticklers Report.
 */
public class TicklersReportLookupAction extends ContractsGrantsReportLookupAction {

    /**
     * implements the print service for Ticklers Lookup.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        TicklersReportLookupForm ticklersReportLookupForm = (TicklersReportLookupForm) form;

        String methodToCall = findMethodToCall(form, request);
        if (methodToCall.equalsIgnoreCase("search")) {
            GlobalVariables.getUserSession().removeObjectsByPrefix(KRADConstants.SEARCH_METHOD);
        }

        Lookupable kualiLookupable = ticklersReportLookupForm.getLookupable();
        if (ObjectUtils.isNull(kualiLookupable)) {
            throw new RuntimeException("Lookupable is null.");
        }

        List<TicklersReport> displayList = new ArrayList<TicklersReport>();
        List<ResultRow> resultTable = new ArrayList<ResultRow>();

        // validate search parameters
        kualiLookupable.validateSearchParameters(ticklersReportLookupForm.getFields());

        // this is for 200 limit. turn it off for report.
        boolean bounded = false;

        displayList = (List<TicklersReport>) kualiLookupable.performLookup(ticklersReportLookupForm, resultTable, bounded);

        Object sortIndexObject = GlobalVariables.getUserSession().retrieveObject(SORT_INDEX_SESSION_KEY);

        if (ObjectUtils.isNull(sortIndexObject) || sortIndexObject.toString() == "0") {
            sortIndexObject = "0";
        }
        // get sort property
        String sortPropertyName = getFieldNameForSorting(Integer.parseInt(sortIndexObject.toString()), "TicklersReport");

        // sort list
        sortReport(displayList, sortPropertyName);

        // build report
        ContractsGrantsReportDataHolder arTicklersReportDataHolder = new ContractsGrantsReportDataHolder();
        List<TicklersReportDetailDataHolder> ticklersReportDetails = arTicklersReportDataHolder.getDetails();
        for (TicklersReport tr : displayList) {
            TicklersReportDetailDataHolder trDataHolder = new TicklersReportDetailDataHolder(tr);
            ticklersReportDetails.add(trDataHolder);
        }
        this.buildReportForSearchCriteia(arTicklersReportDataHolder.getSearchCriteria(), ticklersReportLookupForm.getFieldsForLookup());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String reportFileName = SpringContext.getBean(TicklersReportService.class).generateReport(arTicklersReportDataHolder, baos);

        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, reportFileName + ReportGeneration.PDF_FILE_EXTENSION);
        return null;
    }

    /**
     * This method Prepares the list of search criteria given in lookupscreen for displaying it PDF file.
     * 
     * @param searchCriteria
     * @param fieldsForLookup
     */
    protected void buildReportForSearchCriteia(List<ContractsGrantsReportSearchCriteriaDataHolder> searchCriteria, Map fieldsForLookup) {
        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        for (Object field : fieldsForLookup.keySet()) {

            String fieldString = (ObjectUtils.isNull(field)) ? "" : field.toString();
            String valueString = (ObjectUtils.isNull(fieldsForLookup.get(field))) ? "" : fieldsForLookup.get(field).toString();

            if (!fieldString.equals("") && !fieldString.equals(ArPropertyConstants.TicklersReportFields.COLLECTOR) && !valueString.equals("") && !ArConstants.ReportsConstants.reportSearchCriteriaExceptionList.contains(fieldString)) {
                ContractsGrantsReportSearchCriteriaDataHolder criteriaData = new ContractsGrantsReportSearchCriteriaDataHolder();
                String label = dataDictionaryService.getAttributeLabel(TicklersReport.class, fieldString);
                criteriaData.setSearchFieldLabel(label);
                criteriaData.setSearchFieldValue(valueString);
                searchCriteria.add(criteriaData);
            }
        }
    }

}
