/*
 * Copyright 2009 The Kuali Foundation.
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
