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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.datadictionary.WorkflowAttributeDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WorkflowAttributesBeanDefinitionParser extends KualiBeanDefinitionParserBase {
    private static final String SEARCHING_ATTRIBUTE = "searchingAttribute";

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder bean) {
        handleAbstractAttribute(element, bean);

        parseChildElements(element, bean);
    }

    protected void parseChildElements(Element workflowAttributeElement, BeanDefinitionBuilder bean) {
        NodeList children = workflowAttributeElement.getChildNodes();
        Map<String, WorkflowAttributeDefinition> searchingAttributesMap = parseSearchableAttributes(children);
        bean.addPropertyValue("searchingAttributeDefinitions", searchingAttributesMap);
        
        Map<String, List<WorkflowAttributeDefinition>> routingTypesMap = parseRoutingTypes(children);
        bean.addPropertyValue("routingTypeDefinitions", routingTypesMap);
    }
    
    protected Map<String, WorkflowAttributeDefinition> parseSearchableAttributes(NodeList workflowAttributesChildren) {
        Map<String, WorkflowAttributeDefinition> searchingAttributesMap = new HashMap<String, WorkflowAttributeDefinition>();
        for (int i = 0; i < workflowAttributesChildren.getLength(); i++) {
            Node workflowAttributesChild = workflowAttributesChildren.item(i);
            if (workflowAttributesChild.getNodeType() == Node.ELEMENT_NODE) {
                if (((Element) workflowAttributesChild).getLocalName().equals(SEARCHING_ATTRIBUTE)) {
                    Element searchableAttribute = (Element) workflowAttributesChild;
                    
                    String name = searchableAttribute.getAttribute("name");
                    
                    List<Element> workflowAttributeList = extractWorkflowAttributeElements(searchableAttribute.getChildNodes());
                    if (workflowAttributeList.size() != 1) {
                        throw new RuntimeException("There should be exactly one workflowAttribute defined for each searchingAttribute definition");
                    }
                    Element workflowAttributeDefinitionElement = (Element) workflowAttributeList.get(0);
                    WorkflowAttributeDefinition workflowAttributeDefinition = parseWorkflowAttributeDefinition(workflowAttributeDefinitionElement);
                    
                    searchingAttributesMap.put(name, workflowAttributeDefinition);
                }
            }
        }
        return searchingAttributesMap;
    }
    
    protected List<Element> extractWorkflowAttributeElements(NodeList nodes) {
        List<Element> results = new ArrayList<Element>();
        
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (((Element) node).getLocalName().equals("workflowAttribute")) {
                    results.add((Element) node);
                }
            }
        }
        return results;
    }
    
    protected Map<String, List<WorkflowAttributeDefinition>> parseRoutingTypes(NodeList workflowAttributesChildren) {
        Map<String, List<WorkflowAttributeDefinition>> routingTypesMap = new HashMap<String, List<WorkflowAttributeDefinition>>();
        
        for (int i = 0; i < workflowAttributesChildren.getLength(); i++) {
            Node workflowAttributesChild = workflowAttributesChildren.item(i);
            if (workflowAttributesChild.getNodeType() == Node.ELEMENT_NODE) {
                if (((Element) workflowAttributesChild).getLocalName().equals("routingType")) {
                    Element routingTypeElement = (Element) workflowAttributesChild;
            
                    String name = routingTypeElement.getAttribute("name");
                    
                    List<WorkflowAttributeDefinition> workflowAttributeDefinitions = new ArrayList<WorkflowAttributeDefinition>();
                    List<Element> workflowAttributeList = extractWorkflowAttributeElements(routingTypeElement.getChildNodes());
                    for (int j = 0; j < workflowAttributeList.size(); j++) {
                        Element workflowAttributeDefinitionElement = (Element) workflowAttributeList.get(j);
                        WorkflowAttributeDefinition workflowAttributeDefinition = parseWorkflowAttributeDefinition(workflowAttributeDefinitionElement);
                        workflowAttributeDefinitions.add(workflowAttributeDefinition);
                    }
                    
                    routingTypesMap.put(name, workflowAttributeDefinitions);
                }
            }
        }
        
        return routingTypesMap;
    }
    
    protected WorkflowAttributeDefinition parseWorkflowAttributeDefinition(Element workflowAttributeDefinitionElement) {
        WorkflowAttributeDefinition workflowAttributeDefinition = new WorkflowAttributeDefinition();
        
        String referenceBusinessObjectClassName = workflowAttributeDefinitionElement.getAttribute("referenceBusinessObjectClassName");
        if (StringUtils.isNotBlank(referenceBusinessObjectClassName)) {
            try {
                workflowAttributeDefinition.setreferenceBusinessObjectClass(Class.forName(referenceBusinessObjectClassName));
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException("Unable to find class of name " + referenceBusinessObjectClassName + " when parsing workflowAttribute");
            }
        }
        
        String referenceBusinessObjectAttributeName = workflowAttributeDefinitionElement.getAttribute("referenceBusinessObjectAttributeName");
        if (StringUtils.isNotBlank(referenceBusinessObjectAttributeName)) {
            workflowAttributeDefinition.setReferenceBusinessObjectAttributeName(referenceBusinessObjectAttributeName);
        }
        
        workflowAttributeDefinition.setDocumentValuesPropertyPaths(parseDocumentValuesPropertyPaths(workflowAttributeDefinitionElement));
        
        return workflowAttributeDefinition;
    }
    
    protected List<String> parseDocumentValuesPropertyPaths(Element workflowAttributeDefinitionElement) {
        List<String> paths = new ArrayList<String>();
        
        NodeList children = workflowAttributeDefinitionElement.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (((Element) child).getLocalName().equals("path")) {
                    String path = ((Element) child).getAttribute("path");
                    if (StringUtils.isNotBlank(path)) {
                        paths.add(path);
                    }
                }
            }
        }
        return paths;
    }
    
    @Override
    protected String getBaseBeanTypeParent(Element element) {
        return "WorkflowAttributes";
    }

}
