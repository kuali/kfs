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
package org.kuali.kfs.sys.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.TransientBusinessObjectBase;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;

public class BusinessObjectProperty extends TransientBusinessObjectBase {
    private String namespaceCode;
    private String componentClass;
    private String propertyName;
    private String propertyLabel;

    private BusinessObjectComponent businessObjectComponent;
    
    public BusinessObjectProperty() {
    }
    
    public BusinessObjectProperty(BusinessObjectComponent businessObjectComponent, AttributeDefinition attributeDefinition) {
        setBusinessObjectComponent(businessObjectComponent);
        setNamespaceCode(businessObjectComponent.getNamespaceCode());
        setComponentClass(businessObjectComponent.getComponentClass());
        setPropertyName(attributeDefinition.getName());
        setPropertyLabel(attributeDefinition.getLabel());
    }

    public String getNamespaceCode() {
        return namespaceCode;
    }

    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
    }

    public String getComponentClass() {
        return componentClass;
    }

    public void setComponentClass(String componentClass) {
        this.componentClass = componentClass;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyLabel() {
        return propertyLabel;
    }

    public void setPropertyLabel(String propertyLabel) {
        this.propertyLabel = propertyLabel;
    }

    public BusinessObjectComponent getBusinessObjectComponent() {
        return businessObjectComponent;
    }

    public void setBusinessObjectComponent(BusinessObjectComponent businessObjectComponent) {
        this.businessObjectComponent = businessObjectComponent;
    }

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> toString = new LinkedHashMap<String, String>();
        toString.put("namespaceCode", getNamespaceCode());
        toString.put("componentClass", getComponentClass());
        toString.put("propertyName", getPropertyName());
        return toString;
    }
}
