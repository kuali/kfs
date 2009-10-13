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

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AttributeBeanDefinitionParser extends KualiBeanDefinitionParserBase {

    private static Logger LOG = Logger.getLogger(AttributeBeanDefinitionParser.class);
    
    
    @Override
    protected String getBaseBeanTypeParent(Element element) {
        return "AttributeDefinition";
    }    
    
    
    @Override
    protected void doParse(Element element, ParserContext context, BeanDefinitionBuilder bean) {
        // get all attributes
        handleAbstractAttribute( element, bean );
        processAttributeAttributes(element, bean);
        // parse inner tags
        NodeList children = element.getChildNodes();
        processChildNodes( children, bean );
        parseEmbeddedPropertyElements(element, bean);
    }
    
    protected void processAttributeAttributes( Element element, BeanDefinitionBuilder bean ) {
        String attributeName = element.getAttribute("attributeName");
        String label = element.getAttribute("label");
        String shortLabel = element.getAttribute("shortLabel");
        String required = element.getAttribute("required");
        String forceUppercase = element.getAttribute("forceUppercase");
        String maxLength = element.getAttribute("maxLength");
        String displayLabelAttribute = element.getAttribute("displayLabelAttribute");
        String formatterClass = element.getAttribute("formatterClass");

        if ( StringUtils.hasText( attributeName ) ) {
            bean.addPropertyValue("name", attributeName);
        }
        if ( StringUtils.hasText( label ) ) {
            bean.addPropertyValue("label", label);
        }
        if ( StringUtils.hasText( shortLabel ) ) {
            bean.addPropertyValue("shortLabel", shortLabel);
        }
        if ( StringUtils.hasText( displayLabelAttribute ) ) {
            bean.addPropertyValue("displayLabelAttribute", displayLabelAttribute);
        }
        if ( StringUtils.hasText( formatterClass ) ) {
            bean.addPropertyValue("formatterClass", formatterClass);
        }
        if ( StringUtils.hasText( required ) ) {
            bean.addPropertyValue("required", Boolean.valueOf(required));
        }
        if ( StringUtils.hasText( forceUppercase ) ) {
            bean.addPropertyValue("forceUppercase", Boolean.valueOf(forceUppercase));
        }
        if ( StringUtils.hasText( maxLength ) ) {
            bean.addPropertyValue("maxLength", Integer.valueOf(maxLength));
        }
    }

    protected void processChildNodes( NodeList children, BeanDefinitionBuilder bean ) {
        for ( int i = 0; i < children.getLength(); i++ ) {
            Node child = children.item(i);
            if ( child.getNodeType() != Node.ELEMENT_NODE ) continue;
            Element ele = (Element)child;
            String elementName = ele.getLocalName();
            if ( elementName == null ) continue;
            if ( elementName.equals( "text" ) ) {
                bean.addPropertyValue("control", processTextControlElement(ele) );
            } else if ( elementName.equals( "textarea" ) ) {
                bean.addPropertyValue("control", processTextareaControlElement(ele) );
            } else if ( elementName.equals( "select" ) ) {
                bean.addPropertyValue("control", processSelectControlElement(ele) );
            } else if ( elementName.equals( "radio" ) ) {
                bean.addPropertyValue("control", processRadioControlElement(ele) );
            } else if ( elementName.equals( "hidden" ) ) {
                bean.addPropertyValue("control", processHiddenControlElement(ele) );
            } else if ( elementName.equals( "user" ) ) {
                bean.addPropertyValue("control", processUserControlElement(ele) );
            } else if ( elementName.equals( "checkbox" ) ) {
                bean.addPropertyValue("control", processCheckboxControlElement(ele) );
            } else if ( elementName.equals( "validationPattern") ) {
                bean.addPropertyValue("validationPattern", processValidationPatternElement(ele) );
            }
        }
    }
    
    protected BeanDefinition processTextControlElement( Element ele ) {
        BeanDefinitionBuilder controlBean = BeanDefinitionBuilder.childBeanDefinition( "TextControlDefinition" );
        
        String size = ele.getAttribute("size");
        if ( StringUtils.hasText(size) ) {
            controlBean.addPropertyValue("size", Integer.valueOf(size) );
        }
        parseEmbeddedPropertyElements(ele, controlBean);
        
        return controlBean.getBeanDefinition();
    }

    protected BeanDefinition processUserControlElement( Element ele ) {
        BeanDefinitionBuilder controlBean = BeanDefinitionBuilder.childBeanDefinition( "KualiUserControlDefinition" );
        
        String universalIdAttribute = ele.getAttribute("universalIdAttribute");
        if ( StringUtils.hasText(universalIdAttribute) ) {
            controlBean.addPropertyValue("universalIdAttributeName", universalIdAttribute );
        }
        String userObjectAttribute = ele.getAttribute("userObjectAttribute");
        if ( StringUtils.hasText(userObjectAttribute) ) {
            controlBean.addPropertyValue("userIdAttributeName", userObjectAttribute + "." + KFSPropertyConstants.PERSON_USER_ID );
            controlBean.addPropertyValue("personNameAttributeName", userObjectAttribute + "." + KFSPropertyConstants.PERSON_NAME );
        }
        parseEmbeddedPropertyElements(ele, controlBean);
        
        return controlBean.getBeanDefinition();
    }

    protected BeanDefinition processTextareaControlElement( Element ele ) {
        BeanDefinitionBuilder controlBean = BeanDefinitionBuilder.childBeanDefinition( "TextareaControlDefinition" );
        
        String rows = ele.getAttribute("rows");
        if ( StringUtils.hasText(rows) ) {
            controlBean.addPropertyValue("rows", Integer.valueOf(rows) );
        }
        String cols = ele.getAttribute("rows");
        if ( StringUtils.hasText(cols) ) {
            controlBean.addPropertyValue("cols", Integer.valueOf(cols) );
        }
        parseEmbeddedPropertyElements(ele, controlBean);
        
        return controlBean.getBeanDefinition();
    }

    protected BeanDefinition processHiddenControlElement( Element ele ) {
        BeanDefinitionBuilder controlBean = BeanDefinitionBuilder.childBeanDefinition( "HiddenControlDefinition" );
        parseEmbeddedPropertyElements(ele, controlBean);
        return controlBean.getBeanDefinition();
    }

    protected BeanDefinition processCheckboxControlElement( Element ele ) {
        BeanDefinitionBuilder controlBean = BeanDefinitionBuilder.childBeanDefinition( "CheckboxControlDefinition" );
        parseEmbeddedPropertyElements(ele, controlBean);
        return controlBean.getBeanDefinition();
    }
    
    protected void setMultiValueControlAttributes( Element ele, BeanDefinitionBuilder controlBean ) {
        String valuesFinderClass = ele.getAttribute("valuesFinderClass");
        if ( StringUtils.hasText(valuesFinderClass) ) {
            controlBean.addPropertyValue("valuesFinderClass", valuesFinderClass );
        }
        String boClass = ele.getAttribute("boClass");
        if ( StringUtils.hasText(boClass) ) {
            controlBean.addPropertyValue("boClass", boClass );
        }
        String keyAttribute = ele.getAttribute("keyAttribute");
        if ( StringUtils.hasText(keyAttribute) ) {
            controlBean.addPropertyValue("keyAttribute", keyAttribute );
        }
        String labelAttribute = ele.getAttribute("labelAttribute");
        if ( StringUtils.hasText(labelAttribute) ) {
            controlBean.addPropertyValue("labelAttribute", labelAttribute );
        }
        String includeKeyInLabel = ele.getAttribute("includeKeyInLabel");
        if ( StringUtils.hasText(includeKeyInLabel) ) {
            controlBean.addPropertyValue("includeKeyInLabel", Boolean.valueOf(includeKeyInLabel) );
        }
    }
    
    protected BeanDefinition processSelectControlElement( Element ele ) {
        BeanDefinitionBuilder controlBean = BeanDefinitionBuilder.childBeanDefinition( "SelectControlDefinition" );
        
        setMultiValueControlAttributes( ele, controlBean );
        parseEmbeddedPropertyElements(ele, controlBean);
                  
        return controlBean.getBeanDefinition();
    }

    protected BeanDefinition processRadioControlElement( Element ele ) {
        BeanDefinitionBuilder controlBean = BeanDefinitionBuilder.childBeanDefinition( "RadioControlDefinition" );

        setMultiValueControlAttributes( ele, controlBean );
        parseEmbeddedPropertyElements(ele, controlBean);
        
        return controlBean.getBeanDefinition();
    }

    protected BeanDefinition processValidationPatternElement( Element ele ) {
        BeanDefinitionBuilder validatorBean = null;
        String parent = ele.getAttribute( "parent" );
        String validationPatternClass = ele.getAttribute( "validationPatternClass" );
        if ( StringUtils.hasText(parent) ) {
            validatorBean = BeanDefinitionBuilder.childBeanDefinition( parent );
        } else if ( StringUtils.hasText(validationPatternClass)) {
            try {
                validatorBean = BeanDefinitionBuilder.rootBeanDefinition(Class.forName(validationPatternClass));
            } catch ( ClassNotFoundException ex ) {
                LOG.fatal( "Invalid class name given for validationPattern bean: " + validationPatternClass );
                throw new RuntimeException( "Invalid class name given for validationPattern bean: " + validationPatternClass, ex );
            }
        }
        
        parseEmbeddedPropertyElements(ele, validatorBean);
                   
        return validatorBean.getBeanDefinition();
    }
}
