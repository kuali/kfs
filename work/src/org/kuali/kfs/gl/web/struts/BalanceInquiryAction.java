/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.web.struts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.gl.ObjectHelper;
import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.gl.businessobject.lookup.AccountBalanceByConsolidationLookupableHelperServiceImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * This class handles Actions for lookup flow
 */

public class BalanceInquiryAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceInquiryAction.class);

    protected static final String TOTALS_TABLE_KEY = "totalsTable";

    protected ConfigurationService kualiConfigurationService;
    protected DataDictionaryService dataDictionaryService;
    protected String[] totalTitles;

    public BalanceInquiryAction() {
        super();
        kualiConfigurationService = SpringContext.getBean(ConfigurationService.class);
        dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
    }

    /**
     * Sets up total titles
     */
    protected void setTotalTitles() {
        totalTitles = new String[7];

        totalTitles[0] = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.INCOME);
        totalTitles[1] = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.INCOME_FROM_TRANSFERS);
        totalTitles[2] = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.INCOME_TOTAL);
        totalTitles[3] = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.EXPENSE);
        totalTitles[4] = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.EXPENSE_FROM_TRANSFERS);
        totalTitles[5] = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.EXPENSE_TOTAL);
        totalTitles[6] = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.TOTAL);

    }

    /**
     * Returns an array of total titles
     *
     * @return array of total titles
     */
    private String[] getTotalTitles() {
        if (null == totalTitles) {
            setTotalTitles();
        }

        return totalTitles;
    }

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
     *
     * KRAD Conversion: Lookupable performs customization of the results if
     * account balance by consolidation. The result rows are added to a collection
     * based on field's actual size if truncated is > 7.
     *
     * Fields are in data dictionary for bo Balance.
     */
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BalanceInquiryForm lookupForm = (BalanceInquiryForm) form;

        // check consolidation option and sub-account number
        Map fieldValues = lookupForm.getFields();
        String consolidationOption = (String) fieldValues.get(Constant.CONSOLIDATION_OPTION);
        String subAccountNumber = (String) fieldValues.get(Constant.SUB_ACCOUNT_OPTION);
        if (Constant.EXCLUDE_SUBACCOUNTS.equals(consolidationOption) && !subAccountNumber.equals("")){
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, KFSKeyConstants.ERROR_BALANCE_CONSOLIDATION_EXCLUDE_SUBACCOUNT);
        }

        Lookupable lookupable = lookupForm.getLookupable();

        if (lookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        Collection displayList = new ArrayList();
        List<ResultRow> resultTable = new ArrayList<ResultRow>();

        lookupable.validateSearchParameters(lookupForm.getFields());

        try {
            displayList = lookupable.performLookup(lookupForm, resultTable, true);

            Object[] resultTableAsArray = resultTable.toArray();

            CollectionIncomplete incompleteDisplayList = (CollectionIncomplete) displayList;
            Long totalSize = ((CollectionIncomplete) displayList).getActualSizeIfTruncated();

            request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS_SIZE, totalSize);

            // TODO: use inheritance instead of this if statement
            if (lookupable.getLookupableHelperService() instanceof AccountBalanceByConsolidationLookupableHelperServiceImpl) {

                Collection totalsTable = new ArrayList();

                int listIndex = 0;
                int arrayIndex = 0;
                int listSize = incompleteDisplayList.size();

                for (; listIndex < listSize;) {

                    AccountBalance balance = (AccountBalance) incompleteDisplayList.get(listIndex);

                    boolean isTotalTitle = ObjectHelper.isOneOf(balance.getTitle(), getTotalTitles());
                    if (isTotalTitle) {
                        if (totalSize > totalTitles.length) {
                            totalsTable.add(resultTableAsArray[arrayIndex]);
                        }
                        resultTable.remove(resultTableAsArray[arrayIndex]);

                        incompleteDisplayList.remove(balance);
                        // account for the removal of the balance which resizes the list
                        listIndex--;
                        listSize--;

                    }

                    listIndex++;
                    arrayIndex++;

                }

                request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS, resultTable);

                request.setAttribute(TOTALS_TABLE_KEY, totalsTable);
                GlobalVariables.getUserSession().addObject(TOTALS_TABLE_KEY, totalsTable);

            }
            else {

                request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS, resultTable);

            }

            if (request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY) != null) {
                GlobalVariables.getUserSession().removeObject(request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY));
            }

            request.setAttribute(KFSConstants.SEARCH_LIST_REQUEST_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(resultTable));

        }
        catch (NumberFormatException e) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, KFSKeyConstants.ERROR_CUSTOM, new String[] { "Fiscal Year must be a four-digit number" });
        }
        catch (Exception e) {
            GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_ERRORS, KFSKeyConstants.ERROR_CUSTOM, new String[] { "Please report the server error." });
            LOG.error("Application Errors", e);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Refresh - is called when one quickFinder returns to the previous one. Sets all the values and performs the new search.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     *
     * KRAD Conversion: Lookupable performs customization of the fields and check for additional fields.
     *
     * Fields are in data dictionary for bo Balance.
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LookupForm lookupForm = (LookupForm) form;
        Lookupable lookupable = lookupForm.getLookupable();
        if (lookupable == null) {
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
        LookupForm lookupForm = (LookupForm) form;

        String backUrl = lookupForm.getBackLocation() + "?methodToCall=refresh&docFormKey=" + lookupForm.getFormKey();
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
     *
     * KRAD Conversion: Lookupable performs setting/clearing of the field values.
     *
     * Fields are in data dictionary for bo Balance.
     */
    public ActionForward clearValues(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        LookupForm lookupForm = (LookupForm) form;
        Lookupable lookupable = lookupForm.getLookupable();
        if (lookupable == null) {
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
        request.setAttribute(KFSConstants.SEARCH_LIST_REQUEST_KEY, request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY));
        request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS, GlobalVariables.getUserSession().retrieveObject(request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY)));
        request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS_SIZE, request.getParameter(KFSConstants.REQUEST_SEARCH_RESULTS_SIZE));

        // TODO: use inheritance instead of this if statement
        if (((BalanceInquiryForm) form).getLookupable().getLookupableHelperService() instanceof AccountBalanceByConsolidationLookupableHelperServiceImpl) {
            Object totalsTable = GlobalVariables.getUserSession().retrieveObject(TOTALS_TABLE_KEY);
            request.setAttribute(TOTALS_TABLE_KEY, totalsTable);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public void setConfigurationService(ConfigurationService kcs) {
        kualiConfigurationService = kcs;
    }

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(KRADConstants.PARAM_MAINTENANCE_VIEW_MODE, KRADConstants.PARAM_MAINTENANCE_VIEW_MODE_LOOKUP);
        org.kuali.rice.kns.datadictionary.BusinessObjectEntry boe = (org.kuali.rice.kns.datadictionary.BusinessObjectEntry) dataDictionaryService.getDataDictionary().getBusinessObjectEntry(((LookupForm) form).getBusinessObjectClassName());
        int numCols = boe.getLookupDefinition().getNumOfColumns();
        if (numCols <= 0) {
            numCols = KRADConstants.DEFAULT_NUM_OF_COLUMNS; // by default, always show one column.
        }
        ((LookupForm) form).setNumColumns(numCols);
        return super.execute(mapping, form, request, response);
    }
}
