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

import static org.kuali.kfs.sys.fixture.UserNameFixture.wklykins;

import org.kuali.kfs.module.ar.businessobject.InvoiceGeneralDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.fixture.ContractsGrantsInvoiceDocumentFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceGeneralDetailFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

@ConfigureContext(session = wklykins)
public class ContractsGrantsInvoiceDocumentValidationTest extends KualiTestBase {

    private ContractsGrantsInvoiceDocumentValidation validation;

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

        assertTrue(validation.validate(null));
    }

}

