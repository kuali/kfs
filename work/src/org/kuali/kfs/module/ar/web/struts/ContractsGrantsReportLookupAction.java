/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.web.struts;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsSuspendedInvoiceSummaryReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportSearchCriteriaDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.sys.DynamicCollectionComparator;
import org.kuali.kfs.sys.DynamicCollectionComparator.SortOrder;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.action.KualiLookupAction;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.control.ControlDefinition;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This Action Class defines all the core methods for Contracts & Grants Lookup.
 */
public abstract class ContractsGrantsReportLookupAction extends KualiLookupAction {

    private static volatile ContractsGrantsReportHelperService contractsGrantsReportHelperService;
    private static volatile DataDictionaryService dataDictionaryService;

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiLookupAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // d-16544-s is field name from display table tab.
        String sortIndexParameter = request.getParameter("d-16544-s");
        if (sortIndexParameter != null) {
            // to store how many times user clicks sort links
            Integer clickedSession = ObjectUtils.isNull(GlobalVariables.getUserSession().retrieveObject(ArConstants.NUM_SORT_INDEX_CLICK_SESSION_KEY)) ? new Integer(1) : (Integer) GlobalVariables.getUserSession().retrieveObject(ArConstants.NUM_SORT_INDEX_CLICK_SESSION_KEY);
            if (ObjectUtils.isNotNull(GlobalVariables.getUserSession().retrieveObject(ArConstants.SORT_INDEX_SESSION_KEY)) && GlobalVariables.getUserSession().retrieveObject(ArConstants.SORT_INDEX_SESSION_KEY).toString().equals(sortIndexParameter)) {
                GlobalVariables.getUserSession().addObject(ArConstants.NUM_SORT_INDEX_CLICK_SESSION_KEY, new Integer(clickedSession + 1));
            }
            GlobalVariables.getUserSession().addObject(ArConstants.SORT_INDEX_SESSION_KEY, sortIndexParameter);
        }
        if (form instanceof ContractsGrantsReportLookupForm) {
            ((ContractsGrantsReportLookupForm)form).setDisplayActionsForRow(shouldDisplayActionsForRow());
        }
        return super.execute(mapping, form, request, response);
    }

    /**
     * This method implements the print functionality - basically, pdf generation - for children reports
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LookupForm lookupForm = (LookupForm) form;

        List<ContractsGrantsSuspendedInvoiceSummaryReport> displayList = lookupReportValues(lookupForm, request, validateLookupFields());
        final String sortPropertyName = sortReportValues(displayList);

        // build report
        ContractsGrantsReportDataBuilderService reportDataBuilderService = getContractsGrantsReportDataBuilderService();
        ContractsGrantsReportDataHolder cgSuspendedInvoiceSummaryReportDataHolder = reportDataBuilderService.buildReportDataHolder(displayList, sortPropertyName);

        // build search criteria for report
        buildSearchCriteriaReportSection(cgSuspendedInvoiceSummaryReportDataHolder.getSearchCriteria(), lookupForm.getFieldsForLookup());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String reportFileName = generateReportPdf(cgSuspendedInvoiceSummaryReportDataHolder, baos);
        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, reportFileName + ReportGeneration.PDF_FILE_EXTENSION);
        return null;
    }

    /**
     * Determines whether or not to validate the lookup fields before carrying out the looup
     * @return true to validate the lookup fields, false otherwise
     */
    protected boolean validateLookupFields() {
        return true;
    }

    /**
     * When the report prints out, it lists the fields that were used for the search criteria query; this builds that
     * @param searchCriteria the reporty versions of the fields
     * @param fieldsForLookup the fields from the lookup itself
     */
    protected void buildSearchCriteriaReportSection(List<ContractsGrantsReportSearchCriteriaDataHolder> searchCriteria, Map fieldsForLookup) {
        for (Object field : fieldsForLookup.keySet()) {
            String fieldString = (ObjectUtils.isNull(field)) ? "" : field.toString();
            String valueString = (ObjectUtils.isNull(fieldsForLookup.get(field))) ? "" : fieldsForLookup.get(field).toString();

            if (StringUtils.isNotBlank(fieldString) && StringUtils.isNotBlank(valueString) &&
                    !ArConstants.ReportsConstants.reportSearchCriteriaExceptionList.contains(fieldString) &&
                    !fieldString.startsWith(ArPropertyConstants.RANGE_LOWER_BOUND_KEY_PREFIX)) {
                final ControlDefinition controldef = getDataDictionaryService().getAttributeControlDefinition(getPrintSearchCriteriaClass(), fieldString);
                if (controldef != null && !controldef.isHidden()) {
                    ContractsGrantsReportSearchCriteriaDataHolder criteriaData = generateDataHolder(fieldString, valueString);
                    searchCriteria.add(criteriaData);
                }
            }
        }
    }

    /**
     * Generates a single field for the buildReportForSearchCriteria
     * @param fieldString the name of the field
     * @param valueString the value from the lookup
     * @return the generated ContractsGrantsReportSearchCriteriaDataHolder
     */
    protected ContractsGrantsReportSearchCriteriaDataHolder generateDataHolder(String fieldString, String valueString) {
        ContractsGrantsReportSearchCriteriaDataHolder criteriaData = new ContractsGrantsReportSearchCriteriaDataHolder();

        String label = getDataDictionaryService().getAttributeLabel(getPrintSearchCriteriaClass(), fieldString);
        criteriaData.setSearchFieldLabel(label);
        criteriaData.setSearchFieldValue(valueString);
        return criteriaData;
    }

    /**
     * @param displayList
     * @param sortPropertyName
     */
    protected void sortReport(List displayList, String sortPropertyName) {
        Integer numSortIndexClick = (ObjectUtils.isNull(GlobalVariables.getUserSession().retrieveObject(ArConstants.NUM_SORT_INDEX_CLICK_SESSION_KEY))) ? 1 : new Integer(GlobalVariables.getUserSession().retrieveObject(ArConstants.NUM_SORT_INDEX_CLICK_SESSION_KEY).toString());
        if (((numSortIndexClick) % 2) == 0) {
            DynamicCollectionComparator.sort(displayList, SortOrder.DESC, sortPropertyName);
        }
        else {
            DynamicCollectionComparator.sort(displayList, SortOrder.ASC, sortPropertyName);
        }
    }

    /**
     * Looks up the values for the report
     * @param form the LookupForm to help us with the lookup
     * @param request the request to get the search method from
     * @param performValidate if true, will perform validation on the values before performing the lookup
     * @return a List of the report values for the given action
     * @throws Exception thrown if findMethodToCall gets ticked
     */
    protected <B extends BusinessObject> List<B> lookupReportValues(LookupForm form, HttpServletRequest request, boolean performValidate) throws Exception {
        String methodToCall = findMethodToCall(form, request);
        if (methodToCall.equalsIgnoreCase(KRADConstants.SEARCH_METHOD)) {
            GlobalVariables.getUserSession().removeObjectsByPrefix(KRADConstants.SEARCH_METHOD);
        }

        Lookupable kualiLookupable = form.getLookupable();
        if (ObjectUtils.isNull(kualiLookupable)) {
            throw new RuntimeException("Lookupable is null.");
        }

        if (performValidate) {
            kualiLookupable.validateSearchParameters(form.getFields());
        }

        List<B> displayList = new ArrayList<B>();
        List<ResultRow> resultTable = new ArrayList<ResultRow>();

        // this is for 200 limit. turn it off for report.
        boolean bounded = false;

        displayList = (List<B>) kualiLookupable.performLookup(form, resultTable, bounded);
        return displayList;
    }

    /**
     * Sorts the values for a report
     * @param displayList the List of report values to sort (in List)
     * @param sortFieldName the field name that the List should be sorted by
     * @return the name of the property to be sorted against
     */
    protected <B extends BusinessObject> String sortReportValues(List<B> displayList) {
        Object sortIndexObject = GlobalVariables.getUserSession().retrieveObject(ArConstants.SORT_INDEX_SESSION_KEY);

        // set default sort index as 0 (Proposal Number)
        if (ObjectUtils.isNull(sortIndexObject)) {
            sortIndexObject = "0";
        }

        // get sort property
        String sortPropertyName = getContractsGrantsReportHelperService().getFieldNameForSorting(Integer.parseInt(sortIndexObject.toString()), getSortFieldName());

        // sort list
        sortReport(displayList, sortPropertyName);

        return sortPropertyName;
    }

    /**
     * @return the default value that sorts on the pdf generation should sort on
     */
    protected abstract String getSortFieldName();

    /**
     * Generates the report PDF
     * @param reportDataHolder the information to report on
     * @param reportInfo information about where to store the report and formatting
     * @param baos the stream to write the PDF to
     * @return the file name of the generated report
     */
    protected String generateReportPdf(ContractsGrantsReportDataHolder reportDataHolder, ByteArrayOutputStream baos) {
        final String reportFileName = getContractsGrantsReportHelperService().generateReport(reportDataHolder, getContractsGrantsReportDataBuilderService().getReportInfo(), baos);
        return reportFileName;
    }

    /**
     * @return the name of the bean which helps the child Action build the reports associated
     */
    public abstract String getReportBuilderServiceBeanName();

    /**
     * @return the class used during pdf generation to build search criteria against
     */
    public abstract Class<? extends BusinessObject> getPrintSearchCriteriaClass();

    /**
     * Always returns false, as most reports do not have actions associated
     * @return true if the form should display actions per row, false otherwise
     */
    public boolean shouldDisplayActionsForRow() {
        return false;
    }

    /**
     * Generates the report title for generated reports.  If null, a report title will not be set
     * @param lookupForm a form with information which may be used in the title
     * @return String the title for the report
     */
    public abstract String generateReportTitle(LookupForm lookupForm);

    public ContractsGrantsReportHelperService getContractsGrantsReportHelperService() {
        if (contractsGrantsReportHelperService == null) {
            contractsGrantsReportHelperService = SpringContext.getBean(ContractsGrantsReportHelperService.class);
        }
        return contractsGrantsReportHelperService;
    }

    /**
     * Returns the ContractsGrantsReportDataBuilderService which builds reports out of the given detailClass
     * @param detailClass the detailClass to find a builder service for
     * @return the ContractsGrantsReportDataBuilderService
     */
    public ContractsGrantsReportDataBuilderService getContractsGrantsReportDataBuilderService() {
        return SpringContext.getBean(ContractsGrantsReportDataBuilderService.class, getReportBuilderServiceBeanName());
    }

    public DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        }
        return dataDictionaryService;
    }
}
