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

import static org.kuali.kfs.sys.fixture.UserNameFixture.appleton;

import java.sql.Date;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.validation.PurapRuleTestBase;
import org.kuali.kfs.module.purap.fixture.PaymentRequestInvoiceTabFixture;
import org.kuali.kfs.module.purap.fixture.PaymentRequestTaxTabFixture;
import org.kuali.kfs.module.purap.util.TestPaymentRequestPayDateNotPastValidation;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.kfs.sys.document.workflow.MockWorkflowDocument;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.action.ReturnPoint;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.springframework.util.AutoPopulatingList;


@ConfigureContext(session = appleton)
public class PaymentRequestDocumentRuleTest extends PurapRuleTestBase {

    private Map<String, GenericValidation> validations;
    PaymentRequestDocument preq;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preq = new PaymentRequestDocument();
        validations = SpringContext.getBeansOfType(GenericValidation.class);
    }

    @Override
    protected void tearDown() throws Exception {
        validations = null;
        preq = null;
        super.tearDown();
    }

    /*
     * Tests of processInvoiceValidation
     */
    public void testProcessInvoiceValidation_With_All() {
        preq = PaymentRequestInvoiceTabFixture.WITH_POID_WITH_DATE_WITH_NUMBER_WITH_AMOUNT.populate(preq);

        PaymentRequestInvoiceValidation validation = (PaymentRequestInvoiceValidation)validations.get("PaymentRequest-invoiceValidation-test");
        assertTrue( validation.validate(new AttributedDocumentEventBase("","", preq)) );
    }

    public void testProcessInvoiceValidation_Without_PO_ID() {
        preq = PaymentRequestInvoiceTabFixture.NO_POID_WITH_DATE_WITH_NUMBER_WITH_AMOUNT.populate(preq);

        PaymentRequestInvoiceValidation validation = (PaymentRequestInvoiceValidation)validations.get("PaymentRequest-invoiceValidation-test");
        assertFalse( validation.validate(new AttributedDocumentEventBase("","", preq)) );
    }

    public void testProcessInvoiceValidation_Without_Date() {
        preq = PaymentRequestInvoiceTabFixture.WITH_POID_NO_DATE_WITH_NUMBER_WITH_AMOUNT.populate(preq);

        PaymentRequestInvoiceValidation validation = (PaymentRequestInvoiceValidation)validations.get("PaymentRequest-invoiceValidation-test");
        assertFalse( validation.validate(new AttributedDocumentEventBase("","", preq)) );
    }

    public void testProcessInvoiceValidation_Without_Number() {
        preq = PaymentRequestInvoiceTabFixture.WITH_POID_WITH_DATE_NO_NUMBER_WITH_AMOUNT.populate(preq);

        PaymentRequestInvoiceValidation validation = (PaymentRequestInvoiceValidation)validations.get("PaymentRequest-invoiceValidation-test");
        assertFalse( validation.validate(new AttributedDocumentEventBase("","", preq)) );
    }

    public void testProcessInvoiceValidation_Without_Amount() {
        preq = PaymentRequestInvoiceTabFixture.WITH_POID_WITH_DATE_WITH_NUMBER_NO_AMOUNT.populate(preq);

        PaymentRequestInvoiceValidation validation = (PaymentRequestInvoiceValidation)validations.get("PaymentRequest-invoiceValidation-test");
        assertFalse( validation.validate(new AttributedDocumentEventBase("","", preq)) );
    }

    /*
     * Tests of processPurchaseOrderIDValidation
     */

    /*
     * Tests of encumberedItemExistsForInvoicing
     */

    /*
     * Tests of processPaymentRequestDateValidationForContinue
     */
    private Date getDateFromOffsetFromToday(int offsetDays) {
        Calendar calendar = SpringContext.getBean(DateTimeService.class).getCurrentCalendar();
        calendar.add(Calendar.DATE, offsetDays);
        return new Date(calendar.getTimeInMillis());
    }

    public void testProcessPaymentRequestDateValidationForContinue_BeforeToday() {
        preq = PaymentRequestInvoiceTabFixture.WITH_POID_WITH_DATE_WITH_NUMBER_WITH_AMOUNT.populate(preq);
        Date yesterday = getDateFromOffsetFromToday(-1);
        preq.setInvoiceDate(yesterday);

        PaymentRequestDateForContinueValidation validation = (PaymentRequestDateForContinueValidation)validations.get("PaymentRequest-dateForContinueValidation-test");
        assertTrue( validation.validate(new AttributedDocumentEventBase("","", preq)) );
    }

    public void testProcessPaymentRequestDateValidationForContinue_AfterToday() {
        preq = PaymentRequestInvoiceTabFixture.WITH_POID_WITH_DATE_WITH_NUMBER_WITH_AMOUNT.populate(preq);
        Date tomorrow = getDateFromOffsetFromToday(1);
        preq.setInvoiceDate(tomorrow);

        PaymentRequestDateForContinueValidation validation = (PaymentRequestDateForContinueValidation)validations.get("PaymentRequest-dateForContinueValidation-test");
        assertFalse( validation.validate(new AttributedDocumentEventBase("","", preq)) );
    }

    public void testProcessPaymentRequestDateValidationForContinue_Today() {
        preq = PaymentRequestInvoiceTabFixture.WITH_POID_WITH_DATE_WITH_NUMBER_WITH_AMOUNT.populate(preq);
        Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        preq.setInvoiceDate(today);

        PaymentRequestDateForContinueValidation validation = (PaymentRequestDateForContinueValidation)validations.get("PaymentRequest-dateForContinueValidation-test");
        assertTrue( validation.validate(new AttributedDocumentEventBase("","", preq)) );
    }

    public void testValidatePaymentRequestDates_PastAndInitiatedDocument() throws Exception {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        PaymentRequestService paymentRequestService = SpringContext.getBean(PaymentRequestService.class);

        // some date in the past
        Date yesterday = getDateFromOffsetFromToday(-1);

        assertTrue("Something is wrong with the test.  Error map was not empty before document saving called", GlobalVariables.getMessageMap().hasErrors());

        // rule 1: past pay dates are NOT allowed if the document has not been successfully saved or submitted yet
        PaymentRequestDocument document1 = (PaymentRequestDocument) documentService.getNewDocument(PaymentRequestDocument.class);
        document1.setPaymentRequestPayDate(yesterday);

        PaymentRequestPayDateNotPastValidation validation = (PaymentRequestPayDateNotPastValidation)validations.get("PaymentRequest-payDateNotPastValidation-test");
        assertFalse( validation.validate(new AttributedDocumentEventBase("","", document1)) );
        AutoPopulatingList<ErrorMessage> l = GlobalVariables.getMessageMap().getMessages("document.paymentRequestPayDate");
        boolean correctError = false;
        for ( ErrorMessage m : l ) {
            if (PurapKeyConstants.ERROR_INVALID_PAY_DATE.equals(m.getErrorKey())) {
                correctError = true;
                break;
            }
        }
        assertTrue("Unable to find error message key '" + PurapKeyConstants.ERROR_INVALID_PAY_DATE + "'", correctError);

        GlobalVariables.getMessageMap().clearErrorMessages();
    }

    public void testValidatePaymentRequestDates_PastAndPersistedDocument() throws Exception {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);

        final Date yesterday = getDateFromOffsetFromToday(-1);

        // rule 2: if a past paydate was already stored on the document in the database, the document may be saved with a past pay date only if
        // the pay date is the same date in the database (i.e. can NOT change from one past paydate to another)

        // we need to test the second part of the rule, but the testing framework makes this difficult because we have to first
        // store a previous document in the DB, so I'm creating a special rule implementation that retrieves a hard coded document
        TestPaymentRequestPayDateNotPastValidation validation = (TestPaymentRequestPayDateNotPastValidation)validations.get("TestPaymentRequest-payDateNotPastValidation-test");
        validation.setTestDate(yesterday);

        // create a workflow document that simulates the document being enroute
        WorkflowDocument workflowDocument = new MockWorkflowDocument() {
            public String getCurrentRouteNodeNames() {
                return null;
            }

            public boolean isStandardSaveAllowed() {
                return false;
            }

            @Override
            public boolean isInitiated() {
                return false;
            }

            @Override
            public boolean isSaved() {
                return false;
            }

            @Override
            public boolean isEnroute() {
                return true;
            }
            public void superUserActionRequestApprove(Long actionRequestId, String annotation)  {
            }

            @Override
            public void superUserCancel(String annotation)  {
            }

            @Override
            public void superUserDisapprove(String annotation)  {
            }

            public boolean userIsRoutedByUser(Person user) {
                return false;
            }

            public Set<Person> getAllPriorApprovers()  {
                return null;
            }

            public void adHocRouteDocumentToPrincipal(String actionRequested, String routeTypeName, String annotation, String principalId, String responsibilityDesc, boolean ignorePreviousActions) {

            }

            public void adHocRouteDocumentToGroup(String actionRequested, String routeTypeName, String annotation, String groupId, String responsibilityDesc, boolean ignorePreviousActions) {

            }

            @Override
            public void returnToPreviousNode(String nodeName, ReturnPoint returnPoint) {
                // TODO Auto-generated method stub
                
            }


        };

        PaymentRequestDocument document2 = (PaymentRequestDocument) documentService.getNewDocument(PaymentRequestDocument.class);
        document2.getDocumentHeader().setWorkflowDocument(workflowDocument);
        document2.setPaymentRequestPayDate(yesterday);
        assertTrue("Didn't change past pay date, so doucment should validate successfully.", validation.validate(new AttributedDocumentEventBase("","", document2)) );
        assertTrue("Error map should be empty: " + GlobalVariables.getMessageMap().getErrorMessages(), GlobalVariables.getMessageMap().hasErrors());

        document2.setPaymentRequestPayDate(getDateFromOffsetFromToday(-2));
        assertFalse("changed past pay date to another past pay date, so document should fail.", validation.validate(new AttributedDocumentEventBase("","", document2)) );
        assertFalse("Error map should not be empty", GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();

        document2.setPaymentRequestPayDate(getDateFromOffsetFromToday(3));
        assertTrue("Changed past pay date to future, so doucment should validate successfully.", validation.validate(new AttributedDocumentEventBase("","", document2)) );
        assertTrue("Error map should be empty: " + GlobalVariables.getMessageMap().getErrorMessages(), GlobalVariables.getMessageMap().hasErrors());

    }

    public void testValidatePaymentRequestDates_FutureAndPersistedDocument() throws Exception {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);

        final Date tomorrow = getDateFromOffsetFromToday(1);

        // rule 3: if a future paydate was already stored on the document in the database, the document may store any future pay date, but no past pay dates

        // we need to test the second part of the rule, but the testing framework makes this difficult because we have to first
        // store a previous document in the DB, so I'm creating a special rule implementation that retrieves a hard coded document
        TestPaymentRequestPayDateNotPastValidation validation = (TestPaymentRequestPayDateNotPastValidation)validations.get("TestPaymentRequest-payDateNotPastValidation-test");
        validation.setTestDate(tomorrow);

        // create a workflow document that simulates the document being enroute
        WorkflowDocument workflowDocument = new MockWorkflowDocument() {
            public String getCurrentRouteNodeNames() {
                return null;
            }

            public String getRoutedByUserNetworkId() {
                return null;
            }

            public boolean isStandardSaveAllowed() {
                return false;
            }

            @Override
            public boolean isInitiated() {
                return false;
            }

            @Override
            public boolean isSaved() {
                return false;
            }

            @Override
            public boolean isEnroute() {
                return true;
            }
            public void superUserActionRequestApprove(Long actionRequestId, String annotation)  {
            }

            @Override
            public void superUserCancel(String annotation)  {
            }

            @Override
            public void superUserDisapprove(String annotation)  {
            }

            public boolean userIsRoutedByUser(Person user) {
                return false;
            }

            public Set<Person> getAllPriorApprovers()  {
                return null;
            }

            public void adHocRouteDocumentToPrincipal(String actionRequested, String routeTypeName, String annotation, String principalId, String responsibilityDesc, boolean ignorePreviousActions) {

            }

            public void adHocRouteDocumentToGroup(String actionRequested, String routeTypeName, String annotation, String groupId, String responsibilityDesc, boolean ignorePreviousActions) {

            }

            @Override
            public void returnToPreviousNode(String nodeName, ReturnPoint returnPoint) {
                // TODO Auto-generated method stub
                
            }
        };

        PaymentRequestDocument document2 = (PaymentRequestDocument) documentService.getNewDocument(PaymentRequestDocument.class);
        document2.getDocumentHeader().setWorkflowDocument(workflowDocument);
        document2.setPaymentRequestPayDate(tomorrow);
        assertTrue("Didn't change future pay date, so doucment should validate successfully.", validation.validate(new AttributedDocumentEventBase("","", document2)) );
        assertTrue("Error map should be empty: " + GlobalVariables.getMessageMap().getErrorMessages(), GlobalVariables.getMessageMap().hasErrors());

        document2.setPaymentRequestPayDate(getDateFromOffsetFromToday(-2));
        assertFalse("changed future pay date to  past pay date, so document should fail.", validation.validate(new AttributedDocumentEventBase("","", document2)) );
        assertFalse("Error map should not be empty", GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();

        document2.setPaymentRequestPayDate(getDateFromOffsetFromToday(3));
        assertTrue("Changed future pay date to another future date, so doucment should validate successfully.", validation.validate(new AttributedDocumentEventBase("","", document2)) );
        assertTrue("Error map should be empty: " + GlobalVariables.getMessageMap().getErrorMessages(), GlobalVariables.getMessageMap().hasErrors());
    }

    public void testValidatePaymentRequestDates_Today() {
        Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        preq.setPaymentRequestPayDate(today);
        PaymentRequestPayDateNotPastValidation validation = (PaymentRequestPayDateNotPastValidation)validations.get("PaymentRequest-payDateNotPastValidation-test");
        assertTrue( validation.validate(new AttributedDocumentEventBase("","", preq)) );
    }

    public void testValidatePaymentRequestDates_Tomorrow() {
        Date tomorrow = getDateFromOffsetFromToday(1);
        preq.setPaymentRequestPayDate(tomorrow);
        PaymentRequestPayDateNotPastValidation validation = (PaymentRequestPayDateNotPastValidation)validations.get("PaymentRequest-payDateNotPastValidation-test");
        assertTrue( validation.validate(new AttributedDocumentEventBase("","", preq)) );
    }

    /*
     * Tests of validateItem
     */

    /*
     * Tests of validateItemAccounts
     */

    /*
     * Tests of validateCancel
     */

    /*
     * Tests of validatePaymentRequestReview
     */

    /*
     * Test for tax area edit rules.
     */
    public void testProcessPreCalculateTaxAreaBusinessRules() {
        MessageMap errMap = GlobalVariables.getMessageMap();
        String pre = PurapConstants.PAYMENT_REQUEST_TAX_TAB_ERRORS + ".";

        // testing tax income class
        PaymentRequestTaxAreaValidation validation = (PaymentRequestTaxAreaValidation)validations.get("PaymentRequest-taxAreaValidation-test");

        errMap.clearErrorMessages();
        PaymentRequestTaxTabFixture.INCOME_EMPTY.populate(preq);
        assertFalse(validation.validate(new AttributedDocumentEventBase("","", preq)));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_REQUIRED));
        assertTrue(errMap.getErrorCount() == 1);

        PaymentRequestTaxTabFixture.INCOME_N_OTHERS_EMPTY.populate(preq);
        assertTrue(validation.validate(new AttributedDocumentEventBase("","", preq)));

        errMap.clearErrorMessages();
        PaymentRequestTaxTabFixture.INCOME_N_OTHERS_NOTEMPTY.populate(preq);
        assertFalse(validation.validate(new AttributedDocumentEventBase("","", preq)));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_FEDERAL_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_STATE_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_COUNTRY_CODE, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_NQI_ID, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF));
        assertTrue(errMap.getErrorCount() == 5);

        errMap.clearErrorMessages();
        PaymentRequestTaxTabFixture.INCOME_NOTN_TAX_COUNTRY_EMPTY.populate(preq);
        assertFalse(validation.validate(new AttributedDocumentEventBase("","", preq)));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_FEDERAL_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_REQUIRED_IF));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_STATE_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_REQUIRED_IF));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_COUNTRY_CODE, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_REQUIRED_IF));
        assertTrue(errMap.getErrorCount() == 3);

        // testing tax rates

        // Fellowship
        PaymentRequestTaxTabFixture.INCOME_F_TAX_VALID.populate(preq);
        assertTrue(validation.validate(new AttributedDocumentEventBase("","", preq)));

        errMap.clearErrorMessages();
        PaymentRequestTaxTabFixture.INCOME_F_FED_INVALID.populate(preq);
        assertFalse(validation.validate(new AttributedDocumentEventBase("","", preq)));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_FEDERAL_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_VALUE_INVALID_IF));
        assertTrue(errMap.getErrorCount() == 1);

        errMap.clearErrorMessages();
        PaymentRequestTaxTabFixture.INCOME_F_ST_INVALID.populate(preq);
        assertFalse(validation.validate(new AttributedDocumentEventBase("","", preq)));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_STATE_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_VALUE_INVALID_IF));
        assertTrue(errMap.getErrorCount() == 1);

        // Independent Contractor
        PaymentRequestTaxTabFixture.INCOME_I_TAX_VALID.populate(preq);
        assertTrue(validation.validate(new AttributedDocumentEventBase("","", preq)));

        errMap.clearErrorMessages();
        PaymentRequestTaxTabFixture.INCOME_I_FED_INVALID.populate(preq);
        assertFalse(validation.validate(new AttributedDocumentEventBase("","", preq)));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_FEDERAL_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_VALUE_INVALID_IF));
        assertTrue(errMap.getErrorCount() == 1);

        errMap.clearErrorMessages();
        PaymentRequestTaxTabFixture.INCOME_I_ST_INVALID.populate(preq);
        assertFalse(validation.validate(new AttributedDocumentEventBase("","", preq)));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_STATE_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_VALUE_INVALID_IF));
        assertTrue(errMap.getErrorCount() == 1);

        // Royalties
        PaymentRequestTaxTabFixture.INCOME_R_TAX_VALID.populate(preq);
        assertTrue(validation.validate(new AttributedDocumentEventBase("","", preq)));

        errMap.clearErrorMessages();
        PaymentRequestTaxTabFixture.INCOME_R_FED_INVALID.populate(preq);
        assertFalse(validation.validate(new AttributedDocumentEventBase("","", preq)));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_FEDERAL_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_VALUE_INVALID_IF));
        assertTrue(errMap.getErrorCount() == 1);

        errMap.clearErrorMessages();
        PaymentRequestTaxTabFixture.INCOME_R_ST_INVALID.populate(preq);
        assertFalse(validation.validate(new AttributedDocumentEventBase("","", preq)));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_STATE_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_VALUE_INVALID_IF));
        assertTrue(errMap.getErrorCount() == 1);

        // relationship between federal/state tax rates
        PaymentRequestTaxTabFixture.FED_ZERO_ST_ZERO.populate(preq);
        assertTrue(validation.validate(new AttributedDocumentEventBase("","", preq)));

        errMap.clearErrorMessages();
        PaymentRequestTaxTabFixture.FFD_ZERO_ST_NOTZERO.populate(preq);
        assertFalse(validation.validate(new AttributedDocumentEventBase("","", preq)));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_STATE_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF));
        assertTrue(errMap.getErrorCount() == 1);

        errMap.clearErrorMessages();
        PaymentRequestTaxTabFixture.FED_NOTZERO_ST_ZERO.populate(preq);
        assertFalse(validation.validate(new AttributedDocumentEventBase("","", preq)));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_STATE_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_NOT_ZERO_IF));
        assertTrue(errMap.getErrorCount() == 1);

        // testing tax indicators

        // choose gross up
        PaymentRequestTaxTabFixture.GROSS_TAX_NOTZERO.populate(preq);
        assertTrue(validation.validate(new AttributedDocumentEventBase("","", preq)));

        errMap.clearErrorMessages();
        PaymentRequestTaxTabFixture.GROSS_TAX_ZERO.populate(preq);
        assertFalse(validation.validate(new AttributedDocumentEventBase("","", preq)));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_FEDERAL_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_NOT_ZERO_IF));
        assertTrue(errMap.getErrorCount() == 1);

        // choose foreign source
        PaymentRequestTaxTabFixture.FOREIGN_TAX_ZERO.populate(preq);
        assertTrue(validation.validate(new AttributedDocumentEventBase("","", preq)));

        errMap.clearErrorMessages();
        PaymentRequestTaxTabFixture.FOREIGN_TAX_NOTZERO.populate(preq);
        assertFalse(validation.validate(new AttributedDocumentEventBase("","", preq)));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_FEDERAL_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_STATE_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF));
        assertTrue(errMap.getErrorCount() == 2);

        // choose USAID per diem
        PaymentRequestTaxTabFixture.USAID_OTHER_INCOME_F_TAX_ZERO.populate(preq);
        assertTrue(validation.validate(new AttributedDocumentEventBase("","", preq)));

        errMap.clearErrorMessages();
        PaymentRequestTaxTabFixture.USAID_INCOME_NOTF_TAX_NOTZERO.populate(preq);
        assertFalse(validation.validate(new AttributedDocumentEventBase("","", preq)));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_OTHER_EXEMPT_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_REQUIRED_IF));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_VALUE_INVALID_IF));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_FEDERAL_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF));
        assertTrue(errMap.getErrorCount() == 3);

        // choose exempt under other code
        PaymentRequestTaxTabFixture.OTHER_TAX_ZERO.populate(preq);
        assertTrue(validation.validate(new AttributedDocumentEventBase("","", preq)));

        errMap.clearErrorMessages();
        PaymentRequestTaxTabFixture.OTHER_TAX_NOTZERO.populate(preq);
        assertFalse(validation.validate(new AttributedDocumentEventBase("","", preq)));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_FEDERAL_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF));
        assertTrue(errMap.getErrorCount() == 1);

        // choose special W4
        PaymentRequestTaxTabFixture.SW4_INCOME_F_TAX_ZERO.populate(preq);
        assertTrue(validation.validate(new AttributedDocumentEventBase("","", preq)));

        errMap.clearErrorMessages();
        PaymentRequestTaxTabFixture.SW4_NEG_INCOME_NOTF_TAX_NOTZERO.populate(preq);
        assertFalse(validation.validate(new AttributedDocumentEventBase("","", preq)));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_VALUE_MUST_NOT_NEGATIVE));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_VALUE_INVALID_IF));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_FEDERAL_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF));
        assertTrue(errMap.getErrorCount() == 3);

        // most indicators shall be mutual exclusive
        errMap.clearErrorMessages();
        PaymentRequestTaxTabFixture.SW4_TREATY_GROSS_FOREIGN_USAID_OTHER.populate(preq);
        assertFalse(validation.validate(new AttributedDocumentEventBase("","", preq)));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_GROSS_UP_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_FOREIGN_SOURCE_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_OTHER_EXEMPT_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF));
        assertTrue(errMap.fieldHasMessage(pre+PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF));
    }
}

