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

import java.math.BigDecimal;
import java.sql.Date;

import org.kuali.core.service.DocumentService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.document.AccountingDocumentTestUtils;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.module.purap.bo.CreditMemoItem;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.CreditMemoDocumentTest;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PaymentRequestDocumentTest;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchaseOrderDocumentTest;
import org.kuali.module.purap.fixtures.CreditMemoInitTabFixture;
import org.kuali.module.purap.fixtures.PaymentRequestDocumentFixture;
import org.kuali.module.purap.fixtures.PurApAccountingLineFixture;
import org.kuali.module.purap.fixtures.PurchaseOrderAccountingLineFixture;
import org.kuali.module.purap.fixtures.PurchaseOrderDocumentFixture;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.service.VendorService;
import org.kuali.module.vendor.util.VendorUtils;
import org.kuali.test.ConfigureContext;
import org.kuali.test.fixtures.AccountingLineFixture;

@ConfigureContext(session = KHUNTLEY)
public class CreditMemoDocumentRuleTest extends PurapRuleTestBase {

    CreditMemoDocument creditMemo;
    CreditMemoDocumentRule rule;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        CreditMemoDocumentTest cmDocTest = new CreditMemoDocumentTest();
        creditMemo = cmDocTest.buildSimpleDocument();
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

    /**
     * Happy path: Date is Today
     */
    public void testValidateInitTabRequiredFields_WithInvoiceWithTodayWithAmount() {
        creditMemo = CreditMemoInitTabFixture.WITH_INVOICE_WITH_DATE_WITH_AMOUNT.populateForRequiredness(creditMemo);
        Date today = SpringContext.getBean(PurapService.class).getDateFromOffsetFromToday(0);
        creditMemo.setCreditMemoDate(today);
        assertTrue(rule.validateInitTabRequiredFields(creditMemo));
    }
    
    /**
     * Happy path: Date is in past
     */
    public void testValidateInitTabRequiredFields_WithInvoiceWithYesterdayWithAmount() {
        creditMemo = CreditMemoInitTabFixture.WITH_INVOICE_WITH_DATE_WITH_AMOUNT.populateForRequiredness(creditMemo);
        Date yesterday = SpringContext.getBean(PurapService.class).getDateFromOffsetFromToday(-1);
        creditMemo.setCreditMemoDate(yesterday);
        assertTrue(rule.validateInitTabRequiredFields(creditMemo));
    }
    
    /**
     * Date should be today or in past, not in future.
     */ 
    public void testValidateInitTabRequiredFields_WithInvoiceWithTomorrowWithAmount() {
        creditMemo = CreditMemoInitTabFixture.WITH_INVOICE_WITH_DATE_WITH_AMOUNT.populateForRequiredness(creditMemo);
        Date tomorrow = SpringContext.getBean(PurapService.class).getDateFromOffsetFromToday(1);
        creditMemo.setCreditMemoDate(tomorrow);
        assertFalse(rule.validateInitTabRequiredFields(creditMemo));
    }
    
    /**
     * Invoice should be present and is not.
     */
    public void testValidateInitTabRequiredFields_NoInvoiceWithTomorrowWithAmount() {
        creditMemo = CreditMemoInitTabFixture.NO_INVOICE_WITH_DATE_WITH_AMOUNT.populateForRequiredness(creditMemo);
        Date tomorrow = SpringContext.getBean(PurapService.class).getDateFromOffsetFromToday(1);
        creditMemo.setCreditMemoDate(tomorrow);
        assertFalse(rule.validateInitTabRequiredFields(creditMemo));
    }
    
    
    /**
     * Date should be present and is not.
     */
    /*
    public void testValidateInitTabRequiredFields_WithInvoiceNoDateWithAmount() {
        creditMemo = CreditMemoInitTabFixture.WITH_INVOICE_NO_DATE_WITH_AMOUNT.populateForRequiredness(creditMemo);
        assertFalse(rule.validateInitTabRequiredFields(creditMemo));
    }
    */
    
    /**
     * Amount should be present and is not.
     */
    public void testValidateInitTabRequiredFields_WithInvoiceWithTomorrowNoAmount() {
        creditMemo = CreditMemoInitTabFixture.WITH_INVOICE_WITH_DATE_NO_AMOUNT.populateForRequiredness(creditMemo);
        Date tomorrow = SpringContext.getBean(PurapService.class).getDateFromOffsetFromToday(1);
        creditMemo.setCreditMemoDate(tomorrow);
        assertFalse(rule.validateInitTabRequiredFields(creditMemo));
    }
    
