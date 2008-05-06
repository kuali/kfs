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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.TestUtils;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.bo.ObjSubTyp;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapRuleConstants;
import org.kuali.module.purap.bo.CapitalAssetTransactionType;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurchasingItemCapitalAsset;
import org.kuali.module.purap.bo.RecurringPaymentType;
import org.kuali.module.purap.bo.RequisitionAccount;
import org.kuali.module.purap.bo.RequisitionItem;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingDocument;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.purap.fixtures.DeliveryRequiredDateFixture;
import org.kuali.module.purap.fixtures.ItemFieldsFixture;
import org.kuali.module.purap.fixtures.PurchaseOrderDocumentFixture;
import org.kuali.module.purap.fixtures.PurchaseOrderDocumentWithCommodityCodeFixture;
import org.kuali.module.purap.fixtures.PurchasingCapitalAssetFixture;
import org.kuali.module.purap.fixtures.RecurringPaymentBeginEndDatesFixture;
import org.kuali.module.purap.fixtures.RequisitionDocumentFixture;
import org.kuali.module.purap.fixtures.RequisitionDocumentWithCommodityCodeFixture;
import org.kuali.test.ConfigureContext;

/**
 * This class contains tests of the rule validation methods present in PurchasingDocumentRuleBase. These should include any tests
 * that test functionality that is common to all Purchasing documents.
 */
@ConfigureContext(session = KHUNTLEY)
public class PurchasingDocumentRuleTest extends PurapRuleTestBase {

    PurchasingDocumentRuleBase rules;

    protected void setUp() throws Exception {
        super.setUp();
        GlobalVariables.setMessageList(new ArrayList<String>());
        rules = new PurchasingDocumentRuleBase();
    }

    protected void tearDown() throws Exception {
        rules = null;
        super.tearDown();
    }

