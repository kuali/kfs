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
package org.kuali.kfs.module.purap.util.cxml;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.kuali.kfs.module.purap.exception.CxmlParseError;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Base class to read Cxml messages
 */
public abstract class CxmlParser {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CxmlParser.class);

  private String cXml;
  protected Document document;
  private static final String SUCCESS_STATUS_CODE = "200";
  
  public CxmlParser(String cXml) throws CxmlParseError {
    super();
    this.cXml = cXml;
    parse();
  }

  public CxmlParser(Document document) throws CxmlParseError {
    super();
    this.document = document;
  }

  public abstract String getStatusCode();
  public abstract String getStatusText();

  /**
   * A 200 status code means success.  Return true if 200.
   * 
   * @return
   */
  public boolean isSuccess() {
    return SUCCESS_STATUS_CODE.equals(getStatusCode());
  }

  /**
   * Return value of attribute of the name specified
   * 
   * @param node Node to search
   * @param name Name of attribute to look for
   * @return value of attribute or null if not found
   */
  protected String getNodeAttribute(Node node, String name) {
    NamedNodeMap nnm = node.getAttributes();

    for (int i = 0; i < nnm.getLength(); i++) {
      Node attr = nnm.item(i);
      if (attr.getNodeName().equalsIgnoreCase(name)) {
        return attr.getNodeValue();
      }
    }
    return null;
  }

  /**
   * Find a node an arbitrary depth into an XML document.
   * Everything in path must occur only once in that path of 
   * XML, or this will only return the first occurrence of it
   * 
   * @param startNode Top of document
   * @param path Path node names separated by /
   * @return
   */
  protected Node getXMLNode(Node startNode, String path) {
    String[] pathList = path.split("/");

    Node child = startNode;
    for (int i = 0; i < pathList.length; i++) {
      if ( child == null ) {
        return null;
      }
      child = findNode(child.getChildNodes(), Node.ELEMENT_NODE, pathList[i]);
    }
    return child;
  }

  /**
   * finds a node by the given name and of the given type, without searching
   * any deeper than the current level if no match is found, returns null
   * if more than one found, just return first one
   *
   * @param nodes list of nodes to search
   * @param type Type of node to search
   * @param name Name of node to search
   * @return The node found or null if not found
   */
  protected Node findNode(NodeList nodes, short type, String name) {
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      if (node.getNodeName().equalsIgnoreCase(name) && (node.getNodeType() == type)) {
        return node;
      }
    }
    return null;
  }

  /**
   * Get the text from a node
   * 
   * @param node Node to read
   * @return Text or null if no text found
   */
  protected String getNodeText(Node node) {
    String value = null;
    Node textNode = findNode(node.getChildNodes(), Node.TEXT_NODE, "#text");
    if (textNode != null) {
      return textNode.getNodeValue();
    } else {
      Node cdataNode = findNode(node.getChildNodes(), Node.CDATA_SECTION_NODE, "#cdata-section");
      if (cdataNode != null) {
        return cdataNode.getNodeValue();
      } else {
        return null;
      }
    }
  }

  private void parse() throws CxmlParseError {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      factory.setCoalescing(true);  //so it expands CDATA stuff

      document = builder.parse(new InputSource(new StringReader(cXml)));
    } catch (SAXParseException spe) {
      this.parseErrorOut(spe);
    } catch (SAXException sxe) {
      this.parseErrorOut(sxe);
    } catch (ParserConfigurationException pce) {
      this.parseErrorOut(pce);
    } catch (IOException ioe) {
      this.parseErrorOut(ioe);
    }
  }
  
  private void parseErrorOut(Exception e) {
    LOG.info("parse() CXML string is: " + cXml);
    LOG.error("parse() Error parsing XML", e);
    throw new CxmlParseError("Error parsing XML");
  }

  /**
   * Find all the nodes of a specific type with a specific name
   * that are direct children of a specified node
   * 
   * @param node parent node
   * @param type Type of child
   * @param name Name of children
   * @return List of nodes or empty list of none found
   */
  protected static List findNodes(Node node, short type, String name) {
    NodeList nodes = node.getChildNodes();
    
    List foundNodes = new ArrayList();
    for (int i = 0; i < nodes.getLength(); i++) {
      Node child = nodes.item(i);
      if (child.getNodeName().equalsIgnoreCase(name) && (child.getNodeType() == type)) {
        foundNodes.add(child);
      }
    }
    return foundNodes;
  }
}
