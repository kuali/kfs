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
import org.kuali.test.WithTestSpringContext;
import org.kuali.workflow.KualiWorkflowUtils;

import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.routeheader.DocumentContent;

/**
 * This class...
 * 
 * @author Kuali Nervous System Team ()
 */
@WithTestSpringContext
public class KualiAttributeXPathTest extends KualiTestBaseWithFixtures {

    private static final String KUALI_SUBFUND_GROUP_ATTRIBUTE = "//org.kuali.core.bo.SourceAccountingLine/account/subFundGroupCode or //org.kuali.core.bo.TargetAccountingLine/account/subFundGroupCode";
    private static final String KUALI_SUBFUND_GROUP_ATTRIBUTE_SOURCE = "//org.kuali.core.bo.SourceAccountingLine/account/subFundGroupCode";
    private static final String KUALI_SUBFUND_GROUP_ATTRIBUTE_TARGET = "//org.kuali.core.bo.TargetAccountingLine/account/subFundGroupCode";
    private static final String KUALI_SUBFUND_GROUP_ATTRIBUTE_XSTREAMSAFE = "wf:xstreamsafe('//org.kuali.core.bo.SourceAccountingLine/account/subFundGroupCode') or wf:xstreamsafe('//org.kuali.core.bo.TargetAccountingLine/account/subFundGroupCode')";
    private static final String KUALI_SUBFUND_GROUP_ATTRIBUTE_SOURCE_XSTREAMSAFE = "wf:xstreamsafe('//org.kuali.core.bo.SourceAccountingLine/account/subFundGroupCode')";
    private static final String KUALI_SUBFUND_GROUP_ATTRIBUTE_TARGET_XSTREAMSAFE = "wf:xstreamsafe('//org.kuali.core.bo.TargetAccountingLine/account/subFundGroupCode')";

    public void setUp() throws Exception {
        super.setUp();
    }

    public void testKualiSubFundGroupAttribute_TransferOfFunds1() throws IOException, InvalidXmlException, XPathExpressionException {

        DocumentContent docContent = KualiAttributeTestUtil.getDocumentContentFromXmlFile("TransferOfFunds_FEMPSubcode_OneLiner.xml", "KualiTransferOfFundsDocument");
        XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());

        String xpathResult;

        // make sure the OR statement works
        xpathResult = (String) xpath.evaluate(KUALI_SUBFUND_GROUP_ATTRIBUTE, docContent.getDocument(), XPathConstants.STRING);
        assertEquals("true", xpathResult);

        // make sure the source FEMP exists
        xpathResult = (String) xpath.evaluate(KUALI_SUBFUND_GROUP_ATTRIBUTE_SOURCE, docContent.getDocument(), XPathConstants.STRING);
        assertEquals("FEMP", xpathResult);

        // make sure the target FEMP exists
        xpathResult = (String) xpath.evaluate(KUALI_SUBFUND_GROUP_ATTRIBUTE_TARGET, docContent.getDocument(), XPathConstants.STRING);
        assertEquals("FEMP", xpathResult);

        // make sure the OR statement works with xstreamsafe
        xpathResult = (String) xpath.evaluate(KUALI_SUBFUND_GROUP_ATTRIBUTE_XSTREAMSAFE, docContent.getDocument(), XPathConstants.STRING);
        assertEquals("true", xpathResult);

        // make sure the source FEMP exists with xstreamsafe
        xpathResult = (String) xpath.evaluate(KUALI_SUBFUND_GROUP_ATTRIBUTE_SOURCE_XSTREAMSAFE, docContent.getDocument(), XPathConstants.STRING);
        assertEquals("FEMP", xpathResult);

        // make sure the target FEMP exists with xstreamsafe
        xpathResult = (String) xpath.evaluate(KUALI_SUBFUND_GROUP_ATTRIBUTE_TARGET_XSTREAMSAFE, docContent.getDocument(), XPathConstants.STRING);
        assertEquals("FEMP", xpathResult);

    }
}