    /**
     * These methods test how the method validating the input to the Payment Info tab on Purchasing documents,
     * PurchasingDocumentRuleBase.processPaymentInfoValidation, works for Requisitions and POs with different combinations of
     * beginning and ending dates, fiscal years, and recurring payment types.
     */
    public void testProcessPaymentInfoValidation_Req_RightOrder() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.REQ_RIGHT_ORDER.populateDocument();
        assertTrue(rules.processPaymentInfoValidation(document));
    }

    public void testProcessPaymentInfoValidation_Req_WrongOrder() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.REQ_WRONG_ORDER.populateDocument();
        assertFalse(rules.processPaymentInfoValidation(document));
    }

    public void testProcessPaymentInfoValidation_Req_Sequential_Next_FY() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.REQ_SEQUENTIAL_NEXT_FY.populateDocument();
        assertTrue(rules.processPaymentInfoValidation(document));
    }

    public void testProcessPaymentInfoValidation_Req_Non_Sequential_Next_FY() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.REQ_NON_SEQUENTIAL_NEXT_FY.populateDocument();
        assertFalse(rules.processPaymentInfoValidation(document));
    }

    public void testProcessPaymentInfoValidation_PO_RightOrder() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.PO_RIGHT_ORDER.populateDocument();
        assertTrue(rules.processPaymentInfoValidation(document));
    }

    public void testProcessPaymentInfoValidation_PO_WrongOrder() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.PO_WRONG_ORDER.populateDocument();
        assertFalse(rules.processPaymentInfoValidation(document));
    }

    public void testProcessPaymentInfoValidation_PO_Sequential_Next_FY() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.PO_SEQUENTIAL_NEXT_FY.populateDocument();
        assertTrue(rules.processPaymentInfoValidation(document));
    }

    public void testProcessPaymentInfoValidation_PO_Non_Sequential_Next_FY() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.PO_NON_SEQUENTIAL_NEXT_FY.populateDocument();
        assertFalse(rules.processPaymentInfoValidation(document));
    }
    
    /**
     * Validates that if the delivery required date is not entered on a requisition,
     * it will pass the rule.
     */
    public void testProcessDeliveryValidation_Req_No_DeliveryRequiredDate() {
        PurchasingDocument document = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
        assertTrue(rules.processDeliveryValidation(document));
    }
    
    /**
     * Validates that if the delivery required date on a requisition is equal to the
     * current date, it will pass the rule.
     */
    public void testProcessDeliveryValidation_Req_DeliveryRequiredDateCurrent() {
        PurchasingDocument document = DeliveryRequiredDateFixture.DELIVERY_REQUIRED_EQUALS_CURRENT_DATE.createRequisitionDocument();
        assertTrue(rules.processDeliveryValidation(document));
    }
    
    /**
     * Validates that if the delivery required date on a requisition is before the
     * current date, it will not pass the rule.
     */
    public void testProcessDeliveryValidation_Req_DeliveryRequiredDateBeforeCurrent() {
        PurchasingDocument document = DeliveryRequiredDateFixture.DELIVERY_REQUIRED_BEFORE_CURRENT_DATE.createRequisitionDocument();
        assertFalse(rules.processDeliveryValidation(document));
    }
    
    /**
     * Validates that if the delivery required date on a requisition is after the
     * current date, it will pass the rule.
     */
    public void testProcessDeliveryValidation_Req_DeliveryRequiredDateAfterCurrent() {
        PurchasingDocument document = DeliveryRequiredDateFixture.DELIVERY_REQUIRED_AFTER_CURRENT_DATE.createRequisitionDocument();
        assertTrue(rules.processDeliveryValidation(document));
    }
    
    /**
     * Validates that if the delivery required date is not entered on a purchase order,
     * it will pass the rule.
     */
    public void testProcessDeliveryValidation_PO_No_DeliveryRequiredDate() {
        PurchasingDocument document = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS.createPurchaseOrderDocument();
        assertTrue(rules.processDeliveryValidation(document));
    }
    
    /**
     * Validates that if the delivery required date on a purchase order is equal to the
     * current date, it will pass the rule.
     */
    public void testProcessDeliveryValidation_PO_DeliveryRequiredDateCurrent() {
        PurchasingDocument document = DeliveryRequiredDateFixture.DELIVERY_REQUIRED_EQUALS_CURRENT_DATE.createPurchaseOrderDocument();
        assertTrue(rules.processDeliveryValidation(document));
    }
    
    /**
     * Validates that if the delivery required date on a purchase order is before the
     * current date, it will not pass the rule.
     */
    public void testProcessDeliveryValidation_PO_DeliveryRequiredDateBeforeCurrent() {
        PurchasingDocument document = DeliveryRequiredDateFixture.DELIVERY_REQUIRED_BEFORE_CURRENT_DATE.createPurchaseOrderDocument();
        assertFalse(rules.processDeliveryValidation(document));
    }
    
    /**
     * Validates that if the delivery required date on a purchase order is after the
     * current date, it will pass the rule.
     */
    public void testProcessDeliveryValidation_PO_DeliveryRequiredDateAfterCurrent() {
        PurchasingDocument document = DeliveryRequiredDateFixture.DELIVERY_REQUIRED_AFTER_CURRENT_DATE.createPurchaseOrderDocument();
        assertTrue(rules.processDeliveryValidation(document));
    }
    
    /* 
     * Capital Asset Validation tests
     */
    
    // Tests of validateAccountingLinesNotCapitalAndExpense
    
    /**
     * Tests that, if two object codes of Capital Asset level have been processed, the
     * rule will be passed.
     */
    public void testValidateAccountingLinesNotCapitalAndExpense_TwoCapital() {
       HashSet<String> set = PurchasingCapitalAssetFixture.TWO_CAPITAL.populateForCapitalAndExpenseCheck();
       ObjectCode objectCode = PurchasingCapitalAssetFixture.TWO_CAPITAL.getObjectCode();
       assertTrue(rules.validateAccountingLinesNotCapitalAndExpense(set, false, "1", objectCode));
    }
    
    /**
     * Tests that, if two object codes of a level that is not Capital Asset have been processed,
     * the rule will be passed.
     */
    public void testValidateAccountingLinesNotCapitalAndExpense_TwoExpense() {
        HashSet<String> set = PurchasingCapitalAssetFixture.TWO_EXPENSE.populateForCapitalAndExpenseCheck();
        ObjectCode objectCode = PurchasingCapitalAssetFixture.TWO_EXPENSE.getObjectCode();
        assertTrue(rules.validateAccountingLinesNotCapitalAndExpense(set, false, "1", objectCode));       
    }
    
    /**
     * Tests that, if an object code with a level of Capital Asset has been processed together 
     * with an object code not of a level of Capital Asset, then the rule will be failed.
     */
    public void testValidateAccountingLinesNotCapitalAndExpense_CapitalExpense() {
        HashSet<String> set = PurchasingCapitalAssetFixture.CAPITAL_EXPENSE.populateForCapitalAndExpenseCheck();
        ObjectCode objectCode = PurchasingCapitalAssetFixture.CAPITAL_EXPENSE.getObjectCode();
        assertFalse(rules.validateAccountingLinesNotCapitalAndExpense(set, false, "1", objectCode));
    }
    
    // Tests of validateLevelCapitalAssetIndication
    
    @SuppressWarnings("deprecation")
    private ObjectCode getObjectCodeWithLevel(PurchasingCapitalAssetFixture fixture, String levelCode) {
        ObjectCode objectCode = fixture.getObjectCode();
        ObjLevel objLevel = new ObjLevel();
        objLevel.setFinancialObjectLevelCode(levelCode);
        objectCode.setFinancialObjectLevel(objLevel);
        return objectCode;
    }
    
    /**
     * Tests that the rule will be failed if given a positive quantity, an extended price above the
     * threshold for capital assets, and an object code whose level should be among those listed in 
     * the POSSIBLE_CAPITAL_ASSET_OBJECT_LEVELS parameter.
     */  
    public void testValidateLevelCapitalAssetIndication_HappyPath() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
        KualiDecimal itemQuantity = fixture.getQuantity();
        KualiDecimal extendedPrice = fixture.getExtendedPrice();
        ObjectCode objectCode = getObjectCodeWithLevel(fixture, "S&E");
        assertFalse(rules.validateLevelCapitalAssetIndication(itemQuantity, extendedPrice, objectCode, "1"));
    }
    
    /**
     * Tests that the rule will be passed if given a positive quantity, an extended price above the
     * threshold for capital assets, but an object code whose level should not be among those listed in 
     * the POSSIBLE_CAPITAL_ASSET_OBJECT_LEVELS parameter, but should indicate a definite capital asset.
     */
    public void testValidateLevelCapitalAssetIndication_CapCodeLevel() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_CAPITAL_PRICE_CAP_OBJECT_CODE;
        KualiDecimal itemQuantity = fixture.getQuantity();
        KualiDecimal extendedPrice = fixture.getExtendedPrice();
        ObjectCode objectCode = getObjectCodeWithLevel(fixture, "CAP");
        assertTrue(rules.validateLevelCapitalAssetIndication(itemQuantity, extendedPrice, objectCode, "1"));
    }
    
    /**
     * Tests that the rule will be passed if given a positive quantity, an extended price above the
     * threshold for capital assets, but an object code whose level should not be among those listed in 
     * the POSSIBLE_CAPITAL_ASSET_OBJECT_LEVELS parameter, but should indicate an expense.
     */
    public void testValidateLevelCapitalAssetIndication_ExpenseCodeLevel() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_CAPITAL_PRICE_EXPENSE_OBJECT_CODE;
        KualiDecimal itemQuantity = fixture.getQuantity();
        KualiDecimal extendedPrice = fixture.getExtendedPrice();
        ObjectCode objectCode = getObjectCodeWithLevel(fixture, "DEBT");      
        assertTrue(rules.validateLevelCapitalAssetIndication(itemQuantity, extendedPrice, objectCode, "1"));
    }
    
    /**
     * Tests that the rule will be passed if given a null quantity, an extended price above the
     * threshold for capital assets, but an object code whose level should be among those listed in 
     * the POSSIBLE_CAPITAL_ASSET_OBJECT_LEVELS parameter, due to the null quantity.
     */
    public void testValidateLevelCapitalAssetIndication_NullQuantity() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.NULL_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
        KualiDecimal itemQuantity = fixture.getQuantity();
        KualiDecimal extendedPrice = fixture.getExtendedPrice();
        ObjectCode objectCode = getObjectCodeWithLevel(fixture, "S&E");      
        assertTrue(rules.validateLevelCapitalAssetIndication(itemQuantity, extendedPrice, objectCode, "1"));
    }
 
    /**
     * Tests that the rule will be passed if given a negative quantity, an extended price above the
     * threshold for capital assets, but an object code whose level should be among those listed in 
     * the POSSIBLE_CAPITAL_ASSET_OBJECT_LEVELS parameter, due to the negative quantity.
     */
    public void testValidateLevelCapitalAssetIndication_NegativeQuantity() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.NEGATIVE_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
        KualiDecimal itemQuantity = fixture.getQuantity();
        KualiDecimal extendedPrice = fixture.getExtendedPrice();
        ObjectCode objectCode = getObjectCodeWithLevel(fixture, "S&E");    
        assertTrue(rules.validateLevelCapitalAssetIndication(itemQuantity, extendedPrice, objectCode, "1"));
    }
    
    /**
     * Tests that the rule will be passed if given a zero quantity, an extended price above the
     * threshold for capital assets, but an object code whose level should be among those listed in 
     * the POSSIBLE_CAPITAL_ASSET_OBJECT_LEVELS parameter, due to the zero quantity.
     */
    public void testValidateLevelCapitalAssetIndication_ZeroQuantity() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.ZERO_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
        KualiDecimal itemQuantity = fixture.getQuantity();
        KualiDecimal extendedPrice = fixture.getExtendedPrice();
        ObjectCode objectCode = getObjectCodeWithLevel(fixture, "S&E");     
        assertTrue(rules.validateLevelCapitalAssetIndication(itemQuantity, extendedPrice, objectCode, "1"));
    }
    
    /**
     * Tests that the rule will be passed if given a null quantity, an extended price below the
     * threshold for capital assets, but an object code whose level should be among those listed in 
     * the POSSIBLE_CAPITAL_ASSET_OBJECT_LEVELS parameter, due to the lower price.
     */
    public void testValidateLevelCapitalAssetIndication_NonCapitalPrice() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_POSITIVE_PRICE_POSSIBLE_OBJECT_CODE;
        KualiDecimal itemQuantity = fixture.getQuantity();
        KualiDecimal extendedPrice = fixture.getExtendedPrice();
        ObjectCode objectCode = getObjectCodeWithLevel(fixture, "S&E");
        assertTrue(rules.validateLevelCapitalAssetIndication(itemQuantity, extendedPrice, objectCode, "1"));
    }
    
    // Tests of validateCapitalAssetTransactionTypeIsRequired
    
    public void validateCapitalAssetTransactionTypeIsRequired_RequiringSubtype() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
        ObjectCode objectCode = fixture.getObjectCode();
        objectCode.setFinancialObjectSubTypeCode("AM"); // "Arts and Museums"
        assertFalse(rules.validateCapitalAssetTransactionTypeIsRequired(objectCode, false, "1"));
    }
    
    public void validateCapitalAssetTransactionTypeIsRequired_NonRequiringSubtype() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
        ObjectCode objectCode = fixture.getObjectCode();
        objectCode.setFinancialObjectSubTypeCode("BI"); // "Bond Issuance"
        assertTrue(rules.validateCapitalAssetTransactionTypeIsRequired(objectCode, false, "1"));
    }
    
    // Tests of validateObjectCodeVersusTransactionType
    
    @SuppressWarnings("deprecation")
    public void testValidateObjectCodeVersusTransactionType_Passing() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
        ObjectCode objectCode = fixture.getObjectCode();
        ObjSubTyp financialObjectSubType = new ObjSubTyp();
        objectCode.setFinancialObjectSubTypeCode("AM");
        financialObjectSubType.setFinancialObjectSubTypeName("Arts and Museums");
        objectCode.setFinancialObjectSubType(financialObjectSubType);
        CapitalAssetTransactionType tranType = new CapitalAssetTransactionType();
        tranType.setCapitalAssetTransactionTypeCode("NEW");
        assertTrue(rules.validateObjectCodeVersusTransactionType(objectCode, tranType, false, "1"));
    }
    
    @SuppressWarnings("deprecation")
    public void testValidateObjectCodeVersusTransactionType_NotIncludedCombination() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
        ObjectCode objectCode = fixture.getObjectCode();
        ObjSubTyp financialObjectSubType = new ObjSubTyp();
        objectCode.setFinancialObjectSubTypeCode("AM");
        financialObjectSubType.setFinancialObjectSubTypeName("Arts and Museums");
        objectCode.setFinancialObjectSubType(financialObjectSubType);
        CapitalAssetTransactionType tranType = new CapitalAssetTransactionType();
        tranType.setCapitalAssetTransactionTypeCode("FABR"); // "Fabrication"
        assertFalse(rules.validateObjectCodeVersusTransactionType(objectCode, tranType, false, "1"));
    }
    
    @SuppressWarnings("deprecation")
    public void testValidateObjectCodeVersusTransactionType_NotIncludedSubtype() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
        ObjectCode objectCode = fixture.getObjectCode();
        ObjSubTyp financialObjectSubType = new ObjSubTyp();
        objectCode.setFinancialObjectSubTypeCode("BI");
        financialObjectSubType.setFinancialObjectSubTypeName("Bond Issuance");
        objectCode.setFinancialObjectSubType(financialObjectSubType);
        CapitalAssetTransactionType tranType = new CapitalAssetTransactionType();
        tranType.setCapitalAssetTransactionTypeCode("NEW");
        assertFalse(rules.validateObjectCodeVersusTransactionType(objectCode, tranType, false, "1"));
    }
    
    // Tests of validateQuantityVersusObjectCode
    
    @SuppressWarnings("deprecation")
    public void testValidateQuantityVersusObjectCode_HappyPath() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
        ObjectCode objectCode = fixture.getObjectCode();
        ObjSubTyp financialObjectSubType = new ObjSubTyp();
        objectCode.setFinancialObjectSubTypeCode("AM");  // Quantity-requiring subtype
        financialObjectSubType.setFinancialObjectSubTypeName("Arts and Museums");
        objectCode.setFinancialObjectSubType(financialObjectSubType);
        CapitalAssetTransactionType tranType = new CapitalAssetTransactionType();
        tranType.setCapitalAssetTransactionTypeCode("NEW"); // Not a service-related tran type
        KualiDecimal itemQuantity = fixture.getQuantity();
        assertTrue(rules.validateQuantityVersusObjectCode(tranType, itemQuantity, objectCode, false, "1"));
    }
    
    @SuppressWarnings("deprecation")
    public void testValidateQuantityVersusObjectCode_ZeroQuantity() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.ZERO_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
        ObjectCode objectCode = fixture.getObjectCode();
        ObjSubTyp financialObjectSubType = new ObjSubTyp();
        objectCode.setFinancialObjectSubTypeCode("AM");  // Quantity-requiring subtype
        financialObjectSubType.setFinancialObjectSubTypeName("Arts and Museums");
        objectCode.setFinancialObjectSubType(financialObjectSubType);
        CapitalAssetTransactionType tranType = new CapitalAssetTransactionType();
        tranType.setCapitalAssetTransactionTypeCode("NEW"); // Not a service-related tran type
        KualiDecimal itemQuantity = fixture.getQuantity();
        assertFalse(rules.validateQuantityVersusObjectCode(tranType, itemQuantity, objectCode, false, "1"));
    }
    
    @SuppressWarnings("deprecation")
    public void testValidateQuantityVersusObjectCode_NullQuantity() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.NULL_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
        ObjectCode objectCode = fixture.getObjectCode();
        ObjSubTyp financialObjectSubType = new ObjSubTyp();
        objectCode.setFinancialObjectSubTypeCode("AM");  // Quantity-requiring subtype
        financialObjectSubType.setFinancialObjectSubTypeName("Arts and Museums");
        objectCode.setFinancialObjectSubType(financialObjectSubType);
        CapitalAssetTransactionType tranType = new CapitalAssetTransactionType();
        tranType.setCapitalAssetTransactionTypeCode("NEW"); // Not a service-related tran type
        KualiDecimal itemQuantity = fixture.getQuantity();
        assertFalse(rules.validateQuantityVersusObjectCode(tranType, itemQuantity, objectCode, false, "1"));
    }
    
    @SuppressWarnings("deprecation")
    public void testValidateQuantityVersusObjectCode_ServiceRelatedTranType() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
        ObjectCode objectCode = fixture.getObjectCode();
        ObjSubTyp financialObjectSubType = new ObjSubTyp();
        objectCode.setFinancialObjectSubTypeCode("AM");  // Quantity-requiring subtype
        financialObjectSubType.setFinancialObjectSubTypeName("Arts and Museums");
        objectCode.setFinancialObjectSubType(financialObjectSubType);
        CapitalAssetTransactionType tranType = new CapitalAssetTransactionType();
        tranType.setCapitalAssetTransactionTypeCode("LEAS"); // Service-related tran type
        KualiDecimal itemQuantity = fixture.getQuantity();
        assertTrue(rules.validateQuantityVersusObjectCode(tranType, itemQuantity, objectCode, false, "1"));
    }      
    
    @SuppressWarnings("deprecation")
    public void testValidateQuantityVersusObjectCode_NonQuantityRequiringSubtype() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.NULL_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
        ObjectCode objectCode = fixture.getObjectCode();
        ObjSubTyp financialObjectSubType = new ObjSubTyp();
        objectCode.setFinancialObjectSubTypeCode("CA");  // Non-quantity-requiring subtype
        financialObjectSubType.setFinancialObjectSubTypeName("Arts and Museums");
        objectCode.setFinancialObjectSubType(financialObjectSubType);
        CapitalAssetTransactionType tranType = new CapitalAssetTransactionType();
        tranType.setCapitalAssetTransactionTypeCode("NEW"); // Not a service-related tran type
        KualiDecimal itemQuantity = fixture.getQuantity();
        assertTrue(rules.validateQuantityVersusObjectCode(tranType, itemQuantity, objectCode, false, "1"));
    }   
    
    // Tests of validateCapitalAssetTransactionTypeVersusRecurrence
    
    /**
     * Tests that, if the rule is given a recurring payment type and a non-recurring transaction type,
     * the rule will fail.
     */
    public void testValidateCapitalAssetTransactionTypeVersusRecurrence_NonRecurringTranType() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.RECURRING_PAYMENT_TYPE_NONRECURRING_TRAN_TYPE;
        CapitalAssetTransactionType tranType = fixture.getCapitalAssetTransactionType();
        RecurringPaymentType recurringPaymentType = fixture.getRecurringPaymentType();
        assertFalse(rules.validateCapitalAssetTransactionTypeVersusRecurrence(tranType, recurringPaymentType, false, "1"));
    }
    
    /**
     * Tests that, if the rule is given no payment type, and a non-recurring transaction type, the rule will pass.
     */
    public void testValidateCapitalAssetTransactionTypeVersusRecurrence_NonRecurringTranTypeAndNoRecurringPaymentType() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.NO_PAYMENT_TYPE_NONRECURRING_TRAN_TYPE;
        CapitalAssetTransactionType tranType = fixture.getCapitalAssetTransactionType();
        RecurringPaymentType recurringPaymentType = fixture.getRecurringPaymentType();
        assertTrue(rules.validateCapitalAssetTransactionTypeVersusRecurrence(tranType, recurringPaymentType, false, "1"));
    }
    
    public void testValidateCapitalAssetTransactionTypeVersusRecurrence_NoTranType() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.RECURRING_PAYMENT_TYPE_NO_TRAN_TYPE;
        CapitalAssetTransactionType tranType = fixture.getCapitalAssetTransactionType();
        RecurringPaymentType recurringPaymentType = fixture.getRecurringPaymentType();
        assertFalse(rules.validateCapitalAssetTransactionTypeVersusRecurrence(tranType, recurringPaymentType, false, "1"));
    }
    
    public void testValidateCapitalAssetTransactionTypeVersusRecurrence_RecurringTranType() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.RECURRING_PAYMENT_TYPE_RECURRING_TRAN_TYPE;
        CapitalAssetTransactionType tranType = fixture.getCapitalAssetTransactionType();
        RecurringPaymentType recurringPaymentType = fixture.getRecurringPaymentType();
        assertTrue(rules.validateCapitalAssetTransactionTypeVersusRecurrence(tranType, recurringPaymentType, false, "1"));
    }
    
    public void testValidateCapitalAssetTransactionTypeVersusRecurrence_RecurringTranTypeAndNoRecurringPaymentType() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.NO_PAYMENT_TYPE_RECURRING_TRAN_TYPE;
        CapitalAssetTransactionType tranType = fixture.getCapitalAssetTransactionType();
        RecurringPaymentType recurringPaymentType = fixture.getRecurringPaymentType();
        assertFalse(rules.validateCapitalAssetTransactionTypeVersusRecurrence(tranType, recurringPaymentType, false, "1"));
    }   
    
    // Tests of validateCapitalAssetNumberRequirements
    
    /**
     * Tests that, if an asset-number-requiring Capital Asset Transaction Type is given, and one asset
     * number has been added, the rule passes.
     */
    public void testValidateCapitalAssetNumberRequirements_AssetNumberRequiringTranTypeOneAsset() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.ASSET_NUMBER_REQUIRING_TRAN_TYPE_ONE_ASSET;
        CapitalAssetTransactionType tranType = fixture.getCapitalAssetTransactionType();
        List<PurchasingItemCapitalAsset> assets = fixture.getAssets();
        assertTrue(rules.validateCapitalAssetNumberRequirements(tranType, assets, false, "1"));
    }
    
    /**
     * Tests that, if an asset-number-requiring Capital Asset Transaction Type is given and no asset
     * numbers have been added, the rule fails.
     */
    public void testValidateCapitalAssetNumberRequirements_AssetNumberRequiringTranTypeNoAssets() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.ASSET_NUMBER_REQUIRING_TRAN_TYPE_NO_ASSETS;
        CapitalAssetTransactionType tranType = fixture.getCapitalAssetTransactionType();
        List<PurchasingItemCapitalAsset> assets = fixture.getAssets();
        assertFalse(rules.validateCapitalAssetNumberRequirements(tranType, assets, false, "1"));
    }
    
    /**
     * Tests that, if an asset-number-requiring Capital Asset Transaction Type is given and more than
     * one asset number has been added, the rule passes.
     */
    public void testValidateCapitalAssetNumberRequirements_AssetNumberRequiringTranTypeTwoAssets() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.ASSET_NUMBER_REQUIRING_TRAN_TYPE_TWO_ASSETS;
        CapitalAssetTransactionType tranType = fixture.getCapitalAssetTransactionType();
        List<PurchasingItemCapitalAsset> assets = fixture.getAssets();
        assertTrue(rules.validateCapitalAssetNumberRequirements(tranType, assets, false, "1"));
    }
    
    /**
     * Tests that, if a Capital Asset Transaction Type is added which does not require asset numbers,
     * and there is no asset number added, the rule will pass anyway.
     */
    public void testValidateCapitalAssetNumberRequirements_NonAssetNumberRequiringTranTypeNoAssets() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.NONASSET_NUMBER_REQUIRING_TRAN_TYPE_NO_ASSETS;
        CapitalAssetTransactionType tranType = fixture.getCapitalAssetTransactionType();
        List<PurchasingItemCapitalAsset> assets = fixture.getAssets();
        assertTrue(rules.validateCapitalAssetNumberRequirements(tranType, assets, false, "1"));
    }
    
    /**
     * Tests that, if a Capital Asset Transaction Type is added which does not require asset numbers,
     * and there is an asset number added, the rule will pass.
     */
    public void testValidateCapitalAssetNumberRequirements_NonAssetNumberRequiringTranTypeOneAsset() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.NONASSET_NUMBER_REQUIRING_TRAN_TYPE_ONE_ASSET;
        CapitalAssetTransactionType tranType = fixture.getCapitalAssetTransactionType();
        List<PurchasingItemCapitalAsset> assets = fixture.getAssets();
        assertTrue(rules.validateCapitalAssetNumberRequirements(tranType, assets, false, "1"));
    }    

    // Tests of validateItemQuantity
    
    public void testValidateItemQuantity_WithQuantity_QuantityBased() {
        RequisitionItem item = ItemFieldsFixture.ALL_FIELDS_ABOVE_QUANTITY_BASED.populateRequisitionItem();
        assertTrue(rules.validateItemQuantity(item));
    }
    
    public void testValidateItemQuantity_WithoutQuantity_QuantityBased() {
        RequisitionItem item = ItemFieldsFixture.NO_QUANTITY_ABOVE_QUANTITY_BASED.populateRequisitionItem();
        assertFalse(rules.validateItemQuantity(item));
    }
    
    public void testValidateItemQuantity_WithQuantity_Service() {
        RequisitionItem item = ItemFieldsFixture.ALL_FIELDS_ABOVE_SERVICE.populateRequisitionItem();
        assertFalse(rules.validateItemQuantity(item));
    }
    
    public void testValidateItemQuantity_WithoutQuantity_Service() {
        RequisitionItem item = ItemFieldsFixture.NO_QUANTITY_ABOVE_SERVICE.populateRequisitionItem();
        assertTrue(rules.validateItemQuantity(item));
    }
    
    // Tests of validateUnitOfMeasure
    
    public void testValidateUnitOfMeasure_WithUOM_QuantityBased() {
        RequisitionItem item = ItemFieldsFixture.ALL_FIELDS_ABOVE_QUANTITY_BASED.populateRequisitionItem();
        assertTrue(rules.validateUnitOfMeasure(item));
    }
    
    public void testValidateUnitOfMeasure_WithoutUOM_QuantityBased() {
        RequisitionItem item = ItemFieldsFixture.NO_UOM_ABOVE_QUANTITY_BASED.populateRequisitionItem();
        assertFalse(rules.validateUnitOfMeasure(item));
    }
    
    public void testValidateUnitOfMeasure_WithoutUOM_Service() {
        RequisitionItem item = ItemFieldsFixture.NO_UOM_ABOVE_SERVICE.populateRequisitionItem();
        assertTrue(rules.validateUnitOfMeasure(item));
    }    
    
    // Tests of validateItemDescription
    
    public void testValidateItemDescription_WithDescription_Above() {
        RequisitionItem item = ItemFieldsFixture.ALL_FIELDS_ABOVE_QUANTITY_BASED.populateRequisitionItem();
        assertTrue(rules.validateItemDescription(item));
    }
        
    public void testValidateItemDescription_WithoutDescription_Above() {
        RequisitionItem item = ItemFieldsFixture.NO_DESC_ABOVE_QUANTITY_BASED.populateRequisitionItem();
        assertFalse(rules.validateItemDescription(item));
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
        rules.processItemValidation(reqFixture.createRequisitionDocument());
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(KFSKeyConstants.ERROR_REQUIRED));
        assertTrue(GlobalVariables.getErrorMap().fieldHasMessage("document.item[0]." + PurapPropertyConstants.ITEM_COMMODITY_CODE, KFSKeyConstants.ERROR_REQUIRED));
        GlobalVariables.getErrorMap().clear();
        TestUtils.setSystemParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
        PurchaseOrderDocumentFixture poFixture = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS;
        rules.processItemValidation(poFixture.createPurchaseOrderDocument());
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(KFSKeyConstants.ERROR_REQUIRED));
        assertTrue(GlobalVariables.getErrorMap().fieldHasMessage("document.item[0]." + PurapPropertyConstants.ITEM_COMMODITY_CODE, KFSKeyConstants.ERROR_REQUIRED));
    
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
        assertTrue(rules.processItemValidation(fixture.createRequisitionDocument()));
        GlobalVariables.getErrorMap().clear();
        TestUtils.setSystemParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
        PurchaseOrderDocumentWithCommodityCodeFixture poFixture = PurchaseOrderDocumentWithCommodityCodeFixture.PO_VALID_ACTIVE_COMMODITY_CODE;
        assertTrue(rules.processItemValidation(poFixture.createPurchaseOrderDocument()));
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
        rules.processItemValidation(fixture.createRequisitionDocument());
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(PurapKeyConstants.PUR_COMMODITY_CODE_INACTIVE));
        GlobalVariables.getErrorMap().clear();
        TestUtils.setSystemParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
        PurchaseOrderDocumentWithCommodityCodeFixture poFixture = PurchaseOrderDocumentWithCommodityCodeFixture.PO_VALID_INACTIVE_COMMODITY_CODE;
        rules.processItemValidation(poFixture.createPurchaseOrderDocument());
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(PurapKeyConstants.PUR_COMMODITY_CODE_INACTIVE));
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
        rules.processItemValidation(fixture.createRequisitionDocument());
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(PurapKeyConstants.PUR_COMMODITY_CODE_INVALID));
        GlobalVariables.getErrorMap().clear();
        TestUtils.setSystemParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
        PurchaseOrderDocumentWithCommodityCodeFixture poFixture = PurchaseOrderDocumentWithCommodityCodeFixture.PO_NON_EXISTENCE_COMMODITY_CODE;
        rules.processItemValidation(poFixture.createPurchaseOrderDocument());
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(PurapKeyConstants.PUR_COMMODITY_CODE_INVALID));
        
    }
    
    // Tests of validateItemUnitPrice
    
    public void testValidateItemUnitPrice_Positive_Above() {
        RequisitionItem item = ItemFieldsFixture.ALL_FIELDS_ABOVE_QUANTITY_BASED.populateRequisitionItem();
        assertTrue(rules.validateItemUnitPrice(item));
    }
    
    public void testValidateItemUnitPrice_Negative_Above() {
        RequisitionItem item = ItemFieldsFixture.NEGATIVE_UNIT_PRICE_QUANTITY_BASED.populateRequisitionItem();
        assertFalse(rules.validateItemUnitPrice(item));
    }
    
    public void testValidateItemUnitPrice_Positive_Discount() {
        RequisitionItem item = ItemFieldsFixture.POSITIVE_UNIT_PRICE_DISCOUNT.populateRequisitionItem();
        assertFalse(rules.validateItemUnitPrice(item));
    }
    
    public void testValidateItemUnitPrice_Negative_Discount() {
        RequisitionItem item = ItemFieldsFixture.NEGATIVE_UNIT_PRICE_DISCOUNT.populateRequisitionItem();
        assertTrue(rules.validateItemUnitPrice(item));
    }
    
    public void testValidateItemUnitPrice_Positive_TradeIn() {
        RequisitionItem item = ItemFieldsFixture.POSITIVE_UNIT_PRICE_TRADEIN.populateRequisitionItem();
        assertFalse(rules.validateItemUnitPrice(item));
    }
    
    public void testValidateItemUnitPrice_Negative_TradeIn() {
        RequisitionItem item = ItemFieldsFixture.NEGATIVE_UNIT_PRICE_TRADEIN.populateRequisitionItem();
        assertTrue(rules.validateItemUnitPrice(item));
    }
    
    // Tests of validateBelowTheLineItemNoUnitCost
    
    public void testValidateBelowTheLineItemNoUnitCost_WithUnitCost() {
        RequisitionItem item = ItemFieldsFixture.ALL_FIELDS_BELOW.populateRequisitionItem();
        List<PurApAccountingLine> accountingLines = new ArrayList<PurApAccountingLine>();
        accountingLines.add(new RequisitionAccount());
        item.setSourceAccountingLines(accountingLines);
        assertTrue(rules.validateBelowTheLineItemNoUnitCost(item));
    }
    
    public void testValidateBelowTheLineItemNoUnitCost_NoUnitCost() {
        RequisitionItem item = ItemFieldsFixture.NO_UNIT_PRICE_BELOW.populateRequisitionItem();
        List<PurApAccountingLine> accountingLines = new ArrayList<PurApAccountingLine>();
        accountingLines.add(new RequisitionAccount());
        item.setSourceAccountingLines(accountingLines);
        assertFalse(rules.validateBelowTheLineItemNoUnitCost(item));
    }
}