    /*
     * Utility helper methods
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
        po.setStatusCode(PurchaseOrderStatuses.OPEN);
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
     *  Tests of validateInitTabReferenceNumbers.  These should pass if there is exactly one document of one of the three
     *  types associated with it.
     */
   
    /**
     * First happy path: CM is derived from Purchase Order.
     * @throws Exception
     */
    public void testValidateInitTabReferenceNumbers_WithPOIDNoPREQIDNoVendorNum() throws Exception {
        // Get the poID of a PO that is validly in the database.
        Integer poID = prepareAndSavePO();
        creditMemo.setPurchaseOrderIdentifier(poID);
        assertTrue(rule.validateInitTabReferenceNumbers(creditMemo));
    }
    
    /**
     * Second happy path: CM is derived from Payment Request.
     * @throws Exception
     */
    public void testValidateInitTabReferenceNumbers_NoPOIDWithPREQIDNoVendorNum() throws Exception{
        // Get the preqId of a PREQ that is validly in the database.
        Integer preqID = prepareAndSavePREQ();
        creditMemo.setPaymentRequestIdentifier(preqID);
        assertTrue(rule.validateInitTabReferenceNumbers(creditMemo));
    }
    
    /**
     * This happy-path test won't fail if there is no such vendor as the one from the fixture.
     * If there is such a vendor in the database, this tests whether a creditMemo associated with
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
    
    /**
     * Should fail with no reference numbers; CM not created from any documents.
     */
    public void testValidateInitTabReferenceNumbers_NoPOIDNoPREQIdNoVendorNum() {
        assertFalse(rule.validateInitTabReferenceNumbers(creditMemo));
    }
    
    /**
     * Should fail since there are both Purchase Order and Vendor reference numbers.
     * @throws Exception
     */
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
    
    /**
     * Should fail since there are both Payment Request and Vendor reference numbers.
     * @throws Exception
     */
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
    
    /**
     * Should fail since there are both Purchase Order and Payment Request reference numbers.
     * @throws Exception
     */
    public void testValidateInitTabReferenceNumbers_WithPOIdWithPREQIdNoVendorNum() throws Exception {
        Integer poID = prepareAndSavePO();
        creditMemo.setPurchaseOrderIdentifier(poID);
        Integer preqID = prepareAndSavePREQ();
        creditMemo.setPaymentRequestIdentifier(preqID);
        assertFalse(rule.validateInitTabReferenceNumbers(creditMemo));
    }
    
    /*
     * Tests of validateItemQuantity
     */
    
    /**
     * Happy path for PO-type Credit Memo.  The Item Quantity is the same as the Invoiced Quantity.
     */
    public void testValidateItemQuantity_POType_EquivalentQuantity() {
        creditMemo.setPurchaseOrderIdentifier(new Integer(99999));
        CreditMemoItem item = (CreditMemoItem)creditMemo.getItemByLineNumber(1);
        item.setPoInvoicedTotalQuantity(new KualiDecimal(1));
        item.setItemQuantity(new KualiDecimal(1));
        String errorKeyPrefix = KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM + "[" + Integer.toString(1) + "].";
        assertTrue(rule.validateItemQuantity(creditMemo,item,errorKeyPrefix + PurapPropertyConstants.QUANTITY));
    }
    
    /**
     * Happy path for PREQ-type Credit Memo.  The Item Quantity is the same as the Invoiced Quantity.
     */
    public void testValidateItemQuantity_PREQType_EquivalentQuantity() {
        creditMemo.setPaymentRequestIdentifier(new Integer(9999));
        CreditMemoItem item = (CreditMemoItem)creditMemo.getItemByLineNumber(1);
        item.setPreqInvoicedTotalQuantity(new KualiDecimal(1));
        item.setItemQuantity(new KualiDecimal(1));
        String errorKeyPrefix = KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM + "[" + Integer.toString(1) + "].";       
        assertTrue(rule.validateItemQuantity(creditMemo,item,errorKeyPrefix + PurapPropertyConstants.QUANTITY));
    }
    
    /**
     * The conditions are met to require a quantity, but there is no quantity: PO-Type.
     */
    public void testValidateItemQuantity_POType_NullQuantity() {
        creditMemo.setPurchaseOrderIdentifier(new Integer(99999));
        CreditMemoItem item = (CreditMemoItem)creditMemo.getItemByLineNumber(1);
        item.setPoInvoicedTotalQuantity(new KualiDecimal(1));
        item.setItemQuantity(null);
        String errorKeyPrefix = KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM + "[" + Integer.toString(1) + "].";
        assertFalse(rule.validateItemQuantity(creditMemo,item,errorKeyPrefix + PurapPropertyConstants.QUANTITY));
    }
 
