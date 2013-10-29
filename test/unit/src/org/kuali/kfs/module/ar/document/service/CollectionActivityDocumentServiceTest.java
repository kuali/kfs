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
package org.kuali.kfs.module.ar.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.kfs.module.ar.document.CollectionActivityDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

/**
 * Test file for Collection Activity Document Service.
 */
@ConfigureContext(session = khuntley)
public class CollectionActivityDocumentServiceTest extends KualiTestBase {

    private static Logger LOG = org.apache.log4j.Logger.getLogger(CollectionActivityDocumentServiceTest.class);

    private static final Long PROPOSAL_NUMBER = 80472L;
    private static final String AGENCY_NUMBER = "12851";
    private static final String INVOICE_NUMBER = "4295";
    private static final String CUSTOMER_NUMBER = "ART362";

    DocumentService documentService;
    CollectionActivityDocumentService collectionActivityDocumentService;
    ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        documentService = SpringContext.getBean(DocumentService.class);
        collectionActivityDocumentService = SpringContext.getBean(CollectionActivityDocumentService.class);
        contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        documentService = null;
        collectionActivityDocumentService = null;
        contractsGrantsInvoiceDocumentService = null;
        super.tearDown();
    }

    /**
     * Tests the addNewEvent() method of service.
     * 
     * @throws WorkflowException
     */
    public void testAddNewEvent() throws WorkflowException {
        CollectionActivityDocument collectionActivityDocument;
        Event newEvent = new Event();

        try {
            collectionActivityDocument = (CollectionActivityDocument) documentService.getNewDocument(CollectionActivityDocument.class);
        }
        catch (Exception e) {
            LOG.error("An Exception was thrown while trying to initiate a new Collection Activity document.", e);
            throw new RuntimeException("An Exception was thrown while trying to initiate a new Collection Activity document.", e);
        }

        collectionActivityDocument.getDocumentHeader().setDocumentDescription("Collection Activity created for testing");
        collectionActivityDocument.setProposalNumber(PROPOSAL_NUMBER);

        this.loadAwardInformationForCollectionActivityDocument(collectionActivityDocument);

        Collection<ContractsGrantsInvoiceDocument> cgInvoices = contractsGrantsInvoiceDocumentService.retrieveOpenAndFinalCGInvoicesByProposalNumber(PROPOSAL_NUMBER, "CATestError.txt");
        if (CollectionUtils.isNotEmpty(cgInvoices)) {
            collectionActivityDocument.setInvoices(new ArrayList<ContractsGrantsInvoiceDocument>(cgInvoices));
        }

        collectionActivityDocument.setEventsFromCGInvoices();
        collectionActivityDocument.setSelectedInvoiceDocumentNumber(collectionActivityDocument.getEvents().get(0).getInvoiceNumber());

        newEvent.setInvoiceNumber(collectionActivityDocument.getSelectedInvoiceDocumentNumber());
        try {
            collectionActivityDocumentService.addNewEvent("Collection Activity created for testing", collectionActivityDocument, newEvent);
        }
        catch (Exception e) {
            LOG.error("An Exception was thrown while trying to add a new collection activity document detail.", e);
            throw new RuntimeException("An Exception was thrown while trying to a new collection activity document detail.", e);
        }
    }

    /**
     * Tests loadAwardInformationForCollectionActivityDocument() method of service.
     * 
     * @param colActDoc
     */
    private void loadAwardInformationForCollectionActivityDocument(CollectionActivityDocument colActDoc) {
        collectionActivityDocumentService.loadAwardInformationForCollectionActivityDocument(colActDoc);
        assertEquals(CUSTOMER_NUMBER, colActDoc.getCustomerNumber());
    }

    /**
     * Tests the retrieveEventsByCriteria() method of service.
     */
    public void testRetrieveEventsByCriteria() {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("invoiceNumber", INVOICE_NUMBER);
        Collection<Event> events = collectionActivityDocumentService.retrieveEventsByCriteria(criteria);
        assertNotNull(events);
        for (Event event : events) {
            assertEquals(INVOICE_NUMBER, event.getInvoiceNumber());
        }
    }

    /**
     * Tests the retrieveAwardByProposalNumber() method of service.
     */
    public void testRetrieveAwardByProposalNumber() {
        ContractsAndGrantsBillingAward award = collectionActivityDocumentService.retrieveAwardByProposalNumber(PROPOSAL_NUMBER);
        assertNotNull(award);
        assertEquals(AGENCY_NUMBER, award.getAgencyNumber());
    }
}
