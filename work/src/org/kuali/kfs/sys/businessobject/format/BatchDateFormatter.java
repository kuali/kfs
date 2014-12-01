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
