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
