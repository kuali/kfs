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
package org.kuali.kfs.module.cg.web.struts;

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
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.ContractsGrantsAwardBalancesReport;
import org.kuali.kfs.module.cg.report.ContractsGrantsAwardBalancesReportDetailDataHolder;
import org.kuali.kfs.module.cg.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.cg.report.ContractsGrantsReportSearchCriteriaDataHolder;
import org.kuali.kfs.module.cg.report.service.ContractsGrantsAwardBalancesReportService;
import org.kuali.kfs.sys.DynamicCollectionComparator;
import org.kuali.kfs.sys.DynamicCollectionComparator.SortOrder;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.action.KualiLookupAction;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Action Class for Contracts Grants Award Balances Report Lookup.
 */
public class ContractsGrantsAwardBalancesReportLookupAction extends KualiLookupAction {


    protected static final String SORT_INDEX_SESSION_KEY = "sortIndex";
    protected static final String NUM_SORT_INDEX_CLICK_SESSION_KEY = "numberOfSortClicked";

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiLookupAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
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
     * @param index
     * @param businessObjectName
     * @return
     */
    protected String getFieldNameForSorting(int index, String businessObjectName) {
        org.kuali.rice.kns.datadictionary.BusinessObjectEntry boe = (org.kuali.rice.kns.datadictionary.BusinessObjectEntry) SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(businessObjectName);
        List<String> lookupResultFields = boe.getLookupDefinition().getResultFieldNames();
        return lookupResultFields.get(index);
    }

    /**
     * @param list
     * @param propertyName
     * @return
     */
    protected List<String> getListOfValuesSortedProperties(List list, String propertyName) {
        List<String> returnList = new ArrayList<String>();
        for (Object object : list) {
            if (!returnList.contains(getPropertyValue(object, propertyName))) {
                returnList.add(getPropertyValue(object, propertyName));
            }
        }
        return returnList;
    }

    /**
     * @param object
     * @param propertyName
     * @return
     */
    protected String getPropertyValue(Object object, String propertyName) {
        Object fieldValue = ObjectUtils.getPropertyValue(object, propertyName);
        return (ObjectUtils.isNull(fieldValue)) ? "" : StringUtils.trimAllWhitespace(fieldValue.toString());
    }

    /**
     * @param searchCriteria
     * @param fieldsForLookup
     */
    protected void buildReportForSearchCriteia(List<ContractsGrantsReportSearchCriteriaDataHolder> searchCriteria, Map fieldsForLookup) {
        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        for (Object field : fieldsForLookup.keySet()) {

            String fieldString = (ObjectUtils.isNull(field)) ? "" : field.toString();
            String valueString = (ObjectUtils.isNull(fieldsForLookup.get(field))) ? "" : fieldsForLookup.get(field).toString();

            if (!fieldString.equals("") && !valueString.equals("") && !CGConstants.ReportsConstants.reportSearchCriteriaExceptionList.contains(fieldString)) {
                ContractsGrantsReportSearchCriteriaDataHolder criteriaData = new ContractsGrantsReportSearchCriteriaDataHolder();
                String label = dataDictionaryService.getAttributeLabel(Award.class, fieldString);
                criteriaData.setSearchFieldLabel(label);
                criteriaData.setSearchFieldValue(valueString);
                searchCriteria.add(criteriaData);
            }
        }
    }

