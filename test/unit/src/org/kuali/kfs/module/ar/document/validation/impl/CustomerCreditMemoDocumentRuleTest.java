/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.math.BigDecimal;
import java.util.List;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentTestUtil;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;

@ConfigureContext(session = khuntley, shouldCommitTransactions = true)
public class CustomerCreditMemoDocumentRuleTest extends KualiTestBase {

    public static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerCreditMemoDocumentRuleTest.class);

    private CustomerCreditMemoDocumentRule rule;
    private CustomerCreditMemoDocument document;

    @Override
    protected  void setUp() throws Exception {
        super.setUp();
        rule = new CustomerCreditMemoDocumentRule();
        document = new CustomerCreditMemoDocument();
    }

    @Override
    protected void tearDown() throws Exception {
        rule = null;
        document = null;
        super.tearDown();
    }

    /*
     *  This method tests if isQtyOrItemAmountEntered returns an empty string if customer doesn't enter any information
     */
    public void testIsQtyOrItemAmountEntered_EmptyString_True() {
       CustomerCreditMemoDetail detail = new CustomerCreditMemoDetail();

       assertTrue(rule.isQtyOrItemAmountEntered(detail).equals(""));
    }

    /*
     *  This method tests if isQtyOrItemAmountEntered returns correct key if customer enteres only quantity
     */
   public void testIsQtyOrItemAmountEntered_Qty_True() {
       CustomerCreditMemoDetail detail = new CustomerCreditMemoDetail();
       detail.setCreditMemoItemQuantity(new BigDecimal(1));

       assertTrue(rule.isQtyOrItemAmountEntered(detail).equals(ArConstants.CustomerCreditMemoConstants.CUSTOMER_CREDIT_MEMO_ITEM_QUANTITY));
    }

    /*
     *  This method tests if isQtyOrItemAmountEntered returns correct key if customer enteres only amount
     */
    public void testIsQtyOrItemAmountEntered_Amount_True() {
       CustomerCreditMemoDetail detail = new CustomerCreditMemoDetail();
       detail.setCreditMemoItemTotalAmount(new KualiDecimal(1));

       assertTrue(rule.isQtyOrItemAmountEntered(detail).equals(ArConstants.CustomerCreditMemoConstants.CUSTOMER_CREDIT_MEMO_ITEM_TOTAL_AMOUNT));
    }

    /*
     *  This method tests if isQtyOrItemAmountEntered returns correct key if customer enteres both amount and quantity
     */
    public void testIsQtyOrItemAmountEntered_Both_True() {
       CustomerCreditMemoDetail detail = new CustomerCreditMemoDetail();

       detail.setCreditMemoItemTotalAmount(new KualiDecimal(1));
       detail.setCreditMemoItemQuantity(new BigDecimal(1));

       assertTrue(rule.isQtyOrItemAmountEntered(detail).equals(ArConstants.CustomerCreditMemoConstants.BOTH_QUANTITY_AND_ITEM_TOTAL_AMOUNT_ENTERED));
    }

    /*
     *  This method tests if isValueGreaterThanZero returns true if passed a valid value
     */
   public void testIsValueGreaterThanZero_True() {
        assertTrue(rule.isValueGreaterThanZero(BigDecimal.ONE));
    }

    /*
     *  This method tests if isValueGreaterThanZero returns false if passed a zero.
     */
    public void testIsValueGreaterThanZero_False() {
        assertFalse(rule.isValueGreaterThanZero(BigDecimal.ZERO));
    }

    /*
     *  This method tests if isValueGreaterThanZero returns true if passed a valid value
     */
    public void testIsValueGreaterThanZero_KualiDecimal_True() {
        assertTrue(rule.isValueGreaterThanZero(new KualiDecimal(1)));
    }

    /*
     *  This method tests if isValueGreaterThanZero returns false if passed a zero.
     */
    public void testIsValueGreaterThanZero_KualiDecimal_False() {
        assertFalse(rule.isValueGreaterThanZero(KualiDecimal.ZERO));
    }

    /*
     *  This method tests if isCustomerCreditMemoItemAmountLessThanEqualToInvoiceOpenItemAmount returns true if passed correct values (invoiceOpenItemAmount = creditMemoItemAmount)
     */
   public void testIsCustomerCreditMemoItemAmountGreaterThanInvoiceOpenItemAmount_Equal_True() {
        CustomerCreditMemoDetail detail = new CustomerCreditMemoDetail();
        detail.setInvoiceOpenItemAmount(new KualiDecimal(2));
        detail.setCreditMemoItemTotalAmount(new KualiDecimal(2));

        assertTrue(rule.isCustomerCreditMemoItemAmountLessThanEqualToInvoiceOpenItemAmount(document, detail));
    }

    /*
     *  This method tests if isCustomerCreditMemoItemAmountLessThanEqualToInvoiceOpenItemAmount returns true if passed correct values (creditMemoItemAmount < invoiceOpenItemAmount)
     */
   public void testIsCustomerCreditMemoItemAmountGreaterThanInvoiceOpenItemAmount_Less_True() {
        CustomerCreditMemoDetail detail = new CustomerCreditMemoDetail();
        detail.setInvoiceOpenItemAmount(new KualiDecimal(2));
        detail.setCreditMemoItemTotalAmount(new KualiDecimal(1));

        assertTrue(rule.isCustomerCreditMemoItemAmountLessThanEqualToInvoiceOpenItemAmount(document, detail));
    }

    /*
     *  This method tests if isCustomerCreditMemoItemAmountLessThanEqualToInvoiceOpenItemAmount returns false if passed creditMemoItemAmount > invoiceOpenItemAmount)
     */
    public void testIsCustomerCreditMemoItemAmountGreaterThanInvoiceOpenItemAmount_False() {
        CustomerCreditMemoDetail detail = new CustomerCreditMemoDetail();
        detail.setInvoiceOpenItemAmount(new KualiDecimal(1));
        detail.setCreditMemoItemTotalAmount(new KualiDecimal(2));

        assertFalse(rule.isCustomerCreditMemoItemAmountLessThanEqualToInvoiceOpenItemAmount(document, detail));
    }

    /*
     *  This method tests if isCustomerCreditMemoQtyLessThanEqualToInvoiceOpenQty returns true if crm qty = invoice qty
     */
    public void testIsCustomerCreditMemoQtyGreaterThanInvoiceQty_Equal_True() {
        CustomerCreditMemoDetail detail = new CustomerCreditMemoDetail();

        detail.setInvoiceOpenItemQuantity(new BigDecimal(2));
        detail.setCreditMemoItemQuantity(new BigDecimal(2));

        assertTrue(rule.isCustomerCreditMemoQtyLessThanEqualToInvoiceOpenQty(detail));
    }

    /*
     *  This method tests if isCustomerCreditMemoQtyLessThanEqualToInvoiceOpenQty returns true if crm qty < invoice qty
     */
    public void testIsCustomerCreditMemoQtyGreaterThanInvoiceQty_Less_True() {
        CustomerCreditMemoDetail detail = new CustomerCreditMemoDetail();

        detail.setInvoiceOpenItemQuantity(new BigDecimal(2));
        detail.setCreditMemoItemQuantity(new BigDecimal(1));

        assertTrue(rule.isCustomerCreditMemoQtyLessThanEqualToInvoiceOpenQty(detail));
    }

    /*
     *  This method tests if isCustomerCreditMemoQtyLessThanEqualToInvoiceOpenQty returns false if crm qty > invoice qty
     */
    public void testIsCustomerCreditMemoQtyGreaterThanInvoiceQty_False() {
        CustomerCreditMemoDetail detail = new CustomerCreditMemoDetail();

        detail.setInvoiceOpenItemQuantity(new BigDecimal(1));
        detail.setCreditMemoItemQuantity(new BigDecimal(2));

        assertFalse(rule.isCustomerCreditMemoQtyLessThanEqualToInvoiceOpenQty(detail));
    }

    /*
     *  This method tests if checkIfCustomerCreditMemoQtyAndCustomerCreditMemoItemAmountValid returns true if passed valid quantity and amount
     */
    public void testCheckIfCustomerCreditMemoQtyAndCustomerCreditMemoItemAmountValid_ExactMatch() {
        CustomerCreditMemoDetail detail = new CustomerCreditMemoDetail();
        detail.setCreditMemoItemTotalAmount(new KualiDecimal(4));
        detail.setCreditMemoItemQuantity(new BigDecimal(2));

        assertTrue(rule.checkIfCustomerCreditMemoQtyAndCustomerCreditMemoItemAmountValid(detail,new BigDecimal(2)));
    }

    /*
     *  This method tests if checkIfCustomerCreditMemoQtyAndCustomerCreditMemoItemAmountValid returns false if passed invalid quantity and/or amount
     */
    public void testCheckIfCustomerCreditMemoQtyAndCustomerCreditMemoItemAmountValid_FarOff() {
        CustomerCreditMemoDetail detail = new CustomerCreditMemoDetail();
        detail.setCreditMemoItemTotalAmount(new KualiDecimal(50));
        detail.setCreditMemoItemQuantity(new BigDecimal(10)); // should be 5

        assertFalse(rule.checkIfCustomerCreditMemoQtyAndCustomerCreditMemoItemAmountValid(detail,new BigDecimal(10)));
    }

    public void testCheckIfCustomerCreditMemoQtyAndCustomerCreditMemoItemAmountValid_JustAboveAllowedDeviation() {
        CustomerCreditMemoDetail detail = new CustomerCreditMemoDetail();
        detail.setCreditMemoItemTotalAmount(new KualiDecimal(50));
        detail.setCreditMemoItemQuantity(new BigDecimal(5.6)); // should be 5

        assertFalse(rule.checkIfCustomerCreditMemoQtyAndCustomerCreditMemoItemAmountValid(detail,new BigDecimal(10)));
    }

    public void testCheckIfCustomerCreditMemoQtyAndCustomerCreditMemoItemAmountValid_ExactlyAllowedDeviation() {
        CustomerCreditMemoDetail detail = new CustomerCreditMemoDetail();
        detail.setCreditMemoItemTotalAmount(new KualiDecimal(50));
        detail.setCreditMemoItemQuantity(new BigDecimal(5.5)); // should be 5

        assertTrue(rule.checkIfCustomerCreditMemoQtyAndCustomerCreditMemoItemAmountValid(detail,new BigDecimal(10)));
    }

    public void testCheckIfCustomerCreditMemoQtyAndCustomerCreditMemoItemAmountValid_JustBelowAllowedDeviation() {
        CustomerCreditMemoDetail detail = new CustomerCreditMemoDetail();
        detail.setCreditMemoItemTotalAmount(new KualiDecimal(50));
        detail.setCreditMemoItemQuantity(new BigDecimal(5.4)); // should be 5

        assertTrue(rule.checkIfCustomerCreditMemoQtyAndCustomerCreditMemoItemAmountValid(detail,new BigDecimal(10)));
    }

    /*
     *  This method tests if checkIfInvoiceNumberIsValid returns false if passed invalid invoice number
     */
    public void testCheckIfInvoiceNumberIsValid_False() {
        assertFalse(rule.checkIfInvoiceNumberIsFinal("KLM0456"));
    }

    /*
     *  This method tests if checkIfInvoiceNumberIsValid returns true if passed a valid invoice number
     */
    public void testCheckIfInvoiceNumberIsValid_True() throws WorkflowException {

        String documentNumber = CustomerInvoiceDocumentTestUtil.submitNewCustomerInvoiceDocument(CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER,
            new CustomerInvoiceDetailFixture[]
            {CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_CHART_RECEIVABLE},
            null);

        assertTrue(rule.checkIfInvoiceNumberIsFinal(documentNumber));
    }

    /*
     *  This method tests if checkIfThereIsNoAnotherCRMInRouteForTheInvoice returns true if there are no CRMs associated with the invoice
     */
    public void testCheckIfThereIsNoAnotherCRMInRouteForTheInvoice_True() throws WorkflowException {
        String documentNumber = CustomerInvoiceDocumentTestUtil.submitNewCustomerInvoiceDocument(CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER,
            new CustomerInvoiceDetailFixture[]
            {CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_CHART_RECEIVABLE},
            null);
        assertTrue(rule.checkIfThereIsNoAnotherCRMInRouteForTheInvoice(documentNumber));
    }

    /*
     *  This method tests if checkIfThereIsNoAnotherCRMInRouteForTheInvoice returns false if passed an invoice number for which there is a CRM in route.
     */

    public void testCheckIfThereIsNoAnotherCRMInRouteForTheInvoice_False() throws WorkflowException {
        String invoiceNumber = "";

        // create a new invoice
        CustomerInvoiceDocument invoice = CustomerInvoiceDocumentTestUtil.submitNewCustomerInvoiceDocumentAndReturnIt(CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER,
            new CustomerInvoiceDetailFixture[]
            {CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_CHART_RECEIVABLE},
            null);

        // create a new credit memo for the invoice and save it
        invoiceNumber = invoice.getDocumentNumber();
        document = createCustomerCreditMemoDocument(invoice);
        document.getDocumentHeader().setDocumentDescription("CREATING TEST CRM DOCUMENT");

        try {
            Document savedDocument = SpringContext.getBean(DocumentService.class).routeDocument(document, "Routing from Unit Test", null);
        } catch (Exception e){
            LOG.error(e.getMessage());
        }
        assertFalse(rule.checkIfThereIsNoAnotherCRMInRouteForTheInvoice(invoiceNumber));
    }

    /**
     * This method creates a customer credit memo document based on the passed in customer invoice document
     *
     * @param customer invoice document
     * @return
     */
    public CustomerCreditMemoDocument createCustomerCreditMemoDocument(CustomerInvoiceDocument invoice){

        CustomerCreditMemoDetail customerCreditMemoDetail;
        CustomerCreditMemoDocument customerCreditMemoDocument = null;
        KualiDecimal invItemTaxAmount, itemAmount;
        Integer itemLineNumber;
        BigDecimal itemQuantity;
        String documentNumber;

        try {
            // the document header is created and set here
            customerCreditMemoDocument = DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), CustomerCreditMemoDocument.class);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Document creation failed.");
        }

        customerCreditMemoDocument.setInvoice(invoice); // invoice, not sure I need to set it at all for this test
        customerCreditMemoDocument.setFinancialDocumentReferenceInvoiceNumber(invoice.getDocumentNumber());

        List<CustomerInvoiceDetail> customerInvoiceDetails = invoice.getCustomerInvoiceDetailsWithoutDiscounts();
        if (customerInvoiceDetails == null) {
            return customerCreditMemoDocument;
        }

        for (CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDetails) {
                customerCreditMemoDetail = new CustomerCreditMemoDetail();
                customerCreditMemoDetail.setDocumentNumber(customerCreditMemoDocument.getDocumentNumber());

                invItemTaxAmount = customerInvoiceDetail.getInvoiceItemTaxAmount();
                if (invItemTaxAmount == null) {
                    invItemTaxAmount = KualiDecimal.ZERO;
                }
                customerCreditMemoDetail.setCreditMemoItemTaxAmount(invItemTaxAmount.divide(new KualiDecimal(2)));

                customerCreditMemoDetail.setReferenceInvoiceItemNumber(customerInvoiceDetail.getSequenceNumber());
                itemQuantity = customerInvoiceDetail.getInvoiceItemQuantity().divide(new BigDecimal(2));
                customerCreditMemoDetail.setCreditMemoItemQuantity(itemQuantity);

                itemAmount = customerInvoiceDetail.getAmount().divide(new KualiDecimal(2));
                customerCreditMemoDetail.setCreditMemoItemTotalAmount(itemAmount);
                customerCreditMemoDetail.setFinancialDocumentReferenceInvoiceNumber(invoice.getDocumentNumber());
                customerCreditMemoDetail.setCustomerInvoiceDetail(customerInvoiceDetail);
                customerCreditMemoDocument.getCreditMemoDetails().add(customerCreditMemoDetail);
        }
        return customerCreditMemoDocument;
    }

}

