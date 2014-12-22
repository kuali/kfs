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

import org.apache.log4j.Logger;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.DocumentService;

/**
 * Test file for Collection Activity Document Service.
 */
@ConfigureContext(session = wklykins)
public class ContractsGrantsCollectionActivityDocumentServiceTest extends KualiTestBase {

    private static Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsCollectionActivityDocumentServiceTest.class);

    private static final Long PROPOSAL_NUMBER = 80472L;
    private static final String AGENCY_NUMBER = "12851";
    private static final String INVOICE_NUMBER = "4295";
    private static final String CUSTOMER_NUMBER = "ART362";

    DocumentService documentService;
    ContractsGrantsCollectionActivityDocumentService contractsGrantsCollectionActivityDocumentService;
    ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        documentService = SpringContext.getBean(DocumentService.class);
        contractsGrantsCollectionActivityDocumentService = SpringContext.getBean(ContractsGrantsCollectionActivityDocumentService.class);
        contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        documentService = null;
        contractsGrantsCollectionActivityDocumentService = null;
        contractsGrantsInvoiceDocumentService = null;
        super.tearDown();
    }

    /**
     * Tests the retrieveAwardByProposalNumber() method of service.
     */
    public void testRetrieveAwardByProposalNumber() {
        // To create AWard as fixture.
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD1.createAward();
        ARAwardFixture.CG_AWARD1.setAgencyFromFixture((Award) award);
        ContractsAndGrantsBillingAward awd = contractsGrantsCollectionActivityDocumentService.retrieveAwardByProposalNumber(new Long(11));
        assertNotNull(awd);
        assertEquals(new String("11505"), award.getAgencyNumber());
    }
}
