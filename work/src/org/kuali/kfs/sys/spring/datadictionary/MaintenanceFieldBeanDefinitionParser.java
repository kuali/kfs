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

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class MaintenanceFieldBeanDefinitionParser extends KualiBeanDefinitionParserBase {

    @Override
    protected String getBaseBeanTypeParent(Element element) {
        return "MaintainableFieldDefinition";
    }
    
    @Override
    protected void doParse(Element element, ParserContext context, BeanDefinitionBuilder bean) {
        // get all attributes
        String attributeName = element.getAttribute("attributeName");
        String required = element.getAttribute("required");
        String unconditionallyReadOnly = element.getAttribute("unconditionallyReadOnly");
        String defaultValue = element.getAttribute("defaultValue");
        String defaultValueFinderClass = element.getAttribute("defaultValueFinderClass");
        String javascriptLeaveFieldFunction = element.getAttribute("javascriptLeaveFieldFunction");
        String javascriptLeaveFieldCallbackFunction = element.getAttribute("javascriptLeaveFieldCallbackFunction");

        // now, set on the bean definition
        if ( StringUtils.hasText(attributeName) ) {
            bean.addPropertyValue("name", attributeName);
        }
        if ( StringUtils.hasText(required) ) {
            bean.addPropertyValue("required", Boolean.parseBoolean(required));
        }
        if ( StringUtils.hasText(unconditionallyReadOnly) ) {
            bean.addPropertyValue("unconditionallyReadOnly", Boolean.parseBoolean(unconditionallyReadOnly));
        }
        if ( StringUtils.hasText(defaultValue) ) {
            bean.addPropertyValue("defaultValue", defaultValue);
        } else if ( StringUtils.hasText(defaultValueFinderClass) ) {
            bean.addPropertyValue("defaultValueFinderClass", defaultValueFinderClass);
        }
        if ( StringUtils.hasText(javascriptLeaveFieldFunction) ) {
            bean.addPropertyValue("webUILeaveFieldFunction", javascriptLeaveFieldFunction);
        }
        if ( StringUtils.hasText(javascriptLeaveFieldCallbackFunction) ) {
            bean.addPropertyValue("webUILeaveFieldCallbackFunction", javascriptLeaveFieldCallbackFunction);
        }
        
        // handle any other simple child properties
        parseEmbeddedPropertyElements(element, bean);
    }

    
}
