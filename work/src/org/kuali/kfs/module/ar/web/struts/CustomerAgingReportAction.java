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
import org.kuali.kfs.module.ar.businessobject.lookup.CustomerAgingReportLookupableHelperServiceImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.lookup.LookupableHelperService;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.GlobalVariables;


/**
 * This class handles Actions for lookup flow
 */

public class CustomerAgingReportAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerAgingReportAction.class);

    private static final String TOTALS_TABLE_KEY = "totalsTable";


    public CustomerAgingReportAction() {
        super();
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
     * KRAD Conversion: Lookupable performs customization of the results.
     * 
     * Fields are in data dictionary.
     */
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerAgingReportForm lookupForm = (CustomerAgingReportForm) form;

        Lookupable lookupable = lookupForm.getLookupable();    

        if (lookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }
        
        LookupableHelperService lookupablehelper =  lookupable.getLookupableHelperService();
        Collection displayList = new ArrayList();
        List<ResultRow> resultTable = new ArrayList<ResultRow>();

        try {
            displayList = lookupable.performLookup(lookupForm, resultTable, true);

            Object[] resultTableAsArray = resultTable.toArray();


            CollectionIncomplete incompleteDisplayList = (CollectionIncomplete) displayList;
            Long totalSize = ((CollectionIncomplete) displayList).getActualSizeIfTruncated();

            request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS_SIZE, totalSize);

            request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS, resultTable);
           
            String resultsKey = request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY);
            if (resultsKey != null) {
                GlobalVariables.getUserSession().removeObject(resultsKey);
                GlobalVariables.getUserSession().removeObject(resultsKey+".form");
            }

            resultsKey = GlobalVariables.getUserSession().addObjectWithGeneratedKey(resultTable);
            GlobalVariables.getUserSession().addObject(resultsKey+".form", form);
            request.setAttribute(KFSConstants.SEARCH_LIST_REQUEST_KEY, resultsKey);
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
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     *      
     * KRAD Conversion: Lookupable performs customization of the fields and check for additional fields.
     *  
     * Data dictionary is used to retrieve the field properties.
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
        CustomerAgingReportForm lookupForm = (CustomerAgingReportForm) form;

        //String backUrl = lookupForm.getBackLocation() + "?methodToCall=refresh&docFormKey=" + lookupForm.getFormKey();
        String backUrl = getApplicationBaseUrl()+"/portal.do?selectedTab=maintenance";
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
     * KRAD Conversion: Lookupable performs customization of the fields.
     *  
     * Data dictionary is used to retrieve the field properties.
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
        CustomerAgingReportForm customerAgeForm = (CustomerAgingReportForm) form;
        String resultKey = request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY);
        request.setAttribute(KFSConstants.SEARCH_LIST_REQUEST_KEY, resultKey);
        request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS, GlobalVariables.getUserSession().retrieveObject(resultKey));
        CustomerAgingReportForm prevForm = (CustomerAgingReportForm) GlobalVariables.getUserSession().retrieveObject(resultKey+".form");
        customerAgeForm.setTotal0to30(prevForm.getTotal0to30());
        customerAgeForm.setTotal31to60(prevForm.getTotal31to60());
        customerAgeForm.setTotal61to90(prevForm.getTotal61to90());
        customerAgeForm.setTotal91toSYSPR(prevForm.getTotal91toSYSPR());
        customerAgeForm.setTotalSYSPRplus1orMore(prevForm.getTotalSYSPRplus1orMore());
        request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS_SIZE, request.getParameter(KFSConstants.REQUEST_SEARCH_RESULTS_SIZE));

        // TODO: use inheritance instead of this if statement
        if (customerAgeForm.getLookupable().getLookupableHelperService() instanceof CustomerAgingReportLookupableHelperServiceImpl) {
            Object totalsTable = GlobalVariables.getUserSession().retrieveObject(TOTALS_TABLE_KEY);
            request.setAttribute(TOTALS_TABLE_KEY, totalsTable);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


}
