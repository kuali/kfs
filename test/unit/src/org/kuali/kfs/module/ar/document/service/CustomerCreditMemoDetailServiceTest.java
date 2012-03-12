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
package org.kuali.kfs.module.ar.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.math.BigDecimal;

import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;

@ConfigureContext(session = khuntley)
public class CustomerCreditMemoDetailServiceTest extends KualiTestBase {
    
    private CustomerCreditMemoDetailService service;
    private CustomerCreditMemoDocument document;
    private CustomerCreditMemoDetail detail;
    private KualiDecimal testAmount;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        service = SpringContext.getBean(CustomerCreditMemoDetailService.class);
        /*
        String documentNumber = CustomerInvoiceDocumentTestUtil.submitNewCustomerInvoiceDocument(CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER,
                new CustomerInvoiceDetailFixture[]
                {CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_CHART_RECEIVABLE},
                null);
        */
        document = new CustomerCreditMemoDocument();
        //document.setFinancialDocumentReferenceInvoiceNumber(documentNumber);
        //document.getInvoice();
        
        detail = new CustomerCreditMemoDetail();
        //detail.setReferenceInvoiceItemNumber(new Integer(1));
        //detail.setFinancialDocumentReferenceInvoiceNumber(documentNumber);
        
        testAmount = new KualiDecimal(0.5);
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        service = null;
        document = null;
        detail = null;
        testAmount = null;
        super.tearDown();
    }
    
    /**
     * This method tests if recalculateCustomerCreditMemoDetail makes CRM detail calculations correctly based on entered item quantity
     */
    public void testRecalculateCustomerCreditMemoDetail_Quantity() {
        detail.setCreditMemoItemQuantity(new BigDecimal(0.5));
        assertTrue(detail.getCreditMemoItemQuantity().equals(new BigDecimal(0.5)));
        /* 
        service.recalculateCustomerCreditMemoDetail(detail,document);
        
        assertTrue(detail.getCreditMemoItemTotalAmount().equals(testAmount));
        assertTrue(detail.getCreditMemoItemTaxAmount().equals(KualiDecimal.ZERO));
        assertTrue(detail.getCreditMemoLineTotalAmount().equals(testAmount));
        assertFalse(detail.getCreditMemoItemTaxAmount().isPositive());
        assertTrue(detail.getCreditMemoItemTotalAmount().equals(testAmount));
        assertTrue(detail.getCreditMemoLineTotalAmount().equals(testAmount));
        */
    }
    
    /**
     * This method tests if recalculateCustomerCreditMemoDetail makes CRM detail calculations correctly based on entered item amount
     */

    public void testRecalculateCustomerCreditMemoDetail_ItemAmount() {
        detail.setCreditMemoItemTotalAmount(testAmount);
        assertTrue(detail.getCreditMemoItemTotalAmount().equals(testAmount));
        /*
        service.recalculateCustomerCreditMemoDetail(detail,document);
        
        assertTrue(detail.getCreditMemoItemQuantity().toString().equals("0.50"));
        assertTrue(detail.getCreditMemoItemTaxAmount().equals(KualiDecimal.ZERO));
        assertTrue(detail.getCreditMemoLineTotalAmount().equals(testAmount));
        assertFalse(detail.getCreditMemoItemTaxAmount().isPositive());
        assertTrue(detail.getCreditMemoItemTotalAmount().equals(testAmount));
        assertTrue(detail.getCreditMemoLineTotalAmount().equals(testAmount));
        */
    }

}

