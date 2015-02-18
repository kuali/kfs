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

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.ContractsGrantsCollectionActivityInvoiceDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsCollectionActivityDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

/**
 * This class tests the rules in ContractsGrantsCollectionActivityDocumentRule
 */
@ConfigureContext(session = khuntley)
public class ContractsGrantsCollectionActivityDocumentRuleTest extends KualiTestBase {

    private DocumentService documentService;
    private ContractsGrantsCollectionActivityDocumentRule contractsGrantsCollectionActivityDocumentRule;
    private ContractsGrantsCollectionActivityDocument contractsGrantsCollectionActivityDocument;

    private final static String ACTIVITY_CODE = "LSTS";

    /**
     *
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        documentService = SpringContext.getBean(DocumentService.class);
        contractsGrantsCollectionActivityDocumentRule = new ContractsGrantsCollectionActivityDocumentRule();
        contractsGrantsCollectionActivityDocument = createCollectionActivityDocument();
    }

    /**
     *
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        documentService = null;
        contractsGrantsCollectionActivityDocumentRule = null;
        contractsGrantsCollectionActivityDocument = null;
    }

    /**
     * Creates the collection activity document.
     *
     * @return Returns new document of collection activity.
     * @throws WorkflowException
     */
    private ContractsGrantsCollectionActivityDocument createCollectionActivityDocument() throws WorkflowException {
        ContractsGrantsCollectionActivityDocument contractsGrantsCollectionActivityDocument = (ContractsGrantsCollectionActivityDocument) documentService.getNewDocument(ContractsGrantsCollectionActivityDocument.class);
        contractsGrantsCollectionActivityDocument.getDocumentHeader().setDocumentDescription("Testing document");

        return contractsGrantsCollectionActivityDocument;
    }

    /**
     * Tests the validateCollectionActivityDocument() method of service and returns true when all rules passed.
     */
    public void testValidateCollectionActivityDocument_True() {
        contractsGrantsCollectionActivityDocument.setProposalNumber(11L);
        contractsGrantsCollectionActivityDocument.setActivityCode(ACTIVITY_CODE);
        contractsGrantsCollectionActivityDocument.setActivityDate(new Date(System.currentTimeMillis()));
        contractsGrantsCollectionActivityDocument.setActivityText("testing activity comment");
        List<ContractsGrantsCollectionActivityInvoiceDetail> invoiceDetails = new ArrayList<>();
        ContractsGrantsCollectionActivityInvoiceDetail invoiceDetail = new ContractsGrantsCollectionActivityInvoiceDetail();
        invoiceDetails.add(invoiceDetail);
        contractsGrantsCollectionActivityDocument.setInvoiceDetails(invoiceDetails);

        boolean result = contractsGrantsCollectionActivityDocumentRule.validateCollectionActivityDocument(contractsGrantsCollectionActivityDocument);
        assertTrue(result);
    }

    /**
     * Tests the validateCollectionActivityDocument() method of service and returns false when the rules fail.
     */
    public void testValidateCollectionActivityDocument_False() {
        boolean result = contractsGrantsCollectionActivityDocumentRule.validateCollectionActivityDocument(contractsGrantsCollectionActivityDocument);
        assertFalse(result);
    }

    /**
     * Tests the validateCollectionActivityDocument() method of service and returns false when the rule fails.
     */
    public void testValidateCollectionActivityDocument_False_missing_ProposalNumber() {
        contractsGrantsCollectionActivityDocument.setActivityCode(ACTIVITY_CODE);
        contractsGrantsCollectionActivityDocument.setActivityDate(new Date(System.currentTimeMillis()));
        contractsGrantsCollectionActivityDocument.setActivityText("testing activity comment");
        List<ContractsGrantsCollectionActivityInvoiceDetail> invoiceDetails = new ArrayList<>();
        ContractsGrantsCollectionActivityInvoiceDetail invoiceDetail = new ContractsGrantsCollectionActivityInvoiceDetail();
        invoiceDetails.add(invoiceDetail);
        contractsGrantsCollectionActivityDocument.setInvoiceDetails(invoiceDetails);
        boolean result = contractsGrantsCollectionActivityDocumentRule.validateCollectionActivityDocument(contractsGrantsCollectionActivityDocument);
        assertFalse(result);
    }

    /**
     * Tests the validateCollectionActivityDocument() method of service and returns false when the rule fails.
     */
    public void testValidateCollectionActivityDocument_False_missing_InvoiceDetails() {
        contractsGrantsCollectionActivityDocument.setProposalNumber(11L);
        contractsGrantsCollectionActivityDocument.setActivityCode(ACTIVITY_CODE);
        contractsGrantsCollectionActivityDocument.setActivityDate(new Date(System.currentTimeMillis()));
        contractsGrantsCollectionActivityDocument.setActivityText("testing activity comment");
        boolean result = contractsGrantsCollectionActivityDocumentRule.validateCollectionActivityDocument(contractsGrantsCollectionActivityDocument);
        assertFalse(result);
    }

}
