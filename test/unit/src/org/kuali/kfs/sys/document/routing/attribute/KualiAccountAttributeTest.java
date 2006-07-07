/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.workflow.attribute;

import java.io.IOException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.kuali.test.KualiTestBaseWithFixtures;
import org.kuali.workflow.KualiWorkflowUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.routeheader.DocumentContent;

/**
 * This class...
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class KualiAccountAttributeTest extends KualiTestBaseWithFixtures {

    KualiAccountAttribute attribute;

    /**
     * Constructs a KualiAccountAttributeTest.java.
     */
    public KualiAccountAttributeTest() {
        super();
    }

    public void setUp() throws Exception {
        super.setUp();
        attribute = new KualiAccountAttribute();
    }

    public void testGetFiscalOfficerCriteria_TOFOneLiner() throws IOException, InvalidXmlException, XPathExpressionException {

        DocumentContent docContent = KualiAttributeTestUtil.getDocumentContentFromXmlFile(KualiAttributeTestUtil.TOF_FEMP_SUBCODE_ONELINER, "KualiTransferOfFundsDocument");

        XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());
        NodeList sourceLineNodes = (NodeList) xpath.evaluate("wf:xstreamsafe('//org.kuali.core.bo.SourceAccountingLine')", docContent.getDocument(), XPathConstants.NODESET);

        String chart = "";
        String accountNumber = "";

        for (int i = 0; i < sourceLineNodes.getLength(); i++) {
            Node node = sourceLineNodes.item(i);

            chart = xpath.evaluate("./chartOfAccountsCode", node);
            assertEquals("BL", chart);
            accountNumber = xpath.evaluate("./accountNumber", node);
            assertEquals("2823205", accountNumber);
        }
    }

    public void testSearchableAccountAttributeXPathTest() throws IOException, InvalidXmlException, XPathExpressionException {
        
        final String xpathQuery = "wf:xstreamsafe('//org.kuali.core.bo.SourceAccountingLine/accountNumber') | wf:xstreamsafe('//org.kuali.core.bo.TargetAccountingLine/accountNumber')";
        
        DocumentContent docContent = KualiAttributeTestUtil.getDocumentContentFromXmlFile(KualiAttributeTestUtil.TOF_FEMP_SUBCODE_ONELINER, "KualiTransferOfFundsDocument");

        XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());
        NodeList sourceLineNodes = (NodeList) xpath.evaluate(xpathQuery, docContent.getDocument(), XPathConstants.NODESET);
        
        for (int i = 0; i < sourceLineNodes.getLength(); i++) {
            Node node = sourceLineNodes.item(i);
            System.err.println("[" + i + "] (" + node.getNodeType() + ") " + node.getNodeName() + " = " + node.getTextContent());
        }
    }
    
}
