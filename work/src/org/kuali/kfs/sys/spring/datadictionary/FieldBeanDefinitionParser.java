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

public class FieldBeanDefinitionParser extends KualiBeanDefinitionParserBase {

    @Override
    protected String getBaseBeanTypeParent(Element element) {
        return "FieldDefinition";
    }
    
    @Override
    protected void doParse(Element element, ParserContext context, BeanDefinitionBuilder bean) {
        // get all attributes
        String attributeName = element.getAttribute("attributeName");
        String required = element.getAttribute("required");
        String inquiry = element.getAttribute("inquiry");
        String lookup = element.getAttribute("lookup");
        String defaultValue = element.getAttribute("defaultValue");
        String defaultValueFinderClass = element.getAttribute("defaultValueFinderClass");
        String maxLength = element.getAttribute("maxLength");
        String useShortLabel = element.getAttribute("useShortLabel");
        String hidden = element.getAttribute("hidden");
        String readOnly = element.getAttribute("readOnly");
        
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
        if ( StringUtils.hasText(useShortLabel) ) {
            bean.addPropertyValue("useShortLabel", Boolean.parseBoolean(useShortLabel));
        }
        if ( StringUtils.hasText(hidden)) {
            bean.addPropertyValue("hidden", Boolean.parseBoolean(hidden));
        }
        if ( StringUtils.hasText(readOnly)) {
            bean.addPropertyValue("readOnly", Boolean.parseBoolean(readOnly));
        }
        
        // handle any other simple child properties
        parseEmbeddedPropertyElements(element, bean);
    }

    
}
