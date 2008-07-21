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
package org.kuali.kfs.sys.spring.datadictionary;

import java.util.ArrayList;

import org.kuali.core.datadictionary.ReferenceDefinition;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DefaultExistenceCheckBeanDefinitionParser extends KualiBeanDefinitionParserBase {

    @Override
    protected String getBaseBeanTypeParent(Element element) {
        return null;
    }
    
    @Override
    protected Class getBeanClass(Element element) {
        return ListFactoryBean.class;
    }
    
    @Override
    protected void doParse(Element element, ParserContext context, BeanDefinitionBuilder bean) {
        // get all attributes

        // parse checks
        NodeList children = element.getChildNodes();
        ArrayList<ReferenceDefinition> checks = new ArrayList<ReferenceDefinition>();
        for ( int i = 0; i < children.getLength(); i++ ) {
            Node child = children.item(i);
            String nodeName = child.getLocalName();
            if ( nodeName == null ) continue;
          
            if ( nodeName.equals( "check" ) ) {
                Element checkElement = (Element)child;
                ReferenceDefinition rd = setStandardAttributes(checkElement);
                checks.add(rd);
            } else if ( nodeName.equals( "collectionCheck" ) ) {
                Element checkElement = (Element)child;
                ReferenceDefinition rd = setStandardAttributes(checkElement);
                String collectionAttribute = checkElement.getAttribute("collectionAttribute");
                rd.setCollection(collectionAttribute);
                checks.add(rd);
            } else if ( nodeName.equals( "userCheck" ) ) {
                Element checkElement = (Element)child;
                ReferenceDefinition rd = new ReferenceDefinition();
                String userAttribute = checkElement.getAttribute("userAttribute");
                
                rd.setAttributeName( userAttribute );
                rd.setAttributeToHighlightOnFail( userAttribute + "." + KFSPropertyConstants.PERSON_USER_ID );
                checks.add(rd);
            }
        }        
        bean.addPropertyValue("sourceList", checks);
    }

    protected ReferenceDefinition setStandardAttributes( Element checkElement ) {
        ReferenceDefinition rd = new ReferenceDefinition();
        String boAttribute = checkElement.getAttribute("boAttribute");
        String activeIndicatorAttribute = checkElement.getAttribute("activeIndicatorAttribute");
        String attributeToHighlightOnFail = checkElement.getAttribute("attributeToHighlightOnFail");
        String activeIndicatorReversed = checkElement.getAttribute("activeIndicatorReversed");
        String displayAttribute = checkElement.getAttribute("displayAttribute");

        rd.setAttributeName( boAttribute );
        rd.setAttributeToHighlightOnFail( attributeToHighlightOnFail );
        if ( StringUtils.hasText(activeIndicatorAttribute) ) {
            rd.setActiveIndicatorAttributeName( activeIndicatorAttribute );
        }
        if ( StringUtils.hasText(displayAttribute) ) {
            rd.setDisplayFieldName( displayAttribute );
        }
        if ( StringUtils.hasText(activeIndicatorReversed) ) {
            rd.setActiveIndicatorReversed( Boolean.valueOf( activeIndicatorReversed ) );
        }
        return rd;
    }
    
}
