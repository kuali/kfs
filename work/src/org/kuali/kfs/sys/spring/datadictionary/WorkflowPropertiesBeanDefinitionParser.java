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

import java.util.ArrayList;

import org.kuali.rice.krad.datadictionary.WorkflowProperty;
import org.kuali.rice.krad.datadictionary.WorkflowPropertyGroup;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WorkflowPropertiesBeanDefinitionParser extends KualiBeanDefinitionParserBase {

    @Override
    protected String getBaseBeanTypeParent(Element element) {
        return "WorkflowProperties";
    }
    
    @Override
    protected void doParse(Element element, ParserContext context, BeanDefinitionBuilder bean) {
        // get all attributes
        handleAbstractAttribute(element, bean);
        
        // parse groups
        NodeList children = element.getChildNodes();
        ArrayList<WorkflowPropertyGroup> groups = new ArrayList<WorkflowPropertyGroup>();
        for ( int i = 0; i < children.getLength(); i++ ) {
            Node child = children.item(i);
            if ( child.getLocalName() != null && child.getLocalName().equals("group") ) {
                WorkflowPropertyGroup group = new WorkflowPropertyGroup();
                Element groupElement = (Element)child;                
                String basePath = groupElement.getAttribute("basePath");
                if ( StringUtils.hasText(basePath) ) {
                    group.setBasePath(basePath);
                }
                groups.add( group );
                // parse group paths
                NodeList groupChildren = groupElement.getChildNodes();
                for ( int j = 0 ; j < groupChildren.getLength(); j++ ) {
                    Node groupChild = groupChildren.item(j);
                    if ( groupChild.getLocalName() != null && groupChild.getLocalName().equals("path") ) {
                        Element pathElement = (Element)groupChild;                
                        String path = pathElement.getAttribute("path");
                        if ( StringUtils.hasText( path ) ) {
                            WorkflowProperty prop = new WorkflowProperty();
                            prop.setPath(path);
                            group.getWorkflowProperties().add( prop );
                        }
                    }
                    
                }
            }
        }        
        bean.addPropertyValue("workflowPropertyGroups", groups);
    }

    
}
