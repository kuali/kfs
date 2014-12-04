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

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DataDictionaryService;

/**
 * This class refers to an attribute which has a value and is labeled in the DataDictionary.
 */
public class AttributeReference {
    private final String propertyName;
    private final String valueString;
    private final String label;

    /**
     * Constructor. It takes the value instead of the BO instance (using PropertyUtils or BeanUtils with the propertyName), in order
     * to promote the use of the property getter, to help usage searches and automated refactorings.
     * 
     * @param businessObjectClass a class with a business object entry in the DataDictionary
     * @param propertyName the name of the attribute in the DD entry and on the form (for the error path)
     * @param value the value of the property on the form (may be null).
     */
    public AttributeReference(Class businessObjectClass, String propertyName, Object value) {
        this.propertyName = propertyName;
        this.valueString = value == null ? null : value.toString();
        this.label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(businessObjectClass, propertyName);
    }

    /**
     * @return the name of the attribute in the DD entry and on the form (for the error path)
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * @return the attribute value as a String
     */
    public String getValueString() {
        return valueString;
    }

    /**
     * @return the label of the attribute in the DD
     */
    public String getLabel() {
        return label;
    }
}
