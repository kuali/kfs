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

import java.io.InputStream;
import java.util.Properties;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.DefaultResourceLoader;

import edu.iu.uis.eden.util.ClassLoaderUtils;

public class PropertyLoadingFactoryBean implements FactoryBean {
    private static final String PROPERTY_FILE_NAMES_KEY = "property.files";
    private static final String PROPERTY_TEST_FILE_NAMES_KEY = "property.test.files";
    protected static final String CONFIGURATION_FILE_NAME = "configuration";
    private boolean testMode;

    public Object getObject() throws Exception {
        Properties properties = new Properties();
        loadPropertyList(properties, PROPERTY_FILE_NAMES_KEY);
        loadProperties(properties, new StringBuffer("classpath:").append(CONFIGURATION_FILE_NAME).append(".properties").toString());
        if (testMode) {
            loadPropertyList(properties, PROPERTY_TEST_FILE_NAMES_KEY);
        }
        return properties;
    }

    public Class getObjectType() {
        return Properties.class;
    }

    public boolean isSingleton() {
        return true;
    }

    private void loadPropertyList(Properties baseProperties, String listPropertyName) {
        for (String propertyFileName : SpringContext.getListConfigurationProperty(listPropertyName)) {
            try {
                loadProperties(baseProperties, propertyFileName);
            }
            catch (Exception e) {
                throw new RuntimeException("PropertyLoadingFactoryBean unable to load properties from file: " + propertyFileName, e);
            }
        }
    }

    private void loadProperties(Properties baseProperties, String propertyFileName) throws Exception {
        InputStream propertyFileInputStream = null;
        try {
            propertyFileInputStream = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader()).getResource(propertyFileName).getInputStream();
            baseProperties.load(propertyFileInputStream);
        }
        finally {
            if (propertyFileInputStream != null) {
                propertyFileInputStream.close();
            }
        }
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }
}
