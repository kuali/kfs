/**
 * Copyright 2005-2012 The Kuali Foundation
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
// begin Kuali Foundation modification
package org.kuali.rice.kns.web.struts.form.pojo;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;
import org.kuali.rice.core.web.format.FormatException;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.EditablePropertiesHistoryHolder;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class is the base form which implements the PojoForm interface.
 * Kuali Foundation modification: javadoc comments changed
 */
// begin Kuali Foundation modification: this class was named SLActionForm
public class PojoFormBase extends ActionForm implements PojoForm {
    private static final long serialVersionUID = 1L;

    // begin Kuali Foundation modification
    private static final Logger LOG = Logger.getLogger(PojoFormBase.class);

    private static final String PREVIOUS_REQUEST_EDITABLE_PROPERTIES_GUID = "editablePropertiesGuid";

    // removed member variables: cachedActionErrors, coder, errorInfo, fieldOrder, formConfig, HEADING_KEY, IGNORED_KEYS,
    //     invalidValueKeys, logger, messageResourceKey, messageResources, padNonRequiredFields, valueBinder

    static final String CREATE_ERR_MSG = "Can't create formatter for keypath ";
    static final String CONVERT_ERR_MSG = "Can't convert value for keypath: ";

    static Map classCache = Collections.synchronizedMap(new HashMap());

    private Map unconvertedValues = new HashMap();
    private List unknownKeys = new ArrayList();
    private Map formatterTypes = new HashMap();
    private List<String> maxUploadFileSizes = new ArrayList<String>();
    private Set<String> editableProperties = new HashSet<String>();
    protected Set<String> requiredNonEditableProperties = new HashSet<String>();
    private String strutsActionMappingScope;
    private boolean isNewForm = true;

    private String populateEditablePropertiesGuid;
    private String actionEditablePropertiesGuid;

    // removed methods: PojoFormBase()/SLActionForm(), addFormLevelMessageInfo, addGlobalMessage, addIgnoredKey, addIgnoredKeys, addLengthValidation, addMessageIfAbsent
    //     addPatternValidation, addPropertyValidationRules, addRangeValidation, addRequiredField, addRequiredFields
    //     addUnknownKey, addValidationRule(String, ValidationRule), addValidationRule(ValidationRule), cachedActionErrors, clearIgnoredKeys,
    //     clearUnknownKeys, clearValidationErrors, coalesceMessageArgs, containsKey, convertValue, createActionMessage, createMessageResourcesIfNecessary, fieldOrder, fieldValidationRuleOrder,
    //     formatMessage, formatMessageArgs, formatterSettingsForKeypath, formatterTypeForKeypath, formBeanConfigForKey, formConfig, formValidationRuleOrder,
    //     generateErrorMessages, getActionErrors, getActionMessages, getErrorMessages, getFieldLabel, getFormatterTypes, getGlobalMessages, getIgnoredKeys, getInvalidValueKeys, getLabels, getLengthValidations, getLocale,
    //     getMultipartRequestParameters, getPadNonRequiredFields,
    //     getPatternValidations, getPropertyConfig, getRangeValidations, getRequiredFields, hasErrorMessageForKey, hasErrors, hasFormatterForKeypath,
    //     hasGlobalMessageForKey, isMultipart, messageForKey, messageForRule, messageInfoForRule, messageResourcesConfigForKey, messageResourcesKey, messageResourcesPath,
    //     messagesForFormLevelRule, messagesForKey, moduleConfigForRequest, removeIgnoredKey, removePropertyConfig,
    //     renderErrorMessages, renderGlobalMessages, renderMessages, setFieldLabel, setFieldOrder, setFormatterType(String, Class, Map)
    //     setFormConfig, setInvalidValueKeys, setLengthValidations, setMessageResourceKey,setPadNonRequiredFields, setPatternValidations, setPropertyConfig, setRangeValidations,
    //     setRequiredFields, setValueBinder, shouldFormat, validate, validateForm, validateLength, validatePattern, validateProperty, validateRange, validateRequestValues, validateRequired, valueBinder

    // end Kuali Foundation modification


    // begin Kuali Foundation modification
    /**
     * Method is called after parameters from a multipart request have been made accessible to request.getParameter calls, but
     * before request parameter values are used to instantiate and populate business objects. Important note: parameters in the
     * given Map which were created from a multipart-encoded parameter will, apparently, be stored in the given Map as String[]
     * instead of as String.
     *
     * @param requestParameters
     */

