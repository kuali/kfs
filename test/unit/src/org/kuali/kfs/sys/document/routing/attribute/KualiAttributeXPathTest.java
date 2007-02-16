/*
 * Copyright 2006 The Kuali Foundation.
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

import java.io.IOException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;
import org.kuali.workflow.KualiWorkflowUtils;

import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.routeheader.DocumentContent;

/**
 * This class...
 * 
 * 
 */
@WithTestSpringContext
public class KualiAttributeXPathTest extends KualiTestBase {

    private static final String KUALI_SUBFUND_GROUP_ATTRIBUTE_SOURCE = "//org.kuali.core.bo.SourceAccountingLine/account/subFundGroupCode";
    private static final String KUALI_SUBFUND_GROUP_ATTRIBUTE_TARGET = "//org.kuali.core.bo.TargetAccountingLine/account/subFundGroupCode";
    private static final String KUALI_SUBFUND_GROUP_ATTRIBUTE = KUALI_SUBFUND_GROUP_ATTRIBUTE_SOURCE + " or " + KUALI_SUBFUND_GROUP_ATTRIBUTE_TARGET;
    private static final String KUALI_SUBFUND_GROUP_ATTRIBUTE_XSTREAMSAFE = "wf:xstreamsafe('" + KUALI_SUBFUND_GROUP_ATTRIBUTE_SOURCE + "') or wf:xstreamsafe('" + KUALI_SUBFUND_GROUP_ATTRIBUTE_TARGET +"')";
    private static final String KUALI_SUBFUND_GROUP_ATTRIBUTE_SOURCE_XSTREAMSAFE = "wf:xstreamsafe('//org.kuali.core.bo.SourceAccountingLine/account/subFundGroupCode')";
    private static final String KUALI_SUBFUND_GROUP_ATTRIBUTE_TARGET_XSTREAMSAFE = "wf:xstreamsafe('//org.kuali.core.bo.TargetAccountingLine/account/subFundGroupCode')";

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
