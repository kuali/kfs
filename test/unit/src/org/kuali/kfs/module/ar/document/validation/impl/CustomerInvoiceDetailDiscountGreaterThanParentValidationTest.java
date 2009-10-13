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

import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

@ConfigureContext(session = khuntley)
public class CustomerInvoiceDetailDiscountGreaterThanParentValidationTest extends KualiTestBase {
    
    private CustomerInvoiceDetailDiscountGreaterThanParentValidation validation;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validation = new CustomerInvoiceDetailDiscountGreaterThanParentValidation();
        validation.setCustomerInvoiceDetailService(SpringContext.getBean(CustomerInvoiceDetailService.class));
        validation.setCustomerInvoiceDocument(CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER.createCustomerInvoiceDocument(null));
    }

    @Override
    protected void tearDown() throws Exception {
        validation = null;
        super.tearDown();
    }
    
    public void testDiscountAmountNotGreaterThanParentAmount_True(){
        validation.setDiscountCustomerInvoiceDetail(CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_DISCOUNT_WITH_POSITIVE_AMOUNT.createCustomerInvoiceDetail());
        validation.getDiscountCustomerInvoiceDetail().setParentDiscountCustomerInvoiceDetail(CustomerInvoiceDetailFixture.BASE_CUSTOMER_INVOICE_DETAIL.createCustomerInvoiceDetail());
        assertTrue(validation.validate(null));
    }
    
    
    public void testDiscountAmountNotGreaterThanParentAmount_False(){
        validation.setDiscountCustomerInvoiceDetail(CustomerInvoiceDetailFixture.BASE_CUSTOMER_INVOICE_DETAIL.createCustomerInvoiceDetail());
        validation.getDiscountCustomerInvoiceDetail().setParentDiscountCustomerInvoiceDetail(CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_DISCOUNT_WITH_POSITIVE_AMOUNT.createCustomerInvoiceDetail());
        assertFalse(validation.validate(null));  
    }

}

