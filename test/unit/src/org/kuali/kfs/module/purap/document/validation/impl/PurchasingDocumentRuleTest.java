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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapRuleConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionAccount;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.PurchasingService;
import org.kuali.kfs.module.purap.document.validation.PurapRuleTestBase;
import org.kuali.kfs.module.purap.fixture.ItemFieldsFixture;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderDocumentFixture;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderDocumentWithCommodityCodeFixture;
import org.kuali.kfs.module.purap.fixture.RecurringPaymentBeginEndDatesFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentWithCapitalAssetItemsFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentWithCommodityCodeFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.document.validation.Validation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.kfs.sys.document.validation.impl.CompositeValidation;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.util.MessageList;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class contains tests of the rule validation methods present in PurchasingDocumentRuleBase. These should include any tests
 * that test functionality that is common to all Purchasing documents.
 */
@ConfigureContext(session = UserNameFixture.parke)
public class PurchasingDocumentRuleTest extends PurapRuleTestBase {

    private Map<String, Validation> validations;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        KNSGlobalVariables.setMessageList(new MessageList());
        validations = SpringContext.getBeansOfType(Validation.class);
    }

    @Override
    protected void tearDown() throws Exception {
        validations = null;
        super.tearDown();
    }

    /**
     * These methods test how the method validating the input to the Payment Info tab on Purchasing documents,
     * PurchasingDocumentRuleBase.processPaymentInfoValidation, works for Requisitions and POs with different combinations of
     * beginning and ending dates, fiscal years, and recurring payment types.
     */
    public void testProcessPaymentInfoValidation_Req_RightOrder() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.REQ_RIGHT_ORDER.populateDocument();

        PurchasingPaymentInfoValidation validation = (PurchasingPaymentInfoValidation)validations.get("Purchasing-paymentInfoValidation-test");
        assertTrue( validation.validate(new AttributedDocumentEventBase("", "", document)) );
    }

    public void testProcessPaymentInfoValidation_Req_WrongOrder() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.REQ_WRONG_ORDER.populateDocument();

        PurchasingPaymentInfoValidation validation = (PurchasingPaymentInfoValidation)validations.get("Purchasing-paymentInfoValidation-test");
        assertFalse( validation.validate(new AttributedDocumentEventBase("", "", document)) );
    }

    public void testProcessPaymentInfoValidation_Req_Sequential_Next_FY() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.REQ_SEQUENTIAL_NEXT_FY.populateDocument();

        PurchasingPaymentInfoValidation validation = (PurchasingPaymentInfoValidation)validations.get("Purchasing-paymentInfoValidation-test");
        assertTrue( validation.validate(new AttributedDocumentEventBase("", "", document)) );
    }

    public void testProcessPaymentInfoValidation_Req_Non_Sequential_Next_FY() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.REQ_NON_SEQUENTIAL_NEXT_FY.populateDocument();

        PurchasingPaymentInfoValidation validation = (PurchasingPaymentInfoValidation)validations.get("Purchasing-paymentInfoValidation-test");
        assertFalse( validation.validate(new AttributedDocumentEventBase("", "", document)) );
    }

    public void testProcessPaymentInfoValidation_PO_RightOrder() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.PO_RIGHT_ORDER.populateDocument();

        PurchasingPaymentInfoValidation validation = (PurchasingPaymentInfoValidation)validations.get("Purchasing-paymentInfoValidation-test");
        assertTrue( validation.validate(new AttributedDocumentEventBase("", "", document)) );
    }

    public void testProcessPaymentInfoValidation_PO_WrongOrder() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.PO_WRONG_ORDER.populateDocument();

        PurchasingPaymentInfoValidation validation = (PurchasingPaymentInfoValidation)validations.get("Purchasing-paymentInfoValidation-test");
        assertFalse( validation.validate(new AttributedDocumentEventBase("", "", document)) );
    }

    public void testProcessPaymentInfoValidation_PO_Sequential_Next_FY() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.PO_SEQUENTIAL_NEXT_FY.populateDocument();

        PurchasingPaymentInfoValidation validation = (PurchasingPaymentInfoValidation)validations.get("Purchasing-paymentInfoValidation-test");
        assertTrue( validation.validate(new AttributedDocumentEventBase("", "", document)) );
    }

    public void testProcessPaymentInfoValidation_PO_Non_Sequential_Next_FY() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.PO_NON_SEQUENTIAL_NEXT_FY.populateDocument();

        PurchasingPaymentInfoValidation validation = (PurchasingPaymentInfoValidation)validations.get("Purchasing-paymentInfoValidation-test");
        assertFalse( validation.validate(new AttributedDocumentEventBase("", "", document)) );
    }

    // Tests of validateItemQuantity

    public void testValidateItemQuantity_WithQuantity_QuantityBased() {
        RequisitionItem item = ItemFieldsFixture.ALL_FIELDS_ABOVE_QUANTITY_BASED.populateRequisitionItem();

        PurchasingItemQuantityValidation validation = (PurchasingItemQuantityValidation)validations.get("Purchasing-itemQuantityValidation");
        validation.setItemForValidation(item);
        assertTrue( validation.validate(null) );
    }

    public void testValidateItemQuantity_WithoutQuantity_QuantityBased() {
        RequisitionItem item = ItemFieldsFixture.NO_QUANTITY_ABOVE_QUANTITY_BASED.populateRequisitionItem();

        PurchasingItemQuantityValidation validation = (PurchasingItemQuantityValidation)validations.get("Purchasing-itemQuantityValidation");
        validation.setItemForValidation(item);
        assertFalse( validation.validate(null) );
    }

    public void testValidateItemQuantity_WithQuantity_Service() {
        RequisitionItem item = ItemFieldsFixture.ALL_FIELDS_ABOVE_SERVICE.populateRequisitionItem();

        PurchasingItemQuantityValidation validation = (PurchasingItemQuantityValidation)validations.get("Purchasing-itemQuantityValidation");
        validation.setItemForValidation(item);
        assertFalse( validation.validate(null) );
    }

    public void testValidateItemQuantity_WithoutQuantity_Service() {
        RequisitionItem item = ItemFieldsFixture.NO_QUANTITY_ABOVE_SERVICE.populateRequisitionItem();

        PurchasingItemQuantityValidation validation = (PurchasingItemQuantityValidation)validations.get("Purchasing-itemQuantityValidation");
        validation.setItemForValidation(item);
        assertTrue( validation.validate(null) );
    }

    // Tests of validateUnitOfMeasure

    public void testValidateUnitOfMeasure_WithUOM_QuantityBased() {
        RequisitionItem item = ItemFieldsFixture.ALL_FIELDS_ABOVE_QUANTITY_BASED.populateRequisitionItem();

        PurchasingUnitOfMeasureValidation validation = (PurchasingUnitOfMeasureValidation)validations.get("Purchasing-unitOfMeasureValidation");
        validation.setItemForValidation(item);
        assertTrue( validation.validate(null) );
    }

    public void testValidateUnitOfMeasure_WithoutUOM_QuantityBased() {
        RequisitionItem item = ItemFieldsFixture.NO_UOM_ABOVE_QUANTITY_BASED.populateRequisitionItem();

        PurchasingUnitOfMeasureValidation validation = (PurchasingUnitOfMeasureValidation)validations.get("Purchasing-unitOfMeasureValidation");
        validation.setItemForValidation(item);
        assertFalse( validation.validate(null) );
    }

    public void testValidateUnitOfMeasure_WithoutUOM_Service() {
        RequisitionItem item = ItemFieldsFixture.NO_UOM_ABOVE_SERVICE.populateRequisitionItem();

        PurchasingUnitOfMeasureValidation validation = (PurchasingUnitOfMeasureValidation)validations.get("Purchasing-unitOfMeasureValidation");
        validation.setItemForValidation(item);
        assertTrue( validation.validate(null) );
    }

    // Tests of validateItemDescription

    public void testValidateItemDescription_WithDescription_Above() {
        RequisitionItem item = ItemFieldsFixture.ALL_FIELDS_ABOVE_QUANTITY_BASED.populateRequisitionItem();

        PurchasingItemDescriptionValidation validation = (PurchasingItemDescriptionValidation)validations.get("Purchasing-itemDescriptionValidation");
        validation.setItemForValidation(item);
        assertTrue( validation.validate(null) );
    }

    public void testValidateItemDescription_WithoutDescription_Above() {
        RequisitionItem item = ItemFieldsFixture.NO_DESC_ABOVE_QUANTITY_BASED.populateRequisitionItem();

        PurchasingItemDescriptionValidation validation = (PurchasingItemDescriptionValidation)validations.get("Purchasing-itemDescriptionValidation");
        validation.setItemForValidation(item);
        assertFalse( validation.validate(null) );
    }

    // Tests of validateCommodityCodes.

    /**
     * Tests that, if a commodity code is not entered on the item, but the system parameter
     * requires the item to have commodity code, it will give validation error about
     * the commodity code is required.
     *
     * @throws Exception
     */
    public void testMissingCommodityCodeWhenRequired() throws Exception {
        TestUtils.setSystemParameter(RequisitionDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
        RequisitionDocumentFixture reqFixture = RequisitionDocumentFixture.REQ_NO_APO_VALID;

        CompositeValidation validation = (CompositeValidation)validations.get("Requisition-newProcessItemValidation");
        RequisitionDocument req = reqFixture.createRequisitionDocument();
        AttributedDocumentEventBase event = new AttributedDocumentEventBase("","", req);

        for(RequisitionItem item : (List<RequisitionItem>)req.getItems()) {
            event.setIterationSubject(item);
            validation.validate(event);
        }
        assertTrue(GlobalVariables.getMessageMap().containsMessageKey(KFSKeyConstants.ERROR_REQUIRED));

        String fieldName = KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM + "[0]." + PurapPropertyConstants.ITEM_COMMODITY_CODE;
        assertTrue(GlobalVariables.getMessageMap().fieldHasMessage(fieldName, KFSKeyConstants.ERROR_REQUIRED));
        GlobalVariables.getMessageMap().clearErrorMessages();
        TestUtils.setSystemParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
        PurchaseOrderDocumentFixture poFixture = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS;

        validation = (CompositeValidation)validations.get("PurchaseOrder-newProcessItemValidation");
        PurchaseOrderDocument po = poFixture.createPurchaseOrderDocument();
        event = new AttributedDocumentEventBase("","", po);

        for(PurchaseOrderItem item : (List<PurchaseOrderItem>)po.getItems()) {
            event.setIterationSubject(item);
            validation.validate(event);
        }
        assertTrue(GlobalVariables.getMessageMap().containsMessageKey(KFSKeyConstants.ERROR_REQUIRED));
        assertTrue(GlobalVariables.getMessageMap().fieldHasMessage(fieldName, KFSKeyConstants.ERROR_REQUIRED));

    }

    /**
     * Tests that, if a valid and active commodity code is entered and is required according to the
     * system parameter, the validation should return true (successful).
     *
     * @throws Exception
     */
    public void testValidActiveCommodityCode() throws Exception {
        TestUtils.setSystemParameter(RequisitionDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
        RequisitionDocumentWithCommodityCodeFixture fixture = RequisitionDocumentWithCommodityCodeFixture.REQ_VALID_ACTIVE_COMMODITY_CODE;

        CompositeValidation validation = (CompositeValidation)validations.get("Requisition-newProcessItemValidation");
        RequisitionDocument req = fixture.createRequisitionDocument();
        AttributedDocumentEventBase event = new AttributedDocumentEventBase("","", req);
        boolean valid = true;

        for(RequisitionItem item : (List<RequisitionItem>)req.getItems()) {
            event.setIterationSubject(item);
            valid &= validation.validate(event);
        }
        assertTrue("There should have been no validation errors: " + GlobalVariables.getMessageMap().getErrorMessages(), valid);
        GlobalVariables.getMessageMap().clearErrorMessages();
        TestUtils.setSystemParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
        PurchaseOrderDocumentWithCommodityCodeFixture poFixture = PurchaseOrderDocumentWithCommodityCodeFixture.PO_VALID_ACTIVE_COMMODITY_CODE;

        validation = (CompositeValidation)validations.get("PurchaseOrder-newProcessItemValidation");
        PurchaseOrderDocument po = poFixture.createPurchaseOrderDocument();
        event = new AttributedDocumentEventBase("","", po);
        valid = true;

        for(PurchaseOrderItem item : (List<PurchaseOrderItem>)po.getItems()) {
            event.setIterationSubject(item);
            valid &= validation.validate(event);
        }
        assertTrue("There should have been no validation errors: " + GlobalVariables.getMessageMap().getErrorMessages(), valid);
    }

    /**
     * Tests that, if a commodity code entered on the item is inactive, it will give validation error
     * about inactive commodity code.
     *
     * @throws Exception
     */
    public void testInactiveCommodityCodeValidation() throws Exception {
        TestUtils.setSystemParameter(RequisitionDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
        RequisitionDocumentWithCommodityCodeFixture fixture = RequisitionDocumentWithCommodityCodeFixture.REQ_VALID_INACTIVE_COMMODITY_CODE;

        CompositeValidation validation = (CompositeValidation)validations.get("Requisition-newProcessItemValidation");
        RequisitionDocument req = fixture.createRequisitionDocument();
        AttributedDocumentEventBase event = new AttributedDocumentEventBase("","", req);

        for(RequisitionItem item : (List<RequisitionItem>)req.getItems()) {
            event.setIterationSubject(item);
            validation.validate(event);
        }
        assertTrue(GlobalVariables.getMessageMap().containsMessageKey(PurapKeyConstants.PUR_COMMODITY_CODE_INACTIVE));
        GlobalVariables.getMessageMap().clearErrorMessages();
        TestUtils.setSystemParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
        PurchaseOrderDocumentWithCommodityCodeFixture poFixture = PurchaseOrderDocumentWithCommodityCodeFixture.PO_VALID_INACTIVE_COMMODITY_CODE;

        validation = (CompositeValidation)validations.get("PurchaseOrder-newProcessItemValidation");
        PurchaseOrderDocument po = poFixture.createPurchaseOrderDocument();
        event = new AttributedDocumentEventBase("","", po);

        for(PurchaseOrderItem item : (List<PurchaseOrderItem>)po.getItems()) {
            event.setIterationSubject(item);
            validation.validate(event);
        }
        assertTrue(GlobalVariables.getMessageMap().containsMessageKey(PurapKeyConstants.PUR_COMMODITY_CODE_INACTIVE));
    }

    /**
     * Tests that, if a commodity code entered on the item has not existed yet in the database, it will give
     * validation error about invalid commodity code.
     *
     * @throws Exception
     */
    public void testNonExistenceCommodityCodeValidation() throws Exception {
        TestUtils.setSystemParameter(RequisitionDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
        RequisitionDocumentWithCommodityCodeFixture fixture = RequisitionDocumentWithCommodityCodeFixture.REQ_NON_EXISTENCE_COMMODITY_CODE;

        CompositeValidation validation = (CompositeValidation)validations.get("Requisition-newProcessItemValidation");
        RequisitionDocument req = fixture.createRequisitionDocument();
        AttributedDocumentEventBase event = new AttributedDocumentEventBase("","", req);

        for(RequisitionItem item : (List<RequisitionItem>)req.getItems()) {
            event.setIterationSubject(item);
            validation.validate(event);
        }
        assertTrue(GlobalVariables.getMessageMap().containsMessageKey(PurapKeyConstants.PUR_COMMODITY_CODE_INVALID));
        GlobalVariables.getMessageMap().clearErrorMessages();
        TestUtils.setSystemParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
        PurchaseOrderDocumentWithCommodityCodeFixture poFixture = PurchaseOrderDocumentWithCommodityCodeFixture.PO_NON_EXISTENCE_COMMODITY_CODE;

        validation = (CompositeValidation)validations.get("PurchaseOrder-newProcessItemValidation");
        PurchaseOrderDocument po = poFixture.createPurchaseOrderDocument();
        event = new AttributedDocumentEventBase("","", po);

        for(PurchaseOrderItem item : (List<PurchaseOrderItem>)po.getItems()) {
            event.setIterationSubject(item);
            validation.validate(event);
        }
        assertTrue(GlobalVariables.getMessageMap().containsMessageKey(PurapKeyConstants.PUR_COMMODITY_CODE_INVALID));

    }

    // Tests of validateItemUnitPrice

    public void testValidateItemUnitPrice_Positive_Above() {
        RequisitionItem item = ItemFieldsFixture.ALL_FIELDS_ABOVE_QUANTITY_BASED.populateRequisitionItem();

        PurchasingItemUnitPriceValidation validation = (PurchasingItemUnitPriceValidation)validations.get("Purchasing-itemUnitPriceValidation");
        validation.setItemForValidation(item);
        assertTrue( validation.validate(null) );
    }

    public void testValidateItemUnitPrice_Negative_Above() {
        RequisitionItem item = ItemFieldsFixture.NEGATIVE_UNIT_PRICE_QUANTITY_BASED.populateRequisitionItem();

        PurchasingItemUnitPriceValidation validation = (PurchasingItemUnitPriceValidation)validations.get("Purchasing-itemUnitPriceValidation");
        validation.setItemForValidation(item);
        assertFalse( validation.validate(null) );
    }

    public void testValidateItemUnitPrice_Positive_Discount() {
        RequisitionItem item = ItemFieldsFixture.POSITIVE_UNIT_PRICE_DISCOUNT.populateRequisitionItem();

        PurchasingItemUnitPriceValidation validation = (PurchasingItemUnitPriceValidation)validations.get("Purchasing-itemUnitPriceValidation");
        validation.setItemForValidation(item);
        assertFalse( validation.validate(null) );
    }

    public void testValidateItemUnitPrice_Negative_Discount() {
        RequisitionItem item = ItemFieldsFixture.NEGATIVE_UNIT_PRICE_DISCOUNT.populateRequisitionItem();

        PurchasingItemUnitPriceValidation validation = (PurchasingItemUnitPriceValidation)validations.get("Purchasing-itemUnitPriceValidation");
        validation.setItemForValidation(item);
        assertTrue( validation.validate(null) );
    }

    public void testValidateItemUnitPrice_Positive_TradeIn() {
        RequisitionItem item = ItemFieldsFixture.POSITIVE_UNIT_PRICE_TRADEIN.populateRequisitionItem();

        PurchasingItemUnitPriceValidation validation = (PurchasingItemUnitPriceValidation)validations.get("Purchasing-itemUnitPriceValidation");
        validation.setItemForValidation(item);
        assertFalse( validation.validate(null) );
    }

    public void testValidateItemUnitPrice_Negative_TradeIn() {
        RequisitionItem item = ItemFieldsFixture.NEGATIVE_UNIT_PRICE_TRADEIN.populateRequisitionItem();

        PurchasingItemUnitPriceValidation validation = (PurchasingItemUnitPriceValidation)validations.get("Purchasing-itemUnitPriceValidation");
        validation.setItemForValidation(item);
        assertTrue( validation.validate(null) );
    }

    // Tests of validateBelowTheLineItemNoUnitCost

    public void testValidateBelowTheLineItemNoUnitCost_WithUnitCost() {
        RequisitionItem item = ItemFieldsFixture.ALL_FIELDS_BELOW.populateRequisitionItem();
        List<PurApAccountingLine> accountingLines = new ArrayList<PurApAccountingLine>();
        accountingLines.add(new RequisitionAccount());
        item.setSourceAccountingLines(accountingLines);

        PurchasingBelowTheLineItemNoUnitCostValidation validation = (PurchasingBelowTheLineItemNoUnitCostValidation)validations.get("Purchasing-belowTheLineItemNoUnitCostValidation");
        validation.setItemForValidation(item);
        assertTrue( validation.validate(null) );
    }

    public void testValidateBelowTheLineItemNoUnitCost_NoUnitCost() {
        RequisitionItem item = ItemFieldsFixture.NO_UNIT_PRICE_BELOW.populateRequisitionItem();
        List<PurApAccountingLine> accountingLines = new ArrayList<PurApAccountingLine>();
        accountingLines.add(new RequisitionAccount());
        item.setSourceAccountingLines(accountingLines);

        PurchasingBelowTheLineItemNoUnitCostValidation validation = (PurchasingBelowTheLineItemNoUnitCostValidation)validations.get("Purchasing-belowTheLineItemNoUnitCostValidation");
        validation.setItemForValidation(item);
        assertFalse( validation.validate(null) );
    }

    public void testValidateOneSystemCapitalAssetSystemChartsRequiringParameters() {
        RequisitionDocument requisition = RequisitionDocumentWithCapitalAssetItemsFixture.REQ_VALID_ONE_NEW_CAPITAL_ASSET_ITEM.createRequisitionDocument();
        SpringContext.getBean(PurchasingService.class).setupCapitalAssetItems(requisition);

        PurchasingCapitalAssetValidation validation = (PurchasingCapitalAssetValidation)validations.get("Purchasing-capitalAssetValidation-test");
        assertTrue( validation.validate(new AttributedDocumentEventBase("", "", requisition)) );

        //BA is one of the chart code that requires some fields (e.g. comments) to be filled in.
        PurchasingDocument purchasingDocument = requisition;
        purchasingDocument.getItems().get(0).getSourceAccountingLines().get(0).setChartOfAccountsCode("BA");
        assertFalse( "Chart BA should have required comments", validation.validate(new AttributedDocumentEventBase("", "", requisition)) );
    }
}

