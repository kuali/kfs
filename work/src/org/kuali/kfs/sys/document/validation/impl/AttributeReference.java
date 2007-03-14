/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.rules;

import org.kuali.kfs.util.SpringServiceLocator;

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
        this.label = SpringServiceLocator.getDataDictionaryService().getAttributeLabel(businessObjectClass, propertyName);
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
