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
package org.kuali.kfs.context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.DefaultResourceLoader;

import edu.iu.uis.eden.util.ClassLoaderUtils;

public class PropertyLoadingFactoryBean implements FactoryBean {
    private static final String PROPERTY_FILE_NAMES_KEY = "property.files";
    private static final String PROPERTY_TEST_FILE_NAMES_KEY = "property.test.files";
    private static final String CONFIGURATION_FILE_NAME = "configuration";
    private static final Properties BASE_PROPERTIES = new Properties();
    private boolean testMode;

    public Object getObject() throws Exception {
        loadBaseProperties();
        loadPropertyList(PROPERTY_FILE_NAMES_KEY);
        if (testMode) {
            loadPropertyList(PROPERTY_TEST_FILE_NAMES_KEY);
        }
        return BASE_PROPERTIES;
    }

    public Class getObjectType() {
        return Properties.class;
    }

    public boolean isSingleton() {
        return true;
    }

    private void loadPropertyList(String listPropertyName) {
        for (String propertyFileName : getBaseListProperty(listPropertyName)) {
            loadProperties(propertyFileName);
        }
    }

    private static void loadProperties(String propertyFileName) {
        InputStream propertyFileInputStream = null;
        try {
            try {
                propertyFileInputStream = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader()).getResource(propertyFileName).getInputStream();
                BASE_PROPERTIES.load(propertyFileInputStream);
            }
            finally {
                if (propertyFileInputStream != null) {
                    propertyFileInputStream.close();
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException("PropertyLoadingFactoryBean unable to load property file: " + propertyFileName);
        }
    }

    public static String getBaseProperty(String propertyName) {
        loadBaseProperties();
        return BASE_PROPERTIES.getProperty(propertyName);
    }

    protected static List<String> getBaseListProperty(String propertyName) {
        loadBaseProperties();
        return Arrays.asList(BASE_PROPERTIES.getProperty(propertyName).split(","));
    }

    protected static void loadBaseProperties() {
        if (BASE_PROPERTIES.isEmpty()) {
            loadProperties(new StringBuffer("classpath:").append(CONFIGURATION_FILE_NAME).append(".properties").toString());
        }
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }
}
