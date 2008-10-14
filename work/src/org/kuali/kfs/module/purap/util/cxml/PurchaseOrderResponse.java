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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.module.purap.exception.CxmlParseError;
import org.w3c.dom.Node;

public class PurchaseOrderResponse extends CxmlParser {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderResponse.class);

  /**
   * @param cXml
   * @throws CxmlParseError
   */
  public PurchaseOrderResponse(String cXml) throws CxmlParseError {
    super(cXml);
  }

  /**
   * Get the status code
   */
  public String getStatusCode() {
    LOG.debug("getStatusCode() started");

    Node statusCodeNode = getXMLNode(document, "PurchaseOrder/ResponseMessage/Status/StatusCode");
    if (statusCodeNode == null) {
      LOG.debug("getStatusCode() statusCodeNode not found");
      return null;
    }
    String code = getNodeText(statusCodeNode);
    
    LOG.debug("getStatusCode() statusCode is " + code);
    return (code != null) ? code.trim() : null;
  }

  /**
   * Get the status text
   */
  public String getStatusText() {
    LOG.debug("getStatusText() started");

    Node statusTextNode = getXMLNode(document, "PurchaseOrderMessage/ResponseMessage/Status/StatusText");
    if (statusTextNode == null) {
      LOG.debug("getStatusText() statusTextNode not found");
      return null;
    }
    return getNodeText(statusTextNode);
  }

  public List getPOResponseErrorMessages() {
    LOG.debug("getPOResponseErrorMessages() started.");

    List errors = new ArrayList();

    if (! isSuccess()) {
      // Look for problems with the non-line items cXML (username, etc.).
      Node errorsNode = getXMLNode(document,"PurchaseOrder/ResponseMessage/Status/Errors");
      if (errorsNode == null) {
        LOG.debug("getPOResponseErrorMessages() errorsNode PurchaseOrder/ResponseMessage/Status/Errors not found");
        return null;
      }

      List nonLineRefErrorList = findNodes(errorsNode, Node.ELEMENT_NODE, "Error");
      for (Iterator iter = nonLineRefErrorList.iterator(); iter.hasNext();) {
        Node errorNode = (Node) iter.next();

        Node errorMessageNode = getXMLNode(errorNode, "ErrorMessage");
        if (errorMessageNode == null) {
          LOG.debug("getPOResponseErrorMessages() errorMessageNode not found");
          return null;
        }
        LOG.debug("getPOResponseErrorMessages(): Non line item error message is: " + getNodeText(errorMessageNode));
        
        errors.add(getNodeText(errorMessageNode));
      }
      
      // Look for problems with line items.
      errorsNode = getXMLNode(document,"PurchaseOrder/ResponseMessage/ObjectErrors");
      if (errorsNode == null) {
        LOG.debug("getPOResponseErrorMessages() errorsNode PurchaseOrder/ResponseMessage/ObjectErrors not found");
        return null;
      }
      List lineRefErrorList = findNodes(errorsNode, Node.ELEMENT_NODE, "PurchaseOrderLineRef");
      for (Iterator iter = lineRefErrorList.iterator(); iter.hasNext();) {
        Node lineRefNode = (Node) iter.next();

        String linenumber = getNodeAttribute(lineRefNode,"linenumber");getNodeText(lineRefNode);
        LOG.debug("getPOResponseErrorMessages(): line referenced is " + linenumber);
        
        Node errorNode = getXMLNode(lineRefNode, "Error");

        Node errorMessageNode = getXMLNode(errorNode, "ErrorMessage");
        if (errorMessageNode == null) {
          LOG.debug("getPOResponseErrorMessages() errorMessageNode not found");
          return null;
        }
        
        String errorMessage = "Line number " + linenumber + ": " + getNodeText(errorMessageNode);
        LOG.debug("errorMessage is " + errorMessage);
        
        errors.add(errorMessage);
      }
    } // if (! isSuccess())
    return errors;
  }

  public List getErrorMessages() {
    LOG.debug("getErrorMessages() started");

    List errors = new ArrayList();

    if (! isSuccess()) {
      Node errorPurchaseOrderRefNode = getXMLNode(document,
          "PurchaseOrder/ResponseMessage/ObjectErrors/PurchaseOrderRef");
      if (errorPurchaseOrderRefNode == null) {
        LOG.debug("getErrorMessages() errorPurchaseOrderRefNode not found");
        return null;
      }

      List errorNodesList = findNodes(errorPurchaseOrderRefNode, Node.ELEMENT_NODE, "Error");
      for (Iterator iter = errorNodesList.iterator(); iter.hasNext();) {
        Node errorNode = (Node) iter.next();

        Node errorMessageNode = getXMLNode(errorNode, "ErrorMessage");
        if (errorMessageNode == null) {
          LOG.debug("getErrorMessages() errorMessageNode not found");
          return null;
        }
        errors.add(getNodeText(errorMessageNode));
      }
    }
    return errors;
  }
}