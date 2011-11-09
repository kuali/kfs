/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.web;

import java.beans.PropertyDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.businessobject.lookup.BusinessObjectFieldConverter;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObject;

/**
 * A class that reads fixtures as property files and then sets the fields of
 * a business object with those properties
 */
public class TestDataGenerator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TestDataGenerator.class);

    private String propertiesFileName;
    private Properties properties;

    private String messageFileName;
    private Properties message;

    /**
     * Constructs a TestDataGenerator instance, with default file names
     */
    public TestDataGenerator() {
        this.messageFileName = "test/unit/src/org/kuali/kfs/gl/web/fixture/message.properties";
        this.propertiesFileName = "test/unit/src/org/kuali/kfs/gl/web/fixture/data.properties";

        properties = loadProperties(propertiesFileName);
        message = loadProperties(messageFileName);
    }

    /**
     * Constructs a TestDataGenerator instance
     * 
     * @param propertiesFileName the name of the properties file to load
     * @param messageFileName the name of the message file to load
     */
    public TestDataGenerator(String propertiesFileName, String messageFileName) {
        this.propertiesFileName = propertiesFileName;
        this.messageFileName = messageFileName;

        properties = loadProperties(propertiesFileName);
        message = loadProperties(messageFileName);
    }


    /**
     * Generates transaction data for a business object from properties
     * 
     * @param businessObject the transction business object
     * @return the transction business object with data
     * @throws Exception thrown if an exception is encountered for any reason
     */
    public Transaction generateTransactionData(Transaction businessObject) throws Exception {
        Iterator propsIter = properties.keySet().iterator();
        while (propsIter.hasNext()) {
            String propertyName = (String) propsIter.next();
            String propertyValue = (String) properties.get(propertyName);

            // if searchValue is empty and the key is not a valid property ignore
            if (StringUtils.isBlank(propertyValue) || !(PropertyUtils.isWriteable(businessObject, propertyName))) {
                continue;
            }

            Object finalPropertyValue = getPropertyValue(businessObject, propertyName, propertyValue);
            if (finalPropertyValue != null) {
                PropertyUtils.setProperty(businessObject, propertyName, finalPropertyValue);
            }
        }
        setFiscalYear(businessObject);
        return businessObject;
    }
    
    /**
     * If the actual transaction implementation has a "setUniversityFiscalYear" method, use that to set the 
     * fiscal year to the value of TestUtils.getFiscalYearForTesting()
     * @param transaction transaction to try to set fiscal year on
     */
    protected void setFiscalYear(Transaction transaction) {
        try {
            final PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(transaction, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
            if (propertyDescriptor.getReadMethod() != null) {
                PropertyUtils.setProperty(transaction, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, TestUtils.getFiscalYearForTesting());
            }
        }
        catch (IllegalAccessException iae) {
            LOG.info("Could test universityFiscalYear property on fixture of transaction type: "+transaction.getClass().getName(), iae);
        }
        catch (InvocationTargetException ite) {
            LOG.info("Could test universityFiscalYear property on fixture of transaction type: "+transaction.getClass().getName(), ite);
        }
        catch (NoSuchMethodException nsme) {
            LOG.info("Could test universityFiscalYear property on fixture of transaction type: "+transaction.getClass().getName(), nsme);
        }
        
    }

    /**
     * Generates lookup fields and values through reading properties
     * 
     * @param businessObject the business object ot populate
     * @throws Exception thrown if an exception is encountered for any reason
     */
    public Map generateLookupFieldValues(PersistableBusinessObject businessObject) throws Exception {
        return generateLookupFieldValues(businessObject, null);
    }

    /**
     * This method generates lookup fields and values through reading properties
     * 
     * @param businessObject the business object to populate
     * @return the map of lookup fields and values
     * @throws Exception thrown if an exception is encountered for any reason
     */
    public Map generateLookupFieldValues(PersistableBusinessObject businessObject, List lookupFields) throws Exception {
        Map fieldValues = new HashMap();

        boolean isTransaction = (businessObject instanceof Transaction);
        if (!isTransaction && lookupFields != null) {
            lookupFields = BusinessObjectFieldConverter.convertToTransactionFields(lookupFields);
        }

        Iterator propsIter = properties.keySet().iterator();
        while (propsIter.hasNext()) {
            String propertyName = (String) propsIter.next();
            String propertyValue = (String) properties.get(propertyName);

            boolean isContains = (lookupFields == null) ? true : lookupFields.contains(propertyName);

            if (!isTransaction) {
                propertyName = BusinessObjectFieldConverter.convertFromTransactionPropertyName(propertyName);
            }

            // ignore the property fields whose values are empty
            if (StringUtils.isBlank(propertyValue)) {
                continue;
            }

            if (isContains) {
                fieldValues.put(propertyName, propertyValue);
            }
        }
        return fieldValues;
    }

    /**
     * This method loads the properties from the property file
     * 
     * @param propertiesFileName the name of file containing the properties
     * @return the properties that have been populated
     */
    private Properties loadProperties(String propertiesFileName) {
        Properties properties = new Properties();

        try {
            FileInputStream fileInputStream = new FileInputStream(propertiesFileName);
            properties.load(fileInputStream);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return properties;
    }

    /**
     * This method gets the approperiate property value by examining the given parameters
     * 
     * @param businessObject the given business object
     * @param propertyName the given property name
     * @param propertyValue the given property value
     * @return the processed property value
     */
    private Object getPropertyValue(Object businessObject, String propertyName, String propertyValue) {
        // get the property type of the given business object
        Class propertyType = null;
        try {
            propertyType = PropertyUtils.getPropertyType(businessObject, propertyName);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // implement the type conversion
        String propertyTypeName = propertyType.getName();
        Object finalPropertyValue = propertyValue;
        if (propertyType.isPrimitive()) {
            finalPropertyValue = null;
        }
        else if (propertyTypeName.indexOf("Integer") >= 0) {
            finalPropertyValue = new Integer(propertyValue.trim());
        }
        else if (propertyTypeName.indexOf("Boolean") >= 0) {
            finalPropertyValue = new Boolean(propertyValue.trim());
        }
        else if (propertyTypeName.indexOf("KualiDecimal") >= 0) {
            finalPropertyValue = new KualiDecimal(propertyValue.trim());
        }

        return finalPropertyValue;
    }

    /**
     * This method obtains the value of the message with the given name
     * 
     * @param messageName the given message name
     * @return the value of the message
     */
    public String getMessageValue(String messageName) {
        if (getMessage().containsKey(messageName)) {
            return getMessage().getProperty(messageName);
        }
        return "";
    }

    /**
     * This method obtains the value of the property with the given name
     * 
     * @param propertyName the given property name
     * @return the value of the property
     */
    public String getPropertyValue(String propertyName) {
        if (getProperties().containsKey(propertyName)) {
            return getProperties().getProperty(propertyName);
        }
        return "";
    }

    /**
     * Gets the properties attribute.
     * 
     * @return Returns the properties.
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Sets the properties attribute value.
     * 
     * @param properties The properties to set.
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * Gets the message attribute.
     * 
     * @return Returns the message.
     */
    public Properties getMessage() {
        return message;
    }

    /**
     * Sets the message attribute value.
     * 
     * @param message The message to set.
     */
    public void setMessage(Properties message) {
        this.message = message;
    }
}
