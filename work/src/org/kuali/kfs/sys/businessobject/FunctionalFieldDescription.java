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

import org.kuali.rice.kns.bo.ParameterNamespace;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public class FunctionalFieldDescription extends PersistableBusinessObjectBase {
    
    private String namespaceCode;
    private String componentClass;
    private String propertyName;
    private String functionalFieldDescription;
    private boolean active;
    
    private ParameterNamespace parameterNamespace;
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public String getComponentClass() {
        return componentClass;
    }
    
    public void setComponentClass(String componentClass) {
        this.componentClass = componentClass;
    }
    
    public String getFunctionalFieldDescription() {
        return functionalFieldDescription;
    }
    
    public void setFunctionalFieldDescription(String functionalFieldDescription) {
        this.functionalFieldDescription = functionalFieldDescription;
    }
    
    public String getPropertyName() {
        return propertyName;
    }
    
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
    
    public String getNamespaceCode() {
        return namespaceCode;
    }
    
    public void setNamespaceCode(String nameSpaceCode) {
        this.namespaceCode = nameSpaceCode;
    }
    
    public ParameterNamespace getParameterNamespace() {
        return parameterNamespace;
    }
    
    public void setParameterNamespace(ParameterNamespace parameterNamespace) {
        this.parameterNamespace = parameterNamespace;
    }
    
    @Override
    protected LinkedHashMap toStringMapper() {
        // TODO Auto-generated method stub
        return null;
    }

}
