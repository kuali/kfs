/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.context;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.core.impl.config.property.JAXBConfigImpl;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.DefaultResourceLoader;

public class PropertyLoadingFactoryBean implements FactoryBean<Properties> {
    private static final String PROPERTY_FILE_NAMES_KEY = "property.files";
    private static final String PROPERTY_TEST_FILE_NAMES_KEY = "property.test.files";
    private static final String SECURITY_PROPERTY_FILE_NAME_KEY = "security.property.file";
    private static final String KFS_DEFAULT_CONFIGURATION_FILE_NAME = "kfs-default-config";
    private static final String KFS_RICE_DEFAULT_CONFIGURATION_FILE_NAME = "kfs-rice-default-config";
    private static final String KFS_SECURITY_DEFAULT_CONFIGURATION_FILE_NAME = "kfs-security-default-config";
    private static final Properties BASE_PROPERTIES = new Properties();
    private static final String HTTP_URL_PROPERTY_NAME = "http.url";
    private static final String KSB_REMOTING_URL_PROPERTY_NAME = "ksb.remoting.url";
    private static final String REMOTING_URL_SUFFIX = "/remoting";
    private static final String ADDITIONAL_KFS_CONFIG_LOCATIONS_PARAM = "additional.kfs.config.locations";
    private Properties props = new Properties();
    private boolean testMode;
    private boolean secureMode;

    public Properties getObject() {
        loadBaseProperties();
        props.putAll(BASE_PROPERTIES);
        if (secureMode) {
            loadPropertyList(props,SECURITY_PROPERTY_FILE_NAME_KEY);
        } else {
            loadPropertyList(props,PROPERTY_FILE_NAMES_KEY);
            if (testMode) {
                loadPropertyList(props,PROPERTY_TEST_FILE_NAMES_KEY);
            }            
        }
        if (StringUtils.isBlank(System.getProperty(HTTP_URL_PROPERTY_NAME))) {
            props.put(KSB_REMOTING_URL_PROPERTY_NAME, props.getProperty(KFSConstants.APPLICATION_URL_KEY) + REMOTING_URL_SUFFIX);
        }
        else {
            props.put(KSB_REMOTING_URL_PROPERTY_NAME, new StringBuilder("http://").append(System.getProperty(HTTP_URL_PROPERTY_NAME)).append("/kfs-").append(props.getProperty(KFSConstants.ENVIRONMENT_KEY)).append(REMOTING_URL_SUFFIX).toString());
        }
        
        return props;
    }

    public Class<Properties> getObjectType() {
        return Properties.class;
    }

    public boolean isSingleton() {
        return true;
    }

    private static void loadPropertyList(Properties props, String listPropertyName) {
        for (String propertyFileName : getBaseListProperty(listPropertyName)) {
            loadProperties(props,propertyFileName);
        }
    }

    private static void loadProperties( Properties props, String propertyFileName) {
        InputStream propertyFileInputStream = null;
        try {
            try {
                propertyFileInputStream = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader()).getResource(propertyFileName).getInputStream();
                props.load(propertyFileInputStream);
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
            List<String> riceXmlConfigurations = new ArrayList<String>();
            riceXmlConfigurations.add("classpath:META-INF/common-config-defaults.xml");
            JAXBConfigImpl riceXmlConfigurer = new JAXBConfigImpl(riceXmlConfigurations);
            try {
                riceXmlConfigurer.parseConfig();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            BASE_PROPERTIES.putAll(riceXmlConfigurer.getProperties());
            loadProperties(BASE_PROPERTIES, new StringBuilder("classpath:").append(KFS_DEFAULT_CONFIGURATION_FILE_NAME).append(".properties").toString());
            loadProperties(BASE_PROPERTIES, new StringBuilder("classpath:").append(KFS_RICE_DEFAULT_CONFIGURATION_FILE_NAME).append(".properties").toString());
            loadProperties(BASE_PROPERTIES, new StringBuilder("classpath:").append(KFS_SECURITY_DEFAULT_CONFIGURATION_FILE_NAME).append(".properties").toString());

            loadExternalProperties(BASE_PROPERTIES);

        }
    }
    
    /**
     * Loads properties from an external file.  Also merges in all System properties
     * @param props the properties object
     */
    private static void loadExternalProperties(Properties props) {
        String externalConfigLocationPaths = System.getProperty(PropertyLoadingFactoryBean.ADDITIONAL_KFS_CONFIG_LOCATIONS_PARAM);
        if (StringUtils.isNotEmpty(externalConfigLocationPaths)) {
            String[] files = externalConfigLocationPaths.split(","); 
            for (String f: files) { 
                if (StringUtils.isNotEmpty(f)) { 
                    System.err.println("Loading properties from " + f);
                    loadProperties(props, new StringBuffer("file:").append(f).toString()); 
                } 
            }
        }

        props.putAll(System.getProperties());
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public void setSecureMode(boolean secureMode) {
        this.secureMode = secureMode;
    }
    
    public static void clear() {
        BASE_PROPERTIES.clear();
    }
}
