/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
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
        propertyMappingTable.put(PropertyConstants.DOCUMENT_NUMBER, PropertyConstants.FINANCIAL_DOCUMENT_NUMBER);

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
