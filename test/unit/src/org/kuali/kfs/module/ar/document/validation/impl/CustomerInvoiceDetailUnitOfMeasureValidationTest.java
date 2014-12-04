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

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

@ConfigureContext(session = khuntley)
public class CustomerInvoiceDetailUnitOfMeasureValidationTest extends KualiTestBase {
    
    private final static String VALID_UNIT_OF_MEASURE_CODE = "EA";
    private final static String INVALID_UNIT_OF_MEASURE_CODE = "XX";
    
    private CustomerInvoiceDetailUnitOfMeasureValidation validation;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validation = new CustomerInvoiceDetailUnitOfMeasureValidation();
        validation.setCustomerInvoiceDetail(new CustomerInvoiceDetail());
        validation.setBusinessObjectService(SpringContext.getBean(BusinessObjectService.class));
    }

    @Override
    protected void tearDown() throws Exception {
        validation = null;
        super.tearDown();
    }
    
    public void testValidUnitOfMeasure_True(){
        validation.getCustomerInvoiceDetail().setInvoiceItemUnitOfMeasureCode(VALID_UNIT_OF_MEASURE_CODE);
        assertTrue(validation.validate(null));
    }
    
    public void testValidUnitOfMeasure_False(){
        validation.getCustomerInvoiceDetail().setInvoiceItemUnitOfMeasureCode(INVALID_UNIT_OF_MEASURE_CODE);
        assertFalse(validation.validate(null));
    }    

}

