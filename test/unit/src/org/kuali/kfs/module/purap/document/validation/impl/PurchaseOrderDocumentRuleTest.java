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
import static org.kuali.test.fixtures.UserNameFixture.PARKE;

import java.util.ArrayList;
import java.util.List;

import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchaseOrderVendorStipulation;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.fixtures.AmountsLimitsFixture;
import org.kuali.module.purap.fixtures.ItemAccountsFixture;
import org.kuali.module.purap.fixtures.ItemTypesFixture;
import org.kuali.test.ConfigureContext;

@ConfigureContext(session = KHUNTLEY)
public class PurchaseOrderDocumentRuleTest extends PurapRuleTestBase {

    PurchaseOrderDocumentRule rule;
    PurchaseOrderDocument po;

    protected void setUp() throws Exception {
        super.setUp();
        po = new PurchaseOrderDocument();
        rule = new PurchaseOrderDocumentRule();
    }

    protected void tearDown() throws Exception {
        rule = null;
        po = null;
        super.tearDown();
    }

    /*
     * Tests of validateEmptyItemWithAccounts
     */
    @ConfigureContext(session = PARKE)
    public void testValidateEmptyItemWithAccounts_NullItemWithAccount() {
        PurchaseOrderItem poItem = ItemAccountsFixture.NULL_ITEM_WITH_ACCOUNT.populateItem();
        assertFalse(rule.validateEmptyItemWithAccounts(poItem, "Item " + poItem.getItemLineNumber().toString()));
    }

    @ConfigureContext(session = PARKE)
    public void testValidateEmptyItemWithAccounts_WithItemWithAccount() {
        PurchaseOrderItem poItem = ItemAccountsFixture.WITH_QUANTITY_WITH_PRICE_WITH_ACCOUNT.populateItem();
        assertTrue(rule.validateEmptyItemWithAccounts(poItem, "Item " + poItem.getItemLineNumber().toString()));
    }

    @ConfigureContext(session = PARKE)
    public void testValidateEmptyItemWithAccounts_WithItemWithoutAccount() {
        PurchaseOrderItem poItem = ItemAccountsFixture.WITH_QUANTITY_WITH_PRICE_NULL_ACCOUNT.populateItem();
        assertTrue(rule.validateEmptyItemWithAccounts(poItem, "Item " + poItem.getItemLineNumber().toString()));
    }

    /*
     * Tests of validateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit
     */
    public void testValidateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit_ZeroAmountSmallLimit() {
        po = AmountsLimitsFixture.ZERO_AMOUNT_SMALL_LIMIT.populatePurchaseOrder();
        assertTrue(rule.validateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit(po));
    }

    public void testValidateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit_SmallAmountSmallLimit() {
        po = AmountsLimitsFixture.SMALL_AMOUNT_SMALL_LIMIT.populatePurchaseOrder();
        assertTrue(rule.validateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit(po));
    }

    public void testValidateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit_LargeAmountSmallLimit() {
        po = AmountsLimitsFixture.LARGE_AMOUNT_SMALL_LIMIT.populatePurchaseOrder();
        assertFalse(rule.validateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit(po));
    }

    /*
     * Tests of validateTradeInAndDiscountCoexistence
     */
    public void testValidateTradeInAndDiscountCoexistence_WithTradeInWithDiscount() {
        po = ItemTypesFixture.WITH_TRADEIN_WITH_DISCOUNT.populate();
        assertFalse(rule.validateTradeInAndDiscountCoexistence(po));
    }

    public void testValidateTradeInAndDiscountCoexistence_WithTradeInWithMisc() {
        po = ItemTypesFixture.WITH_TRADEIN_WITH_MISC.populate();
        assertTrue(rule.validateTradeInAndDiscountCoexistence(po));
    }

    public void testValidateTradeInAndDiscountCoexistence_WithDiscountWithMisc() {
        po = ItemTypesFixture.WITH_MISC_WITH_DISCOUNT.populate();
        assertTrue(rule.validateTradeInAndDiscountCoexistence(po));
    }

    /*
     * Tests of processVendorStipulationValidation
     */
    public void testProcessVendorStipulationValidation_NotBlank() {
        po = new PurchaseOrderDocument();
        PurchaseOrderVendorStipulation stip = new PurchaseOrderVendorStipulation();
        stip.setVendorStipulationDescription("test");
        List<PurchaseOrderVendorStipulation> stipulations = new ArrayList();
        stipulations.add(stip);
        po.setPurchaseOrderVendorStipulations(stipulations);
        assertTrue(rule.processVendorStipulationValidation(po));
    }

    public void testProcessVendorStipulationValidation_Blank() {
        po = new PurchaseOrderDocument();
        PurchaseOrderVendorStipulation stip = new PurchaseOrderVendorStipulation();
        stip.setVendorStipulationDescription("");
        List<PurchaseOrderVendorStipulation> stipulations = new ArrayList();
        stipulations.add(stip);
        po.setPurchaseOrderVendorStipulations(stipulations);
        assertFalse(rule.processVendorStipulationValidation(po));
    }
}
