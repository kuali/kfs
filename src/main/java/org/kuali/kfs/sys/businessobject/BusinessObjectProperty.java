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
