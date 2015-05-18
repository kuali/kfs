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
package org.kuali.rice.kns.lookup;

import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Kuali lookup implementation. Implements methods necessary to render the lookup and provides search and return methods.
 */
@Transactional
public class KualiLookupableImpl implements Lookupable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiLookupableImpl.class);
    protected static final String[] IGNORE_LIST = { KRADConstants.DOC_FORM_KEY, KRADConstants.BACK_LOCATION };

    protected Class businessObjectClass;
    protected LookupableHelperService lookupableHelperService;
    protected String extraOnLoad = ""; // This is supposed to be a javascript function.

    /**
     * Default constructor initializes services from spring
     */
    public KualiLookupableImpl() {
    }

    /**
     * Sets the business object class for the lookup instance, then rows can be set for search render.
     *
     * @param boClass Class for the lookup business object
     */
    public void setBusinessObjectClass(Class boClass) {
        if (boClass == null) {
            throw new RuntimeException("Business object class is null.");
        }

        this.businessObjectClass = boClass;

        // next line initializes the helper to return correct values for getRow();
        getLookupableHelperService().setBusinessObjectClass(boClass);
    }

    /**
     * Initializes the lookup with the given Map of parameters.
     *
     * @param parameters
     */
    public void setParameters(Map<String, String[]> parameters) {
        getLookupableHelperService().setParameters(parameters);
    }

    /**
     * @return Returns the parameters passed to this lookup
     */
    public Map<String, String[]> getParameters() {
        return getLookupableHelperService().getParameters();
    }

    /**
     * Constructs the list of columns for the search results. All properties for the column objects come from the DataDictionary.
     */
    public List<Column> getColumns() {
        return getLookupableHelperService().getColumns();
    }

    /**
     * Checks that any required search fields have value.
     *
     * @see Lookupable#validateSearchParameters(Map)
     */
    public void validateSearchParameters(Map<String, String> fieldValues) {
        getLookupableHelperService().validateSearchParameters(fieldValues);
    }

    /**
     * Uses Lookup Service to provide a basic unbounded search.
     *
     * @param fieldValues - Map containing prop name keys and search values
     *
     * @return List found business objects
     */
    public List<? extends BusinessObject> getSearchResultsUnbounded(Map<String, String> fieldValues) {
        return getLookupableHelperService().getSearchResultsUnbounded(fieldValues);
    }

    /**
     * Uses Lookup Service to provide a basic search.
     *
     * @param fieldValues - Map containing prop name keys and search values
     *
     * @return List found business objects
     */
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        return getLookupableHelperService().getSearchResults(fieldValues);
    }

    /**
     * @return the return url for each result row.
     */
    public HtmlData getReturnUrl(BusinessObject bo, Map fieldConversions, String lookupImpl, BusinessObjectRestrictions businessObjectRestrictions) {
        return getLookupableHelperService().getReturnUrl(bo, fieldConversions, lookupImpl, getReturnKeys(), businessObjectRestrictions);
    }

    /**
     * @see Lookupable#getCreateNewUrl()
     */
    public String getCreateNewUrl() {
        String url = "";

        if (getLookupableHelperService().allowsMaintenanceNewOrCopyAction()) {
            Properties parameters = new Properties();
            parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.MAINTENANCE_NEW_METHOD_TO_CALL);
            parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, this.businessObjectClass.getName());

            url = UrlFactory.parameterizeUrl(KRADConstants.MAINTENANCE_ACTION, parameters);
            url = "<a title=\"Create a new record\" href=\"" + url + "\"><img src=\"images/tinybutton-createnew.gif\" alt=\"create new\" width=\"70\" height=\"15\"/></a>";
        }

        return url;
    }


    /**
     * @see Lookupable#getHtmlMenuBar()
     */
    public String getHtmlMenuBar() {
        return getBusinessObjectDictionaryService().getLookupMenuBar(getBusinessObjectClass());
    }

    /**
     * @see Lookupable#getSupplementalMenuBar()
     */
    public String getSupplementalMenuBar() {
        return getLookupableHelperService().getSupplementalMenuBar();
    }

    /**
     * @see Lookupable#getRows()
     */
    public List<Row> getRows() {
        return getLookupableHelperService().getRows();
    }

    /**
     * @see Lookupable#getTitle()
     */
    public String getTitle() {
        return getLookupableHelperService().getTitle();
    }

    /**
     * @see Lookupable#getReturnLocation()
     */
    public String getReturnLocation() {
        return getLookupableHelperService().getReturnLocation();
    }

    /**
     * @return Returns the dataObjectClass.
     */
    public Class getBusinessObjectClass() {
        return businessObjectClass;
    }

    /**
     * @return a List of the names of fields which are marked in data dictionary as return fields.
     */
    public List<String> getReturnKeys() {
        return getLookupableHelperService().getReturnKeys();
    }


    /**
     * @see Lookupable#getExtraButtonSource()
     */
    public String getExtraButtonSource() {
        return getBusinessObjectDictionaryService().getExtraButtonSource(getBusinessObjectClass());
    }

    /**
     * @see Lookupable#getExtraButtonParams()
     */
    public String getExtraButtonParams() {
        return getBusinessObjectDictionaryService().getExtraButtonParams(getBusinessObjectClass());
    }

    /**
     * @return property names that will be used to sort on by default
     */
    public List<String> getDefaultSortColumns() {
        return getLookupableHelperService().getDefaultSortColumns();
    }

    /**
     * @see Lookupable#checkForAdditionalFields(Map)
     */
    public boolean checkForAdditionalFields(Map<String, String> fieldValues) {
        return getLookupableHelperService().checkForAdditionalFields(fieldValues);
    }

    /**
     * @return Returns the backLocation.
     */
    public String getBackLocation() {
        return getLookupableHelperService().getBackLocation();
    }

    /**
     * @param backLocation The backLocation to set.
     */
    public void setBackLocation(String backLocation) {
        getLookupableHelperService().setBackLocation(backLocation);
    }

    /**
     * @return Returns the docFormKey.
     */
    public String getDocFormKey() {
        return getLookupableHelperService().getDocFormKey();
    }

    /**
     * // this method is public because unit tests depend upon it
     * @param docFormKey The docFormKey to set.
     */
    public void setDocFormKey(String docFormKey) {
        getLookupableHelperService().setDocFormKey(docFormKey);
    }

    /**
     * @return Returns the businessObjectDictionaryService.
     */
    protected BusinessObjectDictionaryService getBusinessObjectDictionaryService() {
        return getLookupableHelperService().getBusinessObjectDictionaryService();
    }

    /**
     * @see Lookupable#setFieldConversions(Map)
     */
    public void setFieldConversions(Map fieldConversions) {
        getLookupableHelperService().setFieldConversions(fieldConversions);
    }

    /**
     * @return Returns the dataDictionaryService.
     */
    protected DataDictionaryService getDataDictionaryService() {
        return getLookupableHelperService().getDataDictionaryService();
    }


    /**
     * Sets the readOnlyFieldsList attribute value.
     *
     * @param readOnlyFieldsList The readOnlyFieldsList to set.
     */
    public void setReadOnlyFieldsList(List<String> readOnlyFieldsList) {
        getLookupableHelperService().setReadOnlyFieldsList(readOnlyFieldsList);
    }


    public LookupableHelperService getLookupableHelperService() {
        return lookupableHelperService;
    }


    /**
     * Sets the lookupableHelperService attribute value.
     * @param lookupableHelperService The lookupableHelperService to set.
     */
    public void setLookupableHelperService(LookupableHelperService lookupableHelperService) {
        this.lookupableHelperService = lookupableHelperService;
    }

    /**
     * Performs a lookup that can only return one row.
     * @see Lookupable#performLookup(org.kuali.rice.krad.web.struts.form.LookupForm, List, boolean)
     */
    public Collection<? extends BusinessObject> performLookup(LookupForm lookupForm, List<ResultRow> resultTable, boolean bounded) {
        return getLookupableHelperService().performLookup(lookupForm, resultTable, bounded);
    }


    public boolean isSearchUsingOnlyPrimaryKeyValues() {
        return getLookupableHelperService().isSearchUsingOnlyPrimaryKeyValues();
    }


    public String getPrimaryKeyFieldLabels() {
        return getLookupableHelperService().getPrimaryKeyFieldLabels();
    }

    /**
     * calls the lookup helper service to do "clear" behaviors
     *
     * @see Lookupable#performClear()
     */
    public void performClear(LookupForm lookupForm) {
         getLookupableHelperService().performClear(lookupForm);
    }

    /**
     * calls the lookup helper service to check if non maintenance actions should be displayed
     *
     * @see Lookupable#shouldDisplayHeaderNonMaintActions()
     */
    public boolean shouldDisplayHeaderNonMaintActions() {
        return getLookupableHelperService().shouldDisplayHeaderNonMaintActions();
    }

    /**
     * calls the lookup helper service to check if criteria should be displayed
     *
     * @see Lookupable#shouldDisplayLookupCriteria()
     */
    public boolean shouldDisplayLookupCriteria() {
        return getLookupableHelperService().shouldDisplayLookupCriteria();
    }

    protected String getCreateNewUrl(String url){
        return "<a title=\"Create a new record\" href=\"" + url + "\"><img src=\"images/tinybutton-createnew.gif\" alt=\"create new\" width=\"70\" height=\"15\"/></a>";
    }

    /**
     * @see Lookupable#performCustomAction(boolean)
     */
    public boolean performCustomAction(boolean ignoreErrors) {
        return getLookupableHelperService().performCustomAction(ignoreErrors);
    }

    /**
     * This overridden method ...
     *
     * @see Lookupable#getExtraField()
     */
    public Field getExtraField() {
        return getLookupableHelperService().getExtraField();
    }

    /**
     * This overridden method ...
     *
     * @see Lookupable#applyFieldAuthorizationsFromNestedLookups(org.kuali.rice.krad.web.ui.Field)
     */
    public void applyFieldAuthorizationsFromNestedLookups(Field field) {
        getLookupableHelperService().applyFieldAuthorizationsFromNestedLookups(field);
    }

    /**
     * This overridden method returns the extraOnLoad variable. The
     * varible is currently accessed in page.tag and is called in the onLoad.
     * it allows us to inject javascript onload.
     *
     * @see Lookupable#getExtraOnLoad()
     */
    public String getExtraOnLoad() {
        return extraOnLoad;
    }

    /**
     * @param extraOnLoad the extraOnLoad to set
     */
    public void setExtraOnLoad(String extraOnLoad) {
        this.extraOnLoad = extraOnLoad;
    }

    /**
     * @see Lookupable#applyConditionalLogicForFieldDisplay()
     */
    public void applyConditionalLogicForFieldDisplay() {
        getLookupableHelperService().applyConditionalLogicForFieldDisplay();
    }

}
