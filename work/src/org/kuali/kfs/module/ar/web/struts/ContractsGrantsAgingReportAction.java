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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsAndGrantsAgingReport;
import org.kuali.kfs.module.ar.businessobject.lookup.ContractsGrantsAgingReportLookupableHelperServiceImpl;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportSearchCriteriaDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.datadictionary.control.HiddenControlDefinition;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.lookup.LookupableHelperService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.control.ControlDefinition;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;


/**
 * This class handles Actions for lookup flow for ContractsGrantsAging Report.
 */
public class ContractsGrantsAgingReportAction extends ContractsGrantsReportLookupAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsAgingReportAction.class);
    private static volatile ContractsGrantsAgingReportService contractsGrantsAgingReportService;

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiLookupAction#start(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
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
    @Override
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
            request.setAttribute(KFSConstants.SEARCH_LIST_REQUEST_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(resultTable));

            // set Total's table
            totalList[0] = lookupForm.getTotal0to30();
            totalList[1] = lookupForm.getTotal31to60();
            totalList[2] = lookupForm.getTotal61to90();
            totalList[3] = lookupForm.getTotal91toSYSPR();
            totalList[4] = lookupForm.getTotalSYSPRplus1orMore();
            totalList[5] = lookupForm.getTotalOpenInvoices();
            totalList[6] = lookupForm.getTotalCredits();
            totalList[7] = lookupForm.getTotalWriteOffs();

            GlobalVariables.getUserSession().addObject(ArConstants.TOTALS_TABLE_KEY, totalList);
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
    @Override
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsAgingReportForm lookupForm = (ContractsGrantsAgingReportForm) form;
        String backUrl = getApplicationBaseUrl() + "/portal.do?selectedTab=maintenance";
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
    @Override
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
    @Override
    public ActionForward viewResults(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsAgingReportForm lookupForm = (ContractsGrantsAgingReportForm) form;
        request.setAttribute(KFSConstants.SEARCH_LIST_REQUEST_KEY, request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY));
        request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS, GlobalVariables.getUserSession().retrieveObject(request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY)));
        request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS_SIZE, request.getParameter(KFSConstants.REQUEST_SEARCH_RESULTS_SIZE));

        String[] totalsList = (String[]) GlobalVariables.getUserSession().retrieveObject(ArConstants.TOTALS_TABLE_KEY);

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
            Object totalsTable = GlobalVariables.getUserSession().retrieveObject(ArConstants.TOTALS_TABLE_KEY);
            request.setAttribute(ArConstants.TOTALS_TABLE_KEY, totalsTable);
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

        List<ContractsGrantsInvoiceDocument> displayList = lookupReportValues(cgInvoiceReportLookupForm, request, true);

        // sort list
        sortReport(displayList, ArPropertyConstants.ContractsGrantsAgingReportFields.LIST_SORT_PROPERTY);

        final ContractsGrantsReportDataBuilderService reportBuilderService = getContractsGrantsReportDataBuilderService();
        ContractsGrantsReportDataHolder cgInvoiceReportDataHolder = reportBuilderService.buildReportDataHolder(displayList, ArPropertyConstants.ContractsGrantsAgingReportFields.PDF_SORT_PROPERTY);
        cgInvoiceReportDataHolder.setReportTitle(generateReportTitle(cgInvoiceReportLookupForm));

        // build search criteria for report
        buildReportForSearchCriteria(cgInvoiceReportDataHolder.getSearchCriteria(), cgInvoiceReportLookupForm.getFieldsForLookup(), ContractsAndGrantsAgingReport.class);

        // export to pdf
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String reportFileName = generateReportPdf(cgInvoiceReportDataHolder, baos);
        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, reportFileName + ReportGeneration.PDF_FILE_EXTENSION);
        return null;
    }

    /**
     * Builds the title for the report
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#setReportTitle(org.kuali.rice.kns.web.struts.form.LookupForm)
     */
    @Override
    public String generateReportTitle(LookupForm lookupForm) {
        return "Contracts and Grants Aged Accounts Receivable Report \nAging Group: Total as of " + (String) lookupForm.getFieldsForLookup().get(ArPropertyConstants.CustomerAgingReportFields.REPORT_RUN_DATE);
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

        List<ContractsGrantsInvoiceDocument> displayList = lookupReportValues(cgInvoiceReportLookupForm, request, true);

        // get sort property
        String sortPropertyName = ArPropertyConstants.ContractsGrantsAgingReportFields.PDF_SORT_PROPERTY;

        // sort list
        sortReport(displayList, ArPropertyConstants.ContractsGrantsAgingReportFields.LIST_SORT_PROPERTY);

        final ContractsGrantsReportDataBuilderService reportBuilderService = getContractsGrantsReportDataBuilderService();
        ContractsGrantsReportDataHolder cgInvoiceReportDataHolder = reportBuilderService.buildReportDataHolder(displayList, ArPropertyConstants.ContractsGrantsAgingReportFields.PDF_SORT_PROPERTY);

        byte[] report = getContractsGrantsAgingReportService().generateCSVToExport(cgInvoiceReportDataHolder, displayList, sortPropertyName);
        if (report.length == 0) {
            throw new RuntimeException("Error: non-existent file or directory provided");
        }
        response.setContentType("text/csv");
        response.setHeader("Content-disposition", "attachment; filename=CSV-Export-C&GAging.csv");
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setContentLength(report.length);
        InputStream fis = new ByteArrayInputStream(report);
        IOUtils.copy(fis, response.getOutputStream());
        response.getOutputStream().flush();
        return null;
    }

    /**
     * Returns "contractsGrantsAgingReportBuilderService"
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#getReportBuilderServiceBeanName()
     */
    @Override
    public String getReportBuilderServiceBeanName() {
        return ArConstants.ReportBuilderDataServiceBeanNames.CONTRACTS_GRANTS_AGING;
    }

    public static ContractsGrantsAgingReportService getContractsGrantsAgingReportService() {
        if (contractsGrantsAgingReportService == null) {
            contractsGrantsAgingReportService = SpringContext.getBean(ContractsGrantsAgingReportService.class);
        }
        return contractsGrantsAgingReportService;
    }
}