    @Override
    public void postprocessRequestParameters(Map requestParameters) {
        // do nothing
    }
    // end Kuali Foundation modification


    private static final String WATCH_NAME = "PojoFormBase.populate";

    /**
     * Populates the form with values from the current request. Uses instances of Formatter to convert strings to the Java types of
     * the properties to which they are bound. Values that can't be converted are cached in a map of unconverted values. Returns an
     * ActionErrors containing ActionMessage instances for each conversion error that occured, if any.
     */
    @Override
    public void populate(HttpServletRequest request) {

        StopWatch watch = null;
        if (LOG.isDebugEnabled()) {
            watch = new StopWatch();
            watch.start();
            LOG.debug(WATCH_NAME + ": started");
        }
        unconvertedValues.clear();
        unknownKeys = new ArrayList();
        addRequiredNonEditableProperties();
        Map params = request.getParameterMap();

        String contentType = request.getContentType();
        String method = request.getMethod();

        if ("POST".equalsIgnoreCase(method) && contentType != null && contentType.startsWith("multipart/form-data")) {
            Map fileElements = (HashMap)request.getAttribute(KRADConstants.UPLOADED_FILE_REQUEST_ATTRIBUTE_KEY);
            Enumeration names = Collections.enumeration(fileElements.keySet());
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                params.put(name, fileElements.get(name));
            }
        }

        postprocessRequestParameters(params);


        /**
         * Iterate through request parameters, if parameter matches a form variable, get the property type, formatter and convert,
         * if not add to the unknowKeys map.
         */
        Comparator<String> nestedPathComparator = new Comparator<String>() {
            @Override
            public int compare(String prop1, String prop2) {
                Integer i1 =  new Integer(prop1.split("\\.").length);
                Integer i2 =  new Integer(prop2.split("\\.").length);
                return (i1.compareTo(i2));
            }
        };


        List<String> pathKeyList = new ArrayList<String>(params.keySet());
        Collections.sort( pathKeyList , nestedPathComparator);

