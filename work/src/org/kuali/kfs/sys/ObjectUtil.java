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
package org.kuali.kfs.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.WrapDynaClass;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;

/**
 * This class provides a set of facilities that can be used to manipulate objects, for example, object population
 */
public class ObjectUtil {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ObjectUtil.class);
    
    /**
     * create an object of the specified type
     * 
     * @param clazz the specified type of the object
     * @return an object of the specified type
     */
    public static <T> T createObject(Class<T> clazz) {
        T object = null;

        try {
            object = clazz.newInstance();
        }
        catch (InstantiationException ie) {
            LOG.error(ie);
            throw new RuntimeException(ie);
        }
        catch (IllegalAccessException iae) {
            LOG.error(iae);
            throw new RuntimeException(iae);
        }

        return object;
    }

    /**
     * Populate the given fields of the target object with the corresponding field values of source object
     * 
     * @param targetObject the target object
     * @param sourceObject the source object
     * @param keyFields the given fields of the target object that need to be popluated
     */
    public static void buildObject(Object targetObject, Object sourceObject, List<String> keyFields) {
        if (sourceObject.getClass().isArray()) {
            buildObject(targetObject, sourceObject, keyFields);
            return;
        }

        for (String propertyName : keyFields) {
            if (PropertyUtils.isReadable(sourceObject, propertyName) && PropertyUtils.isWriteable(targetObject, propertyName)) {
                try {
                    Object propertyValue = PropertyUtils.getProperty(sourceObject, propertyName);
                    PropertyUtils.setProperty(targetObject, propertyName, propertyValue);
                }
                catch (Exception e) {
                    LOG.debug(e);
                }
            }
        }
    }

    /**
     * Populate the given fields of the target object with the values of an array
     * 
     * @param targetObject the target object
     * @param sourceObject the given array
     * @param keyFields the given fields of the target object that need to be popluated
     */
    public static void buildObject(Object targetObject, Object[] sourceObject, List<String> keyFields) {
        int indexOfArray = 0;
        for (String propertyName : keyFields) {
            if (PropertyUtils.isWriteable(targetObject, propertyName) && indexOfArray < sourceObject.length) {
                try {
                    Object value = sourceObject[indexOfArray];
                    String propertyValue = value != null ? value.toString() : "";

                    String type = PropertyUtils.getPropertyType(targetObject, propertyName).getSimpleName();
                    Object realPropertyValue = valueOf(type, propertyValue);

                    if (realPropertyValue != null && !StringUtils.isEmpty(realPropertyValue.toString())) {
                        PropertyUtils.setProperty(targetObject, propertyName, realPropertyValue);
                    }
                    else {
                        PropertyUtils.setProperty(targetObject, propertyName, null);
                    }
                }
                catch (Exception e) {
                    LOG.debug(e);
                }
            }
            indexOfArray++;
        }
    }

    /**
     * Get an object of the given type holding the property value of the specified String.
     * 
     * @param type the given type of the returning object
     * @param propertyValue the property value of the specified string
     * @return an object of the given type holding the property value of the specified String
     */
    public static Object valueOf(String type, String propertyValue) {
        Object realPropertyValue = null;

        if (type.equals("Integer")) {
            realPropertyValue = isInteger(propertyValue) ? Integer.valueOf(propertyValue) : null;
        }
        else if (type.equals("KualiInteger")) {
            realPropertyValue = isInteger(propertyValue) ? new KualiInteger(propertyValue) : null;
        }
        else if (type.equalsIgnoreCase("Boolean")) {
            realPropertyValue = Boolean.valueOf(propertyValue);
        }
        else if (type.equals("KualiDecimal")) {
            realPropertyValue = isDecimal(propertyValue) ? new KualiDecimal(propertyValue) : null;
        }
        else if (type.equals("Date")) {
            realPropertyValue = formatDate(propertyValue);
        }
        else if (type.equals("BigDecimal")) {
            realPropertyValue = isDecimal(propertyValue) ? new BigDecimal(propertyValue) : null;
        }
        else if (type.equals("Timestamp")) {
            realPropertyValue = formatTimeStamp(propertyValue);
        }
        else {
            realPropertyValue = propertyValue;
        }
        return realPropertyValue;
    }

    /**
     * determine if the given string can be converted into an Integer
     * 
     * @param value the value of the specified string
     * @return true if the string can be converted into an Integer; otherwise, return false
     */
    public static boolean isInteger(String value) {
        String pattern = "^(\\+|-)?\\d+$";
        return value != null && value.matches(pattern);
    }

    /**
     * determine if the given string can be converted into a decimal
     * 
     * @param value the value of the specified string
     * @return true if the string can be converted into a decimal; otherwise, return false
     */
    public static boolean isDecimal(String value) {
        String pattern = "^(((\\+|-)?\\d+(\\.\\d*)?)|((\\+|-)?(\\d*\\.)?\\d+))$";
        return value != null && value.matches(pattern);
    }

    /**
     * convert the given string into a date
     * @param value the given string
     * @return a date converted from the given string
     */
    public static Date formatDate(String value) {
        Date formattedDate = null;

        try {
            formattedDate = Date.valueOf(value);
        }
        catch (Exception e) {
            return formattedDate;
        }
        return formattedDate;
    }

    /**
     * convert the given string into a timestamp object if the string is in the valid format of timestamp 
     * @param value the given string
     * @return a timestamp converted from the given string
     */
    public static Timestamp formatTimeStamp(String value) {
        Timestamp formattedTimestamp = null;

        String pattern = "^(\\d{1,4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}(\\.\\d{1,9})?)$";
        boolean isTimestamp = value != null && value.matches(pattern);

        try {
            if (isTimestamp) {
                formattedTimestamp = Timestamp.valueOf(value);
            }
            else {
                formattedTimestamp = new Timestamp(formatDate(value).getTime());
            }
        }
        catch (Exception e) {
            return formattedTimestamp;
        }
        return formattedTimestamp;
    }

    /**
     * Populate the target object with the source object
     * 
     * @param targetObject the target object
     * @param sourceObject the source object
     */
    public static void buildObject(Object targetObject, Object sourceObject) {
        DynaClass dynaClass = WrapDynaClass.createDynaClass(targetObject.getClass());
        DynaProperty[] properties = dynaClass.getDynaProperties();

        for (DynaProperty property : properties) {
            String propertyName = property.getName();
            if (PersistableBusinessObjectBase.class.isAssignableFrom(property.getClass())) {
                continue;
            }

            if (PropertyUtils.isReadable(sourceObject, propertyName) && PropertyUtils.isWriteable(targetObject, propertyName)) {
                try {
                    Object propertyValue = PropertyUtils.getProperty(sourceObject, propertyName);
                    PropertyUtils.setProperty(targetObject, propertyName, propertyValue);
                }
                catch (Exception e) {
                    LOG.debug(e + propertyName);
                }
            }
        }
    }

    /**
     * Determine if they have the same values in the specified fields
     * 
     * @param targetObject the target object
     * @param sourceObject the source object
     * @param keyFields the specified fields
     * @return true if the two objects have the same values in the specified fields; otherwise, false
     */
    public static boolean equals(Object targetObject, Object sourceObject, List<String> keyFields) {
        if (targetObject == sourceObject) {
            return true;
        }

        if (targetObject == null || sourceObject == null) {
            return false;
        }

        for (String propertyName : keyFields) {
            try {
                Object propertyValueOfSource = PropertyUtils.getProperty(sourceObject, propertyName);
                Object propertyValueOfTarget = PropertyUtils.getProperty(targetObject, propertyName);

                if (!ObjectUtils.equals(propertyValueOfSource, propertyValueOfTarget)) {
                    return false;
                }
            }
            catch (Exception e) {
                LOG.info(e);
                return false;
            }
        }
        return true;
    }

    /**
     * compute the hash code for the given object from the given fields
     * 
     * @param object the given object
     * @param keyFields the specified fields
     * @return the hash code for the given object from the given fields
     */
    public static int generateHashCode(Object object, List<String> keyFields) {
        if (object == null) {
            return 0;
        }

        final int prime = 31;
        int result = 1;
        for (String propertyName : keyFields) {
            try {
                Object propertyValue = PropertyUtils.getProperty(object, propertyName);
                result = prime * result + ((propertyValue == null) ? 0 : propertyValue.hashCode());
            }
            catch (Exception e) {
                LOG.info(e);
            }
        }
        return result;
    }

    /**
     * This method builds a map of business object with its specified property names and corresponding values
     * 
     * @param businessObject the given business object
     * @param the specified fields that need to be included in the return map
     * @return the map of business object with its property names and values
     */
    public static Map<String, Object> buildPropertyMap(Object object, List<String> keyFields) {
        DynaClass dynaClass = WrapDynaClass.createDynaClass(object.getClass());
        DynaProperty[] properties = dynaClass.getDynaProperties();
        Map<String, Object> propertyMap = new LinkedHashMap<String, Object>();

        for (DynaProperty property : properties) {
            String propertyName = property.getName();

            if (PropertyUtils.isReadable(object, propertyName) && keyFields.contains(propertyName)) {
                try {
                    Object propertyValue = PropertyUtils.getProperty(object, propertyName);

                    if (propertyValue != null && !StringUtils.isEmpty(propertyValue.toString())) {
                        propertyMap.put(propertyName, propertyValue);
                    }
                }
                catch (Exception e) {
                    LOG.info(e);
                }
            }
        }
        return propertyMap;
    }

    /**
     * concat the specified properties of the given object as a string
     * 
     * @param object the given object
     * @param the specified fields that need to be included in the return string
     * @return the specified properties of the given object as a string
     */
    public static String concatPropertyAsString(Object object, List<String> keyFields) {
        StringBuilder propertyAsString = new StringBuilder();
        for (String field : keyFields) {
            if (PropertyUtils.isReadable(object, field)) {
                try {
                    propertyAsString.append(PropertyUtils.getProperty(object, field));
                }
                catch (Exception e) {
                    LOG.error(e);
                }
            }
        }

        return propertyAsString.toString();
    }

    /**
     * Tokenize the input line with the given deliminator and populate the given object with values of the tokens
     * 
     * @param targetObject the target object
     * @param line the input line
     * @param delim the deminator that separates the fields in the given line
     * @param keyFields the specified fields
     */
    public static void convertLineToBusinessObject(Object targetObject, String line, String delim, List<String> keyFields) {
        String[] tokens = StringUtils.split(line, delim);
        ObjectUtil.buildObject(targetObject, tokens, keyFields);
    }

    /**
     * Tokenize the input line with the given deliminator and populate the given object with values of the tokens
     * 
     * @param targetObject the target object
     * @param line the input line
     * @param delim the deminator that separates the fields in the given line
     * @param keyFields the specified fields
     */
    public static void convertLineToBusinessObject(Object targetObject, String line, String delim, String fieldNames) {
        List<String> tokens = split(line, delim);
        List<String> keyFields = Arrays.asList(StringUtils.split(fieldNames, delim));
        ObjectUtil.buildObject(targetObject, tokens.toArray(), keyFields);
    }

    /**
     * Tokenize the input line with the given deliminator and store the tokens in a list
     * 
     * @param line the input line
     * @param delim the deminator that separates the fields in the given line
     * 
     * @return a list of tokens
     */
    public static List<String> split(String line, String delim) {
        List<String> tokens = new ArrayList<String>();

        int currentPosition = 0;
        for (int step = 0; step < line.length(); step++) {
            int previousPosition = currentPosition;
            currentPosition = StringUtils.indexOf(line, delim, currentPosition);
            currentPosition = currentPosition == -1 ? line.length() - 1 : currentPosition;

            String sub = line.substring(previousPosition, currentPosition);
            tokens.add(sub); // don't trim the string

            currentPosition += delim.length();
            if (currentPosition >= line.length()) {
                break;
            }
        }
        return tokens;
    }

    /**
     * Tokenize the input line with the given deliminator and populate the given object with values of the tokens
     * 
     * @param targetObject the target object
     * @param line the input line
     * @param delim the deminator that separates the fields in the given line
     * @param keyFields the specified fields
     */
    public static void convertLineToBusinessObject(Object targetObject, String line, int[] fieldLength, List<String> keyFields) {
        String[] tokens = new String[fieldLength.length];

        int currentPosition = 0;
        for (int i = 0; i < fieldLength.length; i++) {
            currentPosition = i <= 0 ? 0 : fieldLength[i - 1] + currentPosition;
            tokens[i] = StringUtils.mid(line, currentPosition, fieldLength[i]).trim();
        }
        ObjectUtil.buildObject(targetObject, tokens, keyFields);
    }

    /**
     * Populate a business object with the given properities and information
     * 
     * @param businessOjbject the business object to be populated
     * @param properties the given properties
     * @param propertyKey the property keys in the properties
     * @param fieldNames the names of the fields to be populated
     * @param deliminator the deliminator that separates the values to be used in a string
     */
    public static void populateBusinessObject(Object businessOjbject, Properties properties, String propertyKey, String fieldNames, String deliminator) {
        String data = properties.getProperty(propertyKey);
        ObjectUtil.convertLineToBusinessObject(businessOjbject, data, deliminator, fieldNames);
    }

    /**
     * Populate a business object with the given properities and information
     * 
     * @param businessOjbject the business object to be populated
     * @param properties the given properties
     * @param propertyKey the property keys in the properties
     * @param fieldNames the names of the fields to be populated
     * @param deliminator the deliminator that separates the values to be used in a string
     */
    public static void populateBusinessObject(Object businessOjbject, Properties properties, String propertyKey, int[] fieldLength, List<String> keyFields) {
        String data = properties.getProperty(propertyKey);
        ObjectUtil.convertLineToBusinessObject(businessOjbject, data, fieldLength, keyFields);
    }

    /**
     * determine if the source object has a field with null as its value
     * 
     * @param sourceObject the source object
     */
    public static boolean hasNullValueField(Object sourceObject) {
        DynaClass dynaClass = WrapDynaClass.createDynaClass(sourceObject.getClass());
        DynaProperty[] properties = dynaClass.getDynaProperties();

        for (DynaProperty property : properties) {
            String propertyName = property.getName();

            if (PropertyUtils.isReadable(sourceObject, propertyName)) {
                try {
                    Object propertyValue = PropertyUtils.getProperty(sourceObject, propertyName);
                    if (propertyValue == null) {
                        return true;
                    }
                }
                catch (Exception e) {
                    LOG.info(e);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * get the types of the nested attributes starting at the given class
     * @param clazz the given class
     * @param nestedAttribute the nested attributes of the given class
     * @return a map that contains the types of the nested attributes and the attribute names
     */
    public static Map<Class<?>, String> getNestedAttributeTypes(Class<?> clazz, String nestedAttribute){
        List<String> attributes = Arrays.asList(StringUtils.split(nestedAttribute, PropertyUtils.NESTED_DELIM));
        Map<Class<?>, String> nestedAttributes = new HashMap<Class<?>, String>();

        Class<?> currentClass = clazz;        
        for (String propertyName : attributes) {
            String methodName = "get" + StringUtils.capitalize(propertyName);
            try {
                Method method = currentClass.getMethod(methodName);
                currentClass = method.getReturnType();
                nestedAttributes.put(currentClass, propertyName);
            }
            catch (Exception e) {
                LOG.info(e);
                break;
            }
        }
        return nestedAttributes;
    }
}