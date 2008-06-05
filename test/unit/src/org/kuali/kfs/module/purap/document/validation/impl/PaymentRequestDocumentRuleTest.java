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

import static org.kuali.test.fixtures.UserNameFixture.APPLETON;

import java.sql.Date;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.ErrorMessage;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.fixtures.PaymentRequestInvoiceTabFixture;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.test.ConfigureContext;
import org.kuali.workflow.MockWorkflowDocument;

import edu.iu.uis.eden.exception.WorkflowException;


@ConfigureContext(session = APPLETON)
public class PaymentRequestDocumentRuleTest extends PurapRuleTestBase {

    PaymentRequestDocumentRule rule;
    PaymentRequestDocument preq;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preq = new PaymentRequestDocument();
        rule = new PaymentRequestDocumentRule();
    }

    @Override
    protected void tearDown() throws Exception {
        rule = null;
        preq = null;
        super.tearDown();
    }

    /*
     * Tests of processInvoiceValidation
     */
    public void testProcessInvoiceValidation_With_All() {
        preq = PaymentRequestInvoiceTabFixture.WITH_POID_WITH_DATE_WITH_NUMBER_WITH_AMOUNT.populate(preq);
        assertTrue(rule.processInvoiceValidation(preq));
    }

    public void testProcessInvoiceValidation_Without_PO_ID() {
        preq = PaymentRequestInvoiceTabFixture.NO_POID_WITH_DATE_WITH_NUMBER_WITH_AMOUNT.populate(preq);
        assertFalse(rule.processInvoiceValidation(preq));
    }

    public void testProcessInvoiceValidation_Without_Date() {
        preq = PaymentRequestInvoiceTabFixture.WITH_POID_NO_DATE_WITH_NUMBER_WITH_AMOUNT.populate(preq);
        assertFalse(rule.processInvoiceValidation(preq));
    }

    public void testProcessInvoiceValidation_Without_Number() {
        preq = PaymentRequestInvoiceTabFixture.WITH_POID_WITH_DATE_NO_NUMBER_WITH_AMOUNT.populate(preq);
        assertFalse(rule.processInvoiceValidation(preq));
    }

    public void testProcessInvoiceValidation_Without_Amount() {
        preq = PaymentRequestInvoiceTabFixture.WITH_POID_WITH_DATE_WITH_NUMBER_NO_AMOUNT.populate(preq);
        assertFalse(rule.processInvoiceValidation(preq));
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
        assertTrue(rule.processPaymentRequestDateValidationForContinue(preq));
    }

    public void testProcessPaymentRequestDateValidationForContinue_AfterToday() {
        preq = PaymentRequestInvoiceTabFixture.WITH_POID_WITH_DATE_WITH_NUMBER_WITH_AMOUNT.populate(preq);
        Date tomorrow = getDateFromOffsetFromToday(1);
        preq.setInvoiceDate(tomorrow);
        assertFalse(rule.processPaymentRequestDateValidationForContinue(preq));
    }

    public void testProcessPaymentRequestDateValidationForContinue_Today() {
        preq = PaymentRequestInvoiceTabFixture.WITH_POID_WITH_DATE_WITH_NUMBER_WITH_AMOUNT.populate(preq);
        Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        preq.setInvoiceDate(today);
        assertTrue(rule.processPaymentRequestDateValidationForContinue(preq));
    }

    public void testValidatePaymentRequestDates_PastAndInitiatedDocument() throws Exception {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        PaymentRequestService paymentRequestService = SpringContext.getBean(PaymentRequestService.class);
        
        // some date in the past
        Date yesterday = getDateFromOffsetFromToday(-1);
        
        assertTrue("Something is wrong with the test.  Error map was not empty before document saving called", GlobalVariables.getErrorMap().isEmpty());
        
        // rule 1: past pay dates are NOT allowed if the document has not been successfully saved or submitted yet
        PaymentRequestDocument document1 = (PaymentRequestDocument) documentService.getNewDocument(PaymentRequestDocument.class);
        document1.setPaymentRequestPayDate(yesterday);
        assertFalse(rule.validatePaymentRequestDates(document1));
        TypedArrayList l = (TypedArrayList) GlobalVariables.getErrorMap().get("document.paymentRequestPayDate");
        boolean correctError = false;
        for (Iterator i = l.iterator(); i.hasNext(); ) {
            ErrorMessage m = (ErrorMessage) i.next();
            if (PurapKeyConstants.ERROR_INVALID_PAY_DATE.equals(m.getErrorKey())) {
                correctError = true;
            }
        }
        assertTrue("Unable to find error message key 'errors.invalid.pay.date'", correctError);
        
        GlobalVariables.getErrorMap().clear();
    }
    
    public void testValidatePaymentRequestDates_PastAndPersistedDocument() throws Exception {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        
        final Date yesterday = getDateFromOffsetFromToday(-1);
        
        // rule 2: if a past paydate was already stored on the document in the database, the document may be saved with a past pay date only if
        // the pay date is the same date in the database (i.e. can NOT change from one past paydate to another)
        
        // we need to test the second part of the rule, but the testing framework makes this difficult because we have to first
        // store a previous document in the DB, so I'm creating a special rule implementation that retrieves a hard coded document
        PaymentRequestDocumentRule ruleWithPastPayDateDocument = new PaymentRequestDocumentRule() {
            protected PaymentRequestDocument retrievePaymentRequestDocumentFromDatabase(PaymentRequestDocument document) {
                PaymentRequestDocument temp = new PaymentRequestDocument();
                PaymentRequestInvoiceTabFixture.WITH_POID_WITH_DATE_WITH_NUMBER_WITH_AMOUNT.populate(temp);
                // set payment date to yesterday
                temp.setPaymentRequestPayDate(yesterday);
                return temp;
            }
        };
        
        // create a workflow document that simulates the document being enroute
        KualiWorkflowDocument workflowDocument = new MockWorkflowDocument() {
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
            public boolean stateIsInitiated() {
                return false;
            }

            @Override
            public boolean stateIsSaved() {
                return false;
            }

            @Override
            public boolean stateIsEnroute() {
                return true;
            }
            public void superUserActionRequestApprove(Long actionRequestId, String annotation) throws WorkflowException {
            }

            public void superUserCancel(String annotation) throws WorkflowException {
            }

            public void superUserDisapprove(String annotation) throws WorkflowException {
            }

            public boolean userIsRoutedByUser(UniversalUser user) {
                return false;
            }

            public Set<UniversalUser> getAllPriorApprovers() throws WorkflowException, UserNotFoundException {
                return null;
            }
        };
        
        PaymentRequestDocument document2 = (PaymentRequestDocument) documentService.getNewDocument(PaymentRequestDocument.class);
        document2.getDocumentHeader().setWorkflowDocument(workflowDocument);
        document2.setPaymentRequestPayDate(yesterday);
        assertTrue("Didn't change past pay date, so doucment should validate successfully.", ruleWithPastPayDateDocument.validatePaymentRequestDates(document2) );
        assertTrue("Error map should be empty", GlobalVariables.getErrorMap().isEmpty());
        
        document2.setPaymentRequestPayDate(getDateFromOffsetFromToday(-2));
        assertFalse("changed past pay date to another past pay date, so document should fail.", ruleWithPastPayDateDocument.validatePaymentRequestDates(document2) );
        assertFalse("Error map should not be empty", GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();
        
        document2.setPaymentRequestPayDate(getDateFromOffsetFromToday(3));
        assertTrue("Changed past pay date to future, so doucment should validate successfully.", ruleWithPastPayDateDocument.validatePaymentRequestDates(document2) );
        assertTrue("Error map should be empty", GlobalVariables.getErrorMap().isEmpty());
        
    }

    public void testValidatePaymentRequestDates_FutureAndPersistedDocument() throws Exception {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        
        final Date tomorrow = getDateFromOffsetFromToday(1);
        
        // rule 3: if a future paydate was already stored on the document in the database, the document may store any future pay date, but no past pay dates
        
        // we need to test the second part of the rule, but the testing framework makes this difficult because we have to first
        // store a previous document in the DB, so I'm creating a special rule implementation that retrieves a hard coded document
        PaymentRequestDocumentRule ruleWithPastPayDateDocument = new PaymentRequestDocumentRule() {
            protected PaymentRequestDocument retrievePaymentRequestDocumentFromDatabase(PaymentRequestDocument document) {
                PaymentRequestDocument temp = new PaymentRequestDocument();
                PaymentRequestInvoiceTabFixture.WITH_POID_WITH_DATE_WITH_NUMBER_WITH_AMOUNT.populate(temp);
                // set payment date to yesterday
                temp.setPaymentRequestPayDate(tomorrow);
                return temp;
            }
        };
        
        // create a workflow document that simulates the document being enroute
        KualiWorkflowDocument workflowDocument = new MockWorkflowDocument() {
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
            public boolean stateIsInitiated() {
                return false;
            }

            @Override
            public boolean stateIsSaved() {
                return false;
            }

            @Override
            public boolean stateIsEnroute() {
                return true;
            }
            public void superUserActionRequestApprove(Long actionRequestId, String annotation) throws WorkflowException {
            }

            public void superUserCancel(String annotation) throws WorkflowException {
            }

            public void superUserDisapprove(String annotation) throws WorkflowException {
            }

            public boolean userIsRoutedByUser(UniversalUser user) {
                return false;
            }
            
            public Set<UniversalUser> getAllPriorApprovers() throws WorkflowException, UserNotFoundException {
                return null;
            }
        };
        
        PaymentRequestDocument document2 = (PaymentRequestDocument) documentService.getNewDocument(PaymentRequestDocument.class);
        document2.getDocumentHeader().setWorkflowDocument(workflowDocument);
        document2.setPaymentRequestPayDate(tomorrow);
        assertTrue("Didn't change future pay date, so doucment should validate successfully.", ruleWithPastPayDateDocument.validatePaymentRequestDates(document2) );
        assertTrue("Error map should be empty", GlobalVariables.getErrorMap().isEmpty());
        
        document2.setPaymentRequestPayDate(getDateFromOffsetFromToday(-2));
        assertFalse("changed future pay date to  past pay date, so document should fail.", ruleWithPastPayDateDocument.validatePaymentRequestDates(document2) );
        assertFalse("Error map should not be empty", GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();
        
        document2.setPaymentRequestPayDate(getDateFromOffsetFromToday(3));
        assertTrue("Changed future pay date to another future date, so doucment should validate successfully.", ruleWithPastPayDateDocument.validatePaymentRequestDates(document2) );
        assertTrue("Error map should be empty", GlobalVariables.getErrorMap().isEmpty());
    }
    
    public void testValidatePaymentRequestDates_Today() {
        Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        preq.setPaymentRequestPayDate(today);
        assertTrue(rule.validatePaymentRequestDates(preq));
    }

    public void testValidatePaymentRequestDates_Tomorrow() {
        Date tomorrow = getDateFromOffsetFromToday(1);
        preq.setPaymentRequestPayDate(tomorrow);
        assertTrue(rule.validatePaymentRequestDates(preq));
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

}
