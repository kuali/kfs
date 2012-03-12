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

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.kew.api.exception.WorkflowException;

@ConfigureContext(session = khuntley)
public class CustomerInvoiceDetailAmountValidationTest extends KualiTestBase {
    
    private CustomerInvoiceDetailAmountValidation validation;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validation = new CustomerInvoiceDetailAmountValidation();
    }

    @Override
    protected void tearDown() throws Exception {
        validation = null;
        super.tearDown();
    }
    
    public void testAmountNotEqualToZero_True() throws WorkflowException {
        CustomerInvoiceDetail customerInvoiceDetail = CustomerInvoiceDetailFixture.BASE_CUSTOMER_INVOICE_DETAIL.createCustomerInvoiceDetail(); 
        CustomerInvoiceDocument customerInvoiceDocument = CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER.createCustomerInvoiceDocument( null );
        
        validation.setCustomerInvoiceDetail(customerInvoiceDetail);
        validation.setCustomerInvoiceDocument(customerInvoiceDocument);
        
        assertTrue(validation.validate(null));        
    }
    
    public void testAmountNotEqualToZero_False() throws WorkflowException {
        CustomerInvoiceDetail customerInvoiceDetail = CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_AMOUNT_EQUALS_ZERO.createCustomerInvoiceDetail(); 
        CustomerInvoiceDocument customerInvoiceDocument = CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER.createCustomerInvoiceDocument( null );
        
        validation.setCustomerInvoiceDetail(customerInvoiceDetail);
        validation.setCustomerInvoiceDocument(customerInvoiceDocument);
        
        assertFalse(validation.validate(null));          
    }
    
    public void testAmountNotNegativeWhenReversalAndDiscount_True() throws WorkflowException {
        CustomerInvoiceDetail customerInvoiceDetail = CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_DISCOUNT_WITH_POSITIVE_AMOUNT.createCustomerInvoiceDetail(); 
        CustomerInvoiceDocument customerInvoiceDocument = CustomerInvoiceDocumentFixture.REVERSAL_CIDOC.createCustomerInvoiceDocument( null );
        
        validation.setCustomerInvoiceDetail(customerInvoiceDetail);
        validation.setCustomerInvoiceDocument(customerInvoiceDocument);
        
        assertTrue(validation.validate(null));        
    }
    
   public void testAmountNotNegativeWhenReversalAndDiscount_False() throws WorkflowException {
       CustomerInvoiceDetail customerInvoiceDetail = CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_DISCOUNT_WITH_NEGATIVE_AMOUNT.createCustomerInvoiceDetail(); 
       CustomerInvoiceDocument customerInvoiceDocument = CustomerInvoiceDocumentFixture.REVERSAL_CIDOC.createCustomerInvoiceDocument( null );
       
       validation.setCustomerInvoiceDetail(customerInvoiceDetail);
       validation.setCustomerInvoiceDocument(customerInvoiceDocument);
       
       assertFalse(validation.validate(null));
    }  
   
   public void testAmountNotPositiveWhenReversalAndNotDiscount_True() throws WorkflowException {
       CustomerInvoiceDetail customerInvoiceDetail = CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_WITH_NEGATIVE_AMOUNT.createCustomerInvoiceDetail(); 
       CustomerInvoiceDocument customerInvoiceDocument = CustomerInvoiceDocumentFixture.REVERSAL_CIDOC.createCustomerInvoiceDocument( null );
       
       validation.setCustomerInvoiceDetail(customerInvoiceDetail);
       validation.setCustomerInvoiceDocument(customerInvoiceDocument);
       
       assertTrue(validation.validate(null));        
   }
   
  public void testAmountNotPositiveWhenReversalAndNotDiscount_False()throws WorkflowException {
      CustomerInvoiceDetail customerInvoiceDetail = CustomerInvoiceDetailFixture.BASE_CUSTOMER_INVOICE_DETAIL.createCustomerInvoiceDetail(); 
      CustomerInvoiceDocument customerInvoiceDocument = CustomerInvoiceDocumentFixture.REVERSAL_CIDOC.createCustomerInvoiceDocument( null );
      
      validation.setCustomerInvoiceDetail(customerInvoiceDetail);
      validation.setCustomerInvoiceDocument(customerInvoiceDocument);
      
      assertFalse(validation.validate(null));
   }   
   

   public void testAmountNotPositiveWhenNotReversalAndDiscount_True() throws WorkflowException {
       CustomerInvoiceDetail customerInvoiceDetail = CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_DISCOUNT_WITH_NEGATIVE_AMOUNT.createCustomerInvoiceDetail(); 
       CustomerInvoiceDocument customerInvoiceDocument = CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER.createCustomerInvoiceDocument( null );
       
       validation.setCustomerInvoiceDetail(customerInvoiceDetail);
       validation.setCustomerInvoiceDocument(customerInvoiceDocument);
       
       assertTrue(validation.validate(null));
    }   
   
   public void testAmountNotPositiveWhenNotReversalAndDiscount_False() throws WorkflowException {
       CustomerInvoiceDetail customerInvoiceDetail = CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_DISCOUNT_WITH_POSITIVE_AMOUNT.createCustomerInvoiceDetail(); 
       CustomerInvoiceDocument customerInvoiceDocument = CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER.createCustomerInvoiceDocument( null );
       
       validation.setCustomerInvoiceDetail(customerInvoiceDetail);
       validation.setCustomerInvoiceDocument(customerInvoiceDocument);
       
       assertFalse(validation.validate(null));        
   }
  
  public void testAmountNotNegativeWhenNotReversalAndNotDiscount_True() throws WorkflowException {
      CustomerInvoiceDetail customerInvoiceDetail = CustomerInvoiceDetailFixture.BASE_CUSTOMER_INVOICE_DETAIL.createCustomerInvoiceDetail(); 
      CustomerInvoiceDocument customerInvoiceDocument = CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER.createCustomerInvoiceDocument( null );
      
      validation.setCustomerInvoiceDetail(customerInvoiceDetail);
      validation.setCustomerInvoiceDocument(customerInvoiceDocument);
      
      assertTrue(validation.validate(null));
   }   
  
  public void testAmountNotNegativeWhenNotReversalAndNotDiscount_False() throws WorkflowException {
      CustomerInvoiceDetail customerInvoiceDetail = CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_WITH_NEGATIVE_AMOUNT.createCustomerInvoiceDetail(); 
      CustomerInvoiceDocument customerInvoiceDocument = CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER.createCustomerInvoiceDocument( null );
      
      validation.setCustomerInvoiceDetail(customerInvoiceDetail);
      validation.setCustomerInvoiceDocument(customerInvoiceDocument);
      
      assertFalse(validation.validate(null));        
  }   
}

