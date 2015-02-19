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
package org.kuali.kfs.sys.document.validation;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.web.format.FormatException;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * An abstract class that defines methods needed to act on parameter properties for a validation.
 */
public abstract class ParameterizedValidation {
    private List<ValidationFieldConvertible> parameterProperties;
    
    /**
     * Gets the parameterProperties attribute. 
     * @return Returns the parameterProperties.
     */
    protected List<ValidationFieldConvertible> getParameterProperties() {
        return parameterProperties;
    }

    /**
     * Sets the parameterProperties attribute value.
     * @param parameterProperties The parameterProperties to set.
     */
    public void setParameterProperties(List<ValidationFieldConvertible> parameterProperties) {
        this.parameterProperties = parameterProperties;
    }
    
    /**
     * Given an event and the parameterProperties given by the validations, copies the values from the events to the proper fields in the validation. 
     * @param event an array to derive properties from
     * @param the parameter to set the parameters on
     */
    public void populateParametersFromEvent(AttributedDocumentEvent event) {
        if (getParameterProperties() != null) {
            for (ValidationFieldConvertible property: getParameterProperties()) {
                populateParameterFromEvent(event, property);
            }
        }
    }
    
    /**
     * Populates a single parameter field based on a field conversion, given an event to populate data from
     * @param event the event which acts as the source of data
     * @param validation the validation to populate
     * @param conversion the conversion information
     */
    protected void populateParameterFromEvent(AttributedDocumentEvent event, ValidationFieldConvertible conversion) {
        try {
            Class propertyClass = PropertyUtils.getPropertyType(event, conversion.getSourceEventProperty());
            Object propertyValue = ObjectUtils.getPropertyValue(event, conversion.getSourceEventProperty());
            if (propertyValue != null) {
                ObjectUtils.setObjectProperty(this, conversion.getTargetValidationProperty(), propertyClass, propertyValue);
            }
        }
        catch (FormatException fe) {
            throw new RuntimeException(fe);
        }
        catch (IllegalAccessException iae) {
            throw new RuntimeException(iae);
        }
        catch (InvocationTargetException ite) {
            throw new RuntimeException(ite);
        }
        catch (NoSuchMethodException nsme) {
            throw new RuntimeException(nsme);
        }
    }
}