        for (String keypath : pathKeyList) {
            if (shouldPropertyBePopulatedInForm(keypath, request)) {
                Object param = params.get(keypath);
                //LOG.debug("(keypath,paramType)=(" + keypath + "," + param.getClass().getName() + ")");

                populateForProperty(keypath, param, params);
            }
        }
        this.registerIsNewForm(false);
        if (LOG.isDebugEnabled()) {
            watch.stop();
            LOG.debug(WATCH_NAME + ": " + watch.toString());
        }
    }



    /**
     * Populates a given parameter value into the given property path
     * @param paramPath the path to a property within the form
     * @param paramValue the value of that property
     * @param params the Map of parameters from the request
     */
    public void populateForProperty(String paramPath, Object paramValue,
            Map params) {
        // get type for property
        Class type = null;
        try {
            // TODO: see KULOWF-194
            //testForPojoHack(this, keypath);
            type = getPropertyType(paramPath);
        }
        catch (Exception e) {
            // deleted redundant unknownKeys.add(keypath)
        }

        // keypath does not match anything on form
        if (type == null) {
            unknownKeys.add(paramPath);
        }
        else {
            Formatter formatter = null;
            try {
                formatter = buildFormatter(paramPath, type, params);
                // SR 16387 - added check to guard against problems if the form encoding
                // is not set properly
                if (FormFile.class.isAssignableFrom(type) && !(paramValue instanceof FormFile)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("We received a string instead of a file object during the form post.  This *could* be an issue if the user was attempting to upload a file and the form was not changed into multipart/form-data mode.");
                        LOG.debug("Path:            " + paramPath);
                        LOG.debug("Detected Type:   " + type.getName());
                        LOG.debug("Value:           " + paramValue);
                        if (paramValue != null) {
                            LOG.debug("Value Class:     " + paramValue.getClass().getName());
                        }
                    }
                } else {
                    ObjectUtils.setObjectProperty(formatter, this, paramPath, type, paramValue);
                }
            } catch (FormatException e1) {
                GlobalVariables.getMessageMap().putError(paramPath, e1.getErrorKey(), e1.getErrorArgs());
                cacheUnconvertedValue(paramPath, paramValue);
            }
            catch (InvocationTargetException e1) {
                if (e1.getTargetException().getClass().equals(FormatException.class)) {
                    // Handle occasional case where FormatException is wrapped in an InvocationTargetException
                    FormatException formatException = (FormatException) e1.getTargetException();
                    GlobalVariables.getMessageMap().putError(paramPath, formatException.getErrorKey(), formatException.getErrorArgs());
                    cacheUnconvertedValue(paramPath, paramValue);
                }
                else {
                    LOG.error("Error occurred in populate " + e1.getMessage());
                    throw new RuntimeException(e1.getMessage(), e1);
                }
            }
            catch (Exception e1) {
                LOG.error("Error occurred in populate " + e1.getMessage());
                LOG.error("FormClass:       " + this.getClass().getName() );
                LOG.error("keypath:         " + paramPath );
                LOG.error("Detected Type:   " + type.getName() );
                LOG.error( "Value:          " + paramValue );
                if ( paramValue != null ) {
                    LOG.error( "Value Class:    " + paramValue.getClass().getName() );
                }
                throw new RuntimeException(e1.getMessage(), e1);
            }
        }
    }

    // begin Kuali Foundation modification
    private Formatter buildFormatter(String keypath, Class propertyType, Map requestParams) {
        Formatter formatter = buildFormatterForKeypath(keypath, propertyType, requestParams);
        if (formatter == null) {
            formatter = buildFormatterForType(propertyType);
        }
        return formatter;
    }
    // end Kuali Foundation modification

    // begin Kuali Foundation modification
    private Formatter buildFormatterForKeypath(String keypath, Class propertyType, Map requestParams) {
        Formatter formatter = null;

        Class formatterClass = formatterClassForKeypath(keypath);

        if (formatterClass != null) {
            try {
                formatter = (Formatter) formatterClass.newInstance();
            }
            catch (InstantiationException e) {
                throw new FormatException("unable to instantiate formatter class '" + formatterClass.getName() + "'", e);
            }
            catch (IllegalAccessException e) {
                throw new FormatException("unable to access formatter class '" + formatterClass.getName() + "'", e);
            }
            formatter.setPropertyType(propertyType);
        }
        return formatter;
    }
    // end Kuali Foundation modification

    // begin Kuali Foundation modification
    private Formatter buildFormatterForType(Class propertyType) {
        Formatter formatter = null;

        if (Formatter.findFormatter(propertyType) != null) {
            formatter = Formatter.getFormatter(propertyType);
        }
        return formatter;
    }
    // end Kuali Foundation modification

    /**
     * Delegates to {@link PropertyUtils#getPropertyType(Object, String)}to look up the property type for the provided keypath.
     * Caches the resulting class so that subsequent lookups for the same keypath can be satisfied by looking in the cache.
     *
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    protected Class getPropertyType(String keypath) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Map propertyTypes = (Map) classCache.get(getClass());
        if (propertyTypes == null) {
            propertyTypes = new HashMap();
            classCache.put(getClass(), propertyTypes);
        }

        // if type has not been retrieve previousely, use ObjectUtils to get type
        if (!propertyTypes.containsKey(keypath)) {
            Class type = ObjectUtils.easyGetPropertyType(this, keypath);
            propertyTypes.put(keypath, type);
        }

        Class propertyType = (Class) propertyTypes.get(keypath);
        return propertyType;
    }


    /**
     * Retrieves a formatter for the keypath and property type.
     *
     * @param keypath
     * @param propertyType
     * @return
     */
    protected Formatter getFormatter(String keypath, Class propertyType) {
        // check for a formatter associated with the keypath
        Class type = formatterClassForKeypath(keypath);

        Formatter formatter;
        if (type == null) {
            // retrieve formatter based on property type
            formatter = Formatter.getFormatter(propertyType);
        }
        else {
            try {
                formatter = (Formatter) type.newInstance();
                formatter.setPropertyType(propertyType);
            }
            catch (Exception e) {
                throw new ValidationException(CREATE_ERR_MSG, e);
            }
        }
        return formatter;
    }


    // begin Kuali Foundation modification
    /**
     * Retrieves any formatters associated specially with the keypath.
     *
     * @param keypath
     * @return
     */
    protected Class formatterClassForKeypath(String keypath) {
        // remove traces of array and map indices from the incoming keypath
        String indexlessKey = keypath.replaceAll("(\\[[0-9]*+\\]|\\(.*?\\))", "");

        return (Class)formatterTypes.get( indexlessKey );
    }
    // end Kuali Foundation modification

    /**
     * Tries to format the provided value by passing it to a suitable {@link Formatter}. Adds an ActionMessage to the ActionErrors
     * in the request if a FormatException is thrown.
     * <p>
     * Caution should be used when invoking this method. It should never be called prior to {@link #populate(HttpServletRequest)}
     * because the cached request reference could be stale.
     */
    @Override
    public Object formatValue(Object value, String keypath, Class type) {

        Formatter formatter = getFormatter(keypath, type);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("formatValue (value,keypath,type) = (" + value + "," + keypath + "," + type.getName() + ")");
        }

        try {
            return Formatter.isSupportedType(type) ? formatter.formatForPresentation(value) : value;
        }
        catch (FormatException e) {
            GlobalVariables.getMessageMap().putError(keypath, e.getErrorKey(), e.getErrorArgs());
            return value.toString();
        }
    }

    /**
     * Sets the Formatter class to use for a given keypath. This class will be used by the form instead of the one returned by calls
     * to {@link Formatter#getFormatter(Class)}, which is the default mechanism.
     */
    public void setFormatterType(String keypath, Class type) {
        formatterTypes.put(keypath, type);
    }

    @Override
    public Map getUnconvertedValues() {
        return unconvertedValues;
    }

    public void setUnconvertedValues(Map unconvertedValues) {
        this.unconvertedValues = unconvertedValues;
    }

    protected List getUnknownKeys() {
        return unknownKeys;
    }

    protected void cacheUnconvertedValue(String key, Object value) {
        Class type = value.getClass();
        if (type.isArray()) {
            value = Formatter.isEmptyValue(value) ? null : ((Object[]) value)[0];
        }

        unconvertedValues.put(key, value);
    }

    // begin Kuali Foundation modification
    @Override
    public void processValidationFail() {
        // do nothing - subclasses can implement this if they want to.
    }
    // end Kuali Foundation modification


    // begin Kuali Foundation modification
    /**
     * Gets the formatterTypes attribute.
     *
     * @return Returns the formatterTypes.
     */
    public Map getFormatterTypes() {
        return formatterTypes;
    }
    // end Kuali Foundation modification


    // begin Kuali Foundation modification
    /**
     * Sets the formatterTypes attribute value.
     * @param formatterTypes The formatterTypes to set.
     */
    public void setFormatterTypes(Map formatterTypes) {
        this.formatterTypes = formatterTypes;
    }
    // end Kuali Foundation modification


    // begin Kuali Foundation modification
    /**
     * Adds the given string as a maximum size to the form.  It will be used if a file upload is used.
     *
     * @param sizeString
     */
    protected final void addMaxUploadSize( String sizeString ) {
    maxUploadFileSizes.add( sizeString );
    }

    /**
     * Initializes the list of max upload sizes if necessary.
     *
     */
    protected final void initMaxUploadSizes() {
        if ( maxUploadFileSizes.isEmpty() ) {
            customInitMaxUploadSizes();
            // if it's still empty, add the default
            if ( maxUploadFileSizes.isEmpty() ) {
                addMaxUploadSize(CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(
                        KRADConstants.KNS_NAMESPACE, KRADConstants.DetailTypes.ALL_DETAIL_TYPE, KRADConstants.MAX_UPLOAD_SIZE_PARM_NM));
            }
        }
    }

    /**
     * Subclasses can override this to add their own max upload size to the list.  Only the largest passed will be used.
     *
     */
    protected void customInitMaxUploadSizes() {
    // nothing here
    }

    public final List<String> getMaxUploadSizes() {
    initMaxUploadSizes();

    return maxUploadFileSizes;
    }

    @Override
    public void registerEditableProperty(String editablePropertyName){
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "KualiSessionId: " + GlobalVariables.getUserSession().getKualiSessionId() + " -- Registering Property: " + editablePropertyName );
        }
        editableProperties.add(editablePropertyName);
    }

    public void registerRequiredNonEditableProperty(String requiredNonEditableProperty) {
        requiredNonEditableProperties.add(requiredNonEditableProperty);
    }

    @Override
    public void clearEditablePropertyInformation(){
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "KualiSessionId: " + GlobalVariables.getUserSession().getKualiSessionId() + " -- Clearing Editable Properties" );
        }
        editableProperties = new HashSet<String>();
    }

    @Override
    public Set<String> getEditableProperties(){
        return editableProperties;
    }

    public boolean isPropertyEditable(String propertyName) {
        final Set<String> populateEditableProperties = getPopulateEditableProperties();
        return WebUtils.isPropertyEditable(populateEditableProperties, propertyName);
    }

    /***
     * @see PojoForm#addRequiredNonEditableProperties()
     */
    @Override
    public void addRequiredNonEditableProperties(){
    }

    public boolean isPropertyNonEditableButRequired(String propertyName) {
        return WebUtils.isPropertyEditable(requiredNonEditableProperties, propertyName);
    }

    protected String getParameter(HttpServletRequest request, String parameterName){
        return request.getParameter(parameterName);
    }

    protected String[] getParameterValues(HttpServletRequest request, String parameterName){
        return request.getParameterValues(parameterName);
    }

    @Override
    public Set<String> getRequiredNonEditableProperties(){
        return requiredNonEditableProperties;
    }

    /**
     * @see PojoForm#registerStrutsActionMappingScope(String)
     */
    @Override
    public void registerStrutsActionMappingScope(String strutsActionMappingScope) {
        this.strutsActionMappingScope = strutsActionMappingScope;
    }

    public String getStrutsActionMappingScope() {
        return strutsActionMappingScope;
    }

    /**
     * @see PojoForm#registerStrutsActionMappingScope(String)
     */
    @Override
    public void registerIsNewForm(boolean isNewForm) {
        this.isNewForm = isNewForm;
    }

    @Override
    public boolean getIsNewForm() {
        return this.isNewForm;
    }


    /**
     * @see PojoForm#shouldPropertyBePopulatedInForm(java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public boolean shouldPropertyBePopulatedInForm(String requestParameterName, HttpServletRequest request) {

        if (requestParameterName.equals(PojoFormBase.PREVIOUS_REQUEST_EDITABLE_PROPERTIES_GUID)) {
            return false; // don't repopulate this
        }
        else if (StringUtils.equalsIgnoreCase("session",getStrutsActionMappingScope()) && !getIsNewForm()) {
            return isPropertyEditable(requestParameterName) || isPropertyNonEditableButRequired(requestParameterName);
        }
        return true;

    }

    /**
     * Base implementation that returns just "start".  sub-implementations should not add values to Set instance returned
     * by this method, and should create its own instance.
     *
     * @see PojoForm#getMethodToCallsToBypassSessionRetrievalForGETRequests()
     */
    @Override
    public Set<String> getMethodToCallsToBypassSessionRetrievalForGETRequests() {
        Set<String> defaultMethodToCalls = new HashSet<String>();
        defaultMethodToCalls.add(KRADConstants.START_METHOD);
        return defaultMethodToCalls;
    }



    /**
     * Sets the guid to editable properties consulted during population
     *
     */
    @Override
    public void setPopulateEditablePropertiesGuid(String guid) {
        this.populateEditablePropertiesGuid = guid;
    }

    /**
     * @return the guid for the populate editable properties
     */
    public String getPopulateEditablePropertiesGuid() {
        return this.populateEditablePropertiesGuid;
    }

    /**
     * Sets the guid of the editable properties which were registered by the action
     * @see PojoForm#setActionEditablePropertiesGuid(java.lang.String)
     */
    @Override
    public void setActionEditablePropertiesGuid(String guid) {
        this.actionEditablePropertiesGuid = guid;
    }

    /**
     * @return the guid of the editable properties which had been registered by the action processing
     */
    public String getActionEditablePropertiesGuid() {
        return actionEditablePropertiesGuid;
    }

    /**
     * @return the editable properties to be consulted during population
     */
    public Set<String> getPopulateEditableProperties() {
        EditablePropertiesHistoryHolder holder = (EditablePropertiesHistoryHolder) GlobalVariables.getUserSession().getObjectMap().get(
                KRADConstants.EDITABLE_PROPERTIES_HISTORY_HOLDER_ATTR_NAME);
        if (holder == null) {
            holder = new EditablePropertiesHistoryHolder();
        }
        GlobalVariables.getUserSession().addObject(KRADConstants.EDITABLE_PROPERTIES_HISTORY_HOLDER_ATTR_NAME, holder);

        return holder.getEditableProperties(getPopulateEditablePropertiesGuid());
    }

    /**
     * Copies all editable properties in the populate editable properties to the action editable properties
     */
    public void copyPopulateEditablePropertiesToActionEditableProperties() {
        Set<String> populateEditableProperties = getPopulateEditableProperties();
        for (String property : populateEditableProperties) {
            registerEditableProperty(property);
        }
    }

    // end Kuali Foundation modification
}