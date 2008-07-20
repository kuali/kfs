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

import org.kuali.core.datadictionary.FieldDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FieldBeanDefinitionParser extends KualiBeanDefinitionParserBase {

    @SuppressWarnings("unchecked")
    @Override
    protected Class getBeanClass(Element element) {
        return FieldDefinition.class;
    }
    
    @Override
    protected void doParse(Element element, BeanDefinitionBuilder bean) {
        // get all attributes
        String attributeName = element.getAttribute("attributeName");
        String required = element.getAttribute("required");
        String inquiry = element.getAttribute("inquiry");
        String lookup = element.getAttribute("lookup");
        String defaultValue = element.getAttribute("defaultValue");
        String defaultValueFinderClass = element.getAttribute("defaultValueFinderClass");
        String maxLength = element.getAttribute("maxLength");

        // now, set on the bean definition
        if ( StringUtils.hasText(attributeName) ) {
            bean.addPropertyValue("attributeName", attributeName);
        }
        if ( StringUtils.hasText(required) ) {
            bean.addPropertyValue("required", Boolean.parseBoolean(required));
        }
        if ( StringUtils.hasText(defaultValue) ) {
            bean.addPropertyValue("defaultValue", defaultValue);
        } else if ( StringUtils.hasText(defaultValueFinderClass) ) {
            bean.addPropertyValue("defaultValueFinderClass", defaultValueFinderClass);
        }
        if ( StringUtils.hasText(maxLength) ) {
            bean.addPropertyValue("maxLength", Integer.parseInt(maxLength));
        }
        if ( inquiry.equals( "no" ) ) {
            bean.addPropertyValue("noInquiry", Boolean.TRUE);
        } else if ( inquiry.equals( "force" ) ) {
            bean.addPropertyValue("forceInquiry", Boolean.TRUE);
        }
        if ( lookup.equals( "no" ) ) {
            bean.addPropertyValue("noLookup", Boolean.TRUE);
        } else if ( lookup.equals( "force" ) ) {
            bean.addPropertyValue("forceLookup", Boolean.TRUE);
        }

        // handle any other simple child properties
        parseEmbeddedPropertyElements(element, bean);
    }

    
}
