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
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

@ConfigureContext(session = khuntley)
public class CustomerCreditMemoDocumentServiceTest extends KualiTestBase {
    private CustomerCreditMemoDocumentService service;
    private CustomerCreditMemoDocument document;
    private List<CustomerCreditMemoDetail> details;
    private KualiDecimal testAmount;

    /**
     * @see junit.framework.TestCase#setUp()
     */

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        
        String documentNumber = CustomerInvoiceDocumentTestUtil.submitNewCustomerInvoiceDocument(CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER,
                new CustomerInvoiceDetailFixture[]
                {CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_CHART_RECEIVABLE,
                 CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_CHART_RECEIVABLE},
                null);
        
        document = new CustomerCreditMemoDocument();
        document.setFinancialDocumentReferenceInvoiceNumber(documentNumber);
        document.getInvoice();
        
        details = new ArrayList<CustomerCreditMemoDetail>();
        
        CustomerCreditMemoDetail detail1 = new CustomerCreditMemoDetail();
        detail1.setFinancialDocumentReferenceInvoiceNumber(documentNumber);
        detail1.setCreditMemoItemQuantity(new BigDecimal(0.5));
        detail1.setReferenceInvoiceItemNumber(new Integer(1));
        
        CustomerCreditMemoDetail detail2 = new CustomerCreditMemoDetail();
        detail2.setFinancialDocumentReferenceInvoiceNumber(documentNumber);
        detail2.setReferenceInvoiceItemNumber(new Integer(2));
        
        details.add(detail1);
        details.add(detail2);
        
        document.setCreditMemoDetails(details);
        
        testAmount = new KualiDecimal(0.5);
        
        service = SpringContext.getBean(CustomerCreditMemoDocumentService.class);
    }


    /**
     * @see junit.framework.TestCase#tearDown()
     */

    @Override
    protected void tearDown() throws Exception {
        document = null;
        details = null;
        service = null;
        super.tearDown();
    }
    
    /**
     * This method tests if recalculateCustomerCreditMemoDocument recalculates CRM document correctly in case of submit or save event
     * No need to test recalculateCustomerCreditMemoDocument for blanket approve event as it does the same calculations skipping the calculations for document totals
     */

    public void testRecalculateCustomerCreditMemoDocument() {
        service.recalculateCustomerCreditMemoDocument(document,false);
        
        details = document.getCreditMemoDetails();
        for (CustomerCreditMemoDetail crmDetail:details) {
            if (crmDetail.getReferenceInvoiceItemNumber().equals(new Integer(1))) {
                assertTrue(crmDetail.getCreditMemoItemTotalAmount().equals(testAmount));
                assertTrue(crmDetail.getCreditMemoItemTaxAmount().equals(KualiDecimal.ZERO));
                assertTrue(crmDetail.getCreditMemoLineTotalAmount().equals(testAmount));
                assertFalse(!crmDetail.getDuplicateCreditMemoItemTotalAmount().equals(testAmount));
                
            } else {
                assertTrue(ObjectUtils.isNull(crmDetail.getCreditMemoItemQuantity()));
                assertTrue(ObjectUtils.isNull(crmDetail.getCreditMemoItemTotalAmount()));
                assertFalse(crmDetail.getCreditMemoItemTaxAmount().isPositive());
                assertFalse(crmDetail.getCreditMemoLineTotalAmount().isPositive());
                assertTrue(ObjectUtils.isNull(crmDetail.getDuplicateCreditMemoItemTotalAmount()));
            }
        }
        assertTrue(document.getCrmTotalItemAmount().equals(testAmount));
        assertTrue(document.getCrmTotalTaxAmount().equals(KualiDecimal.ZERO));
        assertTrue(document.getCrmTotalAmount().equals(testAmount));
    }

}

