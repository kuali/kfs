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
package org.kuali.kfs.sys.document.validation.impl;

import org.kuali.kfs.sys.document.validation.ValidationFieldConvertible;

/**
 * A simple class to create field conversions to specify in validations.
 */
public class ValidationFieldConversion implements ValidationFieldConvertible {
    private String sourceEventProperty;
    private String targetValidationProperty;
    /**
     * Gets the sourceEventProperty attribute, the property of the event to transfer to the validation
     * @return Returns the sourceEventProperty.
     */
    public String getSourceEventProperty() {
        return sourceEventProperty;
    }
    /**
     * Sets the sourceEventProperty attribute value, the property of the event to transfer to the validation
     * @param sourceEventProperty The sourceEventProperty to set.
     */
    public void setSourceEventProperty(String sourceEventProperty) {
        this.sourceEventProperty = cleanParameterProperty(sourceEventProperty);
    }
    /**
     * Gets the targetValidationProperty attribute, the property on the validation to transfer information from the event to
     * @return Returns the targetValidationProperty.
     */
    public String getTargetValidationProperty() {
        return targetValidationProperty;
    }
    /**
     * Sets the targetValidationProperty attribute value, the property on the validation to transfer information from the event to
     * @param targetValidationProperty The targetValidationProperty to set.
     */
    public void setTargetValidationProperty(String targetValidationProperty) {
        this.targetValidationProperty = targetValidationProperty;
    }
    
    /**
     * Removes extraneous information from a single property
     * @param property a property name to clean up
     * @return a cleaned up parameter property
     */
    private String cleanParameterProperty(String property) {
        return (property.startsWith("event.") ? property.replaceFirst("^event\\.", "") : property);
    }
}
