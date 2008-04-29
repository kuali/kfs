/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.workflow.attribute;

import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.iu.uis.eden.routetemplate.xmlrouting.StandardGenericXMLRuleAttribute;
import edu.iu.uis.eden.routetemplate.xmlrouting.XPathHelper;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.util.XmlHelper;

/**
 * This class extends the workflow xml rule attribute implementation to use the information in the data dictionary to generate
 * labels.
 */
public class MyXmlRuleAttributeImpl extends StandardGenericXMLRuleAttribute implements KualiXmlAttribute {
    private static final long serialVersionUID = -3453451186396963835L;
    
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MyXmlRuleAttributeImpl.class);

    private static final String FIELD_DEF_E = "fieldDef";


    /**
     * Constructs a KualiXmlRuleAttributeImpl.java.
     */
    public MyXmlRuleAttributeImpl() {
        super();
    }

    /**
     * This method overrides the super class and modifies the XML that it operates on to put the name and the title in the place
     * where the super class expects to see them, even though they may no longer exist in the original XML.
     * 
     * @see edu.iu.uis.eden.routetemplate.xmlrouting.StandardGenericXMLRuleAttribute#getConfigXML()
     */
    public Element getConfigXML() {
        Element root = getAttributeConfigXML();
        KualiXmlAttributeHelper attributeHelper = new KualiXmlAttributeHelper();
        // this adds the name and title to the xml based on the data dictionary
        return attributeHelper.processConfigXML(root);
    }

    public Element getAttributeConfigXML() {
        return super.getConfigXML();
    }

    public String getDocContent() {
        XPath xpath = XPathHelper.newXPath();
        final String findDocContent = "//routingConfig/xmlDocumentContent";
        try {
            Node xmlDocumentContent = (Node) xpath.evaluate(findDocContent, getConfigXML(), XPathConstants.NODE);

            NodeList nodes = getFields(xpath, getConfigXML(), new String[] { "ALL", "REPORT", "RULE" });
//            if (nodes == null || nodes.getLength() == 0) {
//                return "";
//            }

            if (xmlDocumentContent != null && xmlDocumentContent.hasChildNodes()) {
                // Custom doc content in the routingConfig xml.
                String documentContent = "";
                NodeList customNodes = xmlDocumentContent.getChildNodes();
                for (int i = 0; i < customNodes.getLength(); i++) {
                    Node childNode = customNodes.item(i);
                    documentContent += XmlHelper.writeNode(childNode);
                }

                for (int i = 0; i < nodes.getLength(); i++) {
                    Node field = nodes.item(i);
                    NamedNodeMap fieldAttributes = field.getAttributes();
                    String fieldName = fieldAttributes.getNamedItem("name").getNodeValue();
                    LOG.debug("Replacing field '" + fieldName + "'");
                    Map map = getParamMap();
                    String fieldValue = (String) map.get(fieldName);
                    if (map != null && !Utilities.isEmpty(fieldValue)) {
                        LOG.debug("Replacing %" + fieldName + "% with field value: '" + fieldValue + "'");
                        documentContent = documentContent.replaceAll("%" + fieldName + "%", fieldValue);
                    } else {
                        LOG.debug("Field map is null or fieldValue is empty");
                    }
                }
                return documentContent;
            } else {
                // Standard doc content if no doc content is found in the routingConfig xml.
                StringBuffer documentContent = new StringBuffer("<xmlRouting>");
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node field = nodes.item(i);
                    NamedNodeMap fieldAttributes = field.getAttributes();
                    String fieldName = fieldAttributes.getNamedItem("name").getNodeValue();
                    Map map = getParamMap();
                    if (map != null && !Utilities.isEmpty((String) map.get(fieldName))) {
                        documentContent.append("<field name=\"");
                        documentContent.append(fieldName);
                        documentContent.append("\"><value>");
                        documentContent.append((String) map.get(fieldName));
                        documentContent.append("</value></field>");
                    }
                }
                documentContent.append("</xmlRouting>");
                return documentContent.toString();
            }
        } catch (XPathExpressionException e) {
            LOG.error("error in getDocContent ", e);
            throw new RuntimeException("Error trying to find xml content with xpath expression", e);
        } catch (Exception e) {
            LOG.error("error in getDocContent attempting to find xml doc content", e);
            throw new RuntimeException("Error trying to get xml doc content.", e);
        }
    }
    
    private static NodeList getFields(XPath xpath, Element root, String[] types) throws XPathExpressionException {
        final String OR = " or ";
        StringBuffer findField = new StringBuffer("//routingConfig/" + FIELD_DEF_E);
        if (types != null && types.length > 0) {
            findField.append("[");
            for (int i = 0; i < types.length; i++) {
                findField.append("@workflowType='" + types[i] + "'" + OR);
                // missing workflowType is equivalent ("defaults") to ALL
                if ("ALL".equals(types[i])) {
                    findField.append("not(@workflowType)" + OR);
                }
            }
            if (types.length > 0) {
                // remove trailing " or "
                findField.setLength(findField.length() - OR.length());
            }
            findField.append("]");
        }

        try {
            return (NodeList) xpath.evaluate(findField.toString(), root, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            LOG.error("Error evaluating expression: '" + findField + "'");
            throw e;
        }
    }

}
