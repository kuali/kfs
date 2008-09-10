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

import org.kuali.kfs.module.purap.exception.CxmlParseError;
import org.w3c.dom.Node;

public class PunchOutSetupResponse extends CxmlParser {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PunchOutSetupResponse.class);
  
  private String punchOutUrl = null;

  /**
   * @param cXml
   * @throws CxmlParseError
   */
  public PunchOutSetupResponse(String cXml) {
    super(cXml);

    LOG.debug("PunchOutSetupResponse() started");

    if ( isSuccess() ) {
      parsePunchOutUrl();
    }
  }
  
  public String getPunchOutUrl() {
    return punchOutUrl;
  }

  private void parsePunchOutUrl() {
    LOG.debug("parsePunchOutUrl() started");

    Node urlNode = getXMLNode(document, "cXML/Response/PunchOutSetupResponse/StartPage/URL");
    if (urlNode == null) {
      LOG.error("parse() URL node not found");
      throw new CxmlParseError("Unable to find URL node");
    }

    punchOutUrl = getNodeText(urlNode);
  }

  /**
   * Get the status node out of the XML document
   */
  private Node getStatusNode() {
    LOG.debug("getStatusNode() started");

    return getXMLNode(document, "cXML/Response/Status");
  }


  public String getStatusCode() {
    LOG.debug("getStatusCode() started");

    Node statusNode = getStatusNode();
    if (statusNode == null) {
      LOG.error("getStatusCode() Status node not found");
      return null;
    }

    String code = getNodeAttribute(statusNode,"code");
    return (code != null) ? code.trim() : null;
  }

  public String getStatusText() {
    LOG.debug("getStatusText() started");

    Node statusNode = getStatusNode();
    if (statusNode == null) {
      LOG.error("getStatusText() Status node not found");
      return null;
    }

    return getNodeAttribute(statusNode,"text");
  }
}
