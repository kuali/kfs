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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.DocumentCollectionPath;
import org.kuali.rice.krad.datadictionary.DocumentValuePathGroup;
import org.kuali.rice.krad.datadictionary.RoutingAttribute;
import org.kuali.rice.krad.datadictionary.RoutingTypeDefinition;
import org.kuali.rice.krad.datadictionary.SearchingAttribute;
import org.kuali.rice.krad.datadictionary.SearchingTypeDefinition;
import org.kuali.rice.krad.datadictionary.WorkflowAttributeMetadata;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WorkflowAttributesBeanDefinitionParser extends KualiBeanDefinitionParserBase {
    private static final String SEARCHING_ATTRIBUTE = "searchingAttribute";
    private static final String ROUTING_ATTRIBUTE = "routingAttribute";
    private static final String DOCUMENT_VALUE_ATTRIBUTE = "documentValue";
    private static final String SEARCHING_TYPES_ELEMENT = "searchingType";
    private static final String ROUTING_TYPES_ELEMENT = "routingType";

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder bean) {
        handleAbstractAttribute(element, bean);

        parseChildElements(element, bean);
    }

    protected void parseChildElements(Element workflowAttributeElement, BeanDefinitionBuilder bean) {
        NodeList children = workflowAttributeElement.getChildNodes();
        List<SearchingTypeDefinition> searchingAttributesMap = parseSearchableAttributes(children);
        bean.addPropertyValue("searchingTypeDefinitions", searchingAttributesMap);
        
        Map<String, RoutingTypeDefinition> routingTypesMap = parseRoutingTypes(children);
        bean.addPropertyValue("routingTypeDefinitions", routingTypesMap);
    }
    
    protected List<SearchingTypeDefinition> parseSearchableAttributes(NodeList workflowAttributesChildren) {
        List<SearchingTypeDefinition> searchingAttributesMap = new ArrayList<SearchingTypeDefinition>();
        for (int i = 0; i < workflowAttributesChildren.getLength(); i++) {
            Node workflowAttributesChild = workflowAttributesChildren.item(i);
            if (workflowAttributesChild.getNodeType() == Node.ELEMENT_NODE) {
                if (((Element) workflowAttributesChild).getLocalName().equals(SEARCHING_TYPES_ELEMENT)) {
                    
                    SearchingTypeDefinition searchingTypeDefinition = parseSearchingTypes(workflowAttributesChild.getChildNodes());
                    
                    searchingAttributesMap.add(searchingTypeDefinition);
                }
            }
        }
        return searchingAttributesMap;
    }
    
    protected List<Element> extractWorkflowAttributeElements(NodeList nodes, String attributeElementName) {
        List<Element> results = new ArrayList<Element>();
        
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (StringUtils.isEmpty(attributeElementName) || ((Element) node).getLocalName().equals(attributeElementName)) {
                    results.add((Element) node);
                }
            }
        }
        return results;
    }
    
    protected SearchingTypeDefinition parseSearchingTypes(NodeList workflowAttributesChildren) {
        SearchingTypeDefinition  searchingTypeDefinition  = new SearchingTypeDefinition();
        
        for (int i = 0; i < workflowAttributesChildren.getLength(); i++) {
            Node workflowAttributesChild = workflowAttributesChildren.item(i);
            if (workflowAttributesChild.getNodeType() == Node.ELEMENT_NODE) {
                if (((Element) workflowAttributesChild).getLocalName().equals(SEARCHING_ATTRIBUTE)){
                    searchingTypeDefinition.setSearchingAttribute((SearchingAttribute)parseAttributeDefinition((Element) workflowAttributesChild));
                }else if(((Element) workflowAttributesChild).getLocalName().equals(DOCUMENT_VALUE_ATTRIBUTE)){
                    if(searchingTypeDefinition.getDocumentValues() == null){
                        searchingTypeDefinition.setDocumentValues(new ArrayList<String>());
                    }
                    searchingTypeDefinition.getDocumentValues().addAll(parseDocumentValueAttributeDefinition((Element) workflowAttributesChild));
                }
            }
        }
        
        return searchingTypeDefinition;
    }
    protected WorkflowAttributeMetadata parseAttributeDefinition(Element workflowAttributeDefinitionElement) {
        WorkflowAttributeMetadata workflowAttributeMetadata = null;
        if(workflowAttributeDefinitionElement.getLocalName().equals(SEARCHING_ATTRIBUTE)){
            return parseSearchingAttribute(workflowAttributeDefinitionElement);
        }else if(workflowAttributeDefinitionElement.getLocalName().equals(ROUTING_ATTRIBUTE)){
            return parseRoutingAttribute(workflowAttributeDefinitionElement);
        }
        return workflowAttributeMetadata;
    }
    
    
    protected WorkflowAttributeMetadata parseSearchingAttribute(Element workflowAttributeDefinitionElement) {
        SearchingAttribute workflowAttributeMetadata = new SearchingAttribute();

        String businessObjectClassName = workflowAttributeDefinitionElement.getAttribute("businessObjectClassName");
        if (StringUtils.isNotBlank(businessObjectClassName)) {
            try {
                Class.forName(businessObjectClassName);
                workflowAttributeMetadata.setBusinessObjectClassName(businessObjectClassName);
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException("Unable to find class of name " + businessObjectClassName + " when parsing workflowAttribute");
            }
        }
        String attributeName = workflowAttributeDefinitionElement.getAttribute("attributeName");
        if (StringUtils.isNotBlank(attributeName)) {
            workflowAttributeMetadata.setAttributeName(attributeName);
        }
        
        return workflowAttributeMetadata;
    }
    
    protected WorkflowAttributeMetadata parseRoutingAttribute(Element workflowAttributeDefinitionElement) {
        RoutingAttribute workflowAttributeMetadata = new RoutingAttribute();

        String attributeName = workflowAttributeDefinitionElement.getAttribute("qualificationAttributeName");
        if (StringUtils.isNotBlank(attributeName)) {
            workflowAttributeMetadata.setQualificationAttributeName(attributeName);
        }
        
        return workflowAttributeMetadata;
    }
    
    protected List<String> parseDocumentValueAttributeDefinition(Element workflowAttributeDefinitionElement) {

        List<String>paths = new ArrayList<String>();
        paths.add(workflowAttributeDefinitionElement.getAttribute("path"));
       
        return paths;
    }
    protected Map<String, RoutingTypeDefinition> parseRoutingTypes(NodeList workflowAttributesChildren) {
        Map<String, RoutingTypeDefinition> routingTypesMap = new HashMap<String, RoutingTypeDefinition>();
        
        for (int i = 0; i < workflowAttributesChildren.getLength(); i++) {
            Node workflowAttributesChild = workflowAttributesChildren.item(i);
            if (workflowAttributesChild.getNodeType() == Node.ELEMENT_NODE) {
                if (((Element) workflowAttributesChild).getLocalName().equals(ROUTING_TYPES_ELEMENT)) {
                    RoutingTypeDefinition routingTypeDefinition = new RoutingTypeDefinition();
                    Element routingTypeElement = (Element) workflowAttributesChild;
                    
                    String name = routingTypeElement.getAttribute("nodeName");
                    
                    List<RoutingAttribute> routingAttributes = new ArrayList<RoutingAttribute>();
                    List<DocumentValuePathGroup> documentValuePathGroups = new ArrayList<DocumentValuePathGroup>();
                    List<Element> workflowAttributeList = extractWorkflowAttributeElements(routingTypeElement.getChildNodes(), "");
                    for (int j = 0; j < workflowAttributeList.size(); j++) {
                        Element workflowAttributeDefinitionElement = (Element) workflowAttributeList.get(j);
                        if(workflowAttributeDefinitionElement.getLocalName().equals("routingAttributes")){
                            List<Element> routingAttributeList = extractWorkflowAttributeElements(workflowAttributeDefinitionElement.getChildNodes(), "routingAttribute");
                            for(Element routingAttribute:routingAttributeList){
                                routingAttributes.add((RoutingAttribute)parseAttributeDefinition(routingAttribute));
                            }
                        }
                        else if(workflowAttributeDefinitionElement.getLocalName().equals("documentValuePathGroup")){
                            documentValuePathGroups.add(parseDocumentValuesPathGroup(workflowAttributeDefinitionElement));
                        }
                    }
                    routingTypeDefinition.setDocumentValuePathGroups(documentValuePathGroups);
                    routingTypeDefinition.setRoutingAttributes(routingAttributes);
                    routingTypesMap.put(name, routingTypeDefinition);
                }
            }
        }
        
        return routingTypesMap;
    }
    
    protected DocumentCollectionPath parseDocumentCollectionPath(Element workflowAttributeDefinitionElement) {
        DocumentCollectionPath documentCollectionPath = new DocumentCollectionPath();
        for(Element element:extractWorkflowAttributeElements(workflowAttributeDefinitionElement.getChildNodes(), "documentCollectionPath")){
            documentCollectionPath.setNestedCollection(parseDocumentCollectionPath(element));
        }
        List<String>paths = new ArrayList<String>();
        for(Element element:extractWorkflowAttributeElements(workflowAttributeDefinitionElement.getChildNodes(), "documentValue")){
            paths.addAll(parseDocumentValueAttributeDefinition(element));
        }
        documentCollectionPath.setDocumentValues(paths);
        
        String collectionName = workflowAttributeDefinitionElement.getAttribute("path");
        documentCollectionPath.setCollectionPath(collectionName);
        
        return documentCollectionPath;
    }
    protected DocumentValuePathGroup parseDocumentValuesPathGroup(Element workflowAttributeDefinitionElement) {
        DocumentValuePathGroup documentValuePathGroup = new DocumentValuePathGroup();
        
        List<Element> documentCollectionPathElements = extractWorkflowAttributeElements(workflowAttributeDefinitionElement.getChildNodes(), "documentCollectionPath");
        if( documentCollectionPathElements.size() > 0){
            documentValuePathGroup.setDocumentCollectionPath(parseDocumentCollectionPath(documentCollectionPathElements.get(0)));
        }
        List<String>paths = new ArrayList<String>();
        for(Element element:extractWorkflowAttributeElements(workflowAttributeDefinitionElement.getChildNodes(), "documentValue")){
            paths.addAll(parseDocumentValueAttributeDefinition(element));
        }
        documentValuePathGroup.setDocumentValues(paths);

        return documentValuePathGroup;
    }
    
    @Override
    protected String getBaseBeanTypeParent(Element element) {
        return "WorkflowAttributes";
    }

}