    /**
     * This method sorts the report.
     *
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
     * This service is used to print the report.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsAwardBalancesReportLookupForm awardBalancesReportLookupForm = (ContractsGrantsAwardBalancesReportLookupForm) form;

        String methodToCall = findMethodToCall(form, request);
        if (methodToCall.equalsIgnoreCase("search")) {
            GlobalVariables.getUserSession().removeObjectsByPrefix(KRADConstants.SEARCH_METHOD);
        }

        Lookupable kualiLookupable = awardBalancesReportLookupForm.getLookupable();
        if (kualiLookupable == null) {
            throw new RuntimeException("Lookupable is null.");
        }

        List<ResultRow> resultTable = new ArrayList<ResultRow>();

        // validate search parameters
        kualiLookupable.validateSearchParameters(awardBalancesReportLookupForm.getFields());

        // this is for 200 limit. turn it off for report.
        boolean isUnbounded = true;

        List<ContractsGrantsAwardBalancesReport> displayList = (List<ContractsGrantsAwardBalancesReport>) kualiLookupable.performLookup(awardBalancesReportLookupForm, resultTable, isUnbounded);
        Object sortIndexObject = GlobalVariables.getUserSession().retrieveObject(SORT_INDEX_SESSION_KEY);

        if (ObjectUtils.isNull(sortIndexObject) || sortIndexObject.toString() == "0") {
            sortIndexObject = "0";
        }
        // get sort property
        String sortPropertyName = getFieldNameForSorting(Integer.parseInt(sortIndexObject.toString()), "ContractsGrantsAwardBalancesReport");

        // sort list
        sortReport(displayList, sortPropertyName);

        // check field is valid for subtotal
        boolean isFieldSubtotalRequired = CGConstants.ReportsConstants.awardBalancesReportSubtotalFieldsList.contains(sortPropertyName);
        Map<String, KualiDecimal> subTotalMap = new HashMap<String, KualiDecimal>();

        if (isFieldSubtotalRequired) {
            subTotalMap = buildSubTotalMap(displayList, sortPropertyName);
        }

        // build report
        ContractsGrantsReportDataHolder awardBalancesReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ContractsGrantsAwardBalancesReportDetailDataHolder> details = awardBalancesReportDataHolder.getDetails();

        for (ContractsGrantsAwardBalancesReport awardBalancesReportEntry : displayList) {
            ContractsGrantsAwardBalancesReportDetailDataHolder reportDetail = new ContractsGrantsAwardBalancesReportDetailDataHolder();
            // set report data
            setReportDate(awardBalancesReportEntry, reportDetail);

            if (isFieldSubtotalRequired) {
                // set sortedFieldValue for grouping in the report
                reportDetail.setSortedFieldValue(getPropertyValue(awardBalancesReportEntry, sortPropertyName));
                reportDetail.setDisplaySubtotalInd(true);
                // set subTotal from subTotalMap
                reportDetail.setSubTotal(subTotalMap.get(getPropertyValue(awardBalancesReportEntry, sortPropertyName)).bigDecimalValue());
            }
            else {
                // set this to empty string for not displaying subtotal
                reportDetail.setDisplaySubtotalInd(false);
            }
            details.add(reportDetail);
        }
        awardBalancesReportDataHolder.setDetails(details);

        // build search criteria for report
        buildReportForSearchCriteia(awardBalancesReportDataHolder.getSearchCriteria(), awardBalancesReportLookupForm.getFieldsForLookup());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String reportFileName = SpringContext.getBean(ContractsGrantsAwardBalancesReportService.class).generateReport(awardBalancesReportDataHolder, baos);
        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, reportFileName + ReportGeneration.PDF_FILE_EXTENSION);
        return null;
    }


    /**
     * @param displayList
     * @param sortPropertyName
     * @return
     */
    private Map<String, KualiDecimal> buildSubTotalMap(List<ContractsGrantsAwardBalancesReport> displayList, String sortPropertyName) {
        Map<String, KualiDecimal> returnSubTotalMap = new HashMap<String, KualiDecimal>();
        // get list of sort fields
        List<String> valuesOfsortProperty = getListOfValuesSortedProperties(displayList, sortPropertyName);

        // calculate sub_total and build subTotalMap
        for (String value : valuesOfsortProperty) {
            KualiDecimal subTotal = KualiDecimal.ZERO;
            for (ContractsGrantsAwardBalancesReport awardBalancesReportEntry : displayList) {
                // set fieldValue as "" when it is null
                if (value.equals(getPropertyValue(awardBalancesReportEntry, sortPropertyName))) {
                    subTotal = subTotal.add(awardBalancesReportEntry.getAwardTotalAmount());
                }
            }
            returnSubTotalMap.put(value, subTotal);
        }
        return returnSubTotalMap;
    }


    /**
     * @param awardBalancesReportEntry
     * @param reportDetail
     */
    private void setReportDate(ContractsGrantsAwardBalancesReport awardBalancesReportEntry, ContractsGrantsAwardBalancesReportDetailDataHolder reportDetail) {
        reportDetail.setProposalNumber(awardBalancesReportEntry.getProposalNumber());
        reportDetail.setAwardId(awardBalancesReportEntry.getAwardId());

        String agencyName = (ObjectUtils.isNull(awardBalancesReportEntry.getAgency())) ? null : awardBalancesReportEntry.getAgency().getReportingName();
        reportDetail.setAgencyName(agencyName);


        reportDetail.setAwardProjectTitle(awardBalancesReportEntry.getAwardProjectTitle());
        reportDetail.setAwardStatusCode(awardBalancesReportEntry.getAwardStatusCode());

        reportDetail.setAwardBeginningDate(awardBalancesReportEntry.getAwardBeginningDate());
        reportDetail.setAwardEndingDate(awardBalancesReportEntry.getAwardEndingDate());

        reportDetail.setPrimaryProjectDirector(awardBalancesReportEntry.getAwardPrimaryProjectDirectorName());
        reportDetail.setPrimaryFundManager(awardBalancesReportEntry.getAwardPrimaryFundManagerName());


        BigDecimal awardTotalAmount = (ObjectUtils.isNull(awardBalancesReportEntry.getAwardTotalAmountForReport())) ? BigDecimal.ZERO : awardBalancesReportEntry.getAwardTotalAmountForReport().bigDecimalValue();
        reportDetail.setAwardTotalAmount(awardTotalAmount);

        BigDecimal totalBilledToDate = (ObjectUtils.isNull(awardBalancesReportEntry.getTotalBilledToDate())) ? BigDecimal.ZERO : awardBalancesReportEntry.getTotalBilledToDate().bigDecimalValue();
        reportDetail.setTotalBilledToDate(totalBilledToDate);

        BigDecimal totalPaymentsToDate = (ObjectUtils.isNull(awardBalancesReportEntry.getTotalPaymentsToDate())) ? BigDecimal.ZERO : awardBalancesReportEntry.getTotalPaymentsToDate().bigDecimalValue();
        reportDetail.setTotalPaymentsToDate(totalPaymentsToDate);

        reportDetail.setAmountCurrentlyDue(totalBilledToDate.subtract(totalPaymentsToDate));

    }

}