    /**
     * The conditions are met to require a quantity, but there is no quantity: PREQ-Type.
     */
    public void testValidateItemQuantity_PREQType_NullQuantity() {
        creditMemo.setPaymentRequestIdentifier(new Integer(9999));
        CreditMemoItem item = (CreditMemoItem)creditMemo.getItemByLineNumber(1);
        item.setPreqInvoicedTotalQuantity(new KualiDecimal(1));
        item.setItemQuantity(null);
        String errorKeyPrefix = KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM + "[" + Integer.toString(1) + "].";
        assertFalse(rule.validateItemQuantity(creditMemo,item,errorKeyPrefix + PurapPropertyConstants.QUANTITY));
    }    
    
    /**
     * Quantities cannot be negative: PO-Type.
     */
    public void testValidateItemQuantity_POType_NegativeQuantity() {
        creditMemo.setPurchaseOrderIdentifier(new Integer(99999));
        CreditMemoItem item = (CreditMemoItem)creditMemo.getItemByLineNumber(1);
        item.setPoInvoicedTotalQuantity(new KualiDecimal(1));
        item.setItemQuantity(new KualiDecimal(-1));
        String errorKeyPrefix = KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM + "[" + Integer.toString(1) + "].";
        assertFalse(rule.validateItemQuantity(creditMemo,item,errorKeyPrefix + PurapPropertyConstants.QUANTITY));
    }
    
    /**
     * Quantities cannot be negative: PREQ-Type
     */
    public void testValidateItemQuantity_PREQType_NegativeQuantity() {
        creditMemo.setPaymentRequestIdentifier(new Integer(9999));
        CreditMemoItem item = (CreditMemoItem)creditMemo.getItemByLineNumber(1);
        item.setPreqInvoicedTotalQuantity(new KualiDecimal(1));
        item.setItemQuantity(new KualiDecimal(-1));
        String errorKeyPrefix = KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM + "[" + Integer.toString(1) + "].";
        assertFalse(rule.validateItemQuantity(creditMemo,item,errorKeyPrefix + PurapPropertyConstants.QUANTITY));
    }
    
    /**
     * Quantity for the item should not be greater than the invoiced quantity: PO-Type
     */
    public void testValidateItemQuantity_POType_GreaterQuantity() {
        creditMemo.setPurchaseOrderIdentifier(new Integer(99999));
        CreditMemoItem item = (CreditMemoItem)creditMemo.getItemByLineNumber(1);
        item.setPoInvoicedTotalQuantity(new KualiDecimal(1));
        item.setItemQuantity(new KualiDecimal(2));
        String errorKeyPrefix = KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM + "[" + Integer.toString(1) + "].";
        assertFalse(rule.validateItemQuantity(creditMemo,item,errorKeyPrefix + PurapPropertyConstants.QUANTITY));
    }
    
    /**
     * Quantity for the item should not be greater than the invoiced quantity: PREQ-Type
     */
    public void testValidateItemQuantity_PREQType_GreaterQuantity() {
        creditMemo.setPaymentRequestIdentifier(new Integer(9999));
        CreditMemoItem item = (CreditMemoItem)creditMemo.getItemByLineNumber(1);
        item.setPreqInvoicedTotalQuantity(new KualiDecimal(1));
        item.setItemQuantity(new KualiDecimal(2));
        String errorKeyPrefix = KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM + "[" + Integer.toString(1) + "].";
        assertFalse(rule.validateItemQuantity(creditMemo,item,errorKeyPrefix + PurapPropertyConstants.QUANTITY));
    }   
    
    // Tests of validateItemUnitPrice
    
    // Tests of validateItemExtendedPrice
       
    /*
     * Tests of checkPurchaseOrdersForInvoicedItems
     */
    
