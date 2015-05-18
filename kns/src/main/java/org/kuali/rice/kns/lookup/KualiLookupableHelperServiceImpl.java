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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.krad.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.datadictionary.RelationshipDefinition;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.BeanPropertyComparator;
import org.kuali.rice.krad.util.ExternalizableBusinessObjectUtils;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Transactional
public class KualiLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {

    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiLookupableHelperServiceImpl.class);
    protected boolean searchUsingOnlyPrimaryKeyValues = false;


    /**
     * Uses Lookup Service to provide a basic search.
     *
     * @param fieldValues - Map containing prop name keys and search values
     *
     * @return List found business objects
     * @see LookupableHelperService#getSearchResults(Map)
     */
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        return getSearchResultsHelper(
                org.kuali.rice.krad.lookup.LookupUtils.forceUppercase(getBusinessObjectClass(), fieldValues), false);
    }


    /**
     * Uses Lookup Service to provide a basic unbounded search.
     *
     * @param fieldValues - Map containing prop name keys and search values
     *
     * @return List found business objects
     * @see LookupableHelperService#getSearchResultsUnbounded(Map)
     */
    public List<? extends BusinessObject> getSearchResultsUnbounded(Map<String, String> fieldValues) {
        return getSearchResultsHelper(
                org.kuali.rice.krad.lookup.LookupUtils.forceUppercase(getBusinessObjectClass(), fieldValues), true);
    }

    // TODO: Fix? - this does not handle nested properties within the EBO.

    /**
     * Check whether the given property represents a property within an EBO starting
     * with the sampleBo object given.  This is used to determine if a criteria needs
     * to be applied to the EBO first, before sending to the normal lookup DAO.
     */
    protected boolean isExternalBusinessObjectProperty(Object sampleBo, String propertyName) {
        try {
        	if ( propertyName.indexOf( "." ) > 0 && !StringUtils.contains( propertyName, "add." ) ) {
	        	Class propertyClass = PropertyUtils.getPropertyType(
						sampleBo, StringUtils.substringBeforeLast( propertyName, "." ) );
	        	if ( propertyClass != null ) {
	        		return ExternalizableBusinessObjectUtils.isExternalizableBusinessObjectInterface( propertyClass );
	        	} else {
	        		if ( LOG.isDebugEnabled() ) {
	        			LOG.debug( "unable to get class for " + StringUtils.substringBeforeLast( propertyName, "." ) + " on " + sampleBo.getClass().getName() );
	        		}
	        	}
        	}
        } catch (Exception e) {
        	LOG.debug("Unable to determine type of property for " + sampleBo.getClass().getName() + "/" + propertyName, e );
        }
        return false;
    }

    /**
     * Get the name of the property which represents the ExternalizableBusinessObject for the given property.
     *
     * This method can not handle nested properties within the EBO.
     *
     * Returns null if the property is not a nested property or is part of an add line.
     */
    protected String getExternalBusinessObjectProperty(Object sampleBo, String propertyName) {
    	if ( propertyName.indexOf( "." ) > 0 && !StringUtils.contains( propertyName, "add." ) ) {
    		return StringUtils.substringBeforeLast( propertyName, "." );
    	}
        return null;
    }

    /**
     * Checks whether any of the fieldValues being passed refer to a property within an ExternalizableBusinessObject.
     */
    protected boolean hasExternalBusinessObjectProperty(Class boClass, Map<String,String> fieldValues ) {
    	try {
	    	Object sampleBo = boClass.newInstance();
	    	for ( String key : fieldValues.keySet() ) {
	    		if ( isExternalBusinessObjectProperty( sampleBo, key )) {
	    			return true;
	    		}
	    	}
    	} catch ( Exception ex ) {
        	LOG.debug("Unable to check " + boClass + " for EBO properties.", ex );
    	}
    	return false;
    }

    /**
     * Returns a map stripped of any properties which refer to ExternalizableBusinessObjects.  These values may not be passed into the
     * lookup service, since the objects they refer to are not in the local database.
     */
    protected Map<String,String> removeExternalizableBusinessObjectFieldValues(Class boClass, Map<String,String> fieldValues ) {
    	Map<String,String> eboFieldValues = new HashMap<String,String>();
    	try {
	    	Object sampleBo = boClass.newInstance();
	    	for ( String key : fieldValues.keySet() ) {
	    		if ( !isExternalBusinessObjectProperty( sampleBo, key )) {
	    			eboFieldValues.put( key, fieldValues.get( key ) );
	    		}
	    	}
    	} catch ( Exception ex ) {
        	LOG.debug("Unable to check " + boClass + " for EBO properties.", ex );
    	}
    	return eboFieldValues;
    }

    /**
     * Return the EBO fieldValue entries explicitly for the given eboPropertyName.  (I.e., any properties with the given
     * property name as a prefix.
     */
    protected Map<String,String> getExternalizableBusinessObjectFieldValues(String eboPropertyName, Map<String,String> fieldValues ) {
    	Map<String,String> eboFieldValues = new HashMap<String,String>();
    	for ( String key : fieldValues.keySet() ) {
    		if ( key.startsWith( eboPropertyName + "." ) ) {
    			eboFieldValues.put( StringUtils.substringAfterLast( key, "." ), fieldValues.get( key ) );
    		}
    	}
    	return eboFieldValues;
    }

    /**
     * Get the complete list of all properties referenced in the fieldValues that are ExternalizableBusinessObjects.
     *
     * This is a list of the EBO object references themselves, not of the properties within them.
     */
    protected List<String> getExternalizableBusinessObjectProperties(Class boClass, Map<String,String> fieldValues ) {
    	Set<String> eboPropertyNames = new HashSet<String>();
    	try {
	    	Object sampleBo = boClass.newInstance();
	    	for ( String key : fieldValues.keySet() ) {
	    		if ( isExternalBusinessObjectProperty( sampleBo, key )) {
	    			eboPropertyNames.add( StringUtils.substringBeforeLast( key, "." ) );
	    		}
	    	}
    	} catch ( Exception ex ) {
        	LOG.debug("Unable to check " + boClass + " for EBO properties.", ex );
    	}
    	return new ArrayList<String>(eboPropertyNames);
    }

    /**
     * Given an property on the main BO class, return the defined type of the ExternalizableBusinessObject.  This will be used
     * by other code to determine the correct module service to call for the lookup.
     *
     * @param boClass
     * @param propertyName
     * @return
     */
    protected Class<? extends ExternalizableBusinessObject> getExternalizableBusinessObjectClass(Class boClass, String propertyName) {
        try {
        	return PropertyUtils.getPropertyType(
					boClass.newInstance(), StringUtils.substringBeforeLast( propertyName, "." ) );
        } catch (Exception e) {
        	LOG.debug("Unable to determine type of property for " + boClass.getName() + "/" + propertyName, e );
        }
        return null;
    }

    /**
     *
     * This method does the actual search, with the parameters specified, and returns the result.
     *
     * NOTE that it will not do any upper-casing based on the DD forceUppercase. That is handled through an external call to
     * LookupUtils.forceUppercase().
     *
     * @param fieldValues A Map of the fieldNames and fieldValues to be searched on.
     * @param unbounded Whether the results should be bounded or not to a certain max size.
     * @return A List of search results.
     *
     */
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
        // remove hidden fields
        LookupUtils.removeHiddenCriteriaFields(getBusinessObjectClass(), fieldValues);

        searchUsingOnlyPrimaryKeyValues = getLookupService().allPrimaryKeyValuesPresentAndNotWildcard(getBusinessObjectClass(), fieldValues);

        setBackLocation(fieldValues.get(KRADConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KRADConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(KRADConstants.REFERENCES_TO_REFRESH));
        List searchResults;
    	Map<String,String> nonBlankFieldValues = new HashMap<String, String>();
    	for (String fieldName : fieldValues.keySet()) {
    		String fieldValue = fieldValues.get(fieldName);
    		if (StringUtils.isNotBlank(fieldValue) ) {
    			if (fieldValue.endsWith(EncryptionService.ENCRYPTION_POST_PREFIX)) {
    				String encryptedValue = StringUtils.removeEnd(fieldValue, EncryptionService.ENCRYPTION_POST_PREFIX);
    				try {
                        if(CoreApiServiceLocator.getEncryptionService().isEnabled()) {
    					    fieldValue = getEncryptionService().decrypt(encryptedValue);
                        }
    				}
    				catch (GeneralSecurityException e) {
            			LOG.error("Error decrypting value for business object " + getBusinessObjectService() + " attribute " + fieldName, e);
            			throw new RuntimeException("Error decrypting value for business object " + getBusinessObjectService() + " attribute " + fieldName, e);
            		}
    			}
    			nonBlankFieldValues.put(fieldName, fieldValue);
    		}
    	}

        // If this class is an EBO, just call the module service to get the results
        if ( ExternalizableBusinessObjectUtils.isExternalizableBusinessObject( getBusinessObjectClass() ) ) {
        	ModuleService eboModuleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService( getBusinessObjectClass() );
        	BusinessObjectEntry ddEntry = eboModuleService.getExternalizableBusinessObjectDictionaryEntry(getBusinessObjectClass());
        	Map<String,String> filteredFieldValues = new HashMap<String, String>();
        	for (String fieldName : nonBlankFieldValues.keySet()) {
        		if (ddEntry.getAttributeNames().contains(fieldName)) {
        			filteredFieldValues.put(fieldName, nonBlankFieldValues.get(fieldName));
        		}
        	}
        	searchResults = eboModuleService.getExternalizableBusinessObjectsListForLookup(getBusinessObjectClass(), (Map)filteredFieldValues, unbounded);
        // if any of the properties refer to an embedded EBO, call the EBO lookups first and apply to the local lookup
        } else if ( hasExternalBusinessObjectProperty( getBusinessObjectClass(), nonBlankFieldValues ) ) {
        	if ( LOG.isDebugEnabled() ) {
        		LOG.debug( "has EBO reference: " + getBusinessObjectClass() );
        		LOG.debug( "properties: " + nonBlankFieldValues );
        	}
        	// remove the EBO criteria
        	Map<String,String> nonEboFieldValues = removeExternalizableBusinessObjectFieldValues( getBusinessObjectClass(), nonBlankFieldValues );
        	if ( LOG.isDebugEnabled() ) {
        		LOG.debug( "Non EBO properties removed: " + nonEboFieldValues );
        	}
        	// get the list of EBO properties attached to this object
        	List<String> eboPropertyNames = getExternalizableBusinessObjectProperties( getBusinessObjectClass(), nonBlankFieldValues );
        	if ( LOG.isDebugEnabled() ) {
        		LOG.debug( "EBO properties: " + eboPropertyNames );
        	}
        	// loop over those properties
        	for ( String eboPropertyName : eboPropertyNames ) {
        		// extract the properties as known to the EBO
        		Map<String,String> eboFieldValues = getExternalizableBusinessObjectFieldValues( eboPropertyName, nonBlankFieldValues );
            	if ( LOG.isDebugEnabled() ) {
            		LOG.debug( "EBO properties for master EBO property: " + eboPropertyName );
            		LOG.debug( "properties: " + eboFieldValues );
            	}
            	// run search against attached EBO's module service
            	ModuleService eboModuleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService( getExternalizableBusinessObjectClass( getBusinessObjectClass(), eboPropertyName) );
            	// KULRICE-4401 made eboResults an empty list and only filled if service is found.
         	 	List eboResults = Collections.emptyList();
         	 	if (eboModuleService != null) 
         	 	{
         	 		eboResults = eboModuleService.getExternalizableBusinessObjectsListForLookup( getExternalizableBusinessObjectClass( getBusinessObjectClass(), eboPropertyName), (Map)eboFieldValues, unbounded);
         	 	}
         	 		else
         	 	{
         	 		LOG.debug( "EBO ModuleService is null: " + eboPropertyName );
         	 	}
        		// get the mapping/relationship between the EBO object and it's parent object
        		// use that to adjust the fieldValues

        		// get the parent property type
        		Class eboParentClass;
        		String eboParentPropertyName;
        		if ( ObjectUtils.isNestedAttribute( eboPropertyName ) ) {
        			eboParentPropertyName = StringUtils.substringBeforeLast( eboPropertyName, "." );
	        		try {
	        			eboParentClass = PropertyUtils.getPropertyType( getBusinessObjectClass().newInstance(), eboParentPropertyName );
	        		} catch ( Exception ex ) {
	        			throw new RuntimeException( "Unable to create an instance of the business object class: " + getBusinessObjectClass().getName(), ex );
	        		}
        		} else {
        			eboParentClass = getBusinessObjectClass();
        			eboParentPropertyName = null;
        		}
        		if ( LOG.isDebugEnabled() ) {
        			LOG.debug( "determined EBO parent class/property name: " + eboParentClass + "/" + eboParentPropertyName );
        		}
        		// look that up in the DD (BOMDS)
        		// find the appropriate relationship
        		// CHECK THIS: what if eboPropertyName is a nested attribute - need to strip off the eboParentPropertyName if not null
        		RelationshipDefinition rd = getBusinessObjectMetaDataService().getBusinessObjectRelationshipDefinition( eboParentClass, eboPropertyName );
        		if ( LOG.isDebugEnabled() ) {
        			LOG.debug( "Obtained RelationshipDefinition for " + eboPropertyName );
        			LOG.debug( rd );
        		}

        		// copy the needed properties (primary only) to the field values
        		// KULRICE-4446 do so only if the relationship definition exists
        		// NOTE: this will work only for single-field PK unless the ORM layer is directly involved
        		// (can't make (field1,field2) in ( (v1,v2),(v3,v4) ) style queries in the lookup framework
        		if ( ObjectUtils.isNotNull(rd)) {
	        		if ( rd.getPrimitiveAttributes().size() > 1 ) {
	        			throw new RuntimeException( "EBO Links don't work for relationships with multiple-field primary keys." );
	        		}
	        		String boProperty = rd.getPrimitiveAttributes().get( 0 ).getSourceName();
	        		String eboProperty = rd.getPrimitiveAttributes().get( 0 ).getTargetName();
	        		StringBuffer boPropertyValue = new StringBuffer();
	        		// loop over the results, making a string that the lookup DAO will convert into an
	        		// SQL "IN" clause
	        		for ( Object ebo : eboResults ) {
	        			if ( boPropertyValue.length() != 0 ) {
	        				boPropertyValue.append( SearchOperator.OR.op() );
	        			}
	        			try {
	        				boPropertyValue.append( PropertyUtils.getProperty( ebo, eboProperty ).toString() );
	        			} catch ( Exception ex ) {
	        				LOG.warn( "Unable to get value for " + eboProperty + " on " + ebo );
	        			}
	        		}
	        		if ( eboParentPropertyName == null ) {
	        			// non-nested property containing the EBO
	        			nonEboFieldValues.put( boProperty, boPropertyValue.toString() );
	        		} else {
	        			// property nested within the main searched-for BO that contains the EBO
	        			nonEboFieldValues.put( eboParentPropertyName + "." + boProperty, boPropertyValue.toString() );
	        		}
        		}
        	}
        	if ( LOG.isDebugEnabled() ) {
        		LOG.debug( "Passing these results into the lookup service: " + nonEboFieldValues );
        	}
        	// add those results as criteria
        	// run the normal search (but with the EBO critieria added)
    		searchResults = (List) getLookupService().findCollectionBySearchHelper(getBusinessObjectClass(), nonEboFieldValues, unbounded);
        } else {
            searchResults = (List) getLookupService().findCollectionBySearchHelper(getBusinessObjectClass(), nonBlankFieldValues, unbounded);
        }
        
        if (searchResults == null) {
        	searchResults = new ArrayList();
        }

        // sort list if default sort column given
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(defaultSortColumns, true));
        }
        return searchResults;
    }


    /**
     * @see LookupableHelperService#isSearchUsingOnlyPrimaryKeyValues()
     */
    @Override
    public boolean isSearchUsingOnlyPrimaryKeyValues() {
        return searchUsingOnlyPrimaryKeyValues;
}


    /**
     * Returns a comma delimited list of primary key field labels, to be used on the UI to tell the user which fields were used to search
     *
     * These labels are generated from the DD definitions for the lookup fields
     *
     * @return a comma separated list of field attribute names.  If no fields found, returns "N/A"
     * @see LookupableHelperService#isSearchUsingOnlyPrimaryKeyValues()
     * @see LookupableHelperService#getPrimaryKeyFieldLabels()
     */
    @Override
    public String getPrimaryKeyFieldLabels() {
        StringBuilder buf = new StringBuilder();
        List<String> primaryKeyFieldNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getBusinessObjectClass());
        Iterator<String> pkIter = primaryKeyFieldNames.iterator();
        while (pkIter.hasNext()) {
            String pkFieldName = (String) pkIter.next();
            buf.append(getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), pkFieldName));
            if (pkIter.hasNext()) {
                buf.append(", ");
            }
        }
        return buf.length() == 0 ? KRADConstants.NOT_AVAILABLE_STRING : buf.toString();
    }


}

