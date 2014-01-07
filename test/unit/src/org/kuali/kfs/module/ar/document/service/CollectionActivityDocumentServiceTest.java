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

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.document.CollectionActivityDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
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


            collectionActivityDocument.getDocumentHeader().setDocumentDescription("Collection Activity created for testing");


            // To create a basic invoice with test data

            String coaCode = "BL";
            String orgCode = "SRS";
            ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.createAward();
            ContractsAndGrantsBillingAwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_1.createAwardAccount();
            List<ContractsAndGrantsBillingAwardAccount> awardAccounts = new ArrayList<ContractsAndGrantsBillingAwardAccount>();
            awardAccounts.add(awardAccount_1);
            award.getActiveAwardAccounts().clear();

            award.getActiveAwardAccounts().add(awardAccount_1);
            award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.setAgencyFromFixture((Award) award);
            collectionActivityDocument.setProposalNumber(award.getProposalNumber());
            collectionActivityDocumentService.loadAwardInformationForCollectionActivityDocument(collectionActivityDocument);

            // To add data for OrganizationOptions as fixture.
            OrganizationOptions organizationOptions = new OrganizationOptions();

            organizationOptions.setChartOfAccountsCode(coaCode);
            organizationOptions.setOrganizationCode(orgCode);
            organizationOptions.setProcessingChartOfAccountCode(coaCode);
            organizationOptions.setProcessingOrganizationCode(orgCode);
            SpringContext.getBean(BusinessObjectService.class).save(organizationOptions);

            ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, awardAccounts, coaCode, orgCode);
            cgInvoice.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
            documentService.saveDocument(cgInvoice);

            // to Add events
            Event event = new Event();
            event.setDocumentNumber(cgInvoice.getDocumentNumber());
            event.setInvoiceNumber(cgInvoice.getDocumentNumber());
            event.setActivityCode("TEST");
            event.setFollowupInd(true);
            event.setActivityText("Activity Text");
            Timestamp ts = new Timestamp(new java.util.Date().getTime());
            Date today = new Date(ts.getTime());
            event.setFollowupDate(today);
            event.setActivityDate(today);
            SpringContext.getBean(BusinessObjectService.class).save(event);
            cgInvoice.getEvents().add(event);
            documentService.saveDocument(cgInvoice);


            Collection<ContractsGrantsInvoiceDocument> cgInvoices = contractsGrantsInvoiceDocumentService.retrieveOpenAndFinalCGInvoicesByProposalNumber(award.getProposalNumber(), "CATestError.txt");
            if (CollectionUtils.isNotEmpty(cgInvoices)) {
                collectionActivityDocument.setInvoices(new ArrayList<ContractsGrantsInvoiceDocument>(cgInvoices));
            }


            collectionActivityDocumentService.addNewEvent("Collection Activity created for testing", collectionActivityDocument, event);
        }
        catch (Exception e) {
            LOG.error("An Exception was thrown while trying to add a new collection activity document detail.", e);
            throw new RuntimeException("An Exception was thrown while trying to a new collection activity document detail.", e);
        }
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
        // To create AWard as fixture.
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD1.createAward();
        ARAwardFixture.CG_AWARD1.setAgencyFromFixture((Award) award);
        ContractsAndGrantsBillingAward awd = collectionActivityDocumentService.retrieveAwardByProposalNumber(new Long(11));
        assertNotNull(awd);
        assertEquals(new String("11505"), award.getAgencyNumber());
    }
}
