/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.sec.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sec.SecPropertyConstants;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;


/**
 * Holds fields that provide metadata information for a security attribute
 */
public class SecurityAttributeMetadata extends TransientBusinessObjectBase {
    private Class attributeClass;
    private String attributeField;
    private String attributeNameField;

    public SecurityAttributeMetadata() {
    }

    public SecurityAttributeMetadata(Class attributeClass, String attributeField) {
        this.attributeClass = attributeClass;
        this.attributeField = attributeField;
    }

    public SecurityAttributeMetadata(Class attributeClass, String attributeField, String attributeNameField) {
        this.attributeClass = attributeClass;
        this.attributeField = attributeField;
        this.attributeNameField = attributeNameField;
    }

    /**
     * Gets the attributeClass attribute.
     * 
     * @return Returns the attributeClass.
     */
    public Class getAttributeClass() {
        return attributeClass;
    }


    /**
     * Sets the attributeClass attribute value.
     * 
     * @param attributeClass The attributeClass to set.
     */
    public void setAttributeClass(Class attributeClass) {
        this.attributeClass = attributeClass;
    }


    /**
     * Gets the attributeField attribute.
     * 
     * @return Returns the attributeField.
     */
    public String getAttributeField() {
        return attributeField;
    }


    /**
     * Sets the attributeField attribute value.
     * 
     * @param attributeField The attributeField to set.
     */
    public void setAttributeField(String attributeField) {
        this.attributeField = attributeField;
    }


    /**
     * Gets the attributeNameField attribute.
     * 
     * @return Returns the attributeNameField.
     */
    public String getAttributeNameField() {
        return attributeNameField;
    }

    /**
     * Sets the attributeNameField attribute value.
     * 
     * @param attributeNameField The attributeNameField to set.
     */
    public void setAttributeNameField(String attributeNameField) {
        this.attributeNameField = attributeNameField;
    }

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(SecPropertyConstants.ATTRIBUTE_ID, this.attributeField);

        return m;
    }

}