    /**
     * Save a PO with the given attributes.  The last two parameters should be set together.
     * 
     * @param   itemInvoicedTotalQuantity           KualiDecimal
     * @param   itemOutstandingEncumberedAmount     KualiDecimal
     * @param   itemUnitPrice                       BigDecimal
     * @return A valid Integer PurchaseOrderIdentifier
     * @throws Exception
     */
    private Integer prepareAndSavePOWithChanges(KualiDecimal itemInvoicedTotalQuantity,
            KualiDecimal itemOutstandingEncumberedAmount,
            BigDecimal itemUnitPrice) throws Exception {
        PurchaseOrderDocumentTest poDocTest = new PurchaseOrderDocumentTest();
        PurchaseOrderDocument po = poDocTest.buildSimpleDocument();
        
        PurchaseOrderItem poItem = (PurchaseOrderItem)po.getItemByLineNumber(1);
        poItem.setItemInvoicedTotalQuantity(itemInvoicedTotalQuantity);
        poItem.setItemUnitPrice(itemUnitPrice);
        poItem.setItemOutstandingEncumberedAmount(itemOutstandingEncumberedAmount);
        
        po.prepareForSave();
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        AccountingDocumentTestUtils.saveDocument(po, documentService);
        PurchaseOrderDocument poWithID = (PurchaseOrderDocument)documentService.getByDocumentHeaderId(po.getDocumentNumber());
        return poWithID.getPurapDocumentIdentifier();
    }
    
    /**
     * Happy path 1: The PO has one Invoiced Item, and itemInvoicedTotalQuantity has been set.
     * @throws Exception
     */
    /*
    public void testCheckPurchaseOrdersForInvoicedItems_WithPositiveInvoicedQuantity() throws Exception {
        Integer poId = prepareAndSavePOWithChanges(new KualiDecimal(1),null,null);
        creditMemo.setPurchaseOrderIdentifier(poId);
        assertTrue(rule.checkPurchaseOrderForInvoicedItems(creditMemo));
    }
    */
    
    /**
     * Happy path 2: The PO has one Invoiced Item, and itemUnitPrice has been set to greater than itemOutstandingEncumberedAmount.
     * @throws Exception
     */
    /*
    public void testCheckPurchaseOrdersForInvoicedItems_WithUnitPriceGreaterThanOutstandingEncumberedAmount() throws Exception {
        Integer poId = prepareAndSavePOWithChanges(null,new KualiDecimal(0.5),new BigDecimal(1));
        creditMemo.setPurchaseOrderIdentifier(poId);
        assertTrue(rule.checkPurchaseOrderForInvoicedItems(creditMemo));
    }
    */
    public void testCheckPurchaseOrdersForInvoicedItems_NullChanges() throws Exception {
        Integer poId = prepareAndSavePOWithChanges(null,null,null);
        creditMemo.setPurchaseOrderIdentifier(poId);
        assertFalse(rule.checkPurchaseOrderForInvoicedItems(creditMemo));
    }
    
    public void testCheckPurchaseOrdersForInvoicedItems_NullOutstandingEncumberedAmount() throws Exception {
        Integer poId = prepareAndSavePOWithChanges(null,null,new BigDecimal(1));
        creditMemo.setPurchaseOrderIdentifier(poId);
        assertFalse(rule.checkPurchaseOrderForInvoicedItems(creditMemo));
    }
    
    public void testCheckPurchaseOrdersForInvoicedItems_NullUnitPrice() throws Exception {
        Integer poId = prepareAndSavePOWithChanges(null,new KualiDecimal(0.5),null);
        creditMemo.setPurchaseOrderIdentifier(poId);
        assertFalse(rule.checkPurchaseOrderForInvoicedItems(creditMemo));
    }
    
    public void testCheckPurchaseOrdersForInvoicedItems_WithUnitPriceLessThanOutstandingEncumberedAmount() throws Exception {
        Integer poId = prepareAndSavePOWithChanges(null,new KualiDecimal(1),new BigDecimal(0.5));
        creditMemo.setPurchaseOrderIdentifier(poId);
        assertFalse(rule.checkPurchaseOrderForInvoicedItems(creditMemo));
    }
    
    public void testCheckPurchaseOrdersForInvoicedItems_WithUnitPriceEqualToOutstandingEncumberedAmount() throws Exception {
        Integer poId = prepareAndSavePOWithChanges(null,new KualiDecimal(1),new BigDecimal(1));
        creditMemo.setPurchaseOrderIdentifier(poId);
        assertFalse(rule.checkPurchaseOrderForInvoicedItems(creditMemo));
    }
    
    /*
     * Tests of validateTotalMatchesVendorAmount
     */
    /*
    public void testValidateTotalMatchesVendorAmount_EqualAmounts() {
        creditMemo = CreditMemoInitTabFixture.HI_AMOUNT_HI_TOTAL.populateForAmounts(creditMemo);
        assertTrue(rule.validateTotalMatchesVendorAmount(creditMemo));
    }
    */
    
