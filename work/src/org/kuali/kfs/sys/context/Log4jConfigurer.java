/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.sys.context;

import java.net.URL;
import java.net.URLClassLoader;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.kuali.kfs.sys.KFSConstants;

public class Log4jConfigurer {
    private static final long MILLISECONDS_CONVERSION_MULTIPLIER = 60 * 1000;

    public static final void configureLogging(boolean doStartupStatsLogging) {
        String settingsFile = PropertyLoadingFactoryBean.getBaseProperty(KFSConstants.LOG4J_SETTINGS_FILE_KEY);
        String reloadMinutes = PropertyLoadingFactoryBean.getBaseProperty(KFSConstants.LOG4J_RELOAD_MINUTES_KEY);
        long reloadMilliseconds = 5 * MILLISECONDS_CONVERSION_MULTIPLIER;
        try {
            reloadMilliseconds = Long.parseLong(reloadMinutes) * MILLISECONDS_CONVERSION_MULTIPLIER;
        }
        catch (NumberFormatException ignored) {
            // default to 5 minutes
        }
        PropertyConfigurator.configureAndWatch(settingsFile, reloadMilliseconds);
        printClasspath();
    }

    private static void printClasspath() {
        StringBuffer classpath = new StringBuffer("Classpath is:\n");
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL[] urls = ((URLClassLoader) classloader).getURLs();
        for (int i = 0; i < urls.length; i++) {
            classpath.append(urls[i].getFile()).append("; ");
        }
        Logger.getLogger(Log4jConfigurer.class).info(classpath.toString());
    }
}
