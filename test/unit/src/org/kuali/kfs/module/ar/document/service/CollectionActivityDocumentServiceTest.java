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
package org.kuali.kfs.module.ar.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.wklykins;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.document.CollectionActivityDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ErrorMessage;

/**
 * Test file for Collection Activity Document Service.
 */
@ConfigureContext(session = wklykins)
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
        CollectionEvent newCollectionEvent = new CollectionEvent();

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

        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, awardAccounts, coaCode, orgCode, errorMessages);
        cgInvoice.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
        for (InvoiceAddressDetail invoiceAddressDetail : cgInvoice.getInvoiceAddressDetails()) {
            invoiceAddressDetail.setCustomerInvoiceTemplateCode("STD");
            invoiceAddressDetail.setInvoiceTransmissionMethodCode("MAIL");
        }
        documentService.saveDocument(cgInvoice);

        // to Add events
        CollectionEvent event = new CollectionEvent();
        event.setDocumentNumber(cgInvoice.getDocumentNumber());
        event.setInvoiceNumber(cgInvoice.getDocumentNumber());
        event.setActivityCode("TEST");
        event.setFollowup(true);
        event.setActivityText("Activity Text");
        Timestamp ts = new Timestamp(new java.util.Date().getTime());
        Date today = new Date(ts.getTime());
        event.setFollowupDate(today);
        event.setActivityDate(today);
        SpringContext.getBean(BusinessObjectService.class).save(event);
        cgInvoice.getCollectionEvents().add(event);

        documentService.saveDocument(cgInvoice);


        Collection<ContractsGrantsInvoiceDocument> cgInvoices = contractsGrantsInvoiceDocumentService.retrieveOpenAndFinalCGInvoicesByProposalNumber(award.getProposalNumber());
        if (CollectionUtils.isNotEmpty(cgInvoices)) {
            collectionActivityDocument.setInvoices(new ArrayList<ContractsGrantsInvoiceDocument>(cgInvoices));
        }

        collectionActivityDocumentService.addNewCollectionEvent("Collection Activity created for testing", collectionActivityDocument, event);
    }

    /**
     * Tests the retrieveEventsByCriteria() method of service.
     */
    public void testRetrieveEventsByCriteria() {
        Map<String,String> fieldValues = new HashMap<String,String>();
        fieldValues.put("invoiceNumber", INVOICE_NUMBER);
        Collection<CollectionEvent> events = collectionActivityDocumentService.retrieveCollectionEvents(fieldValues, null);
        assertNotNull(events);
        for (CollectionEvent event : events) {
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
