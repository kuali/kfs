/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/gl/businessobject/lookup/BusinessObjectFieldConverter.java,v $
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
package org.kuali.module.gl.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.PropertyConstants;

/**
 * This class...
 * 
 * 
 */
public class BusinessObjectFieldConverter {

    /**
     * This method converts the field values from normal GL business objects to GL transaction
     * 
     * @param fields
     * @return the list of fields for GL transaction
     */
    public static List convertToTransactionFields(List fields) {
        List transactionFields = new ArrayList();

        Iterator propsIter = fields.iterator();
        while (propsIter.hasNext()) {
            String propertyName = (String) propsIter.next();

            // convert property name from normal BO to GL transaction
            String transactionPropertyName = propertyName;

            Map propertyMappingTable = getPropertyMappingTable();
            transactionPropertyName = convertPropertyName(propertyMappingTable, propertyName);

            // create a new entry for current property
            transactionFields.add(transactionPropertyName);
        }
        return transactionFields;
    }

    /**
     * This method converts the field values from normal GL business objects to GL transaction
     * 
     * @param fieldValues the map of field values for normal GL business objects
     * @return the map of field values for GL transaction
     */
    public static Map convertToTransactionFieldValues(Map fieldValues) {
        Map transactionFieldValues = new HashMap();

        Iterator propsIter = fieldValues.keySet().iterator();
        while (propsIter.hasNext()) {
            String propertyName = (String) propsIter.next();
            String propertyValue = (String) fieldValues.get(propertyName);

            // convert property name from normal BO to GL transaction
            String transactionPropertyName = propertyName;

            Map propertyMappingTable = getPropertyMappingTable();
            transactionPropertyName = convertPropertyName(propertyMappingTable, propertyName);

            // create a new entry for current property
            transactionFieldValues.put(transactionPropertyName, propertyValue);
        }
        return transactionFieldValues;
    }

    /**
     * This method converts the property name of a normal business object to GL transaction
     * 
     * @param propertyName the property name of a normal business object
     * @return the property name of GL transaction
     */
    public static String convertToTransactionPropertyName(String propertyName) {
        return convertPropertyName(getPropertyMappingTable(), propertyName);
    }

    /**
     * This method converts the property name of a normal business object from GL transaction
     * 
     * @param propertyName the property name of GL transaction
     * @return the property name of a normal business object
     */
    public static String convertFromTransactionPropertyName(String propertyName) {
        return convertPropertyName(getSwappedPropertyMappingTable(), propertyName);
    }

    /**
     * This method converts the field values from GL transaction to normal GL business objects
     * 
     * @param fieldValues the map of field values for GL transaction
     * @return the map of field values for normal GL business objects
     */
    public static Map convertFromTransactionFieldValues(Map fieldValues) {
        Map boFieldValues = new HashMap();

        Iterator propsIter = fieldValues.keySet().iterator();
        while (propsIter.hasNext()) {
            String propertyName = (String) propsIter.next();
            String propertyValue = (String) fieldValues.get(propertyName);

            // convert property name from normal BO to GL transaction
            String transactionPropertyName = propertyName;
            Map propertyMappingTable = getSwappedPropertyMappingTable();
            transactionPropertyName = convertPropertyName(propertyMappingTable, propertyName);

            // create a new entry for current property
            boFieldValues.put(transactionPropertyName, propertyValue);
        }
        return boFieldValues;
    }

    /**
     * This method defines a table that maps normal properties into transaction properties
     * 
     * @return a property mapping table
     */
    private static Map getPropertyMappingTable() {
        Map propertyMappingTable = new HashMap();

        propertyMappingTable.put(PropertyConstants.OBJECT_CODE, PropertyConstants.FINANCIAL_OBJECT_CODE);
        propertyMappingTable.put(PropertyConstants.SUB_OBJECT_CODE, PropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        propertyMappingTable.put(PropertyConstants.OBJECT_TYPE_CODE, PropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);

        propertyMappingTable.put(PropertyConstants.BALANCE_TYPE_CODE, PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        propertyMappingTable.put(PropertyConstants.DOCUMENT_TYPE_CODE, PropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
        propertyMappingTable.put(PropertyConstants.ORIGIN_CODE, PropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
        propertyMappingTable.put(PropertyConstants.DOCUMENT_NUMBER, PropertyConstants.DOCUMENT_NUMBER);

        return propertyMappingTable;
    }

    /**
     * This method defines a table that maps transaction properties into normal properties
     * 
     * @return a property mapping table
     */
    private static Map getSwappedPropertyMappingTable() {
        Map propertyMappingTable = getPropertyMappingTable();
        Map swappedPropertyMappingTable = new HashMap();

        Iterator iterator = propertyMappingTable.keySet().iterator();
        while (iterator.hasNext()) {
            String propertyKey = (String) iterator.next();
            String propertyValue = (String) propertyMappingTable.get(propertyKey);

            if (propertyValue != null && !swappedPropertyMappingTable.containsKey(propertyValue)) {
                swappedPropertyMappingTable.put(propertyValue, propertyKey);
            }
        }
        return swappedPropertyMappingTable;
    }

    /**
     * This method retrieves a name of the given property name from the given mapping table
     * 
     * @param propertyMappingTable the property mapping table
     * @param propertyName the property name of a normal business object
     * @return the property name of GL transaction
     */
    private static String convertPropertyName(Map propertyMappingTable, String propertyName) {

        String transactionPropertyName = propertyName;
        if (propertyMappingTable.containsKey(propertyName)) {
            transactionPropertyName = (String) propertyMappingTable.get(propertyName);
        }
        return transactionPropertyName;
    }

    public static void escapeSpecialCharacter(Map fieldValues, String specialCharacter, String replacement) {
        Iterator propsIter = fieldValues.keySet().iterator();
        while (propsIter.hasNext()) {
            String propertyName = (String) propsIter.next();
            String propertyValue = (String) fieldValues.get(propertyName);

            String propertyValueAfterEscaped = propertyValue.replaceAll(specialCharacter, replacement);
            fieldValues.put(propertyName, propertyValueAfterEscaped);
        }
    }

    public static void escapeSingleQuote(Map fieldValues) {
        String specialCharacter = "'";
        String replacement = " ";
        escapeSpecialCharacter(fieldValues, specialCharacter, replacement);
    }
}
