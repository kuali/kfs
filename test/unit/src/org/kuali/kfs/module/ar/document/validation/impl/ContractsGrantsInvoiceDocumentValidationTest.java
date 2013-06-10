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
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceGeneralDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.fixture.ContractsGrantsInvoiceDocumentFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDocumentFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceGeneralDetailFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

@ConfigureContext(session = khuntley)
public class ContractsGrantsInvoiceDocumentValidationTest extends KualiTestBase {
    
    private ContractsGrantsInvoiceDocumentValidation validation;
    
    ;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validation = new ContractsGrantsInvoiceDocumentValidation();
       }

    @Override
    protected void tearDown() throws Exception {
        validation = null;
        super.tearDown();
    }
    
    public void testAmountNotEqualToZero_True() throws WorkflowException {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        
        validation.setContractsGrantsInvoiceDocument(contractsGrantsInvoiceDocument);
        validation.getContractsGrantsInvoiceDocument().setInvoiceGeneralDetail(invoiceGeneralDetail);
        
        assertTrue(validation.validate(null));        
    }
    
    public void testAmountNotEqualToZero_False() throws WorkflowException {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL5.createInvoiceGeneralDetail();
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        
        validation.setContractsGrantsInvoiceDocument(contractsGrantsInvoiceDocument);
        validation.getContractsGrantsInvoiceDocument().setInvoiceGeneralDetail(invoiceGeneralDetail);
        
        assertFalse(validation.validate(null));         
    }

}

