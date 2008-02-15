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

import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.bo.ObjSubTyp;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.purap.bo.CapitalAssetTransactionType;
import org.kuali.module.purap.document.PurchasingDocument;
import org.kuali.module.purap.fixtures.DeliveryRequiredDateFixture;
import org.kuali.module.purap.fixtures.PurchaseOrderDocumentFixture;
import org.kuali.module.purap.fixtures.PurchasingCapitalAssetFixture;
import org.kuali.module.purap.fixtures.RecurringPaymentBeginEndDatesFixture;
import org.kuali.module.purap.fixtures.RequisitionDocumentFixture;
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
        GlobalVariables.setMessageList(new ArrayList());
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
       HashSet set = PurchasingCapitalAssetFixture.TWO_CAPITAL.populateForCapitalAndExpenseCheck();
       ObjectCode objectCode = PurchasingCapitalAssetFixture.TWO_CAPITAL.getObjectCode();
       assertTrue(rules.validateAccountingLinesNotCapitalAndExpense(set, false, "1", objectCode));
    }
    
    /**
     * Tests that, if two object codes of a level that is not Capital Asset have been processed,
     * the rule will be passed.
     */
    public void testValidateAccountingLinesNotCapitalAndExpense_TwoExpense() {
        HashSet set = PurchasingCapitalAssetFixture.TWO_EXPENSE.populateForCapitalAndExpenseCheck();
        ObjectCode objectCode = PurchasingCapitalAssetFixture.TWO_EXPENSE.getObjectCode();
        assertTrue(rules.validateAccountingLinesNotCapitalAndExpense(set, false, "1", objectCode));       
    }
    
    /**
     * Tests that, if an object code with a level of Capital Asset has been processed together 
     * with an object code not of a level of Capital Asset, then the rule will be failed.
     */
    public void testValidateAccountingLinesNotCapitalAndExpense_CapitalExpense() {
        HashSet set = PurchasingCapitalAssetFixture.CAPITAL_EXPENSE.populateForCapitalAndExpenseCheck();
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
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.NULL_QUANTIY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
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
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.NEGATIVE_QUANTIY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
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
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.ZERO_QUANTIY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
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
    
    
}
