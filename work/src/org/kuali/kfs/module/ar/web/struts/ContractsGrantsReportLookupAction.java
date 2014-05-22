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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportSearchCriteriaDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.sys.DynamicCollectionComparator;
import org.kuali.kfs.sys.DynamicCollectionComparator.SortOrder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.web.struts.action.KualiLookupAction;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This Action Class defines all the core methods for Contracts and Grants Lookup.
 */
public abstract class ContractsGrantsReportLookupAction extends KualiLookupAction {

    protected static final String SORT_INDEX_SESSION_KEY = "sortIndex";
    protected static final String NUM_SORT_INDEX_CLICK_SESSION_KEY = "numberOfSortClicked";
    private static volatile ContractsGrantsReportHelperService contractsGrantsReportHelperService;

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
            Integer clickedSession = ObjectUtils.isNull(GlobalVariables.getUserSession().retrieveObject(NUM_SORT_INDEX_CLICK_SESSION_KEY)) ? new Integer(1) : (Integer) GlobalVariables.getUserSession().retrieveObject(NUM_SORT_INDEX_CLICK_SESSION_KEY);
            if (ObjectUtils.isNotNull(GlobalVariables.getUserSession().retrieveObject(SORT_INDEX_SESSION_KEY)) && GlobalVariables.getUserSession().retrieveObject(SORT_INDEX_SESSION_KEY).toString().equals(sortIndexParameter)) {
                GlobalVariables.getUserSession().addObject(NUM_SORT_INDEX_CLICK_SESSION_KEY, new Integer(clickedSession + 1));
            }
            GlobalVariables.getUserSession().addObject(SORT_INDEX_SESSION_KEY, sortIndexParameter);
        }
        return super.execute(mapping, form, request, response);
    }

    /**
     * @param searchCriteria
     * @param fieldsForLookup
     */
    protected void buildReportForSearchCriteria(List<ContractsGrantsReportSearchCriteriaDataHolder> searchCriteria, Map fieldsForLookup, Class<? extends BusinessObject> detailClass) {
        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        for (Object field : fieldsForLookup.keySet()) {

            String fieldString = (ObjectUtils.isNull(field)) ? "" : field.toString();
            String valueString = (ObjectUtils.isNull(fieldsForLookup.get(field))) ? "" : fieldsForLookup.get(field).toString();

            if (!fieldString.equals("") && !valueString.equals("") && !ArConstants.ReportsConstants.reportSearchCriteriaExceptionList.contains(fieldString)) {
                ContractsGrantsReportSearchCriteriaDataHolder criteriaData = new ContractsGrantsReportSearchCriteriaDataHolder();
                String label = dataDictionaryService.getAttributeLabel(detailClass, fieldString);
                criteriaData.setSearchFieldLabel(label);
                criteriaData.setSearchFieldValue(valueString);
                searchCriteria.add(criteriaData);
            }
        }
    }

    /**
     * @param displayList
     * @param sortPropertyName
     */
    protected void sortReport(List displayList, String sortPropertyName) {
        Integer numSortIndexClick = (ObjectUtils.isNull(GlobalVariables.getUserSession().retrieveObject(NUM_SORT_INDEX_CLICK_SESSION_KEY))) ? 1 : new Integer(GlobalVariables.getUserSession().retrieveObject(NUM_SORT_INDEX_CLICK_SESSION_KEY).toString());
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
    protected <B extends BusinessObject> String sortReportValues(List<B> displayList, String sortFieldName) {
        Object sortIndexObject = GlobalVariables.getUserSession().retrieveObject(SORT_INDEX_SESSION_KEY);

        // set default sort index as 0 (Proposal Number)
        if (ObjectUtils.isNull(sortIndexObject)) {
            sortIndexObject = "0";
        }

        // get sort property
        String sortPropertyName = getContractsGrantsReportHelperService().getFieldNameForSorting(Integer.parseInt(sortIndexObject.toString()), sortFieldName);

        // sort list
        sortReport(displayList, sortPropertyName);

        return sortPropertyName;
    }

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
}
