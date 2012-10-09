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

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.Map;

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.validation.PurapRuleTestBase;
import org.kuali.kfs.module.purap.fixture.AmountsLimitsFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.datadictionary.validation.fieldlevel.PhoneNumberValidationPattern;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

@ConfigureContext(session = khuntley)
public class RequisitionDocumentRuleTest extends PurapRuleTestBase {

    private Map<String, GenericValidation> validations;
    RequisitionDocument req;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        req = new RequisitionDocument();
        validations = SpringContext.getBeansOfType(GenericValidation.class);
    }

    @Override
    protected void tearDown() throws Exception {
        validations = null;
        req = null;
        super.tearDown();
    }

    /** Additional Validations */
    public void testValidateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit_ZeroAmountSmallLimit() {
        req = AmountsLimitsFixture.ZERO_AMOUNT_SMALL_LIMIT.populateRequisition();

        RequisitionProcessAdditionalValidation validation = (RequisitionProcessAdditionalValidation)validations.get("Requisition-processAdditionalValidation-test");
        assertTrue( validation.validate(new AttributedDocumentEventBase("","", req)) );
    }

    public void testValidateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit_SmallAmountSmallLimit() {
        req = AmountsLimitsFixture.SMALL_AMOUNT_SMALL_LIMIT.populateRequisition();

        RequisitionProcessAdditionalValidation validation = (RequisitionProcessAdditionalValidation)validations.get("Requisition-processAdditionalValidation-test");
        assertTrue( validation.validate(new AttributedDocumentEventBase("","", req)) );
    }

    public void testValidateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit_LargeAmountSmallLimit() {
        req = AmountsLimitsFixture.LARGE_AMOUNT_SMALL_LIMIT.populateRequisition();

        RequisitionProcessAdditionalValidation validation = (RequisitionProcessAdditionalValidation)validations.get("Requisition-processAdditionalValidation-test");
        assertFalse( validation.validate(new AttributedDocumentEventBase("","", req)) );
    }
    /** End of Additional Validations */

    /** Vendor Validations */
    //Debarred Vendor should fail the vendor validation.
    public void testProcessVendorValidation_REQ_B2B_With_Debarred_Vendor() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_B2B_WITH_DEBARRED_VENDOR.createRequisitionDocument();

        PurchasingProcessVendorValidation validation = (PurchasingProcessVendorValidation)validations.get("Purchasing-processVendorValidation-test");
        assertFalse( validation.validate(new AttributedDocumentEventBase("","", req)) );
        if (SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KFSConstants.OptionalModuleNamespaces.PURCHASING_ACCOUNTS_PAYABLE, "Requisition", PurapParameterConstants.SHOW_DEBARRED_VENDOR_WARNING_IND)) {
            assertTrue(GlobalVariables.getMessageMap().hasWarnings());
        } else {
            assertTrue(GlobalVariables.getMessageMap().containsMessageKey(PurapKeyConstants.ERROR_DEBARRED_VENDOR));
        }
    }

    //Inactive Vendor should fail the vendor validation.
    public void testProcessVendorValidation_REQ_B2B_With_Inactive_Vendor() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_B2B_WITH_INACTIVE_VENDOR.createRequisitionDocument();

        PurchasingProcessVendorValidation validation = (PurchasingProcessVendorValidation)validations.get("Purchasing-processVendorValidation-test");
        assertFalse( validation.validate(new AttributedDocumentEventBase("","", req)) );
        assertTrue(GlobalVariables.getMessageMap().containsMessageKey(PurapKeyConstants.ERROR_INACTIVE_VENDOR));
    }

    //Vendor Type PO is currently the valid vendor type in the system parameter. DV is not a valid type.
    //TODO: We'll probably need to change this test somehow so that the valid type is obtained dynamically,
    //need to figure out how to do that in the future.
    public void testProcessVendorValidation_REQ_B2B_With_DV_Vendor() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_B2B_WITH_DV_VENDOR.createRequisitionDocument();

        PurchasingProcessVendorValidation validation = (PurchasingProcessVendorValidation)validations.get("Purchasing-processVendorValidation-test");
        assertFalse( validation.validate(new AttributedDocumentEventBase("","", req)) );
        assertTrue(GlobalVariables.getMessageMap().containsMessageKey(PurapKeyConstants.ERROR_INVALID_VENDOR_TYPE));
    }

    //Vendor Fax Number should not contain letter. It should be in ###-###-#### format.
    public void testProcessVendorValidation_REQ_Invalid_Vendor_Fax_Number_Contains_Letter() {
        boolean validationFailed = false;
        RequisitionDocument req = RequisitionDocumentFixture.REQ_INVALID_VENDOR_FAX_NUMBER_CONTAINS_LETTER.createRequisitionDocument();

        PurchasingProcessVendorValidation validation = (PurchasingProcessVendorValidation)validations.get("Purchasing-processVendorValidation-test");
        assertTrue( validation.validate(new AttributedDocumentEventBase("","", req)) );

        try {
            req.validateBusinessRules(new SaveDocumentEvent(req));
        } catch (ValidationException e) {
            validationFailed = true;
        }

        assertTrue(validationFailed);
        assertTrue(GlobalVariables.getMessageMap().containsMessageKey(new PhoneNumberValidationPattern().getValidationErrorMessageKey()));
    }

    //Vendor Fax Number should be in ###-###-#### format.
    public void testProcessVendorValidation_REQ_Invalid_Vendor_Fax_Number_Bad_Format() {
        boolean validationFailed = false;
        RequisitionDocument req = RequisitionDocumentFixture.REQ_INVALID_VENDOR_FAX_NUMBER_BAD_FORMAT.createRequisitionDocument();

        PurchasingProcessVendorValidation validation = (PurchasingProcessVendorValidation)validations.get("Purchasing-processVendorValidation-test");
        assertTrue( validation.validate(new AttributedDocumentEventBase("","", req)) );

        try {
            req.validateBusinessRules(new SaveDocumentEvent(req));
        } catch (ValidationException e) {
            validationFailed = true;
        }

        assertTrue(validationFailed);
        assertTrue(GlobalVariables.getMessageMap().containsMessageKey(new PhoneNumberValidationPattern().getValidationErrorMessageKey()));
    }

    //The vendor fax number when the value is correct (in ###-###-#### format).
    public void testProcessVendorValidation_REQ_Valid_Vendor_Fax_Number() {
        boolean validationFailed = false;
        RequisitionDocument req = RequisitionDocumentFixture.REQ_VALID_VENDOR_FAX_NUMBER.createRequisitionDocument();

        PurchasingProcessVendorValidation validation = (PurchasingProcessVendorValidation)validations.get("Purchasing-processVendorValidation-test");
        assertTrue( validation.validate(new AttributedDocumentEventBase("","", req)) );

        try {
            req.validateBusinessRules(new SaveDocumentEvent(req));
        } catch (ValidationException e) {
            validationFailed = true;
        }

        assertFalse(validationFailed);
        assertTrue(GlobalVariables.getMessageMap().hasNoErrors());
    }

    //The vendor zip code validation should not take place if the requisition is a B2B, even if
    //the zip code is actually invalid.
    public void testProcessVendorValidation_REQ_B2B_WITH_INVALID_US_VENDOR_ZIP_CODE_CONTAINS_LETTER() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_B2B_WITH_INVALID_US_VENDOR_ZIP_CODE_CONTAINS_LETTER.createRequisitionDocument();
        PurchasingProcessVendorValidation validation = (PurchasingProcessVendorValidation)validations.get("Purchasing-processVendorValidation-test");
        assertTrue( validation.validate(new AttributedDocumentEventBase("","", req)) );
    }

    //The vendor zip code validation should not take place if the vendor country is not US.
    public void testProcessVendorValidation_REQ_WITH_INVALID_NON_US_VENDOR_ZIP_CODE_CONTAINS_LETTER() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_WITH_INVALID_NON_US_VENDOR_ZIP_CODE_CONTAINS_LETTER.createRequisitionDocument();
        PurchasingProcessVendorValidation validation = (PurchasingProcessVendorValidation)validations.get("Purchasing-processVendorValidation-test");
        assertTrue( validation.validate(new AttributedDocumentEventBase("","", req)) );
    }

    /** End of Vendor Validations */

    /** Payment Info Validations */

    //If the begin is after the end date, it should give error.
    public void testCheckBeginDateAfterEndDate() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_PO_BEGIN_DATE_AFTER_END_DATE.createRequisitionDocument();

        PurchasingPaymentInfoValidation validation = (PurchasingPaymentInfoValidation)validations.get("Purchasing-paymentInfoValidation-test");
        assertFalse( validation.validate(new AttributedDocumentEventBase("","", req)) );
        assertTrue(GlobalVariables.getMessageMap().containsMessageKey(PurapKeyConstants.ERROR_PURCHASE_ORDER_BEGIN_DATE_AFTER_END));
    }
    //If the begin date is not null, the end date must not be null.
    public void testCheckBeginDateNoEndDate() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_PO_BEGIN_DATE_NO_END_DATE.createRequisitionDocument();

        PurchasingPaymentInfoValidation validation = (PurchasingPaymentInfoValidation)validations.get("Purchasing-paymentInfoValidation-test");
        assertFalse( validation.validate(new AttributedDocumentEventBase("","", req)) );
        assertTrue(GlobalVariables.getMessageMap().containsMessageKey(PurapKeyConstants.ERROR_PURCHASE_ORDER_BEGIN_DATE_NO_END_DATE));
    }

    //If the end date is not null, the begin date must not be null.
    public void testCheckEndDateNoBeginDate() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_PO_END_DATE_NO_BEGIN_DATE.createRequisitionDocument();

        PurchasingPaymentInfoValidation validation = (PurchasingPaymentInfoValidation)validations.get("Purchasing-paymentInfoValidation-test");
        assertFalse( validation.validate(new AttributedDocumentEventBase("","", req)) );
        assertTrue(GlobalVariables.getMessageMap().containsMessageKey(PurapKeyConstants.ERROR_PURCHASE_ORDER_END_DATE_NO_BEGIN_DATE));
    }

    //If the begin and end dates are both entered, the recurring payment type must not be null.
    public void testRecurringPaymentTypeNullWhenBeginAndEndDatesAreEntered() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_PO_BEGIN_DATE_AND_END_DATE_NO_RECURRING_PAYMENT_TYPE.createRequisitionDocument();

        PurchasingPaymentInfoValidation validation = (PurchasingPaymentInfoValidation)validations.get("Purchasing-paymentInfoValidation-test");
        assertFalse( validation.validate(new AttributedDocumentEventBase("","", req)) );
        assertTrue(GlobalVariables.getMessageMap().containsMessageKey(PurapKeyConstants.ERROR_RECURRING_DATE_NO_TYPE));
    }

    //If the recurring payment type is entered, the begin and end dates must not be null.
    public void testRecurringPaymentTypeEnteredWithoutBeginNorEndDates() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_WITH_RECURRING_PAYMENT_TYPE_WITHOUT_BEGIN_NOR_END_DATE.createRequisitionDocument();

        PurchasingPaymentInfoValidation validation = (PurchasingPaymentInfoValidation)validations.get("Purchasing-paymentInfoValidation-test");
        assertFalse( validation.validate(new AttributedDocumentEventBase("","", req)) );
        assertTrue(GlobalVariables.getMessageMap().containsMessageKey(PurapKeyConstants.ERROR_RECURRING_TYPE_NO_DATE));
    }

    //If the recurring payment type, begin and end dates are all entered, it should pass the validation.
    public void testRecurringPaymentTypeBeginAndEndDatesEntered() {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_WITH_RECURRING_PAYMENT_TYPE_BEGIN_AND_END_DATE.createRequisitionDocument();
        PurchasingPaymentInfoValidation validation = (PurchasingPaymentInfoValidation)validations.get("Purchasing-paymentInfoValidation-test");
        assertTrue( validation.validate(new AttributedDocumentEventBase("","", req)) );
    }
    /** End of Payment Info Validations */


}

