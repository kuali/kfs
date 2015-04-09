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
