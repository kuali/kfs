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

import org.kuali.kfs.sys.KFSUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.lookup.keyvalues.ParameterNamespaceValuesFinder;

public class BusinessObjectComponent extends PersistableBusinessObjectBase {
    private String namespaceCode;
    private String namespaceName;
    private String componentClass;
    private String componentLabel;
    
    public BusinessObjectComponent() {
    }
    
    public BusinessObjectComponent(BusinessObjectEntry businessObjectEntry) {
        setNamespaceCode(SpringContext.getBean(ParameterService.class).getNamespace(businessObjectEntry.getBusinessObjectClass()));
        setNamespaceName(new ParameterNamespaceValuesFinder().getKeyLabel(getNamespaceCode()));
        setComponentClass(businessObjectEntry.getBusinessObjectClass().getName());
        setComponentLabel(businessObjectEntry.getObjectLabel());
    }

    public String getNamespaceCode() {
        return namespaceCode;
    }

    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
    }

    public String getNamespaceName() {
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
        return componentLabel;
    }

    public void setComponentLabel(String componentLabel) {
        this.componentLabel = componentLabel;
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, String> toString = new LinkedHashMap<String, String>();
        toString.put("namespaceCode", getNamespaceCode());
        toString.put("componentClass", getComponentClass());
        return toString;
    }
}
