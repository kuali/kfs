/*
 * Copyright 2007 The Kuali Foundation.
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

import java.io.StringWriter;
import java.util.List;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routetemplate.xmlrouting.XPathHelper;
import edu.iu.uis.eden.util.Utilities;

/**
 * This class represents rule attributes that support kfs_*AccountingLineClass markers in the XPath
 * expressions they have.
 */
public class AccountLineReplacingXmlRuleAttribute extends KualiXmlRuleAttributeImpl {
    private static Log LOG = LogFactory.getLog(AccountLineReplacingXmlRuleAttribute.class);
    
    /**
     * This returns a DOM Element, which is the parent element for the XML in a route rule. This implementation
     * replaces all kfs_*AccountingLineClass markers in XPath expressions in the XML.
     * 
     * @see org.kuali.workflow.attribute.KualiXmlRuleAttributeImpl#getConfigXML()
     */
    public Element getConfigXML() {
        Element xmlRoot = super.getConfigXML();
        try {
            RouteContext routeContext = RouteContext.getCurrentRouteContext();
            DocumentRouteHeaderValue docHeader = routeContext.getDocument();
            if (docHeader != null && docHeader.getDocumentType() != null) {
                String docTypeName = docHeader.getDocumentType().getName();
                
                AccountingLineClassDeterminer accountingLineClassDeterminer = new AccountingLineClassDeterminer(docTypeName);
                NodeList xpathNodes = getXPathExpressionNodes(xmlRoot);
                for (int i = 0; i < xpathNodes.getLength(); i++) {
                    Node xpathExpressionNode = xpathNodes.item(i);
                    LOG.debug("original node: "+getStringFromElement(xpathExpressionNode));
                    // update the text in that element
                    updateNode(xpathExpressionNode, accountingLineClassDeterminer);
                }
                LOG.debug("resultant xmlRoot = "+getStringFromElement(xmlRoot));
            }
        }
        catch (TransformerException e) {
            LOG.error("Transformer Exception: "+e);
            throw new RuntimeException(e);
        }
        return xmlRoot;
    }
    
    /**
     * Writes an XML Element and all children as a String
     * @param rootElement the XML Element, which will serve as the root of what is being written to the String
     * @return the XML document with the given Element as a parent, written into a String
     * @throws TransformerException thrown if, for some unholy reason, the given XML element can't be turned into a String
     */
    private String getStringFromElement(Node rootElement) throws TransformerException {
           DOMSource domSource = new DOMSource(rootElement);
           StringWriter writer = new StringWriter();
           TransformerFactory.newInstance().newTransformer().transform(domSource, new StreamResult(writer));
           return writer.toString();
    }
    
    /**
     * Finds a list in the rule attribute XML of nodes that hold XPathExpressions, which must have any
     * kfs_*AccountingLineClass markers replaced
     * @param rootElement the root Element of the document to search through
     * @return a NodeList of XPathExpression nodes
     */
    private NodeList getXPathExpressionNodes(Element rootElement) {
        String findXpathExpression = "//fieldEvaluation/xpathexpression";
        NodeList xpathNodes = null;
        XPath xpath = XPathHelper.newXPath(rootElement);
        try {
            xpathNodes = (NodeList) xpath.evaluate(findXpathExpression, rootElement, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            LOG.error("error in isMatch ", e);
            throw new RuntimeException("Error trying to find xml content with xpath expressions: " + findXpathExpression, e);
        }
        return xpathNodes;
    }
    
    /**
     * This updates a node with an XPath expression in it to replace the kfs_*AccountingLineClass with the actual correct class names
     * @param node the XML node to update
     * @param accountingLineClassDeterminer  the class that figures out which child of AccountingLine is being used for this particular document
     */
    private void updateNode(Node node, AccountingLineClassDeterminer accountingLineClassDeterminer) {
        String extrapolatedXPathExpression = extrapolateInXPathExpression(node.getTextContent(), accountingLineClassDeterminer);
        LOG.debug("Extrapolated XPath Expression = "+extrapolatedXPathExpression);
        node.setTextContent(extrapolatedXPathExpression);
    }
    
    /**
     * This method "extrapolates" the kfs_*AccountingLineClass in XPath rules we find.
     * @param expression the XPath expression to fix
     * @param accountingLineClassDeterminer the class that figures out which child of AccountingLine is being used for this particular document
     * @return the extrapolated XPath expression.
     */
    private String extrapolateInXPathExpression(String expression, AccountingLineClassDeterminer accountingLineClassDeterminer) {
        expression = expression.replaceAll("kfs_sourceAccountingLineClass", accountingLineClassDeterminer.getSourceAccountingLineClassName());
        expression = expression.replaceAll("kfs_targetAccountingLineClass", accountingLineClassDeterminer.getTargetAccountingLineClassName());
        return expression;
    }
}
