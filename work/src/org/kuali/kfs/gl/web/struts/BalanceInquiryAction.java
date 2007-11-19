/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl.web.struts.action;

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
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.lookup.Lookupable;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.core.web.struts.form.LookupForm;
import org.kuali.core.web.ui.Field;
import org.kuali.core.web.ui.ResultRow;
import org.kuali.core.web.ui.Row;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.gl.bo.AccountBalance;
import org.kuali.module.gl.util.ObjectHelper;
import org.kuali.module.gl.web.lookupable.AccountBalanceByConsolidationLookupableHelperServiceImpl;
import org.kuali.module.gl.web.struts.form.BalanceInquiryForm;

/**
 * This class handles Actions for lookup flow
 */

public class BalanceInquiryAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceInquiryAction.class);

    private static final String TOTALS_TABLE_KEY = "totalsTable";

    private KualiConfigurationService kualiConfigurationService;
    private String[] totalTitles;

    public BalanceInquiryAction() {
        super();
        kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
    }

    /**
     * Sets up total titles
     */
    private void setTotalTitles() {
        totalTitles = new String[7];

        totalTitles[0] = kualiConfigurationService.getPropertyString(KFSKeyConstants.AccountBalanceService.INCOME);
        totalTitles[1] = kualiConfigurationService.getPropertyString(KFSKeyConstants.AccountBalanceService.INCOME_FROM_TRANSFERS);
        totalTitles[2] = kualiConfigurationService.getPropertyString(KFSKeyConstants.AccountBalanceService.INCOME_TOTAL);
        totalTitles[3] = kualiConfigurationService.getPropertyString(KFSKeyConstants.AccountBalanceService.EXPENSE);
        totalTitles[4] = kualiConfigurationService.getPropertyString(KFSKeyConstants.AccountBalanceService.EXPENSE_FROM_TRANSFERS);
        totalTitles[5] = kualiConfigurationService.getPropertyString(KFSKeyConstants.AccountBalanceService.EXPENSE_TOTAL);
        totalTitles[6] = kualiConfigurationService.getPropertyString(KFSKeyConstants.AccountBalanceService.TOTAL);

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
     */
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BalanceInquiryForm lookupForm = (BalanceInquiryForm) form;

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

                    boolean ok = ObjectHelper.isOneOf(balance.getTitle(), getTotalTitles());
                    if (ok) {

                        if (totalSize > 7) {
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

            request.setAttribute(KFSConstants.SEARCH_LIST_REQUEST_KEY, GlobalVariables.getUserSession().addObject(resultTable));

        }
        catch (NumberFormatException e) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, KFSKeyConstants.ERROR_CUSTOM, new String[] { "Fiscal Year must be a four-digit number" });
        }
        catch (Exception e) {
            GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_ERRORS, KFSKeyConstants.ERROR_CUSTOM, new String[] { "Please report the server error." });
            LOG.error("Application Errors", e);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Refresh - is called when one quickFinder returns to the previous one. Sets all the values and performs the new search.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
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

    public void setKualiConfigurationService(KualiConfigurationService kcs) {
        kualiConfigurationService = kcs;
    }

}