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
package org.kuali.module.purap.rules;

import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.purap.fixtures.AmountsLimitsFixture;
import org.kuali.module.purap.rules.RequisitionDocumentRule;
import org.kuali.test.RequiresSpringContext;

@RequiresSpringContext(session = KHUNTLEY)
public class RequisitionDocumentRuleTest extends PurapRuleTestBase {
 
    RequisitionDocumentRule rule;
    RequisitionDocument req;
    
    protected void setUp() throws Exception {
        super.setUp();
        req = new RequisitionDocument();
        rule = new RequisitionDocumentRule();
    }
    
    protected void tearDown() throws Exception {
        rule = null;
        req = null;
        super.tearDown();      
    }

    public void testValidateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit_ZeroAmountSmallLimit() {
        req = AmountsLimitsFixture.ZERO_AMOUNT_SMALL_LIMIT.populateRequisition();
        assertTrue(rule.validateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit(req));
    }
    
    public void testValidateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit_SmallAmountSmallLimit() {
        req = AmountsLimitsFixture.SMALL_AMOUNT_SMALL_LIMIT.populateRequisition();
        assertTrue(rule.validateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit(req));
    }
    
    public void testValidateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit_LargeAmountSmallLimit() {
        req = AmountsLimitsFixture.LARGE_AMOUNT_SMALL_LIMIT.populateRequisition();
        assertFalse(rule.validateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit(req));
    }
}
