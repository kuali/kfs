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

import java.math.BigDecimal;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;

@ConfigureContext(session = khuntley)
public class CustomerInvoiceDetailItemQuantityValidationTest extends KualiTestBase {
    
    private CustomerInvoiceDetailItemQuantityValidation validation;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validation = new CustomerInvoiceDetailItemQuantityValidation();
        validation.setCustomerInvoiceDetail(new CustomerInvoiceDetail());
    }

    @Override
    protected void tearDown() throws Exception {
        validation = null;
        super.tearDown();
    }
    
    public void testQuantityEqualZero(){
        validation.getCustomerInvoiceDetail().setInvoiceItemQuantity(BigDecimal.ZERO);
        assertFalse(validation.validate(null));
    }
    
    public void testQuantityLessThanZero(){
        validation.getCustomerInvoiceDetail().setInvoiceItemQuantity(BigDecimal.ONE.negate());
        assertFalse(validation.validate(null));
    }
    
    public void testQuantityGreaterThanZero(){
        validation.getCustomerInvoiceDetail().setInvoiceItemQuantity(BigDecimal.ONE);
        assertTrue(validation.validate(null));
    }
    
}

