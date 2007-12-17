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

import org.kuali.module.purap.document.PurchasingDocument;
import org.kuali.module.purap.fixtures.DeliveryRequiredDateFixture;
import org.kuali.module.purap.fixtures.PurchaseOrderDocumentFixture;
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
}
