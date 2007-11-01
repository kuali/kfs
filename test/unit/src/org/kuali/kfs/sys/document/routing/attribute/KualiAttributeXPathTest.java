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

import java.io.IOException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.kuali.kfs.context.KualiTestBase;
import org.kuali.test.ConfigureContext;
import org.kuali.workflow.KualiWorkflowUtils;

import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.routeheader.DocumentContent;

/**
 * This class...
 */
@ConfigureContext
public class KualiAttributeXPathTest extends KualiTestBase {

    private static final String KUALI_SUBFUND_GROUP_ATTRIBUTE_SOURCE = "//org.kuali.kfs.bo.SourceAccountingLine/account/subFundGroupCode";
    private static final String KUALI_SUBFUND_GROUP_ATTRIBUTE_TARGET = "//org.kuali.kfs.bo.TargetAccountingLine/account/subFundGroupCode";
    private static final String KUALI_SUBFUND_GROUP_ATTRIBUTE = KUALI_SUBFUND_GROUP_ATTRIBUTE_SOURCE + " or " + KUALI_SUBFUND_GROUP_ATTRIBUTE_TARGET;
    private static final String KUALI_SUBFUND_GROUP_ATTRIBUTE_XSTREAMSAFE = "wf:xstreamsafe('" + KUALI_SUBFUND_GROUP_ATTRIBUTE_SOURCE + "') or wf:xstreamsafe('" + KUALI_SUBFUND_GROUP_ATTRIBUTE_TARGET + "')";
    private static final String KUALI_SUBFUND_GROUP_ATTRIBUTE_SOURCE_XSTREAMSAFE = "wf:xstreamsafe('//org.kuali.kfs.bo.SourceAccountingLine/account/subFundGroupCode')";
    private static final String KUALI_SUBFUND_GROUP_ATTRIBUTE_TARGET_XSTREAMSAFE = "wf:xstreamsafe('//org.kuali.kfs.bo.TargetAccountingLine/account/subFundGroupCode')";
    private static final String KUALI_CAMPUS_TYPE_ACTIVE_INDICATOR_XSTREAMSAFE = KualiWorkflowUtils.XSTREAM_SAFE_PREFIX + KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX + "campus/campusType/dataObjectMaintenanceCodeActiveIndicator" + KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX;
    private static final String KUALI_INITIATOR_UNIVERSAL_USER_STUDENT_INDICATOR_XSTREAMSAFE = KualiWorkflowUtils.XSTREAM_SAFE_PREFIX + KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX + "kualiTransactionalDocumentInformation/documentInitiator/universalUser/student" + KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX;

    public void testKualiSubFundGroupAttribute_TransferOfFunds1() throws IOException, InvalidXmlException, XPathExpressionException {

        DocumentContent docContent = KualiAttributeTestUtil.getDocumentContentFromXmlFile("TransferOfFunds_FEMPSubcode_OneLiner.xml", "TransferOfFundsDocument");
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

    public void testKualiIndicatorTranslationAttributeXPath() throws IOException, InvalidXmlException, XPathExpressionException {
        DocumentContent docContent = KualiAttributeTestUtil.getDocumentContentFromXmlFileAndPath(KualiAttributeTestUtil.PURCHASE_ORDER_DOCUMENT, KualiAttributeTestUtil.RELATIVE_PATH_IN_PROJECT_WORKFLOW, "PurchaseOrderDocument");
        XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());

        String valueForTrue = "Yes";
        String valueForFalse = "No";

        // test campus active indicator field translation to 'Yes'
        String xpathConditionStatement = KUALI_CAMPUS_TYPE_ACTIVE_INDICATOR_XSTREAMSAFE + " = 'true'";
        String xpathExpression = constructXpathExpression(valueForTrue, valueForFalse, xpathConditionStatement);
        String xpathResult = (String) xpath.evaluate(xpathExpression, docContent.getDocument(), XPathConstants.STRING);
        assertEquals("Using translated xpath expression '" + xpathExpression + "'", valueForTrue, xpathResult);

        // test user student indicator translation to 'No'
        xpathConditionStatement = KUALI_INITIATOR_UNIVERSAL_USER_STUDENT_INDICATOR_XSTREAMSAFE + " = 'true'";
        xpathExpression = constructXpathExpression(valueForTrue, valueForFalse, xpathConditionStatement);
        xpathResult = (String) xpath.evaluate(xpathExpression, docContent.getDocument(), XPathConstants.STRING);
        assertEquals("Using translated xpath expression '" + xpathExpression + "'", valueForFalse, xpathResult);

        // test filled in date field translates to 'Yes'
        String expression = KualiWorkflowUtils.XSTREAM_SAFE_PREFIX + KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX + "document/purchaseOrderCreateDate" + KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX;
        xpathConditionStatement = "boolean(" + expression + ") and not(" + expression + " = '')";
        xpathExpression = constructXpathExpression(valueForTrue, valueForFalse, xpathConditionStatement);
        xpathResult = (String) xpath.evaluate(xpathExpression, docContent.getDocument(), XPathConstants.STRING);
        assertEquals("Using translated xpath expression '" + xpathExpression + "'", valueForTrue, xpathResult);

        // test empty date field translates to 'No'
        expression = KualiWorkflowUtils.XSTREAM_SAFE_PREFIX + KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX + "document/oldPurchaseOrderCreateDate" + KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX;
        xpathConditionStatement = "boolean(" + expression + ") and not(" + expression + " = '')";
        xpathExpression = constructXpathExpression(valueForTrue, valueForFalse, xpathConditionStatement);
        xpathResult = (String) xpath.evaluate(xpathExpression, docContent.getDocument(), XPathConstants.STRING);
        assertEquals("Using translated xpath expression '" + xpathExpression + "'", valueForFalse, xpathResult);

        // test non-existant date field translates to 'No'
        expression = KualiWorkflowUtils.XSTREAM_SAFE_PREFIX + KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX + "document/newPurchaseOrderCreateDate" + KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX;
        xpathConditionStatement = "boolean(" + expression + ") and not(" + expression + " = '')";
        xpathExpression = constructXpathExpression(valueForTrue, valueForFalse, xpathConditionStatement);
        xpathResult = (String) xpath.evaluate(xpathExpression, docContent.getDocument(), XPathConstants.STRING);
        assertEquals("Using translated xpath expression '" + xpathExpression + "'", valueForFalse, xpathResult);
    }

