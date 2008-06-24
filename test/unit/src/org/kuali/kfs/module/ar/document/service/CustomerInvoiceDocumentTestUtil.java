/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.document.service;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.fixture.CustomerFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDocumentFixture;
import org.kuali.kfs.sys.context.SpringContext;

public class CustomerInvoiceDocumentTestUtil {
    
    public static final String CUSTOMER_MAINT_DOC_NAME = "CustomerMaintenanceDocument";
    
    
    /**
     * This method saves a customer BO based on the passed in customer fixture
     * 
     * @param customerFixture
     */
    public static void saveNewCustomer(CustomerFixture customerFixture) throws Exception {
        SpringContext.getBean(BusinessObjectService.class).save(customerFixture.createCustomer());
    }
    
    /**
     * This method saves a customer invoice document BO based on passed in customer invoice document fixture/document detail fixtures
     * @param customerFixture
     * @param customerInvoiceDocumentFixture
     * @param customerInvoiceDocumentFixtures
     */
    public static void saveNewCustomerInvoiceDocument(CustomerInvoiceDocumentFixture customerInvoiceDocumentFixture, CustomerInvoiceDetailFixture[] customerInvoiceDocumentFixtures, CustomerFixture customerFixture) throws Exception{
                
        CustomerInvoiceDocument document = null;
        if( ObjectUtils.isNotNull( customerFixture ) ){
            document  = customerInvoiceDocumentFixture.createCustomerInvoiceDocument(customerFixture, customerInvoiceDocumentFixtures);
        } else {
            document  = customerInvoiceDocumentFixture.createCustomerInvoiceDocument(customerInvoiceDocumentFixtures);
        }
        
        SpringContext.getBean(BusinessObjectService.class).save(document);
    }    
    
    /**
     * This method submits a customer invoice document based on passed in customer fix
     * @param customerFixture
     * @param customerInvoiceDocumentFixture
     * @param customerInvoiceDocumentFixtures
     */
    public static void submitNewCustomerInvoiceDocument(CustomerInvoiceDocumentFixture customerInvoiceDocumentFixture, CustomerInvoiceDetailFixture[] customerInvoiceDocumentFixtures, CustomerFixture customerFixture) throws Exception{
        
        
        CustomerInvoiceDocument document = null;
        if( ObjectUtils.isNotNull( customerFixture ) ){
            document  = customerInvoiceDocumentFixture.createCustomerInvoiceDocument(customerFixture, customerInvoiceDocumentFixtures);
        } else {
            document  = customerInvoiceDocumentFixture.createCustomerInvoiceDocument(customerInvoiceDocumentFixtures);
        }
        document.getDocumentHeader().setDocumentDescription("CREATING TEST CUSTOMER INVOICE DOCUMENT");
        
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        documentService.routeDocument(document, null, null);
    }
}
