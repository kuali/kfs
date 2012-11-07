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
package org.kuali.rice.krad.workflow.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.joda.time.DateTime;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeDateTime;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeDecimal;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeFactory;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeInteger;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeString;
import org.kuali.rice.kns.service.BusinessObjectMetaDataService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.datadictionary.DocumentCollectionPath;
import org.kuali.rice.krad.datadictionary.DocumentValuePathGroup;
import org.kuali.rice.krad.datadictionary.RoutingAttribute;
import org.kuali.rice.krad.datadictionary.RoutingTypeDefinition;
import org.kuali.rice.krad.datadictionary.SearchingTypeDefinition;
import org.kuali.rice.krad.datadictionary.WorkflowAttributes;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.DataTypeUtil;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.attribute.DataDictionarySearchableAttribute;
import org.kuali.rice.krad.workflow.service.WorkflowAttributePropertyResolutionService;

/**
 * The default implementation of the WorkflowAttributePropertyResolutionServiceImpl
 */
public class WorkflowAttributePropertyResolutionServiceImpl implements WorkflowAttributePropertyResolutionService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WorkflowAttributePropertyResolutionServiceImpl.class);

    private PersistenceStructureService persistenceStructureService;
    private BusinessObjectMetaDataService businessObjectMetaDataService;

    /**
     * Using the proper RoutingTypeDefinition for the current routing node of the document, aardvarks out the proper routing type qualifiers
     */
    @Override
    public List<Map<String, String>> resolveRoutingTypeQualifiers(Document document, RoutingTypeDefinition routingTypeDefinition) {
        List<Map<String, String>> qualifiers = new ArrayList<Map<String, String>>();

        if (routingTypeDefinition != null) {
            document.populateDocumentForRouting();
            RoutingAttributeTracker routingAttributeTracker = new RoutingAttributeTracker(routingTypeDefinition.getRoutingAttributes());
            for (DocumentValuePathGroup documentValuePathGroup : routingTypeDefinition.getDocumentValuePathGroups()) {
                qualifiers.addAll(resolveDocumentValuePath(document, documentValuePathGroup, routingAttributeTracker));
                routingAttributeTracker.reset();
            }
            // SR 16387
            // Now, go through the Map<String,String> objects and eliminate duplicates
            Set<Map<String,String>> deDuplicatedQualifiers = new HashSet<Map<String,String>>(qualifiers);
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "Number of qualifier sets before deduplication: " + qualifiers.size() + " -- after: " + deDuplicatedQualifiers.size() );
            }
            qualifiers = new ArrayList<Map<String,String>>( deDuplicatedQualifiers );
        }
        return qualifiers;
    }

    /**
     * Resolves all of the values in the given DocumentValuePathGroup from the given BusinessObject
     * @param businessObject the business object which is the source of values
     * @param group the DocumentValuePathGroup which tells us which values we want
     * @return a List of Map<String, String>s
     */
    protected List<Map<String, String>> resolveDocumentValuePath(BusinessObject businessObject, DocumentValuePathGroup group, RoutingAttributeTracker routingAttributeTracker) {
        List<Map<String, String>> qualifiers;
        Map<String, String> qualifier = new HashMap<String, String>();
        if (group.getDocumentValues() == null && group.getDocumentCollectionPath() == null) {
            throw new IllegalStateException("A document value path group must have the documentValues property set, the documentCollectionPath property set, or both.");
        }
        if (group.getDocumentValues() != null) {
            addPathValuesToQualifier(businessObject, group.getDocumentValues(), routingAttributeTracker, qualifier);
        }
        if (group.getDocumentCollectionPath() != null) {
            qualifiers = resolveDocumentCollectionPath(businessObject, group.getDocumentCollectionPath(), routingAttributeTracker);
            qualifiers = cleanCollectionQualifiers(qualifiers);
            for (Map<String, String> collectionElementQualifier : qualifiers) {
                copyQualifications(qualifier, collectionElementQualifier);
            }
        } else {
            qualifiers = new ArrayList<Map<String, String>>();
            qualifiers.add(qualifier);
        }
        return qualifiers;
    }

    /**
     * Resolves document values from a collection path on a given business object
     * @param businessObject the business object which has a collection, each element of which is a source of values
     * @param collectionPath the information about what values to pull from each element of the collection
     * @return a List of Map<String, String>s
     */
    protected List<Map<String, String>> resolveDocumentCollectionPath(BusinessObject businessObject, DocumentCollectionPath collectionPath, RoutingAttributeTracker routingAttributeTracker) {
        List<Map<String, String>> qualifiers = new ArrayList<Map<String, String>>();
        final Collection collectionByPath = getCollectionByPath(businessObject, collectionPath.getCollectionPath());
        if (!ObjectUtils.isNull(collectionByPath)) {
            if (collectionPath.getNestedCollection() != null) {
                // we need to go through the collection...
                for (Object collectionElement : collectionByPath) {
                    // for each element, we need to get the child qualifiers
                    if (collectionElement instanceof BusinessObject) {
                        List<Map<String, String>> childQualifiers = resolveDocumentCollectionPath((BusinessObject)collectionElement, collectionPath.getNestedCollection(), routingAttributeTracker);
                        for (Map<String, String> childQualifier : childQualifiers) {
                            Map<String, String> qualifier = new HashMap<String, String>();
                            routingAttributeTracker.checkPoint();
                            // now we need to get the values for the current element of the collection
                            addPathValuesToQualifier(collectionElement, collectionPath.getDocumentValues(), routingAttributeTracker, qualifier);
                            // and move all the child keys to the qualifier
                            copyQualifications(childQualifier, qualifier);
                            qualifiers.add(qualifier);
                            routingAttributeTracker.backUpToCheckPoint();
                        }
                    }
                }
            } else {
                // go through each element in the collection
                for (Object collectionElement : collectionByPath) {
                    Map<String, String> qualifier = new HashMap<String, String>();
                    routingAttributeTracker.checkPoint();
                    addPathValuesToQualifier(collectionElement, collectionPath.getDocumentValues(), routingAttributeTracker, qualifier);
                    qualifiers.add(qualifier);
                    routingAttributeTracker.backUpToCheckPoint();
                }
            }
        }
        return qualifiers;
    }

    /**
     * Returns a collection from a path on a business object
     * @param businessObject the business object to get values from
     * @param collectionPath the path to that collection
     * @return hopefully, a collection of objects
     */
    protected Collection getCollectionByPath(BusinessObject businessObject, String collectionPath) {
        return (Collection)getPropertyByPath(businessObject, collectionPath.trim());
    }

    /**
     * Aardvarks values out of a business object and puts them into an Map<String, String>, based on a List of paths
     * @param businessObject the business object to get values from
     * @param paths the paths of values to get from the qualifier
     * @param routingAttributes the RoutingAttribute associated with this qualifier's document value
     * @param qualifier the qualifier to put values into
     */
    protected void addPathValuesToQualifier(Object businessObject, List<String> paths, RoutingAttributeTracker routingAttributes, Map<String, String> qualifier) {
        if (ObjectUtils.isNotNull(paths)) {
            for (String path : paths) {
                // get the values for the paths of each element of the collection
                final Object value = getPropertyByPath(businessObject, path.trim());
                if (value != null) {
                    qualifier.put(routingAttributes.getCurrentRoutingAttribute().getQualificationAttributeName(), value.toString());
                }
                routingAttributes.moveToNext();
            }
        }
    }

    /**
     * Copies all the values from one qualifier to another
     * @param source the source of values
     * @param target the place to write all the values to
     */
    protected void copyQualifications(Map<String, String> source, Map<String, String> target) {
        for (String key : source.keySet()) {
            target.put(key, source.get(key));
        }
    }

    /**
     * Resolves all of the searching values to index for the given document, returning a list of SearchableAttributeValue implementations
     *
     */
    @Override
    public List<DocumentAttribute> resolveSearchableAttributeValues(Document document, WorkflowAttributes workflowAttributes) {
        List<DocumentAttribute> valuesToIndex = new ArrayList<DocumentAttribute>();
        if (workflowAttributes != null && workflowAttributes.getSearchingTypeDefinitions() != null) {
            for (SearchingTypeDefinition definition : workflowAttributes.getSearchingTypeDefinitions()) {
                valuesToIndex.addAll(aardvarkValuesForSearchingTypeDefinition(document, definition));
            }
        }
        return valuesToIndex;
    }

    /**
     * Pulls SearchableAttributeValue values from the given document for the given searchingTypeDefinition
     * @param document the document to get search values from
     * @param searchingTypeDefinition the current SearchingTypeDefinition to find values for
     * @return a List of SearchableAttributeValue implementations
     */
    protected List<DocumentAttribute> aardvarkValuesForSearchingTypeDefinition(Document document, SearchingTypeDefinition searchingTypeDefinition) {
        List<DocumentAttribute> searchAttributes = new ArrayList<DocumentAttribute>();

        final List<Object> searchValues = aardvarkSearchValuesForPaths(document, searchingTypeDefinition.getDocumentValues());
        for (Object value : searchValues) {
            try {
                final DocumentAttribute searchableAttributeValue = buildSearchableAttribute(((Class<? extends BusinessObject>)Class.forName(searchingTypeDefinition.getSearchingAttribute().getBusinessObjectClassName())), searchingTypeDefinition.getSearchingAttribute().getAttributeName(), value);
                if (searchableAttributeValue != null) {
                    searchAttributes.add(searchableAttributeValue);
                }
            }
            catch (ClassNotFoundException cnfe) {
                throw new RuntimeException("Could not find instance of class "+searchingTypeDefinition.getSearchingAttribute().getBusinessObjectClassName(), cnfe);
            }
        }
        return searchAttributes;
    }

    /**
     * Pulls values as objects from the document for the given paths
     * @param document the document to pull values from
     * @param paths the property paths to pull values
     * @return a List of values as Objects
     */
    protected List<Object> aardvarkSearchValuesForPaths(Document document, List<String> paths) {
        List<Object> searchValues = new ArrayList<Object>();
        for (String path : paths) {
            flatAdd(searchValues, getPropertyByPath(document, path.trim()));
        }
        return searchValues;
    }

    /**
     * Removes empty Map<String, String>s from the given List of qualifiers
     * @param qualifiers a List of Map<String, String>s holding qualifiers for responsibilities
     * @return a cleaned up list of qualifiers
     */
    protected List<Map<String, String>> cleanCollectionQualifiers(List<Map<String, String>> qualifiers) {
       List<Map<String, String>> cleanedQualifiers = new ArrayList<Map<String, String>>();
       for (Map<String, String> qualifier : qualifiers) {
           if (qualifier.size() > 0) {
               cleanedQualifiers.add(qualifier);
           }
       }
       return cleanedQualifiers;
    }

    @Override
    public String determineFieldDataType(Class<? extends BusinessObject> businessObjectClass, String attributeName) {
        return DataTypeUtil.determineFieldDataType(businessObjectClass, attributeName);
    }

    /**
     * Using the type of the sent in value, determines what kind of SearchableAttributeValue implementation should be passed back
     * @param attributeKey
     * @param value
     * @return
     */
    @Override
    public DocumentAttribute buildSearchableAttribute(Class<? extends BusinessObject> businessObjectClass, String attributeKey, Object value) {
        if (value == null) return null;
        final String fieldDataType = determineFieldDataType(businessObjectClass, attributeKey);
        if (fieldDataType.equals(KewApiConstants.SearchableAttributeConstants.DATA_TYPE_STRING)) return buildSearchableStringAttribute(attributeKey, value); // our most common case should go first
        if (fieldDataType.equals(KewApiConstants.SearchableAttributeConstants.DATA_TYPE_FLOAT) && DataTypeUtil.isDecimaltastic(value.getClass())) return buildSearchableRealAttribute(attributeKey, value);
        if (fieldDataType.equals(KewApiConstants.SearchableAttributeConstants.DATA_TYPE_DATE) && DataTypeUtil.isDateLike(value.getClass())) return buildSearchableDateTimeAttribute(attributeKey, value);
        if (fieldDataType.equals(KewApiConstants.SearchableAttributeConstants.DATA_TYPE_LONG) && DataTypeUtil.isIntsy(value.getClass())) return buildSearchableFixnumAttribute(attributeKey, value);
        if (fieldDataType.equals(DataDictionarySearchableAttribute.DATA_TYPE_BOOLEAN) && DataTypeUtil.isBooleanable(value.getClass())) return buildSearchableYesNoAttribute(attributeKey, value);
        return buildSearchableStringAttribute(attributeKey, value);
    }

    /**
     * Builds a date time SearchableAttributeValue for the given key and value
     * @param attributeKey the key for the searchable attribute
     * @param value the value that will be coerced to date/time data
     * @return the generated SearchableAttributeDateTimeValue
     */
    protected DocumentAttributeDateTime buildSearchableDateTimeAttribute(String attributeKey, Object value) {
        return DocumentAttributeFactory.createDateTimeAttribute(attributeKey, new DateTime(value));
    }

    /**
     * Builds a "float" SearchableAttributeValue for the given key and value
     * @param attributeKey the key for the searchable attribute
     * @param value the value that will be coerced to "float" data
     * @return the generated SearchableAttributeFloatValue
     */
    protected DocumentAttributeDecimal buildSearchableRealAttribute(String attributeKey, Object value) {
        BigDecimal decimalValue = null;
        if (value instanceof BigDecimal) {
            decimalValue = (BigDecimal)value;
        } else if (value instanceof KualiDecimal) {
            decimalValue = ((KualiDecimal)value).bigDecimalValue();
        } else {
            decimalValue = new BigDecimal(((Number)value).doubleValue());
        }
        return DocumentAttributeFactory.createDecimalAttribute(attributeKey, decimalValue);
    }

    /**
     * Builds a "integer" SearchableAttributeValue for the given key and value
     * @param attributeKey the key for the searchable attribute
     * @param value the value that will be coerced to "integer" type data
     * @return the generated SearchableAttributeLongValue
     */
    protected DocumentAttributeInteger buildSearchableFixnumAttribute(String attributeKey, Object value) {
        BigInteger integerValue = null;
        if (value instanceof BigInteger) {
            integerValue = (BigInteger)value;
        } else {
            integerValue = BigInteger.valueOf(((Number)value).longValue());
        }
        return DocumentAttributeFactory.createIntegerAttribute(attributeKey, integerValue);
    }

    /**
     * Our last ditch attempt, this builds a String SearchableAttributeValue for the given key and value
     * @param attributeKey the key for the searchable attribute
     * @param value the value that will be coerced to a String
     * @return the generated SearchableAttributeStringValue
     */
    protected DocumentAttributeString buildSearchableStringAttribute(String attributeKey, Object value) {
        return DocumentAttributeFactory.createStringAttribute(attributeKey, value.toString());
    }

    /**
     * This builds a String SearchableAttributeValue for the given key and value, correctly correlating booleans
     * @param attributeKey the key for the searchable attribute
     * @param value the value that will be coerced to a String
     * @return the generated SearchableAttributeStringValue
     */
    protected DocumentAttributeString buildSearchableYesNoAttribute(String attributeKey, Object value) {
        final String boolValueAsString = booleanValueAsString((Boolean)value);
        return DocumentAttributeFactory.createStringAttribute(attributeKey, boolValueAsString);
   }

    /**
     * Converts the given boolean value to "" for null, "Y" for true, "N" for false
     * @param booleanValue the boolean value to convert
     * @return the corresponding String "Y","N", or ""
     */
    private String booleanValueAsString(Boolean booleanValue) {
        if (booleanValue == null) return "";
        if (booleanValue.booleanValue()) return "Y";
        return "N";
    }

    @Override
    public Object getPropertyByPath(Object object, String path) {
        if (object instanceof Collection) return getPropertyOfCollectionByPath((Collection)object, path);

        final String[] splitPath = headAndTailPath(path);
        final String head = splitPath[0];
        final String tail = splitPath[1];

        if (object instanceof PersistableBusinessObject && tail != null) {
            if (getBusinessObjectMetaDataService().getBusinessObjectRelationship((BusinessObject) object, head) != null) {
                ((PersistableBusinessObject)object).refreshReferenceObject(head);

            }
        }
        final Object headValue = ObjectUtils.getPropertyValue(object, head);
        if (!ObjectUtils.isNull(headValue)) {
            if (tail == null) {
                return headValue;
            } else {
                // we've still got path left...
                if (headValue instanceof Collection) {
                    // oh dear, a collection; we've got to loop through this
                    Collection values = makeNewCollectionOfSameType((Collection)headValue);
                    for (Object currentElement : (Collection)headValue) {
                        flatAdd(values, getPropertyByPath(currentElement, tail));
                    }
                    return values;
                } else {
                    return getPropertyByPath(headValue, tail);
                }
            }
        }
        return null;
    }

    /**
     * Finds a child object, specified by the given path, on each object of the given collection
     * @param collection the collection of objects
     * @param path the path of the property to retrieve
     * @return a Collection of the values culled from each child
     */
    public Collection getPropertyOfCollectionByPath(Collection collection, String path) {
        Collection values = makeNewCollectionOfSameType(collection);
        for (Object o : collection) {
            flatAdd(values, getPropertyByPath(o, path));
        }
        return values;
    }

    /**
     * Makes a new collection of exactly the same type of the collection that was handed to it
     * @param collection the collection to make a new collection of the same type as
     * @return a new collection.  Of the same type.
     */
    public Collection makeNewCollectionOfSameType(Collection collection) {
        if (collection instanceof List) return new ArrayList();
        if (collection instanceof Set) return new HashSet();
        try {
            return collection.getClass().newInstance();
        }
        catch (InstantiationException ie) {
            throw new RuntimeException("Couldn't instantiate class of collection we'd already instantiated??", ie);
        }
        catch (IllegalAccessException iae) {
            throw new RuntimeException("Illegal Access on class of collection we'd already accessed??", iae);
        }
    }

    /**
     * Splits the first property off from a path, leaving the tail
     * @param path the path to split
     * @return an array; if the path is nested, the first element will be the first part of the path up to a "." and second element is the rest of the path while if the path is simple, returns the path as the first element and a null as the second element
     */
    protected String[] headAndTailPath(String path) {
        final int firstDot = path.indexOf('.');
        if (firstDot < 0) {
            return new String[] { path, null };
        }
        return new String[] { path.substring(0, firstDot), path.substring(firstDot + 1) };
    }

    /**
     * Convenience method which makes sure that if the given object is a collection, it is added to the given collection flatly
     * @param c a collection, ready to be added to
     * @param o an object of dubious type
     */
    protected void flatAdd(Collection c, Object o) {
        if (o instanceof Collection) {
            c.addAll((Collection) o);
        } else {
            c.add(o);
        }
    }

    /**
     * Gets the persistenceStructureService attribute.
     * @return Returns the persistenceStructureService.
     */
    public PersistenceStructureService getPersistenceStructureService() {
        return persistenceStructureService;
    }

    /**
     * Sets the persistenceStructureService attribute value.
     * @param persistenceStructureService The persistenceStructureService to set.
     */
    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    /**
     * Inner helper class which will track which routing attributes have been used
     */
    class RoutingAttributeTracker {

        private List<RoutingAttribute> routingAttributes;
        private int currentRoutingAttributeIndex;
        private Stack<Integer> checkPoints;

        /**
         * Constructs a WorkflowAttributePropertyResolutionServiceImpl
         * @param routingAttributes the routing attributes to track
         */
        public RoutingAttributeTracker(List<RoutingAttribute> routingAttributes) {
            this.routingAttributes = routingAttributes;
            checkPoints = new Stack<Integer>();
        }

        /**
         * @return the routing attribute hopefully associated with the current qualifier
         */
        public RoutingAttribute getCurrentRoutingAttribute() {
            return routingAttributes.get(currentRoutingAttributeIndex);
        }

        /**
         * Moves this routing attribute tracker to its next routing attribute
         */
        public void moveToNext() {
            currentRoutingAttributeIndex += 1;
        }

        /**
         * Check points at the current routing attribute, so that this position is saved
         */
        public void checkPoint() {
            checkPoints.push(new Integer(currentRoutingAttributeIndex));
        }

        /**
         * Returns to the point of the last check point
         */
        public void backUpToCheckPoint() {
            currentRoutingAttributeIndex = checkPoints.pop().intValue();
        }

        /**
         * Resets this RoutingAttributeTracker, setting the current RoutingAttribute back to the top one and
         * clearing the check point stack
         */
        public void reset() {
            currentRoutingAttributeIndex = 0;
            checkPoints.clear();
        }
    }

    protected BusinessObjectMetaDataService getBusinessObjectMetaDataService() {
        if ( businessObjectMetaDataService == null ) {
            businessObjectMetaDataService = KNSServiceLocator.getBusinessObjectMetaDataService();
        }
        return businessObjectMetaDataService;
    }
}
