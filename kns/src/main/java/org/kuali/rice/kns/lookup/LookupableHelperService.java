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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface LookupableHelperService extends Serializable{

    /**
     * Initializes the lookup with a business object class.  This is set
     * via the LookupableHelperService's consumer, namely the Lookupable
     * implementation (or in cases of nesting a wrapping LookupableHelperService).
     * The Lookupable in turn receives this value from the UI via
     * LookupForm population.
     *
     * @param businessObjectClass
     */
    public void setBusinessObjectClass(Class businessObjectClass);

    /**
     * @return Returns the dataObjectClass this lookupable is representing
     */
    // NOTE: used in exactly one place in RuleBaseValuesLookupableHelperServiceImpl to conditionally initialize
    // an internal lookup helper service reference; this reference should in theory be a unique instance or prototype
    // so the check should not be necessary
    public Class getBusinessObjectClass();

    /**
     * Initializes the lookup with the given Map of parameters.
     *
     * @param parameters
     */
    public void setParameters(Map<String, String[]> parameters);

    /**
     * @return Returns the parameters passed to this lookup
     */
    public Map<String, String[]> getParameters();

    /**
     * @return String url for the location to return to after the lookup
     */
    public String getReturnLocation();

    /**
     * @return List of Column objects used to render the result table
     */
    public List<Column> getColumns();

    /**
     * Validates the values filled in as search criteria, also checks for required field values.
     *
     * @param fieldValues - Map of property/value pairs
     */
    public void validateSearchParameters(Map<String, String> fieldValues);

    /**
     * Performs a search and returns result list.
     *
     * @param fieldValues - Map of property/value pairs
     * @return List of business objects found by the search
     * @throws Exception
     */
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues);

    /**
     * Similar to getSearchResults, but the number of returned rows is not bounded
     *
     * @param fieldValues
     * @return
     */
    public List<? extends BusinessObject> getSearchResultsUnbounded(Map<String, String> fieldValues);

    /**
     * Determines if there should be more search fields rendered based on already entered search criteria.
     *
     * @param fieldValues - Map of property/value pairs
     * @return boolean
     */
    public boolean checkForAdditionalFields(Map<String, String> fieldValues);

    /**
     * Builds the return value url.
     *
     * @param businessObject - Instance of a business object containing the return values
     * @param fieldConversions - Map of conversions mapping bo names to caller field names.
     * @param lookupImpl - Current lookup impl name
     * @param returnKeys - Keys to return
     * @return String url called when selecting a row from the result set
     */
    public HtmlData getReturnUrl(BusinessObject businessObject, Map fieldConversions, String lookupImpl, List returnKeys, BusinessObjectRestrictions businessObjectRestrictions);

    /**
     * This method builds the return url
     * 
     * @param businessObject
     * @param lookupForm
     * @param returnKeys
     * @return
     */
    public HtmlData getReturnUrl(BusinessObject businessObject, LookupForm lookupForm, List returnKeys, BusinessObjectRestrictions businessObjectRestrictions);
    
    /**
     * Builds string of action urls that can take place for a result row
     *
     * @param businessObject - Instance of a business object containing the return values
     * @param pkNames - List of primary key names
     * @return String rendered in actions column of result set
     */
    public String getActionUrls(BusinessObject businessObject, List pkNames, BusinessObjectRestrictions businessObjectRestrictions);

    /**
     * 
     * This method is a template method that allows child classes to return their own custom action html data.
     * 
     * @param businessObject
     * @param pkNames
     * @return
     */
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames);

    /**
     * Builds string an inquiry url for drill down on a result field
     *
     * @param businessObject - Instance of a business object containing the return values
     * @param propertyName - Name of the property in the business object
     * @return String url called on selection of the result field
     */
    public HtmlData getInquiryUrl(BusinessObject businessObject, String propertyName);

    /**
     * Sets the requested fields conversions in the lookupable
     *
     * @param fieldConversions
     */
    public void setFieldConversions(Map fieldConversions);

    /**
     * Gets the readOnlyFieldsList attribute.
     * @return Returns the readOnlyFieldsList.
     */
    public List<String> getReadOnlyFieldsList();

    /**
     * Sets the requested read only fields list in the lookupable
     *
     * @param readOnlyFieldsList
     */
    public void setReadOnlyFieldsList(List<String> readOnlyFieldsList);

    /**
     * This method is public because some unit tests depend on it.
     *
     * @return a List of the names of fields which are marked in data dictionary as return fields.
     */
    public List<String> getReturnKeys();

    public String getDocFormKey();

    public void setDocFormKey(String docFormKey);

    public String getDocNum();

    public void setDocNum(String docNum);

    /**
     * 
     * This method builds a maintenance url.
     * 
     * @param businessObject
     * @param htmlData
     * @param pkNames
     * @return
     */
    public String getMaintenanceUrl(BusinessObject businessObject, HtmlData htmlData, List pkNames, BusinessObjectRestrictions businessObjectRestrictions);

    /**
     * Determines if underlying lookup bo has associated maintenance document that allows new or copy maintenance actions.
     *
     * @return true if bo has maint doc that allows new or copy actions
     */
    public boolean allowsMaintenanceNewOrCopyAction();

    /**
     * Determines if underlying lookup bo has associated document that allows new or copy maintenance actions.
     *
     * @return true if bo has doc that allows new or copy actions
     */
    public boolean allowsNewOrCopyAction(String documentTypeName);

    /**
     * Returns a list of Row objects to be used to generate the search query screen
     *
     * Generally, setDataObjectClass needs to be called with a non-null value for proper operation
     * @return
     */
    public List<Row> getRows();

    /**
     * This method returns the DataDictionaryService used to initialize this helper service and is used by Lookupable implementations to
     * retrieve the proper service.
     *
     * @return
     */
    public DataDictionaryService getDataDictionaryService();

    /**
     * This method returns the BusinessObjectDictionaryService used to initialize this helper service and is used by Lookupable implementations to
     * retrieve the proper service.
     *
     * @return
     */
    public BusinessObjectDictionaryService getBusinessObjectDictionaryService();

    public void setBackLocation(String backLocation);

    public String getBackLocation();

    /**
     *
     * This method performs the lookup and returns a collection of BO items
     * @param lookupForm
     * @param resultTable
     * @param bounded
     * @return the list of result BOs, possibly bounded
     */
    public Collection<? extends BusinessObject> performLookup(LookupForm lookupForm, Collection<ResultRow> resultTable, boolean bounded);

    /**
     * This method returns a list of the default columns used to sort the result set.  For multiple value lookups,
     * this method does not change when different columns are sorted.
     *
     * @return
     */
    public List<String> getDefaultSortColumns();

    /**
     * This method returns whether the previously executed getSearchResults used the primary key values to search, ignoring all non key values
     *
     * @return
     * @see LookupableHelperService#getPrimaryKeyFieldLabels()
     */
    public boolean isSearchUsingOnlyPrimaryKeyValues();

    /**
     * Returns a comma delimited list of primary key field labels, to be used on the UI to tell the user which fields were used to search
     *
     * @return
     * @see LookupableHelperService#isSearchUsingOnlyPrimaryKeyValues()
     */
    public String getPrimaryKeyFieldLabels();

    /**
     * Determines whether a given BusinessObject that's returned as one of the lookup's results is considered returnable, which means that for
     * single-value lookups, a "return value" link may be rendered, and for multiple value lookups, a checkbox is rendered.
     *
     * Note that this can be part of an authorization mechanism, but not the complete authorization mechanism.  The component that invoked the lookup/
     * lookup caller (e.g. document, nesting lookup, etc.) needs to check that the object that was passed to it was returnable as well because there
     * are ways around this method (e.g. crafting a custom return URL).
     *
     * @param object an object from the search result set
     * @return
     */
    public boolean isResultReturnable(BusinessObject object);
    
    /**
     * 
     * This method allows for overriding the clear behavior
     *
     */
    public void performClear(LookupForm lookupForm);
    
    public boolean shouldDisplayHeaderNonMaintActions();
    
    public boolean shouldDisplayLookupCriteria();

	/**
	 * This method gets the supplemental lookup menu if any
	 * 
	 * @return supplemental menu bar
	 */
	public String getSupplementalMenuBar();
    
    /**
     * @return String displayed as title for the lookup
     */
    public String getTitle();
    
    /**
     * 
     * performs custom actions.  return true to reperform search
     * 
     * @param ignoreErrors
     * @return boolean to reperform search
     */
    public boolean performCustomAction(boolean ignoreErrors);
    
    /**
     * get an extra field
     * @return
     */
    public Field getExtraField();
    
    public void applyFieldAuthorizationsFromNestedLookups(Field field);
    
    /**
     * Performs conditional logic (based on current search values or other parameters) to
     * override field hidden, read-only, and required attributes previously set.
     */
    public void applyConditionalLogicForFieldDisplay();
}
