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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsAndGrantsAgingReport;
import org.kuali.kfs.module.ar.businessobject.lookup.ContractsGrantsAgingReportLookupableHelperServiceImpl;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.lookup.LookupableHelperService;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;


/**
 * This class handles Actions for lookup flow for ContractsGrantsAging Report.
 */
public class ContractsGrantsAgingReportAction extends ContractsGrantsReportLookupAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsAgingReportAction.class);
    private static volatile ContractsGrantsAgingReportService contractsGrantsAgingReportService;
    private static volatile ConfigurationService configurationService;

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
     * Reports are more hard coded here in C&G Aging...
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#sortReportValues(java.util.List)
     */
    @Override
    protected <B extends BusinessObject> String sortReportValues(List<B> displayList) {
        sortReport(displayList, ArPropertyConstants.ContractsGrantsAgingReportFields.LIST_SORT_PROPERTY);
        return ArPropertyConstants.ContractsGrantsAgingReportFields.PDF_SORT_PROPERTY;
    }

    /**
     * Builds the title for the report
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#setReportTitle(org.kuali.rice.kns.web.struts.form.LookupForm)
     */
    @Override
    public String generateReportTitle(LookupForm lookupForm) {
        final String reportTitlePattern = getConfigurationService().getPropertyValueAsString(ArKeyConstants.CONTRACTS_REPORTS_AGING_REPORT_TITLE);
        return MessageFormat.format(reportTitlePattern, (String) lookupForm.getFieldsForLookup().get(ArPropertyConstants.CustomerAgingReportFields.REPORT_RUN_DATE));
    }

    /**
     * Returns "contractsGrantsAgingReportBuilderService"
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#getReportBuilderServiceBeanName()
     */
    @Override
    public String getReportBuilderServiceBeanName() {
        return ArConstants.ReportBuilderDataServiceBeanNames.CONTRACTS_GRANTS_AGING;
    }

    /**
     * We don't really call this on this report
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#getSortFieldName()
     */
    @Override
    protected String getSortFieldName() {
        return null;
    }

    /**
     * Returns the class of ContractsAndGrantsAgingReport
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#getPrintSearchCriteriaClass()
     */
    @Override
    public Class<? extends BusinessObject> getPrintSearchCriteriaClass() {
        return ContractsAndGrantsAgingReport.class;
    }

    /**
     * Looks up different report values, because this report's print report items are different than the normal lookup
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#lookupReportValues(org.kuali.rice.kns.web.struts.form.LookupForm, javax.servlet.http.HttpServletRequest, boolean)
     */
    @Override
    protected List<ContractsGrantsInvoiceDocument> lookupReportValues(LookupForm form, HttpServletRequest request, boolean performValidate) throws Exception {
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

        final List<ContractsGrantsInvoiceDocument> displayList = getContractsGrantsAgingReportService().flattenContrantsGrantsInvoiceDocumentMap(getContractsGrantsAgingReportService().lookupContractsGrantsInvoiceDocumentsForAging(form.getFieldsForLookup()));
        return displayList;
    }

    public static ContractsGrantsAgingReportService getContractsGrantsAgingReportService() {
        if (contractsGrantsAgingReportService == null) {
            contractsGrantsAgingReportService = SpringContext.getBean(ContractsGrantsAgingReportService.class);
        }
        return contractsGrantsAgingReportService;
    }

    public static ConfigurationService getConfigurationService() {
        if (configurationService == null) {
            configurationService = SpringContext.getBean(ConfigurationService.class);
        }
        return configurationService;
    }
}