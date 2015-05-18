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

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.struts.form.pojo.PojoForm;
import org.kuali.rice.kns.web.struts.form.pojo.PojoFormBase;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.UrlFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * <p>The base {@link org.apache.struts.action.Action} class for all KNS-based Actions. Extends from the standard 
 * {@link DispatchAction} which allows for a <i>methodToCall</i> request parameter to
 * be used to indicate which method to invoke.</p>
 * 
 * <p>This Action overrides #execute to set methodToCall for image submits.  Also performs other setup
 * required for KNS framework calls.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class KualiAction extends DispatchAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiAction.class);

    private static KualiModuleService kualiModuleService = null;
    private static BusinessObjectAuthorizationService businessObjectAuthorizationService = null;
    private static EncryptionService encryptionService = null;
    private static Boolean OUTPUT_ENCRYPTION_WARNING = null;
    private static String applicationBaseUrl = null;
    
    private Set<String> methodToCallsToNotCheckAuthorization = new HashSet<String>();
    
    {
        methodToCallsToNotCheckAuthorization.add( "performLookup" );
        methodToCallsToNotCheckAuthorization.add( "performQuestion" );
        methodToCallsToNotCheckAuthorization.add( "performQuestionWithInput" );
        methodToCallsToNotCheckAuthorization.add( "performQuestionWithInputAgainBecauseOfErrors" );
        methodToCallsToNotCheckAuthorization.add( "performQuestionWithoutInput" );
        methodToCallsToNotCheckAuthorization.add( "performWorkgroupLookup" );
    }
    
    /**
     * Entry point to all actions.
     *
     * NOTE: No need to hook into execute for handling framework setup anymore. Just implement the methodToCall for the framework
     * setup, Constants.METHOD_REQUEST_PARAMETER will contain the full parameter, which can be sub stringed for getting framework
     * parameters.
     *
     * @see org.apache.struts.action.Action#execute(ActionMapping, ActionForm,
     *      HttpServletRequest, HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward returnForward = null;

        String methodToCall = findMethodToCall(form, request);
        
        if(isModuleLocked(form, methodToCall, request)) {
            return mapping.findForward(RiceConstants.MODULE_LOCKED_MAPPING);
        }
        
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getMethodToCall())) {
            if (StringUtils.isNotBlank(getImageContext(request, KRADConstants.ANCHOR))) {
                ((KualiForm) form).setAnchor(getImageContext(request, KRADConstants.ANCHOR));
            }
            else if (StringUtils.isNotBlank(request.getParameter(KRADConstants.ANCHOR))) {
                ((KualiForm) form).setAnchor(request.getParameter(KRADConstants.ANCHOR));
            }
            else {
                ((KualiForm) form).setAnchor(KRADConstants.ANCHOR_TOP_OF_FORM);
            }
        }
        // if found methodToCall, pass control to that method, else return the basic forward
        if (StringUtils.isNotBlank(methodToCall)) {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("methodToCall: '" + methodToCall+"'");
            }
            returnForward = dispatchMethod(mapping, form, request, response, methodToCall);
            if ( returnForward!=null && returnForward.getRedirect() && returnForward.getName()!=null && returnForward.getName().equals(KRADConstants.KRAD_INITIATED_DOCUMENT_VIEW_NAME)) {
                return returnForward;
            }
        }
        else {
            returnForward = defaultDispatch(mapping, form, request, response);
        }

        // make sure the user can do what they're trying to according to the module that owns the functionality
        if ( !methodToCallsToNotCheckAuthorization.contains(methodToCall) ) {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "'" + methodToCall + "' not in set of excempt methods: " + methodToCallsToNotCheckAuthorization);
            }
            checkAuthorization(form, methodToCall);
        } else {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("'" + methodToCall + "' is exempt from auth checks." );
            }
        }

        // Add the ActionForm to GlobalVariables
        // This will allow developers to retrieve both the Document and any request parameters that are not
        // part of the Form and make them available in ValueFinder classes and other places where they are needed.
        if(KNSGlobalVariables.getKualiForm() == null) {
            KNSGlobalVariables.setKualiForm((KualiForm)form);
        }

        return returnForward;
    }
    
    /**
     * When no methodToCall is specified, the defaultDispatch method is invoked.  Default implementation
     * returns the "basic" ActionForward.
     */
    protected ActionForward defaultDispatch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    @Override
    protected ActionForward dispatchMethod(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String methodToCall) throws Exception {
        GlobalVariables.getUserSession().addObject(DocumentAuthorizerBase.USER_SESSION_METHOD_TO_CALL_OBJECT_KEY, methodToCall);
        return super.dispatchMethod(mapping, form, request, response, methodToCall);
    }
    
    protected String findMethodToCall(ActionForm form, HttpServletRequest request) throws Exception {
        String methodToCall;
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getMethodToCall())) {
            methodToCall = ((KualiForm) form).getMethodToCall();
        }
        else {
            // call utility method to parse the methodToCall from the request.
            methodToCall = WebUtils.parseMethodToCall(form, request);
        }
        return methodToCall;
    }

    /**
     * Toggles the tab state in the ui
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward toggleTab(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiForm kualiForm = (KualiForm) form;
        String tabToToggle = getTabToToggle(request);
        if (StringUtils.isNotBlank(tabToToggle)) {
            if (kualiForm.getTabState(tabToToggle).equals(KualiForm.TabState.OPEN.name())) {
                kualiForm.getTabStates().remove(tabToToggle);
                kualiForm.getTabStates().put(tabToToggle, KualiForm.TabState.CLOSE.name());
            }
            else {
                kualiForm.getTabStates().remove(tabToToggle);
                kualiForm.getTabStates().put(tabToToggle, KualiForm.TabState.OPEN.name());
            }
        }

        doProcessingAfterPost( kualiForm, request );
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * Toggles all tabs to open
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward showAllTabs(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return this.doTabOpenOrClose(mapping, form, request, response, true);
    }

    /**
     * Toggles all tabs to closed
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward hideAllTabs(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return this.doTabOpenOrClose(mapping, form, request, response, false);
    }
    
    /**
     * 
     * Toggles all tabs to open of closed depending on the boolean flag.
     * 
     * @param mapping the mapping
     * @param form the form
     * @param request the request
     * @param response the response
     * @param open whether to open of close the tabs
     * @return the action forward
     */
    private ActionForward doTabOpenOrClose(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, boolean open) {
        KualiForm kualiForm = (KualiForm) form;

        Map<String, String> tabStates = kualiForm.getTabStates();
        Map<String, String> newTabStates = new HashMap<String, String>();
        for (String tabKey: tabStates.keySet()) {
            newTabStates.put(tabKey, open ? "OPEN" : "CLOSE");
        }
        kualiForm.setTabStates(newTabStates);
        doProcessingAfterPost( kualiForm, request );
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * Default refresh method. Called from returning frameworks.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }


    /**
     * Parses the method to call attribute to pick off the line number which should be deleted.
     *
     * @param request
     * @return
     */
    protected int getLineToDelete(HttpServletRequest request) {
        return getSelectedLine(request);
    }

    /**
     * Parses the method to call attribute to pick off the line number which should be edited.
     *
     * @param request
     * @return
     */
    protected int getLineToEdit(HttpServletRequest request) {
        return getSelectedLine(request);
    }

    /**
     * Parses the method to call attribute to pick off the line number which should have an action performed on it.
     *
     * @param request
     * @return
     */
    protected int getSelectedLine(HttpServletRequest request) {
        int selectedLine = -1;
        String parameterName = (String) request.getAttribute(KRADConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            String lineNumber = StringUtils.substringBetween(parameterName, ".line", ".");
            if (StringUtils.isEmpty(lineNumber)) {
                return selectedLine;
            }
            selectedLine = Integer.parseInt(lineNumber);
        }

        return selectedLine;
    }

    /**
     * Determines which tab was requested to be toggled
     *
     * @param request
     * @return
     */
    protected String getTabToToggle(HttpServletRequest request) {
        String tabToToggle = "";
        String parameterName = (String) request.getAttribute(KRADConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            tabToToggle = StringUtils.substringBetween(parameterName, ".tab", ".");
        }

        return tabToToggle;
    }

    /**
     * Retrieves the header tab to navigate to.
     *
     * @param request
     * @return
     */
    protected String getHeaderTabNavigateTo(HttpServletRequest request) {
        String headerTabNavigateTo = RiceConstants.MAPPING_BASIC;
        String imageContext = getImageContext(request, KRADConstants.NAVIGATE_TO);
        if (StringUtils.isNotBlank(imageContext)) {
            headerTabNavigateTo = imageContext;
        }
        return headerTabNavigateTo;
    }

    /**
     * Retrieves the header tab dispatch.
     *
     * @param request
     * @return
     */
    protected String getHeaderTabDispatch(HttpServletRequest request) {
        String headerTabDispatch = null;
        String imageContext = getImageContext(request, KRADConstants.HEADER_DISPATCH);
        if (StringUtils.isNotBlank(imageContext)) {
            headerTabDispatch = imageContext;
        }
        else {
            // In some cases it might be in request params instead
            headerTabDispatch = request.getParameter(KRADConstants.METHOD_TO_CALL_ATTRIBUTE);
        }
        return headerTabDispatch;
    }

    /**
     * Retrieves the image context
     *
     * @param request
     * @param contextKey
     * @return
     */
    protected String getImageContext(HttpServletRequest request, String contextKey) {
        String imageContext = "";
        String parameterName = (String) request.getAttribute(KRADConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isBlank(parameterName)) {
            parameterName = request.getParameter("methodToCallPath");
        }
        if (StringUtils.isNotBlank(parameterName)) {
            imageContext = StringUtils.substringBetween(parameterName, contextKey, ".");
        }
        return imageContext;
    }

    protected String getReturnLocation(HttpServletRequest request, ActionMapping mapping) {
        String mappingPath = mapping.getPath();
        String basePath = getApplicationBaseUrl();
        return basePath + ("/lookup".equals(mappingPath) || "/maintenance".equals(mappingPath) || "/multipleValueLookup".equals(mappingPath) ? "/kr" : "") + mappingPath + ".do";
    }

    /**
     * Retrieves the value of a parameter to be passed into the lookup or inquiry frameworks.  The default implementation of this method will attempt to look
     * in the request to determine wheter the appropriate value exists as a request parameter.  If not, it will attempt to look through the form object to find
     * the property.
     * 
     * @param boClass a class implementing boClass, representing the BO that will be looked up
     * @param parameterName the name of the parameter
     * @param parameterValuePropertyName the property (relative to the form object) where the value to be passed into the lookup/inquiry may be found
     * @param form
     * @param request
     * @return
     */
    protected String retrieveLookupParameterValue(Class<? extends BusinessObject> boClass, String parameterName, String parameterValuePropertyName, ActionForm form, HttpServletRequest request) throws Exception {
        String value;
        if (StringUtils.contains(parameterValuePropertyName, "'")) {
            value = StringUtils.replace(parameterValuePropertyName, "'", "");
        } else if (request.getParameterMap().containsKey(parameterValuePropertyName)) {
            value = request.getParameter(parameterValuePropertyName);
        } else if (request.getParameterMap().containsKey(KewApiConstants.DOCUMENT_ATTRIBUTE_FIELD_PREFIX + parameterValuePropertyName)) {
            value = request.getParameter(KewApiConstants.DOCUMENT_ATTRIBUTE_FIELD_PREFIX + parameterValuePropertyName);
        } else {
            if (form instanceof KualiForm) {
                value = ((KualiForm) form).retrieveFormValueForLookupInquiryParameters(parameterName, parameterValuePropertyName);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Unable to retrieve lookup/inquiry parameter value for parameter name " + parameterName + " parameter value property " + parameterValuePropertyName);
                }
                value = null;
            }
        }
        
        if (value != null && boClass != null && getBusinessObjectAuthorizationService().attributeValueNeedsToBeEncryptedOnFormsAndLinks(boClass, parameterName)) {
            if(CoreApiServiceLocator.getEncryptionService().isEnabled()) {
                value = getEncryptionService().encrypt(value) + EncryptionService.ENCRYPTION_POST_PREFIX;
            }
        }
        return value;
    }
    
    /**
     * Takes care of storing the action form in the User session and forwarding to the lookup action.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // parse out the important strings from our methodToCall parameter
        String fullParameter = (String) request.getAttribute(KRADConstants.METHOD_TO_CALL_ATTRIBUTE);
        validateLookupInquiryFullParameter(request, form, fullParameter);
        
        KualiForm kualiForm = (KualiForm) form;
        
        // when we return from the lookup, our next request's method to call is going to be refresh
        kualiForm.registerEditableProperty(KRADConstants.DISPATCH_REQUEST_PARAMETER);
        
        // parse out the baseLookupUrl if there is one
        String baseLookupUrl = StringUtils.substringBetween(fullParameter, KRADConstants.METHOD_TO_CALL_PARM14_LEFT_DEL, KRADConstants.METHOD_TO_CALL_PARM14_RIGHT_DEL);
        
        // parse out business object class name for lookup
        String boClassName = StringUtils.substringBetween(fullParameter, KRADConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL, KRADConstants.METHOD_TO_CALL_BOPARM_RIGHT_DEL);
        if (StringUtils.isBlank(boClassName)) {
            throw new RuntimeException("Illegal call to perform lookup, no business object class name specified.");
        }
        Class boClass = null;

        try{
            boClass = Class.forName(boClassName);
        } catch(ClassNotFoundException cnfex){
            if ((StringUtils.isNotEmpty(baseLookupUrl) && baseLookupUrl.startsWith(getApplicationBaseUrl() + "/kr/"))
                    || StringUtils.isEmpty(baseLookupUrl)) {
                throw new IllegalArgumentException("The class (" + boClassName + ") cannot be found by this particular "
                    + "application. " + "ApplicationBaseUrl: " + getApplicationBaseUrl()
                    + " ; baseLookupUrl: " + baseLookupUrl);
            }  else {
                LOG.info("The class (" + boClassName + ") cannot be found by this particular application. "
                   + "ApplicationBaseUrl: " + getApplicationBaseUrl() + " ; baseLookupUrl: " + baseLookupUrl);
            }
        }
        
        // build the parameters for the lookup url
        Properties parameters = new Properties();
        String conversionFields = StringUtils.substringBetween(fullParameter, KRADConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, KRADConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);
        if (StringUtils.isNotBlank(conversionFields)) {
            parameters.put(KRADConstants.CONVERSION_FIELDS_PARAMETER, conversionFields);
            
            // register each of the destination parameters of the field conversion string as editable
            String[] fieldConversions = conversionFields.split(KRADConstants.FIELD_CONVERSIONS_SEPARATOR);
            for (String fieldConversion : fieldConversions) {
                String destination = fieldConversion.split(KRADConstants.FIELD_CONVERSION_PAIR_SEPARATOR, 2)[1];
                kualiForm.registerEditableProperty(destination);
            }
        }

        // pass values from form that should be pre-populated on lookup search
        String parameterFields = StringUtils.substringBetween(fullParameter, KRADConstants.METHOD_TO_CALL_PARM2_LEFT_DEL, KRADConstants.METHOD_TO_CALL_PARM2_RIGHT_DEL);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "fullParameter: " + fullParameter );
            LOG.debug( "parameterFields: " + parameterFields );
        }
        if (StringUtils.isNotBlank(parameterFields)) {
            String[] lookupParams = parameterFields.split(KRADConstants.FIELD_CONVERSIONS_SEPARATOR);
            if ( LOG.isDebugEnabled() ) {
                 LOG.debug( "lookupParams: " + Arrays.toString(lookupParams) ); 
            }
            for (String lookupParam : lookupParams) {
                String[] keyValue = lookupParam.split(KRADConstants.FIELD_CONVERSION_PAIR_SEPARATOR, 2);
                if (keyValue.length != 2) {
                    throw new RuntimeException("malformed field conversion pair: " + Arrays.toString(keyValue));
                } 

                String lookupParameterValue = retrieveLookupParameterValue(boClass, keyValue[1], keyValue[0], form, request);
                if (StringUtils.isNotBlank(lookupParameterValue)) {
                    parameters.put(keyValue[1], lookupParameterValue);
                }

                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( "keyValue[0]: " + keyValue[0] );
                    LOG.debug( "keyValue[1]: " + keyValue[1] );
                }
            }
        }

        // pass values from form that should be read-Only on lookup search
        String readOnlyFields = StringUtils.substringBetween(fullParameter, KRADConstants.METHOD_TO_CALL_PARM8_LEFT_DEL, KRADConstants.METHOD_TO_CALL_PARM8_RIGHT_DEL);
        if (StringUtils.isNotBlank(readOnlyFields)) {
            parameters.put(KRADConstants.LOOKUP_READ_ONLY_FIELDS, readOnlyFields);
        }

        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "fullParameter: " + fullParameter );
            LOG.debug( "readOnlyFields: " + readOnlyFields );
        }

        // grab whether or not the "return value" link should be hidden or not
        String hideReturnLink = StringUtils.substringBetween(fullParameter, KRADConstants.METHOD_TO_CALL_PARM3_LEFT_DEL, KRADConstants.METHOD_TO_CALL_PARM3_RIGHT_DEL);
        if (StringUtils.isNotBlank(hideReturnLink)) {
            parameters.put(KRADConstants.HIDE_LOOKUP_RETURN_LINK, hideReturnLink);
        }

        // add the optional extra button source and parameters string
        String extraButtonSource = StringUtils.substringBetween(fullParameter, KRADConstants.METHOD_TO_CALL_PARM4_LEFT_DEL, KRADConstants.METHOD_TO_CALL_PARM4_RIGHT_DEL);
        if (StringUtils.isNotBlank(extraButtonSource)) {
            parameters.put(KRADConstants.EXTRA_BUTTON_SOURCE, extraButtonSource);
        }
        String extraButtonParams = StringUtils.substringBetween(fullParameter, KRADConstants.METHOD_TO_CALL_PARM5_LEFT_DEL, KRADConstants.METHOD_TO_CALL_PARM5_RIGHT_DEL);
        if (StringUtils.isNotBlank(extraButtonParams)) {
            parameters.put(KRADConstants.EXTRA_BUTTON_PARAMS, extraButtonParams);
        }

        String lookupAction = KRADConstants.LOOKUP_ACTION;

        // is this a multi-value return?
        boolean isMultipleValue = false;
        String multipleValues = StringUtils.substringBetween(fullParameter, KRADConstants.METHOD_TO_CALL_PARM6_LEFT_DEL, KRADConstants.METHOD_TO_CALL_PARM6_RIGHT_DEL);
        if ((new Boolean(multipleValues).booleanValue())) {
            parameters.put(KRADConstants.MULTIPLE_VALUE, multipleValues);
            lookupAction = KRADConstants.MULTIPLE_VALUE_LOOKUP_ACTION;
            isMultipleValue = true;
        }

        // the name of the collection being looked up (primarily for multivalue lookups
        String lookedUpCollectionName = StringUtils.substringBetween(fullParameter, KRADConstants.METHOD_TO_CALL_PARM11_LEFT_DEL, KRADConstants.METHOD_TO_CALL_PARM11_RIGHT_DEL);
        if (StringUtils.isNotBlank(lookedUpCollectionName)) {
            parameters.put(KRADConstants.LOOKED_UP_COLLECTION_NAME, lookedUpCollectionName);
        }

        // grab whether or not the "supress actions" column should be hidden or not
        String supressActions = StringUtils.substringBetween(fullParameter, KRADConstants.METHOD_TO_CALL_PARM7_LEFT_DEL, KRADConstants.METHOD_TO_CALL_PARM7_RIGHT_DEL);
        if (StringUtils.isNotBlank(supressActions)) {
            parameters.put(KRADConstants.SUPPRESS_ACTIONS, supressActions);
        }

        // grab the references that should be refreshed upon returning from the lookup
        String referencesToRefresh = StringUtils.substringBetween(fullParameter, KRADConstants.METHOD_TO_CALL_PARM10_LEFT_DEL, KRADConstants.METHOD_TO_CALL_PARM10_RIGHT_DEL);
        if (StringUtils.isNotBlank(referencesToRefresh)) {
            parameters.put(KRADConstants.REFERENCES_TO_REFRESH, referencesToRefresh);
        }

        // anchor, if it exists
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            parameters.put(KRADConstants.LOOKUP_ANCHOR, ((KualiForm) form).getAnchor());
        }

        // now add required parameters
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, "start");

        // pass value from form that shows if autoSearch is desired for lookup search
        String autoSearch = StringUtils.substringBetween(fullParameter, KRADConstants.METHOD_TO_CALL_PARM9_LEFT_DEL, KRADConstants.METHOD_TO_CALL_PARM9_RIGHT_DEL);

        if (StringUtils.isNotBlank(autoSearch)) {
            parameters.put(KRADConstants.LOOKUP_AUTO_SEARCH, autoSearch);
            if ("YES".equalsIgnoreCase(autoSearch)){
                parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, "search");
            }
        }

        parameters.put(KRADConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(form));
        parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, boClassName);

        parameters.put(KRADConstants.RETURN_LOCATION_PARAMETER, getReturnLocation(request, mapping));
        
        if (form instanceof KualiDocumentFormBase) {
            String docNum = ((KualiDocumentFormBase) form).getDocument().getDocumentNumber();
            if(docNum != null){
                parameters.put(KRADConstants.DOC_NUM, docNum);
            }
        }else if(form instanceof LookupForm){
            String docNum = ((LookupForm) form).getDocNum();
            if(docNum != null){
                parameters.put(KRADConstants.DOC_NUM, ((LookupForm) form).getDocNum());
            }
        }

        if (boClass != null) {
            ModuleService responsibleModuleService = getKualiModuleService().getResponsibleModuleService(boClass);
            if(responsibleModuleService!=null && responsibleModuleService.isExternalizable(boClass)){
                Map<String, String> parameterMap = new HashMap<String, String>();
                Enumeration<Object> e = parameters.keys();
                while (e.hasMoreElements()) {
                    String paramName = (String) e.nextElement();
                    parameterMap.put(paramName, parameters.getProperty(paramName));
                }
                return new ActionForward(responsibleModuleService.getExternalizableBusinessObjectLookupUrl(boClass, parameterMap), true);
            }
        }
        
        if (StringUtils.isBlank(baseLookupUrl)) {
            baseLookupUrl = getApplicationBaseUrl() + "/kr/" + lookupAction;
        } else {
            if (isMultipleValue) {
                LookupUtils.transformLookupUrlToMultiple(baseLookupUrl);
            }
        }
        String lookupUrl = UrlFactory.parameterizeUrl(baseLookupUrl, parameters);
        return new ActionForward(lookupUrl, true);
    }

    protected void validateLookupInquiryFullParameter(HttpServletRequest request, ActionForm form, String fullParameter){
        PojoFormBase pojoFormBase = (PojoFormBase) form;
        if(WebUtils.isFormSessionDocument((PojoFormBase) form)){
            if(!pojoFormBase.isPropertyEditable(fullParameter)) {
                throw new RuntimeException("The methodToCallAttribute is not registered as an editable property.");
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public ActionForward performInquiry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // parse out the important strings from our methodToCall parameter
        String fullParameter = (String) request.getAttribute(KRADConstants.METHOD_TO_CALL_ATTRIBUTE);
        validateLookupInquiryFullParameter(request, form, fullParameter);
        
        // when javascript is disabled, the inquiry will appear in the same window as the document.  when we close the inquiry, 
        // our next request's method to call is going to be refresh
        KualiForm kualiForm = (KualiForm) form;
        kualiForm.registerEditableProperty(KRADConstants.DISPATCH_REQUEST_PARAMETER);
        
        // parse out business object class name for lookup
        String boClassName = StringUtils.substringBetween(fullParameter, KRADConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL, KRADConstants.METHOD_TO_CALL_BOPARM_RIGHT_DEL);
        if (StringUtils.isBlank(boClassName)) {
            throw new RuntimeException("Illegal call to perform inquiry, no business object class name specified.");
        }

        // build the parameters for the inquiry url
        Properties parameters = new Properties();
        parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, boClassName);

        parameters.put(KRADConstants.RETURN_LOCATION_PARAMETER, getReturnLocation(request, mapping));

        // pass values from form that should be pre-populated on inquiry
        String parameterFields = StringUtils.substringBetween(fullParameter, KRADConstants.METHOD_TO_CALL_PARM2_LEFT_DEL, KRADConstants.METHOD_TO_CALL_PARM2_RIGHT_DEL);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "fullParameter: " + fullParameter );
            LOG.debug( "parameterFields: " + parameterFields );
        }
        if (StringUtils.isNotBlank(parameterFields)) {
            // TODO : create a method for this to be used by both lookup & inquiry ?
            String[] inquiryParams = parameterFields.split(KRADConstants.FIELD_CONVERSIONS_SEPARATOR);
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "inquiryParams: " + inquiryParams );
            }
            Class<? extends BusinessObject> boClass = (Class<? extends BusinessObject>) Class.forName(boClassName);
            for (String inquiryParam : inquiryParams) {
                String[] keyValue = inquiryParam.split(KRADConstants.FIELD_CONVERSION_PAIR_SEPARATOR, 2);

                String inquiryParameterValue = retrieveLookupParameterValue(boClass, keyValue[1], keyValue[0], form, request);
                if (inquiryParameterValue == null) {
                    parameters.put(keyValue[1], "directInquiryKeyNotSpecified");
                }
                else {
                    parameters.put(keyValue[1], inquiryParameterValue);
                }

                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( "keyValue[0]: " + keyValue[0] );
                    LOG.debug( "keyValue[1]: " + keyValue[1] );
                }
            }
        }
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, "start");
        parameters.put(KRADConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(form));
        String inquiryUrl = null;
        try {
            Class.forName(boClassName);
            inquiryUrl = getApplicationBaseUrl() + "/kr/" + KRADConstants.DIRECT_INQUIRY_ACTION;
        } catch ( ClassNotFoundException ex ) {
            // allow inquiry url to be null (and therefore no inquiry link will be displayed) but at least log a warning
            LOG.warn("Class name does not represent a valid class which this application understands: " + boClassName);
        }
        inquiryUrl = UrlFactory.parameterizeUrl(inquiryUrl, parameters);
        return new ActionForward(inquiryUrl, true);

    }

    /**
     * This method handles rendering the question component, but without any of the extra error fields
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @param questionId
     * @param questionText
     * @param questionType
     * @param caller
     * @param context
     * @return ActionForward
     * @throws Exception
     */
    protected ActionForward performQuestionWithoutInput(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionId, String questionText, String questionType, String caller, String context) throws Exception {
        return performQuestion(mapping, form, request, response, questionId, questionText, questionType, caller, context, false, "", "", "", "");
    }

    /**
     * Handles rendering a question prompt - without a specified context.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @param questionId
     * @param questionText
     * @param questionType
     * @param caller
     * @param context
     * @return ActionForward
     * @throws Exception
     */
    protected ActionForward performQuestionWithInput(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionId, String questionText, String questionType, String caller, String context) throws Exception {
        return performQuestion(mapping, form, request, response, questionId, questionText, questionType, caller, context, true, "", "", "", "");
    }

    /**
     * Handles re-rendering a question prompt because of an error on what was submitted.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @param questionId
     * @param questionText
     * @param questionType
     * @param caller
     * @param context
     * @param reason
     * @param errorKey
     * @param errorPropertyName
     * @param errorParameter
     * @return ActionForward
     * @throws Exception
     */
    protected ActionForward performQuestionWithInputAgainBecauseOfErrors(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionId, String questionText, String questionType, String caller, String context, String reason, String errorKey, String errorPropertyName, String errorParameter) throws Exception {
        return performQuestion(mapping, form, request, response, questionId, questionText, questionType, caller, context, true, reason, errorKey, errorPropertyName, errorParameter);
    }

    /**
     * Handles rendering a question prompt - with a specified context.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @param questionId
     * @param questionText
     * @param questionType
     * @param caller
     * @param context
     * @param showReasonField
     * @param reason
     * @param errorKey
     * @param errorPropertyName
     * @param errorParameter
     * @return ActionForward
     * @throws Exception
     */
    private ActionForward performQuestion(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionId, String questionText, String questionType, String caller, String context, boolean showReasonField, String reason, String errorKey, String errorPropertyName, String errorParameter) throws Exception {
        Properties parameters = new Properties();

        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, "start");
        parameters.put(KRADConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(form));
        parameters.put(KRADConstants.CALLING_METHOD, caller);
        parameters.put(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME, questionId);
        parameters.put(KRADConstants.QUESTION_IMPL_ATTRIBUTE_NAME, questionType);
        //parameters.put(KRADConstants.QUESTION_TEXT_ATTRIBUTE_NAME, questionText);
        parameters.put(KRADConstants.RETURN_LOCATION_PARAMETER, getReturnLocation(request, mapping));
        parameters.put(KRADConstants.QUESTION_CONTEXT, context);
        parameters.put(KRADConstants.QUESTION_SHOW_REASON_FIELD, Boolean.toString(showReasonField));
        parameters.put(KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME, reason);
        parameters.put(KRADConstants.QUESTION_ERROR_KEY, errorKey);
        parameters.put(KRADConstants.QUESTION_ERROR_PROPERTY_NAME, errorPropertyName);
        parameters.put(KRADConstants.QUESTION_ERROR_PARAMETER, errorParameter);
        parameters.put(KRADConstants.QUESTION_ANCHOR, form instanceof KualiForm ? ObjectUtils.toString(((KualiForm) form).getAnchor()) : "");
        Object methodToCallAttribute = request.getAttribute(KRADConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (methodToCallAttribute != null) {
            parameters.put(KRADConstants.METHOD_TO_CALL_PATH, methodToCallAttribute);
            ((PojoForm) form).registerEditableProperty(String.valueOf(methodToCallAttribute));
        }
        
        if (form instanceof KualiDocumentFormBase) {
            String docNum = ((KualiDocumentFormBase) form).getDocument().getDocumentNumber();
            if(docNum != null){
                parameters.put(KRADConstants.DOC_NUM, ((KualiDocumentFormBase) form)
                    .getDocument().getDocumentNumber());
            }
        }
        
        // KULRICE-8077: PO Quote Limitation of Only 9 Vendors
        String questionTextAttributeName = KRADConstants.QUESTION_TEXT_ATTRIBUTE_NAME + questionId;
        GlobalVariables.getUserSession().addObject(questionTextAttributeName, (Object)questionText);

        String questionUrl = UrlFactory.parameterizeUrl(getApplicationBaseUrl() + "/kr/" + KRADConstants.QUESTION_ACTION, parameters);
        return new ActionForward(questionUrl, true);
    }


    /**
     * Takes care of storing the action form in the User session and forwarding to the workflow workgroup lookup action.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performWorkgroupLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String returnUrl = null;
        if ("/kr".equals(mapping.getModuleConfig().getPrefix())) {
            returnUrl = getApplicationBaseUrl() + mapping.getModuleConfig().getPrefix() + mapping.getPath() + ".do";
        } else {
            returnUrl = getApplicationBaseUrl() + mapping.getPath() + ".do";
        }


        String fullParameter = (String) request.getAttribute(KRADConstants.METHOD_TO_CALL_ATTRIBUTE);
        String conversionFields = StringUtils.substringBetween(fullParameter, KRADConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, KRADConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);

        String deploymentBaseUrl = KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(
                KRADConstants.WORKFLOW_URL_KEY);
        String workgroupLookupUrl = deploymentBaseUrl + "/Lookup.do?lookupableImplServiceName=WorkGroupLookupableImplService&methodToCall=start&docFormKey=" + GlobalVariables.getUserSession().addObjectWithGeneratedKey(form);

        if (conversionFields != null) {
            workgroupLookupUrl += "&conversionFields=" + conversionFields;
        }
        if (form instanceof KualiDocumentFormBase) {
            workgroupLookupUrl +="&docNum="+ ((KualiDocumentFormBase) form).getDocument().getDocumentNumber();
        }
        
        workgroupLookupUrl += "&returnLocation=" + returnUrl;

        return new ActionForward(workgroupLookupUrl, true);
    }

    /**
     * Handles requests that originate via Header Tabs.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward headerTab(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // header tab actions can do two things - 1, call into an action and perform what needs to happen in there and 2, forward to
        // a new location.
        String headerTabDispatch = getHeaderTabDispatch(request);
        if (StringUtils.isNotEmpty(headerTabDispatch)) {
            ActionForward forward = dispatchMethod(mapping, form, request, response, headerTabDispatch);
            if (GlobalVariables.getMessageMap().getNumberOfPropertiesWithErrors() > 0) {
                return mapping.findForward(RiceConstants.MAPPING_BASIC);
            }
            this.doTabOpenOrClose(mapping, form, request, response, false);
            if (forward.getRedirect()) {
                return forward;
            }
        }
        return dispatchMethod(mapping, form, request, response, getHeaderTabNavigateTo(request));
    }

    /**
     * Override this method to provide action-level access controls to the application.
     *
     * @param form
     * @throws AuthorizationException
     */
    protected void checkAuthorization( ActionForm form, String methodToCall) throws AuthorizationException 
    {
        String principalId = GlobalVariables.getUserSession().getPrincipalId();
        Map<String, String> roleQualifier = new HashMap<String, String>(getRoleQualification(form, methodToCall));
        Map<String, String> permissionDetails = KRADUtils.getNamespaceAndActionClass(this.getClass());
        
        if (!KimApiServiceLocator.getPermissionService().isAuthorizedByTemplate(principalId,
                KRADConstants.KNS_NAMESPACE, KimConstants.PermissionTemplateNames.USE_SCREEN, permissionDetails,
                roleQualifier))
        {
            throw new AuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalName(), 
                    methodToCall,
                    this.getClass().getSimpleName());
        }
    }
    
    /** 
     * override this method to add data from the form for role qualification in the authorization check
     */
    protected Map<String,String> getRoleQualification(ActionForm form, String methodToCall) {
        return new HashMap<String,String>();
    }

    protected static KualiModuleService getKualiModuleService() {
        if ( kualiModuleService == null ) {
            kualiModuleService = KRADServiceLocatorWeb.getKualiModuleService();
        }
        return kualiModuleService;
    }

    /**
     * Constant defined to match with TextArea.jsp and updateTextArea function in core.js
     * <p>Value is textAreaFieldName
     */
    public static final String TEXT_AREA_FIELD_NAME="textAreaFieldName";
    /**
     * Constant defined to match with TextArea.jsp and updateTextArea function in core.js
     * <p>Value is textAreaFieldLabel
    */
    public static final String TEXT_AREA_FIELD_LABEL="textAreaFieldLabel";
    /**
     * Constant defined to match with TextArea.jsp and updateTextArea function in core.js
     * <p>Value is textAreaReadOnly
    */
    public static final String TEXT_AREA_READ_ONLY="textAreaReadOnly";
    /**
     * Constant defined to match with TextArea.jsp and updateTextArea function in core.js
     * <p>Value is textAreaFieldAnchor
    */
    public static final String TEXT_AREA_FIELD_ANCHOR="textAreaFieldAnchor";
    /**
     * Constant defined to match with TextArea.jsp and updateTextArea function in core.js
     * <p>Value is textAreaFieldAnchor
    */
    public static final String TEXT_AREA_MAX_LENGTH="textAreaMaxLength";
    /**
     * Constant defined to match with TextArea.jsp and updateTextArea function in core.js
     * <p>Value is htmlFormAction
    */
    public static final String FORM_ACTION="htmlFormAction";
    /**
     * Constant defined to match input parameter from URL and from TextArea.jsp.
     * <p>Value is methodToCall
    */
    public static final String METHOD_TO_CALL="methodToCall";
    /**
     * Constant defined to match with global forwarding in struts-config.xml
     * for Text Area Update.
     * <p>Value is updateTextArea
    */
    public static final String FORWARD_TEXT_AREA_UPDATE="updateTextArea";
    /**
     * Constant defined to match with method to call in TextArea.jsp.
     * <p>Value is postTextAreaToParent
    */
    public static final String POST_TEXT_AREA_TO_PARENT="postTextAreaToParent";
    /**
     * Constant defined to match with local forwarding in struts-config.xml
     * for the parent of the Updated Text Area.
     * <p>Value is forwardNext
    */
    public static final String FORWARD_NEXT="forwardNext";

    /**
     * This method is invoked when Java Script is turned off from the web browser. It
     * setup the information that the update text area requires for copying current text
     * in the calling page text area and returning to the calling page. The information
     * is passed to the JSP through Http Request attributes. All other parameters are
     * forwarded 
     *  
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward updateTextArea(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)  {
        if (LOG.isTraceEnabled()) {
            String lm=String.format("ENTRY %s%n%s", form.getClass().getSimpleName(),
                    request.getRequestURI());
            LOG.trace(lm);
        }
                                
        final String[] keyValue = getTextAreaParams(request);
        
        request.setAttribute(TEXT_AREA_FIELD_NAME, keyValue[0]);
        request.setAttribute(FORM_ACTION,keyValue[1]);
        request.setAttribute(TEXT_AREA_FIELD_LABEL,keyValue[2]);
        request.setAttribute(TEXT_AREA_READ_ONLY,keyValue[3]);
        request.setAttribute(TEXT_AREA_MAX_LENGTH,keyValue[4]);
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            request.setAttribute(TEXT_AREA_FIELD_ANCHOR,((KualiForm) form).getAnchor());
        }
        
        // Set document related parameter
        String docWebScope=(String)request.getAttribute(KRADConstants.DOCUMENT_WEB_SCOPE);
        if (docWebScope != null && docWebScope.trim().length() >= 0) {
            request.setAttribute(KRADConstants.DOCUMENT_WEB_SCOPE, docWebScope);
        }

        request.setAttribute(KRADConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(form));
        
        ActionForward forward=mapping.findForward(FORWARD_TEXT_AREA_UPDATE);

        if (LOG.isTraceEnabled()) {
            String lm=String.format("EXIT %s", (forward==null)?"null":forward.getPath());
            LOG.trace(lm);
        }
                        
        return forward;
    }
    
    /**
     * This method takes the {@link KRADConstants.METHOD_TO_CALL_ATTRIBUTE} out of the request
     * and parses it returning the required fields needed for a text area. The fields returned
     * are the following in this order.
     * <ol>
     * <li>{@link #TEXT_AREA_FIELD_NAME}</li>
     * <li>{@link #FORM_ACTION}</li>
     * <li>{@link #TEXT_AREA_FIELD_LABEL}</li>
     * <li>{@link #TEXT_AREA_READ_ONLY}</li>
     * <li>{@link #TEXT_AREA_MAX_LENGTH}</li>
     * </ol>
     * 
     * @param request the request to retrieve the textarea parameters
     * @return a string array holding the parsed fields
     */
    private String[] getTextAreaParams(HttpServletRequest request) {
        // parse out the important strings from our methodToCall parameter
        String fullParameter = (String) request.getAttribute(
                KRADConstants.METHOD_TO_CALL_ATTRIBUTE);

        // parse textfieldname:htmlformaction
        String parameterFields = StringUtils.substringBetween(fullParameter,
                KRADConstants.METHOD_TO_CALL_PARM2_LEFT_DEL,
                KRADConstants.METHOD_TO_CALL_PARM2_RIGHT_DEL);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "fullParameter: " + fullParameter );
            LOG.debug( "parameterFields: " + parameterFields );
        }
        String[] keyValue = null;
        if (StringUtils.isNotBlank(parameterFields)) {
            String[] textAreaParams = parameterFields.split(
                    KRADConstants.FIELD_CONVERSIONS_SEPARATOR);
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "lookupParams: " + textAreaParams );
            }
            for (final String textAreaParam : textAreaParams) {
                keyValue = textAreaParam.split(KRADConstants.FIELD_CONVERSION_PAIR_SEPARATOR, 2);

                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( "keyValue[0]: " + keyValue[0] );
                    LOG.debug( "keyValue[1]: " + keyValue[1] );
                    LOG.debug( "keyValue[2]: " + keyValue[2] );
                    LOG.debug( "keyValue[3]: " + keyValue[3] );
                    LOG.debug( "keyValue[4]: " + keyValue[4] );
                }
            }
        }
        
        return keyValue;
    }
    
    /**
     * This method is invoked from the TextArea.jsp for posting its value to the parent
     * page that called the extended text area page. The invocation is done through
     * Struts action. The default forwarding id is RiceContants.MAPPING_BASIC. This
     * can be overridden using the parameter key FORWARD_NEXT.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward postTextAreaToParent(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        if (LOG.isTraceEnabled()) {
            String lm=String.format("ENTRY %s%n%s", form.getClass().getSimpleName(),
                    request.getRequestURI());
            LOG.trace(lm);
        }
                        
        String forwardingId=request.getParameter(FORWARD_NEXT);
        if (forwardingId == null) {
            forwardingId=RiceConstants.MAPPING_BASIC;
        }
        ActionForward forward=mapping.findForward(forwardingId);
             
        if (LOG.isTraceEnabled()) {
            String lm=String.format("EXIT %s", (forward==null)?"null":forward.getPath());
            LOG.trace(lm);
        }
                        
        return forward;
    }
    
    /**
     * Use to add a methodToCall to the a list which will not have authorization checks.
     * This assumes that the call will be redirected (as in the case of a lookup) that will perform
     * the authorization.
     */
    protected final void addMethodToCallToUncheckedList( String methodToCall ) {
        methodToCallsToNotCheckAuthorization.add(methodToCall);
    }
    
    /**
     * This method does all special processing on a document that should happen on each HTTP post (ie, save, route, approve, etc).
     */
    protected void doProcessingAfterPost( KualiForm form, HttpServletRequest request ) {
        
    }
    
    protected BusinessObjectAuthorizationService getBusinessObjectAuthorizationService() {
        if (businessObjectAuthorizationService == null) {
            businessObjectAuthorizationService = KNSServiceLocator.getBusinessObjectAuthorizationService();
        }
        return businessObjectAuthorizationService;
    }
    
    protected EncryptionService getEncryptionService() {
        if (encryptionService == null) {
            encryptionService = CoreApiServiceLocator.getEncryptionService();
        }
        return encryptionService;
    }

    public static String getApplicationBaseUrl() {
        if ( applicationBaseUrl == null ) {
            applicationBaseUrl = KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(
                    KRADConstants.APPLICATION_URL_KEY);
        }
        return applicationBaseUrl;
    }
    
    protected boolean isModuleLocked(ActionForm form, String methodToCall, HttpServletRequest request) {
        String boClass = request.getParameter(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE);
        ModuleService moduleService = null;
        if(StringUtils.isNotBlank(boClass)) {
            try {
                moduleService = getKualiModuleService().getResponsibleModuleService(Class.forName(boClass));
            } catch (ClassNotFoundException classNotFoundException) {
                LOG.warn("BO class not found: " + boClass, classNotFoundException);
            }
        } else {
            moduleService = getKualiModuleService().getResponsibleModuleService(this.getClass());
        }
        if(moduleService != null && moduleService.isLocked()) {
            String principalId = GlobalVariables.getUserSession().getPrincipalId();
            String namespaceCode = KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE;
            String permissionName = KimConstants.PermissionNames.ACCESS_LOCKED_MODULE;
            Map<String, String> qualification = getRoleQualification(form, methodToCall);
            if(!KimApiServiceLocator.getPermissionService().isAuthorized(principalId, namespaceCode, permissionName, qualification)) {
                ParameterService parameterSerivce = CoreFrameworkServiceLocator.getParameterService();
                String messageParamNamespaceCode = moduleService.getModuleConfiguration().getNamespaceCode();
                String messageParamComponentCode = KRADConstants.DetailTypes.ALL_DETAIL_TYPE;
                String messageParamName = KRADConstants.SystemGroupParameterNames.OLTP_LOCKOUT_MESSAGE_PARM;
                String lockoutMessage = parameterSerivce.getParameterValueAsString(messageParamNamespaceCode, messageParamComponentCode, messageParamName);
                
                if(StringUtils.isBlank(lockoutMessage)) {
                    String defaultMessageParamName = KRADConstants.SystemGroupParameterNames.OLTP_LOCKOUT_DEFAULT_MESSAGE;
                    lockoutMessage = parameterSerivce.getParameterValueAsString(KRADConstants.KNS_NAMESPACE, messageParamComponentCode, defaultMessageParamName);
                }
                request.setAttribute(KRADConstants.MODULE_LOCKED_MESSAGE_REQUEST_PARAMETER, lockoutMessage);
                return true;
            }
        }
        return false;
    }
}
