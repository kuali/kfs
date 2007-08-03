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
package org.kuali.workflow;

import java.io.IOException;

import javax.xml.xpath.XPathExpressionException;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.test.RequiresSpringContext;
import org.kuali.workflow.attribute.KualiAttributeTestUtil;

import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.routeheader.DocumentContent;

/**
 * This class...
 * 
 * 
 */
@RequiresSpringContext
public class KualiWorkflowUtilsTest extends KualiTestBase {
    
    public void testGetFinancialDocumentTotalAmount() throws Exception {
        KualiDecimal validAmount = new KualiDecimal(3.00);
        DocumentContent docContent = KualiAttributeTestUtil.getDocumentContentFromXmlFileAndPath(KualiAttributeTestUtil.PURCHASE_ORDER_DOCUMENT, KualiAttributeTestUtil.RELATIVE_PATH_IN_PROJECT_WORKFLOW,"PurchaseOrderDocument");

        KualiDecimal testAmount = KualiWorkflowUtils.getFinancialDocumentTotalAmount(docContent.getDocument());
        assertEquals(validAmount, testAmount);
        
        testAmount = KualiWorkflowUtils.getFinancialDocumentTotalAmount(docContent.getRouteContext());
        assertEquals(validAmount, testAmount);
    }

}
