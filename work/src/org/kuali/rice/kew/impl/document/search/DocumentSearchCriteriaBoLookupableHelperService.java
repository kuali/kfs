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
package org.kuali.rice.kew.impl.document.search;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.kew.api.KEWPropertyConstants;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteriaContract;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.api.document.search.DocumentSearchResults;
import org.kuali.rice.kew.docsearch.DocumentSearchCriteriaProcessor;
import org.kuali.rice.kew.docsearch.DocumentSearchCriteriaProcessorKEWAdapter;
import org.kuali.rice.kew.docsearch.service.DocumentSearchService;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.exception.WorkflowServiceError;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.framework.document.search.DocumentSearchCriteriaConfiguration;
import org.kuali.rice.kew.framework.document.search.DocumentSearchResultSetConfiguration;
import org.kuali.rice.kew.framework.document.search.StandardResultField;
import org.kuali.rice.kew.lookup.valuefinder.SavedSearchValuesFinder;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.UserUtils;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Implementation of lookupable helper service which handles the complex lookup behavior required by the KEW
 * document search screen.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentSearchCriteriaBoLookupableHelperService extends KualiLookupableHelperServiceImpl {

    static final String SAVED_SEARCH_NAME_PARAM = "savedSearchToLoadAndExecute";
    static final String DOCUMENT_TYPE_NAME_PARAM = "documentTypeName";

    // warning message keys

    private static final String EXCEED_THRESHOLD_MESSAGE_KEY = "docsearch.DocumentSearchService.exceededThreshold";
    private static final String SECURITY_FILTERED_MESSAGE_KEY = "docsearch.DocumentSearchService.securityFiltered";
    private static final String EXCEED_THRESHOLD_AND_SECURITY_FILTERED_MESSAGE_KEY = "docsearch.DocumentSearchService.exceededThresholdAndSecurityFiltered";

    private static final boolean DOCUMENT_HANDLER_POPUP_DEFAULT = true;
    private static final boolean ROUTE_LOG_POPUP_DEFAULT = true;

    // injected services

    private DocumentSearchService documentSearchService;
    private DocumentSearchCriteriaProcessor documentSearchCriteriaProcessor;
    private DocumentSearchCriteriaTranslator documentSearchCriteriaTranslator;

    // These two fields are *only* used to pass side-channel information across the superclass API boundary between
    // performLookup and getSearchResultsHelper.
    // (in theory these could be replaced with some threadlocal subterfuge, but keeping as-is for simplicity)
    private DocumentSearchResults searchResults = null;
    private DocumentSearchCriteria criteria = null;

    @Override
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
        criteria = loadCriteria(fieldValues);
        searchResults = null;
        try {
            searchResults = KEWServiceLocator.getDocumentSearchService().lookupDocuments(GlobalVariables.getUserSession().getPrincipalId(), criteria);
            if (searchResults.isCriteriaModified()) {
                criteria = searchResults.getCriteria();
            }
        } catch (WorkflowServiceErrorException wsee) {
            for (WorkflowServiceError workflowServiceError : (List<WorkflowServiceError>) wsee.getServiceErrors()) {
                if (workflowServiceError.getMessageMap() != null && workflowServiceError.getMessageMap().hasErrors()) {
                    // merge the message maps
                    GlobalVariables.getMessageMap().merge(workflowServiceError.getMessageMap());
                } else {
                    GlobalVariables.getMessageMap().putError(workflowServiceError.getMessage(), RiceKeyConstants.ERROR_CUSTOM, workflowServiceError.getMessage());
                }
            }
        }

        if (!GlobalVariables.getMessageMap().hasNoErrors() || searchResults == null) {
            throw new ValidationException("error with doc search");
        }

        populateResultWarningMessages(searchResults);

        List<DocumentSearchResult> individualSearchResults = searchResults.getSearchResults();

        setBackLocation(fieldValues.get(KRADConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KRADConstants.DOC_FORM_KEY));

        applyCriteriaChangesToFields(criteria);

        return populateSearchResults(individualSearchResults);

    }

    /**
     * Inspects the lookup results to determine if any warning messages should be published to the message map.
     */
    protected void populateResultWarningMessages(DocumentSearchResults searchResults) {
        // check various warning conditions
        boolean overThreshold = searchResults.isOverThreshold();
        int numFiltered = searchResults.getNumberOfSecurityFilteredResults();
        int numResults = searchResults.getSearchResults().size();
        if (overThreshold && numFiltered > 0) {
            GlobalVariables.getMessageMap().putWarning(KRADConstants.GLOBAL_MESSAGES, EXCEED_THRESHOLD_AND_SECURITY_FILTERED_MESSAGE_KEY, String.valueOf(numResults), String.valueOf(numFiltered));
        } else if (numFiltered > 0) {
            GlobalVariables.getMessageMap().putWarning(KRADConstants.GLOBAL_MESSAGES, SECURITY_FILTERED_MESSAGE_KEY, String.valueOf(numFiltered));
        } else if (overThreshold) {
            GlobalVariables.getMessageMap().putWarning(KRADConstants.GLOBAL_MESSAGES, EXCEED_THRESHOLD_MESSAGE_KEY, String.valueOf(numResults));
        }
    }

    /**
     * Applies changes that might have happened to the criteria back to the fields so that they show up on the form.
     * Namely, this handles populating the form with today's date if the create date was not filled in on the form.
     */
    protected void applyCriteriaChangesToFields(DocumentSearchCriteriaContract criteria) {
        Field field = getFormFields().getField(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + "dateCreated");
        if (field != null && StringUtils.isEmpty(field.getPropertyValue())) {
            if (criteria.getDateCreatedFrom() != null) {
                field.setPropertyValue(CoreApiServiceLocator.getDateTimeService().toDateString(criteria.getDateCreatedFrom().toDate()));
            }
        }
    }

    // CURRENT_USER token pattern: CURRENT_USER(.type) surrounded by positive lookahead/lookbehind for non-alphanum terminal tokens
    // (to support expression operators)
    private static final Pattern CURRENT_USER_PATTERN = Pattern.compile("(?<=[\\s\\p{Punct}]|^)CURRENT_USER(\\.\\w+)?(?=[\\s\\p{Punct}]|$)");

    protected static String replaceCurrentUserToken(String value, Person person) {
        Matcher matcher = CURRENT_USER_PATTERN.matcher(value);
        boolean matched = false;
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matched = true;
            String idType = "principalName";
            if (matcher.groupCount() > 0) {
                String group = matcher.group(1);
                if (group != null) {
                    idType = group.substring(1); // discard period after CURRENT_USER
                }
            }
            String idValue = UserUtils.getIdValue(idType, person);
            if (!StringUtils.isBlank(idValue)) {
                value = idValue;
            } else {
                value = matcher.group();
            }
            matcher.appendReplacement(sb, value);

        }
        matcher.appendTail(sb);
        return matched ? sb.toString() : null;
    }

    /**
     * Cleans up various issues with fieldValues coming from the lookup form (namely, that they don't include
     * multi-valued field values!). Handles these by adding them comma-separated.
     */
    protected static Map<String, String> cleanupFieldValues(Map<String, String> fieldValues, Map<String, String[]> parameters) {
        Map<String, String> cleanedUpFieldValues = new HashMap<String, String>(fieldValues);
        if (ArrayUtils.isNotEmpty(parameters.get(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_STATUS_CODE))) {
            cleanedUpFieldValues.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_STATUS_CODE,
                    StringUtils.join(parameters.get(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_STATUS_CODE), ","));
        }
        if (ArrayUtils.isNotEmpty(parameters.get(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_STATUS))) {
            cleanedUpFieldValues.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_STATUS,
                    StringUtils.join(parameters.get(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_STATUS), ","));
        }
        Map<String, String> documentAttributeFieldValues = new HashMap<String, String>();
        for (String parameterName : parameters.keySet()) {
            if (parameterName.contains(KewApiConstants.DOCUMENT_ATTRIBUTE_FIELD_PREFIX)) {
                String[] value = parameters.get(parameterName);
                if (ArrayUtils.isNotEmpty(value)) {
                    documentAttributeFieldValues.put(parameterName, StringUtils.join(value, " " + SearchOperator.OR.op() + " "));
                }
            }
        }
        // if any of the document attributes are range values, process them
        documentAttributeFieldValues.putAll(LookupUtils.preProcessRangeFields(documentAttributeFieldValues));
        cleanedUpFieldValues.putAll(documentAttributeFieldValues);

        replaceCurrentUserInFields(cleanedUpFieldValues);

        return cleanedUpFieldValues;
    }

    protected static void replaceCurrentUserInFields(Map<String, String> fields) {
        Person person = GlobalVariables.getUserSession().getPerson();
        // replace the dynamic CURRENT_USER token
        for (Map.Entry<String, String> entry: fields.entrySet()) {
            if (StringUtils.isNotEmpty(entry.getValue())) {
                String replaced = replaceCurrentUserToken(entry.getValue(), person);
                if (replaced != null) {
                    entry.setValue(replaced);
                }
            }
        }
    }

    /**
     * Loads the document search criteria from the given map of field values as submitted from the search screen, and
     * populates the current form Rows/Fields with the saved criteria fields
     */
    protected DocumentSearchCriteria loadCriteria(Map<String, String> fieldValues) {
        fieldValues = cleanupFieldValues(fieldValues, getParameters());
        String[] savedSearchToLoad = getParameters().get(SAVED_SEARCH_NAME_PARAM);
        boolean savedSearch = savedSearchToLoad != null && savedSearchToLoad.length > 0 && StringUtils.isNotBlank(savedSearchToLoad[0]);
        if (savedSearch) {
            DocumentSearchCriteria criteria = getDocumentSearchService().getNamedSearchCriteria(GlobalVariables.getUserSession().getPrincipalId(), savedSearchToLoad[0]);
            if (criteria != null) {
                getFormFields().setFieldValues(getDocumentSearchCriteriaTranslator().translateCriteriaToFields(criteria));
                return criteria;
            }
        }
        // either it wasn't a saved search or the saved search failed to resolve
        return getDocumentSearchCriteriaTranslator().translateFieldsToCriteria(fieldValues);
    }

    protected List<DocumentSearchCriteriaBo> populateSearchResults(List<DocumentSearchResult> lookupResults) {
        List<DocumentSearchCriteriaBo> searchResults = new ArrayList<DocumentSearchCriteriaBo>();
        for (DocumentSearchResult searchResult : lookupResults) {
            DocumentSearchCriteriaBo result = new DocumentSearchCriteriaBo();
            result.populateFromDocumentSearchResult(searchResult);
            searchResults.add(result);
        }
        return searchResults;
    }

    @Override
    public Collection<? extends BusinessObject> performLookup(LookupForm lookupForm, Collection<ResultRow> resultTable, boolean bounded) {
        Collection<? extends BusinessObject> lookupResult = super.performLookup(lookupForm, resultTable, bounded);
        postProcessResults(resultTable, this.searchResults);
        return lookupResult;
    }

    /**
     * Overrides a Field value; sets a fallback/restored value if there is no new value
     */
    protected void overrideFieldValue(Field field, Map<String, String[]> newValues, Map<String, String[]> oldValues) {
        if (StringUtils.isNotBlank(field.getPropertyName())) {
            if (newValues.get(field.getPropertyName()) != null) {
                getFormFields().setFieldValue(field, newValues.get(field.getPropertyName()));
            } else if (oldValues.get(field.getPropertyName()) != null) {
                getFormFields().setFieldValue(field, oldValues.get(field.getPropertyName()));
            }
        }
    }

    /**
     * Handles toggling between form views.
     * Reads and sets the Rows state.
     */
    protected void toggleFormView() {
        Map<String,String[]> fieldValues = new HashMap<String,String[]>();
        Map<String, String[]> savedValues = getFormFields().getFieldValues();

        // the original implementation saved the form values and then re-applied them
        // we do the same here, however I suspect we may be able to avoid this re-application
        // of existing values

        for (Field field: getFormFields().getFields()) {
            overrideFieldValue(field, this.getParameters(), savedValues);
            // if we are sure this does not depend on or cause side effects in other fields
            // then this phase can be extracted and these loops simplified
            applyFieldAuthorizationsFromNestedLookups(field);
            fieldValues.put(field.getPropertyName(), new String[] { field.getPropertyValue() });
        }

        // checkForAdditionalFields generates the form (setRows)
        if (checkForAdditionalFieldsMultiValued(fieldValues)) {
            for (Field field: getFormFields().getFields()) {
                overrideFieldValue(field, this.getParameters(), savedValues);
                fieldValues.put(field.getPropertyName(), new String[] { field.getPropertyValue() });
             }
        }

        // unset the clear search param, since this is not really a state, but just an action
        // it can never be toggled "off", just "on"
        getFormFields().setFieldValue(DocumentSearchCriteriaProcessorKEWAdapter.CLEARSAVED_SEARCH_FIELD, "");
    }

    /**
     * Loads a saved search
     * @return returns true on success to run the loaded search, false on error.
     */
    protected boolean loadSavedSearch(boolean ignoreErrors) {
        Map<String,String[]> fieldValues = new HashMap<String,String[]>();

        String savedSearchName = getSavedSearchName();
        if(StringUtils.isEmpty(savedSearchName) || "*ignore*".equals(savedSearchName)) {
            if(!ignoreErrors) {
                GlobalVariables.getMessageMap().putError(SAVED_SEARCH_NAME_PARAM, RiceKeyConstants.ERROR_CUSTOM, "You must select a saved search");
            } else {
                //if we're ignoring errors and we got an error just return, no reason to continue.  Also set false to indicate not to perform lookup
                return false;
            }
            getFormFields().setFieldValue(SAVED_SEARCH_NAME_PARAM, "");
        }
        if (!GlobalVariables.getMessageMap().hasNoErrors()) {
            throw new ValidationException("errors in search criteria");
        }

        DocumentSearchCriteria criteria = KEWServiceLocator.getDocumentSearchService().getSavedSearchCriteria(GlobalVariables.getUserSession().getPrincipalId(), savedSearchName);

        // get the document type
        String docTypeName = criteria.getDocumentTypeName();

        // update the parameters to include whether or not this is an advanced search
        if(this.getParameters().containsKey(KRADConstants.ADVANCED_SEARCH_FIELD)) {
            Map<String, String[]> parameters = this.getParameters();
            String[] params = parameters.get(KRADConstants.ADVANCED_SEARCH_FIELD);
            if (ArrayUtils.isNotEmpty(params)) {
                params[0] = criteria.getIsAdvancedSearch();
                this.setParameters(parameters);
            }
        }

        // and set the rows based on doc type
        setRows(docTypeName);

        // clear the name of the search in the form
        //fieldValues.put(SAVED_SEARCH_NAME_PARAM, new String[0]);

        // set the custom document attribute values on the search form
        for (Map.Entry<String, List<String>> entry: criteria.getDocumentAttributeValues().entrySet()) {
            fieldValues.put(entry.getKey(), entry.getValue().toArray(new String[entry.getValue().size()]));
        }

        // sets the field values on the form, trying criteria object properties if a field value is not present in the map
        for (Field field : getFormFields().getFields()) {
            if (field.getPropertyName() != null && !field.getPropertyName().equals("")) {
                // UI Fields know whether they are single or multiple value
                // just set both so they can make the determination and render appropriately
                String[] values = null;
                if (fieldValues.get(field.getPropertyName()) != null) {
                    values = fieldValues.get(field.getPropertyName());
                } else {
                    //may be on the root of the criteria object, try looking there:
                    try {
                        if (field.isRanged() && field.isDatePicker()) {
                            if (field.getPropertyName().startsWith(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX)) {
                                String lowerBoundName = field.getPropertyName().replace(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX, "") + "From";
                                Object lowerBoundDate = PropertyUtils.getProperty(criteria, lowerBoundName);
                                if (lowerBoundDate != null) {
                                    values = new String[] { CoreApiServiceLocator.getDateTimeService().toDateTimeString(((org.joda.time.DateTime)lowerBoundDate).toDate()) };
                                }
                            } else {
                                // the upper bound prefix may or may not be on the propertyName.  Using "replace" just in case.
                                String upperBoundName = field.getPropertyName().replace(KRADConstants.LOOKUP_RANGE_UPPER_BOUND_PROPERTY_PREFIX, "") + "To";
                                Object upperBoundDate = PropertyUtils.getProperty(criteria, upperBoundName);
                                if (upperBoundDate != null) {
                                    values = new String[] { CoreApiServiceLocator.getDateTimeService().toDateTimeString(
                                        ((org.joda.time.DateTime)upperBoundDate)
                                                .toDate()) };
                                }
                            }
                        } else {
                            values = new String[] { ObjectUtils.toString(PropertyUtils.getProperty(criteria, field.getPropertyName())) };
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        // e.printStackTrace();
                        //hmm what to do here, we should be able to find everything either in the search atts or at the base as far as I know.
                    }
                }
                if (values != null) {
                    getFormFields().setFieldValue(field, values);
                }
            }
        }

        return true;
    }

    /**
     * Performs custom document search/lookup actions.
     * 1) switching between simple/detailed search
     * 2) switching between non-superuser/superuser search
     * 3) clearing saved search results
     * 4) restoring a saved search and executing the search
     * @param ignoreErrors
     * @return whether to rerun the previous search; false in cases 1-3 because we are just updating the form
     */
    @Override
    public boolean performCustomAction(boolean ignoreErrors) {
        //boolean isConfigAction = isAdvancedSearch() || isSuperUserSearch() || isClearSavedSearch();
        if (isClearSavedSearch()) {
            KEWServiceLocator.getDocumentSearchService().clearNamedSearches(GlobalVariables.getUserSession().getPrincipalId());
            return false;
        }
        else if (getSavedSearchName() != null) {
            return loadSavedSearch(ignoreErrors);
        } else {
            toggleFormView();
            // Finally, return false to prevent the search from being performed and to skip the other custom processing below.
            return false;
        }
    }

    /**
     * Custom implementation of getInquiryUrl that sets up doc handler link.
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        DocumentSearchCriteriaBo criteriaBo = (DocumentSearchCriteriaBo)bo;
        if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_ID.equals(propertyName)) {
            return generateDocumentHandlerUrl(criteriaBo.getDocumentId(), criteriaBo.getDocumentType(),
                    isSuperUserSearch());
        } else if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG.equals(propertyName)) {
            return generateRouteLogUrl(criteriaBo.getDocumentId());
        } else if(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_INITIATOR_DISPLAY_NAME.equals(propertyName)) {
            return generateInitiatorUrl(criteriaBo.getInitiatorPerson());
        }
        return super.getInquiryUrl(bo, propertyName);
    }

    /**
     * Generates the appropriate document handler url for the given document.  If superUserSearch is true then a super
     * user doc handler link will be generated if the document type policy allows it.
     */
    protected HtmlData.AnchorHtmlData generateDocumentHandlerUrl(String documentId, DocumentType documentType, boolean superUserSearch) {
        HtmlData.AnchorHtmlData link = new HtmlData.AnchorHtmlData();
        link.setDisplayText(documentId);
        if (isDocumentHandlerPopup()) {
            link.setTarget("_blank");
        }else{
            link.setTarget("_self");
        }
        String url = ConfigContext.getCurrentContextConfig().getProperty(Config.KEW_URL) + "/";
        if (superUserSearch) {
            if (documentType.getUseWorkflowSuperUserDocHandlerUrl().getPolicyValue().booleanValue()) {
                url += "SuperUser.do?methodToCall=displaySuperUserDocument&documentId=" + documentId;
            } else {
                url = KewApiConstants.DOC_HANDLER_REDIRECT_PAGE
                        + "?" + KewApiConstants.COMMAND_PARAMETER + "="
                        + KewApiConstants.SUPERUSER_COMMAND + "&"
                        + KewApiConstants.DOCUMENT_ID_PARAMETER + "="
                        + documentId;
            }
        } else {
            url += KewApiConstants.DOC_HANDLER_REDIRECT_PAGE + "?"
                    + KewApiConstants.COMMAND_PARAMETER + "="
                    + KewApiConstants.DOCSEARCH_COMMAND + "&"
                    + KewApiConstants.DOCUMENT_ID_PARAMETER + "="
                    + documentId;
        }
        link.setHref(url);
        return link;
    }

    protected HtmlData.AnchorHtmlData generateRouteLogUrl(String documentId) {
        HtmlData.AnchorHtmlData link = new HtmlData.AnchorHtmlData();
        // KULRICE-6822 Route log link target parameter always causing pop-up
        if (isRouteLogPopup()) {
            link.setTarget("_blank");
        }
        else {
            link.setTarget("_self");
        }
        link.setDisplayText("Route Log for document " + documentId);
        String url = ConfigContext.getCurrentContextConfig().getProperty(Config.KEW_URL) + "/" +
                "RouteLog.do?documentId=" + documentId;
        link.setHref(url);
        return link;
    }

    protected HtmlData.AnchorHtmlData generateInitiatorUrl(Person person) {
        HtmlData.AnchorHtmlData link = new HtmlData.AnchorHtmlData();
        if (isRouteLogPopup()) {
            link.setTarget("_blank");
        }
        else {
            link.setTarget("_self");
        }
        link.setDisplayText("Initiator Inquiry for User with ID:" + person.getPrincipalId());
        String url = ConfigContext.getCurrentContextConfig().getProperty(Config.KIM_URL) + "/" +
                "identityManagementPersonInquiry.do?principalId=" + person.getPrincipalId();
        link.setHref(url);
        return link;
    }

    /**
     * Returns true if the document handler should open in a new window.
     */
    protected boolean isDocumentHandlerPopup() {
      return BooleanUtils.toBooleanDefaultIfNull(
                CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(
                    KewApiConstants.KEW_NAMESPACE,
                    KRADConstants.DetailTypes.DOCUMENT_SEARCH_DETAIL_TYPE,
                    KewApiConstants.DOCUMENT_SEARCH_DOCUMENT_POPUP_IND),
                DOCUMENT_HANDLER_POPUP_DEFAULT);
    }

    /**
     * Returns true if the route log should open in a new window.
     */
    public boolean isRouteLogPopup() {
        return BooleanUtils.toBooleanDefaultIfNull(
                CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(KewApiConstants.KEW_NAMESPACE,
                        KRADConstants.DetailTypes.DOCUMENT_SEARCH_DETAIL_TYPE,
                        KewApiConstants.DOCUMENT_SEARCH_ROUTE_LOG_POPUP_IND), ROUTE_LOG_POPUP_DEFAULT);
    }

    /**
     * Parses a boolean request parameter
     */
    protected boolean isFlagSet(String flagName) {
        if(this.getParameters().containsKey(flagName)) {
            String[] params = this.getParameters().get(flagName);
            if (ArrayUtils.isNotEmpty(params)) {
                return "YES".equalsIgnoreCase(params[0]);
            }
        }
        return false;
    }

    /**
     * Returns true if the current search being executed is a super user search.
     */
    protected boolean isSuperUserSearch() {
        return isFlagSet(DocumentSearchCriteriaProcessorKEWAdapter.SUPERUSER_SEARCH_FIELD);
    }

    /**
     * Returns true if the current search being executed is an "advanced" search.
     */
    protected boolean isAdvancedSearch() {
        return isFlagSet(KRADConstants.ADVANCED_SEARCH_FIELD);
    }

    /**
     * Returns true if the current "search" being executed is an "clear" search.
     */
    protected boolean isClearSavedSearch() {
        return isFlagSet(DocumentSearchCriteriaProcessorKEWAdapter.CLEARSAVED_SEARCH_FIELD);
    }

    protected String getSavedSearchName() {
        String[] savedSearchName = getParameters().get(SAVED_SEARCH_NAME_PARAM);
        if (savedSearchName != null && savedSearchName.length > 0) {
            return savedSearchName[0];
        }
        return null;
    }

    /**
     * Override setRows in order to post-process and add documenttype-dependent fields
     */
    @Override
    protected void setRows() {
        this.setRows(null);
    }

    /**
     * Returns wrapper around current form fields
     */
    protected FormFields getFormFields() {
        return new FormFields(this.getRows());
    }

    /**
     * Sets the rows for the search criteria.  This method will delegate to the DocumentSearchCriteriaProcessor
     * in order to pull in fields for custom search attributes.
     *
     * @param documentTypeName the name of the document type currently entered on the form, if this is a valid document
     * type then it may have search attribute fields that need to be displayed; documentType name may also be loaded
     * via a saved search
     */
    protected void setRows(String documentTypeName) {
        // Always call superclass to regenerate the rows since state may have changed (namely, documentTypeName parsed from params)
        super.setRows();

        List<Row> lookupRows = new ArrayList<Row>();
        //copy the current rows
        for (Row row : getRows()) {
            lookupRows.add(row);
        }
        //clear out
        getRows().clear();

        DocumentType docType = getValidDocumentType(documentTypeName);

        boolean advancedSearch = isAdvancedSearch();
        boolean superUserSearch = isSuperUserSearch();

        //call get rows
        List<Row> rows = getDocumentSearchCriteriaProcessor().getRows(docType,lookupRows, advancedSearch, superUserSearch);

        BusinessObjectEntry boe = (BusinessObjectEntry) KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(this.getBusinessObjectClass().getName());
        int numCols = boe.getLookupDefinition().getNumOfColumns();
        if(numCols == 0) {
            numCols = KRADConstants.DEFAULT_NUM_OF_COLUMNS;
        }

        super.getRows().addAll(FieldUtils.wrapFields(new FormFields(rows).getFieldList(), numCols));

    }

    /**
     * Checks for a valid document type with the given name in a case-sensitive manner.
     *
     * @return the DocumentType which matches the given name or null if no valid document type could be found
     */
    private DocumentType getValidDocumentType(String documentTypeName) {
        if (StringUtils.isNotEmpty(documentTypeName)) {
            DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByNameCaseInsensitive(documentTypeName.trim());
            if (documentType != null && documentType.isActive()) {
                return documentType;
            }
        }
        return null;
    }

    private static String TOGGLE_BUTTON = "<input type='image' name=''{0}'' id=''{0}'' class='tinybutton' src=''..{1}/images/tinybutton-{2}search.gif'' alt=''{3} search'' title=''{3} search''/>";

    @Override
    public String getSupplementalMenuBar() {
        boolean advancedSearch = isAdvancedSearch();
        boolean superUserSearch = isSuperUserSearch();
        StringBuilder suppMenuBar = new StringBuilder();

        // Add the detailed-search-toggling button.
        // to mimic previous behavior, basic search button is shown both when currently rendering detailed search AND super user search
        // as super user search is essentially a detailed search
        String type = advancedSearch ? "basic" : "detailed";
        suppMenuBar.append(MessageFormat.format(TOGGLE_BUTTON, "toggleAdvancedSearch", KewApiConstants.WEBAPP_DIRECTORY, type, type));

        // Add the superuser-search-toggling button.
        suppMenuBar.append("&nbsp;");
        suppMenuBar.append(MessageFormat.format(TOGGLE_BUTTON, "toggleSuperUserSearch", KewApiConstants.WEBAPP_DIRECTORY, superUserSearch ? "nonsupu" : "superuser", superUserSearch ? "non-superuser" : "superuser"));

        // Add the "clear saved searches" button.
        suppMenuBar.append("&nbsp;");
        suppMenuBar.append(MessageFormat.format(TOGGLE_BUTTON, DocumentSearchCriteriaProcessorKEWAdapter.CLEARSAVED_SEARCH_FIELD, KewApiConstants.WEBAPP_DIRECTORY, "clearsaved", "clear saved searches"));

        // Wire up the onblur for document type name
        suppMenuBar.append("<script type=\"text/javascript\">"
                + " jQuery(document).ready(function () {"
                + " jQuery(\"#documentTypeName\").blur(function () { validateDocTypeAndRefresh( this ); });"
                + "});</script>");

        return suppMenuBar.toString();
    }

    @Override
    public boolean shouldDisplayHeaderNonMaintActions() {
        return true;
    }

    @Override
    public boolean shouldDisplayLookupCriteria() {
        return true;
    }

    /**
     * Determines if there should be more search fields rendered based on already entered search criteria, and
     * generates additional form rows.
     */
    @Override
    public boolean checkForAdditionalFields(Map<String, String> fieldValues) {
        return checkForAdditionalFieldsForDocumentType(fieldValues.get(DOCUMENT_TYPE_NAME_PARAM));
    }

    private boolean checkForAdditionalFieldsMultiValued(Map<String, String[]> fieldValues) {
        String[] valArray = fieldValues.get(DOCUMENT_TYPE_NAME_PARAM);
        String val = null;
        if (valArray != null && valArray.length > 0) {
            val = valArray[0];
        }
        return checkForAdditionalFieldsForDocumentType(val);
    }

    private boolean checkForAdditionalFieldsForDocumentType(String documentTypeName) {
        if (StringUtils.isNotBlank(documentTypeName)) {
            setRows(documentTypeName);
        }
        return true;
    }

    @Override
    public Field getExtraField() {
        SavedSearchValuesFinder savedSearchValuesFinder = new SavedSearchValuesFinder();
        List<KeyValue> savedSearchValues = savedSearchValuesFinder.getKeyValues();
        Field savedSearch = new Field();
        savedSearch.setPropertyName(SAVED_SEARCH_NAME_PARAM);
        savedSearch.setFieldType(Field.DROPDOWN_SCRIPT);
        savedSearch.setScript("customLookupChanged()");
        savedSearch.setFieldValidValues(savedSearchValues);
        savedSearch.setFieldLabel("Saved Searches");
        return savedSearch;
    }

    @Override
    public void performClear(LookupForm lookupForm) {
        //KULRICE-7709 Convert dateCreated value to range before loadCriteria
        Map<String, String> formFields = LookupUtils.preProcessRangeFields(lookupForm.getFields());
        DocumentSearchCriteria criteria = loadCriteria(formFields);
        super.performClear(lookupForm);
        repopulateSearchTypeFlags();
        DocumentType documentType = getValidDocumentType(criteria.getDocumentTypeName());
        if (documentType != null) {
            DocumentSearchCriteria clearedCriteria = documentSearchService.clearCriteria(documentType, criteria);
            applyCriteriaChangesToFields(DocumentSearchCriteria.Builder.create(clearedCriteria));
        }
    }

    /**
     * Repopulate the fields indicating advanced/superuser search type.
     */
    protected void repopulateSearchTypeFlags() {
        boolean advancedSearch = isAdvancedSearch();
        boolean superUserSearch = isSuperUserSearch();
        int fieldsRepopulated = 0;
        Map<String, String[]> values = new HashMap<String, String[]>();
        values.put(KRADConstants.ADVANCED_SEARCH_FIELD, new String[] { advancedSearch ? "YES" : "NO" });
        values.put(DocumentSearchCriteriaProcessorKEWAdapter.SUPERUSER_SEARCH_FIELD, new String[] { superUserSearch ? "YES" : "NO" });
        getFormFields().setFieldValues(values);
    }

    /**
     * Takes a collection of result rows and does final processing on them.
     */
    protected void postProcessResults(Collection<ResultRow> resultRows, DocumentSearchResults searchResults) {
        if (resultRows.size() != searchResults.getSearchResults().size()) {
            throw new IllegalStateException("Encountered a mismatch between ResultRow items and document search results "
                    + resultRows.size() + " != " + searchResults.getSearchResults().size());
        }
        DocumentType documentType = getValidDocumentType(criteria.getDocumentTypeName());
        DocumentSearchResultSetConfiguration resultSetConfiguration = null;
        DocumentSearchCriteriaConfiguration criteriaConfiguration = null;
        if (documentType != null) {
            resultSetConfiguration = KEWServiceLocator.getDocumentSearchCustomizationMediator().customizeResultSetConfiguration(documentType, criteria);
            criteriaConfiguration =  KEWServiceLocator.getDocumentSearchCustomizationMediator().getDocumentSearchCriteriaConfiguration(documentType);
        }
        int index = 0;
        for (ResultRow resultRow : resultRows) {
            DocumentSearchResult searchResult = searchResults.getSearchResults().get(index);
            executeColumnCustomization(resultRow, searchResult, resultSetConfiguration, criteriaConfiguration);
            index++;
        }
    }

    /**
     * Executes customization of columns, could include removing certain columns or adding additional columns to the
     * result row (in cases where columns are added by document search customization, such as searchable attributes).
     */
    protected void executeColumnCustomization(ResultRow resultRow, DocumentSearchResult searchResult,
            DocumentSearchResultSetConfiguration resultSetConfiguration,
            DocumentSearchCriteriaConfiguration criteriaConfiguration) {
        if (resultSetConfiguration == null) {
            resultSetConfiguration = DocumentSearchResultSetConfiguration.Builder.create().build();
        }
        if (criteriaConfiguration == null) {
            criteriaConfiguration = DocumentSearchCriteriaConfiguration.Builder.create().build();
        }
        List<StandardResultField> standardFieldsToRemove = resultSetConfiguration.getStandardResultFieldsToRemove();
        if (standardFieldsToRemove == null) {
            standardFieldsToRemove = Collections.emptyList();
        }
        List<Column> newColumns = new ArrayList<Column>();
        for (Column standardColumn : resultRow.getColumns()) {
            if (!standardFieldsToRemove.contains(StandardResultField.fromFieldName(standardColumn.getPropertyName()))) {
                newColumns.add(standardColumn);
                // modify the route log column so that xml values are not escaped (allows for the route log <img ...> to be
                // rendered properly)
                if (standardColumn.getPropertyName().equals(
                        KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG)) {
                    standardColumn.setEscapeXMLValue(false);
                }
            }
        }

        // determine which document attribute fields should be added
        List<RemotableAttributeField> searchAttributeFields = criteriaConfiguration.getFlattenedSearchAttributeFields();
        List<String> additionalFieldNamesToInclude = new ArrayList<String>();
        if (!resultSetConfiguration.isOverrideSearchableAttributes()) {
            for (RemotableAttributeField searchAttributeField : searchAttributeFields) {
                // TODO - KULRICE-5738 - add check here to make sure the searchable attribute should be displayed in result set
                // right now this is default always including all searchable attributes!
                if (searchAttributeField.getAttributeLookupSettings() == null ||
                    searchAttributeField.getAttributeLookupSettings().isInResults()) {
                    additionalFieldNamesToInclude.add(searchAttributeField.getName());
                }
            }
        }
        if (resultSetConfiguration.getCustomFieldNamesToAdd() != null) {
            additionalFieldNamesToInclude.addAll(resultSetConfiguration.getCustomFieldNamesToAdd());
        }

        // now assemble the custom columns
        List<Column> customColumns = new ArrayList<Column>();
        List<Column> additionalAttributeColumns = FieldUtils.constructColumnsFromAttributeFields(resultSetConfiguration.getAdditionalAttributeFields());

        outer:for (String additionalFieldNameToInclude : additionalFieldNamesToInclude) {
            // search the search attribute fields
            for (RemotableAttributeField searchAttributeField : searchAttributeFields) {
                if (additionalFieldNameToInclude.equals(searchAttributeField.getName())) {
                    Column searchAttributeColumn = FieldUtils.constructColumnFromAttributeField(searchAttributeField);
                    wrapDocumentAttributeColumnName(searchAttributeColumn);
                    customColumns.add(searchAttributeColumn);
                    continue outer;
                }
            }
            for (Column additionalAttributeColumn : additionalAttributeColumns) {
                if (additionalFieldNameToInclude.equals(additionalAttributeColumn.getPropertyName())) {
                    wrapDocumentAttributeColumnName(additionalAttributeColumn);
                    customColumns.add(additionalAttributeColumn);
                    continue outer;
                }
            }
            LOG.warn("Failed to locate a proper column definition for requested additional field to include in"
                    + "result set with name '"
                    + additionalFieldNameToInclude
                    + "'");
        }
        populateCustomColumns(customColumns, searchResult);

      //TEM begin - if there is an action custom column, always put that before any other field
        for (Column column : customColumns){
            //TOOD: change to the applicable constant Actions
            if (column.getColumnTitle().equals("Actions")){
                newColumns.add(0, column);
                customColumns.remove(column);
                break;
            }
        }
        //TEM end

        // now merge the custom columns into the standard columns right before the route log (if the route log column wasn't removed!)
        if (newColumns.isEmpty() || !StandardResultField.ROUTE_LOG.isFieldNameValid(newColumns.get(newColumns.size() - 1).getPropertyName())) {
            newColumns.addAll(customColumns);
        } else {
            newColumns.addAll(newColumns.size() - 1, customColumns);
        }
        resultRow.setColumns(newColumns);
    }

    protected void populateCustomColumns(List<Column> customColumns, DocumentSearchResult searchResult) {
        for (Column customColumn : customColumns) {
            DocumentAttribute documentAttribute = searchResult.getSingleDocumentAttributeByName(customColumn.getPropertyName());
            if (documentAttribute != null && documentAttribute.getValue() != null) {
                wrapDocumentAttributeColumnName(customColumn);
                // list moving forward if the attribute has more than one value
                Formatter formatter = customColumn.getFormatter();
                Object attributeValue = documentAttribute.getValue();
                if (formatter.getPropertyType().equals(KualiDecimal.class)
                        && documentAttribute.getValue() instanceof BigDecimal) {
                    attributeValue = new KualiDecimal((BigDecimal)attributeValue);
                } else if (formatter.getPropertyType().equals(KualiPercent.class)
                        && documentAttribute.getValue() instanceof BigDecimal) {
                    attributeValue = new KualiPercent((BigDecimal)attributeValue);
                }
                customColumn.setPropertyValue(formatter.format(attributeValue).toString());

                //TEM begin - populate the custom column columnAnchor because it is used for determining if the result field is displayed
                //                    as static string or links
                HtmlData anchor = customColumn.getColumnAnchor();
                if (anchor != null && anchor instanceof AnchorHtmlData){
                    AnchorHtmlData anchorHtml = (AnchorHtmlData)anchor;
                    if (StringUtils.isEmpty(anchorHtml.getHref()) && StringUtils.isEmpty(anchorHtml.getTitle())){
                        customColumn.setColumnAnchor(new AnchorHtmlData(formatter.format(attributeValue).toString(), documentAttribute.getName()));
                    }
                }
                //TEM end
            }
        }
    }

    private void wrapDocumentAttributeColumnName(Column column) {
        // TODO - comment out for now, not sure we really want to do this...
        //column.setPropertyName(DOCUMENT_ATTRIBUTE_PROPERTY_NAME_PREFIX + column.getPropertyName());
    }

    public void setDocumentSearchService(DocumentSearchService documentSearchService) {
        this.documentSearchService = documentSearchService;
    }

    public DocumentSearchService getDocumentSearchService() {
        return documentSearchService;
    }

    public DocumentSearchCriteriaProcessor getDocumentSearchCriteriaProcessor() {
        return documentSearchCriteriaProcessor;
    }

    public void setDocumentSearchCriteriaProcessor(DocumentSearchCriteriaProcessor documentSearchCriteriaProcessor) {
        this.documentSearchCriteriaProcessor = documentSearchCriteriaProcessor;
    }

    public DocumentSearchCriteriaTranslator getDocumentSearchCriteriaTranslator() {
        return documentSearchCriteriaTranslator;
    }

    public void setDocumentSearchCriteriaTranslator(DocumentSearchCriteriaTranslator documentSearchCriteriaTranslator) {
        this.documentSearchCriteriaTranslator = documentSearchCriteriaTranslator;
    }
}
