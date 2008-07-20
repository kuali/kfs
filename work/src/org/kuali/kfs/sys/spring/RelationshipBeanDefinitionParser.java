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
package org.kuali.kfs.sys.spring;

import java.util.ArrayList;

import org.kuali.core.datadictionary.PrimitiveAttributeDefinition;
import org.kuali.core.datadictionary.RelationshipDefinition;
import org.kuali.core.datadictionary.SupportAttributeDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RelationshipBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @SuppressWarnings("unchecked")
    @Override
    protected Class getBeanClass(Element element) {
        return RelationshipDefinition.class;
    }
    
    @Override
    protected void doParse(Element element, BeanDefinitionBuilder bean) {
        // get all attributes
        String objectAttribute = element.getAttribute("objectAttribute");

        // now, set on the bean definition
        if ( StringUtils.hasText(objectAttribute) ) {
            bean.addPropertyValue("objectAttributeName", objectAttribute);
        }
        
        NodeList children = element.getChildNodes();
        ArrayList<PrimitiveAttributeDefinition> pDefs = new ArrayList<PrimitiveAttributeDefinition>();
        ArrayList<SupportAttributeDefinition> sDefs = new ArrayList<SupportAttributeDefinition>();
        
        for ( int i = 0; i < children.getLength(); i++ ) {
            Node child = children.item(i);
            String nodeName = child.getLocalName();
            if ( nodeName == null ) continue;
            
            if ( nodeName.equals("primitiveAttribute") ) {
                NamedNodeMap attributes = child.getAttributes();
                String source = attributes.getNamedItem("source").getNodeValue();
                String target = attributes.getNamedItem("target").getNodeValue();
                PrimitiveAttributeDefinition pad = new PrimitiveAttributeDefinition();
                pad.setSourceName(source);
                pad.setTargetName(target);
                pDefs.add(pad);
            } else if ( nodeName.equals("supportAttribute") ) {
                NamedNodeMap attributes = child.getAttributes();
                String source = attributes.getNamedItem("source").getNodeValue();
                String target = attributes.getNamedItem("target").getNodeValue();
                boolean identifier = false;
                String identifierStr = attributes.getNamedItem("identifier").getNodeValue();
                if ( identifierStr != null ) {
                    identifier = Boolean.valueOf( identifierStr );
                }
                SupportAttributeDefinition pad = new SupportAttributeDefinition();
                pad.setSourceName(source);
                pad.setTargetName(target);
                pad.setIdentifier(identifier);
                sDefs.add(pad);
            }
            bean.addPropertyValue("primitiveAttributes", pDefs);
            bean.addPropertyValue("supportAttributes", sDefs);
        }
    }

    
}
