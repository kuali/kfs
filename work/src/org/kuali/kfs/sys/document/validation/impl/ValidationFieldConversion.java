/*
 * Copyright 2008 The Kuali Foundation
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