    public void testValidateTotalMatchesVendorAmount_GrandTotalGreaterThanAmount() {
        creditMemo = CreditMemoInitTabFixture.LO_AMOUNT_HI_TOTAL.populateForAmounts(creditMemo);
        assertFalse(rule.validateTotalMatchesVendorAmount(creditMemo));
    }
    
    public void testValidateTotalMatchesVendorAmount_AmountGreaterThanGrandTotal() {
        creditMemo = CreditMemoInitTabFixture.HI_AMOUNT_LO_TOTAL.populateForAmounts(creditMemo);
        assertFalse(rule.validateTotalMatchesVendorAmount(creditMemo));
    }
        
    /*
     * Tests of validateTotalOverZero
     */
    public void testValidateTotalOverZero_PositiveTotal() {
        creditMemo = CreditMemoInitTabFixture.HI_AMOUNT_HI_TOTAL.populateForAmounts(creditMemo);
        assertTrue(rule.validateTotalOverZero(creditMemo));
    }
    
    /*
    public void testValidateTotalOverZero_ZeroTotal() {
        creditMemo = CreditMemoInitTabFixture.HI_AMOUNT_ZERO_TOTAL.populateForAmounts(creditMemo);
        assertFalse(rule.validateTotalOverZero(creditMemo));
    }
    */
    
    /*
     * Tests of validateObjectCode
     */
    public void testValidateObjectCode_Happy() {
        PurApAccountingLine accountingLine = PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1.createPurApAccountingLine(
                CreditMemoDocument.class,
                PurApAccountingLineFixture.BASIC_ACCOUNT_1,
                AccountingLineFixture.LINE6);
        assertTrue(rule.validateObjectCode(creditMemo, accountingLine));
    }
    
    /*
    @SuppressWarnings("deprecation")
    public void testValidateObjectCode_BadObjectCode() {
        PurApAccountingLine accountingLine = PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1.createPurApAccountingLine(
                CreditMemoDocument.class,
                PurApAccountingLineFixture.BASIC_ACCOUNT_1,
                AccountingLineFixture.LINE6);
        //This new accounting line has a bad level.
        ObjectCode badObjectCode = new ObjectCode(1970,"UA","99999");
        badObjectCode.setFinancialObjectLevelCode("XX");
        badObjectCode.setFinancialObjectTypeCode("XX");
        accountingLine.setObjectCode(badObjectCode);
        assertFalse(rule.validateObjectCode(creditMemo, accountingLine));
    }
    */
    
    /*
     * Tests of verifyAccountingStringsBetween0And100Percent
     */ 
    public void testVerifyAccountingStringsBetween0And100Percent_Happy() {
        PurApAccountingLine accountingLine = PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1.createPurApAccountingLine(
                CreditMemoDocument.class,
                PurApAccountingLineFixture.BASIC_ACCOUNT_1,
                AccountingLineFixture.LINE6);
        assertTrue(rule.verifyAccountingStringsBetween0And100Percent(accountingLine,PurapPropertyConstants.ACCOUNT_LINE_PERCENT,"1"));
    }
    
    public void testVerifyAccountingStringsBetween0And100Percent_PercentTooHigh() {
        PurApAccountingLine accountingLine = PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1.createPurApAccountingLine(
                CreditMemoDocument.class,
                PurApAccountingLineFixture.BAD_ACCOUNT_PERCENT_TOO_HIGH,
                AccountingLineFixture.LINE6);
        assertFalse(rule.verifyAccountingStringsBetween0And100Percent(accountingLine,PurapPropertyConstants.ACCOUNT_LINE_PERCENT,"1"));
    }
    
    public void testVerifyAccountingStringsBetween0And100Percent_PercentZero() {
        PurApAccountingLine accountingLine = PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1.createPurApAccountingLine(
                CreditMemoDocument.class,
                PurApAccountingLineFixture.BAD_ACCOUNT_PERCENT_ZERO,
                AccountingLineFixture.LINE6);
        assertFalse(rule.verifyAccountingStringsBetween0And100Percent(accountingLine,PurapPropertyConstants.ACCOUNT_LINE_PERCENT,"1"));
    }
    
    public void testVerifyAccountingStringsBetween0And100Percent_PercentNegative() {
        PurApAccountingLine accountingLine = PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1.createPurApAccountingLine(
                CreditMemoDocument.class,
                PurApAccountingLineFixture.BAD_ACCOUNT_PERCENT_NEGATIVE,
                AccountingLineFixture.LINE6);
        assertFalse(rule.verifyAccountingStringsBetween0And100Percent(accountingLine,PurapPropertyConstants.ACCOUNT_LINE_PERCENT,"1"));
    }
}
