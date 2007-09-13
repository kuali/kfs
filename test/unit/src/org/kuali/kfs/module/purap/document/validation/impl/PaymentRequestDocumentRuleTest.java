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

import java.sql.Date;
import java.util.Calendar;

import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.fixtures.PaymentRequestInvoiceTabFixture;
import org.kuali.test.ConfigureContext;

@ConfigureContext(session = KHUNTLEY)
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
        preq = PaymentRequestInvoiceTabFixture.WITH_POID_WITH_DATE_WITH_NUMBER_WITH_AMOUNT.populate( preq );
        assertTrue(rule.processInvoiceValidation(preq));
    }
    
    public void testProcessInvoiceValidation_Without_PO_ID() {
        preq = PaymentRequestInvoiceTabFixture.NO_POID_WITH_DATE_WITH_NUMBER_WITH_AMOUNT.populate( preq );
        assertFalse(rule.processInvoiceValidation(preq));
    }
    
    public void testProcessInvoiceValidation_Without_Date() {
        preq = PaymentRequestInvoiceTabFixture.WITH_POID_NO_DATE_WITH_NUMBER_WITH_AMOUNT.populate( preq );
        assertFalse(rule.processInvoiceValidation(preq));
    }
    
    public void testProcessInvoiceValidation_Without_Number() {
        preq = PaymentRequestInvoiceTabFixture.WITH_POID_WITH_DATE_NO_NUMBER_WITH_AMOUNT.populate( preq );
        assertFalse(rule.processInvoiceValidation(preq));
    }
    
    public void testProcessInvoiceValidation_Without_Amount() {
        preq = PaymentRequestInvoiceTabFixture.WITH_POID_WITH_DATE_WITH_NUMBER_NO_AMOUNT.populate( preq );
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
    private Date getDateFromOffsetFromToday(int offsetDays){
        Calendar calendar = SpringContext.getBean(DateTimeService.class).getCurrentCalendar();
        calendar.add(Calendar.DATE,offsetDays);
        return new Date(calendar.getTimeInMillis());
    }
    
    public void testProcessPaymentRequestDateValidationForContinue_BeforeToday() {
        preq = PaymentRequestInvoiceTabFixture.WITH_POID_WITH_DATE_WITH_NUMBER_WITH_AMOUNT.populate( preq );
        Date yesterday = getDateFromOffsetFromToday(-1);
        preq.setInvoiceDate(yesterday);
        assertTrue(rule.processPaymentRequestDateValidationForContinue(preq));
    }
    
    public void testProcessPaymentRequestDateValidationForContinue_AfterToday() {
        preq = PaymentRequestInvoiceTabFixture.WITH_POID_WITH_DATE_WITH_NUMBER_WITH_AMOUNT.populate( preq );
        Date tomorrow = getDateFromOffsetFromToday(1);
        preq.setInvoiceDate(tomorrow);
        assertFalse(rule.processPaymentRequestDateValidationForContinue(preq));
    }
    
    public void testProcessPaymentRequestDateValidationForContinue_Today() {
        preq = PaymentRequestInvoiceTabFixture.WITH_POID_WITH_DATE_WITH_NUMBER_WITH_AMOUNT.populate( preq );
        Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        preq.setInvoiceDate(today);
        assertTrue(rule.processPaymentRequestDateValidationForContinue(preq));
    }
    
    /* 
     * Tests of validatePaymentRequestDates
     */
    public void testValidatePaymentRequestDates_Yesterday() {
        Date yesterday = getDateFromOffsetFromToday(-1);
        preq.setPaymentRequestPayDate(yesterday);
        assertFalse(rule.validatePaymentRequestDates(preq));
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
