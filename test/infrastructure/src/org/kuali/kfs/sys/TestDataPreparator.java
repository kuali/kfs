/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.test.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.PersistenceService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.util.ObjectUtil;

/**
 * provide with a set of utilities that can be used to prepare test data for unit testing. The core idea is to convert Java
 * properties into a list of specified business objects or search criteria.
 */
public class TestDataPreparator {
    public static final String DEFAULT_FIELD_NAMES = "fieldNames";
    public static final String DEFAULT_DELIMINATOR = "deliminator";
    private static BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    private static PersistenceService persistenceService = SpringContext.getBean(PersistenceService.class);

    /**
     * build a list of objects of type "clazz" from the test data provided by the given properties. The default fieldNames and
     * deliminator are used.
     * 
     * @param clazz the the specified object type
     * @param properties the given properties that contain the test data
     * @param propertyKeyPrefix the test data with the given key prefix can be used to construct the return objects
     * @param numberOfData the number of test data matching the search criteria
     * @return a list of objects of type "clazz" from the test data provided by the given properties
     */
    public static <T> List<T> buildTestDataList(Class<? extends T> clazz, Properties properties, String propertyKeyPrefix, int numberOfData) {
        String fieldNames = properties.getProperty(DEFAULT_FIELD_NAMES);
        String deliminator = properties.getProperty(DEFAULT_DELIMINATOR);
        return buildTestDataList(clazz, properties, propertyKeyPrefix, fieldNames, deliminator, numberOfData);
    }

    /**
     * build a list of objects of type "clazz" from the test data provided by the given properties
     * 
     * @param clazz the the specified object type
     * @param properties the given properties that contain the test data
     * @param propertyKeyPrefix the test data with the given key prefix can be used to construct the return objects
     * @param fieldNames the field names of the test data columns
     * @param deliminator the deliminator that is used to separate the field from each other
     * @param numberOfData the number of test data matching the search criteria
     * @return a list of objects of type "clazz" from the test data provided by the given properties
     */
    public static <T> List<T> buildTestDataList(Class<? extends T> clazz, Properties properties, String propertyKeyPrefix, String fieldNames, String deliminator, int numberOfData) {
        List<T> testDataList = new ArrayList<T>();
        for (int i = 1; i <= numberOfData; i++) {
            String propertyKey = propertyKeyPrefix + i;
            T testData = buildTestDataObject(clazz, properties, propertyKey, fieldNames, deliminator);
            testDataList.add(testData);
        }
        return testDataList;
    }

