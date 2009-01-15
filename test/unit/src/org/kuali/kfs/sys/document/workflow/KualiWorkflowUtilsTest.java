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
package org.kuali.kfs.sys.document.workflow;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.document.routing.attribute.KualiAttributeTestUtil;
import org.kuali.rice.kew.routeheader.DocumentContent;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * This class...
 */
@ConfigureContext
public class KualiWorkflowUtilsTest extends KualiTestBase {

    public void testGetFinancialDocumentTotalAmount() throws Exception {
        KualiDecimal validAmount = new KualiDecimal(3.00);
        DocumentContent docContent = KualiAttributeTestUtil.getDocumentContentFromXmlFile(KualiAttributeTestUtil.PURCHASE_ORDER_DOCUMENT, "PurchaseOrderDocument");

        KualiDecimal testAmount = KualiWorkflowUtils.getFinancialDocumentTotalAmount(docContent.getDocument());
        assertEquals(validAmount, testAmount);

        testAmount = KualiWorkflowUtils.getFinancialDocumentTotalAmount(docContent.getRouteContext());
        assertEquals(validAmount, testAmount);
    }

}
