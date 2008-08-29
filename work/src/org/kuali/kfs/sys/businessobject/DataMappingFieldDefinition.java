/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.businessobject;

import java.util.LinkedHashMap;

import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.kuali.rice.kns.bo.TransientBusinessObjectBase;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;

public class DataMappingFieldDefinition extends TransientBusinessObjectBase {
    private String namespaceCode;
    private String componentClass;
    private String propertyName;
    private FunctionalFieldDescription functionalFieldDescription;
    private BusinessObjectEntry businessObjectEntry;
    private ClassDescriptor classDescriptor;
    private AttributeDefinition attributeDefinition;
    private FieldDescriptor fieldDescriptor;
    
    // just on here to get lookups right
    private BusinessObjectComponent businessObjectComponent;
    private BusinessObjectProperty businessObjectProperty;

    public DataMappingFieldDefinition() {
    }

    public DataMappingFieldDefinition(FunctionalFieldDescription functionalFieldDescription, BusinessObjectEntry businessObjectEntry, ClassDescriptor classDescriptor, AttributeDefinition attributeDefinition) {
        setNamespaceCode(functionalFieldDescription.getNamespaceCode());
        setComponentClass(functionalFieldDescription.getComponentClass());
        setPropertyName(functionalFieldDescription.getPropertyName());
        setFunctionalFieldDescription(functionalFieldDescription);
        setBusinessObjectEntry(businessObjectEntry);
        setClassDescriptor(classDescriptor);
        setAttributeDefinition(attributeDefinition);
        setFieldDescriptor(classDescriptor.getFieldDescriptorByName(getPropertyName()));
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

    public FunctionalFieldDescription getFunctionalFieldDescription() {
        return functionalFieldDescription;
    }

    public void setFunctionalFieldDescription(FunctionalFieldDescription functionalFieldDescription) {
        this.functionalFieldDescription = functionalFieldDescription;
    }

    public BusinessObjectEntry getBusinessObjectEntry() {
        return businessObjectEntry;
    }

    public void setBusinessObjectEntry(BusinessObjectEntry businessObjectEntry) {
        this.businessObjectEntry = businessObjectEntry;
    }

    public AttributeDefinition getAttributeDefinition() {
        return attributeDefinition;
    }

    public ClassDescriptor getClassDescriptor() {
        return classDescriptor;
    }

    public void setClassDescriptor(ClassDescriptor classDescriptor) {
        this.classDescriptor = classDescriptor;
    }

    public void setAttributeDefinition(AttributeDefinition attributeDefinition) {
        this.attributeDefinition = attributeDefinition;
    }

    public FieldDescriptor getFieldDescriptor() {
        return fieldDescriptor;
    }

    public void setFieldDescriptor(FieldDescriptor fieldDescriptor) {
        this.fieldDescriptor = fieldDescriptor;
    }

    public BusinessObjectComponent getBusinessObjectComponent() {
        return businessObjectComponent;
    }

    public void setBusinessObjectComponent(BusinessObjectComponent businessObjectComponent) {
        this.businessObjectComponent = businessObjectComponent;
    }

    public BusinessObjectProperty getBusinessObjectProperty() {
        return businessObjectProperty;
    }

    public void setBusinessObjectProperty(BusinessObjectProperty businessObjectProperty) {
        this.businessObjectProperty = businessObjectProperty;
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        return new LinkedHashMap();
    }
}
