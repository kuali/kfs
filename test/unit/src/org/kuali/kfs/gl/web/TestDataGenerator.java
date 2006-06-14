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
package org.kuali.module.gl.web;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.util.BusinessObjectFieldConverter;

/**
 * This class...
 * 
 * @author Bin Gao from Michigan State University
 */
public class TestDataGenerator {

    private final String propertiesFileName = "test/src/org/kuali/module/gl/web/data.properties";
    private Properties properties;

    private final String messageFileName = "test/src/org/kuali/module/gl/web/message.properties";
    private Properties message;

    /**
     * Constructs a TestDataGenerator.java.
     */
    public TestDataGenerator() {
        properties = loadProperties(propertiesFileName);
        message = loadProperties(messageFileName);
    }

    /**
     * This method generates transaction data for a business object from properties
     * 
     * @param businessObject the transction business object
     * @return the transction business object with data
     * @throws Exception
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
        return businessObject;
    }

    /**
     * This method generates lookup fields and values through reading properties
     * 
     * @param businessObject
     * @return the map of lookup fields and values
     * @throws Exception
     */
    public Map generateLookupFieldValues(BusinessObjectBase businessObject) throws Exception {
        return generateLookupFieldValues(businessObject, null);
    }

    /**
     * This method generates lookup fields and values through reading properties
     * 
     * @param businessObject
     * @return the map of lookup fields and values
     * @throws Exception
     */
    public Map generateLookupFieldValues(BusinessObjectBase businessObject, List lookupFields) throws Exception {
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
     * 
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
