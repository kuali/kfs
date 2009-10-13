/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorStipulation;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.validation.PurapRuleTestBase;
import org.kuali.kfs.module.purap.document.validation.event.AttributedAddPurchasingAccountsPayableItemEvent;
import org.kuali.kfs.module.purap.fixture.AmountsLimitsFixture;
import org.kuali.kfs.module.purap.fixture.ItemAccountsFixture;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.kfs.sys.fixture.UserNameFixture;

@ConfigureContext(session = UserNameFixture.parke)
public class PurchaseOrderDocumentRuleTest extends PurapRuleTestBase {

    private Map<String, GenericValidation> validations;
    PurchaseOrderDocument po;

    protected void setUp() throws Exception {
        super.setUp();
        po = new PurchaseOrderDocument();
        validations = SpringContext.getBeansOfType(GenericValidation.class);
    }

    protected void tearDown() throws Exception {
        validations = null;
        po = null;
        super.tearDown();
    }

    /*
     * Tests of validateEmptyItemWithAccounts
     */
    @ConfigureContext(session = parke)
    public void testValidateEmptyItemWithAccounts_NullItemWithAccount() {
        PurchaseOrderItem poItem = ItemAccountsFixture.NULL_ITEM_WITH_ACCOUNT.populateItem();
        
        PurchaseOrderEmptyItemWithAccountsValidation validation = (PurchaseOrderEmptyItemWithAccountsValidation)validations.get("PurchaseOrder-emptyItemWithAccountsValidation");
        validation.setItemForValidation(poItem);
        assertFalse( validation.validate(new AttributedAddPurchasingAccountsPayableItemEvent("", po, poItem)) );
    }

    @ConfigureContext(session = parke)
    public void testValidateEmptyItemWithAccounts_WithItemWithAccount() {
        PurchaseOrderItem poItem = ItemAccountsFixture.WITH_QUANTITY_WITH_PRICE_WITH_ACCOUNT.populateItem();

        PurchaseOrderEmptyItemWithAccountsValidation validation = (PurchaseOrderEmptyItemWithAccountsValidation)validations.get("PurchaseOrder-emptyItemWithAccountsValidation");
        validation.setItemForValidation(poItem);
        assertTrue( validation.validate(new AttributedAddPurchasingAccountsPayableItemEvent("", po, poItem)) );
    }

    @ConfigureContext(session = parke)
    public void testValidateEmptyItemWithAccounts_WithItemWithoutAccount() {
        PurchaseOrderItem poItem = ItemAccountsFixture.WITH_QUANTITY_WITH_PRICE_NULL_ACCOUNT.populateItem();

        PurchaseOrderEmptyItemWithAccountsValidation validation = (PurchaseOrderEmptyItemWithAccountsValidation)validations.get("PurchaseOrder-emptyItemWithAccountsValidation");
        validation.setItemForValidation(poItem);
        assertTrue( validation.validate(new AttributedAddPurchasingAccountsPayableItemEvent("", po, poItem)) );
    }

    /*
     * Tests of validateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit
     */
    public void testValidateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit_ZeroAmountSmallLimit() {
        po = AmountsLimitsFixture.ZERO_AMOUNT_SMALL_LIMIT.populatePurchaseOrder();
        PurchaseOrderDocumentPreRules preRule = new PurchaseOrderDocumentPreRules();        
        assertTrue(preRule.validateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit(po));
    }

    public void testValidateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit_SmallAmountSmallLimit() {
        po = AmountsLimitsFixture.SMALL_AMOUNT_SMALL_LIMIT.populatePurchaseOrder();
        PurchaseOrderDocumentPreRules preRule = new PurchaseOrderDocumentPreRules();
        assertTrue(preRule.validateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit(po));
    }

    public void testValidateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit_LargeAmountSmallLimit() {
        po = AmountsLimitsFixture.LARGE_AMOUNT_SMALL_LIMIT.populatePurchaseOrder();
        PurchaseOrderDocumentPreRules preRule = new PurchaseOrderDocumentPreRules();
        assertFalse(preRule.validateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit(po));
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

        PurchaseOrderProcessVendorStipulationValidation validation = (PurchaseOrderProcessVendorStipulationValidation)validations.get("PurchaseOrder-processVendorStipulationValidation-test");
        assertTrue( validation.validate(new AttributedDocumentEventBase("", "", po)) );
    }

    public void testProcessVendorStipulationValidation_Blank() {
        po = new PurchaseOrderDocument();
        PurchaseOrderVendorStipulation stip = new PurchaseOrderVendorStipulation();
        stip.setVendorStipulationDescription("");
        List<PurchaseOrderVendorStipulation> stipulations = new ArrayList();
        stipulations.add(stip);
        po.setPurchaseOrderVendorStipulations(stipulations);

        PurchaseOrderProcessVendorStipulationValidation validation = (PurchaseOrderProcessVendorStipulationValidation)validations.get("PurchaseOrder-processVendorStipulationValidation-test");
        assertFalse( validation.validate(new AttributedDocumentEventBase("", "", po)) );
    }
    
    /*
     * Tests of validateSplit
     */
    public void testValidateSplit_OneMovingOneRemaining() {
        po = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS_MULTI_ITEMS.createPurchaseOrderDocument();
        ((PurchaseOrderItem)po.getItems().get(0)).setMovingToSplit(true);
               
        PurchaseOrderSplitValidation validation = (PurchaseOrderSplitValidation)validations.get("PurchaseOrder-splitValidation-test");
        assertTrue( validation.validate(new AttributedDocumentEventBase("", "", po)) );        
    }
    
    public void testValidateSplit_NoneMovingTwoRemaining() {
        po = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS_MULTI_ITEMS.createPurchaseOrderDocument();

        PurchaseOrderSplitValidation validation = (PurchaseOrderSplitValidation)validations.get("PurchaseOrder-splitValidation-test");
        assertFalse( validation.validate(new AttributedDocumentEventBase("", "", po)) );        
    }
    
    public void testValidateSplit_TwoMovingNoneRemaining() {
        po = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS_MULTI_ITEMS.createPurchaseOrderDocument();
        ((PurchaseOrderItem)po.getItems().get(0)).setMovingToSplit(true);
        ((PurchaseOrderItem)po.getItems().get(1)).setMovingToSplit(true);

        PurchaseOrderSplitValidation validation = (PurchaseOrderSplitValidation)validations.get("PurchaseOrder-splitValidation-test");
        assertFalse( validation.validate(new AttributedDocumentEventBase("", "", po)) );        
    }
}

