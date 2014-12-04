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
