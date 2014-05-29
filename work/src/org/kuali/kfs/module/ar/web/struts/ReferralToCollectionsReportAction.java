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
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.datadictionary.control.HiddenControlDefinition;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.control.ControlDefinition;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class handles Actions for lookup flow for Referral To Collections Report
 */
public class ReferralToCollectionsReportAction extends ContractsGrantsReportLookupAction {
    private static final String TOTALS_TABLE_KEY = "totalsTable";
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

        List<ReferralToCollectionsReport> displayList = lookupReportValues(refToCollReportLookupForm, request, true);
        String sortPropertyName = sortReportValues(displayList, ArPropertyConstants.ReferralToCollectionsReportFields.PDF_SORT_PROPERTY);

        ContractsGrantsReportDataHolder cgInvoiceReportDataHolder = getContractsGrantsReportDataBuilderService().buildReportDataHolder(displayList, sortPropertyName);

        // Avoid generating pdf if there were no search results were returned
        if (CollectionUtils.isEmpty(cgInvoiceReportDataHolder.getDetails())){
            // this is so the message about no search results shows up in the same place as when doing a search
            request.setAttribute(KFSPropertyConstants.REQUEST_SEARCH_RESULTS_ACTUAL_SIZE, 0 );
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // set report name using invoiceReportOption
        cgInvoiceReportDataHolder.setReportTitle(generateReportTitle(refToCollReportLookupForm));

        // build search criteria for report
        buildReportForSearchCriteria(cgInvoiceReportDataHolder.getSearchCriteria(), refToCollReportLookupForm.getFieldsForLookup(), CollectionActivityReport.class);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String reportFileName = generateReportPdf(cgInvoiceReportDataHolder, baos);
        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, reportFileName + ReportGeneration.PDF_FILE_EXTENSION);
        return null;
    }

    /**
     * Returns the String "Referral to Collections Report" as a title
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#generateReportTitle(org.kuali.rice.kns.web.struts.form.LookupForm)
     */
    @Override
    public String generateReportTitle(LookupForm lookupForm) {
        return "Referral to Collections Report";
    }

    /**
     * This method is used to build pdf report search criteria for Collection activity report
     *
     * @param searchCriteria
     * @param fieldsForLookup
     */
    @Override
    protected void buildReportForSearchCriteria(List<ContractsGrantsReportSearchCriteriaDataHolder> searchCriteria, Map fieldsForLookup, Class<? extends BusinessObject> dataObjectClass) {
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
     * The sortPropertyName here is hard coded to so we need to do some special logic
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#sortReportValues(java.util.List, java.lang.String)
     */
    @Override
    protected <B extends BusinessObject> String sortReportValues(List<B> displayList, String sortFieldName) {
        Object sortIndexObject = GlobalVariables.getUserSession().retrieveObject(ArConstants.SORT_INDEX_SESSION_KEY);
        // set default sort index as 0 (Proposal Number)
        if (ObjectUtils.isNull(sortIndexObject)) {
            sortIndexObject = "0";
        }

        // get sort property
        String sortPropertyName = ArPropertyConstants.ReferralToCollectionsReportFields.PDF_SORT_PROPERTY;
        // sort list
        sortReport(displayList, ArPropertyConstants.ReferralToCollectionsReportFields.LIST_SORT_PROPERTY);
        return sortPropertyName;
    }

    /**
     * Returns "referralToCollectionsReportBuilderService"
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#getReportBuilderServiceBeanName()
     */
    @Override
    public String getReportBuilderServiceBeanName() {
        return ArConstants.ReportBuilderDataServiceBeanNames.REFERRAL_TO_COLLECTION;
    }
}