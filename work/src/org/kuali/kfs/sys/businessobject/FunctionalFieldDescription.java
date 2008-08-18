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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.DataDictionaryService;

public class FunctionalFieldDescription extends PersistableBusinessObjectBase {
    private String namespaceCode;
    private String namespaceName;
    private String componentClass;
    private String componentLabel;
    private String propertyName;
    private String propertyLabel;
    private String functionalFieldDescription;
    private boolean active;
    
    private BusinessObjectComponent businessObjectComponent;
    private BusinessObjectProperty businessObjectProperty;
    
    @Override
    public void refreshNonUpdateableReferences() {
        if (((businessObjectComponent == null)
                || !businessObjectComponent.getNamespaceCode().equals(getNamespaceCode())
                || !businessObjectComponent.getComponentClass().equals(getComponentClass())
                || !businessObjectProperty.getPropertyName().equals(getPropertyName()))
              && (StringUtils.isNotBlank(getComponentClass()) && StringUtils.isNotBlank(getPropertyName()))) {
            setBusinessObjectComponent(new BusinessObjectComponent(SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(getComponentClass())));
            setNamespaceCode(businessObjectComponent.getNamespaceCode());
            setNamespaceName(businessObjectComponent.getNamespaceName());
            setComponentLabel(businessObjectComponent.getComponentLabel());
            if (((businessObjectProperty == null)
                    || !businessObjectProperty.getNamespaceCode().equals(getNamespaceCode())
                    || !businessObjectProperty.getComponentClass().equals(getComponentClass())
                    || !businessObjectProperty.getPropertyName().equals(getPropertyName()))
                  && (StringUtils.isNotBlank(getComponentClass()) && StringUtils.isNotBlank(getPropertyName()))) {
                setBusinessObjectProperty(new BusinessObjectProperty(businessObjectComponent, SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(getComponentClass()).getAttributeDefinition(getPropertyName())));
                setPropertyLabel(businessObjectProperty.getPropertyLabel());
            }
        }
    }

    public String getNamespaceCode() {
        return namespaceCode;
    }

    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
    }

    public String getNamespaceName() {
        refreshNonUpdateableReferences();
        return namespaceName;
    }

    public void setNamespaceName(String namespaceName) {
        this.namespaceName = namespaceName;
    }

    public String getComponentClass() {
        return componentClass;
    }

    public void setComponentClass(String componentClass) {
        this.componentClass = componentClass;
    }

    public String getComponentLabel() {
        refreshNonUpdateableReferences();
        return componentLabel;
    }

    public void setComponentLabel(String componentLabel) {
        this.componentLabel = componentLabel;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyLabel() {
        refreshNonUpdateableReferences();
        return propertyLabel;
    }

    public void setPropertyLabel(String propertyLabel) {
        this.propertyLabel = propertyLabel;
    }

    public String getFunctionalFieldDescription() {
        return functionalFieldDescription;
    }

    public void setFunctionalFieldDescription(String functionalFieldDescription) {
        this.functionalFieldDescription = functionalFieldDescription;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
        if (businessObjectProperty != null) {
            return businessObjectProperty.toStringMapper();
        }
        return new LinkedHashMap<String,String>();
    }
}
