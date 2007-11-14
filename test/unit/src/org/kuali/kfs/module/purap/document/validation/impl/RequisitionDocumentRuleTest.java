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

import org.kuali.core.util.GlobalVariables;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.purap.fixtures.AmountsLimitsFixture;
import org.kuali.module.purap.fixtures.RequisitionDocumentFixture;
import org.kuali.test.ConfigureContext;

@ConfigureContext(session = KHUNTLEY)
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

    /** Additional Validations */
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
    /** End of Additional Validations */
    
    /** Vendor Validations */    
    //Debarred Vendor should fail the vendor validation.
    public void testProcessVendorValidation_REQ_B2B_With_Debarred_Vendor() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_B2B_WITH_DEBARRED_VENDOR.createRequisitionDocument();
        assertFalse(rule.processVendorValidation(req));
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(PurapKeyConstants.ERROR_DEBARRED_VENDOR));
    }
    
    //Inactive Vendor should fail the vendor validation.
    public void testProcessVendorValidation_REQ_B2B_With_Inactive_Vendor() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_B2B_WITH_INACTIVE_VENDOR.createRequisitionDocument();
        assertFalse(rule.processVendorValidation(req));
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(PurapKeyConstants.ERROR_INACTIVE_VENDOR));
    }
    
    //Vendor Type PO is currently the valid vendor type in the system parameter. DV is not a valid type.
    //TODO: We'll probably need to change this test somehow so that the valid type is obtained dynamically, 
    //need to figure out how to do that in the future.
    public void testProcessVendorValidation_REQ_B2B_With_DV_Vendor() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_B2B_WITH_DV_VENDOR.createRequisitionDocument();
        assertFalse(rule.processVendorValidation(req));
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(PurapKeyConstants.ERROR_INVALID_VENDOR_TYPE));
    }
    
    //Vendor Fax Number should not contain letter. It should be in ###-###-#### format.
    public void testProcessVendorValidation_REQ_Invalid_Vendor_Fax_Number_Contains_Letter() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_INVALID_VENDOR_FAX_NUMBER_CONTAINS_LETTER.createRequisitionDocument();
        assertFalse(rule.processVendorValidation(req));
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(PurapKeyConstants.ERROR_FAX_NUMBER_INVALID));
    }
    
    //Vendor Fax Number should be in ###-###-#### format.
    public void testProcessVendorValidation_REQ_Invalid_Vendor_Fax_Number_Bad_Format() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_INVALID_VENDOR_FAX_NUMBER_BAD_FORMAT.createRequisitionDocument();
        assertFalse(rule.processVendorValidation(req));
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(PurapKeyConstants.ERROR_FAX_NUMBER_INVALID));
    }
    
    //The vendor fax number when the value is correct (in ###-###-#### format).
    public void testProcessVendorValidation_REQ_Valid_Vendor_Fax_Number() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_VALID_VENDOR_FAX_NUMBER.createRequisitionDocument();
        assertTrue(rule.processVendorValidation(req));
    }
    
    //The vendor zip code validation should not take place if the requisition is a B2B, even if
    //the zip code is actually invalid.
    public void testProcessVendorValidation_REQ_B2B_WITH_INVALID_US_VENDOR_ZIP_CODE_CONTAINS_LETTER() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_B2B_WITH_INVALID_US_VENDOR_ZIP_CODE_CONTAINS_LETTER.createRequisitionDocument();
        assertTrue(rule.processVendorValidation(req));
    }
    
    //The vendor zip code validation should not take place if the vendor country is not US.
    public void testProcessVendorValidation_REQ_WITH_INVALID_NON_US_VENDOR_ZIP_CODE_CONTAINS_LETTER() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_WITH_INVALID_NON_US_VENDOR_ZIP_CODE_CONTAINS_LETTER.createRequisitionDocument();
        assertTrue(rule.processVendorValidation(req));
    }
    
    //The vendor zip code validation should take place if the vendor country is US and
    //the requisition is not B2B. The valid zip code is ##### or #####-####
    public void testProcessVendorValidation_REQ_WITH_INVALID_US_VENDOR_ZIP_CODE_CONTAINS_LETTER() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_WITH_INVALID_US_VENDOR_ZIP_CODE_CONTAINS_LETTER.createRequisitionDocument();
        assertFalse(rule.processVendorValidation(req));
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(PurapKeyConstants.ERROR_POSTAL_CODE_INVALID));
    }
    
    //The vendor zip code validation should take place if the vendor country is US and
    //the requisition is not B2B. The valid zip code is ##### or #####-####
    public void testProcessVendorValidation_REQ_WITH_INVALID_US_VENDOR_ZIP_CODE_BAD_FORMAT() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_WITH_INVALID_US_VENDOR_ZIP_CODE_BAD_FORMAT.createRequisitionDocument();
        assertFalse(rule.processVendorValidation(req));
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(PurapKeyConstants.ERROR_POSTAL_CODE_INVALID));
    }
    
    //The vendor zip code validation when the country is US and requisition is not B2B and the zip code is #####
    public void testProcessVendorValidation_REQ_WITH_VALID_ZIP_CODE_SHORT_FORM() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_APO_VALID.createRequisitionDocument();
        assertTrue(rule.processVendorValidation(req));
    }
    
    //The vendor zip code validation when the country is US and requisition is not B2B and the zip code is #####-####
    public void testProcessVendorValidation_REQ_WITH_VALID_ZIP_CODE_WITH_4_TRAILING_NUMBERS() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_WITH_VALID_US_VENDOR_ZIP_CODE_WITH_4_TRAILING_NUMBERS.createRequisitionDocument();
        assertTrue(rule.processVendorValidation(req));
    }
    
    /** End of Vendor Validations */
    
    /** Payment Info Validations */
    
    //If the begin is after the end date, it should give error.
    public void testCheckBeginDateAfterEndDate() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_PO_BEGIN_DATE_AFTER_END_DATE.createRequisitionDocument();
        assertFalse(rule.processPaymentInfoValidation(req));
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(PurapKeyConstants.ERROR_PURCHASE_ORDER_BEGIN_DATE_AFTER_END));
    }
    //If the begin date is not null, the end date must not be null.
    public void testCheckBeginDateNoEndDate() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_PO_BEGIN_DATE_NO_END_DATE.createRequisitionDocument();
        assertFalse(rule.processPaymentInfoValidation(req));
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(PurapKeyConstants.ERROR_PURCHASE_ORDER_BEGIN_DATE_NO_END_DATE));
    }
    
    //If the end date is not null, the begin date must not be null.
    public void testCheckEndDateNoBeginDate() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_PO_END_DATE_NO_BEGIN_DATE.createRequisitionDocument();
        assertFalse(rule.processPaymentInfoValidation(req));
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(PurapKeyConstants.ERROR_PURCHASE_ORDER_END_DATE_NO_BEGIN_DATE));
    }
    
    //If the begin and end dates are both entered, the recurring payment type must not be null.
    public void testRecurringPaymentTypeNullWhenBeginAndEndDatesAreEntered() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_PO_BEGIN_DATE_AND_END_DATE_NO_RECURRING_PAYMENT_TYPE.createRequisitionDocument();
        assertFalse(rule.processPaymentInfoValidation(req));
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(PurapKeyConstants.ERROR_RECURRING_DATE_NO_TYPE));
    }
    
    //If the recurring payment type is entered, the begin and end dates must not be null.
    public void testRecurringPaymentTypeEnteredWithoutBeginNorEndDates() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_WITH_RECURRING_PAYMENT_TYPE_WITHOUT_BEGIN_NOR_END_DATE.createRequisitionDocument();
        assertFalse(rule.processPaymentInfoValidation(req));
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(PurapKeyConstants.ERROR_RECURRING_TYPE_NO_DATE));
    }

    //If the recurring payment type, begin and end dates are all entered, it should pass the validation.
    public void testRecurringPaymentTypeBeginAndEndDatesEntered() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_WITH_RECURRING_PAYMENT_TYPE_BEGIN_AND_END_DATE.createRequisitionDocument();
        assertTrue(rule.processPaymentInfoValidation(req));
    }
    /** End of Payment Info Validations */    
 
    
}
