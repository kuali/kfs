/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.web.struts.action;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class handles Actions for lookup flow
 *
 *
 */

public class KualiLookupAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiLookupAction.class);

    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
        if (!(form instanceof LookupForm)) {
            super.checkAuthorization(form, methodToCall);
        } else {
            try {
                Class businessObjectClass = Class.forName(((LookupForm) form).getBusinessObjectClassName());
                if (!KimApiServiceLocator.getPermissionService().isAuthorizedByTemplate(
                        GlobalVariables.getUserSession().getPrincipalId(), KRADConstants.KNS_NAMESPACE,
                        KimConstants.PermissionTemplateNames.LOOK_UP_RECORDS,
                        KRADUtils.getNamespaceAndComponentSimpleName(businessObjectClass),
                        Collections.<String, String>emptyMap())) {
                    throw new AuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalName(),
                            KimConstants.PermissionTemplateNames.LOOK_UP_RECORDS,
                            businessObjectClass.getSimpleName());
                }
            }
            catch (ClassNotFoundException e) {
                LOG.warn("Unable to load BusinessObject class: " + ((LookupForm) form).getBusinessObjectClassName(), e);
                super.checkAuthorization(form, methodToCall);
            }
        }
    }

    private static MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;
    private static DocumentHelperService documentHelperService;
    private static MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
        if (maintenanceDocumentDictionaryService == null) {
            maintenanceDocumentDictionaryService = KNSServiceLocator.getMaintenanceDocumentDictionaryService();
        }
        return maintenanceDocumentDictionaryService;
    }
    private static DocumentHelperService getDocumentHelperService() {
        if (documentHelperService == null) {
            documentHelperService = KNSServiceLocator.getDocumentHelperService();
        }
        return documentHelperService;
    }
    /**
     * Checks if the user can create a document for this business object.  Used to suppress the actions on the results.
     *
     * @param form
     * @return
     * @throws ClassNotFoundException
     */
    protected void supressActionsIfNeeded( ActionForm form ) throws ClassNotFoundException {
        if ((form instanceof LookupForm) && ( ((LookupForm)form).getBusinessObjectClassName() != null )) {
            Class businessObjectClass = Class.forName( ((LookupForm)form).getBusinessObjectClassName() );
            // check if creating documents is allowed
            String documentTypeName = getMaintenanceDocumentDictionaryService().getDocumentTypeName(businessObjectClass);
            if ((documentTypeName != null) && !getDocumentHelperService().getDocumentAuthorizer(documentTypeName).canInitiate(documentTypeName, GlobalVariables.getUserSession().getPerson())) {
                ((LookupForm)form).setSuppressActions( true );
            }
        }
    }

    /**
     * This method hides the criteria if set in parameter or lookupable
     *
     * @param form
     */
    private void setCriteriaEnabled(ActionForm form) {
         LookupForm lookupForm = (LookupForm) form;
         if(lookupForm.isLookupCriteriaEnabled()) {
             //only overide if it's enabled, if disabled don't call lookupable
         }
    }
    /**
     * This method hides actions that are not related to the maintenance (as opposed to supressActionsIfNeeded)
     *
     * @param form
     */
    private void suppressNonMaintActionsIfNeeded(ActionForm form) {
        LookupForm lookupForm = (LookupForm) form;
        if(lookupForm.getLookupable()!=null) {
            if(StringUtils.isNotEmpty(lookupForm.getLookupable().getSupplementalMenuBar())) {
                lookupForm.setSupplementalActionsEnabled(true);
            }

        }
    }

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        LookupForm lookupForm = (LookupForm) form;

        request.setAttribute(KRADConstants.PARAM_MAINTENANCE_VIEW_MODE, KRADConstants.PARAM_MAINTENANCE_VIEW_MODE_LOOKUP);
        supressActionsIfNeeded(form);
        suppressNonMaintActionsIfNeeded(form);
        setCriteriaEnabled(form);

        hideHeaderBarIfNeeded(form, request);

        int numCols = KNSServiceLocator.getBusinessObjectDictionaryService().getLookupNumberOfColumns(
                Class.forName(lookupForm.getBusinessObjectClassName()));
        lookupForm.setNumColumns(numCols);

        ActionForward forward = super.execute(mapping, form, request, response);

        // apply conditional logic after all setting of field values has been completed
        lookupForm.getLookupable().applyConditionalLogicForFieldDisplay();

        return forward;
    }

    private void hideHeaderBarIfNeeded(ActionForm form, HttpServletRequest request) {
        if (!((LookupForm) form).isHeaderBarEnabled()) {
            ((LookupForm) form).setHeaderBarEnabled(false);
        }
    }


    /**
     * Entry point to lookups, forwards to jsp for search render.
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * search - sets the values of the data entered on the form on the jsp into a map and then searches for the results.
     */
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LookupForm lookupForm = (LookupForm) form;


        String methodToCall = findMethodToCall(form, request);
        if (methodToCall.equalsIgnoreCase("search")) {
            GlobalVariables.getUserSession().removeObjectsByPrefix(KRADConstants.SEARCH_METHOD);
        }



        Lookupable kualiLookupable = lookupForm.getLookupable();
        if (kualiLookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        Collection displayList = new ArrayList();
        ArrayList<ResultRow> resultTable = new ArrayList<ResultRow>();

        // validate search parameters
        kualiLookupable.validateSearchParameters(lookupForm.getFields());

        boolean bounded = true;

        displayList = kualiLookupable.performLookup(lookupForm, resultTable, bounded);

        if (kualiLookupable.isSearchUsingOnlyPrimaryKeyValues()) {
            lookupForm.setSearchUsingOnlyPrimaryKeyValues(true);
            lookupForm.setPrimaryKeyFieldLabels(kualiLookupable.getPrimaryKeyFieldLabels());
        }
        else {
            lookupForm.setSearchUsingOnlyPrimaryKeyValues(false);
            lookupForm.setPrimaryKeyFieldLabels(KRADConstants.EMPTY_STRING);
        }

        if ( displayList instanceof CollectionIncomplete ){
            request.setAttribute("reqSearchResultsActualSize", ((CollectionIncomplete) displayList).getActualSizeIfTruncated());
        } else {
            request.setAttribute("reqSearchResultsActualSize", displayList.size() );
        }
        
        int resultsLimit = LookupUtils.getSearchResultsLimit(Class.forName(lookupForm.getBusinessObjectClassName()));
        request.setAttribute("reqSearchResultsLimitedSize", resultsLimit);

        // Determine if at least one table entry has an action available. If any non-breaking space (&nbsp; or '\u00A0') characters
        // exist in the URL's value, they will be converted to regular whitespace ('\u0020').
        boolean hasActionUrls = false;
        for (Iterator<ResultRow> iterator = resultTable.iterator(); !hasActionUrls && iterator.hasNext();) {
            if (StringUtils.isNotBlank(HtmlUtils.htmlUnescape(iterator.next().getActionUrls()).replace('\u00A0', '\u0020'))) {
                hasActionUrls = true;
            }
        }
        lookupForm.setActionUrlsExist(hasActionUrls);

        request.setAttribute("reqSearchResults", resultTable);

        if (request.getParameter(KRADConstants.SEARCH_LIST_REQUEST_KEY) != null) {
            GlobalVariables.getUserSession().removeObject(request.getParameter(KRADConstants.SEARCH_LIST_REQUEST_KEY));
        }

        request.setAttribute(KRADConstants.SEARCH_LIST_REQUEST_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(resultTable, KRADConstants.SEARCH_LIST_KEY_PREFIX));

        request.getParameter(KRADConstants.REFRESH_CALLER);

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }


    /**
     * refresh - is called when one quickFinder returns to the previous one. Sets all the values and performs the new search.
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LookupForm lookupForm = (LookupForm) form;
        Lookupable kualiLookupable = lookupForm.getLookupable();
        if (kualiLookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        if(StringUtils.equals(lookupForm.getRefreshCaller(),"customLookupAction")) {
            return this.customLookupableMethodCall(mapping, lookupForm, request, response);
        }

        Map<String, String> fieldValues = new HashMap();
        Map<String, String> values = lookupForm.getFields();

        for (Row row: kualiLookupable.getRows()) {
            for (Field field: row.getFields()) {
                if (field.getPropertyName() != null && !field.getPropertyName().equals("")) {
                    if (request.getParameter(field.getPropertyName()) != null) {
                        if(!Field.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType())) {
                            field.setPropertyValue(request.getParameter(field.getPropertyName()));
                        } else {
                            //multi value, set to values
                            field.setPropertyValues(request.getParameterValues(field.getPropertyName()));
                        }
                    }
                }
                else if (values.get(field.getPropertyName()) != null) {
                    field.setPropertyValue(values.get(field.getPropertyName()));
                }

                kualiLookupable.applyFieldAuthorizationsFromNestedLookups(field);

                fieldValues.put(field.getPropertyName(), field.getPropertyValue());
            }
        }
        fieldValues.put("docFormKey", lookupForm.getFormKey());
        fieldValues.put("backLocation", lookupForm.getBackLocation());
        fieldValues.put("docNum", lookupForm.getDocNum());

        if (kualiLookupable.checkForAdditionalFields(fieldValues)) {
            for (Row row: kualiLookupable.getRows()) {
                for (Object element : row.getFields()) {
                    Field field = (Field) element;
                    if (field.getPropertyName() != null && !field.getPropertyName().equals("")) {
                        if (request.getParameter(field.getPropertyName()) != null) {
//                            field.setPropertyValue(request.getParameter(field.getPropertyName()));
                            if(!Field.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType())) {
                                field.setPropertyValue(request.getParameter(field.getPropertyName()));
                            } else {
                                //multi value, set to values
                                field.setPropertyValues(request.getParameterValues(field.getPropertyName()));
                            }
                            //FIXME: any reason this is inside this "if" instead of the outer one, like above - this seems inconsistent
                            fieldValues.put(field.getPropertyName(), request.getParameter(field.getPropertyName()));
                        }
                        else if (values.get(field.getPropertyName()) != null) {
                            field.setPropertyValue(values.get(field.getPropertyName()));
                        }
                    }
                }
            }
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * Just returns as if return with no value was selected.
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LookupForm lookupForm = (LookupForm) form;

        String backUrl = lookupForm.getBackLocation() + "?methodToCall=refresh&docFormKey=" + lookupForm.getFormKey()+"&docNum="+lookupForm.getDocNum();
        return new ActionForward(backUrl, true);
    }


    /**
     * clearValues - clears the values of all the fields on the jsp.
     */
    public ActionForward clearValues(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        LookupForm lookupForm = (LookupForm) form;
        Lookupable kualiLookupable = lookupForm.getLookupable();
        if (kualiLookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        kualiLookupable.performClear(lookupForm);


        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }


    public ActionForward viewResults(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LookupForm lookupForm = (LookupForm) form;
        if (lookupForm.isSearchUsingOnlyPrimaryKeyValues()) {
            lookupForm.setPrimaryKeyFieldLabels(lookupForm.getLookupable().getPrimaryKeyFieldLabels());
        }
        request.setAttribute(KRADConstants.SEARCH_LIST_REQUEST_KEY, request.getParameter(KRADConstants.SEARCH_LIST_REQUEST_KEY));
        request.setAttribute("reqSearchResults", GlobalVariables.getUserSession().retrieveObject(request.getParameter(
                KRADConstants.SEARCH_LIST_REQUEST_KEY)));
        request.setAttribute("reqSearchResultsActualSize", request.getParameter("reqSearchResultsActualSize"));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward customLookupableMethodCall(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
//      lookupableMethodToCall
        Lookupable kualiLookupable = ((LookupForm)form).getLookupable();
        if (kualiLookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        boolean ignoreErrors=false;
        if(StringUtils.equals(((LookupForm)form).getRefreshCaller(),"customLookupAction")) {
            ignoreErrors=true;
        }

        if(kualiLookupable.performCustomAction(ignoreErrors)) {
            //redo the search if the method comes back
            return search(mapping, form, request, response);
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);

    }

}