    private String constructXpathExpression(String valueForTrue, String valueForFalse, String booleanXPathExpression) {
        String[] xpathElementsToInsert = new String[3];
        xpathElementsToInsert[0] = "concat( substring('" + valueForTrue + "', number(not(";
        xpathElementsToInsert[1] = "))*string-length('" + valueForTrue + "')+1), substring('" + valueForFalse + "', number(";
        xpathElementsToInsert[2] = ")*string-length('" + valueForFalse + "')+1))";

        StringBuffer returnableString = new StringBuffer();
        for (int i = 0; i < xpathElementsToInsert.length; i++) {
            String newXpathElement = xpathElementsToInsert[i];
            returnableString.append(newXpathElement);

            /*
             * Append the given xpath expression onto the end of the stringbuffer only in the following cases - if there is only one
             * element in the string array - if there is more than one element in the string array and if the current element is not
             * the last element
             */
            if (((i + 1) != xpathElementsToInsert.length) || (xpathElementsToInsert.length == 1)) {
                returnableString.append(booleanXPathExpression);
            }
        }
        return returnableString.toString();

    }

    public void testConcatFunctionWithNonExistantNode() throws IOException, InvalidXmlException, XPathExpressionException {
        DocumentContent docContent = KualiAttributeTestUtil.getDocumentContentFromXmlFileAndPath(KualiAttributeTestUtil.PURCHASE_ORDER_DOCUMENT, KualiAttributeTestUtil.RELATIVE_PATH_IN_PROJECT_WORKFLOW, "PurchaseOrderDocument");
        XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());

        String tempXpathNugget = KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX + "campus/campusType/dataObjectMaintenanceCodeActiveIndicator";
        String xpathExistingNodeStatement = "(" + KualiWorkflowUtils.XSTREAM_SAFE_PREFIX + tempXpathNugget + KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX + ")";
        String xpathNonExistingNodeStatement = "(" + KualiWorkflowUtils.XSTREAM_SAFE_PREFIX + tempXpathNugget + "/dummystuff" + KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX + ")";
        String xpathExpression = "concat(" + xpathExistingNodeStatement + ", " + xpathNonExistingNodeStatement + ")";
        String xpathResult = (String) xpath.evaluate(xpathExpression, docContent.getDocument(), XPathConstants.STRING);
        assertEquals("true", xpathResult);

        xpathExpression = "concat(" + xpathNonExistingNodeStatement + ", " + xpathExistingNodeStatement + ")";
        xpathResult = (String) xpath.evaluate(xpathExpression, docContent.getDocument(), XPathConstants.STRING);
        assertEquals("true", xpathResult);

        xpathExpression = "concat(" + xpathNonExistingNodeStatement + ", " + xpathNonExistingNodeStatement + ")";
        xpathResult = (String) xpath.evaluate(xpathExpression, docContent.getDocument(), XPathConstants.STRING);
        assertEquals("", xpathResult);
    }
}
