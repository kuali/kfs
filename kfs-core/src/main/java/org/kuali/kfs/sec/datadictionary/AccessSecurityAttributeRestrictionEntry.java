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
package org.kuali.kfs.sec.datadictionary;


import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.sec.service.AccessPermissionEvaluator;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;


/**
 * Holds configuration on an attribute restriction. Mapping the attribute restriction type to a business object property name
 */
public class AccessSecurityAttributeRestrictionEntry {
    protected String securityAttributeName;
    protected AttributeDefinition attribute;
    protected Class<? extends AccessPermissionEvaluator> accessPermissionEvaluatorClass;
    protected Map<String, AttributeDefinition> otherKeyFields;

    public AccessSecurityAttributeRestrictionEntry() {
        otherKeyFields = new HashMap<String, AttributeDefinition>();
    }

    /**
     * Gets the securityAttributeName attribute.
     * 
     * @return Returns the securityAttributeName.
     */
    public String getSecurityAttributeName() {
        return securityAttributeName;
    }

    /**
     * Sets the securityAttributeName attribute value.
     * 
     * @param securityAttributeName The securityAttributeName to set.
     */
    public void setSecurityAttributeName(String securityAttributeName) {
        this.securityAttributeName = securityAttributeName;
    }

    /**
     * Gets the attribute attribute.
     * 
     * @return Returns the attribute.
     */
    public AttributeDefinition getAttribute() {
        return attribute;
    }

    /**
     * Sets the attribute attribute value.
     * 
     * @param attribute The attribute to set.
     */
    public void setAttribute(AttributeDefinition attribute) {
        this.attribute = attribute;
    }

    /**
     * Gets the accessPermissionEvaluatorClass attribute.
     * 
     * @return Returns the accessPermissionEvaluatorClass.
     */
    public Class<? extends AccessPermissionEvaluator> getAccessPermissionEvaluatorClass() {
        return accessPermissionEvaluatorClass;
    }

    /**
     * Sets the accessPermissionEvaluatorClass attribute value.
     * 
     * @param accessPermissionEvaluatorClass The accessPermissionEvaluatorClass to set.
     */
    public void setAccessPermissionEvaluatorClass(Class<? extends AccessPermissionEvaluator> accessPermissionEvaluatorClass) {
        this.accessPermissionEvaluatorClass = accessPermissionEvaluatorClass;
    }

    /**
     * Gets the otherKeyFields attribute.
     * 
     * @return Returns the otherKeyFields.
     */
    public Map<String, AttributeDefinition> getOtherKeyFields() {
        return otherKeyFields;
    }

    /**
     * Sets the otherKeyFields attribute value.
     * 
     * @param otherKeyFields The otherKeyFields to set.
     */
    public void setOtherKeyFields(Map<String, AttributeDefinition> otherKeyFields) {
        this.otherKeyFields = otherKeyFields;
    }
    
    

}
