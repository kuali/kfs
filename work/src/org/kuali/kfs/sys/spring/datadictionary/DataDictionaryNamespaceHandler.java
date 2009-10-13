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
package org.kuali.kfs.sys.spring.datadictionary;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class DataDictionaryNamespaceHandler extends NamespaceHandlerSupport {

    public void init() {
        registerBeanDefinitionParser( "field", new FieldBeanDefinitionParser() );
        registerBeanDefinitionParser( "relationship", new RelationshipBeanDefinitionParser() );
        registerBeanDefinitionParser( "maintField", new MaintenanceFieldBeanDefinitionParser() );
        registerBeanDefinitionParser( "workflow", new WorkflowPropertiesBeanDefinitionParser() );
        registerBeanDefinitionParser( "boAttribute", new AttributeBeanDefinitionParser() );
        registerBeanDefinitionParser( "boAttributeRef", new AttributeBeanDefinitionParser() );
        registerBeanDefinitionParser( "workflowAttributes", new WorkflowAttributesBeanDefinitionParser() );
    }

}
