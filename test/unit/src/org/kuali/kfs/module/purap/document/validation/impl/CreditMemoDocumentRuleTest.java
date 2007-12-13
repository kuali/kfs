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

import org.kuali.core.service.DocumentService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.document.AccountingDocumentTestUtils;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PaymentRequestDocumentTest;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchaseOrderDocumentTest;
import org.kuali.module.purap.fixtures.CreditMemoInitTabFixture;
import org.kuali.module.purap.fixtures.PaymentRequestDocumentFixture;
import org.kuali.module.purap.fixtures.PurchaseOrderDocumentFixture;
import org.kuali.module.purap.service.PurapService;
import org.kuali.test.ConfigureContext;
import org.kuali.module.purap.fixtures.PurapTestConstants;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.service.VendorService;
import org.kuali.module.vendor.util.VendorUtils;

@ConfigureContext(session = KHUNTLEY)
public class CreditMemoDocumentRuleTest extends PurapRuleTestBase {

    CreditMemoDocument creditMemo;
    CreditMemoDocumentRule rule;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        creditMemo = new CreditMemoDocument();
        rule = new CreditMemoDocumentRule();
    }

    @Override
    protected void tearDown() throws Exception {
        rule = null;
        creditMemo = null;
        super.tearDown();
    }
        
    /*
     * Tests of validateInitTabRequiredFields
     */
    /*
    public void testValidateInitTabRequiredFields_WithInvoiceWithTomorrowWithAmount() {
        creditMemo = CreditMemoInitTabFixture.WITH_INVOICE_WITH_DATE_WITH_AMOUNT.populateForRequiredness(creditMemo);
        Date tomorrow = SpringContext.getBean(PurapService.class).getDateFromOffsetFromToday(1);
        creditMemo.setCreditMemoDate(tomorrow);
        assertTrue(rule.validateInitTabRequiredFields(creditMemo));
    }
    
    public void testValidateInitTabRequiredFields_WithInvoiceWithTodayWithAmount() {
        creditMemo = CreditMemoInitTabFixture.WITH_INVOICE_WITH_DATE_WITH_AMOUNT.populateForRequiredness(creditMemo);
        Date today = SpringContext.getBean(PurapService.class).getDateFromOffsetFromToday(0);
        creditMemo.setCreditMemoDate(today);
        assertFalse(rule.validateInitTabRequiredFields(creditMemo));
    }
    
    public void testValidateInitTabRequiredFields_WithInvoiceWithYesterdayWithAmount() {
        creditMemo = CreditMemoInitTabFixture.WITH_INVOICE_WITH_DATE_WITH_AMOUNT.populateForRequiredness(creditMemo);
        Date yesterday = SpringContext.getBean(PurapService.class).getDateFromOffsetFromToday(-1);
        creditMemo.setCreditMemoDate(yesterday);
        assertFalse(rule.validateInitTabRequiredFields(creditMemo));
    }
    */
    
    public void testValidateInitTabRequiredFields_NoInvoiceWithTomorrowWithAmount() {
        creditMemo = CreditMemoInitTabFixture.NO_INVOICE_WITH_DATE_WITH_AMOUNT.populateForRequiredness(creditMemo);
        Date tomorrow = SpringContext.getBean(PurapService.class).getDateFromOffsetFromToday(1);
        creditMemo.setCreditMemoDate(tomorrow);
        assertFalse(rule.validateInitTabRequiredFields(creditMemo));
    }
    
    /*
    public void testValidateInitTabRequiredFields_WithInvoiceNoDateWithAmount() {
        creditMemo = CreditMemoInitTabFixture.WITH_INVOICE_NO_DATE_WITH_AMOUNT.populateForRequiredness(creditMemo);
        assertFalse(rule.validateInitTabRequiredFields(creditMemo));
    }
    */
    
    public void testValidateInitTabRequiredFields_WithInvoiceWithTomorrowNoAmount() {
        creditMemo = CreditMemoInitTabFixture.WITH_INVOICE_WITH_DATE_NO_AMOUNT.populateForRequiredness(creditMemo);
        Date tomorrow = SpringContext.getBean(PurapService.class).getDateFromOffsetFromToday(1);
        creditMemo.setCreditMemoDate(tomorrow);
        assertFalse(rule.validateInitTabRequiredFields(creditMemo));
    }
    
    /*
     *  Tests of validateInitTabReferenceNumbers.  These should pass if there is exactly one document of one of the three
     *  types associated with it.
     */
    /**
     * Save a PO to get one with a valid poID in the database.
     * 
     * @return A valid Integer PurchaseOrderIdentifier
     * @throws Exception
     */
    private Integer prepareAndSavePO() throws Exception {
        PurchaseOrderDocumentTest poDocTest = new PurchaseOrderDocumentTest();
        PurchaseOrderDocument po = poDocTest.buildSimpleDocument();       
        po.prepareForSave();
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        AccountingDocumentTestUtils.saveDocument(po, documentService);
        PurchaseOrderDocument poWithID = (PurchaseOrderDocument)documentService.getByDocumentHeaderId(po.getDocumentNumber());
        return poWithID.getPurapDocumentIdentifier();
    }
    
    /**
     * Save a PREQ to get one with a valid preqID in the database.
     * 
     * @return  A valid Integer PaymentRequestIdentifier
     * @throws Exception
     */
    private Integer prepareAndSavePREQ() throws Exception {
        PaymentRequestDocumentTest preqDocTest = new PaymentRequestDocumentTest();        
        PaymentRequestDocument preq = preqDocTest.createPaymentRequestDocument(
                PaymentRequestDocumentFixture.PREQ_APPROVAL_REQUIRED, 
                preqDocTest.createPurchaseOrderDocument(PurchaseOrderDocumentFixture.PO_APPROVAL_REQUIRED,false), 
                true, new KualiDecimal[] {new KualiDecimal(100)});        
        return preq.getPurapDocumentIdentifier();
    }
   
    /*
    public void testValidateInitTabReferenceNumbers_WithPOIDNoPREQIDNoVendorNum() throws Exception {
        // Get the poID of a PO that is validly in the database.
        Integer poID = prepareAndSavePO();
        creditMemo.setPurchaseOrderIdentifier(poID);
        assertTrue(rule.validateInitTabReferenceNumbers(creditMemo));
    }
    */
    
    public void testValidateInitTabReferenceNumbers_NoPOIDWithPREQIDNoVendorNum() throws Exception{
        // Get the preqId of a PREQ that is validly in the database.
        Integer preqID = prepareAndSavePREQ();
        creditMemo.setPaymentRequestIdentifier(preqID);
        assertTrue(rule.validateInitTabReferenceNumbers(creditMemo));
    }
    
    /**
     * This happy-path test won't fail if there is no such vendor as the one from the fixture.
     * If there is such a vendor in the database, this test whether a creditMemo associated with
     * that vendor number alone will pass the validateInitTabReferenceNumbers validation method.
     */
    public void testValidateInitTabReferenceNumbers_NoPOIDNoPREQIdWithVendorNum() {
        creditMemo = CreditMemoInitTabFixture.WITH_VENDOR_NUMBER.populateForReferenceNumbers(creditMemo);
        String vendorNumber = creditMemo.getVendorNumber();
        // If there is such a vendor in the database, we can proceed to test this.
        VendorDetail vendor = SpringContext.getBean(VendorService.class).getVendorDetail(
                VendorUtils.getVendorHeaderId(vendorNumber), VendorUtils.getVendorDetailId(vendorNumber));
        if(org.kuali.core.util.ObjectUtils.isNotNull(vendor)) {
            assertTrue(rule.validateInitTabReferenceNumbers(creditMemo));
        }
        else {
            assertTrue(true);
        }
    }
    
    public void testValidateInitTabReferenceNumbers_NoPOIDNoPREQIdNoVendorNum() {
        assertFalse(rule.validateInitTabReferenceNumbers(creditMemo));
    }
    
    public void testValidateInitTabReferenceNumbers_WithPOIdNoPREQIdWithVendorNum() throws Exception {
        creditMemo = CreditMemoInitTabFixture.WITH_VENDOR_NUMBER.populateForReferenceNumbers(creditMemo);
        String vendorNumber = creditMemo.getVendorNumber();
        // If there is such a vendor in the database, we can proceed to test this.
        VendorDetail vendor = SpringContext.getBean(VendorService.class).getVendorDetail(
                VendorUtils.getVendorHeaderId(vendorNumber), VendorUtils.getVendorDetailId(vendorNumber));
        if(org.kuali.core.util.ObjectUtils.isNotNull(vendor)) {
            Integer poID = prepareAndSavePO();
            creditMemo.setPurchaseOrderIdentifier(poID);
            assertFalse(rule.validateInitTabReferenceNumbers(creditMemo));
        }
        else {
            assertTrue(true);
        }      
    }
    
    public void testValidateInitTabReferenceNumbers_NoPOIdWithPREQIdWithVendorNum() throws Exception {
        creditMemo = CreditMemoInitTabFixture.WITH_VENDOR_NUMBER.populateForReferenceNumbers(creditMemo);
        String vendorNumber = creditMemo.getVendorNumber();
        // If there is such a vendor in the database, we can proceed to test this.
        VendorDetail vendor = SpringContext.getBean(VendorService.class).getVendorDetail(
                VendorUtils.getVendorHeaderId(vendorNumber), VendorUtils.getVendorDetailId(vendorNumber));
        if(org.kuali.core.util.ObjectUtils.isNotNull(vendor)) {
            Integer preqID = prepareAndSavePREQ();
            creditMemo.setPaymentRequestIdentifier(preqID);
            assertFalse(rule.validateInitTabReferenceNumbers(creditMemo));
        }
        else {
            assertTrue(true);
        }      
    }
    
    public void testValidateInitTabReferenceNumbers_WithPOIdWithPREQIdNoVendorNum() throws Exception {
        Integer poID = prepareAndSavePO();
        creditMemo.setPurchaseOrderIdentifier(poID);
        Integer preqID = prepareAndSavePREQ();
        creditMemo.setPaymentRequestIdentifier(preqID);
        assertFalse(rule.validateInitTabReferenceNumbers(creditMemo));
    }
    
    // Tests of validateItemQuantity
    
    // Tests of validateItemUnitPrice
    
    // Tests of validateItemExtendedPrice
    
    // Tests of checkPurchaseOrdersForInvoicedItems
    
    // Tests of validateTotalMatchesVendorAmount
    
    // Tests of validateTotalOverZero
    
    // Tests of validateObjectCode
    
    // Tests of verifyAccountingStringsBetween0And100Percent
}
