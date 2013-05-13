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
package org.kuali.kfs.sys.batch;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.kfs.sys.businessobject.format.BatchDateFormatter;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.springframework.util.StringUtils;


/**
 * Base abstract class for configuration element which represents a substring of a given line
 * to be formatted and set on a business object
 */
public abstract class AbstractFlatFilePropertySpecificationBase implements FlatFilePropertySpecification {
    protected String propertyName;
    protected boolean rightTrim;
    protected boolean leftTrim;
    protected Class<? extends Formatter> formatterClass = Formatter.class;
    protected String dateFormat;
    protected boolean formatToTimestamp = false;

    /**
     * @return the name of the property that should be set
     */
    @Override
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Sets the property on the business object
     * @param value the substring of the parsed line to set
     * @param businessObject the business object to set the parsed line on
     * @param lineNumber the parsed line number
     */

    @Override
    public void setProperty(String value, Object businessObject, int lineNumber) {
        if (leftTrim) {
            value = StringUtils.trimLeadingWhitespace(value);
        }

        if (rightTrim) {
            value = StringUtils.trimTrailingWhitespace(value);
        }
        try {
            PropertyUtils.setProperty(businessObject, propertyName, getFormattedObject(value, businessObject));
        }
        catch (Exception e) {
            throw new RuntimeException("Exception occurred on line " + lineNumber + " while setting value " + value + " for property " + propertyName + "."  , e);
        }
    }

    /**
     * Returns the formatter class to format the substring before it is set on the business object
     * @param parsedObject the object that is being parsed into
     * @return the class for the formatter
     */
    protected Class<?> getFormatterClass(Object parsedObject) {
        if (Formatter.class.isAssignableFrom(formatterClass) ) {
            Class<? extends Formatter> attributeFormatter = KRADServiceLocatorWeb.getDataDictionaryService().getAttributeFormatter(parsedObject.getClass(), this.propertyName);
            if (attributeFormatter != null) {
                this.formatterClass = attributeFormatter;
            }
        }

        if (!Formatter.class.isAssignableFrom(this.formatterClass)) {
            throw new RuntimeException("formatterClass is not a valid instance of " + Formatter.class.getName() + " instead was: " + formatterClass.getName());
        }
        return formatterClass;
    }

    /**
     * Builds a formatter to format the parsed substring before it is set as a property on the business object
     * @param parsedObject the business object to parse into
     * @return the formatter
     */
    protected Formatter getFormatter(Object parsedObject) {
        Formatter formatter = null;
        try {
            formatter = (Formatter) getFormatterClass(parsedObject).newInstance();
        }
        catch (InstantiationException ie) {
            throw new RuntimeException("Could not instantiate object of class " + formatterClass.getName(), ie);
        }
        catch (IllegalAccessException iae) {
            throw new RuntimeException("Illegal access attempting to instantiate object of class " + formatterClass.getName(), iae);
        }
        return formatter;
    }

    /**
     * Sets the formatter class to use in this proprety specification
     * @param formatterClass the class of the formatter to use
     */
    public void setFormatterClass(Class<? extends Formatter> formatterClass) {
        if (!Formatter.class.isAssignableFrom(formatterClass)) {
            throw new RuntimeException("formatterClass is not a valid instance of " + Formatter.class.getName() + " instead was: " + formatterClass.getName());
        }
        this.formatterClass = formatterClass;
    }

    /**
     * This method returns the formatted object for the given string. It uses the formatter class define for specification property
     * if exists, otherwise look to the Data Dictionary of the parsedObject , otherwise use the default formatter class.
     * @param subString the parsed subString
     * @param the object to parse into
     */
    protected Object getFormattedObject(String subString, Object parsedObject) {
        Formatter formatter = getFormatter(parsedObject);
        if (formatter instanceof BatchDateFormatter) {
            ((BatchDateFormatter) formatter).setDateFormat(dateFormat);
            if (formatToTimestamp) {
                ((BatchDateFormatter) formatter).setFormatToTimestamp(true);
            }
        }
        return formatter.convertFromPresentationFormat(subString);
    }

    /**
     * Sets the name of the property this property specification will target for filling in the business object to parse into
     * @param propertyName the name of the target property
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Determines if the substring should have all whitespace on the right removed before setting
     * @param rightTrim true if all whitespace on the right should be removed, false otherwise
     */
    public void setRightTrim(boolean rightTrim) {
        this.rightTrim = rightTrim;
    }

    /**
     * Determines if the substring should have all whitepsace on the left removed before setting
     * @param leftTrim true if all whitespace on the left should be removed, false otherwise
     */
    public void setLeftTrim(boolean leftTrim) {
        this.leftTrim = leftTrim;
    }

    /**
     * If the substring represents a date, then this is the format used to parse that date; it should be
     * compatible with java.text.SimpleDateFormat
     * @param dateFormat the date format to utilized
     */
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    /**
     * If the formatter for this class is a BatchDateFormatter, then the formatted Date should be in the form of a timestamp
     *
     * @param formatToTimestamp true if we should format to timestamp, false (the default) if we should not
     */
    public void setFormatToTimestamp(boolean formatToTimestamp) {
        this.formatToTimestamp = formatToTimestamp;
    }

}
