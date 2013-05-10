/*
 * Copyright 2012 The Kuali Foundation.
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

import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Provides a method to add a ConsoleAppender to a Logger if one has not already been configured.
 */
public class BatchLogger {
    public static final String DEFAULT_CONVERSION_PATTERN = "%d [%t] u:%X{user}/d:%X{docId} %-5p %c :: %m%n";
    public static final Layout DEFAULT_PATTERN_LAYOUT = new PatternLayout(DEFAULT_CONVERSION_PATTERN);
    public static final String LOG_FILE_APPENDER_NAME = "LogFile";

    /**
     * Adds a ConsoleAppender to the Logger if one doesn't already exist.
     * Attempts to use the layout from the 'LogFile' appender if it exists, otherwise it uses the DEFAULT_CONVERSION_PATTERN
     *
     * @param logger the Logger to which to add a ConsoleAppender
     */
    public static void addConsoleAppender(Logger logger) {
        if (!isConsoleAppenderExists(logger)) {

            Appender console = new ConsoleAppender(getLogFileAppenderLayout(logger));

            logger.addAppender(console);
        }
    }

    /**
     * Returns the layout for the 'LogFile' appender. Returns the DEFAULT_PATTERN_LAYOUT if the 'LogFile' doesn't exist.
     *
     * @return the layout for the 'LogFile' appender
     */
    public static Layout getLogFileAppenderLayout() {
        return getLogFileAppenderLayout(null);
    }

    /**
     * Checks whether a ConsoleAppender has already been added to either the RootLogger or to the requested Logger
     *
     * @param logger the Logger to check
     * @return true if a ConsoleAppender has already been added to the RootLogger or to the requested Logger, false otherwise
     */
    private static boolean isConsoleAppenderExists(Logger logger) {
        Enumeration<Appender> rootAppenders = Logger.getRootLogger().getAllAppenders();
        while(rootAppenders.hasMoreElements()) {
            Appender appender = rootAppenders.nextElement();
            if (appender instanceof ConsoleAppender) {
                return true;
            }
        }

        Enumeration<Appender> appenders = logger.getAllAppenders();
        while(appenders.hasMoreElements()) {
            Appender appender = appenders.nextElement();
            if (appender instanceof ConsoleAppender) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the appender for 'LogFile' if it exists on the RootLogger or the requested Logger, null otherwise
     */
    private static Appender getLogFileAppender(Logger logger) {
        if (Logger.getRootLogger() != null) {
            return Logger.getRootLogger().getAppender(LOG_FILE_APPENDER_NAME);
        }
        else if (logger != null ) {
            return logger.getAppender(LOG_FILE_APPENDER_NAME);
        }
        return null;
    }

    /**
     * Returns the layout for the 'LogFile' appender.
     *
     * @param logger the Logger to check
     * @return the Layout for the 'LogFile' appender, the DEFAULT_PATTERN_LAYOUT if 'LogFile' doesn't exist
     */
    private static Layout getLogFileAppenderLayout(Logger logger) {
        Appender logFileAppender = getLogFileAppender(logger);

        if (logFileAppender != null) {
            return logFileAppender.getLayout();
        }
        else {
            System.out.println(BatchLogger.class.getName() +": Could not find '"+ LOG_FILE_APPENDER_NAME +"' appender on RootLogger. Using DEFAULT_CONVERSION_PATTERN for layout to console");
            return DEFAULT_PATTERN_LAYOUT;
        }
    }
}