    /**
     * build an object of type "clazz" from the test data provided by the given properties
     * 
     * @param clazz the the specified object type
     * @param properties the given properties that contain the test data
     * @param propertyKey the test data with the given key
     * @param fieldNames the field names of the test data columns
     * @param deliminator the deliminator that is used to separate the field from each other
     * @return an object of type "clazz" from the test data provided by the given properties
     */
    public static <T> T buildTestDataObject(Class<? extends T> clazz, Properties properties, String propertyKey, String fieldNames, String deliminator) {
        T testData = null;
        try {
            testData = clazz.newInstance();
            ObjectUtil.populateBusinessObject(testData, properties, propertyKey, fieldNames, deliminator);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return testData;
    }

    /**
     * build a list of objects of type "clazz" from the expected results provided by the given properties. The default fieldNames
     * and deliminator are used.
     * 
     * @param clazz the the specified object type. The instance of this type should be comparable through overriding Object.equals()
     * @param properties the given properties that contain the expected results
     * @param propertyKeyPrefix the expected results with the given key prefix can be used to construct the return objects
     * @param numberOfData the number of the expected results matching the search criteria
     * @return a list of objects of type "clazz" from the expected results provided by the given properties
     */
    public static <T> List<T> buildExpectedValueList(Class<? extends T> clazz, Properties properties, String propertyKeyPrefix, int numberOfData) {
        String fieldNames = properties.getProperty(DEFAULT_FIELD_NAMES);
        String deliminator = properties.getProperty(DEFAULT_DELIMINATOR);
        return buildExpectedValueList(clazz, properties, propertyKeyPrefix, fieldNames, deliminator, numberOfData);
    }

    /**
     * build a list of objects of type "clazz" from the expected results provided by the given properties
     * 
     * @param clazz the the specified object type. The instance of this type should be comparable through overriding Object.equals()
     * @param properties the given properties that contain the expected results
     * @param propertyKeyPrefix the expected results with the given key prefix can be used to construct the return objects
     * @param fieldNames the field names of the expected results columns
     * @param deliminator the deliminator that is used to separate the field from each other
     * @param numberOfData the number of the expected results matching the search criteria
     * @return a list of objects of type "clazz" from the expected results provided by the given properties
     */
    public static <T> List<T> buildExpectedValueList(Class<? extends T> clazz, Properties properties, String propertyKeyPrefix, String fieldNames, String deliminator, int numberOfData) {
        List<T> expectedDataList = new ArrayList<T>();
        for (int i = 1; i <= numberOfData; i++) {
            String propertyKey = propertyKeyPrefix + i;
            try {
                T expectedData = TestDataPreparator.buildTestDataObject(clazz, properties, propertyKey, fieldNames, deliminator);

                if (!expectedDataList.contains(expectedData)) {
                    expectedDataList.add(expectedData);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return expectedDataList;
    }

    /**
     * build the cleanup criteria for "clazz" from the given properties. The default fieldNames and deliminator are used.
     * 
     * @param clazz the the specified object type.
     * @param properties the given properties that contain the cleanup criteria fields and values
     * @param propertyKey the given property whose value provides the cleanup criteria values
     * @return the cleanup criteria for "clazz" from the given properties
     */
    public static <T> Map<String, Object> buildCleanupCriteria(Class<? extends T> clazz, Properties properties, String propertyKey) {
        String fieldNames = properties.getProperty(DEFAULT_FIELD_NAMES);
        String deliminator = properties.getProperty(DEFAULT_DELIMINATOR);
        return buildCleanupCriteria(clazz, properties, propertyKey, fieldNames, deliminator);
    }

    /**
     * build the cleanup criteria for "clazz" from the given properties.
     * 
     * @param clazz the the specified object type.
     * @param properties the given properties that contain the cleanup criteria fields and values
     * @param propertyKey the given property whose value provides the cleanup criteria values
     * @param fieldNames the field names of the cleanup columns
     * @param deliminator the deliminator that is used to separate the field from each other
     * @return the cleanup criteria for "clazz" from the given properties
     */
    public static <T> Map<String, Object> buildCleanupCriteria(Class<? extends T> clazz, Properties properties, String propertyKey, String fieldNames, String deliminator) {
        T instanceOfClazz = TestDataPreparator.buildTestDataObject(clazz, properties, propertyKey, fieldNames, deliminator);
        return ObjectUtil.buildPropertyMap(instanceOfClazz, Arrays.asList(StringUtils.split(fieldNames, deliminator)));
    }

    /**
     * persist the given data object if it is not in the persistent store
     * 
     * @param dataObject the given data object
     * @return return the data object persisted into the data store
     */
    public static <T extends PersistableBusinessObject> T persistDataObject(T dataObject) {
        T existingDataObject = (T) businessObjectService.retrieve(dataObject);
        if (existingDataObject == null) {
            List<T> dataObjects = new ArrayList<T>();
            dataObjects.add(dataObject);
            
            businessObjectService.save(dataObjects);
            persistenceService.retrieveNonKeyFields(dataObject);
            
            return dataObject;
        }

        return existingDataObject;
    }

    /**
     * persist the given data object if it is not in the persistent store
     * 
     * @param dataObject the given data object
     * @return return the data object persisted into the data store
     */
    public static <T extends PersistableBusinessObject> void persistDataObject(List<T> dataObjects) {
        for (T dataObject : dataObjects) {
            persistDataObject(dataObject);
        }
    }

    /**
     * remove the existing data from the database so that they cannot affact the test results
     */
    public static <T extends PersistableBusinessObject> void doCleanUpWithoutReference(Class<T> clazz, Properties properties, String propertykey, String fieldNames, String deliminator) throws Exception {
        Map<String, Object> fieldValues = buildFieldValues(clazz, properties, propertykey, fieldNames, deliminator);
        businessObjectService.deleteMatching(clazz, fieldValues);
    }
    
    /**
     * remove the existing data from the database so that they cannot affact the test results
     */
    public static <T extends PersistableBusinessObject> void doCleanUpWithReference(Class<T> clazz, Properties properties, String propertykey, String fieldNames, String deliminator) throws Exception {
        List<T> dataObjects = findMatching(clazz, properties, propertykey, fieldNames, deliminator);
        
        for(T object : dataObjects) {
            businessObjectService.delete(object);
        }
    }

    /**
     * remove the existing data from the database so that they cannot affact the test results
     */
    public static <T extends PersistableBusinessObject> List<T> findMatching(Class<T> clazz, Properties properties, String propertykey, String fieldNames, String deliminator) throws Exception {
        Map<String, Object> fieldValues = buildFieldValues(clazz, properties, propertykey, fieldNames, deliminator);
        return (List<T>) businessObjectService.findMatching(clazz, fieldValues);
    }

    /**
     * build the field name and value pairs from the given properties in the specified properties file 
     */
    public static <T> Map<String, Object> buildFieldValues(Class<T> clazz, Properties properties, String propertykey, String fieldNames, String deliminator) throws Exception {
        T cleanup = clazz.newInstance();
        ObjectUtil.populateBusinessObject(cleanup, properties, propertykey, fieldNames, deliminator);
        return ObjectUtil.buildPropertyMap(cleanup, Arrays.asList(StringUtils.split(fieldNames, deliminator)));
    }
    
    /**
     * test if the given object is in the given collection. The given key fields can be used for the comparison.
     * 
     * @return true if the given object is in the given collection; otherwise, false
     */
    public static <T> boolean contains(List<T> collection, T object, List<String> keyFields) { 
        for(T objectInCollection : collection) {
            boolean contains = ObjectUtil.compareObject(objectInCollection, object, keyFields);
            
            if(contains) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * test if the given two collections contain the exactly same elements.  The given key fields can be used for the comparison.
     * 
     * @return true if the given two collections contain the exactly same elements; otherwise, false
     */
    public static <T> boolean hasSameElements(List<T> collection1, List<T> collection2, List<String> keyFields) {
        if(collection1 == collection2) {
            return true;
        }
        
        if(collection1 == null || collection2 == null) {
            return false;
        }
        
        if(collection1.size() != collection2.size()) {
            return false;
        }
        
        for(T object : collection2) {
            boolean contains = contains(collection1, object, keyFields);
            if(!contains) {
                return false;
            }
        }
        
        return true;
    }    
}
