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

import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

@ConfigureContext(session = khuntley)
public class CustomerInvoiceCustomerAddressValidationTest extends KualiTestBase {
    
    private CustomerInvoiceCustomerAddressValidation validation;
    
    private static final String VALID_CUSTOMER_NUMBER = "ABB2";
    private static final Integer VALID_ADDRESS_IDENTIFIER = new Integer(1);
    private static final Integer INVALID_ADDRESS_IDENTIFIER = new Integer(99);
    
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validation = new CustomerInvoiceCustomerAddressValidation();
        validation.setCustomerInvoiceDocument(new CustomerInvoiceDocument());
        validation.getCustomerInvoiceDocument().setAccountsReceivableDocumentHeader(new AccountsReceivableDocumentHeader());
        validation.setCustomerAddressService(SpringContext.getBean(CustomerAddressService.class));
    }

    @Override
    protected void tearDown() throws Exception {
        validation = null;
        super.tearDown();
    }
    
    public void testIsValidBillToAddress_True(){
        validation.getCustomerInvoiceDocument().getAccountsReceivableDocumentHeader().setCustomerNumber(VALID_CUSTOMER_NUMBER);
        validation.getCustomerInvoiceDocument().setCustomerBillToAddressIdentifier(VALID_ADDRESS_IDENTIFIER);
        assertTrue(validation.validate(null));
    }
    
    public void testIsValidBillToAddress_False(){
        validation.getCustomerInvoiceDocument().getAccountsReceivableDocumentHeader().setCustomerNumber(VALID_CUSTOMER_NUMBER);
        validation.getCustomerInvoiceDocument().setCustomerBillToAddressIdentifier(INVALID_ADDRESS_IDENTIFIER);
        assertFalse(validation.validate(null));        
    }
    
    public void testIsValidShipToAddress_True(){
        validation.getCustomerInvoiceDocument().getAccountsReceivableDocumentHeader().setCustomerNumber(VALID_CUSTOMER_NUMBER);
        validation.getCustomerInvoiceDocument().setCustomerShipToAddressIdentifier(VALID_ADDRESS_IDENTIFIER);
        assertTrue(validation.validate(null));        
    }
    
    public void testIsValidShipToAddress_False(){
        validation.getCustomerInvoiceDocument().getAccountsReceivableDocumentHeader().setCustomerNumber(VALID_CUSTOMER_NUMBER);
        validation.getCustomerInvoiceDocument().setCustomerShipToAddressIdentifier(INVALID_ADDRESS_IDENTIFIER);
        assertFalse(validation.validate(null));            
    }
}

