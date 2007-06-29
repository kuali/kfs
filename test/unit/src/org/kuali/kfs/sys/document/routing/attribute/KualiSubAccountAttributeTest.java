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

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;
import org.kuali.workflow.KualiWorkflowUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.routeheader.DocumentContent;

@WithTestSpringContext
public class KualiSubAccountAttributeTest extends KualiTestBase {

    public void testGetSubAccountValuesFromDocContentData() throws IOException, InvalidXmlException, XPathExpressionException {
        String documentTypeName = "TransferOfFundsDocument";
        DocumentContent docContent = KualiAttributeTestUtil.getDocumentContentFromXmlFile(KualiAttributeTestUtil.TOF_SUB_ACCOUNT_TEST_DOC, documentTypeName);
        XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());
        KualiSubAccountAttribute subAccountAttribute = new KualiSubAccountAttribute();
        Set subAccountValues = subAccountAttribute.populateFromDocContent(documentTypeName, docContent, new RouteContext());
        
        assertFalse("At least one valid sub account should be returned",subAccountValues.isEmpty());
    }
    
    public void testGetNonReportDocumentContentValues() throws IOException, InvalidXmlException, XPathExpressionException {
        String docTypeName = "TransferOfFundsDocument";
        DocumentContent docContent = KualiAttributeTestUtil.getDocumentContentFromXmlFile(KualiAttributeTestUtil.TOF_SUB_ACCOUNT_TEST_DOC, docTypeName);
        String xpathExp = null;

        if (!KualiWorkflowUtils.isSourceLineOnly(docTypeName)) {
            // checking target lines
            xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getTargetAccountingLineClassName(docTypeName)).append("/subAccount").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
            checkValidValues(docContent, xpathExp,"BL","1031400",null,"DPD");
        }
        
        if (!KualiWorkflowUtils.isTargetLineOnly(docTypeName)) {
            // checking source lines
            xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getSourceAccountingLineClassName(docTypeName)).append("/subAccount").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
            checkValidValues(docContent, xpathExp,"BL","1031400",null,"BLDG");
        }
    }
    
    private void checkValidValues(DocumentContent docContent,String xpathExp,String chart,String account,String org,String subAccount) throws XPathExpressionException {
        XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());
        NodeList nodes = (NodeList) xpath.evaluate(xpathExp, docContent.getDocument(), XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node subAccountNode = nodes.item(i);
            String referenceString = xpath.evaluate("@reference", subAccountNode);
            if (!StringUtils.isEmpty(referenceString)) {
                subAccountNode = (Node) xpath.evaluate(referenceString, subAccountNode, XPathConstants.NODE);
            }
            // we check for blanks below because of the potential for account or org being null as a valid value
            String accountNbr = null;
            if (StringUtils.isNotBlank(account)) {
                accountNbr = xpath.evaluate(KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + "accountNumber", subAccountNode);
                assertEquals("Account number found was invalid", account, accountNbr);
            }
            if (StringUtils.isNotBlank(org)) {
                String orgCd = xpath.evaluate(KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + "finReportOrganizationCode", subAccountNode);
                assertEquals("Org code found was invalid", org, orgCd);
            }
            if (StringUtils.isNotBlank(chart)) {
                String fieldName = "chartOfAccountsCode";
                if (StringUtils.isBlank(accountNbr)) {
                    fieldName = "financialReportChartCode";
                }
                String finCoaCd = xpath.evaluate(KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + fieldName, subAccountNode);
                assertEquals("Chart code found was invalid", chart, finCoaCd);
            }
            if (StringUtils.isNotBlank(subAccount)) {
                String subAccountNbr = xpath.evaluate(KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + "subAccountNumber", subAccountNode);
                assertEquals("Sub account number found was invalid", subAccount, subAccountNbr);
            }
        }
    }
}
