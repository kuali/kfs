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

import org.kuali.core.document.Document;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.fixture.CustomerFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDocumentFixture;
import org.kuali.kfs.module.ec.service.impl.EffortCertificationDocumentServiceImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.DocumentSystemSaveEvent;

import edu.iu.uis.eden.exception.WorkflowException;

public class CustomerInvoiceDocumentTestUtil {
    
    public static final String CUSTOMER_MAINT_DOC_NAME = "CustomerMaintenanceDocument";
    
    public static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerInvoiceDocumentTestUtil.class);
    
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
    public static String saveNewCustomerInvoiceDocument(CustomerInvoiceDocumentFixture customerInvoiceDocumentFixture, CustomerInvoiceDetailFixture[] customerInvoiceDocumentFixtures, CustomerFixture customerFixture){
                
        CustomerInvoiceDocument document = null;
        if( ObjectUtils.isNotNull( customerFixture ) ){
            document  = customerInvoiceDocumentFixture.createCustomerInvoiceDocument(customerFixture, customerInvoiceDocumentFixtures);
        } else {
            document  = customerInvoiceDocumentFixture.createCustomerInvoiceDocument(customerInvoiceDocumentFixtures);
        }
        
        Document savedDocument = null;
        try {
            savedDocument = SpringContext.getBean(DocumentService.class).saveDocument(document, DocumentSystemSaveEvent.class);
        } catch (Exception e){
            LOG.error(e.getMessage());
        }
        return ObjectUtils.isNotNull(savedDocument)? savedDocument.getDocumentNumber() : null;
    }    
    
    /**
     * This method submits a customer invoice document based on passed in customer fix
     * @param customerFixture
     * @param customerInvoiceDocumentFixture
     * @param customerInvoiceDocumentFixtures
     */
    public static String submitNewCustomerInvoiceDocument(CustomerInvoiceDocumentFixture customerInvoiceDocumentFixture, CustomerInvoiceDetailFixture[] customerInvoiceDetailFixtures, CustomerFixture customerFixture){
        
        CustomerInvoiceDocument document = null;
        if( ObjectUtils.isNotNull( customerFixture ) ){
            document  = customerInvoiceDocumentFixture.createCustomerInvoiceDocument(customerFixture, customerInvoiceDetailFixtures);
        } else {
            document  = customerInvoiceDocumentFixture.createCustomerInvoiceDocument(customerInvoiceDetailFixtures);
        }
        document.getDocumentHeader().setDocumentDescription("CREATING TEST CUSTOMER INVOICE DOCUMENT");
        
        Document routedDocument = null;
        try {
            routedDocument = SpringContext.getBean(DocumentService.class).routeDocument(document, null, null);
        } catch (Exception e){
            LOG.error(e.getMessage());
        }
        return ObjectUtils.isNotNull(routedDocument)? routedDocument.getDocumentNumber() : null;
    }
    
    /**
     * This method submits a customer invoice document based on passed in customer fix
     * @param customerFixture
     * @param customerInvoiceDocumentFixture
     * @param customerInvoiceDocumentFixtures
     */
    public static CustomerInvoiceDocument submitNewCustomerInvoiceDocumentAndReturnIt(CustomerInvoiceDocumentFixture customerInvoiceDocumentFixture, CustomerInvoiceDetailFixture[] customerInvoiceDetailFixtures, CustomerFixture customerFixture){
        CustomerInvoiceDocument document = null;
        if( ObjectUtils.isNotNull( customerFixture ) ){
            document  = customerInvoiceDocumentFixture.createCustomerInvoiceDocument(customerFixture, customerInvoiceDetailFixtures);
        } else {
            document  = customerInvoiceDocumentFixture.createCustomerInvoiceDocument(customerInvoiceDetailFixtures);
        }
        document.getDocumentHeader().setDocumentDescription("CREATING TEST CUSTOMER INVOICE DOCUMENT");
        
        Document routedDocument = null;
        try {
            routedDocument = SpringContext.getBean(DocumentService.class).routeDocument(document, null, null);
        } catch (Exception e){
            LOG.error(e.getMessage());
        }
        return ObjectUtils.isNotNull(routedDocument)? (CustomerInvoiceDocument)routedDocument: null;
    }
}
