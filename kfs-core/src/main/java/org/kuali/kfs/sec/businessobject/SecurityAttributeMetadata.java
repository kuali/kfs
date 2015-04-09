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
