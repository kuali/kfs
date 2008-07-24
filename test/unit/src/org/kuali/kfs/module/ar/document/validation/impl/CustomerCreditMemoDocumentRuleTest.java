/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.KHUNTLEY;

import java.math.BigDecimal;

import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentTestUtil;
import org.kuali.kfs.module.ar.fixture.CustomerFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDocumentFixture;

import java.util.ArrayList;
import java.util.List;

@ConfigureContext(session = KHUNTLEY)

public class CustomerCreditMemoDocumentRuleTest extends KualiTestBase {
    
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
        
        assertTrue(rule.isCustomerCreditMemoItemAmountLessThanEqualToInvoiceOpenItemAmount(detail));   
    }
    
    /*
     *  This method tests if isCustomerCreditMemoItemAmountLessThanEqualToInvoiceOpenItemAmount returns true if passed correct values (creditMemoItemAmount < invoiceOpenItemAmount)
     */
    public void testIsCustomerCreditMemoItemAmountGreaterThanInvoiceOpenItemAmount_Less_True() {
        CustomerCreditMemoDetail detail = new CustomerCreditMemoDetail();
        detail.setInvoiceOpenItemAmount(new KualiDecimal(2));
        detail.setCreditMemoItemTotalAmount(new KualiDecimal(1));
        
        assertTrue(rule.isCustomerCreditMemoItemAmountLessThanEqualToInvoiceOpenItemAmount(detail));   
    }
    
    /*
     *  This method tests if isCustomerCreditMemoItemAmountLessThanEqualToInvoiceOpenItemAmount returns false if passed creditMemoItemAmount > invoiceOpenItemAmount)
     */
    public void testIsCustomerCreditMemoItemAmountGreaterThanInvoiceOpenItemAmount_False() {
        CustomerCreditMemoDetail detail = new CustomerCreditMemoDetail();
        detail.setInvoiceOpenItemAmount(new KualiDecimal(1));
        detail.setCreditMemoItemTotalAmount(new KualiDecimal(2));
        
        assertFalse(rule.isCustomerCreditMemoItemAmountLessThanEqualToInvoiceOpenItemAmount(detail));   
    }
    
    /*
     *  This method tests if isCustomerCreditMemoQtyLessThanEqualToInvoiceOpenQty returns true if crm qty = invoice qty
     */
    public void testIsCustomerCreditMemoQtyGreaterThanInvoiceQty_Equal_True() {
        CustomerCreditMemoDetail detail = new CustomerCreditMemoDetail();
        
        detail.setInvoiceOpenItemQuantity(new KualiDecimal(2));
        detail.setCreditMemoItemQuantity(new BigDecimal(2));
        
        assertTrue(rule.isCustomerCreditMemoQtyLessThanEqualToInvoiceOpenQty(detail));   
    }
    
    /*
     *  This method tests if isCustomerCreditMemoQtyLessThanEqualToInvoiceOpenQty returns true if crm qty < invoice qty
     */
    public void testIsCustomerCreditMemoQtyGreaterThanInvoiceQty_Less_True() {
        CustomerCreditMemoDetail detail = new CustomerCreditMemoDetail();
        
        detail.setInvoiceOpenItemQuantity(new KualiDecimal(2));
        detail.setCreditMemoItemQuantity(new BigDecimal(1));
        
        assertTrue(rule.isCustomerCreditMemoQtyLessThanEqualToInvoiceOpenQty(detail));   
    }
    
    /*
     *  This method tests if isCustomerCreditMemoQtyLessThanEqualToInvoiceOpenQty returns false if crm qty > invoice qty
     */
    public void testIsCustomerCreditMemoQtyGreaterThanInvoiceQty_False() {
        CustomerCreditMemoDetail detail = new CustomerCreditMemoDetail();
        
        detail.setInvoiceOpenItemQuantity(new KualiDecimal(1));
        detail.setCreditMemoItemQuantity(new BigDecimal(2));
        
        assertFalse(rule.isCustomerCreditMemoQtyLessThanEqualToInvoiceOpenQty(detail));   
    }
    
    /*
     *  This method tests if checkIfCustomerCreditMemoQtyAndCustomerCreditMemoItemAmountValid returns true if passed valid quantity and amount
     */ 
    public void testCheckIfCustomerCreditMemoQtyAndCustomerCreditMemoItemAmountValid_True() {
        CustomerCreditMemoDetail detail = new CustomerCreditMemoDetail();
        detail.setCreditMemoItemTotalAmount(new KualiDecimal(4));
        detail.setCreditMemoItemQuantity(new BigDecimal(2));
        
        assertTrue(rule.checkIfCustomerCreditMemoQtyAndCustomerCreditMemoItemAmountValid(detail,new KualiDecimal(2)));
    }
    
    /*
     *  This method tests if checkIfCustomerCreditMemoQtyAndCustomerCreditMemoItemAmountValid returns false if passed invalid quantity and/or amount
     */ 
    public void testCheckIfCustomerCreditMemoQtyAndCustomerCreditMemoItemAmountValid_False() {
        CustomerCreditMemoDetail detail = new CustomerCreditMemoDetail();
        detail.setCreditMemoItemTotalAmount(new KualiDecimal(4));
        detail.setCreditMemoItemQuantity(new BigDecimal(1.5));
        
        assertFalse(rule.checkIfCustomerCreditMemoQtyAndCustomerCreditMemoItemAmountValid(detail,new KualiDecimal(2)));
    }
    
    /*
     *  This method tests if checkIfInvoiceNumberIsValid returns false if passed invalid invoice number
     */
    public void testCheckIfInvoiceNumberIsValid_False() {
        assertFalse(rule.checkIfInvoiceNumberIsValid("KLM0456"));   
    }
    
    /*
     *  This method tests if checkIfInvoiceNumberIsValid returns true if passed a valid invoice number
     */
    public void testCheckIfInvoiceNumberIsValid_True() {

        String documentNumber = CustomerInvoiceDocumentTestUtil.submitNewCustomerInvoiceDocument(CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER,
            new CustomerInvoiceDetailFixture[]
            {CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_CHART_RECEIVABLE},
            null);

        assertTrue(rule.checkIfInvoiceNumberIsValid(documentNumber));   
    }
    
    /*
     *  This method tests if checkIfThereIsNoAnotherCRMInRouteForTheInvoice returns true if there are no CRMs associated with the invoice 
     */
    public void testCheckIfThereIsNoAnotherCRMInRouteForTheInvoice_True() {
        String documentNumber = CustomerInvoiceDocumentTestUtil.submitNewCustomerInvoiceDocument(CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER,
            new CustomerInvoiceDetailFixture[]
            {CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_CHART_RECEIVABLE},
            null);
        assertTrue(rule.checkIfThereIsNoAnotherCRMInRouteForTheInvoice(documentNumber));   
    }
    
    /*
     *  This method tests if checkIfThereIsNoAnotherCRMInRouteForTheInvoice returns false if passed an invoice number for which there ia a CRM in route. 
     */
/*
    public void testCheckIfThereIsNoAnotherCRMInRouteForTheInvoice_False() {
        assertFalse(rule.checkIfThereIsNoAnotherCRMInRouteForTheInvoice("328019"));   
    }
*/
}
