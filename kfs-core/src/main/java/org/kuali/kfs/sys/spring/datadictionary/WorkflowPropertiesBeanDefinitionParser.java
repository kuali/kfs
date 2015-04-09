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
