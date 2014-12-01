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
package org.kuali.kfs.module.ar.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

@ConfigureContext(session = khuntley)
public class CustomerInvoiceDetailParentLessThanDiscountValidationTest extends KualiTestBase {

    private CustomerInvoiceDetailParentLessThanDiscountValidation validation;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validation = new CustomerInvoiceDetailParentLessThanDiscountValidation();
        validation.setCustomerInvoiceDetailService(SpringContext.getBean(CustomerInvoiceDetailService.class));
        validation.setCustomerInvoiceDocument(CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER.createCustomerInvoiceDocument(null));
    }

    @Override
    protected void tearDown() throws Exception {
        validation = null;
        super.tearDown();
    }
    
    public void testParentAmountNotLessThanDiscountAmount_True(){
        validation.setParentCustomerInvoiceDetail(CustomerInvoiceDetailFixture.BASE_CUSTOMER_INVOICE_DETAIL.createCustomerInvoiceDetail());
        validation.getParentCustomerInvoiceDetail().setDiscountCustomerInvoiceDetail(CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_DISCOUNT_WITH_POSITIVE_AMOUNT.createCustomerInvoiceDetail());
        assertTrue(validation.validate(null));
    }
    
    
    public void testParentAmountNotGreaterThanParentAmount_False(){
        validation.setParentCustomerInvoiceDetail(CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_DISCOUNT_WITH_POSITIVE_AMOUNT.createCustomerInvoiceDetail());
        validation.getParentCustomerInvoiceDetail().setDiscountCustomerInvoiceDetail(CustomerInvoiceDetailFixture.BASE_CUSTOMER_INVOICE_DETAIL.createCustomerInvoiceDetail());
        assertFalse(validation.validate(null));  
    }    
    
}

