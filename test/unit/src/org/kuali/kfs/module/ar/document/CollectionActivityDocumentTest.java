/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;

/**
 * This class tests the CollectionActivityDocument class.
 */
@ConfigureContext(session = khuntley)
public class CollectionActivityDocumentTest extends KualiTestBase {

    private CollectionActivityDocument collectionActivityDocument;
    private ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    private List<ContractsGrantsInvoiceDocument> invoices;
    private ContractsGrantsInvoiceDocument cgInvoice;

    private final static String CUSTOMER_NUMBER = "ABB2";
    private final static String CUSTOMER_NAME = "WOODS CORPORATION";

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();

        // setting up document
        String chartCode = "BL";
        String orgCode = "UGCS";

        // To create a basic invoice with test data
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.createAward();
        ContractsAndGrantsBillingAwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_1.createAwardAccount();
        List<ContractsAndGrantsBillingAwardAccount> awardAccounts = new ArrayList<ContractsAndGrantsBillingAwardAccount>();
        awardAccounts.add(awardAccount_1);
        award.getActiveAwardAccounts().clear();

        award.getActiveAwardAccounts().add(awardAccount_1);
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.setAgencyFromFixture((Award) award);

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, awardAccounts, chartCode, orgCode);

        DocumentHeader documentHeader = cgInvoice.getDocumentHeader();
        WorkflowDocument workflowDocument = documentHeader.getWorkflowDocument();

        cgInvoice.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
        cgInvoice.getAccountsReceivableDocumentHeader().setCustomerNumber(CUSTOMER_NUMBER);
        cgInvoice.getAccountsReceivableDocumentHeader().setDocumentHeader(documentHeader);

        cgInvoice.setBillingDate(new java.sql.Date(new Date().getTime()));
        cgInvoice.setAward(award);
        cgInvoice.setOpenInvoiceIndicator(true);
        cgInvoice.setCustomerName(CUSTOMER_NAME);

        documentService.saveDocument(cgInvoice);

        collectionActivityDocument = new CollectionActivityDocument();
        contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        invoices = new ArrayList<ContractsGrantsInvoiceDocument>(contractsGrantsInvoiceDocumentService.retrieveOpenAndFinalCGInvoicesByProposalNumber(cgInvoice.getProposalNumber(), "ROCTestError.txt"));
        collectionActivityDocument.setInvoices(invoices);
        collectionActivityDocument.setSelectedInvoiceDocumentNumber(cgInvoice.getDocumentNumber());
    }

    /**
     * This method tests the method setEventsFromCGInvoices() of CollectionActivityDocument.
     */
    public void testSetEventsFromCGInvoices() {
        int size = 0;
        for (ContractsGrantsInvoiceDocument cg : invoices) {
            size += cg.getEvents().size();
        }
        collectionActivityDocument.setEventsFromCGInvoices();
        assertEquals(size, collectionActivityDocument.getEvents().size());
    }

    /**
     * This method tests the method getSelectedInvoiceApplication() of CollectionActivityDocument.
     */
    public void testGetSelectedInvoiceApplication() {
        ContractsGrantsInvoiceDocument selectedInvoice = collectionActivityDocument.getSelectedInvoiceApplication();
        assertNotNull(selectedInvoice);
        assertEquals(cgInvoice.getDocumentNumber(), selectedInvoice.getDocumentNumber());
    }

    /**
     * This method tests the method getInvoiceApplicationsByDocumentNumber() of CollectionActivityDocument.
     */
    public void testGetInvoiceApplicationsByDocumentNumber() {
        ContractsGrantsInvoiceDocument invoice = collectionActivityDocument.getInvoiceApplicationsByDocumentNumber().get(cgInvoice.getDocumentNumber());
        assertNotNull(invoice);
        assertEquals(cgInvoice.getDocumentNumber(), invoice.getDocumentNumber());
    }

    /**
     * This method tests the method getSelectedInvoiceEvents() of CollectionActivityDocument.
     */
    public void testGetSelectedInvoiceEvents() {
        collectionActivityDocument.setEventsFromCGInvoices();
        List<Event> caDetails = collectionActivityDocument.getSelectedInvoiceEvents();
        for (Event caDetail : caDetails) {
            assertEquals(cgInvoice.getDocumentNumber(), caDetail.getInvoiceNumber());
        }
    }
}
