/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kns.util.properties;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.kns.exception.PropertiesException;

/**
 * This class is used to obtain properties from a properites file.
 * 
 * 
 */
public class FilePropertySource implements PropertySource {
    private static Log log = LogFactory.getLog(FilePropertySource.class);


    private String fileName;
    private boolean allowOverrides;

    /**
     * Set source fileName.
     * 
     * @param fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return source fileName
     */
    public String getFileName() {
        return this.fileName;
    }

    public boolean isAllowOverrides() {
        return this.allowOverrides;
    }

    public void setAllowOverrides(boolean allowOverrides) {
        this.allowOverrides = allowOverrides;
    }
    
    /**
     * Attempts to load properties from a properties file which has the current fileName and is located on the classpath.
     * 
     * @see org.kuali.rice.kns.util.properties.PropertySource#loadProperties()
     * @throws IllegalStateException if the fileName is null or empty
     */
    public Properties loadProperties() {
        if (StringUtils.isBlank(getFileName())) {
            throw new IllegalStateException("invalid (blank) fileName");
        }

        Properties properties = new Properties();

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(getFileName());
        if (url == null) {
            throw new PropertiesException("unable to locate properties file '" + getFileName() + "'");
        }

        InputStream in = null;

        try {
            in = url.openStream();
            properties.load(in);
        }
        catch (IOException e) {
            throw new PropertiesException("error loading from properties file '" + getFileName() + "'", e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {
                    log.error("caught exception closing InputStream: " + e);
                }

            }
        }

        return properties;
    }

}
