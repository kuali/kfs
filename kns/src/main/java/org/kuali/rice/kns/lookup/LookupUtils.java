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

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.kuali.rice.core.api.search.Range;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.framework.persistence.platform.DatabasePlatform;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.service.BusinessObjectMetaDataService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.comparator.NullValueComparator;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.DataObjectRelationship;
import org.kuali.rice.krad.datadictionary.RelationshipDefinition;
import org.kuali.rice.krad.datadictionary.control.ControlDefinition;
import org.kuali.rice.krad.exception.ClassNotPersistableException;
import org.kuali.rice.krad.lookup.SelectiveReferenceRefresher;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Utility class for Lookup related utilities and helper methods.
 */
public class LookupUtils {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LookupUtils.class);

    public LookupUtils() {
        // default constructor for Spring to call to start up initialization process
    }

    /**
     * Removes fields identified in the data dictionary as hidden from the lookup field values.
     * (This will remove Universal User ID and Person name from search requests when a user ID is entered.)
     *
     * @param fieldValues
     */
    public static void removeHiddenCriteriaFields(Class businessObjectClass, Map fieldValues) {
        List<String> lookupFieldAttributeList =
                getBusinessObjectMetaDataService().getLookupableFieldNames(businessObjectClass);
        if (lookupFieldAttributeList != null) {
            for (Iterator iter = lookupFieldAttributeList.iterator(); iter.hasNext(); ) {
                String attributeName = (String) iter.next();
                if (fieldValues.containsKey(attributeName)) {
                    ControlDefinition controlDef = getDataDictionaryService()
                            .getAttributeControlDefinition(businessObjectClass, attributeName);
                    if (controlDef != null && controlDef.isHidden()) {
                        fieldValues.remove(attributeName);
                    }
                }
            }
        }
    }

    /**
     * Parses and returns the lookup result set limit, checking first for the limit
     * for the BO being looked up, and then the global application limit if there isn't a limit
     * specific to this BO.
     *
     * @param businessObjectClass BO class to search on / get limit for.  If the passed in type is not of type
     * {@link BusinessObject}, then the application-wide default limit is used.
     * @return result set limit (or null if there isn't one)
     */
    public static Integer getSearchResultsLimit(Class businessObjectClass) {
        Integer limit = null;
        if (BusinessObject.class.isAssignableFrom(businessObjectClass)) {
            limit = getBusinessObjectSearchResultsLimit(businessObjectClass);
        }
        if (limit == null) {
            limit = getApplicationSearchResultsLimit();
        }
        return limit;
    }

    /**
     *
     */
    public static Integer getApplicationSearchResultsLimit() {
        String limitString = CoreFrameworkServiceLocator.getParameterService()
                .getParameterValueAsString(KRADConstants.KNS_NAMESPACE,
                        KRADConstants.DetailTypes.LOOKUP_PARM_DETAIL_TYPE,
                        KRADConstants.SystemGroupParameterNames.LOOKUP_RESULTS_LIMIT);
        if (limitString != null) {
            return Integer.valueOf(limitString);
        }
        return null;
    }

    /**
     * Parses and returns the lookup result set limit for the passed in BO (if one exists)
     *
     * @param businessObjectClass
     * @return result set limit for this BO (or null if the BO doesn't have a limit)
     */
    public static Integer getBusinessObjectSearchResultsLimit(Class businessObjectClass) {
		if (!(isMultipleValueLookup())) {
			return getBusinessObjectDictionaryService().getLookupResultSetLimit(businessObjectClass);
		} else {
	        return getBusinessObjectDictionaryService().getMultipleValueLookupResultSetLimit(businessObjectClass);
		}
    }

	private static boolean isMultipleValueLookup() {
		KualiForm kualiForm = KNSGlobalVariables.getKualiForm();
		if (kualiForm instanceof LookupForm) {
			LookupForm lookupForm = (LookupForm) kualiForm;
			return lookupForm.isMultipleValues();
		} else {
			return false;
		}
	} 
	
    /**
     * This method applies the search results limit to the search criteria for this BO
     *
     * @param businessObjectClass BO class to search on / get limit for
     * @param criteria search criteria
     * @param platform database platform
     */
    public static void applySearchResultsLimit(Class businessObjectClass, Criteria criteria,
            DatabasePlatform platform) {
        Integer limit = getSearchResultsLimit(businessObjectClass);
        if (limit != null) {
            platform.applyLimit(limit, criteria);
        }
    }

    /**
     * Applies the search results limit to the search criteria for this BO (JPA)
     *
     * @param businessObjectClass BO class to search on / get limit for
     * @param criteria search criteria
     */
    public static void applySearchResultsLimit(Class businessObjectClass,
            org.kuali.rice.core.framework.persistence.jpa.criteria.Criteria criteria) {
        Integer limit = getSearchResultsLimit(businessObjectClass);
        if (limit != null) {
            criteria.setSearchLimit(limit);
        }
    }

    /**
     * This method the maximum rows per page in a multiple value lookup
     *
     * @see org.kuali.KRADConstants.SystemGroupParameterNames#MULTIPLE_VALUE_LOOKUP_RESULTS_PER_PAGE
     * @return
     */
    public static Integer getApplicationMaximumSearchResulsPerPageForMultipleValueLookups() {
        String limitString = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(KRADConstants.KNS_NAMESPACE, KRADConstants.DetailTypes.LOOKUP_PARM_DETAIL_TYPE, KRADConstants.SystemGroupParameterNames.MULTIPLE_VALUE_LOOKUP_RESULTS_PER_PAGE);
        if (limitString != null) {
            return Integer.valueOf(limitString);
        }
        return null;
    }

    /**
     * This makes a , delimited String list of fields separated by a , into a List of target --> lookup readOnlyFieldsList.
     *
     * @param readOnlyFields
     * @return the List representation of the readOnlyFields  String provided.
     */
    public static List<String> translateReadOnlyFieldsToList(String readOnlyFieldsString) {
        List<String> readOnlyFieldsList = new ArrayList<String>();
        if (StringUtils.isNotEmpty(readOnlyFieldsString)) {
            if (readOnlyFieldsString.indexOf(",") > 0) {
                StringTokenizer token = new StringTokenizer(readOnlyFieldsString, ",");
                while (token.hasMoreTokens()) {
                    String element = token.nextToken();
                    readOnlyFieldsList.add(element);
                }
            }
            else {
                readOnlyFieldsList.add(readOnlyFieldsString);
            }
        }
      return readOnlyFieldsList;
    }

    /**
     * This translates a , delimited list of pairs separated by a : into a Map of target --> lookup field conversions.
     *
     * @param conversionFields
     * @return the Map representation of the fieldConversions String provided.
     */
    public static Map<String, String> translateFieldConversions(String fieldConversionsString) {
        Map<String, String> fieldConversionsMap = new HashMap();
        if (StringUtils.isNotEmpty(fieldConversionsString)) {
            if (fieldConversionsString.indexOf(",") > 0) {
                StringTokenizer token = new StringTokenizer(fieldConversionsString, ",");
                while (token.hasMoreTokens()) {
                    String element = token.nextToken();
                    fieldConversionsMap.put(element.substring(0, element.indexOf(":")), element.substring(element.indexOf(":") + 1));
                }
            }
            else {
                fieldConversionsMap.put(fieldConversionsString.substring(0, fieldConversionsString.indexOf(":")), fieldConversionsString.substring(fieldConversionsString.indexOf(":") + 1));
            }
        }
        return fieldConversionsMap;
    }

    @Deprecated
    public static Field setFieldQuickfinder(BusinessObject businessObject,
            String attributeName, Field field, List displayedFieldNames) {
        return setFieldQuickfinder( businessObject, (String)null, false, 0, attributeName, field, displayedFieldNames );
    }

    @Deprecated
    public static Field setFieldQuickfinder(BusinessObject businessObject,
            String attributeName, Field field, List displayedFieldNames, SelectiveReferenceRefresher srr) {
        return setFieldQuickfinder( businessObject, (String)null, false, 0, attributeName, field, displayedFieldNames, srr );
    }

    /**
     * Sets a fields quickfinder class and field conversions for an attribute.
     */
    @Deprecated
    public static Field setFieldQuickfinder(BusinessObject businessObject, String collectionName, boolean addLine, int index,
            String attributeName, Field field, List displayedFieldNames, SelectiveReferenceRefresher srr) {
        field = setFieldQuickfinder(businessObject, collectionName, addLine, index, attributeName, field, displayedFieldNames);
        if (srr != null) {
            String collectionPrefix = "";
            if ( collectionName != null ) {
                if (addLine) {
                    collectionPrefix = KRADConstants.MAINTENANCE_ADD_PREFIX + collectionName + ".";
                }
                else {
                    collectionPrefix = collectionName + "[" + index + "].";
                }
            }
            field.setReferencesToRefresh(convertReferencesToSelectCollectionToString(
                    srr.getAffectedReferencesFromLookup(businessObject, attributeName, collectionPrefix)));
        }
        return field;
    }

    /**
     * Sets a fields quickfinder class and field conversions for an attribute.
     */
    @Deprecated
    public static Field setFieldQuickfinder(BusinessObject businessObject, String collectionName, boolean addLine, int index,
                                            String attributeName, Field field, List displayedFieldNames) {
        boolean noLookup = false;
        if (businessObject == null) {
            return field;
        }

        Boolean noLookupField = getBusinessObjectDictionaryService().noLookupFieldLookup(businessObject.getClass(), attributeName);
        if (noLookupField != null && noLookupField) {
            noLookup = true;
        }

         return setFieldQuickfinder(businessObject, collectionName, addLine, index, attributeName, field, displayedFieldNames, noLookup);

    }

    @Deprecated
    public static Field setFieldQuickfinder(BusinessObject businessObject, String collectionName, boolean addLine, int index, String attributeName, Field field, List displayedFieldNames, boolean noLookupField)
    {
         if (businessObject == null) {
            return field;
        }

        if (noLookupField) {
            return field;
        }
        DataObjectRelationship relationship = null;
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "setFieldQuickfinder("+businessObject.getClass().getName()+","+attributeName+","+field+","+displayedFieldNames+")" );
        }

        relationship = getBusinessObjectMetaDataService().getBusinessObjectRelationship(businessObject, businessObject.getClass(), attributeName, "", false);

        String collectionPrefix = "";
        if ( collectionName != null ) {
            if (addLine) {
                collectionPrefix = KRADConstants.MAINTENANCE_ADD_PREFIX + collectionName + ".";
            }
            else {
                collectionPrefix = collectionName + "[" + index + "].";
            }
        }

        if (relationship == null) {
            Class c = ObjectUtils.getPropertyType(businessObject, attributeName, getPersistenceStructureService());

            if(c!=null) {
                if (attributeName.contains(".")) {
                    attributeName = StringUtils.substringBeforeLast( attributeName, "." );
                }

                RelationshipDefinition ddReference = getBusinessObjectMetaDataService().getBusinessObjectRelationshipDefinition(businessObject, attributeName);
                relationship = getBusinessObjectMetaDataService().getBusinessObjectRelationship(ddReference, businessObject, businessObject.getClass(), attributeName, "", false);
                if(relationship!=null) {
                    field.setQuickFinderClassNameImpl(relationship.getRelatedClass().getName());
                    field.setFieldConversions(generateFieldConversions( businessObject, collectionPrefix, relationship, field.getPropertyPrefix(), displayedFieldNames, null));
                    field.setLookupParameters(generateLookupParameters( businessObject, collectionPrefix, relationship, field.getPropertyPrefix(), displayedFieldNames, null));
                    field.setBaseLookupUrl(LookupUtils.getBaseLookupUrl(false));
                    field.setImageSrc(getBusinessObjectDictionaryService().getSearchIconOverride(businessObject.getClass()));
                }
            }

            return field;
        }
        if (ObjectUtils.isNestedAttribute(attributeName)) {
            //first determine the prefix and the attribute we are referring to
            String nestedAttributePrefix = StringUtils.substringBeforeLast(attributeName, ".");

            field.setQuickFinderClassNameImpl(relationship.getRelatedClass().getName());
            field.setFieldConversions( generateFieldConversions( businessObject, collectionPrefix, relationship, field.getPropertyPrefix(), displayedFieldNames, nestedAttributePrefix ) );
            field.setLookupParameters( generateLookupParameters( businessObject, collectionPrefix, relationship, field.getPropertyPrefix(), displayedFieldNames, nestedAttributePrefix ) );
            field.setBaseLookupUrl(LookupUtils.getBaseLookupUrl(false));
        } else {
            field.setQuickFinderClassNameImpl(relationship.getRelatedClass().getName());
            field.setFieldConversions( generateFieldConversions( businessObject, collectionPrefix, relationship, field.getPropertyPrefix(), displayedFieldNames, null ) );
            field.setLookupParameters( generateLookupParameters( businessObject, collectionPrefix, relationship, field.getPropertyPrefix(), displayedFieldNames, null ) );
            field.setBaseLookupUrl(LookupUtils.getBaseLookupUrl(false));
        }
        field.setImageSrc(getBusinessObjectDictionaryService().getSearchIconOverride(businessObject.getClass()));

        return field;
    }

    private static String BASE_LOOKUP_ACTION_URL = null;
    private static String BASE_MULTIPLE_VALUE_LOOKUP_ACTION_URL = null;
    private static String BASE_INQUIRY_ACTION_URL = null;

    /**
     * @see org.kuali.rice.krad.uif.util.LookupInquiryUtils#getBaseLookupUrl()
     */
    @Deprecated
    public static String getBaseLookupUrl(boolean isMultipleValue) {
        ConfigurationService kualiConfigurationService = KRADServiceLocator.getKualiConfigurationService();
    	if ( isMultipleValue ) {
    		if ( BASE_MULTIPLE_VALUE_LOOKUP_ACTION_URL == null ) {
    			String lookupUrl = kualiConfigurationService.getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY);
    			if (!lookupUrl.endsWith("/")) {
    				lookupUrl = lookupUrl + "/";
    			}
				lookupUrl += "kr/" + KRADConstants.MULTIPLE_VALUE_LOOKUP_ACTION;
				BASE_MULTIPLE_VALUE_LOOKUP_ACTION_URL = lookupUrl;
    		}
    		return BASE_MULTIPLE_VALUE_LOOKUP_ACTION_URL;
    	} else {
    		if ( BASE_LOOKUP_ACTION_URL == null ) {
    			String lookupUrl = kualiConfigurationService.getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY);
    			if (!lookupUrl.endsWith("/")) {
    				lookupUrl = lookupUrl + "/";
    			}
				lookupUrl += "kr/" + KRADConstants.LOOKUP_ACTION;
				BASE_LOOKUP_ACTION_URL = lookupUrl;
    		}
    		return BASE_LOOKUP_ACTION_URL;
    	}
    }

    @Deprecated
    public static String getBaseInquiryUrl() {
    	if ( BASE_INQUIRY_ACTION_URL == null ) {
	    	StringBuffer inquiryUrl = new StringBuffer( 
	    			KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(
                            KRADConstants.APPLICATION_URL_KEY) );
			if (inquiryUrl.charAt(inquiryUrl.length()-1) != '/' ) {
				inquiryUrl.append( '/' );
			}
			inquiryUrl.append("kr/");
			inquiryUrl.append( KRADConstants.INQUIRY_ACTION );
			BASE_INQUIRY_ACTION_URL = inquiryUrl.toString();
    	}
    	return BASE_INQUIRY_ACTION_URL;
    }

    public static String transformLookupUrlToMultiple(String lookupUrl) {
    	return lookupUrl.replace("kr/" + KRADConstants.LOOKUP_ACTION, "kr/" + KRADConstants.MULTIPLE_VALUE_LOOKUP_ACTION);
    }

    /**
     * Sets whether a field should have direct inquiries enabled.  The direct inquiry is the functionality on a page such that if the primary key for
     * a quickfinder is filled in and the direct inquiry button is pressed, then a new window will popup showing an inquiry page without going through
     * the lookup first.
     *
     * For this method to work properly, it must be called after setFieldQuickfinder
     * //TODO: chb: that should not be the case -- the relationship object the two rely upon should be established outside of the lookup/quickfinder code
     *
     *
     * @param field
     */
    private static void setFieldDirectInquiry(Field field) {
        if (StringUtils.isNotBlank(field.getFieldConversions())) {
            boolean directInquiriesEnabled = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(
                    KRADConstants.KNS_NAMESPACE, KRADConstants.DetailTypes.ALL_DETAIL_TYPE, KRADConstants.SystemGroupParameterNames.ENABLE_DIRECT_INQUIRIES_IND);
            if (directInquiriesEnabled) {
                if (StringUtils.isNotBlank(field.getFieldConversions())) {
                    String fieldConversions = field.getFieldConversions();
                    String newInquiryParameters = KRADConstants.EMPTY_STRING;
                    String[] conversions = StringUtils.split(fieldConversions, KRADConstants.FIELD_CONVERSIONS_SEPARATOR);

                    for (int l = 0; l < conversions.length; l++) {
                        String conversion = conversions[l];
                        //String[] conversionPair = StringUtils.split(conversion, KRADConstants.FIELD_CONVERSION_PAIR_SEPARATOR);
                        String[] conversionPair = StringUtils.split(conversion, KRADConstants.FIELD_CONVERSION_PAIR_SEPARATOR, 2);
                        String conversionFrom = conversionPair[0];
                        String conversionTo = conversionPair[1];
                        newInquiryParameters += (conversionTo + KRADConstants.FIELD_CONVERSION_PAIR_SEPARATOR + conversionFrom);

                        if (l < conversions.length - 1) {
                            newInquiryParameters += KRADConstants.FIELD_CONVERSIONS_SEPARATOR;
                        }
                    }

                    field.setInquiryParameters(newInquiryParameters);
                }
            }
            field.setFieldDirectInquiryEnabled(directInquiriesEnabled);
        }
        else {
            field.setFieldDirectInquiryEnabled(false);
        }
    }

    /**
     *
     * @param field
     * @return the altered Field object
     */
    public static Field setFieldDirectInquiry(BusinessObject businessObject, String attributeName, Field field)
    {
		if (businessObject == null)
		{
            return field;
        }

        Boolean noDirectInquiry = getBusinessObjectDictionaryService().noDirectInquiryFieldLookup(businessObject.getClass(), attributeName);
        //check if noDirectInquiry is present and true, but if it's not set in existing data dictionary definitions, don't create a direct inquiry
        if (noDirectInquiry != null && noDirectInquiry.booleanValue() || noDirectInquiry == null) {
            return field;
        }

        setFieldDirectInquiry(field);

        return field;
    }

    private static Map<Class,Map<String,Map>> referencesForForeignKey = new HashMap<Class, Map<String,Map>>();

    @Deprecated
    public static Map getPrimitiveReference(BusinessObject businessObject, String attributeName) {
        Map chosenReferenceByKeySize = new HashMap();
        Map chosenReferenceByFieldName = new HashMap();

        Map referenceClasses = null;

        try {
            // add special caching of these relationships since the Spring caching is so expensive
            Map<String,Map> propMap = referencesForForeignKey.get(businessObject.getClass());
            if ( propMap == null ) {
                propMap = new HashMap<String, Map>();
                referencesForForeignKey.put(businessObject.getClass(), propMap);
            }
            if ( propMap.containsKey(attributeName) ) {
                referenceClasses = propMap.get( attributeName );
            } else {
            	//KFSMI-709: Make Inquiry Framework use BusinessObjectMetadataService instead of just PersistenceStructureService
            	referenceClasses = getBusinessObjectMetaDataService().getReferencesForForeignKey(businessObject, attributeName);
            	if(referenceClasses==null || referenceClasses.isEmpty()) {
            	    if ( getPersistenceStructureService().isPersistable(businessObject.getClass()) ) {
            	        referenceClasses = getPersistenceStructureService().getReferencesForForeignKey(businessObject.getClass(), attributeName);
            	    }
            	}
                propMap.put(attributeName, referenceClasses);
            }
        } catch ( ClassNotPersistableException ex ) {
            // do nothing, there is no quickfinder
            Map<String,Map> propMap = referencesForForeignKey.get(businessObject.getClass());
            propMap.put(attributeName, null);
        }

        // if field is not fk to any reference class, return field object w no quickfinder
        if (referenceClasses == null || referenceClasses.isEmpty()) {
            return chosenReferenceByKeySize;
        }

        /*
         * if field is fk to more than one reference, take the class with the least # of pk fields, this should give the correct
         * grain for the attribute
         */
        int minKeys = Integer.MAX_VALUE;
        for (Iterator iter = referenceClasses.keySet().iterator(); iter.hasNext();) {
            String attr = (String) iter.next();
            Class clazz = (Class) referenceClasses.get(attr);
            List pkNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(clazz);

            // Compare based on key size.
            if (pkNames.size() < minKeys) {
                minKeys = pkNames.size();
                chosenReferenceByKeySize.clear();
                chosenReferenceByKeySize.put(attr, clazz);
            }

            // Compare based on field name.
            if (attributeName.startsWith(attr)) {
                chosenReferenceByFieldName.clear();
                chosenReferenceByFieldName.put(attr, clazz);
            }
        }

        // If a compatible key was found based on field names, prefer it, otherwise use choice by key size.
        return chosenReferenceByFieldName.isEmpty() ? chosenReferenceByKeySize : chosenReferenceByFieldName;
    }

    /**
     *
     * This method walks through the nested attribute and finds the last business object in the chain and returns it (excluding the
     * last parameter which is the actual attribute)
     *
     * @param attributeName
     * @return
     */
    public static BusinessObject getNestedBusinessObject(BusinessObject bo, String attributeName) {
        String[] nestedAttributes = StringUtils.split(attributeName, ".");

        BusinessObject childBO = null;
        String attributeRefName = "";
        Class clazz = null;
        if (nestedAttributes.length > 1) {
            String attributeStringSoFar = "";
            for (int i = 0; i < nestedAttributes.length - 1; i++) {
                // we need to build a string of the attribute names depending on which iteration we're in.
                // so if the original attributeName string we're using is "a.b.c.d.e", then first iteration would use
                // "a", 2nd "a.b", 3rd "a.b.c", etc.
                if (i != 0) {
                    attributeStringSoFar = attributeStringSoFar + ".";
                }
                attributeStringSoFar = attributeStringSoFar + nestedAttributes[i];

                clazz = ObjectUtils.getPropertyType( bo, attributeStringSoFar, getPersistenceStructureService() );

                if (clazz != null && BusinessObject.class.isAssignableFrom(clazz)) {
                    try {
                    	childBO = (BusinessObject) ObjectUtils.createNewObjectFromClass(clazz);
                    }
                    catch (Exception e) {
                        return null;
                    }
                }
            }
        }
        return childBO;
    }

    public static Class getNestedReferenceClass(BusinessObject businessObject, String attributeName) {
        BusinessObject bo = getNestedBusinessObject(businessObject, attributeName);
        return null == bo ? null : bo.getClass();
    }

    @Deprecated
    private static String generateFieldConversions(BusinessObject businessObject, String collectionName, DataObjectRelationship relationship, String propertyPrefix, List displayedFieldNames, String nestedObjectPrefix) {
        String fieldConversions = "";

        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "generateFieldConversions(" + businessObject.getClass().getName() + "," + collectionName + ",\n" + relationship + "\n," + propertyPrefix + "," + displayedFieldNames + "," + nestedObjectPrefix + ")" );
        }

        // get the references for the given property
        for ( Map.Entry<String,String> entry : relationship.getParentToChildReferences().entrySet() ) {
            String fromField = entry.getValue();
            String toField = entry.getKey();

            // find the displayed to field mapping
            if (!displayedFieldNames.contains(toField)) {
                toField = translateToDisplayedField(businessObject.getClass(), toField, displayedFieldNames);
            }

            if (StringUtils.isNotBlank(fieldConversions)) {
                fieldConversions += ",";
            }

            if ( StringUtils.isNotEmpty( propertyPrefix ) ) {
                toField = propertyPrefix + "." + toField;
            }

            if ( StringUtils.isNotEmpty( collectionName ) ) {
                toField = collectionName + toField;
            }

            fieldConversions += fromField + ":" + toField;
        }

        return fieldConversions;
    }

    @Deprecated
    private static String generateLookupParameters(BusinessObject businessObject, String collectionName, DataObjectRelationship relationship, String propertyPrefix, List displayedFieldNames, String nestedObjectPrefix) {

        String lookupParameters = "";

        List displayedQFFieldNames = getBusinessObjectDictionaryService().getLookupFieldNames(relationship.getRelatedClass());
        for ( Map.Entry<String,String> entry : relationship.getParentToChildReferences().entrySet() ) {
            String fromField = entry.getKey();
            String toField = entry.getValue();

            if ( relationship.getUserVisibleIdentifierKey() == null || relationship.getUserVisibleIdentifierKey().equals( fromField ) ) {
                // find the displayed from field mapping
                if (!displayedFieldNames.contains(fromField)) {
                    fromField = translateToDisplayedField(businessObject.getClass(), fromField, displayedFieldNames);
                }

                // translate to field
                if (displayedQFFieldNames != null && !displayedQFFieldNames.contains(toField)) {
                    toField = translateToDisplayedField(relationship.getRelatedClass(), toField, displayedQFFieldNames);
                }

                if (StringUtils.isNotBlank(lookupParameters)) {
                    lookupParameters += ",";
                }

                if (propertyPrefix != null && !propertyPrefix.equals("")) {
                    fromField = propertyPrefix + "." + fromField;
                }

                if ( StringUtils.isNotEmpty( collectionName ) ) {
                    fromField = collectionName + fromField;
                }

                lookupParameters += fromField + ":" + toField;
            }
        }

        return lookupParameters;
    }

    @Deprecated
    private static String translateToDisplayedField(Class businessObjectClass, String fieldName, List displayedFieldNames) {        
        if ( getPersistenceStructureService().isPersistable(businessObjectClass) ) {
            Map nestedFkMap = getPersistenceStructureService().getNestedForeignKeyMap(businessObjectClass);

            // translate to primitive fk if nested
            /*
             * if (ObjectUtils.isNestedAttribute(fieldName) && nestedFkMap.containsKey(fieldName)) { fieldName = (String)
             * nestedFkMap.get(fieldName); }
             */

            if (!displayedFieldNames.contains(fieldName)) {
                for (Iterator iterator = displayedFieldNames.iterator(); iterator.hasNext();) {
                    String dispField = (String) iterator.next();

                    if (nestedFkMap.containsKey(dispField) && nestedFkMap.get(dispField).equals(fieldName)) {
                        fieldName = dispField;
                    }
                }
            }
        }

        return fieldName;
    }

    public static String convertReferencesToSelectCollectionToString(Collection<String> referencesToRefresh) {
        StringBuilder buf = new StringBuilder();
        for (String reference : referencesToRefresh) {
            buf.append(reference).append(KRADConstants.REFERENCES_TO_REFRESH_SEPARATOR);
        }
        if (!referencesToRefresh.isEmpty()) {
            // we appended one too many separators, remove it
            buf.delete(buf.length() - KRADConstants.REFERENCES_TO_REFRESH_SEPARATOR.length(), buf.length());
        }
        return buf.toString();
    }

    public static String convertSetOfObjectIdsToString(Set<String> objectIds) {
        if (objectIds.isEmpty()) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        for (String objectId : objectIds) {
            if (objectId.contains(KRADConstants.MULTIPLE_VALUE_LOOKUP_OBJ_IDS_SEPARATOR)) {
                throw new RuntimeException("object ID " + objectId + " contains the selected obj ID separator");
            }
            buf.append(objectId).append(KRADConstants.MULTIPLE_VALUE_LOOKUP_OBJ_IDS_SEPARATOR);
        }
        // added one extra separator, remove it
        buf.delete(buf.length() - KRADConstants.MULTIPLE_VALUE_LOOKUP_OBJ_IDS_SEPARATOR.length(), buf.length());

        return buf.toString();
    }

    public static Set<String> convertStringOfObjectIdsToSet(String objectIdsString) {
        Set<String> set = new HashSet<String>();

        if (StringUtils.isNotBlank(objectIdsString)) {
            String[] objectIds = StringUtils.splitByWholeSeparator(objectIdsString, KRADConstants.MULTIPLE_VALUE_LOOKUP_OBJ_IDS_SEPARATOR);
            for (String objectId : objectIds) {
                set.add(objectId);
            }
        }
        return set;
    }

    /**
     * Given a list of results from a lookup, determines the best comparator to use on the String values of each of these columns
     *
     * This method exists because each cell (represented by the Column object) lists the comparator that should be used within it based on the property value class,
     * so we gotta go thru the whole list and determine the best comparator to use
     *
     * @param resultsTable
     * @param column
     * @return
     */
    public static Comparator findBestValueComparatorForColumn(List<ResultRow> resultTable, int column) {
        // BIG HACK
        Comparator comp = NullValueComparator.getInstance();
        for (ResultRow row : resultTable) {
            Comparator tempComp = row.getColumns().get(column).getValueComparator();
            if (tempComp != null && !NullValueComparator.class.equals(tempComp.getClass())) {
                return tempComp;
            }
        }
        return comp;
    }
    /**
     * Changes ranged search fields like from/to dates into the range operators the lookupable dao expects
     * ("..",">" etc) this method modifies the passed in map and returns a list containing only the modified fields
     *
     * This method does not handle document searchable attributes.  This is handled in a second pass by the docsearch-specific
     * DocumentSearchCriteriaTranslator
     */
    public static Map<String, String> preProcessRangeFields(Map<String, String> lookupFormFields) {
        Map<String, String> fieldsToUpdate = new HashMap<String, String>();
        Set<String> fieldsForLookup = lookupFormFields.keySet();
        for (String propName : fieldsForLookup) {
            if (propName.startsWith(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX)) {
                String rangedLowerBoundValue = lookupFormFields.get(propName);
                String rangedFieldName = StringUtils.remove(propName, KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX);
                String rangedValue = lookupFormFields.get(rangedFieldName);

                Range range = new Range();
                // defaults for general lookup/search
                range.setLowerBoundInclusive(true);
                range.setUpperBoundInclusive(true);
                range.setLowerBoundValue(rangedLowerBoundValue);
                range.setUpperBoundValue(rangedValue);

                 String expr = range.toString();
                if (StringUtils.isEmpty(expr)) {
                    expr = rangedValue;
                }

                fieldsToUpdate.put(rangedFieldName, expr);
            }
        }
        //update lookup values from found ranged values to update
        Set<String> keysToUpdate = fieldsToUpdate.keySet();
        for (String updateKey : keysToUpdate) {
            lookupFormFields.put(updateKey, fieldsToUpdate.get(updateKey));
        }
        return fieldsToUpdate;
    }

    /**
     * Given 3 sets of object IDs: the set of selected object IDs before rendering the current page,
     * the set of object IDs rendered on the page, and the set of object IDs selected on the page, computes
     * the total set of selected object IDs.
     *
     * Instead of storing it in a set, returns it in a map with the selected object ID as both the key and value
     * @param previouslySelectedObjectIds
     * @param displayedObjectIds
     * @param selectedObjectIds
     * @return
     */
    public static Map<String, String> generateCompositeSelectedObjectIds(Set<String> previouslySelectedObjectIds, Set<String> displayedObjectIds, Set<String> selectedObjectIds) {
        Map<String, String> tempMap = new HashMap<String, String>();
        // Equivalent to the set operation:
        // (P - D) union C, where - is the set difference operator
        // P is the list of object IDs previously passed in, D is the set of displayed object IDs, and C is the set of checked obj IDs
        // since HTML does not pass a value for non-selected dcheckboxes

        // first build a map w/ all the previouslySelectedObjectIds as keys
        for (String previouslySelectedObjectId : previouslySelectedObjectIds) {
            tempMap.put(previouslySelectedObjectId, previouslySelectedObjectId);
        }
        // then remove all the displayed elements (any selected displayed elements will be added back in the next loop)
        for (String displayedObjectId : displayedObjectIds) {
            tempMap.remove(displayedObjectId);
        }
        // put back the selected IDs
        for (String selectedObjectId : selectedObjectIds) {
            tempMap.put(selectedObjectId, selectedObjectId);
        }
        return tempMap;
    }

    public static DataDictionaryService getDataDictionaryService() {
        return KRADServiceLocatorWeb.getDataDictionaryService();
    }

    public static PersistenceStructureService getPersistenceStructureService() {
        return KRADServiceLocator.getPersistenceStructureService();
    }

    public static BusinessObjectDictionaryService getBusinessObjectDictionaryService() {
        return KNSServiceLocator.getBusinessObjectDictionaryService();
    }

    public static BusinessObjectMetaDataService getBusinessObjectMetaDataService() {
        return KNSServiceLocator.getBusinessObjectMetaDataService();
    }

    public static DateTimeService getDateTimeService() {
        return CoreApiServiceLocator.getDateTimeService();
    }
}
