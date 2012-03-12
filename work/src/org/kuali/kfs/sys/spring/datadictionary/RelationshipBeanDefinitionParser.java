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

import org.kuali.rice.krad.datadictionary.PrimitiveAttributeDefinition;
import org.kuali.rice.krad.datadictionary.SupportAttributeDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RelationshipBeanDefinitionParser extends KualiBeanDefinitionParserBase {

    @Override
    protected String getBaseBeanTypeParent(Element element) {
        return "RelationshipDefinition";
    }
    
    @Override
    protected void doParse(Element element, ParserContext context, BeanDefinitionBuilder bean) {
        // get all attributes
        String objectAttribute = element.getAttribute("objectAttribute");
        String targetClass = element.getAttribute("targetClass");

        // now, set on the bean definition
        if ( StringUtils.hasText(objectAttribute) ) {
            bean.addPropertyValue("objectAttributeName", objectAttribute);
        }
        if ( StringUtils.hasText(targetClass) ) {
            bean.addPropertyValue("targetClass", targetClass);
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
