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
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.kew.api.exception.WorkflowException;

@ConfigureContext(session = khuntley)
public class CustomerInvoiceDetailUnitPriceValidationTest extends KualiTestBase {
    
   private CustomerInvoiceDetailUnitPriceValidation validation;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validation = new CustomerInvoiceDetailUnitPriceValidation();
    }

    @Override
    protected void tearDown() throws Exception {
        validation = null;
        super.tearDown();
    }
    
    public void testUnitPriceNotEqualToZero_True() throws WorkflowException {
        CustomerInvoiceDetail customerInvoiceDetail = CustomerInvoiceDetailFixture.BASE_CUSTOMER_INVOICE_DETAIL.createCustomerInvoiceDetail(); 
        CustomerInvoiceDocument customerInvoiceDocument = CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER.createCustomerInvoiceDocument( null );
        
        validation.setCustomerInvoiceDetail(customerInvoiceDetail);
        validation.setCustomerInvoiceDocument(customerInvoiceDocument);
        
        assertTrue(validation.validate(null));        
    }
    
    public void testUnitPriceNotEqualToZero_False() throws WorkflowException {
        CustomerInvoiceDetail customerInvoiceDetail = CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_AMOUNT_EQUALS_ZERO.createCustomerInvoiceDetail(); 
        CustomerInvoiceDocument customerInvoiceDocument = CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER.createCustomerInvoiceDocument( null );
        
        validation.setCustomerInvoiceDetail(customerInvoiceDetail);
        validation.setCustomerInvoiceDocument(customerInvoiceDocument);
        
        assertFalse(validation.validate(null));          
    }
    
    public void testUnitPriceNotNegativeWhenReversalAndDiscount_True() throws WorkflowException {
        CustomerInvoiceDetail customerInvoiceDetail = CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_DISCOUNT_WITH_POSITIVE_AMOUNT.createCustomerInvoiceDetail(); 
        CustomerInvoiceDocument customerInvoiceDocument = CustomerInvoiceDocumentFixture.REVERSAL_CIDOC.createCustomerInvoiceDocument( null );
        
        validation.setCustomerInvoiceDetail(customerInvoiceDetail);
        validation.setCustomerInvoiceDocument(customerInvoiceDocument);
        
        assertTrue(validation.validate(null));        
    }
    
   public void testUnitPriceNotNegativeWhenReversalAndDiscount_False() throws WorkflowException {
       CustomerInvoiceDetail customerInvoiceDetail = CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_DISCOUNT_WITH_NEGATIVE_AMOUNT.createCustomerInvoiceDetail(); 
       CustomerInvoiceDocument customerInvoiceDocument = CustomerInvoiceDocumentFixture.REVERSAL_CIDOC.createCustomerInvoiceDocument( null );
       
       validation.setCustomerInvoiceDetail(customerInvoiceDetail);
       validation.setCustomerInvoiceDocument(customerInvoiceDocument);
       
       assertFalse(validation.validate(null));
    }  
   
   public void testUnitPriceNotPositiveWhenReversalAndNotDiscount_True() throws WorkflowException {
       CustomerInvoiceDetail customerInvoiceDetail = CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_WITH_NEGATIVE_AMOUNT.createCustomerInvoiceDetail(); 
       CustomerInvoiceDocument customerInvoiceDocument = CustomerInvoiceDocumentFixture.REVERSAL_CIDOC.createCustomerInvoiceDocument( null );
       
       validation.setCustomerInvoiceDetail(customerInvoiceDetail);
       validation.setCustomerInvoiceDocument(customerInvoiceDocument);
       
       assertTrue(validation.validate(null));        
   }
   
  public void testUnitPriceNotPositiveWhenReversalAndNotDiscount_False() throws WorkflowException {
      CustomerInvoiceDetail customerInvoiceDetail = CustomerInvoiceDetailFixture.BASE_CUSTOMER_INVOICE_DETAIL.createCustomerInvoiceDetail(); 
      CustomerInvoiceDocument customerInvoiceDocument = CustomerInvoiceDocumentFixture.REVERSAL_CIDOC.createCustomerInvoiceDocument( null );
      
      validation.setCustomerInvoiceDetail(customerInvoiceDetail);
      validation.setCustomerInvoiceDocument(customerInvoiceDocument);
      
      assertFalse(validation.validate(null));
   }   
  
  public void testUnitPriceNotNegativeWhenNotReversalAndNotDiscount_True() throws WorkflowException {
      CustomerInvoiceDetail customerInvoiceDetail = CustomerInvoiceDetailFixture.BASE_CUSTOMER_INVOICE_DETAIL.createCustomerInvoiceDetail(); 
      CustomerInvoiceDocument customerInvoiceDocument = CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER.createCustomerInvoiceDocument( null );
      
      validation.setCustomerInvoiceDetail(customerInvoiceDetail);
      validation.setCustomerInvoiceDocument(customerInvoiceDocument);
      
      assertTrue(validation.validate(null));
   }   
  
  public void testUnitPriceNotNegativeWhenNotReversalAndNotDiscount_False() throws WorkflowException {
      CustomerInvoiceDetail customerInvoiceDetail = CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_WITH_NEGATIVE_AMOUNT.createCustomerInvoiceDetail(); 
      CustomerInvoiceDocument customerInvoiceDocument = CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER.createCustomerInvoiceDocument( null );
      
      validation.setCustomerInvoiceDetail(customerInvoiceDetail);
      validation.setCustomerInvoiceDocument(customerInvoiceDocument);
      
      assertFalse(validation.validate(null));        
  }

}

