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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.KfsBusinessObjectMetaDataService;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class FunctionalFieldDescription extends PersistableBusinessObjectBase implements MutableInactivatable {
    private String namespaceCode;
    private String componentClass;
    private String propertyName;
    private String description;
    private boolean active;

    private BusinessObjectProperty businessObjectProperty;
    
    public FunctionalFieldDescription() {        
    }
    
    public FunctionalFieldDescription(String componentClass, String propertyName) {
        setComponentClass(componentClass);
        setPropertyName(propertyName);
    }
        
    
    public void refreshNonUpdateableReferences() {
        if (StringUtils.isNotBlank(getComponentClass()) && StringUtils.isNotBlank(getPropertyName()) && ((businessObjectProperty == null) || !getPropertyName().equals(businessObjectProperty.getPropertyName()) || (businessObjectProperty.getBusinessObjectComponent() == null) || !getComponentClass().equals(businessObjectProperty.getBusinessObjectComponent().getComponentClass()))) {
            setBusinessObjectProperty(SpringContext.getBean(KfsBusinessObjectMetaDataService.class).getBusinessObjectProperty(getComponentClass(), getPropertyName()));
            setNamespaceCode(businessObjectProperty.getNamespaceCode());
        }
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public BusinessObjectProperty getBusinessObjectProperty() {
        refreshNonUpdateableReferences();
        return businessObjectProperty;
    }

    public void setBusinessObjectProperty(BusinessObjectProperty businessObjectProperty) {
        this.businessObjectProperty = businessObjectProperty;
    }

}
