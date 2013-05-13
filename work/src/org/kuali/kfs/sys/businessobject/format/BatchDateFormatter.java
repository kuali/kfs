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
package org.kuali.kfs.sys.businessobject.format;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.web.format.FormatException;
import org.kuali.rice.core.web.format.Formatter;

/**
 * Formatter which specializes in reading dates from flat files
 */
public class BatchDateFormatter extends Formatter {
    private static final String DEFAULT_FLAT_FILE_DATE_FORMAT = "default.flatFile.dateFormat";
    private static String defaultDateFormat;

    private String dateFormat;
    private boolean formatToTimestamp = false;

    /**
     * Sets the date format to use for parsing
     * @param dateFormat
     */
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    /**
     * @return the date format associated with this batch date format - if not specified, then the default
     */
    public String getDateFormat() {
        return StringUtils.isBlank(dateFormat) ? getDefaultDateFormat() : dateFormat;
    }

    /**
     * @return the default date format, pulled from the application resource messages
     */
    public String getDefaultDateFormat() {
        if (defaultDateFormat == null) {
            defaultDateFormat = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(BatchDateFormatter.DEFAULT_FLAT_FILE_DATE_FORMAT);
        }
        return defaultDateFormat;
    }

    /**
     * Determines if the date should be parsed to a java.sql.Timestamp rather than the default java.sql.Date
     * @param formatToTimestamp true if result should be parsed as timestamp, false otherwise
     */
    public void setFormatToTimestamp(boolean formatToTimestamp) {
        this.formatToTimestamp = formatToTimestamp;
    }

    /**
     * Parses the given String to a date
     */
    @Override
    protected Object convertToObject(String string) {
        try {
            long time = new SimpleDateFormat(getDateFormat()).parse(string).getTime();
            if (formatToTimestamp) {
                return new java.sql.Timestamp(time);
            }
            return new java.sql.Date(time);
        }
        catch (ParseException e) {
            throw new FormatException("Date must be of the format " + dateFormat, e);
        }
    }
}
