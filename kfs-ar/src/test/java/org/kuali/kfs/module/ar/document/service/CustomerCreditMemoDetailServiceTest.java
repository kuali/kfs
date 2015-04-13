/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.math.BigDecimal;

import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDocumentFixture;
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
        String documentNumber = CustomerInvoiceDocumentTestUtil.submitNewCustomerInvoiceDocument(CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER,
                new CustomerInvoiceDetailFixture[]
                {CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_CHART_RECEIVABLE},
                null);
        document = new CustomerCreditMemoDocument();
        document.setFinancialDocumentReferenceInvoiceNumber(documentNumber);
        document.getInvoice();

        detail = new CustomerCreditMemoDetail();
        detail.setReferenceInvoiceItemNumber(new Integer(1));
        detail.setFinancialDocumentReferenceInvoiceNumber(documentNumber);

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
        service.recalculateCustomerCreditMemoDetail(detail,document);

        assertEquals(0, detail.getCreditMemoItemTotalAmount().compareTo(testAmount));
        assertTrue(detail.getCreditMemoItemTaxAmount().equals(KualiDecimal.ZERO));
        assertTrue(detail.getCreditMemoLineTotalAmount().equals(testAmount));
        assertFalse(detail.getCreditMemoItemTaxAmount().isPositive());
        assertTrue(detail.getCreditMemoItemTotalAmount().equals(testAmount));
        assertTrue(detail.getCreditMemoLineTotalAmount().equals(testAmount));
    }

    /**
     * This method tests if recalculateCustomerCreditMemoDetail makes CRM detail calculations correctly based on entered item amount
     */

    public void testRecalculateCustomerCreditMemoDetail_ItemAmount() {
        detail.setCreditMemoItemTotalAmount(testAmount);
        assertTrue(detail.getCreditMemoItemTotalAmount().equals(testAmount));
        service.recalculateCustomerCreditMemoDetail(detail,document);

        assertEquals(0, detail.getCreditMemoItemQuantity().compareTo(new BigDecimal(0.5)));
        assertTrue(detail.getCreditMemoItemTaxAmount().equals(KualiDecimal.ZERO));
        assertTrue(detail.getCreditMemoLineTotalAmount().equals(testAmount));
        assertFalse(detail.getCreditMemoItemTaxAmount().isPositive());
        assertTrue(detail.getCreditMemoItemTotalAmount().equals(testAmount));
        assertTrue(detail.getCreditMemoLineTotalAmount().equals(testAmount));
    }

}

