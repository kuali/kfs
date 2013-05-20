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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsAndGrantsAgingReport;
import org.kuali.kfs.module.ar.businessobject.CustomerCollector;
import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.kfs.module.ar.businessobject.lookup.ContractsGrantsAgingReportLookupableHelperServiceImpl;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.report.ContractsGrantsAgingReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportSearchCriteriaDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.datadictionary.control.ControlDefinition;
import org.kuali.rice.kns.datadictionary.control.HiddenControlDefinition;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.lookup.LookupableHelperService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kew.api.WorkflowDocument;


/**
 * This class handles Actions for lookup flow for ContractsGrantsAging Report.
 */

public class ContractsGrantsAgingReportAction extends ContractsGrantsReportLookupAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsAgingReportAction.class);

    private static final String TOTALS_TABLE_KEY = "totalsTable";

    /**
     * Default Constructor
     */
    public ContractsGrantsAgingReportAction() {
        super();
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiLookupAction#start(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Search - sets the values of the data entered on the form on the jsp into a map and then searches for the results.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsAgingReportForm lookupForm = (ContractsGrantsAgingReportForm) form;
        Lookupable lookupable = lookupForm.getLookupable();
        if (ObjectUtils.isNull(lookupable)) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }
        LookupableHelperService lookupablehelper = lookupable.getLookupableHelperService();
        Collection displayList = new ArrayList();
        List<ResultRow> resultTable = new ArrayList<ResultRow>();
        String[] totalList = new String[8];
        try {
            displayList = lookupable.performLookup(lookupForm, resultTable, true);
            Object[] resultTableAsArray = resultTable.toArray();
            CollectionIncomplete incompleteDisplayList = (CollectionIncomplete) displayList;
            Long totalSize = ((CollectionIncomplete) displayList).getActualSizeIfTruncated();
            request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS_SIZE, totalSize);
            request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS, resultTable);
            if (request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY) != null) {
                GlobalVariables.getUserSession().removeObject(request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY));
            }
            request.setAttribute(KFSConstants.SEARCH_LIST_REQUEST_KEY, GlobalVariables.getUserSession().addObject(resultTable));

            // set Total's table
            totalList[0] = lookupForm.getTotal0to30();
            totalList[1] = lookupForm.getTotal31to60();
            totalList[2] = lookupForm.getTotal61to90();
            totalList[3] = lookupForm.getTotal91toSYSPR();
            totalList[4] = lookupForm.getTotalSYSPRplus1orMore();
            totalList[5] = lookupForm.getTotalOpenInvoices();
            totalList[6] = lookupForm.getTotalCredits();
            totalList[7] = lookupForm.getTotalWriteOffs();

            GlobalVariables.getUserSession().addObject(TOTALS_TABLE_KEY, totalList);
        }
        catch (NumberFormatException e) {
            LOG.error("Number format Exception", e);
        }
        catch (Exception e) {
            LOG.error("Application Errors", e);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Refresh - is called when one quickFinder returns to the previous one. Sets all the values and performs the new search.
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LookupForm lookupForm = (LookupForm) form;
        Lookupable lookupable = lookupForm.getLookupable();
        if (ObjectUtils.isNull(lookupable)) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        Map fieldValues = new HashMap();
        Map values = lookupForm.getFields();

        for (Iterator iter = lookupable.getRows().iterator(); iter.hasNext();) {
            Row row = (Row) iter.next();

            for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                Field field = (Field) iterator.next();

                if (field.getPropertyName() != null && !field.getPropertyName().equals("")) {
                    if (request.getParameter(field.getPropertyName()) != null) {
                        field.setPropertyValue(request.getParameter(field.getPropertyName()));
                    }
                    else if (values.get(field.getPropertyName()) != null) {
                        field.setPropertyValue(values.get(field.getPropertyName()));
                    }
                }
                fieldValues.put(field.getPropertyName(), field.getPropertyValue());
            }
        }
        fieldValues.put(KFSConstants.DOC_FORM_KEY, lookupForm.getFormKey());
        fieldValues.put(KFSConstants.BACK_LOCATION, lookupForm.getBackLocation());

        if (lookupable.checkForAdditionalFields(fieldValues)) {
            for (Iterator iter = lookupable.getRows().iterator(); iter.hasNext();) {
                Row row = (Row) iter.next();
                for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                    Field field = (Field) iterator.next();
                    if (field.getPropertyName() != null && !field.getPropertyName().equals("")) {
                        if (request.getParameter(field.getPropertyName()) != null) {
                            field.setPropertyValue(request.getParameter(field.getPropertyName()));
                            fieldValues.put(field.getPropertyName(), request.getParameter(field.getPropertyName()));
                        }
                        else if (values.get(field.getPropertyName()) != null) {
                            field.setPropertyValue(values.get(field.getPropertyName()));
                        }
                    }
                }
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Returns as if return with no value was selected.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsAgingReportForm lookupForm = (ContractsGrantsAgingReportForm) form;
        String backUrl = getBasePath(request) + "/portal.do?selectedTab=maintenance";
        return new ActionForward(backUrl, true);
    }


    /**
     * Clears the values of all the fields on the jsp.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward clearValues(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        LookupForm lookupForm = (LookupForm) form;
        Lookupable lookupable = lookupForm.getLookupable();
        if (ObjectUtils.isNull(lookupable)) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        for (Iterator iter = lookupable.getRows().iterator(); iter.hasNext();) {
            Row row = (Row) iter.next();
            for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                Field field = (Field) iterator.next();
                if (!field.getFieldType().equals(Field.RADIO)) {
                    field.setPropertyValue(field.getDefaultValue());
                }
            }
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * View results from balance inquiry action
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward viewResults(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsAgingReportForm lookupForm = (ContractsGrantsAgingReportForm) form;
        request.setAttribute(KFSConstants.SEARCH_LIST_REQUEST_KEY, request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY));
        request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS, GlobalVariables.getUserSession().retrieveObject(request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY)));
        request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS_SIZE, request.getParameter(KFSConstants.REQUEST_SEARCH_RESULTS_SIZE));

        String[] totalsList = (String[]) GlobalVariables.getUserSession().retrieveObject(TOTALS_TABLE_KEY);

        // set into form Fields
        if (ObjectUtils.isNotNull(totalsList) && totalsList.length > 0) {
            lookupForm.setTotal0to30(totalsList[0]);
            lookupForm.setTotal31to60(totalsList[1]);
            lookupForm.setTotal61to90(totalsList[2]);
            lookupForm.setTotal91toSYSPR(totalsList[3]);
            lookupForm.setTotalSYSPRplus1orMore(totalsList[4]);
            lookupForm.setTotalOpenInvoices(totalsList[5]);
            lookupForm.setTotalCredits(totalsList[6]);
            lookupForm.setTotalWriteOffs(totalsList[7]);
        }


        if (((ContractsGrantsAgingReportForm) form).getLookupable().getLookupableHelperService() instanceof ContractsGrantsAgingReportLookupableHelperServiceImpl) {
            Object totalsTable = GlobalVariables.getUserSession().retrieveObject(TOTALS_TABLE_KEY);
            request.setAttribute(TOTALS_TABLE_KEY, totalsTable);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Print the pdf file for all cginvoices by agency
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ContractsGrantsAgingReportForm cgInvoiceReportLookupForm = (ContractsGrantsAgingReportForm) form;
        String methodToCall = findMethodToCall(form, request);
        if (methodToCall.equalsIgnoreCase(KRADConstants.SEARCH_METHOD)) {
            GlobalVariables.getUserSession().removeObjectsByPrefix(KRADConstants.SEARCH_METHOD);
        }

        Lookupable kualiLookupable = cgInvoiceReportLookupForm.getLookupable();
        if (ObjectUtils.isNull(kualiLookupable)) {
            throw new RuntimeException("Lookupable is null.");
        }

        List<ContractsGrantsInvoiceDocument> displayList = new ArrayList<ContractsGrantsInvoiceDocument>();
        List<ResultRow> resultTable = new ArrayList<ResultRow>();

        // validate search parameters
        kualiLookupable.validateSearchParameters(cgInvoiceReportLookupForm.getFields());

        // this is for 200 limit. turn it off for report.
        boolean bounded = false;

        displayList = (List<ContractsGrantsInvoiceDocument>) kualiLookupable.performLookup(cgInvoiceReportLookupForm, resultTable, bounded);

        // get sort property
        String sortPropertyName = ArPropertyConstants.ContractsGrantsAgingReportFields.PDF_SORT_PROPERTY;

        // sort list
        sortReport(displayList, ArPropertyConstants.ContractsGrantsAgingReportFields.LIST_SORT_PROPERTY);

        // check field is valid for subtotal
        boolean isFieldSubtotalRequired = true;
        Map<String, List<KualiDecimal>> subTotalMap = new HashMap<String, List<KualiDecimal>>();

        if (isFieldSubtotalRequired) {
            subTotalMap = buildSubTotalMap(displayList, sortPropertyName);
        }

        BigDecimal invoiceTotal = BigDecimal.ZERO;
        BigDecimal paymentTotal = BigDecimal.ZERO;
        BigDecimal remainingTotal = BigDecimal.ZERO;

        // build report
        ContractsGrantsReportDataHolder cgInvoiceReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ContractsGrantsAgingReportDetailDataHolder> details = cgInvoiceReportDataHolder.getDetails();

        for (ContractsGrantsInvoiceDocument cgInvoiceEntry : displayList) {
            ContractsGrantsAgingReportDetailDataHolder reportDetail = new ContractsGrantsAgingReportDetailDataHolder();
            // set report data
            setReportData(cgInvoiceEntry, reportDetail);

            if (isFieldSubtotalRequired) {
                // set sortedFieldValue for grouping in the report
                reportDetail.setSortedFieldValue(getPropertyValue(cgInvoiceEntry, sortPropertyName));
                reportDetail.setDisplaySubtotalInd(true);
                // set subTotal from subTotalMap
                reportDetail.setInvoiceSubTotal(subTotalMap.get(getPropertyValue(cgInvoiceEntry, sortPropertyName)).get(0).bigDecimalValue());
                reportDetail.setPaymentSubTotal(subTotalMap.get(getPropertyValue(cgInvoiceEntry, sortPropertyName)).get(1).bigDecimalValue());
                reportDetail.setRemainingSubTotal(subTotalMap.get(getPropertyValue(cgInvoiceEntry, sortPropertyName)).get(2).bigDecimalValue());


            }
            else {
                // set this to empty string for not displaying subtotal
                reportDetail.setDisplaySubtotalInd(false);
            }

            invoiceTotal = invoiceTotal.add(reportDetail.getInvoiceAmount());
            paymentTotal = paymentTotal.add(reportDetail.getPaymentAmount());
            remainingTotal = remainingTotal.add(reportDetail.getRemainingAmount());
            details.add(reportDetail);
        }

        // set total field
        ContractsGrantsAgingReportDetailDataHolder reportDetail = new ContractsGrantsAgingReportDetailDataHolder();
        reportDetail.setDisplayTotalInd(true);
        reportDetail.setInvoiceTotal(invoiceTotal);
        reportDetail.setPaymentTotal(paymentTotal);
        reportDetail.setRemainingTotal(remainingTotal);

        details.add(reportDetail);

        cgInvoiceReportDataHolder.setDetails(details);
        cgInvoiceReportDataHolder.setReportTitle("Contracts and Grants Aged Accounts Receivable Report \nAging Group: Total as of " + (String) cgInvoiceReportLookupForm.getFieldsForLookup().get(ArPropertyConstants.CustomerAgingReportFields.REPORT_RUN_DATE));

        // build search criteria for report
        buildReportForSearchCriteia(cgInvoiceReportDataHolder.getSearchCriteria(), cgInvoiceReportLookupForm.getFieldsForLookup());

        // export to pdf
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String reportFileName = SpringContext.getBean(ContractsGrantsAgingReportService.class).generateReport(cgInvoiceReportDataHolder, baos);
        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, reportFileName + ReportGeneration.PDF_FILE_EXTENSION);
        return null;
    }

    /**
     * @param displayList
     * @param sortPropertyName
     * @return
     */
    private Map<String, List<KualiDecimal>> buildSubTotalMap(List<ContractsGrantsInvoiceDocument> displayList, String sortPropertyName) {
        Map<String, List<KualiDecimal>> returnSubTotalMap = new HashMap<String, List<KualiDecimal>>();
        // get list of sort fields
        List<String> valuesOfsortProperty = getListOfValuesSortedProperties(displayList, sortPropertyName);

        // calculate sub_total and build subTotalMap
        for (String value : valuesOfsortProperty) {
            KualiDecimal invoiceSubTotal = KualiDecimal.ZERO;
            KualiDecimal paymentSubTotal = KualiDecimal.ZERO;
            KualiDecimal remainingSubTotal = KualiDecimal.ZERO;

            for (ContractsGrantsInvoiceDocument cgInvoiceReportEntry : displayList) {
                // set fieldValue as "" when it is null
                if (value.equals(getPropertyValue(cgInvoiceReportEntry, sortPropertyName))) {
                    KualiDecimal sourceTotal = cgInvoiceReportEntry.getSourceTotal();
                    KualiDecimal paymentAmount = cgInvoiceReportEntry.getPaymentAmount();
                    invoiceSubTotal = invoiceSubTotal.add(sourceTotal);
                    paymentSubTotal = paymentSubTotal.add(paymentAmount);
                    remainingSubTotal = remainingSubTotal.add(sourceTotal.subtract(paymentSubTotal));
                }
            }
            List<KualiDecimal> allSubTotal = new ArrayList<KualiDecimal>();
            allSubTotal.add(0, invoiceSubTotal);
            allSubTotal.add(1, paymentSubTotal);
            allSubTotal.add(2, remainingSubTotal);

            returnSubTotalMap.put(value, allSubTotal);
        }
        return returnSubTotalMap;
    }

    /**
     * @param cgInvoiceReportEntry
     * @param reportDetail
     */
    private void setReportData(ContractsGrantsInvoiceDocument cgInvoiceReportEntry, ContractsGrantsAgingReportDetailDataHolder reportDetail) {

        java.util.Date today = new java.util.Date();
        Date sqlToday = new java.sql.Date(today.getTime());
        reportDetail.setAgencyNumber(cgInvoiceReportEntry.getAward().getAgency().getAgencyNumber());
        reportDetail.setAgencyName(cgInvoiceReportEntry.getAward().getAgency().getReportingName());
        reportDetail.setCustomerNumber(cgInvoiceReportEntry.getAccountsReceivableDocumentHeader().getCustomerNumber());
        reportDetail.setProposalNumber(cgInvoiceReportEntry.getProposalNumber().toString());
        reportDetail.setAwardEndDate(cgInvoiceReportEntry.getAward().getAwardEndingDate());
        reportDetail.setDocumentNumber(cgInvoiceReportEntry.getDocumentNumber());

        WorkflowDocument workflowDocument = cgInvoiceReportEntry.getDocumentHeader().getWorkflowDocument();
        Date docCreateDate = new Date(workflowDocument.getDateCreated().getTime());
        reportDetail.setInvoiceDate(docCreateDate);

        // last event date
        List<Event> events = cgInvoiceReportEntry.getEvents();
        if (ObjectUtils.isNotNull(events) && CollectionUtils.isNotEmpty(events)) {
            Collections.sort(events, new Comparator<Event>() {

                @Override
                public int compare(Event o1, Event o2) {
                    return o2.getActivityDate().compareTo(o1.getActivityDate());
                }
            });
            reportDetail.setLastEventDate(events.get(0).getActivityDate());
        }

        // set Customer Collector
        String custColl = null;
        if (cgInvoiceReportEntry.getAccountsReceivableDocumentHeader().getCustomer() != null) {
            CustomerCollector custCollector = cgInvoiceReportEntry.getAccountsReceivableDocumentHeader().getCustomer().getCustomerCollector();
            if (ObjectUtils.isNotNull(custCollector)) {
                custColl = custCollector.getPrincipalId();
                custColl = ObjectUtils.isNotNull(custCollector.getCollector()) ? custCollector.getCollector().getName() : null;
                reportDetail.setCollectorName(custColl);
            }
        }

        // calculate ageInDays : current date - created date
        final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000;
        reportDetail.setAgeInDays((sqlToday.getTime() - docCreateDate.getTime()) / MILLSECS_PER_DAY);

        BigDecimal invoiceAmount = cgInvoiceReportEntry.getTotalDollarAmount().bigDecimalValue();
        reportDetail.setInvoiceAmount(invoiceAmount);

        BigDecimal paymentAmount = cgInvoiceReportEntry.getPaymentAmount().bigDecimalValue();
        reportDetail.setPaymentAmount(paymentAmount);

        BigDecimal remainingAmount = invoiceAmount.subtract(paymentAmount);
        reportDetail.setRemainingAmount(remainingAmount);

    }

    /**
     * This method is used to build pdf report search criteria for Collection activity report
     * 
     * @param searchCriteria
     * @param fieldsForLookup
     */
    protected void buildReportForSearchCriteia(List<ContractsGrantsReportSearchCriteriaDataHolder> searchCriteria, Map fieldsForLookup) {
        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        for (Object field : fieldsForLookup.keySet()) {
            String fieldString = (ObjectUtils.isNull(field)) ? "" : field.toString();
            String valueString = (ObjectUtils.isNull(fieldsForLookup.get(field))) ? "" : fieldsForLookup.get(field).toString();
            if (!fieldString.equals("") && !valueString.equals("") && !ArConstants.ReportsConstants.reportSearchCriteriaExceptionList.contains(fieldString)) {
                ControlDefinition controldef = dataDictionaryService.getAttributeControlDefinition(ContractsAndGrantsAgingReport.class, fieldString);
                if (!(controldef instanceof HiddenControlDefinition)) {
                    ContractsGrantsReportSearchCriteriaDataHolder criteriaData = new ContractsGrantsReportSearchCriteriaDataHolder();
                    String label = dataDictionaryService.getAttributeLabel(ContractsAndGrantsAgingReport.class, fieldString);
                    criteriaData.setSearchFieldLabel(label);
                    criteriaData.setSearchFieldValue(valueString);
                    searchCriteria.add(criteriaData);
                }
            }
        }
    }

    /**
     * This method is called when export button is clicked and export a csv file.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsAgingReportForm cgInvoiceReportLookupForm = (ContractsGrantsAgingReportForm) form;
        String methodToCall = findMethodToCall(form, request);
        if (methodToCall.equalsIgnoreCase(KRADConstants.SEARCH_METHOD)) {
            GlobalVariables.getUserSession().removeObjectsByPrefix(KRADConstants.SEARCH_METHOD);
        }

        Lookupable kualiLookupable = cgInvoiceReportLookupForm.getLookupable();
        if (ObjectUtils.isNull(kualiLookupable)) {
            throw new RuntimeException("Lookupable is null.");
        }

        List<ContractsGrantsInvoiceDocument> displayList = new ArrayList<ContractsGrantsInvoiceDocument>();
        List<ResultRow> resultTable = new ArrayList<ResultRow>();

        // validate search parameters
        kualiLookupable.validateSearchParameters(cgInvoiceReportLookupForm.getFields());

        // this is for 200 limit. turn it off for report.
        boolean bounded = false;

        displayList = (List<ContractsGrantsInvoiceDocument>) kualiLookupable.performLookup(cgInvoiceReportLookupForm, resultTable, bounded);

        // get sort property
        String sortPropertyName = ArPropertyConstants.ContractsGrantsAgingReportFields.PDF_SORT_PROPERTY;

        // sort list
        sortReport(displayList, ArPropertyConstants.ContractsGrantsAgingReportFields.LIST_SORT_PROPERTY);

        // check field is valid for subtotal
        boolean isFieldSubtotalRequired = true;
        Map<String, List<KualiDecimal>> subTotalMap = new HashMap<String, List<KualiDecimal>>();

        if (isFieldSubtotalRequired) {
            subTotalMap = buildSubTotalMap(displayList, sortPropertyName);
        }

        BigDecimal invoiceTotal = BigDecimal.ZERO;
        BigDecimal paymentTotal = BigDecimal.ZERO;
        BigDecimal remainingTotal = BigDecimal.ZERO;

        // build report
        Map<String, List<ContractsGrantsAgingReportDetailDataHolder>> map = new LinkedHashMap<String, List<ContractsGrantsAgingReportDetailDataHolder>>();
        ContractsGrantsReportDataHolder cgInvoiceReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ContractsGrantsAgingReportDetailDataHolder> details = cgInvoiceReportDataHolder.getDetails();

        for (ContractsGrantsInvoiceDocument cgInvoiceEntry : displayList) {
            ContractsGrantsAgingReportDetailDataHolder reportDetail = new ContractsGrantsAgingReportDetailDataHolder();
            // set report data
            setReportData(cgInvoiceEntry, reportDetail);

            if (isFieldSubtotalRequired) {
                // set sortedFieldValue for grouping in the report
                reportDetail.setSortedFieldValue(getPropertyValue(cgInvoiceEntry, sortPropertyName));
                // set subTotal from subTotalMap
                reportDetail.setInvoiceSubTotal(subTotalMap.get(getPropertyValue(cgInvoiceEntry, sortPropertyName)).get(0).bigDecimalValue());
                reportDetail.setPaymentSubTotal(subTotalMap.get(getPropertyValue(cgInvoiceEntry, sortPropertyName)).get(1).bigDecimalValue());
                reportDetail.setRemainingSubTotal(subTotalMap.get(getPropertyValue(cgInvoiceEntry, sortPropertyName)).get(2).bigDecimalValue());

            }
            else {
                // set this to empty string for not displaying subtotal
                reportDetail.setDisplaySubtotalInd(false);
            }

            invoiceTotal = invoiceTotal.add(reportDetail.getInvoiceAmount());
            paymentTotal = paymentTotal.add(reportDetail.getPaymentAmount());
            remainingTotal = remainingTotal.add(reportDetail.getRemainingAmount());

            List<ContractsGrantsAgingReportDetailDataHolder> list = map.get(reportDetail.getAgencyNumber());
            if (ObjectUtils.isNull(list)) {
                list = new LinkedList<ContractsGrantsAgingReportDetailDataHolder>();
            }
            list.add(reportDetail);
            map.put(reportDetail.getAgencyNumber(), list);
            details.add(reportDetail);
        }

        if (ObjectUtils.isNotNull(map) && !map.isEmpty()) {
            for (String key : map.keySet()) {
                List<ContractsGrantsAgingReportDetailDataHolder> list = map.get(key);
                if (ObjectUtils.isNotNull(list) && !list.isEmpty()) {
                    ContractsGrantsAgingReportDetailDataHolder cGDetailHolderNew = new ContractsGrantsAgingReportDetailDataHolder();
                    cGDetailHolderNew.setDisplaySubtotalInd(true);
                    cGDetailHolderNew.setInvoiceSubTotal((subTotalMap.get(key)).get(0).bigDecimalValue());
                    cGDetailHolderNew.setPaymentSubTotal((subTotalMap.get(key)).get(1).bigDecimalValue());
                    cGDetailHolderNew.setRemainingSubTotal((subTotalMap.get(key)).get(2).bigDecimalValue());
                    list.add(cGDetailHolderNew);
                    map.put(key, list);
                }
            }
        }

        // set total field
        ContractsGrantsAgingReportDetailDataHolder reportDetail = new ContractsGrantsAgingReportDetailDataHolder();
        reportDetail.setDisplayTotalInd(true);
        reportDetail.setInvoiceTotal(invoiceTotal);
        reportDetail.setPaymentTotal(paymentTotal);
        reportDetail.setRemainingTotal(remainingTotal);

        ContractsGrantsAgingReportService reportService = SpringContext.getBean(ContractsGrantsAgingReportService.class);
        byte[] report = reportService.generateCSVToExport(map, reportDetail);
        if (report.length == 0) {
            throw new RuntimeException("Error: non-existent file or directory provided");
        }
        response.setContentType("text/csv");
        response.setHeader("Content-disposition", "attachment; filename=CSV-Export-C&GAging.csv");
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setContentLength((int) report.length);
        InputStream fis = new ByteArrayInputStream(report);
        IOUtils.copy(fis, response.getOutputStream());
        response.getOutputStream().flush();
        return null;
    }

}
